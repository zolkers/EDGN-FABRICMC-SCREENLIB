package com.edgn.ui.core.models.values;

public class DefaultDoubleModel implements DoubleModel {
    private double value;
    public DefaultDoubleModel() { this(0d); }
    public DefaultDoubleModel(double initial) { value = initial; }
    @Override public double get(){ return value; }
    @Override public void set(double v){ value = v; }
}