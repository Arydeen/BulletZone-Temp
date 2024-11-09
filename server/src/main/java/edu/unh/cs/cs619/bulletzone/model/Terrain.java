package edu.unh.cs.cs619.bulletzone.model;

public class Terrain extends FieldEntity {
    int destructValue, pos;

    public Terrain(){
        this.destructValue = 4000;
    }

    public Terrain(int destructValue, int pos){
        this.destructValue = destructValue;
        this.pos = pos;
    }

    @Override
    public FieldEntity copy() {
        return new Terrain();
    }

    @Override
    public int getIntValue() {
        return destructValue;
    }

    @Override
    public String toString() {
        return "T";
    }

    public int getPos(){
        return pos;
    }
}
