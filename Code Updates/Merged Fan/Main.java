package team_9;



public class Main {

    public static void main(String[] args) {
        SimDriver monitorSim = new SimDriver();

        // check for the test scenario file name
        if (args.length == 0) {
            System.out.println("ERROR: Test scenario file not found.");
        } else {
            monitorSim.uploadStartingFile(args[0]);
        }
    }	
	
}

