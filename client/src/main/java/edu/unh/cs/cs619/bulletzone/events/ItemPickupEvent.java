package edu.unh.cs.cs619.bulletzone.events;

public class ItemPickupEvent {
    private final int itemType;
    private final double amount;

    public ItemPickupEvent(int itemType, double amount) {
        this.itemType = itemType;
        this.amount = amount;
    }

    public int getItemType() {
        return itemType;
    }

    public double getAmount() {
        return amount;
    }
}