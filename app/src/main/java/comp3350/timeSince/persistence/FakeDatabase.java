import java.util.ArrayList;

package comp3350.timeSince.persistence;

public class FakeDatabase implements I_FakeDatabase{

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
        
        boolean userFound = false;
        for(int i = 0; i < usersDatabase.size(); i++){
            if(usersDatabase.get(i).getUuid().equals(user.getUuid())){ //check to see if the user is already in the database
                userFound = true;
            }
        }
        if(!userFound){ //if the user is not in the database then add the user
            usersDatabase.add(user);
            if(usersDatabase.size() >= maxCapacity){ //increase capacity when we reach max capacity
                maxCapacity += MAX_SIZE_INCREASE;
                for(int i = 0; i < MAX_SIZE_INCREASE; i++){
                    eventsDatabase.add(new ArrayList<EventDSO>());
                }
            }
        }
    }

    public void removeUser(String uuid){
        for(int i = 0; i < usersDatabase.size(); i++){
            if(usersDatabase.get(i).getUuid().equals(uuid)){
                usersDatabase.remove(i);
                break;
            }
        }
    }

    public void addEvent(UserDSO user, EventDSO event){
        if(user == null || event == null)
            return;
        
        for(int i = 0; i < usersDatabase.size(); i++){
            if(user.getUuid().equals(usersDatabase.get(i).getUuid())){
                eventsDatabase.get(i).add(event);
                break;
            }
        }
    }

    public void removeEvent(UserDSO user, EventDSO event){
        if(user == null || event == null)
            return;
        
        int index = -1;
        for(int i = 0; i < usersDatabase.size(); i++){
            if(user.getUuid().equals(usersDatabase.get(i).getUuid())){
                index = i;
                break;
            }
        }
        if(index != -1) {
            for(int i = 0; i < eventsDatabase.get(index).size(); i++) {
                if(event.getDescription() != null){
                    // if the name and description of the event we want to remove 
                    // matches the name and description of the event in the database 
                    // then remove that event
                    if(event.getName().equals(eventsDatabase.get(index).get(i).getName()) && event.getDescription().equals(eventsDatabase.get(index).get(i).getDescription())) {
                        eventsDatabase.get(index).remove(i);
                        break;
                    }
                }
                //if there is a match but no description (case of: description = null)
                else if(event.getName().equals(eventsDatabase.get(index).get(i).getName()) && eventsDatabase.get(index).get(i).getDescription() == null){
                    eventsDatabase.get(index).remove(i);
                    break;
                }
            }
        }
    }

    //----------------------------------------
    // getters
    //----------------------------------------

    public ArrayList<EventDSO> getUserEvents(UserDSO user){
        for(int i = 0; i < usersDatabase.size(); i++){
            if(user.getUuid().equals(usersDatabase.get(i).getUuid())){
                return eventsDatabase.get(i);
            }
        }
        return new ArrayList<EventDSO>(); //reutrn empty list if the user doesn't exist in the database
    }

    public UserDSO getUser(String uuid){
        for(int i = 0; i < usersDatabase.size(); i++){
            if(usersDatabase.get(i).getUuid().equals(uuid)){
                return usersDatabase.get(i);
            }
        }
        return null;
    }

}
