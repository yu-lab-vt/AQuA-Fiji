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

import va.vt.cbil.ProgressBarRealizedStep3.RiseNode;

public class ProgressBarRealizedStep5 extends SwingWorker<int[][][], Integer> {
	JFrame frame = new JFrame("Step5");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	
	static long start = System.currentTimeMillis();;
	static long end;
	ImageDealer imageDealer = null;
	String proPath = null;
	public ProgressBarRealizedStep5(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
		proPath = imageDealer.proPath;
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
		// ------------------------ Read Data ----------------------------- //
		Opts opts = imageDealer.opts;
		HashMap<Integer,ArrayList<int[]>> evtLstFilterZ = null;
		HashMap<Integer, float[]> dffMatFilterZ = null;
		HashMap<Integer, Integer> tBeginFilterZ = null;
		
		try {
			FileInputStream fi = null;
			ObjectInputStream oi = null;
			
			// ResultInStep4_EvtLstFilterZ
			fi = new FileInputStream(new File(proPath + "ResultInStep4_EvtLstFilterZ.ser"));
			oi = new ObjectInputStream(fi);
			evtLstFilterZ = (HashMap<Integer,ArrayList<int[]>>) oi.readObject();
			oi.close();
			fi.close();
			
			// ResultInStep4_DffMatFilterZ
			fi = new FileInputStream(new File(proPath + "ResultInStep4_DffMatFilterZ.ser"));
			oi = new ObjectInputStream(fi);
			dffMatFilterZ = (HashMap<Integer, float[]>) oi.readObject();
			oi.close();
			fi.close();
			
			// ResultInStep4_TBeginFilterZ
			fi = new FileInputStream(new File(proPath + "ResultInStep4_TBeginFilterZ.ser"));
			oi = new ObjectInputStream(fi);
			tBeginFilterZ = (HashMap<Integer, Integer>) oi.readObject();
			oi.close();
			fi.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		publish(2);
		HashMap<Integer,ArrayList<int[]>> evtLstMerge = null;
		if(opts.ignoreMerge==0) {
			evtLstMerge = mergeEvt(evtLstFilterZ, dffMatFilterZ, tBeginFilterZ, opts);
		}else {
			evtLstMerge = evtLstFilterZ;
		}
		
		int W = opts.W;
		int H = opts.H;
		int T = opts.T;
		int[][][] labels = new int[W][H][T];
		 
		for(int i=1;i<=evtLstMerge.size();i++) {
			for(int[] p:evtLstMerge.get(i)) {
				labels[p[0]][p[1]][p[2]] = i;
			}
		}
		
		System.out.println(evtLstMerge.size());
		
		
		publish(3);
		try {
			FileOutputStream f = null;
			ObjectOutputStream o = null;
			f = new FileOutputStream(new File(proPath + "Step5_Labels.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(labels);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath + "evtLstMerge.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(evtLstMerge);
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return labels;
	}
	
	private HashMap<Integer, ArrayList<int[]>> mergeEvt(HashMap<Integer, ArrayList<int[]>> evtLst,
			HashMap<Integer, float[]> dffMat, HashMap<Integer, Integer> tBegin, Opts opts) {
		System.out.println("Merging");
		int W = opts.W;
		int H = opts.H;
		int T = opts.T;
		
		int minDist = opts.mergeEventDiscon;
		int minCorr = opts.mergeEventCorr;
		int maxTimeDif = opts.mergeEventMaxTimeDif;
		
		int[][][] mIn = new int[W][H][T];
		 
		for(int i=1;i<=evtLst.size();i++) {
			for(int[] p:evtLst.get(i)) {
				mIn[p[0]][p[1]][p[2]] = i;
			}
		}
		
		// dilate events
		dilate(mIn,minDist);
		
		// neighbor graphs
		HashMap<Integer, ArrayList<int[]>> evtLstM = evtNeibCorr(mIn,dffMat,tBegin,minCorr,maxTimeDif,evtLst.size());
		
		
		return evtLstM;
	}

	private HashMap<Integer, ArrayList<int[]>> evtNeibCorr(int[][][] mIn, HashMap<Integer, float[]> dffMat, HashMap<Integer, Integer> tBegin,
			int minCorr, int maxTimeDif,int N) {
		// evtNeib detect neighboring events based on curve corrrelation
		// mainly used for merging
		
		HashMap<Integer,ArrayList<int[]>> evtLst = label2idx(mIn);
		int W = mIn.length;
		int H = mIn[0].length;
		int T = mIn[0][0].length;
		
		int[] dh = new int[] {-1,0,1,-1,1,-1,0,1};
		int[] dw = new int[] {-1,-1,-1,0,0,1,1,1};
		
		// neighbors
		HashMap<Integer,HashSet<Integer>> neibLst = new HashMap<>();
		for(int n=1;n<=N;n++) {
			if(n%1000==0)
				System.out.println(n);
			ArrayList<int[]> vox = evtLst.get(n);
			HashSet<Integer> neib0 = new HashSet<>();
			for(int r=0;r<dh.length;r++) {
				HashSet<Integer> xNeib = new HashSet<>();
				for(int[] p:vox) {
					int x = Math.min(Math.max(0, p[0]+dw[r]),W-1);
					int y = Math.min(Math.max(0, p[1]+dh[r]),H-1);
					int t = p[2];
					if(mIn[x][y][t]>0&&mIn[x][y][t]<n)
						xNeib.add(mIn[x][y][t]);
				}
				xNeib.removeAll(neib0);
				ArrayList<Integer> xNeibLst = new ArrayList<>(xNeib);
				if(xNeib.size()>0) {
					// correlation based or delay based
					float[] c0 = dffMat.get(n);
					float[][] c1 = new float[xNeib.size()][];
					int tb0 = tBegin.get(n);
					int[] tb1 = new int[xNeib.size()];
					for(int i=0;i<xNeibLst.size();i++) {
						c1[i] = dffMat.get(xNeibLst.get(i));
						tb1[i] = tBegin.get(xNeibLst.get(i));
					}
					boolean[] ixSig = new boolean[c0.length];
					for(int t=0;t<c0.length;t++) {
						if(!Float.isNaN(c0[t])) {
							ixSig[t] = true;
							continue;
						}
						for(int i=0;i<xNeibLst.size();i++) {
							if(!Float.isNaN(c1[i][t])) {
								ixSig[t] = true;
								break;
							}
						}
					}
					int t0 = 0;
					int t1 = c0.length-1;
					for(int t=0;t<c0.length;t++) {
						if(ixSig[t]) {
							t0 = t;
							break;
						}
					}
					for(int t=c0.length-1;t>=0;t--) {
						if(ixSig[t]) {
							t1 = t;
							break;
						}
					}
					
					// impute
					int T1 = t1-t0+1;
					for(int i=0;i<=xNeibLst.size();i++) {
						float[] cx = null;
						if(i==0) {
							cx = c0;
						}else {
							cx = c1[i-1];
						}
						int t0x = t0;
						int t1x = t1;
						for(int t=t0;t<=t1;t++) {
							if(!Float.isNaN(cx[t])) {
								t0x = t;
								break;
							}
						}
						for(int t=t1;t>=t0;t--) {
							if(!Float.isNaN(cx[t])) {
								t1x = t;
								break;
							}
						}
						if(t0x>t0) {
							for(int t=t0;t<t0x;t++) {
								cx[t] = cx[t0x];
							}
						}
						if(t1x<t1) {
							for(int t=t1;t>t1x;t--) {
								cx[t] = cx[t1x];
							}
						}
						if(i==0) {
							c0 = cx;
						}else {
							c1[i-1] = cx;
						}
					}
					
					// correlation
					boolean[] x1Good = new boolean[xNeib.size()];
					for(int i=0;i<xNeibLst.size();i++) {
						float[] c1x = c1[i];
						float cor = correlation(c0,c1x,t0,t1);
						int tDif00 = Math.abs(tb0-tb1[i]);
						if(cor>minCorr || tDif00<maxTimeDif) {
							x1Good[i] = true;
						}
					}
					
					for(int i=0;i<xNeibLst.size();i++) {
						if(x1Good[i])
							neib0.add(xNeibLst.get(i));
					}
					
				}
			}
			neib0.add(n);
			neibLst.put(n, neib0);
		}
		
		//  graph
		ArrayList<Integer> labelList = new ArrayList<>();
		for(int i=0;i<=N;i++) {
			labelList.add(0);
		}
		for(int n=1;n<=N;n++) {
			HashSet<Integer> neib0 = neibLst.get(n);
			for(int label:neib0) {
				ConnectedComponents.union_connect(n,label,labelList);
			}
		}
		for(int n=1;n<=N;n++) {
			int root = ConnectedComponents.union_find(n, labelList);
			if(root!=0 && root!=n) {
				evtLst.get(root).addAll(evtLst.get(n));
				evtLst.remove(n);
			}
		}
		
		int cnt = 1;
		HashMap<Integer,ArrayList<int[]>> evtM = new HashMap<>();
		for(int n=1;n<=N;n++) {
			if(evtLst.get(n)!=null) {
				evtM.put(cnt, evtLst.get(n));
				cnt++;
			}
		}
		
		
		return evtM;
	}
	
	public float correlation(float[] c0, float[] c1, int t0, int t1) {
		int T = t1-t0+1;
		float m1 = 0;
		float m2 = 0;
		float s1 = 0;
		float s2 = 0;
		float cov = 0;
		for(int t=t0;t<=t1;t++) {
			m1 += c0[t]/T;
			m2 += c1[t]/T;
			s1 += c0[t]*c0[t]/T;
			s2 += c1[t]*c1[t]/T;
			cov += c0[t]*c1[t]/T;
		}
		s1 -= m1*m1;
		s2 -= m2*m2;
		cov -= m1*m2;
		float result = (float) (cov/Math.sqrt(s1*s2));
		
		return result;
	}
	
	public HashMap<Integer,ArrayList<int[]>> label2idx(int[][][] labelMap){
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

	public int[][][] dilate(int[][][] mIn, int minDist) {
		if(minDist==0)
			return mIn;
		int W = mIn.length;
		int H = mIn[0].length;
		int T = mIn[0][0].length;
		
		int[][][] result = new int[W][H][T];
		for(int t=0;t<T;t++) {
			HashMap<Integer,ArrayList<int[]>> map = new HashMap<>();
			int maxL = 0;
			// dilate from small to large
			for(int x=0;x<W;x++) {
				for(int y=0;y<H;y++) {
					if(mIn[x][y][t]>0) {
						int label = mIn[x][y][t];
						maxL = Math.max(maxL,label);
						ArrayList<int[]> points = map.get(label);
						if(points == null)
							points = new ArrayList<>();
						
						points.add(new int[] {x,y});
						map.put(label, points);
					}
				}
			}
			
			for(int i=1;i<=maxL;i++) {
				ArrayList<int[]> points = map.get(i);
				if(points==null)
					continue;
				for(int[] p:points) {
					dilateOnePoint(p[0],p[1],t,minDist,i,result,W,H);
				}
			}
			
		}
		
		return result;
	}
	
	

	public void dilateOnePoint(int x0, int y0, int t, int minDist, int label, int[][][] result, int W, int H) {
		int xs = Math.max(x0-minDist, 0);
		int xe = Math.min(x0+minDist, W-1);
		int ys = Math.max(y0-minDist, 0);
		int ye = Math.min(y0+minDist, H-1);
		
		for(int x=xs;x<=xe;x++) {
			for(int y=ys;y<=ye;y++) {
				result[x][y][t] = label;
			}
		}
		
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
			str = "Merging " + value + "/" + total;
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
		JOptionPane.showMessageDialog(null, "Step5 Finish!");
		imageDealer.left.nextButton.setEnabled(true);
		imageDealer.left.backButton.setEnabled(true);
		imageDealer.left.jTP.setEnabledAt(5, true);
		imageDealer.left.jTPStatus = Math.max(imageDealer.left.jTPStatus, 5);;
		imageDealer.right.typeJCB.addItem("Step5: Events Merged");
//		imageDealer.right.typeJCB.setSelectedIndex(5);
		
		try {
			imageDealer.label = this.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		imageDealer.dealImage();
		imageDealer.saveStatus();
	}
}
