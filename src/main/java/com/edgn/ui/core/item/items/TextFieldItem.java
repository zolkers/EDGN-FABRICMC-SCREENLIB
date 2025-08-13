package com.edgn.ui.core.item.items;

import com.edgn.ui.core.container.components.TextComponent;
import com.edgn.ui.core.item.BaseItem;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.css.rules.Shadow;
import com.edgn.ui.layout.LayoutConstraints;
import com.edgn.ui.layout.ZIndex;
import com.edgn.ui.utils.DrawingUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings({"unused","unchecked","UnusedReturnValue"})
public class TextFieldItem extends BaseItem {
    private final StringBuilder value = new StringBuilder();
    private TextComponent textComponent;
    private TextComponent placeholderComponent;
    private int textSafetyMargin = 8;
    private int caretIndex = 0;
    private int selectionAnchor = -1;
    private long lastBlink = System.currentTimeMillis();
    private boolean caretVisible = true;
    private int maxLength = Integer.MAX_VALUE;
    private boolean passwordMode = false;
    private char passwordChar = '•';
    private int selectionColor = 0x803A86FF;

    public TextFieldItem(UIStyleSystem styleSystem, int x, int y, int w, int h) {
        super(styleSystem, x, y, w, h);
        addClass(StyleKey.BG_SURFACE, StyleKey.ROUNDED_MD, StyleKey.P_2);
    }

    public TextFieldItem(UIStyleSystem styleSystem, int x, int y, int w, int h, String placeholder) {
        this(styleSystem, x, y, w, h);
        setPlaceholder(placeholder);
    }

    public TextFieldItem withText(String text) {
        value.setLength(0);
        if (text != null) value.append(text);
        caretIndex = Math.min(value.length(), caretIndex);
        ensureTextComponent();
        return this;
    }

    public TextFieldItem withText(TextComponent comp) {
        textComponent = comp == null ? null
                : comp.setOverflowMode(TextComponent.TextOverflowMode.TRUNCATE)
                .align(TextComponent.TextAlign.LEFT)
                .verticalAlign(TextComponent.VerticalAlign.MIDDLE)
                .setSafetyMargin(textSafetyMargin);
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

    public TextFieldItem setPasswordMode(boolean enabled) { this.passwordMode = enabled; return this; }
    public TextFieldItem setPasswordChar(char c) { this.passwordChar = c; return this; }
    public TextFieldItem setMaxLength(int max) { this.maxLength = Math.max(0, max); trimToMax(); return this; }
    public TextFieldItem setTextSafetyMargin(int margin) { this.textSafetyMargin = Math.max(0, margin); if (textComponent!=null) textComponent.setSafetyMargin(textSafetyMargin); if (placeholderComponent!=null) placeholderComponent.setSafetyMargin(textSafetyMargin); return this; }
    public TextFieldItem setSelectionColor(int argb) { this.selectionColor = argb; return this; }

    public String getText() { return value.toString(); }
    public boolean isEmpty() { return value.length()==0; }
    public int length() { return value.length(); }
    public int getCaretIndex() { return caretIndex; }
    public boolean hasSelection() { return selectionAnchor >= 0 && selectionAnchor != caretIndex; }
    public int getSelectionStart() { return hasSelection() ? Math.min(selectionAnchor, caretIndex) : caretIndex; }
    public int getSelectionEnd() { return hasSelection() ? Math.max(selectionAnchor, caretIndex) : caretIndex; }
    public TextComponent getTextComponent() { return textComponent; }
    public TextComponent getPlaceholderComponent() { return placeholderComponent; }

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
        selectionAnchor = caretIndex;
        return true;
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, int button, double dx, double dy) {
        if (!enabled || !isFocused()) return false;
        moveCaretToMouse(mouseX);
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
        switch (key) {
            case GLFW.GLFW_KEY_LEFT -> { moveCaret(ctrl ? wordLeft() : caretIndex - 1, shift); return true; }
            case GLFW.GLFW_KEY_RIGHT -> { moveCaret(ctrl ? wordRight() : caretIndex + 1, shift); return true; }
            case GLFW.GLFW_KEY_HOME -> { moveCaret(0, shift); return true; }
            case GLFW.GLFW_KEY_END -> { moveCaret(value.length(), shift); return true; }
            case GLFW.GLFW_KEY_BACKSPACE -> { backspace(ctrl); return true; }
            case GLFW.GLFW_KEY_DELETE -> { delete(ctrl); return true; }
            case GLFW.GLFW_KEY_A -> { if (ctrl) { selectAll(); return true; } break; }
            case GLFW.GLFW_KEY_C -> { if (ctrl) { copySelection(); return true; } break; }
            case GLFW.GLFW_KEY_X -> { if (ctrl) { cutSelection(); return true; } break; }
            case GLFW.GLFW_KEY_V -> { if (ctrl) { pasteClipboard(); return true; } break; }
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> { return true; }
        }
        return false;
    }

