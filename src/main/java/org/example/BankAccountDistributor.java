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
        }
        spendingAccounts.get(accountId).put(spendingName, new AbstractMap.SimpleEntry<>(amount, percentage));
    }

    public void addMoneyToSavingAccount(int accountId, double amount) {
        if (!savingAccount.containsKey(accountId)) {
            System.out.println("Account " + accountId + " does not exist");
        }
        savingAccount.put(accountId, savingAccount.get(accountId) + amount);
    }

    public void distributeMoney(int accountId, double amount) {
        if (!spendingAccounts.containsKey(accountId)) {
            System.out.println("Account " + accountId + " does not exist");
            return;
        }

        if (amount <= 0) {
            System.out.println("Error: Amount must be greater than zero.");
            return;
        } else {
            System.out.println("Amount to be distributed: " + amount);
        }

        Map<String, AbstractMap.SimpleEntry<Double, Double>> spendingMap = spendingAccounts.get(accountId);

        double totalPercentage = 0.0;

        for (Map.Entry<String, AbstractMap.SimpleEntry<Double, Double>> entry : spendingMap.entrySet()) {
            AbstractMap.SimpleEntry<Double, Double> entryValue = entry.getValue();
            totalPercentage += entryValue.getValue();
        }

        if (totalPercentage > 100 - EPS) {
            System.out.println("Total spending percentage exceeds 100%");
            return;
        }

        if (totalPercentage > 100 - EPS && totalPercentage < 100 + EPS) {
            System.out.println("No savings for the amount " + amount);
            return;
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

        System.out.println("Remaining money of " + remainingAmount + " added to savings account for account " + accountId);
    }

    public static void main(String[] args) {

    }
}