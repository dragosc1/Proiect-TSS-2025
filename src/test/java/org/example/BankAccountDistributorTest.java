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

    // Functional testing

    // a) Equivalence partitioning
    @Test
    public void equivalencePartitioning() {

    }

    // b) Boundary values analysis
    @Test
    public void boundaryValuesAnalysis() {

    }

    // c) Category partitioning
    @Test
    public void categoryPartitioning() {

    }

    @Test
    public void testAccountDoesNotExistMessage() {
        distributor.distributeMoney(999, 100.0, "Salary");

        assertTrue(log.getLog().contains("Account 999 does not exist"));
    }

    @Before
    public void setUp() {
        distributor = new BankAccountDistributor();
        distributor.addUser(1);
        distributor.addSpendingAccount(1, "Rent", 0.0, 40.0);
        distributor.addSpendingAccount(1, "Food", 0.0, 30.0);
        distributor.addSpendingAccount(1, "Entertainment", 0.0, 20.0);
        distributor.addMoneyToSavingAccount(1, 100.0);
    }

    @Test
    public void testInitialSavings() {
        assertEquals(100.0, distributor.getSavingsForAccount(1), 0.001);
    }

    @Test
    public void testDistribution() {
        distributor.distributeMoney(1, 1000.0, "Salary");

        Map<String, AbstractMap.SimpleEntry<Double, Double>> spending = distributor.getSpendingMapForAccount(1);

        assertEquals(400.0, spending.get("Rent").getKey(), 0.001);
        assertEquals(300.0, spending.get("Food").getKey(), 0.001);
        assertEquals(200.0, spending.get("Entertainment").getKey(), 0.001);

        // Savings before: 100.0; after: +100 remaining = 200
        assertEquals(200.0, distributor.getSavingsForAccount(1), 0.001);
    }

    @Test
    public void testDistributionExact100Percent() {
        distributor.addUser(3);  // 🛠️ This line is required!

        distributor.addSpendingAccount(3, "A", 0.0, 50.0);
        distributor.addSpendingAccount(3, "B", 0.0, 50.0);
        distributor.distributeMoney(3, 500.0, "Salary");

        Map<String, AbstractMap.SimpleEntry<Double, Double>> spending = distributor.getSpendingMapForAccount(3);

        assertEquals(250.0, spending.get("A").getKey(), 0.001);
        assertEquals(250.0, spending.get("B").getKey(), 0.001);
        assertEquals(0.0, distributor.getSavingsForAccount(3), 0.001);
    }

    @Test
    public void testNegativeAmountIgnored() {
        distributor.distributeMoney(1, -100.0, "Salary");
        assertEquals(100.0, distributor.getSavingsForAccount(1), 0.001);
    }
}
