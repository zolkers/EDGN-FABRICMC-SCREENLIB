package com.edgn.ui.core.container;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.layout.ClipBounds;

import java.util.List;

public interface IContainer {
    <T extends IContainer> T addChild(UIElement child);
    <T extends IContainer> T removeChild(UIElement child);
    <T extends IContainer> T clearChildren();
    List<UIElement> getChildren();
    boolean hasChildren();
    void updateChildrenLayout();
    UIElement findElementAt(double mouseX, double mouseY);
    ClipBounds getClipBounds();
}