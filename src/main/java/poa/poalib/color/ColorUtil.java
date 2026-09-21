package poa.poalib.color;

import lombok.Getter;
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

    @Getter
    public static class RotatingColours {

        private float progress = 0.0f;
        private int phase = 0;

        private int red = 255;
        private int green = 0;
        private int blue = 0;

        public RotatingColours() {
        }

        public void cycleRGB(float speed) {
            progress += speed;

            while (progress >= 255.0f) {
                progress -= 255.0f;
                phase++;

                if (phase >= 6) {
                    phase = 0;
                }
            }

            int value = Math.round(progress);

            switch (phase) {
                case 0 -> {
                    red = 255;
                    green = value;
                    blue = 0;
                }

                case 1 -> {
                    red = 255 - value;
                    green = 255;
                    blue = 0;
                }

                case 2 -> {
                    red = 0;
                    green = 255;
                    blue = value;
                }

                case 3 -> {
                    red = 0;
                    green = 255 - value;
                    blue = 255;
                }

                case 4 -> {
                    red = value;
                    green = 0;
                    blue = 255;
                }

                case 5 -> {
                    red = 255;
                    green = 0;
                    blue = 255 - value;
                }
            }
        }
    }

}

