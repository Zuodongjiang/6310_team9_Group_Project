public class Lawn {
    public enum SquareStatus {
        mower, empty, grass, crater, fence
    }
    private SquareStatus[][] status;
    private int initialGrassCount;
    private int height;
    private int width;

    public Lawn(SquareStatus[][] status, ScenarioSetting setting) {
        this.status = status;
        initialGrassCount = countStatus(status, SquareStatus.grass) + setting.getLawnMowerCount();
        width = status.length;
        height = status[0].length;
    }

    private int countStatus(SquareStatus[][] status, SquareStatus target) {
        int count = 0;
        for(int i = 0; i < status.length; i ++) {
            for(int j = 0; j < status[i].length; j++) {
                if (status[i][j] == target) {
                    count ++;
                }
            }
        }
        return count;
    }

    public int countGrassCut(SquareStatus[][] status) {
        return initialGrassCount - countStatus(status, SquareStatus.grass);
    }
    public boolean isAllCut() {
        if (initialGrassCount == this.countGrassCut(status)) {
            return true;
        } else {
            return false;
        }
    }

    public SquareStatus[][] getStatus() {
        return status;
    }

    public void setStatus(SquareStatus[][] status) {
        this.status = status;
    }

    public int getInitialGrassCount() {
        return initialGrassCount;
    }

    public SquareStatus getSquareStatus(int i, int j) {
        if (i < 0 || j < 0 || i >= width || j >= height) {
            return SquareStatus.fence;
        }
        return status[i][j];
    }

    public SquareStatus setSquareStatus(int i, int j, Lawn.SquareStatus squareStatus) {

        return status[i][j] = squareStatus;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}