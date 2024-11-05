package edu.unh.cs.cs619.bulletzone.model.items;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Tank;

public class AntiGravItem extends PowerUpItem {
    public AntiGravItem(long id) {
        super(id, ItemType.ANTI_GRAV);
    }

    @Override
    public int getIntValue() {
        return type.getBaseValue();
    }

    @Override
    public FieldEntity copy() {
        return new AntiGravItem(id);
    }

    @Override
    public void onPickup(Tank tank) {
        tank.enableAntiGrav();
        tank.setAllowedMoveInterval(tank.getAllowedMoveInterval() / 2);
        tank.setAllowedFireInterval(tank.getAllowedFireInterval() + 100);
    }

    @Override
    public void onEject(Tank tank) {
        tank.disableAntiGrav();
    }
}