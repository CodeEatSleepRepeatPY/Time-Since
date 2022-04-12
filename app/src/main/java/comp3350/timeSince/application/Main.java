package comp3350.timeSince.application;

/**
 * Time Since is an application to keep track of when the user last did something.
 *
 * @since January 2022
 */
public class Main {

    private static String dbName = "TS";


    /**
     * @param name the name of the file
     * @author Taken from the sample project for this course
     */
    public static void setDBPathName(final String name) {
        try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        dbName = name;
    }

    /**
     * @return the name of the file
     * @author Taken from teh sample project for this course
     */
    public static String getDBPathName() {
        return dbName;
    }

}
