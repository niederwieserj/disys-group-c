-- Energy monitoring data table
CREATE TABLE energy_hourly_metrics (
   id SERIAL PRIMARY KEY,
   hour TIMESTAMP WITH TIME ZONE NOT NULL UNIQUE,
   community_produced NUMERIC(10, 3) NOT NULL DEFAULT 0,
   community_used NUMERIC(10, 3) NOT NULL DEFAULT 0,
   grid_used NUMERIC(10, 3) NOT NULL DEFAULT 0
);