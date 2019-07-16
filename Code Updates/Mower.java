package team_9;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;


public class Mower {
	private int mowerID;
	private static CommunicationChannel cc;
	private static SimDriver sim;

	//hashmap to define mower movement on coordinate for each direction
    private static HashMap<String, Integer> xDIR_MAP = new HashMap<>();
    private static HashMap<String, Integer> yDIR_MAP = new HashMap<>();

	// discovered: a list of mower that the mower can see, the Point records the relative position of these mowers
	//public Map<Integer, Point> discovered;

    //a set to record the id that this mower has discovered
    private HashSet<Integer> discovered_mowers;

	Integer mowerX, mowerY;
	private String mowerDirection = "North";


	private String trackAction;
	private Integer trackMoveDistance;
	private String trackNewDirection;
	private String trackMoveCheck;
	private String trackScanResults;
	private final int UNKNOWN_CODE = -1;
	private final int EMPTY_CODE = 0;
	private final int GRASS_CODE = 1;
	private final int CRATER_CODE = 2;
	private final int FENCE_CODE = 3;
	private final int CHARGE_CODE = 4;

	private boolean crashed = false;
//	int dx = 10;
//	int dy = 10;


	private int mapHeight = 2 * 10 + 1; //lawn height is 1~10 inclusive
	private int mapWidth = 2 * 15 + 1; //lawn width is 1~15 inclusive

	boolean up = false;
	boolean down = false;
	boolean left = false;
	boolean right = false;
	boolean findLawn = false;
	private String[] dirs = { "north", "northeast", "east", "southeast", "south", "southwest", "west", "northwest" };
	private List<String> path = new ArrayList<>();
	boolean enable = true;
	public int curEnergy = 0;
	private int maxEnergy = 0;

	public Mower(String direction, int id, int energy_capacity, int numMowers) {
		cc = new CommunicationChannel(numMowers);


		mowerDirection = direction;
		mowerID = id;
		maxEnergy = energy_capacity;
		curEnergy = energy_capacity;
		discovered_mowers = new HashSet<Integer>();

		mowerX = 10;
		mowerY = 10;

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

		//put self on the map: code = mowerID*10+100+CHARGE_CODE
		int code = mowerID*10 + 100 + CHARGE_CODE;
		InfoMap mowerMap = cc.getMap(mowerID);
		mowerMap.updateMapSquare(mowerX, mowerY, code);
	}

	private boolean needScan() {
		int i = mowerX;
		int j = mowerY;
		InfoMap mowerMap = cc.getMap(mowerID);
		int[][] neis = new int[][] { { i, j + 1 }, { i + 1, j + 1 }, { i + 1, j }, { i + 1, j - 1 }, { i, j - 1 },
				{ i - 1, j - 1 }, { i - 1, j }, { i - 1, j + 1 } };
		for (int[] nei : neis) {
			if (mowerMap.checkSquare(nei[0], nei[1]) == UNKNOWN_CODE) {
				return true;
			}
		}
		return false;
	}


	private void scan() {
		sim.scan(mowerID);
	}

	public void updateScannedInfo(int[] scannedInfo){
		InfoMap mowerMap = cc.getMap(mowerID);
		mowerMap.updateScannedInfo(mowerX, mowerY, scannedInfo);
	}

	// canCut(): check if mower can cut grass by moving at the current direction
	private boolean canCut() {
		InfoMap mowerMap = cc.getMap(mowerID);
		if (!validMove()) {
			return false;
		}
		return mowerMap.checkSquare(mowerX + xDIR_MAP.get(mowerDirection), mowerY + yDIR_MAP.get(mowerDirection)) == GRASS_CODE;
	}

	private boolean validMove() {
		int a = mowerX + xDIR_MAP.get(mowerDirection);
		int b = mowerY + yDIR_MAP.get(mowerDirection);
		InfoMap mowerMap = cc.getMap(mowerID);
		int square = mowerMap.checkSquare(a, b);
		return square == EMPTY_CODE || square == GRASS_CODE || square == CHARGE_CODE;
	}

	// move() : mover the mower, if the mower hit the crate or the fence, make
	// crashed = true and return, otherwise, generate the motion.

