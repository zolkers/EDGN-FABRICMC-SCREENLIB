package com.edgn.ui.core.models.values;

public class DefaultIntModel implements IntModel {
    private int value;
    public DefaultIntModel() { this(0); }
    public DefaultIntModel(int initial) { value = initial; }
    @Override public int get(){ return value; }
    @Override public void set(int v){ value = v; }
}