package comp3350.timeSince.business;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Adapter: convert between calendar and timestamp.
 * <p>
 * Database layer stores values as a timestamp, and the presentation layer stores
 * the values as a calendar.
 */
public class DateUtils {

    public static Timestamp calToTimestamp(Calendar calendar) {
        Timestamp toReturn = null;
        if (calendar != null) {
            toReturn = new Timestamp(calendar.getTimeInMillis());
        }
        return toReturn;
    }

    public static Calendar timestampToCal(Timestamp timestamp) {
        Calendar toReturn = null;
        if (timestamp != null) {
            toReturn = Calendar.getInstance();
            toReturn.setTimeInMillis(timestamp.getTime());
        }
        return toReturn;
    }

}
