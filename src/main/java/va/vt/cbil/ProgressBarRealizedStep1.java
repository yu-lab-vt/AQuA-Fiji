package va.vt.cbil;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * The first step of the whole software, extract the preliminary 
 * event seeds and the active regions. 
 * It will return label matrix which contains the label of different 
 * active regions, which will be shown in the interface. 
 * The seeds will be used in step 2.
 * During the progress, the progress bar will appear for monitor.
 * 
 * @author Xuelong Mi
 * @version 1.0
 */
/**
 * @author Xuelong Mi
 *
 */
public class ProgressBarRealizedStep1 extends SwingWorker<int[][][], Integer>{
	JFrame frame = new JFrame("Step1");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	ImageDealer imageDealer = null;
	ImagePlus imgPlus = null;
	int width = 0;
	int height = 0;
	int pages = 0;
	Opts opts = null;
	float[][][] dat = null;
	boolean[][] evtSpatialMask = null;
	String proPath = null;
	float[][] stdMap1 = null;
	float[][] stdMap2 = null;
	
	/**
	 * Construct the class by imageDealer. 
	 * 
	 * @param imageDealer used to read the parameter
	 */
	public ProgressBarRealizedStep1(ImageDealer imageDealer){
		this.imageDealer = imageDealer;
		imgPlus = imageDealer.imgPlus.duplicate();
		width = imageDealer.width;
		height = imageDealer.height;
		pages = imageDealer.pages;
		opts = imageDealer.opts;
		evtSpatialMask = new boolean[width][height];
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				evtSpatialMask[x][y] = imageDealer.regionMark[x][y];
			}
		}
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
	 * Read the image, form the original data matrix
	 * 
	 * @param imagePlus For reading the image. 
	 * @return Original data matrix
	 */
	public float[][][] inputOrgImage(ImagePlus imagePlus) {
	    float[][][] datOrg = new float[width][height][pages];
	    
	    for(int k=0;k<pages;k++) {
	    	imagePlus.setPosition(k+1);
	    	float[][] tmp = imagePlus.getProcessor().getFloatArray();
		    for(int i=0;i<width;i++){
	            for(int j=0;j<height;j++){
	            	datOrg[i][j][k] = tmp[i][j];
	            }
	        }
		}
	    return datOrg;
	}
	
	/**
	 * Read the image after smoothing. 
	 * Form two matrix. 
	 * dat: the smoothing data matrix
	 * dif: the difference of the previous frame and the current frame.
	 * 
	 * @param imagePlus The image/video after 2D gaussian filter
	 * @return	the 3D matrix of data after smoothing
	 */
	public float[][][] inputImageAndGauss(ImagePlus imagePlus, float[][][] datOrg) {
	    dat = new float[width][height][pages];
	    
	 // GaussBlur: smooth the data
 		ImageProcessor imgProcessor = imgPlus.getProcessor();
	    
	    for(int k=0;k<pages;k++) {
	    	imgPlus.setPosition(k+1);
 			float[][] frameDat = imgProcessor.getFloatArray();
 			frameDat = GaussFilter.gaussFilter(frameDat, opts.smoXY, opts.smoXY);
 			imgProcessor.setFloatArray(frameDat);
 			
		    for(int i=0;i<width;i++){
	            for(int j=0;j<height;j++){
	                dat[i][j][k] = frameDat[i][j];
	            }
	        }
		}
	    
	    stdMap1 = new float[width][height];
	    stdMap2 = new float[width][height];
	    for(int i=0;i<width;i++) {
	    	for(int j=0;j<height;j++) {
//	    		ArrayList<Float> df1 = new ArrayList<>();
//	    		ArrayList<Float> df2 = new ArrayList<>();
	    		float[] df1 = new float[pages-1];
	    		float[] df2 = new float[pages-1];
	    		for(int k=1;k<pages;k++) {
//	    			df1.add((datOrg[i][j][k]-datOrg[i][j][k-1])*(datOrg[i][j][k]-datOrg[i][j][k-1]));
//	    			df2.add((dat[i][j][k]-dat[i][j][k-1])*(dat[i][j][k]-dat[i][j][k-1]));
	    			df1[k-1] = (datOrg[i][j][k]-datOrg[i][j][k-1])*(datOrg[i][j][k]-datOrg[i][j][k-1]);
	    			df2[k-1] = (dat[i][j][k]-dat[i][j][k-1])*(dat[i][j][k]-dat[i][j][k-1]);
	    			
	    		}
	    		stdMap1[i][j] = (float) Math.sqrt(getMedian(df1)/0.9133);
    			stdMap2[i][j] = (float) Math.sqrt(getMedian(df2)/0.9133);
	    		
	    	}
	    }
	    return dat;
	}
	
	/**
	 * Calculate the standard variance of the data in valid region
	 * 
	 * @param noiseEstMask The valid region
	 * @return the standard variance
	 */
