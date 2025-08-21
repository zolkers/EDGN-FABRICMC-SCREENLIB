package com.edgn.ui.css;


import com.edgn.ui.css.values.*;
import com.edgn.ui.event.UIEventManager;

import java.util.EnumMap;
import java.util.Map;

@SuppressWarnings("unused")
public class UIStyleSystem {
    private final Map<StyleKey, Object> styleValues = new EnumMap<>(StyleKey.class);
    private final UIEventManager eventManager = new UIEventManager();

    public UIStyleSystem() {
        initializeDefaults();
    }

    private void initializeDefaults() {
        styleValues.put(StyleKey.ROUNDED_NONE, BorderRadius.NONE.value);    
        styleValues.put(StyleKey.ROUNDED_SM, BorderRadius.SM.value);        
        styleValues.put(StyleKey.ROUNDED_MD, BorderRadius.MD.value);        
        styleValues.put(StyleKey.ROUNDED_LG, BorderRadius.LG.value);        
        styleValues.put(StyleKey.ROUNDED_XL, BorderRadius.XL.value);        
        styleValues.put(StyleKey.ROUNDED_XXL, BorderRadius.XXL.value);      
        styleValues.put(StyleKey.ROUNDED_FULL, BorderRadius.FULL.value);    

        styleValues.put(StyleKey.P_0, Spacing.NONE.value);     
        styleValues.put(StyleKey.P_1, Spacing.XS.value);       
        styleValues.put(StyleKey.P_2, Spacing.SM.value);       
        styleValues.put(StyleKey.P_3, Spacing.MD.value);       
        styleValues.put(StyleKey.P_4, Spacing.LG.value);       
        styleValues.put(StyleKey.P_5, Spacing.XL.value);       

        styleValues.put(StyleKey.PT_0, Spacing.NONE.value);    
        styleValues.put(StyleKey.PT_1, Spacing.XS.value);      
        styleValues.put(StyleKey.PT_2, Spacing.SM.value);      
        styleValues.put(StyleKey.PT_3, Spacing.MD.value);      
        styleValues.put(StyleKey.PT_4, Spacing.LG.value);      
        styleValues.put(StyleKey.PT_5, Spacing.XL.value);      
        styleValues.put(StyleKey.PT_6, Spacing.XXL.value);     
        styleValues.put(StyleKey.PT_7, Spacing.XXXL.value);    
        styleValues.put(StyleKey.PT_8, Spacing.XXXL.value);

        styleValues.put(StyleKey.PR_0, Spacing.NONE.value);    
        styleValues.put(StyleKey.PR_1, Spacing.XS.value);      
        styleValues.put(StyleKey.PR_2, Spacing.SM.value);      
        styleValues.put(StyleKey.PR_3, Spacing.MD.value);      
        styleValues.put(StyleKey.PR_4, Spacing.LG.value);      
        styleValues.put(StyleKey.PR_5, Spacing.XL.value);      
        styleValues.put(StyleKey.PR_6, Spacing.XXL.value);     
        styleValues.put(StyleKey.PR_7, Spacing.XXXL.value);    
        styleValues.put(StyleKey.PR_8, Spacing.XXXL.value);

        styleValues.put(StyleKey.PB_0, Spacing.NONE.value);    
        styleValues.put(StyleKey.PB_1, Spacing.XS.value);      
        styleValues.put(StyleKey.PB_2, Spacing.SM.value);      
        styleValues.put(StyleKey.PB_3, Spacing.MD.value);      
        styleValues.put(StyleKey.PB_4, Spacing.LG.value);      
        styleValues.put(StyleKey.PB_5, Spacing.XL.value);      
        styleValues.put(StyleKey.PB_6, Spacing.XXL.value);     
        styleValues.put(StyleKey.PB_7, Spacing.XXXL.value);    
        styleValues.put(StyleKey.PB_8, Spacing.XXXL.value);

        styleValues.put(StyleKey.PL_0, Spacing.NONE.value);    
        styleValues.put(StyleKey.PL_1, Spacing.XS.value);      
        styleValues.put(StyleKey.PL_2, Spacing.SM.value);      
        styleValues.put(StyleKey.PL_3, Spacing.MD.value);      
        styleValues.put(StyleKey.PL_4, Spacing.LG.value);      
        styleValues.put(StyleKey.PL_5, Spacing.XL.value);      
        styleValues.put(StyleKey.PL_6, Spacing.XXL.value);     
        styleValues.put(StyleKey.PL_7, Spacing.XXXL.value);    
        styleValues.put(StyleKey.PL_8, Spacing.XXXL.value);

        styleValues.put(StyleKey.M_0, Spacing.NONE.value);     
        styleValues.put(StyleKey.M_1, Spacing.XS.value);       
        styleValues.put(StyleKey.M_2, Spacing.SM.value);       
        styleValues.put(StyleKey.M_3, Spacing.MD.value);       
        styleValues.put(StyleKey.M_4, Spacing.LG.value);       
        styleValues.put(StyleKey.M_5, Spacing.XL.value);       

        styleValues.put(StyleKey.MT_0, Spacing.NONE.value);    
        styleValues.put(StyleKey.MT_1, Spacing.XS.value);      
        styleValues.put(StyleKey.MT_2, Spacing.SM.value);      
        styleValues.put(StyleKey.MT_3, Spacing.MD.value);      
        styleValues.put(StyleKey.MT_4, Spacing.LG.value);      
        styleValues.put(StyleKey.MT_5, Spacing.XL.value);      
        styleValues.put(StyleKey.MT_6, Spacing.XXL.value);     
        styleValues.put(StyleKey.MT_7, Spacing.XXXL.value);    
        styleValues.put(StyleKey.MT_8, Spacing.XXXL.value);

        styleValues.put(StyleKey.MR_0, Spacing.NONE.value);    
        styleValues.put(StyleKey.MR_1, Spacing.XS.value);      
        styleValues.put(StyleKey.MR_2, Spacing.SM.value);      
        styleValues.put(StyleKey.MR_3, Spacing.MD.value);      
        styleValues.put(StyleKey.MR_4, Spacing.LG.value);      
        styleValues.put(StyleKey.MR_5, Spacing.XL.value);      
        styleValues.put(StyleKey.MR_6, Spacing.XXL.value);     
        styleValues.put(StyleKey.MR_7, Spacing.XXXL.value);    
        styleValues.put(StyleKey.MR_8, Spacing.XXXL.value);

        styleValues.put(StyleKey.MB_0, Spacing.NONE.value);    
        styleValues.put(StyleKey.MB_1, Spacing.XS.value);      
        styleValues.put(StyleKey.MB_2, Spacing.SM.value);      
        styleValues.put(StyleKey.MB_3, Spacing.MD.value);      
        styleValues.put(StyleKey.MB_4, Spacing.LG.value);      
        styleValues.put(StyleKey.MB_5, Spacing.XL.value);      
        styleValues.put(StyleKey.MB_6, Spacing.XXL.value);     
        styleValues.put(StyleKey.MB_7, Spacing.XXXL.value);    
        styleValues.put(StyleKey.MB_8, Spacing.XXXL.value);

        styleValues.put(StyleKey.ML_0, Spacing.NONE.value);    
        styleValues.put(StyleKey.ML_1, Spacing.XS.value);      
        styleValues.put(StyleKey.ML_2, Spacing.SM.value);      
        styleValues.put(StyleKey.ML_3, Spacing.MD.value);      
        styleValues.put(StyleKey.ML_4, Spacing.LG.value);      
        styleValues.put(StyleKey.ML_5, Spacing.XL.value);      
        styleValues.put(StyleKey.ML_6, Spacing.XXL.value);     
        styleValues.put(StyleKey.ML_7, Spacing.XXXL.value);    
        styleValues.put(StyleKey.ML_8, Spacing.XXXXL.value);   

        styleValues.put(StyleKey.GAP_0, Spacing.NONE.value);   
        styleValues.put(StyleKey.GAP_1, Spacing.XS.value);     
        styleValues.put(StyleKey.GAP_2, Spacing.SM.value);     
        styleValues.put(StyleKey.GAP_3, Spacing.MD.value);     
        styleValues.put(StyleKey.GAP_4, Spacing.LG.value);     
        styleValues.put(StyleKey.GAP_5, Spacing.XL.value);     
        styleValues.put(StyleKey.GAP_6, Spacing.XXL.value);    
        styleValues.put(StyleKey.GAP_8, Spacing.XXXL.value);

        styleValues.put(StyleKey.FLEX_BASIS_0,   FlexBasis.BASIS_0.value);
        styleValues.put(StyleKey.FLEX_BASIS_10,  FlexBasis.BASIS_10.value);
        styleValues.put(StyleKey.FLEX_BASIS_15,  FlexBasis.BASIS_15.value);
        styleValues.put(StyleKey.FLEX_BASIS_20,  FlexBasis.BASIS_20.value);
        styleValues.put(StyleKey.FLEX_BASIS_25,  FlexBasis.BASIS_25.value);
        styleValues.put(StyleKey.FLEX_BASIS_30,  FlexBasis.BASIS_30.value);
        styleValues.put(StyleKey.FLEX_BASIS_33,  FlexBasis.BASIS_33.value);
        styleValues.put(StyleKey.FLEX_BASIS_40,  FlexBasis.BASIS_40.value);
        styleValues.put(StyleKey.FLEX_BASIS_50,  FlexBasis.BASIS_50.value);
        styleValues.put(StyleKey.FLEX_BASIS_60,  FlexBasis.BASIS_60.value);
        styleValues.put(StyleKey.FLEX_BASIS_66,  FlexBasis.BASIS_66.value);
        styleValues.put(StyleKey.FLEX_BASIS_75,  FlexBasis.BASIS_75.value);
        styleValues.put(StyleKey.FLEX_BASIS_100, FlexBasis.BASIS_100.value);

        styleValues.put(StyleKey.FLEX_GROW_0, FlexGrow.GROW_0.value);
        styleValues.put(StyleKey.FLEX_GROW_1, FlexGrow.GROW_1.value);
        styleValues.put(StyleKey.FLEX_GROW_2, FlexGrow.GROW_2.value);
        styleValues.put(StyleKey.FLEX_GROW_3, FlexGrow.GROW_3.value);

        styleValues.put(StyleKey.FLEX_SHRINK_0, FlexShrink.SHRINK_0.value);
        styleValues.put(StyleKey.FLEX_SHRINK_1, FlexShrink.SHRINK_1.value);

        styleValues.put(StyleKey.SHADOW_NONE, Shadow.NONE);
        styleValues.put(StyleKey.SHADOW_SM, Shadow.SM);
        styleValues.put(StyleKey.SHADOW_MD, Shadow.MD);
        styleValues.put(StyleKey.SHADOW_LG, Shadow.LG);
        styleValues.put(StyleKey.SHADOW_XL, Shadow.XL);
        styleValues.put(StyleKey.SHADOW_GLOW, Shadow.GLOW);

        styleValues.put(StyleKey.HOVER_SCALE, HoverEffect.SCALE.value);
        styleValues.put(StyleKey.HOVER_BRIGHTEN, HoverEffect.BRIGHTEN.value);
        styleValues.put(StyleKey.HOVER_OPACITY, HoverEffect.OPACITY.value);
        styleValues.put(StyleKey.HOVER_ROTATE, HoverEffect.ROTATE.value);

        styleValues.put(StyleKey.FOCUS_RING, FocusEffect.RING.value);
        styleValues.put(StyleKey.FOCUS_OUTLINE, FocusEffect.OUTLINE.value);

        styleValues.put(StyleKey.ACTIVE_SCALE, ActiveEffect.SCALE.value);
    }

    public int getValue(StyleKey key) {
        Object value = styleValues.get(key);
        if (value instanceof Integer integer) return integer;
        if (value instanceof Float floatValue) return Math.round(floatValue * 100);
        return 0;
    }

    public static int applyOpacity(int color, float opacity) {
        int alpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        int newAlpha = Math.round(alpha * Math.clamp(opacity, 0.0f, 1.0f));

        return (newAlpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public Map<StyleKey, Object> getStyleValues() { return styleValues; }
    public UIEventManager getEventManager() { return eventManager; }
}