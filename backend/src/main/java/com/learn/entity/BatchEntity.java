package com.learn.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor(onConstructor_ = @JdbiConstructor)
public class BatchEntity {

    private Long id;

    private String batchName;

    private String description;

    private String status;

    private Integer sampleCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
