package va.vt.cbil;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

//import sc.fiji.CMP_BIA.segmentation.structures.Labelling2D;
//import sc.fiji.CMP_BIA.segmentation.superpixels.jSLIC;

public class GTW2 {

	public static float[][][] imputeMov(float[][][] df0) {
		int W = df0.length;
		int H = df0[0].length;
		int T = df0[0][0].length;
		ArrayList<int[]> points = new ArrayList<>();
		
		float[][][] df0ip = df0;
		for(int i=0;i<W;i++) {
			for(int j=0;j<H;j++) {
				for(int k=0;k<T;k++) {
					if(df0[i][j][k] == -1) {
						points.add(new int[] {i,j});
						break;
					}
				}
			}
		}
		
		for(int[] p:points) {
			int px = p[0];
			int py = p[1];
			
			float[] x0 = df0[px][py];
			
			for(int t=1;t<T;t++) {
				if(x0[t] == -1)
					x0[t] = x0[t-1];
			}
			for(int t=T-2;t>=0;t--) {
				if(x0[t] == -1)
					x0[t] = x0[t+1];
			}
			
			df0ip[px][py] = x0;
		}
		
		for(int i=0;i<W;i++) {
			for(int j=0;j<H;j++) {
				for(int k=0;k<T;k++) {
					if(df0ip[i][j][k] == -1) {
						df0ip[i][j][k] = 0;
					}
				}
			}
		}
		return df0ip;
	}
	
	public static float[][][] imputeMov2(float[][][] df0) {
		int W = df0.length;
		int H = df0[0].length;
		int T = df0[0][0].length;
		ArrayList<int[]> points = new ArrayList<>();
		
		float[][][] df0ip = df0;
		for(int i=0;i<W;i++) {
			for(int j=0;j<H;j++) {
				for(int k=0;k<T;k++) {
					if(Float.isNaN(df0[i][j][k])) {
						points.add(new int[] {i,j});
						break;
					}
				}
			}
		}
		
		for(int[] p:points) {
			int px = p[0];
			int py = p[1];
			
			float[] x0 = df0[px][py];
			
			for(int t=1;t<T;t++) {
				if(Float.isNaN(x0[t]))
					x0[t] = x0[t-1];
			}
			for(int t=T-2;t>=0;t--) {
				if(Float.isNaN(x0[t]))
					x0[t] = x0[t+1];
			}
			
			df0ip[px][py] = x0;
		}
		
		for(int i=0;i<W;i++) {
			for(int j=0;j<H;j++) {
				for(int k=0;k<T;k++) {
					if(Float.isNaN(df0ip[i][j][k])) {
						df0ip[i][j][k] = 0;
					}
				}
			}
		}
		return df0ip;
	}
	
