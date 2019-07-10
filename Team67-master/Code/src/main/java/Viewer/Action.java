package Viewer;

public class Action {
    private String name;
    private int stepSize;
    private Direction direction;

    public Action(String name) {
        this.name = name;
    }

    //TODO: set the mower action
    public void setMoveAction(int stepSize, Direction direction) {
        this.stepSize = stepSize;
        this.direction = direction;
    }

    public String getName() {
        return name;
    }

    public int getStepSize() {
        return stepSize;
    }

    public Direction getDirection() {
        return direction;
    }
}
