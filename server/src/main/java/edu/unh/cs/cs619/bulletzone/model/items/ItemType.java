package edu.unh.cs.cs619.bulletzone.model.items;

public enum ItemType {
    THINGAMAJIG(3000000),
    ANTI_GRAV(3100000),
    FUSION_REACTOR(3200000);

    private final int baseValue;

    ItemType(int baseValue) {
        this.baseValue = baseValue;
    }

    public int getBaseValue() {
        return baseValue;
    }
}