	public static float[][] maxMoveMean(float[][][] data, int movWin){
		int width = data.length;
		int height = data[0].length;
		int pages = data[0][0].length;
		
		int left = (movWin-1)/2;
		int right = (movWin-1)/2;
		if(movWin%2==0)
			left++;
		
		float[][] result = new float[width][height];
		
		
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				float sum = 0;
				float max = Integer.MIN_VALUE;
				// right
				for(int k=0;k<right;k++) {
					sum += data[i][j][k];
				}
				
				for(int k=0;k<pages;k++) {
					if(k<=left) {
						sum += data[i][j][k+right];
						max = Math.max(max, sum/(k+right+1));
					}else if(k>left && k<pages-right) {
						sum += data[i][j][k+right] - data[i][j][k-left-1];
						max = Math.max(max, sum/movWin);
					}else {
						sum -= data[i][j][k-left-1];
						max = Math.max(max, sum/(pages-k+left));
					}
				}
				
				if(max<0)
					max = 0;
				result[i][j] = max;
			}
		}
		
		
		return result;
		
	}

	public static int cellQuantile(float[][][] dFip, float p, float s00) {
		int W = dFip.length;
		int H = dFip[0].length;
		int T = dFip[0][0].length;
		ArrayList<Float> df = new ArrayList<>();
		for(int i=0;i<W;i++) {
			for(int j=0;j<H;j++) {
				for(int k=0;k<T;k++) {
					df.add(dFip[i][j][k]);
				}
			}
		}
		
		Collections.sort(df, new Comparator<Float>() {
			@Override
			public int compare(Float e1, Float e2) {
				if(e1<e2)
					return 1;
				else if (e1>e2)
					return -1;
				else
					return 0;
			}				
		});
		
		float pp = 1-p;
		float dp = 1.0f/W/H/T;
		int index = (int) (pp/dp);
		float result;
		if(index<0)
			result = df.get(0);
		else
			result = df.get(index);

		return (int) Math.ceil(result/s00);
	}
	
	public static int union_find(int label, ArrayList<Integer> list){
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

	public static int[][][] getSuperEventRisingMapMultiThr(float[][][] dFx, int[][][] mskIn, int thrMax, float s00) {
		int W0 = dFx.length;
		int H0 = dFx[0].length;
		int T0 = dFx[0][0].length;
		
		int[][][] tMapMT = new int[W0][H0][thrMax+1];							// checked
		for(int i=0;i<=thrMax;i++) {
			boolean[][][] dFxHi = new boolean[W0][H0][T0];
			for(int x=0;x<W0;x++) {
				for(int y=0;y<H0;y++) {
					for(int z=0;z<T0;z++) {
						dFxHi[x][y][z] = dFx[x][y][z]>i*s00;
						if(mskIn[x][y][z]==-1)
							dFxHi[x][y][z] = false;					
					}
				}
			}
//			GTW.twoPassConnect2D(dFxHi, 4);
			ConnectedComponents.twoPassConnect2DRemoveSmallArea(dFxHi, 4);		// checked
			boolean[][] M0 = new boolean[W0][H0];
			for(int x=0;x<W0;x++) {
				for(int y=0;y<H0;y++) {
					for(int z=0;z<T0;z++) {
						if(dFxHi[x][y][z]) {
							M0[x][y] = true;
							break;
						}
					}
				}
			}
			int[][] tMap = new int[W0][H0];										// checked
			for(int x=0;x<W0;x++) {
				for(int y=0;y<H0;y++) {
					tMap[x][y] = -1;
					if(M0[x][y]) {
						int t0 = -1;
						for(int t=0;t<T0;t++) {
							if(dFxHi[x][y][t]) {
								t0=t;
								break;
							}
						}
						if(t0!=-1)
							tMap[x][y] = t0;
					}
					tMapMT[x][y][i] = tMap[x][y];
				}
			}
		}
		return tMapMT;
		
	}

	public static Mov2spResult mov2sp(float[][] dFAvg, boolean[][] validMap, int spSz,
			float s00, String path) {
		int W = dFAvg.length;
		int H = dFAvg[0].length;
		
		// Just for test, ignore them
		ImageProcessor blurer = new FloatProcessor(dFAvg);
		blurer.blurGaussian(1);	// gauss
		dFAvg = blurer.getFloatArray();
		
		ImagePlus image = new ImagePlus(path);
		ImageStack stk = image.getImageStack().crop(0, 0, 0, W, H, 1);
		for(int i=0;i<W;i++) {
			for(int j=0;j<H;j++) {
				if(!validMap[i][j]) {
					dFAvg[i][j] = -100;
				}
				stk.setVoxel(i,j,0,dFAvg[i][j]);

			}
		}
		spSz = (int)Math.sqrt(spSz/3);
		ImagePlus imP = new ImagePlus("",stk);
		jSLIC sp = new jSLIC(imP);
		sp.process(spSz, 0.01f, 10, 0.1f);
		Labelling2D segm = sp.getSegmentation();
		int[][] labels = segm.getData();
		
		HashMap<Integer, ArrayList<int[]>> spLst = new HashMap<>();
		int cnt = 0;
		HashMap<Integer, Integer> labelSet = new HashMap<>();
		for(int i=0;i<W;i++) {
			for(int j=0;j<H;j++) {
				if(validMap[i][j]) {
					int label = labels[i][j];
					if(labelSet.get(label)==null) {
						cnt++;
						labelSet.put(label, cnt);
					}
					int key = labelSet.get(label);
					ArrayList<int[]> l = spLst.get(key);
					if(l==null) {
						l = new ArrayList<>();
					}
					l.add(new int[] {i,j});
					spLst.put(key, l);
				}
			}
		}
		
		HashMap<Integer, Float> spStd = new HashMap<>();
		HashMap<Integer, int[]> spSeedVec = new HashMap<>();
		for(int i=1;i<=spLst.size();i++) {
			ArrayList<int[]> points = spLst.get(i);
			float std = (float) (s00/Math.sqrt(points.size()));
			spStd.put(i,std);
			float sumX = 0;
			float sumY = 0;
			for(int[] p:points) {
				sumX += p[0];
				sumY += p[1];
			}
			int iw0 = Math.round(sumX/points.size());
			int ih0 = Math.round(sumY/points.size());
			spSeedVec.put(i, new int[] {iw0,ih0});
		}
		
		return new Mov2spResult(spStd, spSeedVec, spLst);
	}

	static class Mov2spResult{
		HashMap<Integer, Float> spStd = null;
		HashMap<Integer, int[]> spSeedVec = null;
		HashMap<Integer, ArrayList<int[]>> spLst = null;
		
		public Mov2spResult(HashMap<Integer, Float> spStd, HashMap<Integer, int[]> spSeedVec, HashMap<Integer, ArrayList<int[]>> spLst) {
			this.spStd = spStd;
			this.spSeedVec = spSeedVec;
			this.spLst = spLst;
		}
	}
	
	public static HashMap<Integer, HashSet<Integer>> getNeighborSuperPixel(HashMap<Integer, ArrayList<int[]>> spLst, int width, int height) {
		HashMap<Integer, HashSet<Integer>> result = new HashMap<>();
		int[] dh = new int[] {-1,0,1,-1,1,-1,0,1};
		int[] dw = new int[] {-1,-1,-1,0,0,1,1,1};
		
		int[][] labels = new int[width][height];
		for(Entry<Integer, ArrayList<int[]>> entry:spLst.entrySet()) {
			ArrayList<int[]> points = entry.getValue();
			int label = entry.getKey();
			for(int[] p:points) {
				labels[p[0]][p[1]] = label;
			}
		}
		
		for(Entry<Integer, ArrayList<int[]>> entry:spLst.entrySet()) {
			int label = entry.getKey();
			ArrayList<int[]> points = entry.getValue();
			HashSet<Integer> l = new HashSet<>();
			for(int[] p:points) {
				for(int i = 0;i<dh.length;i++) {
					int px = Math.min(Math.max(p[0]+dw[i],0),width-1);
					int py = Math.min(Math.max(p[1]+dh[i],0),height-1);
					if(labels[px][py]>0 && labels[px][py]!=label)
						l.add(labels[px][py]);
				}
			}
			result.put(label, l);
			
			
		}
		return result;
	}

	public static EvtReconResult evtRecon(HashMap<Integer, ArrayList<int[]>> spLst, float[][] cx, int[][] evtMap0, float minShow) {
		int W = evtMap0.length;
		int H = evtMap0[0].length;
//		int nSp = cx.length;
		int T = cx[0].length;
		
		float[][][] evtRecon = new float[W][H][T];
		int[][][] evtL = new int[W][H][T];
		
		for(Entry<Integer, ArrayList<int[]>> entry:spLst.entrySet()) {
			int index = entry.getKey()-1;
			ArrayList<int[]> sp0 = entry.getValue();
			float[] x0 = cx[index];
			for(int[] p:sp0) {												// checked
				for(int t=0;t<x0.length;t++)
					evtRecon[p[0]][p[1]][t] = x0[t];
			}
			
			// get the mode
			HashMap<Integer,Integer> evtNum = new HashMap<>();
			for(int[] p:sp0) {												// checked
				int label = evtMap0[p[0]][p[1]];
				Integer cnt = evtNum.get(label);
				if(cnt==null)
					cnt = 0;
				cnt = cnt + 1;
				evtNum.put(label, cnt);
			}
			int l0 = 0;
			int num = 0;
			for(Entry<Integer, Integer> entry0:evtNum.entrySet()) {			// checked
				if(entry0.getValue()>num) {
					num = entry0.getValue();
					l0 = entry0.getKey();
				}
			}
			
			int t0 = 0;														// checked
			for(int t=0;t<x0.length;t++) {
				if(x0[t]>=minShow) {
					t0 = t;
					break;
				}
			}
			int t1 = x0.length-1;											// checked
			for(int t=x0.length-1;t>=0;t--) {
				if(x0[t]>=minShow) {
					t1 = t;
					break;
				}
			}
			
			for(int[] p:sp0) {												// checked
				for(int t=t0;t<=t1;t++)
					evtL[p[0]][p[1]][t] = l0;
			}
		}
		
		return new EvtReconResult(evtL, evtRecon);
	}
	
	public static GTWResult spgtw(float[][][] dF, int[][][] seMap, int seSel, float smoBase, int maxStp, int cDelay,
			int spSz, int spT, Opts opts, int rgws, int rgwe, int rghs, int rghe, int rgts, int rgte, String path) {
		// spgtw super pixel GTW
		// make one burst to super pixels and run gtw
		int width = rgwe-rgws+1;
		int height = rghe-rghs+1;
		int pages = rgte-rgts+1;
		
		boolean isFail = false;
		maxStp = Math.max(1, Math.min(maxStp, (pages+1)/2));	// checked
		
		// dF and local noise
		float s00 = getDfNoise(dF,rgws,rgwe,rghs,rghe,rgts,rgte);	// checked
		
		// super pixels
		// extract one event
		boolean[][] validMap = new boolean[width][height];		// checked
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				for(int k=0;k<pages;k++) {
					if(seMap[i+rgws][j+rghs][k+rgts]==seSel) {
						validMap[i][j] = true;
						break;
					}
				}
			}
		}
		
		float[][][] dFip = new float[width][height][pages];		// checked
		float[][] dFAvg = new float[width][height];
		ArrayList<Float> dFM = new ArrayList<>();
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				float sum = 0;
				int cnt = 0;
				for(int k=0;k<pages;k++) {
					dFip[i][j][k] = dF[i+rgws][j+rghs][k+rgts];
					if(!Float.isNaN(dF[i+rgws][j+rghs][k+rgts])) {
						sum += dFip[i][j][k];
						cnt++;
						// Changed
						if(seMap[i+rgws][j+rghs][k+rgts]==seSel)
							dFM.add(dF[i+rgws][j+rghs][k+rgts]/s00);
					}
				}
				dFAvg[i][j] = sum/cnt;
			}
		}
		float pk = getMedian(dFM);
		float thrpk = pk/2;
		
		for(int i=0;i<width;i++) {								// checked
			for(int j=0;j<height;j++) {
				for(int k=0;k<pages;k++) {
					int seMapValue = seMap[i+rgws][j+rghs][k+rgts];
					if(seMapValue!=seSel && (seMapValue>0  || dF[i+rgws][j+rghs][k+rgts]>thrpk*s00))
						dFip[i][j][k] = -Float.NaN;
				}
			}
		}
		dFip = imputeMov2(dFip);								// checked
		
		// rising time as feature
		int thrMax = cellQuantile(dFip, 0.999f, s00);		// checked: do not understand the principle of matlab.  could be used
		
		int[][][] m0Msk = new int[width][height][pages];		// checked
		int it0s = Integer.MAX_VALUE;
		int it0e = Integer.MIN_VALUE;
		
		for(int i=0;i<width;i++) {								// checked
			for(int j=0;j<height;j++) {
				for(int k=0;k<pages;k++) {
					int seMapValue = seMap[i+rgws][j+rghs][k+rgts];
					if(seMapValue==seSel) {
						m0Msk[i][j][k] = 1;
						it0s = Math.min(it0s, k);
						it0e = Math.max(it0e, k);
					}
					else if(seMapValue>0 && seMapValue!=seSel)
						m0Msk[i][j][k] = -1;
				}
			}
		}
		int[][][] tMapMT = getSuperEventRisingMapMultiThr(dFip,m0Msk,thrMax,s00); 	// checked
		for(int i=0;i<width;i++) {								// checked
			for(int j=0;j<height;j++) {
				boolean tmp = false;
				for(int k=0;k<thrMax+1;k++) {
					if(tMapMT[i][j][k]!=-1) {
						tmp = true;
						break;
					}
				}
				validMap[i][j] &= tmp;
			}
		}
		// signal part	// TODO	
		int rgt00s = Math.max(it0s-5, 0);
		int rgt00e = Math.min(it0e+5, pages-1);
		// dat dFip use
		
		// region growing
		int nSpMax = Math.round(10000*spT/(it0e-it0s+1));		// checked
