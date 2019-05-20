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

/**
 * The fourth step of the whole software, to clean the events whose
 * z score lower than threshold 
 * 
 * @author Xuelong Mi
 * @version 1.0
 */

public class ProgressBarRealizedStep4 extends SwingWorker<int[][][], Integer> {
	JFrame frame = new JFrame("Step4");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	
	static long start = System.currentTimeMillis();;
	static long end;
	ImageDealer imageDealer = null;
	String proPath = null;
	/**
	 * Construct the class by imageDealer. 
	 * 
	 * @param imageDealer used to read the parameter
	 */
	public ProgressBarRealizedStep4(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
		proPath = imageDealer.proPath;
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
	 * Clean the events
	 * 
	 * @return return the labels of different events
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected int[][][] doInBackground() throws Exception {

		publish(1);
		// ------------------------ Read Data ----------------------------- //
		Opts opts = imageDealer.opts;
		HashMap<Integer,ArrayList<int[]>> evtLst = null;
		HashMap<Integer,RiseNode> riseLst = null;
		float[][] dffMat = null;
		FtsLst ftsLst = null;
//		int[][][] datR = null;
		
		
		try {
			FileInputStream fi = null;
			ObjectInputStream oi = null;
			
			// evtLst
			fi = new FileInputStream(new File(proPath + "ResultInStep3_EvtLstAll.ser"));
			oi = new ObjectInputStream(fi);
			evtLst = (HashMap<Integer,ArrayList<int[]>>) oi.readObject();
			oi.close();
			fi.close();
			
			// riseLst
			fi = new FileInputStream(new File(proPath + "ResultInStep3_RiseLstAll.ser"));
			oi = new ObjectInputStream(fi);
			riseLst = (HashMap<Integer,RiseNode>) oi.readObject();
			oi.close();
			fi.close();
			
			// dffMat
			fi = new FileInputStream(new File(proPath + "ResultInStep3_DffMatAll.ser"));
			oi = new ObjectInputStream(fi);
			dffMat = (float[][]) oi.readObject();
			oi.close();
			fi.close();
			
			// ftsLst
			fi = new FileInputStream(new File(proPath + "ResultInStep3_FtsLstAll.ser"));
			oi = new ObjectInputStream(fi);
			ftsLst = (FtsLst) oi.readObject();
			oi.close();
			fi.close();
			
//			// datR
//			fi = new FileInputStream(new File("ResultInStep3_DatRAll.txt"));
//			oi = new ObjectInputStream(fi);
//			datR = (int[][][]) oi.readObject();
//			oi.close();
//			fi.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// ---------------------- Clean -------------------------------- //
		publish(2);
		int validNum = 0;
		boolean[] mskx = new boolean[riseLst.size()];
		for(int i=1;i<=mskx.length;i++) {
			Float dffMaxZ = ftsLst.curve.dffMaxZ.get(i);
			if(dffMaxZ!=null && dffMaxZ>=opts.zThr) {
				mskx[i-1] = true;							// i-1 since i is label number, not index
				validNum++;
			}
		}
		System.out.println(validNum);
		HashMap<Integer, float[]> dffMatFilterZ = new HashMap<>();
		HashMap<Integer, ArrayList<int[]>> evtLstFilterZ = new HashMap<>();
		HashMap<Integer, Integer> tBeginFilterZ = new HashMap<>();
		HashMap<Integer, RiseNode> riseLstFilterZ = new HashMap<>();
		int cnt = 1;
		for(int i=0;i<riseLst.size();i++) {
			int label = i+1;
			if(mskx[i] && evtLst.get(label)!=null) {
				evtLstFilterZ.put(cnt, evtLst.get(label));
				dffMatFilterZ.put(cnt, dffMat[i]);
				tBeginFilterZ.put(cnt, ftsLst.curve.tBegin.get(label));
				riseLstFilterZ.put(cnt, riseLst.get(label));
				cnt++;
			}
		}
		
		
		// ---------------------- Save -------------------------------- //
		publish(3);
		try {
			FileOutputStream f = null;
			ObjectOutputStream o = null;
			// evtLstFilterZ
			f = new FileOutputStream(new File(proPath + "ResultInStep4_EvtLstFilterZ.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(evtLstFilterZ);
			o.close();
			f.close();
			
			// dffMatFilterZ
			f = new FileOutputStream(new File(proPath + "ResultInStep4_DffMatFilterZ.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(dffMatFilterZ);
			o.close();
			f.close();
			
			// tBeginFilterZ
			f = new FileOutputStream(new File(proPath + "ResultInStep4_TBeginFilterZ.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(tBeginFilterZ);
			o.close();
			f.close();
			
			// riseLstFilterZ
			f = new FileOutputStream(new File(proPath + "ResultInStep4_RiseLstFilterZ.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(riseLstFilterZ);
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		imageDealer.riseLst = riseLstFilterZ;
		
		int nEvt = evtLstFilterZ.size();
		
		int W = imageDealer.width;
		int H = imageDealer.height;
		int T = imageDealer.pages;
		int[][][] labels = new int[W][H][T];
		for(Entry<Integer, ArrayList<int[]>> entry:evtLstFilterZ.entrySet()) {
			int label = entry.getKey();
			ArrayList<int[]> points = entry.getValue();
			for(int[] p:points) {
				labels[p[0]][p[1]][p[2]] = label;
			}
		}
		
		
		try {
			FileOutputStream f = null;
			ObjectOutputStream o = null;
			f = new FileOutputStream(new File(proPath + "Step4_Labels.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(labels);
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
		imageDealer.center.EvtNumber.setText(evtLstFilterZ.size()+"");
		
		return labels;
	}
	
	/** 
	 * Report the progress.
	 */
	protected void process(List<Integer> chunks) {
		int value = chunks.get(chunks.size()-1);
		int total = 3;
		String str = "";
		switch(value) {
		case 1:
			str = "Read the Data " + value + "/" + total;
			break;
		case 2:
			str = "Cleanning " + value + "/" + total;
			break;
		case 3:
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
//		JOptionPane.showMessageDialog(null, "Step4 Finish! Events Cleaned");
		imageDealer.left.nextButton.setEnabled(true);
		imageDealer.left.backButton.setEnabled(true);
		imageDealer.left.jTP.setEnabledAt(4, true);
		
		if(imageDealer.left.jTPStatus<4) {
			imageDealer.left.jTPStatus = Math.max(imageDealer.left.jTPStatus, 4);;
			imageDealer.right.typeJCB.addItem("Step4: Events Cleaned");
		}
//		imageDealer.right.typeJCB.setSelectedIndex(4);
		
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
