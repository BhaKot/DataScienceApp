package com.learn.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor(onConstructor_ = @JdbiConstructor)
public class ResultEntity {

    private Long id;

    private Long batchId;

    private String sampleId;

    private Double ctValue;

    private String dye;

    private String status;

    private Double score;

    private LocalDateTime createdAt;
}
