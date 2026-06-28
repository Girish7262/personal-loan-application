# Personal Loan Application System
## Technical Architecture Document (TAD)

| Property | Value |
|---|---|
| **Document Type** | Technical Architecture Document |
| **Project** | Personal Loan Application System |
| **Version** | 1.0 |
| **Status** | Draft — Ready for Review |
| **Author Role** | Senior Solution Architect / Java Technical Lead |
| **Related Documents** | BRS v1.0, API Design v1.0, API Specification v1.0, DB Design v1.0 |
| **Date** | 2026-06-28 |

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Technology Stack](#2-technology-stack)
3. [System Architecture](#3-system-architecture)
4. [Project Folder Structure](#4-project-folder-structure)
5. [Microservice vs Monolithic Recommendation](#5-microservice-vs-monolithic-recommendation)
6. [Backend Layered Architecture](#6-backend-layered-architecture)
7. [Frontend Architecture](#7-frontend-architecture)
8. [Security Architecture](#8-security-architecture)
9. [File Upload Architecture](#9-file-upload-architecture)
10. [Notification Architecture](#10-notification-architecture)
11. [Logging](#11-logging)
12. [Exception Handling](#12-exception-handling)
13. [Database Strategy](#13-database-strategy)
14. [Deployment Architecture](#14-deployment-architecture)
15. [Cross-Cutting Concerns & Open Decisions](#15-cross-cutting-concerns--open-decisions)

---

## 1. Executive Summary

This document defines the technical architecture for the **Personal Loan Application System** — a secure, scalable platform enabling customers to apply for personal loans online and supporting internal banking workflows (Maker-Checker approval, sanction, disbursement).

The architecture is designed for **Version 1.0** delivery with:

- A **modular monolithic backend** (Spring Boot) aligned with existing API and database designs
- A **separate SPA frontend** for customers and internal staff
- **MySQL 8+** as the system of record
- **JWT-based security** with role-based access control (RBAC)
- **Pluggable integrations** for file storage, email, and SMS (stubbed in v1 where third-party contracts are pending)

This TAD resolves known inconsistencies across source documents (status model, edit rules) and establishes a single authoritative technical baseline for development.

---

## 2. Technology Stack

### 2.1 Backend

| Layer | Technology | Version | Rationale |
|---|---|---|---|
| Language | Java | 21 LTS | Long-term support, modern language features, industry standard for banking systems |
| Framework | Spring Boot | 3.3.x | Aligns with API docs (JPA, Bean Validation, Security); mature ecosystem |
| Web | Spring Web MVC | 6.x (via Boot) | RESTful APIs per API Design Document |
| Security | Spring Security + JWT | 6.x | JWT Bearer auth, RBAC as specified |
| Persistence | Spring Data JPA / Hibernate | 6.x | Parameterized queries; SQL injection prevention per API security spec |
| Validation | Jakarta Bean Validation | 3.x | Controller-layer validation per API Specification |
| Database Migration | Flyway | 10.x | Version-controlled DDL; repeatable deployments |
| API Documentation | SpringDoc OpenAPI | 2.x | Swagger UI for 55+ endpoints |
| Mapping | MapStruct | 1.5.x | DTO ↔ Entity mapping; compile-time safety |
| Build Tool | Maven | 3.9.x | Standard enterprise Java build |
| Testing | JUnit 5, Mockito, Testcontainers | Latest stable | Unit, integration, and DB-backed tests |

### 2.2 Frontend

| Layer | Technology | Version | Rationale |
|---|---|---|---|
| Framework | React | 18.x | Component model, large ecosystem, suitable for multi-role portals |
| Language | TypeScript | 5.x | Type safety for complex loan workflows and API contracts |
| Build Tool | Vite | 5.x | Fast dev experience |
| Routing | React Router | 6.x | Role-based route guards |
| State Management | TanStack Query (React Query) | 5.x | Server state, caching, pagination |
| UI Library | Material UI (MUI) | 5.x | Enterprise-grade components for forms, tables, dashboards |
| Forms | React Hook Form + Zod | Latest | Client-side validation aligned with API field rules |
| HTTP Client | Axios | 1.x | Interceptors for JWT refresh and error handling |
| Auth Storage | In-memory access token + HttpOnly cookie (refresh) | — | Mitigates XSS on access token |

**Alternative (acceptable):** Angular 17+ if the organization has an existing Angular practice. React is recommended for faster iteration on customer-facing flows.

### 2.3 Database & Caching

| Component | Technology | Version | Rationale |
|---|---|---|---|
| Primary Database | MySQL | 8.0+ | Per DB Design Document; InnoDB, utf8mb4 |
| Connection Pool | HikariCP | (via Boot) | Default high-performance pool |
| Cache (optional v1) | Redis | 7.x | Session blacklist, rate limiting, notification dedup (Phase 1.1) |

### 2.4 File Storage

| Environment | Technology | Rationale |
|---|---|---|
| Development | Local filesystem | Per DB Design Document |
| Production | AWS S3 (or Azure Blob Storage) | Scalable, durable document storage |

### 2.5 Notifications

| Channel | Technology (v1) | Rationale |
|---|---|---|
| Email | AWS SES or SendGrid | Transactional email for registration, approval, rejection |
| SMS | Twilio or MSG91 (India) | OTP and status alerts |
| In-App | MySQL `notifications` table + REST API | Already defined in API spec |

### 2.6 DevOps & Infrastructure

| Component | Technology | Rationale |
|---|---|---|
| Containerization | Docker | Consistent environments |
| Orchestration (prod) | AWS ECS / Azure App Service / Kubernetes | Team-dependent; ECS/App Service for simpler v1 |
| CI/CD | GitHub Actions / GitLab CI | Automated build, test, deploy |
| Secrets | AWS Secrets Manager / Azure Key Vault | JWT secret, DB credentials, API keys |
| Reverse Proxy | Nginx / AWS ALB | TLS termination, routing |
| Monitoring | Prometheus + Grafana (or CloudWatch) | Metrics and dashboards |
| Log Aggregation | ELK Stack / CloudWatch Logs | Centralized log search |
| Error Tracking | Sentry (optional) | Frontend and backend exception tracking |

### 2.7 Document & Integration (Future / Stub in v1)

| Integration | v1 Approach | Future |
|---|---|---|
| PAN Verification | Manual officer verification | NSDL / third-party API |
| Aadhaar Verification | Manual | UIDAI-compliant provider |
| Credit Score (CIBIL) | Manual entry / officer reference | CIBIL / Experian API |
| Payment Gateway | Out of v1 scope | Razorpay / bank APIs |
| Digital Signature | Out of v1 scope | eSign provider |

---

## 3. System Architecture

### 3.1 High-Level Context Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              EXTERNAL ACTORS                                 │
├──────────────┬──────────────┬──────────────┬──────────────┬─────────────────┤
│   Customer   │ Loan Officer │Credit Manager│Finance Team  │     Admin       │
└──────┬───────┴──────┬───────┴──────┬───────┴──────┬───────┴────────┬────────┘
       │              │              │              │                │
       └──────────────┴──────────────┴──────────────┴────────────────┘
                                      │
                                      ▼
                         ┌────────────────────────┐
                         │   Web Browser (SPA)    │
                         │  React + TypeScript    │
                         └───────────┬────────────┘
                                     │ HTTPS / REST (JSON)
                                     ▼
                         ┌────────────────────────┐
                         │   API Gateway / LB     │
                         │   (TLS, Rate Limit)    │
                         └───────────┬────────────┘
                                     │
                                     ▼
              ┌──────────────────────────────────────────────┐
              │     Personal Loan Application Backend         │
              │     (Modular Monolith — Spring Boot)          │
              ├──────────────────────────────────────────────┤
              │  Auth │ Customer │ Loan │ Document │ Approval │
              │  EMI  │ Notification │ Admin │ Audit           │
              └──────┬───────────────┬───────────────┬───────┘
                     │               │               │
         ┌───────────▼───┐   ┌───────▼───────┐  ┌────▼─────┐
         │   MySQL 8+    │   │  File Store  │  │ Email/SMS│
         │  (11 tables)  │   │ Local / S3   │  │ Providers│
         └───────────────┘   └──────────────┘  └──────────┘
```

### 3.2 Module Boundaries (Logical)

The backend is a **single deployable unit** with **eight bounded modules** matching the API Specification:

| Module | Base Path | Responsibility |
|---|---|---|
| Authentication | `/api/v1/auth` | Registration, login, JWT, password management |
| Customer | `/api/v1/customers` | Profile CRUD, KYC data |
| Loan | `/api/v1/loans` | Application lifecycle, status transitions |
| Document | `/api/v1/documents` | Upload, metadata, download |
| Approval | `/api/v1/approvals` | Maker-Checker workflow |
| EMI | `/api/v1/emi` | Calculation, schedule generation |
| Notification | `/api/v1/notifications` | In-app notifications, event dispatch |
| Admin | `/api/v1/admin` | Users, loan types, reports, audit logs |

### 3.3 Canonical Loan Status Model (Resolved)

The following status model is authoritative for v1, reconciling BRS, API, and DB documents:

```
DRAFT ──submit──► SUBMITTED ──pickup──► UNDER_VERIFICATION ──verify──► VERIFIED
                                                                          │
                                    ┌─────────────────────────────────────┤
                                    ▼                                     ▼
                               APPROVED                              REJECTED
                                    │
                                    ▼
                              SANCTIONED
                                    │
                                    ▼
                               DISBURSED ──repaid──► CLOSED

DRAFT / SUBMITTED ──cancel──► CANCELLED
```

**Edit rule (resolved):** Customer may edit loan application only in `DRAFT` status. Once `SUBMITTED`, the application is locked until rejected or cancelled.

### 3.4 Key Integration Flows

**Loan Submission Flow:**
```
Customer → Frontend → POST /loans → LoanService → Validate profile + FOIR
  → Create loan (DRAFT) → Customer uploads docs → PATCH /loans/{id}/submit
  → Validate mandatory docs → Status = SUBMITTED → Publish LoanSubmittedEvent
  → NotificationService → Email/SMS/In-App
```

**Maker-Checker Flow:**
```
Loan Officer → POST /approvals/{id}/verify → Status = VERIFIED
Credit Manager → POST /approvals/{id}/approve|reject → Status = APPROVED|REJECTED
Finance Officer → POST /approvals/{id}/sanction → Status = SANCTIONED
Finance Officer → POST /approvals/{id}/disburse → Status = DISBURSED
  → EMIService generates schedule → NotificationService notifies customer
```

### 3.5 Communication Patterns

| Pattern | Usage |
|---|---|
| Synchronous REST | All client ↔ backend interactions |
| In-process Spring Events | Decouple notification triggers from business logic (v1) |
| Async `@Async` / thread pool | Email/SMS dispatch (non-blocking to API response) |
| Message Queue (future) | Kafka/RabbitMQ when scaling notification and audit workloads |

---

## 4. Project Folder Structure

### 4.1 Repository Layout (Monorepo Recommended for v1)

```
personal-loan-application/
├── README.md
├── docker-compose.yml                 # Local MySQL, Redis (optional), backend, frontend
├── .github/
│   └── workflows/
│       ├── backend-ci.yml
│       └── frontend-ci.yml
│
├── docs/                              # Project documentation
│   ├── Loan_Application_Version_1.0.pdf
│   ├── API_Design_Document.pdf
│   ├── API_Specification_Document.pdf
│   ├── Personal_Loan_DB_Design.pdf
│   └── Technical_Architecture_Document.md
│
├── backend/                           # Spring Boot application
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
│       ├── main/
│       │   ├── java/com/personalloan/
│       │   │   ├── PersonalLoanApplication.java
│       │   │   ├── config/            # Security, CORS, OpenAPI, Async, Storage
│       │   │   ├── common/            # Shared utilities, constants, base classes
│       │   │   │   ├── dto/           # ApiResponse, PageResponse, ErrorResponse
│       │   │   │   ├── exception/     # Custom exceptions, GlobalExceptionHandler
│       │   │   │   ├── security/      # JWT filter, UserDetails, SecurityUtils
│       │   │   │   ├── audit/         # AuditAspect, AuditLogService
│       │   │   │   └── util/          # EMI calculator, validators
│       │   │   ├── module/
│       │   │   │   ├── auth/
│       │   │   │   │   ├── controller/
│       │   │   │   │   ├── service/
│       │   │   │   │   ├── repository/
│       │   │   │   │   ├── entity/
│       │   │   │   │   ├── dto/
│       │   │   │   │   └── mapper/
│       │   │   │   ├── customer/
│       │   │   │   ├── loan/
│       │   │   │   ├── document/
│       │   │   │   ├── approval/
│       │   │   │   ├── emi/
│       │   │   │   ├── notification/
│       │   │   │   └── admin/
│       │   │   └── event/             # Domain events (LoanSubmittedEvent, etc.)
│       │   └── resources/
│       │       ├── application.yml
│       │       ├── application-dev.yml
│       │       ├── application-prod.yml
│       │       ├── db/migration/      # Flyway: V1__init_schema.sql, etc.
│       │       └── logback-spring.xml
│       └── test/
│           ├── java/                  # Unit + integration tests
│           └── resources/
│
└── frontend/                          # React SPA
    ├── package.json
    ├── vite.config.ts
    ├── Dockerfile
    ├── nginx.conf                     # Production static serving
    └── src/
        ├── main.tsx
        ├── App.tsx
        ├── api/                       # Axios client, endpoint modules
        ├── auth/                      # Auth context, guards, token management
        ├── components/                # Shared UI components
        ├── features/                  # Feature-based modules
        │   ├── customer-portal/
        │   │   ├── registration/
        │   │   ├── profile/
        │   │   ├── loan-application/
        │   │   ├── documents/
        │   │   └── emi-calculator/
        │   ├── officer-portal/
        │   │   ├── verification/
        │   │   └── pending-loans/
        │   ├── manager-portal/
        │   │   └── approval/
        │   ├── finance-portal/
        │   │   ├── sanction/
        │   │   └── disbursement/
        │   └── admin-portal/
        │       ├── users/
        │       ├── loan-types/
        │       ├── reports/
        │       └── audit-logs/
        ├── hooks/
        ├── routes/                    # Role-based routing
        ├── types/                     # TypeScript interfaces (API contracts)
        └── utils/
```

### 4.2 Backend Package Naming Convention

| Layer | Package Pattern | Example |
|---|---|---|
| Controller | `...module.{name}.controller` | `LoanController` |
| Service | `...module.{name}.service` | `LoanApplicationService` |
| Repository | `...module.{name}.repository` | `LoanApplicationRepository` |
| Entity | `...module.{name}.entity` | `LoanApplication` |
| DTO | `...module.{name}.dto` | `LoanApplicationRequest` |
| Mapper | `...module.{name}.mapper` | `LoanApplicationMapper` |

Aligns with DB Design Document Java/Spring naming conventions.

---

## 5. Microservice vs Monolithic Recommendation

### 5.1 Recommendation: **Modular Monolith (v1)**

| Criterion | Assessment |
|---|---|
| Team size | Likely small-to-medium; monolith reduces operational overhead |
| Domain complexity | Single bounded context (personal loans); tightly coupled workflow |
| Transaction boundaries | Loan approval requires ACID across loan, approval, status, EMI tables |
| API count | 55+ endpoints — manageable in one service |
| Time to market | Monolith is faster for v1 |
| Existing design | Single base URL, single database — already monolith-oriented |

### 5.2 Modular Monolith Design Principles

- **Strict module boundaries** — no cross-module repository access; interact via service interfaces only
- **Separate packages per module** — enables future extraction
- **Shared kernel** — common DTOs, security, exception handling, audit in `common/`
- **Domain events** — in-process events prepare for async messaging later

### 5.3 Future Extraction Candidates (v2+)

| Service | Trigger for Extraction |
|---|---|
| Notification Service | High email/SMS volume; independent scaling needed |
| Document Service | Large file processing; virus scan pipeline |
| Integration Service | PAN/Aadhaar/CIBIL API orchestration |
| Reporting Service | Heavy analytics queries impacting OLTP |

### 5.4 When to Revisit

Re-evaluate microservices when:

- Independent teams own distinct modules
- Notification or document workloads require separate scaling
- Regulatory requirements mandate network isolation for PII/document processing
- Deployment frequency differs significantly across modules

---

## 6. Backend Layered Architecture

### 6.1 Layer Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│  REST Controllers │ Request/Response DTOs │ Validation  │
│  OpenAPI Annotations │ @PreAuthorize RBAC                 │
└────────────────────────────┬────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────┐
│                     Application Layer                    │
│  Services │ Business Logic │ Transaction Management     │
│  Domain Events │ Orchestration │ FOIR/EMI Rules          │
└────────────────────────────┬────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────┐
│                      Domain Layer                        │
│  Entities │ Enums │ Domain Validators │ Business Rules   │
└────────────────────────────┬────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────┐
│                   Infrastructure Layer                   │
│  JPA Repositories │ File Storage Adapter │ Email/SMS     │
│  Audit Logger │ External API Clients (future)            │
└─────────────────────────────────────────────────────────┘
```

### 6.2 Layer Responsibilities

| Layer | Responsibility | Rules |
|---|---|---|
| **Controller** | HTTP mapping, input validation (`@Valid`), auth annotations, response wrapping | No business logic; no direct repository access |
| **Service** | Business rules, status transitions, FOIR/EMI calculation, event publishing | `@Transactional` boundaries; throws domain exceptions |
| **Repository** | Data access via Spring Data JPA | Custom queries only when necessary; no business logic |
| **Entity** | JPA-mapped domain objects | Lazy loading awareness; audit columns |
| **DTO** | API contract objects | Separate request/response DTOs; never expose entities |
| **Mapper** | Entity ↔ DTO conversion | MapStruct; mask PAN/Aadhaar in responses |

### 6.3 Standard API Response Envelope

All endpoints return the wrapper defined in the API Design Document:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { },
  "timestamp": "2026-06-28T10:00:00Z",
  "statusCode": 200
}
```

Implemented via a `ResponseAdvice` or explicit `ApiResponse<T>` builder in the controller layer.

### 6.4 Key Service Interactions

| Service | Collaborators |
|---|---|
| `AuthService` | `UserRepository`, `JwtTokenProvider`, `EmailService` |
| `CustomerProfileService` | `CustomerProfileRepository`, `UserRepository` |
| `LoanApplicationService` | `LoanRepository`, `CustomerProfileService`, `DocumentService`, `EligibilityService`, `LoanStatusHistoryService` |
| `DocumentService` | `DocumentRepository`, `FileStorageService`, `LoanApplicationService` |
| `ApprovalService` | `LoanApplicationService`, `ApprovalHistoryRepository`, `NotificationService` |
| `EmiService` | Pure calculation + `EmiScheduleRepository` |
| `NotificationService` | `NotificationRepository`, `EmailService`, `SmsService` |
| `AdminService` | `UserRepository`, `AuditLogRepository`, `LoanTypeRepository` |

### 6.5 Transaction Strategy

- **Write operations:** `@Transactional` on service methods
- **Read operations:** `@Transactional(readOnly = true)` for list/detail queries
- **Status transitions:** Optimistic locking via `@Version` on `loan_application` to prevent concurrent approval conflicts
- **Audit + status history:** Written in the same transaction as the status change

---

## 7. Frontend Architecture

### 7.1 Portal Strategy

A **single SPA** with **role-based views** rather than separate applications:

| Portal | Roles | Key Screens |
|---|---|---|
| Customer Portal | `CUSTOMER` | Register, Login, Profile, Eligibility Check, Apply Loan, Upload Docs, Track Status, EMI Calculator |
| Officer Portal | `LOAN_OFFICER` | Pending Applications, Verify Loan, Document Review |
| Manager Portal | `CREDIT_MANAGER` | Verified Loans Queue, Approve/Reject |
| Finance Portal | `FINANCE_OFFICER` | Sanction, Disburse, EMI Schedule View |
| Admin Portal | `ADMIN` | User Management, Loan Types, Reports, Audit Logs |

### 7.2 Frontend Layer Diagram

```
┌─────────────────────────────────────────────────────────┐
│                      Route Layer                         │
│  React Router │ Role Guards │ Lazy-loaded Feature Routes │
└────────────────────────────┬────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────┐
│                    Feature Modules                       │
│  Pages │ Feature Components │ Feature Hooks              │
└────────────────────────────┬────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────┐
│                   Shared / Core Layer                    │
│  API Client │ Auth Context │ UI Components │ Utils       │
└─────────────────────────────────────────────────────────┘
```

### 7.3 Authentication Flow

```
1. Login → POST /api/v1/auth/login
2. Store accessToken in memory (React context)
3. Store refreshToken in HttpOnly Secure cookie (set by backend)
4. Axios interceptor attaches: Authorization: Bearer {accessToken}
5. On 401 → POST /api/v1/auth/refresh-token → retry original request
6. On refresh failure → redirect to login
7. Route guards check JWT role claim for portal access
```

### 7.4 State Management Strategy

| State Type | Approach |
|---|---|
| Server state (API data) | TanStack Query — caching, pagination, refetch on focus |
| Auth state | React Context + in-memory token |
| Form state | React Hook Form (local) |
| UI state (modals, tabs) | Component-local `useState` |

### 7.5 Key Frontend Concerns

| Concern | Approach |
|---|---|
| Validation | Zod schemas mirroring API Specification field rules |
| File upload | Multipart form via Axios; progress indicator; 5MB limit enforced client-side |
| Pagination | Reusable `DataTable` component; page/size/sort query params |
| Error display | Map API `errors[]` array to form field errors |
| Sensitive data | PAN/Aadhaar displayed masked (server returns masked values) |
| Responsive design | Mobile-first for customer portal; desktop-optimized for internal portals |

---

## 8. Security Architecture

### 8.1 Security Layer Diagram

```
┌──────────────┐     HTTPS/TLS 1.2+     ┌──────────────────────┐
│   Browser    │ ◄──────────────────► │  Reverse Proxy / LB  │
└──────────────┘                       └──────────┬───────────┘
                                                  │
                       ┌──────────────────────────▼──────────────────────────┐
                       │              Spring Security Filter Chain            │
                       ├─────────────────────────────────────────────────────┤
                       │  CORS Filter → Rate Limit Filter → JWT Auth Filter  │
                       │  → RBAC (@PreAuthorize) → Controller                │
                       └─────────────────────────────────────────────────────┘
```

### 8.2 Authentication

| Mechanism | Detail |
|---|---|
| Protocol | JWT Bearer Token |
| Access Token TTL | 24 hours (86400 seconds) |
| Refresh Token TTL | 7 days |
| Algorithm | HS256 (v1); RS256 recommended for multi-service future |
| Password Hashing | BCrypt, strength 10 |
| Account Lockout | 5 consecutive failed logins → status `LOCKED` |
| Email Verification | Required before first login (account `INACTIVE` until verified) |
| Password Reset Token | Single-use, 15-minute expiry |

### 8.3 Authorization (RBAC)

Five roles per API Specification:

| Role | Code | Key Permissions |
|---|---|---|
| Customer | `CUSTOMER` | Own profile, submit loan, upload docs, view own loans |
| Loan Officer | `LOAN_OFFICER` | View all loans, verify (Maker) |
| Credit Manager | `CREDIT_MANAGER` | Approve/reject (Checker) |
| Finance Officer | `FINANCE_OFFICER` | Sanction, disburse |
| Admin | `ADMIN` | User management, loan types, audit logs, reports |

Enforcement:

- **Method-level:** `@PreAuthorize("hasRole('LOAN_OFFICER')")`
- **Object-level:** Service-layer checks (e.g., customer can only access own `loanId`)
- **Maker-Checker rule:** Loan Officer cannot approve; Credit Manager cannot modify customer data

### 8.4 API Security Controls

| Control | Implementation |
|---|---|
| HTTPS Only | Enforced at load balancer; HSTS header |
| CORS | Whitelist frontend origins per environment |
| Rate Limiting | 100 requests/minute/IP (Bucket4j or Redis-backed) |
| Input Validation | Jakarta Bean Validation on all request DTOs |
| SQL Injection | JPA parameterized queries only; no dynamic SQL |
| XSS | Output encoding; CSP headers on frontend |
| CSRF | Disabled for stateless JWT APIs; HttpOnly cookie for refresh token with SameSite=Strict |
| Sensitive Data | PAN/Aadhaar masked in API responses; Aadhaar encrypted at rest (AES-256) |
| Audit Logging | All mutating API calls logged with userId, IP, action, result |

### 8.5 Data Classification

| Data | Classification | Protection |
|---|---|---|
| Passwords | Critical | BCrypt hash only |
| PAN, Aadhaar | PII — Sensitive | Masked in API; Aadhaar encrypted in DB |
| Bank statements | Confidential | Encrypted file storage; access logged |
| JWT Secret | Critical | Secrets Manager; never in source control |
| Audit logs | Compliance | 7-year retention; immutable |

---

## 9. File Upload Architecture

### 9.1 Upload Flow

```
Customer → Frontend (validate type/size) → POST /api/v1/documents/upload (multipart)
  → DocumentController → DocumentService
    → Validate: loan status (DRAFT/SUBMITTED), doc type uniqueness, file rules
    → FileStorageService.store(file) → returns filePath
    → Save metadata to `documents` table
    → Return documentId + metadata (not file content)
```

### 9.2 Storage Strategy

| Environment | Storage | Path Pattern |
|---|---|---|
| Development | Local filesystem | `./storage/documents/{customerId}/{loanId}/{documentType}/{uuid}.pdf` |
| Production | AWS S3 | `s3://{bucket}/documents/{customerId}/{loanId}/{documentType}/{uuid}.pdf` |

**Database stores metadata only** (per DB Design Document):

- `file_name`, `file_path`, `file_type`, `file_size`, `document_type`, `loan_id`, `uploaded_at`

### 9.3 File Validation Rules

| Rule | Value |
|---|---|
| Allowed types | PDF, JPG, JPEG, PNG |
| Max size | 5 MB per file |
| One upload per document type per loan | Enforced at service layer |
| Upload allowed statuses | `DRAFT`, `SUBMITTED` only |

### 9.4 Storage Abstraction

```
interface FileStorageService {
    StoredFile store(MultipartFile file, StoragePath path);
    Resource retrieve(String filePath);
    void delete(String filePath);
}

Implementations:
  - LocalFileStorageService (dev)
  - S3FileStorageService (prod)
```

Selected via Spring profile (`dev` / `prod`).

### 9.5 Download Flow

```
GET /api/v1/documents/{documentId}/download
  → Verify RBAC + loan ownership
  → FileStorageService.retrieve(filePath)
  → Stream file with Content-Disposition header
  → Audit log entry
```

### 9.6 Future Enhancements

- Virus/malware scanning (ClamAV) before storage
- Pre-signed S3 URLs for direct browser upload (reduces backend load)
- Document OCR for automated data extraction

---

## 10. Notification Architecture

### 10.1 Architecture Overview

```
┌──────────────────┐     Domain Event      ┌───────────────────────┐
│  Business Service │ ──────────────────► │ NotificationEventListener│
│  (Loan, Approval) │                      └───────────┬───────────┘
└──────────────────┘                                  │
                                                      ▼
                                          ┌───────────────────────┐
                                          │   NotificationService  │
                                          └───────────┬───────────┘
                                    ┌─────────────────┼─────────────────┐
                                    ▼                 ▼                 ▼
                            ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
                            │  In-App DB  │  │ EmailService│  │  SmsService │
                            │ notifications│  │ (SES/SG)   │  │ (Twilio)    │
                            └─────────────┘  └─────────────┘  └─────────────┘
```

### 10.2 Notification Triggers (v1)

| Event | Channels | Recipients |
|---|---|---|
| User Registration | Email | Customer |
| Loan Submitted | Email, SMS, In-App | Customer |
| Documents Required | Email, In-App | Customer |
| Under Verification | Email, In-App | Customer |
| Loan Approved | Email, SMS, In-App | Customer |
| Loan Rejected | Email, SMS, In-App | Customer |
| Loan Disbursed | Email, SMS, In-App | Customer |
| EMI Reminder (3 days before) | Email, SMS, In-App | Customer |
| EMI Overdue | Email, SMS, In-App | Customer |

### 10.3 Implementation Strategy (v1)

| Component | Approach |
|---|---|
| Event publishing | Spring `ApplicationEventPublisher` from service layer |
| Event handling | `@EventListener` + `@Async` on `NotificationService` |
| In-app storage | Insert into `notifications` table synchronously within async handler |
| Email/SMS | Async dispatch via provider SDK; retry 3 times with exponential backoff |
| Templates | HTML email templates (Thymeleaf); SMS plain text templates |
| Scheduled jobs | Spring `@Scheduled` cron for EMI reminders and overdue checks |
| Idempotency | Event ID + loan ID dedup key to prevent duplicate notifications |

### 10.4 Notification Data Model

Uses existing `notifications` table:

- `notification_id`, `user_id`, `title`, `message`, `type` (EMAIL/SMS/IN_APP), `is_read`, `created_at`

Separate delivery log (recommended addition):

- `notification_delivery_log`: `notification_id`, `channel`, `status` (SENT/FAILED), `provider_ref`, `attempted_at`

### 10.5 v1 Stub Strategy

If email/SMS provider contracts are not ready:

- Implement `EmailService` and `SmsService` interfaces
- Provide `LoggingEmailService` / `LoggingSmsService` that log payload to application log
- Swap to real providers via configuration without code changes

---

## 11. Logging

### 11.1 Logging Framework

| Component | Technology |
|---|---|
| API | SLF4J |
| Implementation | Logback (via Spring Boot default) |
| Format | JSON structured logging in production (Logstash encoder) |
| Correlation | `X-Request-ID` / `X-Correlation-ID` propagated through MDC |

### 11.2 Log Levels by Layer

| Layer | Default Level | Notes |
|---|---|---|
| Controller | INFO | Request received, response status |
| Service | INFO | Business events (loan submitted, approved) |
| Repository | DEBUG | SQL queries (dev only) |
| Security | WARN | Failed login, unauthorized access |
| External integrations | INFO/WARN | Provider call results |
| Root | INFO (prod), DEBUG (dev) | |

### 11.3 MDC (Mapped Diagnostic Context) Fields

| Field | Source |
|---|---|
| `requestId` | `X-Request-ID` header or generated UUID |
| `correlationId` | `X-Correlation-ID` header |
| `userId` | JWT claims |
| `role` | JWT claims |
| `clientIp` | `X-Forwarded-For` or remote address |

### 11.4 What to Log

| Event | Level | Data Logged |
|---|---|---|
| API request | INFO | Method, URI, requestId, userId |
| API response | INFO | Status code, duration (ms) |
| Authentication | INFO/WARN | Login success/failure (never log password) |
| Status transition | INFO | loanId, fromStatus, toStatus, actor |
| File upload | INFO | documentId, loanId, documentType, fileSize |
| Notification dispatch | INFO | userId, channel, event type, result |
| Exception | ERROR | Stack trace, requestId, userId |
| Audit (compliance) | INFO | Separate `audit_log` table (authoritative) |

### 11.5 What NOT to Log

- Passwords, tokens, refresh tokens
- Full PAN, Aadhaar numbers
- File binary content
- Credit score details (future)

### 11.6 Log Aggregation (Production)

```
Application (JSON logs) → CloudWatch / Filebeat → Elasticsearch → Kibana/Grafana
```

Retention: 90 days operational logs; audit logs in DB retained 7 years.

---

## 12. Exception Handling

### 12.1 Exception Hierarchy

```
BaseException (abstract)
├── BusinessException          → HTTP 422 (business rule violation)
├── ResourceNotFoundException  → HTTP 404
├── DuplicateResourceException → HTTP 409
├── UnauthorizedException      → HTTP 401
├── ForbiddenException         → HTTP 403
├── ValidationException        → HTTP 400
└── FileStorageException       → HTTP 500
```

Spring Security handles 401/403 for auth failures independently.

### 12.2 Global Exception Handler

Single `@RestControllerAdvice` class handles all exceptions and returns the standard error envelope:

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": ["email is required", "mobile must be 10 digits"],
  "timestamp": "2026-06-28T10:00:00Z",
  "statusCode": 400
}
```

### 12.3 Exception Mapping Table

| Exception | HTTP Code | Example Message |
|---|---|---|
| `MethodArgumentNotValidException` | 400 | Field-level validation errors |
| `UnauthorizedException` | 401 | Authentication token is missing or invalid |
| `ForbiddenException` | 403 | You do not have permission to access this resource |
| `ResourceNotFoundException` | 404 | Loan application with ID 501 not found |
| `DuplicateResourceException` | 409 | Email address is already registered |
| `BusinessException` | 422 | FOIR exceeds the allowed 60% |
| `FileStorageException` | 500 | Failed to store document |
| Unhandled `Exception` | 500 | An unexpected error occurred. Please try again later. |

### 12.4 Business Rule Enforcement Pattern

Business rules are enforced in the **service layer**, not the controller:

```
// Service throws BusinessException with clear message
if (foir > 0.60) {
    throw new BusinessException("Fixed Obligation to Income Ratio exceeds the allowed 60%");
}
```

Controllers remain thin; the global handler converts exceptions to HTTP responses.

### 12.5 Error Code Strategy (Optional Enhancement)

For client-side i18n and programmatic handling, add an `errorCode` field:

| errorCode | Meaning |
|---|---|
| `AUTH_001` | Invalid credentials |
| `LOAN_001` | FOIR exceeded |
| `LOAN_002` | Active loan exists |
| `DOC_001` | File too large |
| `DOC_002` | Duplicate document type |

---

## 13. Deployment Architecture

### 13.1 Environment Strategy

| Environment | Purpose | Infrastructure |
|---|---|---|
| **Local** | Developer machines | Docker Compose (MySQL, backend, frontend) |
| **DEV** | Integration testing | Cloud VM or ECS; shared MySQL |
| **UAT** | Business acceptance testing | Mirrors production; anonymized data |
| **PROD** | Live system | HA deployment; managed MySQL; S3 |

### 13.2 Production Deployment Diagram

```
                    ┌─────────────────┐
                    │   Route 53 /    │
                    │   DNS           │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │  AWS ALB / CDN  │
                    │  (TLS终止)       │
                    └────────┬────────┘
              ┌──────────────┼──────────────┐
              │                             │
     ┌────────▼────────┐          ┌────────▼────────┐
     │  Frontend (S3 +  │          │  Backend (ECS/   │
     │  CloudFront)     │          │  App Service)     │
     │  Static React    │          │  Spring Boot x2+  │
     └─────────────────┘          └────────┬────────┘
                                             │
                              ┌──────────────┼──────────────┐
                              │              │              │
                     ┌────────▼───┐  ┌───────▼──────┐ ┌────▼─────┐
                     │ RDS MySQL  │  │  S3 Bucket   │ │ SES /    │
                     │ Multi-AZ   │  │  Documents   │ │ Twilio   │
                     └────────────┘  └──────────────┘ └──────────┘
```

### 13.3 Container Strategy

| Component | Base Image | Notes |
|---|---|---|
| Backend | `eclipse-temurin:21-jre-alpine` | Multi-stage build; non-root user |
| Frontend | `nginx:alpine` | Static build served by Nginx |
| Database | Managed RDS | Not containerized in production |

### 13.4 CI/CD Pipeline

```
Push to main/develop
  → Backend: mvn test → mvn package → Docker build → Push to ECR → Deploy to ECS
  → Frontend: npm test → npm build → Deploy to S3/CloudFront
  → Flyway migrations run on backend startup (or dedicated migration step)
```

### 13.5 Configuration Management

| Config | Source |
|---|---|
| Database URL, credentials | Secrets Manager / env vars |
| JWT secret | Secrets Manager |
| S3 bucket name | Environment config |
| Email/SMS API keys | Secrets Manager |
| CORS allowed origins | Environment config |
| Feature flags | Environment config |

Spring profiles: `local`, `dev`, `uat`, `prod`

### 13.6 Scalability & Availability

| Concern | v1 Target | Approach |
|---|---|---|
| Backend instances | 2+ (prod) | Horizontal scaling behind ALB |
| Database | Multi-AZ RDS | Automatic failover |
| File storage | S3 | Inherently scalable |
| Session state | Stateless JWT | No sticky sessions required |
| Backup | Daily automated | RDS snapshots + S3 versioning |
| RTO | 4 hours | Documented recovery runbook |
| RPO | 1 hour | Point-in-time recovery enabled |

### 13.7 Health Checks

| Endpoint | Purpose |
|---|---|
| `GET /actuator/health` | Liveness (Spring Boot Actuator) |
| `GET /actuator/health/readiness` | Readiness (DB connectivity check) |
| `GET /actuator/info` | Version and build info |

---

## 15. Cross-Cutting Concerns & Open Decisions

### 15.1 Resolved Architectural Decisions

| Decision | Resolution |
|---|---|
| Monolith vs Microservices | Modular monolith for v1 |
| Edit after submit | Edit allowed in `DRAFT` only |
| Canonical status model | 10-state model (see Section 3.3) |
| Frontend approach | Single SPA with role-based portals |
| File storage | Metadata in DB; files in local/S3 |
| Notification dispatch | In-process events + async providers |

### 15.2 Open Decisions Requiring Stakeholder Input

| # | Decision | Options | Recommendation |
|---|---|---|---|
| 1 | Cloud provider | AWS / Azure / On-prem | AWS (if no constraint) |
| 2 | SMS provider | Twilio / MSG91 | MSG91 for India-focused deployment |
| 3 | Email provider | SES / SendGrid | SES if on AWS |
| 4 | Frontend framework | React / Angular | React (see Section 2.2) |
| 5 | Loan officer assignment | Manual pickup / Round-robin / Branch-based | Manual pickup for v1 |
| 6 | Interest rate assignment | Fixed from loan_type / Manager override | Base rate from `loan_type`; manager sets within 7–36% on approval |
| 7 | Eligibility engine | Rule-based v1 / External CIBIL | Rule-based (FOIR + age + income) for v1 |
| 8 | Sanction letter | PDF generation in v1? | Generate simple PDF via backend library in v1.1 |
| 9 | Aadhaar encryption | Application-level / DB TDE | Application-level AES-256 for v1 |
| 10 | Redis introduction | v1 / v1.1 | v1.1 (rate limiting can use in-memory Bucket4j initially) |

### 15.3 Prerequisites Before Development Sprint 1

| # | Deliverable | Owner |
|---|---|---|
| 1 | Complete table-level DB specification (DDL) | DBA / Backend Lead |
| 2 | Sign-off on this TAD | Architect + Product Owner |
| 3 | Cloud account and environment provisioning | DevOps |
| 4 | Email/SMS sandbox accounts | DevOps |
| 5 | UI wireframes for 5 portals | UX / Frontend Lead |
| 6 | Seed data spec (roles, default loan type, admin user) | Backend Lead |

### 15.4 Non-Functional Requirements (Baseline Targets)

| Metric | Target (v1) |
|---|---|
| API response time (P95) | < 500 ms (excluding file upload) |
| File upload | < 10 seconds for 5 MB |
| Concurrent users | 500 |
| Uptime | 99.5% |
| RPO / RTO | 1 hour / 4 hours |

---

## Document Approval

| Role | Name | Signature | Date |
|---|---|---|---|
| Solution Architect | | | |
| Java Technical Lead | | | |
| Product Owner | | | |
| DevOps Lead | | | |
| Security Officer | | | |

---

*End of Technical Architecture Document v1.0*
