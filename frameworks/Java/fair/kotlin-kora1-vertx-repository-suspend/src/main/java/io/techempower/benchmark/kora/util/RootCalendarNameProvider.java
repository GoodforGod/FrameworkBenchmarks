package io.techempower.benchmark.kora.util;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.spi.CalendarNameProvider;

public final class RootCalendarNameProvider extends CalendarNameProvider {

    @Override
    public String getDisplayName(String calendarType, int field, int value, int style, Locale locale) {
        if (isRootEra(field, locale)) {
            if (value == 0) {
                return "BC";
            }
            if (value == 1) {
                return "AD";
            }
        }
        return null;
    }

    @Override
    public Map<String, Integer> getDisplayNames(String calendarType, int field, int style, Locale locale) {
        if (isRootEra(field, locale)) {
            return Map.of("BC", 0, "AD", 1);
        }
        return null;
    }

    @Override
    public Locale[] getAvailableLocales() {
        return new Locale[] { Locale.ROOT };
    }

    private static boolean isRootEra(int field, Locale locale) {
        // Vert.x 4.3.x initializes PostgreSQL date codecs by parsing "BC" with Locale.ROOT.
        return field == Calendar.ERA && (locale == null || locale.equals(Locale.ROOT));
    }
}
