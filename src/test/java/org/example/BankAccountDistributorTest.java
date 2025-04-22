package org.example;

import org.junit.contrib.java.lang.system.StandardOutputStreamLog;

import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;

import java.util.AbstractMap;
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
    // 6 classes of equivalence
    @Test
    public void equivalencePartitioning() {
        // 1. accountId valid, positive amount => Distributing the money
        distributor.distributeMoney(1, 100.0, "Valid input");
        assertTrue(log.getLog().contains("Distributing $100.0 for account 1 [Valid input]"));

        // 2. accountId invalid => Error message
        distributor.distributeMoney(404, 100.0, "Invalid input");
        assertTrue(log.getLog().contains("Error: Account 404 does not exist"));

        // 3. negative amount => Error message
        distributor.distributeMoney(1, -50.0, "Negative amount");
        assertTrue(log.getLog().contains("Error: Amount must be greater than zero."));

        // 4. amount = 0 => Error message
        distributor.distributeMoney(1, 0.0, "0 amount");
        assertTrue(log.getLog().contains("Error: Amount must be greater than zero."));

        // 5. Partial Spending percentage (< 100%) => Savings
        double prev = distributor.getSavingsForAccount(1);
        distributor.distributeMoney(1, 100.0, "Partial spending");
        double now = distributor.getSavingsForAccount(1);
        assertTrue(now > prev);

        // 6. Full Spending percentage (100%) => No Savings
        distributor.addUser(2);
        distributor.addSpendingAccount(2, "Car", 0.0, 50.0);
        distributor.addSpendingAccount(2, "Housing", 0.0, 50.0);
        distributor.distributeMoney(2, 200.0, "Full spending");
        assertTrue(log.getLog().contains("No savings for account 2"));
    }

    // b) Boundary values analysis
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

    // c) Category partitioning
    @Test
    public void categoryPartitioning() {

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
