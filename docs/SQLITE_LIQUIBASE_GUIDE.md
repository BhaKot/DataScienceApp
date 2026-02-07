# SQLite + Liquibase vs H2 - Why This Choice

## Quick Comparison

| Aspect | SQLite + Liquibase | H2 + Auto DDL |
|--------|-------------------|---------------|
| **Configuration** | Requires Liquibase setup | Zero config (auto: create-drop) |
| **Learning Value** | ⭐⭐⭐⭐⭐ High | ⭐⭐ Low |
| **Production Ready** | ⭐⭐⭐⭐ Yes (for small/medium) | ⭐ No (testing only) |
| **Schema Versioning** | ⭐⭐⭐⭐⭐ Yes (tracked) | ⭐ No (recreated each run) |
| **Database Persistence** | ⭐⭐⭐⭐⭐ File-based (.db) | ⭐ In-memory (lost on restart) |
| **Team Collaboration** | ⭐⭐⭐⭐⭐ Yes (migrations in Git) | ⭐⭐ Difficult |
| **Migration History** | ⭐⭐⭐⭐⭐ Complete audit trail | ⭐ None |
| **Rollback Capability** | ⭐⭐⭐⭐ Supported by Liquibase | ⭐ Not possible |

---

## Why SQLite?

### ✅ Advantages
1. **Zero Configuration**: No server setup needed
2. **File-Based**: Single `app.db` file, easy to backup/version control
3. **Production Viable**: Used in real products (Slack, Discord, Firefox)
4. **Same Code Works Everywhere**: Switch to PostgreSQL with minimal changes
5. **Perfect for Learning**: Focus on code, not database administration
6. **Portable**: App + database in single container

### When to Use SQLite
- ✅ Learning projects
- ✅ Desktop applications
- ✅ Mobile apps
- ✅ IoT devices
- ✅ Simple web apps with single server
- ✅ Microservices with local cache

### When NOT to Use SQLite
- ❌ Large-scale distributed systems
- ❌ High concurrency requirements (>10 concurrent writers)
- ❌ Multi-server deployments (each gets separate DB)
- ❌ Real-time replication needs

---

## Why Liquibase?

### ✅ Advantages
1. **Version Control**: Track every schema change in Git
2. **Reproducibility**: Same migrations on dev, staging, production
3. **Team Friendly**: Multiple developers can add migrations in parallel
4. **Audit Trail**: Complete history of who changed what when
5. **Rollback Support**: (Can undo migrations if something breaks)
6. **Database Agnostic**: Switch databases without rewriting migrations
7. **CI/CD Ready**: Automated schema deployment in pipelines

### ✅ Learning Value
- Understand how real enterprises manage databases
- Learn SQL & XML in structured format
- See schema evolution patterns
- Practice database versioning

### ✅ Production Benefits
- Teams can collaborate safely
- Deployments are reproducible
- Database changes are auditable
- Easy to debug schema issues

---

## Liquibase Workflow in This Project

### On First Run
```
Maven build starts
  ↓
Spring Boot initializes
  ↓
Liquibase: "Is DATABASECHANGELOG table present?"
  ├─ No → Create tables + metadata
  └─ Yes → Continue
  ↓
Liquibase: "Read master changelog"
  ↓
Liquibase: "Compare changesets with DATABASECHANGELOG"
  ├─ New changesets → Execute them
  └─ Already executed → Skip
  ↓
Database ready with v1.0 schema
```

### On Subsequent Runs
```
Maven build starts
  ↓
Spring Boot initializes
  ↓
Liquibase: "Read master changelog"
  ↓
Liquibase: "Query DATABASECHANGELOG"
  ├─ Has: 001, 002, 003, 004
  └─ Looking for: 001, 002, 003, 004
  ↓
All changesets already applied
  ↓
Continue to API layer
```

### Adding New Migration
```
Developer: Creates db/changelog/v1/02-add-users.xml
  ↓
Developer: Includes in db.changelog-master.xml
  ↓
Maven build
  ↓
Liquibase: Detects new changeset
  ↓
Executes 002 changeset
  ↓
Updates DATABASECHANGELOG
  ↓
Schema updated
```

---

## Migration Format: XML vs SQL

### Liquibase XML (What We Use)
```xml
<changeSet id="002-add-users" author="learn">
    <createTable tableName="users">
        <column name="id" type="BIGINT" autoIncrement="true">
            <constraints primaryKey="true"/>
        </column>
        <column name="email" type="VARCHAR(100)">
            <constraints unique="true"/>
        </column>
    </createTable>
</changeSet>
```

