package com.reactivelab.reactor.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.util.retry.Retry;

import java.time.Duration;

public class ReactiveConsumer {
    private final KafkaReceiver<String, String> receiver;
    private final KafkaSender<String, String> dlqSender;
    private final String dlqTopic;

    public ReactiveConsumer(KafkaReceiver<String, String> receiver, KafkaSender<String, String> dlqSender, String dlqTopic) {
        this.receiver = receiver;
        this.dlqSender = dlqSender;
        this.dlqTopic = dlqTopic;
    }

    /**
     * Consumes messages from a Kafka topic and acknowledges offsets manually.
     * Includes Error Handling: Retries failures, and routes to a Dead Letter Queue (DLQ) if retries are exhausted.
     * @return Flux of successfully processed string values
     */
    public Flux<String> consumeMessages() {
        return receiver.receive()
                .concatMap(record -> 
                    processRecord(record.value())
                        // Retry up to 3 times with exponential backoff
                        .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                        // If it still fails, send to DLQ and acknowledge original
                        .onErrorResume(e -> sendToDlq(record).then(Mono.empty()))
                        .doFinally(signalType -> record.receiverOffset().acknowledge())
                );
    }

    private Mono<String> processRecord(String value) {
        if (value.contains("FAIL")) {
            return Mono.error(new RuntimeException("Simulated processing failure for: " + value));
        }
        return Mono.just(value);
    }

    private Mono<Void> sendToDlq(org.apache.kafka.clients.consumer.ReceiverRecord<String, String> record) {
        SenderRecord<String, String, String> senderRecord = SenderRecord.create(
                new ProducerRecord<>(dlqTopic, record.key(), record.value() + "-DLQ"),
                record.key()
        );
        return dlqSender.send(Mono.just(senderRecord)).then();
    }

    /**
     * Scenario 4: Demonstrates strict backpressure and prefetching.
     * The limitRate() operator dictates how many items are fetched before requesting more.
     * delayElements simulates a slow consumer to force Reactor Kafka to pause the internal KafkaConsumer.
     */
    public Flux<String> consumeWithStrictBackpressure(int prefetch) {
        return receiver.receive()
                .limitRate(prefetch)
                .delayElements(Duration.ofMillis(200))
                .doOnNext(record -> record.receiverOffset().acknowledge())
                .map(ConsumerRecord::value);
    }
}
