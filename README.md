#  JobTracker

A full-stack job application management system with secure authentication and automated Telegram reminders.

Built using **Spring Boot + React + MySQL + Firebase + Telegram Bot API**.

---

##  Features

- Manage job applications (Create, Update, Delete)
-  Track application status, interview rounds & stages
-  Schedule next actions (Interview, Follow-up, Assignment)
- Automated Telegram reminders via secure webhook
-  Firebase JWT-based authentication
-  Webhook secret validation for Telegram integration
-  Background scheduler for real-time notifications
-  Clean layered architecture (Controller → Service → Repository)

---

##  Tech Stack

### Backend
- Java 24
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL

### Frontend
- React
- Axios
- Context API
- Tailwind CSS

### Integrations
- Firebase Authentication
- Telegram Bot API (secure token-based linking)
- ngrok (development webhook testing)

---

##  Telegram Integration Flow

1. User generates secure link token  
2. Telegram `/start <token>` links account  
3. Chat ID stored securely in database  
4. Background scheduler sends automated reminders  

---

##  Architecture Highlights

- Token-based Telegram linking (10-minute expiry)
- Unique Telegram chat ID enforcement
- Hard delete for job applications
- Soft delete for users
- Global exception handling
- Composite DB index for reminder performance
- Secure webhook using secret header validation

