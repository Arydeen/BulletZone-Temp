package edu.unh.cs.cs619.bulletzone.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class FieldHolder {

    private final Map<Direction, FieldHolder> neighbors = new HashMap<>();
    private Optional<FieldEntity> playerEntityHolder = Optional.empty();
    private Optional<FieldEntity> itemEntityHolder = Optional.empty();
    private Optional<FieldEntity> terrainEntityHolder = Optional.empty();
    private final int position;

    public FieldHolder(int pos) {
        this.position = pos;
    }

    public int getPosition() {return position;}

    public void addNeighbor(Direction direction, FieldHolder fieldHolder) {
        neighbors.put(checkNotNull(direction), checkNotNull(fieldHolder));
    }

    public FieldHolder getNeighbor(Direction direction) {
        return neighbors.get(checkNotNull(direction,
                "Direction cannot be null."));
    }

    public boolean isPresent() {
        return playerEntityHolder.isPresent();
    }

    public FieldEntity getEntity() {
        return playerEntityHolder.get();
    }

    public FieldEntity getItemEntity() {
        return itemEntityHolder.get();
    }

    public FieldEntity getTerrainEntityHolder() {
        return terrainEntityHolder.get();
    }

    public void setFieldEntity(FieldEntity entity) {
        playerEntityHolder = Optional.of(checkNotNull(entity, "Field Entitity cannot be null"));}

    public void setItemEntity(FieldEntity entity) {
        itemEntityHolder = Optional.of(checkNotNull(entity, "Field Entitity cannot be null"));
    }

    public void setTerrainEntityHolder(FieldEntity entity) {
        terrainEntityHolder = Optional.of(checkNotNull(entity, "Field Entitity cannot be null"));
    }

    public void clearField() {
        if (playerEntityHolder.isPresent()) {
            playerEntityHolder = Optional.empty();
        }
    }

    public void clearItem() {
        if (itemEntityHolder.isPresent()) {
            itemEntityHolder = Optional.empty();
        }
    }

    public void clearTerrain() {
        if (terrainEntityHolder.isPresent()) {
            terrainEntityHolder = Optional.empty();
        }
    }

    public void clearAll() {
        if (playerEntityHolder.isPresent()) {
            playerEntityHolder = Optional.empty();
        }

        if (itemEntityHolder.isPresent()) {
            itemEntityHolder = Optional.empty();
        }

        if (terrainEntityHolder.isPresent()) {
            terrainEntityHolder = Optional.empty();
        }
    }

}
