package com.reactivelab.r2dbc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.r2dbc.postgresql.codec.Json;
import io.r2dbc.proxy.ProxyConnectionFactory;
import io.r2dbc.proxy.support.QueryExecutionInfoFormatter;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
class ProductService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final DatabaseClient databaseClient;

    @Transactional
    public Mono<Order> purchaseProduct(Long productId, Integer quantity) {
        log.info("Starting purchase for product: {} quantity: {}", productId, quantity);
        
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found")))
                .flatMap(product -> {
                    if (product.getStock() < quantity) {
                        return Mono.error(new RuntimeException("Insufficient stock"));
                    }
                    
                    product.setStock(product.getStock() - quantity);
                    
                    return productRepository.save(product)
                            .then(orderRepository.save(Order.builder()
                                    .productId(productId)
                                    .quantity(quantity)
                                    .totalAmount(product.getPrice() * quantity)
                                    .build()));
                })
                .doOnSuccess(order -> log.info("Purchase completed: {}", order.getId()))
                .doOnError(err -> log.error("Purchase failed, rolling back: {}", err.getMessage()));
    }

    public Flux<Product> findExpensiveProducts(Double minPrice) {
        return databaseClient.sql("SELECT * FROM products WHERE price > :minPrice")
                .bind("minPrice", minPrice)
                .map((row, metadata) -> Product.builder()
                        .id(row.get("id", Long.class))
                        .name(row.get("name", String.class))
                        .price(row.get("price", Double.class))
                        .stock(row.get("stock", Integer.class))
                        .build())
                .all();
    }
}

@Configuration
@Slf4j
class R2dbcConfiguration {

    @Bean
    public BeanPostProcessor connectionFactoryBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof ConnectionFactory && !(bean instanceof ProxyConnectionFactory)) {
                    log.info("Proxying ConnectionFactory: {}", beanName);
                    return ProxyConnectionFactory.builder((ConnectionFactory) bean)
                            .onAfterQuery(queryInfo -> {
                                String formattedQuery = QueryExecutionInfoFormatter.showAll().format(queryInfo);
                                log.info("R2DBC Proxy -> {}", formattedQuery);
                            })
                            .build();
                }
                return bean;
            }
        };
    }

    @Bean
    public SimpleModule r2dbcJsonModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Json.class, new JsonSerializer<Json>() {
            @Override
            public void serialize(Json value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeRawValue(value.asString());
            }
        });
        module.addDeserializer(Json.class, new JsonDeserializer<Json>() {
            @Override
            public Json deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                JsonNode node = p.getCodec().readTree(p);
                return Json.of(node.toString());
            }
        });
        return module;
    }
}
