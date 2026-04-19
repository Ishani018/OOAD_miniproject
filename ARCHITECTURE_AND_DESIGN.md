# Design Principles, and Design Patterns

## MVC Architecture Used? **Yes**

The Law Firm Case Management System implements the **MVC (Model-View-Controller) Architecture** using Spring Boot with the following components:

### MVC Implementation:

- **Model**: 
  - Located in `src/main/java/com/lawfirm/cms/model/` - Contains domain entities (User, Case, Document, Hearing, Invoice, Notification)
  - Data access via JPA Repositories in `src/main/java/com/lawfirm/cms/repository/`

- **View**: 
  - Thymeleaf HTML templates in `src/main/resources/templates/`
  - Includes login, dashboard, case management, document upload, hearing scheduling, and invoice management views
  - Static resources (CSS/JS) in `src/main/resources/static/`

- **Controller**: 
  - Located in `src/main/java/com/lawfirm/cms/controller/`
  - Controllers: AuthController, CaseController, DashboardController, DocumentController, HearingController, InvoiceController, NotificationController, UserController
  - Handles HTTP requests and coordinates between Model and View

---

## Design Principles

### 1. **Single Responsibility Principle (SRP)**
Each class has a single, well-defined responsibility:
- **Controllers** handle HTTP requests and routing
- **Services** contain business logic
- **Repositories** manage data persistence
- **Factories** handle object creation
- **Observers** handle event notifications

### 2. **Open/Closed Principle (OCP)**
The system is open for extension but closed for modification:
- **Strategy Pattern** for billing calculations allows adding new billing strategies without modifying existing code
- **Observer Pattern** allows adding new observers (notification handlers) without changing the core notification system
- **Factory Pattern** allows adding new case types without modifying existing case creation logic

### 3. **Liskov Substitution Principle (LSP)**
Interfaces are properly implemented to ensure substitutability:
- `BillingStrategy` interface with multiple implementations (HourlyRateStrategy, FlatFeeStrategy)
- `IObserver` interface with various observer implementations
- `ICaseFactory` for factory implementations

### 4. **Interface Segregation Principle (ISP)**
Interfaces are focused and client-specific:
- `IObserver` has specific update method for notification handling
- `ISubject` manages observer subscriptions
- `BillingStrategy` focuses only on fee calculation

### 5. **Dependency Inversion Principle (DIP)**
High-level modules depend on abstractions, not concrete implementations:
- Services depend on Repository interfaces, not concrete implementations
- Controllers depend on Service interfaces
- Uses Spring's dependency injection to manage dependencies

### 6. **DRY (Don't Repeat Yourself)**
Code reusability through:
- Shared layout fragments in `fragments/layout.html`
- Service layer consolidates business logic
- Base configurations in SecurityConfig and DataInitializer

### 7. **KISS (Keep It Simple, Stupid)**
- Clear separation of concerns
- Straightforward class hierarchies
- Non-complex inheritance structures

---

## Design Patterns

### Design Patterns Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          CONTROLLER LAYER                                    │
│    AuthController │ CaseController │ InvoiceController │ HearingController   │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        SERVICE LAYER                                         │
│         CaseService │ InvoiceService │ HearingService │ NotificationService │
└────────────────────────────┬────────────────────────────────────────────────┘
                ┌────────────┼────────────┬──────────────┐
                │            │            │              │
                ▼            ▼            ▼              ▼
        ┌──────────────┐ ┌──────────┐ ┌───────────┐ ┌─────────────┐
        │   🏭 FACTORY│ │⚔️STRATEGY│ │👁️OBSERVER│ │📦REPOSITORY│
        ├──────────────┤ ├──────────┤ ├───────────┤ ├─────────────┤
        │ ICaseFactory │ │Billing   │ │IObserver  │ │JPA          │
        │              │ │Strategy  │ │ISubject   │ │Repositories│
        │ CaseFactory  │ │          │ │           │ │             │
        │              │ │Hourly    │ │Notif      │ │ Case        │
        │Creates:      │ │RateStrat │ │System     │ │ Invoice     │
        │ CivilCase    │ │          │ │           │ │ Hearing     │
        │ CriminalCase │ │FlatFee   │ │Event-     │ │ Document    │
        │ CorporateCase│ │Strategy  │ │Driven     │ │ User        │
        └──────────────┘ └──────────┘ └───────────┘ └─────────────┘
                │            │            │              │
                └────────────┼────────────┴──────────────┘
                             │
                             ▼
        ┌──────────────────────────────────────────┐
        │    💉 DEPENDENCY INJECTION                │
        │  (Spring Framework - @Service, @Repo)   │
        └──────────────────────────────────────────┘
                             │
                             ▼
        ┌──────────────────────────────────────────┐
        │    🔒 SECURITY PATTERN                    │
        │  (Spring Security - Role-based Access)  │
        └──────────────────────────────────────────┘
                             │
                             ▼
        ┌──────────────────────────────────────────┐
        │    📦 MVC PATTERN                         │
        │  (Model-View-Controller)                 │
        │  Model: Entities  │ View: Thymeleaf     │
        │  Controller: HTTP Handlers               │
        └──────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          DATABASE LAYER                                      │
