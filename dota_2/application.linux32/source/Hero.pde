import java.util.Hashtable;

/*
http://www.gicentre.org/utils/#examples
*/

public class Hero implements Entity
{   
    public String name;
    public int level;
    public int gold;
    public int kills, deaths, assists;
    String team; 
    public color colour; 
    
    public Hero(){}
    
    public Hero(String name, String team)
    {
        this.name = name;
        this.level = 1; //heroes start out at level 1
        this.gold = 0;
        this.kills = 0;
        this.deaths = 0;
        this.assists = 0;
        this.team = team;
    }     
  
    public String getEntityName() { return name; }       
  
    public int getMaxKDA()
    {
        int max = kills;
        
        if(max < deaths)
            max = deaths;
        if(max < assists)
            max = assists;
        
        return max;
    }      
}
