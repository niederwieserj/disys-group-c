ALTER TABLE energy_hourly_metrics
    ALTER COLUMN hour TYPE timestamp USING hour AT TIME ZONE 'UTC';

ALTER TABLE energy_percentage_metrics
    ALTER COLUMN hour TYPE timestamp USING hour AT TIME ZONE 'UTC';