package com.edgn.ui.core.item.items;

import com.edgn.ui.core.container.components.TextComponent;
import com.edgn.ui.core.item.BaseItem;
import com.edgn.ui.core.models.text.DefaultTextInputModel;
import com.edgn.ui.core.models.text.TextInputModel;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.css.rules.Shadow;
import com.edgn.ui.layout.LayoutConstraints;
import com.edgn.ui.layout.ZIndex;
import com.edgn.ui.utils.DrawingUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused","unchecked","UnusedReturnValue"})
public class TextAreaItem extends BaseItem {
    private final TextInputModel model = new DefaultTextInputModel();
    private TextComponent textComponent;
    private TextComponent placeholderComponent;
    private int textSafetyMargin = 8;
    private long lastBlink = System.currentTimeMillis();
    private boolean caretVisible = true;
    private int selectionColor = 0x803A86FF;
    private int lineSpacing = 2;
    private int scrollY = 0;
    private boolean wrap = false;
    private int preferredCaretX = -1;

    public TextAreaItem(UIStyleSystem styleSystem, int x, int y, int w, int h) {
        super(styleSystem, x, y, w, h);
        addClass(StyleKey.BG_SURFACE, StyleKey.ROUNDED_MD, StyleKey.P_2);
    }

    public TextAreaItem(UIStyleSystem styleSystem, int x, int y, int w, int h, String placeholder) {
        this(styleSystem, x, y, w, h);
        setPlaceholder(placeholder);
    }

    public TextAreaItem withText(String text) { model.setText(text); ensureTextComponent(); return this; }

    public TextAreaItem withText(TextComponent comp) {
        textComponent = comp == null ? null
                : comp.align(TextComponent.TextAlign.LEFT)
                .verticalAlign(TextComponent.VerticalAlign.TOP)
                .setSafetyMargin(textSafetyMargin);
        model.setText(comp != null ? comp.getText() : "");
        return this;
    }

    public TextAreaItem withPlaceholder(String placeholder) {
        placeholderComponent = new TextComponent(placeholder == null ? "" : placeholder, textRenderer)
                .align(TextComponent.TextAlign.LEFT)
                .verticalAlign(TextComponent.VerticalAlign.TOP)
                .setSafetyMargin(textSafetyMargin);
        return this;
    }
    public TextAreaItem withPlaceholder(TextComponent comp) {
        placeholderComponent = comp == null ? null
                : comp.align(TextComponent.TextAlign.LEFT)
                .verticalAlign(TextComponent.VerticalAlign.TOP)
                .setSafetyMargin(textSafetyMargin);
        return this;
    }

    public TextAreaItem setWrap(boolean enabled) { wrap = enabled; return this; }
    public TextAreaItem setLineSpacing(int px) { lineSpacing = Math.max(0, px); return this; }
    public TextAreaItem setTextSafetyMargin(int m) { textSafetyMargin = Math.max(0, m); if (textComponent!=null) textComponent.setSafetyMargin(textSafetyMargin); if (placeholderComponent!=null) placeholderComponent.setSafetyMargin(textSafetyMargin); return this; }
    public TextAreaItem setSelectionColor(int argb) { selectionColor = argb; return this; }
    public TextAreaItem textColor(int color) { ensureTextComponent().color(color); return this; }
    public TextAreaItem textBold() { ensureTextComponent().bold(); return this; }
    public TextAreaItem textItalic() { ensureTextComponent().italic(); return this; }
    public TextAreaItem textShadow() { ensureTextComponent().shadow(); return this; }
    public TextAreaItem textGlow() { ensureTextComponent().glow(); return this; }
    public TextAreaItem textGlow(int color) { ensureTextComponent().glow(color); return this; }
    public TextAreaItem textPulse() { ensureTextComponent().pulse(); return this; }
    public TextAreaItem textWave() { ensureTextComponent().wave(); return this; }
    public TextAreaItem textTypewriter() { ensureTextComponent().typewriter(); return this; }
    public TextAreaItem textRainbow() { ensureTextComponent().rainbow(); return this; }
    public TextAreaItem setPlaceholder(String placeholder) { return withPlaceholder(placeholder); }
    public TextAreaItem setPlaceholder(TextComponent comp) { return withPlaceholder(comp); }
    public TextAreaItem setText(String text) { return withText(text); }
    public TextAreaItem setText(TextComponent comp) { return withText(comp); }

