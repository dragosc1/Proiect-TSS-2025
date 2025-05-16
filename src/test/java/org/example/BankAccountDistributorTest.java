package org.example;


import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class BankAccountDistributorTest {

    private BankAccountDistributor distributor;

    @Rule
    public final SystemOutRule log = new SystemOutRule().enableLog();

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
    // 6 classes of equivalence
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
     *  s  – description contains the keyword "SAVE"
     *      s1  { desc with "SAVE" }
     *      s2  { desc without "SAVE" }
     */
    @Test
    public void FunctionalTesting_1_equivalencePartitioning() {
        // 1. accountId invalid => Error message (i1, _, _, _)
        int start = log.getLog().length();
        distributor.distributeMoney(404, 100.0, "Invalid input");
        String out1 = log.getLog().substring(start);
        assertTrue(out1.contains("Error: Account 404 does not exist"));

        // 2. negative amount => Error message (i2, a1, _, _)
        start = log.getLog().length();
        distributor.distributeMoney(1, -50.0, "Negative amount");
        String out2 = log.getLog().substring(start);
        assertTrue(out2.contains("Error: Amount must be greater than zero."));

        // 3. amount = 0 => Error message (i2, a2, _, _)
        start = log.getLog().length();
        distributor.distributeMoney(1, 0.0, "0 amount");
        String out3 = log.getLog().substring(start);
        assertTrue(out3.contains("Error: Amount must be greater than zero."));

        // 4. Partial Spending percentage (< 100%) => Savings  (i2, a3, p1, s1)
        double prev_SAVE = distributor.getSavingsForAccount(1);
        distributor.distributeMoney(1, 100.0, "Partial spending SAVE");
        double now_SAVE = distributor.getSavingsForAccount(1);
        assertTrue(now_SAVE > prev_SAVE);

        // 5. Partial Spending percentage (< 100%) NO SAVE => No Savings  (i2, a3, p1, s2)
        double prev_NO_SAVE = distributor.getSavingsForAccount(1);
        distributor.distributeMoney(1, 100.0, "Partial spending");
        double now_NO_SAVE = distributor.getSavingsForAccount(1);
        assertTrue(prev_NO_SAVE == now_NO_SAVE);

        // 6. Full Spending percentage (100%) => No Savings (i2, a3, p2, _)
        distributor.addUser(2);
        distributor.addSpendingAccount(2, "Car", 0.0, 50.0);
        distributor.addSpendingAccount(2, "Housing", 0.0, 50.0);
        start = log.getLog().length();
        distributor.distributeMoney(2, 200.0, "Full spending");
        String out4 = log.getLog().substring(start);
        assertTrue(out4.contains("No savings for account 2"));
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
     *   s  – description contains the keyword "SAVE"
     *      s1  { desc with "SAVE" }
     *      s2  { desc without "SAVE" }
     */
    @Test
    public void FunctionalTesting_2_boundaryValuesAnalysis() {
        // CASE 1. 0% total spending
        distributor.addUser(3);
        double amountZeroSpending = 100.0;
        distributor.distributeMoney(3, amountZeroSpending, "0% spending SAVE");
        assertEquals("Entire amount should be saved", amountZeroSpending, distributor.getSavingsForAccount(3), 0.0001);

        // CASE 2. 99% total spending
        distributor.addUser(4);
        distributor.addSpendingAccount(4, "Just under full", 0.0, 99.0);
        double amountAlmostFull = 100.0;
        distributor.distributeMoney(4, amountAlmostFull, "99% spending SAVE");
        double expectedSavings = amountAlmostFull * 0.01;
        assertEquals("Only 1% should go to savings", expectedSavings, distributor.getSavingsForAccount(4), 0.0001);

        // CASE 3. 100% spending
        distributor.addUser(5);
        distributor.addSpendingAccount(5, "Full", 0.0, 100.0);
        distributor.distributeMoney(5, 120.0, "100% spending");
        assertTrue(log.getLog().contains("No savings for account 5"));

        // CASE 4: amount just over 0
        double savingsBefore = distributor.getSavingsForAccount(1);
        distributor.distributeMoney(1, 0.01, "smallest positive amount SAVE");
        double savingsAfter = distributor.getSavingsForAccount(1);
        assertTrue("Savings should increase", savingsAfter > savingsBefore);
    }

    enum Outcome { ACCOUNT_ERR, AMOUNT_ERR, PERCENT_ERR, ALL_SAVED, PARTIAL_SAVED, NO_SAVED }

    static class CPCase {
        int      accountId;
        boolean  createAccount;
        double   amount;
        double[] percentages;
        boolean  saveFlag;
        Outcome  outcome;
        double   expectedSavings;
        String   note;

        CPCase(int accountId, boolean createAccount, double amount, double[] percentages, boolean saveFlag, Outcome outcome, double expectedSavings, String note) {
            this.accountId = accountId;
            this.createAccount = createAccount;
            this.amount = amount;
            this.percentages = percentages;
            this.saveFlag = saveFlag;
            this.outcome = outcome;
            this.expectedSavings = expectedSavings;
            this.note = note;
        }

        String description() { return (saveFlag ? "Case SAVE" : "Case") + accountId; }
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
     *  4  Categories
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
     *  s  – description contains the keyword "SAVE"
     *      s1  { desc with "SAVE" }
     *      s2  { desc without "SAVE" }
     */

    @Test
    public void FunctionalTesting_3_categoryPartitioning() {
        List<CPCase> cases = new ArrayList<>();
        Identificator ID = new Identificator(10);

        // i1 - Account does not exist
        cases.add(new CPCase(404, false, 100,   new double[]{}, false, Outcome.ACCOUNT_ERR, 0, "Cat1"));
        // i2 + a1 - Amount is negative
        cases.add(new CPCase(nextId(ID),  true, -75,       new double[]{}, false, Outcome.AMOUNT_ERR, 0, "Cat2"));
        // i2 + a2 - Amount is 0
        cases.add(new CPCase(nextId(ID), true, 0,         new double[]{}, false, Outcome.AMOUNT_ERR, 0, "Cat3"));

        // ALL_SAVED (p=0%)
        cases.add(new CPCase(nextId(ID), true, 0.01,      new double[]{}, true,  Outcome.ALL_SAVED,   0.01,  "Cat4 SAVE")); // (a3,p1,s1)
        cases.add(new CPCase(nextId(ID), true, 100,       new double[]{}, false, Outcome.NO_SAVED,    0,     "Cat5"));      // (a4,p1,s2)
        cases.add(new CPCase(nextId(ID), true, 100,       new double[]{}, true,  Outcome.ALL_SAVED,   100,   "Cat6 SAVE")); // (a4,p1,s1)
        cases.add(new CPCase(nextId(ID), true, 1000000,   new double[]{}, true,  Outcome.ALL_SAVED,   1_000_000, "Cat7 SAVE")); // (a5,p1,s1)

        // PARTIAL_SAVED (p<100%)
        cases.add(new CPCase(nextId(ID), true, 0.001,      new double[]{80}, false, Outcome.NO_SAVED,       0,      "Cat8"));       // (a3,p2,s2)
        cases.add(new CPCase(nextId(ID), true, 0.001,      new double[]{80}, true,  Outcome.PARTIAL_SAVED,  0.002,  "Cat9 SAVE"));  // (a3,p2,s1)
        cases.add(new CPCase(nextId(ID), true, 100,       new double[]{80}, false, Outcome.NO_SAVED,       0,      "Cat10"));      // (a4,p2,s2)
        cases.add(new CPCase(nextId(ID), true, 100,       new double[]{80}, true,  Outcome.PARTIAL_SAVED,  20,     "Cat11 SAVE")); // (a4,p2,s1)
        cases.add(new CPCase(nextId(ID), true, 1000000,   new double[]{30,30}, false, Outcome.NO_SAVED,    0,      "Cat12"));      // (a5,p2,s2)
        cases.add(new CPCase(nextId(ID), true, 1000000,   new double[]{30,30}, true,  Outcome.PARTIAL_SAVED,400_000,"Cat13 SAVE"));// (a5,p2,s1)

        // NO_SAVED (p=100%)
        cases.add(new CPCase(nextId(ID), true, 0.001,      new double[]{100},      false, Outcome.NO_SAVED, 0, "Cat14"));
        cases.add(new CPCase(nextId(ID), true, 0.001,      new double[]{100},      true,  Outcome.NO_SAVED, 0, "Cat15 SAVE"));
        cases.add(new CPCase(nextId(ID), true, 200,       new double[]{60,40},    false, Outcome.NO_SAVED, 0, "Cat16"));
        cases.add(new CPCase(nextId(ID), true, 200,       new double[]{60,40},    true,  Outcome.NO_SAVED, 0, "Cat17 SAVE"));

        // PERCENT_ERR (p>100%)
        cases.add(new CPCase(nextId(ID), true, 0.001,      new double[]{100.1},    false, Outcome.PERCENT_ERR, 0, "Cat18"));
        cases.add(new CPCase(nextId(ID), true, 100,       new double[]{150},      true,  Outcome.PERCENT_ERR, 0, "Cat19 SAVE"));
        cases.add(new CPCase(nextId(ID), true, 1000000,   new double[]{60,60},    false, Outcome.PERCENT_ERR, 0, "Cat20"));

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

    // Mutation Testing Killing Println
    @Test
    public void MutationTesting_1_testDistributeMoneyPrintMessage() {
        distributor.distributeMoney(1, 120.0, "Mutation testing");
        assertTrue(log.getLog().contains("Distributing $120.0 for account 1 [Mutation testing]"));
    }

    // Mutation testing Killing Println
    @Test
    public void MutationTesting_2_testSavingNumberChanged() {
        distributor.distributeMoney(1, 100.0, "Mutation testing SAVE");
        assertTrue(log.getLog().contains("Remaining money of $10.0 added to savings account for account 1"));
    }

    // -------- AUXILIARY TESTS FOR FULL COVERAGE ------ //
    @Test
    public void aux_addSpendingAccountToNotExistingAccount() {
        distributor.addSpendingAccount(404, "Car", 0.0, 40.0);
        assertTrue(log.getLog().contains("Account 404 does not exist"));
    }

    @Test
    public void aux_addMoneyToNotExistingSavingAccount() {
        distributor.addMoneyToSavingAccount(404, 100.0);
        assertTrue(log.getLog().contains("Account 404 does not exist"));
    }

    @Test
    public void aux_addExistingSpendingAccount() {
        distributor.addSpendingAccount(1, "Rent", 0.0, 40.0);
        assertTrue(log.getLog().contains("Spending account Rent already exists"));
    }

    @Test
    public void aux_addLargePercentageSpendingAccount() {
        distributor.addSpendingAccount(1, "Car", 100.0, 30.0);
        assertTrue(log.getLog().contains("Spending account Car has a very large percentage"));
    }

    //STRUCTURAL TESTING
    //a) statement testing
    // 1) entry accountId = 404, amount = 100, description = "Invalid id"
    //    instructions covered -> 1, 2..3, 42
    // 2) entry accountId = 1, amount = -1, description = "Negative amount"
    //    instructions covered -> 1, 4..5, 6, 7..8
    // 3) entry accountId = 1, amount = 100, description = "SAVE THE EXTRA"
    //    instructions covered -> 1, 4..5, 6, 9..15, 16, 17..19, 20, 21, 22..27, 28..30, 31, 34..40, 41, 42
    // 4) entry accountId = 1, amount = 100, description = "No saving"
    //    instructions covered -> 1, 4..5, 6, 9..15, 16, 17..19, 20, 21, 22..27, 28..30, 31, 32..33, 41, 42

    @Test
    public void StructuralTesting_A_StatementCoverage() {
        int start = log.getLog().length();
        distributor.distributeMoney(404, 100, "Invalid id");
        String out1 = log.getLog().substring(start);
        assertTrue(out1.contains("Error: Account 404 does not exist"));

        start = log.getLog().length();
        distributor.distributeMoney(1, -1, "Negative amount");
        String out2 = log.getLog().substring(start);
        assertTrue(out2.contains("Error: Amount must be greater than zero."));

        double prev_SAVE = distributor.getSavingsForAccount(1);
        distributor.distributeMoney(1, 100.0, "SAVE THE EXTRA");
        double now_SAVE = distributor.getSavingsForAccount(1);
        assertTrue(now_SAVE > prev_SAVE);

        start = log.getLog().length();
        distributor.distributeMoney(1, 100, "No saving");
        String out3 = log.getLog().substring(start);
        assertTrue(out3.contains("No savings for account 1"));
    }

    //b) Decision coverage
    //decisions:
    //    (1) if (!spendingAccounts.containsKey(accountId))
    //    (2) if (amount <= 0)
    //    (3) for (Map.Entry<String, AbstractMap.SimpleEntry<Double, Double>> entry : spendingMap.entrySet())
    //    (4) for (Map.Entry<String, AbstractMap.SimpleEntry<Double, Double>> entry : spendingMap.entrySet())
    //    (5) if (totalPercentage > 100 - EPS || !hasSaveFlag)
    //
    // accountId |  amount  |   description     | spend % | (1) | (2) | (3) | (4) | (5)
    //     404   |   100    |   "Invalid id"    |    -    |  T  |  -  |  -  |  -  |  -
    //      1    |   -1     | "Negative amount" |   90%   |  F  |  T  |  -  |  -  |  -
    //      1    |   100    |    "No saving"    |   90%   |  F  |  F  |  T  |  T  |  T
    //      2    |   100    |       "SAVE"      |    0%   |  F  |  F  |  F  |  F  |  F
    @Test
    public void StructuralTesting_B_DecisionCoverage() {
        int start = log.getLog().length();
        distributor.distributeMoney(404, 100, "Invalid id");
        String out1 = log.getLog().substring(start);
        assertTrue(out1.contains("Error: Account 404 does not exist"));

        start = log.getLog().length();
        distributor.distributeMoney(1, -1, "Negative amount");
        String out2 = log.getLog().substring(start);
        assertTrue(out2.contains("Error: Amount must be greater than zero."));

        start = log.getLog().length();
        distributor.distributeMoney(1, 100, "No saving");
        String out3 = log.getLog().substring(start);
        assertTrue(out3.contains("No savings for account 1"));

        //Add a new user with id = 2 for the test with no categories for condition (2) and (3)
        distributor.addUser(2);
        double prev_SAVE = distributor.getSavingsForAccount(2);
        distributor.distributeMoney(2, 100.0, "SAVE");
        double now_SAVE = distributor.getSavingsForAccount(2);
        assertTrue(now_SAVE > prev_SAVE);

    }

    //c) Condition coverage
    //decisions:
    //    (1) if (!spendingAccounts.containsKey(accountId))
    //    (2) if (amount <= 0)
    //    (3) for (Map.Entry<String, AbstractMap.SimpleEntry<Double, Double>> entry : spendingMap.entrySet())
    //    (4) for (Map.Entry<String, AbstractMap.SimpleEntry<Double, Double>> entry : spendingMap.entrySet())
    //    (5) if (totalPercentage > 100 - EPS || !hasSaveFlag)
    //
    //conditions:
    //    c1: !spendingAccounts.containsKey(accountId)
    //    c2: amount <= 0
    //    c3: Map.Entry<String, AbstractMap.SimpleEntry<Double, Double>> entry : spendingMap.entrySet()
    //    c4: Map.Entry<String, AbstractMap.SimpleEntry<Double, Double>> entry : spendingMap.entrySet()
    //    c5: totalPercentage > 100 - EPS
    //    c6: !hasSaveFlag
    //
    // accountId |  amount  |   description     | spend % | c1 | c2 | c3 | c4 | c5 | c6
    //     404   |   100    |   "Invalid id"    |    -    | T  | -  | -  | -  | -  | -
    //      1    |   -1     | "Negative amount" |   90%   | F  | T  | -  | -  | -  | -
    //      1    |   100    |    "No saving"    |   90%   | F  | T  | T  | T  | F  | T
    //      2    |   100    |       "SAVE"      |    0%   | F  | F  | F  | F  | F  | F
    //      2    |   100    |    "No saving"    |   100%  | F  | T  | T  | T  | T  | T
    //      2    |   100    |       "SAVE"      |   100%  | F  | T  | T  | T  | T  | F

    @Test
    public void StructuralTesting_C_ConditionCoverage() {
        int start = log.getLog().length();
        distributor.distributeMoney(404, 100, "Invalid id");
        String out1 = log.getLog().substring(start);
        assertTrue(out1.contains("Error: Account 404 does not exist"));

        start = log.getLog().length();
        distributor.distributeMoney(1, -1, "Negative amount");
        String out2 = log.getLog().substring(start);
        assertTrue(out2.contains("Error: Amount must be greater than zero."));

        start = log.getLog().length();
        distributor.distributeMoney(1, 100, "No saving");
        String out3 = log.getLog().substring(start);
        assertTrue(out3.contains("No savings for account 1"));

        //Add a new user with id = 2
        distributor.addUser(2);
        double prev_SAVE = distributor.getSavingsForAccount(2);
        distributor.distributeMoney(2, 100.0, "SAVE");
        double now_SAVE = distributor.getSavingsForAccount(2);
        assertTrue(now_SAVE > prev_SAVE);

        //Add new spending category for spending = 100%
        distributor.addSpendingAccount(2, "Japan trip", 0.0, 100.0);

        start = log.getLog().length();
        distributor.distributeMoney(2, 100, "No saving");
        String out4 = log.getLog().substring(start);
        assertTrue(out4.contains("No savings for account 2"));

        start = log.getLog().length();
        distributor.distributeMoney(2, 100, "SAVE");
        String out5 = log.getLog().substring(start);
        assertTrue(out5.contains("No savings for account 2"));
    }

    //Independent Circuit testing
    //n = 17
    //e = 22
    //V(G) = 6
    //Circuits:
    //    (1) 1, 2..3, 42, 1
    //    (2) 1, 4..5, 6, 7..8, 42, 1
    //    (3) 16, 17..19, 16
    //    (4) 21, 22..27, 21
    //    (5) 1, 4..5, 6, 9-15, 16, 20, 21, 28..30, 31, 32..33, 41, 42, 1
    //    (6) 1, 4..5, 6, 9-15, 16, 20, 21, 28..30, 31, 34..40, 41, 42, 1
    //
    // accountId |  amount  |   description     | spend % | circuits covered
    //     404   |   100    |   "Invalid id"    |    -    | (1)
    //      1    |   -1     | "Negative amount" |   90%   | (2)
    //      1    |   100    |    "No saving"    |   90%   | (3), (4)
    //      2    |   100    |       "SAVE"      |    0%   | (6)
    //      2    |   100    |    "No saving"    |    0%   | (5)

    @Test
    public void StructuralTesting_G_IndependentCircuits() {
        int start = log.getLog().length();
        distributor.distributeMoney(404, 100, "Invalid id");
        String out1 = log.getLog().substring(start);
        assertTrue(out1.contains("Error: Account 404 does not exist"));

        start = log.getLog().length();
        distributor.distributeMoney(1, -1, "Negative amount");
        String out2 = log.getLog().substring(start);
        assertTrue(out2.contains("Error: Amount must be greater than zero."));

        start = log.getLog().length();
        distributor.distributeMoney(1, 100, "No saving");
        String out3 = log.getLog().substring(start);
        assertTrue(out3.contains("No savings for account 1"));

        //Add a new user with id = 2
        distributor.addUser(2);
        double prev_SAVE = distributor.getSavingsForAccount(2);
        distributor.distributeMoney(2, 100.0, "SAVE");
        double now_SAVE = distributor.getSavingsForAccount(2);
        assertTrue(now_SAVE > prev_SAVE);

        start = log.getLog().length();
        distributor.distributeMoney(2, 100, "No saving");
        String out4 = log.getLog().substring(start);
        assertTrue(out4.contains("No savings for account 2"));
    }

}
