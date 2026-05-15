package com.reactivelab.r2dbc.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.r2dbc.postgresql.codec.Json;
import io.r2dbc.proxy.ProxyConnectionFactory;
import io.r2dbc.proxy.support.QueryExecutionInfoFormatter;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class R2dbcConfiguration {

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
