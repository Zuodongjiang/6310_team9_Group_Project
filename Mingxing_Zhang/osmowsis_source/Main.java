public class Main {
    public Main() {

    }

    public static void main(String[] args) {
        FileUtilities loadData = new FileUtilities();
        lawn lawn = new lawn();
        lawnMower lawnMower = new lawnMower();

        // initialize a partial knowledge map with the size of 100 by 100 for the mower
        Integer[][] partialMapInitial = new Integer[100][100];

        // check for the test scenario file name
        if (args.length == 0) {
//        if (args.length == 10) {
            System.out.println("ERROR: Test scenario file name not found.");
        } else {
            loadData.uploadStartingFile(args[0]);
//            String filePathCSV = "/home/student/cs6310/ProjectIndividual/osmowsis/scenario6.csv";
//            loadData.uploadStartingFile(filePathCSV);

            lawn.setLawnWidth(loadData.getLawnWidth());
            lawn.setLawnHeight(loadData.getLawnHeight());
            lawn.setLawnInfo(loadData.getLawnInfo());
            lawn.setMowerX(loadData.getMowerX());
            lawn.setMowerY(loadData.getMowerY());
            lawn.setMowerDirection(loadData.getMowerDirection());
            lawn.setCraterInfoLawn(loadData.getCraterInfo());

            Integer numTotalTurns = loadData.getNumTurns();
            lawn.setNumTotalTurns(numTotalTurns);
            lawn.setNumCraters(loadData.getNumCraters());
            lawn.setNumTotalGrass(loadData.getNumGrass());

            // set the original direction for the mower, the original direction is read from the csv file
            lawnMower.setMowerDirection(loadData.getMowerDirection());
            // initialize the partial knowledge map for the mower
            lawnMower.setPartialMap(partialMapInitial);
            // put the mower at (50, 50) into its partial knowledge map
            lawnMower.setMowerXPartial(50);
            lawnMower.setMowerYPartial(50);
            // when the simulation start, I want the mower to scan first, so I set the original action as "Start" to
            // tell the pollMowerForAction method in the lawnMower class that this is the start of the simulation, do scan first.
            lawnMower.setOldTrackAction("Start");
            lawnMower.setTrackMoveDistance(1);

            // run the simulation for the number of turns
            for(int turns = 0; turns < numTotalTurns; turns++) {

                // query the mower to determine the next action
                lawnMower.pollMowerForAction();

                // get information of the new action and sent them to the lawn class, these information will be used to
                // track the mower by the lawn class.
                lawn.setTrackAction(lawnMower.getTrackAction());
                lawn.setMowerDirection(lawnMower.getTrackNewDirection());
                lawn.setTrackMoveDistance(lawnMower.getTrackMoveDistance());

                // the trackMower method in class lawn will validate the action and perform the action
                lawn.trackMower();

                // if the action is scan, send the scan result to the mower with the setScanResult method,
                if (lawnMower.getTrackAction().equals("scan")){
                    lawnMower.setScanResults(lawn.scanResult());
                }

                // update the number of exe turns
                lawn.setNumExeTurns(turns + 1);

                // check if the simulation should be stop or not and display the information for each action
                if (lawn.checkToStop() == 1){
                    lawn.displayActionAndResponses();
                    return;
                }else{
                    lawn.displayActionAndResponses();

                }

                // after each action, update the partial knowledge map for the mower
                lawnMower.updatePartialMap();

                // REMEMBER to delete or comment out the rendering before submission
//                lawn.renderLawn();

            }
        }
    }

}
