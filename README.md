# Law Firm Case Management System (CMS)

A comprehensive Spring Boot application for managing legal cases, invoices, hearings, documents, and user roles. Built with Java 17, Spring Boot 3.2, and modern design patterns.

## 🏛️ Project Overview

The Law Firm CMS is an enterprise-grade case management system designed to streamline operations in law firms. It implements robust security, role-based access control, and multiple design patterns for maintainable and scalable code.

### Key Features

- **User Management**: Role-based access control (Admin, Lawyer, Client, Staff)
- **Case Management**: Create, track, and manage legal cases with different types (Civil, Criminal, Corporate)
- **Hearing Scheduling**: Schedule, reschedule, and manage court hearings
- **Invoice Management**: Generate invoices with multiple billing strategies (Hourly, Flat Fee)
- **Document Management**: Upload and manage case documents
- **Notifications**: Real-time event notifications using Observer pattern
- **Security**: Spring Security with password encryption and role-based authorization

---

## 🛠️ Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Framework** | Spring Boot | 3.2.0 |
| **Language** | Java | 17 |
| **Database** | H2 (Development) | In-Memory |
| **ORM** | Hibernate + JPA | Latest |
| **View Engine** | Thymeleaf | 3.x |
| **Security** | Spring Security | 6 |
| **Build Tool** | Maven | 3.8+ |
| **Validation** | Spring Validation | Latest |

---

## 🏗️ Architecture & Design Patterns

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                                │
│                    (Thymeleaf Web UI)                               │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ HTTP Requests
                               ▼
┌──────────────────────────────────────────────────────────────────────┐
│                    CONTROLLER LAYER (MVC)                            │
│  AuthController │ CaseController │ InvoiceController │ HearingCtrl  │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ Uses
                               ▼
┌──────────────────────────────────────────────────────────────────────┐
│                    SERVICE LAYER (Business Logic)                    │
│  CaseService │ InvoiceService │ HearingService │ NotificationService│
└──────────────────────────────┬──────────────────────────────────────┘
                    ┌──────────┴──────────┬──────────┐
                    │                     │          │
                    ▼                     ▼          ▼
        ┌───────────────────┐  ┌──────────────┐  ┌──────────────┐
        │   DESIGN PATTERNS │  │  SECURITY    │  │ DEPENDENCY   │
        ├───────────────────┤  ├──────────────┤  ├──────────────┤
        │ 🏭 Factory        │  │ 🔒 Spring    │  │ 💉 Spring    │
        │ ⚔️  Strategy      │  │    Security  │  │    DI        │
        │ 👁️  Observer      │  │ Role-based   │  │ Constructor  │
        │ 📦 Repository     │  │    Access    │  │  Injection   │
        └───────────────────┘  └──────────────┘  └──────────────┘
                    │
                    ▼
┌──────────────────────────────────────────────────────────────────────┐
│              REPOSITORY LAYER (Data Access)                          │
│  CaseRepository │ InvoiceRepository │ HearingRepository │ UserRepo   │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ JPA/Hibernate
                               ▼
