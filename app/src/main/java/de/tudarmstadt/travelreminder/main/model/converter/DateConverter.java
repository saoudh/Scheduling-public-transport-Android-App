package de.tudarmstadt.travelreminder.main.model.converter;

import android.arch.persistence.room.TypeConverter;
import android.content.Context;
import android.text.format.DateUtils;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Converts the DateTime from and to an Database usable format.
 *
 * Additionally this class has some methods to convert an DateTime to an human readable String.
 */
public class DateConverter {

    /**
     * The Format of an DateTime.
     */
    private static final int FORMAT_DATE_TIME = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME;

    /**
     * Formats the DateTime to an human readable String.
     * @param context The current context.
     * @param dateTime The DateTime to convert.
     * @return The converted human readable String.
     */
    public static String format(Context context, DateTime dateTime) {
        if (dateTime == null) dateTime = DateTime.now();
        return DateUtils.formatDateTime(context, dateTime.getMillis(), FORMAT_DATE_TIME);
    }

    /**
     * Formats an Duration object to an human readable String.
     * @param duration The Duration object to convert.
     * @return The converted human readable String.
     */
    public static String format(Duration duration) {
        return DateUtils.formatElapsedTime(duration.toPeriod().getSeconds());
    }

    /**
     * Converts the database value to a valid DateTime value.
     * @param timestamp The database value.
     * @return The equivalent DateTime.
     */
    @TypeConverter
    public static DateTime toDate(Long timestamp) {
        if (timestamp == null) return null;
        return new DateTime(timestamp);
    }


    /**
     * Converts the DateTime to an database usable value.
     * @param dateTime The DateTime to convert.
     * @return The equivalent database value.
     */
    @TypeConverter
    public static Long toTimestamp(DateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.getMillis();
    }
}
