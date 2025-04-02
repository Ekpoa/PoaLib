package poa.poalib.commands;

public class TimeInput {

    public static long parseTimeToMilliseconds(String timeFrame) {
        long milliseconds = 0;

        if (timeFrame.matches("\\d+[hmsdw]")) {
            char timeUnit = timeFrame.charAt(timeFrame.length() - 1);
            int timeValue = Integer.parseInt(timeFrame.substring(0, timeFrame.length() - 1));

            switch (timeUnit) {
                case 'w' -> milliseconds = timeValue * 604800000L;
                case 'd' -> milliseconds = timeValue * 86400000L;
                case 'h' -> milliseconds = timeValue * 3600000L;
                case 'm' -> milliseconds = timeValue * 60000L;
                case 's' -> milliseconds = timeValue * 1000L;
            }
        } else {
            throw new IllegalArgumentException("Invalid time frame format. Use a number followed by 'h', 'm', or 's'.");
        }

        return milliseconds;
    }

    public static long parseTimeToSeconds(String timeFrame){
        return parseTimeToMilliseconds(timeFrame)/1000;
    }

}
