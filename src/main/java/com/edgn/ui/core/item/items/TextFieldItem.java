package com.edgn.ui.core.item.items;

import com.edgn.ui.core.container.components.TextComponent;
import com.edgn.ui.core.item.AbstractTextItem;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.utils.DrawingUtils;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class TextFieldItem extends AbstractTextItem<TextFieldItem> {

    public TextFieldItem(UIStyleSystem styleSystem, int x, int y, int w, int h) {
        super(styleSystem, x, y, w, h);
    }

    public TextFieldItem(UIStyleSystem styleSystem, int x, int y, int w, int h, String placeholder) {
        super(styleSystem, x, y, w, h, placeholder);
    }

    public TextFieldItem setPasswordMode(boolean enabled) {
        model.setPassword(enabled);
        return this;
    }

    public TextFieldItem setPasswordChar(char c) {
        model.setPasswordChar(c);
        return this;
    }

    public TextFieldItem setMaxLength(int max) {
        model.setMaxLength(max);
        return this;
    }

    public TextFieldItem textColorIfUnset(int color) {
        if (!ensureTextComponent().hasCustomStyling()) textComponent.color(color);
        return this;
    }

    @Override
    protected TextComponent configureTextComponent(TextComponent comp) {
        return comp.setOverflowMode(TextComponent.TextOverflowMode.TRUNCATE)
                .align(TextComponent.TextAlign.LEFT)
                .verticalAlign(TextComponent.VerticalAlign.MIDDLE)
                .setSafetyMargin(textSafetyMargin);
    }

    @Override
    protected TextComponent configurePlaceholderComponent(TextComponent comp) {
        return comp.setOverflowMode(TextComponent.TextOverflowMode.TRUNCATE)
                .align(TextComponent.TextAlign.LEFT)
                .verticalAlign(TextComponent.VerticalAlign.MIDDLE)
                .setSafetyMargin(textSafetyMargin);
    }

    @Override
    protected void renderContent(DrawContext context, int cx, int cy, int cw, int ch) {
        int x = cx + getPaddingLeft();
        int y = cy + getPaddingTop();
        int w = Math.max(0, cw - getPaddingLeft() - getPaddingRight());
        int h = Math.max(0, ch - getPaddingTop() - getPaddingBottom());

        String display = currentDisplay();
        ensureTextComponent();
        if (placeholderComponent == null) withPlaceholder("");
        if (!placeholderComponent.hasCustomStyling())
            placeholderComponent.color(PLACEHOLDER_COLOR_DEFAULT);

        DrawingUtils.pushClip(context, x, y, w, h);

        if (model.length() == 0 && !isFocused()) {
            placeholderComponent.render(context, x, y, w, h);
        } else {
            if (model.hasSelection()) renderSelection(context, x, y, h, display);
            textComponent.cloneWithNewText(display).render(context, x, y, w, h);
        }

        if (isFocused() && caretVisible) renderCaret(context, x, y, h, display);

        DrawingUtils.popClip(context);
    }

    @Override
    protected boolean onKeyPressSpecific(int key, int sc, int mods, boolean ctrl, boolean shift) {
        if (ctrl) {
            switch (key) {
                case GLFW.GLFW_KEY_LEFT -> { moveCaret(model.wordLeft(), shift); return true; }
                case GLFW.GLFW_KEY_RIGHT -> { moveCaret(model.wordRight(), shift); return true; }
                case GLFW.GLFW_KEY_BACKSPACE -> { model.backspace(true); onTextModified(); return true; }
                case GLFW.GLFW_KEY_DELETE -> { model.delete(true); onTextModified(); return true; }
                default -> { return false; }
            }
        } else {
            switch (key) {
                case GLFW.GLFW_KEY_LEFT -> { moveCaret(model.getCaret() - 1, shift); return true; }
                case GLFW.GLFW_KEY_RIGHT -> { moveCaret(model.getCaret() + 1, shift); return true; }
                case GLFW.GLFW_KEY_BACKSPACE -> { model.backspace(false); onTextModified(); return true; }
                case GLFW.GLFW_KEY_DELETE -> { model.delete(false); onTextModified(); return true; }
                case GLFW.GLFW_KEY_HOME -> { moveCaret(0, shift); return true; }
                case GLFW.GLFW_KEY_END -> { moveCaret(model.length(), shift); return true; }
                case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> { return true; }
                default -> { return false; }
            }
        }
    }

    @Override
    protected boolean isValidCharacter(char chr) {
        return chr >= 32 && chr != 127;
    }

    @Override
    protected String processClipboardText(String clip) {
        return clip.replace("\n", " ").replace("\r", "");
    }

    @Override
    protected void onMouseClickText(double mouseX, double mouseY) {
        moveCaretToMouse(mouseX);
    }

    @Override
    protected void onMouseDragText(double mouseX, double mouseY) {
        moveCaretToMouse(mouseX);
    }

    @Override
    protected void onCharInserted() {
        //No need to listen on it
    }

    @Override
    protected void onPasteComplete() {
        //No need to listen on it
    }

    @Override
    protected void onTextModified() {
        //No need to listen on it
    }

    private void renderSelection(DrawContext ctx, int x, int y, int h, String display) {
        int baseY = y + (h - fontRenderer.lineHeight()) / 2;
        int sx = textXFor(display, x, model.getSelectionStart());
        int ex = textXFor(display, x, model.getSelectionEnd());
        if (ex < sx) { int t = sx; sx = ex; ex = t; }
        int pad = 1;
        DrawingUtils.fillRect(ctx, sx, baseY - pad, Math.max(0, ex - sx),
                fontRenderer.lineHeight() + pad * 2, selectionColor);
    }

    private void renderCaret(DrawContext ctx, int x, int y, int h, String display) {
        int baseY = y + (h - fontRenderer.lineHeight()) / 2;
        int cx = textXFor(display, x, model.getCaret());
        int caretColor = (textComponent != null && textComponent.hasCustomStyling())
                ? (textComponent.getColor() | 0xFF000000)
                : (getComputedStyles().getTextColor() | 0xFF000000);

        DrawingUtils.drawVLine(ctx, cx, baseY - 1, baseY + fontRenderer.lineHeight() + 1, caretColor);
    }

    private int textXFor(String display, int x, int index) {
        String sub = display.substring(0, Math.clamp(index, 0, display.length()));
        return x + fontRenderer.width(sub);
    }

    private String currentDisplay() {
        if (!model.isPassword()) return model.getText();
        int n = model.length();
        if (n <= 0) return "";
        char[] arr = new char[n];
        for (int i = 0; i < n; i++) arr[i] = model.getPasswordChar();
        return new String(arr);
    }

    private void moveCaretToMouse(double mouseX) {
        int x = getCalculatedX() + getPaddingLeft();
        String display = currentDisplay();
        int rel = (int) Math.max(0, mouseX - x);
        int best = 0;
        for (int i = 0; i <= display.length(); i++) {
            int width = fontRenderer.width(display.substring(0, i));
            if (width <= rel) best = i;
            else break;
        }
        model.setCaret(best);
    }

    @Override
    public String toString() {
        return String.format("TextFieldItem{text='%s', length=%d, password=%b, caret=%d, selection=[%d,%d], visible=%b, enabled=%b, bounds=[%d,%d,%d,%d]}",
                model.isPassword() ? "***" : model.getText(),
                model.length(),
                model.isPassword(),
                model.getCaret(),
                model.getSelectionStart(),
                model.getSelectionEnd(),
                isVisible(),
                isEnabled(),
                getCalculatedX(),
                getCalculatedY(),
                getCalculatedWidth(),
                getCalculatedHeight()
        );
    }
}