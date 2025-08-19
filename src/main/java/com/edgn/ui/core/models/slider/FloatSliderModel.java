package com.edgn.ui.core.models.slider;

public class FloatSliderModel implements SliderModel<Float> {
    private float value, min, max, step;

    public FloatSliderModel() { this(0f, 0f, 1f, 0.01f); }
    public FloatSliderModel(float value, float min, float max, float step) {
        if (max < min) { float t=min; min=max; max=t; }
        this.min=min; this.max=max; this.step=Math.max(1e-6f, step);
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
    @Override public void setStep(Float s){ this.step=Math.max(1e-6f, s); set(value); }

    @Override public Float clampSnap(Float v){
        float x = Math.max(min, Math.min(max, v));
        float steps = Math.round((x - min) / step);
        x = min + steps * step;
        if (x < min) x = min;
        if (x > max) x = max;
        return x;
    }

    @Override public Float ratioToValue(double t){
        t = Math.max(0, Math.min(1, t));
        float x = (float)(min + t * (max - min));
        return clampSnap(x);
    }

    @Override public double valueToRatio(Float v){
        float x = Math.max(min, Math.min(max, v));
        float usable = Math.max(1e-9f, (max - min));
        return (x - min) / (double) usable;
    }
}
