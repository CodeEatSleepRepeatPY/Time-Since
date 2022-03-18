package comp3350.timeSince.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EventDSO {

    private int id;
    private String eventName;
    private final Date DATE_CREATED;
    private String description;

    private Date targetFinishTime;
    private boolean isFavorite;
    private List<EventLabelDSO> tags;

    //----------------------------------------
    // constructor
    //----------------------------------------

    public EventDSO(final String name) {
        id = -1;
        eventName = name;
        DATE_CREATED = new Date(System.currentTimeMillis());
        description = "";
        targetFinishTime = null;
        isFavorite = false;
        tags = new ArrayList<>();
    }

    //----------------------------------------
    // getters
    //----------------------------------------

    public int getID() {
        return this.id;
    }

    public String getName(){
        return eventName;
    }

    public Date getDateCreated() {
        return DATE_CREATED;
    }

    public String getDescription() {
        return description;
    }

    public Date getTargetFinishTime() {
        return targetFinishTime;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public List<EventLabelDSO> getEventTags(){
        return tags;
    }

    //----------------------------------------
    // setters
    //----------------------------------------

    public void setID(int id) {
        this.id = id;
    }

    public void setName(String newName) {
        this.eventName = newName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTargetFinishTime(Date target) {
        targetFinishTime = target;
    }

    public void setFavorite() {
        isFavorite = true;
    }

    public void unsetFavorite() {
        isFavorite = false;
    }

    //----------------------------------------
    // general
    //----------------------------------------

    public void appendDescription(String newDescription) {
        description += newDescription;
    }

    public boolean addTag(EventLabelDSO eventLabelDSO){
        boolean result = false;
        if( eventLabelDSO != null ){
            tags.add(eventLabelDSO);
            result = true;
        }
        return result;
    }

    public boolean removeTag(EventLabelDSO eventLabelDSO) {
        boolean result = false;
        if (eventLabelDSO != null && tags.contains(eventLabelDSO)) {
            tags.remove(eventLabelDSO);
            result = true;
        }
        return result;
    }

    public String toString() {
        return String.format("EventID: %d, Name: %s", id, eventName);
    }

    public boolean equals(EventDSO other) {
        return this.id == other.getID() && this.eventName.equals(other.getName());
    }

}
