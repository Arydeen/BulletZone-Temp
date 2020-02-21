/**
 * Public class for accessing data for individual categories in the ItemCategory table
 */
package edu.unh.cs.cs619.bulletzone.datalayer;

import java.util.Collection;
import java.util.HashMap;

public class ItemProperty {
    public enum PropertyType {Null, Additive, Average, Multiplicative, Restorative};
    static private HashMap<Integer, ItemProperty> propertyMap = new HashMap<Integer, ItemProperty>();
    static private HashMap<String, ItemProperty> nameToPropertyMap = new HashMap<>();
    public enum ID {Size, Weight, Price, PropertyVal1, PropertyVal2, PropertyVal3};

    /**
     * @return A collection of all ItemProperties in the database
     */
    static public Collection<ItemProperty> getAll() { return propertyMap.values(); }

    /**
     * Gives the ItemProperty associated with the passed name
     * @param propertyName  Name of the desired type (case-sensitive)
     * @return  ItemType object corresponding to the typeName
     */
    static public ItemProperty get(String propertyName) {
        ItemProperty property = nameToPropertyMap.get(propertyName);
        if (property == null)
            throw new NullPointerException("Unable to resolve " + propertyName + " to a valid ItemProperty.");
        return property;
    }

    /**
     * Gives the ItemProperty associated with the passed property ID
     * @param propertyID  ID of the desired type (from the database)
     * @return  ItemType object corresponding to the typeName
     */
    static public ItemProperty get(int propertyID) {
        ItemProperty property = propertyMap.get(propertyID);
        if (property == null)
            throw new NullPointerException("Unable to resolve " + propertyID + " to a valid ItemProperty.");
        return property;
    }

    private final int itemPropertyID;
    private final String name;
    private final PropertyType propertyType;

    ItemProperty(int id, String propertyName, PropertyType pType) {
        itemPropertyID = id;
        name = propertyName;
        propertyType = pType;
        propertyMap.put(id, this);
        nameToPropertyMap.put(name, this);
    }

    /**
     * Initializes an accumulator for this property type
     * @return the starting value for accumulating values of this property type
     */
    public double getIdentity() { return (propertyType == ItemProperty.PropertyType.Multiplicative)? 1.0 : 0.0; }

    /**
     * Combines the passed operand with the starting accumulator value to return an accumulated result
     * @param acc  Current value of the accumulator
     * @param operand  Value to be accumulated next
     * @return the accumulated value achieved by combining the operand with the existing acc(umulator)
     */
    public double accumulate(double acc, double operand) {
        switch(propertyType) {
            case Additive: case Restorative: case Average:
                return acc + operand;
            case Multiplicative:
                return acc * operand;
        }
        return acc; //unchanged
    }

    /**
     * If this property accumulates an average, finalize the accumulator by returning the average (else do nothing)
     * @param acc  Current value of the accumulator
     * @param count  How many values are in the accumulator
     * @return the starting accumulator value or the average (if this property accumulates to an average)
     */
    public double finalize(double acc, int count) {
        if (propertyType == PropertyType.Average)
            return acc / count;
        return acc;
    }

    /**
     * Determines if this property corresponds to the given ID
     * @param id  PropertyID to compare with
     * @return true if there's a match, and false otherwise
     */
    public boolean is(ID id) { return itemPropertyID == id.ordinal();}

    public int getID() {
        return itemPropertyID;
    }

    public String getName(){
        return name;
    }

    public PropertyType getType() { return propertyType; }
}