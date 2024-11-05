package edu.unh.cs.cs619.bulletzone.model.items;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Tank;

public class FusionReactorItem extends PowerUpItem {
    public FusionReactorItem(long id) {
        super(id, ItemType.FUSION_REACTOR);
    }

    @Override
    public int getIntValue() {
        return type.getBaseValue();
    }

    @Override
    public FieldEntity copy() {
        return new FusionReactorItem(id);
    }

    @Override
    public void onPickup(Tank tank) {
        tank.increaseEnergy(50);
        tank.setAllowedFireInterval(tank.getAllowedFireInterval() / 2);
        tank.setAllowedMoveInterval(tank.getAllowedMoveInterval() + 100);
    }

    @Override
    public void onEject(Tank tank) {
        tank.decreaseEnergy(50);
        tank.setAllowedFireInterval(tank.getAllowedFireInterval() * 2);
        tank.setAllowedMoveInterval(tank.getAllowedMoveInterval() - 100);
    }
}