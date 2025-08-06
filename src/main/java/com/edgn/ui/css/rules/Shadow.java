package com.edgn.ui.css.rules;

public enum Shadow {
    NONE(0x00000000, 0),
    SM(0x20000000, 2),
    MD(0x30000000, 4),
    LG(0x40000000, 8),
    XL(0x50000000, 12),
    GLOW(0x80000000, 16);

    public final int color;
    public final int blur;

    Shadow(int color, int blur) {
        this.color = color;
        this.blur = blur;
    }
}