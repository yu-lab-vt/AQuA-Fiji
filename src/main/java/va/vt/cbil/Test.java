package va.vt.cbil;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Map.Entry;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import va.vt.cbil.GTW.BuildGTWGraphResult;
import va.vt.cbil.GTW.Mov2spResult;
import va.vt.cbil.GTW.Sp2GraphResult;
import va.vt.cbil.GTW2.TemplateResult;
import va.vt.cbil.ProgressBarRealizedStep3.EvtGrowResult;
import va.vt.cbil.ProgressBarRealizedStep3.RiseMapResult;
import va.vt.cbil.ProgressBarRealizedStep3.RiseNode;
import va.vt.cbil.ProgressBarRealizedStep3.RiseXX;

import java.util.Random;


public class Test{
	public static void main(String[]args) throws FileNotFoundException{
		step3test2();
		
		
		
		
		
	}
	
	
	public static void step7test1() {
		float[][][] datOrg = null;
		int[][][] datL = null;
		String proPath = "D:\\New Folder\\";
		int[][][] datR = null;
		float[][][] dat = null;
		try {
			// data in step1
			FileInputStream fi1 = null;
			ObjectInputStream oi1 = null;
			
			fi1 = new FileInputStream(new File(proPath + "DataOrg.txt"));
			oi1 = new ObjectInputStream(fi1);
			datOrg = (float[][][])oi1.readObject();
			oi1.close();
			fi1.close();
			
			fi1 = new FileInputStream(new File(proPath + "Data.txt"));
			oi1 = new ObjectInputStream(fi1);
			dat = (float[][][])oi1.readObject();
			oi1.close();
			fi1.close();
			
			fi1 = new FileInputStream(new File(proPath + "datL.txt"));
			oi1 = new ObjectInputStream(fi1);
			datL = (int[][][])oi1.readObject();
			oi1.close();
			fi1.close();
			
			fi1 = new FileInputStream(new File(proPath + "datR.txt"));
			oi1 = new ObjectInputStream(fi1);
			datR = (int[][][])oi1.readObject();
			oi1.close();
			fi1.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		HashMap<Integer, ArrayList<int[]>> evtLst = ProgressBarRealizedStep3.label2idx(datL);
		Opts opts = new Opts(1);
		ProgressBarRealizedStep7 s7 = new ProgressBarRealizedStep7();
		FeatureTopResult featureTopResult = s7.getFeaturesTop(datOrg, evtLst, datL, opts);
		FtsLst ftsLstE = featureTopResult.ftsLst;
		
		s7.getFeaturesProTop(dat, datR, evtLst, ftsLstE, opts);
		
		int W = datR.length;
		int H = datR[0].length;
		int T = datR[0][0].length;
		float muPerPix = opts.spatialRes;
		
		boolean[][] evtSpatialMask = new boolean[W][H];
		int[][] regionLabel = new int[W][H];
		for(int x=50;x<100;x++) {
			for(int y=50;y<100;y++) {
				regionLabel[x][y] = 1;
				evtSpatialMask[x][y] = true;
			}
		}
		for(int x=150;x<200;x++) {
			for(int y=150;y<200;y++) {
				regionLabel[x][y] = 2;
				evtSpatialMask[x][y] = true;
			}
		}
		HashMap<Integer, ArrayList<int[]>> regLst = new HashMap<>();
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				int label = regionLabel[x][y];
				if(label>0) {
					ArrayList<int[]> l = regLst.get(label);
					if(l == null)
						l = new ArrayList<>();
					
					l.add(new int[] {x,y});
					regLst.put(label, l);
				}
			}
		}
		
		boolean[][] landMark = new boolean[W][H];
		for(int x=30;x<60;x++) {
			for(int y=30;y<60;y++) {
				landMark[x][y] = true;
			}
		}
		for(int x=180;x<200;x++) {
			for(int y=180;y<200;y++) {
				landMark[x][y] = true;
			}
		}
		HashMap<Integer, ArrayList<int[]>> lmkLst = ConnectedComponents.twoPassConnect2D(landMark);
		System.out.println(lmkLst.size());
		// LandMark Features
		if(regLst.size()>0 || lmkLst.size()>0) {
			System.out.println("Updating region and landmark features ...");
			ftsLstE.region = s7.getDistRegionBorderMIMO(evtLst,datR,regLst,lmkLst,muPerPix,opts.minShow1);
		}
		
