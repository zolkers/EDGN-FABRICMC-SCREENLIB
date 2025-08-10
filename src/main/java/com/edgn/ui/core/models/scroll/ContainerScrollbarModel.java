package com.edgn.ui.core.models.scroll;

import com.edgn.ui.core.container.containers.ScrollContainer;

public class ContainerScrollbarModel implements ScrollbarModel {

    private final ScrollContainer target;

    public ContainerScrollbarModel(ScrollContainer target) {
        this.target = target;
    }

    @Override
    public boolean isVerticalEnabled() {
        return target.isVerticalScrollEnabled();
    }

    @Override
    public boolean isHorizontalEnabled() {
        return target.isHorizontalScrollEnabled();
    }

    @Override
    public int getViewportWidth() {
        return target.getViewportWidth();
    }

    @Override
    public int getViewportHeight() {
        return target.getViewportHeight();
    }

    @Override
    public int getContentWidth() {
        return target.getContentWidth();
    }

    @Override
    public int getContentHeight() {
        return target.getContentHeight();
    }

    @Override
    public int getScrollX() {
        return target.getScrollX();
    }

    @Override
    public int getScrollY() {
        return target.getScrollY();
    }

    @Override
    public void setScrollX(int value) {
        target.setScrollX(value);
    }

    @Override
    public void setScrollY(int value) {
        target.setScrollY(value);
    }
}
