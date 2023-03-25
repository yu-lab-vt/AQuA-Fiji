package va.vt.cbil;

import java.util.ArrayList;
import java.util.HashMap;

import ij.ImagePlus;
import ij.ImageStack;

public class SeedSearch2 {
	public static ArrayList<int[]> searchSeed(float[][][] img, HashMap<Integer, ArrayList<int[]>> map) {
//		int width = imgPlus.getWidth();
//		int height = imgPlus.getHeight();
//		int pages = imgPlus.getImageStackSize();
//		System.out.printf("p, w, h = %d, %d, %d\n" , pages, width, height);
//		int gaph = 3;
//		int gapt = 3;
		
		ArrayList<int[]> seeds = new ArrayList<>();
		for(int n=1;n<=map.size();n++) {
			ArrayList<int[]> list = map.get(n);
			  
	        for (int[] p: list) {
	          int x = p[0]; 
	          int y = p[1]; 
	          int z = p[2];
	          if (isMax(x, y, z, img)) {
	            seeds.add(new int[] {x,y,z});
	          }
	        }
		}
		
		return seeds;
	}
	
	private static boolean isMax(int x, int y, int z, float[][][] img) {	
	  if (z==0 | z==img[0][0].length-1) return false;
      float v = img[x][y][z];      
      // go through 3*3*3
      for (int i=x-1; i<=x+1; i++) {
        // check if border
        if (i==-1 | i>=img.length) continue;
        for (int j=y-1; j<=y+1; j++) { 
          // check if border
          if (j==-1 | j>=img[0].length) continue;
          for (int k=z-1; k<=z+1; k++) { 
            // check if border
            if (k==-1 | k>=img[0][0].length) continue;
            if (i==x & j==y & k==z) continue;
            if (img[i][j][k] > v) return false;  
          }
        }
      }
	  return true;
	}
}
