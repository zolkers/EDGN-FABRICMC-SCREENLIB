package com.edgn.ui.template;

import com.edgn.mixin.accessors.ScreenAccessor;
import com.edgn.ui.core.ElementHost;
import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.css.UIStyleSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Calendar;

public abstract class BaseTemplate extends EventScreen {
    private final Screen prevScreen;
    private TemplateSettings settings;

    protected int headerHeight;
    protected int footerHeight;
    protected int contentHeight;
    protected int contentX, contentY, contentWidth;

    private BaseContainer headerContainer;
    private BaseContainer mainContainer;
    private BaseContainer footerContainer;

    private long layoutChangeTime = 0;
    private boolean layoutAnimating = false;

    protected BaseTemplate(Text title, Screen prevScreen) {
        super(title);
        this.prevScreen = prevScreen;
        this.settings = settings();
        if (this.settings == null) {
            this.settings = TemplateSettings.DEFAULT;
        }
    }

    protected abstract TemplateSettings settings();
    protected abstract BaseContainer createHeader();
    protected abstract BaseContainer createContent();
    protected abstract BaseContainer createFooter();

    @Override
    protected void onInit() {
        updateLayout();
        buildUI();
        initializeComponents();
    }

    protected void updateLayout() {
        headerHeight = settings.calculateHeaderHeight(height);
        footerHeight = settings.calculateFooterHeight(height);
        contentHeight = settings.calculateContentHeight(height);

        contentX = settings.getPadding();
        contentY = headerHeight + settings.getPadding();
        contentWidth = width - (settings.getPadding() * 2);

        if (settings.isAnimateLayout() && layoutChangeTime > 0) {
            layoutAnimating = true;
            animateLayoutTransition();
        }

        layoutChangeTime = System.currentTimeMillis();
    }

    private void animateLayoutTransition() {
        long elapsed = System.currentTimeMillis() - layoutChangeTime;
        float progress = Math.min(1.0f, (float) elapsed / settings.getAnimationDuration());

        progress = 1.0f - (1.0f - progress) * (1.0f - progress);
    }

    private void initializeComponents() {
        if (headerContainer != null) {
            initializeContainerComponents(headerContainer);
        }
        if (mainContainer != null) {
            initializeContainerComponents(mainContainer);
        }
        if (footerContainer != null) {
            initializeContainerComponents(footerContainer);
        }
    }

    private void initializeContainerComponents(BaseContainer container) {
        container.getChildren().forEach(child -> {
            if (child instanceof ElementHost) {
                ((ElementHost) child).initializeComponents();
            }

            if (child instanceof BaseContainer childContainer) {
                initializeContainerComponents(childContainer);
            }
        });
    }

    protected void buildUI() {
        if (settings.hasHeader()) {
            headerContainer = createHeader();
            if (headerContainer != null) {
                headerContainer.setX(settings.getPadding());
                headerContainer.setY(settings.getPadding());
                headerContainer.setWidth(width - (settings.getPadding() * 2));
                headerContainer.setHeight(headerHeight - settings.getPadding());
            }
        } else {
            headerContainer = null;
        }

        mainContainer = createContent();
        if (mainContainer != null) {
            mainContainer.setX(contentX);
            mainContainer.setY(contentY);
            mainContainer.setWidth(contentWidth);
            mainContainer.setHeight(contentHeight);
        }

        if (settings.hasFooter()) {
            int footerY = height - footerHeight - settings.getPadding();
            footerContainer = createFooter();
            if (footerContainer != null) {
                footerContainer.setX(settings.getPadding());
                footerContainer.setY(footerY);
                footerContainer.setWidth(width - (settings.getPadding() * 2));
                footerContainer.setHeight(footerHeight - settings.getPadding());
            }
        } else {
            footerContainer = null;
        }
    }

    @Override
    public final void render(DrawContext context, int mouseX, int mouseY, float delta) {
        updateLayout();

        renderBackground(context, mouseX, mouseY, delta);

        renderHeader(context, mouseX, mouseY, delta);
        renderContent(context, mouseX, mouseY, delta);
        renderFooter(context, mouseX, mouseY, delta);

        for (Drawable drawable : ((ScreenAccessor) this).getDrawables()) {
            if (drawable != null) {
                drawable.render(context, mouseX, mouseY, delta);
            }
        }
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!settings.hasDefaultBackground()) return;

