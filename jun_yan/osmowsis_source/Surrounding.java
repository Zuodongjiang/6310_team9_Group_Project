public class Surrounding {
    Lawn.SquareStatus[] status = new Lawn.SquareStatus[8];

    public Lawn.SquareStatus[] getStatus() {
        return status;
    }

    public void setStatus(Lawn.SquareStatus[] status) {
        this.status = status;
    }

    public Lawn.SquareStatus getSquareStatus(int i) {
        return status[i];
    }

}