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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import va.vt.cbil.ProgressBarRealizedStep2.MidResult;
import va.vt.cbil.ProgressBarRealizedStep3.EvtTopResult;
import va.vt.cbil.ProgressBarRealizedStep3.RiseNode;
import va.vt.cbil.ProgressBarRealizedStep3.SeToEvent;

public class ProgressBarRealizedStep6 extends SwingWorker<int[][][], Integer> {
	JFrame frame = new JFrame("Step6");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	
	static long start = System.currentTimeMillis();;
	static long end;
	ImageDealer imageDealer = null;
	String proPath = null;
	public ProgressBarRealizedStep6(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
		proPath = imageDealer.proPath;
		imageDealer.running = true;
	}
	
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
	
	@SuppressWarnings("unchecked")
	@Override
	protected int[][][] doInBackground() throws Exception {

		publish(1);
		Opts opts = imageDealer.opts;
		
		HashMap<Integer,ArrayList<int[]>> evtLstE = null;
		float[][][] dat = null;
		float[][][] dF = null;
		HashMap<Integer,RiseNode> riseLst = null;
		int[][][] datR = null;
		// ------------------------ Read Data ----------------------------- //
		try {
			FileInputStream fi = null;
			ObjectInputStream oi = null;
			
			// ResultInStep4_EvtLstFilterZ
			fi = new FileInputStream(new File(proPath + "evtLstMerge.ser"));
			oi = new ObjectInputStream(fi);
			evtLstE = (HashMap<Integer,ArrayList<int[]>>) oi.readObject();
			oi.close();
			fi.close();
			
			fi = new FileInputStream(new File(proPath + "Data.ser"));
			oi = new ObjectInputStream(fi);
			dat = (float[][][]) oi.readObject();
			oi.close();
			fi.close();
			
			fi = new FileInputStream(new File(proPath + "DF.ser"));
			oi = new ObjectInputStream(fi);
			dF = (float[][][]) oi.readObject();
			oi.close();
			fi.close();
			
			fi = new FileInputStream(new File(proPath + "ResultInStep4_RiseLstFilterZ.ser"));
			oi = new ObjectInputStream(fi);
			riseLst = (HashMap<Integer,RiseNode>) oi.readObject();
			oi.close();
			fi.close();
			
			fi = new FileInputStream(new File(proPath + "ResultInStep3_DatRAll.ser"));
			oi = new ObjectInputStream(fi);
			datR = (int[][][]) oi.readObject();
			oi.close();
			fi.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		int[][][] labels = new int[opts.W][opts.H][opts.T];
		
		publish(2);
		if(opts.extendSV==0 || opts.ignoreMerge==0 || opts.extendEvtRe>0) {
			EvtTopResult re = evtTopEx(dat,dF,evtLstE,opts);
			evtLstE = re.evtLst;
			datR = re.datR;
			riseLst = re.riseLst;
		}
		
		int maxLabel = 0;
		for(Entry<Integer, ArrayList<int[]>> entry : evtLstE.entrySet()) {
			maxLabel = Math.max(entry.getKey(), maxLabel);
		}
		
		// relabel
		HashMap<Integer,ArrayList<int[]>> newEvtLst = new HashMap<>();
		HashMap<Integer,RiseNode> newRiseLst = new HashMap<>();
		int cnt = 1;
		for(int i=1;i<=maxLabel;i++) {
			ArrayList<int[]> points = evtLstE.get(i);
			if(points!=null) {
				newEvtLst.put(cnt, points);
				newRiseLst.put(cnt, riseLst.get(i));
				for(int[] p:points) {
					labels[p[0]][p[1]][p[2]] = cnt;
				}
				cnt++;
			}
		}
		evtLstE = newEvtLst;
		riseLst = newRiseLst;
	
		
		imageDealer.datR = datR;
		int nEvt = evtLstE.size();
		publish(3);
		try {
			FileOutputStream f = null;
			ObjectOutputStream o = null;
			
			// datR
			f = new FileOutputStream(new File(proPath + "ResultInStep6_DatR.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(datR);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath + "Step6_Labels.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(labels);
			o.close();
			f.close();
			
			// evtLst
			f = new FileOutputStream(new File(proPath + "ResultInStep6_Evt.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(evtLstE);
			o.close();
			f.close();
			
			// riseLst
			f = new FileOutputStream(new File(proPath + "ResultInStep6_RiseLstAll.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(riseLst);
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
		
		imageDealer.center.EvtNumber.setText(nEvt+"");
		
		return labels;
	}


	private EvtTopResult evtTopEx(float[][][] dat, float[][][] dF, HashMap<Integer, ArrayList<int[]>> seLst, Opts opts) {
		int W = dat.length;
		int H = dat[0].length;
		int T = dat[0][0].length;
		int changeParameter = Math.max(W, H);
		int[][][] seMap = new int[W][H][T];
		for(int i=1;i<=seLst.size();i++) {
			for(int[] p:seLst.get(i)) {
				seMap[p[0]][p[1]][p[2]] = i;
			}
		}
		
		// extend event time windows
		if(opts.extendEvtRe>0) {
			opts.extendSV = 1;
			int[][][] lblMapEx = new int[W][H][T];
			HashMap<Integer, ArrayList<int[]>> lblxMap = new HashMap<>();
			MidResult re = getSpDelay(dat,seMap,seLst,opts,lblxMap,lblMapEx);
			seMap = re.lblMapEx;
			seLst = re.map;
		}
		
//		//TODO: delete
//		try {
//			FileOutputStream f = null;
//			ObjectOutputStream o = null;
//			
//			f = new FileOutputStream(new File("D:\\TestFolder\\" + "seMap2.ser"));
//			o = new ObjectOutputStream(f);
//			o.writeObject(seMap);
//			o.close();
//			f.close();
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		// super event to events
		System.out.println("Detecting events");
		HashMap<Integer,RiseNode> riseLst = new HashMap<>();
		int[][][] datR = new int[W][H][T];
		int[][][] datL = new int[W][H][T];
		int nEvt = 0;
		for(int n=1;n<=seLst.size();n++) {
			int label = n;
			ArrayList<int[]> se0 = seLst.get(label);
			if(se0.size()==0)
				continue;
			
			
//			// TODO: delete
//			if(n==258)
//				System.out.println("Becareful");
			
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
			rgte = Math.min(T-1, rgte+gapt);
			

			ProgressBarRealizedStep3.SeToEvent seToEventResult = ProgressBarRealizedStep3.se2evt(dF,seMap,n,ihw0,rgws,rgwe,rghs,rghe,rgts,rgte,it0s,it0e,T,opts,changeParameter);			// checked
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
			ProgressBarRealizedStep3.addToRisingMap(riseLst, evtMap, dlyMap, nEvt, nEvt0, rghs, rghe, rgws, rgwe, rgts, rgte, rgtSels, rgtSele);		// checked
			nEvt = nEvt + nEvt0;	// checked
		}
		
		HashMap<Integer,ArrayList<int[]>> evtLst = label2idx(datL);			// checked
		return new ProgressBarRealizedStep3.EvtTopResult(riseLst, datR, evtLst, seLst,datL);
	}
	

	public MidResult getSpDelay(float[][][] dat, int[][][] lblMap, HashMap<Integer, ArrayList<int[]>> map, Opts opts, HashMap<Integer, ArrayList<int[]>> lblxMap, int[][][] lblMapEx) {
		int width = dat.length;
		int height = dat[0].length;
		int pages = dat[0][0].length;
		
		float[][][] dataSmoth = new float[width][height][pages];
		
		for(int k = 0;k<pages;k++) {
			
			float[][] f = new float[width][height];
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					f[i][j] = dat[i][j][k];
				}
			}
			float[][] frameSmo = GaussFilter.gaussFilter(f,1,1);
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					dataSmoth[i][j][k] = frameSmo[i][j];
				}
			}
			
		}
		int movAvgWin = Math.max(Math.round(((float)pages)/10), 3);
		float[][][] dfSmo = MinMoveMean.subMinMoveMean(dataSmoth, movAvgWin);
		// extend super voxels
		if(opts.extendSV==0)
			lblMapEx = lblMap;
		else
			lblxMap = extendVoxGrp(lblMap,map,dfSmo,opts.varEst,lblMapEx);
		lblxMap = label2idx(lblMapEx);
		
		// rising time
		float s0 = (float) Math.sqrt(opts.varEst);
		int nSp = lblxMap.size();
		
		int levels = 30;
		int[][] riseX = new int[nSp][levels+1];
		for(int i=0;i<nSp;i++) {
			for(int j=0;j<=levels;j++) {
				riseX[i][j] = -1;
			}
		}
		
		for(int n=0;n<nSp;n++) {
			if(n%1000==0)
				System.out.println(n + "/" + nSp);
			ArrayList<int[]> points = lblxMap.get(n+1);
			if(points==null)
				continue;
			
			int hs = Integer.MAX_VALUE;
			int he = Integer.MIN_VALUE;
			int ws = Integer.MAX_VALUE;
			int we = Integer.MIN_VALUE;
			int ts = Integer.MAX_VALUE;
			int te = Integer.MIN_VALUE;
			for(int[] p:points) {
				ws = Math.min(ws, p[0]);
				hs = Math.min(hs, p[1]);
				ts = Math.min(ts, p[2]);
				we = Math.max(we, p[0]);
				he = Math.max(he, p[1]);
				te = Math.max(te, p[2]);
			}
			
			float[][][] df0 = cropData(dfSmo,ws,we,hs,he,ts,te);
			for(int k=ts;k<=te;k++) {
				for(int i=ws;i<=we;i++) {
					for(int j=hs;j<=he;j++) {
						if(lblMapEx[i][j][k]>0 && lblMapEx[i][j][k]!=n+1) {
							df0[i-ws][j-hs][k-ts] = 0;
						}
					}
				}
			}
			
			float[] x0 = new float[te-ts+1];
			for(int k=ts;k<=te;k++) {
				float sum = 0;
				for(int i=ws;i<=we;i++) {
					for(int j=hs;j<=he;j++) {
						sum += df0[i-ws][j-hs][k-ts];
					}
				}
				x0[k-ts] = sum/(we-ws+1)/(he-hs+1);
			}
			
			// estimate noise
			float s1;
			if(x0.length>5) {
				float[] dfx0 = new float[x0.length-1];
				for(int i=0;i<x0.length-1;i++) {
					dfx0[i] = (x0[i+1]-x0[i])*(x0[i+1]-x0[i]);
				}
				Arrays.sort(dfx0);
				float median = 0;
				if(dfx0.length%2==0)
					median = (dfx0[dfx0.length/2 - 1] + dfx0[dfx0.length/2])/2;
				else
					median = dfx0[dfx0.length/2];
				s1 = (float) Math.sqrt(median/0.9133);
			}else {
				s1 = (float) Math.min(s0/Math.sqrt((we-ws+1)*(he-hs+1))*3, s0);
			}
			
			// rising time
			for(int i=0;i<=levels;i++) {
				float thr1 = i*s1;
				int t00 = -1;
				for(int t=0;t<x0.length;t++) {
					if(x0[t]>thr1) {
						t00=t;
						break;
					}
				}
				
				riseX[n][i] = -1;
				if(t00!=-1) {
					riseX[n][i] = ts+t00;
				}else {
					break;
				}
			}
		}
		
		// remove the 0 term
		int[][] riseX2 = new int[nSp][levels];
		
		for(int n=0;n<nSp;n++) {
			for(int i=0;i<levels;i++) {
				riseX2[n][i] = riseX[n][i+1];
				if(i==0 && riseX2[n][i]==-1)
					riseX2[n][i] = riseX[n][i];
			}
		}
		
		
		
		MidResult result = new MidResult(lblxMap,riseX2,lblMapEx);
		return result;
	}
	
	class MidResult{
		HashMap<Integer, ArrayList<int[]>> map = null;
		int[][] riseX = null;
		int[][][] lblMapEx = null;
		
		public MidResult(HashMap<Integer, ArrayList<int[]>> map, int[][] riseX, int[][][] lblMapEx) {
			this.map = map;
			this.riseX = riseX;
			this.lblMapEx = lblMapEx;
		}
	}
	
	private HashMap<Integer,ArrayList<int[]>> label2idx(int[][][] labelMap){
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
	
	private float[][][] cropData(float[][][] dat,int rgWs, int rgWe, int rgHs, int rgHe, int rgTs, int rgTe) {
//		System.out.println(rgWs + " " + rgWe + " " + rgHs + " " + rgHe + " " + rgTs + " " + rgTe);
		float[][][] result = new float[rgWe-rgWs+1][rgHe-rgHs+1][rgTe-rgTs+1];
		for(int k = rgTs;k<=rgTe;k++) {
			for(int i = rgWs;i<=rgWe;i++) {
				for(int j = rgHs;j<=rgHe;j++) {
					result[i-rgWs][j-rgHs][k-rgTs] = dat[i][j][k];
				}
			}
		}
		
		
		return result;
	}
	
	public HashMap<Integer, ArrayList<int[]>> extendVoxGrp(int[][][] lblMap, HashMap<Integer, ArrayList<int[]>> map, float[][][] dfSmo, float varEst, int[][][] lblMapEx) {
		int width = lblMap.length;
		int height = lblMap[0].length;
		int pages = lblMap[0][0].length;
		int nRg = map.size();
		int gapt = 10;
		int changeParameter = Math.max(width, height);
		HashMap<Integer, ArrayList<int[]>> lblMapX = new HashMap<>();
		int nSp = 1;
		for(int n=0;n<nRg;n++) {
			
			if(n%1000==0)
				System.out.println(n + "/" + nRg);
			
			// current super voxel and spatial footprint
			ArrayList<int[]> points = map.get(n+1);
			if(points==null)
				continue;
			
			int ts = Integer.MAX_VALUE;
			int te = Integer.MIN_VALUE;
			HashSet<Integer> points2D = new HashSet<>();
			for(int[] p:points) {
				points2D.add(p[0]*changeParameter + p[1]);
				ts = Math.min(ts, p[2]);
				te = Math.max(te, p[2]);
			}
			ArrayList<Integer> points2DList = new ArrayList<>(points2D); 
			int number = points2DList.size();
			if(number<4)
				continue;
			
			// overall time window due to active region
			ts = Math.max(ts-gapt, 0);
			te = Math.min(te+gapt, pages-1);
			int T = te-ts+1;
			
			
			// for each pixel, replace time points from other events baseline
			for(int u = 0;u<number;u++) {
				int[] lblOnePix = new int[T];
				float[] dat1SmoOnePix = new float[T];
				int xy = points2DList.get(u);
				int px = xy/changeParameter;
				int py = xy%changeParameter;
				
				for(int t=ts;t<=te;t++) {
					lblOnePix[t-ts] = lblMap[px][py][t];
					dat1SmoOnePix[t-ts] = dfSmo[px][py][t];
				}
				
				// bounded by previous and/or next events
				int t0 = 0;
				int t1 = T-1;
				for(int t = 0;t<T;t++) {
					if(lblOnePix[t]==n+1) {
						t0 = t;
						break;
					}
				}
				for(int t = T-1;t>=0;t--) {
					if(lblOnePix[t]==n+1) {
						t1 = t;
						break;
					}
				}
				int t0p = 0;
				for(int t = Math.max(t0-1,0);t>=0;t--) {
					if(lblOnePix[t]>0) {
						t0p = t;
						break;
					}
				}
				int t1p = T-1;
				for(int t = Math.min(t1+1, T-1);t<T;t++) {
					if(lblOnePix[t]>0) {
						t1p = t;
						break;
					}
				}
				
				//bounded by active region
				
				// lowest point between temporal adjacent events
				float x0 = Float.MAX_VALUE;
				int t0a = t0;
				for(int t=t0p;t<=t0;t++) {
					if(dat1SmoOnePix[t]<x0) {
						x0 = dat1SmoOnePix[t];
						t0a = t;
					}
				}
				
				float x1 = Float.MAX_VALUE;
				int t1a = t1;
				for(int t=t1;t<=t1p;t++) {
					if(dat1SmoOnePix[t]<x1) {
						x1 = dat1SmoOnePix[t];
						t1a = t;
					}
				}
				
				// stop if signal low enough
				int t0b = t0a;
				int t1b = t1a;
				float thr0 = (float) (x0 + Math.sqrt(varEst));
				float thr1 = (float) (x1 + Math.sqrt(varEst));
				for(int t=t0;t>=t0a;t--) {
					if(dat1SmoOnePix[t]<thr0) {
						t0b = t;
						break;
					}
				}
				for(int t=t1;t<=t1a;t++) {
					if(dat1SmoOnePix[t]<thr1) {
						t1b = t;
						break;
					}
				}
				
				// save current pixel
				int t00a = ts + t0b;
				int t00b = ts + t1b;

				ArrayList<int[]> l = lblMapX.get(n+1);
				if(l==null)
					l = new ArrayList<>();
				for(int t=t00a;t<=t00b;t++) {
					l.add(new int[] {px,py,t});
					lblMapEx[px][py][t] = nSp;
				}
				lblMapX.put(nSp, l);
				
			}
			nSp++;
		}
		
		return lblMapX;
	}
	
	
	protected void process(List<Integer> chunks) {
		int value = chunks.get(chunks.size()-1);
		int total = 3;
		String str = "";
		switch(value) {
		case 1:
			str = "Read the Data " + value + "/" + total;
			break;
		case 2:
			str = "Find the Events " + value + "/" + total;
			break;
		case 3:
			str = "Save the Data " + value + "/" + total;
			break;
		}
		jLabel.setText(str);
	}
	
	@Override
	protected void done() {
		frame.setVisible(false);
//		JOptionPane.showMessageDialog(null, "Step6 Finish! Events Reconstructed");
		imageDealer.left.nextButton.setEnabled(true);
		imageDealer.left.backButton.setEnabled(true);
		imageDealer.left.jTP.setEnabledAt(6, true);
		
		if(imageDealer.left.jTPStatus<6) {
			imageDealer.left.jTPStatus = Math.max(imageDealer.left.jTPStatus, 6);;
			imageDealer.right.typeJCB.addItem("Step6: Events Reconstructed");
		}
//		imageDealer.right.typeJCB.setSelectedIndex(6);
		
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
}
