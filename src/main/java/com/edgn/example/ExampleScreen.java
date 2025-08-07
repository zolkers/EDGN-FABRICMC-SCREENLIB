package com.edgn.example;

import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.core.container.FlexContainer;
import com.edgn.ui.core.item.items.ButtonItem;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.layout.ZIndex;
import com.edgn.ui.template.BaseTemplate;
import com.edgn.ui.template.TemplateSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ExampleScreen extends BaseTemplate {

    public ExampleScreen(Screen prevScreen) {
        super(Text.literal("Example"), prevScreen);
    }

    @Override
    protected BaseContainer createHeader() {
        FlexContainer header = new FlexContainer(uiSystem, 0, 0, width, headerHeight)
                .addClass(StyleKey.BG_SURFACE, StyleKey.P_3, StyleKey.FLEX_ROW, StyleKey.JUSTIFY_CENTER, StyleKey.ITEMS_CENTER);

        ButtonItem title = new ButtonItem(uiSystem, 0, 0 ,100, 100)
                .setZIndex(ZIndex.CONTENT)
                .setEnabled(false);

        header.addChild(title);
        return header;
    }

    @Override
    protected BaseContainer createContent() {
        FlexContainer mainContainer = new FlexContainer(uiSystem, 0, 0, width, contentHeight)
                .addClass(StyleKey.P_4, StyleKey.FLEX_COLUMN, StyleKey.GAP_4);

        FlexContainer demoArea = new FlexContainer(uiSystem, 0, 0, width - 40, 300)
                .setZIndex(ZIndex.CONTENT)
                .addClass(StyleKey.BG_BACKGROUND, StyleKey.ROUNDED_LG, StyleKey.P_4);

        ButtonItem contentBtn1 = new ButtonItem(uiSystem, 150, 75, 150, 40, "Content 1")
                .setZIndex(ZIndex.content(1))
                .asFancyButton()
                .onClick(() -> System.out.println("Content 1 clicked"));

        ButtonItem contentBtn2 = new ButtonItem(uiSystem, 200, 100, 150, 40, "Info")
                .setZIndex(ZIndex.content(10))
                .asInfoButton()
                .onClick(() -> System.out.println("Content 2 clicked"));

        ButtonItem overlayBtn = new ButtonItem(uiSystem, 250, 125, 150, 40, "Overlay")
                .setZIndex(ZIndex.OVERLAY)
                .asSecondaryButton()
                .onClick(() -> System.out.println("Overlay clicked"));

        ButtonItem modalBtn = new ButtonItem(uiSystem, 300, 150, 150, 40, "Modal")
                .setZIndex(ZIndex.MODAL)
                .asDangerButton()
                .onClick(() -> System.out.println("Modal clicked"));

        demoArea.addChild(contentBtn1).addChild(contentBtn2).addChild(overlayBtn).addChild(modalBtn);

        FlexContainer controls = new FlexContainer(uiSystem, 0, 0, width - 40, 100)
                .setZIndex(ZIndex.content(50))
                .addClass(StyleKey.BG_SURFACE, StyleKey.ROUNDED_LG, StyleKey.P_3,
                         StyleKey.FLEX_ROW, StyleKey.GAP_3, StyleKey.JUSTIFY_CENTER);

        ButtonItem hideBtn = new ButtonItem(uiSystem, 0, 0, 100, 100)
                .setZIndex(ZIndex.content(51))
                .onClick(() -> {
                    overlayBtn.setVisible(!overlayBtn.isVisible());
                });

        ButtonItem disableBtn = new ButtonItem(uiSystem, 0, 0,100, 100)
                .setZIndex(ZIndex.content(52))
                .onClick(() -> {
                    modalBtn.setEnabled(!modalBtn.isEnabled());
                });

        ButtonItem resetBtn = new ButtonItem(uiSystem, 0, 0, 100, 100)
                .setZIndex(ZIndex.content(53))
                .onClick(() -> {
                    overlayBtn.setVisible(true);
                    modalBtn.setEnabled(true);
                });

        controls.addChild(hideBtn).addChild(disableBtn).addChild(resetBtn);

        mainContainer.addChild(demoArea).addChild(controls);
        return mainContainer;
    }

    @Override
    protected BaseContainer createFooter() {
        FlexContainer footer = new FlexContainer(uiSystem, 0, 0, width, footerHeight)
                .addClass(StyleKey.BG_SURFACE, StyleKey.P_2, StyleKey.FLEX_ROW,
                         StyleKey.JUSTIFY_CENTER, StyleKey.ITEMS_CENTER);

        ButtonItem info = new ButtonItem(uiSystem, 0, 0, 100, 100)
                .setZIndex(ZIndex.content(100))
                .addClass(StyleKey.BG_INFO, StyleKey.TEXT_WHITE)
                .setEnabled(false);

        footer.addChild(info);
        return footer;
    }

    @Override
    protected TemplateSettings templateSettings() {
        return new TemplateSettings().setFooter(false).setHeader(false);
    }
}