package org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model;

import java.time.LocalDate;
import java.util.List;

public record SearchSession(
        String sessionId,
        String hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        List<Integer> ages,
        Integer count) {
    public SearchSession {
        if (sessionId == null || sessionId.isEmpty()) { throw new IllegalArgumentException("Session ID cannot be null or empty"); }
        if (hotelId == null || hotelId.isBlank()) throw new IllegalArgumentException("Hotel ID cannot be null or empty");
        if (checkIn == null || checkOut == null) throw new IllegalArgumentException("Dates cannot be null");
        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) throw new IllegalArgumentException("CheckIn must be before CheckOut");
        if (ages == null || ages.isEmpty()) throw new IllegalArgumentException("Ages cannot be null or empty");
        for (int age : ages) {
            if (age < 0) throw new IllegalArgumentException("Ages must be >= 0");
        }
        if(count == null)
            count = 0;
        ages = List.copyOf(ages);
    }
}
