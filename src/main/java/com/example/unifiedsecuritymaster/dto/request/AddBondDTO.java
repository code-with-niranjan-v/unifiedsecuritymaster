package com.example.unifiedsecuritymaster.dto.request;

import com.example.unifiedsecuritymaster.model.enums.Exchange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddBondDTO {
    private String isin;

    private String name;

    private String issuerName;

    private String bondType;

    private Exchange exchange;

    private String currency;

    private Double faceValue;

    private Double couponRate;

    private String couponFrequency;

    private LocalDate issueDate;

    private LocalDate maturityDate;

    private String creditRating;

    private Integer assetId;


}
