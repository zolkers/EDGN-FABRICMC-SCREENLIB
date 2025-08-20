package com.edgn.ui.css.values;

public enum BorderRadius {
    NONE(0), SM(2), MD(4), LG(8), XL(12), XXL(16), FULL(9999);
    
    public final int value;
    
    BorderRadius(int value) {
        this.value = value;
    }
}