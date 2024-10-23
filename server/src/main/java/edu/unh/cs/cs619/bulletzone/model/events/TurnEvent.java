package edu.unh.cs.cs619.bulletzone.model.events;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TurnEvent extends GameEvent {
    @JsonProperty
    private int rawServerValue;
    @JsonProperty
    private int position;
    @JsonProperty
    private int direction;

    public TurnEvent() {}

    public TurnEvent(int rawServerValue, int position, int direction) {
        this.rawServerValue = rawServerValue;
        this.position = position;
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "Turn " + rawServerValue +
                " to face " + direction +
                super.toString();
    }

}
