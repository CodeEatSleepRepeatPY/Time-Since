package comp3350.timeSince.persistence.fakes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventDescriptionException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventPersistence;

public class EventPersistence implements IEventPersistence {

    private final List<EventDSO> eventList;
    private static int nextID;

    public EventPersistence() {
        this.eventList = new ArrayList<>();
        setDefaults();
        nextID = eventList.size(); // number of values in the database at creation
    }

    @Override
    public List<EventDSO> getEventList() {
        return Collections.unmodifiableList(eventList);
    }

    @Override
    public EventDSO getEventByID(int eventID) throws EventNotFoundException {
        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).getID() == eventID) {
                return eventList.get(i);
            }
        }
        throw new EventNotFoundException("The event: " + eventID + " could not be found.");
    }

    @Override
    public EventDSO insertEvent(EventDSO newEvent) throws DuplicateEventException {
        int index = eventList.indexOf(newEvent);
        if (index < 0) {
            eventList.add(newEvent);
            nextID++;
            return newEvent;
        } //else: already exists in database
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
    public EventDSO updateEventDescription(EventDSO event, String newDescription)
            throws EventNotFoundException, EventDescriptionException {
        EventDSO toReturn = null;
        if (event != null) {
            event.setDescription(newDescription); // will throw an exception if too long
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
    public EventDSO addLabel(EventDSO event, EventLabelDSO label) throws EventNotFoundException {
        EventDSO toReturn = null;
        if (event != null && label != null && event.validate() && label.validate()) {
            event.addLabel(label);
            toReturn = updateEvent(event); // may throw an exception
        }
        return toReturn;
    }

    @Override
    public EventDSO removeLabel(EventDSO event, EventLabelDSO label) throws EventNotFoundException {
        EventDSO toReturn = null;
        if (event != null && label != null) {
            event.removeLabel(label);
            toReturn = updateEvent(event); // may throw an exception
        }
        return toReturn;
    }

    private EventDSO updateEvent(EventDSO event) throws EventNotFoundException {
        int index = eventList.indexOf(event);
        if (index >= 0) {
            eventList.set(index, event);
            return event;
        }
        throw new EventNotFoundException("The event: " + event.getName() + " could not be updated.");
    }

    @Override
    public EventDSO deleteEvent(EventDSO event) throws EventNotFoundException {
        int index = eventList.indexOf(event);
        if (index >= 0) {
            eventList.remove(index);
            return event;
        } // else: event is not in list
        throw new EventNotFoundException("The event: " + event.getName() + " could not be deleted.");
    }

    @Override
    public int numEvents() {
        return eventList.size();
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

        eventList.add(event1);
        eventList.add(event2);
        eventList.add(event3);
        eventList.add(event4);
        eventList.add(event5);
        eventList.add(event6);
        eventList.add(event7);
        eventList.add(event8);
    }

}
