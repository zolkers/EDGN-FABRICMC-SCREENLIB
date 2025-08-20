package com.edgn.examples;

import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.core.container.components.TextComponent;
import com.edgn.ui.core.container.containers.FlexContainer;
import com.edgn.ui.core.item.items.LabelItem;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.template.BaseTemplate;
import com.edgn.ui.template.TemplateSettings;
import com.edgn.ui.utils.ColorUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class LabelExample extends BaseTemplate {
    protected LabelExample(Screen prevScreen) {
        super(Text.literal("LabelExample"), prevScreen);
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
                        StyleKey.GAP_4,
                        StyleKey.P_2,
                        StyleKey.M_4,
                        StyleKey.BG_BACKGROUND
                );

        LabelItem labelItem = new LabelItem(uiSystem, 0, 0, "IF YOU FIND ANY BUG CONTACT edgn ON DISCORD")
                .color(ColorUtils.NamedColor.PALEGOLDENROD.toInt())
                .pulse()
                .align(TextComponent.TextAlign.CENTER)
                .addClass(StyleKey.FLEX_BASIS_100);

        LabelItem idk = new LabelItem(uiSystem, 0, 0, "ANYWAYS HEYOTH IS NUL BY THE WAY")
                .color(ColorUtils.NamedColor.PALEGOLDENROD.toInt())
                .pulse()
                .align(TextComponent.TextAlign.LEFT)
                .addClass(StyleKey.FLEX_BASIS_40);

        LabelItem tytoo = new LabelItem(uiSystem, 0, 0, "TYTOO TOO XDXDXDXDXDXDXDXDXD")
                .color(ColorUtils.NamedColor.PALEGOLDENROD.toInt())
                .pulse()
                .align(TextComponent.TextAlign.RIGHT)
                .addClass(StyleKey.FLEX_BASIS_40);

        return content.addChild(labelItem).addChild(idk).addChild(tytoo);
    }

    @Override
    protected BaseContainer createFooter() {
        return null;
    }
}
