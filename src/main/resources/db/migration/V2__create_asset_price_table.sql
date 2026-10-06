CREATE TABLE asset_price (
    id          BIGSERIAL        PRIMARY KEY,
    asset_id    BIGINT           NOT NULL,
    price_time  TIMESTAMPTZ      NOT NULL,
    open_price  NUMERIC(19, 4)   NOT NULL,
    close_price NUMERIC(19, 4)   NOT NULL,
    high_price  NUMERIC(19, 4)   NOT NULL,
    low_price   NUMERIC(19, 4)   NOT NULL,

    CONSTRAINT fk_asset_price_asset
        FOREIGN KEY (asset_id) REFERENCES asset(id),

    CONSTRAINT uq_asset_price_asset_id_price_time
        UNIQUE (asset_id, price_time)
);
