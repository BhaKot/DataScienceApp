package com.learn.mapper;

import com.learn.dao.SampleDAO;
import com.learn.dto.SampleAggregateDTO;
import com.learn.dto.SampleDTO;
import com.learn.entity.BatchEntity;
import com.learn.entity.SampleEntity;
import com.learn.util.DomainMapper;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RDBMSSampleMapper {

    private final Jdbi jdbi;
    private final DomainMapper domainMapper;

    public SampleDTO create(SampleDTO in) {
        SampleEntity inEntity = domainMapper.toEntity(in, SampleEntity.class);
        SampleEntity outEntity = jdbi.withExtension(SampleDAO.class, dao -> dao.createAndGet(inEntity));
        return domainMapper.toDTO(outEntity, SampleDTO.class);
    }

    public SampleDTO findById(Long id) {
        SampleEntity outEntity = jdbi.withExtension(SampleDAO.class, dao -> dao.findById(id));
        return domainMapper.toDTO(outEntity, SampleDTO.class);
    }

    public SampleDTO findBySampleId(String sampleId) {
        SampleEntity outEntity = jdbi.withExtension(SampleDAO.class, dao -> dao.findBySampleId(sampleId));
        return domainMapper.toDTO(outEntity, SampleDTO.class);
    }

    public List<SampleDTO> findByBatchId(Long batchId) {
        return jdbi.withExtension(SampleDAO.class, dao -> dao.findByBatchId(batchId)).stream()
                .map(entity -> domainMapper.toDTO(entity, SampleDTO.class))
                .collect(Collectors.toList());
    }


    public SampleAggregateDTO findAggregateBySampleId(String sampleId) {
        return jdbi.withHandle(handle -> handle.createQuery("""
                SELECT
                    s.id AS sample_id_pk,
                    s.sample_id AS sample_id,
                    s.batch_id AS sample_batch_id,
                    s.description AS sample_description,
                    s.status AS sample_status,
                    s.created_at AS sample_created_at,
                    s.updated_at AS sample_updated_at,
                    b.id AS batch_id_pk,
                    b.batch_name AS batch_name,
                    b.description AS batch_description,
                    b.status AS batch_status,
                    b.sample_count AS batch_sample_count,
                    b.created_at AS batch_created_at,
                    b.updated_at AS batch_updated_at
                FROM samples s
                JOIN batches b ON b.id = s.batch_id
                WHERE s.sample_id = :sampleId
                """)
                .bind("sampleId", sampleId)
                .map((rs, ctx) -> {
                    SampleAggregateDTO dto = new SampleAggregateDTO();
                    dto.setId(rs.getLong("sample_id_pk"));
                    dto.setSampleId(rs.getString("sample_id"));
                    dto.setBatchId(rs.getLong("sample_batch_id"));
                    dto.setDescription(rs.getString("sample_description"));
                    dto.setStatus(rs.getString("sample_status"));
                    dto.setCreatedAt(parseLocalDateTime(rs.getString("sample_created_at")));
                    dto.setUpdatedAt(parseLocalDateTime(rs.getString("sample_updated_at")));

                    BatchEntity batch = new BatchEntity();
                    batch.setId(rs.getLong("batch_id_pk"));
                    batch.setBatchName(rs.getString("batch_name"));
                    batch.setDescription(rs.getString("batch_description"));
                    batch.setStatus(rs.getString("batch_status"));
                    batch.setSampleCount(rs.getInt("batch_sample_count"));
                    batch.setCreatedAt(parseLocalDateTime(rs.getString("batch_created_at")));
                    batch.setUpdatedAt(parseLocalDateTime(rs.getString("batch_updated_at")));
                    dto.setBatch(batch);

                    return dto;
                })
                .findOne()
                .orElse(null));
    }

    public List<SampleDTO> getAll() {
        List<SampleEntity> outEntities = jdbi.withExtension(SampleDAO.class, SampleDAO::getAll);
        return outEntities.stream()
                .map(entity -> domainMapper.toDTO(entity, SampleDTO.class))
                .collect(Collectors.toList());
    }

    public SampleDTO update(SampleDTO in) {
        SampleEntity inEntity = domainMapper.toEntity(in, SampleEntity.class);
        SampleEntity outEntity = jdbi.withExtension(SampleDAO.class, dao -> dao.updateAndGet(inEntity));
        return domainMapper.toDTO(outEntity, SampleDTO.class);
    }

    public void delete(Long id) {
        jdbi.withExtension(SampleDAO.class, dao -> dao.delete(id));
    }

    private LocalDateTime parseLocalDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
}
