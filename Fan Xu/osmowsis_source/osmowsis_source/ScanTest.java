import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;




public class ScanTest {
	class Point {
		int x;
		int y;

		Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

//	private static Random randGenerator;

	private static final int DEFAULT_WIDTH = 100;
	private static final int DEFAULT_HEIGHT = 100;

	private Integer lawnHeight;
	private Integer lawnWidth;
	private Integer[][] lawnInfo;
	private Integer mowerX, mowerY;
	private String mowerDirection;
	private HashMap<String, Integer> xDIR_MAP;
	private HashMap<String, Integer> yDIR_MAP;
	private HashMap<Integer, String> type = new HashMap<>();
//	private String trackAction;
//	private Integer trackMoveDistance;
//	private String trackNewDirection;
//	private String trackMoveCheck;
//	private String trackScanResults;
	private String[] dirs = { "north", "northeast", "east", "southeast", "south", "southwest", "west", "northwest" };

	private final int EMPTY_CODE = 0;
	private final int GRASS_CODE = 1;
	private final int CRATER_CODE = 2;
	private int total_cut = 0;
	private int total_grass = 0;
	private int total_step = 0;

	private int numTurn;
	private boolean crashed = false;
	int dx = 10;
	int dy = 10;
	private int mapWidth = 2 * dx + 1;
	private int mapHeight = 2 * dy + 1;
	private int[][] mowerMap = new int[mapWidth][mapHeight];

	boolean up = false;
	boolean down = false;
	boolean left = false;
	boolean right = false;

	public ScanTest() {
//		randGenerator = new Random();

		lawnHeight = 0;
		lawnWidth = 0;
		lawnInfo = new Integer[DEFAULT_WIDTH][DEFAULT_HEIGHT];
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
		type.put(0, "empty");
		type.put(1, "grass");
		type.put(2, "crater");
		type.put(3, "fence");
//		System.out.println(type);
	}

