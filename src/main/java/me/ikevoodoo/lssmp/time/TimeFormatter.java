package me.ikevoodoo.lssmp.time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class TimeFormatter {

    private static final DateTimeFormatter EU_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter US_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");



    private static final Map<String, Long> UNIT_CONVERSIONS = new HashMap<>();
    private static final Map<String, Pattern> UNIT_PATTERNS = new HashMap<>();

    static {
        registerUnit("day", 1000L * 60 * 60 * 24);
        registerUnit("hour", 1000L * 60 * 60);
        registerUnit("minute", 1000L * 60);
        registerUnit("second", 1000L);
    }

    public static String formatDuration(long milliseconds) {
        if (milliseconds < 0 || milliseconds == Long.MAX_VALUE) {
            return "infinite";
        }

        var seconds = milliseconds / 1000;
        var days = seconds / (24 * 3600);
        var hours = (seconds % (24 * 3600)) / 3600;
        var minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        var result = new StringBuilder();

        appendUnit(result, days, "day");
        appendUnit(result, hours, "hour");
        appendUnit(result, minutes, "minute");
        appendUnit(result, seconds, "second");

        var length = result.length();
        if (length > 2) {
            result.setLength(length - 2);
        }

        return result.toString();
    }

    public static long parseDuration(String duration) {
        if (duration == null || duration.isBlank() || duration.equalsIgnoreCase("infinite")) {
            return Long.MAX_VALUE;
        }

        var totalMilliseconds = 0L;

        for (var entry : UNIT_CONVERSIONS.entrySet()) {
            var unit = entry.getKey();
            var conversionFactor = entry.getValue();

            var pattern = UNIT_PATTERNS.get(unit);
            var matcher = pattern.matcher(duration);

            if (!matcher.find()) continue;

            var value = Integer.parseInt(matcher.group(1));
            totalMilliseconds += value * conversionFactor;
        }

        return totalMilliseconds;
    }

    public static String formatDate(LocalDateTime date, boolean us) {
        return us ? US_FORMATTER.format(date) : EU_FORMATTER.format(date);
    }

    private static void appendUnit(StringBuilder result, long value, String unit) {
        if (value <= 0) {
            return;
        }

        result.append(value).append(" ").append(unit);

        if (value > 1) {
            result.append("s");
        }

        result.append(", ");
    }

    private static void registerUnit(String unit, long conversion) {
        UNIT_CONVERSIONS.put(unit, conversion);
        UNIT_PATTERNS.put(unit, Pattern.compile("(\\d+)\\s*" + unit + "s?"));
    }

}
