package com.tdd.app;

import junit.framework.TestCase;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class BudgetServiceTest extends TestCase {

    private IBudgetRepo repo = Mockito.mock(IBudgetRepo.class);
    private BudgetService budgetService = new BudgetService(repo);

    @Override
    public void setUp() throws Exception {
        super.setUp();
        budgetService = new BudgetService(repo);
    }

    @Test
    public void no_budgets() {
        givenBudgets();
        totalAmountShouldBe(0,
                LocalDate.of(2020, 4, 1),
                LocalDate.of(2020, 4, 1));
    }

    private void totalAmountShouldBe(double expected, LocalDate start, LocalDate end) {
        assertEquals(expected, budgetService.query(
                start,
                end));
    }

    private void givenBudgets(Budget... budgets) {
        when(repo.getAll()).thenReturn(
                Arrays.asList(budgets));
    }

    @Test
    public void period_inside_budget_month() {
        givenBudgets(new Budget("202004", 30));
        totalAmountShouldBe(1,
                LocalDate.of(2020, 4, 1),
                LocalDate.of(2020, 4, 1));
    }

    @Test
    public void period_without_overlap_before_budget_first_day() {
        givenBudgets(new Budget("202004", 30));
        totalAmountShouldBe(0,
                LocalDate.of(2020, 3, 31),
                LocalDate.of(2020, 3, 31));
   }
}