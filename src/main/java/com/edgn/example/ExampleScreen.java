package com.edgn.example;

import com.edgn.ui.core.components.TextComponent;
import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.core.container.FlexContainer;
import com.edgn.ui.core.item.items.ButtonItem;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.template.BaseTemplate;
import com.edgn.ui.template.TemplateSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ExampleScreen extends BaseTemplate {

    public ExampleScreen(Screen prev) {
        super(Text.literal("Example Screen"), prev);
    }

    @Override
    protected TemplateSettings templateSettings() {
        return new TemplateSettings().setHeader(false).setFooter(false);
    }

    @Override
    protected BaseContainer createHeader() {
        return null;
    }

    @Override
    protected BaseContainer createFooter() {
        return null;
    }

    @Override
    protected BaseContainer createContent() {
        FlexContainer content = new FlexContainer(getUISystem(), 0, getHeaderHeight(), this.width, getContentHeight())
                .addClass(
                        StyleKey.FLEX_ROW,
                        StyleKey.FLEX_WRAP,
                        StyleKey.ITEMS_STRETCH,
                        StyleKey.JUSTIFY_START,
                        StyleKey.GAP_4,
                        StyleKey.P_4,
                        StyleKey.BG_BACKGROUND
                );

        UIStyleSystem ui = getUISystem();

        content.addChild(button(ui, "Nouveau monde", StyleKey.PRIMARY, true)
                .addClass(StyleKey.FLEX_BASIS_25)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1, StyleKey.P_2)
        );

        content.addChild(button(ui, "Charger", StyleKey.SECONDARY, false)
                .addClass(StyleKey.FLEX_BASIS_25)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(ui, "Multijoueur", StyleKey.SUCCESS, true)
                .addClass(StyleKey.FLEX_BASIS_25)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(ui, "Réseau local", StyleKey.INFO, false)
                .addClass(StyleKey.FLEX_BASIS_25)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(ui, "Ressources", StyleKey.WARNING, true)
                .addClass(StyleKey.FLEX_BASIS_33)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(ui, "Options", StyleKey.SECONDARY, true)
                .addClass(StyleKey.FLEX_BASIS_33)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(ui, "Commandes", StyleKey.PRIMARY, false)
                .addClass(StyleKey.FLEX_BASIS_33)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(ui, "Langue", StyleKey.SUCCESS, false)
                .addClass(StyleKey.FLEX_BASIS_50)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(ui, "Accessibilité", StyleKey.INFO, true)
                .addClass(StyleKey.FLEX_BASIS_50)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(ui, "Crédits", StyleKey.SECONDARY, false)
                .addClass(StyleKey.FLEX_BASIS_25)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(ui, "Site web", StyleKey.PRIMARY, false)
                .addClass(StyleKey.FLEX_BASIS_25)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        content.addChild(button(ui, "Quitter", StyleKey.DANGER, true)
                .addClass(StyleKey.FLEX_BASIS_25)
                .addClass(StyleKey.FLEX_GROW_1, StyleKey.FLEX_SHRINK_1)
        );

        return content;
    }

    private ButtonItem button(UIStyleSystem ui, String label, StyleKey tone, boolean scaleOnHover) {
        ButtonItem b = new ButtonItem(ui, 0, 0, 220, 48)
                .withText(new TextComponent(label).rainbow(TextComponent.EffectMode.HORIZONTAL_LTR))
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
