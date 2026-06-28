# Personal Loan Application System

Monorepo for the Personal Loan Application System — Sprint 0 project setup.

## Project Structure

```
personal-loan-application/
├── backend/          # Spring Boot 3.3 + Java 21
├── frontend/         # React 18 + TypeScript + Vite
├── docs/             # Project documentation (PDFs + architecture docs)
└── docker-compose.yml
```

## Prerequisites

- Java 21
- Maven 3.9+
- Node.js 20+
- MySQL 8.0+ (or use Docker Compose)

## Quick Start (Local)

### 1. Start MySQL

```bash
docker compose up mysql -d
```

### 2. Backend

```bash
cd backend
mvn spring-boot:run
```

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

### 3. Frontend

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

- App: http://localhost:5173

## Quick Start (Docker Compose)

```bash
docker compose up --build
```

## Sprint 0 Status

- [x] Backend project scaffold (Spring Boot 3.3, Java 21, Maven)
- [x] Package structure for all modules
- [x] Flyway configuration + baseline migration
- [x] Security configuration skeleton
- [x] Logging configuration (Logback + MDC request ID)
- [x] Frontend project scaffold (React 18, TypeScript, Vite, MUI)
- [x] Theme configuration
- [x] React Router + TanStack Query setup
- [ ] Business APIs (Sprint 1+)
- [ ] Database schema migrations (Sprint 0 continuation)

## Documentation

See the `docs/` folder for BRS, API Design, API Specification, DB Design, TAD, and Development Plan.
