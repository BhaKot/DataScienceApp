package com.learn.mapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.learn.dto.BatchDTO;
import com.learn.dto.ResultDTO;
import com.learn.dto.SampleDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;

@ExtendWith(SpringExtension.class)
@org.springframework.test.context.ContextConfiguration(classes = MapperTestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = {
    "spring.liquibase.enabled=true",
    "spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml",
    "spring.datasource.url=jdbc:sqlite:file:testdb?mode=memory&cache=shared",
    "spring.datasource.driver-class-name=org.sqlite.JDBC",
    "spring.datasource.type=org.sqlite.SQLiteDataSource"
})
public class MapperTest {

    @Autowired
    private RDBMSBatchMapper batchMapper;

    @Autowired
    private RDBMSSampleMapper sampleMapper;

    @Autowired
    private RDBMSResultMapper resultMapper;

    @Autowired
    private DataSource dataSource;

    @BeforeAll
    public void migrate() throws Exception {
        liquibase.integration.spring.SpringLiquibase liquibase = new liquibase.integration.spring.SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml");
        liquibase.setShouldRun(true);
        liquibase.afterPropertiesSet();

           // Clean up tables to avoid old/bad data (especially for timestamp columns)
           try (var conn = dataSource.getConnection(); var stmt = conn.createStatement()) {
              stmt.executeUpdate("DELETE FROM results");
              stmt.executeUpdate("DELETE FROM samples");
              stmt.executeUpdate("DELETE FROM batches");
           }
    }

    private BatchDTO testBatch;
    private SampleDTO testSample;

    @BeforeEach
    public void setUp() {
        testBatch = new BatchDTO();
        testBatch.setBatchName("TEST-BATCH-" + System.currentTimeMillis());
        testBatch.setDescription("Test batch for JDBI mapper testing");
        testBatch.setStatus("CREATED");
        testBatch.setSampleCount(0);
        testBatch.setCreatedAt(LocalDateTime.now());
        testBatch.setUpdatedAt(LocalDateTime.now());
        testBatch = batchMapper.create(testBatch);

        testSample = new SampleDTO();
        testSample.setSampleId("TEST-SAMPLE-" + System.currentTimeMillis());
        testSample.setBatchId(testBatch.getId());
        testSample.setDescription("Test sample");
        testSample.setStatus("PENDING");
        testSample.setCreatedAt(LocalDateTime.now());
        testSample.setUpdatedAt(LocalDateTime.now());
        testSample = sampleMapper.create(testSample);
    }

    @AfterEach
    public void tearDown() {
        if (testSample != null && testSample.getId() != null) {
            sampleMapper.delete(testSample.getId());
        }
        if (testBatch != null && testBatch.getId() != null) {
            batchMapper.delete(testBatch.getId());
        }
    }

    @Test
    public void testBatchCreate() {
        assertThat(testBatch.getId()).isNotNull();
        assertThat(testBatch.getBatchName()).startsWith("TEST-BATCH-");
    }

    @Test
    public void testSampleFindBySampleId() {
        SampleDTO found = sampleMapper.findBySampleId(testSample.getSampleId());
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(testSample.getId());
    }

    @Test
    public void testResultCreate() {
        ResultDTO result = new ResultDTO();
        result.setBatchId(testBatch.getId());
        result.setSampleId(testSample.getSampleId());
        result.setStatus("PROCESSED");
        result.setCreatedAt(LocalDateTime.now());
        result = resultMapper.create(result);
        assertThat(result.getId()).isNotNull();
        resultMapper.delete(result.getId());
    }
}
