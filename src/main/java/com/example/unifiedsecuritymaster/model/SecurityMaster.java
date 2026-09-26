package com.example.unifiedsecuritymaster.model;

import com.example.unifiedsecuritymaster.model.enums.SecurityType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "security_master",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_sm_symbol_exchange",
                columnNames = {"symbol", "exchange_code"}),
        indexes = {
                @Index(name = "idx_sm_isin",   columnList = "isin"),
                @Index(name = "idx_sm_symbol", columnList = "symbol"),
                @Index(name = "idx_sm_gics",   columnList = "gics_sector, gics_industry")
        })
public class SecurityMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String isin;

    @Column
    private String symbol;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private SecurityType securityType;

    @ManyToOne
    private Asset asset;

    @Column(length = 30)
    private String gicsSector;

    @Column(length = 30)
    private String gicsGroup;

    @Column(length = 30)
    private String gicsIndustry;

    @Column(length = 30)
    private String gicsSubIndustry;

    @Column
    private String issuerName;

    @Column
    private String faceValue;


    @Column(nullable = false, length = 20)
    private String exchangeCode;

    @Column(length = 3)
    private String currencyCode;

    @Column(length = 2)
    private String countryCode;

    @Column(nullable = false, length = 20)
    private String status;


    @Column
    private LocalDate listingDate;

    @Column
    private Integer lotSize;


}
