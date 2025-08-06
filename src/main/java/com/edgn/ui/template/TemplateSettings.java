package com.edgn.ui.template;

public class TemplateSettings {

    public static final TemplateSettings DEFAULT = new TemplateSettings();

    private boolean hasHeader = true;
    private Integer headerHeight = null;
    private Integer minHeaderHeight = 20;
    private Integer maxHeaderHeight = null;
    private float headerHeightRatio = 1.0f / 15.0f;

    private boolean hasFooter = true;
    private Integer footerHeight = null;
    private Integer minFooterHeight = 15;
    private Integer maxFooterHeight = null;
    private float footerHeightRatio = 1.0f / 20.0f;

    private boolean hasDefaultBackground = true;
    private boolean hasDefaultHeader = true;
    private boolean hasDefaultFooter = true;

    private int padding = 0;
    private int headerPadding = 0;
    private int footerPadding = 0;
    private int contentPadding = 0;

    private Integer backgroundColor = null;
    private Integer headerBackgroundColor = null;
    private Integer footerBackgroundColor = null;
    private Integer contentBackgroundColor = null;

    private boolean animateLayout = false;
    private int animationDuration = 200;

    private boolean responsive = true;
    private int minScreenWidth = 320;
    private int minScreenHeight = 240;

    public TemplateSettings() {}

    public TemplateSettings noHeader() {
        this.hasHeader = false;
        return this;
    }

    public TemplateSettings withHeader() {
        this.hasHeader = true;
        return this;
    }

    public TemplateSettings headerHeight(int height) {
        this.headerHeight = Math.max(0, height);
        return this;
    }

    public TemplateSettings minHeaderHeight(int minHeight) {
        this.minHeaderHeight = Math.max(0, minHeight);
        return this;
    }

    public TemplateSettings maxHeaderHeight(int maxHeight) {
        this.maxHeaderHeight = Math.max(0, maxHeight);
        return this;
    }

    public TemplateSettings headerHeightRatio(float ratio) {
        this.headerHeightRatio = Math.max(0.0f, Math.min(1.0f, ratio));
        return this;
    }

    public TemplateSettings noDefaultHeader() {
        this.hasDefaultHeader = false;
        return this;
    }

    public TemplateSettings headerBackground(int color) {
        this.headerBackgroundColor = color;
        return this;
    }

    public TemplateSettings headerPadding(int padding) {
        this.headerPadding = Math.max(0, padding);
        return this;
    }

    public TemplateSettings noFooter() {
        this.hasFooter = false;
        return this;
    }

    public TemplateSettings withFooter() {
        this.hasFooter = true;
        return this;
    }

    public TemplateSettings footerHeight(int height) {
        this.footerHeight = Math.max(0, height);
        return this;
    }

    public TemplateSettings minFooterHeight(int minHeight) {
        this.minFooterHeight = Math.max(0, minHeight);
        return this;
    }

    public TemplateSettings maxFooterHeight(int maxHeight) {
        this.maxFooterHeight = Math.max(0, maxHeight);
        return this;
    }

    public TemplateSettings footerHeightRatio(float ratio) {
        this.footerHeightRatio = Math.max(0.0f, Math.min(1.0f, ratio));
        return this;
    }

    public TemplateSettings noDefaultFooter() {
        this.hasDefaultFooter = false;
        return this;
    }

    public TemplateSettings footerBackground(int color) {
        this.footerBackgroundColor = color;
        return this;
    }

    public TemplateSettings footerPadding(int padding) {
        this.footerPadding = Math.max(0, padding);
        return this;
    }

    public TemplateSettings contentBackground(int color) {
        this.contentBackgroundColor = color;
        return this;
    }

    public TemplateSettings contentPadding(int padding) {
        this.contentPadding = Math.max(0, padding);
        return this;
    }

    public TemplateSettings noDefaultBackground() {
        this.hasDefaultBackground = false;
        return this;
    }

    public TemplateSettings padding(int padding) {
        this.padding = Math.max(0, padding);
        return this;
    }

    public TemplateSettings allPadding(int padding) {
        return padding(padding)
                .headerPadding(padding)
                .footerPadding(padding)
                .contentPadding(padding);
    }

    public TemplateSettings backgroundColor(int color) {
        this.backgroundColor = color;
        return this;
    }

    public TemplateSettings animated() {
        this.animateLayout = true;
        return this;
    }

