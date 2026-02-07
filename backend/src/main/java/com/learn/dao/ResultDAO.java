package com.learn.dao;

import com.learn.entity.ResultEntity;
// ...existing code...
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@RegisterConstructorMapper(ResultEntity.class)
public interface ResultDAO {

    String SELECT_ALL = "SELECT * FROM results WHERE 1=1 ";
    String SELECT_BY_ID = SELECT_ALL + "AND id=:id";
    String SELECT_BY_BATCH_ID = SELECT_ALL + "AND batch_id=:batchId";
    String SELECT_BY_SAMPLE_ID = SELECT_ALL + "AND sample_id=:sampleId";
    String SELECT_BY_STATUS = SELECT_ALL + "AND status=:status";
    
        String INSERT = "INSERT INTO results (batch_id, sample_id, ct_value, dye, status, score, created_at) " +
            "VALUES (:batchId, :sampleId, :ctValue, :dye, :status, :score, :createdAt)";
    
    String UPDATE = "UPDATE results SET batch_id=:batchId, sample_id=:sampleId, ct_value=:ctValue, dye=:dye, " +
            "status=:status, score=:score WHERE id=:id";
    
    String DELETE = "DELETE FROM results WHERE id=:id";

    @SqlQuery(SELECT_ALL)
    List<ResultEntity> getAll();

    @SqlQuery(SELECT_BY_ID)
    ResultEntity findById(@Bind("id") Long id);

    @SqlQuery(SELECT_BY_BATCH_ID)
    List<ResultEntity> findByBatchId(@Bind("batchId") Long batchId);

    @SqlQuery(SELECT_BY_SAMPLE_ID)
    List<ResultEntity> findBySampleId(@Bind("sampleId") String sampleId);

    @SqlQuery(SELECT_BY_STATUS)
    List<ResultEntity> findByStatus(@Bind("status") String status);

    @SqlUpdate(INSERT)
    void insert(@BindBean ResultEntity result);

    @SqlQuery("SELECT last_insert_rowid()")
    long getLastInsertId();

    @SqlUpdate(UPDATE)
    int update(@BindBean ResultEntity result);

    @SqlUpdate(DELETE)
    int delete(@Bind("id") Long id);

    default ResultEntity createAndGet(ResultEntity result) {
        insert(result);
        long id = getLastInsertId();
        return findById(id);
    }

    default ResultEntity updateAndGet(ResultEntity result) {
        update(result);
        return findById(result.getId());
    }
}
