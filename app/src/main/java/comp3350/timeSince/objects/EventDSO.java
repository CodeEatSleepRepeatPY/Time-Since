package comp3350.timeSince.objects;
import java.util.*;

public class EventDSO{

    private final String NAME;
    private final Date DATE_CREATED;
    private String description;
    private List<EventLabelDSO> tags;
    private String favorite;
    private Date targetFinishTime;

    public EventDSO(final String name) {
        this.NAME = name;
        this.DATE_CREATED = new Date(System.currentTimeMillis());
        this.tags = new ArrayList<>();
        description = "";
        targetFinishTime = null;
    }

    public String getName(){
        return NAME;
    }

    public Date getDateCreated() {
        return DATE_CREATED;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void appendDescription(String newDescription){
        description += newDescription;
    }

    public String getFavorite(){
        return favorite;
    }

    public void setFavorite(String favorite){
        this.favorite = favorite;
    }

    public List<EventLabelDSO> getEventTags(){
        return tags;
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

}