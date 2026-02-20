package com.example.jobtracker.dto;

public class AdminStatsDTO {

    // ===============================
    // USER METRICS
    // ===============================
    private long totalUsers;
    private long activeUsers;
    private long deletedUsers;
    private long disabledUsers;

    private long superAdminUsers;
    private long adminUsers;
    private long normalUsers;

    // ===============================
    // JOB METRICS
    // ===============================
    private long totalJobs;
    private long applied;
    private long interviewing;
    private long rejected;
    private long selected;

    // ===== Getters & Setters =====

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }

    public long getDeletedUsers() { return deletedUsers; }
    public void setDeletedUsers(long deletedUsers) { this.deletedUsers = deletedUsers; }

    public long getDisabledUsers() { return disabledUsers; }
    public void setDisabledUsers(long disabledUsers) { this.disabledUsers = disabledUsers; }

    public long getSuperAdminUsers() { return superAdminUsers; }
    public void setSuperAdminUsers(long superAdminUsers) { this.superAdminUsers = superAdminUsers; }

    public long getAdminUsers() { return adminUsers; }
    public void setAdminUsers(long adminUsers) { this.adminUsers = adminUsers; }

    public long getNormalUsers() { return normalUsers; }
    public void setNormalUsers(long normalUsers) { this.normalUsers = normalUsers; }

    public long getTotalJobs() { return totalJobs; }
    public void setTotalJobs(long totalJobs) { this.totalJobs = totalJobs; }

    public long getApplied() { return applied; }
    public void setApplied(long applied) { this.applied = applied; }

    public long getInterviewing() { return interviewing; }
    public void setInterviewing(long interviewing) { this.interviewing = interviewing; }

    public long getRejected() { return rejected; }
    public void setRejected(long rejected) { this.rejected = rejected; }

    public long getSelected() { return selected; }
    public void setSelected(long selected) { this.selected = selected; }
}
