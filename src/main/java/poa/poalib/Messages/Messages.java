package poa.poalib.Messages;

import java.text.DecimalFormat;

public class Messages {


    public static String essentialsToMinimessage(String string) {
        String x = string.replaceAll("&#([a-fA-F0-9]{6})", "<#$1><bold:false><underlined:false><strikethrough:false><italic:false><obfuscated:false>");
        x = x.replace("§", "&");
        x = x.replace("&l", "<bold>");
        x = x.replace("&n", "<underlined>");
        x = x.replace("&m", "<strikethrough>");
        x = x.replace("&o", "<italic>");
        x = x.replace("&k", "<obfuscated>");
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

}
