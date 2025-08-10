package com.edgn.examples;

import com.edgn.ui.core.components.TextComponent;
import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.core.container.containers.FlexContainer;
import com.edgn.ui.core.container.containers.GridContainer;
import com.edgn.ui.core.item.items.ButtonItem;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.template.BaseTemplate;
import com.edgn.ui.template.TemplateSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class GridContainerExample extends BaseTemplate {

    public GridContainerExample(Screen prev) {
        super(Text.literal("Grid Container"), prev);
    }

    @Override
    protected TemplateSettings templateSettings() {
        return null;
    }

    @Override
    protected BaseContainer createHeader() {
        FlexContainer header = new FlexContainer(getUISystem(), 0, 0, this.width, getHeaderHeight())
                .addClass(
                        StyleKey.FLEX_ROW,
                        StyleKey.ITEMS_CENTER,
                        StyleKey.JUSTIFY_START,
                        StyleKey.GAP_3,
                        StyleKey.P_3,
                        StyleKey.BG_SURFACE,
                        StyleKey.SHADOW_SM
                );

        ButtonItem back = new ButtonItem(uiSystem, 0, 0, 120, 28)
                .withText(new TextComponent("Back"))
                .addClass(
                        StyleKey.SECONDARY,
                        StyleKey.TEXT_WHITE,
                        StyleKey.ROUNDED_MD,
                        StyleKey.P_2,
                        StyleKey.HOVER_SCALE,
                        StyleKey.FOCUS_RING
                );
        back.onClick(() -> MinecraftClient.getInstance().setScreen(prevScreen));

        header.addChild(back);
        return header;
    }

    @Override
    protected BaseContainer createFooter() {
        return null;
    }

    @Override
    protected BaseContainer createContent() {
        UIStyleSystem ui = getUISystem();

        GridContainer grid = new GridContainer(ui, 0, getHeaderHeight(), this.width, getContentHeight())
                .setColumns(3)
                .setScrollable(true)
                .setScrollAxes(true, false)
                .setDragScrollEnabled(true)
                .addClass(StyleKey.P_4, StyleKey.GAP_4, StyleKey.BG_BACKGROUND);

        for (int i = 1; i <= 36; i++) {
            ButtonItem tile = new ButtonItem(ui, 0, 0, 220, 80)
                    .withText(new TextComponent("Card " + i))
                    .addClass(
                            StyleKey.INFO,
                            StyleKey.TEXT_WHITE,
                            StyleKey.ROUNDED_LG,
                            StyleKey.P_3,
                            StyleKey.SHADOW_MD,
                            StyleKey.HOVER_SCALE,
                            StyleKey.FOCUS_RING
                    );
            grid.addChild(tile);
        }
        return grid;
    }
}
