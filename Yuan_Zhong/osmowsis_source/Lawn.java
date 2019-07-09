package OsMowSis;


import java.util.HashMap;


public class Lawn {	
	//hashmap to define mower movement on coordinate for each direction
    private HashMap<String, Integer> xDIR_MAP;
    private HashMap<String, Integer> yDIR_MAP;
	

	private String[][] lawnMap;
	private Integer lawnWidth;
	private Integer lawnHeight;
	private Integer lawnmowerNumberLeft;
	private Lawnmower[] lawnmowers;
	//In the original design the location of mowers are tracked directly in lawnMap, but due to efficiency it's better to have an individual array to record mower locations.
	private Integer[][] mowerLocations;
	private Integer craterNumber;
	private Integer originalGrassNumber;
	private Integer grassNumberCut;

	//elements code
	private static final String EMPTY = "empty";
	private static final String GRASS = "grass";
	private static final String CRATER = "crater";
	private static final String MOWER = "mower";
	private static final String FENCE = "fence";
	
	
	public Lawn(Integer lawnWidth, Integer lawnHeight, Integer mowerNo,String[][] mowerLD, Integer craterNo, Integer[][] craterLocation){	
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
        
        
		lawnMap = new String[lawnWidth][lawnHeight];
		//fill the map with grass
		for (int i = 0; i < lawnWidth; i++) {
            for (int j = 0; j < lawnHeight; j++) {
                lawnMap[i][j] = Lawn.GRASS;
            }
        }
		
		this.lawnWidth = lawnWidth;
		this.lawnHeight = lawnHeight;
		lawnmowerNumberLeft = mowerNo;
		lawnmowers = new Lawnmower[mowerNo];
		mowerLocations = new Integer[mowerNo][2];
		for(int id=0; id<mowerNo;id++){
			String mowerDirection = mowerLD[id][2]; //get mower direction 
			lawnmowers[id] = new Lawnmower(id, mowerDirection);
			try {
				int x = Integer.parseInt(mowerLD[id][0]);
				int y = Integer.parseInt(mowerLD[id][1]);
				mowerLocations[id][0] = x;
				mowerLocations[id][1] = y;
				lawnMap[x][y] = Lawn.MOWER;//put mower into lawnMap	
			} catch (Exception e) {
	            e.printStackTrace();
	            System.out.println();
	        }			
		}
		craterNumber = craterNo;
		//fill in the map with craters
		for(int i=0; i<craterNo; i++){
			int x = craterLocation[i][0];
			int y = craterLocation[i][1];
			lawnMap[x][y] = Lawn.CRATER;
		}
		originalGrassNumber = lawnWidth * lawnHeight - craterNumber;
		grassNumberCut = mowerNo; //grass will be cut when put a mower in that square		


	}
	
	//added a function to act for a round
	public void actOneRound(){
		//check if no available mower
		if(this.lawnmowerNumberLeft.equals(0)) return;
					
		for (int id=0;id<this.lawnmowers.length;id++){
			Lawnmower currentMower = this.lawnmowers[id];
			if (currentMower==null) continue;//if mower is deleted from lawn
									
			String[] action = currentMower.determineAction();
			
			//scan
			if (action.length==1){
				System.out.println(action[0]);
				this.scanedByMower(id);				
			}
			
			//move
			if (action.length==3){
				String moveDirection = action[0];
				Integer distance = Integer.parseInt(action[1]);
				String reorient = action[2];
				System.out.println("move," + distance + "," + reorient);
				Boolean result = this.moveMower(id, distance, moveDirection);
				if (result) {
					System.out.println("ok");
				} else {
					System.out.println("crash");
					this.removeMower(id);					
				}
				
			}
		}
	}
	
	
	//move one mower in one round
	public Boolean moveMower(Integer mowerID, Integer distance, String direction){
		Lawnmower currentMower = this.lawnmowers[mowerID];
		if (currentMower==null) return false;
		
		//not move
		if (distance.equals(0)) return true;
		
		//get mower location
		int x = this.mowerLocations[mowerID][0];
		int y = this.mowerLocations[mowerID][1];
		//get move direction
		int xDir = this.xDIR_MAP.get(direction);
		int yDir = this.yDIR_MAP.get(direction);
		
		for (int i=0;i<distance;i++){
			String nextSquare = this.checkSquare(x+xDir, y+yDir);
			if (nextSquare.equals(Lawn.GRASS) || nextSquare.equals(Lawn.EMPTY)){
				//move mower and update location
				this.lawnMap[x][y] = Lawn.EMPTY;
				x += xDir;
				y += yDir;
				this.mowerLocations[mowerID][0] = x;
				this.mowerLocations[mowerID][1] = y;
				this.lawnMap[x][y] = Lawn.MOWER;
				if (nextSquare.equals(Lawn.GRASS)){
					this.lawnMap[x][y] = Lawn.EMPTY;
					this.grassNumberCut += 1;
				}
								
			} else {
				return false;
			}
			
		}		
		return true;
			
	}
	
	//transfer scanned info to mower
	public void scanedByMower(Integer mowerID){
		//get mower's absolute location
		Integer x = this.mowerLocations[mowerID][0];
		Integer y = this.mowerLocations[mowerID][1];
		
		String[] info = new String[8];
		//check from north and clockwise
		String[] directions = {"north","northeast","east","southeast","south","southwest","west","northwest"};
		for(int i=0;i<8;i++){
			String direction = directions[i];
			int xDir = this.xDIR_MAP.get(direction);
			int yDir = this.yDIR_MAP.get(direction);
			info[i] = this.checkSquare(x+xDir, y+yDir);
		}	
		Lawnmower currentMower = this.lawnmowers[mowerID];
		currentMower.scan(info);
	}
	
	//added a function to check the square element 
	public String checkSquare(int x, int y){
		if (x<0 || y<0 || x>=this.lawnWidth || y>=this.lawnHeight){
			return Lawn.FENCE;
		} else {
			return this.lawnMap[x][y];
		}
	}
	
	public void removeMower(Integer mowerID){
		this.lawnmowers[mowerID] = null;
		int x = this.mowerLocations[mowerID][0];
		int y = this.mowerLocations[mowerID][1];
		this.lawnMap[x][y] = Lawn.EMPTY;
		this.mowerLocations[mowerID][0] = null; 
		this.mowerLocations[mowerID][1] = null; 
		this.lawnmowerNumberLeft -= 1; 		
	}
		
	public Integer checkMowerLeft(){
		return this.lawnmowerNumberLeft;
	}
	
	public Boolean checkFinish(){
		if (this.originalGrassNumber.equals(this.grassNumberCut)){
			return true;
		} else if(this.originalGrassNumber<this.grassNumberCut){
			System.out.println("error");
			return null;
		} else {
			return false;
		}
	}
	
	//getters
	public Integer getOriginalGrassNumber(){
		return this.originalGrassNumber;
	}

	public Integer getGrassNumberCut(){
		return this.grassNumberCut;
	}
	
	public Integer getTotalNumberSquares(){
		return (this.lawnWidth * this.lawnHeight);
	}
}
