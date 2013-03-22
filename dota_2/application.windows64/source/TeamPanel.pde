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
  
    public color borderColor;
  
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

