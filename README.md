# InterviewFlow

**InterviewFlow** is a Backend API for simple and effective application designed to help jobseekers organize and manage
all their job
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
- Liquibase (for DB migrations)

### Project Profiles and Configuration

The project comes with two profiles:

* **`dev`** – used for local development and testing
* **`prod`** – intended for deployment on AWS

By default, the `dev` profile is active, so you can start coding and testing right away without any extra setup.

To keep things clean and centralized, the project uses a **`.env` file** for storing all service configuration values. 
Now `.env` file contains default settings, just for local development.

To fill up `application.properties` values run in terminal

```bash
POSTGRES_HOST=localhost \
POSTGRES_PORT=5432 \
POSTGRES_DB=interview_flow_db \
POSTGRES_USER=postgres \
POSTGRES_PASSWORD=postgres \
./mvnw spring-boot:run
```

or obviously put following in "Edit Configuration" → "Environment Variables" if you use IntelliJ IDEA

```text 
POSTGRES_DB=interview_flow_db;POSTGRES_USER=postgres;POSTGRES_PASSWORD=postgres;POSTGRES_HOST=localhost;POSTGRES_PORT=5432
```

`docker-compose.yml` automatically pulls settings from `.env` file.

## 💾 Data Model

To be confirmed

- **`users` table**: `id`, `username`, `password`, `email`, `created_at`, `updated_at`
- **`applications` table**: `id`, `user_id`, `status`, `company_name`, `company_link`, `position`, `applied_at`,
  `created_at`, `updated_at`
- **`notes` table**: `id`, `application_id`, `content`, `created_at`, `updated_at`

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
# Launch with dev profile
java -Dspring.profiles.active=dev -jar target/InterviewFlow-0.0.1-SNAPSHOT.jar
```

Once the application has been launched for the first time, it is necessary to initiate the creation of default bucket for the S3 service to work, this only needs to be done once

```bash
docker-compose -f docker-compose.yml -f docker-compose.init.yml up minio-init
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
