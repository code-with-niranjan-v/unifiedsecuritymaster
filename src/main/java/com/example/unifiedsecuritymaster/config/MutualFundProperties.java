package com.example.unifiedsecuritymaster.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Data
@Component
@ConfigurationProperties(prefix = "securitymaster.mf")
public class MutualFundProperties {

    private String baseUrl;

    private String navPath;

    private LocalDate initialSince = LocalDate.of(2020, 1, 1);

    private int overlapDays = 5;

    private int     connectTimeoutMs = 10000;
    private int     readTimeoutMs    = 120000;

    private boolean insecureSsl = false;
}