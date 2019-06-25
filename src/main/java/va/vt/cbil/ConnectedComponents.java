package va.vt.cbil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ConnectedComponents {
	
	public static void twoPassConnect2DRemoveSmallArea(boolean[][][] dActVoxDi, int minSize) {	
		int width = dActVoxDi.length;
		int height = dActVoxDi[0].length;
		int pages = dActVoxDi[0][0].length;
		
		for(int k=0;k<pages;k++) {
			int[][] label = new int[width][height];
			ArrayList<Integer> list = new ArrayList<>();
			list.add(0);
			int curLabel = 1;
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					if(dActVoxDi[i][j][k]) {
						int[] labels = new int[4];
						labels[0] = i>0 && j>0? label[i-1][j-1]:0;
						labels[1] = i>0? label[i-1][j]:0;
						labels[2] = i>0 && j<height-1? label[i-1][j+1]:0;
						labels[3] = j>0? label[i][j-1]:0;
						
						ArrayList<Integer> labelList = new ArrayList<>();
						int min = Integer.MAX_VALUE;
						for(int ii=0;ii<4;ii++) {
							if(labels[ii]!=0) {
								min = Math.min(min, labels[ii]);
								labelList.add(labels[ii]);
							}
						}
						if(labelList.size()==0) {
							label[i][j] = curLabel;
							list.add(0);
							curLabel++;
						}else {
							label[i][j] = min;
							for(int ii=1;ii<labelList.size();ii++) {
								union_connect(min,labelList.get(ii),list);
							}
						}
					}
				}
			}
			
			HashMap<Integer, ArrayList<int[]>> map = new HashMap<>();
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					if(label[i][j]!=0) {
						int root = union_find(label[i][j], list);
						label[i][j] = root;
						ArrayList<int[]> l = map.get(root);
						if(l==null) {
							l= new ArrayList<>();
							map.put(root, l);
						}
						l.add(new int[] {i,j});
					}
				}
			}
			
			for(Map.Entry<Integer, ArrayList<int[]>> entry:map.entrySet()) {
				ArrayList<int[]> l = entry.getValue(); 
				if(l.size()<minSize) {
					for(int[] p:l) {
						dActVoxDi[p[0]][p[1]][k] = false;
					}
				}
			}
		}
	}
	
	public static void twoPassConnect2DRemoveSmallArea(boolean[][][] dActVoxDi, int minSize, boolean[][] evtSpatialMask) {	
		int width = dActVoxDi.length;
		int height = dActVoxDi[0].length;
		int pages = dActVoxDi[0][0].length;
		
		for(int k=0;k<pages;k++) {
			int[][] label = new int[width][height];
			ArrayList<Integer> list = new ArrayList<>();
			list.add(0);
			int curLabel = 1;
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					if(dActVoxDi[i][j][k]) {
						int[] labels = new int[4];
						labels[0] = i>0 && j>0? label[i-1][j-1]:0;
						labels[1] = i>0? label[i-1][j]:0;
						labels[2] = i>0 && j<height-1? label[i-1][j+1]:0;
						labels[3] = j>0? label[i][j-1]:0;
						
						ArrayList<Integer> labelList = new ArrayList<>();
						int min = Integer.MAX_VALUE;
						for(int ii=0;ii<4;ii++) {
							if(labels[ii]!=0) {
								min = Math.min(min, labels[ii]);
								labelList.add(labels[ii]);
							}
						}
						if(labelList.size()==0) {
							label[i][j] = curLabel;
							list.add(0);
							curLabel++;
						}else {
							label[i][j] = min;
							for(int ii=1;ii<labelList.size();ii++) {
								union_connect(min,labelList.get(ii),list);
							}
						}
					}
				}
			}
			
			HashMap<Integer, ArrayList<int[]>> map = new HashMap<>();
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					if(label[i][j]!=0) {
						int root = union_find(label[i][j], list);
						label[i][j] = root;
						ArrayList<int[]> l = map.get(root);
						if(l==null) {
							l= new ArrayList<>();
							map.put(root, l);
						}
						l.add(new int[] {i,j});
					}
				}
			}
			
			for(Map.Entry<Integer, ArrayList<int[]>> entry:map.entrySet()) {
				ArrayList<int[]> l = entry.getValue(); 
				if(l.size()<minSize) {
					for(int[] p:l) {
						dActVoxDi[p[0]][p[1]][k] = false;
					}
				}
			}
			
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					if(!evtSpatialMask[i][j])
						dActVoxDi[i][j][k] = false;
				}
			}
		}
	}
	
	public static void twoPassConnect2DRemoveSmallArea4Conn(boolean[][][] dActVoxDi, int minSize, boolean[][] evtSpatialMask) {	
		int width = dActVoxDi.length;
		int height = dActVoxDi[0].length;
		int pages = dActVoxDi[0][0].length;
		
		for(int k=0;k<pages;k++) {
			int[][] label = new int[width][height];
			ArrayList<Integer> list = new ArrayList<>();
			list.add(0);
			int curLabel = 1;
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					if(dActVoxDi[i][j][k]) {
						int[] labels = new int[2];
						labels[0] = i>0? label[i-1][j]:0;
						labels[1] = j>0? label[i][j-1]:0;
						
						ArrayList<Integer> labelList = new ArrayList<>();
						int min = Integer.MAX_VALUE;
						for(int ii=0;ii<2;ii++) {
							if(labels[ii]!=0) {
								min = Math.min(min, labels[ii]);
								labelList.add(labels[ii]);
							}
						}
						if(labelList.size()==0) {
							label[i][j] = curLabel;
							list.add(0);
							curLabel++;
						}else {
							label[i][j] = min;
							for(int ii=1;ii<labelList.size();ii++) {
								union_connect(min,labelList.get(ii),list);
							}
						}
					}
				}
			}
			
			HashMap<Integer, ArrayList<int[]>> map = new HashMap<>();
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					if(label[i][j]!=0) {
						int root = union_find(label[i][j], list);
						label[i][j] = root;
						ArrayList<int[]> l = map.get(root);
						if(l==null) {
							l= new ArrayList<>();
							map.put(root, l);
						}
						l.add(new int[] {i,j});
					}
				}
			}
			
			for(Map.Entry<Integer, ArrayList<int[]>> entry:map.entrySet()) {
				ArrayList<int[]> l = entry.getValue(); 
				if(l.size()<minSize) {
					for(int[] p:l) {
						dActVoxDi[p[0]][p[1]][k] = false;
					}
				}
			}
			
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					if(!evtSpatialMask[i][j])
						dActVoxDi[i][j][k] = false;
				}
			}
		}
	}
	
	public static HashMap<Integer, ArrayList<int[]>> twoPassConnect2D(boolean[][] input) {		
		int width = input.length;
		int height = input[0].length;
		
		int[][] label = new int[width][height];
		ArrayList<Integer> list = new ArrayList<>();
		list.add(0);
		int curLabel = 1;
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(input[i][j]) {
					int[] labels = new int[4];
					labels[0] = i>0 && j>0? label[i-1][j-1]:0;
					labels[1] = i>0? label[i-1][j]:0;
					labels[2] = i>0 && j<height-1? label[i-1][j+1]:0;
					labels[3] = j>0? label[i][j-1]:0;
					
					ArrayList<Integer> labelList = new ArrayList<>();
					int min = Integer.MAX_VALUE;
					for(int ii=0;ii<4;ii++) {
						if(labels[ii]!=0) {
							min = Math.min(min, labels[ii]);
							labelList.add(labels[ii]);
						}
					}
					if(labelList.size()==0) {
						label[i][j] = curLabel;
						list.add(0);
						curLabel++;
					}else {
						label[i][j] = min;
						for(int ii=1;ii<labelList.size();ii++) {
							union_connect(min,labelList.get(ii),list);
						}
					}
				}
			}
		}
		
		HashMap<Integer,Integer> rootMap = new HashMap<>();
		int cnt = 1;
		HashMap<Integer, ArrayList<int[]>> map = new HashMap<>();
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(label[i][j]!=0) {
					int root = union_find(label[i][j], list);
					int value;
					if(rootMap.get(root)!=null) {
						value = rootMap.get(root);
					}else {
						value = cnt;
						rootMap.put(root, value);
						cnt++;
					}
					
					label[i][j] = value;
					ArrayList<int[]> l = map.get(value);
					if(l==null) {
						l= new ArrayList<>();
						map.put(value, l);
					}
					l.add(new int[] {i,j});
				}
			}
		}
		
		return map;
	}
	
	public static HashMap<Integer, ArrayList<int[]>> twoPassConnect2D(int[][] input) {		
		int width = input.length;
		int height = input[0].length;
		
		int[][] label = new int[width][height];
		ArrayList<Integer> list = new ArrayList<>();
		list.add(0);
		int curLabel = 1;
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(input[i][j]>0) {
					int[] labels = new int[4];
					labels[0] = i>0 && j>0? label[i-1][j-1]:0;
					labels[1] = i>0? label[i-1][j]:0;
					labels[2] = i>0 && j<height-1? label[i-1][j+1]:0;
					labels[3] = j>0? label[i][j-1]:0;
					
					ArrayList<Integer> labelList = new ArrayList<>();
					int min = Integer.MAX_VALUE;
					for(int ii=0;ii<4;ii++) {
						if(labels[ii]!=0) {
							min = Math.min(min, labels[ii]);
							labelList.add(labels[ii]);
						}
					}
					if(labelList.size()==0) {
						label[i][j] = curLabel;
						list.add(0);
						curLabel++;
					}else {
						label[i][j] = min;
						for(int ii=1;ii<labelList.size();ii++) {
							union_connect(min,labelList.get(ii),list);
						}
					}
				}
			}
		}
		
		HashMap<Integer,Integer> rootMap = new HashMap<>();
		int cnt = 1;
		HashMap<Integer, ArrayList<int[]>> map = new HashMap<>();
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(label[i][j]!=0) {
					int root = union_find(label[i][j], list);
					int value;
					if(rootMap.get(root)!=null) {
						value = rootMap.get(root);
					}else {
						value = cnt;
						rootMap.put(root, value);
						cnt++;
					}
					
					label[i][j] = value;
					ArrayList<int[]> l = map.get(value);
					if(l==null) {
						l= new ArrayList<>();
						map.put(value, l);
					}
					l.add(new int[] {i,j});
				}
			}
		}
		
		return map;
	}
	
	public static HashMap<Integer, ArrayList<int[]>> twoPassConnect2D(int[][][] mapS0, int t) {	
		int width = mapS0.length;
		int height = mapS0[0].length;
		int[][] label = new int[width][height];
		ArrayList<Integer> list = new ArrayList<>();
		list.add(0);
		int curLabel = 1;
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(mapS0[i][j][t]>0) {
					int[] labels = new int[4];
					labels[0] = i>0 && j>0? label[i-1][j-1]:0;
					labels[1] = i>0? label[i-1][j]:0;
					labels[2] = i>0 && j<height-1? label[i-1][j+1]:0;
					labels[3] = j>0? label[i][j-1]:0;
					
					ArrayList<Integer> labelList = new ArrayList<>();
					int min = Integer.MAX_VALUE;
					for(int ii=0;ii<4;ii++) {
						if(labels[ii]!=0) {
							min = Math.min(min, labels[ii]);
							labelList.add(labels[ii]);
						}
					}
					if(labelList.size()==0) {
						label[i][j] = curLabel;
						list.add(0);
						curLabel++;
					}else {
						label[i][j] = min;
						for(int ii=1;ii<labelList.size();ii++) {
							union_connect(min,labelList.get(ii),list);
						}
					}
				}
			}
		}
			
		int cnt = 1;
		HashMap<Integer,Integer> rootMap = new HashMap<>();
		HashMap<Integer, ArrayList<int[]>> map = new HashMap<>();
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(label[i][j]!=0) {
					int root = union_find(label[i][j], list);
					int value;
					if(rootMap.get(root)!=null) {
						value = rootMap.get(root);
					}else {
						value = cnt;
						rootMap.put(root, value);
						cnt++;
					}
					label[i][j] = value;
					ArrayList<int[]> l = map.get(value);
					if(l==null) {
						l= new ArrayList<>();
						map.put(value, l);
					}
					l.add(new int[] {i,j});
				}
			}
		}
			
		return map;
		
	}
	
	public static HashMap<Integer, ArrayList<int[]>> twoPassConnect3D(int[][][] dActVoxDi) {
		int width = dActVoxDi.length;
		int height = dActVoxDi[0].length;
		int pages = dActVoxDi[0][0].length;
		
		
		int[][][] label = new int[width][height][pages];
		ArrayList<Integer> list = new ArrayList<>();
		list.add(0);
		int curLabel = 1;
		for(int k=0;k<pages;k++) {
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					if(dActVoxDi[i][j][k]>0) {
						int[] labels = new int[13];
						labels[0] = i>0 && j>0 && k>0?label[i-1][j-1][k-1]:0;	// previous page
						labels[1] = i>0 && k>0?label[i-1][j][k-1]:0;
						labels[2] = i>0 && j<height-1 && k>0?label[i-1][j+1][k-1]:0;
						labels[3] = j>0 && k>0?label[i][j-1][k-1]:0;
						labels[4] = k>0?label[i][j][k-1]:0;
						labels[5] = j<height-1 && k>0?label[i][j+1][k-1]:0;
						labels[6] = i<width-1 && j>0 && k>0?label[i+1][j-1][k-1]:0;
						labels[7] = i<width-1 && k>0?label[i+1][j][k-1]:0;
						labels[8] = i<width-1 && j<height-1 && k>0?label[i+1][j+1][k-1]:0;
						
						labels[9] = i>0 && j>0?label[i-1][j-1][k]:0;			// previous row
						labels[10] = i>0?label[i-1][j][k]:0;
						labels[11] = i>0 && j<height-1?label[i-1][j+1][k]:0;
						
						labels[12] = j>0?label[i][j-1][k]:0;					// previous column
						
						ArrayList<Integer> labelList = new ArrayList<>();
						int min = Integer.MAX_VALUE;
						for(int ii=0;ii<13;ii++) {
							if(labels[ii]!=0) {
								min = Math.min(min, labels[ii]);
								labelList.add(labels[ii]);
							}
						}
						if(labelList.size()==0) {
							label[i][j][k] = curLabel;
							list.add(0);
							curLabel++;
						}else {
							label[i][j][k] = min;
							for(int ii=1;ii<labelList.size();ii++) {
								union_connect(min,labelList.get(ii),list);
							}
						}
						
					}
				}
			}
		}
		
		HashMap<Integer,Integer> rootMap = new HashMap<>();
		HashMap<Integer, ArrayList<int[]>> map = new HashMap<>();
		int cnt = 1;

		for(int k=0;k<pages;k++) {
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					if(dActVoxDi[i][j][k]>0) {
						int root = union_find(label[i][j][k], list);
						int value;
						if(rootMap.get(root)!=null) {
							value = rootMap.get(root);
						}else {
							value = cnt;
							rootMap.put(root, value);
//							System.out.println(value + " ");
							cnt++;
						}

						label[i][j][k] = value;
						ArrayList<int[]> l = map.get(value);
						if(l==null) {
							l= new ArrayList<>();
							map.put(value, l);
						}
						l.add(new int[] {i,j,k});
					}
				}
			}
		}
		
		return map;
	}
	
	public static int[][][] twoPassConnect3D(boolean[][][] dActVoxDi, HashMap<Integer, ArrayList<int[]>> map) {
		int width = dActVoxDi.length;
		int height = dActVoxDi[0].length;
		int pages = dActVoxDi[0][0].length;
		
		
		int[][][] label = new int[width][height][pages];
		ArrayList<Integer> list = new ArrayList<>();
		list.add(0);
		int curLabel = 1;
		for(int k=0;k<pages;k++) {
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					if(dActVoxDi[i][j][k]) {
						int[] labels = new int[13];
						labels[0] = i>0 && j>0 && k>0?label[i-1][j-1][k-1]:0;	// previous page
						labels[1] = i>0 && k>0?label[i-1][j][k-1]:0;
						labels[2] = i>0 && j<height-1 && k>0?label[i-1][j+1][k-1]:0;
						labels[3] = j>0 && k>0?label[i][j-1][k-1]:0;
						labels[4] = k>0?label[i][j][k-1]:0;
						labels[5] = j<height-1 && k>0?label[i][j+1][k-1]:0;
						labels[6] = i<width-1 && j>0 && k>0?label[i+1][j-1][k-1]:0;
						labels[7] = i<width-1 && k>0?label[i+1][j][k-1]:0;
						labels[8] = i<width-1 && j<height-1 && k>0?label[i+1][j+1][k-1]:0;
						
						labels[9] = i>0 && j>0?label[i-1][j-1][k]:0;			// previous row
						labels[10] = i>0?label[i-1][j][k]:0;
						labels[11] = i>0 && j<height-1?label[i-1][j+1][k]:0;
						
						labels[12] = j>0?label[i][j-1][k]:0;					// previous column
						
						ArrayList<Integer> labelList = new ArrayList<>();
						int min = Integer.MAX_VALUE;
						for(int ii=0;ii<13;ii++) {
							if(labels[ii]!=0) {
								min = Math.min(min, labels[ii]);
								labelList.add(labels[ii]);
							}
						}
						if(labelList.size()==0) {
							label[i][j][k] = curLabel;
							list.add(0);
							curLabel++;
						}else {
							label[i][j][k] = min;
							for(int ii=1;ii<labelList.size();ii++) {
								union_connect(min,labelList.get(ii),list);
							}
						}
						
					}
				}
			}
		}
		
		HashMap<Integer,Integer> rootMap = new HashMap<>();
		int cnt = 1;

		for(int k=0;k<pages;k++) {
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					if(dActVoxDi[i][j][k]) {
						int root = union_find(label[i][j][k], list);
						int value;
						if(rootMap.get(root)!=null) {
							value = rootMap.get(root);
						}else {
							value = cnt;
							rootMap.put(root, value);
//							System.out.println(value + " ");
							cnt++;
						}

						label[i][j][k] = value;
						ArrayList<int[]> l = map.get(value);
						if(l==null) {
							l= new ArrayList<>();
							map.put(value, l);
						}
						l.add(new int[] {i,j,k});
					}
				}
			}
		}
		
		return label;
	}
	
	public static HashMap<Integer, ArrayList<Integer>> twoPassConnect2D_ForBuilder(boolean[][] input) {		
		int width = input.length;
		int height = input[0].length;
		int changeParameter = Math.max(width, height);
		
		
		int[][] label = new int[width][height];
		ArrayList<Integer> list = new ArrayList<>();
		list.add(0);
		int curLabel = 1;
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(input[i][j]) {
					int[] labels = new int[4];
					labels[0] = i>0 && j>0? label[i-1][j-1]:0;
					labels[1] = i>0? label[i-1][j]:0;
					labels[2] = i>0 && j<height-1? label[i-1][j+1]:0;
					labels[3] = j>0? label[i][j-1]:0;
					
					ArrayList<Integer> labelList = new ArrayList<>();
					int min = Integer.MAX_VALUE;
					for(int ii=0;ii<4;ii++) {
						if(labels[ii]!=0) {
							min = Math.min(min, labels[ii]);
							labelList.add(labels[ii]);
						}
					}
					if(labelList.size()==0) {
						label[i][j] = curLabel;
						list.add(0);
						curLabel++;
					}else {
						label[i][j] = min;
						for(int ii=1;ii<labelList.size();ii++) {
							union_connect(min,labelList.get(ii),list);
						}
					}
				}
			}
		}
		
		HashMap<Integer,Integer> rootMap = new HashMap<>();
		int cnt = 1;
		HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(label[i][j]!=0) {
					int root = union_find(label[i][j], list);
					int value;
					if(rootMap.get(root)!=null) {
						value = rootMap.get(root);
					}else {
						value = cnt;
						rootMap.put(root, value);
						cnt++;
					}
					
					label[i][j] = value;
					ArrayList<Integer> l = map.get(value);
					if(l==null) {
						l= new ArrayList<>();
						map.put(value, l);
					}
					l.add(i*changeParameter + j);
				}
			}
		}
		
		return map;
	}
	
	public static HashMap<Integer, ArrayList<Integer>> twoPassConnect2D_ForBuilder(boolean[][] input, int minSize) {		
		int width = input.length;
		int height = input[0].length;
		int changeParameter = Math.max(width, height);
		
		
		int[][] label = new int[width][height];
		ArrayList<Integer> list = new ArrayList<>();
		list.add(0);
		int curLabel = 1;
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(input[i][j]) {
					int[] labels = new int[4];
					labels[0] = i>0 && j>0? label[i-1][j-1]:0;
					labels[1] = i>0? label[i-1][j]:0;
					labels[2] = i>0 && j<height-1? label[i-1][j+1]:0;
					labels[3] = j>0? label[i][j-1]:0;
					
					ArrayList<Integer> labelList = new ArrayList<>();
					int min = Integer.MAX_VALUE;
					for(int ii=0;ii<4;ii++) {
						if(labels[ii]!=0) {
							min = Math.min(min, labels[ii]);
							labelList.add(labels[ii]);
						}
					}
					if(labelList.size()==0) {
						label[i][j] = curLabel;
						list.add(0);
						curLabel++;
					}else {
						label[i][j] = min;
						for(int ii=1;ii<labelList.size();ii++) {
							union_connect(min,labelList.get(ii),list);
						}
					}
				}
			}
		}
		
		HashMap<Integer,Integer> rootMap = new HashMap<>();
		int cnt = 1;
		HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(label[i][j]!=0) {
					int root = union_find(label[i][j], list);
					int value;
					if(rootMap.get(root)!=null) {
						value = rootMap.get(root);
					}else {
						value = cnt;
						rootMap.put(root, value);
						cnt++;
					}
					
					label[i][j] = value;
					ArrayList<Integer> l = map.get(value);
					if(l==null) {
						l= new ArrayList<>();
						map.put(value, l);
					}
					l.add(i*changeParameter + j);
				}
			}
		}
		
		cnt = 1;
		HashMap<Integer, ArrayList<Integer>> newMap = new HashMap<>();
		for(Entry<Integer, ArrayList<Integer>> entry : map.entrySet()) {
			ArrayList<Integer> points = entry.getValue();
			if(points.size()>minSize) {
				newMap.put(cnt, points);
				cnt++;
			}else {
				for(int xy:points) {
					int x = xy/changeParameter;
					int y = xy%changeParameter;
					input[x][y] = false;
				}
			}
		}
		
		return newMap;
	}
	
	public static int union_find(int label, ArrayList<Integer> list){
		int i = label;
		
		while(list.get(i)!=0) {
			i = list.get(i);
		}
		if(i!=label)
			list.set(label, i);
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
}
