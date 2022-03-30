package comp3350.timeSince.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Calendar;

public class EventDSO {

    private final int id;
    private String eventName;
    private final Calendar DATE_CREATED;
    private String description;

    private Calendar targetFinishTime;
    private int frequency; //TODO: This should probably change to a different format?
    private boolean isFavorite;
    private final List<EventLabelDSO> labels;

    private boolean eventIsDone;

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
        labels = new ArrayList<>();
        eventIsDone = false;
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

    public int getFrequency() {
        return frequency;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public List<EventLabelDSO> getEventLabels() {
        return Collections.unmodifiableList(labels);
    }

    public boolean getEventIsDone(){ return eventIsDone; }

    //----------------------------------------
    // setters
    //----------------------------------------

    public void setName(String newName) {
        this.eventName = newName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTargetFinishTime(Calendar target) {
        targetFinishTime = target;
    }

    public void setFrequency(int frequency) {
        if (frequency > 0) {
            this.frequency = frequency;
        }
        // TODO: throw an exception?
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public void setEventIsDone(boolean isDone){ eventIsDone = isDone; }

    //----------------------------------------
    // general
    //----------------------------------------

    public void appendDescription(String newDescription) {
        description += newDescription;
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
        return String.format("EventID: %d, Name: %s", id, eventName);
    }

    public boolean equals(EventDSO other) {
        return this.id == other.getID();
    }

    public boolean validName(){
        return (eventName != null);
    }
}
