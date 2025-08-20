package com.edgn.ui.core.item.items;

import com.edgn.ui.core.container.components.TextComponent;
import com.edgn.ui.core.item.BaseItem;
import com.edgn.ui.core.models.text.DefaultTextInputModel;
import com.edgn.ui.core.models.text.TextInputModel;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.css.values.Shadow;
import com.edgn.ui.layout.LayoutConstraints;
import com.edgn.ui.layout.ZIndex;
import com.edgn.ui.utils.DrawingUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings({"unused","unchecked","UnusedReturnValue"})
public class TextFieldItem extends BaseItem {
    private final TextInputModel model = new DefaultTextInputModel();
    private TextComponent textComponent;
    private TextComponent placeholderComponent;
    private int textSafetyMargin = 8;
    private long lastBlink = System.currentTimeMillis();
    private boolean caretVisible = true;
    private int selectionColor = 0x803A86FF;

    public TextFieldItem(UIStyleSystem styleSystem, int x, int y, int w, int h) {
        super(styleSystem, x, y, w, h);
        addClass(StyleKey.BG_SURFACE, StyleKey.ROUNDED_MD, StyleKey.P_2);
    }

    public TextFieldItem(UIStyleSystem styleSystem, int x, int y, int w, int h, String placeholder) {
        this(styleSystem, x, y, w, h);
        setPlaceholder(placeholder);
    }

    public TextFieldItem withText(String text) { model.setText(text); ensureTextComponent(); return this; }

    public TextFieldItem withText(TextComponent comp) {
        textComponent = comp == null ? null
                : comp.setOverflowMode(TextComponent.TextOverflowMode.TRUNCATE)
                .align(TextComponent.TextAlign.LEFT)
                .verticalAlign(TextComponent.VerticalAlign.MIDDLE)
                .setSafetyMargin(textSafetyMargin);
        model.setText(comp != null ? comp.getText() : "");
        return this;
    }


    public TextFieldItem withPlaceholder(String placeholder) {
        placeholderComponent = new TextComponent(placeholder == null ? "" : placeholder, textRenderer)
                .setOverflowMode(TextComponent.TextOverflowMode.TRUNCATE)
                .align(TextComponent.TextAlign.LEFT)
                .verticalAlign(TextComponent.VerticalAlign.MIDDLE)
                .setSafetyMargin(textSafetyMargin);
        return this;
    }
    public TextFieldItem withPlaceholder(TextComponent comp) {
        placeholderComponent = comp == null ? null
                : comp.setOverflowMode(TextComponent.TextOverflowMode.TRUNCATE)
                .align(TextComponent.TextAlign.LEFT)
                .verticalAlign(TextComponent.VerticalAlign.MIDDLE)
                .setSafetyMargin(textSafetyMargin);
        return this;
    }

    public TextFieldItem setText(String text) { return withText(text); }
    public TextFieldItem setText(TextComponent comp) { return withText(comp); }
    public TextFieldItem setPlaceholder(String placeholder) { return withPlaceholder(placeholder); }
    public TextFieldItem setPlaceholder(TextComponent comp) { return withPlaceholder(comp); }

    public TextFieldItem setPasswordMode(boolean enabled) { model.setPassword(enabled); return this; }
    public TextFieldItem setPasswordChar(char c) { model.setPasswordChar(c); return this; }
    public TextFieldItem setMaxLength(int max) { model.setMaxLength(max); return this; }

    public TextFieldItem setTextSafetyMargin(int m) {
        textSafetyMargin = Math.max(0, m);
        if (textComponent!=null) textComponent.setSafetyMargin(textSafetyMargin);
        if (placeholderComponent!=null) placeholderComponent.setSafetyMargin(textSafetyMargin);
        return this;
    }

    public TextFieldItem setSelectionColor(int argb) { selectionColor = argb; return this; }

    public String getText() { return model.getText(); }
    public int getCaretIndex() { return model.getCaret(); }
    public boolean hasSelection() { return model.hasSelection(); }
    public int getSelectionStart() { return model.getSelectionStart(); }
    public int getSelectionEnd() { return model.getSelectionEnd(); }

