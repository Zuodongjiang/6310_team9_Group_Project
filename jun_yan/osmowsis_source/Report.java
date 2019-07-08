public class Report {
    private int totalSquareCount;
    private int initalGrassCount;
    private int cutGrassCount;
    private int turnCount;

    public int getTotalSquareCount() {
        return totalSquareCount;
    }

    public void setTotalSquareCount(int lawnWidth, int lawnHeight) {
        this.totalSquareCount = lawnWidth * lawnHeight ;
    }

    public int getInitalGrassCount() {
        return initalGrassCount;
    }

    public void setInitalGrassCount(int initalGrassCount) {
        this.initalGrassCount = initalGrassCount;
    }

    public int getCutGrassCount() {
        return cutGrassCount;
    }

    public void setCutGrassCount(int cutGrassCount) {
        this.cutGrassCount = cutGrassCount;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(int turnCount) {
		this.turnCount = turnCount;
	}
}