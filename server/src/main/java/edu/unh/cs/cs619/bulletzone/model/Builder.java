package edu.unh.cs.cs619.bulletzone.model;

public class Builder extends Tank{
    public Builder(long id, Direction direction, String ip) {
        super(id, direction, ip);
    }

    @Override
    public int getIntValue() {
        return (int) (20000000 + (10000 * getId()) + (10 * getLife()) + Direction.toByte(getDirection()));
    }

}
