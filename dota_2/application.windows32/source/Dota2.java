import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Collections; 
import java.util.ArrayList; 
import java.util.Comparator; 
import java.util.Hashtable; 
import java.awt.event.MouseEvent; 
import java.io.FileNotFoundException; 
import java.io.FileReader; 
import java.io.IOException; 
import java.util.Iterator; 
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.JSONParser; 
import org.json.simple.parser.ParseException; 
import java.util.Scanner; 
import java.io.FileInputStream; 
import java.util.StringTokenizer; 
import org.gicentre.utils.stat.*; 
import java.util.Hashtable; 

import org.gicentre.utils.colour.*; 
import org.gicentre.utils.io.*; 
import org.gicentre.utils.gui.*; 
import org.gicentre.utils.move.*; 
import org.gicentre.utils.multisketch.*; 
import org.gicentre.utils.stat.*; 
import org.json.simple.*; 
import org.gicentre.utils.*; 
import org.gicentre.utils.network.*; 
import org.gicentre.utils.spatial.*; 
import org.json.simple.parser.*; 
import org.gicentre.utils.geom.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Dota2 extends PApplet {







/*
http://www.cyborgmatt.com/2013/01/dota-2-replay-parser-bruno/
http://processing.org/learning/transform2d/
http://processing.org/reference/
http://wiki.processing.org/w/Wheel_mouse
http://forum.processing.org/tag/scroll
http://processing.org/learning/topics/zoom.html
http://wiki.processing.org/w/Double-click
http://processing.org/learning/topics/chain.html
http://processing.org/learning/topics/springs.html
http://processing.org/learning/topics/bouncybubbles.html
*/

public int w;
public int h;
public int outsideBorder;

public Hashtable<String, Team> teams;
public Hashtable<String, Hero> heroes;
public Hashtable<String, String> longToShortHeroNames;
public Hashtable<String, String> shortToLongHeroNames;

public ArrayList<Event> events;
public int currentEventPosition;
public int maxGameTime;
public int maxHeroKDAValue;

//time
public boolean ticking;
public long startingTime;

public ArrayList<Visual> visuals;    
public PFont optimusPrincepsFont;

public void setup()
{
    w = displayWidth;
    h = displayHeight;
    outsideBorder = h/15 - h/14/2;

    size(w, h);
    background(0);         
    
    //initialize teams
    teams = new Hashtable<String, Team>();
    teams.put("radiant", new Team("radiant"));
    teams.put("dire", new Team("dire"));
    
    //initialize hero structures
    heroes = new Hashtable<String, Hero>();
    longToShortHeroNames = new Hashtable<String, String>();
    shortToLongHeroNames = new Hashtable<String, String>();
    events = new ArrayList<Event>();
    
    //read in data and sort it
    FileReaders fr = new FileReaders();
    fr.readAllFiles();
    
    Collections.sort(events, new Comparator<Event>() {
        public int compare(Event a, Event b)
        {
            if(a.time < b.time)
                return -1;
            else if(b.time < a.time)
                return 1;
            else
                return 0;
        }            
    });
    
    //events setup
    currentEventPosition = 0;
    maxHeroKDAValue = 0;        
    maxGameTime = events.get(events.size() - 1).time;
    
    //time setup
    ticking = false;
    startingTime = System.currentTimeMillis() / 1000;
    
    //load font
    optimusPrincepsFont = loadFont("OptimusPrinceps-70.vlw");
    
    //initialize visuals  
    visuals = new ArrayList<Visual>();  
    
    //game clock (0)
    visuals.add(new Clock(w/2 - (w/4 - outsideBorder)/4, //x
                          h/12));
    
    //scrollbar (1)
    visuals.add(new Controls(outsideBorder + w/4, //x
                              h/4)); //y

    //radiant panel (2)
    visuals.add(new TeamPanel(outsideBorder, //x
                              h/4, //y
                              "radiant")); //team
    //dire panel (3)
    visuals.add(new TeamPanel(w - outsideBorder - (w/4 - outsideBorder), //x
                              h/4, //y
                              "dire")); //team                             
    //graph (4)
    visuals.add(new Graph(outsideBorder + w/4, //x
                          h/4 + ((3*h)/4 - outsideBorder)/3)); //y                                       
}

public void draw()
{       
    background(0);

    for(Visual v : visuals)
    {
        v.draw();
    }
    
    if(ticking)
    {
        ((Controls)visuals.get(1)).advanceTime();
    }
    
    for(Visual v : visuals)
    {
        if(v.onTopOfVisual() != 0) //clicking on something
        {
            if(v instanceof TeamPanel)
            {
                ((TeamPanel)v).hoverOverHero(v.onTopOfVisual());
            }
        }
    }    
}

public Visual lockedVisual;

public void mousePressed()
{
    for(Visual v : visuals)
    {
        if(v.onTopOfVisual() != 0) //clicking on something
        {   
            if(v.onTopOfVisual() == 1)
            {
                //scrollbar
                if(v instanceof Controls)
                {
                    lockedVisual = v; //lock the visual to be dragged
                    ((Controls)v).manualScrollBarUpdate(mouseX); //if the scrollbar is clicked, update the position of the slider to that place
                }
                else if(v instanceof TeamPanel)
                {
                    if(mouseEvent.getClickCount() == 2) //2 clicks - change to itemsview                    
                        ((TeamPanel)v).doubleClickedHero(1);
                    else
                        ((TeamPanel)v).singleClicked(1);
                }
            }
            else if(v.onTopOfVisual() == 2)
            {
                //scrollbar
                if(v instanceof Controls)
                {
                    lockedVisual = v; //lock the visual to be dragged
                    ((Controls)v).manualScrollBarUpdate(mouseX); //if the scrollbar is clicked, update the position of the slider to that place
                }
                else if(v instanceof TeamPanel)
                {
                    if(mouseEvent.getClickCount() == 2) //2 clicks - change to itemsview
                        ((TeamPanel)v).doubleClickedHero(2);
                    else
                        ((TeamPanel)v).singleClicked(2);
                }
            }
            else if(v.onTopOfVisual() == 3)
            {
                //play/pause button
                if(v instanceof Controls)
                {                    
                    ((Controls)v).clickedPlayPauseButton(); //if the scrollbar is clicked, update the position of the slider to that place
                }
                else if(v instanceof TeamPanel)
                {
                    if(mouseEvent.getClickCount() == 2) //2 clicks - change to itemsview
                        ((TeamPanel)v).doubleClickedHero(3);
                    else
                        ((TeamPanel)v).singleClicked(3);
                }
            }
            else if(v.onTopOfVisual() == 4)
            {
                //gold button
                if(v instanceof Controls)
                {                    
                    ((Controls)v).clickedGoldButton(); //if the scrollbar is clicked, update the position of the slider to that place
                }
                else if(v instanceof TeamPanel)
                {
                    if(mouseEvent.getClickCount() == 2) //2 clicks - change to itemsview
                        ((TeamPanel)v).doubleClickedHero(4);
                    else
                        ((TeamPanel)v).singleClicked(4);
                }
            }
            else if(v.onTopOfVisual() == 5)
            {
                //kills button
                if(v instanceof Controls)
                {                    
                    ((Controls)v).clickedKillsButton(); //if the scrollbar is clicked, update the position of the slider to that place
                }
                else if(v instanceof TeamPanel)
                {
                    if(mouseEvent.getClickCount() == 2) //2 clicks - change to itemsview
                        ((TeamPanel)v).doubleClickedHero(5);
                    else
                        ((TeamPanel)v).singleClicked(5);
                }
            }
            else if(v.onTopOfVisual() == 6)
            {
                //deaths button
                if(v instanceof Controls)
                    ((Controls)v).clickedDeathsButton(); //if the scrollbar is clicked, update the position of the slider to that place
            }
            else if(v.onTopOfVisual() == 7)
            {
                //assists button
                if(v instanceof Controls)
                    ((Controls)v).clickedAssistsButton(); //if the scrollbar is clicked, update the position of the slider to that place
            }
            else if(v.onTopOfVisual() == 8)
            {
                //radiant button
                if(v instanceof Controls)
                    ((Controls)v).clickedRadiantButton(); //if the scrollbar is clicked, update the position of the slider to that place
            }
            else if(v.onTopOfVisual() == 9)
            {
                //dire button
                if(v instanceof Controls)
                    ((Controls)v).clickedDireButton(); //if the scrollbar is clicked, update the position of the slider to that place
            }
                        
            break; //break so that the item clicked doesn't get set to null if there are more things in the visuals array
        }
        else
            lockedVisual = null;
    }
}

public void mouseDragged()
{
    //if a visual was clicked before, lockedVisual will contain that visual
    if(lockedVisual != null)
    {        
        //if it's a scrollbar, only the slider will move which is why that's the only thing that is update
        if(lockedVisual instanceof Controls)
            ((Controls)lockedVisual).manualScrollBarUpdate(mouseX);
    }
    else
    {
//        System.out.println("Nothing selected");
    }
}
public class Clock implements Visual
{   
    public int boxX, boxY, boxW, boxH, boxColour;
    public int timeX, timeY, timeW, timeH;
    public int minutes, seconds, timeColour; 
    public float gameTimeInSecs, speed;
    
    public Clock(int x, int y)
    {
        //box
        boxX = x;
        boxY = y;
        boxW = (w/4 - outsideBorder)/2;               
        boxH = (3 * ((((3*h)/4 - outsideBorder) - (2*outsideBorder))/5))/4;
        boxColour = color(30);   
    
        //time
        minutes = 0;
        seconds = 0;
        timeColour = color(255); 
        gameTimeInSecs = 0; 
        speed = 32;
    
        //time box
        timeX = (boxX + (boxW/2)) - (boxW - (outsideBorder/2))/2;
        timeY = (boxY + (2*boxH)/3) - (((2*boxH)/3) - (outsideBorder/4))/2;
        timeW = boxW - (outsideBorder/2);
        timeH = ((2*boxH)/3) - (outsideBorder/4);      
    }

    public void draw()
    {
        drawOuterBox();
        drawMatchTimeText();
        drawInnerBox();
        drawTime();
    }
  
    public void drawOuterBox()
    {
        pushMatrix();
        pushStyle();
        
        //border
        fill(color(55));
        rectMode(CORNER);
        rect(boxX - 8, boxY - 8, boxW + 16, boxH + 16);
        
        //outer box
        noStroke();
        fill(boxColour);
        rectMode(CORNER);
        rect(boxX, boxY, boxW, boxH);
        
        popStyle();
        popMatrix();
    }

  public void drawMatchTimeText()
    {
        pushMatrix();
        pushStyle();                
    
        //"Match Text" text      
        int fontsize = boxH/3 - 5;
        
        if ((double)(boxW - outsideBorder/2)/(boxH/3) < 5.0f)
            fontsize = ((boxW - outsideBorder/2) - (boxH/3))/((boxW - outsideBorder/2)/(boxH/3)) - 5;
            
        fill(timeColour); 
        textFont(optimusPrincepsFont, fontsize);
        textAlign(CENTER, CENTER);       
        text("Match Time", boxX + boxW/2, boxY + boxH/6);        
    
        popStyle();
        popMatrix();
    }   
  
    public void drawInnerBox()
    {
        pushMatrix();
        pushStyle();
        
        noStroke();
        fill(color(40));
        rectMode(CENTER);
        rect(boxX + boxW/2, boxY + (2*boxH)/3, boxW - outsideBorder/2, (2*boxH)/3 - outsideBorder/4);
        
        popStyle();
        popMatrix();
    }

    public void drawTime()
    {
        pushMatrix();
        pushStyle();
    
        //adjust fontsize if needed    
        int fontsize = timeH - 5;
        
        if ((double)timeW/timeH < 2.0f)
            fontsize = (timeW - timeH)/(timeW/timeH);
    
        //setup customizations
        fill(timeColour);
        textFont(optimusPrincepsFont, fontsize);
        textAlign(CENTER, CENTER); //originally textAlign(CENTER);
        
        //add a "0" if the number of seconds is less than 10 (ie 0:04 instead of 0:4)
        String secs;
        if (seconds < 10)
          secs = "0" + seconds;
        else
          secs = "" + seconds;
    
        text(minutes + " : " + secs, boxX + boxW/2, boxY + (2*boxH)/3);
    
        popStyle();
        popMatrix();      
    }
    
    public void update(float newTimeInSecs)
    {
        gameTimeInSecs = newTimeInSecs;
        gameTimeInSecs = constrain(gameTimeInSecs, 0, maxGameTime/30);
        
        //update minutes and seconds of the clock
        minutes = (int)gameTimeInSecs / 60;
        seconds = (int)(gameTimeInSecs % 60);
    }

    public int onTopOfVisual()
    {
        return 0;
    }
}

public class Controls implements Visual 
{   
    //scrollbar
    public int barX, barY, barW, barH;
    public int sliderX, sliderY, sliderW, sliderH, sliderXMin, sliderXMax, sliderYMinMax;      
  
    //play/pause button
    public int playPauseButtonX, playPauseButtonY, playPauseButtonW, playPauseButtonH, playPauseSymbolWH;
    
    //speed
    public float speed;
    
    //gold button
    PImage gold;
    public int goldButtonX, goldButtonY, goldButtonW, goldButtonH;
    public boolean goldButtonToggle;
    public int goldColor;
    
    //kills button
    PImage kills;
    public int killsButtonX, killsButtonY, killsButtonW, killsButtonH;
    public boolean killsButtonToggle;
    public int killsColor;
    
    //deaths button
    PImage deaths;
    public int deathsButtonX, deathsButtonY, deathsButtonW, deathsButtonH;
    public boolean deathsButtonToggle;
    public int deathsColor;
    
    //assists button
    PImage assists;
    public int assistsButtonX, assistsButtonY, assistsButtonW, assistsButtonH;
    public boolean assistsButtonToggle;
    public int assistsColor;
    
    //radiant button
//    PImage radiant;
    public int radiantButtonX, radiantButtonY, radiantButtonW, radiantButtonH;
    public boolean radiantButtonToggle;
    public int radiantColor;
    
    //dire button
//    PImage dire;
    public int direButtonX, direButtonY, direButtonW, direButtonH;
    public boolean direButtonToggle;
    public int direColor;
    
    public Controls(int x, int y)
    {        
        //bar
        barX = x;
        barY = y;
        barW = (w - outsideBorder - (w/4 - outsideBorder)) - (outsideBorder + w/4 - outsideBorder) - (2 * outsideBorder);
        barH = (2*outsideBorder)/3;
        
        //slider
        sliderW = outsideBorder;
        sliderH = outsideBorder;
        sliderX = barX + outsideBorder/2;
        sliderY = barY + outsideBorder/3;
        sliderXMin = barX + outsideBorder/2;
        sliderXMax = barX + barW - outsideBorder/2;
        sliderYMinMax = barY + outsideBorder/3;

        //speed
        speed = 32;
        
        //play/pause button
        playPauseButtonX = w/4 + outsideBorder;
        playPauseButtonY = h/4 + outsideBorder + outsideBorder/4;
        playPauseButtonW = 2 * outsideBorder;
        playPauseButtonH = outsideBorder;
        playPauseSymbolWH = outsideBorder;
  
        //gold button
        gold = loadImage(dataPath("miscimages/gold.png"));
        goldButtonX = w/4 + outsideBorder + (3*outsideBorder)/4;
        goldButtonY = h/3 + outsideBorder/2; 
        goldButtonW = (w/2)/6 - outsideBorder; 
        goldButtonH = (w/2)/6 - outsideBorder;
        goldButtonToggle = false;
        goldColor = color(207, 161, 9);
        
        //kills button
        kills = loadImage(dataPath("miscimages/kills.png"));
        killsButtonX = goldButtonX + goldButtonW + outsideBorder/2;
        killsButtonY = h/3 + outsideBorder/2;
        killsButtonW = (w/2)/6 - outsideBorder; 
        killsButtonH = (w/2)/6 - outsideBorder;
        killsButtonToggle = false;
        killsColor = color(44, 131, 9);
        
        //deaths button
        deaths = loadImage(dataPath("miscimages/deaths2.png"));
        deathsButtonX = killsButtonX + killsButtonW + outsideBorder/2;
        deathsButtonY = h/3 + outsideBorder/2;
        deathsButtonW = (w/2)/6 - outsideBorder; 
        deathsButtonH = (w/2)/6 - outsideBorder;
        deathsButtonToggle = false;
        deathsColor = color(157, 6, 16);
        
        //assists button
        assists = loadImage(dataPath("miscimages/assists.png"));
        assistsButtonX = deathsButtonX + deathsButtonW + outsideBorder/2;
        assistsButtonY = h/3 + outsideBorder/2;
        assistsButtonW = (w/2)/6 - outsideBorder; 
        assistsButtonH = (w/2)/6 - outsideBorder;
        assistsButtonToggle = false;
        assistsColor = color(8, 77, 131);
        
        //radiant button
        radiantButtonX = assistsButtonX + assistsButtonW + outsideBorder/2;
        radiantButtonY = h/3 + outsideBorder/2;
//        radiantButtonY = h/3 - (outsideBorder)/2;
        radiantButtonW = (w/2)/6 - outsideBorder; 
        radiantButtonH = (w/2)/6 - outsideBorder; 
        radiantButtonToggle = false;
        radiantColor = color(99, 102, 51);
        
        //dire button 
        direButtonX = radiantButtonX + radiantButtonW + outsideBorder/2;
        direButtonY = h/3 + outsideBorder/2;
//        direButtonY = h/3 + (3*outsideBorder)/4;
        direButtonW = (w/2)/6 - outsideBorder; 
        direButtonH = (w/2)/6 - outsideBorder; 
        direButtonToggle = false;
        direColor = color(67, 76, 87);
    }
    
    public void draw()
    {
        drawScrollBar();
        drawPlayPauseButton();
        drawGoldButton();
        drawKillsButton();
        drawDeathsButton();
        drawAssistsButton();
        drawRadiantButton();
        drawDireButton();
    }
    
    public void drawScrollBar()
    {
        pushMatrix();
        pushStyle();
        
        noStroke();
        
        //draw bar
        fill(color(30));
        rectMode(CORNER);        
        rect(barX, barY, barW, barH, 2);  
        
        //draw slider
        fill(color(55));
        rectMode(CENTER);                            
        rect(sliderX, sliderY, sliderW, sliderH, 2);   
        
        popStyle();
        popMatrix();        
    }    
    
    public void drawPlayPauseButton()
    {
        pushMatrix();
        pushStyle();
        
        noStroke();        
        rectMode(CORNER);        
   
        //border
        fill(color(55));
        rect(playPauseButtonX - 3, playPauseButtonY - 3, playPauseButtonW + 6, playPauseButtonH + 6);
   
        //play button
        fill(color(30));
        rect(playPauseButtonX, playPauseButtonY, playPauseButtonW, playPauseButtonH, 2);
        
        fill(125);

        if(ticking == false) //play symbol
        {
            beginShape();
            vertex(playPauseButtonX + playPauseButtonW/2 - playPauseSymbolWH/4, playPauseButtonY + 5);
            vertex(playPauseButtonX + playPauseButtonW/2 - playPauseSymbolWH/4, playPauseButtonY + playPauseButtonH - 5);
            vertex(playPauseButtonX + playPauseButtonW/2 + playPauseSymbolWH/4, playPauseButtonY + playPauseButtonH/2);
            endShape(CLOSE);
        }
        else //pause symbol
        {
            beginShape();       
            vertex(playPauseButtonX + playPauseButtonW/2 - playPauseSymbolWH/4, playPauseButtonY + 5);
            vertex(playPauseButtonX + playPauseButtonW/2 - playPauseSymbolWH/4, playPauseButtonY + playPauseButtonH - 5);
            vertex(playPauseButtonX + playPauseButtonW/2 - playPauseSymbolWH/12, playPauseButtonY + playPauseButtonH - 5);
            vertex(playPauseButtonX + playPauseButtonW/2 - playPauseSymbolWH/12, playPauseButtonY + 5);
            
            vertex(playPauseButtonX + playPauseButtonW/2 + playPauseSymbolWH/4, playPauseButtonY + 5);
            vertex(playPauseButtonX + playPauseButtonW/2 + playPauseSymbolWH/4, playPauseButtonY + playPauseButtonH - 5);
            vertex(playPauseButtonX + playPauseButtonW/2 + playPauseSymbolWH/12, playPauseButtonY + playPauseButtonH - 5);
            vertex(playPauseButtonX + playPauseButtonW/2 + playPauseSymbolWH/12, playPauseButtonY + 5);
            endShape(CLOSE);      
        }
        
        popStyle();
        popMatrix();
    }
    
    public void clickedPlayPauseButton()
    {                
        if(!ticking)
        {          
            ticking = true;
            startingTime = System.currentTimeMillis();
        }
        else
        {
            ticking = false;  
        }      
    }
    
    public void drawGoldButton()
    {
        pushMatrix();
        pushStyle();
        
        noStroke();        
        rectMode(CORNER);        
   
        //border
        fill(color(55));
        rect(goldButtonX - 5, goldButtonY - 5, goldButtonW + 10, goldButtonH + 10);
   
        //gold button
        fill(color(30));
        rect(goldButtonX, goldButtonY, goldButtonW, goldButtonH, 2);  
        image(gold, goldButtonX + goldButtonW/10, goldButtonY + goldButtonH/10, goldButtonW - goldButtonW/5, goldButtonH - goldButtonH/5);
        
        //toggle effect
        if(goldButtonToggle)
        {
            fill(color(255, 77));
            rect(goldButtonX, goldButtonY, goldButtonW, goldButtonH, 2);  
        }
        
        popStyle();
        popMatrix();
    }
    
    public void clickedGoldButton()
    {
        if(goldButtonToggle)
            goldButtonToggle = false;
        else
            goldButtonToggle = true;
    }
    
    public void drawKillsButton()
    {
        pushMatrix();
        pushStyle();
        
        noStroke();        
        rectMode(CORNER);        
   
        //border
        fill(color(55));
        rect(killsButtonX - 5, killsButtonY - 5, killsButtonW + 10, killsButtonH + 10);
   
        //kill button
        fill(color(30));
        rect(killsButtonX, killsButtonY, killsButtonW, killsButtonH, 2);  
        image(kills, killsButtonX + killsButtonW/10, killsButtonY + killsButtonH/10, killsButtonW - killsButtonW/5, killsButtonH - killsButtonH/5);
        
        //toggle effect
        if(killsButtonToggle)
        {
            fill(color(255, 77));
            rect(killsButtonX, killsButtonY, killsButtonW, killsButtonH, 2);  
        }
        
        popStyle();
        popMatrix();
    }
    
    public void clickedKillsButton()
    {
        if(killsButtonToggle)
            killsButtonToggle = false;
        else
            killsButtonToggle = true;
    }
    
    public void drawDeathsButton()
    {
        pushMatrix();
        pushStyle();
        
        noStroke();        
        rectMode(CORNER);
  
        //border
        fill(color(55));
        rect(deathsButtonX - 5, deathsButtonY - 5, deathsButtonW + 10, deathsButtonH + 10);    
   
        //kills button
        fill(color(30));
        rect(deathsButtonX, deathsButtonY, deathsButtonW, deathsButtonH, 2);  
        image(deaths, deathsButtonX + deathsButtonW/10, deathsButtonY + deathsButtonH/10, deathsButtonW - deathsButtonW/5, deathsButtonH - deathsButtonH/5);

        //toggle effect
        if(deathsButtonToggle)
        {
            fill(color(255, 77));
            rect(deathsButtonX, deathsButtonY, deathsButtonW, deathsButtonH, 2);  
        }
        
        popStyle();
        popMatrix();
    }
    
    public void clickedDeathsButton()
    {
        if(deathsButtonToggle)
            deathsButtonToggle = false;
        else
            deathsButtonToggle = true;
    }
    
    public void drawAssistsButton()
    {
        pushMatrix();
        pushStyle();
        
        noStroke();        
        rectMode(CORNER);

        //border
        fill(color(55));
        rect(assistsButtonX - 5, assistsButtonY - 5, assistsButtonW + 10, assistsButtonH + 10);      
   
        //assists button
        fill(color(30));
        rect(assistsButtonX, assistsButtonY, assistsButtonW, assistsButtonH, 2);  
        image(assists, assistsButtonX + assistsButtonW/10, assistsButtonY + assistsButtonH/10, assistsButtonW - assistsButtonW/5, assistsButtonH - assistsButtonH/5);
        
        //toggle effect
        if(assistsButtonToggle)
        {
            fill(color(255, 77));
            rect(assistsButtonX, assistsButtonY, assistsButtonW, assistsButtonH, 2);  
        }
        
        popStyle();
        popMatrix();
    }
    
    public void clickedAssistsButton()
    {
        if(assistsButtonToggle)
            assistsButtonToggle = false;
        else
            assistsButtonToggle = true;
    }
    
    public void drawRadiantButton()
    {
        pushMatrix();
        pushStyle();
        
        noStroke();        
        rectMode(CORNER);

        //border
        fill(color(55));
        rect(radiantButtonX - 5, radiantButtonY - 5, radiantButtonW + 10, radiantButtonH + 10);      

        //assists button
        PImage radiant = teams.get("radiant").image;
        image(radiant, radiantButtonX, radiantButtonY, radiantButtonW, radiantButtonH);
        
        //toggle effect
        if(radiantButtonToggle)
        {
            fill(color(255, 77));
            rect(radiantButtonX, radiantButtonY, radiantButtonW, radiantButtonH, 2);  
        }
        
        popStyle();
        popMatrix();
    }
    
    public void clickedRadiantButton()
    {
        if(radiantButtonToggle)
            radiantButtonToggle = false;
        else
            radiantButtonToggle = true;
    }
    
    public void drawDireButton()
    {
        pushMatrix();
        pushStyle();
        
        noStroke();        
        rectMode(CORNER);

        //border
        fill(color(55));
        rect(direButtonX - 5, direButtonY - 5, direButtonW + 10, direButtonH + 10);      

        //assists button
        PImage dire = teams.get("dire").image;
        image(dire, direButtonX, direButtonY, direButtonW, direButtonH);
        
        //toggle effect
        if(direButtonToggle)
        {
            fill(color(255, 77));
            rect(direButtonX, direButtonY, direButtonW, direButtonH, 2);  
        }
        
        popStyle();
        popMatrix();
    }
    
    public void clickedDireButton()
    {
        if(direButtonToggle)
            direButtonToggle = false;
        else
            direButtonToggle = true;
    }
    
    public void advanceTime()
    {
        float gameTimeInSecs = 0;

        if(System.currentTimeMillis() - startingTime >= 1000/speed) //if a second has passed in realtime
        {
            startingTime = System.currentTimeMillis(); //update startingTime to start at currentTime
       
            //increment gameTimeInSecs from Clock by 1
            gameTimeInSecs = ((Clock)visuals.get(0)).gameTimeInSecs + 1; 
            ((Clock)visuals.get(0)).update(gameTimeInSecs);            
            
            //update slider position by mapping number seconds to slider position
            sliderX = (int) map(gameTimeInSecs, 0, maxGameTime/30, sliderXMin, sliderXMax);
            
            //update events using game time, not seconds
            updateEvents((int)(gameTimeInSecs * 30));
        }
         
        //stop the ticking after the game has reached ending time
        if(gameTimeInSecs >= maxGameTime/30)
            ticking = false;
    }
    
    public void manualScrollBarUpdate(int x)
    {
        sliderX = constrain(x, sliderXMin, sliderXMax);                               
        
        //get new game time by maping slider position to time        
        float totalGameTime = map(sliderX, sliderXMin, sliderXMax, 0, maxGameTime);
                
        //update clock using seconds
        ((Clock)visuals.get(0)).update(totalGameTime/30);               
        
        //update events
        updateEvents((totalGameTime));
    }
    
    public void updateEvents(float newGameTime)
    {        
        int currentGameTime = events.get(currentEventPosition).time;                      

        if(currentGameTime < newGameTime)
        {            
            while(currentEventPosition < events.size() && events.get(currentEventPosition).time < newGameTime)
            {
                if(events.get(currentEventPosition).set == 0) //if the event hasn't been set, set it
                    events.get(currentEventPosition).apply();
                currentEventPosition++;
            }
        }
        else if(currentGameTime > newGameTime)
        {
            while(currentEventPosition >= 0 && events.get(currentEventPosition).time > newGameTime)
            {
                if(events.get(currentEventPosition).set == 1) //if the event has been set, unset it
                    events.get(currentEventPosition).undo();
                                    
                currentEventPosition--;
            }
        }
        else if(currentGameTime == newGameTime)
        {          
            if(events.get(currentEventPosition).set == 0) //if the event hasn't been set, set it
            {
//                System.out.println("currentGameTime: " + currentGameTime + " currentEventPosition: " + currentEventPosition);
                events.get(currentEventPosition).apply();
                currentEventPosition++;
            }
        }

        currentEventPosition = constrain(currentEventPosition, 0, events.size()-1); //set constraints for event position
    }
    
    public int onTopOfVisual()
    {
        //scrollbar   
        if((mouseX > barX && mouseX < (barX + barW)) && //w
                (mouseY > barY && mouseY < (barY + barH))) //h
            return 1;
        //slider
        else if((mouseX > (sliderX - sliderW/2) && mouseX < (sliderX + sliderW/2)) && //w
           (mouseY > (sliderY - sliderH/2) && mouseY < (sliderY + sliderH/2))) //h
            return 2;                
        //play button
        else if((mouseX > playPauseButtonX && mouseX < (playPauseButtonX + playPauseButtonW)) && //w
            (mouseY > playPauseButtonY && mouseY < (playPauseButtonY + playPauseButtonH))) //h        
            return 3;
        //gold button
        else if((mouseX > goldButtonX && mouseX < (goldButtonX + goldButtonW)) && //w
            (mouseY > goldButtonY && mouseY < (goldButtonY + goldButtonH))) //h        
            return 4;
        //kills button
        else if((mouseX > killsButtonX && mouseX < (killsButtonX + killsButtonW)) && //w
            (mouseY > killsButtonY && mouseY < (killsButtonY + killsButtonH))) //h        
            return 5;
        //deaths button
        else if((mouseX > deathsButtonX && mouseX < (deathsButtonX + deathsButtonW)) && //w
            (mouseY > deathsButtonY && mouseY < (deathsButtonY + deathsButtonH))) //h        
            return 6;
        //assists button
        else if((mouseX > assistsButtonX && mouseX < (assistsButtonX + assistsButtonW)) && //w
            (mouseY > assistsButtonY && mouseY < (assistsButtonY + assistsButtonH))) //h        
            return 7;
        //radiant button
        else if((mouseX > radiantButtonX && mouseX < (radiantButtonX + radiantButtonW)) && //w
            (mouseY > radiantButtonY && mouseY < (radiantButtonY + radiantButtonH))) //h        
            return 8;
        //dire button
        else if((mouseX > direButtonX && mouseX < (direButtonX + direButtonW)) && //w
            (mouseY > direButtonY && mouseY < (direButtonY + direButtonH))) //h        
            return 9;
        //nothing selected
        else    
            return 0;
    }
}

public interface Entity
{   
    public String getEntityName();        
}

public class Event
{   
    public int time;
    public int set; //1 == applied, 0 == not applied (aka undone); used to make sure events are applied and undone properly
    public int barSet;
    
    public void apply() {}
    public void undo() {}
}













/*
https://code.google.com/p/json-simple/wiki/DecodingExamples
http://www.mkyong.com/java/json-simple-example-read-and-write-json/
https://code.google.com/p/json-simple/
http://answers.oreilly.com/topic/257-how-to-parse-json-in-java/
*/

public class FileReaders
{   
    public FileReaders(){}
    public int subcategoriesCount = 0;        
    public int subdomainsCount = 0;
    public int maxCount = 2499;
    
    public void readAllFiles()
    {
        readAllHeroNames();
        readPlayers();
        readGoldEvents();
        readKDAEvents();
        readLevelUpEvents();
    }
    
    public void readAllHeroNames()
    {
        Scanner inputStream = null;
    
        try
        {
          inputStream = new Scanner(new FileInputStream(dataPath("heroes.txt"))); // Initialize new file input stream.
        }
        catch(FileNotFoundException e)
        {
          System.out.println("The file \"" + "heroes.txt" + "\" was not found.");
          System.exit(0);
        }  
        
        while(inputStream.hasNext())
        {
            String line = inputStream.nextLine();
            StringTokenizer st = new StringTokenizer(line, ",");
            
            String longName = st.nextToken();
            String shortName = st.nextToken().substring(1); //get rid of the space at the beginning of each name
                        
            longToShortHeroNames.put(longName, shortName); //insert into Hashtable
            shortToLongHeroNames.put(shortName,  longName);
        }
        
        inputStream.close(); // Close the file input stream.
    }
    
    public void readPlayers()
    {
        JSONParser parser = new JSONParser();
        
        try 
        {
            Object obj = parser.parse(new FileReader(dataPath("json/players.json")));
            JSONObject jsonObject = (JSONObject) obj;
                        
            JSONArray players = (JSONArray) jsonObject.get("players");
            Iterator itr = players.iterator();
            
            int counter = 0; //first 5 heroes are radiant, second 5 are dire
            
            while(itr.hasNext())
            {
                JSONObject tmpObj = (JSONObject) itr.next();
               
                String hero = longToShortHeroNames.get((String) tmpObj.get("hero")); //use hashtable to get short version of the name
                
                if(counter < 5) //radiant heroes
                {
                    heroes.put(hero, new Hero(hero, "radiant"));
                    teams.get("radiant").teamHeroes.add(hero);
                    switch(counter)
                    {
                        case 0:
                            heroes.get(hero).colour = color(46, 106, 230);
                            break;
                        case 1:
                            heroes.get(hero).colour = color(93, 230, 173);
                            break;
                        case 2:
                            heroes.get(hero).colour = color(173, 0, 173);
                            break;
                        case 3:
                            heroes.get(hero).colour = color(220, 217, 10);
                            break;
                        case 4:
                            heroes.get(hero).colour = color(230, 98, 0);
                            break;
                    }
                    
                }
                else //dire heroes
                {
                    heroes.put(hero, new Hero(hero, "dire"));
                    teams.get("dire").teamHeroes.add(hero);
                    switch(counter)
                    {
                        case 5:
                            heroes.get(hero).colour = color(230, 122, 176);
                            break;
                        case 6:
                            heroes.get(hero).colour = color(146, 164, 64);
                            break;
                        case 7:
                            heroes.get(hero).colour = color(92, 197, 224);
                            break;
                        case 8:
                            heroes.get(hero).colour = color(0, 119, 31);
                            break;
                        case 9:
                            heroes.get(hero).colour = color(149, 96, 0);
                            break;
                    }
                }
                counter++;
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        } 
        catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    public void readGoldEvents()
    {
        JSONParser parser = new JSONParser();

        try 
        {
            Object obj = parser.parse(new FileReader(dataPath("json/gold.json")));
            JSONObject jsonObject = (JSONObject) obj;
                        
            JSONArray goldEvents = (JSONArray) jsonObject.get("gold");
            Iterator itr = goldEvents.iterator();                        
            
            while(itr.hasNext())
            {
                JSONObject tmpObj = (JSONObject) itr.next();
                
                long longTime = (Long) tmpObj.get("time");     
                int time = (int) longTime;           
                String hero = longToShortHeroNames.get((String) tmpObj.get("hero")); //use hashtable to get short version of the name
                long longGold = (Long) tmpObj.get("gold");
                int gold = (int) longGold;
                events.add(new GoldEvent(time, hero, gold));
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        } 
        catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    public void readKDAEvents()
    {
        JSONParser parser = new JSONParser();
        
        try 
        {
            Object obj = parser.parse(new FileReader(dataPath("json/herokills.json")));
            JSONObject jsonObject = (JSONObject) obj;
                        
            JSONArray kdaEvents = (JSONArray) jsonObject.get("herokills");
            Iterator kdaItr = kdaEvents.iterator();                        
            
            while(kdaItr.hasNext())
            {
                JSONObject tmpObj = (JSONObject) kdaItr.next();
                
                long longTime = (Long) tmpObj.get("time");     
                int time = (int) longTime; 
                
                String dead = longToShortHeroNames.get((String) tmpObj.get("dead")); //use hashtable to get short version of the name
                String killer = longToShortHeroNames.get((String) tmpObj.get("killer")); //use hashtable to get short version of the name                
                
                ArrayList<String> longAssists = (ArrayList<String>) tmpObj.get("assists");
                ArrayList<String> assists = new ArrayList<String>();
                for(String s : longAssists)
                    assists.add(longToShortHeroNames.get(s));
                
                events.add(new KDAEvent(time, killer, dead, assists));              
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        } 
        catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    public void readLevelUpEvents()
    {
        JSONParser parser = new JSONParser();
        
        try 
        {
            Object obj = parser.parse(new FileReader(dataPath("json/levelups.json")));
            JSONObject jsonObject = (JSONObject) obj;
                        
            JSONArray goldEvents = (JSONArray) jsonObject.get("leveluptimes");
            Iterator itr = goldEvents.iterator();                        
            
            while(itr.hasNext())
            {
                JSONObject tmpObj = (JSONObject) itr.next();
                
                long longTime = (Long) tmpObj.get("time");     
                int time = (int) longTime;           
                String hero = longToShortHeroNames.get((String) tmpObj.get("hero")); //use hashtable to get short version of the name
                long longLevel = (Long) tmpObj.get("level");
                int level = (int) longLevel;
                
                events.add(new LevelUpEvent(time, hero, level));
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        } 
        catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
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
    public int colour; 
    
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
public class Line
{
    public Entity entity;      
    String type; 
    
    public XYChart line;
    public int lineX, lineY, lineW, lineH;
    public int colour;
  
    public ArrayList<PVector> points;
    public int[] barSet;
    public int maxX, maxY;        
    public int total;
    public int graphEventPosition;
    
    public Line(Entity entity, String type, int c, int lineX, int lineY, int lineW, int lineH)
    {
        this.entity = entity;
        this.type = type;
        maxX = maxGameTime;
        maxY = getMaxY();
        total = 0;
        colour = c;        
        
        this.lineX = lineX;
        this.lineY = lineY;
        this.lineW = lineW;
        this.lineH = lineH;
        
        points = new ArrayList<PVector>();
        points.add(new PVector(0, 0)); //initial point at (0, 0)
        barSet = new int[events.size()];   
        
        line = new XYChart(Dota2.this);
        line.setMinX(0);
        line.setMinY(0);
        line.setMaxX(maxX);
        line.setMaxY(maxY);

        line.setPointColour(colour);
        line.setLineColour(colour);
        line.setPointSize(3);
        line.setLineWidth(4);            
    }
    
    public void updateXYPoints()
    {
        //update points
        update();
        
        //set data
        line.setData(points);                      
        
        //set maxX and maxY
        line.setMaxX(maxX);
        line.setMaxY(maxY);
        
        //set color
        line.setPointColour(colour);
        line.setLineColour(colour);                      
    }
    
    public void draw()
    {        
        line.draw(lineX, lineY, lineW, lineH);
        
        if(points.size() < 2) //show the image helpers only when there is more than 1 point
            return;
        
        int endX = (int)line.getDataToScreen(points.get(points.size() - 1)).x;
        int endY = (int)line.getDataToScreen(points.get(points.size() - 1)).y;
        
        PImage entityImg = null;
        PImage attrImg = null;
        int maxData = (int)points.get(points.size() - 1).y;
        int numBoxes = 2; //count the entity
        
        if(entity instanceof Team)
            entityImg = teams.get(((Team)entity).name).image;
        
        if(type.equals("gold"))
            attrImg = ((Controls)visuals.get(1)).gold;
        else if(type.equals("kills"))
            attrImg = ((Controls)visuals.get(1)).kills;
        else if(type.equals("deaths"))
            attrImg = ((Controls)visuals.get(1)).deaths;
        else if(type.equals("assists"))
            attrImg = ((Controls)visuals.get(1)).assists;
        
        pushMatrix();
        pushStyle();
             
        //border
        String data = "" + maxData;
        float dataW = textWidth(data);
        
        int boxW = outsideBorder, boxH = outsideBorder;
        int startX = endX - (numBoxes * boxW) - (numBoxes * outsideBorder/8) - (int)dataW - 7;
        int startY = endY;
         
        noStroke();
                 
        //border
        fill(color(85));
        rect(startX - 4, startY - 4, (boxW * 2) + dataW + outsideBorder/4 + 8, boxH + 8, 2);             
             
        //images
        image(entityImg, startX, startY, boxW, boxH);
        image(attrImg, startX + boxW + outsideBorder/8, startY, boxW, boxH);
        
        //data text
        fill(color(255));
        textAlign(LEFT, CENTER);
        text(data, startX + (2*boxW) + outsideBorder/4, startY + boxH/2);
        
        popStyle();
        popMatrix();
    }
    
    public void update()
    {                
        int currentGameTime = events.get(graphEventPosition).time;
        float newGameTime = map(((Controls)visuals.get(1)).sliderX, ((Controls)visuals.get(1)).sliderXMin, ((Controls)visuals.get(1)).sliderXMax, 0, maxGameTime);
        
        //forward
        if(currentGameTime < newGameTime)
        {                
            while(graphEventPosition < events.size() && events.get(graphEventPosition).time < newGameTime)
            {                
                if(barSet[graphEventPosition] == 0)
                {   
                    //gold
                    if(type.equals("gold") && events.get(graphEventPosition) instanceof GoldEvent)
                    {
                        //team
                        if(entity instanceof Team)
                        {
                            String heroName = ((GoldEvent)events.get(graphEventPosition)).hero;
                            String teamName = ((Team)entity).name;
                            String heroTeamName = heroes.get(heroName).team;
                            if(teamName.equals(heroTeamName))
                            {
                                total += ((GoldEvent)events.get(graphEventPosition)).gold;
//                                barSet[graphEventPosition] = 1;
//                                points.add(new PVector(events.get(graphEventPosition).time, total));
                            }
                        }
                    }
                    //kills
                    else if(type.equals("kills") && events.get(graphEventPosition) instanceof KDAEvent)
                    {
                        //team
                        if(entity instanceof Team)
                        {
                            String heroName = ((KDAEvent)events.get(graphEventPosition)).killer;
                            String teamName = ((Team)entity).name;

                            if(heroName != null)
                            {
                                String heroTeamName = heroes.get(heroName).team;
                                
                                if(teamName.equals(heroTeamName))
                                {
                                    total += 1;
    //                                barSet[graphEventPosition] = 1;
    //                                points.add(new PVector(events.get(graphEventPosition).time, total));
                                }
                            }
                        }
                    }
                    //deaths
                    else if(type.equals("deaths") && events.get(graphEventPosition) instanceof KDAEvent)
                    {
                        //team
                        if(entity instanceof Team)
                        {
                            String heroName = ((KDAEvent)events.get(graphEventPosition)).dead;
                            String teamName = ((Team)entity).name;

                            if(heroName != null)
                            {
                                String heroTeamName = heroes.get(heroName).team;
                                
                                if(teamName.equals(heroTeamName))
                                {
                                    total += 1;
    //                                barSet[graphEventPosition] = 1;
    //                                points.add(new PVector(events.get(graphEventPosition).time, total));
                                }
                            }
                        }
                    }
                    //assists
                    else if(type.equals("assists") && events.get(graphEventPosition) instanceof KDAEvent)
                    {
                        //team
                        if(entity instanceof Team)
                        {
                            ArrayList<String> heroNames = ((KDAEvent)events.get(graphEventPosition)).assists;
                            String teamName = ((Team)entity).name;

                            if(heroNames.size() > 0)
                            {
                                for(String heroName : heroNames)
                                {
                                    String heroTeamName = heroes.get(heroName).team;
                                    
                                    if(teamName.equals(heroTeamName))
                                    {
                                        total += 1;
        //                                barSet[graphEventPosition] = 1;
        //                                points.add(new PVector(events.get(graphEventPosition).time, total));
                                    }
                                }
                            }
                        }
                    }
  
                    barSet[graphEventPosition] = 1;
                    points.add(new PVector(events.get(graphEventPosition).time, total));
                }
                   
                graphEventPosition++;
            }
        }
        //backward
        else if(currentGameTime > newGameTime)
        {
            while(graphEventPosition >= 0 && events.get(graphEventPosition).time > newGameTime)
            {
                if(barSet[graphEventPosition] == 1)
                {
                    //gold
                    if(type.equals("gold") && events.get(graphEventPosition) instanceof GoldEvent)
                    {
                        //team
                        if(entity instanceof Team)
                        {
                            String heroName = ((GoldEvent)events.get(graphEventPosition)).hero;
                            String teamName = ((Team)entity).name;
                            String heroTeamName = heroes.get(heroName).team;
                            if(teamName.equals(heroTeamName))
                            {
                                total -= ((GoldEvent)events.get(graphEventPosition)).gold;
//                                barSet[graphEventPosition] = 0;
//                                points.remove(points.size() - 1);
                            }
                        }
                    }
                    //kills
                    else if(type.equals("kills") && events.get(graphEventPosition) instanceof KDAEvent)
                    {
                        //team
                        if(entity instanceof Team)
                        {
                            String heroName = ((KDAEvent)events.get(graphEventPosition)).killer;
                            String teamName = ((Team)entity).name;
                            
                            if(heroName != null)
                            {
                                String heroTeamName = heroes.get(heroName).team;
                                if(teamName.equals(heroTeamName))
                                {
                                    total -= 1;
                                }
                            }
                        }
                    }
                    //deaths
                    else if(type.equals("deaths") && events.get(graphEventPosition) instanceof KDAEvent)
                    {
                        //team
                        if(entity instanceof Team)
                        {
                            String heroName = ((KDAEvent)events.get(graphEventPosition)).dead;
                            String teamName = ((Team)entity).name;
                            
                            if(heroName != null)
                            {
                                String heroTeamName = heroes.get(heroName).team;
                                if(teamName.equals(heroTeamName))
                                {
                                    total -= 1;
                                }
                            }
                        }
                    }
                    //assists
                    else if(type.equals("assists") && events.get(graphEventPosition) instanceof KDAEvent)
                    {
                        //team
                        if(entity instanceof Team)
                        {
                            ArrayList<String> heroNames = ((KDAEvent)events.get(graphEventPosition)).assists;
                            String teamName = ((Team)entity).name;

                            if(heroNames.size() > 0)
                            {
                                for(String heroName : heroNames)
                                {
                                    String heroTeamName = heroes.get(heroName).team;
                                    
                                    if(teamName.equals(heroTeamName))
                                    {
                                        total -= 1;
        //                                barSet[graphEventPosition] = 1;
        //                                points.add(new PVector(events.get(graphEventPosition).time, total));
                                    }
                                }
                            }
                        }
                    }

                    barSet[graphEventPosition] = 0;
                    points.remove(points.size() - 1);
                }
                                    
                graphEventPosition--;
            }
        }
        else if(currentGameTime == newGameTime)
        {
            if(barSet[graphEventPosition] == 0)
            {
                //gold
                if(type.equals("gold") && events.get(graphEventPosition) instanceof GoldEvent)
                {
                    //team
                    if(entity instanceof Team)
                    {
                        String heroName = ((GoldEvent)events.get(graphEventPosition)).hero;
                        String teamName = ((Team)entity).name;
                        String heroTeamName = heroes.get(heroName).team;
                        if(teamName.equals(heroTeamName))
                        {
                            total += ((GoldEvent)events.get(graphEventPosition)).gold;
                            barSet[graphEventPosition] = 1;
                            points.add(new PVector(events.get(graphEventPosition).time, total));
                            graphEventPosition++;
                        }
                    }
                }
                //kills
                else if(type.equals("kills") && events.get(graphEventPosition) instanceof KDAEvent)
                {
                    //team
                    if(entity instanceof Team)
                    {
                        String heroName = ((KDAEvent)events.get(graphEventPosition)).killer;
                        String teamName = ((Team)entity).name;
                        
                        if(heroName != null)
                        {
                            String heroTeamName = heroes.get(heroName).team;
                            if(teamName.equals(heroTeamName))
                            {
                                total += 1;
                                barSet[graphEventPosition] = 1;
                                points.add(new PVector(events.get(graphEventPosition).time, total));
                                graphEventPosition++;
                            }
                        }
                    }
                }
                //deaths
                else if(type.equals("deaths") && events.get(graphEventPosition) instanceof KDAEvent)
                {
                    //team
                    if(entity instanceof Team)
                    {
                        String heroName = ((KDAEvent)events.get(graphEventPosition)).dead;
                        String teamName = ((Team)entity).name;
                        
                        if(heroName != null)
                        {
                            String heroTeamName = heroes.get(heroName).team;
                            if(teamName.equals(heroTeamName))
                            {
                                total += 1;
                                barSet[graphEventPosition] = 1;
                                points.add(new PVector(events.get(graphEventPosition).time, total));
                                graphEventPosition++;
                            }
                        }
                    }
                }
                //assists
                else if(type.equals("assists") && events.get(graphEventPosition) instanceof KDAEvent)
                {
                    //team
                    if(entity instanceof Team)
                    {
                        ArrayList<String> heroNames = ((KDAEvent)events.get(graphEventPosition)).assists;
                        String teamName = ((Team)entity).name;

                        if(heroNames.size() > 0)
                        {
                            for(String heroName : heroNames)
                            {
                                String heroTeamName = heroes.get(heroName).team;
                                
                                if(teamName.equals(heroTeamName))
                                {
                                    total += 1;

                                }
                            }
                            barSet[graphEventPosition] = 1;
                            points.add(new PVector(events.get(graphEventPosition).time, total));
                            graphEventPosition++;
                        }
                    }
                }
            }
        }

        graphEventPosition = constrain(graphEventPosition, 0, events.size() - 1); //set constraints for event position
    }//update()  

    public int getMaxY()
    {
        int totalCount = 0;
        
        for(Event e : events)
        {
            //gold
            if(type.equals("gold") && e instanceof GoldEvent)
            {                
                //team
                if(entity instanceof Team)
                {                    
                    String eventHeroName = ((GoldEvent)e).hero;
                    String teamName = ((Team)entity).name;
                    String heroTeamName = heroes.get(eventHeroName).team;
                    if(teamName.equals(heroTeamName))
                    {
                        totalCount += ((GoldEvent)e).gold;
                    }
                }
                //hero
                else if(entity instanceof Hero)
                {
                    String eventHeroName = ((GoldEvent)e).hero;
                    String entityHeroName = ((Hero)entity).name;
                    
                    if(eventHeroName.equals(entityHeroName))
                    {
                        totalCount += ((GoldEvent)e).gold;
                    }
                }
            }
            //kills
            else if(type.equals("kills") && e instanceof KDAEvent)
            {
                //team
                if(entity instanceof Team)
                {                    
                    String eventHeroName = ((KDAEvent)e).killer;
                    String teamName = ((Team)entity).name;
                    
                    if(eventHeroName != null)
                    {
                        String heroTeamName = heroes.get(eventHeroName).team;
                        if(teamName.equals(heroTeamName))
                        {
                            totalCount += 1;
                        }
                    }
                }
                //hero
                else if(entity instanceof Hero)
                {
                    String eventHeroName = ((KDAEvent)e).killer;
                    String entityHeroName = ((Hero)entity).name;
                    
                    if(eventHeroName != null)
                    {
                        if(eventHeroName.equals(entityHeroName))
                        {
                            totalCount += 1;
                        }
                    }
                }
            }
            //deaths
            else if(type.equals("deaths") && e instanceof KDAEvent)
            {
                //team
                if(entity instanceof Team)
                {                    
                    String eventHeroName = ((KDAEvent)e).dead;
                    String teamName = ((Team)entity).name;
                    
                    if(eventHeroName != null)
                    {
                        String heroTeamName = heroes.get(eventHeroName).team;
                        if(teamName.equals(heroTeamName))
                        {
                            totalCount += 1;
                        }
                    }
                }
                //hero
                else if(entity instanceof Hero)
                {
                    String eventHeroName = ((KDAEvent)e).dead;
                    String entityHeroName = ((Hero)entity).name;
                    
                    if(eventHeroName != null)
                    {
                        if(eventHeroName.equals(entityHeroName))
                        {
                            totalCount += 1;
                        }
                    }
                }
            }                
            //assists   
            else if(type.equals("assists") && e instanceof KDAEvent)
            {
                //team
                if(entity instanceof Team)
                {                    
                    ArrayList<String> eventHeroNames = ((KDAEvent)e).assists;
                    String teamName = ((Team)entity).name;
                    
                    if(eventHeroNames.size() > 0)
                    {
                        for(String heroName : eventHeroNames)
                        {
                            String heroTeamName = heroes.get(heroName).team;
                            
                            if(teamName.equals(heroTeamName))
                            {
                                totalCount += 1;

                            }
                        }
                    }
                }
                //hero
                else if(entity instanceof Hero)
                {
                    ArrayList<String> eventHeroNames = ((KDAEvent)e).assists;
                    String entityHeroName = ((Hero)entity).name;
                    
                    if(eventHeroNames.size() > 0)
                    {
                        for(String heroName : eventHeroNames)
                        {
                            if(entityHeroName.equals(heroName))
                            {
                                totalCount += 1;
                            }
                        }
                    }
                }
            }
            else if(type.equals("levels"))
            {
            }
        }
        
        return totalCount;
    }    
}
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
/*
http://www.ibm.com/developerworks/library/x-svggrph/
 */

public class TeamPanel implements Visual
{   
    public int boxX, boxY, boxW, boxH, boxColour;
    public int heroX, heroY, heroW, heroH, heroColour, heroNameX, heroNameY;
    public int barX1, barY1, barColour1, barX2, barY2, barColour2, barX3, barY3, barColour3, barW, barH;
  
    public String team;    
    public ArrayList<PImage> portraits;
    public ArrayList<PImage> smallPortraits;
    public boolean heroViewToggle;
    public int toggledHero;
    public String toggledHeroName;
    
    public PImage back;
  
    public int borderColor;
  
    public TeamPanel(int x, int y, String team)
    {
        //border color 
        borderColor = color(55);
    
        //view toggle
        heroViewToggle = true;
        toggledHero = 0;
        toggledHeroName = "";
        
        //back button
        back = loadImage(dataPath("miscimages/back.png"));
        
        //box
        boxX = x;
        boxY = y;
        boxW = w/4 - outsideBorder;
        boxH = (3*h)/4 - outsideBorder;
        boxColour = color(30);
    
        //heroes
        heroX = boxX + outsideBorder/2;
        heroY = boxY + outsideBorder/2;
        heroH = (boxH - (2*outsideBorder))/5;
        heroW = (boxW - outsideBorder - outsideBorder/4) / 3;
        heroColour = color(85);
        heroNameX = heroX + heroW + outsideBorder/2;
        heroNameY = heroY + heroH/4 - outsideBorder/4;
    
        //bar chart
        barColour1 = color(44, 131, 9); //kills
        barX1 = heroX + heroW + outsideBorder/2 + 4;
        barY1 = heroY + heroH/4 + outsideBorder/4;
    
        barColour2 = color(157, 6, 16); //deaths
        barX2 = heroX + heroW + outsideBorder/2 + 4;
        barY2 = heroY + (2*heroH)/4 + outsideBorder/8;
    
        barColour3 = color(8, 77, 131); //assistss
        barX3 = heroX + heroW + outsideBorder/2 + 4;
        barY3 = heroY + (3*heroH)/4 ;
    
        barW = (boxX + boxW) - (heroX + heroW) - outsideBorder; //max bar width
        barH = heroH/4 - outsideBorder/2;
    
        this.team = team;        
    
        //portraits
        portraits = new ArrayList<PImage>();
    
        for (String hero : teams.get(this.team).teamHeroes) //load images
        {
          String filePath = "heroimages/" + shortToLongHeroNames.get(hero) + ".jpg";
          PImage img = loadImage(dataPath(filePath));
          portraits.add(img);
        }
        
        //small portraits
        smallPortraits = new ArrayList<PImage>();
    
        for (String hero : teams.get(this.team).teamHeroes) //load images
        {
          String filePath = "smallheroimages/" + shortToLongHeroNames.get(hero) + ".png";
          PImage img = loadImage(dataPath(filePath));
          smallPortraits.add(img);
        }
    }
  
    public void draw()
    {
        drawBox();
        drawTeamSymbol();
    
        if (heroViewToggle)
          drawHeroOverview();
        else
          drawHeroDetail();
    }
    
    public void drawTeamSymbol()
    {
        pushMatrix();
        pushStyle();
        
        noStroke();        
        rectMode(CORNER);
       
        //assists button
        PImage teamImg = teams.get(team).image;
        image(teamImg, boxX + (boxW - (w/2)/6 - outsideBorder/4)/2, boxY - (2*outsideBorder) - (w/2)/6 - outsideBorder/2, (w/2)/6 - outsideBorder/4, (w/2)/6 - outsideBorder/4);
        
        popStyle();
        popMatrix();
    }
  
    public void drawHeroOverview()
    {
        drawHeroPortraits();
        drawHeroLevels();
        drawHeroNames();
        drawBarGraphs();
    }
  
    public void drawHeroDetail()
    {
        pushMatrix();
        pushStyle();
        
        noStroke();
    
        //name
        fill(heroes.get(toggledHeroName).colour);        
        textFont(optimusPrincepsFont, outsideBorder);        
        textAlign(LEFT, TOP);
        text(toggledHeroName, boxX + (boxW - textWidth(toggledHeroName))/2, boxY + outsideBorder/2);
    
        //portrait border
        rectMode(CORNER);
        fill(borderColor);
        rect(boxX + (boxW - (3*boxW)/7)/2 - 5, heroY + (3*outsideBorder)/2 - 5, (3*boxW)/7 + 10, (7*boxH)/24 + 10);
    
        //portrait 
        image(portraits.get(toggledHero - 1), boxX + (boxW - (3*boxW)/7)/2, heroY + (3*outsideBorder)/2, (3*boxW)/7, (7*boxH)/24);
    
        //back button and small portraits
        int littleW = (boxW - outsideBorder)/5;
        int littleH = outsideBorder + outsideBorder/3;
        
        //level
        ellipseMode(CENTER);
        //outer circle
        fill(color(90, 83, 14));
        ellipse(boxX + (boxW - (3*boxW)/7)/2, heroY + (3*outsideBorder)/2 + (7*boxH)/24, outsideBorder + outsideBorder/2, outsideBorder + outsideBorder/2);
        //inner circle
        fill(color(118, 109, 18));
        ellipse(boxX + (boxW - (3*boxW)/7)/2, heroY + (3*outsideBorder)/2 + (7*boxH)/24, outsideBorder + outsideBorder/3, outsideBorder + outsideBorder/3);  
        //level text
        fill(255);
        textAlign(CENTER, CENTER);
        text(heroes.get(toggledHeroName).level, boxX + (boxW - (3*boxW)/7)/2, heroY + (3*outsideBorder)/2 + (7*boxH)/24);
          
        //back button        
        image(back, boxX, boxY - (2 * outsideBorder), littleW, littleH);        
        
        //hero toggles
        int counter = 1;
        for(int i = 0; i < 5; i++)
        {
            fill(boxColour);
            if((i + 1) != toggledHero)
            rect(boxX + (counter * (outsideBorder/4 + littleW)) - 3, boxY - (2 * outsideBorder) - 3, littleW + 6, littleH + 6);
            
            fill(borderColor);
            if((i + 1) != toggledHero)
                image(smallPortraits.get(i), boxX + (counter++ * (outsideBorder/4 + littleW)), boxY - (2 * outsideBorder), littleW, littleH);
        } 
        
        //gold
        fill(color(207, 161, 9));
        image(((Controls)visuals.get(1)).gold, boxX + (3*outsideBorder), boxY + (7*boxH)/24 + (3*outsideBorder) + outsideBorder/2, littleW, littleH);
        textAlign(LEFT, CENTER);        
        text(heroes.get(toggledHeroName).gold, boxX + (3*outsideBorder) + outsideBorder/2 + littleW, boxY + (7*boxH)/24 + (3*outsideBorder) + outsideBorder/2 + littleH/2);
        
        //key
        //kills
        fill(color(44, 131, 9));
        textAlign(LEFT, CENTER);        
        text("Kills: ", boxX + outsideBorder/2, heroY + (2*outsideBorder)/2 + (7*boxH)/24 + (4*outsideBorder) + outsideBorder/2);
        fill(color(255));
        textAlign(LEFT, CENTER);        
        text(heroes.get(toggledHeroName).kills, boxX + outsideBorder/2 + textWidth("Kills: "), heroY + (2*outsideBorder)/2 + (7*boxH)/24 + (4*outsideBorder) + outsideBorder/2);        
        //deaths
        fill(color(157, 6, 16));
        textAlign(LEFT, CENTER);        
        text("Deaths: ", boxX + outsideBorder/2 + textWidth("  Kills: " + heroes.get(toggledHeroName).kills), heroY + (2*outsideBorder)/2 + (7*boxH)/24 + (4*outsideBorder) + outsideBorder/2);
        fill(color(255));
        textAlign(LEFT, CENTER);        
        text(heroes.get(toggledHeroName).deaths, boxX + outsideBorder/2 + textWidth("  Kills: " + heroes.get(toggledHeroName).kills + "Deaths: "), heroY + (2*outsideBorder)/2 + (7*boxH)/24 + (4*outsideBorder) + outsideBorder/2);        
        //assists
        fill(color(8, 77, 131));
        textAlign(LEFT, CENTER);        
        text("Assists: ", boxX + outsideBorder/2 + textWidth("  Kills: " + heroes.get(toggledHeroName).kills + "  Deaths: " + heroes.get(toggledHeroName).deaths), heroY + (2*outsideBorder)/2 + (7*boxH)/24 + (4*outsideBorder) + outsideBorder/2);
        fill(color(255));
        textAlign(LEFT, CENTER);        
        text(heroes.get(toggledHeroName).assists, boxX + outsideBorder/2 + textWidth("  Kills: " + heroes.get(toggledHeroName).kills + "  Deaths: " + heroes.get(toggledHeroName).deaths + "Assists: "), heroY + (2*outsideBorder)/2 + (7*boxH)/24 + (4*outsideBorder) + outsideBorder/2);        
        
        //pie chart
        float diameter = boxW - (4*outsideBorder);
        float total = heroes.get(toggledHeroName).kills + heroes.get(toggledHeroName).deaths + heroes.get(toggledHeroName).assists;
        
        float angle1 = radians((heroes.get(toggledHeroName).kills/(float)total) * (360));
        float angle2 = radians((heroes.get(toggledHeroName).deaths/(float)total) * (360));
        float angle3 = radians((heroes.get(toggledHeroName).assists/(float)total) * (360));
        float lastAngle = 0;
        
        fill(color(20));
        ellipseMode(CENTER);
        ellipse(boxX + boxW/2, heroY + (2*outsideBorder)/2 + (7*boxH)/24 + (5*outsideBorder) + outsideBorder/4 + diameter/2, diameter, diameter);
        
        fill(color(44, 131, 9));
        arc(boxX + boxW/2, heroY + (2*outsideBorder)/2 + (7*boxH)/24 + (5*outsideBorder) + outsideBorder/4 + diameter/2, diameter, diameter, lastAngle, lastAngle + angle1);
        lastAngle += angle1;
        fill(color(157, 6, 16));
        arc(boxX + boxW/2, heroY + (2*outsideBorder)/2 + (7*boxH)/24 + (5*outsideBorder) + outsideBorder/4 + diameter/2, diameter, diameter, lastAngle, lastAngle + angle2);
        lastAngle += angle2;
        fill(color(8, 77, 131));
        arc(boxX + boxW/2, heroY + (2*outsideBorder)/2 + (7*boxH)/24 + (5*outsideBorder) + outsideBorder/4 + diameter/2, diameter, diameter, lastAngle, lastAngle + angle3);
        lastAngle += angle3;
                       
        popStyle();
        popMatrix();
    }
  
    public void drawBox()
    {
        pushMatrix();
        pushStyle();
    
        noStroke();              
    
        //border
        fill(borderColor);
        rectMode(CORNER);
        rect(boxX - 8, boxY - 8, boxW + 16, boxH + 16);
    
        //draw box
        fill(boxColour);
        rectMode(CORNER);
        rect(boxX, boxY, boxW, boxH);   
    
        popStyle();
        popMatrix();
    }
  
    public void drawHeroPortraits()
    {
        pushMatrix();
        pushStyle();               
    
        noStroke();
    
        //border
        fill(borderColor);
        rectMode(CORNER);
    
        for (int i = 0; i < 5; i++)        
          rect(heroX - 3, heroY + (i * (heroH + outsideBorder/4)) - 3, heroW + 6, heroH + 6);         
    
        int imgCounter = 0;
    
        //display previously loaded images
        for (PImage img : portraits)                          
          image(img, heroX, heroY + (imgCounter++ * (heroH + outsideBorder/4)), heroW, heroH);                      
    
        ellipseMode(CORNER);
    
    
        popStyle();            
        popMatrix();
    }
  
    public void drawHeroLevels()
    {
        pushMatrix();
        pushStyle();
    
        noStroke();    
    
        int counter = 0;
        int spaceIncreasePerHero = heroH + outsideBorder/4; 
    
        for (String hero : teams.get(team).teamHeroes)
        {        
          ellipseMode(CORNER);
    
          //outer circle
          fill(color(90, 83, 14));
          ellipse(heroX - (heroW/4)/2 - 4, heroY + heroH - (heroW/4) + (counter * spaceIncreasePerHero) - 4, heroW/4 + 8, heroW/4 + 8);
    
          //inner circle
          fill(color(118, 109, 18));          
          ellipse(heroX - (heroW/4)/2, heroY + heroH - (heroW/4) + (counter * spaceIncreasePerHero), heroW/4, heroW/4);
    
          //level
          fill(255);
          textAlign(CENTER, CENTER);
          text(heroes.get(hero).level, heroX, heroY + heroH - (heroW/4) + (heroW/4)/2 + (counter * spaceIncreasePerHero));
    
          counter++;
        } 
    
        popStyle();            
        popMatrix();
    }
  
    public void drawHeroNames()
    {
        pushMatrix();
        pushStyle();
    
        noStroke();        
    
        int counter = 0;
        int spaceIncreasePerHero = heroH + outsideBorder/4;
    
        for (String hero : teams.get(team).teamHeroes)
        {        
          fill(heroes.get(hero).colour);
          text(hero, heroNameX, heroNameY + (counter++ * spaceIncreasePerHero));
        }      
    
        popStyle();
        popMatrix();
    }
  
    public void drawBarGraphs()
    {
        pushMatrix();
        pushStyle();
    
        noStroke();
    
        int counter = 0;
        int spaceIncreasePerHero = heroH + outsideBorder/4;
        for (String hero : teams.get(team).teamHeroes)
        {
          int maxKDAValue = heroes.get(hero).getMaxKDA();
          double scalingFactor = 0;                                  
    
          //scale it according to the max k/d/a value of all heroes
          if (maxHeroKDAValue != 0)
            scalingFactor = barW / maxHeroKDAValue;
    
          //compute length of the bars based on the largest k/d/a value
          int killsBarW = (int)scalingFactor * heroes.get(hero).kills;            
          int deathsBarW = (int)scalingFactor * heroes.get(hero).deaths;            
          int assistsBarW = (int)scalingFactor * heroes.get(hero).assists;            
    
          //if any of them are 0 then give them a small default value so that you can see a sliver of the bar 
          if (killsBarW == 0) killsBarW = 4;
          if (deathsBarW == 0) deathsBarW = 4;
          if (assistsBarW == 0) assistsBarW = 4;
    
          //kills bar and label
          fill(barColour1);
          rect(barX1, barY1 + (counter * spaceIncreasePerHero), killsBarW, barH, 3);
          fill(255);
          textFont(optimusPrincepsFont, barH);
          text("K : " + heroes.get(hero).kills, barX1 + outsideBorder/8, (barY1 + (counter * spaceIncreasePerHero)) + barH - 2);
    
          //deaths bar and label
          fill(barColour2);
          rect(barX2, barY2 + (counter * spaceIncreasePerHero), deathsBarW, barH, 3);
          fill(255);
          textFont(optimusPrincepsFont, barH);
          text("D : " + heroes.get(hero).deaths, barX2 + outsideBorder/8, (barY2 + (counter * spaceIncreasePerHero)) + barH - 2);
    
          //assists bar and label
          fill(barColour3);
          rect(barX3, barY3 + (counter * spaceIncreasePerHero), assistsBarW, barH, 3);
          fill(255);
          textFont(optimusPrincepsFont, barH);
          text("A : " + heroes.get(hero).assists, barX3 + outsideBorder/8, (barY3 + (counter * spaceIncreasePerHero)) + barH - 2);
    
          counter++;
        }
    
        popStyle();
        popMatrix();
    }
  
    public void hoverOverHero(int hero)
    {
        pushMatrix();
        pushStyle();
    
        noStroke();        
        rectMode(CORNER);
    
        if(heroViewToggle)
        {
            fill(color(255, 77));
            rect(boxX, heroY + ((hero - 1) * (heroH + outsideBorder/4)) - 3, boxW, heroH + 6, 2); 
        }
        else
        {
            int littleW = (boxW - outsideBorder)/5;
            int littleH = outsideBorder + outsideBorder/3;
            fill(color(255, 77));
            rect(boxX + ((hero - 1) * (outsideBorder/4 + littleW)), boxY - (2 * outsideBorder), littleW, littleH);
        } 
    
        popStyle();
        popMatrix();
    }
  
    public void singleClicked(int newHero)
    {
        if(!heroViewToggle) //detailed hero view
        {
            if(newHero == 1)
               heroViewToggle = true;
            else
            {
                if(toggledHero >= newHero)
                {
                    toggledHero = newHero - 1;
                    toggledHeroName = teams.get(team).teamHeroes.get(toggledHero - 1);
                }
                else
                {
                    toggledHero = newHero;
                    toggledHeroName = teams.get(team).teamHeroes.get(toggledHero - 1);
                }
            }
        }
    }
  
    public void doubleClickedHero(int hero)
    {
        if(heroViewToggle)
        {
            heroViewToggle = false;
            toggledHero = hero;
            toggledHeroName = teams.get(team).teamHeroes.get(toggledHero - 1);
        }
    }
  
    public int onTopOfVisual()
    {
        if (heroViewToggle) //hero overview
        {
          for (int i = 0; i < 5; i++)
          {
            //                System.out.println("mousex: " + mouseX + " " + heroX + " < " + (heroX + boxW) + " mouseY: " + mouseY + " " + (heroY + (i+1 * (heroH + outsideBorder/4))) + " < " + heroY + (i * (heroH + outsideBorder/4)));
            if ((mouseX > heroX && mouseX < (heroX + boxW)) && //w
            (mouseY > heroY + (i * (heroH + outsideBorder/4)) && mouseY < heroY + ((i+1) * (heroH + outsideBorder/4)))) //h 
              return i + 1;
          }
        }
        else //hero details
        {
            int littleW = (boxW - outsideBorder)/5;
            int littleH = outsideBorder + outsideBorder/3;
            
            for(int i = 0; i < 5; i++)
            {
                if(((mouseX > boxX + (i * (outsideBorder/4 + littleW))) && (mouseX < boxX + (i * (outsideBorder/4 + littleW)) + littleW)) &&
                   ((mouseY > boxY - (2 * outsideBorder)) && (mouseY < boxY - (2 * outsideBorder) + littleH)))
                {
                    return i + 1;
                }
//                    image(smallPortraits.get(i), boxX + (counter++ * (outsideBorder/4 + littleW)), boxY - (2 * outsideBorder), littleW, littleH);
            } 
        }
        return 0;
    }
}

public interface Visual
{   
    public void draw(); 
    public int onTopOfVisual();
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "Dota2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
