public class Main {

    public static void main(String[] args) {
        SimDriver monitorSim = new SimDriver();

        // check for the test scenario file name
        if (args.length == 0) {
            System.out.println("ERROR: Test scenario file name not found.");
        } else {
            int numturns = monitorSim.uploadStartingFile(args[0]);
           // System.out.println(numturns);
            // run the simulation for a fixed number of steps
            for(int turns = 0; turns < numturns; turns++) {
            	 monitorSim.pollMowerForAction();
                monitorSim.validateMowerAction();
                boolean cutall =monitorSim.displayActionAndResponses();
                
                // REMEMBER to delete or comment out the rendering before submission
            //   monitorSim.renderLawn();
               if (cutall)break;
            }
            monitorSim.finalresult();
        }
    }

}
