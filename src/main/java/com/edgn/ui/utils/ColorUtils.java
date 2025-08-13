package com.edgn.ui.utils;

import java.awt.*;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"unused"})
public class ColorUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("^#?([A-Fa-f0-9]{3,8})$");
    private static final Pattern RGB_PATTERN = Pattern.compile("^rgb\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)$");
    private static final Pattern RGBA_PATTERN = Pattern.compile("^rgba\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*([0-9]*\\.?[0-9]+)\\s*\\)$");
    private static final Pattern HSL_PATTERN = Pattern.compile("^hsl\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)%\\s*,\\s*(\\d+)%\\s*\\)$");
    private static final Pattern HSLA_PATTERN = Pattern.compile("^hsla\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)%\\s*,\\s*(\\d+)%\\s*,\\s*([0-9]*\\.?[0-9]+)\\s*\\)$");

    public enum NamedColor {
        ALICEBLUE(0xFFF0F8FF),
        ANTIQUEWHITE(0xFFFAEBD7),
        AQUA(0xFF00FFFF),
        AQUAMARINE(0xFF7FFFD4),
        AZURE(0xFFF0FFFF),
        BEIGE(0xFFF5F5DC),
        BISQUE(0xFFFFE4C4),
        BLACK(0xFF000000),
        BLANCHEDALMOND(0xFFFFEBCD),
        BLUE(0xFF0000FF),
        BLUEVIOLET(0xFF8A2BE2),
        BROWN(0xFFA52A2A),
        BURLYWOOD(0xFFDEB887),
        CADETBLUE(0xFF5F9EA0),
        CHARTREUSE(0xFF7FFF00),
        CHOCOLATE(0xFFD2691E),
        CORAL(0xFFFF7F50),
        CORNFLOWERBLUE(0xFF6495ED),
        CORNSILK(0xFFFFF8DC),
        CRIMSON(0xFFDC143C),
        CYAN(0xFF00FFFF),
        DARKBLUE(0xFF00008B),
        DARKCYAN(0xFF008B8B),
        DARKGOLDENROD(0xFFB8860B),
        DARKGRAY(0xFFA9A9A9),
        DARKGREEN(0xFF006400),
        DARKKHAKI(0xFFBDB76B),
        DARKMAGENTA(0xFF8B008B),
        DARKOLIVEGREEN(0xFF556B2F),
        DARKORANGE(0xFFFF8C00),
        DARKORCHID(0xFF9932CC),
        DARKRED(0xFF8B0000),
        DARKSALMON(0xFFE9967A),
        DARKSEAGREEN(0xFF8FBC8F),
        DARKSLATEBLUE(0xFF483D8B),
        DARKSLATEGRAY(0xFF2F4F4F),
        DARKTURQUOISE(0xFF00CED1),
        DARKVIOLET(0xFF9400D3),
        DEEPPINK(0xFFFF1493),
        DEEPSKYBLUE(0xFF00BFFF),
        DIMGRAY(0xFF696969),
        DODGERBLUE(0xFF1E90FF),
        FIREBRICK(0xFFB22222),
        FLORALWHITE(0xFFFFFAF0),
        FORESTGREEN(0xFF228B22),
        FUCHSIA(0xFFFF00FF),
        GAINSBORO(0xFFDCDCDC),
        GHOSTWHITE(0xFFF8F8FF),
        GOLD(0xFFFFD700),
        GOLDENROD(0xFFDAA520),
        GRAY(0xFF808080),
        GREEN(0xFF008000),
        GREENYELLOW(0xFFADFF2F),
        HONEYDEW(0xFFF0FFF0),
        HOTPINK(0xFFFF69B4),
        INDIANRED(0xFFCD5C5C),
        INDIGO(0xFF4B0082),
        IVORY(0xFFFFFFF0),
        KHAKI(0xFFF0E68C),
        LAVENDER(0xFFE6E6FA),
        LAVENDERBLUSH(0xFFFFF0F5),
        LAWNGREEN(0xFF7CFC00),
        LEMONCHIFFON(0xFFFFFACD),
        LIGHTBLUE(0xFFADD8E6),
        LIGHTCORAL(0xFFF08080),
        LIGHTCYAN(0xFFE0FFFF),
        LIGHTGOLDENRODYELLOW(0xFFFAFAD2),
        LIGHTGRAY(0xFFD3D3D3),
        LIGHTGREEN(0xFF90EE90),
        LIGHTPINK(0xFFFFB6C1),
        LIGHTSALMON(0xFFFFA07A),
        LIGHTSEAGREEN(0xFF20B2AA),
        LIGHTSKYBLUE(0xFF87CEFA),
        LIGHTSLATEGRAY(0xFF778899),
        LIGHTSTEELBLUE(0xFFB0C4DE),
        LIGHTYELLOW(0xFFFFFFE0),
        LIME(0xFF00FF00),
        LIMEGREEN(0xFF32CD32),
        LINEN(0xFFFAF0E6),
        MAGENTA(0xFFFF00FF),
        MAROON(0xFF800000),
        MEDIUMAQUAMARINE(0xFF66CDAA),
        MEDIUMBLUE(0xFF0000CD),
        MEDIUMORCHID(0xFFBA55D3),
        MEDIUMPURPLE(0xFF9370DB),
        MEDIUMSEAGREEN(0xFF3CB371),
        MEDIUMSLATEBLUE(0xFF7B68EE),
        MEDIUMSPRINGGREEN(0xFF00FA9A),
        MEDIUMTURQUOISE(0xFF48D1CC),
        MEDIUMVIOLETRED(0xFFC71585),
        MIDNIGHTBLUE(0xFF191970),
        MINTCREAM(0xFFF5FFFA),
        MISTYROSE(0xFFFFE4E1),
        MOCCASIN(0xFFFFE4B5),
        NAVAJOWHITE(0xFFFFDEAD),
        NAVY(0xFF000080),
        OLDLACE(0xFFFDF5E6),
        OLIVE(0xFF808000),
        OLIVEDRAB(0xFF6B8E23),
        ORANGE(0xFFFFA500),
        ORANGERED(0xFFFF4500),
        ORCHID(0xFFDA70D6),
        PALEGOLDENROD(0xFFEEE8AA),
        PALEGREEN(0xFF98FB98),
        PALETURQUOISE(0xFFAFEEEE),
        PALEVIOLETRED(0xFFDB7093),
        PAPAYAWHIP(0xFFFFEFD5),
        PEACHPUFF(0xFFFFDAB9),
        PERU(0xFFCD853F),
        PINK(0xFFFFC0CB),
        PLUM(0xFFDDA0DD),
        POWDERBLUE(0xFFB0E0E6),
        PURPLE(0xFF800080),
        RED(0xFFFF0000),
        ROSYBROWN(0xFFBC8F8F),
        ROYALBLUE(0xFF4169E1),
        SADDLEBROWN(0xFF8B4513),
        SALMON(0xFFFA8072),
        SANDYBROWN(0xFFF4A460),
        SEAGREEN(0xFF2E8B57),
        SEASHELL(0xFFFFF5EE),
        SIENNA(0xFFA0522D),
        SILVER(0xFFC0C0C0),
        SKYBLUE(0xFF87CEEB),
        SLATEBLUE(0xFF6A5ACD),
        SLATEGRAY(0xFF708090),
        SNOW(0xFFFFFAFA),
        SPRINGGREEN(0xFF00FF7F),
        STEELBLUE(0xFF4682B4),
        TAN(0xFFD2B48C),
        TEAL(0xFF008080),
        THISTLE(0xFFD8BFD8),
        TOMATO(0xFFFF6347),
        TURQUOISE(0xFF40E0D0),
        VIOLET(0xFFEE82EE),
        WHEAT(0xFFF5DEB3),
        WHITE(0xFFFFFFFF),
        WHITESMOKE(0xFFF5F5F5),
        YELLOW(0xFFFFFF00),
        YELLOWGREEN(0xFF9ACD32),
        TRANSPARENT(0x00000000);

        private final int argb;

        NamedColor(int argb) { this.argb = argb; }
        public int toInt() { return argb; }
        public RGBA toRGBA() { return ColorUtils.fromInt(argb); }

        public static NamedColor fromName(String name) {
            if (name == null) return null;
            String n = name.trim().toUpperCase(Locale.ROOT).replace(" ", "");
            try { return NamedColor.valueOf(n); } catch (IllegalArgumentException ignored) { return null; }
        }

        public static String[] names() {
            NamedColor[] vals = values();
            String[] out = new String[vals.length];
            for (int i = 0; i < vals.length; i++) out[i] = vals[i].name().toLowerCase(Locale.ROOT);
            return out;
        }
    }

    public static class RGBA {
        public final int r, g, b, a;
        public RGBA(int r, int g, int b) { this(r, g, b, 255); }
        public RGBA(int r, int g, int b, int a) {
            this.r = clamp(r, 0, 255);
            this.g = clamp(g, 0, 255);
            this.b = clamp(b, 0, 255);
            this.a = clamp(a, 0, 255);
        }
        public int toInt() { return (a << 24) | (r << 16) | (g << 8) | b; }
        public String toHex() { return String.format("#%02X%02X%02X%02X", r, g, b, a); }
        public String toHexNoAlpha() { return String.format("#%02X%02X%02X", r, g, b); }
        public String toRgb() { return String.format("rgb(%d, %d, %d)", r, g, b); }
        public String toRgba() { return String.format("rgba(%d, %d, %d, %.2f)", r, g, b, a / 255.0f); }
        public String toString() { return toRgba(); }
        public boolean equals(Object o) { if (this == o) return true; if (!(o instanceof RGBA x)) return false; return r==x.r && g==x.g && b==x.b && a==x.a; }
        public int hashCode() { return toInt(); }
    }

    public static class HSV {
        public final float h, s, v, a;
        public HSV(float h, float s, float v) { this(h, s, v, 1.0f); }
        public HSV(float h, float s, float v, float a) {
            this.h = wrap360(h);
            this.s = clamp(s, 0.0f, 1.0f);
            this.v = clamp(v, 0.0f, 1.0f);
            this.a = clamp(a, 0.0f, 1.0f);
        }
        public RGBA toRGBA() { return ColorUtils.hsvToRgba(this); }
        public String toString() { return String.format("hsv(%.1f, %.1f%%, %.1f%%)", h, s * 100, v * 100); }
    }

    public static class HSL {
        public final float h, s, l, a;
        public HSL(float h, float s, float l) { this(h, s, l, 1.0f); }
        public HSL(float h, float s, float l, float a) {
            this.h = wrap360(h);
            this.s = clamp(s, 0.0f, 1.0f);
            this.l = clamp(l, 0.0f, 1.0f);
            this.a = clamp(a, 0.0f, 1.0f);
        }
        public RGBA toRGBA() { return ColorUtils.hslToRgba(this); }
        public String toString() { return String.format("hsl(%.1f, %.1f%%, %.1f%%)", h, s * 100, l * 100); }
    }

    public enum PaletteType { RANDOM, PASTEL, VIBRANT, MONOCHROMATIC, COMPLEMENTARY }
    public enum InterpolationMode { LINEAR, EASE_IN, EASE_OUT, EASE_IN_OUT, HSV, HSL }

    public static RGBA parse(String colorString) {
        if (colorString == null || colorString.trim().isEmpty()) throw new IllegalArgumentException("Color string cannot be null or empty");
        String color = colorString.trim();
        NamedColor named = NamedColor.fromName(color);
        if (named != null) return named.toRGBA();

        Matcher hexMatcher = HEX_PATTERN.matcher(color);
        if (hexMatcher.matches()) return parseHex(hexMatcher.group(1));

        Matcher rgbMatcher = RGB_PATTERN.matcher(color.toLowerCase(Locale.ROOT));
        if (rgbMatcher.matches()) {
            return new RGBA(
                    Integer.parseInt(rgbMatcher.group(1)),
                    Integer.parseInt(rgbMatcher.group(2)),
                    Integer.parseInt(rgbMatcher.group(3))
            );
        }

        Matcher rgbaMatcher = RGBA_PATTERN.matcher(color.toLowerCase(Locale.ROOT));
        if (rgbaMatcher.matches()) {
            return new RGBA(
                    Integer.parseInt(rgbaMatcher.group(1)),
                    Integer.parseInt(rgbaMatcher.group(2)),
                    Integer.parseInt(rgbaMatcher.group(3)),
                    Math.round(Float.parseFloat(rgbaMatcher.group(4)) * 255f)
            );
        }

        Matcher hslMatcher = HSL_PATTERN.matcher(color.toLowerCase(Locale.ROOT));
        if (hslMatcher.matches()) {
            return hslToRgba(new HSL(
                    Float.parseFloat(hslMatcher.group(1)),
                    Float.parseFloat(hslMatcher.group(2)) / 100.0f,
                    Float.parseFloat(hslMatcher.group(3)) / 100.0f
            ));
        }

        Matcher hslaMatcher = HSLA_PATTERN.matcher(color.toLowerCase(Locale.ROOT));
        if (hslaMatcher.matches()) {
            return hslToRgba(new HSL(
                    Float.parseFloat(hslaMatcher.group(1)),
                    Float.parseFloat(hslaMatcher.group(2)) / 100.0f,
                    Float.parseFloat(hslaMatcher.group(3)) / 100.0f,
                    Float.parseFloat(hslaMatcher.group(4))
            ));
        }

        throw new IllegalArgumentException("Invalid color format: " + colorString);
    }

    private static RGBA parseHex(String hex) {
        String h = hex;
        int len = h.length();
        if (len == 3 || len == 4) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) { char c = h.charAt(i); sb.append(c).append(c); }
            h = sb.toString();
            len = h.length();
        }
        return switch (len) {
            case 6 -> new RGBA(
                    Integer.parseInt(h.substring(0, 2), 16),
                    Integer.parseInt(h.substring(2, 4), 16),
                    Integer.parseInt(h.substring(4, 6), 16)
            );
            case 8 -> new RGBA(
                    Integer.parseInt(h.substring(0, 2), 16),
                    Integer.parseInt(h.substring(2, 4), 16),
                    Integer.parseInt(h.substring(4, 6), 16),
                    Integer.parseInt(h.substring(6, 8), 16)
            );
            default -> throw new IllegalArgumentException("Invalid hex color length: " + hex);
        };
    }

    public static RGBA fromInt(int color) {
        return new RGBA((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >> 24) & 0xFF);
    }

    public static RGBA fromIntNoAlpha(int color) {
        return new RGBA((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, 255);
    }

    public static RGBA fromColor(Color color) {
        return new RGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static Color toColor(RGBA rgba) {
        return new Color(rgba.r, rgba.g, rgba.b, rgba.a);
    }

    public static HSV rgbaToHsv(RGBA rgba) {
        float r = rgba.r / 255f, g = rgba.g / 255f, b = rgba.b / 255f, a = rgba.a / 255f;
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float d = max - min;
        float h = 0f;
        if (d != 0f) {
            if (max == r) h = 60f * (((g - b) / d) % 6f);
            else if (max == g) h = 60f * (((b - r) / d) + 2f);
            else h = 60f * (((r - g) / d) + 4f);
        }
        if (h < 0) h += 360f;
        float s = max == 0f ? 0f : d / max;
        float v = max;
        return new HSV(h, s, v, a);
    }

    public static RGBA hsvToRgba(HSV hsv) {
        float c = hsv.v * hsv.s;
        float x = c * (1 - Math.abs(((hsv.h / 60f) % 2) - 1));
        float m = hsv.v - c;
        float r, g, b;
        if (hsv.h < 60) { r = c; g = x; b = 0; }
        else if (hsv.h < 120) { r = x; g = c; b = 0; }
        else if (hsv.h < 180) { r = 0; g = c; b = x; }
        else if (hsv.h < 240) { r = 0; g = x; b = c; }
        else if (hsv.h < 300) { r = x; g = 0; b = c; }
        else { r = c; g = 0; b = x; }
        return new RGBA(Math.round((r + m) * 255), Math.round((g + m) * 255), Math.round((b + m) * 255), Math.round(hsv.a * 255));
    }

    public static HSL rgbaToHsl(RGBA rgba) {
        float r = rgba.r / 255f, g = rgba.g / 255f, b = rgba.b / 255f, a = rgba.a / 255f;
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float d = max - min;
        float h = 0f;
        if (d != 0f) {
            if (max == r) h = 60f * (((g - b) / d) % 6f);
            else if (max == g) h = 60f * (((b - r) / d) + 2f);
            else h = 60f * (((r - g) / d) + 4f);
        }
        if (h < 0) h += 360f;
        float l = (max + min) / 2f;
        float s = (l == 0f || l == 1f) ? 0f : d / (1f - Math.abs(2f * l - 1f));
        return new HSL(h, s, l, a);
    }

    public static RGBA hslToRgba(HSL hsl) {
        float c = (1 - Math.abs(2 * hsl.l - 1)) * hsl.s;
        float x = c * (1 - Math.abs(((hsl.h / 60f) % 2) - 1));
        float m = hsl.l - c / 2;
        float r, g, b;
        if (hsl.h < 60) { r = c; g = x; b = 0; }
        else if (hsl.h < 120) { r = x; g = c; b = 0; }
        else if (hsl.h < 180) { r = 0; g = c; b = x; }
        else if (hsl.h < 240) { r = 0; g = x; b = c; }
        else if (hsl.h < 300) { r = x; g = 0; b = c; }
        else { r = c; g = 0; b = x; }
        return new RGBA(Math.round((r + m) * 255), Math.round((g + m) * 255), Math.round((b + m) * 255), Math.round(hsl.a * 255));
    }

    public static RGBA lighten(RGBA color, float amount) {
        HSL hsl = rgbaToHsl(color);
        return hslToRgba(new HSL(hsl.h, hsl.s, clamp(hsl.l + amount, 0f, 1f), hsl.a));
    }

    public static RGBA darken(RGBA color, float amount) {
        HSL hsl = rgbaToHsl(color);
        return hslToRgba(new HSL(hsl.h, hsl.s, clamp(hsl.l - amount, 0f, 1f), hsl.a));
    }

    public static RGBA saturate(RGBA color, float amount) {
        HSL hsl = rgbaToHsl(color);
        return hslToRgba(new HSL(hsl.h, clamp(hsl.s + amount, 0f, 1f), hsl.l, hsl.a));
    }

    public static RGBA desaturate(RGBA color, float amount) {
        HSL hsl = rgbaToHsl(color);
        return hslToRgba(new HSL(hsl.h, clamp(hsl.s - amount, 0f, 1f), hsl.l, hsl.a));
    }

    public static RGBA adjustHue(RGBA color, float degrees) {
        HSL hsl = rgbaToHsl(color);
        return hslToRgba(new HSL(hsl.h + degrees, hsl.s, hsl.l, hsl.a));
    }

    public static RGBA withAlpha(RGBA color, int alpha) {
        return new RGBA(color.r, color.g, color.b, alpha);
    }

    public static RGBA withAlpha(RGBA color, float alpha) {
        return new RGBA(color.r, color.g, color.b, Math.round(clamp(alpha, 0f, 1f) * 255));
    }

    public static RGBA invert(RGBA color) {
        return new RGBA(255 - color.r, 255 - color.g, 255 - color.b, color.a);
    }

    public static RGBA complement(RGBA color) {
        return adjustHue(color, 180);
    }

    public static RGBA grayscale(RGBA color) {
        int gray = Math.round(0.299f * color.r + 0.587f * color.g + 0.114f * color.b);
        return new RGBA(gray, gray, gray, color.a);
    }

    public static RGBA mix(RGBA c1, RGBA c2, float ratio) {
        float t = clamp(ratio, 0f, 1f);
        return new RGBA(
                Math.round(c1.r + (c2.r - c1.r) * t),
                Math.round(c1.g + (c2.g - c1.g) * t),
                Math.round(c1.b + (c2.b - c1.b) * t),
                Math.round(c1.a + (c2.a - c1.a) * t)
        );
    }

    public static RGBA overlay(RGBA base, RGBA over) {
        float a = over.a / 255f;
        float inv = 1f - a;
        return new RGBA(
                Math.round(base.r * inv + over.r * a),
                Math.round(base.g * inv + over.g * a),
                Math.round(base.b * inv + over.b * a),
                Math.max(base.a, over.a)
        );
    }

    public static RGBA multiply(RGBA c1, RGBA c2) {
        return new RGBA(
                (c1.r * c2.r) / 255,
                (c1.g * c2.g) / 255,
                (c1.b * c2.b) / 255,
                (c1.a * c2.a) / 255
        );
    }

    public static RGBA screen(RGBA c1, RGBA c2) {
        return new RGBA(
                255 - ((255 - c1.r) * (255 - c2.r)) / 255,
                255 - ((255 - c1.g) * (255 - c2.g)) / 255,
                255 - ((255 - c1.b) * (255 - c2.b)) / 255,
                255 - ((255 - c1.a) * (255 - c2.a)) / 255
        );
    }

    public static RGBA additive(RGBA c1, RGBA c2) {
        return new RGBA(
                Math.min(255, c1.r + c2.r),
                Math.min(255, c1.g + c2.g),
                Math.min(255, c1.b + c2.b),
                Math.min(255, c1.a + c2.a)
        );
    }

    public static RGBA subtractive(RGBA c1, RGBA c2) {
        return new RGBA(
                Math.max(0, c1.r - c2.r),
                Math.max(0, c1.g - c2.g),
                Math.max(0, c1.b - c2.b),
                Math.max(0, c1.a - c2.a)
        );
    }

    public static RGBA[] complementaryPalette(RGBA base) {
        return new RGBA[]{ base, complement(base) };
    }

    public static RGBA[] triadicPalette(RGBA base) {
        return new RGBA[]{ base, adjustHue(base,120), adjustHue(base,240) };
    }

    public static RGBA[] tetradicPalette(RGBA base) {
        return new RGBA[]{ base, adjustHue(base,90), adjustHue(base,180), adjustHue(base,270) };
    }

    public static RGBA[] analogousPalette(RGBA base) {
        return new RGBA[]{ adjustHue(base,-60), adjustHue(base,-30), base, adjustHue(base,30), adjustHue(base,60) };
    }

    public static RGBA[] splitComplementaryPalette(RGBA base) {
        return new RGBA[]{ base, adjustHue(base,150), adjustHue(base,210) };
    }

    public static RGBA[] monochromaticPalette(RGBA base, int count) {
        RGBA[] palette = new RGBA[Math.max(1, count)];
        HSL h = rgbaToHsl(base);
        for (int i = 0; i < palette.length; i++) {
            float l = (palette.length == 1) ? h.l : (float) i / (palette.length - 1);
            palette[i] = hslToRgba(new HSL(h.h, h.s, l, h.a));
        }
        return palette;
    }

    public static RGBA[] gradientPalette(RGBA start, RGBA end, int steps) {
        int n = Math.max(2, steps);
        RGBA[] palette = new RGBA[n];
        for (int i = 0; i < n; i++) {
            float t = i / (float) (n - 1);
            palette[i] = mix(start, end, t);
        }
        return palette;
    }

    public static float getLuminance(RGBA c) {
        float r = c.r / 255f, g = c.g / 255f, b = c.b / 255f;
        r = r <= 0.03928f ? r / 12.92f : (float) Math.pow((r + 0.055f) / 1.055f, 2.4f);
        g = g <= 0.03928f ? g / 12.92f : (float) Math.pow((g + 0.055f) / 1.055f, 2.4f);
        b = b <= 0.03928f ? b / 12.92f : (float) Math.pow((b + 0.055f) / 1.055f, 2.4f);
        return 0.2126f * r + 0.7152f * g + 0.0722f * b;
    }

    public static float getContrastRatio(RGBA c1, RGBA c2) {
        float l1 = getLuminance(c1);
        float l2 = getLuminance(c2);
        float bright = Math.max(l1, l2);
        float dark = Math.min(l1, l2);
        return (bright + 0.05f) / (dark + 0.05f);
    }

    public static boolean isAccessible(RGBA fg, RGBA bg, boolean largeText) {
        float ratio = getContrastRatio(fg, bg);
        return largeText ? ratio >= 3.0f : ratio >= 4.5f;
    }

    public static boolean isAccessibleAAA(RGBA fg, RGBA bg, boolean largeText) {
        float ratio = getContrastRatio(fg, bg);
        return largeText ? ratio >= 4.5f : ratio >= 7.0f;
    }

    public static boolean isDark(RGBA c) { return getLuminance(c) < 0.5f; }
    public static boolean isLight(RGBA c) { return !isDark(c); }

    public static RGBA getBestTextColor(RGBA bg) {
        return isDark(bg) ? new RGBA(255,255,255,255) : new RGBA(0,0,0,255);
    }

    public static float getDistance(RGBA c1, RGBA c2) {
        int dr = c1.r - c2.r, dg = c1.g - c2.g, db = c1.b - c2.b;
        return (float) Math.sqrt(dr * dr + dg * dg + db * db);
    }

    public static float getPerceptualDistance(RGBA c1, RGBA c2) {
        HSL h1 = rgbaToHsl(c1);
        HSL h2 = rgbaToHsl(c2);
        float dh = Math.min(Math.abs(h1.h - h2.h), 360 - Math.abs(h1.h - h2.h));
        float ds = Math.abs(h1.s - h2.s);
        float dl = Math.abs(h1.l - h2.l);
        return (float) Math.sqrt(dh * dh + ds * ds * 100 + dl * dl * 100);
    }

    public static RGBA fromTemperature(int kelvin) {
        float temp = kelvin / 100f;
        float red, green, blue;
        if (temp <= 66) red = 255;
        else { red = temp - 60; red = (float) (329.698727446 * Math.pow(red, -0.1332047592)); red = clamp(red, 0f, 255f); }
        if (temp <= 66) { green = temp; green = (float) (99.4708025861 * Math.log(green) - 161.1195681661); }
        else { green = temp - 60; green = (float) (288.1221695283 * Math.pow(green, -0.0755148492)); }
        green = clamp(green, 0f, 255f);
        if (temp >= 66) blue = 255;
        else if (temp <= 19) blue = 0;
        else { blue = temp - 10; blue = (float) (138.5177312231 * Math.log(blue) - 305.0447927307); blue = clamp(blue, 0f, 255f); }
        return new RGBA(Math.round(red), Math.round(green), Math.round(blue));
    }

    public static RGBA warmColor(RGBA color, float intensity) {
        float t = clamp(intensity, 0f, 1f);
        return mix(color, new RGBA(255, 200, 100), t * 0.3f);
    }

    public static RGBA coolColor(RGBA color, float intensity) {
        float t = clamp(intensity, 0f, 1f);
        return mix(color, new RGBA(100, 200, 255), t * 0.3f);
    }

    public static RGBA interpolate(RGBA from, RGBA to, float progress) {
        return interpolate(from, to, progress, InterpolationMode.LINEAR);
    }

    public static RGBA interpolate(RGBA from, RGBA to, float progress, InterpolationMode mode) {
        float t = clamp(progress, 0f, 1f);
        switch (mode) {
            case EASE_IN -> t = t * t;
            case EASE_OUT -> t = 1 - (1 - t) * (1 - t);
            case EASE_IN_OUT -> t = t < 0.5f ? 2 * t * t : 1 - 2 * (1 - t) * (1 - t);
            case HSV -> {
                HSV a = rgbaToHsv(from), b = rgbaToHsv(to);
                float h = interpolateHue(a.h, b.h, t);
                float s = a.s + (b.s - a.s) * t;
                float v = a.v + (b.v - a.v) * t;
                float al = a.a + (b.a - a.a) * t;
                return hsvToRgba(new HSV(h, s, v, al));
            }
            case HSL -> {
                HSL a = rgbaToHsl(from), b = rgbaToHsl(to);
                float h = interpolateHue(a.h, b.h, t);
                float s = a.s + (b.s - a.s) * t;
                float l = a.l + (b.l - a.l) * t;
                float al = a.a + (b.a - a.a) * t;
                return hslToRgba(new HSL(h, s, l, al));
            }
        }
        return mix(from, to, t);
    }

    private static float interpolateHue(float from, float to, float t) {
        float diff = to - from;
        if (Math.abs(diff) > 180) { if (diff > 0) from += 360; else to += 360; }
        float res = from + (to - from) * t;
        return wrap360(res);
    }

    public static int parseToInt(String colorString) { return parse(colorString).toInt(); }
    public static String parseToHex(String colorString) { return parse(colorString).toHex(); }
    public static boolean isValidColor(String colorString) { try { parse(colorString); return true; } catch (Exception e) { return false; } }
    public static String[] getNamedColors() { return NamedColor.names(); }

    public static String findClosestNamedColor(RGBA color) {
        String best = NamedColor.BLACK.name().toLowerCase(Locale.ROOT);
        float bestD = Float.MAX_VALUE;
        for (NamedColor nc : NamedColor.values()) {
            RGBA named = nc.toRGBA();
            float d = getDistance(color, named);
            if (d < bestD) { bestD = d; best = nc.name().toLowerCase(Locale.ROOT); }
        }
        return best;
    }

    private static int clamp(int v, int min, int max) { return Math.max(min, Math.min(max, v)); }
    private static float clamp(float v, float min, float max) { return Math.max(min, Math.min(max, v)); }
    private static float wrap360(float h) { float x = h % 360f; return x < 0 ? x + 360f : x; }

    public static RGBA randomColor() {
        return new RGBA((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256));
    }

    public static RGBA randomColor(int seed) {
        Random r = new Random(seed);
        return new RGBA(r.nextInt(256), r.nextInt(256), r.nextInt(256));
    }

    public static RGBA randomPastelColor() {
        HSL h = new HSL((float)(Math.random()*360), 0.3f + (float)(Math.random()*0.4f), 0.7f + (float)(Math.random()*0.2f));
        return hslToRgba(h);
    }

    public static RGBA randomVibrantColor() {
        HSL h = new HSL((float)(Math.random()*360), 0.7f + (float)(Math.random()*0.3f), 0.4f + (float)(Math.random()*0.4f));
        return hslToRgba(h);
    }

    public static RGBA[] generatePalette(int count, PaletteType type) { return generatePalette(count, type, (int)(Math.random()*1000)); }

    public static RGBA[] generatePalette(int count, PaletteType type, int seed) {
        Random random = new Random(seed);
        RGBA[] out = new RGBA[Math.max(1, count)];
        switch (type) {
            case RANDOM -> { for (int i=0;i<out.length;i++) out[i] = randomColor(seed + i); }
            case PASTEL -> {
                for (int i=0;i<out.length;i++) {
                    random.setSeed(seed + i);
                    HSL h = new HSL(random.nextFloat()*360, 0.3f + random.nextFloat()*0.4f, 0.7f + random.nextFloat()*0.2f);
                    out[i] = hslToRgba(h);
                }
            }
            case VIBRANT -> {
                for (int i=0;i<out.length;i++) {
                    random.setSeed(seed + i);
                    HSL h = new HSL(random.nextFloat()*360, 0.7f + random.nextFloat()*0.3f, 0.4f + random.nextFloat()*0.4f);
                    out[i] = hslToRgba(h);
                }
            }
            case MONOCHROMATIC -> {
                float baseHue = random.nextFloat()*360;
                for (int i=0;i<out.length;i++) {
                    float l = (out.length==1) ? 0.5f : (float)i/(out.length-1);
                    out[i] = hslToRgba(new HSL(baseHue, 0.7f, l));
                }
            }
            case COMPLEMENTARY -> {
                float baseHue = random.nextFloat()*360;
                for (int i=0;i<out.length;i++) {
                    float hue = i%2==0 ? baseHue : wrap360(baseHue+180);
                    out[i] = hslToRgba(new HSL(hue, 0.7f, 0.5f + (i/(float)Math.max(1,out.length-1))*0.3f));
                }
            }
        }
        return out;
    }
}