│               H2 Database (JPA/Hibernate ORM)                               │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Design Patterns at a Glance

| Pattern | Type | Purpose | Location |
|---------|------|---------|----------|
| **🏭 Factory** | Creational | Encapsulates object creation | `factory/` |
| **⚔️ Strategy** | Behavioral | Encapsulates billing algorithms | `strategy/` |
| **👁️ Observer** | Behavioral | Event notification system | `observer/` |
| **💉 Dependency Injection** | Structural | Loose coupling via injection | Framework-wide |
| **🔒 Security** | Behavioral | Authentication & Authorization | `config/SecurityConfig` |
| **📦 Repository** | Data Access | Abstraction for data operations | `repository/` |
| **🏛️ MVC** | Architectural | Separation of concerns | Full application |

---

### 1. **Factory Pattern**
**Location**: `com.lawfirm.cms.factory` package

**Implementation**:
- **Interface**: `ICaseFactory` - defines contract for creating Case objects
- **Concrete Factory**: `CaseFactory` - implements case creation logic
- **Purpose**: Encapsulates case object creation, allowing different case types to be created without exposing creation logic to clients
- **Benefits**: Centralizes object creation, makes the system extensible for new case types

**Usage**: Controllers use CaseFactory to create new cases instead of directly instantiating them

### 2. **Observer Pattern**
**Location**: `com.lawfirm.cms.observer` package

**Implementation**:
- **Subject Interface**: `ISubject` - manages observers (subscribe, unsubscribe, notify)
- **Observer Interface**: `IObserver` - defines observer update method
- **Concrete Subject**: Various notification sources in the system
- **Concrete Observers**: Notification handlers that react to case updates, hearing schedules, etc.

**Purpose**: Implements loose coupling between the notification system and multiple handlers. When events occur (case creation, hearing scheduled, invoice generated), all registered observers are notified without direct dependencies.

**Example Use Cases**:
- When a case is created, all observers (email notifier, SMS notifier, dashboard notifier) are notified
- When a hearing is scheduled, interested parties receive notifications
- When an invoice is generated, relevant observers are triggered

**Benefits**: 
- Decoupled event producers from event consumers
- Easy to add new observers without modifying existing code
- Supports one-to-many relationships between objects

### 3. **Strategy Pattern**
**Location**: `com.lawfirm.cms.strategy` package

**Implementation**:
- **Strategy Interface**: `BillingStrategy` - defines contract for billing calculations
- **Concrete Strategies**:
  - `HourlyRateStrategy` - calculates fees based on hourly rates
  - `FlatFeeStrategy` - calculates fixed fees
- **Context**: Services and Controllers use strategies to calculate invoices

**Purpose**: Encapsulates different billing algorithms, allowing them to be interchangeable. The system can switch between hourly billing and flat fee billing at runtime without code changes.

**Usage**: 
```
BillingStrategy strategy = new HourlyRateStrategy(); // or FlatFeeStrategy
double fee = strategy.calculateFee(hours, rate);
```

**Benefits**:
- Easy to add new billing strategies (e.g., contingency fees, retainer-based billing)
- No modification to existing billing code when adding strategies
- Runtime strategy selection based on case requirements

### 4. **Dependency Injection (Spring Framework)**
**Location**: Throughout the application via Spring annotations (`@Autowired`, `@Service`, `@Repository`, `@Controller`)

**Purpose**: Provides loose coupling by injecting dependencies rather than having classes create their own

**Benefits**: Improves testability and maintainability

### 5. **Security Pattern (Spring Security)**
**Location**: `com.lawfirm.cms.config.SecurityConfig`

**Purpose**: Implements authentication and authorization using Spring Security framework

### 6. **MVC Pattern**
Already covered above - the primary architectural pattern

### 7. **Repository Pattern**
**Location**: `com.lawfirm.cms.repository` package - JPA Repositories

**Purpose**: Abstracts data access logic, providing a collection-like interface to access domain objects

---

## Design Patterns in Action - Real Use Cases

### Use Case 1: Creating an Invoice

