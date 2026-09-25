package com.example.unifiedsecuritymaster.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "commodity_watchlist",
        uniqueConstraints = @UniqueConstraint(name = "uk_comm_wl_symbol", columnNames = {"symbol"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommodityWatchList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "product_id", length = 20)
    private String productId;          // "10001"

    @Column(nullable = false, length = 32)
    private String symbol;             // "GOLD"

    @Column(length = 128)
    private String name;               // "Gold 999 Spot"

    @Column(length = 32)
    private String quotation;          // "10 Grams"

    @Column(length = 32)
    private String unit;               // "1 Kg"

    @Column(length = 10)
    private String exchange;           // "NSE"

    @ManyToOne
    private Asset asset;

    private Boolean status;

    @Column(name = "last_updated_at")
    private LocalDate lastUpdatedAt;
}
