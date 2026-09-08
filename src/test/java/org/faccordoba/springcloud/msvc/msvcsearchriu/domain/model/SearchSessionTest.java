package org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SearchSessionTest {
    private String searchId;
    private String hotelId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private List<Integer> ages;

    @BeforeEach
    void setUp() {
        searchId = UUID.randomUUID().toString().substring(0, 7);
        hotelId = "12ert";
        checkIn = LocalDate.of(2024, 6, 1);
        checkOut = LocalDate.of(2024, 6, 5);
        ages = List.of(25, 30);
    }

    @AfterEach
    void tearDown() {
        searchId = null;
        hotelId = null;
        checkIn = null;
        checkOut = null;
        ages = null;
    }

    @Test
    public void newSearchSessionShouldBeCreated() {
        SearchSession session = new SearchSession(searchId,
                hotelId,
                checkIn,
                checkOut,
                ages,
                null
        );
        assertThat(session.sessionId())
                .isEqualTo(searchId);
        assertThat(session.hotelId())
                .isEqualTo(hotelId);
        assertThat(session.checkIn())
                .isEqualTo(checkIn);
        assertThat(session.checkOut())
                .isEqualTo(checkOut);
        assertThat(session.ages())
                .isEqualTo(List.of(25, 30));
        assertThat(session.count())
                .isEqualTo(0);
    }



    @Test
    public void newSearchSessionShouldBeCreatedWhenCountIsNotNull() {
        SearchSession session = new SearchSession(searchId,
                hotelId,
                checkIn,
                checkOut,
                ages,
                1
        );
        assertThat(session.sessionId())
                .isEqualTo(searchId);
        assertThat(session.hotelId())
                .isEqualTo(hotelId);
        assertThat(session.checkIn())
                .isEqualTo(checkIn);
        assertThat(session.checkOut())
                .isEqualTo(checkOut);
        assertThat(session.ages())
                .isEqualTo(List.of(25, 30));
        assertThat(session.count())
                .isEqualTo(1);
    }


    @Test
    public void newSearchSessionShouldThrowExceptionWhenSessionIdIsNull() {
        assertThatThrownBy(() -> {
            SearchSession session = new SearchSession(null,
                    hotelId,
                    checkIn,
                    checkOut,
                    ages,
                    null
            );
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Session ID cannot be null or empty");
    }

    @Test
    public void newSearchSessionShouldThrowExceptionWhenSessionIdIsEmpty() {
        assertThatThrownBy(() -> {
            SearchSession session = new SearchSession("",
                    hotelId,
                    checkIn,
                    checkOut,
                    ages,
                    null
            );
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Session ID cannot be null or empty");
    }


    @Test
    public void newSearchSessionShouldThrowExceptionWhenHotelIdIsNull() {
        assertThatThrownBy(() -> {
            SearchSession session = new SearchSession(searchId,
                    null,
                    checkIn,
                    checkOut,
                    ages,
                    null
            );
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Hotel ID cannot be null or empty");
    }

    @Test
    public void newSearchSessionShouldThrowExceptionWhenHotelIdIsEmpty() {
        assertThatThrownBy(() -> {
            SearchSession session = new SearchSession(searchId,
                    "",
                    checkIn,
                    checkOut,
                    ages,
                    null
            );
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Hotel ID cannot be null or empty");
    }


    @Test
    public void newSearchSessionShouldThrowExceptionWhenCheckInIsNull() {
        assertThatThrownBy(() -> {
            SearchSession session = new SearchSession(searchId,
                    hotelId,
                    null,
                    checkOut,
                    ages,
                    null
            );
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dates cannot be null");
    }

    @Test
    public void newSearchSessionShouldThrowExceptionWhenCheckOutIsNull() {
        assertThatThrownBy(() -> {
            SearchSession session = new SearchSession(searchId,
                    hotelId,
                    checkIn,
                    null,
                    ages,
                    null
            );
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dates cannot be null");
    }

    @Test
    public void newSearchSessionShouldThrowExceptionWhenCheckInIsEqualsToCheckOut() {
        assertThatThrownBy(() -> {
            SearchSession session = new SearchSession(searchId,
                    hotelId,
                    checkIn,
                    checkIn,
                    ages,
                    null
            );
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CheckIn must be before CheckOut");
    }

    @Test
    public void newSearchSessionShouldThrowExceptionWhenCheckInIsAfterCheckOut() {
        assertThatThrownBy(() -> {
            SearchSession session = new SearchSession(searchId,
                    hotelId,
                    checkOut,
                    checkIn,
                    ages,
                    null
            );
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CheckIn must be before CheckOut");
    }


    @Test
    public void newSearchSessionShouldThrowExceptionWhenAgesIsNull() {
        assertThatThrownBy(() -> {
            SearchSession session = new SearchSession(searchId,
                    hotelId,
                    checkIn,
                    checkOut,
                    null,
                    null
            );
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ages cannot be null or empty");
    }


    @Test
    public void newSearchSessionShouldThrowExceptionWhenAgesIsEmpty() {
        assertThatThrownBy(() -> {
            SearchSession session = new SearchSession(searchId,
                    hotelId,
                    checkIn,
                    checkOut,
                    List.of(),
                    null
            );
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ages cannot be null or empty");
    }


    @Test
    public void newSearchSessionShouldThrowExceptionWhenAgesContainsValuesNotPositive() {
        assertThatThrownBy(() -> {
            SearchSession session = new SearchSession(searchId,
                    hotelId,
                    checkIn,
                    checkOut,
                    List.of(-1,20,0),
                    null
            );
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ages must be >= 0");
    }


}