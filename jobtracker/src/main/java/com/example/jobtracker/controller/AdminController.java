package com.example.jobtracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.jobtracker.dto.AdminLogDTO;
import com.example.jobtracker.dto.AdminStatsDTO;
import com.example.jobtracker.dto.UserDTO;
import com.example.jobtracker.model.User;
import com.example.jobtracker.service.AdminService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")// SUPER_ADMIN inherits ADMIN
@Validated 
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ===============================
    // ADMIN STATS
    // ===============================
    @GetMapping("/stats")
    public AdminStatsDTO getStats() {
        return adminService.getStatsDTO();
    }

    // ===============================
    // VIEW USERS (FILTER SUPPORT)
    // ===============================
    @GetMapping("/users")
    public Page<UserDTO> getUsers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return adminService.getFilteredUsers(status, role, search, page, size);
    }

    // ===============================
    // MAKE ADMIN
    // ===============================
    @PutMapping("/make-admin/{uid}")
    public UserDTO makeAdmin(
            @PathVariable String uid,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();
        return adminService.promoteToAdmin(uid, currentUser.getUid());
    }

    // ===============================
    // DEMOTE ADMIN
    // ===============================
    @PutMapping("/demote-admin/{uid}")
    public UserDTO demoteAdmin(
            @PathVariable String uid,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();
        return adminService.demoteToUser(uid, currentUser.getUid());
    }

    // ===============================
    // TOGGLE USER
    // ===============================
    @PutMapping("/toggle-user/{uid}")
    public UserDTO toggleUser(
            @PathVariable String uid,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();
        return adminService.toggleUser(uid, currentUser.getUid());
    }

    // ===============================
    // DELETE USER (SOFT DELETE)
    // ===============================
    @DeleteMapping("/delete-user/{uid}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String uid,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();
        adminService.softDeleteUser(uid, currentUser.getUid());
        return ResponseEntity.noContent().build();
    }

    // ===============================
    // RESTORE USER
    // ===============================
    @PutMapping("/restore-user/{uid}")
    public UserDTO restoreUser(
            @PathVariable String uid,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();
        return adminService.restoreUser(uid, currentUser.getUid());
    }

    // ===============================
    // VIEW ADMIN ACTIVITY LOGS (DTO VERSION)
    // ===============================
    @GetMapping("/logs")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Page<AdminLogDTO> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return adminService.getAdminLogs(page, size);
    }

    // ===============================
    // EXPORT LOGS TO CSV
    // ===============================
    @GetMapping("/logs/export")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public void exportLogs(HttpServletResponse response) throws IOException {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=admin_logs.csv");

        List<AdminLogDTO> logs = adminService.getAllLogsForExport();

        PrintWriter writer = response.getWriter();

        writer.println("Time,Actor Email,Actor Role,Action,Target Email,Target Role,Details");

        for (AdminLogDTO log : logs) {
            writer.println(
                    log.getTimestamp() + "," +
                    safe(log.getActorEmail()) + "," +
                    safe(log.getActorRole()) + "," +
                    safe(log.getAction()) + "," +
                    safe(log.getTargetEmail()) + "," +
                    safe(log.getTargetRole()) + "," +
                    safe(log.getDetails())
            );
        }

        writer.flush();
        writer.close();
    }

   private String safe(String value) {
    if (value == null) return "";
    String sanitized = value.replace(",", " ");
    if (sanitized.startsWith("=") ||
        sanitized.startsWith("+") ||
        sanitized.startsWith("-") ||
        sanitized.startsWith("@")) {
        sanitized = "'" + sanitized;
    }
    return sanitized;
}
}
