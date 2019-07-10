package Model;

import Viewer.*;

import java.util.HashMap;
import java.util.Random;

public class SimulationMonitor {
    private static Random randGenerator;
    private Location[] mowerLocations;
    private Direction[] mowerDirections;
    private Puppy[] puppyList;
    private LawnMower[] mowerList;
    private RealLawn lawn;
    private MowerMap mowerMap;
    private int totalSize;
    private int totalGrass;
    private int totalCrater;
    private int cutGrass;
    private int stoppedMowers;
    private int crashedMowers; // for debug
    private boolean simulationOn;
    private int totalTurn;
    private int initialTotalTurn;
    private int stallTurn;
    private double stayPercent;
    private int turnUsed;

    public int getCurrentMowerIdx() {
        return currentMowerIdx;
    }

    private int currentMowerIdx;
    private int currentPuppyIdx;

    private HashMap<Direction, Integer> xDIR_MAP;
    private HashMap<Direction, Integer> yDIR_MAP;

    public LawnMower[] getMowerList() {
        return mowerList;
    }

    public RealLawn getLawn() {
        return lawn;
    }

    public int getTotalGrass() {
        return totalGrass;
    }

    public int getStallTurn() {
        return stallTurn;
    }

    public int getInitialTotalTurn() {
        return initialTotalTurn;
    }

    public int getTotalTurn() {
        return totalTurn;
    }

    public int getTotalCrater() {
        return totalCrater;
    }

    // initialize the simulation.
    public void initialize(InputFile input) {
        randGenerator = new Random();
        simulationOn = true;
        lawn = new RealLawn(input.getLawnWidth(), input.getLawnHeight());
        mowerMap = new MowerMap();
        mowerMap.initializeMap();
        lawn.squares = new SquareState[input.getLawnWidth()][input.getLawnHeight()];
        mowerLocations = input.getMowerLocationsCopy();
        mowerDirections = input.getMowerDirectionsCopy();
        totalSize = input.getLawnWidth() * input.getLawnHeight();
        cutGrass = 0;
        totalGrass = totalSize;
        totalTurn = input.getTotalTurn();
        initialTotalTurn = input.getTotalTurn();
        stallTurn = input.getStallTurn();
        stayPercent = input.getStayPercent();
        stoppedMowers = 0;
        currentMowerIdx = 0;
        currentPuppyIdx = 0;
        turnUsed = 0;

        for(int i = 0; i < input.getLawnWidth(); i++){
            for(int j = 0; j < input.getLawnHeight(); j++){
                lawn.squares[i][j] = SquareState.grass;
            }
        }

        mowerList = new LawnMower[mowerLocations.length];
        for (int i = 0; i < mowerLocations.length; i++) {
            lawn.setSquare(mowerLocations[i], SquareState.mower);
            mowerMap.setSquare(mowerLocations[i], SquareState.mower, Direction.south); // the last parameter does not matter in this case.
            mowerList[i] = new LawnMower(mowerLocations[i], mowerDirections[i]);
            mowerList[i].setCachedNextAction(new Action("scan"));
        }
        cutGrass = mowerList.length; // mower always cut the grass at initial location.

        Location[] craterLocs = input.getCraterLocations();
        totalCrater = craterLocs.length;
        for (int i = 0; i < craterLocs.length; i++) {
            lawn.setSquare(craterLocs[i], SquareState.crater);
            totalGrass -= 1;
        }

        Location[] puppyLocations = input.getPuppyLocations();
        puppyList = new Puppy[puppyLocations.length];
        for (int i = 0; i < puppyLocations.length; i++) {
            lawn.setSquare(puppyLocations[i], SquareState.puppy_grass);
            puppyList[i] = new Puppy(stayPercent);
            puppyList[i].setPuppyLocation(puppyLocations[i]);
        }
    }

