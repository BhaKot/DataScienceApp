package com.learn.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResultDTO {
    private Long id;
    private Long batchId;
    private String sampleId;
    private Double ctValue;
    private String dye;
    private String status;
    private Double score;
    private LocalDateTime createdAt;
}
