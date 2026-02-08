package com.learn.dao;

import com.learn.dto.SampleAggregateDTO;
import com.learn.entity.BatchEntity;
import com.learn.entity.SampleEntity;
// ...existing code...
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMappers;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindFields;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@RegisterConstructorMapper(SampleEntity.class)
public interface SampleDAO {

    String SELECT_ALL = "SELECT * FROM samples WHERE 1=1 ";
    String SELECT_BY_ID = SELECT_ALL + "AND id=:id";
    String SELECT_BY_SAMPLE_ID = SELECT_ALL + "AND sample_id=:sampleId";
    String SELECT_BY_BATCH_ID = SELECT_ALL + "AND batch_id=:batchId";

    String SELECT_AGGREGATE_BY_SAMPLE_ID = """
        SELECT
            s.id,
            s.sample_id,
            s.batch_id,
            s.description,
            s.status,
            s.created_at,
            s.updated_at,
            b.id AS batchEntity_id,
            b.batch_name AS batchEntity_batch_name,
            b.description AS batchEntity_description,
            b.status AS batchEntity_status,
            b.sample_count AS batchEntity_sample_count,
            b.created_at AS batchEntity_created_at,
            b.updated_at AS batchEntity_updated_at
        FROM samples s
        JOIN batches b ON b.id = s.batch_id
        WHERE s.sample_id = :sampleId
        """;

    String INSERT = """
                INSERT INTO samples (
                    sample_id, batch_id, description, status, created_at, updated_at
                )
                VALUES (
                    :sampleId, :batchId, :description, :status,
                    :createdAt, :updatedAt
                )
                """;

    String UPDATE = """
        UPDATE samples
        SET sample_id=:sampleId,
            batch_id=:batchId,
            description=:description,
            status=:status,
            updated_at=CURRENT_TIMESTAMP
        WHERE id=:id
        """;

    String DELETE = "DELETE FROM samples WHERE id=:id";

    @SqlQuery(SELECT_ALL)
    List<SampleEntity> getAll();

    @SqlQuery(SELECT_BY_ID)
    SampleEntity findById(@Bind("id") Long id);

    @SqlQuery(SELECT_BY_SAMPLE_ID)
    SampleEntity findBySampleId(@Bind("sampleId") String sampleId);

    @SqlQuery(SELECT_BY_BATCH_ID)
    List<SampleEntity> findByBatchId(@Bind("batchId") Long batchId);

    @RegisterConstructorMappers({
        @RegisterConstructorMapper(SampleAggregateDTO.class),
        @RegisterConstructorMapper(value = BatchEntity.class, prefix = "batchEntity")
    })
    @SqlQuery(SELECT_AGGREGATE_BY_SAMPLE_ID)
    SampleAggregateDTO findAggregateBySampleId(@Bind("sampleId") String sampleId);

    // Write path: use flat write entity
    @SqlUpdate(INSERT)
    void insert(@BindBean SampleEntity sample);

    @SqlQuery("SELECT last_insert_rowid()")
    long getLastInsertId();

    @SqlUpdate(UPDATE)
    int update(@BindBean SampleEntity sample);

    @SqlUpdate(DELETE)
    int delete(@Bind("id") Long id);

    default SampleEntity createAndGet(SampleEntity sample) {
        insert(sample);
        long id = getLastInsertId();
        return findById(id);
    }

    default SampleEntity updateAndGet(SampleEntity sample) {
        update(sample);
        return findById(sample.getId());
    }
}
