package org.wordpress.android.util;

import android.text.format.DateUtils;

import org.wordpress.android.R;
import org.wordpress.android.WordPress;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by nbradbury on 7/3/13.
 */
public class DateTimeUtils {

    private DateTimeUtils() {
        throw new AssertionError();
    }

    /*
     * see http://drdobbs.com/java/184405382
     */
    private static final ThreadLocal<DateFormat> ISO8601Format = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
        }
    };

    /*
     * converts a date to a relative time span ("8h", "3d", "yesterday", etc.)
     */
    public static String javaDateToTimeSpan(Date date) {
        if (date==null)
            return "";

        long localTime = date.getTime();
        long currentMs = System.currentTimeMillis();

        // return "now" if less than a minute has elapsed
        long secondsSince = (currentMs - localTime) / 1000;
        if (secondsSince < 60)
            return WordPress.getContext().getString(R.string.reader_timespan_now);

        // less than an hour (ex: 12m)
        long minutesSince = secondsSince / 60;
        if (minutesSince < 60)
            return Long.toString(minutesSince) + "m";

        // less than a day (ex: 17h)
        long hoursSince = minutesSince / 60;
        if (hoursSince < 24)
            return Long.toString(hoursSince) + "h";

        // less than a week (ex: 5d)
        long daysSince = hoursSince / 24;
        if (daysSince < 7)
            return Long.toString(daysSince) + "d";

        // date is older, so fall back to using getRelativeTimeSpanString
        return DateUtils.getRelativeTimeSpanString(localTime, currentMs, DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
    }

    /*
     * converts an ISO8601 date to a Java date
     */
    public static Date iso8601ToJavaDate(final String strDate) {
        try {
            DateFormat formatter = ISO8601Format.get();
            return formatter.parse(strDate);
        } catch (ParseException e) {
            return null;
        }
    }

    /*
     * converts a Java date to ISO8601
     */
    public static String javaDateToIso8601(Date date) {
        if (date==null)
            return "";
        DateFormat formatter = ISO8601Format.get();
        return formatter.format(date);
    }

    /*
     * returns the current UTC date
     */
    public static Date nowUTC() {
        Date dateTimeNow = new Date();
        return localDateToUTC(dateTimeNow);
    }

    public static Date localDateToUTC(Date dtLocal) {
        if (dtLocal==null)
            return null;
        TimeZone tz = TimeZone.getDefault();
        int currentOffsetFromUTC = tz.getRawOffset() + (tz.inDaylightTime(dtLocal) ? tz.getDSTSavings() : 0);
        return new Date(dtLocal.getTime() - currentOffsetFromUTC);
    }

    /*
     * routines to return a diff between two dates - always return a positive number
     */
    public static int minutesBetween(Date dt1, Date dt2) {
        long msDiff = millisecondsBetween(dt1, dt2);
        if (msDiff==0)
            return 0;
        return (int)(msDiff / 60000);
    }
    public static long millisecondsBetween(Date dt1, Date dt2) {
        if (dt1==null || dt2==null)
            return 0;
        return Math.abs(dt1.getTime() - dt2.getTime());
    }
    public static long iso8601ToTimestamp(final String strDate) {
        Date date = iso8601ToJavaDate(strDate);
        if (date==null)
            return 0;
        return (date.getTime() / 1000);
    }
}
