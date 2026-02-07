# DataScienceApp - Revised 12-Phase Implementation Plan

## Overview

This is the executable task list for building DataScienceApp end-to-end, including Docker early for testing and install4j packaging.

**Total Phases**: 12  
**Estimated Time**: 12-15 hours  
**Key Milestones**: Docker (Phase 4), install4j (Phase 5 & 11)

---

## Phase 1: ✅ Project Scaffolding (COMPLETE)

All backend structure, SQLite, Liquibase, and configuration completed.

**Deliverable**: Project ready for entity implementation

---

## Phase 2: Backend Entities & Repositories (1.5 hours)

### Tasks
- [ ] Create `BatchEntity.java` with @Entity, @Table, Lombok @Data
- [ ] Create `SampleEntity.java` with FK to Batch
- [ ] Create `ResultEntity.java` with FK to Batch
- [ ] Create `BatchRepository` interface extending JpaRepository
- [ ] Create `SampleRepository` interface extending JpaRepository
- [ ] Create `ResultRepository` interface extending JpaRepository
- [ ] Write repository tests with @DataJpaTest

**Files to Create**:
- `backend/src/main/java/com/learn/entity/BatchEntity.java`
- `backend/src/main/java/com/learn/entity/SampleEntity.java`
- `backend/src/main/java/com/learn/entity/ResultEntity.java`
- `backend/src/main/java/com/learn/repository/BatchRepository.java`
- `backend/src/main/java/com/learn/repository/SampleRepository.java`
- `backend/src/main/java/com/learn/repository/ResultRepository.java`
- `backend/src/test/java/com/learn/repository/RepositoryTest.java`

**Learning Focus**:
- JPA @Entity and relationship annotations
- @OneToMany, @ManyToOne, @JoinColumn
- Spring Data JPA magic methods
- Testing with @DataJpaTest

**Success Criteria**:
- Entities compile without errors
- Repository methods auto-generate (findAll, save, delete)
- Tests pass with @DataJpaTest

---

## Phase 3: Services & REST Controllers (2 hours)

### Tasks
- [ ] Create `BatchService` with CRUD business logic
- [ ] Create `SampleService` with CRUD business logic
- [ ] Create `BatchController` (@RestController) with GET, POST, DELETE
- [ ] Create `SampleController` (@RestController) with GET, POST, DELETE
- [ ] Create DTOs: `BatchDTO`, `SampleDTO`, `ResultDTO`
- [ ] Create mappers: `BatchMapper`, `SampleMapper`
- [ ] Write controller tests with @WebMvcTest

**Files to Create**:
- `backend/src/main/java/com/learn/service/BatchService.java`
- `backend/src/main/java/com/learn/service/SampleService.java`
- `backend/src/main/java/com/learn/controller/BatchController.java`
- `backend/src/main/java/com/learn/controller/SampleController.java`
- `backend/src/main/java/com/learn/dto/BatchDTO.java`
- `backend/src/main/java/com/learn/dto/SampleDTO.java`
- `backend/src/main/java/com/learn/dto/ResultDTO.java`
- `backend/src/main/java/com/learn/mapper/BatchMapper.java`
- `backend/src/main/java/com/learn/mapper/SampleMapper.java`
- `backend/src/test/java/com/learn/controller/ControllerTest.java`

**Endpoints to Implement**:
```
GET    /api/batches              - List all batches
POST   /api/batches              - Create batch
GET    /api/batches/{id}         - Get batch detail
DELETE /api/batches/{id}         - Delete batch

GET    /api/samples              - List all samples
POST   /api/samples              - Create sample
GET    /api/samples/{id}         - Get sample detail
DELETE /api/samples/{id}         - Delete sample
```

**Learning Focus**:
- @RestController and @RequestMapping
- @GetMapping, @PostMapping, @DeleteMapping
- Request/response DTOs
- Entity to DTO mapping
- Exception handling

**Success Criteria**:
- All endpoints respond with JSON
- CRUD operations work
- Tests pass with @WebMvcTest
- Postman/curl can query endpoints

---

## Phase 4: Docker Setup (1 hour)

### Tasks
- [ ] Create `backend/Dockerfile` (multi-stage build)
- [ ] Create `docker-compose.yml` with backend + SQLite volume
- [ ] Build Docker image: `docker build -f backend/Dockerfile -t datascienceapp:backend .`
- [ ] Test with Docker: `docker-compose up`
- [ ] Verify API responds on `http://localhost:8080`
- [ ] Test database persistence: stop/start container, data persists

**Files to Create**:
- `backend/Dockerfile`
- `docker-compose.yml`

