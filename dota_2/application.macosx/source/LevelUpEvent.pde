public class LevelUpEvent extends Event
{           
    public String hero;
    public int previousLevel;  
    public int newLevel;           

    public LevelUpEvent(int time, String hero, int level)
    {
        this.time = time;
        this.hero = hero;
        this.newLevel = level;
    }
  
    public void apply()
    {
        set = 1;        
        previousLevel = heroes.get(hero).level;
        heroes.get(hero).level = newLevel; //update hero level
    }
    
    public void undo()
    {
        set = 0;        
        heroes.get(hero).level = previousLevel; //change level back to previous level
    }  
}
