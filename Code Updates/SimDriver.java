package team_9;

import java.io.File;
import java.time.chrono.IsoChronology;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import OsMowSis.Lawnmower;



public class SimDriver {
	private static final int DEFAULT_WIDTH = 100;
	private static final int DEFAULT_HEIGHT = 100;

	
	private static Mower[] mowerList;
	private int[][] mowerPosition;
	private Lawn lawn;
	
	
	
	private HashMap<String, Integer> xDIR_MAP;
	private HashMap<String, Integer> yDIR_MAP;
	// type Enum: empty, grass, crate...
	private HashMap<Integer, String> type = new HashMap<>();

	private String[] dirs = { "north", "northeast", "east", "southeast", "south", "southwest", "west", "northwest" };

	private final int EMPTY_CODE = 0;
	private final int GRASS_CODE = 1;
	private final int CRATER_CODE = 2;
	private final int FENCE_CODE = 3;
	private final int CHARGE_CODE = 4;
	

	private int total_cut = 0;
	private int total_grass = 0;
	private int total_step = 0;
	private int numTurn = 0;
	private int activeMowerCount = 0;
	private static int mowerCount = 0;
	private int collision_delay = 0;
	

	public SimDriver() {
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
	}

	public void uploadStartingFile(String testFileName) {
		String DELIMITER = ",";

		try {
			Scanner takeCommand = new Scanner(new File(testFileName));
			String[] tokens;

			// read in the lawn information
			tokens = takeCommand.nextLine().split(DELIMITER);
			Integer lawnWidth = Integer.parseInt(tokens[0]); //line 1
			tokens = takeCommand.nextLine().split(DELIMITER);
			Integer lawnHeight = Integer.parseInt(tokens[0]); //line 2
			

			// Initilize mower list
			tokens = takeCommand.nextLine().split(DELIMITER);
			int numMowers = Integer.parseInt(tokens[0]);  //line 3
			activeMowerCount = numMowers;
			mowerCount = numMowers;
			mowerList = new Mower[numMowers];
			
			//collision delay
			tokens = takeCommand.nextLine().split(DELIMITER);
			collision_delay = Integer.parseInt(tokens[0]);  //line 4
			
			//energy capacity
			tokens = takeCommand.nextLine().split(DELIMITER);
			int energy_capacity = Integer.parseInt(tokens[0]);  //line 5
			
			mowerPosition = new int[numMowers][2];
			for (int k = 0; k < numMowers; k++) {
				tokens = takeCommand.nextLine().split(DELIMITER); //line 6
				int px = Integer.parseInt(tokens[0]); // 3
				int py = Integer.parseInt(tokens[1]); // 2
				String mowerDirection = tokens[2];
				mowerList[k] = new Mower(mowerDirection, k, energy_capacity, numMowers); //mower id start from 0
				mowerPosition[k][0] = px;
				mowerPosition[k][1] = py;

			}

			// read in the crater information 
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numCraters = Integer.parseInt(tokens[0]); //line 7
            int [][] craterLocation = new int [numCraters][2];
            if (numCraters==0){ //there might be no crater
            	craterLocation = null;
            } else {	            
	            for (int k = 0; k < numCraters; k++) {
	                tokens = takeCommand.nextLine().split(DELIMITER); //line 8
	                Integer craterX = Integer.parseInt(tokens[0]);
	                Integer craterY = Integer.parseInt(tokens[1]);
	                craterLocation[k][0] = craterX;
	                craterLocation[k][1] = craterY;
	            }
            }

			tokens = takeCommand.nextLine().split(DELIMITER);
			numTurn = Integer.parseInt(tokens[0]); //line 9
			
			// create Lawn instance
            this.lawn = new Lawn(lawnWidth, lawnHeight, numMowers, mowerPosition, numCraters, craterLocation);
    
			takeCommand.close();
			// renderLawn();
			// scan();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("");
		}
	}
	
	public void scan(int mowerID){
		Mower mower = owerList[mowerID];
		int x = mowerPosition[mowerID][0];
		int y = mowerPosition[mowerID][1];
		String[] scanned_info = lawn.scanedByMower(x,y);
		for(int i=0;i<8;i++){			
			//mower location
			int relativeX = this.currentRelativeLocation[0];
			int relativeY = this.currentRelativeLocation[1];
			//location of square to be checked
			String direction = directions[i];
			int xDir = this.xDIR_MAP.get(direction);
			int yDir = this.yDIR_MAP.get(direction);
			String square = this.checkSquare(x + xDir, y + yDir);
			if (square.equals(Lawnmower.OUTSIDE)){
				this.resizeMap(xDir, yDir);
				//update mower's relative location
				relativeX = mower.getMowerRelativeX();
				relativeY = mower.getMowerRelativeY();
			}
			//update knowledge map
			String element = scanned_info[i];
			this.knowledgeMap[x+xDir][y+yDir] = element;
			System.out.print(element);
			if(i!=7) System.out.print(",");			
		}
		System.out.print("\n");
		
	}
	
