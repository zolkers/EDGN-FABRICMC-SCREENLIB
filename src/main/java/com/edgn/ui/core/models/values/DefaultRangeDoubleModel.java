package com.edgn.ui.core.models.values;

public class DefaultRangeDoubleModel implements RangeDoubleModel {
    private double min, max;
    public DefaultRangeDoubleModel(double min, double max) { 
        if (max < min) { double t=min; min=max; max=t; }
        this.min = min; this.max = max; 
    }
    @Override public double getMin(){ return min; }
    @Override public double getMax(){ return max; }
    @Override public void setMin(double v){ min = v; if (max < min) max = min; }
    @Override public void setMax(double v){ max = v; if (max < min) min = max; }
}