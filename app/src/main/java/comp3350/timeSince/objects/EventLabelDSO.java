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

    private String name;    // name of the Event Label
    private Color color;  // color of the Event Label

    //----------------------------------------
    // constructors
    //----------------------------------------

    public EventLabelDSO(String name){
        this.name = name;
        this.color = null; // will have to change this to be some default
    }

    public EventLabelDSO(String name, Color color){
        this.name = name;
        this.color = color;
    }

    //----------------------------------------
    // getters
    //----------------------------------------

    public String getName() {
        return name;
    }

    public Color getColor(){
        return color;
    }

    //----------------------------------------
    // setters
    //----------------------------------------

    public void setName(String newName){
        this.name = newName;
    }

    public void setColor(Color newColor){
        this.color = newColor;
    }
}
