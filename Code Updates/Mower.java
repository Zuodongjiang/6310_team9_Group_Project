
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class Mower {
	public int mowerID;
	private static SimDriver sim;
	public Set<Mower> discovered;
	Integer mowerX, mowerY;
	private String mowerDirection;
	private static HashMap<String, Integer> xDIR_MAP;
	private static HashMap<String, Integer> yDIR_MAP;
	private HashMap<Integer, String> type = new HashMap<>();
	private String trackAction;
	private Integer trackMoveDistance;
	private String trackNewDirection;
	private String trackMoveCheck;
	private String trackScanResults;
	private final int EMPTY_CODE = 0;
	private final int GRASS_CODE = 1;
	private final int CRATER_CODE = 2;
	private final int FENCE_CODE = 3;
	private final int MOWER_CODE = 4;
	private final int CHARGE_CODE = 5;
	private boolean crashed = false;
	int dx = 10;
	int dy = 10;
	int mapWidth = 2 * dx + 1;
	int mapHeight = 2 * dy + 1;
	int[][] mowerMap = new int[mapWidth][mapHeight];
	boolean up = false;
	boolean down = false;
	boolean left = false;
	boolean right = false;
	private String[] dirs = { "north", "northeast", "east", "southeast", "south", "southwest", "west", "northwest" };
	private List<String> path = new ArrayList<>();
	boolean enable = true;
	int CurEnergy = 0;
	int maxEnergy = 0;

	public Mower(String direction) {
		mowerDirection = direction;
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

	private void scan() {
		List<Integer> scanResult = sim.scan(mowerID);
		StringBuilder res = new StringBuilder();
		int[][] neis = { { 0, 1 }, { 1, 1 }, { 1, 0 }, { -1, 1 }, { -1, 0 }, { -1, -1 }, { -1, 0 }, { -1, 1 } };
		for (int i = 0; i < 8; i++) {
			int dx = neis[i][0];
			int dy = neis[i][1];
			int gridType = scanResult.get(i);
			if (gridType == FENCE_CODE) {
				res.append("fence");
				if ((!left && i == 7)) {
					for (int k = 0; k < mapHeight; k++) {
						mowerMap[mowerX - 1][k] = 3;
					}
					left = true;
				} else if (!right && i == 3) {
					for (int k = 0; k < mapHeight; k++) {
						mowerMap[mowerX + 1][k] = 3;
					}
					right = true;
				} else if (!down && i == 5) {
					for (int k = 0; k < mapHeight; k++) {
						mowerMap[k][mowerY - 1] = 3;
					}
					down = true;
				} else if (!up && i == 1) {
					for (int k = 0; k < mapHeight; k++) {
						mowerMap[k][mowerY + 1] = 3;
					}
					up = true;
				}
			} else {
				mowerMap[mowerX + dx][mowerY + dy] = gridType;
			}
		}

		
//		res.deleteCharAt(res.length() - 1);
		// System.out.println("scan");
		// System.out.println(res.toString());
//		trackScanResults = res.toString();
//		displayActionAndResponses();
	}

	// canCut(): check if mower can cut grass by moving at the current direction
	private boolean canCut() {
		if (!validMove()) {
			return false;
		}
		return mowerMap[mowerX + dx + xDIR_MAP.get(mowerDirection)][mowerY + dy + yDIR_MAP.get(mowerDirection)] == 1;
	}

	private boolean validMove() {
		int a = mowerX + xDIR_MAP.get(mowerDirection) + dx;
		int b = mowerY + yDIR_MAP.get(mowerDirection) + dy;
		return mowerMap[a][b] == 0 || mowerMap[a][b] == 1;
	}

	// move() : mover the mower, if the mower hit the crate or the fence, make
	// crashed = true and return, otherwise, generate the motion.
	
	private void move() {

		int dx = xDIR_MAP.get(mowerDirection);
		int dy = yDIR_MAP.get(mowerDirection);
		if(sim.validateMove(mowerID, dx, dy)) {
			updateMowerMap(dx, dy);
		}
	}
	private boolean isChargePad(int x, int y) {
		return false;
	}
	private void updateMowerMap(int dx, int dy) {
		int x_pos = mowerX;
		int y_pos = mowerY;
		if(isChargePad(x_pos, y_pos)) {
			mowerMap[x_pos][y_pos] = CHARGE_CODE;
		} else {
			mowerMap[x_pos][y_pos] = EMPTY_CODE;
		}
		
		mowerX += dx;
		mowerY += dy;
		mowerMap[mowerX][mowerY] = MOWER_CODE;
	}
	
	private void turning(String dir) {
		trackAction = "move";
		trackMoveDistance = 0;
		mowerDirection = dir;

		trackNewDirection = mowerDirection;
		displayActionAndResponses();
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
							// System.out.println("find x: " + neiX + " y: " + neiY);
							// System.out.println("Mower x: " + (mowerX+ dx) + " y: " + (mowerY + dy));
							// System.out.println("Mower x: " + (mowerX) + " y: " + (mowerY));
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
			turning(dirs[dir_index]);
			return;
		}
		path = findNextLocation();
		// List<String> path = findNextLocation();
		// System.out.println("path generated");
		// movePath(path);

	}

	public void validateMowerAction() {

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
