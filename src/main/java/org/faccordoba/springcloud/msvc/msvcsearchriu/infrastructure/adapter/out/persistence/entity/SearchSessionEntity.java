package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.entity;


import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "SEARCH_SESSIONS")
public class SearchSessionEntity {

    @Id
    @Column(name = "SEARCH_ID", length = 36, nullable = false)
    private String searchId;

    @Column(name = "HOTEL_ID", length = 8, nullable = false)
    private String hotelId;

    @Column(name = "CHECK_IN", nullable = false)
    private LocalDate checkIn;

    @Column(name = "CHECK_OUT", nullable = false)
    private LocalDate checkOut;

    @Column(name = "AGES_SEQUENCE", length = 255, nullable = false)
    private String agesSequence;

    @Column(name = "VISIT_COUNT", nullable = false)
    private Integer visitCount;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    public  SearchSessionEntity() {

    }

    public SearchSessionEntity(String searchId, String hotelId, LocalDate checkIn, LocalDate checkOut,
                               String agesSequence, Integer visitCount) {
        this.searchId = searchId;
        this.hotelId = hotelId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.agesSequence = agesSequence;
        this.visitCount = visitCount;
    }

    @PrePersist
    protected void onCreate() {
        ZoneId zoneId = ZoneId.systemDefault();
        this.createdAt = LocalDateTime.now(zoneId);
        this.updatedAt = LocalDateTime.now(zoneId);
    }

    @PreUpdate
    protected void onUpdate() {
        ZoneId zoneId = ZoneId.systemDefault();
        this.updatedAt = LocalDateTime.now(zoneId);
    }

    public String getSearchId() {
        return searchId;
    }

    public String getHotelId() {
        return hotelId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public String getAgesSequence() {
        return agesSequence;
    }

    public Integer getVisitCount() {
        return visitCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}