    public TextFieldItem textColor(int color) { ensureTextComponent().color(color); return this; }
    public TextFieldItem textBold() { ensureTextComponent().bold(); return this; }
    public TextFieldItem textItalic() { ensureTextComponent().italic(); return this; }
    public TextFieldItem textShadow() { ensureTextComponent().shadow(); return this; }
    public TextFieldItem textGlow() { ensureTextComponent().glow(); return this; }
    public TextFieldItem textGlow(int color) { ensureTextComponent().glow(color); return this; }
    public TextFieldItem textPulse() { ensureTextComponent().pulse(); return this; }
    public TextFieldItem textWave() { ensureTextComponent().wave(); return this; }
    public TextFieldItem textTypewriter() { ensureTextComponent().typewriter(); return this; }
    public TextFieldItem textRainbow() { ensureTextComponent().rainbow(); return this; }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!enabled || !canInteract(mouseX, mouseY)) return false;
        styleSystem.getEventManager().setFocus(this);
        setState(ItemState.HOVERED);
        moveCaretToMouse(mouseX);
        model.setSelection(model.getCaret(), model.getCaret());
        return true;
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, int button, double dx, double dy) {
        if (!enabled || !isFocused()) return false;
        int anchor = model.getSelectionStart();
        moveCaretToMouse(mouseX);
        model.setSelection(anchor, model.getCaret());
        return true;
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) { return isFocused(); }

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
            switch (key) {
                case GLFW.GLFW_KEY_LEFT  -> { moveCaret(model.wordLeft(),  shift); return true; }
                case GLFW.GLFW_KEY_RIGHT -> { moveCaret(model.wordRight(), shift); return true; }
                case GLFW.GLFW_KEY_BACKSPACE -> { model.backspace(true); return true; }
                case GLFW.GLFW_KEY_DELETE    -> { model.delete(true);    return true; }
                case GLFW.GLFW_KEY_A -> { model.setSelection(0, model.length()); return true; }
                case GLFW.GLFW_KEY_C -> { copySelection();  return true; }
                case GLFW.GLFW_KEY_X -> { cutSelection();   return true; }
                case GLFW.GLFW_KEY_V -> { pasteClipboard(); return true; }
                default -> {
                    return false;
                }
            }
        } else {
            switch (key) {
                case GLFW.GLFW_KEY_LEFT  -> { moveCaret(model.getCaret() - 1, shift); return true; }
                case GLFW.GLFW_KEY_RIGHT -> { moveCaret(model.getCaret() + 1, shift); return true; }
                case GLFW.GLFW_KEY_BACKSPACE -> { model.backspace(false); return true; }
                case GLFW.GLFW_KEY_DELETE    -> { model.delete(false);    return true; }
                case GLFW.GLFW_KEY_HOME      -> { moveCaret(0,              shift); return true; }
                case GLFW.GLFW_KEY_END       -> { moveCaret(model.length(), shift); return true; }
                case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> { return true; }
                default -> {
                    return false;
                }
            }
        }

    }

    @Override
    public boolean onCharTyped(char chr, int mods) {
        if (!enabled || !isFocused()) return false;
        if (chr >= 32 && chr != 127) { model.insert(String.valueOf(chr)); return true; }
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
        int baseBg = getBgColor(); if (baseBg == 0) baseBg = styleSystem.getColor(StyleKey.SURFACE);
        int bg = backgroundForState(baseBg);
        int radius = getBorderRadius();
        Shadow shadow = getShadow();
        if (shadow != null) DrawingUtils.drawShadow(context, cx, cy, cw, ch, 2, 2, shadow.color);
        DrawingUtils.drawRoundedRect(context, cx, cy, cw, ch, radius, bg);
        if (isFocused() && hasClass(StyleKey.FOCUS_RING)) {
            int focusColor = styleSystem.getColor(StyleKey.PRIMARY_LIGHT);
            DrawingUtils.drawRoundedRectBorder(context, cx - 2, cy - 2, cw + 4, ch + 4, radius + 2, focusColor, 2);
        }
        renderContent(context, cx, cy, cw, ch);
        blinkCaret();
    }

    private void renderContent(DrawContext context, int cx, int cy, int cw, int ch) {
        int x = cx + getPaddingLeft();
        int y = cy + getPaddingTop();
        int w = Math.max(0, cw - getPaddingLeft() - getPaddingRight());
        int h = Math.max(0, ch - getPaddingTop() - getPaddingBottom());
        String display = currentDisplay();
        ensureTextComponent();
        if (placeholderComponent == null) withPlaceholder("");
        if (!placeholderComponent.hasCustomStyling()) placeholderComponent.color(0x7FFFFFFF);
        DrawingUtils.pushClip(context, x, y, w, h);
        if (model.length() == 0 && !isFocused()) {
            placeholderComponent.render(context, x, y, w, h);
        } else {
            if (model.hasSelection()) renderSelection(context, x, y, w, h, display);
            textComponent.cloneWithNewText(display).render(context, x, y, w, h);
        }
        if (isFocused() && caretVisible) renderCaret(context, x, y, w, h, display);
        DrawingUtils.popClip(context);
    }

    private void renderSelection(DrawContext ctx, int x, int y, int w, int h, String display) {
        int baseY = y + (h - textRenderer.fontHeight) / 2;
        int sx = textXFor(display, x, model.getSelectionStart());
        int ex = textXFor(display, x, model.getSelectionEnd());
        if (ex < sx) { int t = sx; sx = ex; ex = t; }
        int pad = 1;
        DrawingUtils.fillRect(ctx, sx, baseY - pad, Math.max(0, ex - sx), textRenderer.fontHeight + pad * 2, selectionColor);
    }

    private void renderCaret(DrawContext ctx, int x, int y, int w, int h, String display) {
        int baseY = y + (h - textRenderer.fontHeight) / 2;
        int cx = textXFor(display, x, model.getCaret());
        int caretColor = (textComponent != null && textComponent.hasCustomStyling())
                        ? (textComponent.getColor() | 0xFF000000)
                        : (getComputedStyles().getTextColor() | 0xFF000000);

        DrawingUtils.drawVLine(ctx, cx, baseY - 1, baseY + textRenderer.fontHeight + 1, caretColor);
    }

    private int textXFor(String display, int x, int index) {
        String sub = display.substring(0, Math.clamp(index, 0, display.length()));
        return x + textRenderer.getWidth(sub);
    }

    private String currentDisplay() {
        if (!model.isPassword()) return model.getText();
        int n = model.length(); if (n <= 0) return "";
        char[] arr = new char[n]; for (int i=0;i<n;i++) arr[i] = model.getPasswordChar();
        return new String(arr);
    }

    private void blinkCaret() {
        long now = System.currentTimeMillis();
        if (now - lastBlink >= 500) { caretVisible = !caretVisible; lastBlink = now; }
    }

    private void moveCaretToMouse(double mouseX) {
        int x = getCalculatedX() + getPaddingLeft();
        String display = currentDisplay();
        int rel = (int) Math.max(0, mouseX - x);
        int best = 0;
        for (int i = 0; i <= display.length(); i++) {
            int width = textRenderer.getWidth(display.substring(0, i));
            if (width <= rel) best = i; else break;
        }
        model.setCaret(best);
    }

    private void moveCaret(int newIndex, boolean extend) {
        newIndex = Math.clamp(newIndex, 0, model.length());
        if (extend) {
            if (!model.hasSelection()) model.setSelection(model.getCaret(), newIndex);
            else model.setSelection(model.getSelectionStart(), newIndex);
        } else {
            model.clearSelection();
        }
        model.setCaret(newIndex);
        caretVisible = true; lastBlink = System.currentTimeMillis();
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

    private TextComponent ensureTextComponent() {
        if (textComponent == null) {
            textComponent = new TextComponent("", textRenderer)
                    .setOverflowMode(TextComponent.TextOverflowMode.TRUNCATE)
                    .align(TextComponent.TextAlign.LEFT)
                    .verticalAlign(TextComponent.VerticalAlign.MIDDLE)
                    .setSafetyMargin(textSafetyMargin)
                    .color(getComputedStyles().getTextColor());
        }
        return textComponent;
    }

    public TextFieldItem textColorIfUnset(int color) {
        if (!ensureTextComponent().hasCustomStyling()) textComponent.color(color);
        return this;
    }

    @Override public TextFieldItem addClass(StyleKey... keys) { super.addClass(keys); return this; }
    @Override public TextFieldItem removeClass(StyleKey key) { super.removeClass(key); return this; }
    @Override public TextFieldItem onClick(Runnable handler) { super.onClick(handler); return this; }
    @Override public TextFieldItem onMouseEnter(Runnable handler) { super.onMouseEnter(handler); return this; }
    @Override public TextFieldItem onMouseLeave(Runnable handler) { super.onMouseLeave(handler); return this; }
    @Override public TextFieldItem onFocusGained(Runnable handler) { super.onFocusGained(handler); return this; }
    @Override public TextFieldItem onFocusLost(Runnable handler) { super.onFocusLost(handler); return this; }
    @Override public TextFieldItem setVisible(boolean v) { super.setVisible(v); return this; }
    @Override public TextFieldItem setEnabled(boolean e) { super.setEnabled(e); return this; }
    @Override public TextFieldItem setZIndex(int z) { super.setZIndex(z); return this; }
    @Override public TextFieldItem setZIndex(ZIndex z) { super.setZIndex(z); return this; }
    @Override public TextFieldItem setZIndex(ZIndex.Layer l) { super.setZIndex(l); return this; }
    @Override public TextFieldItem setZIndex(ZIndex.Layer l, int p) { super.setZIndex(l, p); return this; }
    @Override public TextFieldItem setConstraints(LayoutConstraints c) { super.setConstraints(c); return this; }

    @Override
    public TextFieldItem setTextRenderer(TextRenderer tr) {
        super.setTextRenderer(tr);
        if (textComponent != null) textComponent.setTextRenderer(tr);
        if (placeholderComponent != null) placeholderComponent.setTextRenderer(tr);
        return this;
    }

    private void copySelection() {
        if (!model.hasSelection()) return;
        MinecraftClient.getInstance().keyboard.setClipboard(model.getText().substring(model.getSelectionStart(), model.getSelectionEnd()));
    }

    private void cutSelection() {
        if (!model.hasSelection()) return;
        MinecraftClient.getInstance().keyboard.setClipboard(model.getText().substring(model.getSelectionStart(), model.getSelectionEnd()));
        String before = model.getText().substring(0, model.getSelectionStart());
        String after = model.getText().substring(model.getSelectionEnd());
        model.setText(before + after);
        model.setCaret(before.length());
    }

    private void pasteClipboard() {
        String clip = MinecraftClient.getInstance().keyboard.getClipboard();
        if (clip == null || clip.isEmpty()) return;
        model.insert(clip.replace("\n"," ").replace("\r",""));
    }
}
