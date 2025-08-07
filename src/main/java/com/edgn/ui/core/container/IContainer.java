package com.edgn.ui.core.container;

import com.edgn.ui.core.UIElement;

public interface IContainer {
    <T extends IContainer> T addChild(UIElement element);
    <T extends IContainer> T removeChild(UIElement element);
    <T extends IContainer> T clearChildren();
}
