package Model;

import Viewer.*;

import java.io.File;
import java.util.Scanner;


public class InputFile {
    private int lawnWidth;
    private int lawnHeight;
    private int stallTurn;
    private Location[] mowerLocations;
    private Direction[] mowerInitialDirections;
    private Location[] craterLocations;
    private Location[] puppyLocations;
    private double stayPercent;
    private int totalTurn;

    public void loadSetting(String testFileName) {
        final String DELIMITER = ",";

        try {
            Scanner takeCommand = new Scanner(new File(testFileName));
            String[] tokens;
            int k;

            // read in the lawn information
            tokens = takeCommand.nextLine().split(DELIMITER);
            lawnWidth = Integer.parseInt(tokens[0]);
            tokens = takeCommand.nextLine().split(DELIMITER);
            lawnHeight = Integer.parseInt(tokens[0]);

            // read in the lawnmower starting information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numMowers = Integer.parseInt(tokens[0]);
            mowerLocations = new Location[numMowers];
            mowerInitialDirections = new Direction[numMowers];
            tokens = takeCommand.nextLine().split(DELIMITER);
            stallTurn = Integer.parseInt(tokens[0]);

            for (k = 0; k < numMowers; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                int mowerX = Integer.parseInt(tokens[0]);
                int mowerY = Integer.parseInt(tokens[1]);
                mowerLocations[k] = new Location(mowerX, mowerY);
                mowerInitialDirections[k] = Direction.valueOf(tokens[2]);
            }

            // read in the crater information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numCraters = Integer.parseInt(tokens[0]);
            craterLocations = new Location[numCraters];
            for (k = 0; k < numCraters; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                int craterX = Integer.parseInt(tokens[0]);
                int craterY = Integer.parseInt(tokens[1]);
                craterLocations[k] = new Location(craterX, craterY);
            }

            // read in the puppy information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numPuppy = Integer.parseInt(tokens[0]);
            puppyLocations = new Location[numPuppy];
            tokens = takeCommand.nextLine().split(DELIMITER);
            stayPercent = Double.parseDouble(tokens[0]) / 100.0;

            for (k = 0; k < numPuppy; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                int craterX = Integer.parseInt(tokens[0]);
                int craterY = Integer.parseInt(tokens[1]);
                puppyLocations[k] = new Location(craterX, craterY);
            }

            tokens = takeCommand.nextLine().split(DELIMITER);
            totalTurn = Integer.parseInt(tokens[0]);

            takeCommand.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
    }

    public int getLawnWidth() {
        return lawnWidth;
    }

    public int getLawnHeight() {
        return lawnHeight;
    }

    public Location[] getMowerLocations() {
        return mowerLocations;
    }

    public int getStallTurn() { return stallTurn; }

    public Direction[] getMowerInitialDirections() {
        return mowerInitialDirections;
    }

    public Location[] getCraterLocations() {
        return craterLocations;
    }

    public Location[] getPuppyLocations() { return puppyLocations; }

    public double getStayPercent() { return stayPercent; }

    public int getTotalTurn() { return totalTurn; }

    public Location[] getMowerLocationsCopy(){
        return mowerLocations.clone();
    }

    public Direction[] getMowerDirectionsCopy(){
        return mowerInitialDirections.clone();
    }
}

