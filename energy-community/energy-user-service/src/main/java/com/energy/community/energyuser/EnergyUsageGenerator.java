package com.energy.community.energyuser;

import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class EnergyUsageGenerator {

    public double generateKwh() {
        int hour = LocalTime.now().getHour();

        if (hour >= 6 && hour <= 9) {
            return randomKwh(0.008, 0.020);
        }

        if (hour >= 17 && hour <= 22) {
            return randomKwh(0.008, 0.020);
        }

        if (hour >= 0 && hour <= 5) {
            return randomKwh(0.001, 0.003);
        }

        return randomKwh(0.003, 0.008);
    }

    private double randomKwh(double min, double max) {
        double value = ThreadLocalRandom.current().nextDouble(min, max);
        return Math.round(value * 1000.0) / 1000.0;
    }
}