┌──────────────────────────────────────────────────────────────────────┐
│                    DATABASE LAYER                                    │
│               H2 Database (In-Memory)                                │
└──────────────────────────────────────────────────────────────────────┘
```

### Design Patterns Implemented

#### 1. **Factory Pattern** 🏭
- **Location**: `com.lawfirm.cms.factory`
- **Purpose**: Encapsulates case object creation
- **Implementation**: 
  - Interface: `ICaseFactory`
  - Implementation: `CaseFactory`
  - Creates different case types: Civil, Criminal, Corporate
- **Benefit**: Easy to extend with new case types without modifying existing code

#### 2. **Observer Pattern** 👁️
- **Location**: `com.lawfirm.cms.observer`
- **Purpose**: Implements loose coupling for event notifications
- **Implementation**:
  - Subject Interface: `ISubject` (manages observers)
  - Observer Interface: `IObserver` (defines update operations)
  - Concrete Observers: Notification handlers
- **Event Triggers**:
  - Case created → All observers notified
  - Hearing scheduled → Stakeholders notified
  - Invoice generated → Relevant parties notified
- **Benefit**: Easy to add new observers without modifying notification system

#### 3. **Strategy Pattern** ⚔️
- **Location**: `com.lawfirm.cms.strategy`
- **Purpose**: Encapsulates different billing algorithms
- **Implementation**:
  - Interface: `BillingStrategy`
  - Concrete Strategies:
    - `HourlyRateStrategy`: Fee = hours × rate
    - `FlatFeeStrategy`: Fee = fixed rate
- **Runtime Selection**: Switch between strategies based on case requirements
- **Benefit**: Add new billing models (contingency, retainer-based) without modifying existing code

#### 4. **Dependency Injection** 💉
- **Framework**: Spring Framework
- **Implementation**: Constructor injection via `@Autowired`
- **Benefits**: Loose coupling, improved testability

#### 5. **Security Pattern** 🔒
- **Framework**: Spring Security 6
- **Features**:
  - Authentication (Email + Password)
  - Authorization (Role-based access control)
  - CSRF Protection
  - Session Management
  - Password Encryption (BCrypt)

#### 6. **Repository Pattern** 📦
- **Location**: `com.lawfirm.cms.repository`
- **Purpose**: Abstracts data access logic
- **Benefits**: Decouples business logic from database operations

#### 7. **MVC Architecture** 🏛️
- **Model**: Domain entities in `model/` package
- **View**: Thymeleaf templates in `templates/`
- **Controller**: HTTP request handlers in `controller/`

---

## 📁 Project Structure

```
lawfirm-cms/
├── src/main/java/com/lawfirm/cms/
│   ├── CmsApplication.java              (Spring Boot Entry Point)
│   ├── config/
│   │   ├── DataInitializer.java         (Database initialization)
│   │   └── SecurityConfig.java          (Spring Security configuration)
│   ├── controller/                      (MVC Controllers)
│   │   ├── AuthController.java
│   │   ├── CaseController.java
│   │   ├── InvoiceController.java
│   │   ├── HearingController.java
│   │   ├── DocumentController.java
│   │   ├── UserController.java
│   │   ├── NotificationController.java
│   │   └── DashboardController.java
│   ├── factory/                         (Factory Pattern)
│   │   ├── ICaseFactory.java
│   │   └── CaseFactory.java
│   ├── model/                           (Domain Entities)
│   │   ├── User.java
│   │   ├── LegalCase.java
│   │   ├── CivilCase.java
│   │   ├── CriminalCase.java
│   │   ├── CorporateCase.java
│   │   ├── Invoice.java
│   │   ├── Hearing.java
│   │   ├── Document.java
│   │   ├── Notification.java
│   │   └── CaseStatus.java
│   ├── observer/                        (Observer Pattern)
│   │   ├── IObserver.java
│   │   └── ISubject.java
│   ├── repository/                      (Data Access Layer)
│   │   ├── CaseRepository.java
│   │   ├── InvoiceRepository.java
│   │   ├── HearingRepository.java
│   │   ├── DocumentRepository.java
│   │   ├── UserRepository.java
│   │   └── NotificationRepository.java
│   ├── service/                         (Business Logic Layer)
│   │   ├── CaseService.java
│   │   ├── InvoiceService.java
│   │   ├── HearingService.java
│   │   ├── DocumentService.java
│   │   ├── UserService.java
│   │   ├── NotificationService.java
│   │   └── NotificationQueryService.java
│   └── strategy/                        (Strategy Pattern)
│       ├── BillingStrategy.java
│       ├── HourlyRateStrategy.java
│       └── FlatFeeStrategy.java
├── src/main/resources/
│   ├── application.properties           (App configuration)
│   ├── static/
│   │   ├── css/
│   │   │   └── style.css               (CSS styling)
│   │   └── js/
│   └── templates/                       (Thymeleaf templates)
│       ├── login.html
│       ├── register.html
│       ├── dashboard.html
│       ├── fragments/
│       │   └── layout.html             (Base layout)
│       ├── cases/
│       │   ├── list.html
│       │   ├── view.html
│       │   ├── create.html
│       │   └── edit.html
│       ├── invoices/
│       │   ├── list.html
│       │   ├── view.html
│       │   └── create.html
│       ├── hearings/
│       │   ├── list.html
│       │   ├── schedule.html
│       │   └── edit.html
│       ├── documents/
│       │   └── upload.html
│       └── users/
│           ├── list.html
│           ├── create.html
│           └── edit.html
├── pom.xml                              (Maven configuration)
├── mvnw / mvnw.cmd                      (Maven Wrapper)
├── Dockerfile                           (Docker configuration)
├── ARCHITECTURE_AND_DESIGN.md           (Detailed documentation)
└── README.md                            (This file)
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **Maven 3.8+**
- **Git**

### Installation & Setup

1. **Clone the repository**:
```bash
git clone https://github.com/Ishani018/OOAD_miniproject.git
cd lawfirm-cms
```

2. **Build the project**:
```bash
mvn clean install
```

3. **Run the application**:
```bash
mvn spring-boot:run
```

4. **Access the application**:
- Open browser: `http://localhost:8080/login`

### Default User Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@lawfirm.com | admin123 |
| Lawyer | lawyer@lawfirm.com | lawyer123 |
| Client | client@lawfirm.com | client123 |
| Staff | staff@lawfirm.com | staff123 |

---

## 💾 Database

The application uses **H2 in-memory database** for development.

### Access H2 Console:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:lawfirmdb`
- Username: `sa`
- Password: (leave blank)

---

## 🔐 Security Features

- **Authentication**: Email and password-based login
- **Authorization**: Role-based access control (RBAC)
  - **ADMIN**: Full system access
  - **LAWYER**: Manage assigned cases, create invoices
  - **CLIENT**: View own cases and invoices
  - **STAFF**: Manage hearings and documents
- **Password Encryption**: BCrypt hashing
- **CSRF Protection**: Enabled for form submissions
- **Session Management**: Secure session handling

---

## 📋 API Endpoints

### Authentication
- `GET /login` - Login page
- `POST /login` - Submit login credentials
- `POST /logout` - Logout
- `GET /register` - Registration page
- `POST /register` - Register new user