    public SimulationMonitor() {
        xDIR_MAP = new HashMap<Direction, Integer>();
        xDIR_MAP.put(Direction.north, 0);
        xDIR_MAP.put(Direction.northeast, 1);
        xDIR_MAP.put(Direction.east, 1);
        xDIR_MAP.put(Direction.southeast, 1);
        xDIR_MAP.put(Direction.south, 0);
        xDIR_MAP.put(Direction.southwest, -1);
        xDIR_MAP.put(Direction.west, -1);
        xDIR_MAP.put(Direction.northwest, -1);

        yDIR_MAP = new HashMap<Direction, Integer>();
        yDIR_MAP.put(Direction.north, 1);
        yDIR_MAP.put(Direction.northeast, 1);
        yDIR_MAP.put(Direction.east, 0);
        yDIR_MAP.put(Direction.southeast, -1);
        yDIR_MAP.put(Direction.south, -1);
        yDIR_MAP.put(Direction.southwest, -1);
        yDIR_MAP.put(Direction.west, 0);
        yDIR_MAP.put(Direction.northwest, 1);
    }


    //TODO: move the mower
    // assmue the input action is move
    // major change: also update mwoermap in this method. simplify code.
    public void moveMower(int mowerID, Action action){
        int xOrientation, yOrientation;

        int stepSize = action.getStepSize(); // get the move step size
        int x = mowerLocations[mowerID].getX();
        int y = mowerLocations[mowerID].getY();
        xOrientation = xDIR_MAP.get(mowerDirections[mowerID]);
        yOrientation = yDIR_MAP.get(mowerDirections[mowerID]);
        while (stepSize > 0) {
            x += xOrientation;
            y += yOrientation;
            SquareState status = lawn.getSquareState(new Location(x, y));
            // check other Mower
            if (status == SquareState.mower || status == SquareState.puppy_mower) {
                // stop before the other mower's square.
                x -= xOrientation;
                y -= yOrientation;
                // update current mower location.
                mowerLocations[mowerID] = new Location(x, y);
                // also update this location is real Lawn.
                lawn.setSquare(mowerLocations[mowerID], SquareState.mower);
                // also update this location in mower map.
                mowerMap.setSquare(mowerLocations[mowerID], SquareState.mower, mowerDirections[mowerID]);
                mowerList[mowerID].setStallTurn(stallTurn);
                System.out.println(String.format("stall,%d", action.getStepSize()-stepSize));
                return;
            }

            // check puppy (puppy_grass, puppy_empty)
            if (status.toString().substring(0, 5).equals("puppy")) {

                if (status == SquareState.puppy_grass) cutGrass += 1; // will cut grass.

                mowerLocations[mowerID] = new Location(x, y); // mower move into puppy's location.
                lawn.setSquare(mowerLocations[mowerID], SquareState.puppy_mower);
                mowerMap.setSquare(mowerLocations[mowerID], SquareState.mower, mowerDirections[mowerID]); // ignore puppy in mowermap.
//                mowerList[mowerID].setStallTurn(stallTurn); // no need to stall

                // set previous location to empty.
                Location prevLoc = new Location(x-xOrientation, y-yOrientation);
                if (lawn.getSquareState(prevLoc) == SquareState.mower) {
                    lawn.setSquare(prevLoc, SquareState.empty);
                    mowerMap.setSquare(prevLoc, SquareState.empty, mowerDirections[mowerID]);
                }
                else if (lawn.getSquareState(prevLoc) == SquareState.puppy_mower) {
                    lawn.setSquare(prevLoc, SquareState.puppy_empty);
                    mowerMap.setSquare(prevLoc, SquareState.empty, mowerDirections[mowerID]); // ignore puppy in mowermap.
                }
                System.out.println(String.format("stall,%d", action.getStepSize()-stepSize+1));
                return;
            }

            // check if square can cut or not. No need to consider puppy or mower here.
            // set previous location to empty.
            Location prevLoc = new Location(x-xOrientation, y-yOrientation);
            if (lawn.getSquareState(prevLoc) == SquareState.mower) {
                lawn.setSquare(prevLoc, SquareState.empty);
                mowerMap.setSquare(prevLoc, SquareState.empty, mowerDirections[mowerID]);
            }
            else if (lawn.getSquareState(prevLoc) == SquareState.puppy_mower) {
                lawn.setSquare(prevLoc, SquareState.puppy_empty);
                mowerMap.setSquare(prevLoc, SquareState.empty, mowerDirections[mowerID]); // ignore puppy in mowermap.
            }

            if (lawn.getSquareState(new Location(x, y)) == SquareState.grass) cutGrass += 1; // will cut grass.

            if (!lawn.cutSquare(new Location(x, y))) {
                // if crashed, the crashed mower supports to know it crashed into fence or crater.
                if (lawn.getSquareState(new Location(x, y)) == SquareState.fence)
                    mowerMap.setSquare(new Location(x, y), SquareState.fence, mowerDirections[mowerID]);
                else
                    mowerMap.setSquare(new Location(x, y), SquareState.crater, mowerDirections[mowerID]);
                mowerList[mowerID].setCurrentStatus(MowerStatus.crashed);
                stoppedMowers += 1;
//                crashedMowers += 1; // for debug
                return;
            } else {
                // set current location to mower.
                lawn.setSquare(new Location(x, y), SquareState.mower);
                mowerMap.setSquare(new Location(x, y), SquareState.mower, mowerDirections[mowerID]);
            }
            stepSize -= 1;
        }
        mowerLocations[mowerID] = new Location(x, y);
        mowerDirections[mowerID] = action.getDirection();
        lawn.setSquare(mowerLocations[mowerID], SquareState.mower);
        mowerMap.setSquare(mowerLocations[mowerID], SquareState.mower, mowerDirections[mowerID]);
        mowerList[mowerID].setCurrentLoc(mowerLocations[mowerID]);
        mowerList[mowerID].setCurrentDirection(mowerDirections[mowerID]);
        System.out.println("ok");
    }

