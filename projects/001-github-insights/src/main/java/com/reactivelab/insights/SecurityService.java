package com.reactivelab.insights;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;

public class SecurityService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final String dbPath;

    public record SecurityInfo(int vulnerabilities, String lastScan) {}

    public SecurityService(String dbPath) {
        this.dbPath = dbPath;
    }

    public Mono<Integer> getVulnerabilityCount(String repoName) {
        return Mono.fromCallable(() -> {
            File file = new File(dbPath);
            if (!file.exists()) return 0;
            
            Map<String, Map<String, Object>> data = mapper.readValue(file, new TypeReference<>() {});
            Map<String, Object> repoInfo = data.get(repoName);
            
            if (repoInfo == null) return 0;
            return (Integer) repoInfo.get("vulnerabilities");
        })
        .subscribeOn(Schedulers.boundedElastic()) // I/O operation
        .onErrorReturn(0);
    }
}
