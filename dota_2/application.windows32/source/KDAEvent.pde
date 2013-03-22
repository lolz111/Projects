public class KDAEvent extends Event
{           
    public String killer;
    public String dead;
    public ArrayList<String> assists;
    
    public KDAEvent(int time, String killer, String dead, ArrayList<String> assists)
    {
        this.time = time;
        this.killer = killer;
        this.dead = dead;
        this.assists = assists;
    }
  
    public void apply()
    {        
        set = 1;
        
        if(killer != null)
        {
            heroes.get(killer).kills += 1; //update hero
            teams.get(heroes.get(killer).team).kills += 1; //update team
        }
        
        heroes.get(dead).deaths += 1; //update hero
        teams.get(heroes.get(dead).team).deaths += 1; //update team
        
        for(String hero : assists)
        {
            heroes.get(hero).assists += 1; //update hero
            teams.get(heroes.get(hero).team).assists += 1; //update team
        }
        
        updateMaxHeroKDAValue();
    }
    
    public void undo()
    {
        set = 0;
        
        if(killer != null)
        {
            heroes.get(killer).kills -= 1; //update hero
            teams.get(heroes.get(killer).team).kills -= 1; //update team
        }
         
        heroes.get(dead).deaths -= 1; //update hero
        teams.get(heroes.get(dead).team).deaths -= 1; //update team
        
        for(String hero : assists)
        {
            heroes.get(hero).assists -= 1; //update hero
            teams.get(heroes.get(hero).team).assists -= 1; //update team
        }
        
        updateMaxHeroKDAValue();
    } 
   
    public void updateMaxHeroKDAValue() //update max value of k/d/a of all heroes in order to scale the bar charts
    {
        int max = 0;
        
        for(String hero : teams.get("radiant").teamHeroes)
        {
            if(max < heroes.get(hero).kills)
                max = heroes.get(hero).kills;
                
            if(max < heroes.get(hero).deaths)
                max = heroes.get(hero).deaths;
                
            if(max < heroes.get(hero).assists)
                max = heroes.get(hero).assists;           
        }
        
        for(String hero : teams.get("dire").teamHeroes)
        {
            if(max < heroes.get(hero).kills)
                max = heroes.get(hero).kills;
                
            if(max < heroes.get(hero).deaths)
                max = heroes.get(hero).deaths;
                
            if(max < heroes.get(hero).assists)
                max = heroes.get(hero).assists;          
        }
        
        maxHeroKDAValue = max;
    }  
}