**Dockerfile Template**:
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY backend/pom.xml .
RUN mvn dependency:go-offline
COPY backend/src src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-slim
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**docker-compose.yml Template**:
```yaml
version: '3.8'
services:
  backend:
    build:
      context: .
      dockerfile: backend/Dockerfile
    ports:
      - "8080:8080"
    volumes:
      - ./data:/app/data
    environment:
      - SPRING_PROFILES_ACTIVE=dev
```

**Learning Focus**:
- Multi-stage Docker builds
- Volume persistence
- Container networking
- Docker Compose orchestration

**Success Criteria**:
- Docker image builds without errors
- Container starts successfully
- API responds: `curl http://localhost:8080/api/batches`
- Database file persists in volume

---

## Phase 5: install4j Packaging & Installer (1.5 hours)

### Tasks
- [ ] Download and install install4j (if not already)
- [ ] Create `deployment/install4j/project.install4j` configuration
- [ ] Configure installer:
  - App name: "DataScienceApp"
  - Include backend JAR
  - Include SQLite database
  - Create start scripts
  - Add uninstaller
- [ ] Build installer: `install4j project.install4j`
- [ ] Test installation on local machine
- [ ] Test uninstallation
- [ ] Verify app starts after installation

**Files to Create**:
- `deployment/install4j/project.install4j` (XML config)
- `deployment/install4j/build.xml` (optional build script)

**Learning Focus**:
- install4j project configuration
- Bundling JARs in installers
- Windows/Mac/Linux installers
- Uninstall scripts
- Post-install scripts

**Success Criteria**:
- Installer executable created (.exe, .dmg, or .sh)
- Installation completes without errors
- App runs after installation
- Uninstaller removes all files
- Can reinstall successfully

---

## Phase 6: Feature Flags System (1 hour)

### Tasks
- [ ] Create `FeatureFlags.java` enum with all flags
- [ ] Create `FeatureFlagConfiguration.java` component reading from properties
- [ ] Create `FeatureFlagManager.java` service collecting all flags
- [ ] Create `FeatureFlagController.java` with GET endpoint
- [ ] Create `FeatureFlagsDTO.java`
- [ ] Add feature flags to initial state in frontend (later)
- [ ] Test: `curl http://localhost:8080/api/featureFlags`

**Files to Create**:
- `backend/src/main/java/com/learn/dto/FeatureFlags.java` (enum)
- `backend/src/main/java/com/learn/config/FeatureFlagConfiguration.java`
- `backend/src/main/java/com/learn/service/FeatureFlagManager.java`
- `backend/src/main/java/com/learn/controller/FeatureFlagController.java`
- `backend/src/main/java/com/learn/dto/FeatureFlagsDTO.java`

**Endpoint**:
```
GET /api/featureFlags
Response:
{
  "BATCHES_TAB_ENABLED": true,
  "ADVANCED_DASHBOARD_ENABLED": true,
  "EXPORT_CSV_ENABLED": true
}
```

**Learning Focus**:
- Feature flag patterns (AlloSoft reference)
- Runtime configuration without rebuilding
- Property-driven flags
- Centralized management

**Success Criteria**:
- GET /api/featureFlags returns JSON
- Dev profile shows all enabled
- Prod profile shows all disabled
- Can toggle in properties without code changes

---

## Phase 7: Data Pipeline (CSV Parser + Processor) (1.5 hours)

### Tasks
- [ ] Create `CSVParser.java` to parse CSV files
- [ ] Create `DataProcessor.java` to process parsed data
- [ ] Create `ResultGenerator.java` to generate results
- [ ] Create `PipelineService.java` orchestrating the flow
- [ ] Create `PipelineController.java` with file upload endpoint
- [ ] Create test CSV file: `data/sample.csv`
- [ ] Test with: `curl -F "file=@data/sample.csv" http://localhost:8080/api/pipeline/upload`

**Files to Create**:
- `backend/src/main/java/com/learn/pipeline/CSVParser.java`
- `backend/src/main/java/com/learn/pipeline/DataProcessor.java`
- `backend/src/main/java/com/learn/pipeline/ResultGenerator.java`
- `backend/src/main/java/com/learn/service/PipelineService.java`
- `backend/src/main/java/com/learn/controller/PipelineController.java`
- `data/sample.csv`

**Endpoints**:
```
POST /api/pipeline/upload              - Upload CSV, process, return results
GET  /api/pipeline/results             - Get all processed results
GET  /api/pipeline/results/{batchId}   - Get results for batch
```

**Sample CSV Format**:
```
Sample,Ct_Value,Dye
S001,25.3,FAM
S002,26.1,HEX
S003,24.8,ROX
```

**Expected Output**:
```json
{
  "id": 1,
  "batchId": 1,
  "samples": [
    {"sampleId": "S001", "ct": 25.3, "dye": "FAM", "status": "VALID", "score": 95.5},
    {"sampleId": "S002", "ct": 26.1, "dye": "HEX", "status": "VALID", "score": 92.3}
  ]
}
```

