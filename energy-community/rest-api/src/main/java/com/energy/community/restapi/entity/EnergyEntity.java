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
    private double community_produced;

    @Column(name="community_used")
    private double community_used;

    @Column(name="grid_used")
    private double grid_used;

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

    public double getCommunity_produced() {
        return community_produced;
    }

    public void setCommunity_produced(double community_produced) {
        this.community_produced = community_produced;
    }

    public double getCommunity_used() {
        return community_used;
    }

    public void setCommunity_used(double community_used) {
        this.community_used = community_used;
    }

    public double getGrid_used() {
        return grid_used;
    }

    public void setGrid_used(double grid_used) {
        this.grid_used = grid_used;
    }
}
