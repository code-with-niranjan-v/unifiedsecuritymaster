package com.example.unifiedsecuritymaster.batch;

import com.example.unifiedsecuritymaster.model.MutualFundWatchList;
import com.example.unifiedsecuritymaster.model.SecurityMaster;
import com.example.unifiedsecuritymaster.model.enums.SecurityType;
import com.example.unifiedsecuritymaster.repository.SecurityMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MutualFundToSecurityProcessor
        implements ItemProcessor<MutualFundWatchList, SecurityMaster> {

    private static final String EXCHANGE = "AMFI";

    private final SecurityMasterRepository securityRepo;

    @Override
    public SecurityMaster process(MutualFundWatchList wl) {

        if (wl.getIsin() == null || wl.getIsin().isBlank()) {
            log.warn("MF watchlist id={} has no ISIN, skipping", wl.getId());
            return null;
        }

        // MFs have no ticker -> use ISIN as the symbol so the NOT NULL + unique key hold
        String symbol = wl.getIsin().trim().toUpperCase();

        SecurityMaster sm = securityRepo
                .findBySymbolAndExchangeCode(symbol, EXCHANGE)
                .orElseGet(SecurityMaster::new);

        sm.setSymbol(symbol);
        sm.setExchangeCode(EXCHANGE);
        sm.setIsin(wl.getIsin().trim().toUpperCase());
        sm.setName(truncate(wl.getSchemeName(), 200));
        sm.setSecurityType(SecurityType.MUTUAL_FUND);
        sm.setIssuerName(extractAmc(wl.getSchemeName()));
        sm.setAsset(wl.getAsset());                 // already linked on the watchlist
        sm.setCurrencyCode("INR");
        sm.setCountryCode("IN");
        sm.setStatus(Boolean.FALSE.equals(wl.getStatus()) ? "INACTIVE" : "ACTIVE");
        sm.setLotSize(1);

        log.debug("Sync MUTUAL_FUND {}", symbol);
        return sm;
    }

    /** "HDFC Corporate Bond Fund - Direct - Growth" -> "HDFC" */
    private static String extractAmc(String schemeName) {
        if (schemeName == null || schemeName.isBlank()) return null;
        String first = schemeName.trim().split("\\s+")[0];
        return first.length() > 100 ? first.substring(0, 100) : first;
    }

    private static String truncate(String v, int max) {
        if (v == null) return null;
        String t = v.trim();
        return t.length() <= max ? t : t.substring(0, max);
    }
}
