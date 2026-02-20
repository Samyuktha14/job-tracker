package com.example.jobtracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.jobtracker.model.AdminLog;

public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {
}
