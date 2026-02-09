package com.learn.dao;

import com.learn.entity.BatchEntity;
// ...existing code...
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindFields;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@RegisterConstructorMapper(BatchEntity.class)
public interface BatchDAO {

    String SELECT_ALL = "SELECT * FROM batches WHERE 1=1 ";
    String SELECT_BY_ID = SELECT_ALL + "AND id=:id";
    String SELECT_BY_NAME = SELECT_ALL + "AND batch_name=:batchName";
    String SELECT_BY_STATUS = SELECT_ALL + "AND status=:status";

    String INSERT = """
                INSERT INTO batches (
                    batch_name, description, status, sample_count, created_at, updated_at
                )
                VALUES (
                    :batchName, :description, :status, :sampleCount,
                    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                )
                """;

    String UPDATE = """
        UPDATE batches
        SET batch_name=:batchName,
            description=:description,
            status=:status,
            sample_count=:sampleCount,
            updated_at=CURRENT_TIMESTAMP
        WHERE id=:id
        """;

    String DELETE = "DELETE FROM batches WHERE id=:id";

    @SqlQuery(SELECT_ALL)
    List<BatchEntity> getAll();

    @SqlQuery(SELECT_BY_ID)
    BatchEntity findById(@Bind("id") Long id);

    @SqlQuery(SELECT_BY_NAME)
    BatchEntity findByBatchName(@Bind("batchName") String batchName);

    @SqlQuery(SELECT_BY_STATUS)
    List<BatchEntity> findByStatus(@Bind("status") String status);

    // Write path: use flat write entity
    @SqlUpdate(INSERT)
    void insert(@BindBean BatchEntity batch);

    @SqlQuery("SELECT last_insert_rowid()")
    long getLastInsertId();

    @SqlUpdate(UPDATE)
    int update(@BindBean BatchEntity batch);

    @SqlUpdate(DELETE)
    int delete(@Bind("id") Long id);

    default BatchEntity createAndGet(BatchEntity batch) {
        insert(batch);
        long id = getLastInsertId();
        return findById(id);
    }

    default BatchEntity updateAndGet(BatchEntity batch) {
        update(batch);
        return findById(batch.getId());
    }
}
