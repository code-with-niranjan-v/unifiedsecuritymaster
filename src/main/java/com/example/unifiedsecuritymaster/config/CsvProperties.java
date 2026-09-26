package com.example.unifiedsecuritymaster.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "securitymaster.csv")
public class CsvProperties {

//    /** URL template with {symbol}, {series}, {from}, {to} placeholders. */
//    private String urlTemplate;
//
//    /** Vendor base URL, used to warm the session cookie. */
//    private String baseUrl;
//
//    /** Default NSE series filter, e.g. EQ. */
//    private String defaultSeries = "EQ";
//
//    /** How many days of history to request per run. */
//    private int lookbackDays = 30;
//
//    /** Local directory where downloaded CSVs are staged. */
//    private String storageDir = "./data/csv";
//
//    /** Keep CSVs on disk after parsing (useful during development). */
//    private boolean keepFiles = true;
//
//    private int connectTimeoutMs = 10000;
//    private int readTimeoutMs    = 60000;

    private String  urlTemplate;
    private String  baseUrl;
    private String  defaultSeries            = "ALL";
    private String  storageDir               = "./data/csv";
    private boolean keepFiles                = true;
    private int     connectTimeoutMs         = 10000;
    private int     readTimeoutMs            = 120000;


    private int initialLookbackDays          = 1825;
    private int overlapDays                  = 5;
    private int skipIfUpdatedWithinHours     = 12;
    private boolean insecureSsl = false;
}