    public TemplateSettings animated(int durationMs) {
        this.animateLayout = true;
        this.animationDuration = Math.max(0, durationMs);
        return this;
    }

    public TemplateSettings noAnimation() {
        this.animateLayout = false;
        return this;
    }

    public TemplateSettings responsive() {
        this.responsive = true;
        return this;
    }

    public TemplateSettings noResponsive() {
        this.responsive = false;
        return this;
    }

    public TemplateSettings minScreenSize(int width, int height) {
        this.minScreenWidth = Math.max(100, width);
        this.minScreenHeight = Math.max(100, height);
        return this;
    }

    public TemplateSettings minimal() {
        return noHeader()
                .noFooter()
                .noDefaultBackground()
                .padding(8);
    }

    public TemplateSettings fullscreen() {
        return noHeader()
                .noFooter()
                .padding(0)
                .noDefaultBackground();
    }

    public TemplateSettings dialog() {
        return headerHeight(40)
                .noFooter()
                .padding(16)
                .contentPadding(20)
                .animated();
    }

    public TemplateSettings compact() {
        return headerHeightRatio(1.0f / 20.0f)
                .footerHeightRatio(1.0f / 25.0f)
                .minHeaderHeight(25)
                .minFooterHeight(20)
                .padding(4);
    }

    public TemplateSettings spacious() {
        return headerHeightRatio(1.0f / 12.0f)
                .footerHeightRatio(1.0f / 18.0f)
                .padding(20)
                .contentPadding(24);
    }

    public boolean hasHeader() { return hasHeader; }
    public boolean hasFooter() { return hasFooter; }
    public boolean hasDefaultBackground() { return hasDefaultBackground; }
    public boolean hasDefaultHeader() { return hasDefaultHeader; }
    public boolean hasDefaultFooter() { return hasDefaultFooter; }

    public Integer getHeaderHeight() { return headerHeight; }
    public Integer getFooterHeight() { return footerHeight; }
    public Integer getMinHeaderHeight() { return minHeaderHeight; }
    public Integer getMaxHeaderHeight() { return maxHeaderHeight; }
    public Integer getMinFooterHeight() { return minFooterHeight; }
    public Integer getMaxFooterHeight() { return maxFooterHeight; }

    public float getHeaderHeightRatio() { return headerHeightRatio; }
    public float getFooterHeightRatio() { return footerHeightRatio; }

    public int getPadding() { return padding; }
    public int getHeaderPadding() { return headerPadding; }
    public int getFooterPadding() { return footerPadding; }
    public int getContentPadding() { return contentPadding; }

    public Integer getBackgroundColor() { return backgroundColor; }
    public Integer getHeaderBackgroundColor() { return headerBackgroundColor; }
    public Integer getFooterBackgroundColor() { return footerBackgroundColor; }
    public Integer getContentBackgroundColor() { return contentBackgroundColor; }

    public boolean isAnimateLayout() { return animateLayout; }
    public int getAnimationDuration() { return animationDuration; }

    public boolean isResponsive() { return responsive; }
    public int getMinScreenWidth() { return minScreenWidth; }
    public int getMinScreenHeight() { return minScreenHeight; }

    public int calculateHeaderHeight(int screenHeight) {
        if (!hasHeader) return 0;

        if (headerHeight != null) {
            return headerHeight;
        }

        int autoHeight = (int) (screenHeight * headerHeightRatio);

        if (minHeaderHeight != null) {
            autoHeight = Math.max(autoHeight, minHeaderHeight);
        }

        if (maxHeaderHeight != null) {
            autoHeight = Math.min(autoHeight, maxHeaderHeight);
        }

        return autoHeight;
    }

    public int calculateFooterHeight(int screenHeight) {
        if (!hasFooter) return 0;

        if (footerHeight != null) {
            return footerHeight;
        }

        int autoHeight = (int) (screenHeight * footerHeightRatio);

        if (minFooterHeight != null) {
            autoHeight = Math.max(autoHeight, minFooterHeight);
        }

        if (maxFooterHeight != null) {
            autoHeight = Math.min(autoHeight, maxFooterHeight);
        }

        return autoHeight;
    }

    public int calculateContentHeight(int screenHeight) {
        int headerHeight = calculateHeaderHeight(screenHeight);
        int footerHeight = calculateFooterHeight(screenHeight);
        return screenHeight - headerHeight - footerHeight - (padding * 2);
    }
}