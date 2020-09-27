package com.tdd.app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BudgetService {

    private IBudgetRepo repo;
    private List<Budget> Budget;

    public BudgetService(IBudgetRepo repo) {
        this.repo = repo;
    }

    public double query(LocalDate start, LocalDate end) {
        Budget = repo.getAll();
        if (Budget.size() > 0)
            return 1;
        else return 0;
    }
}
