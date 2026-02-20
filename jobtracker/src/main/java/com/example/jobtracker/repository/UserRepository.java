package com.example.jobtracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.jobtracker.model.Role;
import com.example.jobtracker.model.User;

public interface UserRepository extends JpaRepository<User, String>,JpaSpecificationExecutor<User> {

    // ===============================
    // BASIC LOOKUPS
    // ===============================
    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    // ===============================
    // COUNTING (FOR DASHBOARD)
    // ===============================
    long countByRole(Role role);

    long countByRoleAndDeletedFalse(Role role);   

    long countByDeletedFalse();

    long countByDeletedTrue();

    long countByActiveTrueAndDeletedFalse();

    long countByActiveFalseAndDeletedFalse();

    // ===============================
    // PAGINATION
    // ===============================
    Page<User> findByDeletedFalse(Pageable pageable);

    Page<User> findByDeletedTrue(Pageable pageable);

    Page<User> findByActiveTrueAndDeletedFalse(Pageable pageable);

    Page<User> findByActiveFalseAndDeletedFalse(Pageable pageable);

    Page<User> findByRoleAndDeletedFalse(Role role, Pageable pageable);

    // ===============================
    // SEARCH SUPPORT
    // ===============================
    Page<User> findByEmailContainingIgnoreCaseAndDeletedFalse(
            String email,
            Pageable pageable
    );


}
