package com.edgn.ui.layout;

@SuppressWarnings("unused")
public class LayoutConstraints {
    private Integer minWidth;
    private Integer maxWidth;
    private Integer minHeight;
    private Integer maxHeight;
    private Float widthPercent;
    private Float heightPercent;
    private Integer leftMargin;
    private Integer rightMargin;
    private Integer topMargin;
    private Integer bottomMargin;
    private HorizontalAlign horizontalAlign = HorizontalAlign.LEFT;
    private VerticalAlign verticalAlign = VerticalAlign.TOP;

    public enum HorizontalAlign { LEFT, CENTER, RIGHT, FILL }
    public enum VerticalAlign { TOP, CENTER, BOTTOM, FILL }

    public LayoutConstraints minWidth(int width) { this.minWidth = width; return this; }
    public LayoutConstraints maxWidth(int width) { this.maxWidth = width; return this; }
    public LayoutConstraints minHeight(int height) { this.minHeight = height; return this; }
    public LayoutConstraints maxHeight(int height) { this.maxHeight = height; return this; }
    public LayoutConstraints widthPercent(float percent) { this.widthPercent = percent; return this; }
    public LayoutConstraints heightPercent(float percent) { this.heightPercent = percent; return this; }
    public LayoutConstraints margin(int margin) { return margin(margin, margin, margin, margin); }
    public LayoutConstraints margin(int horizontal, int vertical) { return margin(horizontal, vertical, horizontal, vertical); }
    public LayoutConstraints margin(int left, int top, int right, int bottom) {
        this.leftMargin = left; this.topMargin = top; this.rightMargin = right; this.bottomMargin = bottom;
        return this;
    }
    public LayoutConstraints align(HorizontalAlign h, VerticalAlign v) {
        this.horizontalAlign = h; this.verticalAlign = v; return this;
    }

    public Integer getMinWidth() { return minWidth; }
    public Integer getMaxWidth() { return maxWidth; }
    public Integer getMinHeight() { return minHeight; }
    public Integer getMaxHeight() { return maxHeight; }
    public Float getWidthPercent() { return widthPercent; }
    public Float getHeightPercent() { return heightPercent; }
    public Integer getLeftMargin() { return leftMargin != null ? leftMargin : 0; }
    public Integer getRightMargin() { return rightMargin != null ? rightMargin : 0; }
    public Integer getTopMargin() { return topMargin != null ? topMargin : 0; }
    public Integer getBottomMargin() { return bottomMargin != null ? bottomMargin : 0; }
    public HorizontalAlign getHorizontalAlign() { return horizontalAlign; }
    public VerticalAlign getVerticalAlign() { return verticalAlign; }
}