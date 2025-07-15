# InterviewFlow

**InterviewFlow** is a Backend API for simple and effective application designed to help jobseekers organize and manage all their job
applications in one place.

---

## 🚀 Features

* **Track Applications**: Log each application with important details like:

    * Date applied
    * Company name
    * Role/Position
* **Flexible Status Tracking**: Filter and update applications by their status:

    * Application sent
    * Approved
    * Preparing for interview
    * Custom statuses (user-defined)
* **Notes & Flexibility**: Most fields behave like notes, giving users freedom to personalize field names and content to
  fit their workflow.
* **Easy Filtering**: Quickly find applications by status, company, or any custom note field.

---

## 📝 How It Works

1. **Add Application**: Enter the company, role, date applied, and any relevant notes or custom fields.
2. **Update Status**: Change the status as your application moves through the hiring process (e.g., Sent → Approved →
   Interview Prep).
3. **Filter & Organize**: Use filters to see only the applications you want to focus on—by status, company, or any note.
4. **Custom Fields**: Use flexible note fields to add any information you need (e.g., recruiter name, salary range, next
   steps).

---
## ✈️ Project Setup

The InterviewFlow created with the following main dependencies:

- Spring Data JPA
- Spring Security
- Spring Web
- Docker Compose
- Lombok
- JUnit5
- Mockito
- PostgreSQL

- Set up schema migrations using Liquibase. (To be implemented)

## 💾 Data Model

To be confirmed

- **`users` table**: `id`, `username`, `password`, `email`, `role`.
- **`applications` table**: `id`, `username`, ...

## 🔐 Security

- Spring Security configured for authentication and authorization.
- Basic type of Authentication.
- BCrypt is used for password hashing.
---

## 📦 Installation

Clone the repo and follow the setup instructions:

```bash
git clone https://github.com/JavaBootcampJuly2025/InterviewFlow.git
cd interviewflow
mvn package
java -cp target/InterviewFlow-0.0.1-SNAPSHOT.jar com.bootcamp.interviewflow
```

---

## 💡 Contributing

Contributions and suggestions are welcome! Open an issue or submit a pull request to improve InterviewFlow.

---

## 📄 License

[MIT License](LICENSE)

---

## 📬 Feedback

For bugs, feature requests, or general feedback,
please [open an issue](https://github.com/yourusername/interviewflow/issues) on GitHub.

---
