package com.reactivelab.reactor.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class KafkaTest {

    private static KafkaContainer kafkaContainer;
    private static KafkaSender<String, String> sender;
    private static KafkaReceiver<String, String> receiver;
    private static KafkaReceiver<String, String> dlqReceiver;

    private static final String TOPIC = "test-topic";
    private static final String DLQ_TOPIC = "test-topic-dlq";

    @BeforeAll
    static void setUp() {
        // Start Kafka Testcontainer using Apache Kafka KRaft mode
        kafkaContainer = new KafkaContainer(DockerImageName.parse("apache/kafka:3.7.0"));
        kafkaContainer.withEnv("KAFKA_LISTENERS", "PLAINTEXT://0.0.0.0:9092,BROKER://0.0.0.0:9093,CONTROLLER://0.0.0.0:9094");
        kafkaContainer.start();

        // Configure Producer
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        SenderOptions<String, String> senderOptions = SenderOptions.create(producerProps);
        sender = KafkaSender.create(senderOptions);

        // Configure Consumer
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        consumerProps.put(ConsumerConfig.CLIENT_ID_CONFIG, "reactive-consumer");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "reactive-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        ReceiverOptions<String, String> receiverOptions = ReceiverOptions.<String, String>create(consumerProps)
                .subscription(Collections.singleton(TOPIC))
                .commitBatchSize(2) // Enable Micro-batching for offset commits
                .commitInterval(Duration.ofMillis(500));
        receiver = KafkaReceiver.create(receiverOptions);

        // Configure DLQ Consumer
        Map<String, Object> dlqConsumerProps = new HashMap<>(consumerProps);
        dlqConsumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "dlq-group");
        ReceiverOptions<String, String> dlqReceiverOptions = ReceiverOptions.<String, String>create(dlqConsumerProps)
                .subscription(Collections.singleton(DLQ_TOPIC));
        dlqReceiver = KafkaReceiver.create(dlqReceiverOptions);
    }

    @AfterAll
    static void tearDown() {
        if (sender != null) sender.close();
        if (kafkaContainer != null) kafkaContainer.stop();
    }

    @Test
    @Order(1)
    void testScenario1_NonBlockingMessageProduction() {
        ReactiveProducer producer = new ReactiveProducer(sender);
        // "FAIL" event is meant to test the DLQ in scenario 2
        Flux<String> messages = Flux.just("Event 1", "Event 2", "FAIL Event", "Event 3");

        StepVerifier.create(producer.sendMessages(TOPIC, messages))
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    @Order(2)
    void testScenario2and3_ConsumerBatchingAndDLQ() {
        ReactiveConsumer consumer = new ReactiveConsumer(receiver, sender, DLQ_TOPIC);

        // The consumer should successfully process "Event 1", "Event 2", and "Event 3"
        // "FAIL Event" should be retried and eventually sent to DLQ without failing the Flux
        StepVerifier.create(consumer.consumeMessages())
                .expectNext("Event 1", "Event 2", "Event 3")
                .thenCancel()
                .verify(Duration.ofSeconds(15));
        
        // Verify DLQ received the failed message
        StepVerifier.create(dlqReceiver.receive().map(r -> r.value()))
                .expectNext("FAIL Event-DLQ")
                .thenCancel()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    @Order(3)
    void testScenario4_PrefetchAndBackpressure() {
        // We will produce a few more events, and test that the consumer 
        // processes them with the strict backpressure and delay setup.
        ReactiveProducer producer = new ReactiveProducer(sender);
        Flux<String> messages = Flux.just("Slow 1", "Slow 2", "Slow 3");
        producer.sendMessages(TOPIC, messages).blockLast();

        ReactiveConsumer consumer = new ReactiveConsumer(receiver, sender, DLQ_TOPIC);
        
        StepVerifier.withVirtualTime(() -> consumer.consumeWithStrictBackpressure(1).take(3))
                .thenAwait(Duration.ofSeconds(1)) // Accounts for 3 items * 200ms delay + buffer
                .expectNext("Slow 1", "Slow 2", "Slow 3")
                .verifyComplete();
    }
}
