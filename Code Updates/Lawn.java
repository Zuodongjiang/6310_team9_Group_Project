package team_9;


public class Lawn {

	public InfoMap lawnMap;


	public Lawn(int lawnWidth, int lawnHeight, int mowerNo, int[][] mowerPosition, int craterNo, int[][] craterLocation){

		lawnMap = new InfoMap(lawnWidth, lawnHeight, mowerNo, mowerPosition, craterNo, craterLocation, true);

	}

	//transfer scanned info to mower
	public int[] scanedByMower(int mowerX, int mowerY){
		return lawnMap.scan(mowerX, mowerY);
	}


}


