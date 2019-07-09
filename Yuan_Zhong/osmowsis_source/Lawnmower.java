package OsMowSis;


import java.util.HashMap;


public class Lawnmower {
	//hashmap to define mower movement on coordinate for each direction
    private HashMap<String, Integer> xDIR_MAP;
    private HashMap<String, Integer> yDIR_MAP;
    
	private Integer[] currentRelativeLocation;
	private String direction;
	private String[][] knowledgeMap;
	
	//mapWidth and mapHeight to track current map size
	private Integer mapWidth;
	private Integer mapHeight;
	
	//elements code
	private static final String EMPTY = "empty";
	private static final String GRASS = "grass";
	private static final String UNKNOWN = "unknown";
	private static final String OUTSIDE = "out";
	
	//action code
	private static final String SCAN = "scan";

	

	public Lawnmower(int id, String mowerDirection){	
		//set hashmap for mower movement
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
        
		
		//knowledgeMap to be initialized as 5x5 grid
		knowledgeMap = new String[5][5];
		mapWidth = 5;
		mapHeight = 5;
		//fill the map with unknown
		for (int i = 0; i < mapWidth; i++) {
			for (int j = 0; j < mapHeight; j++) {
				knowledgeMap[i][j] = Lawnmower.UNKNOWN;
			}
		}
		
		direction = mowerDirection.toLowerCase();
		//put mower into map at different coordinate depend on direction
		switch(direction){
			case "north":
				knowledgeMap[2][1] = Lawnmower.EMPTY; 
				currentRelativeLocation = new Integer[2];
				currentRelativeLocation[0] = 2;
				currentRelativeLocation[1] = 1;
				break;
			case "northeast":
				knowledgeMap[1][1] = Lawnmower.EMPTY; 
				currentRelativeLocation = new Integer[2];
				currentRelativeLocation[0] = 1;
				currentRelativeLocation[1] = 1;
				break;
			case "east":
				knowledgeMap[1][2] = Lawnmower.EMPTY; 
				currentRelativeLocation = new Integer[2];
				currentRelativeLocation[0] = 1;
				currentRelativeLocation[1] = 2;
				break;
			case "southeast":
				knowledgeMap[1][3] = Lawnmower.EMPTY; 
				currentRelativeLocation = new Integer[2];
				currentRelativeLocation[0] = 1;
				currentRelativeLocation[1] = 3;
				break;
			case "south":
				knowledgeMap[2][3] = Lawnmower.EMPTY; 
				currentRelativeLocation = new Integer[2];
				currentRelativeLocation[0] = 2;
				currentRelativeLocation[1] = 3;
				break;
			case "southwest":
				knowledgeMap[3][3] = Lawnmower.EMPTY; 
				currentRelativeLocation = new Integer[2];
				currentRelativeLocation[0] = 3;
				currentRelativeLocation[1] = 3;
				break;
			case "west":
				knowledgeMap[3][2] = Lawnmower.EMPTY; 
				currentRelativeLocation = new Integer[2];
				currentRelativeLocation[0] = 3;
				currentRelativeLocation[1] = 2;
				break;
			case "northwest":
				knowledgeMap[3][1] = Lawnmower.EMPTY; 
				currentRelativeLocation = new Integer[2];
				currentRelativeLocation[0] = 3;
				currentRelativeLocation[1] = 1;
				break;
		}

	}

