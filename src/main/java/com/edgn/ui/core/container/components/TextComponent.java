package com.edgn.ui.core.container.components;

import com.edgn.ui.core.renderer.FontRenderer;
import com.edgn.ui.core.renderer.font.MinecraftFontRenderer;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class TextComponent implements Component {

    public enum TextAlign { LEFT, CENTER, RIGHT }
    public enum VerticalAlign { TOP, MIDDLE, BOTTOM }
    public enum AnimationType { NONE, WAVE, TYPEWRITER, GLOW, PULSE, SHAKE }
    public enum EffectType { NONE, SOLID, GRADIENT, RAINBOW }
    public enum EffectMode { PULSE, HORIZONTAL_LTR, HORIZONTAL_RTL }
    public enum TextOverflowMode { NONE, TRUNCATE, WRAP, SCALE }

    public static FontRenderer getDefaultFontRenderer() {
        return new MinecraftFontRenderer();
    }

    private final String text;
    private FontRenderer font;

    private TextOverflowMode overflowMode = TextOverflowMode.NONE;
    private String ellipsis = "...";
    private int maxWidth = -1;
    private int maxLines = 1;
    private float minScale = 0.5f;
    private int safetyMargin = 0;

    private EffectType effectType = EffectType.SOLID;
    private EffectMode effectMode = EffectMode.PULSE;
    private int startColor = 0xFFFFFFFF;
    private int endColor = 0xFF000000;
    private float effectSpeed = 1.0f;

    private TextAlign textAlign = TextAlign.LEFT;
    private VerticalAlign verticalAlign = VerticalAlign.MIDDLE;
    private boolean hasShadow = false;
    private int shadowColor = 0xFF000000;
    private int shadowOffsetX = 1;
    private int shadowOffsetY = 1;

    private final Set<AnimationType> activeAnimations = EnumSet.noneOf(AnimationType.class);
    private float animationSpeed = 1.0f;
    private long animationStartTime = System.currentTimeMillis();
    private boolean animationEnabled = true;

    private float waveAmplitude = 5.0f;
    private float waveFrequency = 2.0f;
    private int typewriterCharCount = 0;
    private long lastTypewriterUpdate = 0;
    private int typewriterDelay = 100;
    private boolean hasGlow = false;
    private int glowColor = 0x80FFFFFF;
    private float glowRadius = 3.0f;
    private float glowIntensity = 1.0f;
    private float pulseMin = 0.8f;
    private float pulseMax = 1.2f;
    private float shakeIntensity = 2.0f;
    private boolean isBold = false;
    private boolean isItalic = false;
    private boolean isUnderlined = false;
    private boolean isStrikethrough = false;
    private final List<TextEffect> customEffects = new ArrayList<>();

    public TextComponent(String text, FontRenderer font) {
        this.text = text;
        this.font = font;
    }

    public TextComponent(String text) {
        this(text, getDefaultFontRenderer());
    }

    public void render(DrawContext context, int x, int y, int maxWidth, int maxHeight) {
        if (text == null || text.isEmpty() || !animationEnabled) return;
        this.maxWidth = maxWidth - safetyMargin;
        String displayText = getProcessedText();
        if (displayText == null || displayText.isEmpty()) return;
        switch (overflowMode) {
            case WRAP -> renderWrapped(context, x, y, this.maxWidth, maxLines);
            case SCALE -> renderScaled(context, x, y, this.maxWidth, maxHeight, displayText);
            default -> renderSingle(context, x, y, this.maxWidth, maxHeight, displayText);
        }
    }

    private void renderSingle(DrawContext context, int x, int y, int maxWidth, int maxHeight, String displayText) {
        int renderX = calculateX(x, maxWidth, displayText);
        int renderY = calculateY(y, maxHeight);
        renderInternal(context, displayText, renderX, renderY, 0);
    }

    private void renderScaled(DrawContext context, int x, int y, int maxWidth, int maxHeight, String displayText) {
        int textWidth = font.width(displayText);
        if (textWidth <= maxWidth) {
            renderSingle(context, x, y, maxWidth, maxHeight, displayText);
            return;
        }
        float scale = Math.max(minScale, (float) maxWidth / textWidth);
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
        int scaledX = (int) (x / scale);
        int scaledY = (int) (y / scale);
        int scaledMaxWidth = (int) (maxWidth / scale);
        int scaledMaxHeight = (int) (maxHeight / scale);
        renderSingle(context, scaledX, scaledY, scaledMaxWidth, scaledMaxHeight, displayText);
        context.getMatrices().pop();
    }

    public void renderWrapped(DrawContext context, int x, int y, int maxWidth, int maxLines) {
        if (text == null || text.isEmpty() || context == null) return;
        List<String> lines = font.wrap(this.text, maxWidth);
        int yOffset = 0;
        int charOffset = 0;
        int lineHeight = font.lineHeight() + 2;
        for (int i = 0; i < Math.min(lines.size(), maxLines); i++) {
            String line = lines.get(i);
            if (line == null) continue;
            if (i == maxLines - 1 && lines.size() > maxLines) line = truncateText(line, maxWidth);
            int renderX = calculateX(x, maxWidth, line);
            renderInternal(context, line, renderX, y + yOffset, charOffset);
            yOffset += lineHeight;
            charOffset += line.length();
        }
    }

    private void renderInternal(DrawContext context, String textToRender, int x, int y, int charOffset) {
        if (textToRender.isEmpty()) return;
        int renderX = x;
        int renderY = y;
        if (activeAnimations.contains(AnimationType.SHAKE)) {
            float t = (System.currentTimeMillis() - animationStartTime) / 1000.0f * animationSpeed;
            renderX += (int) (Math.sin(t * 20) * shakeIntensity);
            renderY += (int) (Math.cos(t * 25) * shakeIntensity);
        }
        for (TextEffect effect : customEffects) effect.apply(this, context, renderX, renderY);
        if (hasGlow && !activeAnimations.contains(AnimationType.TYPEWRITER)) renderGlow(context, textToRender, renderX, renderY);
        if (hasShadow) renderTextWithFormatting(context, textToRender, renderX + shadowOffsetX, renderY + shadowOffsetY, shadowColor, charOffset);
        boolean perChar = activeAnimations.contains(AnimationType.WAVE) || effectMode != EffectMode.PULSE;
        if (perChar) renderPerChar(context, textToRender, renderX, renderY, charOffset);
        else {
            int textColor = getCurrentColor(charOffset);
            if (activeAnimations.contains(AnimationType.PULSE)) textColor = applyPulseEffect(textColor);
            renderTextWithFormatting(context, textToRender, renderX, renderY, textColor, charOffset);
        }
        if (isUnderlined || isStrikethrough) renderTextDecorations(context, textToRender, renderX, renderY, charOffset);
    }

    private void renderPerChar(DrawContext context, String displayText, int x, int y, int charOffset) {
        int charX = x;
        int i = 0;
        int visIndex = 0;
        while (i < displayText.length()) {
            int cp = displayText.codePointAt(i);
            int len = Character.charCount(cp);
            String charStr = new String(Character.toChars(cp));
            int color = getCurrentColor(visIndex + charOffset);
            int cy = y;
            if (activeAnimations.contains(AnimationType.WAVE)) {
                float t = (System.currentTimeMillis() - animationStartTime) / 1000.0f;
                cy += (int) (Math.sin(t * animationSpeed * waveFrequency + (visIndex + charOffset) * 0.5f) * waveAmplitude);
            }
            if (activeAnimations.contains(AnimationType.PULSE)) color = applyPulseEffect(color);
            font.draw(context, charStr, charX, cy, color, false);
            charX += font.advance(cp);
            i += len;
            visIndex++;
        }
    }

    private int getCurrentColor(int charIndex) {
        float time = (System.currentTimeMillis() - animationStartTime) / 1000.0f;
        float positionFactor = charIndex / 15.0f;
        return switch (effectType) {
            case SOLID -> this.startColor;
            case GRADIENT -> getGradientColorAt(time, positionFactor);
            case RAINBOW -> getRainbowColorAt(time, positionFactor);
            default -> 0xFFFFFFFF;
        };
    }

    private void renderTextWithFormatting(DrawContext context, String text, int x, int y, int color, int charOffset) {
        if (isBold && isItalic) {
            renderBoldItalicText(context, text, x, y, color, charOffset);
        } else if (isBold) {
            renderBoldText(context, text, x, y, color);
        } else if (isItalic) {
            renderItalicText(context, text, x, y, color, charOffset);
        } else {
            font.draw(context, text, x, y, color, false);
        }
    }

    private void renderItalicText(DrawContext context, String text, int x, int y, int color, int charOffset) {
        int charX = x;
        int i = 0;
        int vis = 0;
        while (i < text.length()) {
            int cp = text.codePointAt(i);
            int len = Character.charCount(cp);
            String s = new String(Character.toChars(cp));
            int italicOffset = (int) (Math.sin((vis + charOffset) * 0.3f) * 1.5f);
            font.draw(context, s, charX + italicOffset, y, color, false);
            charX += font.advance(cp);
            i += len;
            vis++;
        }
    }

    private void renderBoldItalicText(DrawContext context, String text, int x, int y, int color, int charOffset) {
        int charX = x;
        int i = 0;
        int vis = 0;
        while (i < text.length()) {
            int cp = text.codePointAt(i);
            int len = Character.charCount(cp);
            String s = new String(Character.toChars(cp));
            int italicOffset = (int) (Math.sin((vis + charOffset) * 0.3f) * 1.5f);
            renderBoldText(context, s, charX + italicOffset, y, color);
            charX += font.advance(cp);
            i += len;
            vis++;
        }
    }

    private void renderTextDecorations(DrawContext context, String displayText, int x, int y, int charOffset) {
        int textWidth = font.width(displayText);
        if (isUnderlined) {
            int underlineY = y + font.lineHeight();
            context.fill(x, underlineY, x + textWidth, underlineY + 1, getCurrentColor(charOffset));
        }
        if (isStrikethrough) {
            int strikeY = y + font.lineHeight() / 2;
            context.fill(x, strikeY, x + textWidth, strikeY + 1, getCurrentColor(charOffset));
        }
    }

    private float robustModulo(float value) {
        return (value % 1.0f + 1.0f) % 1.0f;
    }

    private int getGradientColorAt(float time, float position) {
        float factor = switch (effectMode) {
            case PULSE -> (float) (Math.sin(time * effectSpeed) * 0.5 + 0.5);
            case HORIZONTAL_LTR -> robustModulo(time * effectSpeed - position);
            case HORIZONTAL_RTL -> robustModulo(time * effectSpeed + position);
        };
        return interpolateColor(startColor, endColor, factor);
    }

    private int getRainbowColorAt(float time, float position) {
        float hue = switch (effectMode) {
            case PULSE -> robustModulo(time * effectSpeed);
            case HORIZONTAL_LTR -> robustModulo(time * effectSpeed - position);
            case HORIZONTAL_RTL -> robustModulo(time * effectSpeed + position);
        };
        return Color.HSBtoRGB(hue, 1.0f, 1.0f);
    }

    private void renderBoldText(DrawContext context, String text, int x, int y, int color) {
        font.draw(context, text, x, y, color, false);
        font.draw(context, text, x + 1, y, color, false);
        font.draw(context, text, x, y + 1, color, false);
        font.draw(context, text, x + 1, y + 1, color, false);
    }

    private int interpolateColor(int color1, int color2, float factor) {
        float f = Math.clamp(factor, 0.0f, 1.0f);
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int a1 = (color1 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        int a2 = (color2 >> 24) & 0xFF;
        int r = (int) (r1 + (r2 - r1) * f);
        int g = (int) (g1 + (r2 - g1) * f);
        int b = (int) (b1 + (r2 - b1) * f);
        int a = (int) (a1 + (r2 - a1) * f);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private String getProcessedText() {
        String displayText = getDisplayText();
        if (overflowMode == TextOverflowMode.TRUNCATE && maxWidth > 0) {
            displayText = truncateText(displayText, maxWidth);
        }
        return displayText;
    }

    private String getDisplayText() {
        if (activeAnimations.contains(AnimationType.TYPEWRITER)) {
            updateTypewriterAnimation();
            return text.substring(0, Math.min(typewriterCharCount, text.length()));
        }
        return text;
    }

    private void updateTypewriterAnimation() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTypewriterUpdate > typewriterDelay && typewriterCharCount < text.length()) {
            typewriterCharCount++;
            lastTypewriterUpdate = currentTime;
        }
    }

    private String truncateText(String text, int availableWidth) {
        if (text == null || text.isEmpty() || availableWidth <= 0) return text;
        int fullTextWidth = font.width(text);
        if (fullTextWidth <= availableWidth) return text;
        int ellipsisWidth = font.width(ellipsis);
        if (ellipsisWidth >= availableWidth) return "";
        int maxTextWidth = availableWidth - ellipsisWidth;
        int left = 0;
        int right = text.length();
        int bestLength = 0;
        while (left <= right) {
            int mid = (left + right) / 2;
            String substring = text.substring(0, mid);
            int substringWidth = font.width(substring);
            if (substringWidth <= maxTextWidth) {
                bestLength = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        String result = text.substring(0, bestLength) + ellipsis;
        while (font.width(result) > availableWidth && bestLength > 0) {
            bestLength--;
            result = text.substring(0, bestLength) + ellipsis;
        }
        return result;
    }

    private int calculateX(int baseX, int maxWidth, String displayText) {
        int textWidth = font.width(displayText);
        return switch (textAlign) {
            case LEFT -> baseX;
            case CENTER -> baseX + (maxWidth - textWidth) / 2;
            case RIGHT -> baseX + maxWidth - textWidth;
        };
    }

    private int calculateY(int baseY, int maxHeight) {
        int textHeight = font.lineHeight();
        return switch (verticalAlign) {
            case TOP -> baseY;
            case MIDDLE -> baseY + (maxHeight - textHeight) / 2;
            case BOTTOM -> baseY + maxHeight - textHeight;
        };
    }

    private void renderGlow(DrawContext context, String displayText, int x, int y) {
        float time = (System.currentTimeMillis() - animationStartTime) / 1000.0f;
        float glowAlpha = (float) (Math.sin(time * 3) * 0.3 + 0.7) * glowIntensity;
        for (int offsetX = -(int) glowRadius; offsetX <= glowRadius; offsetX++) {
            for (int offsetY = -(int) glowRadius; offsetY <= glowRadius; offsetY++) {
                if (offsetX == 0 && offsetY == 0) continue;
                float distance = (float) Math.sqrt((double) offsetX * offsetX + offsetY * offsetY);
                if (distance <= glowRadius) {
                    float alpha = (1.0f - distance / glowRadius) * glowAlpha * 0.3f;
                    int currentGlowColor = (glowColor & 0x00FFFFFF) | ((int) (255 * alpha) << 24);
                    renderTextWithFormatting(context, displayText, x + offsetX, y + offsetY, currentGlowColor, 0);
                }
            }
        }
    }

    private int applyPulseEffect(int color) {
        float time = (System.currentTimeMillis() - animationStartTime) / 1000.0f * animationSpeed;
        float scale = (float) (Math.sin(time * 3) * 0.5 + 0.5);
        float alpha = pulseMin + (pulseMax - pulseMin) * scale;
        int a1 = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int newAlpha = (int) (a1 * alpha);
        return (Math.min(255, newAlpha) << 24) | (r << 16) | (g << 8) | b;
    }

    public String getText() { return text; }
    public int getBaseColor() { return startColor; }
    public TextAlign getTextAlign() { return textAlign; }
    public VerticalAlign getVerticalAlign() { return verticalAlign; }
    public Set<AnimationType> getActiveAnimations() { return EnumSet.copyOf(activeAnimations); }
    public boolean isAnimationEnabled() { return animationEnabled; }
    public int getTextWidth() { return font.width(text); }
    public int getTextHeight() { return font.lineHeight(); }
    public TextOverflowMode getOverflowMode() { return overflowMode; }
    public int getMaxWidth() { return maxWidth; }
    public int getMaxLines() { return maxLines; }
    public String getEllipsis() { return ellipsis; }
    public int getSafetyMargin() { return safetyMargin; }
    public float getMinScale() { return minScale; }
    public void startAnimation() { this.animationEnabled = true; this.animationStartTime = System.currentTimeMillis(); if (activeAnimations.contains(AnimationType.TYPEWRITER)) { this.typewriterCharCount = 0; this.lastTypewriterUpdate = System.currentTimeMillis(); } }
    public void stopAnimation() { this.animationEnabled = false; }
    public void resetAnimation() { startAnimation(); }
    public int getColor() {return effectType == EffectType.SOLID ? startColor : getCurrentColor(0);}
    public TextComponent setOverflowMode(TextOverflowMode mode) { this.overflowMode = mode; return this; }
    public TextComponent setMaxWidth(int maxWidth) { this.maxWidth = maxWidth; return this; }
    public TextComponent setMaxLines(int maxLines) { this.maxLines = Math.max(1, maxLines); return this; }
    public TextComponent setEllipsis(String ellipsis) { this.ellipsis = ellipsis != null ? ellipsis : "..."; return this; }
    public TextComponent setSafetyMargin(int margin) { this.safetyMargin = Math.max(0, margin); return this; }
    public TextComponent setMinScale(float minScale) { this.minScale = Math.clamp(minScale, 0.1f, 1.0f); return this; }
    public TextComponent truncate() { return setOverflowMode(TextOverflowMode.TRUNCATE); }
    public TextComponent truncate(int maxWidth) { return setOverflowMode(TextOverflowMode.TRUNCATE).setMaxWidth(maxWidth); }
    public TextComponent wrap(int maxLines) { return setOverflowMode(TextOverflowMode.WRAP).setMaxLines(maxLines); }
    public TextComponent autoScale() { return setOverflowMode(TextOverflowMode.SCALE); }
    public TextComponent autoScale(float minScale) { return setOverflowMode(TextOverflowMode.SCALE).setMinScale(minScale); }
    public TextComponent color(int color) { this.effectType = EffectType.SOLID; this.startColor = color; return this; }
    public TextComponent gradient(int startColor, int endColor, EffectMode mode, float speed) { this.effectType = EffectType.GRADIENT; this.startColor = startColor; this.endColor = endColor; this.effectMode = mode; this.effectSpeed = speed; return this; }
    public TextComponent gradient(int startColor, int endColor, EffectMode mode) { return gradient(startColor, endColor, mode, 1.0f); }
    public TextComponent rainbow(EffectMode mode, float speed) { this.effectType = EffectType.RAINBOW; this.effectMode = mode; this.effectSpeed = speed; return this; }
    public TextComponent rainbow(EffectMode mode) { return rainbow(mode, 1.0f); }
    public TextComponent rainbow() { return rainbow(EffectMode.HORIZONTAL_LTR, 1.0f); }
    public TextComponent align(TextAlign align) { this.textAlign = align; return this; }
    public TextComponent verticalAlign(VerticalAlign align) { this.verticalAlign = align; return this; }
    public TextComponent shadow(int color, int offsetX, int offsetY) { this.hasShadow = true; this.shadowColor = color; this.shadowOffsetX = offsetX; this.shadowOffsetY = offsetY; return this; }
    public TextComponent shadow() { return shadow(0xFF000000, 1, 1); }
    public TextComponent wave(float amplitude, float frequency, float speed) { this.activeAnimations.add(AnimationType.WAVE); this.waveAmplitude = amplitude; this.waveFrequency = frequency; this.animationSpeed = speed; return this; }
    public TextComponent wave() { return wave(5.0f, 2.0f, 1.0f); }
    public TextComponent typewriter(int delayMs) { this.activeAnimations.add(AnimationType.TYPEWRITER); this.typewriterDelay = delayMs; return this; }
    public TextComponent typewriter() { return typewriter(100); }
    public TextComponent glow(int color, float radius, float intensity) { this.hasGlow = true; this.glowColor = color; this.glowRadius = radius; this.glowIntensity = intensity; return this; }
    public TextComponent glow(int color) { return glow(color, 3.0f, 1.0f); }
    public TextComponent glow() { return glow(0x80FFFFFF, 3.0f, 1.0f); }
    public TextComponent pulse(float min, float max, float speed) { this.activeAnimations.add(AnimationType.PULSE); this.pulseMin = min; this.pulseMax = max; this.animationSpeed = speed; return this; }
    public TextComponent pulse() { return pulse(0.8f, 1.2f, 1.0f); }
    public TextComponent shake(float intensity, float speed) { this.activeAnimations.add(AnimationType.SHAKE); this.shakeIntensity = intensity; this.animationSpeed = speed; return this; }
    public TextComponent shake() { return shake(2.0f, 1.0f); }
    public TextComponent bold() { this.isBold = true; return this; }
    public TextComponent italic() { this.isItalic = true; return this; }
    public TextComponent underlined() { this.isUnderlined = true; return this; }
    public TextComponent strikethrough() { this.isStrikethrough = true; return this; }
    public TextComponent addEffect(TextEffect effect) { this.customEffects.add(effect); return this; }
    public TextComponent asTitle() { return color(0xFF0D6EFD); }
    public TextComponent asSubtitle() { return color(0xFF888888).italic(); }
    public TextComponent asError() { return color(0xFFDC3545).shake(); }
    public TextComponent asSuccess() { return color(0xFF198754).glow(0xFF198754); }
    public TextComponent asWarning() { return color(0xFFFFC107).pulse(); }
    public TextComponent asHighlight() { return gradient(0xFF0D6EFD, 0xFF6EA8FE, EffectMode.HORIZONTAL_LTR); }
    public TextComponent asFancy() { return rainbow(EffectMode.HORIZONTAL_LTR).glow().shadow().wave().pulse().bold(); }

    public interface TextEffect {
        void apply(TextComponent textModel, DrawContext context, int x, int y);
    }

    public boolean hasCustomStyling() {
        return effectType != EffectType.SOLID
                || hasShadow
                || isBold || isItalic || isUnderlined || isStrikethrough
                || !customEffects.isEmpty()
                || !activeAnimations.isEmpty();
    }

    public TextComponent cloneWithNewText(String newText) {
        TextComponent c = new TextComponent(newText, this.font);
        c.overflowMode = this.overflowMode;
        c.ellipsis = this.ellipsis;
        c.maxWidth = this.maxWidth;
        c.maxLines = this.maxLines;
        c.minScale = this.minScale;
        c.safetyMargin = this.safetyMargin;
        c.effectType = this.effectType;
        c.effectMode = this.effectMode;
        c.startColor = this.startColor;
        c.endColor = this.endColor;
        c.effectSpeed = this.effectSpeed;
        c.textAlign = this.textAlign;
        c.verticalAlign = this.verticalAlign;
        c.hasShadow = this.hasShadow;
        c.shadowColor = this.shadowColor;
        c.shadowOffsetX = this.shadowOffsetX;
        c.shadowOffsetY = this.shadowOffsetY;
        c.animationSpeed = this.animationSpeed;
        c.waveAmplitude = this.waveAmplitude;
        c.waveFrequency = this.waveFrequency;
        c.typewriterDelay = this.typewriterDelay;
        c.hasGlow = this.hasGlow;
        c.glowColor = this.glowColor;
        c.glowRadius = this.glowRadius;
        c.glowIntensity = this.glowIntensity;
        c.pulseMin = this.pulseMin;
        c.pulseMax = this.pulseMax;
        c.shakeIntensity = this.shakeIntensity;
        c.isBold = this.isBold;
        c.isItalic = this.isItalic;
        c.isUnderlined = this.isUnderlined;
        c.isStrikethrough = this.isStrikethrough;
        c.activeAnimations.addAll(this.activeAnimations);
        c.customEffects.addAll(this.customEffects);
        return c;
    }

    public TextComponent setFontRenderer(FontRenderer font) {
        this.font = font;
        return this;
    }

    @Override
    public String toString() {
        return String.format("TextComponent{text='%s', length=%d, color=0x%08X, align=%s, vAlign=%s, overflow=%s, effects=%s, animations=%s, styled=%b}",
                text != null ? (text.length() > 20 ? text.substring(0, 17) + "..." : text) : "null",
                text != null ? text.length() : 0,
                getColor(),
                textAlign,
                verticalAlign,
                overflowMode,
                effectType + (effectType != EffectType.SOLID ? "(" + effectMode + ")" : ""),
                activeAnimations.toString(),
                hasCustomStyling()
        );
    }
}
