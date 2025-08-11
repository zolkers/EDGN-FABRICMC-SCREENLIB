package com.edgn.ui.core.item.items;

import com.edgn.ui.core.item.BaseItem;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.css.rules.Shadow;
import com.edgn.ui.layout.LayoutConstraints;
import com.edgn.ui.layout.ZIndex;
import com.edgn.ui.utils.DrawContextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class TextFieldWidget extends BaseItem {
    private final StringBuilder value = new StringBuilder();
    private String placeholder = "";
    private int textSafetyMargin = 8;

    private int caret = 0;
    private int selectionAnchor = -1;
    private int scrollX = 0;

    private int maxLength = Integer.MAX_VALUE;
    private Predicate<String> validator = null;
    private boolean passwordMode = false;
    private char passwordMask = '•';

    private static final int CARET_BLINK_MS = 530;
    private long lastBlinkSwap = System.currentTimeMillis();
    private boolean caretOn = true;

    private long lastClickTime = 0;
    private static final long DOUBLE_CLICK_MS = 250;

    private java.util.function.Consumer<String> onChange;

    public TextFieldWidget(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
        addClass(StyleKey.BG_SURFACE, StyleKey.ROUNDED_MD, StyleKey.P_2, StyleKey.TEXT_WHITE, StyleKey.SHADOW_SM, StyleKey.FOCUS_RING);
    }

    public TextFieldWidget(UIStyleSystem styleSystem, int x, int y, int width, int height, String initialText) {
        this(styleSystem, x, y, width, height);
        setText(initialText);
    }

    public TextFieldWidget withPlaceholder(String text) { this.placeholder = Objects.toString(text, ""); return this; }
    public TextFieldWidget setPlaceholder(String text) { return withPlaceholder(text); }
    public TextFieldWidget setText(String text) {
        value.setLength(0);
        if (text != null) value.append(text);
        clampLength();
        moveCaretToEnd(false);
        fireChange();
        return this;
    }
    public String getText() { return value.toString(); }
    public boolean isEmpty() { return value.isEmpty(); }

    public TextFieldWidget setTextSafetyMargin(int margin) {
        this.textSafetyMargin = Math.max(0, margin);
        return this;
    }

    public TextFieldWidget setMaxLength(int max) {
        this.maxLength = Math.max(0, max);
        clampLength();
        return this;
    }

    public TextFieldWidget setValidator(Predicate<String> validator) { this.validator = validator; return this; }

    public TextFieldWidget password(boolean enabled) { this.passwordMode = enabled; return this; }
    public TextFieldWidget password(char mask) { this.passwordMode = true; this.passwordMask = mask; return this; }

    public TextFieldWidget onChange(java.util.function.Consumer<String> cb) { this.onChange = cb; return this; }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!enabled || !canInteract(mouseX, mouseY)) return false;

        styleSystem.getEventManager().setFocus(this);

        long now = System.currentTimeMillis();
        boolean doubleClick = (now - lastClickTime) < DOUBLE_CLICK_MS;
        lastClickTime = now;

        int idx = indexFromMouse((int) mouseX, (int) mouseY);
        if (doubleClick) {
            selectWordAt(idx);
        } else {
            caret = idx;
            clearSelection();
            selectionAnchor = caret;
        }
        ensureCaretVisible();
        setState(ItemState.FOCUSED);
        return true;
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!enabled || !focused) return false;
        int idx = indexFromMouse((int) mouseX, (int) mouseY);
        if (selectionAnchor < 0) selectionAnchor = caret;
        caret = idx;
        ensureCaretVisible();
        return true;
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        // garder le focus / sélection telle quelle
        return contains(mouseX, mouseY);
    }

    @Override
    public void onFocusGained() {
        super.onFocusGained();
        resetBlink();
        setState(ItemState.FOCUSED);
    }

    @Override
    public void onFocusLost() {
        super.onFocusLost();
        clearSelection();
        setState(ItemState.NORMAL);
    }

    @Override
    public boolean onCharTyped(char chr, int modifiers) {
        if (!enabled || !focused) return false;
        if (chr == '\r' || chr == '\n' || chr == '\t') return false;

        if (Character.isISOControl(chr)) return false;

        deleteSelectionIfAny();

        if (value.length() >= maxLength) return true;

        String newText = value.substring(0, caret) + chr + value.substring(caret);
        if (validator != null && !validator.test(newText)) return true;

        value.insert(caret, chr);
        caret++;
        ensureCaretVisible();
        fireChange();
        resetBlink();
        return true;
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (!enabled || !focused) return false;

        boolean shift = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;
        boolean ctrlOrCmd = (modifiers & (GLFW.GLFW_MOD_CONTROL | GLFW.GLFW_MOD_SUPER)) != 0;
        boolean handled = true;

        switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT -> moveCaretLeft(ctrlOrCmd, shift);
            case GLFW.GLFW_KEY_RIGHT -> moveCaretRight(ctrlOrCmd, shift);
            case GLFW.GLFW_KEY_HOME -> moveCaretToStart(shift);
            case GLFW.GLFW_KEY_END -> moveCaretToEnd(shift);
            case GLFW.GLFW_KEY_BACKSPACE -> backspace();
            case GLFW.GLFW_KEY_DELETE -> delete();
            case GLFW.GLFW_KEY_A -> { if (ctrlOrCmd) selectAll(); else handled = false; }
            case GLFW.GLFW_KEY_C -> { if (ctrlOrCmd) copySelection(); else handled = false; }
            case GLFW.GLFW_KEY_X -> { if (ctrlOrCmd) cutSelection(); else handled = false; }
            case GLFW.GLFW_KEY_V -> { if (ctrlOrCmd) pasteClipboard(); else handled = false; }
            default -> handled = false;
        }

        if (handled) {
            ensureCaretVisible();
            resetBlink();
        }
        return handled;
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

        float scale = (hasClass(StyleKey.HOVER_SCALE) && (hovered || state == ItemState.HOVERED))
                ? getAnimatedScale() : 1.0f;

        if (scale != 1.0f) {
            int sw = Math.max(0, Math.round(cw * scale));
            int sh = Math.max(0, Math.round(ch * scale));
            int ox = (sw - cw) / 2;
            int oy = (sh - ch) / 2;
            if (shadow != null) DrawContextUtils.drawShadow(context, cx - ox, cy - oy, sw, sh, 3, 3, shadow.color);
            DrawContextUtils.drawRoundedRect(context, cx - ox, cy - oy, sw, sh, radius, bg);
        } else {
            if (shadow != null) DrawContextUtils.drawShadow(context, cx, cy, cw, ch, 2, 2, shadow.color);
            DrawContextUtils.drawRoundedRect(context, cx, cy, cw, ch, radius, bg);
        }

        if (isFocused() && hasClass(StyleKey.FOCUS_RING)) {
            int focusColor = styleSystem.getColor(StyleKey.PRIMARY_LIGHT);
            DrawContextUtils.drawRoundedRectBorder(context, cx - 2, cy - 2, cw + 4, ch + 4, radius + 2, focusColor, 2);
        }

        renderTextAndCaret(context, cx, cy, cw, ch);

        markAsRendered();
    }

    private void renderTextAndCaret(DrawContext context, int cx, int cy, int cw, int ch) {
        int contentX = cx + getPaddingLeft();
        int contentY = cy + getPaddingTop();
        int contentW = Math.max(0, cw - getPaddingLeft() - getPaddingRight() - textSafetyMargin);
        int contentH = Math.max(0, ch - getPaddingTop() - getPaddingBottom());

        try {
            context.enableScissor(contentX, contentY, contentX + contentW, contentY + contentH);
        } catch (Throwable ignored) {}

        int textColor = getComputedStyles().textColor;
        int placeholderColor = (textColor & 0x00FFFFFF) | (0x66 << 24);
        int selectionColor = (styleSystem.getColor(StyleKey.PRIMARY_LIGHT) & 0x00FFFFFF) | (0x66 << 24);

        String fullText = passwordMode ? mask(value.length()) : value.toString();

        int textY = contentY + (contentH - textRenderer.fontHeight) / 2;

        if (hasSelection()) {
            int selStart = Math.min(caret, selectionAnchor);
            int selEnd = Math.max(caret, selectionAnchor);
            int x0 = contentX - scrollX + widthUpTo(fullText, selStart);
            int x1 = contentX - scrollX + widthUpTo(fullText, selEnd);
            int h = textRenderer.fontHeight + 2;
            int y0 = textY - 1;
            DrawContextUtils.fillRect(context, Math.max(x0, contentX), y0,
                    Math.min(x1, contentX + contentW), y0 + h, selectionColor);
        }

        int textX = contentX - scrollX;
        if (fullText.isEmpty() && !isFocused() && !placeholder.isEmpty()) {
            context.drawText(textRenderer, placeholder, textX, textY, placeholderColor, false);
        } else {
            context.drawText(textRenderer, fullText, textX, textY, textColor, false);
        }

        tickBlink();
        if (focused && caretOn && !hasSelection()) {
            int caretPx = contentX - scrollX + widthUpTo(fullText, caret);
            DrawContextUtils.fillRect(context, caretPx, textY - 1,
                    caretPx + 1, textY + textRenderer.fontHeight + 1, textColor);
        }

        try { context.disableScissor(); } catch (Throwable ignored) {}
    }

    private int widthUpTo(String s, int endExclusive) {
        if (endExclusive <= 0) return 0;
        endExclusive = Math.min(endExclusive, s.length());
        return textRenderer.getWidth(s.substring(0, endExclusive));
    }

    private int indexFromMouse(int mx, int my) {
        int cx = getCalculatedX() + getPaddingLeft();
        int cw = Math.max(0, getCalculatedWidth() - getPaddingLeft() - getPaddingRight() - textSafetyMargin);
        int localX = Math.max(0, Math.min(mx - cx + scrollX, Integer.MAX_VALUE));
        String s = passwordMode ? mask(value.length()) : value.toString();
        int best = 0;
        int prevWidth = 0;
        for (int i = 1; i <= s.length(); i++) {
            int w = textRenderer.getWidth(s.substring(0, i));
            if (localX < (prevWidth + w) / 2) {
                best = i - 1;
                break;
            }
            best = i;
            prevWidth = w;
        }
        if (best < 0) best = 0;
        if (best > value.length()) best = value.length();

        if (localX > cw + scrollX) ensureCaretVisible();
        return best;
    }

    private String mask(int count) {
        if (count <= 0) return "";
        char[] arr = new char[count];
        java.util.Arrays.fill(arr, passwordMask);
        return new String(arr);
    }

    private void selectWordAt(int idx) {
        if (value.isEmpty()) { clearSelection(); caret = 0; return; }
        idx = Math.max(0, Math.min(idx, value.length()));
        int start = idx, end = idx;
        while (start > 0 && !Character.isWhitespace(value.charAt(start - 1))) start--;
        while (end < value.length() && !Character.isWhitespace(value.charAt(end))) end++;
        caret = end;
        selectionAnchor = start;
    }

    private void selectAll() { selectionAnchor = 0; caret = value.length(); }
    private boolean hasSelection() { return selectionAnchor >= 0 && selectionAnchor != caret; }
    private void clearSelection() { selectionAnchor = -1; }
    private void deleteSelectionIfAny() {
        if (!hasSelection()) return;
        int a = Math.min(caret, selectionAnchor);
        int b = Math.max(caret, selectionAnchor);
        String newText = value.substring(0, a) + value.substring(b);
        if (validator != null && !validator.test(newText)) return;
        value.delete(a, b);
        caret = a;
        clearSelection();
        fireChange();
    }

    private void backspace() {
        if (hasSelection()) { deleteSelectionIfAny(); return; }
        if (caret <= 0) return;
        String newText = value.substring(0, caret - 1) + value.substring(caret);
        if (validator != null && !validator.test(newText)) return;
        value.deleteCharAt(caret - 1);
        caret--;
        fireChange();
    }

    private void delete() {
        if (hasSelection()) { deleteSelectionIfAny(); return; }
        if (caret >= value.length()) return;
        String newText = value.substring(0, caret) + value.substring(caret + 1);
        if (validator != null && !validator.test(newText)) return;
        value.deleteCharAt(caret);
        fireChange();
    }

    private void pasteClipboard() {
        String clip = Objects.toString(MinecraftClient.getInstance().keyboard.getClipboard(), "");
        if (clip.isEmpty()) return;
        deleteSelectionIfAny();
        int room = Math.max(0, maxLength - value.length());
        if (room == 0) return;
        String insert = clip.substring(0, Math.min(room, clip.length()));
        String newText = value.substring(0, caret) + insert + value.substring(caret);
        if (validator != null && !validator.test(newText)) return;
        value.insert(caret, insert);
        caret += insert.length();
        fireChange();
    }

    private void copySelection() {
        if (!hasSelection()) return;
        int a = Math.min(caret, selectionAnchor);
        int b = Math.max(caret, selectionAnchor);
        MinecraftClient.getInstance().keyboard.setClipboard(value.substring(a, b));
    }

    private void cutSelection() {
        if (!hasSelection()) return;
        copySelection();
        deleteSelectionIfAny();
    }

    private void moveCaretLeft(boolean byWord, boolean extend) {
        if (byWord) caret = wordLeft(caret); else caret = Math.max(0, caret - 1);
        updateSelection(extend);
    }

    private void moveCaretRight(boolean byWord, boolean extend) {
        if (byWord) caret = wordRight(caret); else caret = Math.min(value.length(), caret + 1);
        updateSelection(extend);
    }

    private void moveCaretToStart(boolean extend) { caret = 0; updateSelection(extend); }
    private void moveCaretToEnd(boolean extend) { caret = value.length(); updateSelection(extend); }
    private void moveCaretToEnd(boolean extend, boolean fire) { caret = value.length(); updateSelection(extend); if (fire) fireChange(); }

    private int wordLeft(int from) {
        int i = Math.max(0, Math.min(from, value.length()));
        while (i > 0 && Character.isWhitespace(value.charAt(i - 1))) i--;
        while (i > 0 && !Character.isWhitespace(value.charAt(i - 1))) i--;
        return i;
    }

    private int wordRight(int from) {
        int i = Math.max(0, Math.min(from, value.length()));
        while (i < value.length() && Character.isWhitespace(value.charAt(i))) i++;
        while (i < value.length() && !Character.isWhitespace(value.charAt(i))) i++;
        return i;
    }

    private void updateSelection(boolean extend) {
        if (extend) {
            if (selectionAnchor < 0) selectionAnchor = caret;
        } else {
            clearSelection();
        }
    }

    private void ensureCaretVisible() {
        int cx = getCalculatedX() + getPaddingLeft();
        int cw = Math.max(0, getCalculatedWidth() - getPaddingLeft() - getPaddingRight() - textSafetyMargin);

        String s = passwordMode ? mask(value.length()) : value.toString();
        int caretPx = widthUpTo(s, caret);

        if (caretPx - scrollX > cw) {
            scrollX = caretPx - cw + 2;
        } else if (caretPx - scrollX < 0) {
            scrollX = Math.max(0, caretPx - 2);
        }
    }

    private void clampLength() {
        if (value.length() > maxLength) value.setLength(maxLength);
    }

    private void fireChange() {
        if (onChange != null) onChange.accept(getText());
    }

    private void tickBlink() {
        long now = System.currentTimeMillis();
        if (now - lastBlinkSwap >= CARET_BLINK_MS) {
            caretOn = !caretOn;
            lastBlinkSwap = now;
        }
    }

    private void resetBlink() { caretOn = true; lastBlinkSwap = System.currentTimeMillis(); }

    private int backgroundForState(int base) {
        if (getState() == ItemState.PRESSED) return darken(base);
        if (hovered || getState() == ItemState.HOVERED) return brighten(base);
        return base;
    }

    private int brighten(int color) {
        int a = (color >>> 24) & 0xFF;
        int r = Math.min(255, ((color >>> 16) & 0xFF) + 12);
        int g = Math.min(255, ((color >>> 8) & 0xFF) + 12);
        int b = Math.min(255, (color & 0xFF) + 12);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int darken(int color) {
        int a = (color >>> 24) & 0xFF;
        int r = Math.max(0, ((color >>> 16) & 0xFF) - 18);
        int g = Math.max(0, ((color >>> 8) & 0xFF) - 18);
        int b = Math.max(0, (color & 0xFF) - 18);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    @Override public TextFieldWidget addClass(StyleKey... keys) { super.addClass(keys); return this; }
    @Override public TextFieldWidget removeClass(StyleKey key) { super.removeClass(key); return this; }
    @Override public TextFieldWidget onClick(Runnable handler) { super.onClick(handler); return this; }
    @Override public TextFieldWidget onMouseEnter(Runnable handler) { super.onMouseEnter(handler); return this; }
    @Override public TextFieldWidget onMouseLeave(Runnable handler) { super.onMouseLeave(handler); return this; }
    @Override public TextFieldWidget onFocusGained(Runnable handler) { super.onFocusGained(handler); return this; }
    @Override public TextFieldWidget onFocusLost(Runnable handler) { super.onFocusLost(handler); return this; }
    @Override public TextFieldWidget setVisible(boolean visible) { super.setVisible(visible); return this; }
    @Override public TextFieldWidget setEnabled(boolean enabled) { super.setEnabled(enabled); return this; }
    @Override public TextFieldWidget setZIndex(int zIndex) { super.setZIndex(zIndex); return this; }
    @Override public TextFieldWidget setZIndex(ZIndex zIndex) { super.setZIndex(zIndex); return this; }
    @Override public TextFieldWidget setZIndex(ZIndex.Layer layer) { super.setZIndex(layer); return this; }
    @Override public TextFieldWidget setZIndex(ZIndex.Layer layer, int priority) { super.setZIndex(layer, priority); return this; }
    @Override public TextFieldWidget setConstraints(LayoutConstraints constraints) { super.setConstraints(constraints); return this; }

    @Override
    public TextFieldWidget setTextRenderer(TextRenderer textRenderer) {
        super.setTextRenderer(textRenderer);
        return this;
    }
}
