package com.edgn.ui.css;


import com.edgn.ui.css.rules.BorderRadius;
import com.edgn.ui.css.rules.Shadow;
import com.edgn.ui.css.rules.Spacing;
import com.edgn.ui.event.UIEventManager;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class UIStyleSystem {
    private final Map<StyleKey, Integer> colorMap = new HashMap<>();
    private final Map<StyleKey, Object> styleValues = new HashMap<>();
    private final UIEventManager eventManager = new UIEventManager();

    public UIStyleSystem() {
        initializeDefaults();
    }

    private void initializeDefaults() {
        colorMap.put(StyleKey.PRIMARY, 0xFF0D6EFD);
        colorMap.put(StyleKey.SECONDARY, 0xFF6C757D);
        colorMap.put(StyleKey.SUCCESS, 0xFF198754);
        colorMap.put(StyleKey.DANGER, 0xFFDC3545);
        colorMap.put(StyleKey.WARNING, 0xFFFFC107);
        colorMap.put(StyleKey.INFO, 0xFF0DCAF0);
        colorMap.put(StyleKey.LIGHT, 0xFFF8F9FA);
        colorMap.put(StyleKey.DARK, 0xFF212529);
        colorMap.put(StyleKey.SURFACE, 0xFF2C2C2C);
        colorMap.put(StyleKey.BACKGROUND, 0xFF1A1A1A);
        colorMap.put(StyleKey.ACCENT, 0xFF7C3AED);
        colorMap.put(StyleKey.MUTED, 0xFF6B7280);
        colorMap.put(StyleKey.GLASS, 0x80FFFFFF);
        colorMap.put(StyleKey.NEON, 0xFF00FFFF);

        colorMap.put(StyleKey.PRIMARY_LIGHT, 0xFF6EA8FE);
        colorMap.put(StyleKey.PRIMARY_DARK, 0xFF0A58CA);
        colorMap.put(StyleKey.SUCCESS_LIGHT, 0xFF75B798);
        colorMap.put(StyleKey.DANGER_LIGHT, 0xFFE1798C);

        styleValues.put(StyleKey.BG_OPACITY_0, 0.0f);
        styleValues.put(StyleKey.BG_OPACITY_1, 0.0625f);   // 1/16
        styleValues.put(StyleKey.BG_OPACITY_2, 0.125f);    // 2/16
        styleValues.put(StyleKey.BG_OPACITY_3, 0.1875f);   // 3/16
        styleValues.put(StyleKey.BG_OPACITY_4, 0.25f);     // 4/16
        styleValues.put(StyleKey.BG_OPACITY_5, 0.3125f);   // 5/16
        styleValues.put(StyleKey.BG_OPACITY_6, 0.375f);    // 6/16
        styleValues.put(StyleKey.BG_OPACITY_7, 0.4375f);   // 7/16
        styleValues.put(StyleKey.BG_OPACITY_8, 0.5f);      // 8/16
        styleValues.put(StyleKey.BG_OPACITY_9, 0.5625f);   // 9/16
        styleValues.put(StyleKey.BG_OPACITY_10, 0.625f);   // 10/16
        styleValues.put(StyleKey.BG_OPACITY_11, 0.6875f);  // 11/16
        styleValues.put(StyleKey.BG_OPACITY_12, 0.75f);    // 12/16
        styleValues.put(StyleKey.BG_OPACITY_13, 0.8125f);  // 13/16
        styleValues.put(StyleKey.BG_OPACITY_14, 0.875f);   // 14/16
        styleValues.put(StyleKey.BG_OPACITY_15, 0.9375f);  // 15/16

        styleValues.put(StyleKey.ROUNDED_NONE, BorderRadius.NONE.value);    // 0
        styleValues.put(StyleKey.ROUNDED_SM, BorderRadius.SM.value);        // 2
        styleValues.put(StyleKey.ROUNDED_MD, BorderRadius.MD.value);        // 4
        styleValues.put(StyleKey.ROUNDED_LG, BorderRadius.LG.value);        // 8
        styleValues.put(StyleKey.ROUNDED_XL, BorderRadius.XL.value);        // 12
        styleValues.put(StyleKey.ROUNDED_XXL, BorderRadius.XXL.value);      // 16
        styleValues.put(StyleKey.ROUNDED_FULL, BorderRadius.FULL.value);    // 9999

        styleValues.put(StyleKey.P_0, Spacing.NONE.value);     // 0
        styleValues.put(StyleKey.P_1, Spacing.XS.value);       // 2
        styleValues.put(StyleKey.P_2, Spacing.SM.value);       // 4
        styleValues.put(StyleKey.P_3, Spacing.MD.value);       // 8
        styleValues.put(StyleKey.P_4, Spacing.LG.value);       // 12
        styleValues.put(StyleKey.P_5, Spacing.XL.value);       // 16

        styleValues.put(StyleKey.PT_0, Spacing.NONE.value);    // 0
        styleValues.put(StyleKey.PT_1, Spacing.XS.value);      // 2
        styleValues.put(StyleKey.PT_2, Spacing.SM.value);      // 4
        styleValues.put(StyleKey.PT_3, Spacing.MD.value);      // 8
        styleValues.put(StyleKey.PT_4, Spacing.LG.value);      // 12
        styleValues.put(StyleKey.PT_5, Spacing.XL.value);      // 16
        styleValues.put(StyleKey.PT_6, Spacing.XXL.value);     // 24
        styleValues.put(StyleKey.PT_7, Spacing.XXXL.value);    // 32
        styleValues.put(StyleKey.PT_8, 48);

        styleValues.put(StyleKey.PR_0, Spacing.NONE.value);    // 0
        styleValues.put(StyleKey.PR_1, Spacing.XS.value);      // 2
        styleValues.put(StyleKey.PR_2, Spacing.SM.value);      // 4
        styleValues.put(StyleKey.PR_3, Spacing.MD.value);      // 8
        styleValues.put(StyleKey.PR_4, Spacing.LG.value);      // 12
        styleValues.put(StyleKey.PR_5, Spacing.XL.value);      // 16
        styleValues.put(StyleKey.PR_6, Spacing.XXL.value);     // 24
        styleValues.put(StyleKey.PR_7, Spacing.XXXL.value);    // 32
        styleValues.put(StyleKey.PR_8, 48);

        styleValues.put(StyleKey.PB_0, Spacing.NONE.value);    // 0
        styleValues.put(StyleKey.PB_1, Spacing.XS.value);      // 2
        styleValues.put(StyleKey.PB_2, Spacing.SM.value);      // 4
        styleValues.put(StyleKey.PB_3, Spacing.MD.value);      // 8
        styleValues.put(StyleKey.PB_4, Spacing.LG.value);      // 12
        styleValues.put(StyleKey.PB_5, Spacing.XL.value);      // 16
        styleValues.put(StyleKey.PB_6, Spacing.XXL.value);     // 24
        styleValues.put(StyleKey.PB_7, Spacing.XXXL.value);    // 32
        styleValues.put(StyleKey.PB_8, 48);

        styleValues.put(StyleKey.PL_0, Spacing.NONE.value);    // 0
        styleValues.put(StyleKey.PL_1, Spacing.XS.value);      // 2
        styleValues.put(StyleKey.PL_2, Spacing.SM.value);      // 4
        styleValues.put(StyleKey.PL_3, Spacing.MD.value);      // 8
        styleValues.put(StyleKey.PL_4, Spacing.LG.value);      // 12
        styleValues.put(StyleKey.PL_5, Spacing.XL.value);      // 16
        styleValues.put(StyleKey.PL_6, Spacing.XXL.value);     // 24
        styleValues.put(StyleKey.PL_7, Spacing.XXXL.value);    // 32
        styleValues.put(StyleKey.PL_8, 48);

        styleValues.put(StyleKey.M_0, Spacing.NONE.value);     // 0
        styleValues.put(StyleKey.M_1, Spacing.XS.value);       // 2
        styleValues.put(StyleKey.M_2, Spacing.SM.value);       // 4
        styleValues.put(StyleKey.M_3, Spacing.MD.value);       // 8
        styleValues.put(StyleKey.M_4, Spacing.LG.value);       // 12
        styleValues.put(StyleKey.M_5, Spacing.XL.value);       // 16

        styleValues.put(StyleKey.MT_0, Spacing.NONE.value);    // 0
        styleValues.put(StyleKey.MT_1, Spacing.XS.value);      // 2
        styleValues.put(StyleKey.MT_2, Spacing.SM.value);      // 4
        styleValues.put(StyleKey.MT_3, Spacing.MD.value);      // 8
        styleValues.put(StyleKey.MT_4, Spacing.LG.value);      // 12
        styleValues.put(StyleKey.MT_5, Spacing.XL.value);      // 16
        styleValues.put(StyleKey.MT_6, Spacing.XXL.value);     // 24
        styleValues.put(StyleKey.MT_7, Spacing.XXXL.value);    // 32
        styleValues.put(StyleKey.MT_8, 48);

        styleValues.put(StyleKey.MR_0, Spacing.NONE.value);    // 0
        styleValues.put(StyleKey.MR_1, Spacing.XS.value);      // 2
        styleValues.put(StyleKey.MR_2, Spacing.SM.value);      // 4
        styleValues.put(StyleKey.MR_3, Spacing.MD.value);      // 8
        styleValues.put(StyleKey.MR_4, Spacing.LG.value);      // 12
        styleValues.put(StyleKey.MR_5, Spacing.XL.value);      // 16
        styleValues.put(StyleKey.MR_6, Spacing.XXL.value);     // 24
        styleValues.put(StyleKey.MR_7, Spacing.XXXL.value);    // 32
        styleValues.put(StyleKey.MR_8, 48);

        styleValues.put(StyleKey.MB_0, Spacing.NONE.value);    // 0
        styleValues.put(StyleKey.MB_1, Spacing.XS.value);      // 2
        styleValues.put(StyleKey.MB_2, Spacing.SM.value);      // 4
        styleValues.put(StyleKey.MB_3, Spacing.MD.value);      // 8
        styleValues.put(StyleKey.MB_4, Spacing.LG.value);      // 12
        styleValues.put(StyleKey.MB_5, Spacing.XL.value);      // 16
        styleValues.put(StyleKey.MB_6, Spacing.XXL.value);     // 24
        styleValues.put(StyleKey.MB_7, Spacing.XXXL.value);    // 32
        styleValues.put(StyleKey.MB_8, 48);

        styleValues.put(StyleKey.ML_0, Spacing.NONE.value);    // 0
        styleValues.put(StyleKey.ML_1, Spacing.XS.value);      // 2
        styleValues.put(StyleKey.ML_2, Spacing.SM.value);      // 4
        styleValues.put(StyleKey.ML_3, Spacing.MD.value);      // 8
        styleValues.put(StyleKey.ML_4, Spacing.LG.value);      // 12
        styleValues.put(StyleKey.ML_5, Spacing.XL.value);      // 16
        styleValues.put(StyleKey.ML_6, Spacing.XXL.value);     // 24
        styleValues.put(StyleKey.ML_7, Spacing.XXXL.value);    // 32
        styleValues.put(StyleKey.ML_8, 48);

        styleValues.put(StyleKey.GAP_0, Spacing.NONE.value);   // 0
        styleValues.put(StyleKey.GAP_1, Spacing.XS.value);     // 2
        styleValues.put(StyleKey.GAP_2, Spacing.SM.value);     // 4
        styleValues.put(StyleKey.GAP_3, Spacing.MD.value);     // 8
        styleValues.put(StyleKey.GAP_4, Spacing.LG.value);     // 12
        styleValues.put(StyleKey.GAP_5, Spacing.XL.value);     // 16
        styleValues.put(StyleKey.GAP_6, Spacing.XXL.value);    // 24
        styleValues.put(StyleKey.GAP_8, Spacing.XXXL.value);   // 32

        styleValues.put(StyleKey.FLEX_BASIS_0,   0);
        styleValues.put(StyleKey.FLEX_BASIS_10, 10);
        styleValues.put(StyleKey.FLEX_BASIS_15, 15);
        styleValues.put(StyleKey.FLEX_BASIS_20, 20);
        styleValues.put(StyleKey.FLEX_BASIS_25, 25);
        styleValues.put(StyleKey.FLEX_BASIS_30, 30);
        styleValues.put(StyleKey.FLEX_BASIS_33, 33);
        styleValues.put(StyleKey.FLEX_BASIS_40, 40);
        styleValues.put(StyleKey.FLEX_BASIS_50, 50);
        styleValues.put(StyleKey.FLEX_BASIS_60, 60);
        styleValues.put(StyleKey.FLEX_BASIS_66, 66);
        styleValues.put(StyleKey.FLEX_BASIS_75, 75);
        styleValues.put(StyleKey.FLEX_BASIS_100, 100);

        styleValues.put(StyleKey.FLEX_GROW_0, 0);
        styleValues.put(StyleKey.FLEX_GROW_1, 1);
        styleValues.put(StyleKey.FLEX_GROW_2, 2);
        styleValues.put(StyleKey.FLEX_GROW_3, 3);

        styleValues.put(StyleKey.FLEX_SHRINK_0, 0);
        styleValues.put(StyleKey.FLEX_SHRINK_1, 1);

        styleValues.put(StyleKey.SHADOW_NONE, Shadow.NONE);
        styleValues.put(StyleKey.SHADOW_SM, Shadow.SM);
        styleValues.put(StyleKey.SHADOW_MD, Shadow.MD);
        styleValues.put(StyleKey.SHADOW_LG, Shadow.LG);
        styleValues.put(StyleKey.SHADOW_XL, Shadow.XL);
        styleValues.put(StyleKey.SHADOW_GLOW, Shadow.GLOW);

        styleValues.put(StyleKey.HOVER_SCALE, 1.05f);
        styleValues.put(StyleKey.HOVER_BRIGHTEN, 1.2f);
        styleValues.put(StyleKey.HOVER_OPACITY, 0.8f);
        styleValues.put(StyleKey.HOVER_ROTATE, 5.0f);

        styleValues.put(StyleKey.FOCUS_RING, true);
        styleValues.put(StyleKey.FOCUS_OUTLINE, true);
        styleValues.put(StyleKey.ACTIVE_SCALE, 0.95f);
    }

    public int getColor(StyleKey key) {
        return colorMap.getOrDefault(key, 0xFFFFFFFF);
    }

    public int getValue(StyleKey key) {
        Object value = styleValues.get(key);
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Float) return Math.round((Float) value * 100);
        return 0;
    }

    public float getOpacity(StyleKey key) {
        Object value = styleValues.get(key);
        if (value instanceof Float) {
            return (Float) value;
        }
        return 1.0f;
    }

    public static int applyOpacity(int color, float opacity) {
        int alpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        int newAlpha = Math.round(alpha * Math.max(0.0f, Math.min(1.0f, opacity)));

        return (newAlpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public int getColorWithOpacity(StyleKey baseColorKey, StyleKey opacityKey) {
        int baseColor = getColor(baseColorKey);
        float opacity = getOpacity(opacityKey);
        return applyOpacity(baseColor, opacity);
    }

    public static boolean isOpacityKey(StyleKey key) {
        return key.name().startsWith("BG_OPACITY_");
    }

    public Map<StyleKey, Object> getStyleValues() { return styleValues; }
    public Map<StyleKey, Integer> getColorMap() { return colorMap; }
    public UIEventManager getEventManager() { return eventManager; }
}