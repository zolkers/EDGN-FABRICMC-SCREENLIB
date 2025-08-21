package com.edgn.ui.css.values;

public enum HoverEffect {
    SCALE(1.05f),
    BRIGHTEN(1.2f),
    OPACITY(0.8f),
    ROTATE(5.0f);

    public final float value;

    HoverEffect(float value) {
        this.value = value;
    }
}
