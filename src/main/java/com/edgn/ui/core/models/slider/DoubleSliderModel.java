package com.edgn.ui.core.models.slider;

public class DoubleSliderModel implements SliderModel<Double> {
    private double value;
    private double min;
    private double max;
    private double step;

    public DoubleSliderModel() {
        this(0d, 0d, 1d, 0.01d);
    }

    public DoubleSliderModel(double value, double min, double max, double step) {
        if (max < min) {
            double t = min;
            min = max;
            max = t;
        }
        this.min = min;
        this.max = max;
        this.step = Math.clamp(step, 1e-12d, Double.MAX_VALUE);
        set(value);
    }

    @Override
    public Double get() {
        return value;
    }

    @Override
    public void set(Double v) {
        value = clampSnap(v);
    }

    @Override
    public Double min() {
        return min;
    }

    @Override
    public Double max() {
        return max;
    }

    @Override
    public void setRange(Double mi, Double ma) {
        if (ma < mi) {
            double t = mi;
            mi = ma;
            ma = t;
        }
        this.min = mi;
        this.max = ma;
        set(value);
    }

    @Override
    public Double step() {
        return step;
    }

    @Override
    public void setStep(Double s) {
        this.step = Math.clamp(s, 1e-12d, Double.MAX_VALUE);
        set(value);
    }

    @Override
    public Double clampSnap(Double v) {
        double x = Math.clamp(v, min, max);
        double stepsCount = Math.round((x - min) / step);
        x = min + stepsCount * step;
        return Math.clamp(x, min, max);
    }

    @Override
    public Double ratioToValue(double t) {
        t = Math.clamp(t, 0.0d, 1.0d);
        double x = min + t * (max - min);
        return clampSnap(x);
    }

    @Override
    public double valueToRatio(Double v) {
        double x = Math.clamp(v, min, max);
        double usable = Math.clamp(max - min, 1e-12d, Double.MAX_VALUE);
        return (x - min) / usable;
    }
}
