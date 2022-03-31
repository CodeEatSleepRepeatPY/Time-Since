package comp3350.timeSince.persistence;

import java.util.List;

import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;

public interface I_Database {
    void addUser(UserDSO user);

    void removeUser(UserDSO user);

    void addEvent(UserDSO user, EventDSO event);

    void removeEvent(UserDSO user, EventDSO event);

    void addEventLabel(EventLabelDSO label);

    List<EventLabelDSO> getAllEventLabels();

    UserDSO getUser(String uuid);

    List<UserDSO> getUsers();
}
