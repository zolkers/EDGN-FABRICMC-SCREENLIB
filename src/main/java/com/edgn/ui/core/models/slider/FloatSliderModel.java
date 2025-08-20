package com.edgn.ui.core.models.slider;

public class FloatSliderModel implements SliderModel<Float> {
    private float value;
    private float min;
    private float max;
    private float step;

    public FloatSliderModel() { this(0f, 0f, 1f, 0.01f); }

    public FloatSliderModel(float value, float min, float max, float step) {
        if (max < min) { float t = min; min = max; max = t; }
        this.min = min;
        this.max = max;
        this.step = Math.clamp(step, 1e-6f, Float.MAX_VALUE);
        set(value);
    }

    @Override public Float get(){ return value; }
    @Override public void set(Float v){ value = clampSnap(v); }

    @Override public Float min(){ return min; }
    @Override public Float max(){ return max; }
    @Override public void setRange(Float mi, Float ma){
        if (ma < mi) { float t=mi; mi=ma; ma=t; }
        this.min=mi; this.max=ma; set(value);
    }

    @Override public Float step(){ return step; }
    @Override
    public void setStep(Float s) {
        this.step = Math.clamp(s, 1e-6f, Float.MAX_VALUE);
        set(value);
    }
    @Override
    public Float clampSnap(Float v) {
        float x = Math.clamp(v, min, max);
        float steps = Math.round((x - min) / step);
        x = min + steps * step;
        return Math.clamp(x, min, max);
    }

    @Override
    public Float ratioToValue(double t) {
        t = Math.clamp(t, 0.0, 1.0);
        float x = (float) (min + t * (max - min));
        return clampSnap(x);
    }

    @Override
    public double valueToRatio(Float v) {
        float x = Math.clamp(v, min, max);
        float usable = Math.clamp(max - min, 1e-9f, Float.MAX_VALUE);
        return (x - min) / (double) usable;
    }
}
