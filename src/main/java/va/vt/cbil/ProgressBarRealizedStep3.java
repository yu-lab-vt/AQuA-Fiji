package va.vt.cbil;

import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.Queue;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

/**
 * The third and the most important step of the whole software, to
 * detect the events. Find the super event first, then split it into
 * several events.Then according to the events detected, extract 
 * premiliary features. After this step finish, we show them in 
 * interface with different colors. 
 * 
 * @author Xuelong Mi
 * @version 1.0
 */
/**
 * @author Xuelong Mi
 *
 */
public class ProgressBarRealizedStep3 extends SwingWorker<int[][][], Integer> {
	JFrame frame = new JFrame("Step3");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	
	static long start;
	static long end;
//	static Opts opts = null;
	static ImagePlus image = null;
	static ImageDealer imageDealer = null;
	String proPath = null;
	
	/**
	 * Construct the class by imageDealer. 
	 * 
	 * @param imageDealer used to read the parameter
	 */
	@SuppressWarnings("static-access")
	public ProgressBarRealizedStep3(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
		proPath = imageDealer.proPath;
		if(imageDealer!=null)
			image = imageDealer.imgPlus;
		imageDealer.running = true;
	}
	
	
	/**
	 * Set the Jframe and its content, used to show the progress bar.
	 */
	protected void setting() {
		frame.setSize(400, 200);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(1);
		
		progressBar.setIndeterminate(true);
		progressBar.setOrientation(SwingConstants.HORIZONTAL);
		progressBar.setPreferredSize(new Dimension(300,20));
		
		jLabel.setPreferredSize(new Dimension(300,30));
		jLabel.setFont(new Font("Dialog",1,15));
		jLabel.setHorizontalAlignment(JLabel.CENTER);;
		GridBagPut settingPanel = new GridBagPut(curPanel);
		settingPanel.fillBoth();
		settingPanel.putGridBag(progressBar, curPanel, 0, 0);
		settingPanel.putGridBag(jLabel, curPanel, 0, 1);
		frame.setContentPane(curPanel);
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
		
	}
	
	/**
	 * Detect the events
	 * 
	 * @return return the labels of different events
	 */
	@Override
	protected int[][][] doInBackground() throws Exception {

		publish(1);
		// ------------------------ Read Data ----------------------------- //
		start = System.currentTimeMillis();
		long start0 = start;
		float[][][] dat = null;;
		float[][][] dF = null;;
		
		int[][][] lblxMapEX = null;

		int[][] riseX = null;
		HashMap<Integer, ArrayList<int[]>> lblxMap = null;
		
		// For test
		Opts opts = imageDealer.opts;
//		if(imageDealer!=null)
//			opts = imageDealer.opts;
		try {
			// data in step1
			FileInputStream fi1 = null;
			ObjectInputStream oi1 = null;
			
			fi1 = new FileInputStream(new File(proPath + "Data.ser"));
			oi1 = new ObjectInputStream(fi1);
			dat = (float[][][])oi1.readObject();
			oi1.close();
			fi1.close();
			
			fi1 = new FileInputStream(new File(proPath + "DF.ser"));
			oi1 = new ObjectInputStream(fi1);
			dF = (float[][][])oi1.readObject();	
			oi1.close();
			fi1.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			// data in step2
			FileInputStream fi2 = null;
			ObjectInputStream oi2 = null;
			
			fi2 = new FileInputStream(new File(proPath + "Step2_Labels.ser"));
			oi2 = new ObjectInputStream(fi2);
//			lblxMap = (HashMap<Integer, ArrayList<int[]>>) oi2.readObject();
			lblxMapEX = (int[][][]) oi2.readObject();
			oi2.close();
			fi2.close();
			
			fi2 = new FileInputStream(new File(proPath + "ResultInStep2_RiseX.ser"));
			oi2 = new ObjectInputStream(fi2);
			riseX = (int[][])oi2.readObject();	
			oi2.close();
			fi2.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		showTime();
		
		int width = dat.length;
		int height = dat[0].length;
		int pages = dat[0][0].length;
		lblxMap = new HashMap<>();
		for(int k = 0;k<pages;k++) {
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					int label = lblxMapEX[i][j][k];
					if(label!=0) {
						ArrayList<int[]> l = lblxMap.get(label);
						if(l==null) {
							l = new ArrayList<>();
						}
						l.add(new int[] {i,j,k});
						lblxMap.put(label, l);
					}
				}
			}
		}
		
		System.out.println(lblxMap.size());
		showTime();
		
		System.out.println(riseX.length);
		
//		if(imageDealer!=null)
//			opts = imageDealer.opts;
		
		publish(2);
		// evtTop
		EvtTopResult evtTopresult = evtTop(dat,dF,lblxMap,riseX,opts,lblxMapEX);
		HashMap<Integer,RiseNode> riseLst = evtTopresult.riseLst;
		HashMap<Integer,ArrayList<int[]>> evtLst = evtTopresult.evtLst;
		HashMap<Integer,ArrayList<int[]>> seLst = evtTopresult.seLst;
		int[][][] datR = evtTopresult.datR;
		int[][][] seMap = evtTopresult.seMap;
		
		// relabel
		HashMap<Integer,ArrayList<int[]>> newEvtLst = new HashMap<>();
		HashMap<Integer,RiseNode> newRiseLst = new HashMap<>();
		int cnt = 1;
		int maxLabel = 0;
		for(Entry<Integer, ArrayList<int[]>> entry : evtLst.entrySet()) {
			maxLabel = Math.max(entry.getKey(), maxLabel);
		}
		for(int i=1;i<=maxLabel;i++) {
			ArrayList<int[]> points = evtLst.get(i);
			if(points!=null) {
				newEvtLst.put(cnt, points);
				newRiseLst.put(cnt, riseLst.get(i));
				cnt++;
			}
		}
		evtLst = newEvtLst;
		riseLst = newRiseLst;
				
