# DataScienceApp Setup Guide

## Prerequisites

- Java 17+ (check with `java -version`)
- Maven 3.8+ (check with `mvn -version`)
- Node.js 18+ (check with `node -v` and `npm -v`)
- Git
- SQLite3 (optional, for CLI inspection)

## Backend Setup

### 1. Navigate to Backend Directory
```bash
cd backend
```

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Run Spring Boot Application
```bash
# Development (uses SQLite with Liquibase migrations)
mvn spring-boot:run

# Or with specific profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

The backend will be available at `http://localhost:8080`

### 4. Verify Database
```bash
# Check SQLite database file
ls -la data/app-dev.db

# Inspect with SQLite CLI (if installed)
sqlite3 data/app-dev.db
sqlite> .tables
sqlite> .schema batches
sqlite> .quit
```

### 5. Test API Endpoints
```bash
# Get feature flags
curl http://localhost:8080/api/featureFlags

# Get all samples
curl http://localhost:8080/api/samples

# Create a batch
curl -X POST http://localhost:8080/api/batches \
  -H "Content-Type: application/json" \
  -d '{"batchName":"BATCH001","description":"Test batch"}'
```

## Frontend Setup

### 1. Navigate to Frontend Directory
```bash
cd frontend
```

### 2. Install Dependencies
```bash
npm install
```

### 3. Serve Locally
```bash
npm start
# Or
ng serve
```

The frontend will be available at `http://localhost:4200`

## Database Migrations with Liquibase

### How It Works

1. **On Application Startup:**
   - Spring Boot automatically detects Liquibase in classpath
   - Liquibase reads `db/changelog/db.changelog-master.xml`
   - Master changelog includes versioned migrations
   - Each migration (changeset) is executed in order

2. **Database Tracking:**
   - Liquibase creates `DATABASECHANGELOG` table to track applied migrations
   - Prevents running same migration twice
   - Maintains version history

3. **Migration Files Location:**
   ```
   backend/src/main/resources/db/changelog/
   ├── db.changelog-master.xml        # Main entry point
   └── v1/
       └── 01-initial-schema.xml      # Version 1 migrations
   ```

### Adding New Migrations

1. Create new XML file in versioned directory:
```xml
<!-- backend/src/main/resources/db/changelog/v1/02-add-feature.xml -->
<changeSet id="005-add-feature" author="learn">
    <addColumn tableName="samples">
        <column name="new_column" type="VARCHAR(100)"/>
    </addColumn>
</changeSet>
```

2. Include in master changelog:
```xml
<include file="db/changelog/v1/02-add-feature.xml" relativeToChangelogFile="false"/>
```

3. Restart application - Liquibase will auto-detect and apply

## Docker Setup

### Build and Run with Docker Compose
```bash
# Build images
docker-compose build

# Start services
docker-compose up

# Backend: http://localhost:8080
# Frontend: http://localhost
```

### Individual Docker Commands
```bash
# Build backend image
docker build -f backend/Dockerfile -t data-science-app:backend .

# Build frontend image
docker build -f frontend/Dockerfile -t data-science-app:frontend .

# Run backend
docker run -p 8080:8080 data-science-app:backend

# Run frontend
docker run -p 80:80 data-science-app:frontend
```

## Troubleshooting

### Maven Build Issues
```bash
# Clear cache and rebuild
mvn clean
mvn install -U

# Check Java version
java -version
```

### Database Issues
```bash
# Remove database and let Liquibase recreate
rm data/app-dev.db

# Run application again
mvn spring-boot:run
```

### Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>

# Or change port in application.properties
# server.port=8081
```

### Angular Build Issues
```bash
# Clear node modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Clear Angular cache
ng cache clean
```

## Development Profiles

### Development (Default)
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```
- Database: `data/app-dev.db`
- Logging: DEBUG
- All feature flags: ENABLED
- H2 Console: N/A (using SQLite)

### Production
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```
- Database: `/var/app/data/app-prod.db`
- Logging: INFO
- Feature flags: DISABLED by default
- Best for testing production behavior

## Liquibase Documentation

- [Liquibase XML Format](https://docs.liquibase.com/concepts/basic/xml-format.html)
- [Liquibase Change Types](https://docs.liquibase.com/change-types/home.html)
- [Spring Boot Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.sql.init)

## Next Steps

1. ✓ Backend running with Liquibase migrations
2. ✓ SQLite database initialized
3. ⬜ Create Entity classes
4. ⬜ Create Repository interfaces
5. ⬜ Create Service layer
6. ⬜ Create REST Controllers
