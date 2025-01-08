package poa.poalib.Messages;

import io.papermc.paper.adventure.PaperAdventure;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Messages {


    public static String essentialsToMinimessage(String string) {
        String x = string.replaceAll("&#([a-fA-F0-9]{6})", "<#$1><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("§", "&");
        x = x.replace("&l", "<bold><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("&n", "<underlined><bold:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("&m", "<strikethrough><bold:false><underlined:false><italic:false><obfuscated:false>");
        x = x.replace("&o", "<italic><bold:false><underlined:false><strikethrough:false><obfuscated:false>");
        x = x.replace("&k", "<obfuscated><bold:false><underlined:false><strikethrough:false><italic:false>");
        x = x.replace("&r", "<reset>");
        x = x.replace("&0", "<black><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("&1", "<dark_blue><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("&2", "<dark_green><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("&3", "<dark_aqua><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("&4", "<dark_red><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("&5", "<dark_purple><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("&6", "<gold><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("&7", "<gray><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("&8", "<dark_gray><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("&9", "<blue><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("&a", "<green><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("&b", "<aqua><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("&c", "<red><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("&d", "<light_purple><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("&e", "<yellow><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("&f", "<white><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");

        return x;
    }



    public static String bukkitToEssentials(String string){
        return string.replaceAll("§x§(.)§(.)§(.)§(.)§(.)§(.)", "&#$1$2$3$4$5$6");
    }

    public static String placeholder(OfflinePlayer player, String string){
        return PlaceholderAPI.setPlaceholders(player, string);
    }



    public static String timeToString(long sec) {
        long days = (sec / 86400);
        long hours = (sec % 86400) / 3600;
        long minutes = ((sec % 86400) % 3600) / 60;
        long seconds = sec % 60;
        String toreplace;
        if (days != 0)
            toreplace = days + " Days, " + hours + " Hours, " + minutes + " Minutes";
        else if (hours != 0)
            toreplace = hours + " Hours, " + minutes + " Minutes";
        else if (minutes != 0)
            toreplace = minutes + " Minutes, " + seconds + " Seconds";
        else toreplace = seconds + " Seconds";
        return toreplace;

    }


    public static String timeToStringShort(long sec) {
        long days = (sec / 86400);
        long hours = (sec % 86400) / 3600;
        long minutes = ((sec % 86400) % 3600) / 60;
        long seconds = sec % 60;
        String toreplace;
        if (days != 0)
            toreplace = days + "d, " + hours + "h, " + minutes + "m";
        else if (hours != 0)
            toreplace = hours + "h, " + minutes + "m";
        else if (minutes != 0)
            toreplace = minutes + "m, " + seconds + "s";
        else toreplace = seconds + "s";
        return toreplace;
    }


    public static String numberFormat(double number){
        DecimalFormat df = new DecimalFormat("###,###,###,###,###,###,###,###.##");
        String formattedBalance = df.format(number);
        return formattedBalance;
    }

    public static String numberToLetterFormat(double number){
        if (number < 1000) {
            return formatDecimal(number); // Less than 1 thousand
        } else if (number < 1000000) {
            return formatDecimal(number / 1000) + "K"; // Thousands
        } else if (number < 1000000000) {
            return formatDecimal(number / 1000000) + "M"; // Millions
        } else if (number < 1000000000000L) {
            return formatDecimal(number / 1000000000) + "B"; // Billions
        } else {
            return formatDecimal(number / 1000000000000L) + "T"; // Trillions
        }
    }
    private static String formatDecimal(double number) {
        if (number % 1 == 0) {
            // If the number is a whole number, cast to long to remove the decimal part
            return String.format("%d", (long) number);
        } else {
            // If the number has a fractional part, format it with one decimal place
            return String.format("%.1f", number).replaceAll("\\.0+$", "");
        }
    }

    private static final char[] SMALL_CAPS_ALPHABET = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘqʀꜱᴛᴜᴠᴡxyᴢ".toCharArray();

    public static String toSmallCaps(String text) {
        if (null == text) {
            return null;
        }
        text = text.toLowerCase();
        int length = text.length();
        StringBuilder smallCaps = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            char c = text.charAt(i);

            if (c >= 'a' && c <= 'z') {
                smallCaps.append(SMALL_CAPS_ALPHABET[c - 'a']);
            } else {
                smallCaps.append(c);
            }
        }
        return smallCaps.toString();
    }

    public static String progressBar(int amount, int outOf, int total) {
        int percent = (int) Math.round((outOf / (double) total) * 100);

        percent = Math.min(percent, 100);

        int greenSymbols = (int) Math.round((percent / 100.0) * amount);

        StringBuilder progressBar = new StringBuilder();

        for (int i = 0; i < amount; i++) {
            if (i < greenSymbols) {
                progressBar.append("§a|");
            } else {
                progressBar.append("§c|");
            }
        }

        return progressBar.toString();
    }

    public static long convertToSeconds(String duration) {
        if (duration == null || duration.isEmpty()) {
            throw new IllegalArgumentException("Duration string cannot be null or empty.");
        }

        // Regular expression to match the pattern (e.g., 2s, 3m, 4h, etc.)
        Pattern pattern = Pattern.compile("(\\d+)([smhdw])");
        Matcher matcher = pattern.matcher(duration);

        long totalSeconds = 0;

        // Conversion factors
        final int SECONDS_IN_MINUTE = 60;
        final int SECONDS_IN_HOUR = 60 * SECONDS_IN_MINUTE;
        final int SECONDS_IN_DAY = 24 * SECONDS_IN_HOUR;
        final int SECONDS_IN_WEEK = 7 * SECONDS_IN_DAY;

        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            char unit = matcher.group(2).charAt(0);

            switch (unit) {
                case 's':
                    totalSeconds += value;
                    break;
                case 'm':
                    totalSeconds += value * SECONDS_IN_MINUTE;
                    break;
                case 'h':
                    totalSeconds += value * SECONDS_IN_HOUR;
                    break;
                case 'd':
                    totalSeconds += value * SECONDS_IN_DAY;
                    break;
                case 'w':
                    totalSeconds += value * SECONDS_IN_WEEK;
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected unit: " + unit);
            }
        }

        return totalSeconds;
    }

    public static Component componentActual(net.minecraft.network.chat.Component nms) {
        return PaperAdventure.WRAPPER_AWARE_SERIALIZER.deserialize(nms);
    }

}
