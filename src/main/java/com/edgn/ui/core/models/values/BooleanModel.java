package com.edgn.ui.core.models.values;

public interface BooleanModel {
    boolean get();
    void set(boolean value);
    default void toggle() { set(!get()); }
}