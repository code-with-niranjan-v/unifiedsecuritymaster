package com.example.unifiedsecuritymaster.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;

@Slf4j
@Configuration
public class MfRestClientConfig {

    @Bean
    public RestClient mfRestClient(MutualFundProperties props) throws Exception {

        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(props.getConnectTimeoutMs()))
                .followRedirects(HttpClient.Redirect.NORMAL);

        if (props.isInsecureSsl()) {
            log.warn("#### MF CLIENT: TLS VALIDATION DISABLED - LOCAL DEV ONLY ####");
            builder.sslContext(trustAllSslContext());
        }

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(builder.build());
        factory.setReadTimeout(Duration.ofMillis(props.getReadTimeoutMs()));

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(props.getBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .defaultHeader(HttpHeaders.USER_AGENT, "unified-security-master/1.0")
                .build();
    }

    private SSLContext trustAllSslContext() throws Exception {
        TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    @Override public void checkClientTrusted(X509Certificate[] c, String a) { }
                    @Override public void checkServerTrusted(X509Certificate[] c, String a) { }
                    @Override public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                }
        };
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, trustAll, new SecureRandom());
        return ctx;
    }
}