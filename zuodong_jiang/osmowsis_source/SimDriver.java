import java.util.Scanner;
import java.util.HashMap;
import java.util.Random;
import java.io.*;

public class SimDriver {
    private static Random randGenerator;

    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 100;

    private Integer lawnHeight;
    private Integer lawnWidth;
    private Integer totalturns;
    private Integer totalsquare;
    private Integer totalgrass;
    private Integer cuttedgrass;
    private Integer[][] lawnInfo;
    private Integer[][] lawnInfom;
    private Integer mowerX, mowerY;
    private String mowerDirection;
    private HashMap<String, Integer> xDIR_MAP;
    private HashMap<String, Integer> yDIR_MAP;
    private HashMap<Integer,String> direction;
    private HashMap<String,Integer> mapdirection;
    private HashMap<Integer, String>surrounding;
    private Boolean changeDirection;
    private Boolean skipScan;
    private String trackAction;
    private Integer trackMoveDistance;
    private String trackNewDirection;
    private String trackMoveCheck;
    private String trackScanResults;

    private final int EMPTY_CODE = 0;
    private final int GRASS_CODE = 1;
    private final int CRATER_CODE = 2;
    private final int FENCE_CODE = 3;
    public SimDriver() {
        randGenerator = new Random();

        lawnHeight = 0;
        lawnWidth = 0;
        lawnInfo = new Integer[DEFAULT_WIDTH][DEFAULT_HEIGHT];
        totalturns=0;
        cuttedgrass=0;
        mowerX = -1;
        mowerY = -1;
        mowerDirection = "North";

        xDIR_MAP = new HashMap<>();
        xDIR_MAP.put("north", 0);
        xDIR_MAP.put("northeast", 1);
        xDIR_MAP.put("east", 1);
        xDIR_MAP.put("southeast", 1);
        xDIR_MAP.put("south", 0);
        xDIR_MAP.put("southwest", -1);
        xDIR_MAP.put("west", -1);
        xDIR_MAP.put("northwest", -1);

        yDIR_MAP = new HashMap<>();
        yDIR_MAP.put("north", 1);
        yDIR_MAP.put("northeast", 1);
        yDIR_MAP.put("east", 0);
        yDIR_MAP.put("southeast", -1);
        yDIR_MAP.put("south", -1);
        yDIR_MAP.put("southwest", -1);
        yDIR_MAP.put("west", 0);
        yDIR_MAP.put("northwest", 1);
        
        direction = new HashMap<>();
        direction.put(0,"north");
        direction.put(1,"northeast");
        direction.put(2,"east");
        direction.put(3,"southeast");
        direction.put(4,"south");
        direction.put(5,"southwest");
        direction.put(6,"west");
        direction.put(7,"northwest");
        
        mapdirection = new HashMap<>();
        mapdirection.put("north",0);
        mapdirection.put("northeast",1);
        mapdirection.put("east",2);
        mapdirection.put("southeast",3);
        mapdirection.put("south",4);
        mapdirection.put("southwest",5);
        mapdirection.put("west",6);
        mapdirection.put("northwest",7);
        
        surrounding = new HashMap<>();
        surrounding.put(0,"empty");
        surrounding.put(1, "grass");
        surrounding.put(2, "crater");
        surrounding.put(3, "fence");
        changeDirection=false;
        skipScan=false;
    }

