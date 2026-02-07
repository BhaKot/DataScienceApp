package com.learn.mapper;

import com.learn.dao.BatchDAO;
import com.learn.dto.BatchDTO;
import com.learn.entity.BatchEntity;
import com.learn.util.DomainMapper;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RDBMSBatchMapper {

    private final Jdbi jdbi;
    private final DomainMapper domainMapper;

    public BatchDTO create(BatchDTO in) {
        BatchEntity inEntity = domainMapper.toEntity(in, BatchEntity.class);
        BatchEntity outEntity = jdbi.withExtension(BatchDAO.class, dao -> dao.createAndGet(inEntity));
        return domainMapper.toDTO(outEntity, BatchDTO.class);
    }

    public BatchDTO findById(Long id) {
        BatchEntity outEntity = jdbi.withExtension(BatchDAO.class, dao -> dao.findById(id));
        return domainMapper.toDTO(outEntity, BatchDTO.class);
    }

    public BatchDTO findByBatchName(String batchName) {
        BatchEntity outEntity = jdbi.withExtension(BatchDAO.class, dao -> dao.findByBatchName(batchName));
        return domainMapper.toDTO(outEntity, BatchDTO.class);
    }

    public List<BatchDTO> findByStatus(String status) {
        return jdbi.withExtension(BatchDAO.class, dao -> dao.findByStatus(status)).stream()
                .map(entity -> domainMapper.toDTO(entity, BatchDTO.class))
                .collect(Collectors.toList());
    }

    public List<BatchDTO> getAll() {
        List<BatchEntity> outEntities = jdbi.withExtension(BatchDAO.class, BatchDAO::getAll);
        return outEntities.stream()
                .map(entity -> domainMapper.toDTO(entity, BatchDTO.class))
                .collect(Collectors.toList());
    }

    public BatchDTO update(BatchDTO in) {
        BatchEntity inEntity = domainMapper.toEntity(in, BatchEntity.class);
        BatchEntity outEntity = jdbi.withExtension(BatchDAO.class, dao -> dao.updateAndGet(inEntity));
        return domainMapper.toDTO(outEntity, BatchDTO.class);
    }

    public void delete(Long id) {
        jdbi.withExtension(BatchDAO.class, dao -> dao.delete(id));
    }
}
