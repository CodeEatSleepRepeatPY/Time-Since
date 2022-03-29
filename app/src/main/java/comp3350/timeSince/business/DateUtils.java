package comp3350.timeSince.business;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static Timestamp dateToTimestamp(Date date) {
        Timestamp toReturn = null;

        if (date != null) {
            toReturn = new Timestamp(date.getTime());
        }
        return toReturn;
    }

    public static Date timestampToDate(Timestamp timestamp) {
        Date toReturn = null;

        if (timestamp != null) {
            toReturn = new Date(timestamp.getTime());
        }
        return toReturn;
    }

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
