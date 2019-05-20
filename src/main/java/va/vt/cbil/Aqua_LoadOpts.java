package va.vt.cbil;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;


public class Aqua_LoadOpts extends SwingWorker<Void, Integer>{
	
	JFrame frame = new JFrame("SaveOpts");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	
	static long start = System.currentTimeMillis();;
	static long end;
	ImageDealer imageDealer = null;
	String savePath = null;
	
	public Aqua_LoadOpts(ImageDealer imageDealer, String savePath) {
		this.imageDealer = imageDealer;
		this.savePath = savePath;
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
	
	public void getFeaTureTable() {
		Opts opts = imageDealer.opts;
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(savePath));
			String[] values;
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.minSize = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.smoXY = Float.parseFloat(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.thrARScl = Float.parseFloat(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.thrTWScl = Float.parseFloat(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.thrExtZ = Float.parseFloat(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.cDelay = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.cRise = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.gtwSmo = Float.parseFloat(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.maxStp = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.zThr = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.ignoreMerge = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.mergeEventDiscon = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.mergeEventCorr = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.mergeEventMaxTimeDif = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.regMaskGap = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.usePG = Integer.parseInt(values[1])>0;
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.cut = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.movAvgWin = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.extendSV = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.legacyModeActRun = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.getTimeWindowExt = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.seedNeib = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.seedRemoveNeib = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.thrSvSig = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.superEventdensityFirst = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.gtwGapSeedRatio = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.gtwGapSeedMin = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.cOver = Float.parseFloat(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.minShow1 = Float.parseFloat(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.minShowEvtGUI = Float.parseFloat(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.ignoreTau = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.correctTrend = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.extendEvtRe = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.frameRate = Float.parseFloat(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.spatialRes = Float.parseFloat(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.varEst = Float.parseFloat(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.fgFluo = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.bgFluo = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.northx = Float.parseFloat(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.northy = Float.parseFloat(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.W = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.H = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.T = Integer.parseInt(values[1]);
			
			values = reader.readLine().split(",");
//			System.out.println(values[0] + " " + values[1]);
			opts.maxValueDat = Integer.parseInt(values[1]);
			
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
			
			
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	

	@Override
	protected Void doInBackground(){
		
		getFeaTureTable();

		return null;
	} 
	
	

	protected void process(List<Integer> chunks) {
		int value = chunks.get(chunks.size()-1);
		String str = "";
		switch(value) {
		case 1:
			str = "Extract Events and Features as Excel";
			break;
		case 2:
			str = "Extract Movies";
			break;
		}
		jLabel.setText(str);
	}
	
    	
    	
    	
	
	@Override
	protected void done() {
		frame.setVisible(false);
		JOptionPane.showMessageDialog(null, "Finish");
	}
}
