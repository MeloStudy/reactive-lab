package com.reactivelab.resilience.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Weather {
    private String city;
    private String temp;
    private String condition;
}
