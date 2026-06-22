package com.energy.community.current.percentage.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity(name="energy_percentage_metrics")
public class PercentageEntity {
    @Id
    @Column(name = "hour", columnDefinition = "timestamp")
    private LocalDateTime hour;

    @Column(name="community_depleted")
    private double communityDepleted;

    @Column(name="grid_portion")
    private double gridPortion;

    public LocalDateTime getHour() {
        return hour;
    }

    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }

    public double getCommunityDepleted() {
        return communityDepleted;
    }

    public void setCommunityDepleted(double community_depleted) {
        this.communityDepleted = community_depleted;
    }

    public double getGridPortion() {
        return gridPortion;
    }

    public void setGridPortion(double grid_portion) {
        this.gridPortion = grid_portion;
    }
}