//	public double calNoiseStd(boolean[][] noiseEstMask) {
//		ArrayList<Float> difMedianList = new ArrayList<Float>();
//		for(int i=0;i<width;i++) {
//			for(int j=0;j<height;j++) {
//				if(noiseEstMask[i][j]) {
//					float[] array = new float[pages-1];
//					System.arraycopy(dif[i][j], 0, array, 0, pages-1);
//					difMedianList.add(getMedian(array));
//				}
//			}
//		}
//		double std = Math.sqrt(getMedian(difMedianList)/0.9133);
////		System.out.println(std);
//		dif = null;
//		return std;
//	}
	
	public double calNoiseStd(boolean[][] noiseEstMask, float[][] stdMap) {

		
		float[][] stdGau = GaussFilter.gaussFilter(stdMap, 0.5f,0.5f);
		ArrayList<Float> difMedianList = new ArrayList<Float>();
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(noiseEstMask[i][j]) {
					difMedianList.add(stdGau[i][j]);
				}
			}
		}
		double std = getMedian(difMedianList);
		return std;
	}
	
	/**
	 * Just a tool to get the median number.
	 * 
	 * @param array input
	 * @return the median number of the input array
	 */
	public float getMedian(float[] array) {
		Arrays.sort(array);
		float result = 0;
		int len = array.length;
		if(len%2==0)
			result = (array[len/2 - 1] + array[len/2])/2;
		else 
			result = array[len/2];
		return result;
		
	}
	
	/**
	 * Just a tool to get the median number.
	 * 
	 * @param difMedianList input
	 * @return the median number of the input list
	 */
	public float getMedian(ArrayList<Float> difMedianList) {
		Collections.sort(difMedianList, new Comparator<Float>() {
			@Override
			public int compare(Float f1, Float f2) {
				if(f1<f2)
					return -1;
				else if(f1>f2)
					return 1;
				else
					return 0;
			}
		});
		float result = 0;
		int len = difMedianList.size();
		if(len%2==0)
			result = (difMedianList.get(len/2 - 1) + difMedianList.get(len/2))/2;
		else 
			result = difMedianList.get(len/2);
		return result;
		
	}
	
	/**
	 * Get the voxels whose value larger than thrArscl*sqrtVarEst.
	 * Then remove the 2D components whose size smaller than minSize.
	 * Here we use 2-Pass algorithm to get the components, and the 
	 * default connectivity is 4.
	 * 
	 * @param data input matrix
	 * @param varEst the variance of the data
	 * @param thrArscl the threshold parameter we set to filter 
	 * @param minSize the minimum size of the 2D components
	 * @param evtSpatialMask the valid region
	 * @return the valid voxels after preliminary process
	 */
	public boolean[][][] connect2D(float[][][] data, float varEst, float thrArscl, int minSize, boolean[][] evtSpatialMask){
		boolean[][][] dActVoxDi = new boolean[width][height][pages];
		float sqrtVarEst = (float) Math.sqrt(varEst);
		for(int k=0;k<pages;k++) {
			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					dActVoxDi[i][j][k] = data[i][j][k] >thrArscl*sqrtVarEst;
				}
			}
		}
		
		ConnectedComponents.twoPassConnect2DRemoveSmallArea4Conn(dActVoxDi,minSize,evtSpatialMask);
		
		return dActVoxDi;
	}
	
	
	
	
	/**
	 * Premiliary processing for data, get the active region and event seeds
	 * 
	 * @return return the labels of different active region
	 */
	protected int[][][] doInBackground() throws Exception {
		long start = System.currentTimeMillis();
		
		
		// valid Region: the region from the interface
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
		
		// noiseEstMask: the new valid region based on the data value
		boolean[][] noiseEstMask = new boolean[width][height];
		float[][] datOrgMean = imageDealer.avgImage;
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(evtSpatialMask[i][j]&&(datOrgMean[i][j]>opts.fgFluo))
					noiseEstMask[i][j] = true;
			}
		}
			
		publish(1);
		// save the original data
		float[][][] dataOrg = inputOrgImage(imgPlus);
		try {
			FileOutputStream f = new FileOutputStream(new File(proPath + "DataOrg.ser"));
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeObject(dataOrg);
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		// Calculate standard variance: Noise for smoothed data
		publish(2);
		long s1 = System.currentTimeMillis();
		
		inputImageAndGauss(imgPlus,dataOrg);
		
		double stdEstBef = calNoiseStd(noiseEstMask,stdMap1);
		opts.varEst = (float) (stdEstBef*stdEstBef);
		System.out.println(stdEstBef);
		double stdEst = calNoiseStd(noiseEstMask,stdMap2);
		dataOrg = null;
		
		long e1 = System.currentTimeMillis();
		System.out.println((e1-s1)+"ms");
		
		// Calculate dF: estimate the background and then subtract it
		publish(3);
			// get Bias
		float bias = MinMoveMean.getBias(opts.movAvgWin, opts.cut, stdEst);
		System.out.println(bias);
		
			// subtract background
		float[][][] dF = MinMoveMean.subMinMoveMean(dat, opts.movAvgWin, opts.cut, bias,(float)stdEst);
		// delete the region whose area less than minSize
		publish(4);
			// thrArscl: scale of noise, how much the pixel is bigger than noise
		boolean[][][] dActVoxDi = connect2D(dF, opts.varEst, opts.thrARScl, opts.minSize, evtSpatialMask);
		
		// connect the group in 3D dimension
		publish(5);
		HashMap<Integer, ArrayList<int[]>> labelMap = new HashMap<Integer, ArrayList<int[]>>();
		int[][][] label = ConnectedComponents.twoPassConnect3D(dActVoxDi, labelMap);
		System.out.println(labelMap.size());
		
		// Fork-in
		// Get the local maximal as the event seeds
		// First, need to gauss filter the whole video
		publish(6);

		HashMap<int[],Float> seeds = new HashMap<int[],Float>();
		ArrayList<int[]> location = new ArrayList<int[]>();
//		ForkJoinPool pool = new ForkJoinPool();
//		FindSeedsTask task = new FindSeedsTask(1,labelMap.size(),imgPlus,labelMap);

			// 3D guassblur
		ImagePlus imgPlusBlur = new ImagePlus(imageDealer.path);
		ImageProcessor imgProcessor2 = imgPlusBlur.getProcessor();
		float[][][] x = new float[width][height][pages];
		for(int k = 0;k<pages;k++) {
			for(int i = 0;i<width;i++) {
				for(int j=0;j<height;j++) {
					x[i][j][k] = dat[i][j][k]*dat[i][j][k]*opts.maxValueDat;
				}
			}
		}
		
		float[][][] y = GaussFilter.gaussFilter(x, 1.0f, 1.0f, 0.5f);
		
		
		for(int k = 1;k<=pages;k++) {
			imgPlusBlur.setPosition(k);
			for(int i = 0;i<width;i++) {
				for(int j=0;j<height;j++) {
					imgProcessor2.set(i, j, (int)y[i][j][k-1]);
				}
			}
		}

		location = SeedSearch.searchSeed(imgPlusBlur, labelMap);
		for(int[] p:location)
			seeds.put(p, dat[p[0]][p[1]][p[2]]);
		System.out.println(seeds.size());
		
		
		
		publish(7);
		// ------------------------ Save Data ----------------------------- //
		try {
			FileOutputStream f = null;
			ObjectOutputStream o = null;
			
			f = new FileOutputStream(new File(proPath+"ResultInStep1_Seeds.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(seeds);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath+"Step1_Labels.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(label);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath+"Data.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(dat);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath+"DF.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(dF);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath+"dActVoxDi.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(dActVoxDi);
			o.close();
			f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		boolean[][][] lmAll = new boolean[width][height][pages];
//		for(int[] p:location)
//			lmAll[p[0]][p[1]][p[2]] = true;
//		// ------------------------ Save Data ----------------------------- //
//		try {
//			FileOutputStream f = null;
//			ObjectOutputStream o = null;
//			
//			f = new FileOutputStream(new File(proPath+"SeedLocation.txt"));
//			o = new ObjectOutputStream(f);
//			o.writeObject(lmAll);
//			o.close();
//			f.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		// assign random color to each active region
		publish(8);
		int colorBase = imageDealer.colorBase;
		Random rv = new Random();
		Color[] labelColors = new Color[Math.max(seeds.size(),labelMap.size())+1];
		for(int i=0;i<labelColors.length;i++) {
			labelColors[i] = new Color(colorBase + rv.nextInt(256-colorBase), colorBase + rv.nextInt(256-colorBase),colorBase + rv.nextInt(256-colorBase));
		}
		imageDealer.labelColors = labelColors;
		
		long end = System.currentTimeMillis();
		System.out.println((end-start)+"ms");
		imageDealer.dat = dat;
		imageDealer.dF = dF;
		
		return label;
		
	}
	
	/** 
	 * Report the progress
	 */
	protected void process(List<Integer> chunks) {
		int value = chunks.get(chunks.size()-1);
//		progressBar.setValue(value);
		String str = "";
		switch(value) {
		case 1:
			str = "Smooth the Data 1/8";
			break;
		case 2:
			str = "Calculate the Variance 2/8";
			break;
		case 3:
			str = "Subtract Background 3/8";
			break;
		case 4:
			str = "Detect the Connected Region 4/8";
			break;
		case 5:
			str = "Detect the 3D Connected Region 5/8";
			break;
		case 6:
			str = "Search for the seeds 6/8";
			break;
		case 7:
			str = "Save the Results 7/8";
			break;
		case 8:
			str = "Assign Color 8/8";
			break;
		}
		jLabel.setText(str);
	}
	
	/** 
	 * Adjust the interface, save the status, and let the interface show the active regions
	 */
	@Override
	protected void done() {
		frame.setVisible(false);
//		JOptionPane.showMessageDialog(null, "Step1 Finish!");
		try {
			imageDealer.label = this.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		imageDealer.changeSignalDrawRegionStatus();
		new Thread(new Runnable() {

			@Override
			public void run() {
				imageDealer.dealImage();
				imageDealer.imageLabel.repaint();
			}
			
		}).start();
		imageDealer.center.gaussfilter.setEnabled(true);
		imageDealer.left.nextButton.setEnabled(true);
		imageDealer.left.backButton.setEnabled(true);
		imageDealer.left.jTP.setEnabledAt(1, true);
		
		imageDealer.right.typeJCB.setEnabled(true);
		if(imageDealer.left.jTPStatus<1) {
			imageDealer.right.typeJCB.addItem("Step1: Active Voxels");
			imageDealer.left.jTPStatus = Math.max(imageDealer.left.jTPStatus, 1);
		}
//		imageDealer.right.typeJCB.setSelectedIndex(1);
		imageDealer.saveStatus();
		imageDealer.running = false;
	}
}