package com.edgn.examples;

import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.core.container.containers.FlexContainer;
import com.edgn.ui.core.item.items.SliderItem;
import com.edgn.ui.core.models.slider.*;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.template.BaseTemplate;
import com.edgn.ui.template.TemplateSettings;
import com.edgn.ui.utils.ColorUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class SliderExample extends BaseTemplate {
    protected SliderExample(Screen prevScreen) {
        super(Text.literal("Slider Example"), prevScreen);
    }

    @Override
    protected TemplateSettings templateSettings() {
        return new TemplateSettings().setToDefault();
    }

    @Override
    protected BaseContainer createHeader() {
        return null;
    }

    @Override
    protected BaseContainer createContent() {
        FlexContainer content = new FlexContainer(uiSystem, 0, getHeaderHeight(), this.width, getContentHeight())
                .addClass(
                        StyleKey.FLEX_ROW,
                        StyleKey.FLEX_WRAP,
                        StyleKey.ITEMS_STRETCH,
                        StyleKey.JUSTIFY_BETWEEN,
                        StyleKey.GAP_8,
                        StyleKey.P_4,
                        StyleKey.M_5,
                        StyleKey.BG_BACKGROUND
                );

        SliderItem<Integer> intSliderItem = new SliderItem<>(uiSystem, 0, 0, 160, 28, new IntSliderModel())
                .valuePosition(SliderItem.ValuePosition.TOP)
                .formatter(v -> v + " %")
                .trackHeight(6)
                .thumbSize(10)
                .trackColor(ColorUtils.NamedColor.AQUAMARINE.toInt())
                .fillColor(ColorUtils.NamedColor.CORNSILK.toInt())
                .thumbColor(ColorUtils.NamedColor.IVORY.toInt())
                .textBold()
                .textRainbow()
                .addClass(StyleKey.FLEX_BASIS_25);

        SliderItem<Float> floatSliderItem = new SliderItem<>(uiSystem, 0, 0, 160, 28, new FloatSliderModel())
                .trackHeight(6)
                .thumbSize(15)
                .trackColor(ColorUtils.NamedColor.AQUAMARINE.toInt())
                .fillColor(ColorUtils.NamedColor.CORNSILK.toInt())
                .thumbColor(ColorUtils.NamedColor.IVORY.toInt())
                .addClass(StyleKey.FLEX_BASIS_25);

        SliderItem<Double> doubleSliderItem = new SliderItem<>(uiSystem, 0, 0, 160, 28, new DoubleSliderModel())
                .trackHeight(6)
                .thumbSize(12)
                .trackColor(ColorUtils.NamedColor.AQUAMARINE.toInt())
                .fillColor(ColorUtils.NamedColor.CORNSILK.toInt())
                .thumbColor(ColorUtils.NamedColor.IVORY.toInt())
                .addClass(StyleKey.FLEX_BASIS_25);

        return content.addChild(intSliderItem).addChild(floatSliderItem).addChild(doubleSliderItem);
    }

    @Override
    protected BaseContainer createFooter() {
        return null;
    }
}
