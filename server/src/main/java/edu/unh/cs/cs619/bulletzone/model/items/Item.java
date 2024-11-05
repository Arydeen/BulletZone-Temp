package edu.unh.cs.cs619.bulletzone.model.items;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Tank;

public abstract class Item extends FieldEntity {
    protected final long id;
    protected ItemType type;

    public Item(long id, ItemType type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public abstract int getIntValue();

    @Override
    public abstract FieldEntity copy();

    public abstract void onPickup(Tank tank);

    public ItemType getType() {
        return type;
    }

    public long getId() {
        return id;
    }
}