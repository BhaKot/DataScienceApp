package com.learn.dto;

import com.learn.entity.BatchEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleAggregateDTO {
    private Long id;
    private String sampleId;
    private Long batchId;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BatchEntity batch;
}