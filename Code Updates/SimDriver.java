import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class SimDriver {
	private static final int DEFAULT_WIDTH = 100;
	private static final int DEFAULT_HEIGHT = 100;

	private Integer lawnHeight;
	private Integer lawnWidth;
	private Integer[][] lawnInfo;

	private Mower[] mowerList;
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
	private final int MOWER_CODE = 4;
	private final int CHARGE_CODE = 5;

	private int total_cut = 1;
	private int total_grass = 0;
	private int total_step = 0;
	private int numTurn = 0;
	private int activeMowerCount = 0; 

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
		type.put(0, "empty");
		type.put(1, "grass");
		type.put(2, "crater");
		type.put(3, "fence");
		type.put(4, "mower");
		type.put(5, "charge");
	}

	public void uploadStartingFile(String testFileName) {
		String DELIMITER = ",";

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
			for (i = 0; i < lawnWidth; i++) {
				for (j = 0; j < lawnHeight; j++) {
					lawnInfo[i][j] = GRASS_CODE;
				}
			}

			// Initilize mower list
			tokens = takeCommand.nextLine().split(DELIMITER);
			int numMowers = Integer.parseInt(tokens[0]);
			activeMowerCount = numMowers; 
			mowerList = new Mower[numMowers];
			mowerPosition = new int[numMowers][2];
			for (k = 0; k < numMowers; k++) {
				tokens = takeCommand.nextLine().split(DELIMITER);
				int px = Integer.parseInt(tokens[0]); // 3
				int py = Integer.parseInt(tokens[1]); // 2
				String mowerDirection = tokens[2];
				mowerList[k] = new Mower(mowerDirection);
				mowerPosition[k][0] = px;
				mowerPosition[k][1] = py;
				lawnInfo[px][py] = EMPTY_CODE;

			}

			// read in the crater information
			tokens = takeCommand.nextLine().split(DELIMITER);
			int numCraters = Integer.parseInt(tokens[0]);
			// find total_grass needs to be cut
			total_grass = lawnWidth * lawnHeight - numCraters - numMowers;
			for (k = 0; k < numCraters; k++) {
				tokens = takeCommand.nextLine().split(DELIMITER);
				// place a crater at the given location
				lawnInfo[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])] = CRATER_CODE;
			}
			tokens = takeCommand.nextLine().split(DELIMITER);
			numTurn = Integer.parseInt(tokens[0]);
			takeCommand.close();
			// renderLawn();
			// scan();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("");
		}
	}

	public List<Integer> scan(int mowerID) {
		total_step++;
		int x_pos = mowerPosition[mowerID][0];
		int y_pos = mowerPosition[mowerID][1];
		List<Integer> res = new ArrayList<>();
		int[][] neis = new int[][] { { x_pos, y_pos + 1 }, { x_pos + 1, y_pos + 1 }, { x_pos + 1, y_pos }, { x_pos + 1, y_pos - 1 }, { x_pos, y_pos - 1 },
				{ x_pos - 1, y_pos - 1 }, { x_pos - 1, y_pos }, { x_pos - 1, y_pos + 1 } };
		for (int[] nei : neis) {
			if (nei[0] < 0 || nei[0] >= lawnWidth || nei[1] < 0 || nei[1] >= lawnHeight) {
				res.add(FENCE_CODE);
			} else {
				res.add(lawnInfo[nei[0]][nei[1]]);
			}
		}
		return res;
	}
//	public void scan(int mowerID) {
//		total_step++;
//		Mower mower = mowerList[mowerID];
//		int x_pos = mowerPosition[mowerID][0];
//		int y_pos = mowerPosition[mowerID][1];
//		int dx = mower.mowerX - x_pos;
//		int dy = mower.mowerY - y_pos; 
//		StringBuilder res = new StringBuilder();
//		int[][] neis = new int[][] { { x_pos, y_pos + 1 }, { x_pos + 1, y_pos + 1 }, { x_pos + 1, y_pos }, { x_pos + 1, y_pos - 1 }, { x_pos, y_pos - 1 },
//				{ x_pos - 1, y_pos - 1 }, { x_pos - 1, y_pos }, { x_pos - 1, y_pos + 1 } };
//		for (int[] nei : neis) {
//			if (nei[0] < 0 || nei[0] >= lawnWidth || nei[1] < 0 || nei[1] >= lawnHeight) {
//				res.append("fence");
//				if ((!mower.left && nei[0] < 0) || (!mower.right && nei[0] >= lawnWidth)) {
//					for (int k = 0; k < mower.mapHeight; k++) {
//						mower.mowerMap[nei[0] + dx][k] = 3;
//					}
//					if (nei[0] < 0) {
//						mower.left = true;
//					} else {
//						mower.right = true;
//					}
//				}
//				if ((!mower.down && nei[1] < 0) || (!mower.up && nei[1] >= lawnHeight)) {
//					for (int k = 0; k < mower.mapWidth; k++) {
//						mower.mowerMap[k][nei[1] + dy] = 3;
//					}
//					if (nei[1] < 0) {
//						mower.down = true;
//					} else {
//						mower.up = true;
//					}
//				}
//			} else {
//				int t = lawnInfo[nei[0]][nei[1]];
//				res.append(type.get(t));
//				mower.mowerMap[nei[0] + dx][nei[1] + dy] = t;
//			}
//			res.append(',');
//		}
//		res.deleteCharAt(res.length() - 1);
//		System.out.println(res.toString());
//	}
	


	public boolean validateMove(int mowerID, int dx, int dy) {
		total_step++;
		mowerList[mowerID].CurEnergy--;
		int x_pos = mowerPosition[mowerID][0];
		int y_pos = mowerPosition[mowerID][1];
		if(x_pos + dx < 0 || x_pos + dx >= lawnInfo.length || y_pos + dy < 0 || y_pos + dy >= lawnInfo[0].length) {
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
		if(isChargePad(x_pos, y_pos)) {
			lawnInfo[x_pos][y_pos] = CHARGE_CODE;
		} else {
			lawnInfo[x_pos][y_pos] = EMPTY_CODE;
		}
	}
	
	private void updatePosition(int mowerID, int dx, int dy) {
		
		int x_pos = mowerPosition[mowerID][0];
		int y_pos = mowerPosition[mowerID][1];
		if(isChargePad(x_pos, y_pos)) {
			lawnInfo[x_pos][y_pos] = CHARGE_CODE;
		} else {
			lawnInfo[x_pos][y_pos] = EMPTY_CODE;
		}
		
		x_pos = mowerPosition[mowerID][0] + dx;
		y_pos = mowerPosition[mowerID][1] + dy;
		
		mowerPosition[mowerID][0] = x_pos;
		mowerPosition[mowerID][1] = y_pos;
		if(lawnInfo[x_pos][y_pos] == GRASS_CODE) {
			total_cut++;
		}
		lawnInfo[x_pos][y_pos] = MOWER_CODE;
		if(mowerList[mowerID].CurEnergy == 0) {
			mowerList[mowerID].enable = false; 
			activeMowerCount--; 
		}
	}
	
	private boolean isComplete() {
		if(activeMowerCount == 0 || total_cut == total_grass || total_step == numTurn) {
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
				case MOWER_CODE:
					System.out.print("m");
					break;
				case CHARGE_CODE:
					System.out.print("p");
				default:
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
//		System.out.println("dir: " + mowerDirection);
		System.out.println("");
	}

}
