# DataScienceApp - Mini Learning Project

A pedagogic full-stack application demonstrating Spring Boot backend, Angular frontend, data pipeline processing, and Docker containerization.

## Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Maven 3.8+
- Docker & Docker Compose
- Git

### Local Development

**Backend (Terminal 1):**
```bash
cd backend
mvn clean install
mvn spring-boot:run
# API available at http://localhost:8080
```

**Frontend (Terminal 2):**
```bash
cd frontend
npm install
npm start
# App available at http://localhost:4200
```

### Docker Setup
```bash
docker-compose up
# Backend: http://localhost:8080
# Frontend: http://localhost
```

## Project Structure

```
DataScienceApp/
├── backend/              # Spring Boot REST API
├── frontend/             # Angular application
├── deployment/           # Docker & install4j configs
├── docs/                 # Documentation
├── scripts/              # Build & run scripts
└── README.md
```

## Learning Objectives

✓ Spring Boot REST API patterns  
✓ Angular state management with Observables  
✓ Feature flags and configuration management  
✓ Data pipeline processing  
✓ Docker containerization  
✓ End-to-end full-stack integration  

## Documentation

- [Setup Guide](docs/SETUP.md) - Local development setup
- [API Documentation](docs/API.md) - REST endpoints
- [Architecture](docs/ARCHITECTURE.md) - System design
- [Database Schema](docs/DATABASE.md) - Data model
- [Tasks](MINI_PROJECT_TASKS.md) - Implementation tasks

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.x, Maven |
| Frontend | Angular 17+, TypeScript, RxJS |
| Database | H2 (dev), PostgreSQL (prod) |
| Containerization | Docker, Docker Compose |
| UI Components | Bootstrap, Charts.js |

## Status

🟢 **Phase 1** - Project Scaffolding (IN PROGRESS)

## Next Steps

1. Initialize Spring Boot project
2. Configure database and entities
3. Implement feature flag system
4. Build CRUD REST APIs
5. Create Angular application
6. Implement data pipeline
7. Dockerize application
8. Add tests and documentation

---

**Reference:** Learning materials based on AlloSoft Core architecture patterns.
