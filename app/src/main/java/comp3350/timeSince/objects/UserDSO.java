/*
 * UserDSO
 *
 * Remarks: Domain Specific Object for a User
 */

package comp3350.timeSince.objects;

import java.util.ArrayList;

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
    private String dateRegistered;
    private String passwordHash;
    private ArrayList<EventLabelDSO> userLabels;
    //private ArrayList<EventDSO> userEvents;
    //private ArrayList<EventDSO> favoritesList; // favorite Events

    //----------------------------------------
    // constructors
    //----------------------------------------

    public UserDSO(String name, MembershipType membershipType, String uuid,
                   String dateRegistered, String passwordHash){
        this.name = name;
        this.membershipType = membershipType;
        this.uuid = uuid;
        this.dateRegistered = dateRegistered;
        this.passwordHash = passwordHash;

        // initialize ArrayLists
        this.userLabels = new ArrayList<EventLabelDSO>();
        // this.userEvents = new ArrayList<EventDSO>();
        // this.favoritesList = new ArrayList<EventDSO>();
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

    public String getDateRegistered() {
        return dateRegistered;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public ArrayList<EventLabelDSO> getUserLabels() {
        return userLabels;
    }

//    public ArrayList<EventDSO> getUserEvents() {
//        return userEvents;
//    }
//
//    public ArrayList<EventDSO> getFavoritesList() {
//        return favoritesList;
//    }

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
