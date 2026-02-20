package com.example.jobtracker.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
public class AdminLogDTO {

    private Long id;

    private String actorEmail;
    private String actorRole;

    private String targetEmail;
    private String targetRole;

    private String action;
    private String details;

    private LocalDateTime timestamp;

    // getters & setters
}
