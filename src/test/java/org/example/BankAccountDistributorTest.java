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
}
