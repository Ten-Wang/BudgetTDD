package com.tdd.app;

import java.time.LocalDate;
import java.util.List;

public class BudgetService {

    private IBudgetRepo repo;
    private List<Budget> budgets;

    public BudgetService(IBudgetRepo repo) {
        this.repo = repo;
    }

    public double query(LocalDate start, LocalDate end) {
        budgets = repo.getAll();
        if (start.isBefore(budgets.get(0).firstDay())) {
            return 0;
        } else if (budgets.size() > 0)
            return 1;
        else return 0;
    }

}
