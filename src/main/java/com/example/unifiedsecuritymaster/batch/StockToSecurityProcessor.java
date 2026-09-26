package com.example.unifiedsecuritymaster.batch;

import com.example.unifiedsecuritymaster.model.SecurityMaster;
import com.example.unifiedsecuritymaster.model.StockWatchList;
import com.example.unifiedsecuritymaster.model.enums.SecurityType;
import com.example.unifiedsecuritymaster.repository.AssetRepository;
import com.example.unifiedsecuritymaster.repository.SecurityMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockToSecurityProcessor implements ItemProcessor<StockWatchList, SecurityMaster> {

    private final SecurityMasterRepository securityRepo;
    private final AssetRepository assetRepo;

    @Override
    public SecurityMaster process(StockWatchList wl) {

        String symbol   = wl.getSymbol().trim().toUpperCase();
        String exchange = wl.getExchange() == null ? "NSE" : wl.getExchange().trim().toUpperCase();


        SecurityMaster sm = securityRepo
                .findBySymbolAndExchangeCode(symbol, exchange)
                .orElseGet(SecurityMaster::new);

        sm.setSymbol(symbol);
        sm.setExchangeCode(exchange);
        sm.setIsin(wl.getIsin());
        sm.setName(truncate(wl.getName(), 200));
        sm.setSecurityType(SecurityType.EQUITY);
        sm.setGicsSubIndustry(truncate(wl.getIndustry(), 30));
        sm.setGicsSector(truncate(wl.getSector(), 30));
        sm.setGicsIndustry(truncate(wl.getIndustry(), 30));
        sm.setCurrencyCode("INR");
        sm.setCountryCode(toCountryCode(wl.getCountry()));
        sm.setStatus("ACTIVE");
        sm.setLotSize(1);

        assetRepo.findFirstByAssetSubclassIgnoreCase("Stock").ifPresent(sm::setAsset);

        log.debug("Sync EQUITY {} / {}", symbol, exchange);
        return sm;
    }

    private static String truncate(String v, int max) {
        if (v == null) return null;
        String t = v.trim();
        return t.length() <= max ? t : t.substring(0, max);
    }

    private static String toCountryCode(String country) {
        if (country == null) return null;
        return switch (country.trim().toLowerCase()) {
            case "india"          -> "IN";
            case "united states", "usa" -> "US";
            case "united kingdom", "uk" -> "GB";
            default -> country.length() >= 2 ? country.substring(0, 2).toUpperCase() : null;
        };
    }
}
