package com.example.unifiedsecuritymaster.batch;

import com.example.unifiedsecuritymaster.model.CommodityWatchList;
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
public class CommodityToSecurityProcessor
        implements ItemProcessor<CommodityWatchList, SecurityMaster> {

    private final SecurityMasterRepository securityRepo;

    @Override
    public SecurityMaster process(CommodityWatchList wl) {

        String symbol   = wl.getSymbol().trim().toUpperCase();
        String exchange = wl.getExchange() == null ? "NSE" : wl.getExchange().trim().toUpperCase();

        SecurityMaster sm = securityRepo
                .findBySymbolAndExchangeCode(symbol, exchange)
                .orElseGet(SecurityMaster::new);

        sm.setSymbol(symbol);
        sm.setExchangeCode(exchange);
        sm.setIsin(null);
        sm.setName(wl.getName() == null ? symbol : wl.getName().trim());
        sm.setSecurityType(SecurityType.COMMODITY);
        sm.setAsset(wl.getAsset());
        sm.setFaceValue(wl.getQuotation());
        sm.setCurrencyCode("INR");
        sm.setCountryCode("IN");
        sm.setStatus(Boolean.FALSE.equals(wl.getStatus()) ? "INACTIVE" : "ACTIVE");
        sm.setLotSize(1);

        log.debug("Sync COMMODITY {}", symbol);
        return sm;
    }
}