    public Integer uploadStartingFile(String testFileName) {
        final String DELIMITER = ",";
        int numturns =0;
        try {
            Scanner takeCommand = new Scanner(new File(testFileName));
            String[] tokens;
            int i, j, k;

            // read in the lawn information
            tokens = takeCommand.nextLine().split(DELIMITER);
            lawnWidth = Integer.parseInt(tokens[0]);
            tokens = takeCommand.nextLine().split(DELIMITER);
            lawnHeight = Integer.parseInt(tokens[0]);

            // generate the lawn information
            lawnInfo = new Integer[lawnWidth][lawnHeight];
            lawnInfom = new Integer[lawnWidth][lawnHeight];
            
            
            
            for (i = 0; i < lawnWidth; i++) {
                for (j = 0; j < lawnHeight; j++) {
                    lawnInfo[i][j] = GRASS_CODE;
                }
            }

            // read in the lawnmower starting information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numMowers = Integer.parseInt(tokens[0]);
            for (k = 0; k < numMowers; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                mowerX = Integer.parseInt(tokens[0]);
                mowerY = Integer.parseInt(tokens[1]);
                mowerDirection = tokens[2];

                // mow the grass at the initial location
                lawnInfo[mowerX][mowerY] = EMPTY_CODE;
            }

            // read in the crater information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numCraters = Integer.parseInt(tokens[0]);
            for (k = 0; k < numCraters; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);

                // place a crater at the given location
                lawnInfo[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])] = CRATER_CODE;
            }
            totalsquare=lawnWidth*lawnHeight;
            totalgrass=totalsquare-numCraters;
            cuttedgrass=1;
            tokens = takeCommand.nextLine().split(DELIMITER);
            numturns = Integer.parseInt(tokens[0]);
            takeCommand.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
        return numturns;
    }

    public void pollMowerForAction() {
      //  int moveRandomChoice;
     //   moveRandomChoice = randGenerator.nextInt(100);
          if (trackAction!="scan"&skipScan==false) {
            // select scanning as the action
            trackAction = "scan";

        } else {
            // select moving forward and the turning as the action
            trackAction = "move";
          // determine a distance
            if (changeDirection)
            { trackMoveDistance = 0;
                skipScan=true;}
            else 
            	{trackMoveDistance = 1;
            	 skipScan=false;  
            	}
            }

        }
    

    public void validateMowerAction() {
        int xOrientation, yOrientation;
        if (trackAction.equals("scan")) {
            // in the case of a scan, return the information for the eight surrounding squares
            // always use a northbound orientation
        	totalturns++;
            String trackScanResults1 ="";
            
            for (int i=0;i<8;i++) { 	                     // 8 directions
                int a =mowerX +xDIR_MAP.get(direction.get(i));
                int b =mowerY + yDIR_MAP.get(direction.get(i));    
                if (a>= 0 & a < lawnWidth & b >= 0 & b < lawnHeight) {
            //    	lawnInfom[x[i]][y[i]]=lawnInfo[x[i]][y[i]];
                    trackScanResults1+=Integer.toString(lawnInfo[a][b]);
                    }
                else
                	trackScanResults1+=Integer.toString(FENCE_CODE);
            }
           
               int index=mapdirection.get(mowerDirection);
               char c=trackScanResults1.charAt(index);
               int a = trackScanResults1.indexOf((char)'1');    // find first grasscode
               int b = trackScanResults1.indexOf((char)'0');
               int d = trackScanResults1.indexOf((char)'0', trackScanResults1.indexOf((char)'0') +1);
               if (a>=0)     	                          // find it
                   if (c==(char)'1')            //default direction
                       trackNewDirection=mowerDirection;
                   else 
                   { trackNewDirection= direction.get(a); 
                       changeDirection=true;
                       }
               else if (b>=0)
                   if (c==(char)'0')            //default direction
                       trackNewDirection=mowerDirection;
                   else 
                   { 

                	   if (d>=0)
                	      trackNewDirection= direction.get(d);
                	   else
                		   trackNewDirection= direction.get(b); 
  
                      changeDirection=true;
                      }
               else trackNewDirection="noway";
               trackScanResults="";
               for(int i =0;i<trackScanResults1.length();i++) {
            	   char x=trackScanResults1.charAt(i);
            	   int y=Character.getNumericValue(x);
            	   trackScanResults=trackScanResults+surrounding.get(y)+",";
            	   
               }
               trackScanResults=trackScanResults.substring(0,trackScanResults.length()-1);
             
               
               
       }  
        else if (trackAction.equals("move")) {
            // in the case of a move, ensure that the move doesn't cross craters or fences
        	totalturns++;	
        	
        	if (trackNewDirection.equals("noway")){trackMoveCheck="crash";}	
            if (changeDirection)mowerDirection=trackNewDirection;
            xOrientation = xDIR_MAP.get(mowerDirection);
            yOrientation = yDIR_MAP.get(mowerDirection);
            // just for this demonstration, allow the mower to change direction
            // even if the move forward causes a crash
            int newSquareX = mowerX + trackMoveDistance * xOrientation;
            int newSquareY = mowerY + trackMoveDistance * yOrientation;

            if (newSquareX >= 0 & newSquareX < lawnWidth & newSquareY >= 0 & newSquareY < lawnHeight) {
                mowerX = newSquareX;
                mowerY = newSquareY;
                trackMoveCheck = "ok";
                // update lawn status
                if (lawnInfo[mowerX][mowerY]==GRASS_CODE) {
                	cuttedgrass++;
                    lawnInfo[mowerX][mowerY] = EMPTY_CODE;
                    }
            }
            changeDirection=false;

            
        	}                
        }
    

    
    
    public boolean displayActionAndResponses() {
        // display the mower's actions
        System.out.print(trackAction);
        if (trackAction.equals("move")) {
            System.out.println("," + trackMoveDistance + "," + trackNewDirection);
        } else {
            System.out.println();
        }

        // display the simulation checks and/or responses
        if (trackAction.equals("move") | trackAction.equals("turn_off")) {
            System.out.println(trackMoveCheck);
        } else if (trackAction.equals("scan")) {
            System.out.println(trackScanResults);
        } else {
            System.out.println("action not recognized");
        }
        
        if (cuttedgrass==totalgrass)
            return true;
        else 
        	return false;
    }

    public void finalresult() {
    	System.out.println(totalsquare+","+totalgrass+","+cuttedgrass+","+totalturns);
    
    }
    
    private void renderHorizontalBar(int size) {
        System.out.print(" ");
        for (int k = 0; k < size; k++) {
            System.out.print("-");
        }
        System.out.println("");
    }

    public void renderLawn() {
        int i, j;
        int charWidth = 2 * lawnWidth + 2;

        // display the rows of the lawn from top to bottom
        for (j = lawnHeight - 1; j >= 0; j--) {
            renderHorizontalBar(charWidth);

            // display the Y-direction identifier
            System.out.print(j);

            // display the contents of each square on this row
            for (i = 0; i < lawnWidth; i++) {
                System.out.print("|");

                // the mower overrides all other contents
                if (i == mowerX & j == mowerY) {
                    System.out.print("M");
                } else {
                    switch (lawnInfo[i][j]) {
                        case EMPTY_CODE:
                            System.out.print(" ");
                            break;
                        case GRASS_CODE:
                            System.out.print("g");
                            break;
                        case CRATER_CODE:
                            System.out.print("c");
                            break;
                        default:
                            break;
                    }
                }
            }
            System.out.println("|");
        }
        renderHorizontalBar(charWidth);

        // display the column X-direction identifiers
        System.out.print(" ");
        for (i = 0; i < lawnWidth; i++) {
            System.out.print(" " + i);
        }
        System.out.println("");

        // display the mower's direction
        System.out.println("dir: " + mowerDirection);
        System.out.println("");
    }

}