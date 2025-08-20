package com.edgn.ui.core.item;

import com.edgn.ui.core.container.components.TextComponent;
import com.edgn.ui.core.container.components.font.FontRenderer;
import com.edgn.ui.core.models.text.DefaultTextInputModel;
import com.edgn.ui.core.models.text.TextInputModel;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.css.values.Shadow;
import com.edgn.ui.layout.LayoutConstraints;
import com.edgn.ui.layout.ZIndex;
import com.edgn.ui.utils.DrawingUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings({"unused","unchecked","UnusedReturnValue"})
public abstract class AbstractTextItem<T extends AbstractTextItem<T>> extends BaseItem {
    protected final TextInputModel model = new DefaultTextInputModel();
    protected TextComponent textComponent;
    protected TextComponent placeholderComponent;
    protected int textSafetyMargin = 8;
    protected long lastBlink = System.currentTimeMillis();
    protected boolean caretVisible = true;
    protected int selectionColor = 0x803A86FF;
    protected long lastClickTime = 0;
    protected static final long DOUBLE_CLICK_TIME = 500;

    protected static final int PLACEHOLDER_COLOR_DEFAULT = 0x7FFFFFFF;

    protected AbstractTextItem(UIStyleSystem styleSystem, int x, int y, int w, int h) {
        super(styleSystem, x, y, w, h);
        addClass(StyleKey.BG_SURFACE, StyleKey.ROUNDED_MD, StyleKey.P_2);
    }

    protected AbstractTextItem(UIStyleSystem styleSystem, int x, int y, int w, int h, String placeholder) {
        this(styleSystem, x, y, w, h);
        setPlaceholder(placeholder);
    }

    @Override
    public void onFocusLost() {
        super.onFocusLost();
        lastClickTime = 0;
    }

    @SuppressWarnings("unchecked")
    protected T self() { return (T) this; }

    public T withText(String text) {
        model.setText(text);
        ensureTextComponent();
        return self();
    }

    public T withText(TextComponent comp) {
        textComponent = comp == null ? null : configureTextComponent(comp);
        model.setText(comp != null ? comp.getText() : "");
        return self();
    }

    public T withPlaceholder(String placeholder) {
        placeholderComponent = new TextComponent(placeholder == null ? "" : placeholder, fontRenderer);
        configurePlaceholderComponent(placeholderComponent);
        return self();
    }

    public T withPlaceholder(TextComponent comp) {
        placeholderComponent = comp == null ? null : configurePlaceholderComponent(comp);
        return self();
    }

    public T setText(String text) { return withText(text); }
    public T setText(TextComponent comp) { return withText(comp); }
    public T setPlaceholder(String placeholder) { return withPlaceholder(placeholder); }
    public T setPlaceholder(TextComponent comp) { return withPlaceholder(comp); }

    public T setTextSafetyMargin(int m) {
        textSafetyMargin = Math.max(0, m);
        if (textComponent != null) textComponent.setSafetyMargin(textSafetyMargin);
        if (placeholderComponent != null) placeholderComponent.setSafetyMargin(textSafetyMargin);
        return self();
    }

    public T setSelectionColor(int argb) {
        selectionColor = argb;
        return self();
    }

    public String getText() { return model.getText(); }
    public int getCaretIndex() { return model.getCaret(); }
    public boolean hasSelection() { return model.hasSelection(); }
    public int getSelectionStart() { return model.getSelectionStart(); }
    public int getSelectionEnd() { return model.getSelectionEnd(); }

