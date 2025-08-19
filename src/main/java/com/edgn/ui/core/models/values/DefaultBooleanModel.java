package com.edgn.ui.core.models.values;

public class DefaultBooleanModel implements BooleanModel {
    private boolean value;
    public DefaultBooleanModel() { this(false); }
    public DefaultBooleanModel(boolean initial) { value = initial; }
    @Override public boolean get(){ return value; }
    @Override public void set(boolean v){ value = v; }
}