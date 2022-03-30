package comp3350.timeSince.objects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class EventDSO {

    private final int id;
    private String eventName;
    private final Calendar DATE_CREATED;
    private String description;

    private Calendar targetFinishTime;
    private boolean isFavorite;
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

    public List<EventLabelDSO> getEventLabels() {
        return Collections.unmodifiableList(labels);
    }

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

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

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
