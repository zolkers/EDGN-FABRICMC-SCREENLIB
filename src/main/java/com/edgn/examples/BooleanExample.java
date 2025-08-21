package com.edgn.examples;

import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.core.container.components.TextComponent;
import com.edgn.ui.core.container.containers.FlexContainer;
import com.edgn.ui.core.item.items.CheckboxItem;
import com.edgn.ui.core.item.items.SwitchItem;
import com.edgn.ui.core.models.values.DefaultBooleanModel;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.template.BaseTemplate;
import com.edgn.ui.template.TemplateSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class BooleanExample extends BaseTemplate {

    public BooleanExample(Screen prevScreen) {
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
                        StyleKey.M_4
                );

        CheckboxItem cb = new CheckboxItem(uiSystem, 20, 20, 140, 24, new DefaultBooleanModel(false), "HEHEHEHEHEHE")
                .checkType(CheckboxItem.CheckType.TICK)
                .onColor(0xFF2563EB)
                .borderColor(0xFF94A3B8)
                .checkColor(0xFFFFFFFF)
                .addClass(StyleKey.FLEX_BASIS_100);

        CheckboxItem cb2 = new CheckboxItem(uiSystem, 20, 20, 140, 24, new DefaultBooleanModel(true))
                .addClass(StyleKey.FLEX_BASIS_100)
                .labelPosition(CheckboxItem.LabelPosition.TOP)
                .checkType(CheckboxItem.CheckType.DOT)
                .checkColor(0xFF10B981);

        CheckboxItem cb4 = new CheckboxItem(uiSystem, 20, 140, 160, 48, new DefaultBooleanModel(false), new TextComponent("HIIHIHIHIHIHIH"))
                .labelPosition(CheckboxItem.LabelPosition.BOTTOM)
                .checkType(CheckboxItem.CheckType.MINUS)
                .onColor(0xFFEAB308)
                .checkColor(0xFF1F2937)
                .addClass(StyleKey.FLEX_BASIS_100);

        SwitchItem sw = new SwitchItem(uiSystem, 20, 50, 80, 28, new DefaultBooleanModel(true))
                .addClass(StyleKey.FLEX_BASIS_100);

        return content.addChild(cb).addChild(cb2).addChild(cb4).addChild(sw);
    }

    @Override
    protected BaseContainer createFooter() {
        return null;
    }
}
