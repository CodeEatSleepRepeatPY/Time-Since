/*
 * UserDSO
 *
 * Remarks: Domain Specific Object for a User
 */

package comp3350.timeSince.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class UserDSO {

    //----------------------------------------
    // enums
    //----------------------------------------

    public enum MembershipType {
        free,
        paid
    }

    //----------------------------------------
    // instance variables
    //----------------------------------------

    private String id; // could be email, or randomly generated
    private String name;
    private final Date DATE_REGISTERED; // generated when creating new object
    private MembershipType membershipType; //TODO: remove this?
    private String passwordHash;
    private final List<EventDSO> userEvents;
    private final List<EventDSO> favoritesList; // favorite Events
    private final List<EventLabelDSO> userLabels;

    //----------------------------------------
    // constructor
    //----------------------------------------

    public UserDSO(String id, Date date, String passwordHash) {
        this.id = id;
        this.name = id; // defaults to the id
        this.DATE_REGISTERED = date;
        this.membershipType = MembershipType.free; // defaults to free
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

    public Date getDateRegistered() {
        return DATE_REGISTERED;
    }

    public MembershipType getMembershipType() {
        return membershipType;
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

    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
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
        return String.format("Name: %s, UserID: %s", name, id);
    }

    public boolean equals(UserDSO other) {
        return this.id.equals(other.getID());
    }
}
