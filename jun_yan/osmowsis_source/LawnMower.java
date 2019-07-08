import java.util.HashMap;
import java.util.Random;
public class LawnMower {
    private MowerStatus.Direction direction;
    private Simulation simulation;
    private Action.Type nextAction;
    private HashMap<Coordinate, Lawn.SquareStatus> partialMap = new HashMap<Coordinate, Lawn.SquareStatus>();
    private Coordinate relCoord = new Coordinate(0, 0);

    public LawnMower(MowerStatus.Direction direction, Simulation simulation) {
        this.direction = direction;
        this.simulation = simulation;
        this.nextAction = Action.Type.scan;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public MowerStatus.Direction getDirection() {
        return direction;
    }

    public void setDirection(MowerStatus.Direction direction) {
        this.direction = direction;
    }

    public void chooseAction() {
        //choose move
        if (nextAction == Action.Type.move) {
            Move move = moveSetUp();
            simulation.moveMower(move);
            this.updateRelCoord(move);
            nextAction = Action.Type.scan;
        // choose scan
        } else if (nextAction == Action.Type.scan) {
            Surrounding surrounding = simulation.scanSurrounding();
            this.updatePartialMap(surrounding);
            nextAction = Action.Type.move;
        }
    }
    public Move moveSetUp() {
        Random randGenerator = new Random();
        int moveRandomChoice;
        Move move = new Move();
        // decide distance
        int relCoordX = relCoord.getX();
        int relCoordY = relCoord.getY();
        int steps = 0;
        for (int i = 0; i < 2; i ++) {
            relCoordX = relCoordX + move.directionToX(this.getDirection());
            relCoordY = relCoordY + move.directionToY(this.getDirection());
            Coordinate newCoord = new Coordinate(relCoordX, relCoordY);
            Lawn.SquareStatus status = partialMap.get(newCoord);
            if (status == Lawn.SquareStatus.grass || status == Lawn.SquareStatus.empty) {
                steps ++;
            } else {
                break;
            }
        }
        move.setSteps(steps);
        // decide direction
        MowerStatus.Direction initialDirection = this.getDirection();

        switch (initialDirection) {
            case south:
                move.setDirection(MowerStatus.Direction.southwest);
                break;
            case southwest:
                move.setDirection(MowerStatus.Direction.west);
                break;
            case west:
                move.setDirection(MowerStatus.Direction.northwest);
                break;
            case northwest:
                move.setDirection(MowerStatus.Direction.north);
                break;
            case southeast:
                move.setDirection(MowerStatus.Direction.south);
                break;
            case north:
                move.setDirection(MowerStatus.Direction.northeast);
                break;
            case northeast:
                move.setDirection(MowerStatus.Direction.east);
                break;
            case east:
                move.setDirection(MowerStatus.Direction.southeast);
                break;
            default: break;
        }
        if (steps != 0) {
            moveRandomChoice = randGenerator.nextInt(100);
            if (moveRandomChoice < 80) {
                move.setDirection(initialDirection);
            }
        }
        return move;
    }

    public void updatePartialMap(Surrounding surrounding) {
        // surrounding: array
        // partialMap: hashMap
        int[] neighborX = {0, 1, 1, 1, 0, -1, -1,  -1};
        int[] neighborY = {1, 1, 0, -1, -1, -1, 0,  1};
        int relCoordX = relCoord.getX();
        int relCoordY = relCoord.getY();
        for (int i = 0; i < 8; i++) {
            int ajacentX = relCoordX + neighborX[i];
            int ajacentY = relCoordY + neighborY[i];
            Coordinate ajacentCoord = new Coordinate(ajacentX, ajacentY);
            Lawn.SquareStatus status = surrounding.getSquareStatus(i);
            partialMap.put(ajacentCoord, status);
        }
        Coordinate newCoord = new Coordinate(relCoordX, relCoordY);
        partialMap.put(newCoord, Lawn.SquareStatus.empty);
        //System.out.println(partialMap);
    }

    public void updateRelCoord(Move move) {
        int currentX = relCoord.getX();
        int currentY = relCoord.getY();
        currentX = currentX + move.directionToX(this.getDirection()) * move.getSteps();
        currentY = currentY + move.directionToY(this.getDirection()) * move.getSteps();
        relCoord.setX(currentX);
        relCoord.setY(currentY);
        this.setDirection(move.getDirection());
        // TODO: move path set empty
        // System.out.println(relCoord.getX() + "," + relCoord.getY());
    }
}


