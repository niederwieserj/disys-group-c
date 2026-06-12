package com.energy.community.restapi.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// Class represents a single table in DB, instance of class represents row in a table
@Entity(name="energy_hourly_metrics")
public class EnergyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "hour", columnDefinition = "timestamp")
    private LocalDateTime hour;

    @Column(name="community_produced")
    private double communityProduced;

    @Column(name="community_used")
    private double communityUsed;

    @Column(name="grid_used")
    private double gridUsed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setCommunityProduced(double community_produced) {
        this.communityProduced = community_produced;
    }

    public double getCommunityUsed() {
        return communityUsed;
    }

    public void setCommunityUsed(double community_used) {
        this.communityUsed = community_used;
    }

    public double getGridUsed() {
        return gridUsed;
    }

    public void setGridUsed(double grid_used) {
        this.gridUsed = grid_used;
    }
}
