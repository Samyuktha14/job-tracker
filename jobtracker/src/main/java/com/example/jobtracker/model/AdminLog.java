package com.example.jobtracker.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actorUid;

    @Enumerated(EnumType.STRING)
    private Role actorRole;

    private String targetUid;

    @Enumerated(EnumType.STRING)
    private Role targetRole;

    @Enumerated(EnumType.STRING)
    private AdminAction action;

    private String details;

    private LocalDateTime timestamp;
}
