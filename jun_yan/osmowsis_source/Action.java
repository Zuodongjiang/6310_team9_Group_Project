public class Action {
    public static enum Type {
        move, scan
    }
    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}