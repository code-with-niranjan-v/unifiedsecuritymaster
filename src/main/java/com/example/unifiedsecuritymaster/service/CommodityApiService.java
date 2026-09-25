package com.example.unifiedsecuritymaster.service;

import com.example.unifiedsecuritymaster.config.CommodityProperties;
import com.example.unifiedsecuritymaster.dto.commodity.NseCommodityApiResponse;
import com.example.unifiedsecuritymaster.dto.commodity.NseCommoditySpotRow;
import com.example.unifiedsecuritymaster.exception.CommodityApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommodityApiService {

    private final CommodityProperties props;
    private final RestClient nseRestClient;   // REUSED from your stock job

    public String buildUrl(String symbol, LocalDate from, LocalDate to) {

        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern(props.getDateFormat(), Locale.ENGLISH);

        String url = props.getUrlTemplate()
                .replace("{symbol}", symbol)
                .replace("{from}",   from.format(fmt))
                .replace("{to}",     to.format(fmt));

        log.info(">>> COMMODITY URL [{}] = {}", symbol, url);

        if (!url.startsWith("http")) {
            throw new CommodityApiException(symbol,
                    "Malformed url-template in application.properties: " + url);
        }
        if (url.contains("{")) {
            throw new CommodityApiException(symbol,
                    "Unresolved placeholder left in URL: " + url);
        }
        return url;
    }

    public List<NseCommoditySpotRow> fetchSpot(String symbol, LocalDate from, LocalDate to) {

        URI uri = URI.create(buildUrl(symbol, from, to));

        try {
            NseCommodityApiResponse res = nseRestClient.get()
                    .uri(uri)
                    .header(HttpHeaders.REFERER, props.getBaseUrl())
                    .retrieve()
                    .body(NseCommodityApiResponse.class);

            if (res == null || res.getData() == null) {
                throw new CommodityApiException(symbol, "Empty or malformed response");
            }

            log.info("<<< COMMODITY RESPONSE [{}] rows={} window={}..{}",
                    symbol, res.getData().size(), from, to);
            return res.getData();

        } catch (CommodityApiException e) {
            throw e;
        } catch (Exception e) {
            throw new CommodityApiException(symbol,
                    e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }
}