    @Override
    public boolean onCharTyped(char chr, int mods) {
        if (!enabled || !isFocused()) return false;
        if (chr >= 32 && chr != 127) { insert(String.valueOf(chr)); return true; }
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
        int baseBg = getBgColor();
        if (baseBg == 0) baseBg = styleSystem.getColor(StyleKey.SURFACE);
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
        if (value.isEmpty()) {
            placeholderComponent.render(context, x, y, w, h);
        } else {
            if (hasSelection()) renderSelection(context, x, y, w, h, display);
            TextComponent renderComp = textComponent.cloneWithNewText(display);
            renderComp.render(context, x, y, w, h);
        }
        if (isFocused() && caretVisible) renderCaret(context, x, y, w, h, display);
        DrawingUtils.popClip(context);
    }

    private void renderSelection(DrawContext context, int x, int y, int w, int h, String display) {
        int baseY = y + (h - textRenderer.fontHeight) / 2;
        int sx = textXFor(display, x, getSelectionStart());
        int ex = textXFor(display, x, getSelectionEnd());
        if (ex < sx) { int t = sx; sx = ex; ex = t; }
        int pad = 1;
        DrawingUtils.fillRect(context, sx, baseY - pad, Math.max(0, ex - sx), textRenderer.fontHeight + pad * 2, selectionColor);
    }

    private void renderCaret(DrawContext context, int x, int y, int w, int h, String display) {
        int baseY = y + (h - textRenderer.fontHeight) / 2;
        int cx = textXFor(display, x, caretIndex);
        DrawingUtils.drawVLine(context, cx, baseY - 1, baseY + textRenderer.fontHeight + 1, getComputedStyles().textColor | 0xFF000000);
    }

    private int textXFor(String display, int x, int index) {
        String sub = safeSubstring(display, 0, Math.max(0, Math.min(index, display.length())));
        return x + textRenderer.getWidth(sub);
    }

    private String currentDisplay() {
        if (!passwordMode) return value.toString();
        int n = value.length();
        if (n <= 0) return "";
        char[] arr = new char[n];
        for (int i = 0; i < n; i++) arr[i] = passwordChar;
        return new String(arr);
    }

    private void blinkCaret() {
        long now = System.currentTimeMillis();
        if (now - lastBlink >= 500) {
            caretVisible = !caretVisible;
            lastBlink = now;
        }
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
        moveCaret(best, selectionAnchor >= 0);
    }

    private void moveCaret(int newIndex, boolean extendSelection) {
        newIndex = Math.max(0, Math.min(newIndex, value.length()));
        if (extendSelection) {
            if (selectionAnchor < 0) selectionAnchor = caretIndex;
        } else {
            selectionAnchor = -1;
        }
        caretIndex = newIndex;
        caretVisible = true;
        lastBlink = System.currentTimeMillis();
    }

