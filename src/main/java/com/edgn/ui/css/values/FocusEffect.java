package com.edgn.ui.css.values;

public enum FocusEffect {
    RING(true),
    OUTLINE(true);

    public final boolean value;

    FocusEffect(boolean value) {
        this.value = value;
    }
}