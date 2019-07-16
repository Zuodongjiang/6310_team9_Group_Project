package team_9;

public class Report {
    private int initalGrassCount;
    private int cutGrassCount;
    private int grassRemaining;
    private int turnCount;

    public int getGrassRemaining() {
        return grassRemaining;
    }

    public void setGrassRemaining(int grassRemaining) {
        this.grassRemaining = grassRemaining;
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
