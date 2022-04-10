package comp3350.timeSince.persistence.hsqldb;

class TS {

    // Tables
    public static final String TABLE_USER = "users";
    public static final String TABLE_EVENT = "events";
    public static final String TABLE_LABEL = "labels"; // table name
    public static final String TABLE_USER_EVENTS = "usersevents"; // table name
    public static final String TABLE_USER_LABELS = "userslabels"; // table name
    public static final String TABLE_EVENT_LABELS = "eventslabels"; // table name

    // Users
    public static final int INITIAL_USER_COUNT = 1;
    public static final String USER_ID = "uid"; // int
    public static final String EMAIL = "email"; // 50 characters, unique, not null
    public static final String USER_NAME = "user_name"; // 30 characters
    public static final String DATE_REGISTERED = "date_registered"; // timestamp, not null
    public static final String PASSWORD = "password_hash"; // 64 characters

    // Events
    public static final int INITIAL_EVENT_COUNT = 0;
    public static final String EVENT_ID = "eid"; // int
    public static final String EVENT_NAME = "event_name"; // 30 characters, not null
    public static final String EVENT_DATE_CREATED = "date_created"; // timestamp, not null
    public static final String EVENT_DESCRIPTION = "description"; // 100 characters
    public static final String FINISH_TIME = "target_finish_time"; // timestamp
    public static final String YEARS = "frequency_year"; // int
    public static final String MONTHS = "frequency_month"; // int
    public static final String DAYS = "frequency_day"; // int

    // Labels
    public static final int INITIAL_LABEL_COUNT = 0;
    public static final String LABEL_ID = "lid"; // int
    public static final String LABEL_NAME = "label_name"; // 30 characters, not null
    public static final String COLOR = "color"; // 15 characters

    // Other
    public static final String FAVORITE = "is_favorite"; // boolean
    public static final String COMPLETE = "is_done"; // boolean

}
