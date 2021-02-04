package edu.unh.cs.cs619.bulletzone.datalayer;

public class BankAccount extends OwnableEntity {
    protected double balance;

    public double getBalance() { return balance; }

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
    boolean modifyBalance(int amount) {
        balance += amount;
        return true;
    }
}
