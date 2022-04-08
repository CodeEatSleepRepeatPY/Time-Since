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
    private int frequencyInYears;
    private int frequencyInMonths;
    private int frequencyInDays;
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
        frequencyInYears = -1;
        frequencyInMonths = -1;
        frequencyInDays = -1;
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

    public int[] getFrequency() {
        return new int[] {frequencyInYears, frequencyInMonths, frequencyInDays};
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

    // TODO: throw an exception???
    public void setFrequency(int years, int months, int days) {
        if (years >= 0) {
            frequencyInYears = years;
        }
        if (months >= 0 && months <= 12) {
            frequencyInMonths = months;
        }
        if (days >= 0 && days <= 31) {
            frequencyInDays = days;
        }
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
        return (id >= 1 && eventName != null);
    }

    public void appendDescription(String newDescription) throws EventDescriptionException {
        if (description.length() + newDescription.length() < 100) {
            description += newDescription;
        } else {
            throw new EventDescriptionException("The description must be less than 100 characters.");
        }
    }

    public void addLabel(EventLabelDSO eventLabelDSO) {
        if (eventLabelDSO != null && !labels.contains(eventLabelDSO)) {
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

    @Override
    public boolean equals(Object other) {
        boolean toReturn = false;

        if (other instanceof EventDSO) {
            toReturn = this.id == ((EventDSO) other).getID()
                    && this.eventName.equals(((EventDSO) other).getName());
        }
        return toReturn;
    }

}
