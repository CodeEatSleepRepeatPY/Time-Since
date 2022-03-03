package comp3350.timeSince.persistence;

import java.util.ArrayList;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;

public class FakeDatabase implements I_Database{

    //----------------------------------------
    // instance variables
    //----------------------------------------

    private ArrayList<UserDSO> usersDatabase;

    //----------------------------------------
    // constructors
    //----------------------------------------

    public FakeDatabase(){
        usersDatabase = new ArrayList<UserDSO>();
    }

    //----------------------------------------
    // typical methods for a database
    //----------------------------------------

    public void addUser(UserDSO user){
        if(user != null)
            usersDatabase.add(user);
    }

    public void removeUser(UserDSO user){
        if(user != null) {
            int index = getUserIndex(user);
            if (index != -1) {
                usersDatabase.remove(index);
            }
        }
    }

    public void addEvent(UserDSO user, EventDSO event){
        if(user != null && event != null) {
            int index = getUserIndex(user);
            if (index != -1) {
                usersDatabase.get(index).getUserEvents().add(event);
            }
        }
    }

    public void removeEvent(UserDSO user, EventDSO event){
        if(user != null && event != null) {
            int index = getUserIndex(user);
            boolean foundEvent = false;
            EventDSO currEvent;
            UserDSO userObj;

            if (index != -1) {
                userObj = usersDatabase.get(index);
                for (int i = 0; i < userObj.getUserEvents().size() && !foundEvent; i++) {
                    // if the name and description of the event we want to remove
                    // matches the name and description of the event in the database
                    // then remove that event from the database
                    currEvent = userObj.getUserEvents().get(i);
                    if (event.getName().equals(currEvent.getName()) &&
                            event.getDescription().equals(currEvent.getDescription())) {
                        userObj.getUserEvents().remove(i);
                        foundEvent = true;
                    }
                }
            }
        }
    }

    //----------------------------------------
    // getters
    //----------------------------------------

    public ArrayList<UserDSO> getUsers(){
        return usersDatabase;
    }

    public UserDSO getUser(String uuid){
        UserDSO user = null;
        boolean foundUser = false;

        for(int i = 0; i < usersDatabase.size() && !foundUser; i++){
            if(usersDatabase.get(i).getUuid().equals(uuid)){
                user = usersDatabase.get(i);
                foundUser = true;
            }
        }

        return user;
    }

    private int getUserIndex(UserDSO user){
        int index = -1;
        boolean foundUser = false;

        if(user != null) {
            for (int i = 0; i < usersDatabase.size() && !foundUser; i++) {
                if (user.getUuid().equals(usersDatabase.get(i).getUuid())) {
                    index = i;
                    foundUser = true;
                }
            }
        }

        return index;
    }
}
