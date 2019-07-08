// There are 5 methods in this class.
// 'trackMower' will check if the mower's action is valid or not. If the move action is valid, it will calculate
// the new location of the mower to track the mower. If the action is scan, it will call the 'scanResult' method to obtain the
// scan result. The scan result will be sent to the lawnMower.
// 'checkToStop' is used to check if the simulation should be stopped. 'displayActionAndResponses' is used to display the information for each action.
// 'renderLawn' is just a method to check what the lawn finally looks. It will not be called in the submitted version.

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class lawn {

    private Integer lawnHeight;
    private Integer lawnWidth;
    private Integer[][] lawnInfo;
    private Integer mowerX, mowerY;
    private String mowerDirection;
    private Integer numTotalTurns;
    private Integer numExeTurns;
    private Integer numCraters;
    private Integer[][] craterInfoLawn;
    private Integer numTotalGrass;
    private Integer numCutGrass;

    private final int EMPTY_CODE = 0;
    private final int GRASS_CODE = 1;
    private final int CRATER_CODE = 2;

    private String trackAction;
    private Integer trackMoveDistance;
//    private String trackNewDirection;
    private String trackMoveCheck;
    private ArrayList<String> scanResults;
    private String trackScanResults;

    public void trackMower() {
        HashMap<String, Integer> xDIR_MAP = new HashMap<>();
        xDIR_MAP.put("north", 0);
        xDIR_MAP.put("northeast", 1);
        xDIR_MAP.put("east", 1);
        xDIR_MAP.put("southeast", 1);
        xDIR_MAP.put("south", 0);
        xDIR_MAP.put("southwest", -1);
        xDIR_MAP.put("west", -1);
        xDIR_MAP.put("northwest", -1);

        HashMap<String, Integer> yDIR_MAP = new HashMap<>();
        yDIR_MAP.put("north", -1);
        yDIR_MAP.put("northeast", -1);
        yDIR_MAP.put("east", 0);
        yDIR_MAP.put("southeast", 1);
        yDIR_MAP.put("south", 1);
        yDIR_MAP.put("southwest", 1);
        yDIR_MAP.put("west", 0);
        yDIR_MAP.put("northwest", -1);

        int xOrientation, yOrientation;

        switch (trackAction) {
            case "scan":
                // in the case of a scan, return the information for the eight surrounding squares
                // always use a northbound orientation
                scanResults = scanResult();
                trackScanResults = scanResults.get(0) + "," + scanResults.get(1) + "," + scanResults.get(2) + "," + scanResults.get(3) + "," + scanResults.get(4) + "," + scanResults.get(5) + "," + scanResults.get(6) + "," + scanResults.get(7);
                trackMoveCheck = "ok";
                break;
            case "move":
                // in the case of a move, ensure that the move doesn't cross craters or fences
                xOrientation = xDIR_MAP.get(mowerDirection);
                yOrientation = yDIR_MAP.get(mowerDirection);

                // get the new location of the mower
                int newSquareX = mowerX + trackMoveDistance * xOrientation;
                int newSquareY = mowerY + trackMoveDistance * yOrientation;

                // check if the index are correct
//                System.out.println("The height is:" + lawnHeight);
//                System.out.println("New row index is:" + newSquareY);
//                System.out.println("The width is:" + lawnWidth);
//                System.out.println("New column index is:" + newSquareX);

                // check the move does not crash a crater
                boolean notCrashCrater = true;
                for (Integer[] integers : craterInfoLawn) {
                    if (newSquareX == integers[1] & newSquareY == integers[0]) {
                        notCrashCrater = false;
                        break;
                    }
                }
                // check if the move is ok or not
                if (newSquareX >= 0 & newSquareX < lawnWidth & newSquareY >= 0 & newSquareY < lawnHeight & notCrashCrater) {
                    mowerX = newSquareX;
                    mowerY = newSquareY;
                    trackMoveCheck = "ok";

                    // the move is valid, update lawn status
                    lawnInfo[mowerY][mowerX] = EMPTY_CODE;
                } else {
                    // the move is invalid, crash happened
                    trackMoveCheck = "crash";
                }

                // check if the index are correct
//                System.out.println("row index is: " + mowerY);
//                System.out.println("column index is: " + mowerX);

                break;
//            case "turn_off":
//                trackMoveCheck = "ok";
//                break;
        }
    }

    public ArrayList<String> scanResult() {
        HashMap<Integer, String> squareCode = new HashMap<>();
        squareCode.put(0, "empty");
        squareCode.put(1, "grass");
        squareCode.put(2, "crater");
        squareCode.put(3, "fence");

        ArrayList<String> scanResult = new ArrayList<>();

        if ((mowerY - 1) < 0) {
            scanResult.add(squareCode.get(3)); // north square if it is fence
        } else {
            scanResult.add(squareCode.get(lawnInfo[mowerY - 1][mowerX])); // north square
        }

        if ((mowerX + 1) >= lawnWidth || (mowerY - 1) < 0) {
            scanResult.add(squareCode.get(3)); // northeast square if it is fence
        } else {
            scanResult.add(squareCode.get(lawnInfo[mowerY - 1][mowerX + 1])); // northeast square
        }

        if ((mowerX + 1) >= lawnWidth) {
            scanResult.add(squareCode.get(3)); // east square if it is fence
        } else {
            scanResult.add(squareCode.get(lawnInfo[mowerY][mowerX + 1])); // east square
        }

        if ((mowerX + 1) >= lawnWidth || (mowerY + 1) >= lawnHeight) {
            scanResult.add(squareCode.get(3)); // southeast square if it is fence
        } else {
            scanResult.add(squareCode.get(lawnInfo[mowerY + 1][mowerX + 1])); // southeast square
        }

        if ((mowerY + 1) >= lawnHeight) {
            scanResult.add(squareCode.get(3)); // south square if it is fence
        } else {
            scanResult.add(squareCode.get(lawnInfo[mowerY + 1][mowerX])); // south square
        }

        if ((mowerX - 1) < 0 || (mowerY + 1) >= lawnHeight) {
            scanResult.add(squareCode.get(3)); // southwest square if it is fence
        } else {
            scanResult.add(squareCode.get(lawnInfo[mowerY + 1][mowerX - 1])); // southwest square
        }

        if ((mowerX - 1) < 0) {
            scanResult.add(squareCode.get(3)); // west square if it is fence
        } else {
            scanResult.add(squareCode.get(lawnInfo[mowerY][mowerX - 1])); // west square
        }

        if ((mowerX - 1) < 0 || (mowerY - 1) < 0) {
            scanResult.add(squareCode.get(3)); // northwest square if it is fence
        } else {
            scanResult.add(squareCode.get(lawnInfo[mowerY - 1][mowerX - 1])); // northwest square
        }
        return scanResult;
    }

    public int checkToStop(){

        int numCutGrassSquares = 0;
        for (int i = 0; i < lawnHeight; i++) {
            for (int j = 0; j < lawnWidth; j++) {
                if (lawnInfo[i][j] == 0) {
                    numCutGrassSquares = numCutGrassSquares + 1;
                }
            }
        }

        numCutGrass = numCutGrassSquares;

        if (numCutGrassSquares == numTotalGrass || numExeTurns >= numTotalTurns || trackMoveCheck.equals("crash")){
//            trackAction = "turn_off";
//            trackMoveCheck = "ok";
            return 1;
        }else{
            return 0;
        }
    }
    public void displayActionAndResponses() {
        // display the mower's actions
//        System.out.print(trackAction);
//        if (trackAction.equals("move")) {
//            System.out.println("," + trackMoveDistance + "," + mowerDirection);
//        } else {
//            System.out.println();
//        }

        // display the mower's actions
        if (trackAction.equals("move")) {
//            System.out.print(trackAction);
            System.out.println(trackAction + "," + trackMoveDistance + "," + mowerDirection);

            if(numCutGrass == numTotalGrass || numExeTurns >= numTotalTurns || trackMoveCheck.equals("crash")){
                System.out.println(trackMoveCheck);
                System.out.println((lawnWidth * lawnHeight) + "," + numTotalGrass + "," + numCutGrass + "," + numExeTurns);

                // check if all grass are cut
//                for(Integer[] lawnInfoTemp2 : lawnInfo) {
//                    System.out.println(Arrays.toString(lawnInfoTemp2) + "\n");
//                }

            }else{

                System.out.println(trackMoveCheck);
            }
        }

        else if (trackAction.equals("scan")) {
            System.out.println(trackAction);
            System.out.println(trackScanResults);
        } else {
            System.out.println("action not recognized");
        }
    }


    public void renderLawn() {
        for (Integer[] rowLawnInfo : lawnInfo){
            System.out.println(Arrays.toString(rowLawnInfo) + "\n");
        }
        // display the mower's direction
        System.out.println("dir: " + mowerDirection);
        System.out.println("");
    }

    public void setLawnInfo(Integer[][] lawnInfo) {
        this.lawnInfo = lawnInfo;
    }

    public Integer[][] getLawnInfo() {
        return lawnInfo;
    }

    public String getTrackScanResults() {
        return trackScanResults;
    }

    public void setLawnHeight(Integer lawnHeight) {
        this.lawnHeight = lawnHeight;
    }

    public void setLawnWidth(Integer lawnWidth) {
        this.lawnWidth = lawnWidth;
    }

    public void setMowerX(Integer mowerX) {
        this.mowerX = mowerX;
    }

    public void setMowerY(Integer mowerY) {
        this.mowerY = mowerY;
    }

    public void setMowerDirection(String mowerDirection) {
        this.mowerDirection = mowerDirection;
    }

    public void setTrackAction(String trackAction) {
        this.trackAction = trackAction;
    }

    public void setTrackMoveDistance(Integer trackMoveDistance) {
        this.trackMoveDistance = trackMoveDistance;
    }

    public void setNumCraters(Integer numCraters) {
        this.numCraters = numCraters;
    }

    public void setNumTotalTurns(Integer numTotalTurns) {
        this.numTotalTurns = numTotalTurns;
    }

    public void setNumExeTurns(Integer numExeTurns) {
        this.numExeTurns = numExeTurns;
    }

    public void setNumTotalGrass(Integer numTotalGrass) {
        this.numTotalGrass = numTotalGrass;
    }

    public void setNumCutGrass(Integer numCutGrass) {
        this.numCutGrass = numCutGrass;
    }

    public void setCraterInfoLawn(Integer[][] craterInfoLawn) {
        this.craterInfoLawn = craterInfoLawn;
    }
}
