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

    private final int id; // not null, positive integer
    private String name; // not null - name of the Event Label

    //----------------------------------------
    // constructors
    //----------------------------------------

    public EventLabelDSO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    //----------------------------------------
    // getters
    //----------------------------------------

    public int getID() {
        return id;
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
        return (id >= 1 && name != null);
    }

    public String toString() {
        String toReturn = "#";
        if (name != null) {
            toReturn = String.format("#%s", name);
        }
        return toReturn;
    }

    public boolean equals(EventLabelDSO other) {
        return this.id == other.getID();
    }
}