//		if(image==null)
//			image = new ImagePlus("D:\\JavaWorkspace\\data\\AVG_Ch2-lck'sEAAT2.tif");
		int spSz1 = Math.max(width*height/nSpMax, spSz);		// checked
//		HashMap<Integer,ArrayList<int[]>> spLst = GTW.mov2sp(dFAvg,validMap,spSz1,s00,image);	// checked
		Mov2spResult movResult = mov2sp(dFAvg,validMap,spSz1,s00,path);
		HashMap<Integer,ArrayList<int[]>> spLst = movResult.spLst;
		HashMap<Integer, Float> spStd = movResult.spStd;
		HashMap<Integer, int[]> spSeedVec = movResult.spSeedVec;
		
		System.out.println(spLst.size());
		

		if(spLst.size()<10) {									// checked
			spLst = new HashMap<>();
			ArrayList<int[]> l = new ArrayList<>();
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					if(validMap[i][j])
						l.add(new int[] {i,j});
				}
			}
			spLst.put(1, l);
			return new GTWResult(spLst,0,pages-1,true);
		}
		
		// alignment
//		int gapSeed = 2;
//		Sp2GraphResult sp2GraphResult = sp2graph(dFip,validMap,spLst,spSeedVec.get(1),gapSeed,rgt00s,rgt00e);
//		float[][] ref = sp2GraphResult.ref;
//		float[][] tst = sp2GraphResult.tst;
//		float[] refBase = sp2GraphResult.refBase;
//		ArrayList<Integer> source = sp2GraphResult.s;
//		ArrayList<Integer> sink = sp2GraphResult.t;
//		
//		// gtw
//		boolean[] idxGood = sp2GraphResult.idxGood;
//		int cnt = 1;
//		float[] s2 = new float[spLst.size()];
//		HashMap<Integer, int[]> spSeedVecNew = new HashMap<>();
//		for(int i=0;i<idxGood.length;i++) {
//			if(idxGood[i]) {
//				int[] p = spSeedVec.get(i+1);
//				float std = spStd.get(i+1);
//				std = std*std;
//				spSeedVecNew.put(cnt, p);
//				s2[cnt-1] = std;
//				cnt++;
//			}
//		}
//		spSeedVec = spSeedVecNew;
//		
//		float medianS = getMedian(s2);
//		for(int i=0;i<s2.length;i++) {
//			if(s2[i]==0)
//				s2[i] = medianS;
//		}
//		
//		HashMap<Integer, float[][]> path0 = null;
//		if(spLst.size()>3 && refBase.length>5) {
//			BuildGTWGraphResult graph = buildGTWGraph(ref,tst,source,sink,smoBase,maxStp,s2);
//			System.out.println("Finish build graph");
//			double[][] ss = graph.ss;
//			double[][] ee = graph.ee;
//			GraphCut graphCut = new GraphCut(ss.length,ee.length);
//			for(int i=0;i<ss.length;i++) {
//				graphCut.setTerminalWeights(i, (float)ss[i][0], (float)ss[i][1]);
//			}
//			for(int i=0;i<ee.length;i++) {
//				graphCut.setEdgeWeight((int)ee[i][0], (int)ee[i][1], (float)ee[i][2], (float)ee[i][3]);
//			}
//			long start = System.currentTimeMillis();
//			graphCut.computeMaximumFlow(true, null);
//			long end = System.currentTimeMillis();
////			System.out.println(ss.length);
//			System.out.println((end-start) + "ms");
////			System.out.println("Finish maximum flow");
//			boolean[] nodeLabel = new boolean[ss.length];
//			for(int i=0;i<ss.length;i++) {
//				if(graphCut.getTerminal(i)==Terminal.FOREGROUND)
//					nodeLabel[i] = true;
//			}
//			path0 = label2path4Aosokin(nodeLabel,graph);
//		}else {
//			int nPix = tst.length;
//			int nTps = tst[0].length;
//			path0 = new HashMap<>();
//			for(int i=1;i<=nPix;i++) {
//				float[][] p0 = new float[nTps+1][4];
//				for(int j=0;j<=nTps;j++) {
//					p0[j] = new float[] {j,j,j+1,j+1};
//				}
//				path0.put(i, p0);
//			}
//		}
////		System.out.println("Finish finding path");
//		
//		// warped curves
//		int[][] vMap1 = new int[width][height];
//		for(int i=1;i<=spSeedVec.size();i++) {
//			int[] p = spSeedVec.get(i);
//			vMap1[p[0]][p[1]] = i;
//		}
//		float[][][] datWarp = warpRef2Tst(path0, refBase,spSeedVec, vMap1,width,height,refBase.length);
//		float[][] cx = new float[spSeedVec.size()][refBase.length];
//		for(int i=0;i<spSeedVec.size();i++) {
//			int[] p = spSeedVec.get(i+1);
//			int x = p[0];
//			int y = p[1];
//			cx[i] = datWarp[x][y];
//		}
		int nSp = spLst.size();			// checked
