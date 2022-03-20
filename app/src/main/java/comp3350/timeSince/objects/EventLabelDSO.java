/*
 * EventLabelDSO
 *
 * Remarks: Domain Specific Object for an Event Label
 */

package comp3350.timeSince.objects;

public class EventLabelDSO{
    //----------------------------------------
    // enums
    //----------------------------------------

    public enum Color{
        red,
        blue,
        green,
        yellow
    }

    //----------------------------------------
    // instance variables
    //----------------------------------------

    private int id;
    private String name;    // name of the Event Label
    private Color color;  // color of the Event Label

    //----------------------------------------
    // constructors
    //----------------------------------------

    public EventLabelDSO(String name){
        this.name = name;
        this.color = Color.blue; // setting the default to be blue
    }

    public EventLabelDSO(String name, Color color){
        this.name = name;
        this.color = color;
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

    public Color getColor(){
        return color;
    }

    //----------------------------------------
    // setters
    //----------------------------------------

    protected void setID(int id) {
        this.id = id;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public void setColor(Color newColor){
        this.color = newColor;
    }

    //----------------------------------------
    // general
    //----------------------------------------

    public String toString() {
        return String.format("#%s", name);
    }

    public boolean equals(EventLabelDSO other) {
        return this.name.equals(other.getName());
    }
}
