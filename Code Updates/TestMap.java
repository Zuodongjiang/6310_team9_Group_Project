package team_9;

public class TestMap {
	public static void main(String[] args) {
		int[] one = {1,2,3,4};
		int[] two = one;
		two[0] = 5;
		for(int i = 0; i < one.length; i++) {
			System.out.print(one[i] + " ");
		}
	}

}
