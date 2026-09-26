package com.example.unifiedsecuritymaster.dto.request;

import com.example.unifiedsecuritymaster.model.Asset;
import com.example.unifiedsecuritymaster.model.enums.SecurityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddSecurityMasterDTO {


    private String isin;

  
    private String symbol;


    private String name;


    private SecurityType securityType;


    private Integer assertId;


    private String gicsSector;


    private String gicsGroup;


    private String gicsIndustry;


    private String gicsSubIndustry;

    
    private String issuerName;

    
    private String faceValue;



    private String exchangeCode;


    private String currencyCode;


    private String countryCode;

    private String status;


    
    private LocalDate listingDate;

    
    private Integer lotSize;


}
