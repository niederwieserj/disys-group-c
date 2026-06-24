package com.energy.community.user;

import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class EnergyUsageGenerator {

    public double generateKwhForCurrentTime() {
        int hour = LocalTime.now().getHour();

        if (isMorningPeak(hour)) {
            return randomRoundedValue(0.006, 0.018);
        }
        if (isEveningPeak(hour)) {
            return randomRoundedValue(0.008, 0.025);
        }
        if (isNight(hour)) {
            return randomRoundedValue(0.001, 0.004);
        }
        return randomRoundedValue(0.003, 0.010);
    }

    private boolean isMorningPeak(int hour) {
        return hour >= 6 && hour <= 9;
    }

    private boolean isEveningPeak(int hour) {
        return hour >= 17 && hour <= 22;
    }

    private boolean isNight(int hour) {
        return hour >= 0 && hour <= 5;
    }

    private double randomRoundedValue(double minInclusive, double maxExclusive) {
        double value = ThreadLocalRandom.current().nextDouble(minInclusive, maxExclusive);
        return Math.round(value * 1000.0) / 1000.0;
    }
}
