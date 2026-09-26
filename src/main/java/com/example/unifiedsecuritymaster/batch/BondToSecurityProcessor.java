package com.example.unifiedsecuritymaster.batch;

import com.example.unifiedsecuritymaster.model.Bond;
import com.example.unifiedsecuritymaster.model.SecurityMaster;
import com.example.unifiedsecuritymaster.model.enums.SecurityType;
import com.example.unifiedsecuritymaster.repository.SecurityMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class BondToSecurityProcessor implements ItemProcessor<Bond, SecurityMaster> {

    private final SecurityMasterRepository securityRepo;

    @Override
    public SecurityMaster process(Bond bond) {

        if (bond.getIsin() == null || bond.getIsin().isBlank()) {
            log.warn("Bond id={} has no ISIN, skipping", bond.getId());
            return null;
        }


        String symbol   = bond.getIsin().trim().toUpperCase();
        String exchange = resolveExchange(bond);

        SecurityMaster sm = securityRepo
                .findBySymbolAndExchangeCode(symbol, exchange)
                .orElseGet(SecurityMaster::new);

        sm.setSymbol(symbol);
        sm.setIsin(symbol);
        sm.setExchangeCode(exchange);
        sm.setName(truncate(bond.getName(), 200));
        sm.setSecurityType(SecurityType.BOND);
        sm.setIssuerName(truncate(bond.getIssuerName(), 255));
        sm.setAsset(bond.getAsset());


        sm.setFaceValue(bond.getFaceValue() == null ? null : String.valueOf(bond.getFaceValue()));

        sm.setCurrencyCode(bond.getCurrency() == null ? "INR" : bond.getCurrency().trim().toUpperCase());
        sm.setCountryCode(bond.getCountry());
        sm.setListingDate(bond.getIssueDate());
        sm.setLotSize(1);
        sm.setStatus(resolveStatus(bond));


        sm.setGicsSector("Fixed Income");
        sm.setGicsIndustry(truncate(bond.getBondType(), 30));
        sm.setGicsSubIndustry(truncate(bond.getCreditRating(), 30));

        log.debug("Sync BOND {} / {}", symbol, exchange);
        return sm;
    }


    private static String resolveStatus(Bond bond) {
        if (bond.getMaturityDate() != null && bond.getMaturityDate().isBefore(LocalDate.now())) {
            return "MATURED";
        }
        return bond.getStatus() == null ? "ACTIVE" : bond.getStatus().trim().toUpperCase();
    }

    private static String resolveExchange(Bond bond) {
        if (bond.getExchange() != null) {
            return bond.getExchange().name();
        }

        return "OTC";
    }

    private static String truncate(String v, int max) {
        if (v == null) return null;
        String t = v.trim();
        return t.length() <= max ? t : t.substring(0, max);
    }
}
