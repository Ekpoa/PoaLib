package poa.poalib.color;

import org.bukkit.Color;

public final class ColorUtil {

    public static String toHex(Color color) {
        if (color == null) return null;
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Color fromHex(String hex) {
        if (hex == null) return null;

        String s = hex.startsWith("#") ? hex.substring(1) : hex;
        if (s.length() != 6)
            throw new IllegalArgumentException("Invalid hex color: " + hex);

        int rgb = Integer.parseInt(s, 16);
        return Color.fromRGB(
                (rgb >> 16) & 0xFF,
                (rgb >> 8) & 0xFF,
                rgb & 0xFF
        );
    }

    public static String toHexARGB(Color color) {
        if (color == null) return null;
        return String.format("#%08X", color.asARGB());
    }

    public static Color fromHexARGB(String hex) {
        if (hex == null) return null;

        String s = hex.startsWith("#") ? hex.substring(1) : hex;
        if (s.length() != 8)
            throw new IllegalArgumentException("Invalid ARGB hex color: " + hex);

        int argb = (int) Long.parseLong(s, 16);
        return Color.fromARGB(argb);
    }
}