	/***
	public void scan(int mowerID) {
		total_step++;
		Mower mower = mowerList[mowerID];
		int x_pos = mowerPosition[mowerID][0];
		int y_pos = mowerPosition[mowerID][1];
		int dx = mower.mowerX - x_pos;
		int dy = mower.mowerY - y_pos;
		StringBuilder res = new StringBuilder();
		int[][] neis = new int[][] { { x_pos, y_pos + 1 }, { x_pos + 1, y_pos + 1 }, { x_pos + 1, y_pos },
				{ x_pos + 1, y_pos - 1 }, { x_pos, y_pos - 1 }, { x_pos - 1, y_pos - 1 }, { x_pos - 1, y_pos },
				{ x_pos - 1, y_pos + 1 } };
		for (int[] nei : neis) {
			if (nei[0] < 0 || nei[0] >= lawnWidth || nei[1] < 0 || nei[1] >= lawnHeight) {
				res.append("fence");
				if(!mower.findLawn) {
					if ((!mower.left && nei[0] < 0) || (!mower.right && nei[0] >= lawnWidth)) {
						for (int k = 0; k < mower.mapHeight; k++) {
							mower.mowerMap[nei[0] + dx][k] = 3;
						}
						if (nei[0] < 0) {
							mower.left = true;
						} else {
							mower.right = true;
						}
					}
					if ((!mower.down && nei[1] < 0) || (!mower.up && nei[1] >= lawnHeight)) {
						for (int k = 0; k < mower.mapWidth; k++) {
							mower.mowerMap[k][nei[1] + dy] = 3;
						}
						if (nei[1] < 0) {
							mower.down = true;
						} else {
							mower.up = true;
						}
					}
					if(findMowerPosition(mower)) {
						mower.findLawn = true;
						findOtherMower(mowerID);
					}
				}
			} else {
				int t = lawnInfo[nei[0]][nei[1]];
				res.append(type.get(t));
				// update mowermap when they see each other
				if(t >= 5 && !mower.discovered.containsKey(t)) {
					mower.discovered.put(t, new Point(nei[0], nei[1]));
					mowerList[t].discovered.put(mowerID, new Point(x_pos, y_pos));
					mergeMap(t);
				}
				mower.mowerMap[nei[0] + dx][nei[1] + dy] = t;
			}
			res.append(',');
		}
		if(mower.discovered != null) {
			mergeMap(mowerID);
		}
		
		res.deleteCharAt(res.length() - 1);
		System.out.println(res.toString());
	}
	***/
	
	private boolean findMowerPosition(Mower mower) {
		return mower.left && mower.right && mower.up && mower.down;
	}
	
	private void findOtherMower(int mowerID) {
		Mower mower = mowerList[mowerID];

		for(int otherID: mowerKnowPosition) {
			if(!mower.discovered.containsKey(otherID)) {
				mower.discovered.put(otherID, new Point(0,0));
				mergeMap(mowerID);
				
				mowerList[otherID].discovered.put(mowerID, new Point(0,0));
				mergeMap(otherID);
			}
		}
		
	}
	
	
	public void mergeMap(int mowerID) {
		Mower mower = mowerList[mowerID];
		for(int id: mower.discovered.keySet()) {
			Point pos = mower.discovered.get(id);
			int dx = pos.x;
			int dy = pos.y;
			for(int i = 0; i < mapWidth; i++) {
				for(int j = 0; j < mapHeight; j++) {
					int x = i + dx;
					int y = j + dy;
					if(isValidPosition(x, y) && mowerList[id].mowerMap[x][y] == -1) {
						mowerList[id].mowerMap[x][y] = mower.mowerMap[i][j];
					}
				}
			}
		}
	}
	
	private boolean isValidPosition(int x, int y) {
		return x >= 0 && x < mapWidth && y >=0 && y < mapHeight;
	}

