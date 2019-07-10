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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

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

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

class Step7Result{
	FtsLst fts = null;
	float[][][] dffMat = null;
	public Step7Result(FtsLst fts, float[][][]dffMat){
		this.fts = fts;
		this.dffMat = dffMat;
	}
}

public class ProgressBarRealizedStep7 extends SwingWorker<Step7Result, Integer> {
	JFrame frame = new JFrame("Step7");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	
	static long start = System.currentTimeMillis();;
	static long end;
	ImageDealer imageDealer = null;
	
	float minArea = Float.MAX_VALUE;
	float maxArea = -Float.MAX_VALUE;
	float minPvalue = Float.MAX_VALUE;
	float maxPvalue = -Float.MAX_VALUE;
	float minDecayTau = Float.MAX_VALUE;
	float maxDecayTau = -Float.MAX_VALUE;
	float mindffMax = Float.MAX_VALUE;
	float maxdffMax = -Float.MAX_VALUE;
	float minDuration = Float.MAX_VALUE;
	float maxDuration = -Float.MAX_VALUE;
	String proPath = null;
	
	public ProgressBarRealizedStep7(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
		proPath = imageDealer.proPath;
		imageDealer.running = true;
	}
	
	public ProgressBarRealizedStep7() {

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
		jLabel.setHorizontalAlignment(JLabel.CENTER);
		GridBagPut settingPanel = new GridBagPut(curPanel);
		settingPanel.fillBoth();
		settingPanel.putGridBag(progressBar, curPanel, 0, 0);
		settingPanel.putGridBag(jLabel, curPanel, 0, 1);
		frame.setContentPane(curPanel);
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
		
	}
	
	@Override
	protected Step7Result doInBackground() throws Exception {

		
		// ------------------------ Read Data ----------------------------- //
		Opts opts = imageDealer.opts;
		// features
		
		Step7Result result = updateFeature(opts,false);;

		return result;
		

	}
	
	protected void process(List<Integer> chunks) {
		int value = chunks.get(chunks.size()-1);
		int total = 5;
		String str = "";
		switch(value) {
			case 1:
				str = "Read the Data " + value + "/" + total;
				break;
			case 2:
				str = "Updating basic features " + value + "/" + total;
				break;
			case 3:
				str = "Updating propagation features " + value + "/" + total;
				break;
			case 4:
				str = "Updating the region features " + value + "/" + total;
				break;
			case 5:
				str = "Save the Data " + value + "/" + total;
				break;
		}
		jLabel.setText(str);
	}
	
