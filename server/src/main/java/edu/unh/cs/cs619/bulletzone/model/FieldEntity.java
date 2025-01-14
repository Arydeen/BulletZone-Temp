package edu.unh.cs.cs619.bulletzone.model;

public abstract class FieldEntity {
    //protected static final EventBus eventBus = new EventBus();
    protected FieldHolder parent;

    /**
     * Serializes the current {@link edu.unh.cs.cs619.bulletzone.model.FieldEntity} instance.
     *
     * @return Integer representation of the current {@link edu.unh.cs.cs619.bulletzone.model.FieldEntity}
     */
    public abstract int getIntValue();

    public FieldHolder getParent() {
        return parent;
    }

    public void setParent(FieldHolder parent) {
        this.parent = parent;
    }

    public int getPosition() { return parent.getPosition(); }

    public abstract FieldEntity copy();

    public void hit(int damage) {
    }

    /*public static final void registerEventBusListener(Object listener) {
        checkNotNull(listener);
        eventBus.register(listener);
    }

    public static final void unregisterEventBusListener(Object listener) {
        checkNotNull(listener);
        eventBus.unregister(listener);
    }*/
}
