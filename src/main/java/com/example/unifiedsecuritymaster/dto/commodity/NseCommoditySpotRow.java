package com.example.unifiedsecuritymaster.dto.commodity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NseCommoditySpotRow {

    @JsonProperty("ProductID")        private String productId;
    @JsonProperty("Symbol")           private String symbol;
    @JsonProperty("Quotation")        private String quotation;
    @JsonProperty("Unit")             private String unit;
    @JsonProperty("SpotPrice1")       private String spotPrice1;
    @JsonProperty("SpotPrice2")       private String spotPrice2;
    @JsonProperty("UpdatedDate")      private String updatedDate;     // 25-SEP-2026
    @JsonProperty("Attachment")       private String attachment;
    @JsonProperty("PRICE_TIMESTAMP")  private String priceTimestamp;  // 2026-09-25 12:40:03
}
