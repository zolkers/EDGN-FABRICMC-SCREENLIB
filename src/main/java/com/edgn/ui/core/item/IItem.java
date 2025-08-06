package com.edgn.ui.core.item;

import com.edgn.ui.layout.ClipBounds;

public interface IItem {
    boolean canInteractAt(double mouseX, double mouseY);
    ClipBounds getVisibleBounds();
    void updateInteractionBounds();
    float getVisibilityRatio();
    boolean isFullyVisible();

    boolean isPartiallyVisible();
}