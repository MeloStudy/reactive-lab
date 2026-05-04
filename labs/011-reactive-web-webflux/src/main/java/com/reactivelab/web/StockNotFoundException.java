package com.reactivelab.web;

public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(String symbol) {
        super("Stock not found: " + symbol);
    }
}