	@SuppressWarnings("resource")
	@Override
	protected void done() {
		frame.setVisible(false);
		try {
			imageDealer.dffMat = this.get().dffMat;
			imageDealer.fts = this.get().fts;
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
//		JOptionPane.showMessageDialog(null, "Step7 Finish!");
		imageDealer.left.jTPStatus = Math.max(imageDealer.left.jTPStatus, 7);
		imageDealer.left.nextButton.setEnabled(false);
		imageDealer.left.backButton.setEnabled(true);
		imageDealer.left.left3.setVisible(true);
		imageDealer.left.left4.setVisible(true);
		imageDealer.right.allFinished();
		imageDealer.left.updateFeatures.setEnabled(true);
		new Thread(new Runnable() {

			@Override
			public void run() {
				imageDealer.dealImage();
				imageDealer.imageLabel.repaint();
			}
			
		}).start();
		imageDealer.left.tableValueSetting(minArea,maxArea,minPvalue,maxPvalue,minDecayTau,maxDecayTau,minDuration,maxDuration,mindffMax,maxdffMax);
		float[] featureTable = new float[] {minArea,maxArea,minPvalue,maxPvalue,minDecayTau,maxDecayTau,minDuration,maxDuration,mindffMax,maxdffMax};
		imageDealer.saveStatus();
		try {
			FileOutputStream f = null;
			ObjectOutputStream o = null;
			
			f = new FileOutputStream(new File(proPath+"FtsTableParameters.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(featureTable);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		imageDealer.running = false;
	}

	@SuppressWarnings("unchecked")
	protected Step7Result updateFeature(Opts opts, boolean stg) {
		// update network features after user draw regions
		// regions are all in x, y coordinate, where y need to be flipped for matrix manipulation
		System.out.println("Updating basic, network, region and landmark features");
		publish(1);
		HashMap<Integer, ArrayList<int[]>> evtLst = null;
		// read data
		try {
			FileInputStream fi = null;
			ObjectInputStream oi = null;		
			// evtLst
			fi = new FileInputStream(new File(proPath + "ResultInStep6_Evt.ser"));
			oi = new ObjectInputStream(fi);
			evtLst = (HashMap<Integer,ArrayList<int[]>>) oi.readObject();
			oi.close();
			fi.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		System.out.println("Updating features");
		int W = opts.W;
		int H = opts.H;
		int T = opts.T;
		int[][][] labels = new int[W][H][T];
		for(Entry<Integer, ArrayList<int[]>> entry:evtLst.entrySet()) {
			int label = entry.getKey();
			ArrayList<int[]> points = entry.getValue();
			for(int[] p:points) {
				labels[p[0]][p[1]][p[2]] = label;
			}
		}
		
		
		// gather data
		int[][][] datR = null;
		try {
			FileInputStream fi = null;
			ObjectInputStream oi = null;		
			// evtLst
			fi = new FileInputStream(new File(proPath + "ResultInStep6_DatR.ser"));
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
		
		for(int t=0;t<T;t++) {
			for(int x=0;x<W;x++) {
				for(int y=0;y<H;y++) {
					if(labels[x][y][t]==0)
						datR[x][y][t] = 0;
				}
			}
		}
		
		// basic features
		float[][][] dataOrg = null;
		float[][][] dat = null;
		try {
			FileInputStream fi = null;
			ObjectInputStream oi = null;		
			fi = new FileInputStream(new File(proPath + "DataOrg.ser"));
			oi = new ObjectInputStream(fi);
			dataOrg = (float[][][]) oi.readObject();
			oi.close();
			fi.close();
			
			fi = new FileInputStream(new File(proPath + "Data.ser"));
			oi = new ObjectInputStream(fi);
			dat = (float[][][]) oi.readObject();
			oi.close();
			fi.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		float[][][] dffMat = null;
		publish(2);
		FtsLst ftsLstE = null;
		if(!stg) {
			System.out.println("Updating basic features");
			FeatureTopResult featureTopResult = getFeaturesTop(dataOrg, evtLst, labels, opts);
			ftsLstE = featureTopResult.ftsLst;
			dffMat = featureTopResult.dffMat;
			try {
				FileOutputStream f = null;
				ObjectOutputStream o = null;
				
				// ftsLst
				f = new FileOutputStream(new File(proPath + "ResultInStep7_DffMat.ser"));
				o = new ObjectOutputStream(f);
				o.writeObject(featureTopResult.dffMat);
				o.close();
				f.close();
				
				// dffMatAll
				f = new FileOutputStream(new File(proPath + "ResultInStep7_DMat.ser"));
				o = new ObjectOutputStream(f);
				o.writeObject(featureTopResult.dMat);
				o.close();
				f.close();
				
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			try {
				// data in step1
				FileInputStream fi1 = null;
				ObjectInputStream oi1 = null;
				fi1 = new FileInputStream(new File(proPath + "ResultInStep3_FtsLstAll.ser"));
				oi1 = new ObjectInputStream(fi1);
				ftsLstE = (FtsLst)oi1.readObject();
				oi1.close();
				fi1.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		publish(3);
		// propagation features
		getFeaturesProTop(dat,datR,evtLst,ftsLstE,opts);
		
		// region, landmark, network and save results
		publish(4);
		updtFeatureRegionLandmarkNetworkShow(datR,evtLst,ftsLstE,opts);
//		System.out.println("ftsE network length " + ftsLstE.network.nOccurSameLoc.length);
		
		
		
		publish(5);
		try {
			FileOutputStream f = new FileOutputStream(new File(proPath + "Fts.ser"));
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeObject(ftsLstE);
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new Step7Result(ftsLstE,dffMat);
	}

	

	private void updtFeatureRegionLandmarkNetworkShow(int[][][] datR, HashMap<Integer, ArrayList<int[]>> evtLst,
			FtsLst ftsLstE, Opts opts) {
		int W = datR.length;
		int H = datR[0].length;
		int T = datR[0][0].length;
		
		float muPerPix = opts.spatialRes;
		
		boolean[][] evtSpatialMask = new boolean[W][H];
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				evtSpatialMask[x][y] = imageDealer.regionMark[x][y];
			}
		}
		boolean noRegion = true;
		for(int i=0;i<W;i++) {
			for(int j=0;j<H;j++) {
				if(evtSpatialMask[i][j]) {
					noRegion = false;
					break;
				}
			}
			if(!noRegion)
				break;
		}
		
		if(noRegion) {
			for(int i=0;i<W;i++) {
				for(int j=0;j<H;j++) {
					evtSpatialMask[i][j] = true;
				}
			}
		}
		
		int[][] regionLabel = imageDealer.regionMarkLabel;
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
		
		int[][] landMark = imageDealer.landMarkLabel;
		HashMap<Integer, ArrayList<int[]>> lmkLst = ConnectedComponents.twoPassConnect2D(landMark);
		System.out.println(lmkLst.size());
		// LandMark Features
		if(regLst.size()>0 || lmkLst.size()>0) {
			System.out.println("Updating region and landmark features ...");
			ftsLstE.region = getDistRegionBorderMIMO(evtLst,datR,regLst,lmkLst,muPerPix,opts.minShow1);
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
		ftsLstE.networkAll = getEvtNetworkFeatures(evtLst,W,H,T);
		ftsLstE.network = getEvtNetworkFeatures(evtx1,W,H,T);
		
		System.out.println("fts network length " + ftsLstE.network.nOccurSameLoc.length);
		
		
		
	}

	public NetWork getEvtNetworkFeatures(HashMap<Integer, ArrayList<int[]>> evts, int W, int H, int T) {
		// getEvtNetworkFeatures get network work level features for each event
		// Not include event level features like size and brightness
		// Pre-filter with bounding box overlapping
		
		int changeParameter = Math.max(W, H);
		int nEvt = evts.size();
		int[][] ex = new int[nEvt][6];
		int[] evtSize = new int[nEvt];
		boolean[] idxBad = new boolean[nEvt];
		for(int i=0;i<nEvt;i++)
			idxBad[i] = true;
		HashMap<Integer, HashSet<Integer>> tIdx = new HashMap<>();
		for(int n=0;n<nEvt;n++) {
			ArrayList<int[]> pix0 = evts.get(n+1);
			if(pix0.size()>0) {
				idxBad[n] = false;
				HashSet<Integer> ihw = new HashSet<>();
				int iws = Integer.MAX_VALUE;
				int iwe = Integer.MIN_VALUE;
				int ihs = Integer.MAX_VALUE;
				int ihe = Integer.MIN_VALUE;
				int its = Integer.MAX_VALUE;
				int ite = Integer.MIN_VALUE;
				for(int[] p:pix0) {
					iws = Math.min(iws, p[0]);
					ihs = Math.min(ihs, p[1]);
					its = Math.min(its, p[2]);
					iwe = Math.max(iwe, p[0]);
					ihe = Math.max(ihe, p[1]);
					ite = Math.max(ite, p[2]);
					ihw.add(p[0]*changeParameter + p[1]);
				}
				evtSize[n] = ihw.size();
				ex[n][0] = iws;
				ex[n][1] = iwe;
				ex[n][2] = ihs;
				ex[n][3] = ihe;
				ex[n][4] = its;
				ex[n][5] = ite;
				for(int t = its;t<=ite;t++) {
					HashSet<Integer> tLst = tIdx.get(t);
					if(tLst==null)
						tLst = new HashSet<>();
					tLst.add(n+1);
					tIdx.put(t, tLst);
				}
			}
		}
		
		int[] tLen = new int[T];
		for(int t = 0;t<T;t++) {
			if(tIdx.get(t)!=null)
				tLen[t] = tIdx.get(t).size();
		}
		boolean[] regSel = new boolean[nEvt];
		for(int i=0;i<nEvt;i++) {
			if(!idxBad[i])
				regSel[i] = true;
		}
		
		// all events and events with similar size
		int[][] nOccurSameLoc = new int[nEvt][2];
		int[] nOccurSameTime = new int[nEvt];
		HashMap<Integer,ArrayList<Integer>> occurSameLocList = new HashMap<>();
		HashMap<Integer,ArrayList<Integer>> occurSameLocList2 = new HashMap<>();
		HashMap<Integer,HashSet<Integer>> occurSameTimeList = new HashMap<>();
		for(int i=0;i<nEvt;i++) {
			if((i+1)%1000==0)
				System.out.println(i+1);
			if(!regSel[i])
				continue;
			
			int w0 = ex[i][0]; int w1 = ex[i][1]; int h0 = ex[i][2]; 
			int h1 = ex[i][3]; int t0 = ex[i][4]; int t1 = ex[i][5];
			
			// occur at same spatial location
			boolean[] isSel = new boolean[nEvt];
			ArrayList<Integer> idxSel = new ArrayList<>();
			int cnt = 0;
			for(int j=0;j<nEvt;j++) {
				isSel[j] = h0<=ex[j][3] && h1>=ex[j][2] && w0<=ex[j][1] && w1>=ex[j][0];
				if(isSel[j]) {
					cnt++;
					idxSel.add(j+1);
				}
			}
			if(cnt>0) {
				int szMe = evtSize[i];
				int[] szCo = new int[idxSel.size()];
				for(int j=0;j<idxSel.size();j++) {
					szCo[j] = evtSize[idxSel.get(j)-1];
				}
				ArrayList<Integer> isSelSimilarSize = new ArrayList<>();
				for(int j=0;j<idxSel.size();j++) {
					if(((float)szMe)/szCo[j]<2 && ((float)szMe)/szCo[j]>0.5f)
						isSelSimilarSize.add(idxSel.get(j));
				}
				nOccurSameLoc[i][0] = idxSel.size();
				nOccurSameLoc[i][1] = isSelSimilarSize.size();
				occurSameLocList.put(i+1, idxSel);
				occurSameLocList2.put(i+1, isSelSimilarSize);
			}
			
			// occur at same time
			int x = Integer.MIN_VALUE;
			int ix = 0;
			for(int t=t0;t<=t1;t++) {
				if(tLen[t]>x) {
					ix = t;
					x = tLen[t];
				}
			}
			HashSet<Integer> tIdx0 = tIdx.get(ix);
			nOccurSameTime[i] = x;
			occurSameTimeList.put(i+1, tIdx0);
		}
		
		// output ------
//		System.out.println("Network Size : " + nOccurSameLoc.length + " " + nOccurSameLoc[0].length);
		return new NetWork(nOccurSameLoc,nOccurSameTime,occurSameLocList,occurSameLocList2,occurSameTimeList);
		
		
	}

	public ResReg getDistRegionBorderMIMO(HashMap<Integer, ArrayList<int[]>> evts, int[][][] datS,
			HashMap<Integer, ArrayList<int[]>> regLst, HashMap<Integer, ArrayList<int[]>> lmkLst, float muPerPix,
			float minThr) {
		int W = datS.length;
		int H = datS[0].length;
		int T = datS[0][0].length;
		
		int nEvts = evts.size();
		int nReg = regLst.size();
		int nLmk = lmkLst.size();
		
		ResReg resReg = new ResReg();
		// landmarks
		PolyInfo lmkInfo = null;
		if(nLmk>0) {
			// regions are flipped here
			lmkInfo = getPolyInfo(lmkLst,W,H,T);
			resReg.landMark.mask = lmkInfo.polyMask;
			resReg.landMark.center = lmkInfo.polyCenter;
			resReg.landMark.border = lmkInfo.polyBorder;
			for(int i=0;i<lmkInfo.polyAvgDist.length;i++) {
				lmkInfo.polyAvgDist[i] *= muPerPix;
			}
			resReg.landMark.centerBorderAvgDist = lmkInfo.polyAvgDist;
			
			// distances to landmarks
			resReg.landmarkDist = evt2lmkProp(evts,lmkInfo.polyBorder,W,H,T,muPerPix);
			
			// frontier based propagation features related to landmark
			resReg.landmarkDir = evt2lmkPropWrap(datS, evts, lmkInfo.polyMask, muPerPix, minThr);
		}else {
			resReg.landMark = new LandMark();
			resReg.landmarkDist = new LandMarkDist();
			resReg.landmarkDir = new LandMarkDir();
		}
		
		// -------------------------------------------------------------------------------------------
		// regions
		if(nReg>0) {
			PolyInfo regionInfo = getPolyInfo(regLst,W,H,T);
			
			// landmark and region relationships
			boolean[][] incluLmk = null;
			if(nLmk>0) {
				incluLmk = new boolean[nReg][nLmk];
				for(int i=0;i<nReg;i++) {
					boolean[][] map00 = regionInfo.polyMask.get(i+1);
					for(int j=0;j<nLmk;j++) {
						boolean[][] map11 = lmkInfo.polyMask.get(j+1);
						boolean inclu = false;
						for(int x=0;x<W;x++) {
							for(int y=0;y<H;y++) {
								if(map00[x][y]&&map11[x][y]) {
									inclu = true;
									break;
								}
							}
							if(inclu)
								break;
						}
						incluLmk[i][j] = inclu;
					}
				}
			}
			
			// distance to region boundary for events in a region
			boolean[][] memberIdx = new boolean[nEvts][nReg];
			float[][] dist2border = new float[nEvts][nReg];
			float[][] dist2borderNorm = new float[nEvts][nReg];
			for(int i=0;i<nEvts;i++) {
				ArrayList<int[]> loc0 = evts.get(i+1);
				boolean flag = false;
				float centerX = 0;
				float centerY = 0;
				for(int j=0;j<nReg;j++) {
					boolean[][] msk0 = regionInfo.polyMask.get(j+1);
					boolean contains = false;
					for(int[] p:loc0) {
						if(msk0[p[0]][p[1]]) {
							contains = true;
							break;
						}
					}
					if(contains) {
						memberIdx[i][j] = true;
						if(!flag) {
							float sumX = 0;
							float sumY = 0;
							for(int[] p:loc0) {
								sumX += p[0];
								sumY += p[1];
							}
							centerX = sumX/loc0.size();
							centerY = sumY/loc0.size();
						}
						flag = true;
						ArrayList<int[]> cc = regionInfo.polyBorder.get(j+1);
						float minDist = Float.MAX_VALUE;
						for(int[] p:cc) {
							float dist = (p[0]-centerX)*(p[0]-centerX) + (p[1]-centerY)*(p[1]-centerY);
							minDist = Math.min(minDist, dist);
						}
						dist2border[i][j] = (float) Math.sqrt(minDist) * muPerPix;
						dist2borderNorm[i][j] = dist2border[i][j]/regionInfo.polyAvgDist[j];
					}
				}
			}
			
			for(int i=0;i<regionInfo.polyAvgDist.length;i++) {
				regionInfo.polyAvgDist[i] *= muPerPix;
			}
			
			
			resReg.cell.mask = regionInfo.polyMask;
			resReg.cell.center = regionInfo.polyCenter;
			resReg.cell.border = regionInfo.polyBorder;
			resReg.cell.centerBorderAvgDist = regionInfo.polyAvgDist;
			resReg.cell.incluLmk = incluLmk;
			resReg.cell.memberIdx = memberIdx;
			resReg.cell.dist2border = dist2border;
			resReg.cell.dist2borderNorm = dist2borderNorm;
			
		}
		
		return resReg;
		
	}

	private LandMarkDir evt2lmkPropWrap(int[][][] dRecon, HashMap<Integer, ArrayList<int[]>> evts,
			HashMap<Integer, boolean[][]> lmkMsk, float muPerPix, float minThr) {
		int W = dRecon.length;
		int H = dRecon[0].length;
//		int T = dRecon[0][0].length;
		
//		float m3 = muPerPix*muPerPix*muPerPix;
		
		float[] thrRg = new float[(int) (Math.round((0.9-minThr)*10))+1];
		thrRg[0] = minThr;
		for(int i=1;i<thrRg.length;i++) {
			thrRg[i] = thrRg[i-1] + 0.1f;
		}
		
		// landmarks
		int nEvts = evts.size();
		int nLmk = lmkMsk.size();
		HashMap<Integer, ArrayList<int[]>> lmkLst = new HashMap<>();
		for(int i=1;i<=nLmk;i++) {
			ArrayList<int[]> points = new ArrayList<>();
			boolean[][] msk = lmkMsk.get(i);
			for(int x=0;x<W;x++) {
				for(int y=0;y<H;y++) {
					if(msk[x][y]) {
						points.add(new int[] {x,y});
					}
				}
			}
			lmkLst.put(i, points);
		}
		
		// extract blocks
		float[][] chgToward = new float[nEvts][nLmk];
		float[][] chgAway = new float[nEvts][nLmk];
		HashMap<Integer,float[][][]> pixTwd = new HashMap<>();
		HashMap<Integer,float[][][]> pixAwy = new HashMap<>();
		float[][][] chgTowardThr = new float[nEvts][thrRg.length][nLmk];
		float[][][] chgAwayThr = new float[nEvts][thrRg.length][nLmk];
		HashMap<Integer,float[][][]> chgTowardThrFrame = new HashMap<>();
		HashMap<Integer,float[][][]> chgAwayThrFrame = new HashMap<>();		
		
		for(int nn=1;nn<=nEvts;nn++) {
			if(nn%100 == 0)
				System.out.println("EvtLmk: "+ nn);
			
			ArrayList<int[]> evt0 = evts.get(nn);
			if(evt0==null)
				continue;
			
			int rghs = Integer.MAX_VALUE;
			int rgws = Integer.MAX_VALUE;
			int rgts = Integer.MAX_VALUE;
			int rghe = Integer.MIN_VALUE;
			int rgwe = Integer.MIN_VALUE;
			int rgte = Integer.MIN_VALUE;
		
			for(int[] p:evt0) {
				rgws = Math.min(rgws, p[0]);
				rghs = Math.min(rghs, p[1]);
				rgts = Math.min(rgts, p[2]);
				rgwe = Math.max(rgwe, p[0]);
				rghe = Math.max(rghe, p[1]);
				rgte = Math.max(rgte, p[2]);
			}
			
			int rgHs = Math.max(rghs-2, 0);
			int rgHe = Math.min(rghe+2, H-1);
			int rgWs = Math.max(rgws-2, 0);
			int rgWe = Math.min(rgwe+2, W-1);
			int rgTs = rgts;
			int rgTe = rgte;
			int H1 = rgHe - rgHs + 1;
			int W1 = rgWe - rgWs + 1;
			int T1 = rgTe - rgTs + 1;
			
			// data
			float[][][] datS = new float[W1][H1][T1];
			for(int[] p:evt0) {
				int x = p[0] - rgWs;
				int y = p[1] - rgHs;
				int t = p[2] - rgTs;
				datS[x][y][t] = ((float)dRecon[p[0]][p[1]][p[2]])/255;
			}
			
			// put landmark inside cropped event
			// if some part inside event box, use that part
			// for outside part, stick it to the border
			HashMap<Integer, boolean[][]> lmkMsk1 = new HashMap<>();
			for(int i=1;i<=nLmk;i++) {
				ArrayList<int[]> points = lmkLst.get(i);
				boolean[][] msk0 = new boolean[W1][H1];
				for(int[] p:points) {
					int x = p[0] - rgWs;
					int y = p[1] - rgHs;
					x = Math.min(Math.max(x,0), W1-1);
					y = Math.min(Math.max(y,0), H1-1);
					msk0[x][y] = true;
					lmkMsk1.put(i, msk0);
				}
			}
			
			
			Evt2LmkProp1Result res = evt2lmkPropl(datS,lmkMsk1,thrRg,muPerPix);
			chgToward[nn-1] = res.chgToward;
			chgAway[nn-1] = res.chgAway;
			pixTwd.put(nn, res.pixTwd);
			pixAwy.put(nn, res.pixAwy);
			chgTowardThr[nn-1] = res.chgTowardThr;
			chgAwayThr[nn-1] = res.chgAwayThr;
			chgTowardThrFrame.put(nn, res.chgTowardThrFrame);
			chgAwayThrFrame.put(nn, res.chgAwayThrFrame);
		}
		
		return new LandMarkDir(chgToward, chgAway, pixTwd, pixAwy, chgTowardThr, chgAwayThr, chgTowardThrFrame, chgAwayThrFrame);
	}

	private Evt2LmkProp1Result evt2lmkPropl(float[][][] datS, HashMap<Integer, boolean[][]> lmkMsk, float[] thrRg, float muPerPix) {
		int W = datS.length;
		int H = datS[0].length;
		int T = datS[0][0].length;
		
		float m3 = muPerPix*muPerPix*muPerPix;
		int nLmk = lmkMsk.size();
		float sck = (float) Math.min(1, Math.sqrt(10000/H/W));
		// resize
		float[][][] datSx = null;
		HashMap<Integer, boolean[][]> lmkMskx = null;
		if(sck!=1) {
			int targetW = (int) (sck*W);
			int targetH = (int) (sck*H);
			datSx = new float[targetW][targetH][T];
			for(int t=0;t<T;t++) {
				float[][] dat = new float[W][H];
				for(int x=0;x<W;x++) {
					for(int y=0;y<H;y++) {
						dat[x][y] = datS[x][y][t];
					}
				}
				ImageProcessor ip = new FloatProcessor(dat);
			    ip.setInterpolationMethod(ImageProcessor.BILINEAR);
			    ip = ip.resize(targetW, targetH);
			    dat = ip.getFloatArray();
			    for(int x=0;x<targetW;x++) {
			    	for(int y=0;y<targetH;y++) {
			    		datSx[x][y][t] = dat[x][y];
			    	}
			    }
			}
			
			lmkMskx = new HashMap<>();
			for(int i=1;i<=nLmk;i++) {
				boolean[][] m0 = lmkMsk.get(i);
				float[][] m0f = new float[W][H];
				for(int x=0;x<W;x++) {
					for(int y=0;y<H;y++) {
						if(m0[x][y])
							m0f[x][y] = 1;
					}
				}
				
				ImageProcessor ip = new FloatProcessor(m0f);
			    ip.setInterpolationMethod(ImageProcessor.BILINEAR);
			    ip = ip.resize(targetW, targetH);
			    m0f = ip.getFloatArray();
			    boolean[][] m0s = new boolean[targetW][targetH];
			    for(int x=0;x<targetW;x++) {
			    	for(int y=0;y<targetH;y++) {
			    		m0s[x][y] = m0f[x][y]>0;
			    	}
			    }
			    lmkMskx.put(i, m0s);
			}
			
			W = targetW;
			H = targetH;
		}else {
			datSx = datS;
			lmkMskx = lmkMsk;
		}

		boolean isBig = false;
		if(H*W*T>1000000) {
			isBig = true;
			System.out.println("Propagation for landmark ...");
		}
		
		
		
		float[][][] chgTowardThrFrame = new float[thrRg.length][T][nLmk];
		float[][][] chgAwayThrFrame = new float[thrRg.length][T][nLmk];
		
		float[][][] pixTwd = new float[W][H][nLmk];
		float[][][] pixAwy = new float[W][H][nLmk];
		
		for(int k=0;k<thrRg.length;k++) {
			if(isBig) {
				System.out.println(k+1);
			}
			
			boolean[][][] evt0 = new boolean[W][H][T];
			HashSet<Integer> tSet = new HashSet<>();
			int tRgs = Integer.MAX_VALUE;
			int tRge = Integer.MIN_VALUE;
			for(int x=0;x<W;x++) {
				for(int y=0;y<H;y++) {
					for(int t=0;t<T;t++) {
						if(datSx[x][y][t]>thrRg[k]) {
							evt0[x][y][t] = true;
							tSet.add(t);
						}
					}
				}
			}
			
			for(int t:tSet) {
				tRgs = Math.min(t, tRgs);
				tRge = Math.max(t, tRge);
			}
			
			if(tRge == tRgs)
				continue;
			
			// impute missed frames
			// some times some frame could be missed due to some post processing
			for(int t=tRgs;t<=tRge;t++) {
				if(!tSet.contains(t)) {
					for(int x=0;x<W;x++) {
						for(int y=0;y<H;y++) {
							evt0[x][y][t] = evt0[x][y][t-1];
						}
					}
				}
			}
			
			// distance of valid pixels to landmarks
			// use the center of the landmark
			// use geodesic distance; if landmark outside event, use Euclidean distances
			// keep landmark simple
			boolean[][] evt0s = new boolean[W][H];
			for(int x=0;x<W;x++) {
				for(int y=0;y<H;y++) {
					for(int t=0;t<T;t++) {
						if(evt0[x][y][t]) {
							evt0s[x][y] = true;
							break;
						}
					}
				}
			}
			HashMap<Integer, float[][]> D = new HashMap<>();
			for(int i=1;i<=nLmk;i++) {
				boolean[][] msk00 = lmkMskx.get(i);
				ArrayList<int[]> points0 = new ArrayList<>();
				for(int x=0;x<W;x++) {
					for(int y=0;y<H;y++) {
						if(msk00[x][y])
							points0.add(new int[] {x,y});
					}
				}
				
				ArrayList<int[]> points1 = new ArrayList<>();
				for(int x=0;x<W;x++) {
					for(int y=0;y<H;y++) {
						if(evt0s[x][y])
							points1.add(new int[] {x,y});
					}
				}
				
				float[][] tmp = new float[W][H];
				for(int x=0;x<W;x++) {
					for(int y=0;y<H;y++) {
						tmp[x][y] = Float.MAX_VALUE;
					}
				}
				
				for(int j=0;j<points0.size();j++) {
					for(int n=0;n<points1.size();n++) {
						int x = points1.get(n)[0];
						int y = points1.get(n)[1];
						int x1 = points0.get(j)[0];
						int y1 = points0.get(j)[1];
						float dist00 = (float) Math.sqrt((x-x1)*(x-x1) + (y-y1)*(y*y1));
						tmp[x][y] = Math.min(tmp[x][y], dist00);
					}
				}
				D.put(i, tmp);
			}
			
			// regions and boundaries per frame
			HashMap<Integer, HashMap<Integer, ArrayList<int[]>>> bdLst = new HashMap<>();		// boundaries in each frame
			int[][][] lblMap = new int[W][H][tRge-tRgs+1];
			HashMap<Integer, HashMap<Integer, ArrayList<int[]>>> ccLst = new HashMap<>();		// region lists in each frame
			for(int t=tRgs;t<=tRge;t++) {
				boolean[][] xCur = new boolean[W][H];
				for(int x=0;x<W;x++) {
					for(int y=0;y<H;y++) {
						xCur[x][y] = evt0[x][y][t];
					}
					
				}
				MulBoundary result = BasicFeatureDealer.findMulBoundary(xCur);
				for(Entry<Integer,ArrayList<int[]>> entry:result.cc.entrySet()) {
					int label = entry.getKey();
					for(int[] p:entry.getValue()) {
						lblMap[p[0]][p[1]][t-tRgs] = label;
					}
				}
				ccLst.put(t-tRgs+1, result.cc);
				bdLst.put(t-tRgs+1, result.boundaries);
			}
			
			// frontier change tracking per frame
			float[][] dxAllPos = new float[tRge-tRgs+1][nLmk];	// change toward landmark
			float[][] dxAllNeg = new float[tRge-tRgs+1][nLmk];	// change away from landmark
			float[] tReach = new float[nLmk];
			for(int i=0;i<nLmk;i++)
				tReach[i] = Float.NaN;
			for(int i = 1;i<tRge-tRgs;i++) {
				// check reach the landmark or not
				for(int j=0;j<nLmk;j++) {
					boolean insideLmk = false;
					for(int x=0;x<W;x++) {
						for(int y=0;y<H;y++) {
							if(lblMap[x][y][i]>0&&lmkMskx.get(j+1)[x][y]) {
								insideLmk = true;
								break;
							}
						}
						if(insideLmk)
							break;
					}
					if(insideLmk&&Float.isNaN(tReach[j]))
						tReach[j] = i;
				}
				
				HashMap<Integer, ArrayList<int[]>> ccCur = ccLst.get(i+1);
				for(int j=1;j<=ccCur.size();j++) {
					// regions in previous frame that connect to this region
					ArrayList<int[]> cc0 = ccCur.get(j);
					HashSet<Integer> lblSel = new HashSet<>();
					for(int[] p:cc0) {
						int x = p[0];
						int y = p[1];
						if(lblMap[x][y][i-1]>0)
							lblSel.add(lblMap[x][y][i-1]);
					}
					if(lblSel.size()==0)
						continue;
					
					// previous boundary, the starting point of propagation
		            // we use some ad hocs:
		            // smaller pre area has higher distance penalty
		            // multiple pre area could compete
		            // if a previous cc is too small itself relative to current cc, ignore
		            // may not be biologically correct, but looks more comfortable
					
					ArrayList<int[]> bdPre = new ArrayList<>();		// location of the boundaries of previous frame region
					ArrayList<Float> bdPreWt = new ArrayList<>();	// boundary weight for choosing propagation origin
					float n0c = cc0.size();
					for(int lb:lblSel) {
						float n0 = ccLst.get(i).get(lb).size();
						if(n0>n0c/5) {
							ArrayList<int[]> tmp = bdLst.get(i).get(lb);
							int nBd = tmp.size();
							bdPre.addAll(tmp);
							for(int m=0;m<nBd;m++) {
								bdPreWt.add(1/n0);
							}
						}
					}
					
					if(bdPre.size()==0)
						continue;
					
					// current boundary, the ending point of propagation
		            // do not include boundary that is active in previous frame
		            // we only use the increasing signals
					
					ArrayList<int[]> bdCurTmp = ccLst.get(i+1).get(j);
					ArrayList<int[]> bdCur = new ArrayList<>();
					for(int[] p:bdCurTmp) {
						int x = p[0];
						int y = p[1];
						if(lblMap[x][y][i-1]==0)
							bdCur.add(new int[] {x,y});
					}
					if(bdCur.size()==0)
						continue;
					
					// link each pixel in bdCur to a pixel in bdPre
		            // for each landmark, find the distance change for each pair
		            // positive change is toward the landmark
		            // if pixCur contains landmark, it is treated as two parts
					float[][] dxPos = new float[bdCur.size()][nLmk];
					float[][] dxNeg = new float[bdCur.size()][nLmk];
					for(int u=0;u<bdCur.size();u++) {
						// weighted closest starting frontier point
						int[] p = bdCur.get(u);
						int w0a = p[0];
						int h0a = p[1];
						float[] d00 = new float[bdPre.size()];
						for(int t=0;t<bdPre.size();t++) {
							int[] p1 = bdPre.get(t);
							d00[t] = (float) Math.sqrt((w0a-p1[0])*(w0a-p1[0]) + (h0a-p1[1])*(h0a-p1[1]));
						}
						int ix = 0;
						float minV = Float.MAX_VALUE;
						for(int t=0;t<bdPre.size();t++) {
							if(minV>d00[t]*bdPreWt.get(t)) {
								minV = d00[t]*bdPreWt.get(t);
								ix = t;
							}
						}
						float d00min = d00[ix];
						
						// find path between points by drawing a line
						int w1a = bdPre.get(ix)[0];
						int h1a = bdPre.get(ix)[1];
						int num = Math.max(Math.round(d00min), 1);
						float wGap = (float)(w1a-w0a)/num;
						float hGap = (float)(h1a-h0a)/num;
						ArrayList<Integer> wx = new ArrayList<>();
						ArrayList<Integer> hx = new ArrayList<>();
						if(wGap!=0) {
							for(int t=0;t<=num;t++) {
								wx.add(Math.round(w0a+t*wGap));
							}
						}
						if(hGap!=0) {
							for(int t=0;t<=num;t++) {
								hx.add(Math.round(h0a+t*hGap));
							}
						}
							 
						if(h0a==h1a && w0a==w1a) {
							hx.add(h0a); wx.add(w0a);
						}else if(h0a==h1a) {
							for(int t=0;t<wx.size();t++) {
								hx.add(h0a);
							} 
						}else if(w0a==w1a){
							for(int t=0;t<hx.size();t++) {
								wx.add(w0a);
							}
						}
						
						// propagation distance w.r.t. landmarks, per pixel
						for(int v=1;v<=nLmk;v++) {
							float[][] D0 = D.get(v);
							float dp0Min = Float.MAX_VALUE;
							for(int t=0;t<wx.size();t++) {
								dp0Min = Math.min(dp0Min, D0[wx.get(t)][hx.get(t)]);
								dxPos[u][v-1] = Math.max(D0[w1a][h1a], 0);	// toward
								dxNeg[u][v-1] = Math.max(D0[w0a][h0a], 0);	// awat
							}
							
						}			
					}
					
					// gather pixel level propagations w.r.t. landmarks
					for(int u=0;u<bdCur.size();u++) {
						int x = bdCur.get(u)[0];
						int y = bdCur.get(u)[1];
						for(int v=0;v<nLmk;v++) {
							pixTwd[x][y][v] = pixTwd[x][y][v] + dxPos[u][v];
							pixAwy[x][y][v] = pixAwy[x][y][v] + dxNeg[u][v];
						}
					}
					
					
					
					float[] sumDxPos = new float[nLmk];
					float[] sumDxNeg = new float[nLmk];
					for(int v=0;v<nLmk;v++) {
						float sum = 0;
						for(int u=0;u<dxPos.length;u++)
							sum += dxPos[u][v];
						sumDxPos[v] = sum;
						float sum2 = 0;
						for(int u=0;u<dxNeg.length;u++)
							sum2 += dxNeg[u][v];
						sumDxNeg[v] = sum2;
					}

					for(int v=0;v<nLmk;v++) {
						// combine results from pixels
						dxAllPos[i][v] += sumDxPos[v];
						dxAllNeg[i][v] += sumDxNeg[v];
						
						// gather results
						chgTowardThrFrame[k][i+tRgs][v] = sumDxPos[v];
						chgAwayThrFrame[k][i+tRgs][v] = sumDxNeg[v];
					}
				}
			}
		}
		
		float scll = sck*sck*sck;
		float[][] chgTowardThr = new float[thrRg.length][nLmk];
		float[][] chgAwayThr = new float[thrRg.length][nLmk];
		float[] chgToward = new float[nLmk];
		float[] chgAway = new float[nLmk];
		for(int z=0;z<chgTowardThrFrame[0][0].length;z++) {
			for(int x=0;x<chgTowardThrFrame.length;x++) {
				for(int y=0;y<chgTowardThrFrame[0].length;y++) {
					chgTowardThrFrame[x][y][z] /= scll;
					chgTowardThrFrame[x][y][z] *= m3;
					chgAwayThrFrame[x][y][z] /= scll;
					chgAwayThrFrame[x][y][z]*= m3;
					chgTowardThr[x][z] += chgTowardThrFrame[x][y][z];
					chgAwayThr[x][z] += chgAwayThrFrame[x][y][z];
				}
				chgToward[z] += chgTowardThr[x][z];
				chgAway[z] += chgAwayThr[x][z];
			}
		}
		
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				for(int z=0;z<nLmk;z++) {
					pixTwd[x][y][z] /= scll;
					pixTwd[x][y][z] *= muPerPix;
					pixAwy[x][y][z] /= scll;
					pixAwy[x][y][z] *= muPerPix;
				}
			}
		}
		
		if(isBig)
			System.out.println(" OK");
		
		return new Evt2LmkProp1Result(chgTowardThrFrame, chgAwayThrFrame, chgTowardThr, chgAwayThr, chgToward, chgAway, pixTwd, pixAwy);
	}
	class Evt2LmkProp1Result{
		float[][][] chgTowardThrFrame = null;
		float[][][] chgAwayThrFrame = null;
		float[][] chgTowardThr = null;
		float[][] chgAwayThr = null;
		float[] chgToward = null;
		float[] chgAway = null;
		float[][][] pixTwd = null;
		float[][][] pixAwy = null;
		
		public Evt2LmkProp1Result(float[][][] chgTowardThrFrame, float[][][] chgAwayThrFrame, float[][] chgTowardThr, 
				float[][] chgAwayThr, float[] chgToward, float[] chgAway, float[][][] pixTwd, float[][][] pixAwy) {
			this.chgTowardThrFrame = chgTowardThrFrame;
			this.chgAwayThrFrame = chgAwayThrFrame;
			this.chgTowardThr = chgTowardThr;
			this.chgAwayThr = chgAwayThr;
			this.chgToward = chgToward;
			this.chgAway = chgAway;
			this.pixTwd = pixTwd;
			this.pixAwy = pixAwy;
		}
	}

	private LandMarkDist evt2lmkProp(HashMap<Integer, ArrayList<int[]>> evts, HashMap<Integer, ArrayList<int[]>> lmkBorder,
			int W, int H, int T, float muPerPix) {
		int nEvt = evts.size();
		int nLmk = lmkBorder.size();
		
		// distance to landmark
		HashMap<Integer, float[][]> d2lmk = new HashMap<>();
		float[][] d2lmkAvg = new float[nEvt][nLmk];
		float[][] d2lmkMin = new float[nEvt][nLmk];
		
		
		for(int i=1;i<=evts.size();i++) {
			if(i%100==0)
				System.out.println("lmkDist: " + i);
			
			ArrayList<int[]> loc0 = evts.get(i);
			int tRgs = Integer.MAX_VALUE;
			int tRge = Integer.MIN_VALUE;
			for(int[] p:loc0) {
				tRgs = Math.min(tRgs, p[2]);
				tRge = Math.max(tRge, p[2]);
			}
			float[][] distPix = new float[tRge-tRgs+1][nLmk];
			
			
			for(int t=0;t<=tRge-tRgs;t++) {
				for(int j=0;j<nLmk;j++) {
					distPix[t][j] = Float.NaN;
					ArrayList<int[]> cc = lmkBorder.get(j+1);
					float xx = Float.MAX_VALUE;
					for(int[] p:loc0) {
						if(p[2] == t + tRgs) {
							for(int[] xy:cc) {
								float tmp = (p[0]-xy[0])*(p[0]-xy[0]) + (p[1]-xy[1])*(p[1]-xy[1]);
								xx = Math.min(xx, tmp);
							}
						}
					}
					distPix[t][j] = (float) Math.sqrt(xx) * muPerPix;
					
					// cleaning
					if(Float.isNaN(distPix[t][j])) {
						if(t>0)
							distPix[t][j] = distPix[t-1][j];
					}
				}
			}
			
			// distance to landmark
			d2lmk.put(i, distPix); 					// shortest distance to landmark at each frame
			for(int j=0;j<nLmk;j++) {
				float sum = 0;
				int cnt = 0;
				float min = Float.MAX_VALUE;
				for(int t=0;t<distPix.length;t++) {
					if(!Float.isNaN(distPix[t][j])) {
						sum += distPix[t][j];
						cnt ++;
						min = Math.min(min, distPix[t][j]);
					}
				}
				d2lmkAvg[i-1][j] = sum/cnt;			// average distance to the landmark
				d2lmkMin[i-1][j] = min;				// minimum distance to the landmark
			}
		}
		
		return new LandMarkDist(d2lmk,d2lmkAvg,d2lmkMin);
		
	}

	private PolyInfo getPolyInfo(HashMap<Integer, ArrayList<int[]>> lmkLst, int W, int H, int T) {
		int nPoly = lmkLst.size();
		float[][] polyCenter = new float[nPoly][2];
		HashMap<Integer, boolean[][]> polyMask = new HashMap<>();
		HashMap<Integer, ArrayList<int[]>> polyBorder = new HashMap<>();
		float[] polyAvgDist = new float[nPoly];
		for(int i=1;i<=nPoly;i++) {
			ArrayList<int[]> poly0 = lmkLst.get(i);
			boolean[][] msk = new boolean[W][H];
			float sumX = 0;
			float sumY = 0;
			for(int[] p:poly0) {
				msk[p[0]][p[1]] = true;
				sumX += p[0];
				sumY += p[1];
			}
			float centerX = sumX/poly0.size();
			float centerY = sumY/poly0.size();
			polyCenter[i-1][0] = centerX;
			polyCenter[i-1][1] = centerY;
			polyMask.put(i, msk);
			ArrayList<int[]> boundary = BasicFeatureDealer.findBoundary(msk);
			polyBorder.put(i, boundary);
			
			ArrayList<Double> dist = new ArrayList<>();
			for(int[] p:boundary) {
				double dt = (p[0]-centerX)*(p[0]-centerX) + (p[1]-centerY)*(p[1]-centerY);
				dist.add(dt);
			}
			float avgDist = (float)Math.max(1, Math.round(Math.sqrt(getMedian(dist))));
			polyAvgDist[i-1] = avgDist;
		}
		
		return new PolyInfo(polyMask,polyCenter,polyBorder,polyAvgDist);
	}

	class PolyInfo{
		HashMap<Integer, boolean[][]> polyMask = null;
		HashMap<Integer, ArrayList<int[]>> polyBorder = null;
		float[][] polyCenter = null;
		float[] polyAvgDist = null;
		public PolyInfo(HashMap<Integer, boolean[][]> polyMask, float[][] polyCenter,
				HashMap<Integer, ArrayList<int[]>> polyBorder, float[] polyAvgDist) {
			this.polyMask = polyMask;
			this.polyCenter = polyCenter;
			this.polyAvgDist = polyAvgDist;
			this.polyBorder = polyBorder;
		}
		
	}
	
	public double getMedian(ArrayList<Double> target) {
		Collections.sort(target, new Comparator<Double>() {
			@Override
			public int compare(Double f1, Double f2) {
				if(f1<f2)
					return -1;
				else
					return 1;
			}
		});
		double result = 0;
		int len = target.size();
		if(len==0)
			result = (target.get(len/2 - 1) + target.get(len/2));
		else 
			result = target.get(len/2);
		return result;
		
	}
	
	public void getFeaturesProTop(float[][][] datOrg, int[][][] evtRec, HashMap<Integer, ArrayList<int[]>> evtLst,
			FtsLst ftsLst, Opts opts) {
		int W = datOrg.length;
		int H = datOrg[0].length;
		int T = datOrg[0][0].length;
		
		float[] northDi = new float[] {opts.northx,opts.northy};
		float[][][] dat = new float[W][H][T];
		for(int t=0;t<T;t++) {
			for(int x=0;x<W;x++) {
				for(int y=0;y<H;y++) {
					if(opts.usePG) {
						dat[x][y][t] = datOrg[x][y][t]*datOrg[x][y][t];
					}else {
						dat[x][y][t] = datOrg[x][y][t];
					}
				}
			}
		}
		
		float muPix = opts.spatialRes;
		for(int i=1;i<=evtLst.size();i++) {
			if(i%100==0)
				System.out.println(i + "/" + evtLst.size());
			
			ArrayList<int[]> pix0 = evtLst.get(i);
			if(pix0==null || pix0.size()==0)
				continue;
			
			int rghs = Integer.MAX_VALUE;
			int rgws = Integer.MAX_VALUE;
			int rghe = Integer.MIN_VALUE;
			int rgwe = Integer.MIN_VALUE;
			for(int[] p:pix0) {
				rgws = Math.min(rgws, p[0]);
				rghs = Math.min(rghs, p[1]);
				rgwe = Math.max(rgwe, p[0]);
				rghe = Math.max(rghe, p[1]);
			}

			rgws = Math.max(rgws-1, 0);
			rghs = Math.max(rghs-1, 0);
			rgwe = Math.min(rgwe+1, W-1);
			rghe = Math.min(rghe+1, H-1);
			int rgts = ftsLst.curve.tBegin.get(i);
			int rgte = ftsLst.curve.tEnd.get(i);
			
			// basic and propagation features
			int[][][] voxi = new int[rgwe-rgws+1][rghe-rghs+1][rgte-rgts+1];
			float[][][] voxr = new float[rgwe-rgws+1][rghe-rghs+1][rgte-rgts+1];
			
			for(int[] p: pix0) {
				int x = p[0];
				int y = p[1];
				int t = p[2];
				voxi[x-rgws][y-rghs][t-rgts] = 1;
			}

			for(int x=rgws;x<=rgwe;x++) {
				for(int y=rghs;y<=rghe;y++) {
					for(int t=rgts;t<=rgte;t++) {
						voxr[x-rgws][y-rghs][t-rgts] = (float)(evtRec[x][y][t])/255;
					}
				}
			}
			
			
			getPropagationCentroidQuad(voxi,voxr,muPix,i,ftsLst.propagation,northDi,opts.minShow1);
			
		}
		
		ftsLst.notes.propDirectionOrder = new String[] {"Anterior", "Posterior", "Left", "Right"};
		
	}

	private void getPropagationCentroidQuad(int[][][] voli1, float[][][] volr1, float muPerPix, int nEvt,
			Propagation ftsPg, float[] northDi, float minShow1) {
		// getFeatures extract local features from events
		// specify direction of 'north', or anterior
		// not good at tracking complex propagation
		
		int W = voli1.length;
		int H = voli1[0].length;
		int T = voli1[0][0].length;
				
		// make coordinate correct
		int[][][] voli0 = new int[W][H][T];
		float[][][] volr0 = new float[W][H][T];
		
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				for(int t=0;t<T;t++) {
					voli0[x][y][t] = voli1[x][H-y-1][t];
					volr0[x][y][t] = volr1[x][H-y-1][t];
					if(voli0[x][y][t]==0 || volr0[x][y][t]<minShow1) {		// exclude values outside event
						volr0[x][y][t] = 0;
					}
				}
			}
		}
		
		float a = northDi[0];
		float b = northDi[1];
		
		float[][] kDi = new float[4][2];
		kDi[0] = new float[] {a,b};
		kDi[1] = new float[] {-a,-b};
		kDi[2] = new float[] {-b,a};
		kDi[3] = new float[] {b,-a};
		
		// propagation features
		float[] thr0 = new float[(int) (Math.round((0.8 - minShow1)*10) + 1)];	// significant propagation (increase of reconstructed signal)
		for(int i=0;i<thr0.length;i++) {
			thr0[i] = minShow1 + 0.1f*i;
		}
		int nThr = thr0.length;
		int nPix = 0;
		int[][] sigMap = new int[W][H];
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				int sum = 0;
				for(int t=0;t<T;t++) {
					if(volr0[x][y][t]>=minShow1) {
						sum++;
					}
				}
				sigMap[x][y] = sum;
				if(sum>0)
					nPix ++;
			}
		}
		
		// time window for propagation
		int t0 = Integer.MAX_VALUE;
		int t1 = Integer.MIN_VALUE;
		for(int t=0;t<T;t++) {
			float max = -Float.MAX_VALUE;
			for(int x=0;x<W;x++) {
				for(int y=0;y<H;y++) {
					max = Math.max(max,volr0[x][y][t]);
				}
			}
			if(max >= minShow1) {
				t0 = Math.min(t0, t);
				t1 = Math.max(t1, t);
			}
		}
		
		// centroid of earliest frame as starting point 
		float sumWeight = 0;
		float sumXWeight = 0;
		float sumYWeight = 0;
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				if(t0!=Integer.MAX_VALUE &&volr0[x][y][t0]>=minShow1) {
					sumWeight += volr0[x][y][t0];
					sumXWeight += x * volr0[x][y][t0];
					sumYWeight += y * volr0[x][y][t0];
				}
			}
		}
		float seedwInit = sumXWeight/sumWeight;
		float seedhInit = sumYWeight/sumWeight;
		int w0 = Math.max(Math.round(seedwInit),0);
		int h0 = Math.max(Math.round(seedhInit),0);
		
		// mask for directions: north, south, west, east
		boolean[][][] msk = new boolean[W][H][4];
		for(int i=0;i<4;i++) {
			for(int x=0;x<W;x++) {
				for(int y=0;y<H;y++) {
					boolean ixSel = false;
					switch(i) {
						case 0:
							ixSel = (float)y>-((float)a)/((float)b)*(x-w0) + h0; break;
						case 1:
							ixSel = (float)y<-((float)a)/((float)b)*(x-w0) + h0; break;
						case 2:
							ixSel = (float)y> ((float)b)/((float)a)*(x-w0) + h0; break;
						case 3:
							ixSel = (float)y< ((float)b)/((float)a)*(x-w0) + h0; break;
					}
					msk[x][y][i] = ixSel;
				}
			}
		}
		
		for(int x=0;x<W;x++) {
			for(int y=0;y<=h0;y++) {
				msk[x][y][0] = true;
			}
		}
		for(int x=0;x<W;x++) {
			for(int y=h0;y<H;y++) {
				msk[x][y][1] = true;
			}
		}
		for(int x=0;x<=w0;x++) {
			for(int y=0;y<H;y++) {
				msk[x][y][2] = true;
			}
		}
		for(int x=w0;x<W;x++) {
			for(int y=0;y<H;y++) {
				msk[x][y][3] = true;
			}
		}
		
		// locations of centroid
		float[][][] sigDist = new float[T][4][nThr];		// weighted distance for each frame (four directions)
		for(int t=0;t<T;t++) {
			for(int i=0;i<4;i++) {
				for(int j=0;j<nThr;j++) {
					sigDist[t][i][j] = Float.NaN;
				}
			}
		}
		int[][] pixNum = new int[T][nThr];				// pixel number increase
		for(int t=t0;t<=t1;t++) {
			for(int k=0;k<nThr;k++) {
				int num = 0;
				for(int x=0;x<W;x++) {
					for(int y=0;y<H;y++) {
						if(volr0[x][y][t]>thr0[k])
							num++;
					}
				}
				pixNum[t][k] = num;
				
				for(int i=0;i<4;i++) {
					float sumX = 0;
					float sumY = 0;
					int cnt = 0;
					for(int x=0;x<W;x++) {
						for(int y=0;y<H;y++) {
							if(volr0[x][y][t]>thr0[k] && msk[x][y][i]) {
								sumX += x;
								sumY += y;
								cnt++;
							}
						}
					}
					if(cnt<4)
						continue;
					float seedw = sumX/cnt;
					float seedh = sumY/cnt;
					float dw = seedw - seedwInit;
					float dh = seedh - seedhInit;
					sigDist[t][i][k] = dw * kDi[i][0] + dh*kDi[i][1];
				}
			}
		}
		float[][][] prop = new float[T][4][nThr];

		for(int i=0;i<4;i++) {
			for(int j=0;j<nThr;j++) {
				for(int t=0;t<T;t++) {
					if(t>0) {
						prop[t][i][j] = sigDist[t][i][j] - sigDist[t-1][i][j];
//						if(prop[t][i][j]<0)
//							System.out.println("" + prop[t][i][j]);
					}else {
						prop[t][i][j] = Float.NaN;
					}
				}
			}
		}
		
		// propGrowMultiThr
		float[][] propGrow = new float[T][4];
		for(int t=0;t<T;t++) {
			for(int i=0;i<4;i++) {
				float max = -Float.MAX_VALUE;
				for(int j=0;j<nThr;j++) {
					if(!Float.isNaN(prop[t][i][j])&&prop[t][i][j]>=0) {
						max = Math.max(max, prop[t][i][j]);
					}
				}
				if(max==-Float.MAX_VALUE)
					max = 0;
				propGrow[t][i] = max;
			}
		}
		float[] propGrowOverall = new float[4];
		for(int i=0;i<4;i++) {
			float sum = 0;
			for(int t=0;t<T;t++) {
				sum += propGrow[t][i];
			}
			propGrowOverall[i] = sum;
		}
		
		// propShrinkMultiThr
		float[][] propShrink = new float[T][4];
		for(int t=0;t<T;t++) {
			for(int i=0;i<4;i++) {
				float max = -Float.MAX_VALUE;
				for(int j=0;j<nThr;j++) {
					if(!Float.isNaN(prop[t][i][j])&&prop[t][i][j]<=0) {
//						float tmp = prop[t][i][j];
						max = Math.max(max, prop[t][i][j]);
					}
				}
				if(max==-Float.MAX_VALUE)
					max = 0;
				propShrink[t][i] = max;
			}
		}
		float[] propShrinkOverall = new float[4];
		for(int i=0;i<4;i++) {
			float sum = 0;
			for(int t=0;t<T;t++) {
				sum += propShrink[t][i];
			}
			propShrinkOverall[i] = sum;
		}
		
		// pixNumChange
		int[][] pixNumChange = new int[T][nThr];
		float[] pixNumChangeRate = new float[T];
		for(int j=0;j<nThr;j++) {
			for(int t=1;t<T;t++) {
				pixNumChange[t][j] = pixNum[t][j] - pixNum[t-1][j];
			}
		}
		for(int t=0;t<T;t++) {
			float max = -Float.MAX_VALUE;
			for(int j=0;j<nThr;j++) {
				max = Math.max(max, (float)(pixNumChange[t][j])/nPix);
			}
			pixNumChangeRate[t] = max;
		}
		
		// max propagation speed
		HashMap<Integer, ArrayList<int[]>> boundaries = new HashMap<>();
		float[][] propMaxSpeed = new float[T][nThr];
		for(int t=Math.max(t0-1, 0);t<=t1;t++) {
			
			for(int k=0;k<nThr;k++) {
				boolean[][] pixCur = new boolean[W][H];
				for(int x=0;x<W;x++) {
					for(int y=0;y<H;y++) {
						pixCur[x][y] = volr0[x][y][t]>=thr0[k];
					}
				}
				
				ArrayList<int[]> curBound = BasicFeatureDealer.findMulBoundary2(pixCur);
				if(t!=Math.max(t0-1, 0)) {
					ArrayList<int[]> preBound = boundaries.get(k);
					for(int i=0;i<curBound.size();i++) {
						float dist = Float.MAX_VALUE;
						for(int j=0;j<preBound.size();j++) {
							int[] xy1 = curBound.get(i);
							int[] xy2 = preBound.get(j);
							float curdist = (float) Math.sqrt((xy1[0]-xy2[0])*(xy1[0]-xy2[0]) + (xy1[1]-xy2[1])*(xy1[1]-xy2[1]));
							dist = Math.min(dist, curdist);
						}
						if(dist<Float.MAX_VALUE) {
							propMaxSpeed[t][k] = Math.max(propMaxSpeed[t][k], dist*muPerPix);
						}
					}
					
					for(int i=0;i<preBound.size();i++) {
						float dist = Float.MAX_VALUE;
						for(int j=0;j<curBound.size();j++) {
							int[] xy1 = preBound.get(i);
							int[] xy2 = curBound.get(j);
							float curdist = (float) Math.sqrt((xy1[0]-xy2[0])*(xy1[0]-xy2[0]) + (xy1[1]-xy2[1])*(xy1[1]-xy2[1]));
							dist = Math.min(dist, curdist);
						}
						if(dist<Float.MAX_VALUE) {
							propMaxSpeed[t][k] = Math.max(propMaxSpeed[t][k], dist*muPerPix);
						}
					}
				}
				
				boundaries.put(k,curBound);
			}
		}
		
		
		// output
		for(int t=0;t<T;t++) {
			for(int i=0;i<4;i++) {
				propGrow[t][i] = propGrow[t][i] * muPerPix;
				propShrink[t][i] = propShrink[t][i] * muPerPix;
			}
		}
		for(int i=0;i<4;i++) {
			propGrowOverall[i] = propGrowOverall[i] * muPerPix;
			propShrinkOverall[i] = propShrinkOverall[i] * muPerPix;
		}
		float[][] areaChange = new float[T][nThr];
		float[][] areaFrame = new float[T][nThr];
		for(int t=0;t<T;t++) {
			for(int j=0;j<nThr;j++) {
				areaChange[t][j] = pixNumChange[t][j] * muPerPix * muPerPix;
				areaFrame[t][j] = pixNum[t][j] * muPerPix * muPerPix;
			}
		}
		
		ftsPg.propGrow.put(nEvt, propGrow);
		ftsPg.propGrowOverall.put(nEvt, propGrowOverall);
		ftsPg.propShrink.put(nEvt, propShrink);
		ftsPg.propShrinkOverall.put(nEvt, propShrinkOverall);
		ftsPg.areaChange.put(nEvt, areaChange);
		ftsPg.areaChangeRate.put(nEvt, pixNumChangeRate);
		ftsPg.areaFrame.put(nEvt, areaFrame);
		ftsPg.propMaxSpeed.put(nEvt, propMaxSpeed);
	}

	public FeatureTopResult getFeaturesTop(float[][][] datOrg, HashMap<Integer, ArrayList<int[]>> evtLst, int[][][] evtMap, Opts opts) {
		int W = datOrg.length;
		int H = datOrg[0].length;
		int T = datOrg[0][0].length;
		int changeParameter = Math.max(W, H);
		float[][][] dat = new float[W][H][T];
		float[][][] datx = new float[W][H][T];
		
		
		for(int t=0;t<T;t++) {
			for(int x=0;x<W;x++) {
				for(int y=0;y<H;y++) {
					if(opts.usePG) {
						dat[x][y][t] = datOrg[x][y][t]*datOrg[x][y][t];
						datx[x][y][t] = datOrg[x][y][t]*datOrg[x][y][t];
					}else {
						dat[x][y][t] = datOrg[x][y][t];
						datx[x][y][t] = datOrg[x][y][t];
					}
					if(evtMap[x][y][t]>0)
						datx[x][y][t] = -1;
				}
			}
		}
		
		float secondPerFrame = opts.frameRate;
		float muPerPix = opts.spatialRes;
		
		// impute events
		System.out.println("Imputing");
		imputeMov(datx);
		
		int Tww = Math.min(Math.round((float)T/4), opts.movAvgWin);
		float bbm = 0;
		
		// ftsLst
		FtsLst ftsLst = new FtsLst();
		// basic
		// propagation
		
		// foptions
		
		int nEvt = evtLst.size();
		imageDealer.center.nEvt.setText("nEvt");
		imageDealer.center.EvtNumber.setText(nEvt+"");
		
		float[][][] dMat = new float[nEvt][T][2];
		float[][][] dffMat = new float[nEvt][T][2];
		
		for(int i=1;i<=nEvt;i++) {
			if(i%100==0)
				System.out.println(i + "/" + nEvt);
			
			ArrayList<int[]> pix0 = evtLst.get(i);
			if(pix0==null || pix0.size()==0)
				continue;
			
			int rghs = Integer.MAX_VALUE;
			int rgws = Integer.MAX_VALUE;
			int rgts = Integer.MAX_VALUE;
			int rghe = Integer.MIN_VALUE;
			int rgwe = Integer.MIN_VALUE;
			int rgte = Integer.MIN_VALUE;
			HashSet<Integer> ihw = new HashSet<>();
			for(int[] p:pix0) {
				rgws = Math.min(rgws, p[0]);
				rghs = Math.min(rghs, p[1]);
				rgts = Math.min(rgts, p[2]);
				rgwe = Math.max(rgwe, p[0]);
				rghe = Math.max(rghe, p[1]);
				rgte = Math.max(rgte, p[2]);
				ihw.add(p[0]*changeParameter + p[1]);
			}
			int its = rgts;
			int ite = rgte;
			rgws = Math.max(rgws-1, 0);
			rghs = Math.max(rghs-1, 0);
			rgts = Math.max(rgts-1, 0);
			rgwe = Math.min(rgwe+1, W-1);
			rghe = Math.min(rghe+1, H-1);
			rgte = Math.min(rgte+1, T-1);
			
			// dff
			// in xy plane, get the sum of label==i
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
			
			float[] charx1 = curvePolyDeTrend(charxIn1, sigz, opts.correctTrend);
			
			// noise level using baseline part of the signal
			float[] charxMv1 = MinMoveMean.moveMean(charx1,Tww);
			float charxMin1 = Float.MAX_VALUE;
			int ixMin = 0;
			for(int t=0;t<T;t++) {
				if(charxMv1[t]<charxMin1) {
					charxMin1 = charxMv1[t];
					ixMin = t;
				}
			}
			int rg00s = Math.max(ixMin - Tww/2, 0);
			int rg00e = Math.min(ixMin + Tww/2, T-1);
			float[] charx1Base = new float[rg00e-rg00s+1];
			for(int t=rg00s;t<=rg00e;t++) {
				charx1Base[t-rg00s] = charx1[t];
			}
			float sigma1 = MinMoveMean.getNoiseSigmal(charx1Base);
			
			float charxBg1 = MinMoveMean.minMoveMean(charx1, Tww);
			charxBg1 = charxBg1 - bbm*sigma1 - opts.bgFluo * opts.bgFluo;
			float[] dff1 = new float[T];
			for(int t=0;t<T;t++) {
				dff1[t] = (charx1[t] - charxBg1)/charxBg1;
			}
			
			float[] dff1Base = new float[rg00e-rg00s+1];
			for(int t=rg00s;t<=rg00e;t++) {
				dff1Base[t-rg00s] = dff1[t];
			}
			float sigma1dff = MinMoveMean.getNoiseSigmal(dff1Base);
			float dffMax1 = -Float.MAX_VALUE;
			for(int t=rgts;t<=rgte;t++) {
				dffMax1 = Math.max(dffMax1, dff1[t]);
			}
			
			
			
			// dff without other events
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
			NormalDistribution normal = new NormalDistribution();
			float dffMaxPval = (float) (1 - normal.cumulativeProbability(dffMaxZ));
			
			
			
			// extend event window in the curve
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
			int rgT1s = rgT1[0];
			int rgT1e = rgT1[1];
			float[] dff2e = new float[rgT1e - rgT1s+1];
			for(int t=rgT1s;t<=rgT1e;t++) {
				dff2e[t-rgT1s] = dff2[t];
			}
			
			
			
			
			// curve features
			GetCurveResult curveResult = getCurveStat(dff2e, secondPerFrame, opts.ignoreTau);
			
			for(int t=0;t<T;t++) {
				dffMat[i-1][t][0] = dff1[t];
				dffMat[i-1][t][1] = dff2[t];
				dMat[i-1][t][0] = charx1[t]*opts.maxValueDat;
				dMat[i-1][t][1] = charx2[t]*opts.maxValueDat;
			}
			
			// table value
			minDuration = Math.min(minDuration, curveResult.width55);
			maxDuration = Math.max(maxDuration, curveResult.width55);
			mindffMax = Math.min(mindffMax, dffMax1);
			maxdffMax = Math.max(maxdffMax, dffMax1);
			minPvalue = Math.min(dffMaxPval, minPvalue);
			maxPvalue = Math.max(dffMaxPval, maxPvalue);
			minDecayTau = Math.min(minDecayTau, curveResult.decayTau);
			maxDecayTau = Math.max(maxDecayTau, curveResult.decayTau);
			
			ftsLst.loc.t0.put(i, its);
			ftsLst.loc.t1.put(i, ite);
			ftsLst.loc.x3D.put(i, pix0);
			ftsLst.loc.x2D.put(i, ihw);
			ftsLst.curve.rgt1.put(i, rgT1);
			ftsLst.curve.dffMax.put(i, dffMax1);
			ftsLst.curve.dffMax2.put(i, dffMax2);
			ftsLst.curve.dffMaxFrame.put(i, (tMax + rgts)*secondPerFrame);
			ftsLst.curve.dffMaxZ.put(i, dffMaxZ);
			ftsLst.curve.dffMaxPval.put(i, dffMaxPval);
			ftsLst.curve.tBegin.put(i, its);
			ftsLst.curve.tEnd.put(i, ite);
			ftsLst.curve.duration.put(i,(ite-its+1)*secondPerFrame);
			ftsLst.curve.rise19.put(i, curveResult.rise19);
			ftsLst.curve.fall91.put(i, curveResult.fall91);
			ftsLst.curve.width55.put(i, curveResult.width55);
			ftsLst.curve.width11.put(i, curveResult.width11);
			ftsLst.curve.decayTau.put(i, curveResult.decayTau);
			
			// basic features
			int[][][] voxi = new int[rgwe-rgws+1][rghe-rghs+1][ite-its+1];
			for(int[] p:pix0) {
				int x = p[0] - rgws;
				int y = p[1] - rghs;
				int t = p[2] - its;
				voxi[x][y][t] = 1;
			}
			
			getBasicFeatures(voxi,muPerPix, i, ftsLst.basic);
			
			// border
			boolean[][] map2D = new boolean[W][H];
			for(int[] p:pix0) {
				map2D[p[0]][p[1]] = true;
			}
			ArrayList<int[]> boundary = BasicFeatureDealer.findBoundary(map2D);
			ftsLst.border.put(i, boundary);
			
		}
		
		
		
		
		
		return new FeatureTopResult(ftsLst,dffMat,dMat);
		
	}
	
	private void getBasicFeatures(int[][][] voxli0, float muPerPix, int nEvt, Basic ftsBase) {
		// basic features
		int W = voxli0.length;
		int H = voxli0[0].length;
		int T = voxli0[0][0].length;
		
		int[][] map = new int[W][H];
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				int sum = 0;
				for(int t=0;t<T;t++) {
					sum += voxli0[x][y][t];
				}
				map[x][y] = sum;
			}
		}
		ftsBase.map.put(nEvt, map);

		float area = 0;
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				if(map[x][y]>0)
					area++;
			}
		}
		
		area = area * muPerPix * muPerPix;
		minArea = Math.min(area, minArea);
		maxArea = Math.max(area, maxArea);
		
		ftsBase.area.put(nEvt, area);
		
		float perimeter = BasicFeatureDealer.calculatePerimeter(map);
		perimeter = perimeter * muPerPix;
		ftsBase.perimeter.put(nEvt, perimeter);
		
		float circMetric = (float) (perimeter*perimeter/(4*Math.PI*area));
		ftsBase.circMetric.put(nEvt, circMetric);
		
	}

	private GetCurveResult getCurveStat(float[] x0, float spf, int ignoreTau) {
		float xPeak = -Float.MAX_VALUE;
		int tPeak =0;
		for(int t=0;t<x0.length;t++) {
			if(x0[t]>xPeak) {
				xPeak = x0[t];
				tPeak = t;
			}
		}
		
		int[][] pp = new int[3][2];		// 10&, 50%, 90% by start/end
		float[] thrVec = new float[] {0.1f,0.5f,0.9f};
		
		for(int n=0;n<3;n++) {
			int tPre = 0;
			for(int t=tPeak;t>=0;t--) {
				if(x0[t]<xPeak*thrVec[n]) {
					tPre = t;
					break;
				}
			}
			
			int tPost = x0.length-1;
			for(int t=tPeak;t<x0.length;t++) {
				if(x0[t]<xPeak*thrVec[n]) {
					tPost = t;
					break;
				}
			}
			
			pp[n][0] = tPre;
			pp[n][1] = tPost;
		}
		
		float rise19 = (pp[2][0]-pp[0][0]+1)*spf;
		float fall91 = (pp[0][1]-pp[2][1]+1)*spf;
		float width55 = (pp[1][1]-pp[1][0])*spf;
		float width11 = (pp[0][1]-pp[0][0])*spf;
		
		// exponential decay time constant, in ms
		float[] y = new float[pp[0][1]-tPeak+1];
		for(int t=tPeak;t<=pp[0][1];t++) {
			y[t-tPeak] = x0[t];
		}
		float decayTau = Float.NaN;
		
		if(y.length>=2 &&	ignoreTau==0) {
			float minY = Float.MAX_VALUE;
			for(int t=0;t<y.length;t++) {
				minY = Math.min(minY, y[t]);
			}
			for(int t=0;t<y.length;t++) {
				y[t] = y[t] - minY;
			}
			float maxY = -Float.MAX_VALUE;
			for(int t=0;t<y.length;t++) {
				maxY = Math.max(maxY, y[t]);
			}
			for(int t=0;t<y.length;t++) {
				y[t] = y[t]/maxY + 0.05f;
			}
			
			// exponential fitter
			WeightedObservedPoints obs = new WeightedObservedPoints();
			for(int t=0;t<y.length;t++) {
				double ytmp = Math.log(y[t]);
				obs.add(t,ytmp);
			}
			PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);
			double[] coeff = fitter.fit(obs.toList());
			
			decayTau = (float) (-1/coeff[1]*spf);
		}
		
		
		
		return new GetCurveResult(rise19,fall91,width55,width11,decayTau,pp);
	}
	
	class GetCurveResult{
		float rise19 = 0;
		float fall91 = 0;
		float width55 = 0;
		float width11 = 0;
		float decayTau = 0;
		int[][] pp = null;
		public GetCurveResult(float rise19, float fall91, float width55, float width11, float decayTau, int[][] pp) {
			this.rise19 = rise19;
			this.fall91 = fall91;
			this.width55 = width55;
			this.width11 = width11;
			this.decayTau = decayTau;
			this.pp = pp;
		}
	}

	private int[] extendEventTimeRangeByCurve(float[] dff, boolean[] sigxOthers, int rgts, int rgte) {
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
	
	private float[] curvePolyDeTrend(float[] c0, boolean[] s0, int correctTrend) {
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
	
	public void imputeMov(float[][][] datx) {
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
			tmp = GaussFilter.gaussFilter(tmp, 1, 1);
			for(int i=0;i<W;i++) {
				for(int j=0;j<H;j++) {
					datx[i][j][k] = tmp[i][j];
				}
			}
			
		}
	}
}
