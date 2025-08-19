package com.edgn.ui.core.models.slider;

public class IntSliderModel implements SliderModel<Integer> {
    private int value, min, max, step;

    public IntSliderModel() { this(0, 0, 100, 1); }
    public IntSliderModel(int value, int min, int max, int step) {
        if (max < min) { int t=min; min=max; max=t; }
        this.min = min; this.max = max; this.step = Math.max(1, step);
        set(value);
    }

    @Override public Integer get() { return value; }
    @Override public void set(Integer v) { value = clampSnap(v); }

    @Override public Integer min(){ return min; }
    @Override public Integer max(){ return max; }
    @Override public void setRange(Integer mi, Integer ma) {
        if (ma < mi) { int t=mi; mi=ma; ma=t; }
        this.min = mi; this.max = ma;
        set(value);
    }

    @Override public Integer step(){ return step; }
    @Override public void setStep(Integer s){ this.step = Math.max(1, s); set(value); }

    @Override public Integer clampSnap(Integer v) {
        int x = Math.max(min, Math.min(max, v));
        int rel = x - min;
        int r = rel % step;
        if (r != 0) {
            x = x - r;
            if (r * 2 >= step) x += step;
        }
        return Math.max(min, Math.min(max, x));
    }

    @Override public Integer ratioToValue(double t) {
        t = Math.max(0, Math.min(1, t));
        int usable = max - min;
        int raw = (int)Math.round(min + t * usable);
        return clampSnap(raw);
    }

    @Override public double valueToRatio(Integer v) {
        int x = Math.max(min, Math.min(max, v));
        int usable = Math.max(1, max - min);
        return (x - min) / (double) usable;
    }
}