### Dashboard
- `GET /dashboard` - User dashboard

### Cases
- `GET /cases` - List cases
- `GET /cases/{id}` - View case details
- `POST /cases/create` - Create new case
- `POST /cases/{id}/edit` - Edit case

### Invoices
- `GET /invoices` - List invoices
- `GET /invoices/{id}` - View invoice details
- `GET /cases/{caseId}/invoices/create` - Invoice creation form
- `POST /cases/{caseId}/invoices/create` - Generate invoice
- `POST /invoices/{id}/pay` - Mark invoice as paid

### Hearings
- `GET /hearings` - List hearings
- `GET /cases/{caseId}/hearings/schedule` - Hearing form
- `POST /cases/{caseId}/hearings/schedule` - Schedule hearing
- `GET /hearings/{id}/edit` - Edit hearing
- `POST /hearings/{id}/reschedule` - Reschedule hearing
- `POST /hearings/{id}/adjourn` - Adjourn hearing
- `POST /hearings/{id}/complete` - Mark hearing complete

### Documents
- `GET /cases/{caseId}/documents/upload` - Upload form
- `POST /cases/{caseId}/documents/upload` - Upload document
- `POST /documents/{id}/delete` - Delete document

### Users
- `GET /admin/users` - List users
- `GET /admin/users/create` - User creation form
- `POST /admin/users/create` - Create user
- `GET /admin/users/{id}/edit` - Edit user form
- `POST /admin/users/{id}/edit` - Update user
- `POST /admin/users/{id}/delete` - Delete user
- `POST /admin/users/{id}/unlock` - Unlock user account

### Notifications
- `GET /notifications` - List notifications
- `POST /notifications/{id}/read` - Mark notification as read

---

## 🧪 Design Principles

The project follows **SOLID principles**:

1. **S**ingle Responsibility Principle: Each class has one responsibility
2. **O**pen/Closed Principle: Open for extension, closed for modification
3. **L**iskov Substitution Principle: Subtypes are substitutable for base types
4. **I**nterface Segregation Principle: Clients depend on specific interfaces
5. **D**ependency Inversion Principle: Depend on abstractions, not concretions

---

## 📚 Additional Documentation

- **[ARCHITECTURE_AND_DESIGN.md](ARCHITECTURE_AND_DESIGN.md)** - Detailed architecture and design pattern explanations

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👥 Contributors

This project was developed equally by all 4 team members as part of the OOAD Mini Project.

| Contributor | USN | Contribution |
|-------------|-----|--------------|
| **Ishani Chakraborthy** | PES1UG23CS254 | Project architecture, Design Patterns (Factory & Observer), Spring Security configuration, Dashboard & Notification UI |
| **Isha K Vasisht** | PES1UG23CS253 | Service Layer (CaseService, InvoiceService, HearingService), Strategy Pattern (Billing), Invoice management module |
| **K Shaman** | PES1UG23CS272 | Controller Layer (MVC), REST endpoints, Case state machine (activate/reject/close/reopen), Case & Hearing UI |
| **Nandini Inuguru** | PES1UG23CS252 | Data Layer (JPA Entities, Repositories), Database initialisation, Document management, User management module |

### Work Breakdown

#### Ishani Chakraborthy — PES1UG23CS254
- Overall project architecture and module design
- Factory Pattern implementation (`ICaseFactory`, `CaseFactory`)
- Observer Pattern implementation (`IObserver`, `ISubject`, `NotificationService`)
- Spring Security configuration (RBAC, BCrypt, session management)
- Dashboard UI and Notifications UI (Thymeleaf templates)
- UI/CSS design system and frontend styling

#### Isha K Vasisht — PES1UG23CS253
- Strategy Pattern implementation (`BillingStrategy`, `HourlyRateStrategy`, `FlatFeeStrategy`)
- `InvoiceService` — invoice generation, billing logic, payment processing
- `HearingService` — scheduling, rescheduling, adjourn, complete workflows
- Invoice and Hearing Thymeleaf templates (list, view, create)
- Integration testing for service layer

#### K Shaman — PES1UG23CS272
- MVC Controller layer (`CaseController`, `HearingController`, `InvoiceController`)
- Case state machine transitions: UNDER_REVIEW → ACTIVE / REJECTED / CLOSED / REOPENED
- `CaseService` business logic and state validation
- Case UI templates (list, view, create, edit)
- API endpoint design and flash message handling

#### Nandini Inuguru — PES1UG23CS252
- JPA entity modelling (`LegalCase`, `Invoice`, `Hearing`, `Document`, `User`, `Notification`)
- Repository interfaces (`CaseRepository`, `UserRepository`, etc.)
- `DataInitializer` — seed data for all roles and demo cases
- `DocumentService` — file upload, versioning, scan status tracking
- `UserService` & `UserController` — admin user management module

---

## 📧 Contact

For questions or feedback about this project, feel free to reach out to any of the contributors.

---

## 📖 References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Design Patterns in Java](https://refactoring.guru/design-patterns/java)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)

---

**Last Updated**: April 18, 2026
