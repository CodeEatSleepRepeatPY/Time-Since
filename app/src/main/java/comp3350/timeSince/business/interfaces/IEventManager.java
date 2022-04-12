package comp3350.timeSince.business.interfaces;

import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventDescriptionException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;

public interface IEventManager {
    EventDSO getEventByID(int eventID) throws EventNotFoundException;

    List<EventDSO> getEventList();

    EventDSO insertEvent(String userID, Calendar dueDate, String eventName,
                         String eventLabelName, String eventDesc, boolean favorite)
            throws UserNotFoundException, DuplicateEventException;

    EventDSO updateEventName(String newName, int eventID) throws EventNotFoundException;

    EventDSO updateEventDescription(String desc, int eventID) throws EventNotFoundException,
            EventDescriptionException;

    EventDSO updateEventFinishTime(Calendar finishTime, int eventID) throws EventNotFoundException;

    EventDSO updateEventFavorite(boolean fav, int eventID, String userID) throws EventNotFoundException;

    EventDSO deleteEvent(int eventID) throws EventNotFoundException;

    EventDSO markEventAsDone(int userID, int eventID, boolean done) throws EventNotFoundException;

    boolean isDone(int eventID) throws EventNotFoundException;

    boolean isOverdue(int eventID) throws EventNotFoundException;

    int numEvents();
}
