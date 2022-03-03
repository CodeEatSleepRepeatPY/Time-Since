package comp3350.timeSince.persistence;

import java.util.ArrayList;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;

public interface I_Database {
    public void addUser(UserDSO user);

    public void removeUser(UserDSO user);

    public void addEvent(UserDSO user, EventDSO event);

    public void removeEvent(UserDSO user, EventDSO event);

    public UserDSO getUser(String uuid);

    public ArrayList<UserDSO> getUsers();
}