```
USER REQUEST (Create Invoice)
        │
        ▼
┌──────────────────────────────┐
│  🔒 SECURITY PATTERN         │
│  Verify User Role (LAWYER)   │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│  InvoiceController           │
│  (MVC CONTROLLER)            │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│  💉 Dependency Injection     │
│  Injects InvoiceService      │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────────────┐
│  InvoiceService (BUSINESS LOGIC)    │
│  1. Validates input                  │
│  2. Selects Strategy                 │
└──────────────┬──────────────────────┘
               │
         ┌─────┴─────┐
         │           │
         ▼           ▼
    ┌─────────┐  ┌──────────┐
    │ HOURLY? │  │ FLAT FEE?│
    ├─────────┤  ├──────────┤
    │ Use     │  │ Use      │
    │Hourly   │  │FlatFee   │
    │Strategy │  │Strategy  │
    └──┬──────┘  └──┬───────┘
       │            │
       └─────┬──────┘
             │
             ▼
      Calculate Fee
      (⚔️ STRATEGY PATTERN)
             │
             ▼
┌──────────────────────────────┐
│  👁️ OBSERVER PATTERN         │
│  Notify All Observers:       │
│  - Email Notifier            │
│  - SMS Notifier              │
│  - Dashboard Notifier        │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│  📦 REPOSITORY PATTERN       │
│  InvoiceRepository.save()    │
└──────────────┬───────────────┘
               │
               ▼
        H2 DATABASE
     (Invoice Stored)
```

### Use Case 2: Creating a Case

```
USER REQUEST (Create Civil Case)
        │
        ▼
┌──────────────────────────────┐
│  🔒 SECURITY PATTERN         │
│  Verify User Role (ADMIN)    │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│  CaseController              │
│  (MVC CONTROLLER)            │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│  💉 Dependency Injection     │
│  Injects CaseService         │
│  & CaseFactory               │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────────────┐
│  CaseService (BUSINESS LOGIC)       │
│  Calls: CaseFactory.createCase()    │
└──────────────┬──────────────────────┘
               │
               ▼
┌──────────────────────────────────────┐
│  🏭 FACTORY PATTERN                  │
│  CaseFactory.createCase("CIVIL", ..)│
│  - Determines case type              │
│  - Creates CivilCase object          │
│  - Sets properties                   │
└──────────────┬──────────────────────┘
               │
               ▼
          Return LegalCase
               │
               ▼
┌──────────────────────────────┐
│  👁️ OBSERVER PATTERN         │
│  Notify All Observers:       │
│  - Case Created Event        │
│  - Notify Participants       │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│  📦 REPOSITORY PATTERN       │
│  CaseRepository.save()       │
└──────────────┬───────────────┘
               │
               ▼
        H2 DATABASE
      (Case Stored)
```

### Pattern Interaction Matrix

```
┌─────────────────────────────────────────────────────────────────────┐
│                    PATTERN INTERACTIONS                              │
├─────────────────┬──────────────────────────────────────────────────┤
│ FACTORY         │ Uses: Dependency Injection                        │
│ STRATEGY        │ Uses: Dependency Injection, Observer              │
│ OBSERVER        │ Uses: Dependency Injection                        │
│ DEPENDENCY INJ. │ Underlies: All other patterns                     │
│ SECURITY        │ Wraps: All HTTP endpoints                         │
│ REPOSITORY      │ Uses: Dependency Injection                        │
│ MVC             │ Uses: All patterns together                       │
└─────────────────┴──────────────────────────────────────────────────┘
```

---

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: H2 (in-memory database for development)
- **ORM**: Hibernate with Spring Data JPA
- **View Engine**: Thymeleaf
- **Security**: Spring Security 6
- **Build Tool**: Maven
- **Validation**: Spring Validation
- **UI Framework**: Bootstrap (via CSS/JS in static resources)

---

## Project Structure

```
lawfirm-cms/
├── src/main/java/com/lawfirm/cms/
│   ├── CmsApplication.java          (Entry point)
│   ├── config/                      (Configuration classes)
│   ├── controller/                  (MVC Controllers)
│   ├── factory/                     (Factory Pattern)
│   ├── model/                       (Domain entities)
│   ├── observer/                    (Observer Pattern)
│   ├── repository/                  (Data access)
│   ├── service/                     (Business logic)
│   └── strategy/                    (Strategy Pattern)
├── src/main/resources/
│   ├── application.properties       (Application configuration)
│   ├── static/                      (CSS, JS files)
│   └── templates/                   (Thymeleaf HTML templates)
└── pom.xml                          (Maven configuration)
```

---

## Github Link to the Codebase

https://github.com/

*(Note: Repository link should be updated to your actual GitHub repository once created)*
