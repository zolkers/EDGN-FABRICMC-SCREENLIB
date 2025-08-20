package com.edgn.examples;

import com.edgn.ui.core.container.components.TextComponent;
import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.core.container.containers.FlexContainer;
import com.edgn.ui.core.item.items.ButtonItem;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.template.BaseTemplate;
import com.edgn.ui.template.TemplateSettings;
import com.edgn.ui.utils.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class FlexContainerExample extends BaseTemplate {

    public FlexContainerExample(Screen prev) {
        super(Text.literal("FlexContainerExample"), prev);
    }

    @Override
    protected TemplateSettings templateSettings() {
        return new TemplateSettings().setToDefault();
    }

    @Override
    protected BaseContainer createHeader() {
        FlexContainer header = new FlexContainer(uiSystem, 0, 0, this.width, getHeaderHeight())
                .addClass(
                        StyleKey.FLEX_ROW,
                        StyleKey.ITEMS_CENTER,
                        StyleKey.JUSTIFY_START,
                        StyleKey.GAP_3,
                        StyleKey.P_1,
                        StyleKey.BG_SURFACE,
                        StyleKey.SHADOW_SM
                );

        ButtonItem toList = new ButtonItem(uiSystem, 0, 0, 160, 28)
                .withText(new TextComponent("List Container").pulse())
                .addClass(
                        StyleKey.TEXT_WHITE,
                        StyleKey.ROUNDED_MD,
                        StyleKey.HOVER_SCALE,
                        StyleKey.FOCUS_RING,
                        StyleKey.FLEX_BASIS_40
                );
        toList.onClick(() -> MinecraftClient.getInstance().setScreen(new ListContainerExample(this)));

        ButtonItem toGrid = new ButtonItem(uiSystem, 0, 0, 160, 28)
                .withText(new TextComponent("Grid Container")
                        .color(ColorUtils.NamedColor.WHITESMOKE.toInt()))
                .addClass(
                        StyleKey.TEXT_WHITE,
                        StyleKey.ROUNDED_MD,
                        StyleKey.HOVER_SCALE,
                        StyleKey.FOCUS_RING,
                        StyleKey.FLEX_BASIS_40
                );
        toGrid.onClick(() -> MinecraftClient.getInstance().setScreen(new GridContainerExample(this)));

        header.addChild(toList);
        header.addChild(toGrid);
        return header;
    }

    @Override
    protected BaseContainer createContent() {
        FlexContainer content = new FlexContainer(uiSystem, 0, getHeaderHeight(), this.width, getContentHeight())
                .addClass(
                        StyleKey.FLEX_ROW,
                        StyleKey.FLEX_WRAP,
                        StyleKey.ITEMS_STRETCH,
                        StyleKey.JUSTIFY_BETWEEN,
                        StyleKey.GAP_4,
                        StyleKey.P_4,
                        StyleKey.BG_BACKGROUND
                );

        content.addChild(button(uiSystem, "Text Field example")
                .addClass(StyleKey.FLEX_BASIS_25, StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
                .onClick(() -> MinecraftClient.getInstance().setScreen(new TextFieldExample(this)))
        );

        content.addChild(button(uiSystem, "Slider example")
                .addClass(StyleKey.FLEX_BASIS_25, StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
                .onClick(() -> MinecraftClient.getInstance().setScreen(new SliderExample(this)))
        );

        return content;
    }


    @Override
    @SuppressWarnings({"ALL", "unused"})
    protected BaseContainer createFooter() {
        FlexContainer footer = new FlexContainer(uiSystem, 0, getContentHeight(), this.width, getFooterHeight())
                .addClass(
                        StyleKey.P_1,
                        StyleKey.BG_BACKGROUND
                );

        footer.addChild(button(uiSystem, "Crash me")
                .addClass(StyleKey.FLEX_BASIS_100, StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
                .onClick(() -> { int crash = 5 / 0; } )
        );

        return footer;
    }


    private ButtonItem button(UIStyleSystem ui, String label) {
        return new ButtonItem(ui, 0, 0, 220, 48)
                .withText(new TextComponent(label).rainbow(TextComponent.EffectMode.HORIZONTAL_LTR))
                .addClass(
                        StyleKey.PRIMARY,
                        StyleKey.TEXT_WHITE,
                        StyleKey.ROUNDED_LG,
                        StyleKey.P_3,
                        StyleKey.SHADOW_MD,
                        StyleKey.HOVER_BRIGHTEN,
                        StyleKey.HOVER_SCALE,
                        StyleKey.BG_SURFACE,
                        StyleKey.FOCUS_RING
                );
    }
}
