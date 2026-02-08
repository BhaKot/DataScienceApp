package com.learn.dto;

import com.learn.entity.BatchEntity;
import com.learn.entity.SampleEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jdbi.v3.core.mapper.Nested;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

@Data
@NoArgsConstructor
public class SampleBatchAggregateDTO {
    private SampleEntity sample;
    private BatchEntity batch;

    @JdbiConstructor
    public SampleBatchAggregateDTO(@Nested("sample") SampleEntity sample,
                                   @Nested("batch") BatchEntity batch) {
        this.sample = sample;
        this.batch = batch;
    }
}
