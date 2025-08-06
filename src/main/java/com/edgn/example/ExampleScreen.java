package com.edgn.example;

import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.core.container.containers.FlexContainer;
import com.edgn.ui.core.item.items.ButtonItem;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.rules.AlignItems;
import com.edgn.ui.css.rules.FlexDirection;
import com.edgn.ui.css.rules.JustifyContent;
import com.edgn.ui.template.BaseTemplate;
import com.edgn.ui.template.TemplateSettings;
import net.minecraft.text.Text;

public class ExampleScreen extends BaseTemplate {

    public ExampleScreen() {
        super(Text.of("Example screen"), null);
    }

    @Override
    protected TemplateSettings settings() {
        return TemplateSettings.DEFAULT
                .contentPadding(16)
                .contentBackground(0x20000000);
    }

    @Override
    protected BaseContainer createHeader() {
        return null;
    }

    @Override
    protected BaseContainer createContent() {
        FlexContainer container = new FlexContainer(getUISystem(), 0, 0, getContentWidth(), getContentHeight())
                .flexDirection(FlexDirection.COLUMN)
                .justifyContent(JustifyContent.CENTER)
                .alignItems(AlignItems.CENTER)
                .addClass(StyleKey.P_2);

        ButtonItem button = new ButtonItem(getUISystem(), 0, 0, 120, 60)
                .setText("Click me!")
                .addClass(StyleKey.PRIMARY, StyleKey.ROUNDED_MD, StyleKey.P_2)
                .onClick(this::close);

        container.addChild(button);

        return container;
    }

    @Override
    protected BaseContainer createFooter() {
        return null;
    }
}