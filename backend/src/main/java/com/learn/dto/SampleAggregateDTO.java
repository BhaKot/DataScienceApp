package com.learn.dto;

import com.learn.entity.BatchEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jdbi.v3.core.mapper.Nested;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SampleAggregateDTO {
    private Long id;
    private String sampleId;
    private Long batchId;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BatchEntity batch;

    @JdbiConstructor
    public SampleAggregateDTO(Long id,
                              String sampleId,
                              Long batchId,
                              String description,
                              String status,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt,
                              @Nested("batchEntity") BatchEntity batch) {
        this.id = id;
        this.sampleId = sampleId;
        this.batchId = batchId;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.batch = batch;
    }
}
