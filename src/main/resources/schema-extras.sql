-- Required by the ON CONFLICT clause in stockDataJdbcWriter
ALTER TABLE stock_data
    ADD CONSTRAINT uk_stock_data_symbol_date UNIQUE (symbol, trade_date);

CREATE INDEX IF NOT EXISTS idx_stock_data_symbol ON stock_data (symbol);
CREATE INDEX IF NOT EXISTS idx_stock_data_date   ON stock_data (trade_date);

-- Helpful for the batch metadata tables once volume

CREATE TABLE IF NOT EXISTS mutualfund_nav (
                                              id            BIGSERIAL PRIMARY KEY,
                                              isin          VARCHAR(12)   NOT NULL,
    scheme_code   BIGINT,
    scheme_name   VARCHAR(255),
    nav_date      DATE          NOT NULL,
    nav           NUMERIC(18,4),
    watchlist_id  INTEGER REFERENCES mutualfund_watchlist(id),
    CONSTRAINT uk_mf_nav_isin_date UNIQUE (isin, nav_date)
    );

CREATE INDEX IF NOT EXISTS idx_mfnav_isin ON mutualfund_nav(isin);
CREATE INDEX IF NOT EXISTS idx_mfnav_date ON mutualfund_nav(nav_date);

CREATE TABLE IF NOT EXISTS commodity_watchlist (
                                                   id              SERIAL PRIMARY KEY,
                                                   product_id      VARCHAR(20),
    symbol          VARCHAR(32) NOT NULL,
    name            VARCHAR(128),
    quotation       VARCHAR(32),
    unit            VARCHAR(32),
    exchange        VARCHAR(10) DEFAULT 'NSE',
    asset_id        INTEGER REFERENCES asset(id),
    status          BOOLEAN DEFAULT TRUE,
    last_updated_at DATE,
    CONSTRAINT uk_comm_wl_symbol UNIQUE (symbol)
    );

CREATE TABLE IF NOT EXISTS commodity_spot_data (
                                                   id              BIGSERIAL PRIMARY KEY,
                                                   symbol          VARCHAR(32) NOT NULL,
    spot_date       DATE        NOT NULL,
    spot_price1     NUMERIC(18,4),
    spot_price2     NUMERIC(18,4),
    spot_price      NUMERIC(18,4),
    quotation       VARCHAR(32),
    price_timestamp TIMESTAMP,
    is_final        BOOLEAN DEFAULT FALSE,
    watchlist_id    INTEGER REFERENCES commodity_watchlist(id),
    CONSTRAINT uk_comm_spot_symbol_date UNIQUE (symbol, spot_date)
    );

CREATE INDEX IF NOT EXISTS idx_comm_spot_symbol ON commodity_spot_data(symbol);
CREATE INDEX IF NOT EXISTS idx_comm_spot_date   ON commodity_spot_data(spot_date);