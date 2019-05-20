package va.vt.cbil;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;


public class Aqua_OutPutOpts extends SwingWorker<Void, Integer>{
	
	JFrame frame = new JFrame("SaveOpts");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	
	static long start = System.currentTimeMillis();;
	static long end;
	ImageDealer imageDealer = null;
	String savePath = null;
	
	public Aqua_OutPutOpts(ImageDealer imageDealer, String savePath) {
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
		System.out.println("Finish read");
		PrintWriter pw = null;
//		if(savePath.substring(-4, -1).equals(".csv"))
//			System.out.println(savePath.substring(-4, -1));
		System.out.println(savePath.substring(savePath.length()-4));
		String outputPath = null;
		if(savePath.substring(savePath.length()-4).equals(".csv"))
			outputPath = savePath;
		else
			outputPath = savePath + ".csv";
		
		try {
			
			pw = new PrintWriter(new File(outputPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        StringBuilder sb = new StringBuilder();
        sb.append("Minimum size");
    	sb.append(',');
        sb.append(opts.minSize+"");
        sb.append('\n');
        
        sb.append("Spatial smoothing level");
    	sb.append(',');
        sb.append(opts.smoXY+"");
        sb.append('\n');
        
        sb.append("Active voxels threshold scale");
    	sb.append(',');
        sb.append(opts.thrARScl+"");
        sb.append('\n');
        
        sb.append("Temporal cut threshold scale");
    	sb.append(',');
        sb.append(opts.thrTWScl+"");
        sb.append('\n');
        
        sb.append("Seed growing threshold");
    	sb.append(',');
        sb.append(opts.thrExtZ+"");
        sb.append('\n');
        
        sb.append("Slowest propagation");
    	sb.append(',');
        sb.append(opts.cDelay+"");
        sb.append('\n');
        
        sb.append("Rising phase uncertainty");
    	sb.append(',');
        sb.append(opts.cRise+"");
        sb.append('\n');
        
        sb.append("GTW smootheness term");
    	sb.append(',');
        sb.append(opts.gtwSmo+"");
        sb.append('\n');
        
        sb.append("GTW windows size");
    	sb.append(',');
        sb.append(opts.maxStp+"");
        sb.append('\n');
        
        sb.append("Z score threshold for events");
    	sb.append(',');
        sb.append(opts.zThr+"");
        sb.append('\n');
        
        sb.append("Ignore merging step");
    	sb.append(',');
        sb.append(opts.ignoreMerge+"");
        sb.append('\n');
        
        sb.append("Maximum merging distance");
    	sb.append(',');
        sb.append(opts.mergeEventDiscon+"");
        sb.append('\n');
        
        sb.append("Minimum merging correlation");
    	sb.append(',');
        sb.append(opts.mergeEventCorr+"");
        sb.append('\n');
        
        sb.append("Maximum merging time difference");
    	sb.append(',');
        sb.append(opts.mergeEventMaxTimeDif+"");
        sb.append('\n');
        
        sb.append("Remove pixels colse to image boundary");
    	sb.append(',');
        sb.append(opts.regMaskGap+"");
        sb.append('\n');
        
        sb.append("Poisson noise model");
    	sb.append(',');
        sb.append(opts.usePG?1:0+"");
        sb.append('\n');
        
        sb.append("Frames per segment");
    	sb.append(',');
        sb.append(opts.cut+"");
        sb.append('\n');
        
        sb.append("Baseline window");
    	sb.append(',');
        sb.append(opts.movAvgWin+"");
        sb.append('\n');
        
        sb.append("Extend super voxels temporally");
    	sb.append(',');
        sb.append(opts.extendSV+"");
        sb.append('\n');
        
        sb.append("Older code for active voxels");
    	sb.append(',');
        sb.append(opts.legacyModeActRun+"");
        sb.append('\n');
        
        sb.append("Time window detection range");
    	sb.append(',');
        sb.append(opts.getTimeWindowExt+"");
        sb.append('\n');
        
        sb.append("Pixels for window detection");
    	sb.append(',');
        sb.append(opts.seedNeib+"");
        sb.append('\n');
        
        sb.append("Remove seeds");
    	sb.append(',');
        sb.append(opts.seedRemoveNeib+"");
        sb.append('\n');
        
        sb.append("Super voxel significance");
    	sb.append(',');
        sb.append(opts.thrSvSig+"");
        sb.append('\n');
        
        sb.append("Super events prefer larger");
    	sb.append(',');
        sb.append(opts.superEventdensityFirst+"");
        sb.append('\n');
        
        sb.append("Area ratio to find seed curve");
    	sb.append(',');
        sb.append(opts.gtwGapSeedRatio+"");
        sb.append('\n');
        
        sb.append("Minimum area to find seed curve");
    	sb.append(',');
        sb.append(opts.gtwGapSeedMin+"");
        sb.append('\n');
        
        sb.append("Spatial overlap threshold");
    	sb.append(',');
        sb.append(opts.cOver+"");
        sb.append('\n');
        
        sb.append("Event show threshold on raw data");
    	sb.append(',');
        sb.append(opts.minShow1+"");
        sb.append('\n');
        
        sb.append("GUI event boundary threshold");
    	sb.append(',');
        sb.append(opts.minShowEvtGUI+"");
        sb.append('\n');
        
        sb.append("Ignore decay tau calculation");
    	sb.append(',');
        sb.append(opts.ignoreTau+"");
        sb.append('\n');
        
        sb.append("Correct baseline trend");
    	sb.append(',');
        sb.append(opts.correctTrend+"");
        sb.append('\n');
        
        sb.append("Extend event temporally after merging");
    	sb.append(',');
        sb.append(opts.extendEvtRe+"");
        sb.append('\n');
        
        sb.append("Frame rate");
    	sb.append(',');
        sb.append(opts.frameRate+"");
        sb.append('\n');
        
        sb.append("Spatial resolution");
    	sb.append(',');
        sb.append(opts.spatialRes+"");
        sb.append('\n');
        
        sb.append("Estimated noise variance");
    	sb.append(',');
        sb.append(opts.varEst+"");
        sb.append('\n');
        
        sb.append("Foreground threshold");
    	sb.append(',');
        sb.append(opts.fgFluo+"");
        sb.append('\n');
        
        sb.append("Background threshold");
    	sb.append(',');
        sb.append(opts.bgFluo+"");
        sb.append('\n');
        
        sb.append("X coordinate for north vector");
    	sb.append(',');
        sb.append(opts.northx+"");
        sb.append('\n');
        
        sb.append("Y coordinate for north vector");
    	sb.append(',');
        sb.append(opts.northy+"");
        sb.append('\n');
        
        sb.append("Width");
    	sb.append(',');
        sb.append(opts.W+"");
        sb.append('\n');
        
        sb.append("Height");
    	sb.append(',');
        sb.append(opts.H+"");
        sb.append('\n');
        
        sb.append("Frame Number");
    	sb.append(',');
        sb.append(opts.T+"");
        sb.append('\n');
        
        sb.append("Maximum value of data");
    	sb.append(',');
        sb.append(opts.maxValueDat+"");
        sb.append('\n');

        pw.write(sb.toString());
        pw.close();
        System.out.println("done!");
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
