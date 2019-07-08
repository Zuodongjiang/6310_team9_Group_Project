import java.util.HashMap;
public class Move extends Action {
    private Action.Type type = Action.Type.move;
    private int steps;
    private MowerStatus.Direction direction;
    private final static HashMap<MowerStatus.Direction, Integer> directionToX = new HashMap<MowerStatus.Direction, Integer>();
    private final static HashMap<MowerStatus.Direction, Integer> directionToY = new HashMap<MowerStatus.Direction, Integer>();

    static {
        directionToX.put(MowerStatus.Direction.north, 0);
        directionToX.put(MowerStatus.Direction.northeast, 1);
        directionToX.put(MowerStatus.Direction.east, 1);
        directionToX.put(MowerStatus.Direction.southeast, 1);
        directionToX.put(MowerStatus.Direction.south, 0);
        directionToX.put(MowerStatus.Direction.southwest, -1);
        directionToX.put(MowerStatus.Direction.west, -1);
        directionToX.put(MowerStatus.Direction.northwest, -1);

        directionToY.put(MowerStatus.Direction.north, 1);
        directionToY.put(MowerStatus.Direction.northeast, 1);
        directionToY.put(MowerStatus.Direction.east, 0);
        directionToY.put(MowerStatus.Direction.southeast, -1);
        directionToY.put(MowerStatus.Direction.south, -1);
        directionToY.put(MowerStatus.Direction.southwest, -1);
        directionToY.put(MowerStatus.Direction.west, 0);
        directionToY.put(MowerStatus.Direction.northwest, 1);
    }
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public MowerStatus.Direction getDirection() {
        return direction;
    }

    public void setDirection(MowerStatus.Direction direction) {
        this.direction = direction;
    }

    public int directionToX(MowerStatus.Direction direction) {
        return directionToX.get(direction);
    }
    public int directionToY(MowerStatus.Direction direction) {
        return directionToY.get(direction);
    }
}