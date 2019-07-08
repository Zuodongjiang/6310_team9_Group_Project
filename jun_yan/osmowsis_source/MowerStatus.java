class MowerStatus {
    public enum Direction {
        east, south, west, north, southeast, northeast, southwest, northwest
    }
    private Coordinate coord;
    private Direction direction;
    private boolean isCrashed;

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isCrashed() {
        return isCrashed;
    }

    public void setCrashed(boolean isCrashed) {
		this.isCrashed = isCrashed;
    }

    @Override
    public String toString() {
        return "MowerStatus [coord=" + coord + ", direction=" + direction + ", isCrashed=" + isCrashed + "]";
    }
}
