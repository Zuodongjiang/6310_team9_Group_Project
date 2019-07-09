package OsMowSis;

public class Main {

    public static void main(String[] args) {
        SimulationRun monitorSim = new SimulationRun();

        // check for the test scenario file name
        if (args.length == 0) {
            System.out.println("ERROR: Test scenario file name not found.");
        } else {
            if(monitorSim.readFile(args[0])){
            	// run the simulation 
                monitorSim.act();
            } else {
            	System.out.println("No mower on the lawn!");
            }
        }
    }	
	
}
