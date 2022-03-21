package comp3350.timeSince.persistence.utils;

import java.sql.Timestamp;
import java.util.Date;

public class DateUtils {

    public static Timestamp dateToTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    public static Date timestampToDate(Timestamp timestamp) {
        return new Date(timestamp.getTime());
    }
}
