CREATE TABLE collection_log (
    id          BIGSERIAL    PRIMARY KEY,
    job_type    VARCHAR(50)  NOT NULL,
    run_at      TIMESTAMPTZ  NOT NULL,
    duration_ms BIGINT,
    status      VARCHAR(20)  NOT NULL CHECK (status IN ('SUCCESS', 'FAILED')),
    message     TEXT
);
