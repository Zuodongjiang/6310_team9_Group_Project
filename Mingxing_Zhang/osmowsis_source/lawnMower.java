// There are two methods in this class. The first one is 'pollMowerForAction'.
// This will be called in every turn to query the mower to make a decision for the next action.
// The other one is 'updatePartialMap'. This method is used to build the partial knowledge map for the mower.
// The mower will make decision for the next action based this partial map. In the next phase of the project, I will
// focus on building an optimized algorithm for the mower to determine the next action.
// Since the mower doesn't have the knowledge of the whole map, so it can not get the scan result by itself.
// An attribute 'scanResults' is created to receive and store the scan result from the 'lawn' ckass,

import java.util.ArrayList;
import java.util.HashMap;

public class lawnMower {

    private Integer mowerXPartial;
    private Integer newMowerXPartial;
    private Integer mowerYPartial;
    private Integer newMowerYPartial;
    private String mowerDirection;
    private Integer [][] partialMap;


    private String oldTrackAction;
    private String trackAction;
    private Integer trackMoveDistance;
    private String trackNewDirection;
    private String trackMoveCheck;
    private ArrayList<String> scanResults;
    private String trackScanResults;

    public void pollMowerForAction() {

        if (oldTrackAction.equals("Start") || oldTrackAction.equals("move") && trackMoveDistance != 0){
            trackAction = "scan";
            oldTrackAction = "scan";
            trackMoveDistance = 0;
            return;
        }else{
            trackAction = "move";
            oldTrackAction = "move";

        }

        // determine the direction for next move
        if(scanResults.get(0).equals("grass")){ // check if north is grass
            trackNewDirection = "north";
//            trackMoveDistance = 1;
        }else{
            if(scanResults.get(6).equals("fence") || scanResults.get(6).equals("crater") || scanResults.get(6).equals("empty")){ // check if west is fence or crater
                if (scanResults.get(4).equals("grass")){ // check if south is grass
                    trackNewDirection = "south";
//                    trackMoveDistance = 1;
                }else{
                    if(scanResults.get(2).equals("grass")){
                        trackNewDirection = "east";

                    }else{
                        // add after only case 5 failed
                        if(scanResults.get(6).equals("empty")){

                            trackNewDirection = "west";
                        }else{

                            trackNewDirection = "southeast";
//                            trackNewDirection = "east";
                        }
                        // add after only case 5 failed
//                        trackNewDirection = "west"; //only case 5 failed
                    }
                }
            }else{
                if (scanResults.get(4).equals("grass")){ // check if south is grass
                    trackNewDirection = "south";
//                    trackMoveDistance = 1;
                }else{
                    if(scanResults.get(2).equals("grass")){
                        trackNewDirection = "east";

                    }else{
                        trackNewDirection = "west";

                    }
                }
            }

        }

        // if change direction, set the distance to 0
        if (mowerDirection.equals(trackNewDirection)){
            trackMoveDistance = 1;
        }else{
            trackMoveDistance = 0;
            mowerDirection = trackNewDirection;
        }

    }

    public void updatePartialMap() {

        HashMap<String, Integer> xDIR_PartialMAP = new HashMap<>();
        xDIR_PartialMAP.put("north", 0);
        xDIR_PartialMAP.put("northeast", 1);
        xDIR_PartialMAP.put("east", 1);
        xDIR_PartialMAP.put("southeast", 1);
        xDIR_PartialMAP.put("south", 0);
        xDIR_PartialMAP.put("southwest", -1);
        xDIR_PartialMAP.put("west", -1);
        xDIR_PartialMAP.put("northwest", -1);

        HashMap<String, Integer> yDIR_PartialMAP = new HashMap<>();
        yDIR_PartialMAP.put("north", -1);
        yDIR_PartialMAP.put("northeast", -1);
        yDIR_PartialMAP.put("east", 0);
        yDIR_PartialMAP.put("southeast", 1);
        yDIR_PartialMAP.put("south", 1);
        yDIR_PartialMAP.put("southwest", 1);
        yDIR_PartialMAP.put("west", 0);
        yDIR_PartialMAP.put("northwest", -1);

        HashMap<String, Integer> squareCodeMower = new HashMap<>();
        squareCodeMower.put("empty", 0);
        squareCodeMower.put("grass", 1);
        squareCodeMower.put("crater", 2);
        squareCodeMower.put("fence", 3);

        int xOrientation, yOrientation;
        Integer initialMowerX = 50;
        Integer initialMowerY = 50;

        if (trackAction.equals("scan")) {
            // in the case of a scan, add scan result to the partial map
            // mowerYPartial is the row index, while mowerXPartial is the column index
            partialMap[mowerYPartial -1][mowerXPartial] = squareCodeMower.get(scanResults.get(0)); // north
            partialMap[mowerYPartial -1][mowerXPartial + 1] = squareCodeMower.get(scanResults.get(1)); // northeast
            partialMap[mowerYPartial][mowerXPartial + 1] = squareCodeMower.get(scanResults.get(2)); // east
            partialMap[mowerYPartial + 1][mowerXPartial + 1] = squareCodeMower.get(scanResults.get(3)); // southeast
            partialMap[mowerYPartial + 1][mowerXPartial] = squareCodeMower.get(scanResults.get(4)); // south
            partialMap[mowerYPartial + 1][mowerXPartial - 1] = squareCodeMower.get(scanResults.get(5)); // southwest
            partialMap[mowerYPartial][mowerXPartial - 1] = squareCodeMower.get(scanResults.get(6)); // west
            partialMap[mowerYPartial - 1][mowerXPartial - 1] = squareCodeMower.get(scanResults.get(7)); // northwest

        } else if (trackAction.equals("move")) {
            // in the case of a move, ensure that the move doesn't cross craters or fences
            xOrientation = xDIR_PartialMAP.get(mowerDirection);
            yOrientation = yDIR_PartialMAP.get(mowerDirection);

            // new location of the mower in its partial map
            mowerXPartial = mowerXPartial + trackMoveDistance * xOrientation;
            mowerYPartial = mowerYPartial + trackMoveDistance * yOrientation;

            // update the partial map
            partialMap[mowerYPartial][mowerXPartial] = 0; // mower's current location

        }
    }

    public void setMowerXPartial(Integer mowerX) {
        this.mowerXPartial = mowerX;
    }

    public void setMowerYPartial(Integer mowerY) {
        this.mowerYPartial = mowerY;
    }

    public void setMowerDirection(String mowerDirection) {
        this.mowerDirection = mowerDirection;
    }

    public void setPartialMap(Integer[][] partialMap) {
        this.partialMap = partialMap;
    }

    public String getMowerDirection() {
        return mowerDirection;
    }

    public String getTrackNewDirection() {
        return trackNewDirection;
    }

    public String getTrackAction() {
        return trackAction;
    }

    public Integer getTrackMoveDistance() {
        return trackMoveDistance;
    }

    public void setScanResults(ArrayList<String> scanResults) {
        this.scanResults = scanResults;
    }

//    public void setNumExeTurns(Integer numExeTurns) {
//        this.numExeTurns = numExeTurns;
//    }
//
//    public void setNumTotalTurns(Integer numTotalTurns) {
//        this.numTotalTurns = numTotalTurns;
//    }

    public void setOldTrackAction(String oldTrackAction) {
        this.oldTrackAction = oldTrackAction;
    }

    public void setTrackMoveDistance(Integer trackMoveDistance) {
        this.trackMoveDistance = trackMoveDistance;
    }

    public void setpartialMap(Integer[][] partialMap){
        this.partialMap = partialMap;
    }
}
