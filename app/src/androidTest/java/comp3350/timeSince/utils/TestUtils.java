package comp3350.timeSince.utils;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.UserEventManager;
import comp3350.timeSince.business.UserManager;
import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.PasswordErrorException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;

public class TestUtils {
    private static String username = "admin";
    private static String password = "12345";
    private static UserManager userManager = new UserManager(true);
    private static UserEventManager userEventManager = new UserEventManager(username, true);
    private static int currEventID;

    public static void setupDB() throws NoSuchAlgorithmException, DuplicateUserException, PasswordErrorException {
        Calendar calendarInstance = Calendar.getInstance();
        String eventName;
        EventDSO event1 = new EventDSO(1, calendarInstance, "event1");
        EventDSO event2 = new EventDSO(2, calendarInstance, "event2");
        EventDSO event3 = new EventDSO(3, calendarInstance, "event3");
        List<EventDSO> userEvents = userEventManager.getUserEvents();

        try{
            userManager.getUserByEmail(username);
        } catch(UserNotFoundException e){
            userManager.createUser(username, password, password);
        }

        for(int i = 0; i < userEvents.size(); i++){
            currEventID = userEvents.get(i).getID();
            eventName = userEvents.get(i).getName();
            if(currEventID == 1 || currEventID == 2 || currEventID == 3 || eventName.equals("event4")){
                userEventManager.removeUserEvent(userEvents.get(i));
            }
        }

        Services.getEventPersistence(true);

        userEventManager.addUserEvent(event1);

        calendarInstance.set(Calendar.YEAR, 2020);
        userEventManager.addUserEvent(event2);

        calendarInstance.set(Calendar.YEAR, 2021);
        userEventManager.addUserEvent(event3);
    }
}
