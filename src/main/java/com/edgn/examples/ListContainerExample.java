package com.edgn.examples;

import com.edgn.EdgnScreenLib;
import com.edgn.ui.core.container.components.TextComponent;
import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.core.container.containers.FlexContainer;
import com.edgn.ui.core.container.containers.ListContainer;
import com.edgn.ui.core.item.items.ButtonItem;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.template.BaseTemplate;
import com.edgn.ui.template.TemplateSettings;
import com.edgn.ui.utils.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class ListContainerExample extends BaseTemplate {

    public ListContainerExample(Screen prev) {
        super(Text.literal("ListContainerExample"), prev);
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
                        StyleKey.SHADOW_SM
                );

        ButtonItem back = new ButtonItem(uiSystem, 0, 0, 120, 28)
                .withText(new TextComponent("Back").color(ColorUtils.NamedColor.WHITESMOKE.toInt()))
                .addClass(
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

        ListContainer list = new ListContainer(uiSystem, 0, getHeaderHeight(), this.width, getContentHeight())
                .setScrollable(true)
                .setScrollAxes(true, false)
                .addClass(StyleKey.P_4, StyleKey.GAP_3);

        for (int i = 1; i <= 30; i++) {
            int finalI = i;
            ButtonItem item = new ButtonItem(uiSystem, 0, 0, this.width - 24, 44)
                    .withText(new TextComponent("Item " + i).color(ColorUtils.NamedColor.WHITESMOKE.toInt()))
                    .addClass(
                            StyleKey.ROUNDED_MD,
                            StyleKey.P_3,
                            StyleKey.SHADOW_SM,
                            StyleKey.HOVER_SCALE,
                            StyleKey.FOCUS_RING,
                            StyleKey.HOVER_BRIGHTEN
                    ).onClick(() -> EdgnScreenLib.LOGGER.info(String.valueOf(finalI)));
            list.addChild(item);
        }
        return list;
    }
}
