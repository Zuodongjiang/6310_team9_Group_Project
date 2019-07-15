package com.example.mainpanel.osmowsis_source;

import java.util.ArrayList;
import java.util.List;


public class SimulationRun {

	// dummy simulationRun for API test

	// TODO: get lawn map
	public LawnMap getLawnMap() {
		LawnMap lawnMap = new LawnMap(6, 6);
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
			mowerStates.setMowerStatus("N/A");
			mowerStates.setEnergyLevel(30);
			mowerStates.setStallTurn(0);
			mowerList.add(mowerStates);
		}
		return mowerList;
	}

	// TODO: move next
	public LawnMap moveNext() {
		LawnMap lawnMap = new LawnMap(6, 6);
		String[][] lawnStatus = lawnMap.getLawnStatus();
		lawnStatus[1][3] = "mower";
		lawnStatus[1][2] = "empty";
		return lawnMap;
	}

	// TODO: move fast-forward
	public void act() {

	}
}
