import org.gicentre.utils.stat.*;

/*
http://www.openprocessing.org/sketch/49101#
http://processing.org/reference/textWidth_.html
*/

public class Graph implements Visual
{           
    public int boxX, boxY, boxW, boxH, boxColour;
    
    //lines
    public Hashtable<String, Line> lines;
    public ArrayList<String> lineNames;

    //options
    public boolean prevGold, prevKills, prevDeaths, prevAssists;
    public boolean gold, kills, deaths, assists;
    public boolean radiant, dire;        
    
    public Graph(int x, int y)
    {
        //box
        boxX = x;
        boxY = y;
        boxW = (w - outsideBorder - (w/4 - outsideBorder)) - (outsideBorder + w/4 - outsideBorder) - (2 * outsideBorder);               
        boxH = (2 * ((3*h)/4 - outsideBorder))/3;
        boxColour = color(30);           
    
        //line graph
        lines = new Hashtable<String, Line>();
        lineNames = new ArrayList<String>();
        checkOptions();
    }
    
    public void draw()
    {
        drawBox();
        drawAxes();
        checkOptions();
        updateMaxY();
        drawGraph();
    }
    
    public void drawBox()
    {
        pushMatrix();
        pushStyle();
        
        noStroke();              
        
        //border
        fill(color(55));
        rectMode(CORNER);
        rect(boxX - 8, boxY - 8, boxW + 16, boxH + 16);
        
        //box
        fill(boxColour);
        rectMode(CORNER);
        rect(boxX, boxY, boxW, boxH);   
        
        popStyle();
        popMatrix();        
    }
    
    public void drawAxes()
    {
        pushMatrix();
        pushStyle();
        
        strokeWeight(4);
        stroke(color(205));
        
        //y-axis
        line(boxX + outsideBorder, boxY + outsideBorder, boxX + outsideBorder, (boxY + outsideBorder) + (boxH - (2*outsideBorder)));
        
        //x-axis
        line(boxX + outsideBorder, (boxY + outsideBorder) + (boxH - (2*outsideBorder)), boxX + outsideBorder + (boxW - (2*outsideBorder)), (boxY + outsideBorder) + (boxH - (2*outsideBorder)));
        
        popStyle();
        popMatrix(); 
    }
    
    public void checkOptions()
    {
        checkTeams();
        checkGold();
        checkKills();  
        checkDeaths();   
        checkAssists();
        checkColors();   
    }
    
    public void checkTeams()
    {
        radiant = ((Controls)visuals.get(1)).radiantButtonToggle;
        dire = ((Controls)visuals.get(1)).direButtonToggle;
        
        if(!radiant)
        {          
            ArrayList<String> remove = new ArrayList<String>();
            for(int i = 0; i < lineNames.size(); i++)
            {
                if(lineNames.get(i).indexOf("radiant") != -1)
                    remove.add(lineNames.get(i));
            }

            for(String s : remove)
            {
                lines.remove(s);
                lineNames.remove(s);
            }
        }  
        if(!dire)
        {
            ArrayList<String> remove = new ArrayList<String>();
            for(int i = 0; i < lineNames.size(); i++)
            {
                if(lineNames.get(i).indexOf("dire") != -1)
                    remove.add(lineNames.get(i));
            }

            for(String s : remove)
            {
                lines.remove(s);
                lineNames.remove(s);
            }
        }      
    }
    
