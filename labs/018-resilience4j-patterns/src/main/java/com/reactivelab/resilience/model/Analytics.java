package com.reactivelab.resilience.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Analytics {
    private String id;
    private long requestCount;
    private String status;
}
