public class Team implements Entity
{   
    public String name;
    public int gold;
    public int kills, deaths, assists;    
    public ArrayList<String> teamHeroes;
    PImage image;
    
    public Team(){}
    
    public Team(String name)
    {
        this.teamHeroes = new ArrayList<String>();
        this.name = name;
        this.gold = 0;
        this.kills = 0;
        this.deaths = 0;
        this.assists = 0;
        this.image = loadImage(dataPath("miscimages/" + name + ".png"));
    } 

    public String getEntityName() { return name; }
}
