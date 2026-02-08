package com.learn.mapper;

import com.learn.dto.BatchDTO;
import com.learn.dto.ResultDTO;
import com.learn.dto.SampleAggregateDTO;
import com.learn.dto.SampleDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

    private BatchDTO testBatch;
    private SampleDTO testSample;

    @BeforeAll
    public void migrate() throws Exception {
        liquibase.integration.spring.SpringLiquibase liquibase = new liquibase.integration.spring.SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml");
        liquibase.setShouldRun(true);
        liquibase.afterPropertiesSet();

        try (var conn = dataSource.getConnection(); var stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM results");
            stmt.executeUpdate("DELETE FROM samples");
            stmt.executeUpdate("DELETE FROM batches");
        }
    }

    @BeforeEach
    public void setUp() {
        testBatch = new BatchDTO();
        testBatch.setBatchName("TEST-BATCH-" + System.currentTimeMillis());
        testBatch.setDescription("Test batch for mapper testing");
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
    public void testBatchOperations() {
        BatchDTO byId = batchMapper.findById(testBatch.getId());
        assertThat(byId).isNotNull();
        assertThat(byId.getBatchName()).isEqualTo(testBatch.getBatchName());

        BatchDTO byName = batchMapper.findByBatchName(testBatch.getBatchName());
        assertThat(byName).isNotNull();
        assertThat(byName.getId()).isEqualTo(testBatch.getId());

        List<BatchDTO> byStatus = batchMapper.findByStatus("CREATED");
        assertThat(byStatus).isNotEmpty();
        assertThat(byStatus).extracting(BatchDTO::getId).contains(testBatch.getId());

        List<BatchDTO> all = batchMapper.getAll();
        assertThat(all).isNotEmpty();
        assertThat(all).extracting(BatchDTO::getId).contains(testBatch.getId());

        testBatch.setDescription("Updated batch description");
        testBatch.setStatus("RUNNING");
        testBatch.setSampleCount(1);
        BatchDTO updated = batchMapper.update(testBatch);

        assertThat(updated.getDescription()).isEqualTo("Updated batch description");
        assertThat(updated.getStatus()).isEqualTo("RUNNING");
        assertThat(updated.getSampleCount()).isEqualTo(1);

        BatchDTO deleteCandidate = new BatchDTO();
        deleteCandidate.setBatchName("DELETE-BATCH-" + System.nanoTime());
        deleteCandidate.setDescription("delete me");
        deleteCandidate.setStatus("CREATED");
        deleteCandidate.setSampleCount(0);
        deleteCandidate.setCreatedAt(LocalDateTime.now());
        deleteCandidate.setUpdatedAt(LocalDateTime.now());
        deleteCandidate = batchMapper.create(deleteCandidate);

        batchMapper.delete(deleteCandidate.getId());
        assertThat(batchMapper.findById(deleteCandidate.getId())).isNull();
    }

    @Test
    public void testSampleOperations() {
        SampleDTO byId = sampleMapper.findById(testSample.getId());
        assertThat(byId).isNotNull();
        assertThat(byId.getSampleId()).isEqualTo(testSample.getSampleId());

        SampleDTO bySampleId = sampleMapper.findBySampleId(testSample.getSampleId());
        assertThat(bySampleId).isNotNull();
        assertThat(bySampleId.getId()).isEqualTo(testSample.getId());

        List<SampleDTO> byBatch = sampleMapper.findByBatchId(testBatch.getId());
        assertThat(byBatch).isNotEmpty();
        assertThat(byBatch).extracting(SampleDTO::getId).contains(testSample.getId());

        List<SampleDTO> all = sampleMapper.getAll();
        assertThat(all).isNotEmpty();
        assertThat(all).extracting(SampleDTO::getId).contains(testSample.getId());

        testSample.setDescription("Updated sample description");
        testSample.setStatus("PROCESSED");
        SampleDTO updated = sampleMapper.update(testSample);

        assertThat(updated.getDescription()).isEqualTo("Updated sample description");
        assertThat(updated.getStatus()).isEqualTo("PROCESSED");

        SampleDTO deleteCandidate = new SampleDTO();
        deleteCandidate.setSampleId("DELETE-SAMPLE-" + System.nanoTime());
        deleteCandidate.setBatchId(testBatch.getId());
        deleteCandidate.setDescription("delete me");
        deleteCandidate.setStatus("PENDING");
        deleteCandidate.setCreatedAt(LocalDateTime.now());
        deleteCandidate.setUpdatedAt(LocalDateTime.now());
        deleteCandidate = sampleMapper.create(deleteCandidate);

        sampleMapper.delete(deleteCandidate.getId());
        assertThat(sampleMapper.findById(deleteCandidate.getId())).isNull();
    }

    @Test
    public void testResultOperations() {
        ResultDTO result = new ResultDTO();
        result.setBatchId(testBatch.getId());
        result.setSampleId(testSample.getSampleId());
        result.setCtValue(31.5);
        result.setDye("FAM");
        result.setStatus("PROCESSED");
        result.setScore(0.98);
        result.setCreatedAt(LocalDateTime.now());

        result = resultMapper.create(result);
        assertThat(result.getId()).isNotNull();

        try {
            ResultDTO byId = resultMapper.findById(result.getId());
            assertThat(byId).isNotNull();
            assertThat(byId.getSampleId()).isEqualTo(testSample.getSampleId());

            List<ResultDTO> byBatch = resultMapper.findByBatchId(testBatch.getId());
            assertThat(byBatch).extracting(ResultDTO::getId).contains(result.getId());

            List<ResultDTO> bySample = resultMapper.findBySampleId(testSample.getSampleId());
            assertThat(bySample).extracting(ResultDTO::getId).contains(result.getId());

            List<ResultDTO> byStatus = resultMapper.findByStatus("PROCESSED");
            assertThat(byStatus).extracting(ResultDTO::getId).contains(result.getId());

            List<ResultDTO> all = resultMapper.getAll();
            assertThat(all).extracting(ResultDTO::getId).contains(result.getId());

            result.setCtValue(29.2);
            result.setDye("HEX");
            result.setStatus("REVIEWED");
            result.setScore(0.88);
            ResultDTO updated = resultMapper.update(result);

            assertThat(updated.getCtValue()).isEqualTo(29.2);
            assertThat(updated.getDye()).isEqualTo("HEX");
            assertThat(updated.getStatus()).isEqualTo("REVIEWED");
            assertThat(updated.getScore()).isEqualTo(0.88);

            resultMapper.delete(result.getId());
            assertThat(resultMapper.findById(result.getId())).isNull();
        } finally {
            ResultDTO maybeExisting = resultMapper.findById(result.getId());
            if (maybeExisting != null) {
                resultMapper.delete(result.getId());
            }
        }
    }

    @Test
    public void testSampleAggregateJoinIncludesBatch() {
        SampleAggregateDTO aggregate = sampleMapper.findAggregateBySampleId(testSample.getSampleId());

        assertThat(aggregate).isNotNull();
        assertThat(aggregate.getId()).isEqualTo(testSample.getId());
        assertThat(aggregate.getSampleId()).isEqualTo(testSample.getSampleId());
        assertThat(aggregate.getBatchId()).isEqualTo(testBatch.getId());

        assertThat(aggregate.getBatch()).isNotNull();
        assertThat(aggregate.getBatch().getId()).isEqualTo(testBatch.getId());
        assertThat(aggregate.getBatch().getBatchName()).isEqualTo(testBatch.getBatchName());
        assertThat(aggregate.getBatch().getStatus()).isEqualTo(testBatch.getStatus());
    }
}
