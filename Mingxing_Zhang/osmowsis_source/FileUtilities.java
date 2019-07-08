// This class is in charge of read the CSV file and format the required data.

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class FileUtilities {

    private Integer lawnHeight;
    private Integer lawnWidth;
    private Integer[][] lawnInfo;
    private Integer mowerX, mowerY;
    private String mowerDirection;
    private Integer numTurns;
    private Integer numCraters;
    private Integer[][] craterInfo;
    private Integer numGrass;

    private final int EMPTY_CODE = 0;
    private final int GRASS_CODE = 1;
    private final int CRATER_CODE = 2;

    public void uploadStartingFile(String testFileName) {
        final String DELIMITER = ",";

        try {
            Scanner takeCommand = new Scanner(new File(testFileName));
            String[] tokens;
            int i, j, k;

            // read in the lawn information
            tokens = takeCommand.nextLine().split(DELIMITER);
            lawnWidth = Integer.parseInt(tokens[0]);
            tokens = takeCommand.nextLine().split(DELIMITER);
            lawnHeight = Integer.parseInt(tokens[0]);

            // generate the lawn information
            lawnInfo = new Integer[lawnHeight][lawnWidth];
            for (i = 0; i < lawnHeight; i++) {
                for (j = 0; j < lawnWidth; j++) {
                    lawnInfo[i][j] = GRASS_CODE;
                }
            }

            // read in the lawnmower starting information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numMowers = Integer.parseInt(tokens[0]);
            for (k = 0; k < numMowers; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                mowerX = Integer.parseInt(tokens[0]);
//                mowerX = lawnWidth - mowerX -1;
                mowerY = Integer.parseInt(tokens[1]);
                mowerY = lawnHeight - mowerY - 1;

                mowerDirection = tokens[2];

                // mow the grass at the initial location
                lawnInfo[mowerY][mowerX] = EMPTY_CODE;
            }

            // read in the crater information
            tokens = takeCommand.nextLine().split(DELIMITER);
            numCraters = Integer.parseInt(tokens[0]);
            Integer[][] craterLocations = new Integer[numCraters][2];
            for (k = 0; k < numCraters; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);

                // place a crater at the given location
                lawnInfo[lawnHeight - 1 - Integer.parseInt(tokens[1])][Integer.parseInt(tokens[0])] = CRATER_CODE;
                craterLocations[k][0] = lawnHeight - 1 - Integer.parseInt(tokens[1]);
                craterLocations[k][1] = Integer.parseInt(tokens[0]);
            }

            // save crater locations
            craterInfo = craterLocations;
            numGrass = lawnWidth * lawnHeight - numCraters;
            // read in total number of turns
            tokens = takeCommand.nextLine().split(DELIMITER);
            numTurns = Integer.parseInt(tokens[0]);

            takeCommand.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
    }

    public Integer getLawnHeight() {
        return lawnHeight;
    }

    public Integer getLawnWidth() {
        return lawnWidth;
    }

    public Integer getMowerX() {
        return mowerX;
    }

    public Integer getMowerY() {
        return mowerY;
    }

    public String getMowerDirection() {
        return mowerDirection;
    }

    public Integer[][] getLawnInfo() {
        return lawnInfo;
    }

    public Integer getNumTurns() {
        return numTurns;
    }

    public Integer getNumCraters() {
        return numCraters;
    }

    public Integer getNumGrass() {
        return numGrass;
    }

    public Integer[][] getCraterInfo() {
        return craterInfo;
    }
}
