package comp3350.timeSince.objects;

/**
 * EventLabelDSO
 * <p>
 * Remarks: Domain Specific Object for an Event Label
 */
public class EventLabelDSO {

    //----------------------------------------
    // instance variables
    //----------------------------------------

    private final int ID; // not null, positive integer
    private String name; // not null - name of the Event Label

    //----------------------------------------
    // constructors
    //----------------------------------------

    public EventLabelDSO(int id, String name) {
        if (id >= 1) {
            this.ID = id;
        } else {
            this.ID = -1;
        }
        this.name = name;
    }

    //----------------------------------------
    // getters
    //----------------------------------------

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    //----------------------------------------
    // setters
    //----------------------------------------

    public void setName(String newName) {
        if (newName != null) {
            name = newName;
        }
    }

    //----------------------------------------
    // general
    //----------------------------------------

    /**
     * @return true if id >= 1 and the name is not null; false otherwise
     */
    public boolean validate() {
        return (ID >= 1 && name != null);
    }

    @Override
    public String toString() {
        String toReturn = "#";
        if (name != null) {
            toReturn = String.format("#%s", name);
        }
        return toReturn;
    }

    /**
     * @param other the object to be compared too (EventLabelDSO)
     * @return true if instanceof EventLabelDSO, id's match, and name's match.
     */
    @Override
    public boolean equals(Object other) {
        boolean toReturn = false;
        if (other instanceof EventLabelDSO) {
            toReturn = this.ID == ((EventLabelDSO) other).getID()
                    && this.name.equals(((EventLabelDSO) other).getName());
        }
        return toReturn;
    }

}
