package comp3350.timeSince.objects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import comp3350.timeSince.business.exceptions.EventDescriptionException;

public class EventDSO {

    private final int id; // not null, positive integer
    private String eventName; // not null
    private final Calendar DATE_CREATED; // not null
    private String description;

    private Calendar targetFinishTime;
    private boolean isFavorite; // not null
    private boolean isDone;
    private final List<EventLabelDSO> labels;

    //----------------------------------------
    // constructor
    //----------------------------------------

    public EventDSO(int id, Calendar creationTime, String name) {
        this.id = id;
        eventName = name;
        DATE_CREATED = creationTime;
        description = "";
        targetFinishTime = null;
        isFavorite = false;
        isDone = false;
        labels = new ArrayList<>();
    }

    //----------------------------------------
    // getters
    //----------------------------------------

    public int getID() {
        return this.id;
    }

    public String getName() {
        return eventName;
    }

    public Calendar getDateCreated() {
        return DATE_CREATED;
    }

    public String getDescription() {
        return description;
    }

    public Calendar getTargetFinishTime() {
        return targetFinishTime;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public boolean isDone() {
        return isDone;
    }

    public List<EventLabelDSO> getEventLabels() {
        return Collections.unmodifiableList(labels);
    }

    //----------------------------------------
    // setters
    //----------------------------------------

    public void setName(String newName) {
        this.eventName = newName;
    }

    public void setDescription(String description) throws EventDescriptionException {
        if (description.length() < 100) {
            this.description = description;
        } else {
            throw new EventDescriptionException("The description must be less than 100 characters.");
        }
    }

    public void setTargetFinishTime(Calendar target) {
        targetFinishTime = target;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    //----------------------------------------
    // general
    //----------------------------------------

    public boolean validate() {
        return (id >= 0 && eventName != null);
    }

    public void appendDescription(String newDescription) throws EventDescriptionException {
        if (description.length() + newDescription.length() < 100) {
            description += newDescription;
        } else {
            throw new EventDescriptionException("The description must be less than 100 characters.");
        }
    }

    public void addLabel(EventLabelDSO eventLabelDSO) {
        if (eventLabelDSO != null) {
            labels.add(eventLabelDSO);
        }
    }

    public void removeLabel(EventLabelDSO eventLabelDSO) {
        if (eventLabelDSO != null) {
            labels.remove(eventLabelDSO);
        }
    }

    public String toString() {
        String toReturn = "No Named Event";
        if (eventName != null) {
            toReturn = String.format("Event Name: %s", eventName);
        }
        return toReturn;
    }

    public boolean equals(EventDSO other) {
        return this.id == other.getID();
    }

}
