package com.edgn.ui.core.models.slider;

public class IntSliderModel implements SliderModel<Integer> {
    private int value;
    private int min;
    private int max;
    private int step;

    public IntSliderModel() { this(0, 0, 100, 1); }
    public IntSliderModel(int value, int min, int max, int step) {
        if (max < min) {
            int t=min;
            min=max;
            max=t; }
        this.min = min;
        this.max = max;
        this.step = Math.max(1, step);
        set(value);
    }

    @Override public Integer get() { return value; }
    @Override public void set(Integer v) { value = clampSnap(v); }

    @Override public Integer min(){ return min; }
    @Override public Integer max(){ return max; }
    @Override public void setRange(Integer mi, Integer ma) {
        if (ma < mi) {
            int t=mi; mi=ma;
            ma=t;
        }
        this.min = mi;
        this.max = ma;
        set(value);
    }

    @Override public Integer step(){ return step; }
    @Override public void setStep(Integer s){
        this.step = Math.max(1, s);
        set(value);
    }

    @Override
    public Integer clampSnap(Integer v) {
        int x = Math.clamp(v, min, max);
        int rel = x - min;
        int r = rel % step;
        if (r != 0) {
            x -= r;
            if (r * 2 >= step) x += step;
        }
        return Math.clamp(x, min, max);
    }

    @Override
    public Integer ratioToValue(double t) {
        t = Math.clamp(t, 0.0, 1.0);
        int usable = max - min;
        int raw = (int) Math.round(min + t * usable);
        return clampSnap(raw);
    }

    @Override
    public double valueToRatio(Integer v) {
        int x = Math.clamp(v, min, max);
        int usable = Math.clamp((long) max - min, 1, Integer.MAX_VALUE);
        return (x - min) / (double) usable;
    }
}
