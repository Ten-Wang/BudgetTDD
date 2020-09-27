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
        Map<String, Integer> targets = calculateDaysOfEachMonth(start, end);
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

    private Map<String, Integer> calculateDaysOfEachMonth(LocalDate start, LocalDate end) {
        Map<String, Integer> daysOfEachMonth = new HashMap<>();

        YearMonth startYearMonth = YearMonth.from(start);
        YearMonth endYearMonth = YearMonth.from(end);

        if (isSameYearMonth(start, end)) {
            String currentYearMonth = String.format("%04d", startYearMonth.getYear()) + String.format("%02d", start.getMonthValue());
            int daysOfCurrentMonth = end.getDayOfMonth() - start.getDayOfMonth() + 1;
            daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
        } else {
            if (startYearMonth.getYear() == endYearMonth.getYear()) {
                for (int monthIndex = start.getMonthValue(); monthIndex <= endYearMonth.getMonthValue(); monthIndex++) {
                    String currentYearMonth = String.format("%04d", startYearMonth.getYear()) + String.format("%02d", monthIndex);
                    if (monthIndex == start.getMonthValue()) {
                        int daysOfCurrentMonth = YearMonth.from(start).lengthOfMonth() - start.getDayOfMonth() + 1;
                        daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
                    } else if (monthIndex == endYearMonth.getMonthValue()) {
                        int daysOfCurrentMonth = end.getDayOfMonth();
                        daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
                    } else {
                        daysOfEachMonth.put(currentYearMonth, YearMonth.of(startYearMonth.getYear(), monthIndex).lengthOfMonth());
                    }
                }
            } else {
                for (int yearIndex = startYearMonth.getYear(); yearIndex <= end.getYear(); yearIndex++) {
                    if (yearIndex == startYearMonth.getYear()) {
                        for (int monthIndex = start.getMonthValue(); monthIndex <= 12; monthIndex++) {
                            String currentYearMonth = String.format("%04d", yearIndex) + String.format("%02d", monthIndex);
                            if (monthIndex == start.getMonthValue()) {
                                int daysOfCurrentMonth = YearMonth.from(start).lengthOfMonth() - start.getDayOfMonth() + 1;
                                daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
                            } else {
                                daysOfEachMonth.put(currentYearMonth, YearMonth.of(yearIndex, monthIndex).lengthOfMonth());
                            }
                        }
                    } else if (yearIndex == end.getYear()) {
                        for (int monthIndex = 1; monthIndex <= endYearMonth.getMonthValue(); monthIndex++) {
                            String currentYearMonth = String.format("%04d", yearIndex) + String.format("%02d", monthIndex);
                            if (monthIndex == endYearMonth.getMonthValue()) {
                                int daysOfCurrentMonth = end.getDayOfMonth();
                                daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
                            } else {
                                daysOfEachMonth.put(currentYearMonth, YearMonth.of(yearIndex, monthIndex).lengthOfMonth());
                            }
                        }
                    } else {
                        for (int monthIndex = 1; monthIndex <= endYearMonth.getMonthValue(); monthIndex++) {
                            String currentYearMonth = String.format("%04d", yearIndex) + String.format("%02d", monthIndex);
                            if (monthIndex == endYearMonth.getMonthValue()) {
                                int daysOfCurrentMonth = end.getDayOfMonth();
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

    private boolean isSameYearMonth(LocalDate start, LocalDate end) {
        return start.equals(end);
    }
}
