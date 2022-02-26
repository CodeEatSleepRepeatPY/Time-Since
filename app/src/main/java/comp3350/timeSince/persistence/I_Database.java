import java.util.ArrayList;

public interface I_Database {
    public void addUser(UserDSO user);

    public void removeUser(String uuid);

    public void addEvent(UserDSO user, EventDSO event);

    public void removeEvent(UserDSO user, EventDSO event);

    public ArrayList<EventDSO> getUserEvents(UserDSO user);

    public UserDSO getUser(String uuid);
}
