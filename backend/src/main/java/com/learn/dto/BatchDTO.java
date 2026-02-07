package com.learn.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BatchDTO {
    private Long id;
    private String batchName;
    private String description;
    private String status;
    private Integer sampleCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
