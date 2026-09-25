package com.example.unifiedsecuritymaster.service;

import com.example.unifiedsecuritymaster.config.MutualFundProperties;
import com.example.unifiedsecuritymaster.dto.mf.MfNavApiResponse;
import com.example.unifiedsecuritymaster.exception.MutualFundApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MutualFundApiService {

    private final MutualFundProperties props;
    private final RestClient mfRestClient;

    public URI buildUri(String isin, LocalDate since) {
        URI uri = UriComponentsBuilder
                .fromUriString(props.getBaseUrl())
                .path(props.getNavPath())
                .queryParam("scheme", isin)
                .queryParam("since",  since.toString())     // ISO yyyy-MM-dd
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();
        log.info(">>> MF REQUEST [{}] = {}", isin, uri);
        return uri;
    }

    /** Fetches the NAV series for one scheme. */
    public MfNavApiResponse fetchNav(String isin, LocalDate since) {

        URI uri = buildUri(isin, since);

        try {
            MfNavApiResponse response = mfRestClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(MfNavApiResponse.class);

            if (response == null) {
                throw new MutualFundApiException(isin, "Empty response body");
            }
            if (response.getData() == null || response.getData().isEmpty()) {
                log.warn("[{}] API returned no NAV points since {}", isin, since);
                response.setData(java.util.List.of());
            }

            log.info("<<< MF RESPONSE [{}] schemeCode={} points={} range={}..{}",
                    isin, response.getSchemeCode(),
                    response.getData().size(),
                    response.getFirstAvailableDate(),
                    response.getLatestAvailableDate());

            return response;

        } catch (MutualFundApiException e) {
            throw e;
        } catch (Exception e) {
            throw new MutualFundApiException(isin,
                    e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }
}
