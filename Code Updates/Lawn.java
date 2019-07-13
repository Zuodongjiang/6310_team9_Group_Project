package team_9;


import java.util.HashMap;


public class Lawn {	
	private Integer lawnHeight; //1~10 inclusive
	private Integer lawnWidth;  //1~15 inclusive
	private Integer[][] lawnMap;

	private Integer craterNumber;
	private Integer originalGrassNumber;
	private Integer grassNumberCut;

	//elements code
	private final int EMPTY_CODE = 0;
	private final int GRASS_CODE = 1;
	private final int CRATER_CODE = 2;
	private final int FENCE_CODE = 3;
	private final int CHARGE_CODE = 4;
	//mower start from 5(mowerID=0)
	
	// type Enum: empty, grass, crate...
	private HashMap<Integer, String> type = new HashMap<>();
	
	
	public Lawn(Integer lawnWidth, Integer lawnHeight, Integer mowerNo,String[][] mowerLD, Integer craterNo, Integer[][] craterLocation){	       
		type.put(0, "empty");
		type.put(1, "grass");
		type.put(2, "crater");
		type.put(3, "fence");
		type.put(4, "charge");
        
        
		lawnMap = new Integer[lawnWidth][lawnHeight];
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