    // TODO: mower scan
    public SquareState[] scan(Location loc){
        SquareState[] sur = new SquareState[8];
        int x = loc.getX();
        int y = loc.getY();
        // clockwise scan
        sur[0] = lawn.getSquareState(new Location(x, y+1)); // North
        sur[1] = lawn.getSquareState(new Location(x+1, y+1)); // NorthEast
        sur[2] = lawn.getSquareState(new Location(x+1, y)); // East
        sur[3] = lawn.getSquareState(new Location(x+1, y-1)); // SouthEast
        sur[4] = lawn.getSquareState(new Location(x, y-1)); // South
        sur[5] = lawn.getSquareState(new Location(x-1, y-1)); // SouthWest
        sur[6] = lawn.getSquareState(new Location(x-1, y)); // West
        sur[7] = lawn.getSquareState(new Location(x-1, y+1)); // NorthWest
        return sur;
    }


    // TODO: get # of grasses cut
    public int getCutGrass(){ return cutGrass;}

    public boolean issimulationOn() {
        return simulationOn;
    }

    public void nextMove() {
        if (totalTurn == 0 || totalGrass == cutGrass) {
            simulationOn = false;
            report();
            return;
        }

        if (stoppedMowers == mowerList.length) {
            simulationOn = false;
            report();
            return;
        }

        while (currentMowerIdx < mowerList.length) {
            if (mowerList[currentMowerIdx].getCurrentStatus() == MowerStatus.crashed ||
                    mowerList[currentMowerIdx].getCurrentStatus() == MowerStatus.turnedOff) {
                currentMowerIdx += 1;
                continue;
            }
            if (mowerList[currentMowerIdx].getCurrentStatus() == MowerStatus.stalled) {
                mowerList[currentMowerIdx].setStallTurn(mowerList[currentMowerIdx].getStallTurn() - 1);
                currentMowerIdx += 1;
                continue;
            }
            if (lawn.getSquareState(mowerLocations[currentMowerIdx]) == SquareState.puppy_mower) {
                currentMowerIdx += 1;
                continue;
            }
            break;
        }
        // move mower.
        if (currentMowerIdx != mowerList.length) {
            System.out.println(String.format("mower,%d", currentMowerIdx+1));
            Action act = mowerList[currentMowerIdx].nextAction(mowerMap);
            mowerList[currentMowerIdx].setCachedNextAction(act);
            if (act.getName().equals("move")) {
                System.out.println(String.format("move,%d,%s", act.getStepSize(), act.getDirection().toString()));
                moveMower(currentMowerIdx, act);
//                System.out.println(String.format("Current direction: %s", mowerDirections[currentMowerIdx].toString()));
//                System.out.println(String.format("Step size: %d", act.getStepSize()));
//                System.out.println(String.format("Next direction: %s", act.getDirection().toString()));
            }
            else if (act.getName().equals("scan")) {
                System.out.println("scan");
                SquareState[] sur = scan(mowerLocations[currentMowerIdx]);
                for (int i = 0; i < sur.length-1; i++) {
                    System.out.print(sur[i].toString());
                    System.out.print(",");
                }
                System.out.println(sur[sur.length-1].toString());
                mowerMap.updateMapFromScan(mowerLocations[currentMowerIdx], sur);
            }
            else {
                System.out.println("turn_off");
                System.out.println("ok");
                mowerList[currentMowerIdx].setCurrentStatus(MowerStatus.turnedOff);
                stoppedMowers += 1;
            }
            currentMowerIdx += 1;
        } else { // move puppy.
            // if iterated both mower and puppy, finish one turn.
            if (currentMowerIdx == mowerList.length && currentPuppyIdx == puppyList.length) {
                currentMowerIdx = 0;
                currentPuppyIdx = 0;
                totalTurn -= 1;
                turnUsed += 1;
//                System.out.println(String.format("Current Turn: %d", totalTurn));
//                System.out.println(String.format("Stopped Mower: %d", stoppedMowers));
//                System.out.println(String.format("Crashed Mower: %d", crashedMowers));
//                lawn.renderLawn(mowerLocations);
//                mowerMap.renderLawn();
                return;
            }
            String action = puppyList[currentPuppyIdx].nextAction();
            System.out.println(String.format("puppy,%d", currentPuppyIdx+1));
            if (action != "stay") {
                // scan surrounding
                Location currentLoc = puppyList[currentPuppyIdx].getPuppyLocation();
                SquareState[] sur = scan(currentLoc);
                int randomMoveChoice = randGenerator.nextInt(sur.length);
                // find a safe random location.
                while (!(sur[randomMoveChoice] == SquareState.empty ||
                        sur[randomMoveChoice] == SquareState.grass ||
                        sur[randomMoveChoice] == SquareState.mower)) {
                    randomMoveChoice = randGenerator.nextInt(sur.length);
                }
                Direction[] dirs = new Direction[]{Direction.north, Direction.northeast, Direction.east,
                        Direction.southeast, Direction.south, Direction.southwest, Direction.west,
                        Direction.northwest};

                int xOrientation = xDIR_MAP.get(dirs[randomMoveChoice]);
                int yOrientation = yDIR_MAP.get(dirs[randomMoveChoice]);
                int x = currentLoc.getX() + xOrientation;
                int y = currentLoc.getY() + yOrientation;
                Location newLoc = new Location(x, y);
                puppyList[currentPuppyIdx].setPuppyLocation(newLoc);
                // update the real lawn based on puppy movement.
                // update the new location.
                if (lawn.getSquareState(newLoc) == SquareState.empty) lawn.setSquare(newLoc, SquareState.puppy_empty);
                else if (lawn.getSquareState(newLoc) == SquareState.grass) lawn.setSquare(newLoc, SquareState.puppy_grass);
                else lawn.setSquare(newLoc, SquareState.puppy_mower);
                // update previous location.
                if (lawn.getSquareState(currentLoc) == SquareState.puppy_grass) lawn.setSquare(currentLoc, SquareState.grass);
                else if (lawn.getSquareState(currentLoc) == SquareState.puppy_empty) lawn.setSquare(currentLoc, SquareState.empty);
                else if (lawn.getSquareState(currentLoc) == SquareState.puppy_mower) lawn.setSquare(currentLoc, SquareState.mower);

                System.out.println(String.format("move,%d,%d", x, y));
            } else {
                System.out.println("stay");
            }
            System.out.println("ok");
            currentPuppyIdx += 1;
        }
    }

    public void report() {
        System.out.print(totalSize);
        System.out.print(",");
        System.out.print(totalGrass);
        System.out.print(",");
        System.out.print(getCutGrass());
        System.out.print(",");
        System.out.println(turnUsed);
    }
}
