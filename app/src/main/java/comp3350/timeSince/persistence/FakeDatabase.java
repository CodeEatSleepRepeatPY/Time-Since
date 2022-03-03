package comp3350.timeSince.persistence;

import java.util.ArrayList;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;

public class FakeDatabase implements I_Database{

    //----------------------------------------
    // instance variables
    //----------------------------------------

    private final int MAX_SIZE_INCREASE = 50;
    private int maxCapacity;
    private ArrayList<UserDSO> usersDatabase;
    private ArrayList<ArrayList<EventDSO>> eventsDatabase;

    //----------------------------------------
    // constructors
    //----------------------------------------

    public FakeDatabase(){
        maxCapacity = 1;
        usersDatabase = new ArrayList<UserDSO>();
        eventsDatabase = new ArrayList<ArrayList<EventDSO>>();
    }

    //----------------------------------------
    // typical methods for a database
    //----------------------------------------

    public void addUser(UserDSO user){
        if(user == null)
            return;

        usersDatabase.add(user);
        if(usersDatabase.size() >= maxCapacity){ //increase capacity when we reach max capacity
            maxCapacity += MAX_SIZE_INCREASE;
            for(int i = 0; i < MAX_SIZE_INCREASE; i++){
                eventsDatabase.add(new ArrayList<EventDSO>());
            }
        }
    }

    public void removeUser(UserDSO user){
        if(user == null)
            return;

        int index = getUserIndex(user);

        if(index != -1){
            usersDatabase.remove(index);
            eventsDatabase.remove(index);
        }
    }

    public void addEvent(UserDSO user, EventDSO event){
        if(user == null || event == null)
            return;

        int index = getUserIndex(user);

        if(index != -1) {
            eventsDatabase.get(index).add(event);
        }
    }

    public void removeEvent(UserDSO user, EventDSO event){
        if(user == null || event == null)
            return;

        int index = getUserIndex(user);
        boolean foundEvent = false;

        if(index != -1) {
            for(int i = 0; i < eventsDatabase.get(index).size() && !foundEvent; i++) {
                // if the name and description of the event we want to remove
                // matches the name and description of the event in the database
                // then remove that event from the database
                if(event.getName().equals(eventsDatabase.get(index).get(i).getName()) && event.getDescription().equals(eventsDatabase.get(index).get(i).getDescription())) {
                    eventsDatabase.get(index).remove(i);
                    foundEvent = true;
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

    public ArrayList<EventDSO> getUserEvents(UserDSO user){
        int index = getUserIndex(user);
        ArrayList<EventDSO> userEvents = new ArrayList<EventDSO>();

        if(index != -1) {
            userEvents = eventsDatabase.get(index);
        }

        return userEvents; //return empty list if the user doesn't exist in the database
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
