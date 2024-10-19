package edu.unh.cs.cs619.bulletzone.datalayer.account;

import edu.unh.cs.cs619.bulletzone.datalayer.permission.OwnableEntity;

public class BankAccount extends OwnableEntity {
    protected double balance;

    public double getBalance() { return balance; }

    public BankAccount(int userId) {
        super(new BankAccountRecord());
        this.balance = 1000.0; // Initial balance of 1000 credits
    }

    //----------------------------------END OF PUBLIC METHODS--------------------------------------
    BankAccount(BankAccountRecord rec) {
        super(rec);
        balance = rec.credits;
    }

    /**
     * Modifies the credit balance for the account
     * @param amount Positive or negative amount to add to the credit balance
     * @return true if successful (cannot be false, currently)
     */
    boolean modifyBalance(double amount) {
        balance += amount;
        return true;
    }
}
