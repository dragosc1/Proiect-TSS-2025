package org.example;

import org.junit.contrib.java.lang.system.StandardOutputStreamLog;

import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class BankAccountDistributorTest {

    private BankAccountDistributor distributor;

    @Rule
    public final StandardOutputStreamLog log = new StandardOutputStreamLog();

    @Before
    public void setUp() {
        distributor = new BankAccountDistributor();
        distributor.addUser(1);
        distributor.addSpendingAccount(1, "Rent", 0.0, 40.0);
        distributor.addSpendingAccount(1, "Food", 0.0, 30.0);
        distributor.addSpendingAccount(1, "Entertainment", 0.0, 20.0);
        distributor.addMoneyToSavingAccount(1, 100.0);
    }

    // Functional testing

    // a) Equivalence partitioning
    // 5 classes of equivalence
    /*
     *  i  – account existence
     *      i1  { i | account does not exist (not in map) }
     *      i2  { i | account exists }
     *
     *  a  – amount value
     *      a1  < 0          (negative amount)
     *      a2  0
     *      a3  > 0          (positive amount)
     *
     *  p  – sum of percentages
     *      p1  1…99%     (spending<100%)
     *      p2  100%
     *      p3  >100% X (excluded because other function is managing this)
     *
     */
    @Test
    public void equivalencePartitioning() {
        // 1. accountId invalid => Error message (i1, _, _)
        distributor.distributeMoney(404, 100.0, "Invalid input");
        assertTrue(log.getLog().contains("Error: Account 404 does not exist"));

        // 2. negative amount => Error message (i2, a1, _)
        distributor.distributeMoney(1, -50.0, "Negative amount");
        assertTrue(log.getLog().contains("Error: Amount must be greater than zero."));

        // 3. amount = 0 => Error message (i2, a2, _)
        distributor.distributeMoney(1, 0.0, "0 amount");
        assertTrue(log.getLog().contains("Error: Amount must be greater than zero."));

        // 4. Partial Spending percentage (< 100%) => Savings  (i2, a3, p1)
        double prev = distributor.getSavingsForAccount(1);
        distributor.distributeMoney(1, 100.0, "Partial spending");
        double now = distributor.getSavingsForAccount(1);
        assertTrue(now > prev);

        // 5. Full Spending percentage (100%) => No Savings (i2, a3, p2)
        distributor.addUser(2);
        distributor.addSpendingAccount(2, "Car", 0.0, 50.0);
        distributor.addSpendingAccount(2, "Housing", 0.0, 50.0);
        distributor.distributeMoney(2, 200.0, "Full spending");
        assertTrue(log.getLog().contains("No savings for account 2"));
    }

    // b) Boundary values analysis
    /*
     *  i  – account existence
     *      i1  { i | account does not exist (not in map) }
     *      i2  { i | account exists }
     *
     *  a  – amount value
     *
     *      a1  < 0          (negative amount)
     *      a2  0
     *      a2 0.01          (amount just over 0) - BOUNDARY CHECK
     *      a3  > 0.01       (positive amount)
     *
     *  p  – sum of percentages
     *      p1  0%        (0% total spending) - BOUNDARY CHECK
     *      p1  1…98%     (spending<99%)
     *      p2  99%       (spending just under full) - BOUNDARY CHECK
     *      p2  100%      - BOUNDARY CHECK
     *      p3  >100% (excluded because other function is managing this)
     *
     */
    @Test
    public void boundaryValuesAnalysis() {
        // CASE 1. 0% total spending
        distributor.addUser(3);
        double amountZeroSpending = 100.0;
        distributor.distributeMoney(3, amountZeroSpending, "0% spending");
        assertEquals("Entire amount should be saved", amountZeroSpending, distributor.getSavingsForAccount(3), 0.0001);

        // CASE 2. 99% total spending
        distributor.addUser(4);
        distributor.addSpendingAccount(4, "Just under full", 0.0, 99.0);
        double amountAlmostFull = 100.0;
        distributor.distributeMoney(4, amountAlmostFull, "99% spending");
        double expectedSavings = amountAlmostFull * 0.01;
        assertEquals("Only 1% should go to savings", expectedSavings, distributor.getSavingsForAccount(4), 0.0001);

        // CASE 3. 100% spending
        distributor.addUser(5);
        distributor.addSpendingAccount(5, "Full", 0.0, 100.0);
        distributor.distributeMoney(5, 120.0, "100% spending");
        assertTrue(log.getLog().contains("No savings for account 5"));

        // CASE 4: amount just over 0
        double savingsBefore = distributor.getSavingsForAccount(1);
        distributor.distributeMoney(1, 0.01, "smallest positive amount");
        double savingsAfter = distributor.getSavingsForAccount(1);
        assertTrue("Savings should increase", savingsAfter > savingsBefore);
    }

    enum Outcome { ACCOUNT_ERR, AMOUNT_ERR, PERCENT_ERR, ALL_SAVED, PARTIAL_SAVED, NO_SAVED }

    static class CPCase {
        int accountId;
        boolean createAccount;
        double amount;
        double[] percentages;
        Outcome outcome;
        double expectedSavings;
        String note;

        CPCase(int accountId, boolean createAccount, double amount, double[] percentages, Outcome outcome, double expectedSavings, String note) {
            this.accountId = accountId;
            this.createAccount = createAccount;
            this.amount = amount;
            this.percentages = percentages;
            this.outcome = outcome;
            this.expectedSavings = expectedSavings;
            this.note = note;
        }
    }

    static class Identificator {
        int id;
        public Identificator(int id) {
            this.id = id;
        }
    }

    public int nextId(Identificator ID) {
        ID.id += 1;
        return ID.id;
    }

    // c) Category partitioning
    /*
     *  2  Categories and alternatives
     *
     *  i  – account existence
     *      i1  { i | account does not exist (not in map) }
     *      i2  { i | account exists }
     *
     *  a  – amount value
     *      a1  < 0          (negative amount)
     *      a2  0
     *      a3  eps          (minimal positive value, e.g.0.01)
     *      a4  M            ("medium" value, e.g.100)
     *      a5  L            (large value, e.g.1000000)
     *
     *  p  – sum of percentages
     *      p1  0%          (no spending accounts)
     *      p2  1…99%     (spending<100%)
     *      p3  100%
     *      p4  >100%
     *
     *  c  – number of categories
     *      c1  0            (savings‑only account)
     *      c2  1            (single spending account)
     *      c3  2…n        (at least two)
     */

    @Test
    public void categoryPartitioning() {
        List<CPCase> cases = new ArrayList<>();
        Identificator ID = new Identificator(10);

        // i1
        cases.add(new CPCase(404, false, 100, new double[]{}, Outcome.ACCOUNT_ERR, 0, "Cat1"));
        // i3 + a1
        cases.add(new CPCase(nextId(ID), true, -50, new double[]{}, Outcome.AMOUNT_ERR, 0, "Cat2"));
        // i3 + a2
        cases.add(new CPCase(nextId(ID), true, 0, new double[]{}, Outcome.AMOUNT_ERR, 0, "Cat3"));

        // ALL_SAVED (p=0%)
        cases.add(new CPCase(nextId(ID), true, 0.01, new double[]{}, Outcome.ALL_SAVED, 0.01, "Cat4"));
        cases.add(new CPCase(nextId(ID), true, 100, new double[]{}, Outcome.ALL_SAVED, 100, "Cat5"));
        cases.add(new CPCase(nextId(ID), true, 1000000, new double[]{}, Outcome.ALL_SAVED, 1000000, "Cat6"));

        // PARTIAL_SAVED (p<100%)
        cases.add(new CPCase(nextId(ID), true, 0.01, new double[]{10}, Outcome.PARTIAL_SAVED, 0.009, "Cat7"));
        cases.add(new CPCase(nextId(ID), true, 100, new double[]{10}, Outcome.PARTIAL_SAVED, 90, "Cat8"));
        cases.add(new CPCase(nextId(ID), true, 1000000, new double[]{30, 30}, Outcome.PARTIAL_SAVED, 400000, "Cat9"));
        cases.add(new CPCase(nextId(ID), true, 0.01, new double[]{75, 0, 0}, Outcome.PARTIAL_SAVED, 0.0025, "Cat10"));
        cases.add(new CPCase(nextId(ID), true, 100, new double[]{75, 0, 0}, Outcome.PARTIAL_SAVED, 25, "Cat11"));
        cases.add(new CPCase(nextId(ID), true, 1000000, new double[]{10}, Outcome.PARTIAL_SAVED, 900000, "Cat12"));

        // NO_SAVED (p=100%)
        cases.add(new CPCase(nextId(ID), true, 0.01, new double[]{100}, Outcome.NO_SAVED, 0, "Cat13"));
        cases.add(new CPCase(nextId(ID), true, 100, new double[]{100}, Outcome.NO_SAVED, 0, "Cat14"));
        cases.add(new CPCase(nextId(ID), true, 1000000, new double[]{100}, Outcome.NO_SAVED, 0, "Cat15"));
        cases.add(new CPCase(nextId(ID), true, 100, new double[]{50, 50, 0}, Outcome.NO_SAVED, 0, "Cat16"));
        cases.add(new CPCase(nextId(ID), true, 0.01, new double[]{40, 60}, Outcome.NO_SAVED, 0, "Cat17"));

        // PERCENT_ERR (p>100%)
        cases.add(new CPCase(nextId(ID), true, 0.01, new double[]{100.01}, Outcome.PERCENT_ERR, 0, "Cat18"));
        cases.add(new CPCase(nextId(ID), true, 100, new double[]{150}, Outcome.PERCENT_ERR, 0, "Cat19"));
        cases.add(new CPCase(nextId(ID), true, 1000000, new double[]{60, 60}, Outcome.PERCENT_ERR, 0, "Cat20"));

        for (CPCase categorycase : cases) {
            int startLog = log.getLog().length();

            if (categorycase.createAccount) {
                distributor.addUser(categorycase.accountId);
                int catIdx = 1;
                for (double pct : categorycase.percentages) {
                    distributor.addSpendingAccount(categorycase.accountId, "cat" + catIdx++, 0.0, pct);
                }
            }

            distributor.distributeMoney(categorycase.accountId, categorycase.amount, categorycase.note);

            String newLog = log.getLog().substring(startLog);

            switch (categorycase.outcome) {
                case ACCOUNT_ERR:
                    assertTrue("Expect account error for " + categorycase.note, newLog.contains("Account " + categorycase.accountId + " does not exist"));
                    break;
                case AMOUNT_ERR:
                    assertTrue("Expect amount error for " + categorycase.note, newLog.contains("Amount must be greater than zero"));
                    break;
                case PERCENT_ERR:
                    assertTrue("Expect percentage error for " + categorycase.note, newLog.contains("has a very large percentage"));
                    break;
                case NO_SAVED:
                    assertTrue("Expect no savings log for " + categorycase.note, newLog.contains("No savings for account " + categorycase.accountId));
                    assertEquals(0.0, distributor.getSavingsForAccount(categorycase.accountId), 0.0001);
                    break;
                case ALL_SAVED:
                    break;
                case PARTIAL_SAVED:
                    assertEquals("Incorrect savings for " + categorycase.note, categorycase.expectedSavings, distributor.getSavingsForAccount(categorycase.accountId), 0.0001);
                    break;
            }
        }
    }


    // -------- AUXILIARY TESTS FOR FULL COVERAGE ------ //
    @Test
    public void addSpendingAccountToNotExistingAccount() {
        distributor.addSpendingAccount(404, "Car", 0.0, 40.0);
        assertTrue(log.getLog().contains("Account 404 does not exist"));
    }

    @Test
    public void addMoneyToNotExistingSavingAccount() {
        distributor.addMoneyToSavingAccount(404, 100.0);
        assertTrue(log.getLog().contains("Account 404 does not exist"));
    }

    @Test
    public void addExistingSpendingAccount() {
        distributor.addSpendingAccount(1, "Rent", 0.0, 40.0);
        assertTrue(log.getLog().contains("Spending account Rent already exists"));
    }

    @Test
    public void addLargePercentageSpendingAccount() {
        distributor.addSpendingAccount(1, "Car", 100.0, 30.0);
        assertTrue(log.getLog().contains("Spending account Car has a very large percentage"));
    }
}