	public boolean validateMove(int mowerID, int dx, int dy) {
		
		total_step++;
		mowerList[mowerID].CurEnergy--;
		int x_pos = mowerPosition[mowerID][0];
		int y_pos = mowerPosition[mowerID][1];
		if (x_pos + dx < 0 || x_pos + dx >= lawnInfo.length || y_pos + dy < 0 || y_pos + dy >= lawnInfo[0].length) {
			removeMower(mowerID);
			return false;
		} else {
			switch (lawnInfo[x_pos + dx][y_pos + dy]) {
			case EMPTY_CODE:
				updatePosition(mowerID, dx, dy);
				return true;
			case GRASS_CODE:
				updatePosition(mowerID, dx, dy);
				return true;
			case CRATER_CODE:
				removeMower(mowerID);
				return false;
			case MOWER_CODE:
				return false;
			case CHARGE_CODE:
				charge(mowerID);
				updatePosition(mowerID, dx, dy);
				return true;
			default:
				return false;
			}
		}
	}

	private void removeMower(int mowerID) {
		activeMowerCount--;
		mowerList[mowerID].enable = false;
		int x_pos = mowerPosition[mowerID][0];
		int y_pos = mowerPosition[mowerID][1];
		if (isChargePad(x_pos, y_pos)) {
			lawnInfo[x_pos][y_pos] = CHARGE_CODE;
		} else {
			lawnInfo[x_pos][y_pos] = EMPTY_CODE;
		}
	}

	
	// update Position: update the lawnInfo and mowerMap, need to add update the other mowerMap;
	private void updatePosition(int mowerID, int dx, int dy) {
		// leave the previous position
		int x_pos = mowerPosition[mowerID][0];
		int y_pos = mowerPosition[mowerID][1];
		
		Mower mower = mowerList[mowerID];
		int x_mower = mower.mowerX;
		int y_mower = mower.mowerY;
		if (isChargePad(x_pos, y_pos)) {
			lawnInfo[x_pos][y_pos] = CHARGE_CODE;
			mower.mowerMap[x_mower][y_mower] = CHARGE_CODE;
		} else {
			lawnInfo[x_pos][y_pos] = EMPTY_CODE;
			mower.mowerMap[x_mower][y_mower] = EMPTY_CODE;
		}
		
		// go to the new position
		x_pos +=  dx;
		y_pos +=  dy;
		x_mower += dx;
		y_mower += dy;

		mowerPosition[mowerID][0] = x_pos;
		mowerPosition[mowerID][1] = y_pos;
		mower.mowerX = x_mower;
		mower.mowerY = y_mower;
		
		// cut grass
		if (lawnInfo[x_pos][y_pos] == GRASS_CODE) {
			total_cut++;
		}
		
		// run out of energy
		lawnInfo[x_pos][y_pos] = mowerID + 5;
		mower.mowerMap[x_mower][y_mower] = mowerID + 5;
		if (mowerList[mowerID].CurEnergy == 0) {
			mowerList[mowerID].enable = false;
			activeMowerCount--;
		}
		
		if(mower.discovered != null) {
			mergeMap(mowerID);
		}
	}

	private boolean isComplete() {
		if (activeMowerCount == 0 || total_cut == total_grass || total_step == numTurn) {
			return true;
		}
		return false;
	}

	private boolean isChargePad(int x, int y) {
		// TO be implemented
		return false;
	}

	private void charge(int mowerID) {
		mowerList[mowerID].CurEnergy = mowerList[mowerID].maxEnergy;
	}

	private void renderHorizontalBar(int size) {
		System.out.print(" ");
		for (int k = 0; k < size; k++) {
			System.out.print("-");
		}
		System.out.println("");
	}

	private boolean occupiedByMower(int i, int j) {
		for (int[] pos : mowerPosition) {
			if (pos[0] == i && pos[1] == j) {
				return true;
			}
		}
		return false;
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
				case CHARGE_CODE:
					System.out.print("p");
				default:
					System.out.print("m");
					break;
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
		// System.out.println("dir: " + mowerDirection);
		System.out.println("");
	}

	public static void main(String[] args) {
		SimDriver test = new SimDriver();
		String arg = "scenario6.csv";
		Mower.sim = test;
		test.uploadStartingFile(arg);
		test.renderLawn();
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < mowerCount; j++) {
				if (mowerList[j].enable) {
					System.out.println(i);
					mowerList[j].pollMowerForAction();
				}
				if (test.isComplete()) {
					break;
				}
			}
			
			if(test.isComplete()) break; 
		}
		test.renderLawn();
	}
}