	private void move() {
		int dx = xDIR_MAP.get(mowerDirection);
		int dy = yDIR_MAP.get(mowerDirection);
		if(sim.validateMove(mowerID, dx, dy)) {
//			updateMowerMap(dx, dy);
			// this is updated in the SimDriver, so it is not called here
		}
	}


	private boolean isChargePad(int x, int y) {
		InfoMap mowerMap = cc.getMap(mowerID);
		return mowerMap.checkSquare(x, y) == CHARGE_CODE;
	}


	private void turning(String dir) {
		trackAction = "move";
		trackMoveDistance = 0;
		mowerDirection = dir;

		trackNewDirection = mowerDirection;
		displayActionAndResponses();
	}

	private int canCutAfterTurning() {
		int i = mowerX ;
		int j = mowerY;
		InfoMap mowerMap = cc.getMap(mowerID);
		int[][] neis = new int[][] { { i, j + 1 }, { i + 1, j + 1 }, { i + 1, j }, { i + 1, j - 1 }, { i, j - 1 },
				{ i - 1, j - 1 }, { i - 1, j }, { i - 1, j + 1 } };
		for (int k = 0; k < 8; k++) {
			if (mowerMap.checkSquare(neis[k][0], neis[k][1]) == GRASS_CODE) {
				return k;
			}
		}
		return -1;
	}

	// find next boundary, or grass to cut, return the closest one
	private List<String> findNextLocation() {
		InfoMap mowerMap = cc.getMap(mowerID);
		int[][] visited = new int[mapWidth][mapHeight];
		int[][] neis = new int[][] { { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 }, { -1, 0 },
				{ -1, 1 } };
		// Use Best First Search to find
		Queue<Point> queue = new ArrayDeque<>();
		queue.offer(new Point(mowerX, mowerY));
		int step = 1;
		visited[mowerX][mowerY] = step;
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
						int square = mowerMap.checkSquare(neiX, neiY);
						if (square == UNKNOWN_CODE || square == GRASS_CODE) {
							visited[neiX][neiY] = step;
							target_x = neiX;
							target_y = neiY;
							// System.out.println("find x: " + neiX + " y: " + neiY);
							// System.out.println("Mower x: " + (mowerX+ dx) + " y: " + (mowerY + dy));
							// System.out.println("Mower x: " + (mowerX) + " y: " + (mowerY));
							flag = false;
							break;
						} else if (mowerMap.checkSquare(neiX, neiY) == EMPTY_CODE) {
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
					if (mowerMap.checkSquare(target_x, target_y) != UNKNOWN_CODE) {
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
		// System.out.println(res.toString());
		return res;
	}

	// movePath: generate a series of mower motion based on the path
	public void movePath(List<String> path) {
		for (String dir : path) {
			if (dir == mowerDirection) {
				move();

			} else {
				turning(dir);
				// System.out.println(dir);
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

		if (path.size() > 0) {
			String dir = path.get(0);
			if (dir == mowerDirection) {
				move();
				path.remove(0);
			} else {
				turning(dir);
				// System.out.println(dir);
			}
			return;
		}
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
			System.out.println("turn");
			turning(dirs[dir_index]);
			return;
		}
		path = findNextLocation();
		// List<String> path = findNextLocation();
		// System.out.println("path generated");
		// movePath(path);

	}


	public void displayActionAndResponses() {
		// display the mower's actions
		System.out.print(trackAction);
		if (trackAction.equals("move")) {
			System.out.println("," + trackMoveDistance + "," + trackNewDirection);
		} else {
			System.out.println();
		}

		// display the simulation checks and/or responses
		if (trackAction.equals("move") | trackAction.equals("turn_off")) {
			trackMoveCheck = crashed ? "crash" : "ok";
			System.out.println(trackMoveCheck);
		} else if (trackAction.equals("scan")) {
			System.out.println(trackScanResults);
		} else {
			System.out.println("action not recognized");
		}
	}

	public void position() {
		System.out.println("mowerX: " + mowerX + " mowerY: " + mowerY);
	}



	public int getMowerRelativeX(){
		return mowerX;
	}

	public int getMowerRelativeY(){
		return mowerY;
	}
}
