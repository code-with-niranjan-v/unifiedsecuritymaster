package com.example.unifiedsecuritymaster.exception;

public class CommodityApiException extends RuntimeException {
    public CommodityApiException(String symbol, String message) {
        super("Commodity spot fetch failed for [" + symbol + "]: " + message);
    }
    public CommodityApiException(String symbol, String message, Throwable cause) {
        super("Commodity spot fetch failed for [" + symbol + "]: " + message, cause);
    }
}
