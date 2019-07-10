package Model;

import Viewer.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class LawnMower {
    private MowerStatus currentStatus;
    private int stallTurn; // 0 means the mower is active. larger than 0 means mower is stalled. should decrease by 1 in each turn.
    private Location currentLoc;
    private Direction currentDirection;
    private Action cacheAction;
    private HashMap<Direction, Integer> xDIR_MAP;
    private HashMap<Direction, Integer> yDIR_MAP;
    private Random randGenerator;
    private Action cachedNextAction;

    public Action getCacheAction() {
        return cacheAction;
    }
    public Action getCachedNextAction() {return cachedNextAction;}
    public Direction getCurrentDirection() {
        return currentDirection;
    }
    public Location getCurrentLoc() {
        return currentLoc;
    }

    public void setCachedNextAction(Action a) {
        this.cachedNextAction = a;
    }

    public LawnMower(Location loc, Direction dir) {
        currentStatus = MowerStatus.active;
        randGenerator = new Random();
        stallTurn = 0;
        currentLoc = loc;
        currentDirection = dir;
        cacheAction = null;
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


    // TODO: get the next action of the mower
    public Action nextAction(MowerMap lawn) {
        if (cacheAction != null) {
            Action act = new Action(cacheAction.getName());
            if (act.getName().equals("move")) {
                act.setMoveAction(cacheAction.getStepSize(), cacheAction.getDirection());
            }
            cacheAction = null;
            return act;
        }
        int x = currentLoc.getX();
        int y = currentLoc.getY();
        int xOrientation, yOrientation;
        int totalCut = 0;
        int steps = 0;
        Direction maxDirection = currentDirection;

        xOrientation = xDIR_MAP.get(currentDirection);
        yOrientation = yDIR_MAP.get(currentDirection);

        x += xOrientation;
        y += yOrientation;

        while (lawn.getSquare(new Location(x, y)) != SquareState.unknown) {
            SquareState tmp = lawn.getSquare(new Location(x, y));
            if (!(tmp == SquareState.empty || tmp == SquareState.grass)) break;
            if (tmp == SquareState.grass) totalCut += 1;
            steps += 1;
            x += xOrientation;
            y += yOrientation;
        }

        // OK to follow current direction.
        if (totalCut > 0) {
            Action act = new Action("move");
            act.setMoveAction(Math.min(2, steps), currentDirection);
            return act;
        }

        for (Direction d : Direction.values()) {
            if (d == currentDirection) continue; // already checked current direction.
            x = currentLoc.getX();
            y = currentLoc.getY();
            xOrientation = xDIR_MAP.get(d);
            yOrientation = yDIR_MAP.get(d);
            x += xOrientation;
            y += yOrientation;

            int tmpCut = 0;
            while (lawn.getSquare(new Location(x, y)) != SquareState.unknown) {
                SquareState tmp = lawn.getSquare(new Location(x, y));
                if (!(tmp == SquareState.empty || tmp == SquareState.grass)) break;
                if (tmp == SquareState.grass) tmpCut += 1;
                x += xOrientation;
                y += yOrientation;
            }

            if (tmpCut > totalCut) {
                totalCut = tmpCut;
                maxDirection = d;
            }
        }

        if (totalCut > 0) {
            Action act = new Action("move");
            act.setMoveAction(0, maxDirection);
            return act;
        }

        // scan if there are any unknown square around.
        for (Direction d : Direction.values()) {
            if (d == currentDirection) continue; // already checked current direction.
            x = currentLoc.getX();
            y = currentLoc.getY();
            xOrientation = xDIR_MAP.get(d);
            yOrientation = yDIR_MAP.get(d);
            x += xOrientation;
            y += yOrientation;
            if (lawn.getSquare(new Location(x, y)) == SquareState.unknown) return new Action("scan");
        }

        if (lawn.isCompleted()) return new Action("turn off");

        // if reach here, means mower could not find any grass to cut on all 8 directions, but there are still remain
        // some invisible squares. So, search for invisible squares next.
        int closestUnknownSquare = Integer.MAX_VALUE;
        List<Direction> randomDirs = new ArrayList<Direction>();
        List<Integer> randomSteps = new ArrayList<Integer>();
        for (Direction d : Direction.values()) {
            if (d == currentDirection) continue; // already checked current direction.
            x = currentLoc.getX();
            y = currentLoc.getY();
            xOrientation = xDIR_MAP.get(d);
            yOrientation = yDIR_MAP.get(d);
            x += xOrientation;
            y += yOrientation;

            int tmpDist = 0;
            while (lawn.getSquare(new Location(x, y)) != SquareState.unknown) {
                SquareState tmp = lawn.getSquare(new Location(x, y));
                if (tmp == SquareState.out_of_bound || tmp == SquareState.crater || tmp == SquareState.fence) {
                    if (tmpDist > 0) {
                        randomDirs.add(d);
                        randomSteps.add(tmpDist);
                        tmpDist = 0; // reach fence or crater, do not find invisible square.
                    }
                    break;
                }
                x += xOrientation;
                y += yOrientation;
                tmpDist += 1;
            }

            if (tmpDist > 0 && tmpDist < closestUnknownSquare) {
                closestUnknownSquare = tmpDist;
                maxDirection = d;
            }
        }

        if (closestUnknownSquare != Integer.MAX_VALUE && closestUnknownSquare > 0) {
            Action act = new Action("move");
            // if mower is currently at maxDirection, just move for next. Otherwise, should turn direction.
            // move max two steps per turn.
            if (currentDirection == maxDirection) act.setMoveAction(Math.min(closestUnknownSquare, 2), maxDirection);
            else
            {
                cacheAction = new Action("move");
                cacheAction.setMoveAction(closestUnknownSquare, maxDirection);
                act.setMoveAction(0, maxDirection);
            }
            return act;
        }

        // if reach here, all direction of mower is blocked by either fence or crater. Do random move.
        if (randomDirs.size() == 0) return new Action("scan");

        int randomMoveChoice = randGenerator.nextInt(randomDirs.size());
        Direction rDir = randomDirs.get(randomMoveChoice);
        int step = Math.min(2, randomSteps.get(randomMoveChoice));

        Action act = new Action("move");
        if (currentDirection == rDir) act.setMoveAction(step, rDir);
        else  {
            cacheAction = new Action("move");
            cacheAction.setMoveAction(step, rDir);
            act.setMoveAction(0, rDir);
        }
        return act;
    }


    public MowerStatus getCurrentStatus() {

        return currentStatus;
    }

    public void setCurrentStatus(MowerStatus currentStatus) {

        this.currentStatus = currentStatus;
    }

    public int getStallTurn() {

        return stallTurn;
    }

    public void setStallTurn(int stallTurn) {
        if (stallTurn <= 0) {
            this.stallTurn = 0;
            currentStatus = MowerStatus.active;
        } else {
            this.stallTurn = stallTurn;
            currentStatus = MowerStatus.stalled;
        }
    }

    public void setCurrentLoc(Location loc) {
        currentLoc = loc;
    }

    public void setCurrentDirection(Direction dir) {
        currentDirection = dir;
    }
}
