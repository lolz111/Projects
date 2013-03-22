public class GoldEvent extends Event
{           
    public String hero;  
    public int gold;           

    public GoldEvent(int time, String hero, int gold)
    {
        this.time = time;
        this.hero = hero;
        this.gold = gold;
    }
  
    public void apply()
    {
        set = 1;        
        heroes.get(hero).gold += gold; //update hero
        teams.get(heroes.get(hero).team).gold += gold; //update team       
    }
    
    public void undo()
    {
        set = 0;        
        heroes.get(hero).gold -= gold; //update hero
        teams.get(heroes.get(hero).team).gold -= gold; //update team
    }  
}
