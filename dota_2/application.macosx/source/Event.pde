public class Event
{   
    public int time;
    public int set; //1 == applied, 0 == not applied (aka undone); used to make sure events are applied and undone properly
    public int barSet;
    
    public void apply() {}
    public void undo() {}
}