		try {
			FileOutputStream f = null;
			ObjectOutputStream o = null;
			
			// datR
			f = new FileOutputStream(new File(proPath + "ResultInStep3_DatRAll.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(datR);
			o.close();
			f.close();
			
			// evtLst
			f = new FileOutputStream(new File(proPath + "ResultInStep3_EvtLstAll.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(evtLst);
			o.close();
			f.close();
			
			// seLst
			f = new FileOutputStream(new File(proPath + "ResultInStep3_SeLstAll.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(seLst);
			o.close();
			f.close();
			
			// seMap
			f = new FileOutputStream(new File(proPath + "Step3_seMap_Labels.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(seMap);
			o.close();
			f.close();
			
			// riseLst
			f = new FileOutputStream(new File(proPath + "ResultInStep3_RiseLstAll.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(riseLst);
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		imageDealer.datR = datR;
		
		publish(3);
		// getFeatureQuick
		QuickFeatureResult quickFeatureResult = getFeatureQuick(dat, evtLst, opts);
		FtsLst ftsLst = quickFeatureResult.ftsLst;
		float[][] dffMat = quickFeatureResult.dffMatExt;
		int[][][] evtMap = quickFeatureResult.evtMap;
		int nEvt = evtLst.size();
		publish(4);
		try {
			FileOutputStream f = null;
			ObjectOutputStream o = null;
			
			// ftsLst
			f = new FileOutputStream(new File(proPath + "ResultInStep3_FtsLstAll.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(ftsLst);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath + "Step3_Labels.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(evtMap);
			o.close();
			f.close();
			
			// dffMatAll
			f = new FileOutputStream(new File(proPath + "ResultInStep3_DffMatAll.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(dffMat);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath + "nEvt.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(nEvt);
			o.close();
			f.close();
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
//		int[][][] evtMap = new int[width][height][pages];
//		for(Entry<Integer, ArrayList<int[]>> entry:evtLst.entrySet()) {
//			int label = entry.getKey();
//			ArrayList<int[]> points = entry.getValue();
//			for(int[] p:points) {
//				evtMap[p[0]][p[1]][p[2]] = label;
//			}
//		}
//		
		
		end = System.currentTimeMillis();
		System.out.println("Total time" + (end-start0) + "ms");
		
		imageDealer.center.EvtNumber.setText(nEvt+"");
		
		return evtMap;
		
	}
	
	
	/**
	 * Extract events
	 * 
	 * @param dat the data matrix
	 * @param dF the data matrix after subtract the background
	 * @param lblxMap the super voxels map
	 * @param riseX the rising time of each super voxel
	 * @param opts the parameter
	 * @param lblMapS the label matrix of super voxels
	 * @return the event label matrix and brightness
	 */
	private static EvtTopResult evtTop(float[][][] dat, float[][][] dF, HashMap<Integer, ArrayList<int[]>> lblxMap, int[][] riseX,
			Opts opts, int[][][] lblMapS) {
		int width = dat.length;									// checked
		int height = dat[0].length;
		int pages = dat[0][0].length;
		int changeParameter = Math.max(width, height);
		float[] riseX0 = nanMedian(riseX);
		int[][][] riseMap = new int[width][height][pages];
		
		for(Map.Entry<Integer,ArrayList<int[]>> entry:lblxMap.entrySet()) {				// checked
			ArrayList<int[]> points = entry.getValue();
			int label = entry.getKey();
			int t00 = Math.round(riseX0[label-1]);
			if(t00!=-1) {
				for(int[] p:points) {
					riseMap[p[0]][p[1]][p[2]] = t00;
				}
			}
		}
		
		// super voxels to super events
		System.out.println("Detecting super events ...");
		int stp11 = (int) Math.max(Math.round((double)opts.maxStp/2), 2);							// checked
		int[][][] seMap = null;
		if(opts.superEventdensityFirst == 1) {
			SvNeibResult svNeibResult = svNeib(lblMapS, lblxMap, riseMap,stp11,opts.cOver);			// checked
			seMap = sv2se(lblMapS,lblxMap,svNeibResult);
		}else {
			seMap = sp2evtStp1(lblMapS,riseMap,0,stp11,opts.cOver,dat);
		}
			
		showTime();
		HashMap<Integer,ArrayList<int[]>> seLst = label2idx(seMap);									// checked
		// Clean super events
		seMap = new int[width][height][pages];
		HashMap<Integer,ArrayList<int[]>> seLstNew = new HashMap<>();
		int cntSE = 0;
		for(Entry<Integer, ArrayList<int[]>> entry:seLst.entrySet()) {
			ArrayList<int[]> pix = entry.getValue();
			HashSet<Integer> pix2D = new HashSet<>();
			for(int[] p:pix) {
				pix2D.add(p[0]*changeParameter+p[1]);
				if(pix2D.size()>opts.minSize) {
					cntSE++;
					break;
				}
			}
			if(pix2D.size()>opts.minSize) {
				seLstNew.put(cntSE, pix);
				for(int[] p:pix) {
					seMap[p[0]][p[1]][p[2]] = cntSE;
				}
			}
		}
		seLst = seLstNew;
		
		System.out.println(seLst.size());
		showTime();
		
		// super event to events
		System.out.println("Detecting events ...");
		HashMap<Integer,RiseNode> riseLst = new HashMap<>();
		int[][][] datR = new int[width][height][pages];
		int[][][] datL = new int[width][height][pages];
		int nEvt = 0;
		for(int n=1;n<=seLst.size();n++) {
			int label = n;
			ArrayList<int[]> se0 = seLst.get(label);
			if(se0.size()==0)
				continue;
			System.out.println("SE " + label);
			
			int rghs = Integer.MAX_VALUE;
			int rgws = Integer.MAX_VALUE;
			int rgts = Integer.MAX_VALUE;
			int rghe = Integer.MIN_VALUE;
			int rgwe = Integer.MIN_VALUE;
			int rgte = Integer.MIN_VALUE;
			
			HashSet<Integer> ihw0 = new HashSet<>();				// checked
			for(int[] p:se0) {
				rgws = Math.min(rgws, p[0]);
				rghs = Math.min(rghs, p[1]);
				rgts = Math.min(rgts, p[2]);
				rgwe = Math.max(rgwe, p[0]);
				rghe = Math.max(rghe, p[1]);
				rgte = Math.max(rgte, p[2]);
			}
			
			for(int[] p:se0) {
				int px = p[0] - rgws;
				int py = p[1] - rghs;
				ihw0.add(px*changeParameter + py);
			}
			
			int gapt = Math.max(5, rgte-rgts);
			int it0s = rgts;
			int it0e = rgte;
			rgts = Math.max(0, rgts-gapt);
			rgte = Math.min(pages-1, rgte+gapt);
			

			showTime();
			SeToEvent seToEventResult = se2evt(dF,seMap,n,ihw0,rgws,rgwe,rghs,rghe,rgts,rgte,it0s,it0e,pages,opts,changeParameter);			// checked
			int rgtxs = seToEventResult.it0s;
			int rgtxe = seToEventResult.it0e;
			int[][] evtMap = seToEventResult.evtMap;
			float[][] dlyMap = seToEventResult.dlyMap;
			int nEvt0 = seToEventResult.nEvt0;
			int rgtSels = seToEventResult.rgtSels;
			int rgtSele = seToEventResult.rgtSele;
			float[][][] evtRecon = seToEventResult.evtRecon;
			int[][][] evtL = seToEventResult.evtL;	
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
			addToRisingMap(riseLst, evtMap, dlyMap, nEvt, nEvt0, rghs, rghe, rgws, rgwe, rgts, rgte, rgtSels, rgtSele);		// checked
			nEvt = nEvt + nEvt0;	// checked
			imageDealer.center.EvtNumber.setText(nEvt+"");
		}
		
		HashMap<Integer,ArrayList<int[]>> evtLst = label2idx(datL);			// checked
		
		// clean the events less than opts.minSize
		datL = new int[width][height][pages];
		HashMap<Integer,ArrayList<int[]>> evtLstNew = new HashMap<>();
		HashMap<Integer,RiseNode> riseLstNew = new HashMap<>();
		int cnt = 0;
		for(Entry<Integer, ArrayList<int[]>> entry:evtLst.entrySet()) {
			ArrayList<int[]> pix = entry.getValue();
			HashSet<Integer> pix2D = new HashSet<>();
			for(int[] p:pix) {
				pix2D.add(p[0]*changeParameter+p[1]);
				if(pix2D.size()>opts.minSize) {
					cnt++;
					break;
				}
			}
			if(pix2D.size()>opts.minSize) {
				evtLstNew.put(cnt, pix);
				RiseNode curRiseNode = riseLst.get(entry.getKey());
				riseLstNew.put(cnt, curRiseNode);
				for(int[] p:pix) {
					datL[p[0]][p[1]][p[2]] = cnt;
				}
			}
		}
		
		
		return new EvtTopResult(riseLstNew,datR, evtLstNew, seLst,datL,seMap);
	}
	
	/**
	 * @author Xuelong Mi
	 * Use to transmit results
	 */
	static class EvtTopResult{
		HashMap<Integer,RiseNode> riseLst = null;
		int[][][] datR = null;
		int[][][] datL = null;
		int[][][] seMap = null;
		HashMap<Integer,ArrayList<int[]>> evtLst = null;
		HashMap<Integer,ArrayList<int[]>> seLst = null;
		
		public EvtTopResult(HashMap<Integer,RiseNode> riseLst, int[][][] datR, 
				HashMap<Integer,ArrayList<int[]>> evtLst, HashMap<Integer,ArrayList<int[]>> seLst, int[][][] datL, int[][][] seMap) {
			this.riseLst = riseLst;
			this.datR = datR;
			this.evtLst = evtLst;
			this.seLst = seLst;
			this.datL = datL;
			this.seMap = seMap;
		}
	}
	
	/**
	 * Record the features of each event
	 * @param riseLst where we record
	 * @param evtMap the event map
	 * @param dlyMap the delay Map
	 * @param nEvt the current number index of events
	 * @param nEvt0 the number of events
	 * @param rghs 
	 * @param rghe
	 * @param rgws
	 * @param rgwe
	 * @param rgts
	 * @param rgte
	 * @param rgtSels
	 * @param rgtSele
	 */
	public static void addToRisingMap(HashMap<Integer, RiseNode> riseLst, int[][] evtMap, float[][] dlyMap, int nEvt,
			int nEvt0, int rghs, int rghe, int rgws, int rgwe, int rgts, int rgte, int rgtSels, int rgtSele) {
		for(int i=1;i<=nEvt0;i++) {
			ArrayList<int[]> points = new ArrayList<>();
			for(int x=0;x<evtMap.length;x++) {
				for(int y=0;y<evtMap[0].length;y++) {
					if(evtMap[x][y]==i)
						points.add(new int[] {x,y});
				}
			}
			if(points.size()>0) {
				int iwrs = Integer.MAX_VALUE;
				int iwre = Integer.MIN_VALUE;
				int ihrs = Integer.MAX_VALUE;
				int ihre = Integer.MIN_VALUE;
				for(int[] p:points) {
					iwrs = Math.min(iwrs, p[0]);
					iwre = Math.max(iwre, p[0]);
					ihrs = Math.min(ihrs, p[1]);
					ihre = Math.max(ihre, p[1]);
				}
				float[][] dlyMapr = new float[iwre-iwrs+1][ihre-ihrs+1];
				for(int x=0;x<iwre-iwrs+1;x++) {
					for(int y=0;y<ihre-ihrs+1;y++) {
						if(evtMap[x+iwrs][y+ihrs]==i)
							dlyMapr[x][y] = dlyMap[x+iwrs][y+ihrs] + rgts + rgtSels;
						if(dlyMapr[x][y]==0)
							dlyMapr[x][y] = -1;
					}
				}
				RiseNode rr = new RiseNode(dlyMapr, rgws + iwrs, rgws + iwre, rghs + ihrs, rghs + ihre );
				riseLst.put(i+nEvt, rr);
			}
		}
		
	}

	/**
	 * @author Xuelong Mi
	 * To transmit the results
	 */
	static class RiseNode implements Serializable{
		private static final long serialVersionUID = 1L;
		float[][] dlyMap = null;
		int rgws = 0;
		int rgwe = 0;
		int rghs = 0;
		int rghe = 0;
		
		public RiseNode(float[][] dlyMap, int rgws, int rgwe, int rghs, int rghe) {
			this.dlyMap = dlyMap;
			this.rgws = rgws;
			this.rgwe = rgwe;
			this.rghs = rghs;
			this.rghe = rghe;
		}
		
		public String toString() {
			return dlyMap.toString() + " " + rgws + " " + rgwe + " " + rghs + " " + rghe;
		}
	}
	
	/**
	 * To split the super events
	 * @param dF the data matrix after subtract the background
	 * @param seMap the super event map
	 * @param seSel the current super event index
	 * @param ihw0 the 2D point 
	 * @param rgws
	 * @param rgwe
	 * @param rghs
	 * @param rghe
	 * @param rgts
	 * @param rgte
	 * @param it0s
	 * @param it0e
	 * @param pages
	 * @param opts
	 * @param changeParameter
	 * @return
	 */
	public static SeToEvent se2evt(float[][][] dF, int[][][] seMap, int seSel, HashSet<Integer> ihw0, int rgws, int rgwe,
			int rghs, int rghe, int rgts, int rgte, int it0s, int it0e, int pages, Opts opts, int changeParameter) {
		float gtwSmo = opts.gtwSmo;		// 0.5
		int maxStp = opts.maxStp;		// 11
		int maxRiseUnc = opts.cRise;	// 1
		int cDelay = opts.cDelay;		// 5
		int spSz = 25;					// preferred super pixel size
		int spT = 30;					// super pixel number scale (larger for more)
		boolean xFail = false;
		int[][] evtMap = null;
		float[][] cx = null;
		HashMap<Integer, ArrayList<int[]>> spLst = null;
		float[][] dlyMap = null;
		int rgtSels = 0;
		int rgtSele = 0;
		// GTW on super pixels
		// group super pixels to events
		if(ihw0.size()>100) {
			GTWResult gTWresult = GTW.spgtw(dF,seMap,seSel,gtwSmo,maxStp,cDelay,spSz,spT,rgws,rgwe,rghs,rghe,rgts,rgte,opts);		// checked
			// ------------------------------------------------ Test ------------------------------------------------------
			xFail = gTWresult.isFail;
			cx = gTWresult.cx;
			spLst = gTWresult.spLst;
			rgtSels = gTWresult.rgtSels;
			rgtSele = gTWresult.rgtSele;
			if(!xFail) {
				RiseMapResult riseMapResult = riseMap2evt(gTWresult.spLst, gTWresult.dlyMap, gTWresult.distMat, maxRiseUnc, cDelay, false);		// checked
				int[] evtMemC = riseMapResult.evtMemC;
				int[][] evtMemCMap = riseMapResult.evtMemCMap;
		
				dlyMap = gTWresult.dlyMap;
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
					float[][] distMat = gTWresult.distMat;
					float[][] distMat0 = new float[len][len];
					for(int x=0;x<len;x++) {												// checked
						for(int y=0;y<len;y++) {
							distMat0[x][y] = distMat[idx0.get(x)-1][idx0.get(y)-1];
						}
					}
					
					// dlyMap0
					float[][] dlyMap0 = copyMap(dlyMap);									// checked
					for(int x=0;x<dlyMap.length;x++) {
						for(int y=0;y<dlyMap[0].length;y++) {
							if(evtMemCMap[x][y]!=i)
								dlyMap0[x][y] = Float.MAX_VALUE;
						}
					}
					
					RiseMapResult riseMapResult0 = riseMap2evt(spLst0, dlyMap0, distMat0, maxRiseUnc, cDelay, true);			// checked
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
		}
		
		
		// for small events, do not use GTW
		if(ihw0.size()<=100 || xFail) {													// checked
			rgtSels = 0;
			rgtSele = rgte-rgts;
			spLst = new HashMap<>();
			ArrayList<int[]> points = new ArrayList<>();
			evtMap = new int[rgwe-rgws+1][rghe-rghs+1];
			dlyMap = new float[rgwe-rgws+1][rghe-rghs+1];
			for(int pxy:ihw0) {															// checked
				int px = pxy/changeParameter;
				int py = pxy%changeParameter;
				points.add(new int[] {px,py});
				evtMap[px][py] = 1;
			}
			spLst.put(1, points);
			cx = new float[1][rgte-rgts+1];												// checked
			for(int t=rgts;t<=rgte;t++) {
				float sum = 0;
				for(int[] p:points) {
					int x = p[0] + rgws;
					int y = p[1] + rghs;
					sum += dF[x][y][t];
				}
				cx[0][t-rgts] = sum/points.size();
			}
			
			float minCx = Float.MAX_VALUE;												// checked
			for(int t=0;t<rgte-rgts+1;t++) {
				minCx = Math.min(minCx, cx[0][t]);
			}
			
			float maxCx = -Float.MAX_VALUE;												// checked
			for(int t=0;t<rgte-rgts+1;t++) {
				cx[0][t] -= minCx;
				maxCx = Math.max(maxCx, cx[0][t]);
			}
			
			for(int t=0;t<rgte-rgts+1;t++) {												// checked
				cx[0][t] /= maxCx;
			}
		}
		
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
		
		// Events
		float minShow = 0;																// checked
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
					evtRecon[i][j][k] = (int)Math.round(evtRecon[i][j][k]*evtRecon[i][j][k]*255);
					evtRecon[i][j][k] = Math.min(evtRecon[i][j][k], 255);
					nEvt0 = Math.max(nEvt0, evtL[i][j][k]);
				}
			}
		}
		
		
		return new SeToEvent(evtRecon, evtL, evtMap, dlyMap, nEvt0, it0s, it0e, rgtSels, rgtSele);
	}
	
	/**
	 * @author Xuelong Mi
	 * To transmit the results
	 */
	static class SeToEvent{
		float[][][] evtRecon = null;
		int[][][] evtL = null;
		int[][] evtMap = null;
		float[][] dlyMap = null;
		int nEvt0 = 0;
		int it0s = 0;
		int it0e = 0;
		int rgtSels = 0;
		int rgtSele = 0;
		
		public SeToEvent(float[][][] evtRecon, int[][][] evtL, int[][] evtMap, float[][] dlyMap, int nEvt0, int it0s, int it0e, int rgtSels, int rgtSele) {
			this.evtRecon = evtRecon;
			this.evtL = evtL;
			this.evtMap = evtMap;
			this.dlyMap = dlyMap;
			this.nEvt0 = nEvt0;
			this.it0s = it0s;
			this.it0e = it0e;
			this.rgtSels = rgtSels;
			this.rgtSele = rgtSele;
		}
	}

	/**
	 * copy matrix
	 * @param input the input matirx
	 * @return
	 */
	public static float[][] copyMap(float[][] input){
		int width = input.length;
		int height = input[0].length;
		float[][] result = new float[width][height];
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				result[i][j] = input[i][j];
			}
		}
		return result;
	}
	
	/**
	 * According to the delay map and super pixel, combine close super pixels to events
	 * @param spLst the super pixel list/map
	 * @param dlyMap the delay map
	 * @param distMat the distance map of super pixels
	 * @param maxRiseUnc the maximum rising phase uncertainty
	 * @param cDelay the slowest propagation
	 * @param stg the flag
	 * @return
	 */
	public static RiseMapResult riseMap2evt(HashMap<Integer, ArrayList<int[]>> spLst, float[][] dlyMap, float[][] distMat,
			int maxRiseUnc, int cDelay, boolean stg) {
		int nSp = spLst.size();
		int W = dlyMap.length;
		int H = dlyMap[0].length;
		int[][] evtMap = new int[W][H];
		
		int[][] spMap = new int[W][H];
		float[] riseX = new float[nSp];
		
		// convert spLst to spMap
		// some riseX is infinity, need  to change					
		for(Entry<Integer, ArrayList<int[]>> entry:spLst.entrySet()) {		// checked
			int label = entry.getKey();
			ArrayList<int[]> points = entry.getValue();
			float sum = 0;
			for(int[] p :points) {
				spMap[p[0]][p[1]] = label;
				sum += dlyMap[p[0]][p[1]];
			}
			riseX[label-1] = sum/points.size();
		}
		
//		try {
//			FileOutputStream f = null;
//			ObjectOutputStream o = null;
//			
//			f = new FileOutputStream(new File("D:\\New Folder212\\" + "riseX.ser"));
//			o = new ObjectOutputStream(f);
//			o.writeObject(riseX);
//			o.close();
//			f.close();
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		// get local minimum as event starting point
		// small area starting point not trustable
		int initAreaThr = 30;
		float[][] dlyMap1 = new float[W][H];			// checked
		boolean[][] mskDly = new boolean[W][H];
		for(int i=0;i<W;i++) {
			for(int j=0;j<H;j++) {
				dlyMap1[i][j] = dlyMap[i][j];
				if(dlyMap1[i][j]<Float.MAX_VALUE)
					mskDly[i][j] = true;
			}
		}
		HashMap<Integer,ArrayList<int[]>> cc = null;		// checked
		for(int n=0;n<1000;n++) {
			int[][] roundDlyMap = new int[W][H];
			for(int i=0;i<W;i++) {
				for(int j=0;j<H;j++) {
					roundDlyMap[i][j] = Math.round(dlyMap1[i][j]*100);
				}
			}
			// get the regional minimal
			ImageProcessor p1 = MinimaAndMaxima.regionalMinima(new FloatProcessor(roundDlyMap),8);
			int[][] lmF = p1.getIntArray();
			boolean[][] lm = new boolean[W][H];
			boolean notAllFalse = false;
			for(int x=0;x<W;x++) {
				for(int y=0;y<H;y++) {
					if(lmF[x][y]==255 && mskDly[x][y]) {
						lm[x][y] = true;
						notAllFalse =true;
					}
				}
			}
			
			if(!notAllFalse)
				lm = mskDly;
			
			// connect the minimal
			cc = ConnectedComponents.twoPassConnect2D(lm);						// checked
			boolean allOK = true;
			
			for(Entry<Integer, ArrayList<int[]>> entry:cc.entrySet()) {
				ArrayList<int[]> points = entry.getValue();
				if(points.size()<initAreaThr) {
					// label set of spMap in this connection
					HashSet<Integer> sp00 = new HashSet<>();		// checked
					for(int[] p:points) {
						int label = spMap[p[0]][p[1]];
						if(label>0)
							sp00.add(label);
					}
					// neighbor set for all labels in this connection
					HashSet<Integer> xNeib = new HashSet<>();		// checked
					for(int label:sp00) {
						// xNeib.addAll(pair.get(label));
						int k = label-1;
						for(int j=0;j<distMat.length;j++) {
							if(distMat[k][j] != Float.MAX_VALUE)
								xNeib.add(j+1);
						}
					}
					xNeib.removeAll(sp00);							// checked
					
					
					if(xNeib.size()>0) {
						allOK = false;
						float tNew = Float.MAX_VALUE;
						for(int label:xNeib) {
							tNew = Math.min(tNew, riseX[label-1]);
						}
						for(int[] p:points) {
							dlyMap1[p[0]][p[1]] = tNew;
						}
					}
				}
				
				
			}
			if(allOK)
				break;
		}
		
		// seeds in current local minimum
		// get the earliest rising label in each connection
		HashSet<Integer> seedLmHash = new HashSet<>();				// checked
		for(Entry<Integer, ArrayList<int[]>> entry:cc.entrySet()) {
			ArrayList<int[]> points = entry.getValue();
			HashSet<Integer> sp00 = new HashSet<>();
			for(int[] p:points) {
				int label = spMap[p[0]][p[1]];
				if(label>0)
					sp00.add(label);
			}
			int ix = 0;
			float min = Float.MAX_VALUE;
			ArrayList<Integer> sp01 = new ArrayList<>(sp00);
			Collections.sort(sp01, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					if(o1<o2)
						return -1;
					else if(o1>o2)
						return 1;
					else
						return 0;
				}
				
			});
			
			
			for(int label:sp01) {
				if(riseX[label-1]<min) {
					ix = label;
					min = riseX[label-1];
				}
			}
			seedLmHash.add(ix);
		}
		
		// remove weak local maximum
		// check whether a seed is valid
		// start searching from earliest one
		// shuffle the order first
		ArrayList<Integer> seedLm = new ArrayList<>(seedLmHash);		// checked
		Collections.sort(seedLm, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				if(o1<o2)
					return -1;
				else if(o1>o2)
					return 1;
				else
					return 0;
			}
			
		});
		
		

		ArrayList<RiseXX> sortList = new ArrayList<>();
		for(int i=0;i<seedLm.size();i++) {
			sortList.add(new RiseXX(i,riseX[seedLm.get(i)-1]));
		}
		Collections.sort(sortList, new Comparator<RiseXX>() {
			@Override
			public int compare(RiseXX e1, RiseXX e2) {
				if(e1.value<e2.value)
					return 1;
				else if (e1.value>e2.value)
					return -1;
				else
					return 0;
			}				
		});
		ArrayList<Integer> seedOrd = new ArrayList<>();
		for(int i=0;i<sortList.size();i++) {					// sort order	checked
			seedOrd.add(sortList.get(i).key);
		}
		
		int[] lmSel = new int[seedLmHash.size()];
		int nSeed = 1;
		
		ArrayList<Integer> seedLm1 = new ArrayList<>(seedLm);
		for(int i=0;i<seedLm.size();i++) {
			if((i+1)%10==0)
				System.out.println(i+1);
			// center label
			int idxCenter = seedLm.get(seedOrd.get(i));			// it's a label, should -1 for index
			float riseCenter = riseX[idxCenter-1];
			
			// find rising time of labels less than riseCenter + maxRiseUnc
			ArrayList<Integer> idxMemCand = new ArrayList<>();	// checked label
			for(int t=0;t<riseX.length;t++) {
				if(riseX[t]<=riseCenter + maxRiseUnc) {
					idxMemCand.add(t+1);
				}
			}
			
			// construct connect list
			int len = idxMemCand.size();
			ArrayList<Integer> labelList = new ArrayList<>();	// for find connected events
			for(int n=0;n<=distMat.length;n++) {
				labelList.add(0);
			}
			for(int x = 0;x<len;x++) {
				int label1 = idxMemCand.get(x);
				for(int y = x;y<len;y++) {
					int label2 = idxMemCand.get(y);
					if(distMat[label1-1][label2-1]!=Float.MAX_VALUE) {
						ConnectedComponents.union_connect(label1,label2,labelList);
					}
				}
			}
			
			// give the connected super pixel labels
			ArrayList<Integer> idxMem = new ArrayList<>();
			int root = ConnectedComponents.union_find(idxCenter,labelList);
			for(int x=0;x<labelList.size();x++) {
				if(root == ConnectedComponents.union_find(x,labelList)) {
					idxMem.add(x);
				}
			}
			
			idxMem.retainAll(seedLm1);							// checked
			if(idxMem.size()==1) {
				lmSel[seedOrd.get(i)] = nSeed;
				nSeed++;
			}
			seedLm1.set(seedOrd.get(i), -1);
		}
		
		// the event each seed belongs to 
		int[] spEvt = new int[nSp];								// checked
		for(int i=0;i<seedLm.size();i++) {
			spEvt[seedLm.get(i)-1] = lmSel[i];	// index = label-1 , seedLm: label     i:index
		}		
				
//		try {
//			FileOutputStream f = null;
//			ObjectOutputStream o = null;
//			
//			f = new FileOutputStream(new File("D:\\New Folder212\\" + "spEvt.txt"));
//			o = new ObjectOutputStream(f);
//			o.writeObject(spEvt);
//			o.close();
//			f.close();
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		
		int[][] evtMemCMap = new int[W][H];						// checked
		int[] evtMemC = null;
		if(stg) {
			evtGrowLm(spEvt,distMat,riseX,spMap);
		}else {
			EvtGrowResult evtResult = evtGrowLm1(spEvt,distMat,cDelay,spMap);
			int[] evtMem = evtResult.evtMem;
			evtMemC = evtResult.evtMemC;
			for(int i=0;i<spEvt.length;i++)
				spEvt[i] = evtMem[i];
			
			for(int i=0;i<evtMemC.length;i++) {
				ArrayList<int[]> points = spLst.get(i+1);
				for(int[] p:points) {
					evtMemCMap[p[0]][p[1]] = evtMemC[i];
				}
			}
		}
		
		// gather events 
		HashMap<Integer,ArrayList<int[]>> pixLst = label2idx(spMap);
		HashSet<Integer> evt0 = new HashSet<>();
		for(int i=0;i<spEvt.length;i++) {
			if(spEvt[i]>0)
				evt0.add(spEvt[i]);
		}
		int cnt = 1;											// The problem may be in the spEvt
		for(int evt0Ele : evt0) {								// checked
			ArrayList<Integer> idx = new ArrayList<>();
			for(int i=0;i<spEvt.length;i++) {
				if(spEvt[i]==evt0Ele)
					idx.add(i+1);	// +1 for label
			}
			for(int label:idx) {
				ArrayList<int[]> pix0 = pixLst.get(label);
				for(int[] p:pix0) {
					evtMap[p[0]][p[1]] = cnt;
				}
			}
			cnt++;
		}
		
//		try {
//			FileOutputStream f = null;
//			ObjectOutputStream o = null;
//			
//			f = new FileOutputStream(new File("D:\\New Folder212\\" + "evtMap.txt"));
//			o = new ObjectOutputStream(f);
//			o.writeObject(evtMap);
//			o.close();
//			f.close();
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		return new RiseMapResult(evtMap,evtMemC,evtMemCMap);
	}
	
	/**
	 * @author Xuelong Mi
	 * To transmit the results
	 */
	static class RiseMapResult{
		int[][] evtMap;
		int[] evtMemC;
		int[][] evtMemCMap;
		public RiseMapResult(int[][] evtMap, int[] evtMemC, int[][] evtMemCMap) {
			this.evtMap = evtMap;
			this.evtMemC = evtMemC;
			this.evtMemCMap = evtMemCMap;
		}
	}

	/**
	 * grow seed, find continuous regions
	 * @param spEvt array to show the event which super pixels belong to
	 * @param distMat the distance map of super pixels
	 * @param cDelay the slowest propagation
	 * @param spMap the super pixels map
	 * @return
	 */
	private static EvtGrowResult evtGrowLm1(int[] spEvt, float[][] distMat, int cDelay, int[][] spMap) {
		int nSp = spEvt.length;								// checked
		int nSeed = Integer.MIN_VALUE;
		int[] evtMem = new int[nSp];
		int[] evtCan = new int[nSp];
		float[] evtDist = new float[nSp];
		for(int n=0;n<nSp;n++) {
			nSeed = Math.max(nSeed, spEvt[n]);
			evtMem[n] = spEvt[n];
			evtCan[n] = -1;
			evtDist[n] = Float.MAX_VALUE;
		}
		
		
		// init neighbors
		for(int i=1;i<=nSeed;i++) {
			int idx = 0;		// each lm has one sp, index	checked
			for(int n=0;n<nSp;n++) {
				if(spEvt[n]==i) {
					idx = n;
					break;
				}
			}
			float[] tmp = new float[distMat.length];
			HashSet<Integer> neib0 = new HashSet<>();

			for(int j=0;j<distMat.length;j++) {					// checked
				tmp[j] = distMat[idx][j];
				if(distMat[idx][j]<Float.MAX_VALUE)
					neib0.add(j);
			}
			
			HashSet<Integer> removeSet = new HashSet<>();		// checked
			for(int n:neib0) {
				if(evtMem[n]!=0)
					removeSet.add(n);
			}
			neib0.removeAll(removeSet);
			
			for(int n:neib0) {		// keep smallest distance	checked
				float dist0 = tmp[n];
				float distNow = evtDist[n];
				int canNow = evtCan[n];
				if(dist0<distNow)
					canNow = i;
				distNow = Math.min(dist0, distNow);
				evtCan[n] = canNow;
				evtDist[n] = distNow;
			}
		}
		
		// add one by one, small distance first
		// some super pixels may not be reachable by seeds
		while(true) {
			float x = Float.MAX_VALUE;		// checked
			int ix = 0;
			for(int n=0;n<nSp;n++) {
				if(evtDist[n]<x) {
					x = evtDist[n];
					ix = n;
				}
			}
			
			if(x == Float.MAX_VALUE)
				break;
			
			evtMem[ix] = evtCan[ix];
			evtDist[ix] = Float.MAX_VALUE;
			evtCan[ix] = -1;
			
			float[] tmp = new float[distMat.length];
			HashSet<Integer> neib0 = new HashSet<>();
			for(int n=0;n<distMat.length;n++) {
				tmp[n] = distMat[ix][n];
				if(tmp[n]<Float.MAX_VALUE)
					neib0.add(n);
			}
			HashSet<Integer> removeSet = new HashSet<>();		// checked
			for(int n:neib0) {
				if(evtMem[n]!=0)
					removeSet.add(n);
			}
			neib0.removeAll(removeSet);							// checked
			for(int n : neib0) {
				float dist0 = tmp[n];
				float distNow = evtDist[n];
				int canNow = evtCan[n];
				if(dist0<distNow)
					canNow = evtMem[ix];
				distNow = Math.min(dist0, distNow);
				evtCan[n] = canNow;
				evtDist[n] = distNow;
			}
		}
		
		// partition by continuity								// checked
		float[][] A = new float[nSeed][nSeed];
		for(int x=0;x<nSeed;x++) {
			for(int y=0;y<nSeed;y++) {
				A[x][y] = Float.MAX_VALUE;
			}
		}
		
		ArrayList<int[]> points = new ArrayList<>();			// checked
		for(int x=0;x<distMat.length;x++) {
			for(int y=0;y<distMat.length;y++) {
				if(distMat[x][y]>0 && distMat[x][y]!=Float.MAX_VALUE) {
					points.add(new int[] {x,y});
				}
			}
		}
		for(int[] p:points) {									// checked
			int x = p[0];
			int y = p[1];
			int evta = evtMem[x]-1;
			int evtb = evtMem[y]-1;
			if(evta>=0 && evtb>=0) {
				A[evta][evtb] = Math.min(A[evta][evtb], distMat[x][y]);
			}
		}
		
		boolean[][] B = new boolean[nSeed][nSeed];				// checked
		for(int x=0;x<nSeed;x++) {
			for(int y=0;y<nSeed;y++) {
				if(A[x][y]>cDelay)
					A[x][y] = Float.MAX_VALUE;
				if(A[x][y]<Float.MAX_VALUE)
					B[x][y] = true;
				if(x==y)
					B[x][y] = true;
			}
		}
		
		for(int x=0;x<nSeed;x++) {								// checked
			for(int y=0;y<nSeed;y++) {
				B[x][y] |= B[y][x];
			}
		}
		
		int[] evtMemC = new int[nSp];							// checked
		HashMap<Integer, ArrayList<Integer>> cc = graphComponent(B);
		for(Entry<Integer, ArrayList<Integer>>entry :cc.entrySet()) {
			ArrayList<Integer> cc0 = entry.getValue();
			for(int i:cc0) {
				for(int j=0;j<evtMem.length;j++) {
					if(evtMem[j]==i+1)	// i is index, i+1 is label 
						evtMemC[j] = entry.getKey();
				}
			}
		}
		
		return new EvtGrowResult(evtMem,evtMemC);
	}

	/**
	 * @author Xuelong Mi
	 * To transmit the results
	 */
	static class EvtGrowResult{
		int[] evtMem;
		int[] evtMemC;
		public EvtGrowResult(int[] evtMem, int[] evtMemC) {
			this.evtMem = evtMem;
			this.evtMemC = evtMemC;
			
		}
	}
	
	/**
	 * To get the connected component of the boolean matrix
	 * @param b
	 * @return
	 */
	private static HashMap<Integer, ArrayList<Integer>> graphComponent(boolean[][] b) {
		HashMap<Integer, ArrayList<Integer>> cc = new HashMap<>();
		boolean[] marked = new boolean[b.length];
		
		int cnt = 1;
		for(int i=0;i<b.length;i++) {
			if(!marked[i]) {
				ArrayList<Integer> l = dfs(b,i,marked);
				cc.put(cnt, l);
				cnt++;
			}
		}
		
		return cc;
	}

	/**
	 * DFS
	 * @param b
	 * @param i
	 * @param marked
	 * @return
	 */
	private static ArrayList<Integer> dfs(boolean[][] b, int i, boolean[] marked) {
		ArrayList<Integer> l = new ArrayList<>();
		Queue<Integer> q = new LinkedList<>();
		q.add(i);
		
		while(!q.isEmpty()) {
			int top = q.poll();
			marked[top] = true;
			l.add(top);
			for(int j=0;j<b.length;j++) {
				if(!marked[j] && (b[top][j] || b[j][top] ))
					q.add(j);
			}
		}
		
		return l;
	}

	/**
	 * Assign neighbor super pixels to event seeds
	 * @param spEvt array to show which event the super pixel belongs to
	 * @param distMat the distance map of super pixels
	 * @param rise0 the rising time array
	 * @param spMap the super pixels map
	 */
	private static void evtGrowLm(int[] spEvt, float[][] distMat, float[] rise0, int[][] spMap) {
		HashSet<Integer> spVec0 = new HashSet<>();
		for(int element:spEvt) {							// checked
			if(element>0 && element!=-1)
				spVec0.add(element);
		}
		
//		DeBugClass.updateLabelAndShow(spLst,spEvt);
		
		ArrayList<Integer> spVec = new ArrayList<>(spVec0);
		int nSeed = spVec.size();
		int nSp = rise0.length;
		
		// seed data structure
		// may use results when removing weak seeds
		HashMap<Integer,ArrayList<Integer>> seedNeibLst = new HashMap<>();
		HashMap<Integer,ArrayList<Integer>> seedLst = new HashMap<>();
		for(int ii=0;ii<nSeed;ii++) {
			int element = spVec.get(ii);
			ArrayList<Integer> idx = new ArrayList<>();
			for(int i=0;i<spEvt.length;i++) {
				if(spEvt[i]==element)
					idx.add(i);								// index
			}
			seedLst.put(ii, idx);
			HashSet<Integer> neib0 = new HashSet<>();		// checked
			for(int idxEle:idx) {
				for(int i=0;i<distMat.length;i++) {
					if(distMat[idxEle][i]<Float.MAX_VALUE) {
						neib0.add(i);
					}
				}
			}
//			for(int i:idx) {
//				neib0.remove(i);
//			}
			neib0.removeAll(idx);
			seedNeibLst.put(ii, new ArrayList<Integer>(neib0));
		}
		
		// randomize order
		ArrayList<RiseXX> sortList = new ArrayList<>();
		for(int i=0;i<nSp;i++) {
			sortList.add(new RiseXX(i,rise0[i]));
		}
		Collections.sort(sortList, new Comparator<RiseXX>() {
			@Override
			public int compare(RiseXX e1, RiseXX e2) {
				if(e1.value<e2.value)
					return -1;
				else if (e1.value>e2.value)
					return 1;
				else
					return 0;
			}				
		});
		float[] risex = new float[sortList.size()];			// checked
		int[] spOrd = new int[sortList.size()];
		for(int i=0;i<sortList.size();i++) {
			risex[i] = sortList.get(i).value;
			spOrd[i] = sortList.get(i).key;
		}
		
		int n0 = 0;
		for(int i=1;i<sortList.size();i++) {				// checked
			if(risex[i] != risex[i-1]) {
				int n1 = i-1;
				if(n1>n0) {
					shuffle(spOrd,n0,n1);
				}
				n0 = i;
			}
		}
		
		
		// assign pixels to seeds
		HashSet<Integer> wtLst = new HashSet<>();
		for(int i=0;i<nSp;i++) {
			if((i+1)%1000 == 0)
				System.out.println(i+1);
			
			int idx = spOrd[i];
			if(spEvt[idx]>0)
				continue;
			
			boolean suc = false;									// checked
			float[] dist00 = new float[nSeed];
			for(int j=0;j<nSeed;j++) {
				dist00[j] = Float.MAX_VALUE;
				ArrayList<Integer> neib0 = seedNeibLst.get(j);
				if(neib0.size()>0) {
					if(neib0.contains(idx)) {
						suc = true;
						float min = Float.MAX_VALUE;
						for(int k:seedLst.get(j)) {
							min = Math.min(min, distMat[k][idx]);
						}
						dist00[j] = min;
					}
				}
			}
			if(suc) {
				float min = Float.MAX_VALUE;				// checked
				int jj = 0;
				for(int j=0;j<nSeed;j++) {
					if(dist00[j]<min) {
						min = dist00[j];
						jj=j;
					}
				}
				spEvt[idx] = spVec.get(jj);
				//---------------------------------------test----------------------------------------------
//				DeBugClass.updateLabelAndShow(spLst,spEvt);
				
				ArrayList<Integer> seedLstElement = seedLst.get(jj);		// checked
				if(!seedLstElement.contains(idx))
					seedLstElement.add(idx);
				ArrayList<Integer> neibNew = new ArrayList<>();
				for(int j=0;j<distMat.length;j++) {
					if(distMat[idx][j]!=Float.MAX_VALUE)
						neibNew.add(j);
				}
				HashSet<Integer> unionNeib = new HashSet<>(seedNeibLst.get(jj));
				unionNeib.addAll(neibNew);
				unionNeib.removeAll(seedLstElement);
				seedNeibLst.put(jj, new ArrayList<>(unionNeib));
				
				// revisit un-decided super pixels
				for(int ee=0;ee<nSp;ee++) {
					boolean suc1 = false;
					for(int idx2:wtLst) {
						idx = idx2;
						boolean suc2 = false;
						float[] dist01 = new float[nSeed];
						for(int j=0;j<nSeed;j++) {					// checked
							dist01[j] = Float.MAX_VALUE;
							ArrayList<Integer> neib0 = seedNeibLst.get(j);
							if(neib0.size()>0) {
								if(neib0.contains(idx)) {
									suc2 = true;
									suc1 = true;
									min = Float.MAX_VALUE;
									for(int k:seedLst.get(j)) {
										min = Math.min(min, distMat[k][idx]);
									}
									dist01[j] = min;
								}
							}
						}
						if(suc2) {									 // checked
							min = Float.MAX_VALUE;
							for(int j=0;j<nSeed;j++) {
								if(dist01[j]<min) {
									min = dist01[j];
									jj=j;
								}
							}
							spEvt[idx] = spVec.get(jj);
							seedLstElement = seedLst.get(jj);
							if(!seedLstElement.contains(idx))
								seedLstElement.add(idx);
							neibNew = new ArrayList<>();
							for(int j=0;j<distMat.length;j++) {
								if(distMat[idx][j]!=Float.MAX_VALUE)
									neibNew.add(j);
							}
							unionNeib = new HashSet<>(seedNeibLst.get(jj));
							unionNeib.addAll(neibNew);
							unionNeib.removeAll(seedLstElement);
							seedNeibLst.put(jj, new ArrayList<>(unionNeib));
							wtLst.remove(idx);
							break;
						}
					}
					if(!suc1)
						break;
				}
			}else {
				wtLst.add(idx);
			}
		}
		
	}

	/**
	 * shuffle the input array, the shuffle range is between n0 and n1.
	 * @param spOrd
	 * @param n0
	 * @param n1
	 */
	private static void shuffle(int[] spOrd, int n0, int n1) {
		Random rv = new Random();
		int len = n1-n0 + 1;
		for(int i=n0;i<=n1;i++) {
			int idx = rv.nextInt(len) + n0;
			if(idx!=i) {
				int tmp = spOrd[i];
				spOrd[i] = spOrd[idx];
				spOrd[idx] = tmp;
			}
		}
		
	}

	/**
	 * Merge the super pixels to events
	 * @param lblMapSO
	 * @param riseMap
	 * @param maxRiseDly1
	 * @param maxRiseDly2
	 * @param minOverRate
	 * @param dat
	 * @return
	 */
	private static int[][][] sp2evtStp1(int[][][] lblMapSO, int[][][] riseMap, int maxRiseDly1, int maxRiseDly2, float minOverRate, float[][][] dat) {
		int width = lblMapSO.length;
		int height = lblMapSO[0].length;
		int pages = lblMapSO[0][0].length;
		int[][][] lblMapS = new int[width][height][pages];
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				for(int k=0;k<pages;k++) {
					lblMapS[i][j][k] = lblMapSO[i][j][k];
				}
			}
		}
		HashMap<Integer,ArrayList<int[]>> spVoxLst = label2idx(lblMapS);
		
		
		int[] dh = new int[] {-1,0,1,-1,1,-1,0,1};
		int[] dw = new int[] {-1,-1,-1,0,0,1,1,1};
		int changeParameter = Math.max(width, height);
		int nSp = spVoxLst.size();
		float[] riseX = new float[nSp];
		for(Map.Entry<Integer,ArrayList<int[]>> entry:spVoxLst.entrySet()) {
			ArrayList<int[]> points = entry.getValue();
			int label = entry.getKey();
			long sum = 0;
			int cnt = 0;
			for(int[] p:points) {
				int t0 = riseMap[p[0]][p[1]][p[2]];
				if(t0>0) {
					sum += t0;
					cnt++;
				}
			}
			if(cnt>0)
				riseX[label-1] = (float) (sum/cnt);
			else
				riseX[label-1] = -1;
		}
		
		// begin with faster propagation
		int[] maxRiseDlyVec = new int[maxRiseDly2-maxRiseDly1+1];
		maxRiseDlyVec[0] = maxRiseDly1;
		for(int i=1;i<maxRiseDlyVec.length;i++) {
			maxRiseDlyVec[i] = maxRiseDlyVec[i-1]+1;
		}
		
		for(int maxRiseDly0:maxRiseDlyVec) {
			
			// spatial location of super pixel for conflicting
			HashMap<Integer, ArrayList<Integer>> spPixLst = new HashMap<>();
			for(Map.Entry<Integer,ArrayList<int[]>> entry:spVoxLst.entrySet()) {
				ArrayList<int[]> points = entry.getValue();
				int label = entry.getKey();
				HashSet<Integer> points2D = new HashSet<>();
				for(int[] p:points) {
					points2D.add(p[0]*changeParameter + p[1]);
				}
				ArrayList<Integer> l = new ArrayList<>(points2D);
				spPixLst.put(label, l);
			}
			
			// neighbors and conflicts
			HashMap<Integer, HashSet<Integer>> neibLst = new HashMap<>();
			HashMap<Integer, HashSet<Integer>> exldLst = new HashMap<>();
			for(int n=0;n<nSp;n++) {
				if(n%1000==0)
					System.out.println(n + "/" + nSp);
				int label = n+1;
				ArrayList<int[]> vox0 = spVoxLst.get(label);
				HashSet<Integer> neib0 = new HashSet<>();
				
				for(int i=0;i<dh.length;i++) {

					for(int[] p:vox0) {
						int px = Math.min(Math.max(p[0]+dw[i],0),width-1);
						int py = Math.min(Math.max(p[1]+dh[i],0),height-1);
						if(lblMapS[px][py][p[2]]>0 && lblMapS[px][py][p[2]]!=label) {

							int riseDif = Math.abs(riseMap[p[0]][p[1]][p[2]] - riseMap[px][py][p[2]]);
							if(riseDif<=maxRiseDly0)
								neib0.add(lblMapS[px][py][p[2]]);	// xGood, neib0 add xGood
						}
					}
				}
				neibLst.put(label, neib0);
			
				// conflicting SPs
				HashSet<Integer> u = new HashSet<>();
				ArrayList<Integer> ihw = spPixLst.get(label);
				
				for(int p:ihw) {
					int px = p/changeParameter;
					int py = p%changeParameter;
					for(int t=0;t<pages;t++) {
						int labelN = lblMapS[px][py][t];
						if(labelN>0 && labelN!=label) {
							u.add(labelN);
						}
					}
				}
				
				HashSet<Integer> e0 = new HashSet<>();
				
				for(int labelN:u) {
					ArrayList<Integer> ihw1 = new ArrayList<>(spPixLst.get(labelN));
					int n1 = ihw1.size();
					ihw1.retainAll(ihw);	// intersect	// int[] should be converted to integer then do retain	
					double nInter = ihw1.size();
					if(nInter/(double)ihw.size()>minOverRate ||nInter/(double)n1>minOverRate) {
						e0.add(labelN);
					}
					
				}
				exldLst.put(label, e0);
			}
			
			// merge super pixels without conflict
			// earlier one fist
			ArrayList<RiseXX> sortList = new ArrayList<>();
			int[] evtLb1 = new int[nSp];
			for(int i=0;i<nSp;i++) {
				evtLb1[i] = -1;
				sortList.add(new RiseXX(i,riseX[i]));
			}
			Collections.sort(sortList, new Comparator<RiseXX>() {
				@Override
				public int compare(RiseXX e1, RiseXX e2) {
					if(e1.value<e2.value)
						return -1;
					else if (e1.value>e2.value)
						return 1;
					else
						return 0;
				}				
			});
			for(RiseXX kk:sortList) {
				int spSeed = kk.key;
				int labelS = spSeed + 1;
				if(evtLb1[spSeed]!=-1)
					continue;
				
				
				HashSet<Integer> newSp = new HashSet<>();
				newSp.add(labelS);
				for(int i:newSp) {
					evtLb1[i-1] = labelS;
				}
				
				while(true) {
					HashSet<Integer> newSp1 = new HashSet<>();
					ArrayList<Integer> newSpList = new ArrayList<>(newSp);
					Collections.sort(newSpList,new Comparator<Integer>() {

						@Override
						public int compare(Integer e1, Integer e2) {
							if(e1<e2)
								return -1;
							else if(e1>e2)
								return 1;
							else
								return 0;
						}
						
					});
					for(int label:newSpList) {
						HashSet<Integer> neib0 = neibLst.get(label);
						for(int labelN:neib0) {
							if(evtLb1[labelN-1]==-1) {
								HashSet<Integer> exld0 = exldLst.get(labelN);
								boolean sum =  true;
								for(int labelE:exld0) {
									if(evtLb1[labelE-1]==labelS) {
										sum = false;
										break;
									}
								}
								if(sum) {
									newSp1.add(labelN);
								}
							}
						}
					}
					if(newSp1.size()==0) {
						break;
					}
					for(int label:newSp1) {
						evtLb1[label-1] = labelS;
					}
					newSp = newSp1;
				}
				
			}
			
			// update super pixel map and rising time
			HashMap<Integer,ArrayList<Integer>> xx = new HashMap<>();
			for(int i=0;i<nSp;i++) {
				if(evtLb1[i] !=-1) {
					int label = i+1;
					ArrayList<Integer> l = xx.get(evtLb1[i]);
					if(l==null) {
						l = new ArrayList<>();
					}
					l.add(label);
					xx.put(evtLb1[i],l);
				}
			}
			nSp = xx.size();
			float[] riseX1 =  new float[nSp];
			int[][][] lblMapS1 = new int[width][height][pages];
			
			int cnt = 0;
			for(int i=1;i<=evtLb1.length;i++) {
				ArrayList<Integer> labels = xx.get(i);
				if(labels!=null) {
					float min = Float.MAX_VALUE;
					for(int label:labels) {
						ArrayList<int[]> points = spVoxLst.get(label);
						min = Math.min(min, riseX[label-1]);
						for(int[] p:points) {
							lblMapS1[p[0]][p[1]][p[2]] = cnt+1;
						}
					}
					riseX1[cnt] = min;
					cnt++;
				}
			}
			
			
			riseX = riseX1;
			lblMapS = lblMapS1;
			spVoxLst = label2idx(lblMapS);
			System.out.println(spVoxLst.size());
			
		}
			
			
		
		
		
		return lblMapS;
	}
		
	/**
	 * @author Xuelong Mi
	 * To transmit results
	 */
	static class RiseXX{
		int key;
		float value;
		public RiseXX(int key, float value){
			this.key = key;
			this.value = value;
		}
	}

	/**
	 * Transfer super voxels to super events
	 * @param lblMapS
	 * @param spVoxLst
	 * @param svNeibResult
	 * @return
	 */
	private static int[][][] sv2se(int[][][] lblMapS, HashMap<Integer, ArrayList<int[]>> spVoxLst, SvNeibResult svNeibResult) {
		HashMap<Integer, HashSet<Integer>> neibLst = svNeibResult.neibLst;
		HashMap<Integer, HashSet<Integer>> exldLst = svNeibResult.exldLst;
		
		int width = lblMapS.length;
		int height = lblMapS[0].length;
		int pages = lblMapS[0][0].length;
		int nSp = spVoxLst.size();
		
		// find one super event in each connected component
		showTime();
		HashMap<Integer, ArrayList<int[]>> ccLst = ConnectedComponents.twoPassConnect3D(lblMapS);		// checked
		showTime();
		System.out.println(ccLst.size());
		int[] evtLb1 = new int[nSp];
		for(int i=0;i<nSp;i++) {
			evtLb1[i] = -1;
		}
		int nSe = 0;
		while(true) {
			// find largest connected component
			int x00 = 0;
			int nnMax = 0;
			for(Map.Entry<Integer,ArrayList<int[]>> entry:ccLst.entrySet()) {		// checked
				int num = entry.getValue().size();
				if(num>x00) {
					x00 = num;
					nnMax = entry.getKey();
				}
			}
			
			if(x00==0) {
				break;
			}
			
			// extract current component
			// !! connected component not the same as super voxel connectivity
			ArrayList<int[]> msk0 = ccLst.get(nnMax);
			HashSet<Integer> idxSv0 = new HashSet<>();
			for(int[] p:msk0) {
				idxSv0.add(lblMapS[p[0]][p[1]][p[2]]);
			}
			HashSet<Integer> idxSv00 = new HashSet<>();	// checked
			for(int label:idxSv0) {
				if(evtLb1[label-1]==-1) {
					idxSv00.add(label);
				}
			}
			idxSv0 = idxSv00;
			
			if(idxSv0.size()==0) {
				ccLst.put(nnMax, new ArrayList<int[]>());
				continue;
			}
			
			msk0 = new ArrayList<int[]>();					// checked
			for(int label:idxSv0) {
				msk0.addAll(spVoxLst.get(label));
			}
			ccLst.put(nnMax, msk0);
			
			nSe = nSe+1;
			if(nSe%100 == 0) {
				System.out.println("SE:" + nSe);
			}
			
			int rghs = Integer.MAX_VALUE;			// checked
			int rgws = Integer.MAX_VALUE;
			int rgts = Integer.MAX_VALUE;
			int rghe = Integer.MIN_VALUE;
			int rgwe = Integer.MIN_VALUE;
			int rgte = Integer.MIN_VALUE;
			for(int[] p:msk0) {
				rgws = Math.min(rgws, p[0]);
				rghs = Math.min(rghs, p[1]);
				rgts = Math.min(rgts, p[2]);
				rgwe = Math.max(rgwe, p[0]);
				rghe = Math.max(rghe, p[1]);
				rgte = Math.max(rgte, p[2]);
			}
			int W1 = rgwe-rgws+1;													// checked
			int H1 = rghe-rghs+1;
			int T1 = rgte-rgts+1;
			int[][][] mapS0 = new int[W1][H1][T1];
			for(int[] p:msk0) {
				mapS0[p[0]-rgws][p[1]-rghs][p[2]-rgts] = lblMapS[p[0]][p[1]][p[2]];
			}
			
			// find largest 2D region in this component as a new super event
			ArrayList<HashSet<Integer>> regLst = new ArrayList<>();						// checked
			ArrayList<Integer> regSz = new ArrayList<>();
			for(int t=0;t<T1;t++) {
				HashMap<Integer, ArrayList<int[]>> cc = ConnectedComponents.twoPassConnect2D(mapS0,t);
				if(cc.size()>0) {
					for(Map.Entry<Integer,ArrayList<int[]>> entry:cc.entrySet()) {
						HashSet<Integer> sp00 = new HashSet<>();
						for(int[] p:entry.getValue()) {
							sp00.add(mapS0[p[0]][p[1]][t]);
						}
						regLst.add(sp00);
						regSz.add(entry.getValue().size());
					}
				}
			}
			
			// super voxels in region with largest area									
			HashSet<Integer> se0 = null;
			int maxNum = Integer.MIN_VALUE;
			for(int i=0;i<regSz.size();i++) {
				if(regSz.get(i)>maxNum) {
					maxNum = regSz.get(i);
					se0 = regLst.get(i);
				}
			}
			
			// super voxels not used yet				// checked
			HashSet<Integer> svVec = new HashSet<>();	// super voxels in this super event
			HashSet<Integer> newSp = new HashSet<>();
			for(int label:se0) {
				if(evtLb1[label-1]==-1) {
					svVec.add(label);
					newSp.add(label);
					evtLb1[label-1] = nSe;
				}
			}
			
			// gather other super voxels
			while(true) {												// checked
				HashSet<Integer> newSp1 = new HashSet<>();
				for(int label:newSp) {
					HashSet<Integer> neib0 = neibLst.get(label);
					for(int labelN:neib0) {
						if(evtLb1[labelN-1]==-1) {
							HashSet<Integer> exld0 = exldLst.get(labelN);
							boolean sum =  true;
							for(int labelE:exld0) {
								if(evtLb1[labelE-1]==nSe) {
									sum = false;
									break;
								}
							}
							if(sum) {
								newSp1.add(labelN);
							}
						}
					}
				}
				if(newSp1.size()==0) {
					break;
				}
				for(int label:newSp1) {
					evtLb1[label-1] = nSe;
				}
				newSp = newSp1;
				svVec.addAll(newSp1);
			}
			
			// remove super voxels already used									// checked
			for(int label:svVec) {
				ArrayList<int[]> points = spVoxLst.get(label);
				for(int[] p:points) {
					int px = p[0] - rgws;
					int py = p[1] - rghs;
					int pt = p[2] - rgts;	// it says it should add Math.max(p,0) in case of outside
					px = Math.min(Math.max(0, px),W1-1);
					py = Math.min(Math.max(0, py),H1-1);
					pt = Math.min(Math.max(0, pt),T1-1);
					mapS0[px][py][pt] = 0;
				}
			}
			
			// update connect component map
			ccLst.put(nnMax, new ArrayList<int[]>());				// checked
			HashMap<Integer, ArrayList<int[]>> ccL1 = ConnectedComponents.twoPassConnect3D(mapS0);
//			System.out.println(nSe);
			int cnt = 0;
			for(Map.Entry<Integer,ArrayList<int[]>> entry:ccL1.entrySet()) {
				ArrayList<int[]> points =  entry.getValue();
				cnt++;
				for(int[] p:points) {
					p[0] += rgws;
					p[1] += rghs;
					p[2] += rgts;
				}
				if(cnt==1) {
					ccLst.put(nnMax,points);
//					System.out.println(ccLst.size());
				}else {
					ccLst.put(ccLst.size()+1,points);
				}
				
			}
//			System.out.println(ccLst.size());
		}
		
		// update super pixel map
//		HashMap<Integer,ArrayList<Integer>> xx = new HashMap<>();
//		for(int i=0;i<nSp;i++) {
//			if(evtLb1[i] !=-1) {
//				int label = i+1;
//				ArrayList<Integer> l = xx.get(evtLb1[i]);
//				if(l==null) {
//					l = new ArrayList<>();
//				}
//				l.add(label);
//				xx.put(evtLb1[i],l);
//			}
//		}
		
		HashSet<Integer> xx = new HashSet<>();			// checked
		for(int i=0;i<nSp;i++) {
			if(evtLb1[i]!=-1) {
				xx.add(i+1);	// add label
			}
		}
		
		
		
		
		int[][][] lblMapC1 = new int[width][height][pages];
//		int cnt = 1;
//		for(Map.Entry<Integer,ArrayList<Integer>> entry:xx.entrySet()) {
//			ArrayList<Integer> labels = entry.getValue();
//			for(int label:labels) {
//				ArrayList<int[]> points = spVoxLst.get(label);
//				for(int[] p:points) {
////					lblMapC1[p[0]][p[1]][p[2]] = cnt;
//					lblMapC1[p[0]][p[1]][p[2]] = entry.getKey();
//				}
//			}
////			cnt++;
//		}
		int cnt = 1;
		for(int label:xx) {
			ArrayList<Integer> idx = new ArrayList<>();
			for(int i=0;i<nSp;i++) {
				if(evtLb1[i] == label) {
					idx.add(i+1);
				}
			}
			for(int label2:idx) {
				ArrayList<int[]> points = spVoxLst.get(label2);
				for(int[] p:points) {
					lblMapC1[p[0]][p[1]][p[2]] = cnt;
				}
			}
			cnt++;
		}
		
		return lblMapC1;
		
	}

	/**
	 * Get the neighbor list of super pixels
	 * @param lblMapS
	 * @param spVoxLst
	 * @param riseMap
	 * @param maxRiseDly
	 * @param minOverRate
	 * @return
	 */
	private static SvNeibResult svNeib(int[][][] lblMapS, HashMap<Integer, ArrayList<int[]>> spVoxLst, int[][][] riseMap, int maxRiseDly, float minOverRate) {
		int nSp = spVoxLst.size();							// checked
		int width = lblMapS.length;
		int height = lblMapS[0].length;
		int pages = lblMapS[0][0].length;
		
		int changeParameter = Math.max(width, height);
		
//		int[] dh = new int[] {-1,0,1,-1,1,-1,0,1};				// checked
//		int[] dw = new int[] {-1,-1,-1,0,0,1,1,1};
		
		int[] dh = new int[] {0,-1,1,0};
		int[] dw = new int[] {-1,0,0,1};
		
		// spatial location of super pixel for conflicting
		HashMap<Integer, ArrayList<Integer>> spPixLst = new HashMap<>();
		for(Map.Entry<Integer,ArrayList<int[]>> entry:spVoxLst.entrySet()) {			// checked
			ArrayList<int[]> points = entry.getValue();
			int label = entry.getKey();
			HashSet<Integer> points2D = new HashSet<>();
			for(int[] p:points) {
				points2D.add(p[0]*changeParameter + p[1]);
			}
			ArrayList<Integer> l = new ArrayList<>(points2D);
			spPixLst.put(label, l);
		}
		
		// neighbors and conflicts
		HashMap<Integer, HashSet<Integer>> neibLst = new HashMap<>();
		HashMap<Integer, HashSet<Integer>> exldLst = new HashMap<>();
		for(int n=0;n<nSp;n++) {
			if(n%1000==0)
				System.out.println(n + "/" + nSp);
			
			int label = n+1;
			ArrayList<int[]> vox0 = spVoxLst.get(label);
			HashSet<Integer> neib0 = new HashSet<>();
			
			for(int i=0;i<dh.length;i++) {											// checked
//				HashSet<Integer> xGood = new HashSet<>();
				for(int[] p:vox0) {
					int px = Math.min(Math.max(p[0]+dw[i],0),width-1);				// checked
					int py = Math.min(Math.max(p[1]+dh[i],0),height-1);
					if(lblMapS[px][py][p[2]]>0 && lblMapS[px][py][p[2]]!=label) {

						int riseDif = Math.abs(riseMap[p[0]][p[1]][p[2]] - riseMap[px][py][p[2]]);
						if(riseDif<maxRiseDly)
							neib0.add(lblMapS[px][py][p[2]]);	// xGood, neib0 add xGood
					}
				}
			}
			
			neibLst.put(label, neib0);
			
			// conflicting SPs
			HashSet<Integer> u = new HashSet<>();						
			ArrayList<Integer> ihw = spPixLst.get(label);
			
			for(int p:ihw) {									// checked
				int px = p/changeParameter;
				int py = p%changeParameter;
				for(int t=0;t<pages;t++) {
					int labelN = lblMapS[px][py][t];
					if(labelN>0 && labelN!=label) {
						u.add(labelN);
					}
				}
			}
			
			HashSet<Integer> e0 = new HashSet<>();				// checked
			
			for(int labelN:u) {
				ArrayList<Integer> ihw1 = new ArrayList<>(spPixLst.get(labelN));
				int n1 = ihw1.size();
				ihw1.retainAll(ihw);	// intersect	// int[] should be converted to integer then do retain	
				double nInter = ihw1.size();
				if(nInter/(double)ihw.size()>minOverRate ||nInter/(double)n1>minOverRate) {
					e0.add(labelN);
				}
				
			}
			exldLst.put(label, e0);
		}
		
		SvNeibResult result = new SvNeibResult(neibLst,exldLst);
		return result;
	}
	
	/**
	 * @author Xuelong Mi
	 * To transmit the results
	 */
	static class SvNeibResult{
		HashMap<Integer, HashSet<Integer>> neibLst = null;
		HashMap<Integer, HashSet<Integer>> exldLst = null;
		
		public SvNeibResult(HashMap<Integer, HashSet<Integer>> neibLst,HashMap<Integer, HashSet<Integer>> exldLst) {
			this.neibLst = neibLst;
			this.exldLst = exldLst;
		}
	}

	/**
	 * get the median of nan value
	 * @param riseX
	 * @return
	 */
	private static float[] nanMedian(int[][] riseX) {
		int nLm = riseX.length;
		int levels = riseX[0].length;
		
		float[] result = new float[nLm];
		
		for(int n=0;n<nLm;n++) {
			ArrayList<Float> nanList = new ArrayList<>();
			for(int i=0;i<levels;i++) {
				if(riseX[n][i]!=-1) {
					float r = riseX[n][i];
					nanList.add(r);
				}
			}
			Collections.sort(nanList, new Comparator<Float>() {
				@Override
				public int compare(Float o1, Float o2) {
					if(o1<o2)
						return -1;
					else
						return 1;
				}
				
			});
			int len = nanList.size();
			if(len==0)
				result[n] = -1;
			else if(len%2==0)
				result[n] = (nanList.get(len/2-1) + nanList.get(len/2))/2;
			else
				result[n] = nanList.get(len/2);
		}
		return result;
	}

	/**
	 * form the location map through label map
	 * @param labelMap
	 * @return
	 */
	public static HashMap<Integer,ArrayList<int[]>> label2idx(int[][][] labelMap){
		HashMap<Integer, ArrayList<int[]>> map = new HashMap<>();
		int width = labelMap.length;
		int height = labelMap[0].length;
		int pages = labelMap[0][0].length;
		
		for(int k=0;k<pages;k++) {
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					int label = labelMap[i][j][k];
					if(label>0) {
						ArrayList<int[]> l = map.get(label);
						if(l==null)
							l = new ArrayList<>();
						l.add(new int[] {i,j,k});
						map.put(label, l);
					}
				}
			}
		}
		
		
		return map;
	}
	
	/**
	 * form the location map through label map
	 * @param labelMap
	 * @return
	 */
	private static HashMap<Integer,ArrayList<int[]>> label2idx(int[][] labelMap){
		HashMap<Integer, ArrayList<int[]>> map = new HashMap<>();
		int width = labelMap.length;
		int height = labelMap[0].length;

		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				int label = labelMap[i][j];
				if(label>0) {
					ArrayList<int[]> l = map.get(label);
					if(l==null)
						l = new ArrayList<>();
					l.add(new int[] {i,j});
					map.put(label, l);
				}
			}
		}
		
		return map;
	}
		
	/** 
	 * Report the progress.
	 */
	protected void process(List<Integer> chunks) {
		int value = chunks.get(chunks.size()-1);
		String str = "";
		int total = 4;
		switch(value) {
			case 1:
				str = "Read the Data " + value + "/" + total;
				break;
			case 2:
				str = "Find the Events " + value + "/" + total;
				break;
			case 3:
				str = "Get the Feature " + value + "/" + total;
				break;
			case 4:
				str = "Save the Data " + value + "/" + total;
				break;
		}
		jLabel.setText(str);
	}
		
	/** 
	 * Adjust the interface, save the status, and let the interface show the super voxels
	 */
	@Override
	protected void done() {
		frame.setVisible(false);
//		JOptionPane.showMessageDialog(null, "Step3 Finish!");
		imageDealer.left.nextButton.setEnabled(true);
		imageDealer.left.backButton.setEnabled(true);
		imageDealer.left.jTP.setEnabledAt(3, true);
		
		if(imageDealer.left.jTPStatus<3) {
			imageDealer.left.jTPStatus = Math.max(imageDealer.left.jTPStatus, 3);
			imageDealer.right.typeJCB.addItem("Step3a: Super Events");
			imageDealer.right.typeJCB.addItem("Step3b: Events");
		}
//		imageDealer.right.typeJCB.setSelectedIndex(3);
		
		try {
			imageDealer.label = this.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				imageDealer.dealImage();
				imageDealer.imageLabel.repaint();
			}
			
		}).start();
		imageDealer.saveStatus();
		imageDealer.running = false;
	}
	


	/**
	 * show the current time
	 */
	static void showTime() {
		end = System.currentTimeMillis();
		System.out.println((end-start) + "ms");
		start = end;
	}
	
	/**
	 * Get the simple features of events
	 * @param datOrg orignal data matrix
	 * @param evtLst event list
	 * @param opts parameters
	 * @return
	 */
	public static QuickFeatureResult getFeatureQuick(float[][][] datOrg, HashMap<Integer, ArrayList<int[]>> evtLst, Opts opts) {
		int W = datOrg.length;
		int H = datOrg[0].length;
		int T = datOrg[0][0].length;
		
		int[][][] evtMap = new int[W][H][T];
		for(Entry<Integer, ArrayList<int[]>> entry:evtLst.entrySet()) {
			int label = entry.getKey();
			ArrayList<int[]> points = entry.getValue();
			for(int[] p:points) {
				evtMap[p[0]][p[1]][p[2]] = label;
			}
		}
		
		float[][][] dat = new float[W][H][T];
		float[][][] datx = new float[W][H][T];
		
		if(opts.usePG) {
			for(int i=0;i<W;i++) {
				for(int j=0;j<H;j++) {
					for(int k=0;k<T;k++) {
						dat[i][j][k] = datOrg[i][j][k]*datOrg[i][j][k];
						datx[i][j][k] = datOrg[i][j][k]*datOrg[i][j][k];
						if(evtMap[i][j][k]>0)
							datx[i][j][k] = -1;
					}
				}
			}
		}else {
			for(int i=0;i<W;i++) {
				for(int j=0;j<H;j++) {
					for(int k=0;k<T;k++) {
						dat[i][j][k] = datOrg[i][j][k];
						datx[i][j][k] = datOrg[i][j][k];
						if(evtMap[i][j][k]>0)
							datx[i][j][k] = -1;
					}
				}
			}
		}
		
		System.out.println("Imputing ...");
		imputeMov(datx);
		int Tww = Math.min(Math.round((float)T/4), opts.movAvgWin);
		
		// bias in moving average minimum
		float bbm = MinMoveMean.getBias(Tww, T, 1);
		
		// ftsLst
		FtsLst ftsLst = new FtsLst();
		
		int maxLabel = 0;		// since some label do not exists
		for(int i:evtLst.keySet()) {
			maxLabel = Math.max(i, maxLabel);
		}
		
		float[][] dffMatExt = new float[maxLabel][T];
		for(int i=0;i<maxLabel;i++) {
			for(int t=0;t<T;t++) {
				dffMatExt[i][t] = -1;
			}
		}
		
		for(int i = 1;i<=maxLabel;i++) {
			if(i%100==0)
				System.out.println(i + "/" + maxLabel);
			
			ArrayList<int[]> pix0 = evtLst.get(i);
			
			if(pix0==null || pix0.size()==0)
				continue;
			
			int rghs = Integer.MAX_VALUE;
			int rgws = Integer.MAX_VALUE;
			int rgts = Integer.MAX_VALUE;
			int rghe = Integer.MIN_VALUE;
			int rgwe = Integer.MIN_VALUE;
			int rgte = Integer.MIN_VALUE;
			
			for(int[] p:pix0) {
				rgws = Math.min(rgws, p[0]);
				rghs = Math.min(rghs, p[1]);
				rgts = Math.min(rgts, p[2]);
				rgwe = Math.max(rgwe, p[0]);
				rghe = Math.max(rghe, p[1]);
				rgte = Math.max(rgte, p[2]);
			}
			int its = rgts;
			int ite = rgte;			
			
			rgws = Math.max(rgws-1, 0);
			rghs = Math.max(rghs-1, 0);
			rgts = Math.max(rgts-1, 0);
			rgwe = Math.min(rgwe+1, W-1);
			rghe = Math.min(rghe+1, H-1);
			rgte = Math.min(rgte+1, T-1);
			
			// in xy plane, get the sum of label==i
//			System.out.println("Getting the sigxy");
			int[][] sigxy = new int[rgwe-rgws+1][rghe-rghs+1];
			boolean[] sigz = new boolean[T];
			for(int x=rgws;x<=rgwe;x++) {
				for(int y=rghs;y<=rghe;y++) {
					int sum = 0;
					for(int t=0;t<T;t++) {
						if(evtMap[x][y][t]==i)
							sum++;
					}
					sigxy[x-rgws][y-rghs] = sum;
				}
			}
			
			// in z axis, get the status whether label>0
//			System.out.println("Getting the sigz");
			int cntSta = 0;
			for(int t=0;t<T;t++) {
				boolean status = false;
				for(int x=rgws;x<=rgwe;x++) {
					for(int y=rghs;y<=rghe;y++) {
						if(evtMap[x][y][t]>0) {
							status = true;
							break;
						}
					}
					if(status)
						break;
				}
				sigz[t] = status;
				if(status)
					cntSta++;
			}
			
			// in z axis, get the status whether label==i
			if(cntSta>T/2) {
				for(int t=0;t<T;t++) {
					boolean status = false;
					for(int x=rgws;x<=rgwe;x++) {
						for(int y=rghs;y<=rghe;y++) {
							if(evtMap[x][y][t]==i) {
								status = true;
								break;
							}
						}
						if(status)
							break;
					}
					sigz[t] = status;
				}
			}
			
			float[] charxIn1 = new float[T];
			for(int t=0;t<T;t++) {
				float sum = 0;
				int cnt = 0;
				for(int x=rgws;x<=rgwe;x++) {
					for(int y=rghs;y<=rghe;y++) {
						if(sigxy[x-rgws][y-rghs]>0) {
							cnt++;
							sum += dat[x][y][t];
						}	
					}
				}
				charxIn1[t] = sum/cnt;
			}
			
			// charx1
//			System.out.println("Getting the charx1");
			float[] charx1 = curvePolyDeTrend(charxIn1, sigz, opts.correctTrend);
			float sigma1 = MinMoveMean.getNoiseSigmal(charx1);
			
			float charxBg1 = MinMoveMean.minMoveMean(charx1, Tww);
			charxBg1 = charxBg1 - bbm*sigma1 - opts.bgFluo*opts.bgFluo;
			float[] dff1 = new float[T];
			for(int t=0;t<T;t++) {
				dff1[t] = (charx1[t] - charxBg1)/charxBg1;
			}
			float sigma1dff = MinMoveMean.getNoiseSigmal(dff1);
			
			// dff without other events
//			System.out.println("Getting the charx2");
			float[] charxIn2 = new float[T];
			for(int t=0;t<T;t++) {
				float sum = 0;
				int cnt = 0;
				for(int x=rgws;x<=rgwe;x++) {
					for(int y=rghs;y<=rghe;y++) {						// bring current event back
						if(sigxy[x-rgws][y-rghs]>0) {
							float curDat = 0;
							if(evtMap[x][y][t]==i) {
								curDat= dat[x][y][t];
							}else
								curDat= datx[x][y][t];
							if(curDat!=-1) {
								sum += curDat;
								cnt++;
							}
						}
					}
				}
				charxIn2[t] = sum/cnt;
			}
			float[] charx2 = curvePolyDeTrend(charxIn2, sigz, opts.correctTrend);
			float charxBg2 = MinMoveMean.minMoveMean(charx2, Tww);
			charxBg2 = charxBg2 - bbm*sigma1 - opts.bgFluo * opts.bgFluo;
			float[] dff2 = new float[T];
			for(int t=0;t<T;t++) {
				dff2[t] = (charx2[t]-charxBg2)/charxBg2;
			}
			
			// for p values
//			System.out.println("Getting the p value");
			float[] dff2Sel = new float[rgte-rgts+1];
			for(int t=rgts;t<=rgte;t++) {
				dff2Sel[t-rgts] = dff2[t];
			}
			int tMax = 0;
			float dffMax2 = -Float.MAX_VALUE;
			for(int t=0;t<dff2Sel.length;t++) {
				if(dffMax2<dff2Sel[t]) {
					tMax = t;
					dffMax2 = dff2Sel[t];
				}
			}
			float dff2SelMinPre = Float.MAX_VALUE;
			float dff2SelMinPos = Float.MAX_VALUE;
			for(int t=0;t<=tMax;t++) {
				dff2SelMinPre = Math.min(dff2Sel[t],dff2SelMinPre);
			}
			for(int t=tMax;t<dff2Sel.length;t++) {
				dff2SelMinPos = Math.min(dff2Sel[t],dff2SelMinPos);
			}
			float xMinPre = Math.max(dff2SelMinPre, sigma1dff);
			float xMinPost = Math.max(dff2SelMinPos, sigma1dff);
			float dffMaxZ = Math.max((dffMax2-xMinPre+dffMax2-xMinPost)/sigma1dff/2, 0);
//			System.out.println(dffMaxZ);
			NormalDistribution normal = new NormalDistribution();
			float dffMaxPval = (float) (1 - normal.cumulativeProbability(dffMaxZ));
//			System.out.println(dffMaxPval);
			
			// extend time window
//			System.out.println("Extending the time");
			boolean[] sigxOthers = new boolean[T];
			for(int t=0;t<T;t++) {
				boolean status = false;
				for(int x=rgws;x<=rgwe;x++) {
					for(int y=rghs;y<=rghe;y++) {
						if(evtMap[x][y][t]>0 && evtMap[x][y][t]!=i) {
							status = true;
							break;
						}
					}
					if(status)
						break;
				}
				sigxOthers[t] = status;
			}
			int[] rgT1 = extendEventTimeRangeByCurve(dff2,sigxOthers,its,ite);
			int t0a = rgT1[0];
			int t1a = rgT1[1];
			for(int t=t0a;t<=t1a;t++) {
				dffMatExt[i-1][t] = dff2[t];
			}
			
			// simple features
			ftsLst.addQuickFeature(i, dffMaxZ, dffMaxPval, rgT1, its, ite);
		}
		
		return new QuickFeatureResult(ftsLst, dffMatExt,evtMap);
	}

	/**
	 * extend event
	 * @param dff
	 * @param sigxOthers
	 * @param rgts
	 * @param rgte
	 * @return
	 */
	private static int[] extendEventTimeRangeByCurve(float[] dff, boolean[] sigxOthers, int rgts, int rgte) {
		int T = dff.length;
		int t0 = Math.max(rgts-1, 0);
		int t1 = Math.min(rgte+1, T-1);
		
		// begin and end of nearest others
		int i0 = -1;
		if(rgts>0) {
			for(int t=t0;t>=0;t--) {
				if(sigxOthers[t]) {
					i0 = t;
					break;
				}
			}
		}
		int i1 = -1;
		if(rgte<T-1) {
			for(int t=t1;t<T;t++) {
				if(sigxOthers[t]) {
					i1 = t;
					break;
				}
			}
		}
		
		// minimum point
		int t0a = rgts;
		if(i0!=-1) {
			float min0 = Float.MAX_VALUE;
			for(int t=i0;t<=rgts;t++) {
				if(min0>dff[t]) {
					t0a = t;
					min0 = dff[t];
				}
			}
		}
		
		int t1a = rgte;
		if(i1!=-1) {
			float min1 = Float.MAX_VALUE;
			for(int t=rgte;t<=i1;t++) {
				if(min1>dff[t]) {
					t1a = t;
					min1 = dff[t];
				}
			}
		}
		
		if(t0a>=t1a) {
			t0a = t0;
			t1a = t1;
		}
		
		return new int[] {t0a,t1a};
	}

	/**
	 * impute the input matrix
	 * @param datx
	 */
	public static void imputeMov(float[][][] datx) {
		int W = datx.length;
		int H = datx[0].length;
		int T = datx[0][0].length;
		// fill
		for(int i=0;i<W;i++) {
			for(int j=0;j<H;j++) {
				for(int k=1;k<T;k++) {
					if(datx[i][j][k] == -1)
						datx[i][j][k] = datx[i][j][k-1];
				}
				for(int k=T-2;k>=0;k--) {
					if(datx[i][j][k] == -1)
						datx[i][j][k] = datx[i][j][k+1];
				}
			}
		}
		// gauss filter for each frame
		for(int k=0;k<T;k++) {
			float[][] tmp = new float[W][H];
			for(int i=0;i<W;i++) {
				for(int j=0;j<H;j++) {
					tmp[i][j] = datx[i][j][k];
				}
			}
//			ImageProcessor blurer = new FloatProcessor(tmp);
//			blurer.blurGaussian(1);
			tmp = GaussFilter.gaussFilter(tmp, 1, 1);
			for(int i=0;i<W;i++) {
				for(int j=0;j<H;j++) {
					datx[i][j][k] = tmp[i][j];
				}
			}
			
		}
	}
	
	/**
	 * Use the fitting method to decrease the influnce of the trend
	 * @param c0
	 * @param s0
	 * @param correctTrend
	 * @return
	 */
	private static float[] curvePolyDeTrend(float[] c0, boolean[] s0, int correctTrend) {
		if(correctTrend>0) {
			WeightedObservedPoints obs = new WeightedObservedPoints();
			for(int t=0;t<c0.length;t++) {
				if(!s0[t])
					obs.add(t,c0[t]);
			}
			PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);
			double[] coeff = fitter.fit(obs.toList());
			float[] y = new float[c0.length];
			float minY = Float.MAX_VALUE;
			float minC0 = Float.MAX_VALUE;
			for(int t=0;t<c0.length;t++) {
				y[t] = c0[t];
				minY = Math.min(minY, y[t]);
				float yFit = (float) (coeff[0] + coeff[1]*t);
				c0[t] = y[t] - yFit;
				minC0 = Math.min(minC0, c0[t]);
			}
			for(int t=0;t<c0.length;t++) {
				c0[t] = c0[t] - minC0 + minY;
			}
		}
		
		return c0;
	}
	
	
	
	
	
	
	
}