    public void checkColors()
    {
        for(String s : lineNames)
        {
            if(radiant && dire)
            {
                if(s.indexOf("radiant") != -1)                
                    lines.get(s).colour = ((Controls)visuals.get(1)).radiantColor;
                else if(s.indexOf("dire") != -1)
                    lines.get(s).colour = ((Controls)visuals.get(1)).direColor;
            }
            else
            {
                if(gold)
                {
                    if(s.indexOf("gold") != -1)
                        lines.get(s).colour = ((Controls)visuals.get(1)).goldColor;    
                }
                if(kills)
                {
                    if(s.indexOf("kills") != -1)
                        lines.get(s).colour = ((Controls)visuals.get(1)).killsColor;    
                }
                if(deaths)
                {
                    if(s.indexOf("deaths") != -1)
                        lines.get(s).colour = ((Controls)visuals.get(1)).deathsColor;
                }
                if(assists)
                {
                    if(s.indexOf("assists") != -1)
                        lines.get(s).colour = ((Controls)visuals.get(1)).assistsColor;
                }
            }
        }
    }

    public void checkKills()
    {
        kills = ((Controls)visuals.get(1)).killsButtonToggle;
        
        if(kills)
        {
            //team
            if(radiant)
            {
                if(lines.get("radiant_kills") == null)
                {                    
                    lines.put("radiant_kills", new Line(teams.get("radiant"), 
                                                       "kills", 
                                                       color(255), 
                                                       boxX + outsideBorder,
                                                       boxY + outsideBorder,
                                                       boxW - (2*outsideBorder),
                                                       boxH - (2*outsideBorder)));
                    lineNames.add("radiant_kills");
                }                
            }               
            if(dire)
            {
                if(lines.get("dire_kills") == null)
                {              
                    lines.put("dire_kills", new Line(teams.get("dire"), //entity
                                                    "kills", //type
                                                    color(255), //colour
                                                    boxX + outsideBorder, //x
                                                    boxY + outsideBorder, //y
                                                    boxW - (2*outsideBorder), //w
                                                    boxH - (2*outsideBorder))); //h
                    lineNames.add("dire_kills");
                }                
            }           
        }
        else
        {
            ArrayList<String> remove = new ArrayList<String>();
            for(int i = 0; i < lineNames.size(); i++)
            {
                if(lineNames.get(i).indexOf("kills") != -1)
                    remove.add(lineNames.get(i));
            }

            for(String s : remove)
            {
                lines.remove(s);
                lineNames.remove(s);
            }
        }
    }    
    
    public void checkDeaths()
    {
        deaths = ((Controls)visuals.get(1)).deathsButtonToggle;
        
        if(deaths)
        {
            //team
            if(radiant)
            {
                if(lines.get("radiant_deaths") == null)
                {                    
                    lines.put("radiant_deaths", new Line(teams.get("radiant"), 
                                                       "deaths", 
                                                       color(255), 
                                                       boxX + outsideBorder,
                                                       boxY + outsideBorder,
                                                       boxW - (2*outsideBorder),
                                                       boxH - (2*outsideBorder)));
                    lineNames.add("radiant_deaths");
                }
            }               
            if(dire)
            {
                if(lines.get("dire_deaths") == null)
                {              
                    lines.put("dire_deaths", new Line(teams.get("dire"), //entity
                                                    "deaths", //type
                                                    color(255), //colour
                                                    boxX + outsideBorder, //x
                                                    boxY + outsideBorder, //y
                                                    boxW - (2*outsideBorder), //w
                                                    boxH - (2*outsideBorder))); //h
                    lineNames.add("dire_deaths");
                }
            }              
        }
        else
        {
            ArrayList<String> remove = new ArrayList<String>();
            for(int i = 0; i < lineNames.size(); i++)
            {
                if(lineNames.get(i).indexOf("deaths") != -1)
                    remove.add(lineNames.get(i));
            }

            for(String s : remove)
            {
                lines.remove(s);
                lineNames.remove(s);
            }
        }
    }    
    
