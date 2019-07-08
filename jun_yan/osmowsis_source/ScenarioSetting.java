import java.util.Arrays;

public class ScenarioSetting {
    private int lawnWidth;
    private int lawnHeight;
    private int lawnMowerCount;
    private MowerStatus mowerStatus;
    private Lawn.SquareStatus[][] lawnStatus;
    private int craterCount;
    private int maxTurnCount;

    public int getLawnWidth() {
        return lawnWidth;
    }

    public void setLawnWidth(int lawnWidth) {
        this.lawnWidth = lawnWidth;
    }



    public int getLawnMowerCount() {
        return lawnMowerCount;
    }

    public void setLawnMowerCount(int lawnMowerCount) {
        this.lawnMowerCount = lawnMowerCount;
    }

    public MowerStatus getMowerStatus() {
        return mowerStatus;
    }

    public void setMowerStatus(MowerStatus mowerStatus) {
        this.mowerStatus = mowerStatus;
    }

    public int getCraterCount() {
        return craterCount;
    }

    public void setCraterCount(int craterCount) {
        this.craterCount = craterCount;
    }

    public int getMaxTurnCount() {
        return maxTurnCount;
    }

    public void setMaxTurnCount(int maxTurnCount) {
        this.maxTurnCount = maxTurnCount;
    }

    public Lawn.SquareStatus[][] getLawnStatus() {
        return lawnStatus;
    }

    public void setLawnStatus(Lawn.SquareStatus[][] lawnStatus) {
        this.lawnStatus = lawnStatus;
    }

    @Override
    public String toString() {
        return "ScenarioSetting [craterCount=" + craterCount + ", lawnHeight=" + lawnHeight + ", lawnMowerCount="
                + lawnMowerCount + ", lawnStatus=" + Arrays.toString(lawnStatus) + ", lawnWidth=" + lawnWidth
                + ", maxTurnCount=" + maxTurnCount + ", mowerStatus=" + mowerStatus + "]";
    }

    public int getLawnHeight() {
        return lawnHeight;
    }

    public void setLawnHeight(int lawnHeight) {
        this.lawnHeight = lawnHeight;
    }
}