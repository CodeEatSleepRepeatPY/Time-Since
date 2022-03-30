/*
 * UserDSO
 *
 * Remarks: Domain Specific Object for a User
 */

package comp3350.timeSince.objects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class UserDSO {

    //----------------------------------------
    // instance variables
    //----------------------------------------

    private final String id; // could be email, or unique name
    private String name;
    private final Calendar DATE_REGISTERED; // generated when creating new object
    private String passwordHash;
    private final List<EventDSO> userEvents;
    private final List<EventDSO> favoritesList; // favorite Events
    private final List<EventLabelDSO> userLabels;

    //----------------------------------------
    // constructor
    //----------------------------------------

    public UserDSO(String id, Calendar date, String passwordHash) {
        this.id = id;
        this.name = id; // defaults to the id
        this.DATE_REGISTERED = date;
        this.passwordHash = passwordHash;

        // initialize ArrayLists
        this.userLabels = new ArrayList<>();
        this.userEvents = new ArrayList<>();
        this.favoritesList = new ArrayList<>();
    }

    //----------------------------------------
    // getters
    //----------------------------------------

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Calendar getDateRegistered() {
        return DATE_REGISTERED;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public List<EventDSO> getUserEvents() {
        return Collections.unmodifiableList(userEvents);
    }

    public List<EventDSO> getFavoritesList() {
        return Collections.unmodifiableList(favoritesList);
    }

    public List<EventLabelDSO> getUserLabels() {
        return Collections.unmodifiableList(userLabels);
    }

    //----------------------------------------
    // setters
    //----------------------------------------

    public void setName(String name) {
        this.name = name;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }


    //----------------------------------------
    // general
    //----------------------------------------

    public void addLabel(EventLabelDSO newLabel) {
        if (newLabel != null && !userLabels.contains(newLabel)) {
            userLabels.add(newLabel);
        }
    }

    public void removeLabel(EventLabelDSO label) {
        userLabels.remove(label);
    }

    public void addEvent(EventDSO newEvent) {
        if (newEvent != null && !userEvents.contains(newEvent)) {
            userEvents.add(newEvent);
        }
    }

    public void removeEvent(EventDSO event) {
        userEvents.remove(event);
    }

    public void addFavorite(EventDSO newFav) {
        if (newFav != null && !favoritesList.contains(newFav)) {
            favoritesList.add(newFav);
        }
    }

    public void removeFavorite(EventDSO event) {
        favoritesList.remove(event);
    }

    public String toString() {
        String toReturn = "";
        if (name != null && id != null) {
            toReturn = String.format("Name: %s, UserID: %s", name, id);
        }
        if (name == null && id != null) {
            toReturn = String.format("UserID: %s", id);
        }
        return toReturn;
    }

    public boolean equals(UserDSO other) {
        return this.id.equals(other.getID());
    }
}