	public String[] determineAction(){
		String[] action;
		//get direction coordinate
		int xDir = xDIR_MAP.get(direction);
		int yDir = yDIR_MAP.get(direction);

		//get the next square element on the current direction
		int x = this.currentRelativeLocation[0];
		int y = this.currentRelativeLocation[1];
		int nextSquareX = x + xDir;
		int nextSquareY = y + yDir;
		String nextSquare = this.checkSquare(nextSquareX, nextSquareY);
		
		
		//check if it is out of the map, if so, resize map
		if (nextSquare.equals(Lawnmower.OUTSIDE)){
			this.resizeMap(xDir, yDir);
			//update next square coordinates
			x = this.currentRelativeLocation[0];
			y = this.currentRelativeLocation[1];
			nextSquareX = x + xDir;
			nextSquareY = y + yDir; 
			nextSquare = this.checkSquare(nextSquareX, nextSquareY);
		}
				
		//if the next square on current direction is unknown, scan for information
		if (nextSquare.equals(Lawnmower.UNKNOWN)){
			action = new String[1];
			action[0] = Lawnmower.SCAN;
			return action;
		}
		
		//determine move distance
		int distance = 0;
		//if the next square on current direction is grass or empty, go to the farthest grass
		if (nextSquare.equals(Lawnmower.GRASS) || nextSquare.equals(Lawnmower.EMPTY)){
			distance += 1;//move one step
			//check the next next square
			String nextNext = this.checkSquare(xDir*2+x, yDir*2+y);
			if (nextNext.equals(Lawnmower.GRASS) || nextNext.equals(Lawnmower.EMPTY)){
				distance += 1;//move another step
			}
			
		}
		
		
		
		this.move(xDir, yDir, distance);
		action = new String[3];
		action[0] = this.direction; //move direction
		action[1] = Integer.toString(distance);
		
		
		//check if should change direction after move
		x = this.currentRelativeLocation[0];
		y = this.currentRelativeLocation[1];
		String newDirection = this.reorient(x, y, this.direction);
		this.direction = newDirection;	//update direction	
		action[2] = newDirection; 
		
		return action;

	}
	
	//added a function to check the square element 
	public String checkSquare(int x, int y){
		if (x<0 || y<0 || x>=this.mapWidth || y>=this.mapHeight){
			return Lawnmower.OUTSIDE;
		} else {
			return this.knowledgeMap[x][y];
		}
	}
	
	public void move(int xDir, int yDir, int distance){
		if (distance==0) return;
		
		//get mower relative location
		int x = this.currentRelativeLocation[0];
		int y = this.currentRelativeLocation[1];
		for (int i=1;i<=distance;i++){
			//set passed squares to empty
			this.knowledgeMap[xDir*distance+x][yDir*distance+y] = Lawnmower.EMPTY;
		}
		//set mower's new location
		this.currentRelativeLocation[0] += xDir*distance;
		this.currentRelativeLocation[1] += yDir*distance;
	}
	
	public void scan(String[] scanedInfo){		
		String[] directions = {"north","northeast","east","southeast","south","southwest","west","northwest"};
		for(int i=0;i<8;i++){			
			//mower location
			int x = this.currentRelativeLocation[0];
			int y = this.currentRelativeLocation[1];
			//location of square to be checked
			String direction = directions[i];
			int xDir = this.xDIR_MAP.get(direction);
			int yDir = this.yDIR_MAP.get(direction);
			String square = this.checkSquare(x + xDir, y + yDir);
			if (square.equals(Lawnmower.OUTSIDE)){
				this.resizeMap(xDir, yDir);
				//update mower's relative location
				x = this.currentRelativeLocation[0];
				y = this.currentRelativeLocation[1];
			}
			//update knowledge map
			String element = scanedInfo[i];
			this.knowledgeMap[x+xDir][y+yDir] = element;
			System.out.print(element);
			if(i!=7) System.out.print(",");			
		}
		System.out.print("\n");
	}
	
	
	public void resizeMap(int xDir, int yDir){
		int newWidth = this.mapWidth + Math.abs(xDir);
		int newHeight = this.mapHeight + Math.abs(yDir);
		String[][] newMap = new String[newWidth][newHeight];
		//fill the map with unknown
		for (int i = 0; i < newWidth; i++) {
			for (int j = 0; j < newHeight; j++) {
				newMap[i][j] = Lawnmower.UNKNOWN;
			}
		}
		
		//transfer info to new map
		int a = 0;
		int b = 0;
		if (xDir<0) a = 1;	
		if (yDir<0) b = 1;			
		for (int i=0; i<this.mapWidth; i++){
			for (int j=0; j<this.mapHeight; j++){
				newMap[i+a][j+b] = this.knowledgeMap[i][j];
			}
		}
		this.mapWidth = newWidth;
		this.mapHeight = newHeight;
		this.knowledgeMap = newMap;	
		this.currentRelativeLocation[0] += a;
		this.currentRelativeLocation[1] += b;
	}
	
