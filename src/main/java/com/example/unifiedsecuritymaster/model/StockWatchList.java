package com.example.unifiedsecuritymaster.model;

import com.example.unifiedsecuritymaster.model.enums.EquityCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_watchlist",uniqueConstraints = @UniqueConstraint(
        name = "uk_watchlist_symbol_exchange",
        columnNames = {"symbol", "exchange"}))
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StockWatchList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String symbol;

    private String name;

    private String exchange;

    private String isin;

    private String gics;

    private String country;

    private String industry;

    private String sector;

    private LocalDateTime lastUpdatedAt;

    @ManyToOne
    private Asset asset;

    @Enumerated(EnumType.STRING)
    private EquityCategory equityCategory;
}
