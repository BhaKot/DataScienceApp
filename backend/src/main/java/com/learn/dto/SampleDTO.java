package com.learn.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SampleDTO {
    private Long id;
    private String sampleId;
    private Long batchId;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