    public String getText() { return model.getText(); }
    public int getCaretIndex() { return model.getCaret(); }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!enabled || !canInteract(mouseX, mouseY)) return false;
        styleSystem.getEventManager().setFocus(this);
        setState(ItemState.HOVERED);
        moveCaretToMouse(mouseX, mouseY);
        model.setSelection(model.getCaret(), model.getCaret());
        preferredCaretX = caretPixelXInLine(model.getCaret());
        return true;
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, int button, double dx, double dy) {
        if (!enabled || !isFocused()) return false;
        int anchor = model.getSelectionStart();
        moveCaretToMouse(mouseX, mouseY);
        model.setSelection(anchor, model.getCaret());
        preferredCaretX = caretPixelXInLine(model.getCaret());
        return true;
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) { return isFocused(); }

    @Override
    public void onMouseEnter() { if (!enabled) return; setState(ItemState.HOVERED); }

    @Override
    public void onMouseLeave() { if (!enabled) return; if (!isFocused()) setState(ItemState.NORMAL); }

    @Override
    public boolean onKeyPress(int key, int sc, int mods) {
        if (!enabled || !isFocused()) return false;
        boolean ctrl = (mods & GLFW.GLFW_MOD_CONTROL) != 0 || (mods & GLFW.GLFW_MOD_SUPER) != 0;
        boolean shift = (mods & GLFW.GLFW_MOD_SHIFT) != 0;
        switch (key) {
            case GLFW.GLFW_KEY_LEFT -> { moveCaret(ctrl ? model.wordLeft() : model.getCaret()-1, shift); preferredCaretX = caretPixelXInLine(model.getCaret()); return true; }
            case GLFW.GLFW_KEY_RIGHT -> { moveCaret(ctrl ? model.wordRight() : model.getCaret()+1, shift); preferredCaretX = caretPixelXInLine(model.getCaret()); return true; }
            case GLFW.GLFW_KEY_UP -> { moveCaretVertical(-1, shift); return true; }
            case GLFW.GLFW_KEY_DOWN -> { moveCaretVertical(1, shift); return true; }
            case GLFW.GLFW_KEY_HOME -> { moveCaret(lineStart(model.getCaret()), shift); preferredCaretX = 0; return true; }
            case GLFW.GLFW_KEY_END -> { moveCaret(lineEnd(model.getCaret()), shift); preferredCaretX = Integer.MAX_VALUE; return true; }
            case GLFW.GLFW_KEY_BACKSPACE -> { model.backspace(ctrl); ensureCaretVisible(); return true; }
            case GLFW.GLFW_KEY_DELETE -> { model.delete(ctrl); ensureCaretVisible(); return true; }
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> { model.insert("\n"); ensureCaretVisible(); return true; }
            case GLFW.GLFW_KEY_A -> { if (ctrl) { model.setSelection(0, model.length()); return true; } break; }
            case GLFW.GLFW_KEY_C -> { if (ctrl) { copySelection(); return true; } break; }
            case GLFW.GLFW_KEY_X -> { if (ctrl) { cutSelection(); return true; } break; }
            case GLFW.GLFW_KEY_V -> { if (ctrl) { pasteClipboard(); ensureCaretVisible(); return true; } break; }
        }
        return false;
    }

    @Override
    public boolean onCharTyped(char chr, int mods) {
        if (!enabled || !isFocused()) return false;
        if (chr == '\r') chr = '\n';
        if (chr >= 32 || chr == '\n' || chr == '\t') { model.insert(String.valueOf(chr)); ensureCaretVisible(); preferredCaretX = caretPixelXInLine(model.getCaret()); return true; }
        return false;
    }

    @Override
    public void render(DrawContext context) {
        if (!visible) return;
        updateConstraints();
        int cx = getCalculatedX(), cy = getCalculatedY(), cw = getCalculatedWidth(), ch = getCalculatedHeight();
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

    private void renderContent(DrawContext ctx, int cx, int cy, int cw, int ch) {
        int x = cx + getPaddingLeft(), y = cy + getPaddingTop();
        int w = Math.max(0, cw - getPaddingLeft() - getPaddingRight());
        int h = Math.max(0, ch - getPaddingTop() - getPaddingBottom());

        ensureTextComponent();
        if (placeholderComponent == null) withPlaceholder("");
        if (!placeholderComponent.hasCustomStyling()) placeholderComponent.color(0x7FFFFFFF);

        List<String> lines = computeLines(model.getText(), w);
        int lh = textRenderer.fontHeight + lineSpacing;
        int contentHeight = Math.max(lh, lines.size() * lh);
        scrollY = Math.max(0, Math.min(scrollY, Math.max(0, contentHeight - h)));

        int firstLine = Math.max(0, scrollY / lh);
        int yOffset = firstLine * lh - scrollY;

        DrawingUtils.pushClip(ctx, x, y, w, h);

        if (model.length() == 0) {
            if (!isFocused()) {
                placeholderComponent
                        .verticalAlign(TextComponent.VerticalAlign.TOP)
                        .render(ctx, x, y, w, h);
            } else if (caretVisible) {
                int caretColor =
                        (textComponent != null && textComponent.hasCustomStyling())
                                ? (textComponent.getColor() | 0xFF000000)
                                : (getComputedStyles().textColor | 0xFF000000);

                DrawingUtils.drawVLine(
                        ctx,
                        x,
                        y - 1,
                        y + textRenderer.fontHeight + 1,
                        caretColor
                );
            }
        } else {
            int globalIndex = indexAtLineStart(lines, firstLine);
            for (int i = firstLine; i < lines.size(); i++) {
                int lineY = y + yOffset + (i - firstLine) * lh;
                if (lineY > y + h) break;

                String line = lines.get(i);

                if (model.hasSelection()) {
                    renderSelectionLine(ctx, x, lineY, w, line, globalIndex);
                }

                textComponent.cloneWithNewText(line)
                        .verticalAlign(TextComponent.VerticalAlign.TOP)
                        .render(ctx, x, lineY, w, textRenderer.fontHeight);

                if (isFocused() && caretVisible) {
                    renderCaretIfOnLine(ctx, x, lineY, line, globalIndex);
                }

                globalIndex += line.length() + 1;
            }
        }

        DrawingUtils.popClip(ctx);
    }


    private void renderSelectionLine(DrawContext ctx, int x, int lineY, int w, String line, int globalStart) {
        int lh = textRenderer.fontHeight;
        int s = model.getSelectionStart(), e = model.getSelectionEnd();
        int lineEnd = globalStart + line.length();
        int rs = Math.max(globalStart, Math.min(lineEnd, s));
        int re = Math.max(globalStart, Math.min(lineEnd, e));
        if (re <= rs) return;
        int sx = x + textRenderer.getWidth(line.substring(0, rs - globalStart));
        int ex = x + textRenderer.getWidth(line.substring(0, re - globalStart));
        DrawingUtils.fillRect(ctx, sx, lineY, Math.max(0, ex - sx), lh, selectionColor);
    }

    private void renderCaretIfOnLine(DrawContext ctx, int x, int lineY, String line, int globalStart) {
        int lh = textRenderer.fontHeight;
        int c = model.getCaret();
        int lineEnd = globalStart + line.length();
        if (c < globalStart || c > lineEnd) return;
        int col = c - globalStart;
        int cx = x + textRenderer.getWidth(line.substring(0, Math.max(0, Math.min(col, line.length()))));
        int caretColor = (textComponent != null && textComponent.hasCustomStyling())
                        ? (textComponent.getColor() | 0xFF000000)
                        : (getComputedStyles().textColor | 0xFF000000);

        DrawingUtils.drawVLine(ctx, cx, lineY - 1, lineY + lh + 1, caretColor);
    }

    private List<String> computeLines(String text, int maxWidth) {
        if (!wrap) {
            List<String> out = new ArrayList<>();
            String[] raw = text.split("\n", -1);
            Collections.addAll(out, raw);
            return out;
        }
        List<OrderedText> ordered = textRenderer.wrapLines(Text.literal(text), Math.max(1, maxWidth));
        List<String> out = new ArrayList<>(ordered.size());
        for (OrderedText ot : ordered) {
            StringBuilder sb = new StringBuilder();
            ot.accept((i, style, cp) -> { sb.append(Character.toChars(cp)); return true; });
            out.add(sb.toString());
        }
        return out;
    }

    private int indexAtLineStart(List<String> lines, int line) {
        int idx = 0;
        for (int i = 0; i < Math.min(line, lines.size()); i++) idx += lines.get(i).length() + 1;
        return idx;
    }

    private int lineStart(int caret) {
        String t = model.getText();
        int i = Math.max(0, Math.min(caret, t.length()));
        while (i > 0 && t.charAt(i - 1) != '\n') i--;
        return i;
    }

    private int lineEnd(int caret) {
        String t = model.getText();
        int i = Math.max(0, Math.min(caret, t.length()));
        int n = t.length();
        while (i < n && t.charAt(i) != '\n') i++;
        return i;
    }

    private int caretPixelXInLine(int caret) {
        int ls = lineStart(caret);
        String line = model.getText().substring(ls, Math.max(ls, caret));
        return textRenderer.getWidth(line);
    }

    private void moveCaretVertical(int dir, boolean extend) {
        int c = model.getCaret();
        int ls = lineStart(c), le = lineEnd(c);
        int x = preferredCaretX >= 0 ? preferredCaretX : caretPixelXInLine(c);
        int targetPos = c;
        if (dir < 0) {
            if (ls == 0) { targetPos = 0; }
            else {
                int pls = lineStart(ls - 1), ple = le == ls ? ls - 1 : ls - 1;
                String prev = model.getText().substring(pls, ple);
                targetPos = pls + columnAtPixel(prev, x);
            }
        } else {
            int n = model.length();
            if (le >= n) { targetPos = n; }
            else {
                int nls = le + 1, nle = lineEnd(le + 1);
                String next = model.getText().substring(nls, nle);
                targetPos = nls + columnAtPixel(next, x);
            }
        }
        moveCaret(targetPos, extend);
        ensureCaretVisible();
    }

    private int columnAtPixel(String line, int px) {
        int col = 0, acc = 0;
        for (int i = 0; i <= line.length(); i++) {
            int w = textRenderer.getWidth(line.substring(0, i));
            if (w <= px) { col = i; acc = w; } else break;
        }
        return col;
    }

    private void ensureCaretVisible() {
        int lh = textRenderer.fontHeight + lineSpacing;
        int yTop = caretLineIndex() * lh;
        int yBottom = yTop + lh;
        int h = Math.max(1, getCalculatedHeight() - getPaddingTop() - getPaddingBottom());
        if (yTop < scrollY) scrollY = yTop;
        else if (yBottom > scrollY + h) scrollY = yBottom - h;
        scrollY = Math.max(0, scrollY);
    }

    private int caretLineIndex() {
        String t = model.getText();
        int idx = model.getCaret(), line = 0;
        for (int i = 0; i < Math.min(idx, t.length()); i++) if (t.charAt(i) == '\n') line++;
        return line;
    }

    private void blinkCaret() {
        long now = System.currentTimeMillis();
        if (now - lastBlink >= 500) { caretVisible = !caretVisible; lastBlink = now; }
    }

    private void moveCaretToMouse(double mouseX, double mouseY) {
        int x = getCalculatedX() + getPaddingLeft();
        int y = getCalculatedY() + getPaddingTop();
        int w = Math.max(1, getCalculatedWidth() - getPaddingLeft() - getPaddingRight());
        int h = Math.max(1, getCalculatedHeight() - getPaddingTop() - getPaddingBottom());
        List<String> lines = computeLines(model.getText(), w);
        int lh = textRenderer.fontHeight + lineSpacing;
        int my = (int) Math.max(0, mouseY - y) + scrollY;
        int lineIdx = Math.max(0, Math.min(lines.size() - 1, my / lh));
        String line = lines.isEmpty() ? "" : lines.get(lineIdx);
        int idxInLine = 0;
        int relX = (int) Math.max(0, mouseX - x);
        for (int i = 0; i <= line.length(); i++) {
            int width = textRenderer.getWidth(line.substring(0, i));
            if (width <= relX) idxInLine = i; else break;
        }
        int global = indexAtLineStart(lines, lineIdx) + idxInLine;
        model.setCaret(Math.max(0, Math.min(global, model.length())));
        preferredCaretX = caretPixelXInLine(model.getCaret());
        ensureCaretVisible();
    }

    private void moveCaret(int newIndex, boolean extend) {
        newIndex = Math.max(0, Math.min(newIndex, model.length()));
        if (extend) {
            if (!model.hasSelection()) model.setSelection(model.getCaret(), newIndex);
            else model.setSelection(model.getSelectionStart(), newIndex);
        } else {
            model.clearSelection();
        }
        model.setCaret(newIndex);
        caretVisible = true; lastBlink = System.currentTimeMillis();
    }

    private int backgroundForState(int base) {
        return switch (getState()) {
            case HOVERED -> brighten(base, hasClass(StyleKey.HOVER_BRIGHTEN) ? 0.20f : 0.08f);
            default -> base;
        };
    }

    private int brighten(int color, float ratio) {
        int a = (color >>> 24) & 0xFF, r = (color >>> 16) & 0xFF, g = (color >>> 8) & 0xFF, b = color & 0xFF;
        r = Math.min(255, Math.round(r + (255 - r) * ratio));
        g = Math.min(255, Math.round(g + (255 - g) * ratio));
        b = Math.min(255, Math.round(b + (255 - b) * ratio));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private TextComponent ensureTextComponent() {
        if (textComponent == null) {
            textComponent = new TextComponent("", textRenderer)
                    .align(TextComponent.TextAlign.LEFT)
                    .verticalAlign(TextComponent.VerticalAlign.TOP)
                    .setSafetyMargin(textSafetyMargin)
                    .color(getComputedStyles().textColor);
        }
        return textComponent;
    }

    @Override public TextAreaItem addClass(StyleKey... keys) { super.addClass(keys); return this; }
    @Override public TextAreaItem removeClass(StyleKey key) { super.removeClass(key); return this; }
    @Override public TextAreaItem onClick(Runnable handler) { super.onClick(handler); return this; }
    @Override public TextAreaItem onMouseEnter(Runnable handler) { super.onMouseEnter(handler); return this; }
    @Override public TextAreaItem onMouseLeave(Runnable handler) { super.onMouseLeave(handler); return this; }
    @Override public TextAreaItem onFocusGained(Runnable handler) { super.onFocusGained(handler); return this; }
    @Override public TextAreaItem onFocusLost(Runnable handler) { super.onFocusLost(handler); return this; }
    @Override public TextAreaItem setVisible(boolean v) { super.setVisible(v); return this; }
    @Override public TextAreaItem setEnabled(boolean e) { super.setEnabled(e); return this; }
    @Override public TextAreaItem setZIndex(int z) { super.setZIndex(z); return this; }
    @Override public TextAreaItem setZIndex(ZIndex z) { super.setZIndex(z); return this; }
    @Override public TextAreaItem setZIndex(ZIndex.Layer l) { super.setZIndex(l); return this; }
    @Override public TextAreaItem setZIndex(ZIndex.Layer l, int p) { super.setZIndex(l, p); return this; }
    @Override public TextAreaItem setConstraints(LayoutConstraints c) { super.setConstraints(c); return this; }

    @Override
    public TextAreaItem setTextRenderer(TextRenderer tr) {
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
        ensureCaretVisible();
    }

    private void pasteClipboard() {
        String clip = MinecraftClient.getInstance().keyboard.getClipboard();
        if (clip == null || clip.isEmpty()) return;
        model.insert(clip);
    }
}
