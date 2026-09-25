package com.example.unifiedsecuritymaster.batch;

import com.example.unifiedsecuritymaster.config.CommodityProperties;
import com.example.unifiedsecuritymaster.dto.commodity.NseCommoditySpotRow;
import com.example.unifiedsecuritymaster.model.CommoditySpotData;
import com.example.unifiedsecuritymaster.model.CommodityWatchList;
import com.example.unifiedsecuritymaster.repository.CommoditySpotDataRepository;
import com.example.unifiedsecuritymaster.repository.CommodityWatchListRepository;
import com.example.unifiedsecuritymaster.service.CommodityApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommoditySpotProcessor
        implements ItemProcessor<CommodityWatchList, List<CommoditySpotData>> {

    private final CommodityProperties props;
    private static final DateTimeFormatter NSE_DATE = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("dd-MMM-yyyy")
            .toFormatter(Locale.ENGLISH);

    private static final DateTimeFormatter TS_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    private final CommodityApiService apiService;
    private final CommoditySpotDataRepository spotRepository;
    private final CommodityWatchListRepository watchListRepository;

    @Override
    public List<CommoditySpotData> process(CommodityWatchList wl) {

        if (Boolean.FALSE.equals(wl.getStatus())) {
            log.debug("[{}] status=false, skipping", wl.getSymbol());
            return null;
        }

        LocalDate to = LocalDate.now();
        LocalDate from;

        // watermark = latest FINAL date, so today's provisional row is re-fetched
        Optional<LocalDate> watermark = spotRepository.findLatestFinalDate(wl.getSymbol());

        if (watermark.isPresent()) {
            from = watermark.get().minusDays(props.getOverlapDays());
            log.info("[{}] INCREMENTAL: watermark={} -> fetching {} to {}",
                    wl.getSymbol(), watermark.get(), from, to);
        } else {
            from = props.getInitialSince();
            log.info("[{}] INITIAL full load: fetching {} to {}", wl.getSymbol(), from, to);
        }

        if (from.isAfter(to)) {
            log.info("[{}] already up to date", wl.getSymbol());
            return null;
        }

        List<NseCommoditySpotRow> rows = apiService.fetchSpot(wl.getSymbol(), from, to);

        List<CommoditySpotData> result = new ArrayList<>(rows.size());

        for (NseCommoditySpotRow row : rows) {

            LocalDate spotDate = toDate(row.getUpdatedDate());
            if (spotDate == null) continue;
            if (spotDate.isBefore(from) || spotDate.isAfter(to)) continue;   // defensive

            BigDecimal p1 = toDecimal(row.getSpotPrice1());
            BigDecimal p2 = toDecimal(row.getSpotPrice2());
            BigDecimal effective = (p2 != null) ? p2 : p1;

            if (effective == null || effective.signum() <= 0) continue;

            result.add(CommoditySpotData.builder()
                    .symbol(wl.getSymbol())
                    .spotDate(spotDate)
                    .spotPrice1(p1)
                    .spotPrice2(p2)
                    .spotPrice(effective)
                    .quotation(row.getQuotation())
                    .priceTimestamp(toTimestamp(row.getPriceTimestamp()))
                    .isFinal(p2 != null)
                    .watchlistId(wl.getId())
                    .build());
        }

        wl.setLastUpdatedAt(to);
        watchListRepository.save(wl);

        log.info("[{}] apiRows={} -> stored={}", wl.getSymbol(), rows.size(), result.size());
        return result.isEmpty() ? null : result;
    }

    // ---------- helpers ----------

    private static BigDecimal toDecimal(String v) {
        String s = strip(v);
        if (s == null) return null;
        try { return new BigDecimal(s); }
        catch (NumberFormatException e) { log.warn("Bad number: '{}'", v); return null; }
    }

    private static LocalDate toDate(String v) {
        String s = strip(v);
        if (s == null) return null;
        try { return LocalDate.parse(s, NSE_DATE); }
        catch (Exception e) { log.warn("Bad date: '{}'", v); return null; }
    }

    private static LocalDateTime toTimestamp(String v) {
        String s = strip(v);
        if (s == null) return null;
        try { return LocalDateTime.parse(s, TS_FORMAT); }
        catch (Exception e) { return null; }
    }

    private static String strip(String v) {
        if (v == null) return null;
        String s = v.replace(",", "").trim();
        return (s.isEmpty() || "-".equals(s) || "null".equalsIgnoreCase(s)) ? null : s;
    }
}
