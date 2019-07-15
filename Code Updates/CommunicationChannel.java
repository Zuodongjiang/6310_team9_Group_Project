package team_9;

import java.util.HashSet;

public class CommunicationChannel {
	private static InfoMap[] mowerMaps;
	private static int[][] mowerRelativeLocation;
	
	public CommunicationChannel(int numMowers){
		//all the mowerMaps and mowerRelativeLocation are the same at beginning
		mowerMaps = new InfoMap[numMowers];
		mowerRelativeLocation = new int[numMowers][2];
		int mapHeight = 2 * 10 + 1; //lawn height is 1~10 inclusive
		int mapWidth = 2 * 15 + 1; //lawn width is 1~15 inclusive
		for (int i=0; i<numMowers; i++){
			mowerMaps[i] = new InfoMap(mapWidth, mapHeight, 0, null, 0, null, false);
			mowerRelativeLocation[i][0] = 15;
			mowerRelativeLocation[i][1] = 10;
		}
	}
	
	
	public void combineMaps(int mowerID_1, int mowerID_2){
		
	}
	
	public InfoMap getMap(int mowerID){
		return mowerMaps[mowerID];
	}
	
	public void updateMowerLocation(int mowerID, int mowerX, int mowerY){
		mowerRelativeLocation[mowerID][0] = mowerX;
		mowerRelativeLocation[mowerID][1] = mowerY;
	}
	
	
	public void shareMaps(int mowerID, HashSet<Integer> discovered_mowers){
		InfoMap shared_map = mowerMaps[mowerID];
		for (int i:discovered_mowers){
			mowerMaps[i] = shared_map;
		}
	}

}