        Integer bgColor = settings.getBackgroundColor();
        if (bgColor != null) {
            context.fill(0, 0, width, height, bgColor);
        } else {
            super.renderBackground(context, mouseX, mouseY, delta);
        }
    }

    protected final void renderHeader(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!settings.hasHeader()) return;

        Integer headerBg = settings.getHeaderBackgroundColor();
        if (headerBg != null) {
            int padding = settings.getPadding();
            context.fill(padding, padding, width - padding, headerHeight + padding, headerBg);
        }

        if (headerContainer != null) {
            headerContainer.render(context);
        } else if (settings.hasDefaultHeader()) {
            renderDefaultHeader(context);
        }
    }

    protected final void renderContent(DrawContext context, int mouseX, int mouseY, float delta) {
        Integer contentBg = settings.getContentBackgroundColor();
        if (contentBg != null) {
            context.fill(contentX, contentY, contentX + contentWidth, contentY + contentHeight, contentBg);
        }

        if (mainContainer != null) {
            mainContainer.render(context);
        }
    }

    protected final void renderFooter(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!settings.hasFooter()) return;

        Integer footerBg = settings.getFooterBackgroundColor();
        if (footerBg != null) {
            int padding = settings.getPadding();
            int footerY = height - footerHeight - padding;
            context.fill(padding, footerY, width - padding, height - padding, footerBg);
        }

        if (footerContainer != null) {
            footerContainer.render(context);
        } else if (settings.hasDefaultFooter()) {
            renderDefaultFooter(context);
        }
    }

    protected void renderDefaultHeader(DrawContext context) {
        int headerPadding = settings.getHeaderPadding();
        int headerCenterY = settings.getPadding() + (headerHeight - settings.getPadding()) / 2 - 4;

        context.drawCenteredTextWithShadow(textRenderer, this.title,
                width / 2,
                headerCenterY,
                0xFFFFFFFF);

        if (settings.hasFooter() || settings.getContentBackgroundColor() != null) {
            int lineY = settings.getPadding() + headerHeight - 1;
            context.fill(settings.getPadding(), lineY, width - settings.getPadding(), lineY + 1, 0x40FFFFFF);
        }
    }

    protected void renderDefaultFooter(DrawContext context) {
        String footerText = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        int footerCenterY = height - footerHeight / 2 - 4;

        context.drawCenteredTextWithShadow(textRenderer, footerText,
                width / 2,
                footerCenterY,
                0xFFAAAAAA);

        if (settings.hasHeader() || settings.getContentBackgroundColor() != null) {
            int lineY = height - footerHeight - settings.getPadding();
            context.fill(settings.getPadding(), lineY, width - settings.getPadding(), lineY + 1, 0x40FFFFFF);
        }
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(prevScreen);
    }

    @Override
    public void onResize(MinecraftClient client, int width, int height) {
        updateLayout();
        buildUI();
    }

    @Override
    protected void onTick() {
        if (layoutAnimating) {
            long elapsed = System.currentTimeMillis() - layoutChangeTime;
            if (elapsed >= settings.getAnimationDuration()) {
                layoutAnimating = false;
            }
        }

        updateComponents();
    }

    protected void updateComponents() {
        if (headerContainer != null) {
            updateContainerComponents(headerContainer);
        }
        if (mainContainer != null) {
            updateContainerComponents(mainContainer);
        }
        if (footerContainer != null) {
            updateContainerComponents(footerContainer);
        }
    }

    private void updateContainerComponents(BaseContainer container) {
        container.getChildren().forEach(child -> {
            if (child instanceof ElementHost) {
                ((ElementHost) child).updateComponents();
            }

            if (child instanceof BaseContainer childContainer) {
                updateContainerComponents(childContainer);
            }
        });
    }

    public TemplateSettings getSettings() { return settings; }
    public UIStyleSystem getUISystem() { return uiSystem; }
    public int getHeaderHeight() { return headerHeight; }
    public int getFooterHeight() { return footerHeight; }
    public int getContentHeight() { return contentHeight; }
    public int getContentX() { return contentX; }
    public int getContentY() { return contentY; }
    public int getContentWidth() { return contentWidth; }

    public BaseContainer getHeaderContainer() { return headerContainer; }
    public BaseContainer getMainContainer() { return mainContainer; }
    public BaseContainer getFooterContainer() { return footerContainer; }
}