////		System.out.println("Finish cx");
		
		// curves						
		float[][] cx = new float[nSp][rgt00e-rgt00s+1];
		for(Entry<Integer,ArrayList<int[]>> entry:spLst.entrySet()) {
			int label = entry.getKey();
			ArrayList<int[]> points = entry.getValue();
			float[] l = new float[rgt00e-rgt00s+1];
			float min = Float.MAX_VALUE;	// normalization
			float max = -Float.MAX_VALUE;
			for(int t=0;t<l.length;t++) {
				float sum = 0;
				for(int[] p:points) {
					sum += dFip[p[0]][p[1]][t + rgt00s];
				}
				l[t] = sum/points.size();
				min = Math.min(min, l[t]);
				max = Math.max(max, l[t]);
			}
			for(int t=0;t<l.length;t++) {	// normalization
				l[t] = (l[t]-min)/(max-min);
			}
			cx[label-1] = l;
		}
		
		// time to achieve different levels for each seed
		float[] thrVec = new float[] {0.5f,0.55f,0.6f,0.65f,0.7f,0.75f,0.8f,0.85f,0.9f,0.95f};		// checked
		int[][] tAch = new int[nSp][thrVec.length];
		float[] tDly = new float[nSp];
		for(int n=0;n<nSp;n++) {
			float[] x = cx[n];				// n is index, n+1 is label
			int t0 = 0;
			float max = -Float.MAX_VALUE;
			float sum = 0;
			for(int t=0;t<x.length;t++) {
				if(x[t]>max) {
					max = x[t];
					t0 = t;
				}
			}
			for(int i=0;i<thrVec.length;i++) {
				int t1 = t0;
				for(int t=0;t<=t0;t++) {
					if(x[t]>= thrVec[i]) {
						t1 = t;
						break;
					}
				}
				sum	+= t1;
				tAch[n][i] = t1;
			}
			tDly[n] = sum/thrVec.length;
		}
		
		// get neighbor super pixel label				
		HashMap<Integer,HashSet<Integer>> pair = GTW.getNeighborSuperPixel(spLst,width,height);		// checked
		float[][] distMat = new float[nSp][nSp];
		for(int i=0;i<nSp;i++) {
			for(int j=0;j<nSp;j++) {
				distMat[i][j] = Float.MAX_VALUE;
			}
		}
		for(Entry<Integer,HashSet<Integer>> entry:pair.entrySet()) {								// checked
			int label1 = entry.getKey();
			HashSet<Integer> neighbors = entry.getValue();
			for(int label2:neighbors) {
				if(label2>label1) {
					float d0sum = 0;
					for(int i=0;i<thrVec.length;i++) {
						d0sum += tAch[label1-1][i] - tAch[label2-1][i];
					}
					float d0 = d0sum/thrVec.length;
					distMat[label1-1][label2-1] = Math.abs(d0);
					distMat[label2-1][label1-1] = Math.abs(d0);
				}
			}
		}
		
		// direction for each pair
//		int nPair = source.size();
//		float[][] distMat = new float[nSp][nSp];
//		for(int i=0;i<nSp;i++) {
//			for(int j=0;j<nSp;j++) {
//				distMat[i][j] = Float.MAX_VALUE;
//			}
//		}
//		for(int nn=0;nn<nPair;nn++) {
//			int s0 = source.get(nn);
//			int t0 = sink.get(nn);
//			float d0sum = 0;
//			for(int i=0;i<thrVec.length;i++) {
//				d0sum += tAch[s0-1][i] - tAch[t0-1][i];
//			}
//			float d0 = d0sum/thrVec.length;
//			distMat[s0-1][t0-1] = Math.abs(d0);
//			distMat[t0-1][s0-1] = Math.abs(d0);
//		}
		
		
		// delayMap
		float[][] dlyMap = new float[width][height];						// checked
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				dlyMap[i][j] = Float.MAX_VALUE;
			}
		}
		for(Entry<Integer, ArrayList<int[]>> entry:spLst.entrySet()) {		// checked
			ArrayList<int[]> points = entry.getValue();
			int label = entry.getKey();
			for(int[] p:points) {
				dlyMap[p[0]][p[1]] = tDly[label-1];
			}
		}
