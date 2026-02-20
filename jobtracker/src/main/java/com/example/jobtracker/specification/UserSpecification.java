package com.example.jobtracker.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.jobtracker.model.Role;
import com.example.jobtracker.model.User;

public class UserSpecification {

    public static Specification<User> emailContains(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction(); // TRUE
            }
            return cb.like(
                    cb.lower(root.get("email")),
                    "%" + search.toLowerCase() + "%"
            );
        };
    }

    public static Specification<User> hasRole(String role) {
        return (root, query, cb) -> {
            if (role == null || role.isBlank()) {
                return cb.conjunction(); // TRUE
            }
            return cb.equal(root.get("role"),
                    Role.valueOf(role.toUpperCase()));
        };
    }

    public static Specification<User> hasStatus(String status) {
        return (root, query, cb) -> {

            if (status == null || status.isBlank()) {
                return cb.conjunction(); // TRUE
            }

            if ("ACTIVE".equalsIgnoreCase(status)) {
                return cb.and(
                        cb.isFalse(root.get("deleted")),
                        cb.isTrue(root.get("active"))
                );
            }

            if ("DISABLED".equalsIgnoreCase(status)) {
                return cb.and(
                        cb.isFalse(root.get("deleted")),
                        cb.isFalse(root.get("active"))
                );
            }

            if ("DELETED".equalsIgnoreCase(status)) {
                return cb.isTrue(root.get("deleted"));
            }

            return cb.conjunction();
        };
    }
}
