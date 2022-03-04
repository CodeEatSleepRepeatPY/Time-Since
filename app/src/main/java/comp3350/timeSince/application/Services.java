package comp3350.timeSince.application;

import comp3350.timeSince.persistence.FakeDatabase;
import comp3350.timeSince.persistence.I_Database;

public class Services {

    private static I_Database databasePersistence = null;

    public static synchronized I_Database getDatabase() {
        if(databasePersistence == null) {
            databasePersistence = new FakeDatabase();
        }

        return databasePersistence;
    }

}

