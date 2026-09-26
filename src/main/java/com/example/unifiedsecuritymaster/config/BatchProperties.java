package com.example.unifiedsecuritymaster.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Data
@Component
@ConfigurationProperties(prefix = "securitymaster.batch")
public class BatchProperties {


    private int chunkSize = 5;


    private int pageSize = 20;

    private int skipLimit = 10;


    private int retryLimit = 3;
}
