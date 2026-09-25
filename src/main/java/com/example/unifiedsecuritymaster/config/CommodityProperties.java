package com.example.unifiedsecuritymaster.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Data
@Component
@ConfigurationProperties(prefix = "securitymaster.commodity")
public class CommodityProperties {

    private String urlTemplate;
    private String baseUrl;

    private String dateFormat = "dd-MM-yyyy";

    private LocalDate initialSince = LocalDate.of(2020, 1, 1);


    private int overlapDays = 5;
}
