package com.reactivelab.reactor.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

public class ReactiveProducer {
    private final KafkaSender<String, String> sender;

    public ReactiveProducer(KafkaSender<String, String> sender) {
        this.sender = sender;
    }

    /**
     * Publishes a stream of messages to a Kafka topic in a fully non-blocking manner.
     * @param topic Target Kafka topic
     * @param messages Stream of String messages
     * @return Flux of SenderResult confirming successful production
     */
    public Flux<SenderResult<Integer>> sendMessages(String topic, Flux<String> messages) {
        Flux<SenderRecord<String, String, Integer>> records = messages.index()
                .map(tuple -> SenderRecord.create(
                        new ProducerRecord<>(topic, "key-" + tuple.getT1(), tuple.getT2()),
                        tuple.getT1().intValue()
                ));
        return sender.send(records);
    }
}