    private int wordLeft() {
        int i = Math.max(0, Math.min(caretIndex - 1, value.length()));
        while (i > 0 && isSeparator(value.charAt(i))) i--;
        while (i > 0 && isWordChar(value.charAt(i - 1))) i--;
        return i;
    }

    private int wordRight() {
        int i = Math.max(0, Math.min(caretIndex, value.length()));
        int n = value.length();
        while (i < n && isSeparator(value.charAt(i))) i++;
        while (i < n && isWordChar(value.charAt(i))) i++;
        return i;
    }

    private boolean isWordChar(char c) { return Character.isLetterOrDigit(c) || c == '_' || c == '-'; }
    private boolean isSeparator(char c) { return !isWordChar(c) && !Character.isWhitespace(c); }

    private void insert(String s) {
        if (s == null || s.isEmpty()) return;
        int start = getSelectionStart();
        int end = getSelectionEnd();
        if (hasSelection()) {
            value.delete(start, end);
            caretIndex = start;
            selectionAnchor = -1;
        }
        int can = Math.max(0, maxLength - value.length());
        if (can <= 0) return;
        String ins = s.length() > can ? s.substring(0, can) : s;
        value.insert(caretIndex, ins);
        caretIndex += ins.length();
        caretVisible = true;
        lastBlink = System.currentTimeMillis();
    }

    private void backspace(boolean ctrl) {
        if (hasSelection()) { deleteSelection(); return; }
        if (caretIndex <= 0) return;
        int start = ctrl ? wordLeft() : caretIndex - 1;
        value.delete(start, caretIndex);
        caretIndex = start;
        caretVisible = true;
        lastBlink = System.currentTimeMillis();
    }

    private void delete(boolean ctrl) {
        if (hasSelection()) { deleteSelection(); return; }
        if (caretIndex >= value.length()) return;
        int end = ctrl ? wordRight() : caretIndex + 1;
        value.delete(caretIndex, end);
        caretVisible = true;
        lastBlink = System.currentTimeMillis();
    }

    private void deleteSelection() {
        int s = getSelectionStart();
        int e = getSelectionEnd();
        value.delete(s, e);
        caretIndex = s;
        selectionAnchor = -1;
        caretVisible = true;
        lastBlink = System.currentTimeMillis();
    }

    private void selectAll() {
        selectionAnchor = 0;
        caretIndex = value.length();
    }

    private void copySelection() {
        if (!hasSelection()) return;
        MinecraftClient.getInstance().keyboard.setClipboard(value.substring(getSelectionStart(), getSelectionEnd()));
    }

    private void cutSelection() {
        if (!hasSelection()) return;
        MinecraftClient.getInstance().keyboard.setClipboard(value.substring(getSelectionStart(), getSelectionEnd()));
        deleteSelection();
    }

    private void pasteClipboard() {
        String clip = MinecraftClient.getInstance().keyboard.getClipboard();
        if (clip == null || clip.isEmpty()) return;
        insert(clip);
    }

    private void trimToMax() {
        if (value.length() > maxLength) {
            value.setLength(maxLength);
            caretIndex = Math.min(caretIndex, maxLength);
            selectionAnchor = -1;
        }
    }

    private TextComponent ensureTextComponent() {
        if (textComponent == null) {
            textComponent = new TextComponent("", textRenderer)
                    .setOverflowMode(TextComponent.TextOverflowMode.TRUNCATE)
                    .align(TextComponent.TextAlign.LEFT)
                    .verticalAlign(TextComponent.VerticalAlign.MIDDLE)
                    .setSafetyMargin(textSafetyMargin)
                    .color(getComputedStyles().textColor);
        }
        return textComponent;
    }

    private String safeSubstring(String s, int from, int to) {
        int n = s.length();
        int a = Math.max(0, Math.min(from, n));
        int b = Math.max(a, Math.min(to, n));
        return s.substring(a, b);
    }

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
}
