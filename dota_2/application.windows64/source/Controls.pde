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
    public color goldColor;
    
    //kills button
    PImage kills;
    public int killsButtonX, killsButtonY, killsButtonW, killsButtonH;
    public boolean killsButtonToggle;
    public color killsColor;
    
    //deaths button
    PImage deaths;
    public int deathsButtonX, deathsButtonY, deathsButtonW, deathsButtonH;
    public boolean deathsButtonToggle;
    public color deathsColor;
    
    //assists button
    PImage assists;
    public int assistsButtonX, assistsButtonY, assistsButtonW, assistsButtonH;
    public boolean assistsButtonToggle;
    public color assistsColor;
    
    //radiant button
//    PImage radiant;
    public int radiantButtonX, radiantButtonY, radiantButtonW, radiantButtonH;
    public boolean radiantButtonToggle;
    public color radiantColor;
    
    //dire button
//    PImage dire;
    public int direButtonX, direButtonY, direButtonW, direButtonH;
    public boolean direButtonToggle;
    public color direColor;
    
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

