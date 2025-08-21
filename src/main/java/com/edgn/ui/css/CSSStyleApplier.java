package com.edgn.ui.css;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.css.values.Shadow;

@SuppressWarnings("unused")
public class CSSStyleApplier {

    private CSSStyleApplier() {/* should not be instantiated */}

    public static ComputedStyles computeStyles(UIElement element) {
        UIStyleSystem styleSystem = element.getStyleSystem();
        ComputedStyles styles = new ComputedStyles();

        styles.borderRadius = computeBorderRadius(element, styleSystem);
        styles.shadow = computeShadow(element, styleSystem);

        styles.paddingTop = computePaddingTop(element, styleSystem);
        styles.paddingRight = computePaddingRight(element, styleSystem);
        styles.paddingBottom = computePaddingBottom(element, styleSystem);
        styles.paddingLeft = computePaddingLeft(element, styleSystem);

        styles.marginTop = computeMarginTop(element, styleSystem);
        styles.marginRight = computeMarginRight(element, styleSystem);
        styles.marginBottom = computeMarginBottom(element, styleSystem);
        styles.marginLeft = computeMarginLeft(element, styleSystem);

        styles.flexGrow = computeFlexGrow(element, styleSystem);
        styles.flexShrink = computeFlexShrink(element, styleSystem);
        styles.flexBasis = computeFlexBasis(element, styleSystem); // NEW

        styles.gap = computeGap(element, styleSystem);

        styles.hasHoverEffect = element.hasClass(StyleKey.HOVER_BRIGHTEN) || element.hasClass(StyleKey.HOVER_SCALE);
        styles.hasFocusRing = element.hasClass(StyleKey.FOCUS_RING);

        return styles;
    }

    private static int computeBorderRadius(UIElement element, UIStyleSystem styleSystem) {
        StyleKey[] radiusKeys = {
                StyleKey.ROUNDED_NONE, StyleKey.ROUNDED_SM, StyleKey.ROUNDED_MD,
                StyleKey.ROUNDED_LG, StyleKey.ROUNDED_XL, StyleKey.ROUNDED_XXL, StyleKey.ROUNDED_FULL
        };
        return computeValueFromKeys(element, styleSystem, radiusKeys);
    }

    private static Shadow computeShadow(UIElement element, UIStyleSystem styleSystem) {
        StyleKey[] shadowKeys = {
                StyleKey.SHADOW_NONE, StyleKey.SHADOW_SM, StyleKey.SHADOW_MD,
                StyleKey.SHADOW_LG, StyleKey.SHADOW_XL, StyleKey.SHADOW_GLOW
        };
        for (StyleKey key : shadowKeys) {
            if (element.hasClass(key)) {
                return (Shadow) styleSystem.getStyleValues().get(key);
            }
        }
        return null;
    }

    private static int computeFlexGrow(UIElement element, UIStyleSystem styleSystem) {
        StyleKey[] keys = {StyleKey.FLEX_GROW_0, StyleKey.FLEX_GROW_1, StyleKey.FLEX_GROW_2, StyleKey.FLEX_GROW_3};
        return computeValueFromKeys(element, styleSystem, keys);
    }

    private static int computeFlexShrink(UIElement element, UIStyleSystem styleSystem) {
        StyleKey[] keys = {StyleKey.FLEX_SHRINK_0, StyleKey.FLEX_SHRINK_1};
        int computed = computeValueFromKeys(element, styleSystem, keys);
        return computed != 0 ? computed : 1;
    }

    private static int computeFlexBasis(UIElement element, UIStyleSystem styleSystem) {
        StyleKey[] keys = {
                StyleKey.FLEX_BASIS_0, StyleKey.FLEX_BASIS_10, StyleKey.FLEX_BASIS_15, StyleKey.FLEX_BASIS_20, StyleKey.FLEX_BASIS_25,
                StyleKey.FLEX_BASIS_30, StyleKey.FLEX_BASIS_33, StyleKey.FLEX_BASIS_40, StyleKey.FLEX_BASIS_50,
                StyleKey.FLEX_BASIS_60, StyleKey.FLEX_BASIS_66, StyleKey.FLEX_BASIS_75, StyleKey.FLEX_BASIS_100,
        };
        return computeValueFromKeys(element, styleSystem, keys);
    }

