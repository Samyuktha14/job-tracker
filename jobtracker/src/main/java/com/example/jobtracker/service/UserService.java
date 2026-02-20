package com.example.jobtracker.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.jobtracker.model.Role;
import com.example.jobtracker.model.User;
import com.example.jobtracker.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User findOrCreate(String uid,
                             String email,
                             String displayName) {

        //  Check by UID
        Optional<User> existingByUid = userRepository.findById(uid);
        if (existingByUid.isPresent()) {
            return existingByUid.get();
        }

        //  Check by email (important for Firebase UID change)
        Optional<User> existingByEmail = userRepository.findByEmail(email);
        if (existingByEmail.isPresent()) {

            User user = existingByEmail.get();

            //  Restore soft-deleted account
            if (user.isDeleted()) {
                user.setDeleted(false);
                user.setActive(true);
                user.setUid(uid);

                if (displayName != null && !displayName.isBlank()) {
                    user.setDisplayName(displayName);
                }

                return userRepository.save(user);
            }

            return user;
        }

        //  Completely new user
        User newUser = new User();
        newUser.setUid(uid);
        newUser.setEmail(email);

        if (displayName != null && !displayName.isBlank()) {
            newUser.setDisplayName(displayName);
        } else {
            newUser.setDisplayName(email.split("@")[0]);
        }

        newUser.setCreatedAt(LocalDate.now());
        newUser.setActive(true);
        newUser.setDeleted(false);

        //  AUTO SUPER ADMIN BOOTSTRAP LOGIC
        long superAdminCount =
                userRepository.countByRoleAndDeletedFalse(Role.SUPER_ADMIN);

        if (superAdminCount == 0) {
            newUser.setRole(Role.SUPER_ADMIN);
            System.out.println("First user auto-bootstrapped as SUPER_ADMIN");
        } else {
            newUser.setRole(Role.USER);
        }

        return userRepository.save(newUser);
    }
}
