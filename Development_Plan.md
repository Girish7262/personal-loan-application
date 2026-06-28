# Personal Loan Application System
## Development Plan

| Property | Value |
|---|---|
| **Document Type** | Development Plan |
| **Project** | Personal Loan Application System |
| **Version** | 1.0 |
| **Status** | Ready for Execution |
| **Author Role** | Senior Full Stack Architect |
| **Related Documents** | BRS v1.0, API Design v1.0, API Specification v1.0, DB Design v1.0, TAD v1.0 |
| **Date** | 2026-06-28 |
| **Sprint Duration** | 2 weeks |
| **Estimated Duration** | 18 weeks (9 sprints + Sprint 0) |

---

## Technology Stack (Confirmed)

### Backend
Java 21 · Spring Boot 3.3 · Spring Security · JWT · Spring Data JPA · MySQL 8 · Flyway · Maven · Lombok · MapStruct

### Frontend
React 18 · TypeScript · Vite · Material UI · TanStack Query · React Hook Form · Zod · React Router

---

## Table of Contents

1. [Project Roadmap](#1-project-roadmap)
2. [Sprint Plan](#2-sprint-plan)
3. [Module Dependency](#3-module-dependency)
4. [Development Order](#4-development-order)
5. [Folder Structure](#5-folder-structure)
6. [Git Branch Strategy](#6-git-branch-strategy)
7. [Coding Standards](#7-coding-standards)

---

## 1. Project Roadmap

### 1.1 Roadmap Overview

```
Phase 0          Phase 1           Phase 2            Phase 3           Phase 4          Phase 5
Foundation   →   Core Platform  →  Loan Lifecycle  →  Workflow & Ops  →  Frontend Complete → Release
(Sprint 0)       (Sprints 1–2)     (Sprints 3–4)      (Sprints 5–7)     (Sprint 8)          (Sprint 9)
 2 weeks           4 weeks           4 weeks            6 weeks           2 weeks           2 weeks
```

**Total estimated timeline:** 20 weeks (including Sprint 0)

---

### 1.2 Phase Breakdown

| Phase | Sprints | Duration | Goal | Exit Criteria |
|---|---|---|---|---|
| **Phase 0 — Foundation** | Sprint 0 | 2 weeks | Repo, CI, DB schema, shared infrastructure | App boots; Flyway migrations run; Swagger accessible; frontend shell loads |
| **Phase 1 — Core Platform** | Sprints 1–2 | 4 weeks | Authentication, RBAC, customer profile | Register, login, JWT, profile CRUD working end-to-end |
| **Phase 2 — Loan Lifecycle** | Sprints 3–4 | 4 weeks | Loan application, EMI, document upload | Customer can apply, calculate EMI, upload docs, submit application |
| **Phase 3 — Workflow & Operations** | Sprints 5–7 | 6 weeks | Maker-Checker approval, finance, notifications, admin | Full loan flow from verification to disbursement; in-app notifications; admin panel |
| **Phase 4 — Frontend Completion** | Sprint 8 | 2 weeks | All role portals integrated, polish, cross-module E2E | All 5 portals functional; role guards enforced |
| **Phase 5 — Release Readiness** | Sprint 9 | 2 weeks | QA, UAT, performance, deployment | UAT sign-off; production deployment runbook validated |

---

### 1.3 Milestone Timeline

| Milestone | Target | Deliverable |
|---|---|---|
| **M0 — Dev Environment Ready** | End Sprint 0 | Monorepo, Docker Compose, CI pipeline green |
| **M1 — Auth MVP** | End Sprint 1 | Customer registration, login, JWT, email verification stub |
| **M2 — Profile Complete** | End Sprint 2 | Customer profile API + UI; RBAC enforced on all endpoints |
| **M3 — Loan Apply MVP** | End Sprint 3 | Create loan (DRAFT), EMI calculator, eligibility (FOIR) |
| **M4 — Document Submit MVP** | End Sprint 4 | Upload docs, submit loan, status = SUBMITTED |
| **M5 — Maker-Checker Live** | End Sprint 5 | Officer verify, manager approve/reject |
| **M6 — Finance Flow Live** | End Sprint 6 | Sanction, disburse, EMI schedule generated |
| **M7 — Notifications & Admin** | End Sprint 7 | In-app notifications, admin user/loan-type management, audit logs |
| **M8 — Feature Complete** | End Sprint 8 | All portals, all 55+ API endpoints implemented |
| **M9 — Production Release** | End Sprint 9 | UAT passed, deployed to production |

---

### 1.4 Scope Boundaries (v1.0)

**In Scope**
- All 8 API modules (55+ endpoints per API Specification)
- 11 database tables with Flyway migrations
- 5 role-based frontend portals (Customer, Officer, Manager, Finance, Admin)
- Maker-Checker workflow (verify → approve/reject → sanction → disburse)
- Local file storage (dev); S3-ready abstraction
- In-app notifications + logging email/SMS stubs
- Audit logging (7-year retention design)
- OpenAPI documentation

**Out of Scope (Deferred to v1.1+)**
- Real PAN/Aadhaar/CIBIL third-party integrations
- Payment gateway / EMI collection
- Sanction letter PDF generation
- WhatsApp notifications
- Redis caching / rate limiting at scale
- Multi-branch / multi-tenant support
- AI eligibility prediction

---

### 1.5 Team Assumptions

| Role | Count | Primary Responsibility |
|---|---|---|
| Backend Developer | 2 | Spring Boot modules, JPA, Flyway, tests |
| Frontend Developer | 2 | React portals, forms, API integration |
| Full Stack / Tech Lead | 1 | Architecture, code review, integration |
| QA Engineer | 1 | Test plans, automation (from Sprint 3) |
| DevOps | 0.5 | CI/CD, Docker, deployment (from Sprint 0) |
| Product Owner | 0.5 | Acceptance, UAT, backlog prioritization |

---

## 2. Sprint Plan

### Sprint 0 — Foundation & Infrastructure (Weeks 1–2)

**Goal:** Establish monorepo, database schema, shared backend/frontend scaffolding, and CI pipeline.

| ID | Task | Layer | Owner | Story Points |
|---|---|---|---|---|
| S0-01 | Initialize Git monorepo (`backend/`, `frontend/`, `docs/`) | DevOps | Tech Lead | 2 |
| S0-02 | Spring Boot 3.3 project scaffold (Maven, Java 21, Lombok, MapStruct) | Backend | BE-1 | 3 |
| S0-03 | React 18 + Vite + TypeScript + MUI project scaffold | Frontend | FE-1 | 3 |
| S0-04 | Docker Compose: MySQL 8, backend, frontend | DevOps | Tech Lead | 3 |
| S0-05 | Flyway: complete DDL for all 11 tables + seed data (roles, loan_type, admin user) | Backend | BE-2 | 8 |
| S0-06 | Common backend: `ApiResponse`, `GlobalExceptionHandler`, error envelope | Backend | BE-1 | 5 |
| S0-07 | Common backend: logging config (Logback, MDC, request ID filter) | Backend | BE-1 | 3 |
| S0-08 | Spring Security skeleton + JWT utility (no endpoints yet) | Backend | BE-2 | 5 |
| S0-09 | Frontend: Axios client, auth context shell, route skeleton, MUI theme | Frontend | FE-1 | 5 |
| S0-10 | Frontend: shared layout (AppBar, Sidebar, Footer), error boundary | Frontend | FE-2 | 3 |
| S0-11 | SpringDoc OpenAPI setup + Swagger UI | Backend | BE-1 | 2 |
| S0-12 | GitHub Actions: backend build/test, frontend build/lint | DevOps | Tech Lead | 3 |
| S0-13 | Coding standards doc review + team walkthrough | All | Tech Lead | 2 |

**Sprint 0 Deliverables**
- [ ] Application starts locally via `docker-compose up`
- [ ] Flyway migrations create all 11 tables
- [ ] Seed data: 5 roles, 1 loan type, 1 admin user
- [ ] Swagger UI accessible at `/swagger-ui.html`
- [ ] Frontend loads with placeholder routes
- [ ] CI pipeline passes on `develop` branch

---

### Sprint 1 — Authentication Module (Weeks 3–4)

**Goal:** Full auth API (8 endpoints) + customer registration/login UI.

| ID | Task | Layer | Owner | Story Points |
|---|---|---|---|---|
| S1-01 | `AuthModule`: User + Role entities, repositories | Backend | BE-1 | 3 |
| S1-02 | JWT token provider (access 24h, refresh 7d), BCrypt encoder | Backend | BE-2 | 5 |
| S1-03 | `POST /auth/register` with validation + duplicate checks | Backend | BE-1 | 5 |
| S1-04 | `POST /auth/login` with lockout after 5 failures | Backend | BE-2 | 5 |
| S1-05 | `POST /auth/refresh-token`, `/logout`, `/change-password` | Backend | BE-2 | 5 |
| S1-06 | `POST /auth/forgot-password`, `/reset-password`, `/verify-email` | Backend | BE-1 | 5 |
| S1-07 | Spring Security filter chain + JWT authentication filter | Backend | BE-2 | 5 |
| S1-08 | `EmailService` interface + logging stub implementation | Backend | BE-1 | 2 |
| S1-09 | Auth unit tests + integration tests (Testcontainers) | Backend | BE-1 | 5 |
| S1-10 | Frontend: Register page (React Hook Form + Zod) | Frontend | FE-1 | 5 |
| S1-11 | Frontend: Login page + JWT storage (in-memory + refresh cookie) | Frontend | FE-1 | 5 |
| S1-12 | Frontend: Forgot/Reset password pages | Frontend | FE-2 | 3 |
| S1-13 | Frontend: Axios interceptor (attach token, refresh on 401) | Frontend | FE-2 | 5 |
| S1-14 | Frontend: `ProtectedRoute` + role guard HOC | Frontend | FE-2 | 3 |

**Sprint 1 Deliverables**
- [ ] All 8 auth endpoints functional and documented in Swagger
- [ ] Customer can register, verify email (stub), login, logout
- [ ] Account lockout after 5 failed attempts
- [ ] Frontend login/register flow complete

---

### Sprint 2 — Customer Profile Module (Weeks 5–6)

**Goal:** Customer profile API (7 endpoints) + profile UI; RBAC fully wired.

| ID | Task | Layer | Owner | Story Points |
|---|---|---|---|---|
| S2-01 | `CustomerModule`: CustomerProfile entity, repository, mapper | Backend | BE-1 | 3 |
| S2-02 | `POST/GET/PUT/PATCH /customers/profile` | Backend | BE-1 | 5 |
| S2-03 | `GET /customers/{id}`, `GET /customers` (paginated, search) | Backend | BE-2 | 5 |
| S2-04 | `DELETE /customers/{id}` (soft delete) | Backend | BE-2 | 3 |
| S2-05 | PAN/Aadhaar uniqueness validation + masking in responses | Backend | BE-1 | 3 |
| S2-06 | Age validation (21–65), `@PreAuthorize` on all customer endpoints | Backend | BE-2 | 3 |
| S2-07 | Customer module unit + integration tests | Backend | BE-1 | 5 |
| S2-08 | Frontend: Customer profile form (multi-step: personal, address, employment) | Frontend | FE-1 | 8 |
| S2-09 | Frontend: Zod schemas mirroring API field validation rules | Frontend | FE-1 | 3 |
| S2-10 | Frontend: Profile view page (masked PAN/Aadhaar display) | Frontend | FE-2 | 3 |
| S2-11 | Frontend: Customer dashboard shell (navigation, status cards) | Frontend | FE-2 | 5 |
| S2-12 | Audit log aspect: log profile create/update actions | Backend | BE-2 | 3 |

**Sprint 2 Deliverables**
- [ ] Customer can create and update profile after login
- [ ] Officer/Admin can list and view customer profiles
- [ ] PAN/Aadhaar masked in API responses
- [ ] Customer dashboard navigable

---

### Sprint 3 — Loan Application & EMI Module (Weeks 7–8)

**Goal:** Loan lifecycle APIs (9 endpoints) + EMI calculator (3 endpoints) + customer loan UI.

| ID | Task | Layer | Owner | Story Points |
|---|---|---|---|---|
| S3-01 | `LoanModule`: LoanApplication, LoanType entities + repositories | Backend | BE-1 | 5 |
| S3-02 | `LoanStatusHistory` entity + service (auto-log on status change) | Backend | BE-2 | 3 |
| S3-03 | `POST /loans` — create application (DRAFT), FOIR validation | Backend | BE-1 | 5 |
| S3-04 | `GET /loans`, `/my-loans`, `/{loanId}`, `/{loanId}/status-history` | Backend | BE-2 | 5 |
| S3-05 | `PUT /loans/{id}` (DRAFT only), `PATCH /submit`, `PATCH /cancel` | Backend | BE-1 | 5 |
| S3-06 | `GET /loan-types` (public) | Backend | BE-2 | 2 |
| S3-07 | `EmiModule`: EMI calculation service (formula from API spec) | Backend | BE-2 | 3 |
| S3-08 | `POST /emi/calculate`, `GET /emi/{loanId}/schedule`, `/summary` | Backend | BE-2 | 5 |
| S3-09 | `EligibilityService`: FOIR check, active loan check, age check | Backend | BE-1 | 5 |
| S3-10 | Optimistic locking (`@Version`) on loan_application | Backend | BE-1 | 2 |
| S3-11 | Loan + EMI unit/integration tests | Backend | BE-1 | 5 |
| S3-12 | Frontend: EMI Calculator page (public, no auth) | Frontend | FE-2 | 5 |
| S3-13 | Frontend: Loan application form (amount, tenure, purpose) | Frontend | FE-1 | 8 |
| S3-14 | Frontend: Eligibility check step with FOIR display | Frontend | FE-1 | 5 |
| S3-15 | Frontend: My Loans list + loan detail page | Frontend | FE-2 | 5 |

**Sprint 3 Deliverables**
- [ ] Customer can create loan in DRAFT, edit, view EMI breakdown
- [ ] FOIR > 60% rejected with 422
- [ ] One active personal loan rule enforced
- [ ] EMI calculator works without login
- [ ] Status history recorded on every transition

---

### Sprint 4 — Document Management Module (Weeks 9–10)

**Goal:** Document upload API (5 endpoints) + upload UI; loan submission gate.

| ID | Task | Layer | Owner | Story Points |
|---|---|---|---|---|
| S4-01 | `DocumentModule`: Document entity, repository, mapper | Backend | BE-2 | 3 |
| S4-02 | `FileStorageService` interface + `LocalFileStorageService` | Backend | BE-2 | 5 |
| S4-03 | `POST /documents/upload` (multipart, validation, 5MB limit) | Backend | BE-2 | 5 |
| S4-04 | `GET /documents/loan/{loanId}`, `/{docId}`, `/{docId}/download` | Backend | BE-1 | 5 |
| S4-05 | `DELETE /documents/{docId}` | Backend | BE-1 | 3 |
| S4-06 | Mandatory document check before `PATCH /loans/{id}/submit` | Backend | BE-1 | 3 |
| S4-07 | Document upload allowed only in DRAFT/SUBMITTED status | Backend | BE-2 | 2 |
| S4-08 | Document module tests (including file upload mock) | Backend | BE-2 | 5 |
| S4-09 | Frontend: Document upload component (drag-drop, progress, type selector) | Frontend | FE-1 | 8 |
| S4-10 | Frontend: Document list per loan with download/delete | Frontend | FE-2 | 5 |
| S4-11 | Frontend: Submit application flow (validate docs → confirm → submit) | Frontend | FE-1 | 5 |
| S4-12 | Frontend: Loan status tracker (stepper UI for status flow) | Frontend | FE-2 | 5 |

**Sprint 4 Deliverables**
- [ ] Customer can upload all mandatory document types
- [ ] Duplicate document type per loan rejected
- [ ] Loan submission blocked until mandatory docs uploaded
- [ ] Loan status changes to SUBMITTED on submit
- [ ] **M4 Milestone:** End-to-end customer flow complete (register → apply → upload → submit)

---

### Sprint 5 — Approval Workflow — Maker (Weeks 11–12)

**Goal:** Loan Officer verification flow (Maker) + Officer portal UI.

| ID | Task | Layer | Owner | Story Points |
|---|---|---|---|---|
| S5-01 | `ApprovalModule`: ApprovalHistory entity, repository | Backend | BE-1 | 3 |
| S5-02 | `GET /approvals/pending` (paginated, filter by status) | Backend | BE-1 | 5 |
| S5-03 | `POST /approvals/{loanId}/verify` (Maker — status → VERIFIED) | Backend | BE-1 | 5 |
| S5-04 | Status transition: SUBMITTED → UNDER_VERIFICATION (officer pickup) | Backend | BE-2 | 3 |
| S5-05 | Maker-Checker rule enforcement (officer cannot approve) | Backend | BE-2 | 3 |
| S5-06 | `GET /approvals/{loanId}/history` | Backend | BE-1 | 3 |
| S5-07 | Domain event: `LoanVerifiedEvent` published on verify | Backend | BE-2 | 2 |
| S5-08 | Approval module tests | Backend | BE-1 | 5 |
| S5-09 | Frontend: Officer portal layout + navigation | Frontend | FE-2 | 3 |
| S5-10 | Frontend: Pending applications table (paginated, sortable) | Frontend | FE-2 | 5 |
| S5-11 | Frontend: Loan verification page (docs review, customer details, remarks) | Frontend | FE-1 | 8 |
| S5-12 | Frontend: Document viewer (PDF/image preview inline) | Frontend | FE-2 | 5 |

**Sprint 5 Deliverables**
- [ ] Loan Officer can view pending applications
- [ ] Officer can pick up and verify loans with remarks
- [ ] Maker cannot approve or reject
- [ ] Approval history recorded
- [ ] Officer portal functional

---

### Sprint 6 — Approval Workflow — Checker & Finance (Weeks 13–14)

**Goal:** Credit Manager approve/reject + Finance sanction/disburse + EMI schedule generation.

| ID | Task | Layer | Owner | Story Points |
|---|---|---|---|---|
| S6-01 | `POST /approvals/{loanId}/approve` (Checker — status → APPROVED) | Backend | BE-1 | 5 |
| S6-02 | `POST /approvals/{loanId}/reject` (Checker — status → REJECTED) | Backend | BE-1 | 5 |
| S6-03 | `POST /approvals/{loanId}/sanction` (Finance — status → SANCTIONED) | Backend | BE-2 | 5 |
| S6-04 | `POST /approvals/{loanId}/disburse` (Finance — status → DISBURSED) | Backend | BE-2 | 5 |
| S6-05 | Auto-generate EMI schedule on APPROVED status | Backend | BE-2 | 5 |
| S6-06 | Interest rate validation (7.0%–36.0%) on approval | Backend | BE-1 | 2 |
| S6-07 | Domain events: LoanApprovedEvent, LoanRejectedEvent, LoanDisbursedEvent | Backend | BE-2 | 3 |
| S6-08 | Approval + finance flow integration tests | Backend | BE-1 | 5 |
| S6-09 | Frontend: Manager portal — verified loans queue | Frontend | FE-1 | 5 |
| S6-10 | Frontend: Approve/Reject dialog with amount and rate fields | Frontend | FE-1 | 5 |
| S6-11 | Frontend: Finance portal — sanction and disburse actions | Frontend | FE-2 | 5 |
| S6-12 | Frontend: EMI schedule table view (month-by-month breakdown) | Frontend | FE-2 | 5 |

**Sprint 6 Deliverables**
- [ ] Credit Manager can approve/reject verified loans
- [ ] Finance can sanction and disburse approved loans
- [ ] EMI schedule auto-generated on approval
- [ ] Full Maker-Checker-Finance workflow operational
- [ ] **M6 Milestone:** Complete backend loan lifecycle functional

---

### Sprint 7 — Notification & Admin Modules (Weeks 15–16)

**Goal:** Notification API (5 endpoints) + Admin API (11 endpoints) + corresponding UI.

| ID | Task | Layer | Owner | Story Points |
|---|---|---|---|---|
| S7-01 | `NotificationModule`: Notification entity, repository | Backend | BE-2 | 3 |
| S7-02 | `NotificationService` + `@EventListener` + `@Async` handlers | Backend | BE-2 | 5 |
| S7-03 | `GET /notifications`, `/unread`, `PATCH /{id}/read`, `/read-all`, `DELETE` | Backend | BE-2 | 5 |
| S7-04 | `SmsService` interface + logging stub | Backend | BE-1 | 2 |
| S7-05 | Auto-trigger notifications per event table (API spec Section 7.2) | Backend | BE-2 | 5 |
| S7-06 | `AdminModule`: user management endpoints (list, status, soft delete) | Backend | BE-1 | 5 |
| S7-07 | `GET /admin/audit-logs` (paginated, filters) | Backend | BE-1 | 5 |
| S7-08 | `POST/PUT/DELETE /admin/loan-types` | Backend | BE-1 | 5 |
| S7-09 | `GET /admin/reports/loans`, `/admin/reports/customers` | Backend | BE-2 | 5 |
| S7-10 | Audit aspect: log all mutating API calls to `audit_log` | Backend | BE-1 | 5 |
| S7-11 | Notification + Admin tests | Backend | BE-2 | 5 |
| S7-12 | Frontend: Notification bell + dropdown + mark read | Frontend | FE-2 | 5 |
| S7-13 | Frontend: Admin portal — user management table + status change | Frontend | FE-1 | 5 |
| S7-14 | Frontend: Admin — loan type CRUD | Frontend | FE-1 | 5 |
| S7-15 | Frontend: Admin — audit log viewer (paginated, date filter) | Frontend | FE-2 | 5 |
| S7-16 | Frontend: Admin — loan/customer summary reports | Frontend | FE-2 | 5 |

**Sprint 7 Deliverables**
- [ ] In-app notifications on all lifecycle events
- [ ] Email/SMS logged via stub services
- [ ] Admin can manage users, loan types, view audit logs and reports
- [ ] All 55+ API endpoints implemented

---

### Sprint 8 — Integration, E2E & Frontend Polish (Weeks 17–18)

**Goal:** Cross-portal integration, E2E test suite, UI polish, accessibility, performance baseline.

| ID | Task | Layer | Owner | Story Points |
|---|---|---|---|---|
| S8-01 | End-to-end backend integration test: full loan lifecycle | Backend | BE-1 | 8 |
| S8-02 | API contract tests: validate all endpoints against OpenAPI spec | Backend | BE-2 | 5 |
| S8-03 | Frontend E2E tests (Playwright/Cypress): customer flow | Frontend | FE-1 | 8 |
| S8-04 | Frontend E2E tests: officer → manager → finance flow | Frontend | FE-2 | 8 |
| S8-05 | Cross-portal role guard testing (unauthorized access blocked) | Frontend | FE-2 | 3 |
| S8-06 | UI polish: loading states, empty states, error toasts (MUI Snackbar) | Frontend | FE-1 | 5 |
| S8-07 | Responsive design pass (customer portal mobile-friendly) | Frontend | FE-2 | 5 |
| S8-08 | Performance baseline: API P95 < 500ms (excluding upload) | Backend | BE-1 | 3 |
| S8-09 | Security review: OWASP top 10 checklist | All | Tech Lead | 5 |
| S8-10 | Update Swagger docs, README, environment setup guide | All | Tech Lead | 3 |

**Sprint 8 Deliverables**
- [ ] Full E2E loan lifecycle test passes
- [ ] All portals polished and responsive
- [ ] Security review completed
- [ ] **M8 Milestone:** Feature complete

---

### Sprint 9 — UAT, Bug Fix & Release (Weeks 19–20)

**Goal:** UAT with business stakeholders, bug fixes, production deployment.

| ID | Task | Layer | Owner | Story Points |
|---|---|---|---|---|
| S9-01 | UAT test plan execution with Product Owner | QA | QA | 8 |
| S9-02 | UAT bug fixes (priority P0/P1) | All | All | 13 |
| S9-03 | Production Docker images + deployment scripts | DevOps | Tech Lead | 5 |
| S9-04 | Production Flyway migration runbook | Backend | BE-1 | 3 |
| S9-05 | Production environment configuration (secrets, CORS, S3) | DevOps | Tech Lead | 5 |
| S9-06 | Smoke test on production environment | QA | QA | 3 |
| S9-07 | Release notes + handover documentation | Tech Lead | Tech Lead | 3 |
| S9-08 | Retrospective + v1.1 backlog grooming | All | All | 2 |

**Sprint 9 Deliverables**
- [ ] UAT sign-off from Product Owner
- [ ] Production deployment successful
- [ ] Smoke tests pass on production
- [ ] **M9 Milestone:** v1.0 Released

---

### 2.1 Sprint Ceremonies

| Ceremony | Frequency | Duration | Participants |
|---|---|---|---|
| Sprint Planning | Start of sprint | 2 hours | Full team |
| Daily Standup | Daily | 15 min | Full team |
| Backlog Refinement | Mid-sprint | 1 hour | PO + Tech Lead + Devs |
| Sprint Review / Demo | End of sprint | 1 hour | Full team + PO |
| Sprint Retrospective | End of sprint | 45 min | Full team |

---

### 2.2 Definition of Ready (DoR)

A backlog item is ready for sprint when:
- [ ] Acceptance criteria defined
- [ ] API endpoint(s) identified in API Specification
- [ ] DB tables/columns identified
- [ ] UI wireframe or description available (for frontend items)
- [ ] Dependencies completed or scheduled in same sprint
- [ ] Story points estimated

### 2.3 Definition of Done (DoD)

A backlog item is done when:
- [ ] Code implemented and peer-reviewed (minimum 1 approval)
- [ ] Unit tests written (backend: ≥ 80% coverage on service layer)
- [ ] Integration test for API endpoints (backend)
- [ ] Swagger/OpenAPI updated if API changed
- [ ] Frontend: component renders without console errors
- [ ] No P0/P1 linter or SonarQube issues
- [ ] Merged to `develop` via PR
- [ ] Demo-able to Product Owner

---

## 3. Module Dependency

### 3.1 Database Entity Dependency Graph

```
roles (Master)
  │
  └──► users
         │
         ├──► customer_profile (1:1)
         │       │
         │       └──► loan_application (1:N)
         │               │
         │               ├── loan_type (N:1, Master)
         │               │
         │               ├──► documents (1:N)
         │               ├──► approval_history (1:N)
         │               ├──► loan_status_history (1:N)
         │               └──► emi_schedule (1:1)
         │
         ├──► notifications (1:N)
         └──► audit_log (1:N)
```

### 3.2 Backend Module Dependency Matrix

| Module | Depends On | Provides To |
|---|---|---|
| **common** | — | All modules |
| **auth** | common, roles/users (DB) | All secured modules |
| **customer** | auth, common | loan, document, admin |
| **loan** | auth, customer, common | document, approval, emi, notification |
| **document** | auth, loan, common (FileStorage) | loan (submit validation) |
| **approval** | auth, loan, common | notification, emi |
| **emi** | auth, loan, common | approval (schedule on approve) |
| **notification** | auth, common, event bus | — (leaf module) |
| **admin** | auth, common, all repositories | — (leaf module) |

### 3.3 Backend Module Dependency Diagram

```
                    ┌─────────────┐
                    │   common    │
                    │ (shared)    │
                    └──────┬──────┘
                           │
              ┌────────────▼────────────┐
              │         auth            │
              └────────────┬────────────┘
                           │
              ┌────────────▼────────────┐
              │       customer          │
              └────────────┬────────────┘
                           │
              ┌────────────▼────────────┐
              │         loan            │◄──── loan_type (seed)
              └──┬─────────┬────────────┘
                 │         │
        ┌────────▼──┐  ┌───▼────────┐
        │ document  │  │    emi     │
        └────────┬──┘  └───┬────────┘
                 │         │
              ┌──▼─────────▼──┐
              │   approval    │
              └──────┬────────┘
                     │
              ┌──────▼────────┐
              │ notification  │
              └───────────────┘

              ┌───────────────┐
              │    admin      │  (reads all modules)
              └───────────────┘
```

### 3.4 Frontend Module Dependency

| Feature Module | Depends On | Portal |
|---|---|---|
| `auth` | `api`, `components` | All |
| `customer-portal/profile` | `auth` | Customer |
| `customer-portal/loan-application` | `auth`, `profile` | Customer |
| `customer-portal/documents` | `auth`, `loan-application` | Customer |
| `customer-portal/emi-calculator` | `api` (public) | Customer |
| `officer-portal/verification` | `auth` | Officer |
| `manager-portal/approval` | `auth` | Manager |
| `finance-portal/sanction` | `auth` | Finance |
| `admin-portal/*` | `auth` | Admin |
| `notifications` | `auth` | All (shared component) |

### 3.5 Critical Path

The critical path for v1.0 delivery:

```
Sprint 0 (DB + common) → Sprint 1 (auth) → Sprint 2 (customer)
  → Sprint 3 (loan) → Sprint 4 (document + submit)
  → Sprint 5 (verify) → Sprint 6 (approve + disburse)
  → Sprint 7 (notification + admin) → Sprint 8 (E2E) → Sprint 9 (release)
```

**Any delay in auth (Sprint 1) or loan (Sprint 3) blocks all downstream sprints.**

---

## 4. Development Order

### 4.1 Backend Development Sequence

| Order | Module / Component | Sprint | Rationale |
|---|---|---|---|
| 1 | Project scaffold + Docker Compose | S0 | Foundation for all work |
| 2 | Flyway DDL (all 11 tables) + seed data | S0 | Database must exist before any entity |
| 3 | Common: exception handler, response wrapper, logging | S0 | Used by every module |
| 4 | Common: JWT utility + security config skeleton | S0 | Required before any secured endpoint |
| 5 | Auth: register, login, JWT filter | S1 | Gateway to all other modules |
| 6 | Auth: password management, email verification | S1 | Complete auth module |
| 7 | Customer: profile CRUD | S2 | Required before loan application |
| 8 | Loan: create (DRAFT), list, get, update | S3 | Core business entity |
| 9 | EMI: calculation service + public endpoint | S3 | Needed during loan application |
| 10 | Loan: submit, cancel, status history | S3–S4 | Completes customer-side flow |
| 11 | Document: upload, download, delete | S4 | Required before loan submit gate |
| 12 | Approval: verify (Maker) | S5 | First internal workflow step |
| 13 | Approval: approve/reject (Checker) | S6 | Second workflow step |
| 14 | Approval: sanction/disburse (Finance) | S6 | Final workflow steps |
| 15 | EMI: schedule generation on approval | S6 | Triggered by approval |
| 16 | Notification: in-app + event listeners | S7 | Depends on all status events |
| 17 | Admin: users, loan types, audit, reports | S7 | Reads from all modules |
| 18 | Integration tests + E2E | S8 | Validates full system |
| 19 | UAT fixes + deployment | S9 | Release |

### 4.2 Frontend Development Sequence

| Order | Feature | Sprint | Parallel With (Backend) |
|---|---|---|---|
| 1 | Project scaffold, theme, layout, routing shell | S0 | Backend S0 |
| 2 | Axios client, auth context, protected routes | S0–S1 | Backend S1 |
| 3 | Register + Login pages | S1 | Backend S1 |
| 4 | Customer profile form + dashboard | S2 | Backend S2 |
| 5 | EMI calculator (public page) | S3 | Backend S3 |
| 6 | Loan application form + my loans | S3 | Backend S3 |
| 7 | Document upload + submit flow | S4 | Backend S4 |
| 8 | Loan status stepper / tracker | S4 | Backend S4 |
| 9 | Officer portal: pending + verify | S5 | Backend S5 |
| 10 | Manager portal: approve/reject | S6 | Backend S6 |
| 11 | Finance portal: sanction/disburse + EMI schedule | S6 | Backend S6 |
| 12 | Notification bell + admin portal | S7 | Backend S7 |
| 13 | E2E tests + UI polish | S8 | Backend S8 |
| 14 | UAT bug fixes | S9 | Backend S9 |

### 4.3 Parallel Work Streams

From Sprint 2 onward, backend and frontend teams work in parallel on the same sprint goals:

```
Sprint N:
  Backend Dev 1 → API endpoints + service layer
  Backend Dev 2 → Tests + integration + next module prep
  Frontend Dev 1 → Primary feature pages
  Frontend Dev 2 → Secondary pages + shared components
  QA            → Test plan for Sprint N-1 features
```

### 4.4 Database Migration Order (Flyway)

| Migration | Tables | Sprint |
|---|---|---|
| `V1__create_roles.sql` | roles | S0 |
| `V2__create_users.sql` | users | S0 |
| `V3__create_customer_profile.sql` | customer_profile | S0 |
| `V4__create_loan_type.sql` | loan_type | S0 |
| `V5__create_loan_application.sql` | loan_application | S0 |
| `V6__create_documents.sql` | documents | S0 |
| `V7__create_approval_history.sql` | approval_history | S0 |
| `V8__create_loan_status_history.sql` | loan_status_history | S0 |
| `V9__create_emi_schedule.sql` | emi_schedule | S0 |
| `V10__create_notifications.sql` | notifications | S0 |
| `V11__create_audit_log.sql` | audit_log | S0 |
| `V12__seed_roles.sql` | Seed: 5 roles | S0 |
| `V13__seed_loan_type.sql` | Seed: Personal Loan | S0 |
| `V14__seed_admin_user.sql` | Seed: admin user | S0 |

All DDL in Sprint 0 — no schema changes in later sprints unless absolutely necessary. Any change requires a new Flyway versioned migration (`V15__...`).

---

## 5. Folder Structure

### 5.1 Monorepo Root

```
personal-loan-application/
├── .github/
│   └── workflows/
│       ├── backend-ci.yml
│       └── frontend-ci.yml
├── docs/
│   ├── Loan_Application_Version_1.0.pdf
│   ├── API_Design_Document.pdf
│   ├── API_Specification_Document.pdf
│   ├── Personal_Loan_DB_Design.pdf
│   ├── Technical_Architecture_Document.md
│   └── Development_Plan.md
├── backend/
├── frontend/
├── docker-compose.yml
├── .gitignore
└── README.md
```

### 5.2 Backend Structure

```
backend/
├── pom.xml
├── Dockerfile
└── src/
    ├── main/
    │   ├── java/com/personalloan/
    │   │   ├── PersonalLoanApplication.java
    │   │   │
    │   │   ├── config/
    │   │   │   ├── SecurityConfig.java
    │   │   │   ├── JwtConfig.java
    │   │   │   ├── CorsConfig.java
    │   │   │   ├── AsyncConfig.java
    │   │   │   ├── OpenApiConfig.java
    │   │   │   └── FileStorageConfig.java
    │   │   │
    │   │   ├── common/
    │   │   │   ├── dto/
    │   │   │   │   ├── ApiResponse.java
    │   │   │   │   ├── PageResponse.java
    │   │   │   │   └── ErrorResponse.java
    │   │   │   ├── exception/
    │   │   │   │   ├── BaseException.java
    │   │   │   │   ├── BusinessException.java
    │   │   │   │   ├── ResourceNotFoundException.java
    │   │   │   │   ├── DuplicateResourceException.java
    │   │   │   │   └── GlobalExceptionHandler.java
    │   │   │   ├── security/
    │   │   │   │   ├── JwtTokenProvider.java
    │   │   │   │   ├── JwtAuthenticationFilter.java
    │   │   │   │   ├── CustomUserDetailsService.java
    │   │   │   │   └── SecurityUtils.java
    │   │   │   ├── audit/
    │   │   │   │   ├── AuditAspect.java
    │   │   │   │   └── AuditLogService.java
    │   │   │   ├── storage/
    │   │   │   │   ├── FileStorageService.java
    │   │   │   │   └── LocalFileStorageService.java
    │   │   │   ├── notification/
    │   │   │   │   ├── EmailService.java
    │   │   │   │   ├── SmsService.java
    │   │   │   │   └── LoggingEmailService.java
    │   │   │   └── util/
    │   │   │       ├── EmiCalculator.java
    │   │   │       └── MaskingUtils.java
    │   │   │
    │   │   ├── module/
    │   │   │   ├── auth/
    │   │   │   │   ├── controller/AuthController.java
    │   │   │   │   ├── service/AuthService.java
    │   │   │   │   ├── repository/UserRepository.java, RoleRepository.java
    │   │   │   │   ├── entity/User.java, Role.java
    │   │   │   │   ├── dto/RegisterRequest.java, LoginRequest.java, ...
    │   │   │   │   └── mapper/UserMapper.java
    │   │   │   │
    │   │   │   ├── customer/
    │   │   │   │   ├── controller/CustomerController.java
    │   │   │   │   ├── service/CustomerProfileService.java
    │   │   │   │   ├── repository/CustomerProfileRepository.java
    │   │   │   │   ├── entity/CustomerProfile.java
    │   │   │   │   ├── dto/CustomerProfileRequest.java, CustomerProfileResponse.java
    │   │   │   │   └── mapper/CustomerProfileMapper.java
    │   │   │   │
    │   │   │   ├── loan/
    │   │   │   │   ├── controller/LoanController.java
    │   │   │   │   ├── service/LoanApplicationService.java, EligibilityService.java
    │   │   │   │   ├── repository/LoanApplicationRepository.java, LoanTypeRepository.java
    │   │   │   │   ├── entity/LoanApplication.java, LoanType.java, LoanStatusHistory.java
    │   │   │   │   ├── dto/LoanApplicationRequest.java, ...
    │   │   │   │   ├── mapper/LoanApplicationMapper.java
    │   │   │   │   └── enums/LoanStatus.java
    │   │   │   │
    │   │   │   ├── document/
    │   │   │   │   ├── controller/DocumentController.java
    │   │   │   │   ├── service/DocumentService.java
    │   │   │   │   ├── repository/DocumentRepository.java
    │   │   │   │   ├── entity/Document.java
    │   │   │   │   ├── dto/DocumentUploadResponse.java
    │   │   │   │   ├── mapper/DocumentMapper.java
    │   │   │   │   └── enums/DocumentType.java
    │   │   │   │
    │   │   │   ├── approval/
    │   │   │   │   ├── controller/ApprovalController.java
    │   │   │   │   ├── service/ApprovalService.java
    │   │   │   │   ├── repository/ApprovalHistoryRepository.java
    │   │   │   │   ├── entity/ApprovalHistory.java
    │   │   │   │   └── dto/VerifyRequest.java, ApproveRequest.java, ...
    │   │   │   │
    │   │   │   ├── emi/
    │   │   │   │   ├── controller/EmiController.java
    │   │   │   │   ├── service/EmiService.java
    │   │   │   │   ├── repository/EmiScheduleRepository.java
    │   │   │   │   ├── entity/EmiSchedule.java, EmiInstallment.java
    │   │   │   │   └── dto/EmiCalculateRequest.java, EmiScheduleResponse.java
    │   │   │   │
    │   │   │   ├── notification/
    │   │   │   │   ├── controller/NotificationController.java
    │   │   │   │   ├── service/NotificationService.java
    │   │   │   │   ├── repository/NotificationRepository.java
    │   │   │   │   ├── entity/Notification.java
    │   │   │   │   └── listener/LoanEventListener.java
    │   │   │   │
    │   │   │   └── admin/
    │   │   │       ├── controller/AdminController.java
    │   │   │       ├── service/AdminService.java, ReportService.java
    │   │   │       ├── repository/AuditLogRepository.java
    │   │   │       ├── entity/AuditLog.java
    │   │   │       └── dto/UserStatusRequest.java, LoanTypeRequest.java, ...
    │   │   │
    │   │   └── event/
    │   │       ├── LoanSubmittedEvent.java
    │   │       ├── LoanVerifiedEvent.java
    │   │       ├── LoanApprovedEvent.java
    │   │       ├── LoanRejectedEvent.java
    │   │       └── LoanDisbursedEvent.java
    │   │
    │   └── resources/
    │       ├── application.yml
    │       ├── application-dev.yml
    │       ├── application-prod.yml
    │       ├── db/migration/          # Flyway scripts V1–V14
    │       └── logback-spring.xml
    │
    └── test/
        ├── java/com/personalloan/
        │   ├── module/auth/AuthControllerIntegrationTest.java
        │   ├── module/loan/LoanApplicationServiceTest.java
        │   ├── module/approval/ApprovalFlowIntegrationTest.java
        │   └── integration/FullLoanLifecycleTest.java
        └── resources/
            └── application-test.yml
```

### 5.3 Frontend Structure

```
frontend/
├── package.json
├── tsconfig.json
├── vite.config.ts
├── Dockerfile
├── nginx.conf
├── index.html
└── src/
    ├── main.tsx
    ├── App.tsx
    ├── vite-env.d.ts
    │
    ├── api/
    │   ├── client.ts                  # Axios instance + interceptors
    │   ├── auth.api.ts
    │   ├── customer.api.ts
    │   ├── loan.api.ts
    │   ├── document.api.ts
    │   ├── approval.api.ts
    │   ├── emi.api.ts
    │   ├── notification.api.ts
    │   └── admin.api.ts
    │
    ├── auth/
    │   ├── AuthContext.tsx
    │   ├── AuthProvider.tsx
    │   ├── useAuth.ts
    │   └── tokenStorage.ts
    │
    ├── routes/
    │   ├── AppRoutes.tsx
    │   ├── ProtectedRoute.tsx
    │   └── RoleGuard.tsx
    │
    ├── components/
    │   ├── layout/
    │   │   ├── AppLayout.tsx
    │   │   ├── Sidebar.tsx
    │   │   ├── Header.tsx
    │   │   └── Footer.tsx
    │   ├── common/
    │   │   ├── DataTable.tsx
    │   │   ├── LoadingSpinner.tsx
    │   │   ├── ErrorAlert.tsx
    │   │   ├── ConfirmDialog.tsx
    │   │   ├── StatusBadge.tsx
    │   │   └── PageHeader.tsx
    │   └── notification/
    │       └── NotificationBell.tsx
    │
    ├── features/
    │   ├── auth/
    │   │   ├── LoginPage.tsx
    │   │   ├── RegisterPage.tsx
    │   │   ├── ForgotPasswordPage.tsx
    │   │   └── ResetPasswordPage.tsx
    │   │
    │   ├── customer-portal/
    │   │   ├── dashboard/CustomerDashboard.tsx
    │   │   ├── profile/ProfileForm.tsx, ProfileView.tsx
    │   │   ├── loan/LoanApplicationForm.tsx, MyLoansPage.tsx, LoanDetailPage.tsx
    │   │   ├── documents/DocumentUpload.tsx, DocumentList.tsx
    │   │   ├── emi/EmiCalculatorPage.tsx
    │   │   └── tracker/LoanStatusStepper.tsx
    │   │
    │   ├── officer-portal/
    │   │   ├── PendingLoansPage.tsx
    │   │   └── VerifyLoanPage.tsx
    │   │
    │   ├── manager-portal/
    │   │   ├── VerifiedLoansPage.tsx
    │   │   └── ApproveRejectPage.tsx
    │   │
    │   ├── finance-portal/
    │   │   ├── SanctionPage.tsx
    │   │   ├── DisbursePage.tsx
    │   │   └── EmiSchedulePage.tsx
    │   │
    │   └── admin-portal/
    │       ├── users/UserManagementPage.tsx
    │       ├── loan-types/LoanTypeManagementPage.tsx
    │       ├── reports/LoanReportPage.tsx, CustomerReportPage.tsx
    │       └── audit/AuditLogPage.tsx
    │
    ├── hooks/
    │   ├── useLoans.ts
    │   ├── useNotifications.ts
    │   └── usePagination.ts
    │
    ├── types/
    │   ├── api.types.ts               # ApiResponse<T>, PageResponse<T>
    │   ├── auth.types.ts
    │   ├── customer.types.ts
    │   ├── loan.types.ts
    │   ├── document.types.ts
    │   └── admin.types.ts
    │
    ├── schemas/                       # Zod validation schemas
    │   ├── auth.schema.ts
    │   ├── customer.schema.ts
    │   ├── loan.schema.ts
    │   └── admin.schema.ts
    │
    ├── theme/
    │   └── theme.ts                   # MUI theme customization
    │
    └── utils/
        ├── formatters.ts              # Currency, date formatting
        ├── constants.ts               # Loan statuses, roles, doc types
        └── errorHandler.ts            # Map API errors to UI messages
```

---

## 6. Git Branch Strategy

### 6.1 Branching Model — GitFlow (Adapted)

```
main ────────────────────────────────────────────────────► (production releases)
  ▲                                    ▲
  │                                    │
  │         release/v1.0 ──────────────┘
  │              ▲
  │              │
develop ──┬──┬──┬──┬──┬──┬──┬──┬──┬──► (integration branch)
          │  │  │  │  │  │  │  │  │
          │  │  │  │  │  │  │  │  └── feature/S9-uat-fixes
          │  │  │  │  │  │  │  └───── feature/S8-e2e-tests
          │  │  │  │  │  │  └──────── feature/S7-notifications
          │  │  │  │  │  └─────────── feature/S6-approval-finance
          │  │  │  │  └────────────── feature/S5-approval-maker
          │  │  │  └───────────────── feature/S4-documents
          │  │  └──────────────────── feature/S3-loan-emi
          │  └─────────────────────── feature/S2-customer-profile
          └────────────────────────── feature/S1-auth
```

### 6.2 Branch Types

| Branch | Naming Convention | Created From | Merged To | Purpose |
|---|---|---|---|---|
| **main** | `main` | — | — | Production-ready code only |
| **develop** | `develop` | `main` | — | Integration branch for all features |
| **feature** | `feature/S{n}-{short-desc}` | `develop` | `develop` | Sprint feature work |
| **bugfix** | `bugfix/{ticket-id}-{desc}` | `develop` | `develop` | Non-urgent bug fixes |
| **release** | `release/v{major}.{minor}` | `develop` | `main` + `develop` | Release stabilization |
| **hotfix** | `hotfix/{ticket-id}-{desc}` | `main` | `main` + `develop` | Production emergency fixes |

### 6.3 Branch Naming Examples

```
feature/S1-auth-module
feature/S1-auth-login-ui
feature/S3-loan-application-api
feature/S3-emi-calculator-ui
feature/S5-officer-portal
feature/S7-admin-audit-logs
bugfix/PLA-142-foir-calculation
release/v1.0
hotfix/PLA-201-jwt-expiry
```

### 6.4 Workflow Rules

| Rule | Detail |
|---|---|
| **Never commit directly to `main`** | All changes via PR |
| **Never commit directly to `develop`** | All changes via PR with review |
| **Feature branch lifespan** | Max 2 weeks (one sprint); rebase if longer |
| **PR required** | Minimum 1 approval before merge |
| **CI must pass** | Backend tests + frontend lint/build green |
| **Squash merge** | Feature branches squash-merged to `develop` |
| **Merge commit** | Release branches merge-committed to `main` |
| **Delete after merge** | Feature branches deleted after merge |
| **Rebase before PR** | Rebase feature branch on latest `develop` before opening PR |

### 6.5 Pull Request Template

Every PR must include:

```markdown
## Summary
Brief description of changes.

## Sprint / Task ID
S3-03, S3-13

## Type
- [ ] Feature
- [ ] Bug Fix
- [ ] Refactor
- [ ] Documentation

## Module
- [ ] Backend — auth / customer / loan / document / approval / emi / notification / admin
- [ ] Frontend — customer-portal / officer-portal / manager-portal / finance-portal / admin-portal

## Checklist
- [ ] Tests added/updated
- [ ] Swagger updated (if API change)
- [ ] No secrets committed
- [ ] Tested locally via Docker Compose
```

### 6.6 Commit Message Convention

Follow **Conventional Commits**:

```
<type>(<scope>): <description>

[optional body]
```

| Type | Usage |
|---|---|
| `feat` | New feature |
| `fix` | Bug fix |
| `refactor` | Code change without feature/fix |
| `test` | Adding/updating tests |
| `docs` | Documentation only |
| `chore` | Build, CI, dependencies |
| `style` | Formatting, no logic change |

**Examples:**
```
feat(auth): implement JWT login endpoint
feat(loan): add FOIR validation on loan submission
fix(document): enforce 5MB file size limit
test(approval): add Maker-Checker integration test
docs(readme): add Docker Compose setup instructions
chore(deps): upgrade Spring Boot to 3.3.5
```

**Scope values:** `auth`, `customer`, `loan`, `document`, `approval`, `emi`, `notification`, `admin`, `common`, `frontend`, `ci`, `db`

### 6.7 Release Process

```
1. Create release/v1.0 from develop
2. Freeze features — bug fixes only on release branch
3. Run full regression test suite
4. Update version in pom.xml and package.json
5. Merge release/v1.0 → main (tag: v1.0.0)
6. Merge release/v1.0 → develop
7. Deploy main to production
8. Delete release branch
```

---

## 7. Coding Standards

### 7.1 Java / Spring Boot Standards

#### 7.1.1 General Java

| Rule | Standard |
|---|---|
| Java version | 21 (use records, pattern matching, text blocks where appropriate) |
| Encoding | UTF-8 |
| Line length | Max 120 characters |
| Indentation | 4 spaces (no tabs) |
| Class naming | PascalCase: `LoanApplicationService` |
| Method naming | camelCase, verb-first: `submitLoanApplication()` |
| Constants | UPPER_SNAKE_CASE: `MAX_FILE_SIZE_BYTES` |
| Package naming | lowercase: `com.personalloan.module.loan` |
| Lombok usage | `@RequiredArgsConstructor`, `@Getter`, `@Builder`, `@Slf4j` — avoid `@Data` on entities |
| Null handling | Use `Optional<T>` for return types that may be absent; never return null collections |

#### 7.1.2 Layer Rules

| Layer | Rules |
|---|---|
| **Controller** | Annotate with `@RestController`, `@RequestMapping`. Use `@Valid` on request bodies. Return `ResponseEntity<ApiResponse<T>>`. No business logic. Max 20 lines per method. |
| **Service** | Annotate with `@Service`. `@Transactional` on write methods. Throw domain exceptions (never return error codes). Single responsibility. |
| **Repository** | Extend `JpaRepository<Entity, Long>`. Custom queries via `@Query` or method naming. No business logic. |
| **Entity** | Annotate with `@Entity`, `@Table(name = "snake_case")`. Use `@Column(name = "snake_case")`. Include audit columns. Never expose directly in API. |
| **DTO** | Separate request and response DTOs. Suffix: `Request`, `Response`. Use Jakarta validation annotations. |
| **Mapper** | MapStruct interfaces with `@Mapper(componentModel = "spring")`. Never map entity to entity. |

#### 7.1.3 Entity Example Pattern

```
@Entity
@Table(name = "loan_application")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private Long loanId;

    @Version
    private Long version;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;
}
```

#### 7.1.4 API Response Pattern

All controllers return the standard envelope via a utility or builder:

```
return ResponseEntity.status(HttpStatus.CREATED)
    .body(ApiResponse.success("Loan application submitted successfully.", data));
```

Never return raw entities or inconsistent response shapes.

#### 7.1.5 Exception Pattern

```
// In service layer
throw new BusinessException("Fixed Obligation to Income Ratio exceeds the allowed 60%");

// GlobalExceptionHandler catches and maps to HTTP 422 with standard error envelope
```

Never catch exceptions silently. Never expose stack traces in API responses.

#### 7.1.6 Validation

- **Controller layer:** Jakarta Bean Validation (`@NotBlank`, `@Email`, `@Size`, `@Pattern`)
- **Service layer:** Business rules (FOIR, status transitions, uniqueness)
- **Custom validators:** For PAN format, Aadhaar format, loan status transitions

#### 7.1.7 Security Annotations

```
@PreAuthorize("hasRole('LOAN_OFFICER')")
@PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
@PreAuthorize("#loanId == authentication.principal.loanId or hasRole('ADMIN')")
```

#### 7.1.8 Testing Standards

| Test Type | Convention | Location |
|---|---|---|
| Unit test | `{ClassName}Test.java` | `src/test/java/.../module/{name}/` |
| Integration test | `{ClassName}IntegrationTest.java` | Same |
| Full lifecycle test | `FullLoanLifecycleTest.java` | `src/test/java/.../integration/` |
| Test method naming | `should_{expectedBehavior}_when_{condition}` | — |
| Coverage target | ≥ 80% on service layer | Enforced in CI |

```
@Test
void should_rejectLoan_when_foirExceeds60Percent() { ... }
```

#### 7.1.9 Logging

```
@Slf4j
public class LoanApplicationService {

    public LoanResponse submitLoan(LoanRequest request) {
        log.info("Submitting loan application for customerId={}", request.getCustomerId());
        // ...
        log.info("Loan created: loanId={}, status=DRAFT", loan.getLoanId());
    }
}
```

- Use `@Slf4j` (Lombok)
- Never log passwords, tokens, full PAN/Aadhaar
- Use parameterized logging: `log.info("...", value)` — never string concatenation

---

### 7.2 TypeScript / React Standards

#### 7.2.1 General TypeScript

| Rule | Standard |
|---|---|
| TypeScript version | 5.x, strict mode enabled |
| File naming | PascalCase for components: `LoanApplicationForm.tsx` |
| Utility files | camelCase: `formatters.ts`, `errorHandler.ts` |
| Types/interfaces | PascalCase: `LoanApplication`, `ApiResponse<T>` |
| Enums/constants | UPPER_SNAKE_CASE: `LOAN_STATUS`, `DOCUMENT_TYPES` |
| No `any` | Use proper types; `unknown` for truly unknown values |
| Imports | Absolute imports via `@/` alias configured in Vite |

#### 7.2.2 Component Rules

| Rule | Standard |
|---|---|
| Component type | Functional components only (no class components) |
| One component per file | File name matches component name |
| Props | Define typed interface: `interface LoanFormProps { ... }` |
| Default export | One default export per component file |
| Max component size | ~200 lines; extract sub-components if larger |
| Hooks | Custom hooks in `hooks/` prefixed with `use` |

#### 7.2.3 Component Pattern

```
interface LoanApplicationFormProps {
  onSuccess: (loanId: number) => void;
}

export default function LoanApplicationForm({ onSuccess }: LoanApplicationFormProps) {
  const { control, handleSubmit } = useForm<LoanFormData>({
    resolver: zodResolver(loanSchema),
  });

  const mutation = useMutation({ mutationFn: loanApi.create });

  // ...
}
```

#### 7.2.4 State Management Rules

| State Type | Tool | When |
|---|---|---|
| Server/API data | TanStack Query (`useQuery`, `useMutation`) | Always for API data |
| Auth state | React Context (`AuthContext`) | Global auth only |
| Form state | React Hook Form | All forms |
| Local UI state | `useState` | Modals, tabs, toggles |
| Never | Redux, MobX | Not needed for this project |

#### 7.2.5 API Integration Pattern

```
// api/loan.api.ts
export const loanApi = {
  create: (data: LoanApplicationRequest) =>
    client.post<ApiResponse<LoanResponse>>('/loans', data),
  getMyLoans: (params: PaginationParams) =>
    client.get<ApiResponse<PageResponse<LoanResponse>>>('/loans/my-loans', { params }),
};

// hooks/useLoans.ts
export function useMyLoans(params: PaginationParams) {
  return useQuery({
    queryKey: ['loans', 'my', params],
    queryFn: () => loanApi.getMyLoans(params),
  });
}
```

#### 7.2.6 Form Validation Pattern

Zod schemas in `schemas/` mirror API Specification validation rules:

```
// schemas/loan.schema.ts
export const loanApplicationSchema = z.object({
  loanAmount: z.number().min(10000).max(5000000),
  loanTenureMonths: z.number().int().min(6).max(84),
  purpose: z.string().min(10).max(500),
  monthlyIncome: z.number().min(15000),
  existingEMIs: z.number().min(0).default(0),
});
```

#### 7.2.7 Routing Pattern

```
<Route element={<ProtectedRoute allowedRoles={['CUSTOMER']} />}>
  <Route path="/customer/loans" element={<MyLoansPage />} />
  <Route path="/customer/loans/new" element={<LoanApplicationForm />} />
</Route>
```

#### 7.2.8 Styling

- Use MUI components and `sx` prop for styling
- No inline CSS strings; no CSS modules unless necessary
- Theme customization in `theme/theme.ts`
- Responsive: use MUI breakpoints (`xs`, `sm`, `md`, `lg`)

#### 7.2.9 Error Handling

```
// utils/errorHandler.ts
export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error) && error.response?.data?.message) {
    return error.response.data.message;
  }
  return 'An unexpected error occurred. Please try again.';
}
```

Display errors via MUI `Snackbar` or `Alert` — never `alert()`.

#### 7.2.10 Frontend Testing

| Test Type | Tool | Convention |
|---|---|---|
| Unit/component | Vitest + React Testing Library | `{Component}.test.tsx` |
| E2E | Playwright or Cypress | `e2e/customer-flow.spec.ts` |
| Test location | Co-located or `__tests__/` folder | — |

---

### 7.3 Database / SQL Standards

| Rule | Standard |
|---|---|
| Migration tool | Flyway only; never manual DDL in production |
| Migration naming | `V{version}__{description}.sql` — e.g., `V1__create_roles.sql` |
| Table naming | snake_case, plural where applicable: `loan_applications` → per DB doc use `loan_application` |
| Column naming | snake_case: `created_at`, `loan_status` |
| Primary keys | `BIGINT AUTO_INCREMENT` |
| Monetary values | `DECIMAL(15,2)` |
| Timestamps | `TIMESTAMP DEFAULT CURRENT_TIMESTAMP` |
| Soft delete | `is_deleted BOOLEAN DEFAULT FALSE` on transactional tables |
| Indexes | `idx_{table}_{column}` — create in same migration as table |
| Seed data | Separate migration files: `V12__seed_roles.sql` |
| No raw SQL in Java | Use JPA repositories; `@Query` only when JPA naming insufficient |

---

### 7.4 Code Review Checklist

Reviewers must verify:

**Backend**
- [ ] No business logic in controller
- [ ] No entity exposed in API response (DTO only)
- [ ] `@Transactional` on write service methods
- [ ] `@PreAuthorize` on secured endpoints
- [ ] Validation on request DTOs
- [ ] Domain exceptions used (not generic RuntimeException)
- [ ] Unit tests for service layer
- [ ] No secrets, passwords, or tokens in code
- [ ] Lombok used appropriately (no `@Data` on entities)
- [ ] MapStruct mapper for entity ↔ DTO

**Frontend**
- [ ] Typed props and API responses
- [ ] Zod schema for form validation
- [ ] TanStack Query for API calls (no raw fetch in components)
- [ ] Loading and error states handled
- [ ] Role guard on protected routes
- [ ] No hardcoded API URLs (use env variable)
- [ ] No `console.log` in production code
- [ ] MUI components used consistently

**General**
- [ ] Conventional commit message
- [ ] PR description complete
- [ ] No unrelated changes in PR
- [ ] CI pipeline green

---

### 7.5 Static Analysis & Quality Gates

| Tool | Layer | Purpose | CI Gate |
|---|---|---|---|
| Checkstyle / SpotBugs | Backend | Code style, bug patterns | Warning only (Sprint 0–4), error (Sprint 5+) |
| SonarQube | Backend + Frontend | Code quality, coverage, vulnerabilities | No new blocker issues |
| ESLint | Frontend | Linting, import order | Error — must pass |
| Prettier | Frontend | Code formatting | Enforced via pre-commit hook |
| JaCoCo | Backend | Test coverage report | ≥ 80% service layer (Sprint 5+) |
| OWASP Dependency Check | Backend | Vulnerable dependencies | No critical CVEs |

---

## Appendix A — Sprint Velocity Tracking Template

| Sprint | Planned SP | Completed SP | Carry Over | Notes |
|---|---|---|---|---|
| Sprint 0 | 44 | | | |
| Sprint 1 | 52 | | | |
| Sprint 2 | 46 | | | |
| Sprint 3 | 55 | | | |
| Sprint 4 | 48 | | | |
| Sprint 5 | 44 | | | |
| Sprint 6 | 48 | | | |
| Sprint 7 | 58 | | | |
| Sprint 8 | 45 | | | |
| Sprint 9 | 42 | | | |

---

## Appendix B — Risk Register

| # | Risk | Impact | Probability | Mitigation |
|---|---|---|---|---|
| R1 | DB schema incomplete (DDL not finalized) | High | Medium | Complete all Flyway migrations in Sprint 0 before feature work |
| R2 | BRS/API inconsistencies cause rework | Medium | High | TAD resolves conflicts; PO sign-off before Sprint 3 |
| R3 | Auth delay blocks all modules | High | Low | Auth is Sprint 1 priority; no other module starts before auth merge |
| R4 | Third-party email/SMS not ready | Low | Medium | Stub implementations from Sprint 1; swap via config |
| R5 | Frontend/backend API contract mismatch | Medium | Medium | OpenAPI spec as contract; contract tests in Sprint 8 |
| R6 | Scope creep (sanction letter, CIBIL) | Medium | High | Strict v1.0 scope boundary (Section 1.4); v1.1 backlog |
| R7 | Insufficient test coverage | Medium | Medium | DoD enforces 80% service coverage from Sprint 5 |
| R8 | Key developer unavailability | High | Low | Module isolation enables handoff; document in README |

---

## Document Approval

| Role | Name | Signature | Date |
|---|---|---|---|
| Full Stack Architect | | | |
| Java Technical Lead | | | |
| Frontend Lead | | | |
| Product Owner | | | |
| QA Lead | | | |

---

*End of Development Plan v1.0*
