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
    private String color;

    //----------------------------------------
    // constructors
    //----------------------------------------

    public EventLabelDSO(int id, String name) {
        this.id = id;
        this.name = name;
        color = null;
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

    public String getColor() {
        return color;
    }

    //----------------------------------------
    // setters
    //----------------------------------------

    public void setName(String newName) {
        name = newName;
    }

    public void setColor(String color) {
        this.color = color;
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

    @Override
    public boolean equals(Object other) {
        boolean toReturn = false;

        if (other instanceof EventLabelDSO) {
            toReturn = this.id == ((EventLabelDSO) other).getID()
                    && this.name.equals(((EventLabelDSO) other).getName());
        }
        return toReturn;
    }
}
