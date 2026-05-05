package com.reactivelab.operators;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class ReplenishmentInvestigator {
    @Test
    void investigate() throws Exception {
        try (PrintWriter out = new PrintWriter(new FileWriter("replenishment_results.txt"))) {
            for (int p : new int[]{2, 4, 8, 16, 32}) {
                AtomicInteger req = new AtomicInteger(0);
                Flux<Integer> source = Flux.range(1, 100).doOnRequest(n -> {
                    out.println("  [Source] p=" + p + " Requested: " + n);
                    req.addAndGet((int) n);
                });

                out.println("Testing prefetch=" + p);
                source.flatMap(i -> Mono.just(i).hide(), 1, p)
                    .take(p + 1)
                    .doOnNext(i -> out.println("    Consumed: " + i))
                    .blockLast();
                
                out.println("Total requested for p=" + p + " after consuming " + (p+1) + " items: " + req.get());
                out.println("-----------------------------");
            }
        }
    }
}
