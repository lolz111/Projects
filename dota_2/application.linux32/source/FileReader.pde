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