    private static int computePaddingTop(UIElement element, UIStyleSystem styleSystem) {
        StyleKey[] keys = {StyleKey.PT_0, StyleKey.PT_1, StyleKey.PT_2, StyleKey.PT_3,
                StyleKey.PT_4, StyleKey.PT_5, StyleKey.PT_6, StyleKey.PT_7, StyleKey.PT_8};
        int specific = computeValueFromKeys(element, styleSystem, keys);
        return specific != 0 ? specific : computePadding(element, styleSystem);
    }

    private static int computePaddingRight(UIElement element, UIStyleSystem styleSystem) {
        StyleKey[] keys = {StyleKey.PR_0, StyleKey.PR_1, StyleKey.PR_2, StyleKey.PR_3,
                StyleKey.PR_4, StyleKey.PR_5, StyleKey.PR_6, StyleKey.PR_7, StyleKey.PR_8};
        int specific = computeValueFromKeys(element, styleSystem, keys);
        return specific != 0 ? specific : computePadding(element, styleSystem);
    }

    private static int computePaddingBottom(UIElement element, UIStyleSystem styleSystem) {
        StyleKey[] keys = {StyleKey.PB_0, StyleKey.PB_1, StyleKey.PB_2, StyleKey.PB_3,
                StyleKey.PB_4, StyleKey.PB_5, StyleKey.PB_6, StyleKey.PB_7, StyleKey.PB_8};
        int specific = computeValueFromKeys(element, styleSystem, keys);
        return specific != 0 ? specific : computePadding(element, styleSystem);
    }

    private static int computePaddingLeft(UIElement element, UIStyleSystem styleSystem) {
        StyleKey[] keys = {StyleKey.PL_0, StyleKey.PL_1, StyleKey.PL_2, StyleKey.PL_3,
                StyleKey.PL_4, StyleKey.PL_5, StyleKey.PL_6, StyleKey.PL_7, StyleKey.PL_8};
        int specific = computeValueFromKeys(element, styleSystem, keys);
        return specific != 0 ? specific : computePadding(element, styleSystem);
    }

    private static int computePadding(UIElement element, UIStyleSystem styleSystem) {
        StyleKey[] keys = {StyleKey.P_0, StyleKey.P_1, StyleKey.P_2, StyleKey.P_3, StyleKey.P_4, StyleKey.P_5};
        return computeValueFromKeys(element, styleSystem, keys);
    }

    private static int computeMarginTop(UIElement element, UIStyleSystem styleSystem) {
        StyleKey[] keys = {StyleKey.MT_0, StyleKey.MT_1, StyleKey.MT_2, StyleKey.MT_3,
                StyleKey.MT_4, StyleKey.MT_5, StyleKey.MT_6, StyleKey.MT_7, StyleKey.MT_8};
        int specific = computeValueFromKeys(element, styleSystem, keys);
        return specific != 0 ? specific : computeMargin(element, styleSystem);
    }

    private static int computeMarginRight(UIElement element, UIStyleSystem styleSystem) {
        StyleKey[] keys = {StyleKey.MR_0, StyleKey.MR_1, StyleKey.MR_2, StyleKey.MR_3,
                StyleKey.MR_4, StyleKey.MR_5, StyleKey.MR_6, StyleKey.MR_7, StyleKey.MR_8};
        int specific = computeValueFromKeys(element, styleSystem, keys);
        return specific != 0 ? specific : computeMargin(element, styleSystem);
    }

    private static int computeMarginBottom(UIElement element, UIStyleSystem styleSystem) {
        StyleKey[] keys = {StyleKey.MB_0, StyleKey.MB_1, StyleKey.MB_2, StyleKey.MB_3,
                StyleKey.MB_4, StyleKey.MB_5, StyleKey.MB_6, StyleKey.MB_7, StyleKey.MB_8};
        int specific = computeValueFromKeys(element, styleSystem, keys);
        return specific != 0 ? specific : computeMargin(element, styleSystem);
    }

    private static int computeMarginLeft(UIElement element, UIStyleSystem styleSystem) {
        StyleKey[] keys = {StyleKey.ML_0, StyleKey.ML_1, StyleKey.ML_2, StyleKey.ML_3,
                StyleKey.ML_4, StyleKey.ML_5, StyleKey.ML_6, StyleKey.ML_7, StyleKey.ML_8};
        int specific = computeValueFromKeys(element, styleSystem, keys);
        return specific != 0 ? specific : computeMargin(element, styleSystem);
    }