    public T textColor(int color) { ensureTextComponent().color(color); return self(); }
    public T textBold() { ensureTextComponent().bold(); return self(); }
    public T textItalic() { ensureTextComponent().italic(); return self(); }
    public T textShadow() { ensureTextComponent().shadow(); return self(); }
    public T textGlow() { ensureTextComponent().glow(); return self(); }
    public T textGlow(int color) { ensureTextComponent().glow(color); return self(); }
    public T textPulse() { ensureTextComponent().pulse(); return self(); }
    public T textWave() { ensureTextComponent().wave(); return self(); }
    public T textTypewriter() { ensureTextComponent().typewriter(); return self(); }
    public T textRainbow() { ensureTextComponent().rainbow(); return self(); }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!enabled || !canInteract(mouseX, mouseY)) return false;
        styleSystem.getEventManager().setFocus(this);
        setState(ItemState.HOVERED);

        onMouseClickText(mouseX, mouseY);

        long now = System.currentTimeMillis();
        if (now - lastClickTime < DOUBLE_CLICK_TIME) {
            model.setSelection(model.wordLeft(), model.wordRight());
        } else {
            model.setSelection(model.getCaret(), model.getCaret());
        }
        lastClickTime = now;

        return true;
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, int button, double dx, double dy) {
        if (!enabled || !isFocused()) return false;
        int anchor = model.getSelectionAnchor();
        onMouseDragText(mouseX, mouseY);
        model.setSelection(anchor, model.getCaret());
        return true;
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        return isFocused();
    }

    @Override
    public void onMouseEnter() {
        if (!enabled) return;
        setState(ItemState.HOVERED);
    }

    @Override
    public void onMouseLeave() {
        if (!enabled) return;
        if (!isFocused()) setState(ItemState.NORMAL);
    }


    @Override
    public boolean onKeyPress(int key, int sc, int mods) {
        if (!enabled || !isFocused()) return false;

        boolean ctrl = (mods & GLFW.GLFW_MOD_CONTROL) != 0 || (mods & GLFW.GLFW_MOD_SUPER) != 0;
        boolean shift = (mods & GLFW.GLFW_MOD_SHIFT) != 0;

        if (ctrl) {
            String name = GLFW.glfwGetKeyName(key, sc);
            if (name != null && !name.isEmpty()) {
                String ch = name.toLowerCase();
                switch (ch) {
                    case "a" -> {
                        model.setSelection(0, model.length());
                        model.setCaret(model.length());
                        caretVisible = true; lastBlink = System.currentTimeMillis();
                        return true;
                    }
                    case "c" -> { copySelection(); return true; }
                    case "x" -> { cutSelection(); return true; }
                    case "v" -> { pasteClipboard(); onPasteComplete(); return true; }
                    case "z" -> { model.undo(); onTextModified(); return true; }
                    case "y" -> { model.redo(); onTextModified(); return true; }
                    default -> {/*useless*/}
                }
            }
        }

        return onKeyPressSpecific(key, sc, mods, ctrl, shift);
    }

    @Override
    public boolean onCharTyped(char chr, int mods) {
        if (!enabled || !isFocused()) return false;
        if (isValidCharacter(chr)) {
            model.insert(String.valueOf(chr));
            onCharInserted();
            return true;
        }
        return false;
    }

    @Override
    public void render(DrawContext context) {
        if (!visible) return;
        updateConstraints();

        int cx = getCalculatedX();
        int cy = getCalculatedY();
        int cw = getCalculatedWidth();
        int ch = getCalculatedHeight();

        renderBackground(context, cx, cy, cw, ch);
        renderContent(context, cx, cy, cw, ch);
        blinkCaret();
    }

    private void renderBackground(DrawContext context, int cx, int cy, int cw, int ch) {
        int baseBg = getBgColor();
        if (baseBg == 0) baseBg = styleSystem.getColor(StyleKey.SURFACE);
        int bg = backgroundForState(baseBg);
        int radius = getBorderRadius();
        Shadow shadow = getShadow();

        if (shadow != null)
            DrawingUtils.drawShadow(context, cx, cy, cw, ch, 2, 2, shadow.color);
        DrawingUtils.drawRoundedRect(context, cx, cy, cw, ch, radius, bg);

        if (isFocused() && hasClass(StyleKey.FOCUS_RING)) {
            int focusColor = styleSystem.getColor(StyleKey.PRIMARY_LIGHT);
            DrawingUtils.drawRoundedRectBorder(context, cx - 2, cy - 2, cw + 4, ch + 4, radius + 2, focusColor, 2);
        }
    }

    protected void blinkCaret() {
        long now = System.currentTimeMillis();
        if (now - lastBlink >= 500) {
            caretVisible = !caretVisible;
            lastBlink = now;
        }
    }

    protected void moveCaret(int newIndex, boolean extend) {
        newIndex = Math.clamp(newIndex, 0, model.length());
        if (extend) {
            int anchor = model.getSelectionAnchor();
            if (anchor < 0) {
                anchor = model.getCaret();
            }
            model.setSelection(anchor, newIndex);
        } else {
            model.clearSelection();
            model.setCaret(newIndex);
        }
        caretVisible = true;
        lastBlink = System.currentTimeMillis();
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private int backgroundForState(int base) {
        return switch (getState()) {
            case HOVERED -> brighten(base, hasClass(StyleKey.HOVER_BRIGHTEN) ? 0.20f : 0.08f);
            default -> base;
        };
    }

    private int brighten(int color, float ratio) {
        int a = (color >>> 24) & 0xFF;
        int r = (color >>> 16) & 0xFF;
        int g = (color >>> 8) & 0xFF;
        int b = color & 0xFF;
        r = Math.min(255, Math.round(r + (255 - r) * ratio));
        g = Math.min(255, Math.round(g + (255 - g) * ratio));
        b = Math.min(255, Math.round(b + (255 - b) * ratio));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    protected TextComponent ensureTextComponent() {
        if (textComponent == null) {
            textComponent = new TextComponent("", fontRenderer);
            configureTextComponent(textComponent);
            textComponent.setSafetyMargin(textSafetyMargin)
                    .color(getComputedStyles().getTextColor());
        }
        return textComponent;
    }

    private void copySelection() {
        if (!model.hasSelection()) return;
        MinecraftClient.getInstance().keyboard.setClipboard(
                model.getText().substring(model.getSelectionStart(), model.getSelectionEnd())
        );
    }

    private void cutSelection() {
        if (!model.hasSelection()) return;
        MinecraftClient.getInstance().keyboard.setClipboard(
                model.getText().substring(model.getSelectionStart(), model.getSelectionEnd())
        );
        model.delete(false); // Utilise la méthode delete qui gère l'historique
        onTextModified();
    }

    private void pasteClipboard() {
        String clip = MinecraftClient.getInstance().keyboard.getClipboard();
        if (clip == null || clip.isEmpty()) return;
        model.insert(processClipboardText(clip));
    }

    @Override
    public T setFontRenderer(FontRenderer fr) {
        super.setFontRenderer(fr);
        if (textComponent != null) textComponent.setFontRenderer(fr);
        if (placeholderComponent != null) placeholderComponent.setFontRenderer(fr);
        return self();
    }

    @Override public T addClass(StyleKey... keys) { super.addClass(keys); return self(); }
    @Override public T removeClass(StyleKey key) { super.removeClass(key); return self(); }
    @Override public T onClick(Runnable handler) { super.onClick(handler); return self(); }
    @Override public T onMouseEnter(Runnable handler) { super.onMouseEnter(handler); return self(); }
    @Override public T onMouseLeave(Runnable handler) { super.onMouseLeave(handler); return self(); }
    @Override public T onFocusGained(Runnable handler) { super.onFocusGained(handler); return self(); }
    @Override public T onFocusLost(Runnable handler) { super.onFocusLost(handler); return self(); }
    @Override public T setVisible(boolean v) { super.setVisible(v); return self(); }
    @Override public T setEnabled(boolean e) { super.setEnabled(e); return self(); }
    @Override public T setZIndex(int z) { super.setZIndex(z); return self(); }
    @Override public T setZIndex(ZIndex z) { super.setZIndex(z); return self(); }
    @Override public T setZIndex(ZIndex.Layer l) { super.setZIndex(l); return self(); }
    @Override public T setZIndex(ZIndex.Layer l, int p) { super.setZIndex(l, p); return self(); }
    @Override public T setConstraints(LayoutConstraints c) { super.setConstraints(c); return self(); }

    protected abstract TextComponent configureTextComponent(TextComponent comp);
    protected abstract TextComponent configurePlaceholderComponent(TextComponent comp);
    protected abstract void renderContent(DrawContext context, int cx, int cy, int cw, int ch);
    protected abstract boolean onKeyPressSpecific(int key, int sc, int mods, boolean ctrl, boolean shift);
    protected abstract boolean isValidCharacter(char chr);
    protected abstract String processClipboardText(String clip);
    protected abstract void onMouseClickText(double mouseX, double mouseY);
    protected abstract void onMouseDragText(double mouseX, double mouseY);
    protected abstract void onCharInserted();
    protected abstract void onPasteComplete();
    protected abstract void onTextModified();
}