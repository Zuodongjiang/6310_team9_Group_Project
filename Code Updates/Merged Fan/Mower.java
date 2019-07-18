package team_9;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;

// Functions of Mower: scan, turning, path planning and moving.
// Before determine each mower action, the mower will get the updated mowerMap and mower position from communication channel. 
// It will then determine the next mower action based on the mower map.
// After determine the action, it will send its next action to the simulationRun. The simulationRun will validate the motion and call communicationChannel to updated all information.  



public class Mower {
	class Point {
		int x;
		int y;

		Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	public int mowerID = 0;
	public static SimDriver sim;
	static CommunicationChannel cc;

	// discovered: a list of mower that the mower can see, the Point records the
	// relative position of these mowers
	public Map<Integer, Point> discovered;
	Integer mowerX, mowerY;
	private String mowerDirection = "North";
	private static HashMap<String, Integer> xDIR_MAP;
	private static HashMap<String, Integer> yDIR_MAP;
	//a set to record the id that this mower has discovered
    private HashSet<Integer> discovered_mowers;
    
	private String trackAction;
	private Integer trackMoveDistance;
	private String trackNewDirection;
	private String trackMoveCheck;
	private String trackScanResults;
	
	private final int CHARGE_CODE = 4;
	
	int code = mowerID*10 + 100 + CHARGE_CODE;

	private boolean crashed = false;
	int mapWidth = 2 * 10 + 1;
	int mapHeight = 2 * 15 + 1;

	int[][] mowerMap;

	private String[] dirs = { "north", "northeast", "east", "southeast", "south", "southwest", "west", "northwest" };
	private List<String> path = new ArrayList<>();
	boolean enable = true;
	int curEnergy = 0;
	int maxEnergy = 0;

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
	private void scan(int mowerID) {
		sim.scan(mowerID);
	}
	private boolean needScan() {
		int i = mowerX;
		int j = mowerY;
		int[][] neis = new int[][] { { i, j + 1 }, { i + 1, j + 1 }, { i + 1, j }, { i + 1, j - 1 }, { i, j - 1 },
				{ i - 1, j - 1 }, { i - 1, j }, { i - 1, j + 1 } };
		for (int[] nei : neis) {
			if (mowerMap[nei[0]][nei[1]] == -1) {
				return true;
			}
		}
		return false;
	}

	// canCut(): check if mower can cut grass by moving at the current direction
	private boolean canCut() {
		if (!validMove()) {
			return false;
		}
		return mowerMap[mowerX + xDIR_MAP.get(mowerDirection)][mowerY + yDIR_MAP.get(mowerDirection)] == 1;
	}

	// make sure it will not move to unknow area
	private boolean validMove() {
		int a = mowerX + xDIR_MAP.get(mowerDirection);
		int b = mowerY + yDIR_MAP.get(mowerDirection);
		return mowerMap[a][b] == 0 || mowerMap[a][b] == 1 || mowerMap[a][b] == 4;
	}

	// move() : mover the mower, if the mower hit the crate or the fence, make
	// crashed = true and return, otherwise, generate the motion.

	private void move() {
		int dx = xDIR_MAP.get(mowerDirection);
		int dy = yDIR_MAP.get(mowerDirection);
		sim.validateMove(mowerID, dx, dy);
	}

	private void turning(String dir) {
		trackAction = "move";
		trackMoveDistance = 0;
		mowerDirection = dir;

		trackNewDirection = mowerDirection;
		displayActionAndResponses();
	}

	private int canCutAfterTurning() {
		int[][] neis = new int[][] { { mowerX, mowerY + 1 }, { mowerX + 1, mowerY + 1 }, { mowerX + 1, mowerY },
				{ mowerX + 1, mowerY - 1 }, { mowerX, mowerY - 1 }, { mowerX - 1, mowerY - 1 }, { mowerX - 1, mowerY },
				{ mowerX - 1, mowerY + 1 } };
		for (int k = 0; k < 8; k++) {
			if (mowerMap[neis[k][0]][neis[k][1]] == 1) {
				return k;
			}
		}
		return -1;
	}

	// find next boundary, or grass to cut, return the closest one
	private List<String> findPath() {
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
						if (mowerMap[neiX][neiY] == -1 || mowerMap[neiX][neiY] == 1) {
							visited[neiX][neiY] = step;
							target_x = neiX;
							target_y = neiY;
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
		// System.out.println(res.toString());
		return res;
	}

	// (1) if there is unknown area around the mower, scan first;
	// (2) if mower can cut any grass without turning, move along this direction
	// (3) if the mower can cut grass after turning, change the direction of the
	// mower;
	// (4) if the mower cannot cut grass after turning, move to the nearest area
	// that is unknown or has grass based on the path finding method.
	// first, find the path; second move one step along the path. Since the map is
	// constantly updating, we might need to change the path everything time.
	// Therefore, path need to be recalculated at each pollMowerForAction.
	public void pollMowerForAction() {
		// Since the mowerMaps is updating, at the beginning of the action, get the
		// latest map, and the position of the mower;
		// the map and the mower position is updated in the commchannel.
		mowerMap = cc.mowerMaps[mowerID].map;
		mowerX = cc.mowerRelativeLocation[mowerID][0];
		mowerY = cc.mowerRelativeLocation[mowerID][1];
		// check if need scan
		if (needScan()) {
			scan(mowerID);
			return;
		}
		// check if can cut without turning
		if (canCut()) {
			move();
			return;
		}
		// check if can cut after turning
		int dir_index = canCutAfterTurning();
		if (dir_index >= 0) {
			System.out.println("turn");
			turning(dirs[dir_index]);
			return;
		}
		// find the path, and move one step along the path.
		path = findPath();
		if (path.size() > 0) {
			String dir = path.get(0);
			if (dir == mowerDirection) {
				move();
				// path.remove(0);
			} else {
				turning(dir);
			}
			return;
		}
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

}