### Raw SQL (Alternative)
```xml
<changeSet id="002-add-users" author="learn">
    <sql>
        CREATE TABLE users (
            id BIGINT PRIMARY KEY AUTO_INCREMENT,
            email VARCHAR(100) UNIQUE NOT NULL
        )
    </sql>
</changeSet>
```

### Why XML?
- Database agnostic (SQLite vs PostgreSQL vs MySQL)
- Type-safe (prevents syntax errors)
- Rollback-friendly (framework knows how to undo)
- Better for CI/CD pipelines
- IDE support and validation

---

## SQLite vs H2 vs PostgreSQL

### Development: SQLite ✅ (What We Chose)
```properties
spring.datasource.url=jdbc:sqlite:data/app.db
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
# File persists, portable, zero config
```

### Development (Alternative): H2
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
# In-memory only, lost on restart - not ideal for learning
```

### Production: PostgreSQL
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
# Networked, scalable, but needs setup
```

### Key Point
**Same Liquibase migrations work for all three!** Only properties change.

---

## Migration Management: DATABASECHANGELOG Table

Liquibase tracks every execution:

```sql
SELECT * FROM DATABASECHANGELOG;

-- Output:
-- ID  | AUTHOR | FILENAME | DATEEXECUTED | ORDEREXECUTED | EXECTYPE
-- 001 | learn  | 01-...   | 2026-02-07   | 1             | EXECUTED
-- 002 | learn  | 01-...   | 2026-02-07   | 2             | EXECUTED
-- 003 | learn  | 01-...   | 2026-02-07   | 3             | EXECUTED
-- 004 | learn  | 01-...   | 2026-02-07   | 4             | EXECUTED
```

This prevents running migrations twice (idempotency).

---

## Real-World Example: LinkedIn

LinkedIn uses similar patterns:
1. All schema changes go through migrations
2. Migrations are code-reviewed
3. Deployed alongside application
4. Rollback capability for safety
5. Audit trail for compliance

---

## Practical Benefits for This Project

### For Learning
✅ See real-world database patterns  
✅ Practice writing migrations  
✅ Understand versioning concepts  
✅ Learn SQL schema design  

### For Collaboration
✅ Multiple people can work on schema  
✅ No merge conflicts in properties  
✅ Each migration is isolated  
✅ Git history shows who changed what  

### For Deployment
✅ Docker containers auto-run migrations  
✅ No manual SQL scripts needed  
✅ Reproducible across environments  
✅ Easy to debug schema issues  

---

## When Would You Switch?

### Switch to PostgreSQL (Production)
```bash
# 1. Change dependency in pom.xml
# 2. Update application-prod.properties:
spring.datasource.url=jdbc:postgresql://...
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# 3. SAME migrations work!
# 4. Deploy
```

### Why This Is Possible
- Liquibase abstracts SQL differences
- JPA handles dialect switching
- No code changes needed
- Just configuration

---

## Common Misconceptions

### ❌ "I need PostgreSQL to learn databases"
✅ Actually: SQLite teaches the same concepts, faster to set up

### ❌ "Liquibase is too complex for small projects"
✅ Actually: More complex to manage schema WITHOUT version control

### ❌ "H2 is better for development"
✅ Actually: H2 teaches bad habits (auto-DDL doesn't exist in production)

### ❌ "SQLite can't be production database"
✅ Actually: SQLite IS production database for millions of apps

---

## Summary: Why This Stack for DataScienceApp

| Layer | Choice | Reason |
|-------|--------|--------|
| **Backend Framework** | Spring Boot | Industry standard, feature-rich |
| **Database** | SQLite | Zero config, file-based, portable |
| **Migration Tool** | Liquibase | Version control, team friendly, DB-agnostic |
| **JPA Provider** | Hibernate | Works with Liquibase, supports all DBs |
| **Properties Config** | Spring profiles | Dev/prod separation, environment flexibility |

**Result**: Professional architecture in minimal time. When you're ready for production, same code scales to PostgreSQL.

---

## Next: Create Entities

Once you understand this architecture, implement:
1. `BatchEntity.java` with @Entity annotations
2. `SampleEntity.java` with FK to Batch
3. `ResultEntity.java` with FK to Batch
4. Corresponding `*Repository` interfaces

Then Spring Data JPA will auto-generate CRUD methods!
