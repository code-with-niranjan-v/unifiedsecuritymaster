package com.example.unifiedsecuritymaster.dto.commodity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NseCommodityApiResponse {
    private List<NseCommoditySpotRow> data;
}
