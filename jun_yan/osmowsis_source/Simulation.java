import java.util.Scanner;
import java.util.stream.Collectors;
import java.io.*;
import java.util.Arrays;
import java.util.Random;


public class Simulation {
    private ScenarioSetting setting;
    private MowerStatus mowerStatus;
    private Lawn lawn;
    private LawnMower mower;
    private int curTurnCount;


    public Simulation(String path) {
        setting = readFile(path);
        mowerStatus = setting.getMowerStatus();
        lawn = new Lawn(setting.getLawnStatus(), setting);
        mower = new LawnMower(mowerStatus.getDirection(), this);
    }

    public ScenarioSetting readFile(String path) {
        final String DELIMITER = ",";
        ScenarioSetting setting = new ScenarioSetting();
        try {
            Scanner takeCommand = new Scanner(new File(path));
            String[] tokens;
            int i, j, k;

            // read in the lawn information
            tokens = takeCommand.nextLine().split(DELIMITER);
            setting.setLawnWidth(Integer.parseInt(tokens[0]));
            tokens = takeCommand.nextLine().split(DELIMITER);
            setting.setLawnHeight(Integer.parseInt(tokens[0]));

            // generate the lawn information
            Lawn.SquareStatus[][] status = new Lawn.SquareStatus[setting.getLawnWidth()][setting.getLawnHeight()];
            for (i = 0; i < setting.getLawnWidth(); i++) {
                for (j = 0; j < setting.getLawnHeight(); j++) {
                    status[i][j] = Lawn.SquareStatus.grass;
                }
            }

            // read in the lawnmower starting information
            tokens = takeCommand.nextLine().split(DELIMITER);
            setting.setLawnMowerCount(Integer.parseInt(tokens[0]));
            for (k = 0; k < setting.getLawnMowerCount(); k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                Coordinate coordinate = new Coordinate(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
                MowerStatus mowerStatus = new MowerStatus();
                mowerStatus.setCoord(coordinate);
                mowerStatus.setDirection(MowerStatus.Direction.valueOf(tokens[2]));
                setting.setMowerStatus(mowerStatus);

                // mow the grass at the initial location
                status[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])] = Lawn.SquareStatus.empty;
            }

            // read in the crater information
            tokens = takeCommand.nextLine().split(DELIMITER);
            setting.setCraterCount(Integer.parseInt(tokens[0]));
            for (k = 0; k < setting.getCraterCount(); k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);

                // place a crater at the given location
                status[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])] = Lawn.SquareStatus.crater;
            }
            setting.setLawnStatus(status);

            // read in the max turn count
            tokens = takeCommand.nextLine().split(DELIMITER);
            setting.setMaxTurnCount(Integer.parseInt(tokens[0]));

            takeCommand.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
        return setting;
    }

    public void printMove(Move move) {
        System.out.println("move" + "," + move.getSteps() + "," + move.getDirection());
    }

    public void printScan() {
        System.out.println("scan");
    }

    public void printSurrounding(Surrounding surrounding) {
        Lawn.SquareStatus[] status = surrounding.getStatus();
        System.out.println(String.join(",", Arrays.asList(status).stream().map(s -> s.toString()).collect(Collectors.toList())));
    }

    public MowerStatus updateLawnAndMowerStatus(Move move, Lawn lawn) {
        if (move.getSteps() != 0) {
            // update mower coordinate
            Coordinate currentCoord = mowerStatus.getCoord();
            int currentX = currentCoord.getX();
            int currentY = currentCoord.getY();
            for (int i = 0; i < move.getSteps(); i ++) {

                currentX = currentX + move.directionToX(mowerStatus.getDirection());
                currentY = currentY + move.directionToY(mowerStatus.getDirection());

                // check new location lawn squarestatus
                Lawn.SquareStatus status = lawn.getSquareStatus(currentX, currentY);
                // if grass or empty, isCrash == false
                // TODO: what if running into another mower
                if (status == Lawn.SquareStatus.grass || status == Lawn.SquareStatus.empty) {
                    mowerStatus.setCrashed(false);
                    Coordinate newCoord = new Coordinate(currentX, currentY);
                    mowerStatus.setCoord(newCoord);
                    lawn.setSquareStatus(currentX, currentY, Lawn.SquareStatus.empty);
                } else {
                    mowerStatus.setCrashed(true);
                    System.out.println(currentX + "," + currentY);
                    return mowerStatus;
                }
            }
        }
        mowerStatus.setDirection(move.getDirection());

        return mowerStatus;
    }

    private void printUpdatedMowerStatus(MowerStatus status) {
        if (status.isCrashed()) {
            System.out.println("crash");
        } else {
            System.out.println("ok");
        }
    }

    protected Surrounding generateSurrounding(Coordinate coord, Lawn lawn) {
        Surrounding surrounding = new Surrounding();
        Lawn.SquareStatus[] surroundingStatus = new Lawn.SquareStatus[8];
        // iterate to get status of each square around coord
        int[] neighborX = {0, 1, 1, 1, 0, -1, -1,  -1};
        int[] neighborY = {1, 1, 0, -1, -1, -1, 0,  1};
        for (int i = 0; i < 8; i++) {
            int ajacentX = coord.getX() + neighborX[i];
            int ajacentY = coord.getY() + neighborY[i];
            surroundingStatus[i] = lawn.getSquareStatus(ajacentX, ajacentY);
        }
        surrounding.setStatus(surroundingStatus);

        return surrounding;
    }

    public void moveMower(Move move) {
        // print move
        printMove(move);
        // update lawn and mower status
        MowerStatus updatedStatus = updateLawnAndMowerStatus(move, lawn);
        // print updated mower status
        printUpdatedMowerStatus(updatedStatus);
    }

    public Surrounding scanSurrounding() {
        // print scan
        printScan();
        // identify mower coord
        Coordinate coord = mowerStatus.getCoord();
        // generate surrounding from coord and lawn
        Surrounding surrounding = generateSurrounding(coord, lawn);
        // print surrounding
        printSurrounding(surrounding);

        return surrounding;
    }

    public Lawn getLawn() {
        return lawn;
    }

    public Report generateReport() {
        // set up report
        Report finalReport = new Report();
        finalReport.setTotalSquareCount(setting.getLawnWidth(), setting.getLawnHeight());
        finalReport.setInitalGrassCount(lawn.getInitialGrassCount());
        finalReport.setCutGrassCount(lawn.countGrassCut(lawn.getStatus()));
        finalReport.setTurnCount(curTurnCount);
        // print out report
        System.out.println(finalReport.getTotalSquareCount() + "," + finalReport.getInitalGrassCount() + "," + finalReport.getCutGrassCount() + "," + finalReport.getTurnCount());
        return finalReport;
    }

    public boolean shouldRun() {
        if (curTurnCount >= setting.getMaxTurnCount() || lawn.isAllCut() || mowerStatus.isCrashed()) {
            return false;
        } else {
            return true;
        }
    }

    public void controlRunLoop() {
        for (int i = 0; i < setting.getMaxTurnCount(); i++) {
            if (this.shouldRun()) {
                mower.chooseAction();
                curTurnCount = curTurnCount + 1;
            } else {
                break;
            }
        }
        this.generateReport();
    }

    public LawnMower getMower() {
        return mower;
    }
}