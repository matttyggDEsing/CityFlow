CREATE TABLE traffic_events (
                                id                BIGSERIAL       PRIMARY KEY,
                                event_id          VARCHAR(36)     NOT NULL UNIQUE,
                                zone_id           VARCHAR(50)     NOT NULL,
                                sensor_id         VARCHAR(100)    NOT NULL,
                                vehicle_count     INTEGER         NOT NULL,
                                avg_speed_kmh     FLOAT8          NOT NULL,
                                congestion_index  FLOAT8          NOT NULL,
                                status            VARCHAR(10)     NOT NULL,
                                recorded_at       TIMESTAMPTZ     NOT NULL,
                                created_at        TIMESTAMPTZ     DEFAULT NOW()
);

CREATE INDEX idx_traffic_zone ON traffic_events (zone_id, recorded_at DESC);

CREATE INDEX idx_traffic_status ON traffic_events (status, recorded_at DESC);