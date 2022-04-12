package comp3350.timeSince.objects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import comp3350.timeSince.business.exceptions.EventDescriptionException;

public class EventDSO {

    private final int ID; // not null, positive integer
    private String eventName; // not null
    private final Calendar DATE_CREATED; // not null
    private String description;
    private Calendar targetFinishTime;
    private boolean isFavorite;
    private boolean isDone;
    private final List<EventLabelDSO> LABELS;

    //----------------------------------------
    // constructor
    //----------------------------------------

    public EventDSO(int id, Calendar creationTime, String name) {
        if (id >= 1) {
            this.ID = id;
        } else {
            this.ID = -1;
        }
        eventName = name;
        DATE_CREATED = creationTime;
        description = "";
        targetFinishTime = null;
        isFavorite = false;
        isDone = false;
        LABELS = new ArrayList<>();
    }

    //----------------------------------------
    // getters
    //----------------------------------------

    public int getID() {
        return ID;
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
        return Collections.unmodifiableList(LABELS);
    }

    //----------------------------------------
    // setters
    //----------------------------------------

    public void setName(String newName) {
        if (newName != null) {
            this.eventName = newName;
        }
    }

    /**
     * @param description the brief explanation of the event.
     * @throws EventDescriptionException the description can not be longer than 100 characters.
     */
    public void setDescription(String description) throws EventDescriptionException {
        if (description != null) {
            if (description.length() <= 100) {
                this.description = description;
            } else {
                throw new EventDescriptionException("The description must be less than 100 characters.");
            }
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

    /**
     * @return true if id >= 1 and the event name is not null; false otherwise
     */
    public boolean validate() {
        return (ID >= 1 && eventName != null);
    }

    /**
     * @param newDescription what to end to the end of the existing description
     * @throws EventDescriptionException the addition must not make the description longer than
     * 100 characters
     */
    public void appendDescription(String newDescription) throws EventDescriptionException {
        if (newDescription != null) {
            if (description.length() + newDescription.length() <= 100) {
                description += newDescription;
            } else {
                throw new EventDescriptionException("The description must be less than 100 characters.");
            }
        }
    }

    public void addLabel(EventLabelDSO eventLabelDSO) {
        if (eventLabelDSO != null && !LABELS.contains(eventLabelDSO)) {
            LABELS.add(eventLabelDSO);
        }
    }

    public void removeLabel(EventLabelDSO eventLabelDSO) {
        if (eventLabelDSO != null) {
            LABELS.remove(eventLabelDSO);
        }
    }

    @Override
    public String toString() {
        String toReturn = "No Named Event";
        if (eventName != null) {
            toReturn = String.format("Event Name: %s", eventName);
        }
        return toReturn;
    }

    /**
     * True if instanceof EventDSO, the id's match, and the name's match.
     *
     * @param other the object to be compared too (should be EventDSO)
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        boolean toReturn = false;

        if (other instanceof EventDSO) {
            toReturn = this.ID == ((EventDSO) other).getID()
                    && this.eventName.equals(((EventDSO) other).getName());
        }
        return toReturn;
    }

}
