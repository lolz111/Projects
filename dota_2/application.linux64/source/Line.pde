public class Line
{
    public Entity entity;      
    String type; 
    
    public XYChart line;
    public int lineX, lineY, lineW, lineH;
    public color colour;
  
    public ArrayList<PVector> points;
    public int[] barSet;
    public int maxX, maxY;        
    public int total;
    public int graphEventPosition;
    
    public Line(Entity entity, String type, color c, int lineX, int lineY, int lineW, int lineH)
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