    private static int computeMargin(UIElement element, UIStyleSystem styleSystem) {
        StyleKey[] keys = {StyleKey.M_0, StyleKey.M_1, StyleKey.M_2, StyleKey.M_3, StyleKey.M_4, StyleKey.M_5};
        return computeValueFromKeys(element, styleSystem, keys);
    }

    private static int computeGap(UIElement element, UIStyleSystem styleSystem) {
        StyleKey[] keys = {StyleKey.GAP_0, StyleKey.GAP_1, StyleKey.GAP_2, StyleKey.GAP_3,
                StyleKey.GAP_4, StyleKey.GAP_5, StyleKey.GAP_6, StyleKey.GAP_8};
        return computeValueFromKeys(element, styleSystem, keys);
    }

    private static int computeValueFromKeys(UIElement element, UIStyleSystem styleSystem, StyleKey... keys) {
        for (StyleKey key : keys) {
            if (element.hasClass(key)) {
                return styleSystem.getValue(key);
            }
        }
        return 0;
    }

    public static class ComputedStyles {
        private int backgroundColor = 0;
        private int textColor = 0xFF000000;
        private int borderRadius = 0;
        private Shadow shadow = null;

        private int paddingTop = 0;
        private int paddingRight = 0;
        private int paddingBottom = 0;
        private int paddingLeft = 0;

        private int marginTop = 0;
        private int marginRight = 0;
        private int marginBottom = 0;
        private int marginLeft = 0;

        private int gap = 0;

        private boolean hasHoverEffect = false;
        private boolean hasFocusRing = false;

        private int flexGrow = 0;
        private int flexShrink = 1;
        private int flexBasis = 0;

        private ComputedStyles() { /* should never be instanciated */}

        public int getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public int getTextColor() {
            return textColor;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }

        public int getBorderRadius() {
            return borderRadius;
        }

        public void setBorderRadius(int borderRadius) {
            this.borderRadius = borderRadius;
        }

        public Shadow getShadow() {
            return shadow;
        }

        public void setShadow(Shadow shadow) {
            this.shadow = shadow;
        }

        public int getPaddingTop() {
            return paddingTop;
        }

        public void setPaddingTop(int paddingTop) {
            this.paddingTop = paddingTop;
        }

        public int getPaddingRight() {
            return paddingRight;
        }

        public void setPaddingRight(int paddingRight) {
            this.paddingRight = paddingRight;
        }

        public int getPaddingBottom() {
            return paddingBottom;
        }

        public void setPaddingBottom(int paddingBottom) {
            this.paddingBottom = paddingBottom;
        }

        public int getPaddingLeft() {
            return paddingLeft;
        }

        public void setPaddingLeft(int paddingLeft) {
            this.paddingLeft = paddingLeft;
        }

        public int getMarginTop() {
            return marginTop;
        }

        public void setMarginTop(int marginTop) {
            this.marginTop = marginTop;
        }

        public int getMarginRight() {
            return marginRight;
        }

        public void setMarginRight(int marginRight) {
            this.marginRight = marginRight;
        }

        public int getMarginBottom() {
            return marginBottom;
        }

        public void setMarginBottom(int marginBottom) {
            this.marginBottom = marginBottom;
        }

        public int getMarginLeft() {
            return marginLeft;
        }

        public void setMarginLeft(int marginLeft) {
            this.marginLeft = marginLeft;
        }

        public int getGap() {
            return gap;
        }

        public void setGap(int gap) {
            this.gap = gap;
        }

        public boolean isHasHoverEffect() {
            return hasHoverEffect;
        }

        public void setHasHoverEffect(boolean hasHoverEffect) {
            this.hasHoverEffect = hasHoverEffect;
        }

        public boolean isHasFocusRing() {
            return hasFocusRing;
        }

        public void setHasFocusRing(boolean hasFocusRing) {
            this.hasFocusRing = hasFocusRing;
        }

        public int getFlexGrow() {
            return flexGrow;
        }

        public void setFlexGrow(int flexGrow) {
            this.flexGrow = flexGrow;
        }

        public int getFlexShrink() {
            return flexShrink;
        }

        public void setFlexShrink(int flexShrink) {
            this.flexShrink = flexShrink;
        }

        public int getFlexBasis() {
            return flexBasis;
        }

        public void setFlexBasis(int flexBasis) {
            this.flexBasis = flexBasis;
        }
    }
}