		// update network feature
		HashMap<Integer, ArrayList<int[]>> evtx1 = new HashMap<>();
		for(int i=1;i<=evtLst.size();i++) {
			evtx1.put(i, evtLst.get(i));
		}
		
		if(regLst.size()>0) {
			for(int i=1;i<=evtLst.size();i++) {
				ArrayList<int[]> loc00 = evtLst.get(i);
				boolean inclu = false;
				for(int[] p:loc00) {
					if(evtSpatialMask[p[0]][p[1]]) {
						inclu = true;
						break;
					}
				}
				if(!inclu) {
					evtx1.put(i, new ArrayList<>());
				}
			}
		}
		
		System.out.println("Output Network");
		ftsLstE.networkAll = s7.getEvtNetworkFeatures(evtLst,W,H,T);
		ftsLstE.network = s7.getEvtNetworkFeatures(evtx1,W,H,T);
	}
	
	public static void step3test3() {
		float[][][] dF = null;;
		String proPath = "D:\\New Folder212\\";
		HashMap<Integer,ArrayList<int[]>> spLst = null;
		float[][] distMat = null;
		float[][] dlyMap = null;
		float[][] cx = null;
		int[][] evtMap = null;
		int[][][] seMap = null;
		float[][][] dat = null;
		try {
			// data in step1
			FileInputStream fi1 = null;
			ObjectInputStream oi1 = null;
			
			fi1 = new FileInputStream(new File(proPath + "DF.ser"));
			oi1 = new ObjectInputStream(fi1);
			dF = (float[][][])oi1.readObject();	
			oi1.close();
			fi1.close();
			
			fi1 = new FileInputStream(new File(proPath + "Data.ser"));
			oi1 = new ObjectInputStream(fi1);
			dat = (float[][][])oi1.readObject();
			oi1.close();
			fi1.close();
			
			
			fi1 = new FileInputStream(new File(proPath + "spLst.txt"));
			oi1 = new ObjectInputStream(fi1);
			spLst = (HashMap<Integer,ArrayList<int[]>> )oi1.readObject();	
			oi1.close();
			fi1.close();
			
			fi1 = new FileInputStream(new File(proPath + "distMat.txt"));
			oi1 = new ObjectInputStream(fi1);
			distMat = (float[][])oi1.readObject();	
			oi1.close();
			fi1.close();
			
			fi1 = new FileInputStream(new File(proPath + "dlyMap.txt"));
			oi1 = new ObjectInputStream(fi1);
			dlyMap = (float[][])oi1.readObject();	
			oi1.close();
			fi1.close();
			
			fi1 = new FileInputStream(new File(proPath + "cx.txt"));
			oi1 = new ObjectInputStream(fi1);
			cx = (float[][])oi1.readObject();	
			oi1.close();
			fi1.close();
			
			fi1 = new FileInputStream(new File(proPath + "evtMap.txt"));
			oi1 = new ObjectInputStream(fi1);
			evtMap = (int[][])oi1.readObject();	
			oi1.close();
			fi1.close();
			
			fi1 = new FileInputStream(new File(proPath + "seMap.ser"));
			oi1 = new ObjectInputStream(fi1);
			seMap = (int[][][])oi1.readObject();	
			oi1.close();
			fi1.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		int rgtSels = 0;
		int rgtSele = 30;
		boolean xFail = false;
		int maxStp = 11;		// 11
		int maxRiseUnc = 1;	// 1
		int cDelay = 5;		// 5
		int label = 1;
		int it0s = 1;
		int it0e = 30;
		int rgts = 0;
		int rgte = 30;
		int rgws = 96;
		int rgwe = 349;
		int rghs = 259;
		int rghe = 499;
		int rgtxs = 1;
		int rgtxe = 30;
		
		int nFind = 0;																	// checked
		for(int x=0;x<evtMap.length;x++) {
			for(int y=0;y<evtMap[0].length;y++) {
				nFind = Math.max(nFind, evtMap[x][y]);
			}
		}
		
		System.out.println("Found " + nFind + " Events");
		
		float[][] cx1 = new float[spLst.size()][it0e-it0s+1]; 							// checked		
		for(int i=0;i<cx.length;i++) {
			for(int t=0;t<it0e-it0s+1;t++) {
				cx1[i][t] = cx[i][t+it0s-rgts-rgtSels];
			}
		}
		HashMap<Integer,RiseNode> riseLst = new HashMap<>();
		int[][][] datR = new int[501][500][31];
		int[][][] datL = new int[501][500][31];
		int nEvt = 0;
		// Events
		float minShow = 0;																// checked
		if(true) {
			minShow = (float) Math.sqrt(0.2f);
		}else {
			minShow = 0.2f;
		}
		
		EvtReconResult evtReconResult = GTW.evtRecon(spLst, cx1, evtMap, minShow);		// checked
		int[][][] evtL = evtReconResult.evtL;
		float[][][] evtRecon = evtReconResult.evtRecon;
					
		
		
		
		
		
		int nEvt0 = Integer.MIN_VALUE;
		for(int i=0;i<evtRecon.length;i++) {
			for(int j=0;j<evtRecon[0].length;j++) {
				for(int k=0;k<evtRecon[0][0].length;k++) {
					evtRecon[i][j][k] = (int)Math.round(evtRecon[i][j][k]*evtRecon[i][j][k]*255);
					evtRecon[i][j][k] = Math.min(evtRecon[i][j][k], 255);
					nEvt0 = Math.max(nEvt0, evtL[i][j][k]);
				}
			}
		}
		
		// TODO: delete
		try {
			FileOutputStream f = null;
			ObjectOutputStream o = null;
			
			f = new FileOutputStream(new File("D:\\New Folder212\\" + "evtRecon.txt"));
			o = new ObjectOutputStream(f);
			o.writeObject(evtRecon);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File("D:\\New Folder212\\" + "evtL.txt"));
			o = new ObjectOutputStream(f);
			o.writeObject(evtL);
			o.close();
			f.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		System.out.println(nEvt0);
		
		for(int i=0;i<rgwe-rgws+1;i++) {
			for(int j=0;j<rghe-rghs+1;j++) {
				for(int k=0;k<rgtxe-rgtxs+1;k++) {
					if(seMap[i+rgws][j+rghs][k+rgtxs]!=label)			// avoid interfering other events	checked
						evtL[i][j][k] = 0;
					if(evtL[i][j][k]>0)									// update the event label	checked
						evtL[i][j][k] = evtL[i][j][k] + nEvt;
					if(evtRecon[i][j][k]<datR[i+rgws][j+rghs][k+rgtxs]) 	// checked
						evtL[i][j][k] = datL[i+rgws][j+rghs][k+rgtxs];
					datR[i+rgws][j+rghs][k+rgtxs] = (int) Math.max(datR[i+rgws][j+rghs][k+rgtxs], evtRecon[i][j][k]);	// combine events	checked
					datL[i+rgws][j+rghs][k+rgtxs] = evtL[i][j][k];
				}
			}
		}
//		ProgressBarRealizedStep3.addToRisingMap(riseLst, evtMap, dlyMap, nEvt, nEvt0, rghs, rghe, rgws, rgwe, rgts, rgte, rgtSels, rgtSele);		// checked
//		nEvt = nEvt + nEvt0;	// checked
		
		// TODO: delete
		try {
			FileOutputStream f = null;
			ObjectOutputStream o = null;
			
			f = new FileOutputStream(new File("D:\\New Folder212\\" + "datL.txt"));
			o = new ObjectOutputStream(f);
			o.writeObject(datL);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File("D:\\New Folder212\\" + "datR.txt"));
			o = new ObjectOutputStream(f);
			o.writeObject(datR);
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		HashMap<Integer, ArrayList<int[]>> evtLst = ProgressBarRealizedStep3.label2idx(datL);
		Opts opts = new Opts(1);
		QuickFeatureResult quickFeatureResult = ProgressBarRealizedStep3.getFeatureQuick(dat, evtLst, opts);
		FtsLst ftsLst = quickFeatureResult.ftsLst;
		float[][] dffMat = quickFeatureResult.dffMatExt;

	}
	
	
	
	public static void step3test2() {
		float[][][] dF = null;;
		String proPath = "D:\\New Folder212\\";
		HashMap<Integer,ArrayList<int[]>> spLst = null;
		float[][] distMat = null;
		float[][] dlyMap = null;
		float[][] cx = null;
		try {
			// data in step1
			FileInputStream fi1 = null;
			ObjectInputStream oi1 = null;
			
			fi1 = new FileInputStream(new File(proPath + "DF.ser"));
			oi1 = new ObjectInputStream(fi1);
			dF = (float[][][])oi1.readObject();	
			oi1.close();
			fi1.close();
			
			fi1 = new FileInputStream(new File(proPath + "spLst.txt"));
			oi1 = new ObjectInputStream(fi1);
			spLst = (HashMap<Integer,ArrayList<int[]>> )oi1.readObject();	
			oi1.close();
			fi1.close();
			
			fi1 = new FileInputStream(new File(proPath + "distMat.txt"));
			oi1 = new ObjectInputStream(fi1);
			distMat = (float[][])oi1.readObject();	
			oi1.close();
			fi1.close();
			
			fi1 = new FileInputStream(new File(proPath + "dlyMap.txt"));
			oi1 = new ObjectInputStream(fi1);
			dlyMap = (float[][])oi1.readObject();	
			oi1.close();
			fi1.close();
			
			fi1 = new FileInputStream(new File(proPath + "cx.txt"));
			oi1 = new ObjectInputStream(fi1);
			cx = (float[][])oi1.readObject();	
			oi1.close();
			fi1.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		int rgtSels = 0;
		int rgtSele = 30;
		boolean xFail = false;
		int[][] evtMap = null;
		int maxStp = 11;		// 11
		int maxRiseUnc = 1;	// 1
		int cDelay = 5;		// 5
		if(!xFail) {
			RiseMapResult riseMapResult = ProgressBarRealizedStep3.riseMap2evt(spLst, dlyMap, distMat, maxRiseUnc, cDelay, false);		// checked
			int[] evtMemC = riseMapResult.evtMemC;
			int[][] evtMemCMap = riseMapResult.evtMemCMap;
			// TODO: delete
			try {
				FileOutputStream f = null;
				ObjectOutputStream o = null;
				
				f = new FileOutputStream(new File("D:\\New Folder212\\" + "evtMemCMap.txt"));
				o = new ObjectOutputStream(f);
				o.writeObject(evtMemCMap);
				o.close();
				f.close();
				
				f = new FileOutputStream(new File("D:\\New Folder212\\" + "evtMemC.txt"));
				o = new ObjectOutputStream(f);
				o.writeObject(evtMemC);
				o.close();
				f.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
						
			evtMap = new int[dlyMap.length][dlyMap[0].length];
			int maxEvtMemC = Integer.MIN_VALUE;
			for(int i=0;i<evtMemC.length;i++) {		// checked
				maxEvtMemC = Math.max(maxEvtMemC, evtMemC[i]);
			}
			for(int i=1;i<=maxEvtMemC;i++) {
				ArrayList<Integer> idx0 = new ArrayList<>();
				for(int j=0;j<evtMemC.length;j++) {			// checked
					if(evtMemC[j]==i)
						idx0.add(j+1);
				}
				int len = idx0.size();
				// spLst0
				HashMap<Integer, ArrayList<int[]>> spLst0 = new HashMap<>();			// checked
				int cnt = 1;
				for(int label:idx0) {
					spLst0.put(cnt, new ArrayList<>(spLst.get(label)));
					cnt++;
				}
				
				// distMat0
				float[][] distMat0 = new float[len][len];
				for(int x=0;x<len;x++) {												// checked
					for(int y=0;y<len;y++) {
						distMat0[x][y] = distMat[idx0.get(x)-1][idx0.get(y)-1];
					}
				}
				
				// dlyMap0
				float[][] dlyMap0 = ProgressBarRealizedStep3.copyMap(dlyMap);									// checked
				for(int x=0;x<dlyMap.length;x++) {
					for(int y=0;y<dlyMap[0].length;y++) {
						if(evtMemCMap[x][y]!=i)
							dlyMap0[x][y] = Float.MAX_VALUE;
					}
				}
				RiseMapResult riseMapResult0 = ProgressBarRealizedStep3.riseMap2evt(spLst0, dlyMap0, distMat0, maxRiseUnc, cDelay, true);			// checked
				int[][] evtMap00 = riseMapResult0.evtMap;								// checked
				int maxEvtMap = Integer.MIN_VALUE;
				for(int x=0;x<evtMap.length;x++) {
					for(int y=0;y<evtMap[0].length;y++) {
						maxEvtMap = Math.max(maxEvtMap, evtMap[x][y]);
					}
				}
				
				for(int x=0;x<evtMap00.length;x++) {									// checked
					for(int y=0;y<evtMap00[0].length;y++) {
						if(evtMap00[x][y]>0)
							evtMap00[x][y] += maxEvtMap;
					}
				}
				
				for(int x=0;x<evtMap00.length;x++) {									// checked
					for(int y=0;y<evtMap00[0].length;y++) {
						evtMap[x][y] = Math.max(evtMap[x][y], evtMap00[x][y]);
					}
				}
				
				// TODO: delete
				try {
					FileOutputStream f = null;
					ObjectOutputStream o = null;
					
					f = new FileOutputStream(new File("D:\\New Folder212\\" + "evtMap.txt"));
					o = new ObjectOutputStream(f);
					o.writeObject(evtMap);
					o.close();
					f.close();
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
			
			
			// without propagation, and when noise is low, the algorithm fails
			// many parts in the super event will be lost
			int sumEvtMap = 0;															// checked
			int sumDlyMap = 0;
			for(int x=0;x<evtMap.length;x++) {
				for(int y=0;y<evtMap[0].length;y++) {
					if(evtMap[x][y]>0)
						sumEvtMap += 1;
					
					if(dlyMap[x][y]<Float.MAX_VALUE)
						sumDlyMap += 1;
				}
			}
			
			if(sumEvtMap<0.9*sumDlyMap) {
				xFail = true;
				System.out.println("Skip propagation");
			}
		}
		
		int nFind = 0;																	// checked
		for(int x=0;x<evtMap.length;x++) {
			for(int y=0;y<evtMap[0].length;y++) {
				nFind = Math.max(nFind, evtMap[x][y]);
			}
		}
		
		
		int it0s = 1;
		int it0e = 30;
		int rgts = 0;
		int rgte = 30;
		System.out.println("Found " + nFind + " Events");
		
		float[][] cx1 = new float[spLst.size()][it0e-it0s+1]; 							// checked		
		for(int i=0;i<cx.length;i++) {
			for(int t=0;t<it0e-it0s+1;t++) {
				cx1[i][t] = cx[i][t+it0s-rgts-rgtSels];
			}
		}
		
		// Events
		float minShow = 0;	
		Opts opts = new Opts(1);
		// checked
		if(opts.usePG) {
			minShow = (float) Math.sqrt(opts.minShow1);
		}else {
			minShow = opts.minShow1;
		}
		
		EvtReconResult evtReconResult = GTW.evtRecon(spLst, cx1, evtMap, minShow);		// checked
		int[][][] evtL = evtReconResult.evtL;
		float[][][] evtRecon = evtReconResult.evtRecon;
		
		int nEvt0 = Integer.MIN_VALUE;
		for(int i=0;i<evtRecon.length;i++) {
			for(int j=0;j<evtRecon[0].length;j++) {
				for(int k=0;k<evtRecon[0][0].length;k++) {
					evtRecon[i][j][k] = evtRecon[i][j][k]*evtRecon[i][j][k]*255;
					nEvt0 = Math.max(nEvt0, evtL[i][j][k]);
				}
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
	public static void step3tes1() {
		float[][][] dF = null;;
		String proPath = "D:\\New Folder\\";
		int[][][] seMap = null;
		try {
			// data in step1
			FileInputStream fi1 = null;
			ObjectInputStream oi1 = null;
			
			fi1 = new FileInputStream(new File(proPath + "DF.txt"));
			oi1 = new ObjectInputStream(fi1);
			dF = (float[][][])oi1.readObject();	
			oi1.close();
			fi1.close();
			
			fi1 = new FileInputStream(new File(proPath + "seMap.txt"));
			oi1 = new ObjectInputStream(fi1);
			seMap = (int[][][])oi1.readObject();	
			oi1.close();
			fi1.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		int seSel = 1;
		float gtwSmo = 0.5f;	// 0.5
		int maxStp = 11;		// 11
		int maxRiseUnc = 1;	// 1
		int cDelay = 5;		// 5
		int spSz = 25;					// preferred super pixel size
		int spT = 30;					// super pixel number scale (larger for more)
		int rgws = 96;
		int rgwe = 349;
		int rghs = 259;
		int rghe = 499;
		int rgts = 0;
		int rgte = 30;
		int it0s = 1;
		int it0e = 30;
		GTW.spgtw(dF,seMap,seSel,gtwSmo,maxStp,cDelay,spSz,spT,rgws,rgwe,rghs,rghe,rgts,rgte, new Opts(1));	
	}
	
	
}