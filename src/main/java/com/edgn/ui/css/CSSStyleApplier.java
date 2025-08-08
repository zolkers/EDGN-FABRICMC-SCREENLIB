package com.edgn.ui.css;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.css.rules.Shadow;

public class CSSStyleApplier {

    public static ComputedStyles computeStyles(UIElement element) {
        UIStyleSystem styleSystem = element.getStyleSystem();
        ComputedStyles styles = new ComputedStyles();

        styles.backgroundColor = computeBackgroundColor(element, styleSystem);
        styles.textColor = computeTextColor(element, styleSystem);

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

    private static int computeBackgroundColor(UIElement element, UIStyleSystem styleSystem) {
        StyleKey baseColorKey = null;
        StyleKey[] colorKeys = {
                StyleKey.PRIMARY, StyleKey.BG_PRIMARY,
                StyleKey.SECONDARY, StyleKey.BG_SECONDARY,
                StyleKey.SUCCESS, StyleKey.BG_SUCCESS,
                StyleKey.DANGER, StyleKey.BG_DANGER,
                StyleKey.WARNING, StyleKey.BG_WARNING,
                StyleKey.INFO, StyleKey.BG_INFO,
                StyleKey.BG_SURFACE, StyleKey.BG_BACKGROUND, StyleKey.BG_GLASS
        };

        for (StyleKey key : colorKeys) {
            if (element.hasClass(key)) {
                baseColorKey = switch (key) {
                    case PRIMARY, BG_PRIMARY -> StyleKey.PRIMARY;
                    case SECONDARY, BG_SECONDARY -> StyleKey.SECONDARY;
                    case SUCCESS, BG_SUCCESS -> StyleKey.SUCCESS;
                    case DANGER, BG_DANGER -> StyleKey.DANGER;
                    case WARNING, BG_WARNING -> StyleKey.WARNING;
                    case INFO, BG_INFO -> StyleKey.INFO;
                    case BG_SURFACE -> StyleKey.SURFACE;
                    case BG_BACKGROUND -> StyleKey.BACKGROUND;
                    case BG_GLASS -> StyleKey.GLASS;
                    default -> null;
                };
                break;
            }
        }

        StyleKey opacityKey = null;
        StyleKey[] opacityKeys = {
                StyleKey.BG_OPACITY_0, StyleKey.BG_OPACITY_1, StyleKey.BG_OPACITY_2, StyleKey.BG_OPACITY_3,
                StyleKey.BG_OPACITY_4, StyleKey.BG_OPACITY_5, StyleKey.BG_OPACITY_6, StyleKey.BG_OPACITY_7,
                StyleKey.BG_OPACITY_8, StyleKey.BG_OPACITY_9, StyleKey.BG_OPACITY_10, StyleKey.BG_OPACITY_11,
                StyleKey.BG_OPACITY_12, StyleKey.BG_OPACITY_13, StyleKey.BG_OPACITY_14, StyleKey.BG_OPACITY_15
        };

        for (StyleKey key : opacityKeys) {
            if (element.hasClass(key)) {
                opacityKey = key;
                break;
            }
        }

        if (baseColorKey != null) {
            int baseColor = styleSystem.getColor(baseColorKey);
            if (opacityKey != null) {
                float opacity = styleSystem.getOpacity(opacityKey);
                return UIStyleSystem.applyOpacity(baseColor, opacity);
            } else {
                return baseColor;
            }
        } else if (opacityKey != null) {
            float opacity = styleSystem.getOpacity(opacityKey);
            return UIStyleSystem.applyOpacity(0xFF000000, opacity);
        }

        return 0;
    }

    private static int computeTextColor(UIElement element, UIStyleSystem styleSystem) {
        StyleKey[] textKeys = {
                StyleKey.TEXT_WHITE, StyleKey.PRIMARY, StyleKey.SECONDARY,
                StyleKey.SUCCESS, StyleKey.DANGER, StyleKey.WARNING, StyleKey.INFO
        };

        for (StyleKey key : textKeys) {
            if (element.hasClass(key)) {
                return switch (key) {
                    case TEXT_WHITE -> 0xFFFFFFFF;
                    case PRIMARY -> styleSystem.getColor(StyleKey.PRIMARY);
                    case SECONDARY -> styleSystem.getColor(StyleKey.SECONDARY);
                    case SUCCESS -> styleSystem.getColor(StyleKey.SUCCESS);
                    case DANGER -> styleSystem.getColor(StyleKey.DANGER);
                    case WARNING -> styleSystem.getColor(StyleKey.WARNING);
                    case INFO -> styleSystem.getColor(StyleKey.INFO);
                    default -> 0xFF000000;
                };
            }
        }
        return 0xFF000000;
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
        public int backgroundColor = 0;
        public int textColor = 0xFF000000;
        public int borderRadius = 0;
        public Shadow shadow = null;
        public int paddingTop = 0, paddingRight = 0, paddingBottom = 0, paddingLeft = 0;
        public int marginTop = 0, marginRight = 0, marginBottom = 0, marginLeft = 0;
        public int gap = 0;
        public boolean hasHoverEffect = false;
        public boolean hasFocusRing = false;
        public int flexGrow = 0;
        public int flexShrink = 1;
        public int flexBasis = 0;
    }
}
