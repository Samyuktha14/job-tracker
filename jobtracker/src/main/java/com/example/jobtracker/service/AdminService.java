package com.example.jobtracker.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.jobtracker.dto.AdminLogDTO;
import com.example.jobtracker.dto.AdminStatsDTO;
import com.example.jobtracker.dto.UserDTO;
import com.example.jobtracker.exception.ResourceNotFoundException;
import com.example.jobtracker.model.*;
import com.example.jobtracker.repository.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;

import static com.example.jobtracker.specification.UserSpecification.*;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobApplicationRepository jobRepository;

    @Autowired
    private AdminLogRepository adminLogRepository;

    // ===============================
    // ADMIN STATS
    // ===============================
    // ===============================
// ADMIN STATS
// ===============================
public AdminStatsDTO getStatsDTO() {

    AdminStatsDTO dto = new AdminStatsDTO();

    // ===============================
    // USER METRICS
    // ===============================

    dto.setTotalUsers(userRepository.countByDeletedFalse());
    dto.setSuperAdminUsers(userRepository.countByRoleAndDeletedFalse(Role.SUPER_ADMIN));
    dto.setAdminUsers(userRepository.countByRoleAndDeletedFalse(Role.ADMIN));
    dto.setNormalUsers(userRepository.countByRoleAndDeletedFalse(Role.USER));
    dto.setActiveUsers(userRepository.countByActiveTrueAndDeletedFalse());
    dto.setDeletedUsers(userRepository.countByDeletedTrue());
    dto.setDisabledUsers(userRepository.countByActiveFalseAndDeletedFalse());

    // ===============================
    // JOB METRICS
    // ===============================

    dto.setTotalJobs(jobRepository.count());

dto.setApplied(
    jobRepository.countByApplicationStatusIgnoreCase("Applied")
);

dto.setInterviewing(
    jobRepository.countByApplicationStatusIgnoreCase("Interviewing")
);

dto.setRejected(
    jobRepository.countByApplicationStatusIgnoreCase("Rejected")
);

dto.setSelected(
    jobRepository.countByApplicationStatusIgnoreCase("Selected")
);

        return dto;
    }


    // ===============================
    // FILTER USERS
    // ===============================
    public Page<UserDTO> getFilteredUsers(
            String status,
            String role,
            String search,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Specification<User> spec = Specification.where(null);

        if (search != null && !search.isBlank()) {
            spec = spec.and(emailContains(search));
        }

        if (role != null && !role.isBlank()) {
            spec = spec.and(hasRole(role));
        }

        if (status != null && !status.isBlank()) {
            spec = spec.and(hasStatus(status));
        }

        Page<User> users = userRepository.findAll(spec, pageable);
        return users.map(this::toDTO);
    }

    // ===============================
    // PROMOTE
    // ===============================
    @Transactional
    public UserDTO promoteToAdmin(String targetUid, String currentUid) {

        User current = getUserOrThrow(currentUid);
        User target = getUserOrThrow(targetUid);

        if (current.getRole() != Role.SUPER_ADMIN)
            throw new AccessDeniedException("Only SUPER_ADMIN can promote users.");

        if (target.getRole() == Role.SUPER_ADMIN)
            throw new IllegalArgumentException("Cannot modify SUPER_ADMIN.");

        if (target.getRole() == Role.ADMIN)
            throw new IllegalArgumentException("User is already ADMIN.");

        target.setRole(Role.ADMIN);
        userRepository.save(target);

        logAction(current, target, AdminAction.PROMOTE, "Promoted USER to ADMIN");

        return toDTO(target);
    }

    // ===============================
    // DEMOTE
    // ===============================
    @Transactional
    public UserDTO demoteToUser(String targetUid, String currentUid) {

        User current = getUserOrThrow(currentUid);
        User target = getUserOrThrow(targetUid);

        if (current.getRole() != Role.SUPER_ADMIN)
            throw new AccessDeniedException("Only SUPER_ADMIN can demote admins.");

        if (target.getRole() == Role.SUPER_ADMIN) {
            long count = userRepository.countByRoleAndDeletedFalse(Role.SUPER_ADMIN);
            if (count <= 1)
                throw new IllegalArgumentException("Cannot demote the last SUPER_ADMIN.");
        }

        if (target.getRole() != Role.ADMIN)
            throw new IllegalArgumentException("Target is not an ADMIN.");

        target.setRole(Role.USER);
        userRepository.save(target);

        logAction(current, target, AdminAction.DEMOTE, "Demoted ADMIN to USER");

        return toDTO(target);
    }

    // ===============================
    // TOGGLE ACTIVE
    // ===============================
    @Transactional
    public UserDTO toggleUser(String targetUid, String currentUid) {

        User current = getUserOrThrow(currentUid);
        User target = getUserOrThrow(targetUid);

        if (target.isDeleted())
            throw new IllegalArgumentException("Cannot toggle deleted user.");

        if (target.getUid().equals(current.getUid()))
            throw new IllegalArgumentException("You cannot disable yourself.");

        if (current.getRole() == Role.ADMIN && target.getRole() != Role.USER)
            throw new AccessDeniedException("Admin cannot modify higher roles.");

        boolean newState = !target.isActive();
        target.setActive(newState);
        userRepository.save(target);

        logAction(
                current,
                target,
                newState ? AdminAction.ENABLE : AdminAction.DISABLE,
                newState ? "Enabled user account" : "Disabled user account"
        );

        try {
            FirebaseAuth.getInstance().updateUser(
                    new UserRecord.UpdateRequest(target.getUid())
                            .setDisabled(!newState)
            );
        } catch (Exception ignored) {}

        return toDTO(target);
    }

    // ===============================
    // SOFT DELETE
    // ===============================
    @Transactional
    public void softDeleteUser(String targetUid, String currentUid) {

        User current = getUserOrThrow(currentUid);
        User target = getUserOrThrow(targetUid);

        if (current.getUid().equals(target.getUid()))
            throw new IllegalArgumentException("You cannot delete yourself.");

        if (target.isDeleted())
            throw new IllegalArgumentException("User already deleted.");

        if (current.getRole() == Role.ADMIN &&
                (target.getRole() == Role.ADMIN || target.getRole() == Role.SUPER_ADMIN))
            throw new AccessDeniedException("Admin cannot delete higher roles.");

        if (target.getRole() == Role.SUPER_ADMIN) {
            long count = userRepository.countByRoleAndDeletedFalse(Role.SUPER_ADMIN);
            if (count <= 1)
                throw new IllegalArgumentException("Cannot delete last SUPER_ADMIN.");
        }

        target.setDeleted(true);
        target.setActive(false);
        target.setDeletedAt(LocalDateTime.now());
        userRepository.save(target);

        logAction(current, target, AdminAction.DELETE, "Soft deleted user");

        try {
            FirebaseAuth.getInstance().updateUser(
                    new UserRecord.UpdateRequest(target.getUid()).setDisabled(true)
            );
        } catch (Exception ignored) {}
    }

    // ===============================
    // RESTORE
    // ===============================
    @Transactional
    public UserDTO restoreUser(String targetUid, String currentUid) {

        User current = getUserOrThrow(currentUid);
        User target = getUserOrThrow(targetUid);

        if (current.getRole() != Role.SUPER_ADMIN)
            throw new AccessDeniedException("Only SUPER_ADMIN can restore.");

        if (!target.isDeleted())
            throw new IllegalArgumentException("User is not deleted.");

        target.setDeleted(false);
        target.setActive(true);
        target.setDeletedAt(null);
        userRepository.save(target);

        logAction(current, target, AdminAction.RESTORE, "Restored deleted user");

        try {
            FirebaseAuth.getInstance().updateUser(
                    new UserRecord.UpdateRequest(target.getUid()).setDisabled(false)
            );
        } catch (Exception ignored) {}

        return toDTO(target);
    }

    // ===============================
    // ADMIN LOGS (DTO)
    // ===============================
    public Page<AdminLogDTO> getAdminLogs(int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "timestamp")
        );

        Page<AdminLog> logs = adminLogRepository.findAll(pageable);

        return logs.map(this::convertToDTO);
    }

    public List<AdminLogDTO> getAllLogsForExport() {

        List<AdminLog> logs = adminLogRepository.findAll(
                Sort.by(Sort.Direction.DESC, "timestamp")
        );

        return logs.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private AdminLogDTO convertToDTO(AdminLog log) {

        User actor = userRepository.findById(log.getActorUid()).orElse(null);
        User target = userRepository.findById(log.getTargetUid()).orElse(null);

        AdminLogDTO dto = new AdminLogDTO();

        dto.setId(log.getId());
        dto.setActorEmail(actor != null ? actor.getEmail() : "Unknown");
        dto.setActorRole(actor != null ? actor.getRole().name() : "N/A");
        dto.setTargetEmail(target != null ? target.getEmail() : "Unknown");
        dto.setTargetRole(target != null ? target.getRole().name() : "N/A");
        dto.setAction(log.getAction().name());
        dto.setDetails(log.getDetails());
        dto.setTimestamp(log.getTimestamp());

        return dto;
    }

    // ===============================
    // AUDIT LOGGING
    // ===============================
    private void logAction(User actor, User target, AdminAction action, String details) {

        AdminLog log = new AdminLog();
        log.setActorUid(actor.getUid());
        log.setActorRole(actor.getRole());
        log.setTargetUid(target.getUid());
        log.setTargetRole(target.getRole());
        log.setAction(action);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());

        adminLogRepository.save(log);
    }

    private User getUserOrThrow(String uid) {
        return userRepository.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUid(user.getUid());
        dto.setEmail(user.getEmail());
        dto.setDisplayName(user.getDisplayName());
        dto.setActive(user.isActive());
        dto.setDeleted(user.isDeleted());
        dto.setRole(user.getRole().name());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setTelegramLinked(user.getTelegramChatId() != null);
        return dto;
    }
}
