package com.example.unifiedsecuritymaster.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCommodityDTO {

    @Size(max = 20)
    private String productId;          // "10001"

    @NotBlank(message = "symbol is required")
    @Size(max = 32)
    private String symbol;             // "GOLD"

    @Size(max = 128)
    private String name;               // "Gold Spot"

    @Size(max = 32)
    private String quotation;          // "10 Grams"

    @Size(max = 32)
    private String unit;               // "1 Kg"

    @Size(max = 10)
    private String exchange;           // "NSE"

    @NotNull(message = "assetId is required")
    private Integer assetId;

    private Boolean status = Boolean.TRUE;
}