	//check if should change direction at coordinate (x,y) and find the first available direction on clockwise  
	public String reorient(int x, int y, String direction){
		int xDir = xDIR_MAP.get(direction);
		int yDir = yDIR_MAP.get(direction);
		
		String nextSquare = this.checkSquare(x+xDir, y+yDir);
	
		if (nextSquare.equals(Lawnmower.GRASS) || nextSquare.equals(Lawnmower.UNKNOWN) || nextSquare.equals(Lawnmower.OUTSIDE)){
			return direction; //not change direction
		}
		
		String[] clockwise = {"north","northeast","east","southeast","south","southwest","west","northwest"};
		int i;
		for (i=0; i<8;i++){
			if (clockwise[i].equals(direction)) break;			
		} 
		
		String newDirection = direction;
		//start from clockwise
		//first face to grass square or unknown square or outside square
		for (int j=1;j<8;j++){
			newDirection = clockwise[(i+j)%8];
			int xDir_temp = xDIR_MAP.get(newDirection);
			int yDir_temp = yDIR_MAP.get(newDirection);
			String nextSquare_temp = this.checkSquare(x+xDir_temp, y+yDir_temp);
			if (nextSquare_temp.equals(Lawnmower.GRASS) || nextSquare_temp.equals(Lawnmower.UNKNOWN) || nextSquare_temp.equals(Lawnmower.OUTSIDE)){
				return newDirection;
			}
		}
		
		//second face to empty + grass square
		for (int j=1;j<8;j++){
			newDirection = clockwise[(i+j)%8];
			int xDir_temp = xDIR_MAP.get(newDirection);
			int yDir_temp = yDIR_MAP.get(newDirection);
			String nextSquare_temp = this.checkSquare(x+xDir_temp, y+yDir_temp);
			String nextNext_temp = this.checkSquare(xDir_temp*2+x, yDir_temp*2+y);
			if (nextSquare_temp.equals(Lawnmower.EMPTY) && nextNext_temp.equals(Lawnmower.GRASS)){
				return newDirection;
			}
		}
		
		//third face to empty + unknown square or outside square
		for (int j=1;j<8;j++){
			newDirection = clockwise[(i+j)%8];
			int xDir_temp = xDIR_MAP.get(newDirection);
			int yDir_temp = yDIR_MAP.get(newDirection);
			String nextSquare_temp = this.checkSquare(x+xDir_temp, y+yDir_temp);
			String nextNext_temp = this.checkSquare(xDir_temp*2+x, yDir_temp*2+y);
			if (nextSquare_temp.equals(Lawnmower.EMPTY) && (nextNext_temp.equals(Lawnmower.UNKNOWN) || nextNext_temp.equals(Lawnmower.OUTSIDE))){
				return newDirection;
			}
		}		
		
		//fourth face to empty square
		for (int j=1;j<8;j++){
			newDirection = clockwise[(i+j)%8];
			int xDir_temp = xDIR_MAP.get(newDirection);
			int yDir_temp = yDIR_MAP.get(newDirection);
			String nextSquare_temp = this.checkSquare(x+xDir_temp, y+yDir_temp);
			if (nextSquare_temp.equals(Lawnmower.EMPTY)) break;
		}		
		
		return newDirection;
		
		/*
		String newDirection = clockwise[(i+1)%8];
		//call recursively to check if this direction available	        		
		return reorient(x,y, newDirection);
		*/
	}

}
