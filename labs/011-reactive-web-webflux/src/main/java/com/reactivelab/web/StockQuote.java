package com.reactivelab.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockQuote {
    private String symbol;
    private double price;
    private Instant timestamp;
}