//		System.out.println("GTW");
		return new GTWResult(spLst, cx, dlyMap, distMat,rgt00s,rgt00e, isFail);
	}
	
	private static float[][][] warpRef2Tst(HashMap<Integer, float[][]> path0, float[] ref, HashMap<Integer, int[]> spSeedVec, int[][] validMap, int W,
			int H, int T) {
		float maxF = -Float.MAX_VALUE;
		for(int t=0;t<T;t++) {
			maxF = Math.max(maxF, ref[t]);
		}
		for(int t=0;t<T;t++) {
			ref[t] = ref[t]/maxF;
		}
		
		@SuppressWarnings("unused")
		int nPix = spSeedVec.size();
//		int[][] validMapx = new int[W][H];
//		int cnt = 1;
//		for(int i=0;i<W;i++) {
//			for(int j=0;j<H;j++) {
//				if(validMap[i][j]>0) {
//					validMapx[i][j] = cnt;
//					cnt++;
//				}
//			}
//		}
		
		float[][][] datWarp = new float[W][H][T];		// checked
		for(int i=0;i<W;i++) {
			for(int j=0;j<H;j++) {
				for(int k=0;k<T;k++) {
					datWarp[i][j][k] = Float.NaN;
				}
			}
		}
		
		for(int ww=0;ww<W;ww++) {
			for(int hh=0;hh<H;hh++) {
				if(validMap[ww][hh]>0) {
					float[] x0 = new float[T];
					for(int t=0;t<T;t++)
						x0[t] = Float.NaN;	// warped curve
					
					int[] c0 = new int[T];	// count the occurrence
					float[][] p0 = path0.get(validMap[ww][hh]);
					boolean[] idxValid = new boolean[p0.length];
					int cnt = 0;
					for(int i=0;i<p0.length;i++) {
						idxValid[i] = p0[i][0]>=0 && p0[i][0]<T && p0[i][1]>=0 && p0[i][1]<T;
						if(idxValid[i])
							cnt++;
					}
					float[][] p0new = new float[cnt][4];
					cnt = 0;
					for(int i=0;i<p0.length;i++) {
						if(idxValid[i]) {
							p0new[cnt] = p0[i];
							cnt++;
						}
					}
					p0 = p0new;
					for(int t=0;t<p0.length;t++) {
						int p_ref = (int) p0[t][0];
						int p_tst = (int) p0[t][1];
						if(!Float.isNaN(ref[p_ref])) {
							if(Float.isNaN(x0[p_tst]))
								x0[p_tst] = ref[p_ref];
							else
								x0[p_tst] = x0[p_tst] + ref[p_ref];
							
							c0[p_tst] = c0[p_tst] + 1;
						}
					}
					
//					// 
//					p0 = path0.get(validMap[ww][hh]);
//					cnt = 0;
//					for(int i=0;i<p0.length;i++) {
//						idxValid[i] = p0[i][2]>=0 && p0[i][2]<T && p0[i][3]>=0 && p0[i][3]<T;
//						cnt++;
//					}
//					p0new = new float[cnt][4];
//					cnt = 0;
//					for(int i=0;i<p0.length;i++) {
//						if(idxValid[i]) {
//							p0new[cnt] = p0[i];
//							cnt++;
//						}
//					}
//					p0 = p0new;
//					for(int i=0;i<p0.length;i++) {
//						int p_ref = (int) p0[i][2];
//						int p_tst = (int) p0[i][3];
//					}
					
					float[] result = new float[T];
					for(int t=0;t<T;t++) {
						if(c0[t]==0)
							c0[t] = 1;
						result[t] = x0[t]/c0[t];
					}
					datWarp[ww][hh] = result;
				}
			}
		}
		return datWarp;
		
	}

	private static HashMap<Integer, float[][]> label2path4Aosokin(boolean[] nodeLabel, BuildGTWGraphResult graph) {
		// label2path4Aosokin Convert label of src and sink to path in primal graph
		// For the graph representation of Aosokin's codes
		// Src and sink edges do not use explicit node names, but other edges do
//		ArrayList<Integer> ct = new ArrayList<>();
//		for(int i=0;i<nodeLabel.length;i++) {
//			if(nodeLabel[i])
//				ct.add(i);
//		}
		
		int nEdgeGrid = graph.nEdgeGrid;
		int nNodeGrid = graph.nNodeGrid;
		int nPix = graph.nPix;
		
		double[][] ss = graph.ss;
		double[][] ee = graph.ee;
		float[][] pEdgeSS = graph.pEdgeSS;
		float[][] pEdgeEE = graph.pEdgeEE;
		int[][] dEdgeIntSS = graph.dEdgeIntSS;
		
		// cut for within grid
		// not suitable do this pixel by pixel due to the spatial edge
		boolean[] isCutEE = new boolean[ee.length];
		for(int i=0;i<ee.length;i++) {
			isCutEE[i] = nodeLabel[(int) ee[i][0]]!=nodeLabel[(int) ee[i][1]];
		}
		
		// cut to mapping pattern in primal graph
		HashMap<Integer,float[][]> resPath = new HashMap<>();
		
		for(int i=0;i<nPix;i++) {
			// cuts within grid
			int idx = i*nEdgeGrid;
//			int cnt = 0;
			ArrayList<Integer> cutNow = new ArrayList<>();
			for(int k=0;k<nEdgeGrid;k++) {
				if(isCutEE[k+idx])
					cutNow.add(k);
			}
			
			// src and sink cuts
			idx = i*nNodeGrid;
			HashSet<Integer> idxSrcCut = new HashSet<>();
			for(int k=0;k<nNodeGrid;k++) {
				if(ss[k+idx][0]>=0 && !nodeLabel[k+idx]) {
					idxSrcCut.add(k);
				}
			}
			ArrayList<Integer> ia = new ArrayList<>();
			for(int k=0;k<dEdgeIntSS.length;k++) {
				if(idxSrcCut.contains(dEdgeIntSS[k][1])) {
					ia.add(k);
				}
			}
			
			HashSet<Integer> idxSinkCut = new HashSet<>();
			for(int k=0;k<nNodeGrid;k++) {
				if(ss[k+idx][1]>=0 && nodeLabel[k+idx]) {
					idxSinkCut.add(k);
				}
			}
			ArrayList<Integer> ib = new ArrayList<>();
			for(int k=0;k<dEdgeIntSS.length;k++) {
				if(idxSinkCut.contains(dEdgeIntSS[k][0])) {
					ib.add(k);
				}
			}
			
			// resPath 
			int cutNowNumber = cutNow.size();
			float[][] result = new float[cutNowNumber+ia.size()+ib.size()][4];
			for(int k=0;k<cutNowNumber;k++) {
				result[k] = pEdgeEE[cutNow.get(k)];
			}
			for(int k=0;k<ia.size();k++) {
				result[k+cutNowNumber] = pEdgeSS[ia.get(k)];
			}
			for(int k=0;k<ib.size();k++) {
				result[k+cutNowNumber+ia.size()] = pEdgeSS[ib.get(k)];
			}
			
			resPath.put(i+1, result);
		}
		
		return resPath;
	}
	
	private static BuildGTWGraphResult buildGTWGraph(float[][] ref, float[][] tst, ArrayList<Integer> s, ArrayList<Integer> t,
			float smoBase, int winSize, float[] s2) {
		int nNode = tst.length;
		int T = tst[0].length;
		winSize = Math.max(Math.min(winSize, T-1), 1);
		
		double capRev = 100000000;
		double[] pmCost = new double[winSize+1];
		for(int i=0;i<=winSize;i++) {
			pmCost[i] = i*capRev;
		}
		
		int nEdge = s.size();
		
		// template using coordinate
		TemplateResult tmpResult = buildPairTemplate(T,winSize,pmCost,0);
		float[][] pEdge = tmpResult.pEdge;
		float[][] dEdge = tmpResult.dEdge;
		int[][] weightPos = tmpResult.cPos;
		double[] weightVal = tmpResult.cVal;
		@SuppressWarnings("unused")
		int[] st01 = tmpResult.st01;
		
		
		// direction penalty, additive or multiplicative
		float[] weightValMul = new float[weightVal.length];
		for(int i=0;i<weightVal.length;i++) {
			weightValMul[i] = 1;
		}
		
		// recode coordinates to single number
		int numEdge = dEdge.length;
		Point[][] tmp = new Point[dEdge.length][2];
		HashSet<Point> tmpUniq = new HashSet<>();
//		float sc10 = 100000000;
		for(int i=0;i<dEdge.length;i++) {

			tmp[i][0] = new Point((int)(dEdge[i][0]*4),(int)(dEdge[i][1]*4));
			tmp[i][1] = new Point((int)(dEdge[i][2]*4),(int)(dEdge[i][3]*4));
			tmpUniq.add(tmp[i][0]);
			tmpUniq.add(tmp[i][1]);
		}
		int nNodeGrid = tmpUniq.size()-2;	// number of nodes in each pair
		int srcNode = nNodeGrid*nNode ;	// id for source node
		int sinkNode = nNodeGrid*nNode + 1;	// id for sink node
		
		HashMap<Point,Integer> mapObj = new HashMap<>();
		ArrayList<Point> tmpUniqArray = new ArrayList<>(tmpUniq);
		tmpUniqArray.sort(new Comparator<Point>() {
			public int compare(Point o1, Point o2) {
				if(o1.getX()<o2.getX())
					return -1;
				else if (o1.getX()>o2.getX())
					return 1;
				else {
					if(o1.getY()<o2.getY())
						return -1;
					else
						return 1;
				}
			}
			
		});
		mapObj.put(tmpUniqArray.get(0), sinkNode);
		for(int i=1;i<tmpUniqArray.size()-1;i++) {
			mapObj.put(tmpUniqArray.get(i), i-1);
		}
		mapObj.put(tmpUniqArray.get(tmpUniqArray.size()-1), srcNode);
		int[][] dEdgeInt = new int[numEdge][2];
		for(int i=0;i<numEdge;i++) {
			dEdgeInt[i][0] = mapObj.get(tmp[i][0]);
			dEdgeInt[i][1] = mapObj.get(tmp[i][1]);
		}
		
		// split the edge matrix to src/sink and within grid
		// nodes connected with src or sink
		ArrayList<Integer> idxSrc = new ArrayList<>();
		ArrayList<Integer> idxSink = new ArrayList<>();
		for(int i=0;i<numEdge;i++) {
			if(dEdgeInt[i][0]==srcNode)
				idxSrc.add(i);
			if(dEdgeInt[i][1]==sinkNode)
				idxSink.add(i);
		}
		
		// extra weight for ss edges
		double[][] ssTmpWt = new double[nNodeGrid][2];			// checked
		for(int i:idxSrc) {
			ssTmpWt[dEdgeInt[i][1]][0] = weightVal[i];
		}
		for(int i:idxSink) {
			ssTmpWt[dEdgeInt[i][0]][1] = weightVal[i];
		}
		
		// weight from curve distance for ss edges				// checked
		int[] wtPos1 = new int[numEdge];
		for(int i=0;i<numEdge;i++) {
			wtPos1[i] = weightPos[i][0] + weightPos[i][1]*T;
		}
		int[][] ssTmpPos = new int[nNodeGrid][2];
		for(int i=0;i<nNodeGrid;i++) {
			ssTmpPos[i][0] = T*T;
			ssTmpPos[i][1] = T*T;
		}
		for(int i:idxSrc) {
			ssTmpPos[dEdgeInt[i][1]][0] = wtPos1[i];
		}
		for(int i:idxSink) {
			ssTmpPos[dEdgeInt[i][0]][1] = wtPos1[i];
		}
		
		// edges between nodes (except src and sink)		// checked
		boolean[] idxNotSS = new boolean[numEdge];
		int nEdgeGrid = 0;
		for(int i=0;i<numEdge;i++) {
			idxNotSS[i] = (dEdgeInt[i][0]!=srcNode) && (dEdgeInt[i][1]!=sinkNode);
			if(idxNotSS[i])
				nEdgeGrid++;
		}
		int[] idxTable = new int[nEdgeGrid];
		int cnt = 0;
		for(int i=0;i<numEdge;i++) {
			if(idxNotSS[i]) {
				idxTable[cnt] = i;
				cnt++;
			}
		}
//		float[][] eeTmp = new float[nEdgeGrid][5];
//		cnt = 0;
//		for(int i=0;i<numEdge;i++) {
//			if(idxNotSS[i]) {
//				eeTmp[cnt][0] = dEdgeInt[i][0];
//				eeTmp[cnt][1] = dEdgeInt[i][1];
//				eeTmp[cnt][2] = wtPos1[i];
//				eeTmp[cnt][3] = weightVal[i];
//				eeTmp[cnt][4] = weightValMul[i];
//				cnt++;
//			}
//		}
		
		// output, for label and path mapping
		float[][] pEdgeSS = new float[numEdge - nEdgeGrid][4];
		float[][] pEdgeEE = new float[nEdgeGrid][4];
		int[][] dEdgeIntSS = new int[numEdge - nEdgeGrid][2];
		int[][] dEdgeIntEE = new int[nEdgeGrid][2];
		int cnt1 = 0;
		int cnt2 = 0;
		for(int i=0;i<numEdge;i++) {
			if(idxNotSS[i]) {
				pEdgeEE[cnt1] = pEdge[i];
				dEdgeIntEE[cnt1] = dEdgeInt[i];
				cnt1++;
			}else {
				pEdgeSS[cnt2] = pEdge[i];
				dEdgeIntSS[cnt2] = dEdgeInt[i];
				cnt2++;
			}
		}
		
		// edge for within pairs, using integer code
		double[][] ssPair = new double[nNode*nNodeGrid][2];
		double[][] eePair = new double[nNode*nEdgeGrid][4];
		for(int i=0;i<nNode*nEdgeGrid;i++) {
			eePair[i][3] = (int) capRev;
		}
		
		for(int i=0;i<nNode;i++) {
			float s2x = s2[i];
			int eeOfst = i*nEdgeGrid;
			int ssOfst = i*nNodeGrid;
			
			// position (1,T+1) means not using distance matrix
			float[][] d0ext = getDisMat(ref[i],tst[i],s2x);
			
			// edges from src and to sink
			for(int k=0;k<nNodeGrid;k++) {
				int x0 = ssTmpPos[k][0]%T;
				int y0 = ssTmpPos[k][0]/T;
				int x1 = ssTmpPos[k][1]%T;
				int y1 = ssTmpPos[k][1]/T;
				ssPair[ssOfst+k][0] = d0ext[x0][y0] + ssTmpWt[k][0];
				ssPair[ssOfst+k][1] = d0ext[x1][y1] + ssTmpWt[k][1];
			}
			
			// edges between nodes
			for(int k=0;k<nEdgeGrid;k++) {
				int index = idxTable[k];
				eePair[k+eeOfst][0] = dEdgeInt[index][0]+ ssOfst;
				eePair[k+eeOfst][1] = dEdgeInt[index][1]+ ssOfst;
				int x = wtPos1[index]%T;
				int y = wtPos1[index]/T;
				eePair[k+eeOfst][2] = (d0ext[x][y] + weightVal[index])*weightValMul[index];
			}
		}
		
		// edges for between pairs
		double[][] eeSpa = new double[nNodeGrid*nEdge][4];
		int nn = 0;
		for(int i=0;i<nEdge;i++) {
			int nowIdx = s.get(i);
			int tgtIdx = t.get(i);
			for(int k=0;k<nNodeGrid;k++) {
				eeSpa[nn+k][0] = (nowIdx-1)*nNodeGrid + k;
				eeSpa[nn+k][1] = (tgtIdx-1)*nNodeGrid + k;
				eeSpa[nn+k][2] = smoBase;
				eeSpa[nn+k][3] = smoBase;
			}
			nn = nn+nNodeGrid;
		}
		
		double[][] ss = ssPair;
		double[][] ee = new double[eePair.length + eeSpa.length][4];
		if(smoBase>0) {
			for(int i=0;i<eePair.length;i++)
				ee[i] = eePair[i];
			
			for(int i=0;i<eeSpa.length;i++)
				ee[i+eePair.length] = eeSpa[i];
		}else
			ee = eePair;
		
		// output
		
		return new BuildGTWGraphResult(ss,ee,nNodeGrid,nEdgeGrid,nNode,pEdgeSS,pEdgeEE,dEdgeIntSS);
	}
	
	static class BuildGTWGraphResult{
		double[][] ss = null;
		double[][] ee = null;
		int nNodeGrid = 0;
		int nEdgeGrid = 0;
		int nPix = 0;
		float[][] pEdgeSS = null;
		float[][] pEdgeEE = null;
		int[][] dEdgeIntSS = null;
		
		public BuildGTWGraphResult(double[][] ss, double[][] ee, int nNodeGrid, int nEdgeGrid, int nPix, float[][] pEdgeSS, float[][] pEdgeEE, int[][] dEdgeIntSS) {
			this.ss = ss;
			this.ee = ee;
			this.nNodeGrid = nNodeGrid;
			this.nEdgeGrid = nEdgeGrid;
			this.nPix = nPix;
			this.pEdgeSS = pEdgeSS;
			this.pEdgeEE = pEdgeEE;
			this.dEdgeIntSS = dEdgeIntSS;
		}
	}

	private static float[][] getDisMat(float[] ref, float[] tst, float s2x) {
		int T = ref.length;
		float[][] d0 = new float[T][T+1];
		for(int i=0;i<T;i++) {
			for(int j=0;j<T;j++) {
				float d0tmp = (ref[i] - tst[j])*(ref[i] - tst[j])/s2x;
				d0[i][j] = d0tmp;
			}
		}
		return d0;
	}

	private static TemplateResult buildPairTemplate(int T, int winSize, double[] pmCost, int VisMe) {
		// s0 = [-1,-1];
		// t0 = [T,T];
		// s1 = [T,-1];
		// t1 = [-1,T];
		int[] st01 = new int[] {-1,-1,T,T,T,-1,-1,T};
		// diagnal, sub diagonals, extra edges to s and t, vertical and horizontal edges
		// all in two directions, the reverse is infinite, but we ONLY build finite portion here
		int nEdges = T-1 + (T-2 + T-winSize)*(winSize-1) + 2*(2*winSize-1) + 2*(T-1 + T-winSize+1)*(winSize-1);
		
		// primal and dual edges as well as weights in coordinate form
		// node1 -> node2: (x,y) for node1, (x,y) for node 2
		// (x,y) for weight matrix position, (1,T+1) means none exist position
		float[][] pEdge = new float[nEdges][4];
		float[][] dEdge = new float[nEdges][4];
		int[][] cPos = new int[nEdges][2];
		for(int i=0;i<nEdges;i++) {
			cPos[i][0] = 0;
			cPos[i][1] = T;			
		}
		double[] cVal = new double[nEdges];
		int[] eType = new int[nEdges];
		
		// assign edges
		// loop through all nodes in primal graph, get edges to the right, top or top-right
		// save each edge, and the corresponding dual edge, as well as the position in the weighting matrix
		// edges outside the window is not included
		
		// from s to nodes
		pEdge[0] = new float[]{-1,-1,0,0};
		dEdge[0] = new float[] {0.5f,-0.5f,-0.5f,0.5f};
		int nn = 1;
		for(int w = 1;w<winSize;w++) {
			pEdge[nn] = new float[] {-1,-1,0,w};	// close to Top left
			pEdge[nn+1] = new float[] {-1,-1,w,0};	// close to Bottom Right
			if(w == winSize-1) {
				dEdge[nn] = new float[] {-0.5f,w-0.5f,-1,T};
				dEdge[nn+1] = new float[] {T,-1,w-0.5f,-0.5f};
			}else {
				dEdge[nn] = new float[] {-0.5f,w-0.5f,-0.5f,w+0.5f};
				dEdge[nn+1] = new float[] {w+0.5f,-0.5f,w-0.5f,-0.5f};
			}
			eType[nn] = 3;
			eType[nn+1] = 3;
			cVal[nn] = pmCost[w];
			cVal[nn+1] = pmCost[w];
			nn = nn+2;
		}
		
		// within grid
		for(int x0=0;x0<T;x0++) {
			for(int y0=0;y0<T;y0++) {
				if(x0==T-1&&y0==T-1)
					continue;
				if(y0<(x0-winSize+1) || y0>(x0+winSize-1))			// unsure
					continue;
				
				int x = x0;
				int y = y0+1;	// top
				if(y>(x-winSize) && y<(x+winSize) && x<T && y<T) {
					pEdge[nn] = new float[]{x0,y0,x,y};
					cPos[nn] = new int[] {x0,y0};
					if(x0==0) {
						dEdge[nn] = new float[] {x0+0.25f,y0+0.75f,-0.5f,y0+0.5f};
					}else if (x0==T-1) {
						dEdge[nn] = new float[] {T-0.5f,y0+0.5f,x0-0.25f,y0+0.25f};
					}else
						dEdge[nn] = new float[] {x0+0.25f,y0+0.75f,x0-0.25f,y0+0.25f};
					
					eType[nn] = 2;
					nn++;
				}
				x = x0+1;
				y = y0;			//right
				if(y>(x-winSize) && y<(x+winSize) && x<T && y<T) {
					pEdge[nn] = new float[]{x0,y0,x,y};
					cPos[nn] = new int[] {x0,y0};
					if(y0==0) {
						dEdge[nn] = new float[] {x0+0.5f,-0.5f,x0+0.75f,y0+0.25f};
					}else if (y0==T-1) {
						dEdge[nn] = new float[] {x0+0.25f,y0-0.25f,x0+0.5f,T-0.5f};
					}else
						dEdge[nn] = new float[] {x0+0.25f,y0-0.25f,x0+0.75f,y0+0.25f};
					
					eType[nn] = 2;
					nn++;
				}
				
				x = x0+1;
				y = y0+1;		// top right (diagonal)
				if(y>(x-winSize) && y<(x+winSize) && x<T && y<T) {
					pEdge[nn] = new float[]{x0,y0,x,y};
					cPos[nn] = new int[] {x0,y0};
					if(y==x+winSize-1) {
						dEdge[nn] = new float[] {x0+0.75f,y0+0.25f,-1,T};
					}else if (y==x-winSize+1) {
						dEdge[nn] = new float[] {T,-1,x0+0.25f,y0+0.75f};
					}else
						dEdge[nn] = new float[] {x0+0.75f,y0+0.25f,x0+0.25f,y0+0.75f};
					
					eType[nn] = 1;
					nn++;
				}
			}
		}
		
		// from nodes to t
		pEdge[nn] = new float[] {T-1,T-1,T,T};
		dEdge[nn] = new float[] {T-0.5f,T-1.5f,T-1.5f,T-0.5f};
		cPos[nn] = new int[] {T-1,T-1};
		nn++;
		for(int w=1;w<winSize;w++) {
			pEdge[nn] = new float[] {T-1-w,T-1,T,T};			// close to Top left
			pEdge[nn+1] = new float[] {T-1,T-1-w,T,T};			// close to Bottom right
			if(w==winSize-1) {
				dEdge[nn] = new float[] {T-0.5f-w,T-0.5f,-1,T};
				dEdge[nn+1] = new float[] {T,-1,T-0.5f,T-0.5f-w};
			}else {
				dEdge[nn] = new float[] {T-0.5f-w,T-0.5f,T-1.5f-w,T-0.5f};
				dEdge[nn+1] = new float[] {T-0.5f,T-1.5f-w,T-0.5f,T-0.5f-w};
			}
			cPos[nn] = new int[] {T-1-w,T-1};
			cPos[nn+1] = new int[] {T-1,T-1-w};
			cVal[nn] = pmCost[w];
			cVal[nn+1] = pmCost[w];
			eType[nn] = 1;
			eType[nn+1] = 1;
			nn = nn+2;
		}
		
		return new TemplateResult(pEdge,dEdge,cPos,cVal,st01,eType);
	}
	
	static class TemplateResult{
		float[][] pEdge = null;
		float[][] dEdge = null;
		int[][] cPos = null;
		double[] cVal = null;
		int[] st01 = null;
		int[] eType = null;
		
		public TemplateResult(float[][] pEdge, float[][] dEdge, int[][] cPos, double[] cVal, int[] st01, int[] eType) {
			this.pEdge = pEdge;
			this.dEdge = dEdge;
			this.cPos = cPos;
			this.cVal = cVal;
			this.st01 = st01;
			this.eType = eType;
		}
	}
	
	private static Sp2GraphResult sp2graph(float[][][] dFip, boolean[][] validMap, HashMap<Integer, ArrayList<int[]>> spLst,
			int[] seedIn, int gapSeedHW, int rgt00s, int rgt00e) {
		int W = dFip.length;
		int H = dFip[0].length;
		int T = rgt00e - rgt00s+1;
		
		@SuppressWarnings("unused")
		float[][] dt0 = new float[W][H];
		int iw = 0;
		int ih = 0;
		
		int maxD = Integer.MIN_VALUE;
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				int d = bwdist(validMap,x,y);
				if(d>maxD) {
					maxD = d;
					iw = x;
					ih = y;
				}
			}
		}
		
		int rgws = Math.max(iw-gapSeedHW, 0);
		int rgwe = Math.min(iw+gapSeedHW, W-1);
		int rghs = Math.max(ih-gapSeedHW, 0);
		int rghe = Math.min(ih+gapSeedHW, H-1);
		
		float[] refBase = new float[T];
		for(int k=rgt00s;k<=rgt00e;k++) {
			float sum = 0;
			int cnt = 0;
			for(int i = rgws;i<=rgwe;i++) {
				for(int j=rghs;j<=rghe;j++) {
					if(validMap[i][j] && !Float.isNaN(dFip[i][j][k])) {
						sum += dFip[i][j][k];
						cnt++;
					}
				}
			}
			if(cnt==0)
				refBase[k-rgt00s] = 0;
			else
				refBase[k-rgt00s] = sum/cnt;
		}
		
		float minBase = Float.MAX_VALUE;
		for(int t=0;t<T;t++) {
			minBase = Math.min(minBase, refBase[t]);
		}
		float maxBase = -Float.MAX_VALUE;
		int ix = 0;
		for(int t=0;t<T;t++) {
			refBase[t] = refBase[t] - minBase;
			if(refBase[t]>maxBase) {
				maxBase = refBase[t];
				ix = t;
			}
		}
		
		// imimposemin
		float minR = maxBase;
		for(int t=ix-1;t>=0;t--) {
			if(refBase[t]>minR)
				refBase[t] = minR;
			else
				minR = refBase[t];
		}
		minR = maxBase;
		for(int t=ix+1;t<T;t++) {
			if(refBase[t]>minR)
				refBase[t] = minR;
			else
				minR = refBase[t];
		}
		
		int nSp = spLst.size();
		// tst
		// ref
		float[][] tst = new float[nSp][T];
		float[][] ref = new float[nSp][T];
		for(int i=1;i<=nSp;i++) {
			ArrayList<int[]> points = spLst.get(i);
			float[] tst0Smo = new float[T];
			float[] tst0 = new float[T];
			for(int t=rgt00s;t<=rgt00e;t++) {
				float sum = 0;
				int cnt = 0;
				for(int[] p:points) {
					if(!Float.isNaN(dFip[p[0]][p[1]][t])) {
						cnt++;
						sum += dFip[p[0]][p[1]][t];
					}
				}
				if(cnt==0)
					tst0Smo[t-rgt00s] = 0;
				else
					tst0Smo[t-rgt00s] = sum/cnt;
				tst0[t-rgt00s] = tst0Smo[t-rgt00s];
			}
			
			float mintst = Float.MAX_VALUE;
			for(int t=0;t<T;t++) {
				mintst = Math.min(mintst, tst0Smo[t]);
			}
			float maxtst = -Float.MAX_VALUE;
			for(int t=0;t<T;t++) {
				tst0Smo[t] = tst0Smo[t] - mintst;
				maxtst = Math.max(maxtst, tst0Smo[t]);
			}
			float k0 = maxtst/maxBase;
			
			// Just for test, blur
			float[][] tstForBlur = new float[1][T];
			tstForBlur[0] = tst0;
			ImageProcessor blurer = new FloatProcessor(tstForBlur);
			blurer.blurGaussian(1);	// gauss
			tstForBlur = blurer.getFloatArray();
			
			float[] ref0 = new float[T];
			
			mintst = Float.MAX_VALUE;
			for(int t=0;t<T;t++) {
				mintst = Math.min(mintst, tstForBlur[0][t]);
			}
			for(int t=0;t<T;t++) {
				tst0[t] = tst0[t] - mintst;
				ref0[t] = refBase[t]*k0;
			}
			tst[i-1] = tst0;
			ref[i-1] = ref0;
		}
		
		// var
		boolean[] idxGood = new boolean[nSp];
		int goodNumber = 0;
		for(int i = 0;i<nSp;i++) {
			float[] tst0 = tst[i];
			float sum = 0;
			for(int t=0;t<T;t++) {
				sum += tst0[t];
			}
			float mean = sum/T;
			float var = 0;
			for(int t=0;t<T;t++) {
				var += (tst0[t]-mean)*(tst0[t]-mean);
			}
			idxGood[i] = var/(T-1)>0.0000000001f;
			if(idxGood[i])
				goodNumber++;
		}
		
		HashMap<Integer, ArrayList<int[]>> spLstNew = new HashMap<>();
		float[][] tstNew = new float[goodNumber][T];
		float[][] refNew = new float[goodNumber][T];
		
		int cnt = 0;
		for(int i=0;i<nSp;i++) {
			if(idxGood[i]) {
				cnt++;
				spLstNew.put(cnt, spLst.get(i+1));
				tstNew[cnt-1] = tst[i];
				refNew[cnt-1] = ref[i];
			}
		}
		tst = tstNew;
		ref = refNew;
		spLst = spLstNew;
		
		// graph, at most one pair between two nodes
		ArrayList<Integer> s = new ArrayList<>();
		ArrayList<Integer> t = new ArrayList<>();
