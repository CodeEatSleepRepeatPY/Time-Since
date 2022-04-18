package comp3350.timeSince.persistence.fakes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;

public class EventPersistence implements IEventPersistence {

    private final List<EventDSO> EVENTS;
    private final IEventLabelPersistence eventLabelPersistence;
    private static int nextID;

    public EventPersistence() {
        this.EVENTS = new ArrayList<>();
        this.eventLabelPersistence = Services.getEventLabelPersistence(false);
        setDefaults();
        nextID = EVENTS.size(); // number of values in the database at creation
    }

    @Override
    public boolean eventExists(EventDSO event) {
        try {
            return getEventByID(event.getID()).equals(event);
        } catch (EventNotFoundException e) {
            return false;
        }
    }

    @Override
    public List<EventDSO> getEventList() {
        return Collections.unmodifiableList(EVENTS);
    }

    @Override
    public EventDSO getEventByID(int eventID) throws EventNotFoundException {
        for (int i = 0; i < EVENTS.size(); i++) {
            if (EVENTS.get(i).getID() == eventID) {
                return EVENTS.get(i);
            }
        } // else: event is not in the database
        throw new EventNotFoundException("The event: " + eventID + " could not be found.");
    }

    @Override
    public EventDSO insertEvent(EventDSO newEvent) throws DuplicateEventException {
        int index = EVENTS.indexOf(newEvent);
        if (index < 0) {
            EVENTS.add(newEvent);
            nextID++;
            return newEvent;
        } // else: already exists in the database
        throw new DuplicateEventException("The event: " + newEvent.getName() + " already exists.");
    }

    @Override
    public EventDSO updateEventName(EventDSO event, String newName) throws EventNotFoundException {
        EventDSO toReturn = null;
        if (event != null) {
            event.setName(newName);
            toReturn = updateEvent(event); // may throw an exception
        }
        return toReturn;
    }

    @Override
    public EventDSO updateEventDescription(EventDSO event, String newDescription) throws EventNotFoundException {
        EventDSO toReturn = null;
        if (event != null) {
            event.setDescription(newDescription);
            toReturn = updateEvent(event); // may throw an exception
        }
        return toReturn;
    }

    @Override
    public EventDSO updateEventFinishTime(EventDSO event, Calendar newDate) throws EventNotFoundException{
        EventDSO toReturn = null;
        if (event != null) {
            event.setTargetFinishTime(newDate);
            toReturn = updateEvent(event); // may throw an exception
        }
        return toReturn;
    }

    @Override
    public EventDSO updateEventStatus(EventDSO event, boolean isComplete) throws EventNotFoundException {
        EventDSO toReturn = null;
        if (event != null) {
            int index = EVENTS.indexOf(event);
            if (index >= 0) {
                event.setIsDone(isComplete);
                toReturn = updateEvent(event);
            } else {
                throw new EventNotFoundException("The event: " + event.getName() + " could not be found.");
            }
        }
        return toReturn;
    }

    @Override
    public EventDSO updateEventFavorite(EventDSO event, boolean isFavorite) throws EventNotFoundException {
        EventDSO toReturn = null;
        if (event != null) {
            int index = EVENTS.indexOf(event);
            if (index >= 0) {
                event.setFavorite(isFavorite);
                toReturn = updateEvent(event);
            } else {
                throw new EventNotFoundException("The event: " + event.getName() + " could not be found.");
            }
        }
        return toReturn;
    }

    @Override
    public EventDSO addLabel(EventDSO event, EventLabelDSO label) throws EventNotFoundException, EventLabelNotFoundException {
        EventDSO toReturn = null;
        if (event != null && label != null) {
            if (eventExists(event) && eventLabelPersistence.labelExists(label)) {
                event.addLabel(label);
                toReturn = updateEvent(event); // may throw an exception
            }
        }
        return toReturn;
    }

    @Override
    public EventDSO removeLabel(EventDSO event, EventLabelDSO label) throws EventNotFoundException, EventLabelNotFoundException {
        EventDSO toReturn = null;
        if (event != null && label != null) {
            if (eventExists(event) && eventLabelPersistence.labelExists(label)) {
                event.removeLabel(label);
                toReturn = updateEvent(event); // may throw an exception
            }
        }
        return toReturn;
    }

    private EventDSO updateEvent(EventDSO event) throws EventNotFoundException {
        int index = EVENTS.indexOf(event);
        if (index >= 0) {
            EVENTS.set(index, event);
            return event;
        } // event is not in the database
        throw new EventNotFoundException("The event: " + event.getName() + " could not be updated.");
    }

    @Override
    public EventDSO deleteEvent(EventDSO event) throws EventNotFoundException {
        int index = EVENTS.indexOf(event);
        if (index >= 0) {
            EVENTS.remove(index);
            return event;
        } // else: event is not in the database
        throw new EventNotFoundException("The event: " + event.getName() + " could not be deleted.");
    }

    @Override
    public int numEvents() {
        return EVENTS.size();
    }

    @Override
    public int getNextID() {
        return nextID + 1;
    }

    private void setDefaults() {
        Calendar defaultFinish = Calendar.getInstance();
        Calendar initialDate = Calendar.getInstance();
        initialDate.set(2022, 3, 27, 15, 0, 30);

        EventDSO event1 = new EventDSO(1, initialDate, "New Toothbrush");
        EventDSO event2 = new EventDSO(2, initialDate, "Wash Sheets");
        EventDSO event3 = new EventDSO(3, initialDate, "Clean Shower");
        EventDSO event4 = new EventDSO(4, initialDate, "Workout");
        EventDSO event5 = new EventDSO(5, initialDate, "Visit Doctor");
        EventDSO event6 = new EventDSO(6, initialDate, "Change Oil");
        EventDSO event7 = new EventDSO(7, initialDate, "Water Plants");
        EventDSO event8 = new EventDSO(8, initialDate, "Clean Floors");

        event1.setDescription("Change electric toothbrush head");
        event2.setDescription("Wash top and fitted sheets");
        event3.setDescription("Scrub shower walls");
        event5.setDescription("Regular doctor visit");
        event6.setDescription("Change car oil");

        defaultFinish.set(2022, 6, 27, 15, 0, 30);
        event1.setTargetFinishTime(defaultFinish);
        defaultFinish.set(2022, 4, 3, 15, 0,30);
        event2.setTargetFinishTime(defaultFinish);
        defaultFinish.set(2022, 3, 27, 15, 0, 30);
        event5.setTargetFinishTime(defaultFinish);
        defaultFinish.set(2022, 9, 22, 12, 30, 0);

        EVENTS.add(event1);
        EVENTS.add(event2);
        EVENTS.add(event3);
        EVENTS.add(event4);
        EVENTS.add(event5);
        EVENTS.add(event6);
        EVENTS.add(event7);
        EVENTS.add(event8);
    }

}
