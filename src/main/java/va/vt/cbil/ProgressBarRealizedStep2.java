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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

/**
 * The second step of the whole software, grow the event seeds 
 * we find in the first step. First we grow the seeds in time dimension,
 * then grow them in each frame. All the points extended from one seed 
 * we call them one super voxel. According the seeds, we could find many
 * super voxels and consider them as the prelimiary events. After this step
 * finish, we show them in interface with different colors. 
 * 
 * @author Xuelong Mi
 * @version 1.0
 */
/**
 * @author Xuelong Mi
 *
 */
public class ProgressBarRealizedStep2 extends SwingWorker<int[][][], Integer> {
	JFrame frame = new JFrame("Step2");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	
	static long start = System.currentTimeMillis();;
	static long end;
	ImageDealer imageDealer = null;
	int width = 0;
	int height = 0;
	boolean[][] evtSpatialMask = null;
	String proPath = null;
	int[][] regionMarkLabel = null;
	
	/**
	 * Construct the class by imageDealer. 
	 * 
	 * @param imageDealer used to read the parameter
	 */
	public ProgressBarRealizedStep2(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
		proPath = imageDealer.proPath;
		width = (int) imageDealer.getOrigWidth();
		height = (int) imageDealer.getOrigHeight();
		evtSpatialMask = new boolean[width][height];
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				evtSpatialMask[x][y] = imageDealer.regionMark[x][y];
			}
		}
		regionMarkLabel = imageDealer.regionMarkLabel;
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
	 * Grow the seeds in first step, form the super voxels
	 * 
	 * @return return the labels of different super voxels
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected int[][][] doInBackground() throws Exception {
		// valid Region
		boolean noRegion = true;
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(evtSpatialMask[i][j]) {
					noRegion = false;
					break;
				}
			}
			if(!noRegion)
				break;
		}
		
		if(noRegion) {
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					evtSpatialMask[i][j] = true;
				}
			}
		}
		
		publish(1);
		// ------------------------ Read Data ----------------------------- //
		HashMap<int[],Float> seeds = null;;
		float[][][] dat = null;;
		float[][][] dF = null;;
		Opts opts = imageDealer.opts;
		try {
			FileInputStream fi = null;
			ObjectInputStream oi = null;
			
			fi = new FileInputStream(new File(proPath + "ResultInStep1_Seeds.ser"));	
			oi = new ObjectInputStream(fi);
			seeds = (HashMap<int[],Float>)oi.readObject();
			oi.close();
			fi.close();
			
			fi = new FileInputStream(new File(proPath + "Data.ser"));	
			oi = new ObjectInputStream(fi);
			dat = (float[][][])oi.readObject();
			oi.close();
			fi.close();
			
			fi = new FileInputStream(new File(proPath + "DF.ser"));	
			oi = new ObjectInputStream(fi);
			dF = (float[][][])oi.readObject();
			oi.close();
			fi.close();
			
			showTime();
					
			oi.close();
			fi.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// ---------------------- Algorithm -------------------------------- //
		// input parameters
		opts.thrTWScl = imageDealer.opts.thrTWScl;
		opts.thrExtZ = imageDealer.opts.thrExtZ;
		
		// default parameters
		int width = dat.length;
		int height = dat[0].length;
		int pages = dat[0][0].length;
		
		// sort the seeds by their lightness
		ArrayList<Map.Entry<int[],Float>> seedsList = sortSeeds(seeds);
		
		publish(2);
		// grow seeds
		Res[] resCell = new Res[seedsList.size()];
		int[][][] lblMap = new int[width][height][pages];
		opts.maxStp = 1;
		boolean[][][] lmAll = new boolean[width][height][pages];
		for(Map.Entry<int[],Float> entry:seedsList) {
			int[] p = entry.getKey();
			lmAll[p[0]][p[1]][p[2]] = true;
		}
		HashMap<Integer, ArrayList<int[]>> map = new HashMap<>(); 
		growSeed(dat,dF,resCell,lblMap,seedsList,lmAll,opts,0,map);	// checked
		
		// grow seeds
		for(int i=1;i<=40;i++) {
			System.out.println("Grow " + i);
			lmAll = new boolean[width][height][pages];
			for(Map.Entry<int[],Float> entry:seedsList) {
				int[] p = entry.getKey();
				lmAll[p[0]][p[1]][p[2]] = true;
			}
			growSeed(dat,dF,resCell,lblMap,seedsList,lmAll,opts,i,map);
		}
		opts.maxStp = 11;		
		
		publish(3);
		// clean super voxels
			// by size
		System.out.println("Cleanning super voxels by size");
			// by mask
		map = cleanByMask(lblMap,map,evtSpatialMask);
		try {
			FileOutputStream f = null;
			ObjectOutputStream o = null;
			
			f = new FileOutputStream(new File(proPath + "Grow1lblMap.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(lblMap);
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		FilterAndFillResult fr = filterAndFillSp(lblMap,map);	// only delete		
		lblMap = fr.lblMap;
		map = fr.map;

			// by z score
		System.out.println("Cleanning super voxels by z score");
		float[] zVec = getSpz(dat,lblMap,opts,map);	// checked
		
		HashMap<Integer,ArrayList<int[]>> newMap = new HashMap<>();
		int[][][] newlblMap = new int[width][height][pages];
		
		int cnt = 1;
		for(int i=1;i<=map.size();i++) {
			if(zVec[i-1]>opts.thrSvSig) {
				ArrayList<int[]> points = map.get(i);
				newMap.put(cnt, points);
				for(int[] p :points) {
					newlblMap[p[0]][p[1]][p[2]] = cnt;
				}
				cnt++;
			}
		}

		map = newMap;
		lblMap = newlblMap;
		
		showTime();
		
		publish(4);
		// Extend and re-fit each patch, estimate delay, reconstruct signal
		HashMap<Integer, ArrayList<int[]>> lblxMap = new HashMap<>();
		int[][][] lblMapEx = new int[width][height][pages];
		MidResult result =  getSpDelay(dat,lblMap,map,opts,lblxMap,lblMapEx);
		
		lblxMap = result.map;
		int[][] riseX = result.riseX;
		lblMapEx = result.lblMapEx;
		
		System.out.println(lblxMap.size());
		showTime();
		
		// Save
		publish(5);
		try {
			FileOutputStream f = null;
			ObjectOutputStream o = null;
			
			f = new FileOutputStream(new File(proPath + "Step2_Labels.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(lblMapEx);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath + "ResultInStep2_RiseX.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(riseX);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath + "Opts.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(opts);
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return lblMapEx;
	}
	
	/**
	 * We may get the valid region from the interface. 
	 * Here we limit all the super voxels inside the valid region.
	 * 
	 * @param lblMap the label matrix of super voxels.
	 * @param map the hashmap for saving the label and positions of super voxels.
	 * @param evtSpatialMask the valid region we get from the interface.
	 * @return the map after cleanning.
	 */
	private HashMap<Integer, ArrayList<int[]>> cleanByMask(int[][][] lblMap, HashMap<Integer, ArrayList<int[]>> map, boolean[][] evtSpatialMask) {
		int W = lblMap.length;
		int H = lblMap[0].length;
		int T = lblMap[0][0].length;
		
		
		map = new HashMap<>();
		for(int k=0;k<T;k++) {
			for(int i=0;i<W;i++) {
				for(int j=0;j<H;j++) {
					if(!evtSpatialMask[i][j])
						lblMap[i][j][k] = 0;
					
				}
			}
		}
		
		map = label2idx(lblMap);
		ArrayList<ArrayList<int[]>> mapNew = new ArrayList<>();
		for(Entry<Integer, ArrayList<int[]>> entry:map.entrySet()) {
			ArrayList<int[]> points = entry.getValue();
			HashMap<Integer,Integer> link = new HashMap<>();
			
			for(int[] p:points) {
				int x = p[0];
				int y = p[1];
				int cellIndex = regionMarkLabel[x][y];
				int index = 0;
				if(!link.keySet().contains(cellIndex)) {
					index = mapNew.size();
					link.put(cellIndex, index);
					mapNew.add(new ArrayList<int[]>());
				}else {
					index = link.get(cellIndex);
				}
				mapNew.get(index).add(p);
			}
		}
		
		int cnt = 1;
		for(ArrayList<int[]> points:mapNew) {
			map.put(cnt,points);
			cnt++;
		}
		
		return map;
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
	
	/** 
	 * Report the progress.
	 */
	protected void process(List<Integer> chunks) {
		int value = chunks.get(chunks.size()-1);
		String str = "";
		switch(value) {
		case 1:
			str = "Read the Data 1/5";
			break;
		case 2:
			str = "Grow Seeds 2/5";
			break;
		case 3:
			str = "Clean Super Voxels 3/5";
			break;
		case 4:
			str = "Extend and Re-fit Each Patch 4/5";
			break;
		case 5:
			str = "Save the Results 5/5";
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
//		JOptionPane.showMessageDialog(null, "Step2 Finish!");
		imageDealer.left.nextButton.setEnabled(true);
		imageDealer.left.backButton.setEnabled(true);
		if(imageDealer.left.jTPStatus<2) {
			imageDealer.left.jTPStatus = Math.max(imageDealer.left.jTPStatus, 2);;
			imageDealer.right.typeJCB.addItem("Step2: Super Voxels");
		}
		imageDealer.left.jTP.setEnabledAt(2, true);
		
//		imageDealer.right.typeJCB.setSelectedIndex(2);
		
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
	 * For return mid-result
	 * 
	 * @author Xuelong Mi
	 */
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
	
	/**
	 * Extend and re-fit each patch, estimate delay, reconstruct signal
	 * Get the delay of each super pixel
	 * 
	 * @param dat the data matrix
	 * @param lblMap the label matrix of super voxels.
	 * @param map the hashmap for saving the label and positions of super voxels.
	 * @param opts the class for reading the parameters
	 * @param lblxMap the hashmap for saving the label and positions of super voxels after extending.
	 * @param lblMapEx the label matrix of super voxels after extending.
	 * @return MidResult, contains the rising/starting time of each super voxel at different level.
	 */
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

	
	/**
	 * Extend super voxels again in time dimension.
	 * 
	 * @param lblMap the label matrix of super voxels.
	 * @param map the hashmap for saving the label and positions of super voxels.
	 * @param dfSmo the data after 2D gaussian blur and subtracting the background
	 * @param varEst the variance of the data
	 * @param lblMapEx the label matrix of super voxels after extending. 
	 * @return lblxMap the hashmap for saving the label and positions of super voxels after extending.
	 */
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

	/**
	 * After extend or delete, the label number may be not continuous.
	 * Here we relabel them.
	 * 
	 * @param map the hashmap for saving the label and positions of super voxels.
	 * @param lblMap the label matrix of super voxels.
	 * @return map after relabeling
	 */
	public HashMap<Integer, ArrayList<int[]>> relabel(HashMap<Integer, ArrayList<int[]>> map, int[][][] lblMap) {
		HashMap<Integer, ArrayList<int[]>> mapAfterDelete = new HashMap<>();
		int label = 1;
		for(Map.Entry<Integer,ArrayList<int[]>> entry:map.entrySet()) {
			ArrayList<int[]> points = entry.getValue();
			for(int[] p:points) {
				lblMap[p[0]][p[1]][p[2]] = label;
			}
			mapAfterDelete.put(label, entry.getValue());
			label++;
		}
		map = mapAfterDelete;
		
		return map;
	}

	/**
	 * Calculate the z score of each super voxel.
	 * 
	 * @param dat the data matrix
	 * @param lblMap the label matrix of super voxels.
	 * @param opts the class for reading the parameters
	 * @param map the hashmap for saving the label and positions of super voxels.
	 * @return z score array
	 */
	private float[] getSpz(float[][][] dat, int[][][] lblMap, Opts opts, HashMap<Integer, ArrayList<int[]>> map) {
		float s0 = (float) Math.sqrt(opts.varEst);
		
		int width = dat.length;
		int height = dat[0].length;
		int pages = dat[0][0].length;
		int changeParameter = Math.max(width, height);
		
		float[] zVec = new float[map.size()];
		
		for(int i = 1;i<=map.size();i++) {
			ArrayList<int[]> points = map.get(i);
			
			int minT = pages;
			int maxT = 0;
			HashSet<Integer> points2D = new HashSet<>();
			for(int[] p:points) {
				minT = Math.min(minT, p[2]);
				maxT = Math.max(maxT, p[2]);
				points2D.add(p[0]*changeParameter + p[1]);
			}
			ArrayList<Integer> points2DList = new ArrayList<>(points2D); 
			int T0 = Math.max(minT-2, 0);
			int T1 = Math.min(maxT+2, pages-1);
			float[][] x = new float[points2DList.size()][T1-T0+1];
			float[] xm = new float[T1-T0+1];
			
			for(int pt = T0;pt<=T1;pt++) {
				float sum = 0;
				int cnt = 0;
				for(int u=0;u<points2DList.size();u++) {
					int xy = points2DList.get(u);
					int px = xy/changeParameter;
					int py = xy%changeParameter;
					int label = lblMap[px][py][pt];
					if(label>0&&label!=i) {
						x[u][pt-T0] = -1;
					}else {
						x[u][pt-T0] = dat[px][py][pt];
						sum += x[u][pt-T0];
						cnt++;
					}
				}
				if(cnt==0)
					xm[pt-T0] = -1;
				else
					xm[pt-T0] = sum/cnt;
			}
			
			// Peak
			int tPeak = 0;
			float max = -2;
			for(int t=0;t<=T1-T0;t++) {
				if(xm[t]!=-1&&xm[t]>max) {
					max = xm[t];
					tPeak = t;
				}
			}
			
			int t0 = 0;
			float min = Float.MAX_VALUE;
			for(int t=tPeak;t>=0;t--) {
				if(xm[t]!=-1&&xm[t]<min) {
					min = xm[t];
					t0 = t;
				}
			}
			
			int t1 = T1-T0;
			min = Float.MAX_VALUE;
			for(int t = tPeak;t<=T1-T0;t++) {
				if(xm[t]!=-1&&xm[t]<min) {
					min = xm[t];
					t1 = t;
				}
			}
			
			float s00 = (float) (s0/Math.sqrt(points2DList.size()));
			
			zVec[i-1] = Math.min(nanMean(x,tPeak,t0),nanMean(x,tPeak,t1))/s00;
		}
		return zVec;
	}
	
	/**
	 * In the process, we may assign -1 as nan.
	 * Here we calculate the mean value of x[i][tPeak] - x[i][t] except nan.
	 * 
	 * @param x the input matrix
	 * @param tPeak one position in time dimension
	 * @param t another position in time dimension
	 * @return the mean value of the tPeak column - the t column
	 */
	public float nanMean(float[][] x, int tPeak, int t) {
		int n = x.length;
		int cnt = 0;
		float sum = 0;
		for(int i=0;i<n;i++) {
			if(x[i][tPeak]==-1||x[i][t]==-1)
				continue;
			else {
				sum += x[i][tPeak] - x[i][t];
				cnt++;
			}
		}
		
		return sum/cnt;
	}
	
	/**
	 * Filter the super voxel whose size less than 4.
	 * 
	 * @param lblMap the label matrix of super voxels.
	 * @param map the hashmap for saving the label and positions of super voxels.
	 * @return 
	 */
	public FilterAndFillResult filterAndFillSp(int[][][] lblMap, HashMap<Integer, ArrayList<int[]>> map) {
		int width = lblMap.length;
		int height = lblMap[0].length;
		int changeParameter = Math.max(width, height);
		int pages = lblMap[0][0].length;
		// remove seeds with small regions patch
		int nLm = 0;
		for(Entry<Integer,ArrayList<int[]>> entry:map.entrySet()) {
			nLm = Math.max(nLm, entry.getKey());
		}
		boolean[] idxSel = new boolean[nLm];
		for(int i=0;i<nLm;i++) {
			ArrayList<int[]> points = map.get(i+1);
			if(points==null)
				continue;
			HashSet<Integer> fiux0g = new HashSet<>();
			for(int[] p:points) {
				int x = p[0];
				int y = p[1];
				fiux0g.add(x*changeParameter + y);
			}
			if(fiux0g.size()>4||points.size()>8) {
				idxSel[i] = true;
			}
		}
		
		int label = 1;
		int[][][] lblMap2 = new int[width][height][pages];
		HashMap<Integer, ArrayList<int[]>> map2 = new HashMap<>();
		for(int i=0;i<nLm;i++) {
			if(idxSel[i]) {
				ArrayList<int[]> points = map.get(i+1);
				for(int[] p:points) {
					lblMap2[p[0]][p[1]][p[2]] = label; 
				}
				map2.put(label, points);
				label++;
			}
		}
		
		// TODO fill holes
		return new FilterAndFillResult(lblMap2,map2);
	}
	
	class FilterAndFillResult {
		int[][][] lblMap = null;
		HashMap<Integer, ArrayList<int[]>> map = null;
		FilterAndFillResult(int[][][] lblMap, HashMap<Integer, ArrayList<int[]>> map){
			this.lblMap = lblMap;
			this.map = map;
		}
	}
	
	/**
	 * Show the time used
	 */
	static void showTime() {
		end = System.currentTimeMillis();
		System.out.println((end-start) + "ms");
		start = end;
	}
	
	
	/**
	 * According the lightness to sort the event seeds. 
	 * The Lighter the seed is, the more forward the position is.
	 * 
	 * @param seeds the event seeds we find in first step.
	 * @return the sorted seed list.
	 */
	public ArrayList<Map.Entry<int[],Float>> sortSeeds(HashMap<int[],Float> seeds){
		Set<Map.Entry<int[],Float>> entrySet = seeds.entrySet();
		ArrayList<Map.Entry<int[],Float>> seedsList = new ArrayList<Map.Entry<int[],Float>>(entrySet);
		
		Collections.sort(seedsList, new Comparator<Map.Entry<int[],Float>>() {
			
			@Override
			public int compare(Entry<int[], Float> e1, Entry<int[], Float> e2) {
				if(e1.getValue()<e2.getValue())
					return 1;
				else if (e1.getValue()>e2.getValue())
					return -1;
				else
					return 0;
			}

			
		});
	
		return seedsList;
		
	}

	/**
	 * Catch the curve of the seed first, then grow the seed by finding similar neighbors.
	 * 
	 * @param dat the data matrix
	 * @param dF the data matrix after subtract the background
	 * @param resCell res array used to save the curve feature of the seed
	 * @param lblMap the label matrix of super voxels.
	 * @param seedsList event seed list
	 * @param lmAll boolean matrix to determine whether the seed eaten by others
	 * @param opts the class for reading the parameters 
	 * @param stg to determine whether it is the first time to grow
	 * @param map the hashmap for saving the label and positions of super voxels.
	 */
	public void growSeed(float[][][] dat, float[][][] dF, Res[] resCell, int[][][] lblMap, ArrayList<Map.Entry<int[],Float>> seedsList, boolean[][][] lmAll, Opts opts, int stg, HashMap<Integer, ArrayList<int[]>> map) {
		int width = dat.length;
		int height = dat[0].length;
		int pages = dat[0][0].length;
		int nLm = seedsList.size();
		float thrTW = (float) (opts.thrTWScl*Math.sqrt(opts.varEst));	//temporal cut threshold
		float thrARBase = (float) (opts.thrARScl*Math.sqrt(opts.varEst));	// growing z threshold
		int tExt = Math.max(opts.maxStp, 5);
		int extWin = opts.getTimeWindowExt;
		int xRm = opts.seedRemoveNeib;
		int sNb = opts.seedNeib;
		
		
		// prepare data
		
		HashMap<Integer,float[][][]> datc = new HashMap<>();
		
		for(int i=0;i<nLm;i++) {
			int[] iSeed = seedsList.get(i).getKey();
			Res res = resCell[i];
			
			// eaten by others
			if(!lmAll[iSeed[0]][iSeed[1]][iSeed[2]])
				continue;
			if(stg==0) {
				
				
				// time window size
				int rgW1s = Math.max(iSeed[0]-sNb, 0);
				int rgW1e = Math.min(iSeed[0]+sNb, width-1);		
				int rgH1s = Math.max(iSeed[1]-sNb, 0);
				int rgH1e = Math.min(iSeed[1]+sNb, height-1);	
				int rgW2s = Math.max(iSeed[0]-xRm, 0);
				int rgW2e = Math.min(iSeed[0]+xRm, width-1);		
				int rgH2s = Math.max(iSeed[1]-xRm, 0);
				int rgH2e = Math.min(iSeed[1]+xRm, height-1);		
				int rgTs = Math.max(iSeed[2]-extWin-tExt, 0);
				int rgTe = Math.min(iSeed[2]+extWin+tExt, pages-1);
					
				// time window detection
				float[] x1 = getMeanInTime(dat, rgW1s,rgW1e,rgH1s,rgH1e,rgTs,rgTe);
				float[] df1 = getMeanInTime(dF, rgW1s,rgW1e,rgH1s,rgH1e,rgTs,rgTe);
				boolean[] z1 = getMaxInTime(lmAll, rgW2s,rgW2e,rgH2s,rgH2e,rgTs,rgTe);
				
				int itS1 = iSeed[2] - rgTs;
				
				// find the time window
				int[] timeWindow = getTimeWindow2a(x1, itS1, thrTW, z1, df1, extWin);
				if(timeWindow == null)
					timeWindow = getTimeWindow2a(x1, itS1, thrTW, new boolean[z1.length], df1, extWin);
				if(timeWindow == null) {	//weak signal, go to base line
					float thrAR;
					if(df1.length>50) {
						float s00 = getMedianDf(df1);
						thrAR = s00*opts.thrARScl;
					}else
						thrAR = thrARBase;
					timeWindow = getTimeWindow2b(x1, itS1, thrAR, df1, extWin);;
				}
				if(timeWindow == null) {
					lmAll[iSeed[0]][iSeed[1]][iSeed[2]] = false;
					continue;
				}
					
				// update seed map
				updateSeedMap(lmAll,rgW2s,rgW2e,rgH2s,rgH2e,timeWindow[2]+rgTs,timeWindow[3]+rgTs);
				
				// initialize res
				res = new Res(x1, timeWindow, iSeed, rgH1s, rgH1e, rgW1s, rgW1e, rgTs, rgTe, true, false);
				resCell[i] = res;
				
			}
			else {
				// based on current res. fiux, ** rgT is unchanged **
				// update location related items in res
				
				if(res!=null && res.cont) {
//					HashMap<Integer,int[]> fiux = res.fiux;
//					ArrayList<int[]> pixBad = res.pixBad;
					int rgWs = res.rgWs;
					int rgWe = res.rgWe;
					int rgHs = res.rgHs;
					int rgHe = res.rgHe;
					
					// extend window by 1
					int rgWs2 = Math.max(rgWs-1, 0);
					int rgWe2 = Math.min(rgWe+1, width-1);
					int rgHs2 = Math.max(rgHs-1, 0);
					int rgHe2 = Math.min(rgHe+1, height-1);
					res.rgWs = rgWs2;
					res.rgWe = rgWe2;
					res.rgHs = rgHs2;
					res.rgHe = rgHe2;
					
					// update fiux: visited voxel
					for(Map.Entry<Integer,int[]> entry:res.fiux.entrySet()) {
						int[] p = entry.getValue();
						p[0] += rgWs-rgWs2;
						p[1] += rgHs-rgHs2;
					}
					
					// update pixBad: Bad voxel ...
					for(int[] p:res.pixBad) {
						p[0] += rgWs-rgWs2;
						p[1] += rgHs-rgHs2;
					}
					
				}else {
					continue;
				}
				
			}
			float[][][] dataTemp = cropData(dat,res.rgWs,res.rgWe,res.rgHs,res.rgHe,res.rgTs,res.rgTe);
			datc.put(i, dataTemp);
		}		
		
		
		// fit around seeds			
		for(int i=0;i<nLm;i++) {
			Res res = resCell[i];
			if(res == null || !res.cont) {
				continue;
			}
			
			resCell[i] = detectGrowSp(datc.get(i),res,opts);
		}
			
		// grow seeds
		for(int i=0;i<nLm;i++) {
			Res res = resCell[i];
			if(res == null || !res.cont) {
				continue;
			}
			boolean isGoodSum = false;
			int rgHs = res.rgHs;
//			int rgHe = res.rgHe;
			int rgWs = res.rgWs;
//			int rgWe = res.rgWe;
			int rgTs = res.rgTs;
//			int rgTe = res.rgTe;
			
			for(int k=0;k<res.pixNew.size();k++) {
				int index = res.pixNew.get(k);
				int x = res.fiux.get(index)[0] + rgWs;
				int y = res.fiux.get(index)[1] + rgHs;
				int tw2 = res.twMap.get(index)[4] + rgTs;
				int tw3 = res.twMap.get(index)[5] + rgTs;
				int tw4 = res.twMap.get(index)[6] + rgTs; 
				boolean isGood = false;
				boolean tmp = false;

				for(int t = tw2;t<=tw3;t++) {
					if(lblMap[x][y][t]!=0) {
						tmp = true;
					}
				}
				isGood = !tmp;
				if(isGood) {
					isGoodSum = true;
					for(int t = tw4;t<=tw3;t++) {
						if(lblMap[x][y][t]>0) {
							break;
						}else {
							lblMap[x][y][t] = i+1;					// change from i to i+1
							ArrayList<int[]> l = map.get(i+1);
							if(l==null) {
								l = new ArrayList<>();
							}
							l.add(new int[] {x,y,t});
							map.put(i+1, l);
						}
					}
					
					for(int t = tw4-1;t>=tw2;t--) {
						if(lblMap[x][y][t]>0) {
							break;
						}else {
							lblMap[x][y][t] = i+1;
							ArrayList<int[]> l = map.get(i+1);
							if(l==null) {
								l = new ArrayList<>();
							}
							l.add(new int[] {x,y,t});
							map.put(i+1, l);
						}
					}
				}else {
					// lose the competition
					res.pixBad.add(new int[] {x-rgWs,y-rgHs});
				}
			}
			// if no new pixels, can't grow
			if(!isGoodSum)
				res.cont = false;
			
			resCell[i] = res;
		}
	}
	
	/**
	 * Crop data.
	 * 
	 * @param dat the data matrix 
	 * @param rgWs the start X
	 * @param rgWe the end X
	 * @param rgHs the start Y
	 * @param rgHe the end Y
	 * @param rgTs the start Z
	 * @param rgTe the end Z
	 * @return
	 */
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
	
	/**
	 * Get the mean value of xy plane, save as array in time dimension
	 * 
	 * @param dat the input matrix
	 * @param Ws the start X
	 * @param We the end X
	 * @param Hs the start Y
	 * @param He the end Y
	 * @param Ts the start Z
	 * @param Te the end Z
	 * @returnarray in time dimension
	 */
	public float[] getMeanInTime(float[][][] dat, int Ws, int We, int Hs, int He, int Ts, int Te) {
		float[] result = new float[Te-Ts+1];
		int num = (He-Hs+1)*(We-Ws+1);
		for(int k=Ts;k<=Te;k++) {
			float sum = 0;
			for(int i=Ws;i<=We;i++) {
				for(int j=Hs;j<=He;j++) {
					sum += dat[i][j][k];
				}
			}
			result[k-Ts] = sum/num;
			
		}
		return result;
	}
	
	/**
	 * Get the max value of xy plane, save as array in time dimension
	 * 
	 * @param lmAll the input matrix
	 * @param Ws the start X
	 * @param We the end X
	 * @param Hs the start Y
	 * @param He the end Y
	 * @param Ts the start Z
	 * @param Te the end Z
	 * @return array in time dimension
	 */
	public boolean[] getMaxInTime(boolean[][][] lmAll, int Ws, int We, int Hs, int He, int Ts, int Te) {
		boolean[] result = new boolean[Te-Ts+1];
		for(int k=Ts;k<=Te;k++) {
			boolean sum = false;
			for(int i=Ws;i<=We;i++) {
				for(int j=Hs;j<=He;j++) {
					sum |= lmAll[i][j][k];
				}
			}
			result[k-Ts] = sum;
		}
		return result;
	}
	
	/**
	 * Find a compact time window for the peak of a seed
	 * 
	 * @param x the curve
	 * @param tPeak the peak position
	 * @param thr0 the threshold
	 * @param otherSeeds the boolean array which contains the position of other seeds
	 * @param df the dF curve
	 * @param extWin the default window length
	 * @return five time position
	 */
	public int[] getTimeWindow2a(float[] x, int tPeak, float thr0, boolean[] otherSeeds, float[] df, int extWin) {
		boolean st0 = false;
		int[] timeWindow = new int[5];
		int T = x.length;
		
		// search peak
		float dfMax = df[tPeak];
		float peak0 = x[tPeak];
		float base0 = x[tPeak];
		float base1 = x[tPeak];
		if(tPeak > 0) {
			dfMax = Math.max(dfMax,df[tPeak-1]);
			peak0 = Math.max(peak0, x[tPeak-1]);
		}
		if(tPeak < T-1) {
			dfMax = Math.max(dfMax,df[tPeak+1]);
			peak0 = Math.max(peak0, x[tPeak+1]);
		}
		if(dfMax < thr0)
			return null;
		
		// need big decrease, if the signal is already bright
		float thr = (float) Math.max(dfMax*0.3, thr0);
		
		// time window (search space)
		int t0 = 0;	// start Point
		int t1 = T-1;	// end Point
		for(int t = tPeak-1;t>=0;t--) {
			if(otherSeeds[t]) {
				t0 = t;
				break;
			}
		}
		if(t0==0)
			t0 = Math.max(0, tPeak-extWin);
		
		for(int t = tPeak+1;t<T;t++) {
			if(otherSeeds[t]) {
				t1 = t;
				break;
			}
		}
		if(t1==T-1)
			t1 = Math.min(T-1, tPeak+extWin);
		
		//	search for point lower than threshold
		st0 = false;
		int iMin = tPeak;
		for(int t=tPeak;t>=t0;t--) {
			if(x[t]<base0) {
				base0 = x[t];
				iMin = t;
			}
			if(x[t]>peak0) {
				t0 = iMin;
				break;
			}
			if(peak0 - x[t]>thr)
				st0 = true;
			if(x[t] - base0>thr&&st0) {
				t0 = iMin;
				break;
			}
		}
		st0 = false;
		for(int t=tPeak;t<=t1;t++) {
			if(x[t]<base1) {
				base1 = x[t];
				iMin = t;
			}
			if(x[t]>peak0) {
				t1 = iMin;
				break;
			}
			if(peak0 - x[t]>thr)
				st0 = true;
			if(x[t] - base1>thr&&st0) {
				t1 = iMin;
				break;
			}
		}
		
		// update peak
		peak0 = -Float.MAX_VALUE;
		for(int t = t0;t<=t1;t++) {
			if(x[t]>peak0) {
				peak0 = x[t];
				tPeak = t;
			}
		}
		
		// balance the base level
		float baseX = Math.max(base0, base1);
		
		// find 10% and 50%
		if(peak0 - baseX<thr)
			return null;
		
		float thr10 = (float) (0.1*(peak0 - baseX)+baseX);
		float thr50 = (float) (0.5*(peak0 - baseX)+baseX);
		
		int t0z = t0;
		int t0a = t0;
		boolean findT0a = false;
		for(int t = tPeak-1;t>=t0;t--) {
			boolean judge = peak0-x[t]>thr;
			if(!findT0a && x[t]<thr50 && judge) {
				t0a = t;
				findT0a = true;
			}
			if(x[t]<thr10 && judge) {
				t0z = t;
				break;
			}
		}
		if(t0a == t0)
			t0a = t0+1;
		
		int t1z = t1;
		int t1a = t1;
		boolean findT1a = false;
		for(int t = tPeak+1;t<=t1;t++) {
			boolean judge = peak0-x[t]>thr;
			if(!findT1a && x[t]<thr50 && judge) {
				t1a = t;
				findT1a = true;
			}
			if(x[t]<thr10 && judge) {
				t1z = t;
				break;
			}
		}
		if(t1a == t1)
			t1a = t1-1;
		
		if(t0z>t1z || t0a > t1a)
			return null;
		
		timeWindow[0] = t0z;
		timeWindow[1] = t1z;
		timeWindow[2] = t0a;
		timeWindow[3] = t1a;
		timeWindow[4] = tPeak;
		
		
		return timeWindow;
		
	}

	/**
	 * Find a compact time window for the peak of a seed
	 * 
	 * @param x the curve
	 * @param tPeak the peak position
	 * @param thr0 the threshold
	 * @param df the dF curve
	 * @param extWin the default window length
	 * @return five time position
	 */
	public int[] getTimeWindow2b(float[] x, int tPeak, float thr0, float[] df, int maxExt) {
		int[] timeWindow = new int[5];
		int T = x.length;
		
		// search peak
		float dfMax = df[tPeak];
		float base0 = x[tPeak];
		float base1 = x[tPeak];
		
		// need big decrease, if the signal is already bright
		thr0 = (float) Math.max(dfMax*0.3, thr0);
		
		// time window (search space)
		int t0 = Math.max(0, tPeak - maxExt);	
		int t1 = Math.min(T-1, tPeak + maxExt);
		
		//	search for point lower than threshold
		for(int t=tPeak;t>=t0;t--) {
			if(df[t]<thr0) {
				base0 = x[t];
				t0 = t;
				break;
			}
		}
		
		for(int t=tPeak;t<=t1;t++) {
			if(df[t]<thr0) {
				base1 = x[t];
				t1 = t;
				break;
			}
		}
		
		// update peak
		float peak0 = x[tPeak];
		for(int t = t0;t<=t1;t++) {
			if(x[t]>peak0) {
				peak0 = x[t];
				tPeak = t;
			}
		}
		
		// balance the base level
		float baseX = Math.max(base0, base1);
		
		// find 10% and 50%
		float thr10 = (float) (0.1*(peak0 - baseX)+baseX);
		float thr50 = (float) (0.5*(peak0 - baseX)+baseX);
		
		int t0z = t0;
		int t0a = t0;
		boolean findT0a = false;
		for(int t = tPeak-1;t>=t0;t--) {
			if(!findT0a && x[t]<thr50) {
				t0a = t+1;
				findT0a = true;
			}
			if(x[t]<thr10) {
				t0z = t;
				break;
			}
		}

		
		int t1z = t1;
		int t1a = t1;
		boolean findT1a = false;
		for(int t = tPeak+1;t<=t1;t++) {
			if(!findT1a && x[t]<thr50) {
				t1a = t-1;
				findT1a = true;
			}
			if(x[t]<thr10) {
				t1z = t;
				break;
			}
		}

//		if(t0z>t1z || t0a > t1a)
//			return null;
		
		timeWindow[0] = t0z;
		timeWindow[1] = t1z;
		timeWindow[2] = t0a;
		timeWindow[3] = t1a;
		timeWindow[4] = tPeak;
		
		
		return timeWindow;
		
	}

	/**
	 * Get the median value of x[i+1]-x[i]
	 * 
	 * @param dF the input array
	 * @return the median value
	 */
	public float getMedianDf(float[] dF) {
		int len = dF.length;
		float[] dif = new float[len-1];
		for(int i=0;i<len - 1;i++) {
			dif[i] = (float) ((dF[i] - dF[i+1])*(dF[i] - dF[i+1])/0.9113);
		}
		Arrays.sort(dif);
		if(len%2==0) {
			return (float) Math.sqrt((dif[len/2] + dif[len/2 - 1])/2);
		}else
			return (float) Math.sqrt(dif[len/2]);

	}

	/**
	 * Update the seed map
	 * 
	 * @param lmAll the input matrix
	 * @param Ws the start X
	 * @param We the end X
	 * @param Hs the start Y
	 * @param He the end Y
	 * @param Ts the start Z
	 * @param Te the end Z
	 */
	public void updateSeedMap(boolean[][][] lmAll, int Ws, int We, int Hs, int He, int Ts, int Te) {
		for(int t=Ts;t<=Te;t++) {
			for(int i=Ws;i<=We;i++) {
				for(int j=Hs;j<=He;j++) {
					lmAll[i][j][t] = false;
				}
			}
		}
		
	}

	/**
	 * Assign true to boolean matrix according to the hashmap
	 * 
	 * @param fiux the hashmap
	 * @param target the target matrix
	 */
	public void assignBoolean(HashMap<Integer,int[]> fiux, boolean[][] target) {
		for(int i=0;i<fiux.size();i++) {
			int px = fiux.get(i)[0];
			int py = fiux.get(i)[1];
			target[px][py] = true;
		}
	}
	
	/**
	 * Assign true to boolean matrix according to the arraylist
	 * 
	 * @param fiux the arraylist
	 * @param target the target matrix
	 */
	public void assignBoolean(ArrayList<int[]> fiux, boolean[][] target) {
		for(int i=0;i<fiux.size();i++) {
			int px = fiux.get(i)[0];
			int py = fiux.get(i)[1];
			target[px][py] = true;
		}
	}
	

	/**
	 * Assign false to boolean matrix according to the boolean input matrix
	 * 
	 * @param pixBad the source matrix
	 * @param fiux the target boolean matrix
	 */
	public void assignBooleanFalse(boolean[][] pixBad, boolean[][] fiux) {
		for(int i=0;i<pixBad.length;i++) {
			for(int j=0;j<pixBad[0].length;j++) {
				if(pixBad[i][j])
					fiux[i][j] = false;
			}
		}
	}
	
	/**
	 * Assign true to boolean matrix according to the boolean input matrix
	 * 
	 * @param valid the source matrix
	 * @param target the target boolean matrix
	 */
	public void assignBooleanTrue(boolean[][] valid, boolean[][] target) {
		for(int i=0;i<valid.length;i++) {
			for(int j=0;j<valid[0].length;j++) {
				if(valid[i][j])
					target[i][j] = true;
			}
		}
	}
	
	/**
	 * Shift the time by adding value
	 * 
	 * @param twMap the source map
	 * @param fiux save the position in xy plane
	 * @param value
	 * @param width
	 * @param height
	 * @return the 3D result matrix after shifting 
	 */
	public int[][][] matrixAddInt(HashMap<Integer,int[]> twMap, HashMap<Integer,int[]> fiux, int value, int width, int height) {
		int[][][] result = new int[width][height][5];
		
		for(int i=0;i<fiux.size();i++) {
			int px = fiux.get(i)[0];
			int py = fiux.get(i)[1];
			for(int t=0;t<5;t++) {
				result[px][py][t] = twMap.get(i)[t+2] + value;
			}
		}
		return result;
	}
	
	/**
	 * Assign the time position for each pixel
	 * 
	 * @param twMap the target matrix
	 * @param tw the source time window of super voxel
	 */
	public void assignTwMap(int[][][] twMap, int[] tw) {
		for(int k=0;k<5;k++) {
			for(int i=0;i<twMap.length;i++) {
				for(int j=0;j<twMap[0].length;j++) {
					twMap[i][j][k] = tw[k];
				}
			}
		}
	}
	
	/**
	 * Detect the super voxel
	 * 
	 * @param dataIn the input matrix
	 * @param res the feature of super voxel
	 * @param opts the class for reading parameters
	 * @return  the feature of super voxel
	 */
	public Res detectGrowSp(float[][][] dataIn, Res res, Opts opts) {
		int width = dataIn.length;
		int height = dataIn[0].length;
		float minPixZ = opts.thrExtZ;
		
		// crop data to reduce cost
		int[] tw = new int[5];
		for(int i=0;i<5;i++)
			tw[i]=res.tw[i];

		int tx0 = tw[0];
		int tx1 = tw[1];
		tw[0] = tw[0] - tx0;
		tw[1] = tw[1] - tx0;
		tw[2] = tw[2] - tx0;
		tw[3] = tw[3] - tx0;
		tw[4] = tw[4] - tx0;
		
		// data
		float[][][] dat = cropData(dataIn,0,dataIn.length-1,0,dataIn[0].length-1,tx0,tx1);
		// ...
		
		// seed loc
		int iwSeed = res.iSeed[0] - res.rgWs;
		int ihSeed = res.iSeed[1] - res.rgHs;
		
		// init
		boolean[][] fiux = new boolean[width][height];
		boolean[][] pixBad = new boolean[width][height];
		int[][][] twMap = new int[width][height][5];
		
		if(res.stg) {
			assignBoolean(res.fiux,fiux);
			assignBoolean(res.pixBad,pixBad);
			twMap = matrixAddInt(res.twMap,res.fiux,-tx0,width,height);
		}else {
			fiux[iwSeed][ihSeed] = true;
			assignBooleanFalse(pixBad,fiux);
		}
		
		twMap[iwSeed][ihSeed][0] = tw[0];
		twMap[iwSeed][ihSeed][1] = tw[1];
		twMap[iwSeed][ihSeed][2] = tw[2];
		twMap[iwSeed][ihSeed][3] = tw[3];
		twMap[iwSeed][ihSeed][4] = tw[4];
		
		//choose pixels for checking
		boolean[][] validMap = getValidMap(twMap,fiux,pixBad,res.stg==false);
		ArrayList<int[]> pix = new ArrayList<>();
		for(int i=0;i<validMap.length;i++) {
			for(int j=0;j<validMap[0].length;j++) {
				if(validMap[i][j])
					pix.add(new int[] {i,j});
			}
		}
		int nPix = pix.size();
		
		if(nPix>0) {
			float[] sz0 = new float[nPix]; // score for each new Pixel
			float s0 = (float) Math.sqrt(opts.varEst);
			
			for(int i=0;i<nPix;i++) {
				int px = pix.get(i)[0];
				int py = pix.get(i)[1];
				
				float x0 = dat[px][py][tw[0]];
				float x1 = dat[px][py][tw[1]];
				float xp = dat[px][py][tw[4]];
				sz0[i] = Math.min((xp-x0)/s0, (xp-x1)/s0);
				
				// is Bad
				if(sz0[i]<minPixZ) {
					pixBad[px][py] = true;
				}
			}	
			for(int x=0;x<width;x++) {
				for(int y=0;y<height;y++) {
					if(validMap[x][y]&&!pixBad[x][y])
						fiux[x][y] = true;
				}
			}
		}
		
		boolean[][] pixNew = new boolean[width][height];
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				pixNew[i][j] = fiux[i][j];
			}
		}
		if(res.fiux!=null) {
			for(int i=0;i<res.fiux.size();i++) {
				int px = res.fiux.get(i)[0];
				int py = res.fiux.get(i)[1];
				pixNew[px][py] = false;
			}
		}
		// output
		res.fiux = new HashMap<Integer,int[]>();
		res.pixNew = new ArrayList<>();
		res.pixBad = new ArrayList<int[]>();
		res.twMap = new HashMap<Integer,int[]>();
		int cnt = 0;
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(fiux[i][j]) {
					res.fiux.put(cnt,new int[] {i,j});
					int tw0 = twMap[i][j][0]+tx0;
					int tw1 = twMap[i][j][1]+tx0;
					int tw2 = twMap[i][j][2]+tx0;
					int tw3 = twMap[i][j][3]+tx0;
					int tw4 = twMap[i][j][4]+tx0;
					res.twMap.put(cnt,new int[] {i,j,tw0,tw1,tw2,tw3,tw4});
					if(pixNew[i][j])
						res.pixNew.add(cnt);
					cnt++;
				}
				
				if(pixBad[i][j])
					res.pixBad.add(new int[] {i,j});
			}
		}
		res.stg = true;

		return res;
		
	}
	
	
	/**
	 * calculate the valid map
	 * 
	 * @param twMap the time window
	 * @param fiux save the position in xy plane
	 * @param pixBad the bad pixel position
	 * @param stg a flag
	 * @return the valid map
	 */
	public boolean[][] getValidMap(int[][][] twMap, boolean[][] fiux, boolean[][] pixBad, boolean stg){
		int[] dh = new int[] {0,-1,0,1,-1,1,-1,0,1};
		int[] dw = new int[] {0,-1,-1,-1,0,0,1,1,1};
		
		int width = fiux.length;
		int height = fiux[0].length;
		
		int diStep = 1;
		boolean[][] validMap = new boolean[width][height];
		boolean[][] mapStart = new boolean[width][height];
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(fiux[i][j]&&!pixBad[i][j])
					mapStart[i][j] = true;
			}
		}
		
		for(int k=1;k<=diStep;k++) {
			boolean[][] validMapnew = imdilate(mapStart);
			if(!stg)
				assignBooleanFalse(mapStart,validMapnew);
			
			assignBooleanFalse(pixBad,validMapnew);
			int[] twx = new int[5];
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					if(validMapnew[i][j]) {
						for(int c=0;c<dh.length;c++) {
							int iw1 = i + dw[c];
							int ih1 = j + dh[c];
							if(iw1<0 || iw1>width-1 || ih1<0 || ih1>height-1)
								continue;
							
							if(mapStart[iw1][ih1]) {
								twx[0] = twMap[iw1][ih1][0];
								twx[1] = twMap[iw1][ih1][1];
								twx[2] = twMap[iw1][ih1][2];
								twx[3] = twMap[iw1][ih1][3];
								twx[4] = twMap[iw1][ih1][4];
								break;
							}
						}
						twMap[i][j][0] = twx[0];
						twMap[i][j][1] = twx[1];
						twMap[i][j][2] = twx[2];
						twMap[i][j][3] = twx[3];
						twMap[i][j][4] = twx[4];
					}
				}
			}
			assignBooleanTrue(validMapnew,validMap);
			assignBooleanTrue(validMapnew,mapStart);
		}
		
		return validMap;
		
	}
	
	/**
	 * Dilate the map.
	 * 
	 * @param mapStart the input matirx
	 * @return the map matrix after extending
	 */
	public boolean[][] imdilate(boolean[][] mapStart){
		int width = mapStart.length;
		int height = mapStart[0].length;
		boolean[][] result = new boolean[width][height];
		for(int i = 0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(mapStart[i][j])
					imdilateOne(result,i,j);
			}
		}
		
		return result;
	}
	
	/**
	 * Extends all the pixel by one.
	 * 
	 * @param result the target matrix
	 * @param i x position
	 * @param j y position
	 */
	private void imdilateOne(boolean[][] result, int i, int j) {		
		int width = result.length;
		int height = result[0].length;
		result[i][j] = true;
		if(j<height-1)
			result[i][j+1] = true;
		if(j>0)
			result[i][j-1] = true;
		if(i>0) {
			result[i-1][j] = true;
			if(j>0)
				result[i-1][j-1] = true;
			if(j<height-1)
				result[i-1][j+1] = true;
		}
		if(i<width-1) {
			result[i+1][j] = true;
			if(j>0)
				result[i+1][j-1] = true;
			if(j<height-1)
				result[i+1][j+1] = true;
		}
			
	}
}
