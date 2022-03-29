/*
 * EventLabelDSO
 *
 * Remarks: Domain Specific Object for an Event Label
 */

package comp3350.timeSince.objects;

public class EventLabelDSO {
    //----------------------------------------
    // enums
    //----------------------------------------

    //TODO: Possibly change this?
    public enum Color {
        red,
        blue,
        green,
        yellow
    }

    //----------------------------------------
    // instance variables
    //----------------------------------------

    private final int id;
    private String name;    // name of the Event Label
    private Color color;  // color of the Event Label

    //----------------------------------------
    // constructors
    //----------------------------------------

    public EventLabelDSO(int id, String name) {
        this.id = id;
        this.name = name;
        this.color = Color.blue; // setting the default to be blue
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

    public Color getColor() {
        return color;
    }

    //----------------------------------------
    // setters
    //----------------------------------------

    public void setName(String newName) {
        this.name = newName;
    }

    public void setColor(Color newColor) {
        this.color = newColor;
    }

    //----------------------------------------
    // general
    //----------------------------------------

    public String toString() {
        return String.format("#%s", name);
    }

    public boolean equals(EventLabelDSO other) {
        return this.id == other.getID();
    }
}
