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

        if (period.isSameYearMonth()) {
            String currentYearMonth = String.format("%04d", period.getStartYear()) + String.format("%02d", period.getStartMonth());
            daysOfEachMonth.put(currentYearMonth, period.getEnd().getDayOfMonth() - period.getStart().getDayOfMonth() + 1);

        } else {
            if (period.isSameYear()) {
                for (int monthIndex = period.getStartMonth(); monthIndex <= period.getEndMonth(); monthIndex++) {
                    String currentYearMonth = String.format("%04d", period.getStartYear()) + String.format("%02d", monthIndex);
                    if (isSameMonth(monthIndex, period.getStartMonth())) {
                        daysOfEachMonth.put(currentYearMonth, YearMonth.from(period.getStart()).lengthOfMonth() - period.getStart().getDayOfMonth() + 1);
                    } else if (isSameMonth(monthIndex, period.getEndMonth())) {
                        daysOfEachMonth.put(currentYearMonth, period.getEnd().getDayOfMonth());
                    } else {
                        daysOfEachMonth.put(currentYearMonth, YearMonth.of(period.getStartYear(), monthIndex).lengthOfMonth());
                    }
                }

            } else {
                for (int yearIndex = period.getStartYear(); yearIndex <= period.getEnd().getYear(); yearIndex++) {
                    if (yearIndex == period.getStartYear()) {
                        for (int monthIndex = period.getStartMonth(); monthIndex <= 12; monthIndex++) {
                            String currentYearMonth = String.format("%04d", yearIndex) + String.format("%02d", monthIndex);
                            if (isSameMonth(monthIndex, period.getStartMonth())) {
                                daysOfEachMonth.put(currentYearMonth, YearMonth.from(period.getStart()).lengthOfMonth() - period.getStart().getDayOfMonth() + 1);
                            } else {
                                daysOfEachMonth.put(currentYearMonth, YearMonth.of(yearIndex, monthIndex).lengthOfMonth());
                            }
                        }
                    } else if (yearIndex == period.getEnd().getYear()) {
                        for (int monthIndex = 1; monthIndex <= period.getEndMonth(); monthIndex++) {
                            String currentYearMonth = String.format("%04d", yearIndex) + String.format("%02d", monthIndex);
                            if (isSameMonth(monthIndex, period.getEndMonth())) {
                                daysOfEachMonth.put(currentYearMonth, period.getEnd().getDayOfMonth());
                            } else {
                                daysOfEachMonth.put(currentYearMonth, YearMonth.of(yearIndex, monthIndex).lengthOfMonth());
                            }
                        }
                    } else {
                        for (int monthIndex = 1; monthIndex <= 12; monthIndex++) {
                            String currentYearMonth = String.format("%04d", yearIndex) + String.format("%02d", monthIndex);
                            if (isSameMonth(monthIndex, period.getEndMonth())) {
                                daysOfEachMonth.put(currentYearMonth, period.getEnd().getDayOfMonth());
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


}
