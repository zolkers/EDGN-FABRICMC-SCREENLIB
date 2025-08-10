package com.edgn.ui.core.models.scroll;

import com.edgn.ui.core.models.Model;

public interface ScrollbarModel extends Model {
    boolean isVerticalEnabled();
    boolean isHorizontalEnabled();
    int getViewportWidth();
    int getViewportHeight();
    int getContentWidth();
    int getContentHeight();
    int getScrollX();
    int getScrollY();
    void setScrollX(int value);
    void setScrollY(int value);
}
