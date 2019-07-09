package OsMowSis;

import java.util.Scanner;
import java.io.*;

public class SimulationRun { 
	
	private Lawn lawn;
	private Integer maxTurns;
	private Integer currentTurn;  //nth turn
	
	
	public SimulationRun(){
		lawn = null;
		maxTurns = 0;
		currentTurn = 0;
	}
	
	//use readFile function to do setup instead of an extra setup function	
	public Boolean readFile(String testFileName){
		Integer lawnWidth = 0;
		Integer lawnHeight = 0;
		Integer numMowers = 0;
		String [][] mowerLD;
		Integer numCraters = 0;
		Integer[][] craterLocation;
		
        final String DELIMITER = ",";

        try {
            Scanner takeCommand = new Scanner(new File(testFileName));
            String[] tokens;
            int i, k;

            // read in the lawn information
            tokens = takeCommand.nextLine().split(DELIMITER);
            lawnWidth = Integer.parseInt(tokens[0]);
            tokens = takeCommand.nextLine().split(DELIMITER);
            lawnHeight = Integer.parseInt(tokens[0]);


            // read in the lawnmower starting information
            tokens = takeCommand.nextLine().split(DELIMITER);
            numMowers = Integer.parseInt(tokens[0]);
            if (numMowers.equals(0)){
            	takeCommand.close();
            	return false;
            }
            
            mowerLD = new String [numMowers][3];// mower's x,y and direction
            for (i = 0; i < numMowers; i++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                mowerLD[i] = tokens;         
 
            }

            // read in the crater information 
            tokens = takeCommand.nextLine().split(DELIMITER);
            numCraters = Integer.parseInt(tokens[0]);
            craterLocation = new Integer [numCraters][2];
            if (numCraters.equals(0)){ //there might be no crater
            	craterLocation = null;
            } else {	            
	            for (k = 0; k < numCraters; k++) {
	                tokens = takeCommand.nextLine().split(DELIMITER);
	                Integer craterX = Integer.parseInt(tokens[0]);
	                Integer craterY = Integer.parseInt(tokens[1]);
	                craterLocation[k][0] = craterX;
	                craterLocation[k][1] = craterY;
	            }
            }
            // read in max turns information
            tokens = takeCommand.nextLine().split(DELIMITER);
            this.maxTurns = Integer.parseInt(tokens[0]);
            
            // create Lawn instance
            this.lawn = new Lawn(lawnWidth, lawnHeight, numMowers, mowerLD, numCraters, craterLocation);
    
            takeCommand.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
        return true;
	}
	
	public void act(){
		while (!this.checkHalt()){
			//one round
			this.currentTurn += 1;
			this.lawn.actOneRound();			
		}
		this.halt();			
	}
	
	//halt the simulation run and print report
	public void halt(){
		System.out.print(this.lawn.getTotalNumberSquares());
		System.out.print(",");
		//print report
		System.out.print(this.lawn.getOriginalGrassNumber());
		System.out.print(",");
		System.out.print(this.lawn.getGrassNumberCut());
		System.out.print(",");
		System.out.print(this.currentTurn);
		System.out.print("\n");
	
	}

	//added a function to check if it should halt
	public Boolean checkHalt(){
		int mowerLeft = this.lawn.checkMowerLeft();
		//halt cases - (1) all mowers crashed; (2) no more turns left; (3) all grass cut. 
		if (mowerLeft==0 || this.maxTurns.equals(this.currentTurn) || this.lawn.checkFinish()){
			return true;
		}
		return false;
	}
	
}