	public void initial() {
		String DELIMITER = ",";

		try {
			System.out.println("Working Directory = " + System.getProperty("user.dir"));
			Scanner takeCommand = new Scanner(new File("scenario6.csv"));
			String[] tokens;
			int i, j, k;

			// read in the lawn information
			tokens = takeCommand.nextLine().split(DELIMITER);
			lawnWidth = Integer.parseInt(tokens[0]);
			tokens = takeCommand.nextLine().split(DELIMITER);
			lawnHeight = Integer.parseInt(tokens[0]);
			System.out.println("Width: " + lawnWidth + " Height: " + lawnHeight);

			// generate the lawn information
			lawnInfo = new Integer[lawnWidth][lawnHeight];
			for (i = 0; i < lawnWidth; i++) {
				for (j = 0; j < lawnHeight; j++) {
					lawnInfo[i][j] = GRASS_CODE;
				}
			}
			
			for(i = 0; i < mowerMap.length; i++) {
				for(j = 0; j < mowerMap[0].length; j++) {
					mowerMap[i][j] = -1;
				}
			}
			// read in the lawnmower starting information
			tokens = takeCommand.nextLine().split(DELIMITER);
			int numMowers = Integer.parseInt(tokens[0]);
			for (k = 0; k < numMowers; k++) {
				tokens = takeCommand.nextLine().split(DELIMITER);
				mowerX = Integer.parseInt(tokens[0]);  // 3
				mowerY = Integer.parseInt(tokens[1]);  // 2
				mowerDirection = tokens[2];
				lawnInfo[mowerX][mowerY] = EMPTY_CODE;
				mowerMap[dx][dy] = 0;
				dx -= mowerX;
				dy -= mowerY;
				System.out.println("Mower" + k + " Direction: " + mowerDirection);
			}

			// read in the crater information
			tokens = takeCommand.nextLine().split(DELIMITER);
			int numCraters = Integer.parseInt(tokens[0]);
			total_grass = lawnWidth * lawnHeight - numCraters;
			for (k = 0; k < numCraters; k++) {
				tokens = takeCommand.nextLine().split(DELIMITER);

				// place a crater at the given location
				lawnInfo[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])] = CRATER_CODE;
				System.out.println("Crate" + k);
			}
			tokens = takeCommand.nextLine().split(DELIMITER);
			numTurn = Integer.parseInt(tokens[0]);
			System.out.println("Max turns: " + numTurn);
			takeCommand.close();
			renderLawn();
			scan();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("");
		}
	}

	// needScan(): check if there is any unknown area around the mower
	private boolean needScan() {
		int i = mowerX + dx;
		int j = mowerY + dy;
		int[][] neis = new int[][] { { i, j + 1 }, { i + 1, j + 1 }, { i + 1, j }, { i + 1, j - 1 }, { i, j - 1 },
				{ i - 1, j - 1 }, { i - 1, j }, { i - 1, j + 1 } };
		for (int[] nei : neis) {
			if (mowerMap[nei[0]][nei[1]] == -1) {
				return true;
			}
		}
		return false;
	}
	
	private void scan() {
		total_step++;
		int i = mowerX;
		int j = mowerY;
		StringBuilder res = new StringBuilder();
		int[][] neis = new int[][] { { i, j + 1 }, { i + 1, j + 1 }, { i + 1, j }, { i + 1, j - 1 }, { i, j - 1 },
				{ i - 1, j - 1 }, { i - 1, j }, { i - 1, j + 1 } };
		for (int[] nei : neis) {
			if (nei[0] < 0 || nei[0] >= lawnWidth || nei[1] < 0 || nei[1] >= lawnHeight) {
				res.append("fence");
				if ((!left && nei[0] < 0) || (!right && nei[0] >= lawnWidth)) {
					for (int k = 0; k < mapHeight; k++) {
						mowerMap[nei[0] + dx][k] = 3;
					}
					if(nei[0] < 0) {
						left = true;
					} else {
						right= true;
					}
				}
				if ((!down && nei[1] < 0) || (!up && nei[1] >= lawnHeight)) {
					for (int k = 0; k < mapWidth; k++) {
						mowerMap[k][nei[1] + dy] = 3;
					}
					if(nei[1] < 0) {
						down = true;
					} else {
						up= true;
					}
				}
			} else {
				int t = lawnInfo[nei[0]][nei[1]];
				res.append(type.get(t));
				mowerMap[nei[0] + dx][nei[1] + dy] = t;
			}
			res.append(',');
		}
		res.deleteCharAt(res.length() - 1);
		System.out.println("scan");
		System.out.println(res.toString());
	}


	// canCut(): check if mower can cut grass by moving at the current direction
	private boolean canCut() {
		if(!validMove()) {
			return false;
		}
		return mowerMap[mowerX + dx + xDIR_MAP.get(mowerDirection)][mowerY + dy + yDIR_MAP.get(mowerDirection)] == 1;
	}

	private boolean validMove() {
		int a = mowerX + xDIR_MAP.get(mowerDirection) + dx;
		int b = mowerY + yDIR_MAP.get(mowerDirection) + dy;
		return mowerMap[a][b] == 0 ||  mowerMap[a][b] == 1;
	}

	// move() : mover the mower, if the mower hit the crate or the fence, make
	// crashed = true and return, otherwise, generate the motion.
	private void move() {
		total_step++;
		if (!validMove()) {
			System.out.println("move, 0, " + mowerDirection);
			System.out.println("crashed");
			crashed = true;
//			printArray(mowerMap);
			position();
			return;
		}
		mowerX += xDIR_MAP.get(mowerDirection);
		mowerY += yDIR_MAP.get(mowerDirection);
		if (lawnInfo[mowerX][mowerY] == 1) {
			total_cut++;
			lawnInfo[mowerX][mowerY] = 0;
			mowerMap[mowerX + dx][mowerY + dy] = 0;
		}
		System.out.println("move, 0, " + mowerDirection);
		System.out.println("ok");
	}

	private void turning(String dir) {
		mowerDirection = dir;
		total_step++;
	}

	private int canCutAfterTurning() {
		int i = mowerX + dx;
		int j = mowerY + dy;
		int[][] neis = new int[][] { { i, j + 1 }, { i + 1, j + 1 }, { i + 1, j }, { i + 1, j - 1 }, { i, j - 1 },
				{ i - 1, j - 1 }, { i - 1, j }, { i - 1, j + 1 } };
		for (int k = 0; k < 8; k++) {
			if (mowerMap[neis[k][0]][neis[k][1]] == 1) {
				return k;
			}
		}
		return -1;
	}

	// find next boundary, or grass to cut, return the closest one
	private List<String> findNextLocation() {
		int[][] visited = new int[mapWidth][mapHeight];
		int[][] neis = new int[][] { { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 }, { -1, 0 },
				{ -1, 1 } };
		// Use Best First Search to find
		Queue<Point> queue = new ArrayDeque<>();
		queue.offer(new Point(mowerX + dx, mowerY + dy));
		int step = 1;
		visited[mowerX + dx][mowerY + dy] = step;
		int target_x = 1000;
		int target_y = 1000;
		boolean flag = true;
		while (flag && !queue.isEmpty()) {
			int size = queue.size();
			step++;
			for (int k = 0; k < size && flag; k++) {
				Point cur = queue.poll();
				for (int[] nei : neis) {
					int neiX = cur.x + nei[0];
					int neiY = cur.y + nei[1];
					if (visited[neiX][neiY] == 0) {
						if (mowerMap[neiX][neiY] == -1 || mowerMap[neiX][neiY] == 1) {
							visited[neiX][neiY] = step;
							target_x = neiX;
							target_y = neiY;
//							System.out.println("find x: " + neiX + " y: " + neiY);
//							System.out.println("Mower x: " + (mowerX+ dx) + " y: " + (mowerY + dy));
//							System.out.println("Mower x: " + (mowerX) + " y: " + (mowerY));
							flag = false;
							break;
						} else if (mowerMap[neiX][neiY] == 0) {
							queue.offer(new Point(neiX, neiY));
							visited[neiX][neiY] = step;
						}
					}
				}

			}
		}

		List<String> res = new ArrayList<>();
		if (target_x == 1000) {
			return res;
		}

		// Find the path using greedy method, the last
		while (step > 1) {
			for (int k = 0; k < 8; k++) {
				int[] nei = neis[k];
				int neiX = target_x - nei[0];
				int neiY = target_y - nei[1];
				if (visited[neiX][neiY] == step - 1) {
					// if the target location is unexplored, do not move to that location, need to
					// scan it first
					if (mowerMap[target_x][target_y] != -1) {
						res.add(dirs[k]);
					}
					target_x = neiX;
					target_y = neiY;
					step--;
					break;
				}
			}
		}
		Collections.reverse(res);
//		System.out.println(res.toString());
		return res;
	}

	// movePath: generate a series of mower motion based on the path
	public void movePath(List<String> path) {
		for (String dir : path) {
			if (dir == mowerDirection) {
				
				move();
				
			} else {
				turning(dir);
//				System.out.println(dir);
				move();
			}
		}
	}

	// Key logic: if there is unknown area around the mower, scan first;
	// then, check if mower can cut any grass by keeping the current direction, if
	// yes, move the mower
	// if not, change the direction of the mower;
	// if the surroundings are know and there is no grass that can be cut, move to
	// the nearest area that is unknown or has grass.
	public void pollMowerForAction() {
		if (needScan()) {
			scan();
			return;
		}
		if (canCut()) {
			move();
			return;
		}
		int dir_index = canCutAfterTurning();
		if (dir_index >= 0) {
			turning(dirs[dir_index]);
			return;
		}
		List<String> path = findNextLocation();
//		System.out.println("path generated");
		movePath(path);
	}

	public void validateMowerAction() {
		return;
	}
	
	public boolean stop() {
		
		return crashed || total_cut == total_grass || total_step == numTurn;
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
	}

	private void printArray(int[][] array) {
		for (int i = array.length -1; i >= 0; i--) {
			for (int j = 0; j < array[0].length; j++) {
				System.out.print(array[j][i]);
				System.out.print(" ");
			}
			System.out.println();
		}
	}
	public void position() {
		System.out.println("mowerX: " + mowerX +" mowerY: " + mowerY);
	}
	
//	public static void main(String[] args) {
//		ScanTest test = new ScanTest();
//		test.initial();
//		for(int i = 0; i < 100; i++) {
//			test.pollMowerForAction();
//			if(test.stop()) {	
//				System.out.println("ssssdd " + i);
//				test.position();
//				break;
//			}
//		}
//		test.renderLawn();
//	}

}
