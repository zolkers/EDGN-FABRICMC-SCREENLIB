package com.edgn.ui.core.models.slider;

public interface SliderModel<N extends Number> {
    N get();
    void set(N v);
    N min();
    N max();
    void setRange(N min, N max);
    N step();
    void setStep(N step);
    N clampSnap(N v);
    N ratioToValue(double t);
    double valueToRatio(N v);
    default void setFromRatio(double t) { set(ratioToValue(t)); }
}
