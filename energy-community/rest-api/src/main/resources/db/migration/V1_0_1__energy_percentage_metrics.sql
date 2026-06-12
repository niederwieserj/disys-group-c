CREATE TABLE energy_percentage_metrics (
   hour TIMESTAMP WITH TIME ZONE NOT NULL UNIQUE PRIMARY KEY,
   community_depleted NUMERIC(10, 3) NOT NULL DEFAULT 0,
   grid_portion NUMERIC(10, 3) NOT NULL DEFAULT 0
);
