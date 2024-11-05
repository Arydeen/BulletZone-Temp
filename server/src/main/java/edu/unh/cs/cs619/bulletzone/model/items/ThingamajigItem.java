package edu.unh.cs.cs619.bulletzone.model.items;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.repository.DataRepository;
import org.greenrobot.eventbus.EventBus;

import java.util.Random;

public class ThingamajigItem extends Item {
    private static final Random random = new Random();
    private final DataRepository dataRepository;

    public ThingamajigItem(long id, DataRepository dataRepository) {
        super(id, ItemType.THINGAMAJIG);
        this.dataRepository = dataRepository;
    }

    @Override
    public int getIntValue() {
        return type.getBaseValue() + random.nextInt(1000);
    }

    @Override
    public FieldEntity copy() {
        return new ThingamajigItem(id, dataRepository);
    }

    @Override
    public void onPickup(Tank tank) {
        int creditAmount = random.nextInt(100) + 100; // Random amount between 100-200
        tank.addCredits(creditAmount);

        // Update the balance in database
        if (dataRepository != null) {
            dataRepository.addUserBalance(tank.getId(), creditAmount);
        }
    }
}