package com.example.jobtracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobtracker.dto.UserDTO;
import com.example.jobtracker.model.User;
import com.example.jobtracker.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public UserDTO me(Authentication auth) {

        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            throw new RuntimeException("Unauthorized");
        }

        User principal = (User) auth.getPrincipal();

        User user = userService.findOrCreate(
                principal.getUid(),
                principal.getEmail(),
                principal.getDisplayName()
        );

        return toUserDTO(user);
    }

    private UserDTO toUserDTO(User user) {

        UserDTO dto = new UserDTO();
        dto.setUid(user.getUid());
        dto.setEmail(user.getEmail());
        dto.setDisplayName(user.getDisplayName());
        dto.setActive(user.isActive());
        dto.setDeleted(user.isDeleted());
        dto.setRole(user.getRole() != null ? user.getRole().name() : "USER");
        dto.setCreatedAt(user.getCreatedAt());
        dto.setTelegramLinked(user.getTelegramChatId() != null);

        return dto;
    }
}