package com.example.mainpanel.osmowsis_source;

public class InfoMap {
    private Integer lawnWidth;
    private Integer lawnHeight;
    private String[][] lawnStatus;

    // dummy LawnMap just for API test
    public InfoMap(Integer lawnWidth, Integer lawnHeight){
        this.lawnWidth = lawnHeight;
        this.lawnHeight = lawnHeight;
		this.lawnStatus = new String[lawnWidth][lawnHeight];
        //fill the map with grass
        String[][] inputStatus = new String[lawnWidth][lawnHeight];
		for (int i = 0; i < lawnWidth; i++) {
            for (int j = 0; j < lawnHeight; j++) {
                inputStatus[i][j] = "grass";
            }
        }
        // dummy crater location
        inputStatus[3][2] = "crater";
        // dummy mower location
        inputStatus[1][2] = "mower";
        // dummy mower charge location
        inputStatus[5][3] = "mower_charge";
        // dummy charge location
        inputStatus[2][4] = "charge";
        // dummy empty location
        inputStatus[3][4] = "empty";

        // rotate map
        for(int i=0; i<inputStatus[0].length; i++){
            for(int j=inputStatus.length-1; j>=0; j--){
                lawnStatus[i][j] = inputStatus[j][i];
            }
        }

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