package com.edgn.ui.core.item.items;

import com.edgn.ui.core.container.components.TextComponent;
import com.edgn.ui.core.item.AbstractTextItem;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.utils.DrawingUtils;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class TextAreaItem extends AbstractTextItem<TextAreaItem> {

    private int lineSpacing = 2;
    private int scrollY = 0;
    private boolean wrap = false;
    private int preferredCaretX = -1;

    private static final class Metrics {
        int x;
        int y;
        int w;
        int h;
        int lh;
    }

    public TextAreaItem(UIStyleSystem styleSystem, int x, int y, int w, int h) {
        super(styleSystem, x, y, w, h);
        addClass(StyleKey.BG_SURFACE, StyleKey.ROUNDED_MD, StyleKey.P_2);
    }

    public TextAreaItem(UIStyleSystem styleSystem, int x, int y, int w, int h, String placeholder) {
        this(styleSystem, x, y, w, h);
        setPlaceholder(placeholder);
    }

    public TextAreaItem setWrap(boolean enabled) { this.wrap = enabled; return this; }
    public TextAreaItem setLineSpacing(int px) { this.lineSpacing = Math.max(0, px); return this; }


    @Override
    protected TextComponent configureTextComponent(TextComponent comp) {
        return comp
                .align(TextComponent.TextAlign.LEFT)
                .verticalAlign(TextComponent.VerticalAlign.TOP)
                .setSafetyMargin(textSafetyMargin);
    }

    @Override
    protected TextComponent configurePlaceholderComponent(TextComponent comp) {
        return comp
                .align(TextComponent.TextAlign.LEFT)
                .verticalAlign(TextComponent.VerticalAlign.TOP)
                .setSafetyMargin(textSafetyMargin);
    }

    @Override
    protected void renderContent(DrawContext ctx, int cx, int cy, int cw, int ch) {
        Metrics m = computeMetrics(cx, cy, cw, ch);
        ensurePlaceholderStyled();

        List<String> lines = computeLines(model.getText(), m.w);
        int contentHeight = Math.max(m.lh, lines.size() * m.lh);
        scrollY = Math.clamp(scrollY, 0, Math.max(0, contentHeight - m.h));

        int firstLine = (m.lh > 0) ? scrollY / m.lh : 0;
        firstLine = Math.clamp(firstLine, 0, Math.max(0, lines.size() - 1));
        int yOffset = firstLine * m.lh - scrollY;

        DrawingUtils.pushClip(ctx, m.x, m.y, m.w, m.h);
        try {
            if (model.length() == 0) {
                renderEmptyState(ctx, m);
            } else {
                renderLinesBlock(ctx, lines, firstLine, yOffset, m);
            }
        } finally {
            DrawingUtils.popClip(ctx);
        }
    }

    @Override
    protected boolean onKeyPressSpecific(int key, int sc, int mods, boolean ctrl, boolean shift) {
        switch (key) {
            case GLFW.GLFW_KEY_LEFT  -> { moveLeftRight(-1, shift); return true; }
            case GLFW.GLFW_KEY_RIGHT -> { moveLeftRight(+1, shift); return true; }

            case GLFW.GLFW_KEY_UP    -> { moveCaretVertical(-1, shift); return true; }
            case GLFW.GLFW_KEY_DOWN  -> { moveCaretVertical(1,  shift); return true; }

            case GLFW.GLFW_KEY_HOME  -> { moveCaret(lineStart(model.getCaret()), shift); preferredCaretX = 0; return true; }
            case GLFW.GLFW_KEY_END   -> { moveCaret(lineEnd(model.getCaret()),   shift); preferredCaretX = Integer.MAX_VALUE; return true; }

            case GLFW.GLFW_KEY_BACKSPACE -> { model.backspace(ctrl); onTextModified(); return true; }
            case GLFW.GLFW_KEY_DELETE    -> { model.delete(ctrl); onTextModified(); return true; }

            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> { model.insert("\n"); onCharInserted(); return true; }

            default -> { return false; }
        }
    }

    @Override
    protected boolean isValidCharacter(char chr) {
        if (chr == '\r') chr = '\n';
        return (chr >= 32) || chr == '\n' || chr == '\t';
    }

    @Override
    protected String processClipboardText(String clip) {
        return clip.replace("\r\n", "\n").replace("\r", "\n");
    }

    @Override
    protected void onMouseClickText(double mouseX, double mouseY) {
        this.onMouseUpdate(mouseX, mouseY);
    }

    @Override
    protected void onMouseDragText(double mouseX, double mouseY) {
        this.onMouseUpdate(mouseX, mouseY);
    }

    private void onMouseUpdate(double mouseX, double mouseY){
        moveCaretToMouse(mouseX, mouseY);
        preferredCaretX = caretPixelXInLine(model.getCaret());
    }

    @Override
    protected void onCharInserted() {
        this.onTextUpdate();
    }

    @Override
    protected void onPasteComplete() {
        this.onTextUpdate();
    }

    @Override
    protected void onTextModified() {
        this.onTextUpdate();
    }

    private void onTextUpdate() {
        ensureCaretVisible();
        preferredCaretX = caretPixelXInLine(model.getCaret());
    }

    private Metrics computeMetrics(int cx, int cy, int cw, int ch) {
        int pl = getPaddingLeft();
        int pr = getPaddingRight();
        int pt = getPaddingTop();
        int pb = getPaddingBottom();

        Metrics m = new Metrics();
        m.x  = cx + pl;
        m.y  = cy + pt;
        m.w  = Math.clamp((long) cw - pl - pr, 0, Integer.MAX_VALUE);
        m.h  = Math.clamp((long) ch - pt - pb, 0, Integer.MAX_VALUE);
        m.lh = fontRenderer.lineHeight() + lineSpacing;
        return m;
    }

    private void ensurePlaceholderStyled() {
        ensureTextComponent();
        if (placeholderComponent == null) withPlaceholder("");
        if (!placeholderComponent.hasCustomStyling())
            placeholderComponent.color(PLACEHOLDER_COLOR_DEFAULT);
    }

    private void renderEmptyState(DrawContext ctx, Metrics m) {
        if (!isFocused()) {
            placeholderComponent
                    .verticalAlign(TextComponent.VerticalAlign.TOP)
                    .render(ctx, m.x, m.y, m.w, m.h);
            return;
        }
        if (!caretVisible) return;

        int caretColor = (textComponent != null && textComponent.hasCustomStyling())
                ? (textComponent.getColor() | 0xFF000000)
                : (getComputedStyles().getTextColor() | 0xFF000000);

        DrawingUtils.drawVLine(ctx, m.x, m.y - 1, m.y + fontRenderer.lineHeight() + 1, caretColor);
    }

    private void renderLinesBlock(DrawContext ctx, List<String> lines, int firstLine, int yOffset, Metrics m) {
        int globalIndex = indexAtLineStart(lines, firstLine);

        for (int i = firstLine; i < lines.size(); i++) {
            int lineY = m.y + yOffset + (i - firstLine) * m.lh;
            if (lineY > m.y + m.h) break;

            String line = lines.get(i);

            if (model.hasSelection()) {
                renderSelectionLine(ctx, m.x, lineY, line, globalIndex);
            }

            textComponent.cloneWithNewText(line)
                    .verticalAlign(TextComponent.VerticalAlign.TOP)
                    .render(ctx, m.x, lineY, m.w, fontRenderer.lineHeight());

            if (isFocused() && caretVisible) {
                renderCaretIfOnLine(ctx, m.x, lineY, line, globalIndex);
            }

            globalIndex += line.length() + 1;
        }
    }

    private void renderSelectionLine(DrawContext ctx, int x, int lineY, String line, int globalStart) {
        int lh = fontRenderer.lineHeight();
        int s = model.getSelectionStart();
        int e = model.getSelectionEnd();
        int lineEnd = globalStart + line.length();
        int rs = Math.clamp(s, globalStart, lineEnd);
        int re = Math.clamp(e, globalStart, lineEnd);
        if (re <= rs) return;

        int sx = x + fontRenderer.width(line.substring(0, rs - globalStart));
        int ex = x + fontRenderer.width(line.substring(0, re - globalStart));
        DrawingUtils.fillRect(ctx, sx, lineY, Math.max(0, ex - sx), lh, selectionColor);
    }

    private void renderCaretIfOnLine(DrawContext ctx, int x, int lineY, String line, int globalStart) {
        int lh = fontRenderer.lineHeight();
        int c = model.getCaret();
        int lineEnd = globalStart + line.length();
        if (c < globalStart || c > lineEnd) return;

        int col = c - globalStart;
        int cx = x + fontRenderer.width(line.substring(0, Math.clamp(col, 0, line.length())));
        int caretColor = (textComponent != null && textComponent.hasCustomStyling())
                ? (textComponent.getColor() | 0xFF000000)
                : (getComputedStyles().getTextColor() | 0xFF000000);

        DrawingUtils.drawVLine(ctx, cx, lineY - 1, lineY + lh + 1, caretColor);
    }

    private List<String> computeLines(String text, int maxWidth) {
        if (!wrap) {
            List<String> out = new ArrayList<>();
            String[] raw = text.split("\n", -1);
            Collections.addAll(out, raw);
            return out;
        }
        List<String> out = new ArrayList<>();
        String[] paras = text.split("\n", -1);
        for (String p : paras) out.addAll(fontRenderer.wrap(p, Math.max(1, maxWidth)));
        return out;
    }

    private int indexAtLineStart(List<String> lines, int line) {
        int idx = 0;
        for (int i = 0; i < Math.min(line, lines.size()); i++) idx += lines.get(i).length() + 1;
        return idx;
    }

    private int lineStart(int caret) {
        String t = model.getText();
        int i = Math.clamp(caret, 0, t.length());
        while (i > 0 && t.charAt(i - 1) != '\n') i--;
        return i;
    }

    private int lineEnd(int caret) {
        String t = model.getText();
        int i = Math.clamp(caret, 0, t.length());
        int n = t.length();
        while (i < n && t.charAt(i) != '\n') i++;
        return i;
    }

    private int caretPixelXInLine(int caret) {
        int ls = lineStart(caret);
        String line = model.getText().substring(ls, Math.max(ls, caret));
        return fontRenderer.width(line);
    }

    private void moveLeftRight(int dir, boolean shift) {
        moveCaret(model.getCaret() + dir, shift);
        preferredCaretX = caretPixelXInLine(model.getCaret());
    }

    private void moveCaretVertical(int dir, boolean extend) {
        int c = model.getCaret();
        int ls = lineStart(c);
        int le = lineEnd(c);
        int x = preferredCaretX >= 0 ? preferredCaretX : caretPixelXInLine(c);
        int targetPos;

        if (dir < 0) {
            if (ls == 0) {
                targetPos = 0;
            } else {
                int pls = lineStart(ls - 1);
                int ple = ls - 1;
                String prev = model.getText().substring(pls, ple);
                targetPos = pls + columnAtPixel(prev, x);
            }
        } else {
            int n = model.length();
            if (le >= n) {
                targetPos = n;
            } else {
                int nls = le + 1;
                int nle = lineEnd(le + 1);
                String next = model.getText().substring(nls, nle);
                targetPos = nls + columnAtPixel(next, x);
            }
        }

        moveCaret(targetPos, extend);
        ensureCaretVisible();
    }

    private int columnAtPixel(String line, int px) {
        int col = 0;
        for (int i = 0; i <= line.length(); i++) {
            int w = fontRenderer.width(line.substring(0, i));
            if (w <= px) col = i; else break;
        }
        return col;
    }

    private void ensureCaretVisible() {
        int lh = fontRenderer.lineHeight() + lineSpacing;
        int yTop = caretLineIndex() * lh;
        int yBottom = yTop + lh;
        int h = Math.max(1, getCalculatedHeight() - getPaddingTop() - getPaddingBottom());
        if (yTop < scrollY) scrollY = yTop;
        else if (yBottom > scrollY + h) scrollY = yBottom - h;
        scrollY = Math.max(0, scrollY);
    }

    private int caretLineIndex() {
        String t = model.getText();
        int idx = model.getCaret();
        int line = 0;
        for (int i = 0; i < Math.min(idx, t.length()); i++) if (t.charAt(i) == '\n') line++;
        return line;
    }

    private void moveCaretToMouse(double mouseX, double mouseY) {
        int x = getCalculatedX() + getPaddingLeft();
        int y = getCalculatedY() + getPaddingTop();
        int w = Math.max(1, getCalculatedWidth() - getPaddingLeft() - getPaddingRight());
        int h = Math.max(1, getCalculatedHeight() - getPaddingTop() - getPaddingBottom());

        List<String> lines = computeLines(model.getText(), w);
        int lh = fontRenderer.lineHeight() + lineSpacing;

        int my = (int) Math.max(0, mouseY - y) + scrollY;
        int maxIndex = Math.max(0, lines.size() - 1);
        int lineIdx  = Math.clamp(my / lh, 0, maxIndex);

        String line = lines.isEmpty() ? "" : lines.get(lineIdx);
        int idxInLine = 0;
        int relX = (int) Math.max(0, mouseX - x);
        for (int i = 0; i <= line.length(); i++) {
            int width = fontRenderer.width(line.substring(0, i));
            if (width <= relX) idxInLine = i; else break;
        }

        int global = indexAtLineStart(lines, lineIdx) + idxInLine;
        model.setCaret(Math.clamp(global, 0, model.length()));
        ensureCaretVisible();
    }
}