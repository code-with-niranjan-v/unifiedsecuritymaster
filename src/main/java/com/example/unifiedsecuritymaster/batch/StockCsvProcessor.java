package com.example.unifiedsecuritymaster.batch;

import com.example.unifiedsecuritymaster.config.CsvProperties;
import com.example.unifiedsecuritymaster.dto.nse.NseEquityCsvRow;
import com.example.unifiedsecuritymaster.model.StockData;
import com.example.unifiedsecuritymaster.model.StockWatchList;
import com.example.unifiedsecuritymaster.repository.StockDataRepository;
import com.example.unifiedsecuritymaster.repository.StockWatchListRepository;
import com.example.unifiedsecuritymaster.service.CsvDownloadService;
import com.example.unifiedsecuritymaster.service.NseCsvParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockCsvProcessor implements ItemProcessor<StockWatchList, List<StockData>> {

    private static final DateTimeFormatter NSE_DATE =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);

    private final CsvDownloadService downloadService;
    private final NseCsvParser parser;
    private final CsvProperties props;
    private final StockWatchListRepository watchListRepository;
    private final StockDataRepository stockDataRepository;

    @Override
    public List<StockData> process(StockWatchList wl) {

        LocalDate to = LocalDate.now();

        if (wl.getLastUpdatedAt() != null) {
            LocalDateTime cutoff = LocalDateTime.now()
                    .minusHours(props.getSkipIfUpdatedWithinHours());
            if (wl.getLastUpdatedAt().isAfter(cutoff)) {
                log.info("[{}] refreshed at {} - within {}h window, skipping download",
                        wl.getSymbol(), wl.getLastUpdatedAt(),
                        props.getSkipIfUpdatedWithinHours());
                return null;
            }
        }

        LocalDate from;
        Optional<LocalDate> watermark = stockDataRepository.findLatestTradeDate(wl.getSymbol());

        if (watermark.isPresent()) {
            from = watermark.get().minusDays(props.getOverlapDays());
            log.info("[{}] INCREMENTAL load: watermark={} -> fetching {} to {}",
                    wl.getSymbol(), watermark.get(), from, to);
        } else {
            from = to.minusDays(props.getInitialLookbackDays());
            log.info("[{}] INITIAL full load: fetching {} to {}",
                    wl.getSymbol(), from, to);
        }

        if (!from.isBefore(to)) {
            log.info("[{}] already up to date, nothing to fetch", wl.getSymbol());
            return null;
        }


        Path csvFile = null;
        try {
            csvFile = downloadService.download(wl, from, to);
            List<NseEquityCsvRow> csvRows = parser.parse(csvFile);

            List<StockData> result = new ArrayList<>(csvRows.size());

            for (NseEquityCsvRow row : csvRows) {
                LocalDate  tradeDate  = toDate(row.getTradeDate());
                BigDecimal openPrice  = toDecimal(row.getOpenPrice());
                BigDecimal closePrice = toDecimal(row.getClosePrice());

                if (tradeDate == null || closePrice == null) {
                    log.debug("[{}] skipping incomplete row: {}", wl.getSymbol(), row);
                    continue;
                }

                result.add(StockData.builder()
                        .symbol(wl.getSymbol())          // from watchlist - system of record
                        .tradeDate(tradeDate)
                        .openPrice(openPrice)
                        .closePrice(closePrice)
                        .watchlistId(wl.getId())
                        .build());
            }

            wl.setLastUpdatedAt(LocalDateTime.now());
            watchListRepository.save(wl);

            log.info("[{}] csvRows={} -> stockData={}",
                    wl.getSymbol(), csvRows.size(), result.size());

            return result.isEmpty() ? null : result;     // null -> filtered, nothing written

        } finally {
            downloadService.deleteQuietly(csvFile);
        }
    }


    private static BigDecimal toDecimal(String value) {
        String s = strip(value);
        if (s == null) return null;
        try {
            return new BigDecimal(s);
        } catch (NumberFormatException e) {
            log.warn("Unparseable number: '{}'", value);
            return null;
        }
    }

    private static LocalDate toDate(String value) {
        String s = strip(value);
        if (s == null) return null;
        try {
            return LocalDate.parse(s, NSE_DATE);
        } catch (Exception e) {
            log.warn("Unparseable date: '{}'", value);
            return null;
        }
    }

   private static String strip(String value) {
        if (value == null) return null;
        String s = value.replace(",", "")
                .replace("\u20B9", "")
                .replace("\u00A0", "")
                .replace("\uFEFF", "")
                .trim();
        return (s.isEmpty() || "-".equals(s) || "NA".equalsIgnoreCase(s)) ? null : s;
    }
}