**Learning Focus**:
- File parsing with Apache Commons CSV
- Data transformation algorithms
- Batch processing
- Error handling for invalid data

**Success Criteria**:
- CSV parser correctly reads files
- Data processor calculates scores
- Results persisted to database
- Upload endpoint works
- Can retrieve results via API

---

## Phase 8: Frontend - Angular Project Setup (1 hour)

### Tasks
- [ ] Initialize Angular project in `frontend/` directory
- [ ] Configure tsconfig.json, angular.json
- [ ] Create `package.json` with dependencies
- [ ] Install dependencies: `npm install`
- [ ] Verify build: `ng build`
- [ ] Verify serve: `ng serve`

**Commands**:
```bash
cd frontend
npm install
ng serve
# Should be available at http://localhost:4200
```

**Verify**:
- [ ] Angular CLI installed
- [ ] Project structure created
- [ ] npm dependencies installed
- [ ] ng serve starts successfully
- [ ] Can view default Angular app in browser

**Learning Focus**:
- Angular project structure
- TypeScript configuration
- npm dependency management

**Success Criteria**:
- Angular app runs on localhost:4200
- Default welcome page displays
- No build errors

---

## Phase 9: Frontend - Core Components & State (2 hours)

### Tasks
- [ ] Create `AppStateService` with BehaviorSubject for global state
- [ ] Create models: `Batch.ts`, `Sample.ts`, `Result.ts`, `FeatureFlags.ts`
- [ ] Create `ApiService` wrapping HttpClient
- [ ] Create `SampleListComponent` displaying samples in table
- [ ] Create `SampleFormComponent` for create/edit
- [ ] Create `BatchListComponent` displaying batches
- [ ] Create `DashboardComponent` with statistics
- [ ] Create routing in `app-routing.module.ts`

**Files to Create**:
- `frontend/src/app/shared/state/app-state.service.ts`
- `frontend/src/app/shared/models/batch.ts`
- `frontend/src/app/shared/models/sample.ts`
- `frontend/src/app/core/services/api.service.ts`
- `frontend/src/app/features/samples/list/sample-list.component.ts`
- `frontend/src/app/features/samples/form/sample-form.component.ts`
- `frontend/src/app/features/batches/list/batch-list.component.ts`
- `frontend/src/app/features/dashboard/dashboard.component.ts`
- `frontend/src/app/app-routing.module.ts`

**Learning Focus**:
- RxJS BehaviorSubject + Observable
- Angular services and dependency injection
- Component lifecycle with ngOnInit, ngOnDestroy
- Template binding and *ngIf, *ngFor
- Reactive Forms
- HTTP client patterns

**Success Criteria**:
- Components render without errors
- Routing works (navigate between pages)
- State management centralizes data
- Can call backend APIs from frontend

---

## Phase 10: Feature Flags in UI (0.5 hours)

### Tasks
- [ ] Load feature flags on app startup
- [ ] Add feature flags to state
- [ ] Hide/show Batches tab based on flag
- [ ] Hide/show Dashboard based on flag
- [ ] Test with dev profile (all visible)
- [ ] Test with prod profile (all hidden)

**Implementation**:
- Load flags in `app.component.ts` ngOnInit
- Use `*ngIf="(featureFlagEnabled$ | async)"` in templates
- Toggle in application*.properties

**Learning Focus**:
- Conditional rendering with async pipe
- Feature flag integration
- Profile-based UI changes

**Success Criteria**:
- Features show in dev, hide in prod
- Can toggle flag without code changes
- Conditional rendering works correctly

---

## Phase 11: Docker Frontend + Full Stack (1 hour)

### Tasks
- [ ] Create `frontend/Dockerfile` (Node builder + nginx)
- [ ] Create `frontend/nginx.conf` for serving Angular
- [ ] Update `docker-compose.yml` to include frontend
- [ ] Update docker-compose with backend + frontend + volumes
- [ ] Build both images: `docker-compose build`
- [ ] Start full stack: `docker-compose up`
- [ ] Verify backend on `http://localhost:8080`
- [ ] Verify frontend on `http://localhost`

**Frontend Dockerfile Template**:
```dockerfile
FROM node:18 AS builder
WORKDIR /app
COPY frontend/package*.json ./
RUN npm install
COPY frontend .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist/frontend /usr/share/nginx/html
COPY frontend/nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**nginx.conf Template**:
```nginx
events {}
http {
  server {
    listen 80;
    location / {
      root /usr/share/nginx/html;
      try_files $uri $uri/ /index.html;
    }
    location /api {
      proxy_pass http://backend:8080;
    }
  }
}
```

**docker-compose.yml Update**:
```yaml
version: '3.8'
services:
  backend:
    # ... existing config
    networks:
      - app-network
  
  frontend:
    build:
      context: .
      dockerfile: frontend/Dockerfile
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - app-network

