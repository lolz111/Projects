import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.awt.event.MouseEvent;

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
