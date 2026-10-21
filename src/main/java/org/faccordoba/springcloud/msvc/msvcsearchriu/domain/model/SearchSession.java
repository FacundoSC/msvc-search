package org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model;

import io.netty.util.internal.StringUtil;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public record SearchSession(
        String sessionId,
        String hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        List<Integer> ages,
        Integer count) {
    public SearchSession {
        if (StringUtil.isNullOrEmpty(sessionId)) { throw new IllegalArgumentException("Session ID cannot be null or empty"); }
        if (StringUtil.isNullOrEmpty(hotelId)) throw new IllegalArgumentException("Hotel ID cannot be null or empty");
        if (checkIn == null || checkOut == null) throw new IllegalArgumentException("Dates cannot be null");
        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        if (checkIn.isBefore(now)) throw new IllegalArgumentException("CheckIn cannot be in the past");
        if (checkOut.isBefore(now)) throw new IllegalArgumentException("CheckOut cannot be in the past");
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