//		int nPair = 0;
		int[] dh = new int[] {0,-1,1,0};
		int[] dw = new int[] {-1,0,0,1};
		int[][] spMap1 = new int[W][H];
		
		for(int i=1;i<=spLst.size();i++) {
			ArrayList<int[]> points = spLst.get(i);
			for(int[] p:points) {
				spMap1[p[0]][p[1]] = i;
			}
		}
		
		for(int i=1;i<=spLst.size();i++) {
			ArrayList<int[]> points = spLst.get(i);
			HashSet<Integer> neib0 = new HashSet<>();
			for(int j=0;j<dh.length;j++) {
				ArrayList<int[]> ihw = new ArrayList<>();
				for(int[] p:points) {
					int x = p[0] + dw[j];
					int y = p[1] + dh[j];
					if(x>=0 && x<W && y>=0 && y<H)
						ihw.add(new int[] {x,y});
				}
				if(ihw.size()>0) {
					HashSet<Integer> newMap = new HashSet<>();
					for(int[] p:ihw) {
						if(spMap1[p[0]][p[1]]>i)
							newMap.add(spMap1[p[0]][p[1]]);
					}
					newMap.removeAll(neib0);
					neib0.addAll(newMap);
					for(int label:newMap) {
//						nPair++;
						s.add(i);									// label, not index
						t.add(label);
					}
				}
			}
		}
		
		return new Sp2GraphResult(ref,tst,refBase,s,t,idxGood);
	}
	
	static class Sp2GraphResult{
		float[][] ref = null;
		float[][] tst = null;
		float[] refBase = null;
		ArrayList<Integer> s = null;
		ArrayList<Integer> t = null;
		boolean[] idxGood = null;
		
		public Sp2GraphResult(float[][] ref, float[][] tst, float[] refBase, ArrayList<Integer> s, ArrayList<Integer> t, boolean[] idxGood) {
			this.ref = ref;
			this.tst = tst;
			this.refBase = refBase;
			this.s = s;
			this.t = t;
			this.idxGood = idxGood;
		}
		
		
	}

	public static int bwdist(boolean[][] validMap, int x, int y) {
		if(!validMap[x][y])
			return 0;
		
		int W = validMap.length;
		int H = validMap[0].length;
		
		for(int i=1;i<=Math.max(W, H);i++) {
			int d = i/2;
			for(int j = 0;j<=d;j++) {
				int d1 = j;
				int d2 = i-j;
				if(x-d1>=0&&y-d2>=0&&!validMap[x-d1][y-d2]) {			// -d1 -d2
					return i;
				}
				if(x-d2>=0&&y-d1>=0&&!validMap[x-d2][y-d1]) {			// -d2 -d1
					return i;
				}
				if(x-d2>=0&&y+d1<H&&!validMap[x-d2][y+d1]) {				// -d2 +d1
					return i;
				}
				if(x-d1>=0&&y+d2<H&&!validMap[x-d1][y+d2]) {				// -d1 +d2
					return i;
				}
				if(x+d1<W&&y-d2>=0&&!validMap[x+d1][y-d2]) {				// +d1 -d2
					return i;
				}
				if(x+d2<W&&y-d1>=0&&!validMap[x+d2][y-d1]) {				// +d2 -d1
					return i;
				}
				if(x+d2<W&&y+d1<H&&!validMap[x+d2][y+d1]) {				// +d2 +d1
					return i;
				}
				if(x+d1<W&&y+d2<H&&!validMap[x+d1][y+d2]) {				// +d2 +d1
					return i;
				}
			}
		}
		
		
		
		
		
		return Integer.MAX_VALUE;
	}

	private static float getMedian(ArrayList<Float> dFM) {
		Collections.sort(dFM, new Comparator<Float>() {
			@Override
			public int compare(Float e1, Float e2) {
				if(e1<e2)
					return -1;
				else if (e1>e2)
					return 1;
				else
					return 0;
			}				
		});
		float result = 0;
		int len = dFM.size();
		if(len%2==0)
			result = (dFM.get(len/2-1) +  dFM.get(len/2))/2;
		else
			result = dFM.get(len/2);
		
		return result;
	}
	
	private static float getMedian(float[] s) {
		ArrayList<Float> dFM = new ArrayList<>();
		for(int i=0;i<s.length;i++)
			dFM.add(s[i]);
		return getMedian(dFM);
	}
	
	private static float getDfNoise(float[][][] dF, int rgws, int rgwe, int rghs, int rghe, int rgts, int rgte) {

		ArrayList<Float> dFDif = new ArrayList<>();
		for(int k=rgts;k<rgte;k++) {
			for(int i=rgws;i<=rgwe;i++) {
				for(int j=rghs;j<=rghe;j++) {
					float dif = (dF[i][j][k] - dF[i][j][k+1])*(dF[i][j][k] - dF[i][j][k+1]);
					dFDif.add(dif);
				}
			}
		}
		Collections.sort(dFDif, new Comparator<Float>() {
			@Override
			public int compare(Float e1, Float e2) {
				if(e1<e2)
					return -1;
				else if (e1>e2)
					return 1;
				else
					return 0;
			}				
		});
		float result = 0;
		int len = dFDif.size();
		if(len%2==0)
			result = (dFDif.get(len/2-1) +  dFDif.get(len/2))/2;
		else
			result = dFDif.get(len/2);
		
		result = (float) Math.sqrt(result/0.9113);
		return result;
	}
	
}



