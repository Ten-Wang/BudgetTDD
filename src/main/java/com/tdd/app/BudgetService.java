package com.tdd.app;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class BudgetService {

    private IBudgetRepo repo;

    public BudgetService(IBudgetRepo repo) {
        this.repo = repo;
    }

    public double query(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0D;
        }
        // 判斷時間
        if (end.isBefore(start)) {
            return 0D;
        }
        // 取得預算列表
        List<Budget> budgetList = repo.getAll();

        // 計算各月份日數
        Map<String, Integer> targets = calculateDaysOfEachMonth(new Period(start, end));
        System.out.println(targets);

        // 過濾預算
        AtomicReference<Double> amount = new AtomicReference<>(0D);
        budgetList.forEach(budget -> {
            Integer days = targets.get(budget.yearMonth);
            if (days != null) {
                int year = Integer.parseInt(budget.yearMonth.substring(0, 4));
                System.out.println("year" + year);
                int month = Integer.parseInt(budget.yearMonth.substring(4));
                System.out.println("month" + month);
                YearMonth yearMonth = YearMonth.of(year, month);
                int daysOfMonth = yearMonth.lengthOfMonth();
                amount.updateAndGet(v -> v + budget.amount * ((double) days / daysOfMonth));
            }
        });

        // 加總
        return amount.get();
    }

    private Map<String, Integer> calculateDaysOfEachMonth(Period period) {
        Map<String, Integer> daysOfEachMonth = new HashMap<>();

        if (isSameYearMonth(period.getStart(), period.getEnd())) {
            String currentYearMonth = String.format("%04d", period.getStart().getYear()) + String.format("%02d", period.getStart().getMonthValue());
            int daysOfCurrentMonth = period.getEnd().getDayOfMonth() - period.getStart().getDayOfMonth() + 1;
            daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
        } else {
            if (isSameYear(period.getStart().getYear(), period.getEnd().getYear())) {
                for (int monthIndex = period.getStart().getMonthValue(); monthIndex <= period.getEnd().getMonthValue(); monthIndex++) {
                    String currentYearMonth = String.format("%04d", period.getStart().getYear()) + String.format("%02d", monthIndex);
                    if (isSameMonth(monthIndex, period.getStart().getMonthValue())) {
                        daysOfEachMonth.put(currentYearMonth, YearMonth.from(period.getStart()).lengthOfMonth() - period.getStart().getDayOfMonth() + 1);
                    } else if (isSameMonth(monthIndex, period.getEnd().getMonthValue())) {
                        daysOfEachMonth.put(currentYearMonth, period.getEnd().getDayOfMonth());
                    } else {
                        daysOfEachMonth.put(currentYearMonth, YearMonth.of(period.getStart().getYear(), monthIndex).lengthOfMonth());
                    }
                }
            } else {
                for (int yearIndex = period.getStart().getYear(); yearIndex <= period.getEnd().getYear(); yearIndex++) {
                    if (isSameYear(yearIndex, period.getStart().getYear())) {
                        for (int monthIndex = period.getStart().getMonthValue(); monthIndex <= 12; monthIndex++) {
                            String currentYearMonth = String.format("%04d", yearIndex) + String.format("%02d", monthIndex);
                            if (isSameMonth(monthIndex, period.getStart().getMonthValue())) {
                                daysOfEachMonth.put(currentYearMonth, YearMonth.from(period.getStart()).lengthOfMonth() - period.getStart().getDayOfMonth() + 1);
                            } else {
                                daysOfEachMonth.put(currentYearMonth, YearMonth.of(yearIndex, monthIndex).lengthOfMonth());
                            }
                        }
                    } else if (isSameYear(yearIndex, period.getEnd().getYear())) {
                        for (int monthIndex = 1; monthIndex <= period.getEnd().getMonthValue(); monthIndex++) {
                            String currentYearMonth = String.format("%04d", yearIndex) + String.format("%02d", monthIndex);
                            if (isSameMonth(monthIndex, period.getEnd().getMonthValue())) {
                                int daysOfCurrentMonth = period.getEnd().getDayOfMonth();
                                daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
                            } else {
                                daysOfEachMonth.put(currentYearMonth, YearMonth.of(yearIndex, monthIndex).lengthOfMonth());
                            }
                        }
                    } else {
                        for (int monthIndex = 1; monthIndex <= 12; monthIndex++) {
                            String currentYearMonth = String.format("%04d", yearIndex) + String.format("%02d", monthIndex);
                            if (isSameMonth(monthIndex, period.getEnd().getMonthValue())) {
                                int daysOfCurrentMonth = period.getEnd().getDayOfMonth();
                                daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
                            } else {
                                daysOfEachMonth.put(currentYearMonth, YearMonth.of(yearIndex, monthIndex).lengthOfMonth());
                            }
                        }
                    }
                }
            }
        }

        return daysOfEachMonth;
    }

    private boolean isSameMonth(int month, int month2) {
        return month == month2;
    }

    private boolean isSameYear(int year, int year2) {
        return year == year2;
    }

    private boolean isSameYearMonth(LocalDate start, LocalDate end) {
        return start.equals(end);
    }
}
