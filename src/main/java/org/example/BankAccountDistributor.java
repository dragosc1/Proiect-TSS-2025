package org.example;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class BankAccountDistributor {
    private Map<Integer, Map<String, AbstractMap.SimpleEntry<Double, Double>>> spendingAccounts;
    private Map<Integer, Double> savingAccount;
    private final double EPS = 1e-6;

    public BankAccountDistributor() {
        spendingAccounts = new HashMap<>();
        savingAccount = new HashMap<>();
    }

    public void addUser(int accountId) {
        spendingAccounts.put(accountId, new HashMap<>());
        savingAccount.put(accountId, 0.0);
    }

    public void addSpendingAccount(int accountId, String spendingName, double amount, double percentage) {
        if (!spendingAccounts.containsKey(accountId)) {
            System.out.println("Account " + accountId + " does not exist");
            return;
        }
        double totalPercentage = 0;
        for (Map.Entry<String, AbstractMap.SimpleEntry<Double, Double>> entry : spendingAccounts.get(accountId).entrySet()) {
            if (entry.getKey().equals(spendingName)) {
                System.out.println("Spending account " + spendingName + " already exists");
                return;
            }
            totalPercentage += entry.getValue().getValue();
        }
        if (totalPercentage + percentage > 100.0 + EPS) {
            System.out.println("Spending account " + spendingName + " has a very large percentage");
            System.out.println("Remaining percentage without account: " + totalPercentage + "%");
            return;
        }
        spendingAccounts.get(accountId).put(spendingName, new AbstractMap.SimpleEntry<>(amount, percentage));
    }

    public void addMoneyToSavingAccount(int accountId, double amount) {
        if (!savingAccount.containsKey(accountId)) {
            System.out.println("Account " + accountId + " does not exist");
            return;
        }
        savingAccount.put(accountId, savingAccount.get(accountId) + amount);
    }

    public void distributeMoney(int accountId, double amount, String description) {
        if (!spendingAccounts.containsKey(accountId)) {
            System.out.println("Error: Account " + accountId + " does not exist");
            return;
        }

        if (amount <= 0) {
            System.out.println("Error: Amount must be greater than zero.");
            return;
        }

        System.out.println("Distributing $" + amount + " for account " + accountId + " [" + description + "]");

        Map<String, AbstractMap.SimpleEntry<Double, Double>> spendingMap = spendingAccounts.get(accountId);
        double totalPercentage = 0.0;

        for (Map.Entry<String, AbstractMap.SimpleEntry<Double, Double>> entry : spendingMap.entrySet()) {
            AbstractMap.SimpleEntry<Double, Double> entryValue = entry.getValue();
            totalPercentage += entryValue.getValue();
        }

        if (totalPercentage > 100 - EPS) {
            System.out.println("No savings for account " + accountId);
        }
        else {
            System.out.println("Remaining money of " + Double.toString((100 - totalPercentage) / 100 * amount) + " added to savings account for account " + accountId);
        }

        for (Map.Entry<String, AbstractMap.SimpleEntry<Double, Double>> entry : spendingMap.entrySet()) {
            String spendingName = entry.getKey();
            AbstractMap.SimpleEntry<Double, Double> entryValue = entry.getValue();

            double newAmount = entryValue.getKey() + (amount * (entryValue.getValue() / 100));
            spendingMap.put(spendingName, new AbstractMap.SimpleEntry<>(newAmount, entryValue.getValue()));
        }

        double totalSpendingAmount = amount * (totalPercentage / 100);
        double remainingAmount = amount - totalSpendingAmount;

        double currentSavings = savingAccount.get(accountId);
        savingAccount.put(accountId, currentSavings + remainingAmount);
    }

    public double getSavingsForAccount(int accountId) {
        return savingAccount.getOrDefault(accountId, 0.0);
    }
}