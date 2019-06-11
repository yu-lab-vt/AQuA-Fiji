package va.vt.cbil;

import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class LabelRead extends SwingWorker<int[][][], Integer> {
	JFrame frame = new JFrame("Reading the labels");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	
	static long start = System.currentTimeMillis();;
	static long end;
	ImageDealer imageDealer = null;
	int index = 0;
	String proPath = null;
	
	public LabelRead(ImageDealer imageDealer, int index) {
		this.imageDealer = imageDealer;
		this.index = index;
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
	
	@Override
	protected int[][][] doInBackground() throws Exception {

		publish(1);
		System.out.println(index);
		String file = null;
		switch(index) {
			case 1:
				file = proPath + "Step1_Labels.ser"; break;
			case 2:
				file = proPath + "Step2_Labels.ser"; break;
			case 3:
				file = proPath + "Step3_seMap_Labels.ser"; break;
			case 4:
				file = proPath + "Step3_Labels.ser"; break;
			case 5:
				file = proPath + "Step4_Labels.ser"; break;
			case 6:
				file = proPath + "Step5_Labels.ser"; break;
			case 7:
				file = proPath + "Step6_Labels.ser"; break;
		}
		
		int[][][] labels = null;
		try {
			FileInputStream fi = null;
			ObjectInputStream oi = null;		
			// evtLst
			fi = new FileInputStream(new File(file));
			oi = new ObjectInputStream(fi);
			labels = (int[][][]) oi.readObject();
			oi.close();
			fi.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
		return labels;
	}
	
	protected void process(List<Integer> chunks) {

		String str = "Reading the data";
		jLabel.setText(str);
	}
	
	@Override
	protected void done() {
		frame.setVisible(false);
		JOptionPane.showMessageDialog(null, "Finish!");
		try {
			imageDealer.label = this.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		imageDealer.dealImage();
	}
}
