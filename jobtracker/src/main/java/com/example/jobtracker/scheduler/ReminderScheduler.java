package com.example.jobtracker.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.jobtracker.integration.TelegramService;
import com.example.jobtracker.model.JobApplication;
import com.example.jobtracker.model.User;
import com.example.jobtracker.repository.JobApplicationRepository;
import com.example.jobtracker.repository.UserRepository;

@Service
public class ReminderScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(ReminderScheduler.class);

    private final JobApplicationRepository jobRepository;
    private final UserRepository userRepository;
    private final TelegramService telegramService;

    public ReminderScheduler(
            JobApplicationRepository jobRepository,
            UserRepository userRepository,
            TelegramService telegramService
    ) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.telegramService = telegramService;
    }

    // ==========================================
    //  SEND DUE REMINDERS (Runs every minute)
    // ==========================================
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void sendDueReminders() {

        LocalDateTime now = LocalDateTime.now().plusSeconds(5);

        log.info("Checking for due reminders at {}", now);

        List<JobApplication> dueJobs =
                jobRepository.findDueReminders(now);

        if (dueJobs.isEmpty()) {
            return;
        }

        for (JobApplication job : dueJobs) {

            try {

                // Safety check (idempotent protection)
                if (job.isReminderSent()) {
                    continue;
                }

                User user = userRepository
                        .findById(job.getUserId())
                        .orElse(null);

                if (user == null) {
                    log.warn("User not found for job ID {}", job.getId());
                    continue;
                }

                if (user.getTelegramChatId() == null) {
                    log.warn("Telegram not linked for user UID {}", user.getUid());
                    continue;
                }

                telegramService.sendMessage(
                        user.getTelegramChatId(),
                        buildMessage(job)
                );

                // Mark reminder as sent
                job.setReminderSent(true);
                job.setReminderSentAt(LocalDateTime.now());

                jobRepository.save(job);

                log.info("Reminder sent successfully for job ID {}",
                        job.getId());

            } catch (Exception e) {

                log.error("Reminder failed for job ID {}",
                        job.getId(), e);
            }
        }
    }

    // ==========================================
    // MESSAGE BUILDER
    // ==========================================
    private String buildMessage(JobApplication job) {

        return """
📌 Job Reminder

Company: %s
Role: %s
Next Step: %s

Stay consistent. You’re building your future !!!
""".formatted(
                job.getCompanyName(),
                job.getRole(),
                job.getNextActionType()
        );
    }
}