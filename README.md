# ✅ Secure To-Do List Application (Java + Vert.x + MongoDB + Redis + JWT + SMTP)

A secure, scalable To-Do List web application built in Java using Vert.x, MongoDB for persistent data, Redis for JWT token storage/blacklisting, and SMTP for email delivery. The app supports registration, login, password reset, and full CRUD operations on private to-do tasks with optional reminders.

---

## 📌 Features

### 🔐 User Registration & Account Management
- Register with email and name.
- Secure random password generation (sent via email).
- Password hashing with bcrypt.
- Duplicate email prevention.
- Password reset via time-limited token (emailed).

### 🛡️ Secure Authentication
- JWT-based token authentication.
- JWTs stored in Redis for session tracking.
- Token refresh and logout support.
- Token invalidation on logout/password reset.

### 📝 To-Do Task Management
- Users can **create, view, update, delete** their own tasks.
- Fields: `title`, `description`, `dueDate`, `priority`, `status`, `timestamps`.
- Mark tasks as completed/incomplete.
- Email reminders before due date (asynchronous).
- Paginated, filterable, and sortable listing.

### 🌐 RESTful API Design
- Standard HTTP methods (`GET`, `POST`, `PUT`, `DELETE`) with proper status codes.
- Secure routes protected by JWT middleware.
- Clean endpoint structure for authentication and task management.

---

## ⚙️ Tech Stack

- **Backend:** Java, Vert.x
- **Database:** MongoDB
- **Session Store:** Redis
- **Authentication:** JWT (Json Web Token)
- **Email Delivery:** SMTP (Jakarta Mail)
- **UI:** HTML, JS, CSS (vanilla)

---

## 🚀 Getting Started

### 1. Prerequisites
- Java 17+
- MongoDB running locally on `mongodb://localhost:27017`
- Redis running locally on `redis://localhost:6379`
- Maven or Gradle
- Gmail account with [App Password](https://support.google.com/accounts/answer/185833)


OUTPUT

![Screenshot 2025-07-04 102546](https://github.com/user-attachments/assets/46c6e9a5-cc4f-4908-a0dd-eaeaa639a63d)

![Screenshot 2025-07-04 102550](https://github.com/user-attachments/assets/c4bda62f-c844-4f5c-b726-363c1404916f)

![Screenshot 2025-07-04 102554](https://github.com/user-attachments/assets/70ab6ac3-aecc-4acd-bd8e-55c2cbfd45ce)

![Screenshot 2025-07-04 102559](https://github.com/user-attachments/assets/cebc558a-a492-488c-9fff-3506bb4784f9)

![Screenshot 2025-07-04 102606](https://github.com/user-attachments/assets/782d8840-6cc4-4562-97a6-966e1ce6cc3e)
