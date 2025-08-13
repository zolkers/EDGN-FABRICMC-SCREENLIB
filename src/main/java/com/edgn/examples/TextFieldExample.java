package com.edgn.examples;

import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.core.container.components.TextComponent;
import com.edgn.ui.core.container.containers.FlexContainer;
import com.edgn.ui.core.item.items.TextFieldItem;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.template.BaseTemplate;
import com.edgn.ui.template.TemplateSettings;
import com.edgn.ui.utils.ColorUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class TextFieldExample extends BaseTemplate {
    protected TextFieldExample(Screen prevScreen) {
        super(Text.literal("TextField example"), prevScreen);
    }

    @Override
    protected TemplateSettings templateSettings() {
        return new TemplateSettings().setFooter(false).setHeader(false);
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
                        StyleKey.M_4,
                        StyleKey.BG_BACKGROUND
                );

        TextFieldItem field = new TextFieldItem(uiSystem, 0, 0, 160, 28)
                .withPlaceholder(new TextComponent("Test").rainbow())
                .textColor(ColorUtils.NamedColor.AQUAMARINE.toInt())
                .addClass(StyleKey.FLEX_BASIS_25, StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1);

        return content.addChild(field);
    }

    @Override
    protected BaseContainer createFooter() {
        return null;
    }
}
