package com.energy.community.usageservice;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "energy_hourly_metrics")
public class EnergyHourlyMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "hour", nullable = false, unique = true)
    private LocalDateTime hour;

    @Column(name = "community_produced", nullable = false)
    private double communityProduced;

    @Column(name = "community_used", nullable = false)
    private double communityUsed;

    @Column(name = "grid_used", nullable = false)
    private double gridUsed;

    public EnergyHourlyMetric() {
    }

    public EnergyHourlyMetric(LocalDateTime hour) {
        this.hour = hour;
        this.communityProduced = 0.0;
        this.communityUsed = 0.0;
        this.gridUsed = 0.0;
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getHour() {
        return hour;
    }

    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }

    public double getCommunityProduced() {
        return communityProduced;
    }

    public void setCommunityProduced(double communityProduced) {
        this.communityProduced = communityProduced;
    }

    public double getCommunityUsed() {
        return communityUsed;
    }

    public void setCommunityUsed(double communityUsed) {
        this.communityUsed = communityUsed;
    }

    public double getGridUsed() {
        return gridUsed;
    }

    public void setGridUsed(double gridUsed) {
        this.gridUsed = gridUsed;
    }
}