package com.tdd.app;

import java.time.LocalDate;

public class Period {
    private final LocalDate start;
    private final LocalDate end;

    public Period(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    boolean isSameYearMonth() {
        return getStart().equals(getEnd());
    }

    boolean isSameYear() {
        return (start.getYear() == end.getYear());
    }
}