    public void checkAssists()
    {
        assists = ((Controls)visuals.get(1)).assistsButtonToggle;
        
        if(assists)
        {
            //team
            if(radiant)
            {
                if(lines.get("radiant_assists") == null)
                {                    
                    lines.put("radiant_assists", new Line(teams.get("radiant"), 
                                                       "assists", 
                                                       color(255), 
                                                       boxX + outsideBorder,
                                                       boxY + outsideBorder,
                                                       boxW - (2*outsideBorder),
                                                       boxH - (2*outsideBorder)));
                    lineNames.add("radiant_assists");
                }
            }               
            if(dire)
            {
                if(lines.get("dire_assists") == null)
                {              
                    lines.put("dire_assists", new Line(teams.get("dire"), //entity
                                                    "assists", //type
                                                    color(255), //colour
                                                    boxX + outsideBorder, //x
                                                    boxY + outsideBorder, //y
                                                    boxW - (2*outsideBorder), //w
                                                    boxH - (2*outsideBorder))); //h
                    lineNames.add("dire_assists");
                }
            }              
        }
        else
        {
            ArrayList<String> remove = new ArrayList<String>();
            for(int i = 0; i < lineNames.size(); i++)
            {
                if(lineNames.get(i).indexOf("assists") != -1)
                    remove.add(lineNames.get(i));
            }

            for(String s : remove)
            {
                lines.remove(s);
                lineNames.remove(s);
            }
        }
    }    
    
    public void checkGold()
    {
        gold = ((Controls)visuals.get(1)).goldButtonToggle;
        
        if(gold)
        {                
            //team
            if(radiant)
            {
                if(lines.get("radiant_gold") == null)
                {                    
                    lines.put("radiant_gold", new Line(teams.get("radiant"), 
                                                       "gold", 
                                                       color(255), 
                                                       boxX + outsideBorder,
                                                       boxY + outsideBorder,
                                                       boxW - (2*outsideBorder),
                                                       boxH - (2*outsideBorder)));
                    lineNames.add("radiant_gold");
                }
            }               
            if(dire)
            {
                if(lines.get("dire_gold") == null)
                {              
                    lines.put("dire_gold", new Line(teams.get("dire"), //entity
                                                    "gold", //type
                                                    color(255), //colour
                                                    boxX + outsideBorder, //x
                                                    boxY + outsideBorder, //y
                                                    boxW - (2*outsideBorder), //w
                                                    boxH - (2*outsideBorder))); //h
                    lineNames.add("dire_gold");
                }
            }                   
        }
        else //new toggle is false
        {                
            ArrayList<String> remove = new ArrayList<String>();
            for(int i = 0; i < lineNames.size(); i++)
            {
                if(lineNames.get(i).indexOf("gold") != -1)
                    remove.add(lineNames.get(i));
            }

            for(String s : remove)
            {
                lines.remove(s);
                lineNames.remove(s);
            }
        }        
    }    
    
    public void updateMaxY()
    {            
        if(gold)
        {
            int maxGold = 0;
            
            for(String s : lineNames)
            {
                if(s.indexOf("gold") != -1)
                {       
                    if(lines.get(s).maxY > maxGold)
                        maxGold = lines.get(s).getMaxY();
                }
            }
            
            for(String s : lineNames)
            {
                if(s.indexOf("gold") != -1)
                    lines.get(s).maxY = maxGold;
            }    
        }
        if(kills || deaths || assists)
        {
            int maxValue = 0;
            
            for(String s : lineNames)
            {
                if(s.indexOf("kills") != -1 || s.indexOf("deaths") != -1 || s.indexOf("assists") != -1)
                {
                    if(lines.get(s).maxY > maxValue)
                        maxValue = lines.get(s).getMaxY();
                }
            }
            
            for(String s : lineNames)
            {
                if(s.indexOf("kills") != -1 || s.indexOf("deaths") != -1 || s.indexOf("assists") != -1)                
                    lines.get(s).maxY = maxValue;
            }
        }
    }
    
    public void drawGraph()
    {        
        for(String s : lineNames)
        {
            lines.get(s).updateXYPoints();
            lines.get(s).draw();
        }
    }
    
    public int onTopOfVisual()
    {
        return 0;
    }
}
