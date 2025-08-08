package com.edgn.ui.template;

import com.edgn.EdgnScreenLib;
import com.edgn.mixin.accessors.ScreenAccessor;
import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.css.UIStyleSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Calendar;

public abstract class BaseTemplate extends EventTemplate {
    protected int headerHeight;
    protected int footerHeight;
    protected int contentHeight;
    protected Screen prevScreen;
    private final TemplateSettings settings;

    private BaseContainer headerContent;
    private BaseContainer mainContent;
    private BaseContainer footerContent;

    protected BaseTemplate(Text title, Screen prevScreen) {
        super(title);
        this.prevScreen = prevScreen;
        if(templateSettings() == null) this.settings = new TemplateSettings().setToDefault();
        else this.settings = templateSettings();
    }

    protected abstract TemplateSettings templateSettings();
    protected abstract BaseContainer createHeader();
    protected abstract BaseContainer createContent();
    protected abstract BaseContainer createFooter();

    @Override
    protected void onInit() {
        this.updateScreenValues();
        this.buildUI();
    }

    protected void buildUI() {
        if(settings.hasHeader()) {
            headerContent = createHeader();
            if (headerContent != null) {
                headerContent.setX(0);
                headerContent.setY(0);
                headerContent.setWidth(width);
                headerContent.setHeight(headerHeight);
            }
        }

        int contentY = headerHeight;
        mainContent = createContent();
        if (mainContent != null) {
            mainContent.setX(0);
            mainContent.setY(contentY);
            mainContent.setWidth(width);
            mainContent.setHeight(contentHeight);
        }

        if(settings.hasFooter()) {
            int footerY = height - footerHeight;
            footerContent = createFooter();
            if (footerContent != null) {
                footerContent.setX(0);
                footerContent.setY(footerY);
                footerContent.setWidth(width);
                footerContent.setHeight(footerHeight);
            }
        }
    }

    protected final void renderHeader(DrawContext context) {
        if(!settings.hasHeader()) return;
        if (headerContent != null) {
            headerContent.render(context);
        } else {
            renderDefaultHeader(context);
        }
    }

    protected final void renderContent(DrawContext context) {
        if (mainContent != null) {
            mainContent.render(context);
        }
    }

    protected final void renderFooter(DrawContext context) {
        if(!settings.hasFooter()) return;
        if (footerContent != null) {
            footerContent.render(context);
        } else {
            renderDefaultFooter(context);
        }
    }

    protected void renderDefaultHeader(DrawContext context) {
        context.drawCenteredTextWithShadow(textRenderer, this.title, width / 2, headerHeight / 2 - 3, 0xFFFFFF);
        context.fill(0, headerHeight - 1, width, headerHeight, 0x40FFFFFF);
    }

    protected void renderDefaultFooter(DrawContext context) {
        String footerText = Calendar.getInstance().get(Calendar.YEAR) + " " + EdgnScreenLib.MOD_ID;
        context.drawCenteredTextWithShadow(textRenderer, footerText, width / 2,
                this.height - (this.footerHeight / 2) - 3, 0xFFAAAAAA);
        context.fill(0, height - footerHeight, width, height - footerHeight + 1, 0x40FFFFFF);
    }

    protected void updateLayout() {
        buildUI();
    }

    @Override
    public final void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.updateScreenValues();

        renderBackground(context, mouseX, mouseY, delta);

        renderHeader(context);
        renderContent(context);
        renderFooter(context);

        for (Drawable drawable : ((ScreenAccessor) this).getDrawables()) {
            if(drawable != null) {
                drawable.render(context, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    public final void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(prevScreen);
    }

    @Override
    public void onTick() {
        super.tick();
        this.updateScreenValues();
    }

    @Override
    public void onResize(MinecraftClient client, int width, int height) {
        this.updateScreenValues();
        this.updateLayout();
    }

    private void updateScreenValues() {
        if(settings.hasHeader()) {
            this.headerHeight = Math.max(30, this.height / 15);
        } else { this.headerHeight = 0; }

        if(settings.hasFooter()) {
            this.footerHeight = Math.max(20, this.height / 20);
        } else { footerHeight = 0; }

        this.contentHeight = height - headerHeight - footerHeight;
    }

    public UIStyleSystem getUISystem() { return uiSystem; }
    public int getHeaderHeight() { return headerHeight; }
    public int getFooterHeight() { return footerHeight; }
    public int getContentHeight() { return contentHeight; }
}