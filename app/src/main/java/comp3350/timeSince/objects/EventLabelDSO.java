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
        this.ID = id >= 1 ? id : -1;
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

    /**
     * @param newName NonNull
     */
    public void setName(String newName) {
        if (newName != null) {
            name = newName;
        }
    }

    //----------------------------------------
    // general
    //----------------------------------------

    /**
     * @return true if id >= 1 and the name is at least one character long; false otherwise
     */
    public boolean validate() {
        return (ID >= 1 && name != null && name.length() >= 1);
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
