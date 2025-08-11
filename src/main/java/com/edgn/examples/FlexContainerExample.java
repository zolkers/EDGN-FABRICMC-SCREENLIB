package com.edgn.examples;

import com.edgn.ui.core.container.components.TextComponent;
import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.core.container.containers.FlexContainer;
import com.edgn.ui.core.item.items.ButtonItem;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.template.BaseTemplate;
import com.edgn.ui.template.TemplateSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class FlexContainerExample extends BaseTemplate {

    public FlexContainerExample(Screen prev) {
        super(Text.literal("Example Screen"), prev);
    }

    @Override
    protected TemplateSettings templateSettings() {
        return null;
    }

    @Override
    protected BaseContainer createHeader() {
        FlexContainer header = new FlexContainer(uiSystem, 0, 0, this.width, getHeaderHeight())
                .addClass(
                        StyleKey.FLEX_ROW,
                        StyleKey.ITEMS_CENTER,
                        StyleKey.JUSTIFY_START,
                        StyleKey.GAP_3,
                        StyleKey.P_3,
                        StyleKey.BG_SURFACE,
                        StyleKey.SHADOW_SM
                );

        ButtonItem toList = new ButtonItem(uiSystem, 0, 0, 160, 28)
                .withText(new TextComponent("List Container"))
                .addClass(
                        StyleKey.SECONDARY,
                        StyleKey.TEXT_WHITE,
                        StyleKey.ROUNDED_MD,
                        StyleKey.P_2,
                        StyleKey.HOVER_SCALE,
                        StyleKey.FOCUS_RING
                );
        toList.onClick(() -> MinecraftClient.getInstance().setScreen(new ListContainerExample(FlexContainerExample.this)));

        ButtonItem toGrid = new ButtonItem(uiSystem, 0, 0, 160, 28)
                .withText(new TextComponent("Grid Container"))
                .addClass(
                        StyleKey.PRIMARY,
                        StyleKey.TEXT_WHITE,
                        StyleKey.ROUNDED_MD,
                        StyleKey.P_2,
                        StyleKey.HOVER_SCALE,
                        StyleKey.FOCUS_RING
                );
        toGrid.onClick(() -> MinecraftClient.getInstance().setScreen(new GridContainerExample(FlexContainerExample.this)));

        header.addChild(toList);
        header.addChild(toGrid);
        return header;
    }


    @Override
    protected BaseContainer createFooter() {
        return null;
    }

    @Override
    protected BaseContainer createContent() {
        FlexContainer content = new FlexContainer(uiSystem, 0, getHeaderHeight(), this.width, getContentHeight())
                .addClass(
                        StyleKey.FLEX_ROW,
                        StyleKey.FLEX_WRAP,
                        StyleKey.ITEMS_STRETCH,
                        StyleKey.JUSTIFY_START,
                        StyleKey.GAP_4,
                        StyleKey.P_4,
                        StyleKey.BG_BACKGROUND
                );

        content.addChild(button(uiSystem, "Hi", StyleKey.PRIMARY, true)
                .addClass(StyleKey.FLEX_BASIS_25)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(uiSystem, "I'm a button", StyleKey.SECONDARY, false)
                .addClass(StyleKey.FLEX_BASIS_25)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(uiSystem, "Might be trimmed cuz too loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong", StyleKey.SUCCESS, true)
                .addClass(StyleKey.FLEX_BASIS_25)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(uiSystem, ":)", StyleKey.INFO, false)
                .addClass(StyleKey.FLEX_BASIS_25)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(uiSystem, "I love melon", StyleKey.WARNING, true)
                .addClass(StyleKey.FLEX_BASIS_33)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(uiSystem, "EDGN ?!", StyleKey.SECONDARY, true)
                .addClass(StyleKey.FLEX_BASIS_33)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(uiSystem, "YAY", StyleKey.PRIMARY, false)
                .addClass(StyleKey.FLEX_BASIS_33)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(uiSystem, "AWESOME !", StyleKey.SUCCESS, false)
                .addClass(StyleKey.FLEX_BASIS_50)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(uiSystem, "Idk if it's easy to use", StyleKey.INFO, true)
                .addClass(StyleKey.FLEX_BASIS_50)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(uiSystem, "AHAHAHAHHAHAHAHAHAHAHA", StyleKey.SECONDARY, false)
                .addClass(StyleKey.FLEX_BASIS_25)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(uiSystem, "YOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", StyleKey.PRIMARY, false)
                .addClass(StyleKey.FLEX_BASIS_25)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(uiSystem, "LEAVE THE SCREEN", StyleKey.DANGER, true)
                .addClass(StyleKey.FLEX_BASIS_25)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
                .onClick(this::close)
        );

        return content;
    }

    private ButtonItem button(UIStyleSystem ui, String label, StyleKey tone, boolean scaleOnHover) {
        ButtonItem b = new ButtonItem(ui, 0, 0, 220, 48)
                .withText(new TextComponent(label).rainbow(TextComponent.EffectMode.HORIZONTAL_LTR).wave())
                .addClass(
                        tone,
                        StyleKey.TEXT_WHITE,
                        StyleKey.ROUNDED_LG,
                        StyleKey.P_3,
                        StyleKey.SHADOW_MD
                );

        if (scaleOnHover) b.addClass(StyleKey.HOVER_SCALE, StyleKey.BG_SURFACE, StyleKey.FOCUS_RING);

        return b;
    }
}
