# Personal Loan Application System (Modular Monolith)

A secure, premium personal loan onboarding and processing system built using a decoupled **Modular Monolith** architecture.

---

## 🚀 Technology Stack

### Backend
* **Core:** Java 17, Spring Boot 3.x, Spring Security (JWT authentication, rate limiting)
* **Database & Migrations:** MySQL 8, Flyway DB, Spring Data JPA
* **Cryptography:** AES-256-GCM (Aadhaar/PAN at-rest encryption), SHA-256 (File duplication hashing)
* **Testing:** JUnit 5, Mockito, MockMvc, Testcontainers (database-locked validation checks)

### Frontend
* **Core:** React 18, TypeScript, Vite, React Router DOM (v6)
* **UI & Theming:** Material UI (MUI), Light/Dark dual theme
* **State & Forms:** TanStack React Query, React Hook Form, Zod
* **Testing:** Vitest, React Testing Library, JSDOM

---

## 🏛️ Modular Architecture

The application is structured as a **Modular Monolith** to support future microservices extraction:
* **Strict Boundary Decoupling:** Business packages (`auth`, `customer`, `loan`, `document`, `approval`, `admin`) communicate exclusively through public interfaces (Facades) and Spring Application Events.
* **Primitive Entity Keys:** Entities reference objects in downstream modules using database IDs (e.g. `Long customerId` instead of direct JPA `@ManyToOne` entity mapping).

```
e:\Loan Application
├── .github/workflows       # CI/CD GitHub Actions pipelines
├── backend                 # Spring Boot Backend
│   ├── src/main/java       # Source classes partitioned by module
│   ├── src/main/resources  # Schema migrations (db/migration/)
│   └── pom.xml
└── frontend                # React + TS Frontend
    ├── src/api             # TanStack axios services
    ├── src/components      # Reusable layouts and route guards
    ├── src/pages           # Auth & Dashboard page views
    └── package.json
```

---

## ⚙️ Getting Started

### 1. Database Setup
Execute Flyway migrations automatically by setting database credentials in `backend/src/main/resources/application.properties`.

### 2. Run Backend Services
```bash
cd backend
mvn spring-boot:run
```

### 3. Run Frontend Portal
```bash
cd frontend
npm install
npm run dev
```

---

## 🧪 Testing Suites

### Running Backend Tests
Runs all unit and Testcontainers integration tests:
```bash
cd backend
mvn test
```

### Running Frontend Tests
Runs UI component test suites:
```bash
cd frontend
npm run test
```
