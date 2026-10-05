package com.example.unifiedsecuritymaster.model;

import com.example.unifiedsecuritymaster.model.enums.EquityCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "mutualfund_watchlist")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MutualFundWatchList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String isin;

    private String schemeName;

    @ManyToOne
    private Asset asset;

    private Boolean status;

    private LocalDate lastUpdatedAt;

    @Enumerated(EnumType.STRING)
    private EquityCategory equityCategory;
}
