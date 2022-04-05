/*
 * EventLabelDSO
 *
 * Remarks: Domain Specific Object for an Event Label
 */

package comp3350.timeSince.objects;

public class EventLabelDSO {

    //----------------------------------------
    // instance variables
    //----------------------------------------

    private final String userID;
    private String name; // not null - name of the Event Label

    //----------------------------------------
    // constructors
    //----------------------------------------

    public EventLabelDSO(String userID, String name) {
        this.userID = userID;
        this.name = name;
    }

    //----------------------------------------
    // getters
    //----------------------------------------

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    //----------------------------------------
    // setters
    //----------------------------------------

    public void setName(String newName) {
        name = newName;
    }

    //----------------------------------------
    // general
    //----------------------------------------

    public boolean validate() {
        return (userID != null && userID.length() > 0
                && name != null && name.length() > 0);
    }

    public String toString() {
        String toReturn = "#";
        if (name != null) {
            toReturn = String.format("#%s", name);
        }
        return toReturn;
    }

    public boolean equals(EventLabelDSO other) {
        return (userID.equals(other.getUserID())
                && name.equals(other.getName()));
    }
}
