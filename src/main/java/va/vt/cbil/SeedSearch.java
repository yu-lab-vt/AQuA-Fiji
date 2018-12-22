package va.vt.cbil;

import java.util.ArrayList;
import java.util.HashMap;

import ij.ImagePlus;
import ij.ImageStack;

public class SeedSearch {
	public static ArrayList<int[]> searchSeed(ImagePlus imgPlus, HashMap<Integer, ArrayList<int[]>> map) {
		int width = imgPlus.getWidth();
		int height = imgPlus.getHeight();
		int pages = imgPlus.getImageStackSize();
		int gaph = 3;
		int gapt = 3;
		
		ArrayList<int[]> seeds = new ArrayList<>();
		for(int n=1;n<=map.size();n++) {
			ArrayList<int[]> list = map.get(n);
			
			int minW = width;
			int minH = height;
			int minT = pages;
			int maxW = 0;
			int maxH = 0;
			int maxT = 0;
			
			
			for(int[] p:list) {
				minW = Math.min(p[0], minW);
				minH = Math.min(p[1], minH);
				minT = Math.min(p[2], minT);
				maxW = Math.max(p[0], maxW);
				maxH = Math.max(p[1], maxH);
				maxT = Math.max(p[2], maxT);
			}
			
			minW = Math.max(0, minW - gaph);
			minH = Math.max(0, minH - gaph);
			minT = Math.max(0, minT - gapt);
			maxW = Math.min(width-1, maxW + gaph);
			maxH = Math.min(height-1, maxH + gaph);
			maxT = Math.min(pages-1, maxT + gapt);
			
			int regionWidth = maxW-minW+1;
			int regionHeight = maxH-minH+1;
			int regionPages = maxT-minT+1;
			
			ImageStack stk = imgPlus.getImageStack().crop(minW, minH, minT, regionWidth, regionHeight, regionPages);			
			ImageStack newStk = MinimaAndMaxima3D.regionalMaxima(stk, 26);
			
			for(int[] point:list) {
				int x = point[0];
				int y = point[1];
				int z = point[2];
				if(z==minT||z==maxT)
					continue;
				if(newStk.getVoxel(x-minW, y-minH, z-minT)==255) {
					seeds.add(new int[] {x,y,z});
				}
			}
		}
		
		return seeds;
	}
}
