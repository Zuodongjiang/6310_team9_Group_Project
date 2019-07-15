package com.example.mainpanel.osmowsis_source;

public class LawnMap {
    private Integer lawnWidth;
    private Integer lawnHeight;
    private String[][] lawnStatus;

    // dummy LawnMap just for API test
    public LawnMap(Integer lawnWidth, Integer lawnHeight){
        this.lawnWidth = lawnHeight;
        this.lawnHeight = lawnHeight;
		this.lawnStatus = new String[lawnWidth][lawnHeight];
		//fill the map with grass
		for (int i = 0; i < lawnWidth; i++) {
            for (int j = 0; j < lawnHeight; j++) {
                lawnStatus[i][j] = "grass";
            }
        }
        // dummy crater location
        lawnStatus[3][2] = "crater";
        // dummy mower location
        lawnStatus[1][2] = "mower";
    }

    public Integer getLawnWidth() {
        return lawnWidth;
    }

    public void setLawnWidth(Integer lawnWidth) {
        this.lawnWidth = lawnWidth;
    }

    public Integer getLawnHeight() {
        return lawnHeight;
    }

    public void setLawnHeight(Integer lawnHeight) {
        this.lawnHeight = lawnHeight;
    }

    public String[][] getLawnStatus() {
        return lawnStatus;
    }

    public void setLawnStatus(String[][] lawnStatus) {
        this.lawnStatus = lawnStatus;
    }
}