# Liquibase + SQLite Quick Reference

## Why Liquibase + SQLite?

**Liquibase Benefits:**
- Version control for database schema
- Track all schema changes with timestamps
- Easy rollback capabilities
- Database agnostic (works with SQLite, PostgreSQL, MySQL, etc.)
- Prevents duplicate migrations
- Supports complex schema evolution

**SQLite Benefits:**
- Zero configuration database
- Single file storage (data/app-dev.db)
- Perfect for learning and testing
- Fast, lightweight, portable
- No server needed

## Project Structure

```
backend/src/main/resources/
└── db/changelog/
    ├── db.changelog-master.xml       # Entry point
    └── v1/
        └── 01-initial-schema.xml     # Version 1 migrations
```

## How Liquibase Works in This Project

### 1. Application Startup
```
Spring Boot starts
  ↓
Detects Liquibase on classpath
  ↓
Reads db.changelog-master.xml
  ↓
Executes all <changeSet> elements in order
  ↓
Creates DATABASECHANGELOG table to track migrations
  ↓
Database is ready
```

### 2. Database Change Tracking

Liquibase creates two tables:
- `DATABASECHANGELOG` - History of applied migrations
- `DATABASECHANGELOGLOCK` - Prevents concurrent migrations

```sql
-- View applied migrations
SELECT * FROM DATABASECHANGELOG;

-- Output:
-- | ID  | AUTHOR | FILENAME | DATEEXECUTED | ORDEREXECUTED | EXECTYPE | MD5SUM | ... |
-- | 001 | learn  | 01-...   | 2026-02-07   | 1             | EXECUTED | xyz   | ... |
```

## Common Liquibase Change Types

### Create Table
```xml
<changeSet id="001-create-table" author="learn">
    <createTable tableName="my_table">
        <column name="id" type="BIGINT" autoIncrement="true">
            <constraints primaryKey="true"/>
        </column>
        <column name="name" type="VARCHAR(100)">
            <constraints nullable="false"/>
        </column>
    </createTable>
</changeSet>
```

### Add Column
```xml
<changeSet id="002-add-column" author="learn">
    <addColumn tableName="my_table">
        <column name="new_field" type="VARCHAR(50)"/>
    </addColumn>
</changeSet>
```

### Drop Column
```xml
<changeSet id="003-drop-column" author="learn">
    <dropColumn tableName="my_table" columnName="old_field"/>
</changeSet>
```

### Create Index
```xml
<changeSet id="004-create-index" author="learn">
    <createIndex tableName="my_table" indexName="idx_name">
        <column name="name"/>
    </createIndex>
</changeSet>
```

### Add Constraint
```xml
<changeSet id="005-add-constraint" author="learn">
    <addForeignKeyConstraint 
        constraintName="fk_my_table_other"
        baseTableName="my_table"
        baseColumnNames="other_id"
        referencedTableName="other_table"
        referencedColumnNames="id"/>
</changeSet>
```

## Adding New Migrations

### Step 1: Create Migration File
```xml
<!-- backend/src/main/resources/db/changelog/v2/02-add-user-table.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                          http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd">

    <changeSet id="006-create-users-table" author="learn">
        <createTable tableName="users">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="username" type="VARCHAR(100)">
                <constraints nullable="false" unique="true"/>
            </column>
            <column name="email" type="VARCHAR(100)">
                <constraints nullable="false" unique="true"/>
            </column>
        </createTable>
    </changeSet>

</databaseChangeLog>
```

### Step 2: Include in Master Changelog
```xml
<!-- backend/src/main/resources/db/changelog/db.changelog-master.xml -->
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                          http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd">

    <include file="db/changelog/v1/01-initial-schema.xml" relativeToChangelogFile="false"/>
    <include file="db/changelog/v2/02-add-user-table.xml" relativeToChangelogFile="false"/>

</databaseChangeLog>
```

### Step 3: Restart Application
- Spring Boot detects the new migration
- Liquibase executes it automatically
- Database schema is updated

## SQLite + JPA Configuration

### In application.properties:
```properties
# SQLite Connection
spring.datasource.url=jdbc:sqlite:data/app.db
spring.datasource.driverClassName=org.sqlite.JDBC

# Hibernate Dialect for SQLite
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=validate
```

### Why `validate` instead of `create-drop`?
- With Liquibase managing schema, we tell Hibernate to **validate** only
- Liquibase handles schema creation and evolution
- This prevents conflicts between Liquibase and Hibernate
- Safer for production

## Viewing SQLite Database

### Using SQLite CLI:
```bash
# Open database
sqlite3 data/app-dev.db

# List all tables
sqlite> .tables

# View schema
sqlite> .schema batches

# Query data
sqlite> SELECT * FROM batches;

# Exit
sqlite> .quit
```

### Using Browser Extension:
- VS Code: SQLite Explorer extension
- Can browse tables graphically within IDE

### Using Java:
```java
// Query database in tests
@DataJpaTest
public class BatchRepositoryTest {
    @Autowired
    private BatchRepository repository;
    
    @Test
    public void testGetBatches() {
        List<BatchEntity> batches = repository.findAll();
        assertThat(batches).isNotEmpty();
    }
}
```

## Troubleshooting

### Issue: Database doesn't update after adding migration

**Solution:**
1. Check file is included in master changelog
2. Verify XML syntax
3. Check Spring profile is correct
4. Review logs for Liquibase errors

### Issue: Migration fails with constraint error

**Solution:**
1. Ensure foreign keys reference existing tables
2. Check table creation order in changelog
3. Drop database and restart if corrupted

### Issue: SQLite file is locked

**Solution:**
```bash
# Force close connections
rm data/app-dev.db

# Restart application - Liquibase recreates schema
```

## Key Differences: SQLite vs H2 vs PostgreSQL

| Feature | SQLite | H2 | PostgreSQL |
|---------|--------|-----|-----------|
| File-based | ✓ | ✓ (optional) | ✗ |
| Zero config | ✓ | ✓ | ✗ |
| Production ready | ✓ (small apps) | ✗ | ✓ |
| Dialect | SQLiteDialect | H2Dialect | PostgreSQLDialect |
| JDBC URL | jdbc:sqlite:data/app.db | jdbc:h2:mem:testdb | jdbc:postgresql://localhost:5432/db |

## Next: Entity Classes

Once database migrations are working, create JPA entities:

```java
@Entity
@Table(name = "batches")
@Data
public class BatchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String batchName;
    
    private String description;
    private String status;
}
```

---

**References:**
- [Liquibase Documentation](https://docs.liquibase.com)
- [SQLite Documentation](https://www.sqlite.org/docs.html)
- [Spring Boot Liquibase](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.sql.liquibase)
