/*
 * UserDSO
 *
 * Remarks: Domain Specific Object for a User
 */

package comp3350.timeSince.objects;

import java.util.ArrayList;
import java.util.Date;

public class UserDSO{
    //----------------------------------------
    // enums
    //----------------------------------------

    public enum MembershipType{
        free,
        paid
    }

    //----------------------------------------
    // instance variables
    //----------------------------------------

    private String name;
    private MembershipType membershipType;
    private String uuid; // could be email, or randomly generated
    private final Date DATE_REGISTERED; // generated when creating new object
    private String passwordHash;
    private ArrayList<EventLabelDSO> userLabels;
    private ArrayList<EventDSO> userEvents;
    private ArrayList<EventDSO> favoritesList; // favorite Events

    //----------------------------------------
    // constructors
    //----------------------------------------

    public UserDSO(String uuid, MembershipType membershipType,
                   String passwordHash){
        this.name = uuid; // defaults to the uuid
        this.membershipType = membershipType;
        this.uuid = uuid;
        this.DATE_REGISTERED = new Date(System.currentTimeMillis());
        this.passwordHash = passwordHash;

        // initialize ArrayLists
        this.userLabels = new ArrayList<>();
        this.userEvents = new ArrayList<>();
        this.favoritesList = new ArrayList<>();
    }

    //----------------------------------------
    // getters
    //----------------------------------------

    public String getName() {
        return name;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public String getUuid() {
        return uuid;
    }

    public Date getDateRegistered() {
        return DATE_REGISTERED;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public ArrayList<EventLabelDSO> getUserLabels() {
        return userLabels;
    }

    public ArrayList<EventDSO> getUserEvents() {
        return userEvents;
    }

    public ArrayList<EventDSO> getFavoritesList() {
        return favoritesList;
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

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