networks:
  app-network:
    driver: bridge
```

**Learning Focus**:
- Multi-stage Docker builds for frontend
- Nginx configuration for SPA
- Docker networking between services
- Reverse proxy setup

**Success Criteria**:
- Both services start successfully
- Backend responds on :8080
- Frontend served on :80
- Frontend can call backend APIs
- Full-stack integration works

---

## Phase 12: install4j Full Stack Packaging (1 hour)

### Tasks
- [ ] Update install4j project to include frontend
- [ ] Configure installer to:
  - Extract JAR to program directory
  - Extract Angular build to webroot
  - Create startup scripts for both services
  - Create stop/start scripts
  - Add to system tray (optional)
- [ ] Rebuild installer
- [ ] Test complete installation flow:
  - Install app
  - Backend runs on :8080
  - Frontend accessible
  - Data persists between restarts
  - Uninstall removes all files

**Files to Update**:
- `deployment/install4j/project.install4j` (add frontend config)

**Installer Features**:
- Windows .exe installer
- Mac .dmg installer
- Linux .sh installer
- Auto-start on boot (Windows registry entry)
- Uninstaller

**Learning Focus**:
- Full-stack application packaging
- Installer bundling (backend + frontend)
- System integration
- Deployment strategy

**Success Criteria**:
- Installer creates successfully
- Installation completes without errors
- Both backend and frontend start after install
- Database initialized and accessible
- Uninstall completely removes application
- Can reinstall successfully

---

## Phase 13: Testing & Documentation (1 hour)

### Tasks
- [ ] Write unit tests for services
- [ ] Write integration tests for controllers
- [ ] Write component tests for Angular
- [ ] Write E2E test scenarios
- [ ] Create API documentation
- [ ] Create deployment guide
- [ ] Create troubleshooting guide
- [ ] Test complete end-to-end flow

**Test Coverage**:
- [ ] Service layer logic
- [ ] Controller endpoints
- [ ] Repository queries
- [ ] Component rendering
- [ ] State management
- [ ] API integration

**Documentation**:
- [ ] README (updated)
- [ ] Setup guide
- [ ] API reference
- [ ] Deployment guide
- [ ] Troubleshooting

**E2E Scenarios**:
- Create batch → Add samples → Process CSV → View results
- Toggle feature flags → Verify UI changes
- Install app → Run backend → Run frontend → Verify

**Success Criteria**:
- Tests pass (>80% coverage)
- Documentation complete
- E2E scenarios work
- No errors in any flow

---

## Time Allocation Summary

| Phase | Tasks | Time | Status |
|-------|-------|------|--------|
| 1. Scaffolding | Setup | ✓ Done | Complete |
| 2. Entities | Repository layer | 1.5 hrs | Ready |
| 3. Services | CRUD controllers | 2 hrs | Ready |
| 4. Docker | Backend container | 1 hr | Ready |
| 5. install4j | Installer packaging | 1.5 hrs | Ready |
| 6. Feature Flags | Flag system | 1 hr | Ready |
| 7. Data Pipeline | CSV processor | 1.5 hrs | Ready |
| 8. Frontend Setup | Angular init | 1 hr | Ready |
| 9. Components | UI layer | 2 hrs | Ready |
| 10. Flags UI | Conditional rendering | 0.5 hrs | Ready |
| 11. Docker Stack | Full containerization | 1 hr | Ready |
| 12. install4j Stack | Full packaging | 1 hr | Ready |
| 13. Testing | Tests + docs | 1 hr | Ready |
| **TOTAL** | **All phases** | **~15 hrs** | **Ready** |

---

## Success Criteria: All Phases

### Backend
- ✅ Spring Boot runs locally
- ✅ SQLite database initialized by Liquibase
- ✅ All CRUD endpoints working
- ✅ Feature flags configurable
- ✅ CSV pipeline processing data
- ✅ Dockerized successfully

### Frontend
- ✅ Angular app runs locally
- ✅ Components display data from backend
- ✅ Feature flags control visibility
- ✅ Forms create/edit data
- ✅ Dockerized successfully

### Packaging
- ✅ Docker images build
- ✅ docker-compose orchestrates both services
- ✅ install4j creates installer
- ✅ Installer installs/uninstalls cleanly
- ✅ App runs from installation

### Testing
- ✅ Unit tests pass
- ✅ Integration tests pass
- ✅ E2E scenarios verified
- ✅ No runtime errors

---

## Next Action

Start **Phase 2: Backend Entities & Repositories**

Files to create:
1. `BatchEntity.java`
2. `SampleEntity.java`
3. `ResultEntity.java`
4. Repository interfaces
5. Tests

Ready to proceed?
