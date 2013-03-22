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
        
        if ((double)(boxW - outsideBorder/2)/(boxH/3) < 5.0)
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
        
        if ((double)timeW/timeH < 2.0)
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

