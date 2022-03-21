/*
 * UserDSO
 *
 * Remarks: Domain Specific Object for a User
 */

package comp3350.timeSince.objects;

import java.util.ArrayList;
import java.util.Date;

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
    private MembershipType membershipType;
    private String passwordHash;
    private ArrayList<EventDSO> userEvents;
    private ArrayList<EventDSO> favoritesList; // favorite Events
    private ArrayList<EventLabelDSO> userLabels;

    //----------------------------------------
    // constructor
    //----------------------------------------

    public UserDSO(String id, String passwordHash) {
        this.id = id;
        this.name = id; // defaults to the id
        this.DATE_REGISTERED = new Date(System.currentTimeMillis());
        this.membershipType = MembershipType.free;
        this.passwordHash = passwordHash;

        // initialize ArrayLists
        this.userLabels = new ArrayList<>();
        this.userEvents = new ArrayList<>();
        this.favoritesList = new ArrayList<>();
    }

    public UserDSO(String id, String name, Date DATE_REGISTERED, String membershipType, String passwordHash) {
        this.id = id;
        this.name = name;
        this.DATE_REGISTERED = DATE_REGISTERED;
        this.membershipType = MembershipType.valueOf(membershipType);
        this.passwordHash = passwordHash;
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

    public ArrayList<EventDSO> getUserEvents() {
        return userEvents;
    }

    public ArrayList<EventDSO> getFavoritesList() {
        return favoritesList;
    }

    public ArrayList<EventLabelDSO> getUserLabels() {
        return userLabels;
    }

    //----------------------------------------
    // setters
    //----------------------------------------

    /**
     * @param id
     * @deprecated We can have this functionality, but then we have to change
     * // how the database is stored.
     * <p>
     * TODO: Remove this method?
     */
    public void setID(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void addFavorite(EventDSO newFav) {
        if (newFav != null) {
            favoritesList.add(newFav);
        }
    }

    //----------------------------------------
    // general
    //----------------------------------------

    public String toString() {
        return String.format("Name: %s, UserID: %s", name, id);
    }

    public boolean equals(UserDSO other) {
        return this.id.equals(other.getID());
    }
}
