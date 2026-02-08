package com.learn.mapper;

import com.learn.dao.SampleDAO;
import com.learn.dto.SampleAggregateDTO;
import com.learn.dto.SampleDTO;
import com.learn.entity.SampleEntity;
import com.learn.util.DomainMapper;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

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
        return jdbi.withExtension(SampleDAO.class, dao -> dao.findAggregateBySampleId(sampleId));
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
}
