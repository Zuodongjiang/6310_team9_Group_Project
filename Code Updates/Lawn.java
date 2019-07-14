package team_9;


import java.util.HashMap;




public class Lawn {	
	//hashmap to define mower movement on coordinate for each direction
    private static HashMap<String, Integer> xDIR_MAP = new HashMap<>();;
    private static HashMap<String, Integer> yDIR_MAP = new HashMap<>();;
    // type Enum: empty, grass, crate...
 	private static HashMap<Integer, String> type = new HashMap<>();;
 	
    
	private int lawnHeight; //1~10 inclusive
	private int lawnWidth;  //1~15 inclusive
	private int[][] lawnMap;
	private int[][] energyLocation;

	private int craterNumber;
	private int originalGrassNumber;
	private int grassNumberCut;

	//elements code
	private final int EMPTY_CODE = 0;
	private final int GRASS_CODE = 1;
	private final int CRATER_CODE = 2;
	private final int FENCE_CODE = 3;
	private final int CHARGE_CODE = 4;
	//mower start from 5(mowerID=0)
	
	
	
	
	public Lawn(int lawnWidth, int lawnHeight, int mowerNo, int[][] mowerPosition, int craterNo, int[][] craterLocation){	
		//set hashmap for mower movement
        
        xDIR_MAP.put("north", 0);
        xDIR_MAP.put("northeast", 1);
        xDIR_MAP.put("east", 1);
        xDIR_MAP.put("southeast", 1);
        xDIR_MAP.put("south", 0);
        xDIR_MAP.put("southwest", -1);
        xDIR_MAP.put("west", -1);
        xDIR_MAP.put("northwest", -1);

        
        yDIR_MAP.put("north", 1);
        yDIR_MAP.put("northeast", 1);
        yDIR_MAP.put("east", 0);
        yDIR_MAP.put("southeast", -1);
        yDIR_MAP.put("south", -1);
        yDIR_MAP.put("southwest", -1);
        yDIR_MAP.put("west", 0);
        yDIR_MAP.put("northwest", 1);		
		
        
		type.put(EMPTY_CODE, "empty");
		type.put(GRASS_CODE, "grass");
		type.put(CRATER_CODE, "crater");
		type.put(FENCE_CODE, "fence");
		type.put(CHARGE_CODE, "charge");		
		for(int i=5;i<mowerNo+5;i++){
			String mowerName = String.format("mower_%d", i-4);
			type.put(i, mowerName);//mowerName start from 1
		}
        
		lawnMap = new int[lawnWidth][lawnHeight];
		//fill the map with grass
		for (int i = 0; i < lawnWidth; i++) {
            for (int j = 0; j < lawnHeight; j++) {
                lawnMap[i][j] = GRASS_CODE;
            }
        }
		
		this.lawnWidth = lawnWidth;
		this.lawnHeight = lawnHeight;

		//fill in the lawn location with mower 
		for(int i=0; i<mowerNo;i++){ 
			int mowerX = mowerPosition[i][0];
			int mowerY = mowerPosition[i][1];
			lawnMap[mowerX][mowerY] = i+5;			
		}
		craterNumber = craterNo;
		//fill in the map with craters
		for(int i=0; i<craterNo; i++){
			int x = craterLocation[i][0];
			int y = craterLocation[i][1];
			lawnMap[x][y] = CRATER_CODE;
		}
		originalGrassNumber = lawnWidth * lawnHeight - craterNumber;
		grassNumberCut = mowerNo; //grass will be cut when put a mower in that square		

		energyLocation = mowerPosition;
	}
	
	//transfer scanned info to mower
	public String[] scanedByMower(int mowerX, int mowerY){
		
		String[] info = new String[8];
		//check from north and clockwise
		String[] directions = {"north","northeast","east","southeast","south","southwest","west","northwest"};
		for(int i=0;i<8;i++){
			String direction = directions[i];
			int xDir = Lawn.xDIR_MAP.get(direction);
			int yDir = Lawn.yDIR_MAP.get(direction);
			info[i] = this.checkSquare(mowerX+xDir, mowerY+yDir);
		}	
		return info;
	}
	
	//a function to check the square element 
	private String checkSquare(int x, int y){
		int square = lawnMap[x][y];
		if (x<0 || y<0 || x>=this.lawnWidth || y>=this.lawnHeight){
			return type.get(FENCE_CODE);
		} 
		if (square>=5){
			//check if it contains a charger
			for(int i=0;i<energyLocation.length;i++){
				if (energyLocation[i][0]==x && energyLocation[i][1]==y){
					//mower + energy
					return String.format("%s|%s", type.get(square), type.get(CHARGE_CODE)); // mower_1|energy
				}
				
			}
		}
		return type.get(square);
		
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

