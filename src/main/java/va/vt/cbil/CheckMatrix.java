package va.vt.cbil;

import java.util.Random;

public class CheckMatrix {
	public static void show(float[][] input) {
		int W = input.length;
		int H = input[0].length;
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				System.out.print(input[x][y]+ " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static void show(int[][] input) {
		int W = input.length;
		int H = input[0].length;
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				System.out.print(input[x][y]+ " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	public static void show(boolean[][] input) {
		int W = input.length;
		int H = input[0].length;
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				int value = input[x][y]?1:0;
				System.out.print(value+ " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static void show(float[] input) {
		int W = input.length;
		for(int x=0;x<W;x++) {
				System.out.print(input[x]+ " ");
		}
		System.out.println();
	}
	
	public static void show(float[][][] input) {
		int W = input.length;
		int H = input[0].length;
		int T = input[0][0].length;
		for(int t=0;t<T;t++) {
			for(int x=0;x<W;x++) {
				for(int y=0;y<H;y++) {
					System.out.print(input[x][y][t]+ " ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	public static void show(boolean[][][] input) {
		int W = input.length;
		int H = input[0].length;
		int T = input[0][0].length;
		for(int t=0;t<T;t++) {
			for(int x=0;x<W;x++) {
				for(int y=0;y<H;y++) {
					int value = input[x][y][t]? 1:0;
					System.out.print(value+ " ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	public static void show(int[][][] input) {
		int W = input.length;
		int H = input[0].length;
		int T = input[0][0].length;
		for(int t=0;t<T;t++) {
			for(int x=0;x<W;x++) {
				for(int y=0;y<H;y++) {
					System.out.print(input[x][y][t]+ " ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	public static void show(float[][] input, int W, int H) {
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				System.out.print(input[x][y]+ " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static void show(float[][] input, int W, int H, int x0, int y0) {
		int x1 = x0 - W/2;
		int x2 = x0 + W/2;
		int y1 = y0 - H/2;
		int y2 = y0 + H/2;
		for(int x=x1;x<=x2;x++) {
			for(int y=y1;y<=y2;y++) {
				System.out.print(input[x][y]+ " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static void show(float[][][] input, int W, int H, int T) {
		for(int t=0;t<T;t++) {
			for(int x=0;x<W;x++) {
				for(int y=0;y<H;y++) {
					System.out.print(input[x][y][t]+ " ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	public static float[][] randomMatrix(int W,int H){
		float[][] input = new float[W][H];
		Random rv = new Random();
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				input[x][y] = rv.nextFloat();
			}
		}
		return input;
	}
	
	public static float[][][] randomMatrix(int W,int H, int T){
		float[][][] input = new float[W][H][T];
		Random rv = new Random();
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				for(int t=0;t<T;t++) {
					input[x][y][t] = rv.nextFloat();
				}
			}
		}
		return input;
	}
}
