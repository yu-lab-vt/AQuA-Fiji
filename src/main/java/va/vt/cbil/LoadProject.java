package va.vt.cbil;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import va.vt.cbil.ProgressBarRealizedStep3.RiseNode;

public class LoadProject extends SwingWorker<Void, Integer> {
	JFrame frame = new JFrame("Read");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Read the Project");
	
	static long start = System.currentTimeMillis();;
	static long end;
	ImageDealer imageDealer = null;
	String proPath = null;
	public LoadProject(ImageDealer imageDealer) {
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
	protected Void doInBackground() throws Exception {
		imageDealer.drawRegion = true;
		Opts opts = null;
		try {
			FileInputStream fi = null;
			ObjectInputStream oi = null;
			
			fi = new FileInputStream(new File(proPath + "LabelColors.ser"));	
			oi = new ObjectInputStream(fi);
			imageDealer.labelColors = (Color[])oi.readObject();
			oi.close();
			fi.close();
			
			fi = new FileInputStream(new File(proPath + "CurStatus.ser"));	
			oi = new ObjectInputStream(fi);
			imageDealer.left.curStatus = (int)oi.readObject();
			oi.close();
			fi.close();
			
			fi = new FileInputStream(new File(proPath + "JTPStatus.ser"));	
			oi = new ObjectInputStream(fi);
			imageDealer.left.jTPStatus = (int)oi.readObject();
			oi.close();
			fi.close();
					
			fi = new FileInputStream(new File(proPath + "Region.ser"));	
			oi = new ObjectInputStream(fi);
			imageDealer.regionMark = (boolean[][])oi.readObject();
			oi.close();
			fi.close();
			
			fi = new FileInputStream(new File(proPath + "RegionLabels.ser"));	
			oi = new ObjectInputStream(fi);
			imageDealer.regionMarkLabel = (int[][])oi.readObject();
			oi.close();
			fi.close();
			
			fi = new FileInputStream(new File(proPath + "LandMark.ser"));	
			oi = new ObjectInputStream(fi);
			imageDealer.landMark = (boolean[][])oi.readObject();
			oi.close();
			fi.close();
			
			fi = new FileInputStream(new File(proPath + "JTPStatus.ser"));	
			oi = new ObjectInputStream(fi);
			imageDealer.left.jTPStatus = (int)oi.readObject();
			oi.close();
			fi.close();
			
			fi = new FileInputStream(new File(proPath + "Opts.ser"));	
			oi = new ObjectInputStream(fi);
			opts = (Opts)oi.readObject();
			oi.close();
			fi.close();
			
			fi = new FileInputStream(new File(proPath + "nEvt.ser"));	
			oi = new ObjectInputStream(fi);
			int nEvt = (int)oi.readObject();
			oi.close();
			fi.close();
			imageDealer.center.EvtNumber.setText(nEvt + "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		imageDealer.left.drawlistener.setRegion(imageDealer.regionMark);
		imageDealer.left.drawlistener2.setRegion(imageDealer.landMark);
		imageDealer.left.removeListener.setRegion(imageDealer.regionMark);
		imageDealer.left.removeListener.setRegionLabel(imageDealer.regionMarkLabel);
		imageDealer.left.removeListener2.setRegion(imageDealer.landMark);
		
		imageDealer.opts = opts;

		imageDealer.left.jTF11.setText(imageDealer.opts.thrARScl+"");
		imageDealer.left.jTF12.setText(imageDealer.opts.smoXY + "");
		imageDealer.left.jTF13.setText(imageDealer.opts.minSize + "");;
		
		imageDealer.left.jTF21.setText(imageDealer.opts.thrTWScl + "");
		imageDealer.left.jTF22.setText(imageDealer.opts.thrExtZ + "");
		
		imageDealer.left.jTF31.setText(imageDealer.opts.cRise + "");
		imageDealer.left.jTF32.setText(imageDealer.opts.cDelay + "");
		imageDealer.left.jTF33.setText(imageDealer.opts.gtwSmo + "");
		
		imageDealer.left.jTF41.setText(imageDealer.opts.zThr + "");
		
		imageDealer.left.jTF51.setSelected(imageDealer.opts.ignoreMerge==1);
		imageDealer.left.jTF52.setText(imageDealer.opts.mergeEventDiscon + "");
		imageDealer.left.jTF53.setText(imageDealer.opts.mergeEventCorr + "");
		imageDealer.left.jTF54.setText(imageDealer.opts.mergeEventMaxTimeDif + "");
		
		imageDealer.left.jTF61.setSelected(imageDealer.opts.extendEvtRe==1);
		
		imageDealer.left.jTF71.setSelected(imageDealer.opts.ignoreTau==1);
		
		if(imageDealer.left.jTPStatus>=1) {
			imageDealer.right.typeJCB.addItem("Step1: Active Voxels");
			try {
				FileInputStream fi = null;
				ObjectInputStream oi = null;
				
				fi = new FileInputStream(new File(proPath + "Data.ser"));	
				oi = new ObjectInputStream(fi);
				imageDealer.dat = (float[][][])oi.readObject();

				oi.close();
				fi.close();
				
				fi = new FileInputStream(new File(proPath + "Df.ser"));	
				oi = new ObjectInputStream(fi);
				imageDealer.dF = (float[][][])oi.readObject();

				oi.close();
				fi.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			imageDealer.center.gaussfilter.setEnabled(true);
			
		}
		if(imageDealer.left.jTPStatus>=2) {
			imageDealer.right.typeJCB.addItem("Step2: Super Voxels");
		}
		if(imageDealer.left.jTPStatus>=3) {
			imageDealer.right.typeJCB.addItem("Step3: Events");
		}
		if(imageDealer.left.jTPStatus>=4) {
			imageDealer.right.typeJCB.addItem("Step4: Events Cleaned");
		}
		if(imageDealer.left.jTPStatus>=5) {
			imageDealer.right.typeJCB.addItem("Step5: Events Merged");
		}
		if(imageDealer.left.jTPStatus>=6) {
			imageDealer.right.typeJCB.addItem("Step6: Events Reconstructed");
			try {
				FileInputStream fi = null;
				ObjectInputStream oi = null;
				
				fi = new FileInputStream(new File(proPath + "ResultInStep4_RiseLstFilterZ.ser"));	
				oi = new ObjectInputStream(fi);
				imageDealer.riseLst = (HashMap<Integer, RiseNode>)oi.readObject();

				oi.close();
				fi.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
//		imageDealer.right.typeJCB.setSelectedIndex(Math.min(6, imageDealer.left.jTPStatus));
		
		String labelPath = null;
		if(imageDealer.left.jTPStatus==1) {
			labelPath = proPath+"Step1_Labels.ser";
		}else if(imageDealer.left.jTPStatus==2) {
			labelPath = proPath+"Step2_Labels.ser";
		}else if(imageDealer.left.jTPStatus==3) {
			labelPath = proPath+"Step3_Labels.ser";
		}else if(imageDealer.left.jTPStatus==4) {
			labelPath = proPath+"Step4_Labels.ser";
		}else if(imageDealer.left.jTPStatus==5) {
			labelPath = proPath+"Step5_Labels.ser";
		}else if(imageDealer.left.jTPStatus>=6) {
			labelPath = proPath+"Step6_Labels.ser";
		}
		try {
			FileInputStream fi = null;
			ObjectInputStream oi = null;
			
			fi = new FileInputStream(new File(labelPath));	
			oi = new ObjectInputStream(fi);
			imageDealer.label = (int[][][])oi.readObject();
			oi.close();
			fi.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
		
		
		if(imageDealer.left.jTPStatus>=3) {
			int[][][] datR = null;
			String datRPath = proPath+"ResultInStep3_DatRAll.ser";
			if(imageDealer.left.jTPStatus>=6) {
				datRPath = proPath+"ResultInStep6_DatR.ser";
			}
			
			try {
				FileInputStream fi = null;
				ObjectInputStream oi = null;
				
				fi = new FileInputStream(new File(datRPath));	
				oi = new ObjectInputStream(fi);
				datR = (int[][][])oi.readObject();
				oi.close();
				fi.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}	
			imageDealer.datR = datR;
		}		
		
		
		for(int i=0;i<=imageDealer.left.jTPStatus;i++) {
			if(i!=7)
				imageDealer.left.jTP.setEnabledAt(i, true);
		}
		imageDealer.left.dealBuilder();
		
		imageDealer.left.jTP.setSelectedIndex(Math.min(imageDealer.left.jTPStatus,6));
		
		if(imageDealer.left.jTPStatus==7) {
			imageDealer.left.nextButton.setEnabled(false);
			imageDealer.left.backButton.setEnabled(true);
			imageDealer.left.left3.setVisible(true);
			imageDealer.left.left4.setVisible(true);
			imageDealer.right.allFinished();
			imageDealer.left.updateFeatures.setEnabled(true);
			float[] ftsTable = null;
			try {
				FileInputStream fi = null;
				ObjectInputStream oi = null;
				
				fi = new FileInputStream(new File(proPath + "FtsTableParameters.ser"));	
				oi = new ObjectInputStream(fi);
				ftsTable = (float[])oi.readObject();
				oi.close();
				fi.close();
				
				fi = new FileInputStream(proPath + "ResultInStep7_DffMat.ser");	
				oi = new ObjectInputStream(fi);
				imageDealer.dffMat = (float[][][])oi.readObject();
				oi.close();
				fi.close();
				
				fi = new FileInputStream(proPath + "Fts.ser");	
				oi = new ObjectInputStream(fi);
				imageDealer.fts = (FtsLst)oi.readObject();
				oi.close();
				fi.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			imageDealer.left.tableValueSetting(ftsTable[0],ftsTable[1],ftsTable[2],ftsTable[3],ftsTable[4],ftsTable[5],ftsTable[6],ftsTable[7],ftsTable[8],ftsTable[9]);
		}
		imageDealer.center.ts = opts.frameRate;
		imageDealer.right.typeJCB.setEnabled(true);
//		imageDealer.dealImage();
		new Thread(new Runnable() {

			@Override
			public void run() {
				imageDealer.imageLabel.repaint();
				imageDealer.dealImage();
			}
			
		}).start();
		
		return null;
	}
	
	protected void process(List<Integer> chunks) {
	}
	
	@Override
	protected void done() {
		frame.setVisible(false);
		JOptionPane.showMessageDialog(null, "Finish Reading");
	}
}
