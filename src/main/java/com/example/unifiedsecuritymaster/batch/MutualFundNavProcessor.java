package com.example.unifiedsecuritymaster.batch;

import com.example.unifiedsecuritymaster.config.MutualFundProperties;
import com.example.unifiedsecuritymaster.dto.mf.MfNavApiResponse;
import com.example.unifiedsecuritymaster.dto.mf.MfNavPoint;
import com.example.unifiedsecuritymaster.model.MutualFundNav;
import com.example.unifiedsecuritymaster.model.MutualFundWatchList;
import com.example.unifiedsecuritymaster.repository.MutualFundNavRepository;
import com.example.unifiedsecuritymaster.repository.MutualFundWatchListRepository;
import com.example.unifiedsecuritymaster.service.MutualFundApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MutualFundNavProcessor implements ItemProcessor<MutualFundWatchList, List<MutualFundNav>> {

    private final MutualFundApiService apiService;
    private final MutualFundProperties props;
    private final MutualFundNavRepository navRepository;
    private final MutualFundWatchListRepository watchListRepository;

    @Override
    public List<MutualFundNav> process(MutualFundWatchList wl) {

        if (Boolean.FALSE.equals(wl.getStatus())) {
            log.debug("[{}] status=false, skipping", wl.getIsin());
            return null;
        }

        if (wl.getIsin() == null || wl.getIsin().isBlank()) {
            log.warn("Watchlist id={} has no ISIN, skipping", wl.getId());
            return null;
        }

        LocalDate today = LocalDate.now();
        if (today.equals(wl.getLastUpdatedAt())) {
            log.info("[{}] already refreshed on {}, skipping", wl.getIsin(), today);
            return null;
        }

        LocalDate since;
        Optional<LocalDate> watermark = navRepository.findLatestNavDate(wl.getIsin());

        if (watermark.isPresent()) {
            since = watermark.get().minusDays(props.getOverlapDays());
            log.info("[{}] INCREMENTAL load: watermark={} -> since={}",
                    wl.getIsin(), watermark.get(), since);
        } else {
            since = props.getInitialSince();
            log.info("[{}] INITIAL full load: since={}", wl.getIsin(), since);
        }

        MfNavApiResponse response = apiService.fetchNav(wl.getIsin(), since);

        List<MutualFundNav> result = new ArrayList<>(response.getData().size());

        for (MfNavPoint point : response.getData()) {
            if (point.getDate() == null || point.getNav() == null) {
                continue;
            }
            if (point.getNav().compareTo(BigDecimal.ZERO) <= 0) {
                continue;                             // liquidating scheme, junk row
            }
            result.add(MutualFundNav.builder()
                    .isin(wl.getIsin())
                    .schemeCode(response.getSchemeCode())
                    .schemeName(wl.getSchemeName())   // watchlist is system of record
                    .navDate(point.getDate())
                    .nav(point.getNav())
                    .watchlistId(wl.getId())
                    .build());
        }

        wl.setLastUpdatedAt(today);
        watchListRepository.save(wl);

        log.info("[{}] apiPoints={} -> navRecords={}",
                wl.getIsin(), response.getData().size(), result.size());

        return result.isEmpty() ? null : result;
    }
}