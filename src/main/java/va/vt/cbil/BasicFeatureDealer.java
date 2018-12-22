package va.vt.cbil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class BasicFeatureDealer {

	public static float calculatePerimeter(int[][] data) {
		HashMap<Integer, ArrayList<int[]>> map = ConnectedComponents.twoPassConnect2D(data);
		float sumPerimeter = 0;
		for(Entry<Integer,ArrayList<int[]>> entry:map.entrySet()) {
			sumPerimeter += calculatePerimeter(entry.getValue());
		}
		return sumPerimeter;
	}
	
	public static float calculatePerimeter(ArrayList<int[]> pix) {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		for(int[] p:pix) {
			minX = Math.min(p[0], minX);
			minY = Math.min(p[1], minY);
			maxX = Math.max(p[0], maxX);
			maxY = Math.max(p[1], maxY);
		}
		
		boolean[][] image = new boolean[maxX-minX+1][maxY-minY+1];
		for(int[] p:pix) {
			image[p[0]-minX][p[1]-minY] = true;
		}
		
		ArrayList<int[]> boundary = new ArrayList<>();
		int x = 0;
		int y = 0;
		while(!image[x][y]) {
			x++;
		}
		
		findBoundary(image,x,y,boundary,1);
		
		if(boundary.size()<=2)
			return 0;
		
		int[][] delta = new int[boundary.size()-1][2];
		for(int i=1;i<boundary.size();i++) {
			delta[i-1][0] = boundary.get(i)[0] - boundary.get(i-1)[0];
			delta[i-1][1] = boundary.get(i)[1] - boundary.get(i-1)[1];
			delta[i-1][0] = delta[i-1][0]*delta[i-1][0];
			delta[i-1][1] = delta[i-1][1]*delta[i-1][1];
		}
		int isCorner = 0;
		int isEven = 0;
		
		for(int i=0;i<boundary.size()-1;i++) {
			if(i!= boundary.size()-2) {
				if(delta[i+1][0]-delta[i][0]!=0 || delta[i+1][1]-delta[i][1]!=0)
					isCorner ++;
			}else {
				if(delta[0][0] - delta[i][0]!=0 || delta[0][1] - delta[i][1]!=0)
					isCorner ++;
			}
			
			if(delta[i][0]==0||delta[i][1]==0)
				isEven ++;
		}
		
//		printBoundary(boundary);
		
		float perimeter = isEven * 0.98f + (delta.length-isEven)*1.406f - isCorner*0.091f;
		return perimeter;
	}

	public static void findBoundary(boolean[][] image, int x, int y, ArrayList<int[]> boundary,int dir) {
		int W = image.length;
		int H = image[0].length;
		
		int[] dw = new int[] {-1,0,1,1,1,0,-1,-1};		// clockwise
		int[] dh = new int[] {1,1,1,0,-1,-1,-1,0};
//

		while(true) {
//			System.out.println(x + " " + y);
			if(boundary.size()!=0&&boundary.get(0)[0]==x&&boundary.get(0)[1]==y) {
				boundary.add(new int[] {x,y});
				break;
			}
			boundary.add(new int[] {x,y});
			int start = dir>=2?dir-2:dir+6;
			boolean find = false;
			for(int k = start;k<8;k++) {
				int px = x + dw[k];
				int py = y + dh[k];
				if(px>=0 && px<W && py>=0 && py<H && image[px][py]) {
					x = px;
					y = py;
					dir = k;
					find = true;
					break;
				}
			}
			if(!find) {
				for(int k = 0;k<start;k++) {
					int px = x + dw[k];
					int py = y + dh[k];
					if(px>=0 && px<W && py>=0 && py<H && image[px][py]) {
						x = px;
						y = py;
						dir = k;
						break;
					}
				}
			}
		}
		
	}
	
	
	public static  ArrayList<int[]> findBoundary(boolean[][] orgImage){
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		int W = orgImage.length;
		int H = orgImage[0].length;
		
		
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				if(orgImage[x][y]) {
					minX = Math.min(x, minX);
					minY = Math.min(y, minY);
					maxX = Math.max(x, maxX);
					maxY = Math.max(y, maxY);
				}
			}
		}
		
		boolean[][] image = new boolean[maxX-minX+1][maxY-minY+1];
		for(int x=minX;x<=maxX;x++) {
			for(int y=minY;y<=maxY;y++) {
				image[x-minX][y-minY] = orgImage[x][y];
			}
		}
		
		ArrayList<int[]> boundary = new ArrayList<>();
		int x = 0;
		int y = 0;
		while(!image[x][y]) {
			x++;
		}
		
		findBoundary(image,x,y,boundary,1);
		for(int[] p:boundary) {
			p[0] = p[0] + minX;
			p[1] = p[1] + minY;
		}
		
		return boundary;
	}
	
	public static  int union_find(int label, ArrayList<Integer> list){
		int i = label;
		while(list.get(i)!=0)
			i = list.get(i);
		return i;
	}
	
	public static void union_connect(int label1, int label2, ArrayList<Integer> list) {
		if(label1==label2)
			return;
		int i = union_find(label1,list);
		int j = union_find(label2,list);
		if(i!=j)
			list.set(j, i);
	}
//	
//	public static void main(String[] args) {
//		int[][] data = new int[5][5];
//		data[0] = new int[] {0,0,0,0,0};
//		data[1] = new int[] {0,0,0,0,1};
//		data[2] = new int[] {0,0,1,0,1};
//		data[3] = new int[] {0,0,1,1,0};
//		data[4] = new int[] {0,0,0,0,0};
//		boolean[][] image = new boolean[5][5];
//		for(int x=0;x<5;x++) {
//			for(int y=0;y<5;y++) {
//				if(data[x][y]>0)
//					image[x][y] = true;
//			}
//		}
//		printBoundary(findBoundary(image));
////		System.out.println(calculatePerimeter(data));
//		
//	}
//	private static void printBoundary(ArrayList<int[]> boundary) {
//		for(int[] p:boundary) {
//			System.out.println(p[0] + " " +  p[1]);
//		}
//	}
	
	public static MulBoundary findMulBoundary(boolean[][] orgImage) {
		int W = orgImage.length;
		int H = orgImage[0].length;
		
		
		
		HashMap<Integer, ArrayList<int[]>> cc = ConnectedComponents.twoPassConnect2D(orgImage);
		HashMap<Integer, ArrayList<int[]>> boundaries = new HashMap<>();
		for(Entry<Integer,ArrayList<int[]>> entry:cc.entrySet()) {
			int label = entry.getKey();
			ArrayList<int[]> points = entry.getValue();
			boolean[][] image = new boolean[W][H];
			for(int[] p:points) {
				image[p[0]][p[1]] = true;
			}
			
			ArrayList<int[]> boundary = findBoundary(image);
			boundaries.put(label, boundary);
		}
		
		return new MulBoundary(cc, boundaries);
	}
	

	

	
	
	
}

class MulBoundary{
	HashMap<Integer, ArrayList<int[]>> cc = null;
	HashMap<Integer, ArrayList<int[]>> boundaries = null;
	public MulBoundary(HashMap<Integer, ArrayList<int[]>> cc, HashMap<Integer, ArrayList<int[]>> boundaries) {
		this.cc = cc;
		this.boundaries = boundaries;
	}
} 