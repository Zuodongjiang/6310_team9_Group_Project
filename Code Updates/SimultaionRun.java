package team_9;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class SimultaionRun {

	private static Mower[] mowerList;
	private int[][] mowerPosition;
	private InfoMap lawnMap;
	private int lawnHeight;
	private int lawnWidth;
	private int[][] lawnInfo;
	private CommunicationChannel cc;
	private Set<Integer> chargePadLocation;

	// private List<Integer> mowerKnowPosition = new ArrayList<>();
	// private Lawn lawn;

	// type Enum: empty, grass, crate...

	private final int EMPTY_CODE = 0;
	private final int GRASS_CODE = 1;
	private final int CRATER_CODE = 2;
	private final int FENCE_CODE = 3;
	private final int CHARGE_CODE = 4;

	private int nextMower = 0;
	private int total_cut = 0;
	private int total_grass = 0;
	private int total_step = 0;
	private int numTurn = 0;
	private int activeMowerCount = 0;
	private static int mowerCount = 0;
	private int collision_delay = 0;
	public boolean pressStop = false;

	public SimultaionRun(String filePath) {
		pressStop = false;
		uploadStartingFile(filePath);
	}

	public void uploadStartingFile(String testFileName) {
		String DELIMITER = ",";

		try {
			Scanner takeCommand = new Scanner(new File(testFileName));
			String[] tokens;

			// read in the lawn information
			tokens = takeCommand.nextLine().split(DELIMITER);
			lawnWidth = Integer.parseInt(tokens[0]); // line 1
			tokens = takeCommand.nextLine().split(DELIMITER);
			lawnHeight = Integer.parseInt(tokens[0]); // line 2

			// Initialize mower list
			tokens = takeCommand.nextLine().split(DELIMITER);
			int numMowers = Integer.parseInt(tokens[0]); // line 3
			activeMowerCount = numMowers;
			mowerCount = numMowers;
			mowerList = new Mower[numMowers];
			cc = new CommunicationChannel(mowerCount);
			Mower.cc = cc;
			Mower.sim = this;
			// collision delay
			tokens = takeCommand.nextLine().split(DELIMITER);
			collision_delay = Integer.parseInt(tokens[0]); // line 4

			// energy capacity
			tokens = takeCommand.nextLine().split(DELIMITER);
			int energy_capacity = Integer.parseInt(tokens[0]); // line 5

			mowerPosition = new int[numMowers][2];
			chargePadLocation = new HashSet<Integer>();

			for (int k = 0; k < numMowers; k++) {
				tokens = takeCommand.nextLine().split(DELIMITER); // line 6
				int px = Integer.parseInt(tokens[0]); // 3
				int py = Integer.parseInt(tokens[1]); // 2
				String mowerDirection = tokens[2];
				mowerList[k] = new Mower(mowerDirection, k, energy_capacity, numMowers); // mower id start from 0
				mowerPosition[k][0] = px;
				mowerPosition[k][1] = py;
				chargePadLocation.add(px + 100 * py);
			}

			// read in the crater information
			tokens = takeCommand.nextLine().split(DELIMITER);
			int numCraters = Integer.parseInt(tokens[0]); // line 7
			int[][] craterLocation = new int[numCraters][2];
			if (numCraters == 0) { // there might be no crater
				craterLocation = null;
			} else {
				for (int k = 0; k < numCraters; k++) {
					tokens = takeCommand.nextLine().split(DELIMITER); // line 8
					Integer craterX = Integer.parseInt(tokens[0]);
					Integer craterY = Integer.parseInt(tokens[1]);
					craterLocation[k][0] = craterX;
					craterLocation[k][1] = craterY;
				}
			}

			tokens = takeCommand.nextLine().split(DELIMITER);
			numTurn = Integer.parseInt(tokens[0]); // line 9
			total_grass = lawnWidth * lawnHeight - numCraters - numMowers;
			// create Lawn instance
//			this.lawn = new Lawn(lawnWidth, lawnHeight, numMowers, mowerPosition, numCraters, craterLocation);
			lawnMap = new InfoMap(lawnWidth, lawnHeight, numMowers, mowerPosition, numCraters, craterLocation, true);

			CommunicationChannel.mowerList = mowerList;
			takeCommand.close();
			lawnInfo = lawnMap.map;

			// renderLawn();
			// scan();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("");
		}
	}

	public MowerStates[] getMowerState() {
		MowerStates[] allMowerStates = new MowerStates[mowerCount];
		for (int i = 0; i < mowerCount; i++) {
			String mowerStatus = mowerList[i].enable ? "enabled" : "disabled";
			int energyLevel = mowerList[i].curEnergy;
			int stallTurn = mowerList[i].stallTurn;
			allMowerStates[i] = new MowerStates(i, mowerStatus, energyLevel, stallTurn);
		}
		return allMowerStates;
	}

	public InfoMap getLawnMap() {
		return lawnMap;
	}

	public Report generateReport() {
		Report report = new Report(this.total_grass, this.total_cut, this.total_grass - this.total_cut,
				this.total_step);
		return report;
	}

	public void scan(int mowerID) {
		mowerList[mowerID].curEnergy--;
		int x_pos = mowerPosition[mowerID][0];
		int y_pos = mowerPosition[mowerID][1];
		List<Integer> res = new ArrayList<>();
		int[][] neis = new int[][] { { x_pos, y_pos + 1 }, { x_pos + 1, y_pos + 1 }, { x_pos + 1, y_pos },
				{ x_pos + 1, y_pos - 1 }, { x_pos, y_pos - 1 }, { x_pos - 1, y_pos - 1 }, { x_pos - 1, y_pos },
				{ x_pos - 1, y_pos + 1 } };

		for (int[] nei : neis) {
			if (nei[0] < 0 || nei[0] >= lawnWidth || nei[1] < 0 || nei[1] >= lawnHeight) {
				res.add(FENCE_CODE);
			} else {
				res.add(lawnInfo[nei[0]][nei[1]]);
			}
		}
		System.out.println("scan: " + res.toString());
		cc.updateMowerMap(mowerID, res);
	}

	public boolean validateMove(int mowerID, int dx, int dy) {
		mowerList[mowerID].curEnergy--;
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
			case CHARGE_CODE:
				charge(mowerID);
				updatePosition(mowerID, dx, dy);
				return true;
			default:
				mowerList[mowerID].stallTurn = collision_delay;
				return true;
			}
		}
	}

	private void removeMower(int mowerID) {
		activeMowerCount--;
		mowerList[mowerID].enable = false;
	}

	// update Position: update the lawnInfo and mowerMap, need to add update the
	// other mowerMap;
	private void updatePosition(int mowerID, int dx, int dy) {
		// leave the previous position
		int x_pos = mowerPosition[mowerID][0];
		int y_pos = mowerPosition[mowerID][1];

		int[][] mowerMap = cc.mowerMaps[mowerID].map;

		int x_mower = cc.mowerRelativeLocation[mowerID][0];
		int y_mower = cc.mowerRelativeLocation[mowerID][1];
		if (chargePadLocation.contains(x_pos + 100 * y_pos)) {
			lawnInfo[x_pos][y_pos] = CHARGE_CODE;
			mowerMap[x_mower][y_mower] = CHARGE_CODE;
		} else {
			lawnInfo[x_pos][y_pos] = EMPTY_CODE;
			mowerMap[x_mower][y_mower] = EMPTY_CODE;
		}

		// go to the new position
		x_pos += dx;
		y_pos += dy;
		x_mower += dx;
		y_mower += dy;

		mowerPosition[mowerID][0] = x_pos;
		mowerPosition[mowerID][1] = y_pos;
		cc.mowerRelativeLocation[mowerID][0] = x_mower;
		cc.mowerRelativeLocation[mowerID][1] = y_mower;

		// cut grass
		if (lawnInfo[x_pos][y_pos] == GRASS_CODE) {
			total_cut++;
		}

		// run out of energy
		lawnInfo[x_pos][y_pos] = mowerID + 5;
		mowerMap[x_mower][y_mower] = mowerID + 5;
		if (mowerList[mowerID].curEnergy == 0) {
			mowerList[mowerID].enable = false;
			activeMowerCount--;
		}
	}

	public void act() {
		while (!checkStop() && !pressStop) {
			moveNext();
		}
		System.out.println(total_step + " total turns " + numTurn);
	}

	// poll next available mower
	public int moveNext() {
		while (!mowerList[nextMower].enable) {
			nextMower++;
			countTurn();
		}
		if (!checkStop()) {
			mowerList[nextMower].pollMowerForAction();
			nextMower++;
			countTurn();
			return nextMower;
		} else {
			return -1;
		}
	}
	private void countTurn() {
		if (nextMower >= mowerCount) {
			total_step++;
		}
		nextMower = nextMower % mowerCount;
	}
	
	public void stopRun() {
		pressStop = true;
	}

	public boolean checkStop() {
		if (activeMowerCount == 0 || total_cut == total_grass || total_step == numTurn) {
			System.out.println("mowerCount" + activeMowerCount);
			System.out.println("mowerCount" + numTurn);
			return true;
		}
		return false;
	}

	private void charge(int mowerID) {
		mowerList[mowerID].curEnergy = mowerList[mowerID].maxEnergy;
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
					break;
				default:
					System.out.print(lawnInfo[i][j]);
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

	public void testLoad(String testFileName) {
		Scanner sc;
		try {
			sc = new Scanner(new File(testFileName));
			while (sc.hasNext()) {
				System.out.println(sc.nextLine());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		String arg = "cs6310_a7_test8.txt";
		SimultaionRun test = new SimultaionRun(arg);
		test.renderLawn();
		test.act();
		// Use the following to see step by step motion, the lawn is updated upon each mower action. 
//		Scanner userInput = new Scanner(System.in);
//		for(int i = 0; i < 100; i++) {
//			String input = userInput.nextLine();
//			test.moveNext();
//			test.renderLawn();
//		}
		test.renderLawn();
	}
}
