package com.learn.mapper;

import com.learn.dao.ResultDAO;
import com.learn.dto.ResultDTO;
import com.learn.entity.ResultEntity;
import com.learn.util.DomainMapper;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RDBMSResultMapper {

    private final Jdbi jdbi;
    private final DomainMapper domainMapper;

    public ResultDTO create(ResultDTO in) {
        ResultEntity inEntity = domainMapper.toEntity(in, ResultEntity.class);
        ResultEntity outEntity = jdbi.withExtension(ResultDAO.class, dao -> dao.createAndGet(inEntity));
        return domainMapper.toDTO(outEntity, ResultDTO.class);
    }

    public ResultDTO findById(Long id) {
        ResultEntity outEntity = jdbi.withExtension(ResultDAO.class, dao -> dao.findById(id));
        return domainMapper.toDTO(outEntity, ResultDTO.class);
    }

    public List<ResultDTO> findByBatchId(Long batchId) {
        return jdbi.withExtension(ResultDAO.class, dao -> dao.findByBatchId(batchId)).stream()
                .map(entity -> domainMapper.toDTO(entity, ResultDTO.class))
                .collect(Collectors.toList());
    }

    public List<ResultDTO> findBySampleId(String sampleId) {
        return jdbi.withExtension(ResultDAO.class, dao -> dao.findBySampleId(sampleId)).stream()
                .map(entity -> domainMapper.toDTO(entity, ResultDTO.class))
                .collect(Collectors.toList());
    }

    public List<ResultDTO> findByStatus(String status) {
        return jdbi.withExtension(ResultDAO.class, dao -> dao.findByStatus(status)).stream()
                .map(entity -> domainMapper.toDTO(entity, ResultDTO.class))
                .collect(Collectors.toList());
    }

    public List<ResultDTO> getAll() {
        List<ResultEntity> outEntities = jdbi.withExtension(ResultDAO.class, ResultDAO::getAll);
        return outEntities.stream()
                .map(entity -> domainMapper.toDTO(entity, ResultDTO.class))
                .collect(Collectors.toList());
    }

    public ResultDTO update(ResultDTO in) {
        ResultEntity inEntity = domainMapper.toEntity(in, ResultEntity.class);
        ResultEntity outEntity = jdbi.withExtension(ResultDAO.class, dao -> dao.updateAndGet(inEntity));
        return domainMapper.toDTO(outEntity, ResultDTO.class);
    }

    public void delete(Long id) {
        jdbi.withExtension(ResultDAO.class, dao -> dao.delete(id));
    }
}
