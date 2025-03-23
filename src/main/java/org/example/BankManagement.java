package org.example;

import java.util.HashMap;
import java.util.Map;

public class BankManagement {
    private Map<Integer, Double> bankAccounts;

    public BankManagement() {
        this.bankAccounts = new HashMap<>();
    }

    void addBankAccount(int accountId, double amount) {
        if (bankAccounts.containsKey(accountId)) {
            System.out.println("Bank account " + Double.toString(amount) + " already exists");
            return;
        }
        if (amount <= 0) {
            System.out.println("Amount must be greater than 0");
            return;
        }
        this.bankAccounts.put(accountId, amount);
    }

    void removeBankAccount(int accountId) {
        if (!bankAccounts.containsKey(accountId)) {
            System.out.println("Bank account " + Double.toString(accountId) + " does not exist");
            return;
        }
        this.bankAccounts.remove(accountId);
    }

    void deposit(int accountId, double amount) {
        if (!bankAccounts.containsKey(accountId)) {
            System.out.println("Bank account " + Double.toString(amount) + " does not exist");
            return;
        }
        if (amount <= 0) {
            System.out.println("Amount must be greater than 0");
            return;
        }
        this.bankAccounts.put(accountId, this.bankAccounts.get(accountId) + amount);
    }

    void withdraw(int accountId, double amount) {
        if (!bankAccounts.containsKey(accountId)) {
            System.out.println("Bank account " + Double.toString(amount) + " does not exist");
            return;
        }
        if (amount <= 0) {
            System.out.println("Amount must be greater than 0");
            return;
        }
        if (this.bankAccounts.get(accountId) < amount) {
            System.out.println("Bank account " + Double.toString(amount) + " does not have enough money");
            return;
        }
        this.bankAccounts.put(accountId, this.bankAccounts.get(accountId) - amount);
    }

    public static void main(String[] args) {

    }
}