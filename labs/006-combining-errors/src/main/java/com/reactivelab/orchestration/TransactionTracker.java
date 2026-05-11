package com.reactivelab.orchestration;

import reactor.core.publisher.Flux;

/**
 * Scenario 3: The Running Balance (Accumulation)
 * Uses 'scan' to calculate intermediate state.
 */
public class TransactionTracker {

    /**
     * Calculates the running balance after each transaction.
     *
     * @param initialBalance The starting balance
     * @param transactions   The stream of transaction amounts (positive or negative)
     * @return A Flux emitting the balance after each transaction
     */
    public Flux<Double> calculateRunningBalance(double initialBalance, Flux<Double> transactions) {
        return transactions.scan(initialBalance, (acc, transaction) -> acc + transaction);
    }
}
