package com.example.unifiedsecuritymaster.dto.request;

import com.example.unifiedsecuritymaster.model.enums.EquityCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddStockDTO {

    private String symbol;
    private String name;
    private String exchange;
    private String isin;
    private String gics;
    private String country;
    private String industry;
    private String sector;
    private Integer assetId;


    private EquityCategory equityCategory;
}
