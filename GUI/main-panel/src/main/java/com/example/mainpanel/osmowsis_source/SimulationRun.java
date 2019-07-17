package com.example.mainpanel.osmowsis_source;

import java.util.ArrayList;
import java.util.List;


public class SimulationRun {

	// dummy simulationRun for API test

	// TODO: get lawn map
	public InfoMap getLawnMap() {
		// The lawnMap includes these three atributes below:
		// 	Integer lawnWidth;
		//  Integer lawnHeight;
		//  String[][] lawnStatus;
		InfoMap lawnMap = new InfoMap(6, 8);
		return lawnMap;
	}

	// TODO: generate report
	public Report generateReport() {
		Report report = new Report();
        report.setInitalGrassCount(35);
        report.setCutGrassCount(1);
        report.setGrassRemaining(34);
        report.setTurnCount(0);
        return report;
    }

	// TODO: get mower states
    public List<MowerStates> getMowerStates() {
		List<MowerStates> mowerList = new ArrayList<MowerStates>();
		for (int i = 0; i < 10; i ++) {
			MowerStates mowerStates = new MowerStates();
			mowerStates.setMower_id(i + 1);
			mowerStates.setMowerStatus("N/A"); // set "N/A" for initilized map
			mowerStates.setEnergyLevel(30);
			mowerStates.setStallTurn(0);
			mowerList.add(mowerStates);
		}
		return mowerList;
	}

	// TODO: move next mower, return the mower ID
	public int moveNext() {
		return 1;
		// if already the end
		// return -1
	}


	// TODO: stop run
	public void stopRun() {

	}

	// TODO: move fast-forward
	public void act() {

	}


}
