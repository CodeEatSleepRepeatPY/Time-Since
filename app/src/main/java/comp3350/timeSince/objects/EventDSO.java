package comp3350.timeSince.objects;

import static java.lang.String.*;

//import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EventDSO{

    private final String name;
    private final Date dataCreated;
    private String description;
    //private List<DSO.EventLabelDSO> tags;
    private long timeRemaining;
    private String favourite;

    public EventDSO(String name) {
        this.name = name;
        this.dataCreated = new Date(System.currentTimeMillis());
        description = "";
        //this.tags = new DSO.EventLabelDSO(null, null);
    }

    public String getName(){
        return name;
    }


    public Date getDataCreated() {
        return dataCreated;
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

    public String getFavourite(){
        return favourite;
    }

    public void setFavourite(String favourite){
        this.favourite = favourite;
    }

    //@SuppressLint("DefaultLocale")
    public long getTimeRemaining() {
        Date currentDateTime = new Date( System.currentTimeMillis() );
        long timeRemaining;
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

        //Calculate time distance in milliseconds
        timeRemaining = currentDateTime.getTime() - dataCreated.getTime();
        assert( timeRemaining >= 0);
        return timeRemaining;
    }


    public String getTimeRemainingHuman() {
        Date currentDateTime = new Date( System.currentTimeMillis() );
        long timeRemaining = getTimeRemaining();
        assert( timeRemaining >= 0 );
        long seconds, minutes, hours, days, years;
        String timeRemainingHuman;
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

        seconds = ( timeRemaining /1000L ) % 60;
        minutes = ( timeRemaining / (1000L * 60) ) % 60;
        hours   = ( timeRemaining / (1000L * 60 * 60) ) % 24;
        days    = ( timeRemaining / (1000L * 60 * 60 * 24) ) % 365;
        years   =  timeRemaining / (1000L * 60 * 60 * 24 * 365) ;

        timeRemainingHuman = String.format(
                "%d years, %d days, %d:%d:%d", years, days, hours, minutes, seconds);
        return timeRemainingHuman;
    }


    //public List<DSO.EventLabelDSO> getEventTags(){
    //    return tags;
    //}

    //public boolean addTag(DSO.EventLabelDSO eventLabelDSO){
    //    tags.add(eventLabelDSO);
    //    return false;
    //}
}
