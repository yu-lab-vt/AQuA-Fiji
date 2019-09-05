package va.vt.cbil;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
//import org.apache.poi.xssf.usermodel.*;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.io.FileSaver;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;


public class Aqua_OutPut extends SwingWorker<Void, Integer>{
	
	JFrame frame = new JFrame("Extract");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	
	static long start = System.currentTimeMillis();;
	static long end;
	ImageDealer imageDealer = null;
	boolean eventsExtract = false;
	boolean movieExtract = false;
	String savePath = null;
	String orgPath = null;
	int[][][] label = null;
	Color[] labelColors = null;
	String proPath = null;
	
	
	public Aqua_OutPut(ImageDealer imageDealer, boolean eventsExtract, boolean movieExtract, 
			String path, int[][][] label, Color[] labelColors, String savePath) {
		this.imageDealer = imageDealer;
		this.eventsExtract = eventsExtract;
		this.movieExtract = movieExtract;
		this.savePath = savePath;
		orgPath = path;
		this.label = label;
		this.labelColors = labelColors;
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
	
	public void getFeaTureTable() {
		FtsLst fts = imageDealer.fts;
		Opts opts = imageDealer.opts;
		
		System.out.println("Finish read");
		
		
		int nEvt = 10000;
		if(fts!=null)
			nEvt = fts.basic.area.size();
		
		ArrayList<Integer> evtLst = new ArrayList<>();
		for(int i=1;i<=nEvt;i++) {
			if(imageDealer.deleteColorSet.contains(i)||imageDealer.deleteColorSet2.contains(i))
				continue;
			evtLst.add(i);
		}
		
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(savePath + "\\Aqua_Output_Excel.csv"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        StringBuilder sb = new StringBuilder();
        // Index
        sb.append("Index");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(i);
        	sb.append(',');
        }
        sb.append('\n');
        
        // Area
		sb.append("Basic - Area");
    	sb.append(',');
        for(int i:evtLst) {
        	sb.append(fts.basic.area.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        // Perimeter
		sb.append("Basic - Perimeter");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.basic.perimeter.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        // Circularity
        sb.append("Basic - Circularity");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.basic.circMetric.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        // P Value
        sb.append("Curve - P Value on max Dff (-log10)");
    	sb.append(',');
    	for(int i:evtLst) {
        	double dffMaxPval = fts.curve.dffMaxPval.get(i);
        	sb.append(-Math.log10(dffMaxPval));
        	sb.append(',');
        }
        sb.append('\n');
        
        // Max Dff
        sb.append("Curve - Max Dff");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.curve.dffMax.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        //Curve - Duration 50% to 50%
        sb.append("Curve - Duration 50% to 50%");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.curve.width55.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        //Curve - Duration 10% to 10%
        sb.append("Curve - Duration 10% to 10%");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.curve.width11.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        // rise19
        sb.append("Curve - Rising Duration 10% to 90%");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.curve.rise19.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        // fall91
        sb.append("Curve - Decaying Duration 90% to 10%");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.curve.fall91.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        // decayTau
        sb.append("Curve - Decay Tau");
    	sb.append(',');
    	for(int i:evtLst) {
        	
        	if(!Float.isNaN(opts.ignoreTau)) {
        		Float decayTau = fts.curve.decayTau.get(i);
        		sb.append(decayTau);
        	}
        	sb.append(',');
        }
        sb.append('\n');
        
        // sum onset
        float[] sumOnset = new float[nEvt];
        // onset overall
        sb.append("Propagation - Onset - Overall");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propGrowOverall.get(i);
			float sum = 0;
			for(int k=0;k<x0.length;k++)
				sum += x0[k];
			sumOnset[i-1] = sum;
        	sb.append(sum);
        	sb.append(',');
        }
        sb.append('\n');
        
        // onset Anterior
        sb.append("Propagation - Onset - One Direction - Anterior");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propGrowOverall.get(i);
        	sb.append(x0[0]);
        	sb.append(',');
        }
        sb.append('\n');
        
        // onset Posterior
        sb.append("Propagation - Onset - One Direction - Posterior");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propGrowOverall.get(i);
        	sb.append(x0[1]);
        	sb.append(',');
        }
        sb.append('\n');
        
        // onset Left
        sb.append("Propagation - Onset - One Direction - Left");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propGrowOverall.get(i);
        	sb.append(x0[2]);
        	sb.append(',');
        }
        sb.append('\n');
        
        // onset Right
        sb.append("Propagation - Onset - One Direction - Right");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propGrowOverall.get(i);
        	sb.append(x0[3]);
        	sb.append(',');
        }
        sb.append('\n');
        
        // onset Ratio Anterior
        sb.append("Propagation - Onset - One Direction - Ratio - Anterior");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propGrowOverall.get(i);
        	if(sumOnset[i-1]!=0)
        		sb.append(x0[0]/sumOnset[i-1]);
        	sb.append(',');
        }
        sb.append('\n');
        
        // onset Ratio Posterior
        sb.append("Propagation - Onset - One Direction - Ratio - Posterior");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propGrowOverall.get(i);
        	if(sumOnset[i-1]!=0)
        		sb.append(x0[1]/sumOnset[i-1]);
        	sb.append(',');
        }
        sb.append('\n');
        
        // onset Ratio Left
        sb.append("Propagation - Onset - One Direction - Ratio - Left");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propGrowOverall.get(i);
        	if(sumOnset[i-1]!=0)
        		sb.append(x0[2]/sumOnset[i-1]);
        	sb.append(',');
        }
        sb.append('\n');
        
        // onset Ratio Right
        sb.append("Propagation - Onset - One Direction - Ratio - Right");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propGrowOverall.get(i);
        	if(sumOnset[i-1]!=0)
        		sb.append(x0[3]/sumOnset[i-1]);
        	sb.append(',');
        }
        sb.append('\n');
        
        // sum offset
        float[] sumOffset = new float[nEvt];
        // offset overall
        sb.append("Propagation - Offset - Overall");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propShrinkOverall.get(i);
			float sum = 0;
			for(int k=0;k<x0.length;k++)
				sum += Math.abs(x0[k]);
			sumOffset[i-1] = sum;
        	sb.append(sum);
        	sb.append(',');
        }
        sb.append('\n');
        
        // offset Anterior
        sb.append("Propagation - Offset - One Direction - Anterior");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propShrinkOverall.get(i);
        	sb.append(Math.abs(x0[0]));
        	sb.append(',');
        }
        sb.append('\n');
        
        // offset Posterior
        sb.append("Propagation - Offset - One Direction - Posterior");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propShrinkOverall.get(i);
        	sb.append(Math.abs(x0[1]));
        	sb.append(',');
        }
        sb.append('\n');
        
        // offset Left
        sb.append("Propagation - Offset - One Direction - Left");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propShrinkOverall.get(i);
        	sb.append(Math.abs(x0[2]));
        	sb.append(',');
        }
        sb.append('\n');
        
        // offset Right
        sb.append("Propagation - Offset - One Direction - Right");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propShrinkOverall.get(i);
        	sb.append(Math.abs(x0[3]));
        	sb.append(',');
        }
        sb.append('\n');
        
        // offset Ratio Anterior
        sb.append("Propagation - Offset - One Direction - Ratio - Anterior");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propShrinkOverall.get(i);
        	if(sumOffset[i-1]!=0)
        		sb.append(Math.abs(x0[0]/sumOffset[i-1]));
        	sb.append(',');
        }
        sb.append('\n');
        
        // offset Ratio Posterior
        sb.append("Propagation - Offset - One Direction - Ratio - Posterior");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propShrinkOverall.get(i);
        	if(sumOffset[i-1]!=0)
        		sb.append(Math.abs(x0[1]/sumOffset[i-1]));
        	sb.append(',');
        }
        sb.append('\n');
        
        // offset Ratio Left
        sb.append("Propagation - Offset - One Direction - Ratio - Left");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propShrinkOverall.get(i);
        	if(sumOffset[i-1]!=0)
        		sb.append(Math.abs(x0[2]/sumOffset[i-1]));
        	sb.append(',');
        }
        sb.append('\n');
        
        // offset Ratio Right
        sb.append("Propagation - Offset - One Direction - Ratio - Right");
    	sb.append(',');
    	for(int i:evtLst) {
        	float[] x0 = fts.propagation.propShrinkOverall.get(i);
        	if(sumOffset[i-1]!=0)
        		sb.append(Math.abs(x0[3]/sumOffset[i-1]));
        	sb.append(',');
        }
        sb.append('\n');
        
        int nLmk = 0;
		if(fts.region!=null && fts.region.landMark!=null&& fts.region.landMark.center!=null)
			nLmk = fts.region.landMark.center.length;
		
		boolean regionExist = false;
		if(fts.region.cell!=null && fts.region.cell.border!=null)
			regionExist = true;

		System.out.println("LandMark Number: " + nLmk);
		System.out.println("RegionExist: " + regionExist);
		
		// LandMark - average
		for(int k=1;k<=nLmk;k++) {
			sb.append("Landmark - Event Average Distance - Landmark " + k);
			sb.append(',');
			for(int i:evtLst) {
				sb.append(fts.region.landmarkDist.distAvg[i-1][k-1]);
				sb.append(',');
			}
			sb.append('\n');
		}
		
		// LandMark - min
		for(int k=1;k<=nLmk;k++) {
			sb.append("Landmark - Event Minimum Distance - Landmark " + k);
			sb.append(',');
			for(int i:evtLst) {
				sb.append(fts.region.landmarkDist.distMin[i-1][k-1]);
				sb.append(',');
			}
			sb.append('\n');
		}
		
		// LandMark - toward
		for(int k=1;k<=nLmk;k++) {
			sb.append("Landmark - Event Toward Landmark - Landmark " + k);
			sb.append(',');
			for(int i:evtLst) {
				sb.append(fts.region.landmarkDir.chgToward[i-1][k-1]);
				sb.append(',');
			}
			sb.append('\n');
		}
		
		// LandMark - away
		for(int k=1;k<=nLmk;k++) {
			sb.append("Landmark - Event Away From Landmark - Landmark " + k);
			sb.append(',');
			for(int i:evtLst) {
				sb.append(fts.region.landmarkDir.chgAway[i-1][k-1]);
				sb.append(',');
			}
			sb.append('\n');
		}
		
		// region TODO： seems multiple regions?
		if(regionExist) {
			sb.append("Region - Event Centroid Distance To Border");
			sb.append(',');
			for(int i:evtLst) {
				float[] x0 = fts.region.cell.dist2border[i-1];
				float minX0 = Float.MAX_VALUE;
				for(int t=0;t<x0.length;t++) {
					if(!Float.isNaN(x0[t]))
						minX0 = Math.min(minX0, x0[t]);
				}
				sb.append(minX0);
				sb.append(',');
			}
			sb.append('\n');
			
			sb.append("Region - Event Centroid Distance To Border - Normalized By Region Radius");
			sb.append(',');
			for(int i:evtLst) {
				float[] x0 = fts.region.cell.dist2borderNorm[i-1];
				float minX0 = Float.MAX_VALUE;
				for(int t=0;t<x0.length;t++) {
					if(!Float.isNaN(x0[t]))
						minX0 = Math.min(minX0, x0[t]);
				}
				sb.append(minX0);
				sb.append(',');
			}
			sb.append('\n');
		}
		
		// network
		sb.append("Network - Temporal Density");
		sb.append(',');
		for(int i:evtLst) {
			sb.append(fts.network.nOccurSameLoc[i-1][0]);
			sb.append(',');
		}
		sb.append('\n');
		
		// network
		sb.append("Network - Temporal Density With Similar Size Only");
		sb.append(',');
		for(int i:evtLst) {
			sb.append(fts.network.nOccurSameLoc[i-1][1]);
			sb.append(',');
		}
		sb.append('\n');
		
		// network
		sb.append("NetWork - Spatial Density");
		sb.append(',');
		for(int i:evtLst) {
			sb.append(fts.network.nOccurSameTime[i-1]);
			sb.append(',');
		}
		sb.append('\n');

        pw.write(sb.toString());
        pw.close();
        System.out.println("done!");
	}
	
	
	/*
	public void getFeaTureTable() {
		FtsLst fts = null;
		Opts opts = null;
		try {
			FileInputStream fi = null;
			ObjectInputStream oi = null;		
			// evtLst
			fi = new FileInputStream(new File(proPath + "Fts.txt"));
			oi = new ObjectInputStream(fi);
			fts = (FtsLst) oi.readObject();
			oi.close();
			fi.close();
			
			fi = new FileInputStream(new File(proPath + "Opts.txt"));
			oi = new ObjectInputStream(fi);
			opts = (Opts) oi.readObject();
			oi.close();
			fi.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
		System.out.println("Finish read");
		
		
		int nEvt = 10000;
		if(fts!=null)
			nEvt = fts.basic.area.size();
		// show in event manager and for exporting
		@SuppressWarnings("resource")
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet();
		
		XSSFRow row1 = sheet.createRow(1);
		row1.createCell(0).setCellValue("Index");
		XSSFRow row2 = sheet.createRow(2);
		row2.createCell(0).setCellValue("Basic - Area");
		XSSFRow row3 = sheet.createRow(3);
		row3.createCell(0).setCellValue("Basic - Perimeter");
		XSSFRow row4 = sheet.createRow(4);
		row4.createCell(0).setCellValue("Basic - Circularity");
		XSSFRow row5 = sheet.createRow(5);
		row5.createCell(0).setCellValue("Curve - P Value on max Dff (-log10)");
		XSSFRow row6 = sheet.createRow(6);
		row6.createCell(0).setCellValue("Curve - Max Dff");
		XSSFRow row7 = sheet.createRow(7);
		row7.createCell(0).setCellValue("Curve - Duration 50% to 50%");
		XSSFRow row8 = sheet.createRow(8);
		row8.createCell(0).setCellValue("Curve - Duration 10% to 10%");
		XSSFRow row9 = sheet.createRow(9);
		row9.createCell(0).setCellValue("Curve - Rising Duration 10% to 90%");
		XSSFRow row10 = sheet.createRow(10);
		row10.createCell(0).setCellValue("Curve - Decaying Duration 90% to 10%");
		XSSFRow row11 = sheet.createRow(11);
		row11.createCell(0).setCellValue("Curve - Decay Tau");
		XSSFRow row12 = sheet.createRow(12);
		row12.createCell(0).setCellValue("Propagation - Onset - Overall");
		XSSFRow row13 = sheet.createRow(13);
		row13.createCell(0).setCellValue("Propagation - Onset - One Direction - Anterior");
		XSSFRow row14 = sheet.createRow(14);
		row14.createCell(0).setCellValue("Propagation - Onset - One Direction - Posterior");
		XSSFRow row15 = sheet.createRow(15);
		row15.createCell(0).setCellValue("Propagation - Onset - One Direction - Left");
		XSSFRow row16 = sheet.createRow(16);
		row16.createCell(0).setCellValue("Propagation - Onset - One Direction - Right");
		XSSFRow row17 = sheet.createRow(17);
		row17.createCell(0).setCellValue("Propagation - Onset - One Direction - Ratio - Anterior");
		XSSFRow row18 = sheet.createRow(18);
		row18.createCell(0).setCellValue("Propagation - Onset - One Direction - Ratio - Posterior");
		XSSFRow row19 = sheet.createRow(19);
		row19.createCell(0).setCellValue("Propagation - Onset - One Direction - Ratio - Left");
		XSSFRow row20 = sheet.createRow(20);
		row20.createCell(0).setCellValue("Propagation - Onset - One Direction - Ratio - Right");
		XSSFRow row21 = sheet.createRow(21);
		row21.createCell(0).setCellValue("Propagation - Offset - Overall");
		XSSFRow row22 = sheet.createRow(22);
		row22.createCell(0).setCellValue("Propagation - Offset - One Direction - Anterior");
		XSSFRow row23 = sheet.createRow(23);
		row23.createCell(0).setCellValue("Propagation - Offset - One Direction - Posterior");
		XSSFRow row24 = sheet.createRow(24);
		row24.createCell(0).setCellValue("Propagation - Offset - One Direction - Left");
		XSSFRow row25 = sheet.createRow(25);
		row25.createCell(0).setCellValue("Propagation - Offset - One Direction - Right");
		XSSFRow row26 = sheet.createRow(26);
		row26.createCell(0).setCellValue("Propagation - Offset - One Direction - Ratio - Anterior");
		XSSFRow row27 = sheet.createRow(27);
		row27.createCell(0).setCellValue("Propagation - Offset - One Direction - Ratio - Posterior");
		XSSFRow row28 = sheet.createRow(28);
		row28.createCell(0).setCellValue("Propagation - Offset - One Direction - Ratio - Left");
		XSSFRow row29 = sheet.createRow(29);
		row29.createCell(0).setCellValue("Propagation - Offset - One Direction - Ratio - Right");
		
		int nLmk = 0;
		if(fts.region!=null && fts.region.landMark!=null&& fts.region.landMark.center!=null)
			nLmk = fts.region.landMark.center.length;
		
		boolean regionExist = false;
		if(fts.region.cell!=null && fts.region.cell.border!=null)
			regionExist = true;

		System.out.println("LandMark Number: " + nLmk);
		System.out.println("RegionExist: " + regionExist);
		XSSFRow[] rowLst = regionExist?new XSSFRow[4*nLmk+5]:new XSSFRow[4*nLmk+3];
		
		for(int k=1;k<=nLmk;k++) {
			rowLst[k-1] = sheet.createRow(29+k);
			rowLst[k-1].createCell(0).setCellValue("Landmark - Event Average Distance - Landmark " + k);
		}
		
		for(int k=nLmk+1;k<=2*nLmk;k++) {
			rowLst[k-1] = sheet.createRow(29+k);
			rowLst[k-1].createCell(0).setCellValue("Landmark - Event Minimum Distance - Landmark " + (k-nLmk));
		}
		
		for(int k=2*nLmk+1;k<=3*nLmk;k++) {
			rowLst[k-1] = sheet.createRow(29+k);
			rowLst[k-1].createCell(0).setCellValue("Landmark - Event Toward Landmark - Landmark " + (k-2*nLmk));
		}
		
		for(int k=3*nLmk+1;k<=4*nLmk;k++) {
			rowLst[k-1] = sheet.createRow(29+k);
			rowLst[k-1].createCell(0).setCellValue("Landmark - Event Away From Landmark - Landmark " + (k-3*nLmk));
		}
		
		int curIndex = 4*nLmk;
		int curRowNum = 29+4*nLmk + 1;
		
		if(regionExist) {
			rowLst[curIndex] = sheet.createRow(curRowNum);
			rowLst[curIndex].createCell(0).setCellValue("Region - Event Centroid Distance To Border");
			curIndex++;
			curRowNum++;
			rowLst[curIndex] = sheet.createRow(curRowNum);
			rowLst[curIndex].createCell(0).setCellValue("Region - Event Centroid Distance To Border - Normalized By Region Radius");
			curIndex++;
			curRowNum++;
		}
		rowLst[curIndex] = sheet.createRow(curRowNum);
		rowLst[curIndex].createCell(0).setCellValue("Network - Temporal Density");
		curIndex++;
		curRowNum++;
		rowLst[curIndex] = sheet.createRow(curRowNum);
		rowLst[curIndex].createCell(0).setCellValue("Network - Temporal Density With Similar Size Only");
		curIndex++;
		curRowNum++;
		rowLst[curIndex] = sheet.createRow(curRowNum);
		rowLst[curIndex].createCell(0).setCellValue("NetWork - Spatial Density");
		
		
		for(int i=1;i<nEvt;i++) {
			// Index
			row1.createCell(i).setCellValue(i);
			// Area
			row2.createCell(i).setCellValue(fts.basic.area.get(i));
			// Perimeter
			row3.createCell(i).setCellValue(fts.basic.perimeter.get(i));
			// Circularity
			row4.createCell(i).setCellValue(fts.basic.circMetric.get(i));
			// P value
			double dffMaxPval = fts.curve.dffMaxPval.get(i);
			if(dffMaxPval>0)
				row5.createCell(i).setCellValue(-Math.log10(dffMaxPval));
			else
				row5.createCell(i).setCellValue(opts.maxValueDat);
			// Max Dff
			row6.createCell(i).setCellValue(fts.curve.dffMax.get(i));
			// width55
			row7.createCell(i).setCellValue(fts.curve.width55.get(i));
			// width11
			row8.createCell(i).setCellValue(fts.curve.width11.get(i));
			// rise19
			row9.createCell(i).setCellValue(fts.curve.rise19.get(i));
			// fall91
			row10.createCell(i).setCellValue(fts.curve.fall91.get(i));
			// decayTau
			Float decayTau = fts.curve.decayTau.get(i);
			if(!Float.isNaN(opts.ignoreTau))
				row11.createCell(i).setCellValue(decayTau);
			// onset overall
			float[] x0 = fts.propagation.propGrowOverall.get(i);
			float sum = 0;
			for(int k=0;k<x0.length;k++)
				sum += x0[k];
			row12.createCell(i).setCellValue(sum);
			// one direction - Anterior
			row13.createCell(i).setCellValue(x0[0]);
			// one direction - Posterior
			row14.createCell(i).setCellValue(x0[1]);
			// one direction - Left
			row15.createCell(i).setCellValue(x0[2]);
			// one direction - Right
			row16.createCell(i).setCellValue(x0[3]);
			if(sum!=0) {
				// One Direction - Ratio - Anterior
				row17.createCell(i).setCellValue(x0[0]/sum);
				// One Direction - Ratio - Posterior
				row18.createCell(i).setCellValue(x0[1]/sum);
				// One Direction - Ratio - Left
				row19.createCell(i).setCellValue(x0[2]/sum);
				// One Direction - Ratio - Right
				row20.createCell(i).setCellValue(x0[3]/sum);
			}
			
			// offset
			x0 = fts.propagation.propShrinkOverall.get(i);
			sum = 0;
			for(int k=0;k<x0.length;k++) {
				x0[k] = Math.abs(x0[k]);
				sum += x0[k];
			}
			row21.createCell(i).setCellValue(sum);
			// one direction - Anterior
			row22.createCell(i).setCellValue(x0[0]);
			// one direction - Posterior
			row23.createCell(i).setCellValue(x0[1]);
			// one direction - Left
			row24.createCell(i).setCellValue(x0[2]);
			// one direction - Right
			row25.createCell(i).setCellValue(x0[3]);
			if(sum!=0) {
				// One Direction - Ratio - Anterior
				row26.createCell(i).setCellValue(x0[0]/sum);
				// One Direction - Ratio - Posterior
				row27.createCell(i).setCellValue(x0[1]/sum);
				// One Direction - Ratio - Left
				row28.createCell(i).setCellValue(x0[2]/sum);
				// One Direction - Ratio - Right
				row29.createCell(i).setCellValue(x0[3]/sum);
			}
			
			// Landmark - average
			for(int k=1;k<=nLmk;k++) {
				rowLst[k-1].createCell(i).setCellValue(fts.region.landmarkDist.distAvg[i-1][k-1]);
			}
			// Landmark - minimum
			for(int k=1;k<=nLmk;k++) {
				rowLst[k+nLmk-1].createCell(i).setCellValue(fts.region.landmarkDist.distMin[i-1][k-1]);
			}
			// Landmark - toward
			for(int k=1;k<=nLmk;k++) {
				rowLst[k+2*nLmk-1].createCell(i).setCellValue(fts.region.landmarkDir.chgToward[i-1][k-1]);
			}
			// Landmark - away
			for(int k=1;k<=nLmk;k++) {
				rowLst[k+3*nLmk-1].createCell(i).setCellValue(fts.region.landmarkDir.chgAway[i-1][k-1]);
			}
			
			curIndex = 4*nLmk;
			if(regionExist) {
				// Region - event centroid distance to border
				x0 = fts.region.cell.dist2border[i-1];
				float minX0 = Float.MAX_VALUE;
				for(int t=0;t<x0.length;t++) {
					if(!Float.isNaN(x0[t]))
						minX0 = Math.min(minX0, x0[t]);
				}
				rowLst[curIndex].createCell(i).setCellValue(minX0);
				curIndex++;
				// Region - event centroid distance to border - normalized by region radius
				x0 = fts.region.cell.dist2borderNorm[i-1];
				minX0 = Float.MAX_VALUE;
				for(int t=0;t<x0.length;t++) {
					if(!Float.isNaN(x0[t]))
						minX0 = Math.min(minX0, x0[t]);
				}
				rowLst[curIndex].createCell(i).setCellValue(minX0);
				curIndex++;
			}
			// Network - Temporal density
			rowLst[curIndex].createCell(i).setCellValue(fts.network.nOccurSameLoc[i-1][0]);
			curIndex++;
			// Network - Temporal density similar
			rowLst[curIndex].createCell(i).setCellValue(fts.network.nOccurSameLoc[i-1][1]);
			curIndex++;
			// Network - Spatial density
			rowLst[curIndex].createCell(i).setCellValue(fts.network.nOccurSameTime[i-1]);
			
		}
		
		try {
			FileOutputStream output = new FileOutputStream(savePath + "\\Aqua_Output_Excel.xlsx");
			wb.write(output);
			output.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
	@Override
	protected Void doInBackground(){
		
		if(eventsExtract) {
			publish(1);
			getFeaTureTable();
			getCurveOutput();
		}
		if(movieExtract) {
			publish(2);
			exportMovie();
		}
		return null;
	} 
	
	private void getCurveOutput() {
		FtsLst fts = imageDealer.fts;
		Opts opts = imageDealer.opts;
		
		System.out.println("Finish read");
		
		
		int nEvt = 10000;
		if(fts!=null)
			nEvt = fts.basic.area.size();
		
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(savePath + "\\Aqua_Curve_Output.csv"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		float[][][] dffMat = imageDealer.dffMat;
		int T = dffMat[0].length;
		
        StringBuilder sb = new StringBuilder();
        // Title
        sb.append("Event Index");
        sb.append(',');
        sb.append("Start Frame");
        sb.append(',');
        sb.append("End Frame");
        sb.append(',');
        for(int t=0;t<T;t++) {
        	sb.append("Frame " + (t+1));
            sb.append(',');
        }
        sb.append('\n');
        
        for(int i=0;i<nEvt;i++) {
        	sb.append("Event " + (i+1));
            sb.append(',');
            sb.append(imageDealer.fts.curve.tBegin.get(i+1)+1);
            sb.append(',');
            sb.append(imageDealer.fts.curve.tEnd.get(i+1)+1);
            sb.append(',');
            
            float min = Float.MAX_VALUE;
			float max = -Float.MAX_VALUE;
			for(int t = 0;t<T;t++) {
				min = Math.min(min, dffMat[i][t][1]);
				max = Math.max(max, dffMat[i][t][1]);
			}	
			
			float[] curve = new float[T];

			for(int t = 0;t<T;t++) {
				curve[t] = (dffMat[i][t][1] - min)/(max-min);
				sb.append(curve[t]);
                sb.append(',');
			}
            sb.append('\n');
        }
        
        pw.write(sb.toString());
        pw.close();
        System.out.println("done!");
        
		
	}

	private void exportMovie() {
		// TODO Auto-generated method stub
		System.out.println(savePath + "\\Aqua_Output_Movie");
		ImagePlus img = new ImagePlus(orgPath);
		ImageConverter converter = new ImageConverter(img);
		converter.convertToGray8();
		converter.convertToRGB();
		int width = label.length;
		int height = label[0].length;
		int pages = label[0][0].length;
		ImageProcessor imgProcessor = img.getProcessor();
		
		for(int k = 1;k<=pages;k++) {
			img.setPosition(k);
			for(int x=0;x<width;x++) {
				for(int y=0;y<height;y++) {
					int gray = (int) ((imgProcessor.getPixel(x, y)&255)*0.6);
					if(label[x][y][k-1]!=0) {
						Color curColor = labelColors[label[x][y][k-1]-1];
						
						int red = (int)(curColor.getRed() + gray*1.2);
						int green = (int)(curColor.getGreen() + gray*1.2);
						int blue = (int)(curColor.getBlue() + gray*1.2);
						red = red>255?255:red;
						green = green>255?255:green;
						blue = blue>255?255:blue;
						imgProcessor.setColor(new Color(red,green,blue));
						Roi roi = new Roi(x,y,1,1);
						imgProcessor.fill(roi);
					}else {
						imgProcessor.setColor(new Color(gray,gray,gray));
						Roi roi = new Roi(x,y,1,1);
						imgProcessor.fill(roi);
					}
				}
			}
		}
		FileSaver fs = new FileSaver(img);
		fs.saveAsTiff(savePath + "\\Aqua_Output_Movie.tif");

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
	
	static public void showDetails(ArrayList<Integer> indexLst, FtsLst fts, Opts opts) {
		JFrame frame = new JFrame("Features for favorite events");
		frame.setSize(850,700);
		frame.setUndecorated(false);
		frame.setLocationRelativeTo(null);
		
		JScrollPane tablePane = new JScrollPane();
		
		DefaultTableModel model = null;
		JTable table = null;
		
    	model = new DefaultTableModel();

    	model.addColumn("");
    	model.addRow(new Object[] {"Index"});
    	model.addRow(new Object[] {"Basic - Area"});
    	model.addRow(new Object[] {"Basic - Perimeter"});
    	model.addRow(new Object[] {"Basic - Circularity"});
    	model.addRow(new Object[] {"Curve - P Value on max Dff (-log10)"});
    	model.addRow(new Object[] {"Curve - Max Dff"});
    	model.addRow(new Object[] {"Curve - Duration 50% to 50%"});
    	model.addRow(new Object[] {"Curve - Duration 10% to 10%"});
    	model.addRow(new Object[] {"Curve - Rising Duration 10% to 90%"});
    	model.addRow(new Object[] {"Curve - Decaying Duration 90% to 10%"});
    	model.addRow(new Object[] {"Curve - Decay Tau"});
    	model.addRow(new Object[] {"Propagation - Onset - Overall"});
    	model.addRow(new Object[] {"Propagation - Onset - One Direction - Anterior"});
    	model.addRow(new Object[] {"Propagation - Onset - One Direction - Posterior"});
    	model.addRow(new Object[] {"Propagation - Onset - One Direction - Left"});
    	model.addRow(new Object[] {"Propagation - Onset - One Direction - Right"});
    	model.addRow(new Object[] {"Propagation - Onset - One Direction - Ratio - Anterior"});
    	model.addRow(new Object[] {"Propagation - Onset - One Direction - Ratio - Posterior"});
    	model.addRow(new Object[] {"Propagation - Onset - One Direction - Ratio - Left"});
    	model.addRow(new Object[] {"Propagation - Onset - One Direction - Ratio - Right"});
    	model.addRow(new Object[] {"Propagation - Offset - Overall"});
    	model.addRow(new Object[] {"Propagation - Offset - One Direction - Anterior"});
    	model.addRow(new Object[] {"Propagation - Offset - One Direction - Posterior"});
    	model.addRow(new Object[] {"Propagation - Offset - One Direction - Left"});
    	model.addRow(new Object[] {"Propagation - Offset - One Direction - Right"});
    	model.addRow(new Object[] {"Propagation - Offset - One Direction - Ratio - Anterior"});
    	model.addRow(new Object[] {"Propagation - Offset - One Direction - Ratio - Posterior"});
    	model.addRow(new Object[] {"Propagation - Offset - One Direction - Ratio - Left"});
    	model.addRow(new Object[] {"Propagation - Offset - One Direction - Ratio - Right"});
    	
    	int nLmk = 0;
		if(fts.region!=null && fts.region.landMark!=null&& fts.region.landMark.center!=null)
			nLmk = fts.region.landMark.center.length;
		
		for(int k=1;k<=nLmk;k++) {
			model.addRow(new Object[] {"Landmark - event average distance - landmark " + k});
			model.addRow(new Object[] {"Landmark - event minimum distance - landmark " + k});
			model.addRow(new Object[] {"Landmark - event toward landmark - landmark " + k});
			model.addRow(new Object[] {"Landmark - event away from landmark - landmark " + k});
		}
		
		boolean regionExist = false;
		if(fts.region.cell!=null && fts.region.cell.border!=null)
			regionExist = true;
    	
    	model.addRow(new Object[] {"Region - event centroid distance to border"});
    	model.addRow(new Object[] {"Region - event centroid distance to border -normalize"});
    	model.addRow(new Object[] {"Network - Temporal density"});
    	model.addRow(new Object[] {"Network - Temporal density with similar size only"});
    	model.addRow(new Object[] {"Network - Spatial density"});
    	
    	
    	
    	table = new JTable(model){
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Class getColumnClass(int column) {
				return String.class;
			}
    		
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				
				if(column==0)
					component.setBackground(new Color(238, 238, 238));
				else {
					if(row%2==1)
						component.setBackground(new Color(245, 245, 250));
					else
						component.setBackground(Color.white);
				}
				return component;
			}
    	};
    	
    	DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
    	tcr.setHorizontalAlignment(JLabel.LEFT);
    	table.setDefaultRenderer(Object.class, tcr);
    	table.getColumnModel().getColumn(0).setPreferredWidth(80);
    	table.setSize(new Dimension(850,550));
    	
    	tablePane = new JScrollPane(table);
    	
    	tablePane.setPreferredSize(new Dimension(850,550));
    	frame.add(tablePane);
    	frame.setVisible(true);
    	
    	tablePane.setOpaque(true);
    	tablePane.setBackground(Color.WHITE);
    	table.setOpaque(true);
    	table.setBackground(Color.white);
    	
    	for(int i=0;i<indexLst.size();i++) {
    		model.addColumn("" + (i+1));
    		int nEvt = indexLst.get(i);
    		System.out.println(""+nEvt);
			// Index
			table.setValueAt(new Integer(nEvt), 0, i+1);
			// Area
			table.setValueAt(new Float(fts.basic.area.get(nEvt)), 1, i+1);
			// Perimeter
			table.setValueAt(new Float(fts.basic.perimeter.get(nEvt)), 2, i+1);
			// Circularity
			table.setValueAt(new Float(fts.basic.circMetric.get(nEvt)), 3, i+1);
			// P value
			double dffMaxPval = fts.curve.dffMaxPval.get(nEvt);
			if(dffMaxPval>0)
				table.setValueAt(new Float(-Math.log10(dffMaxPval)), 4, i+1);
			else
				table.setValueAt(new Float(opts.maxValueDat), 4, i+1);
			// Max Dff
			table.setValueAt(new Float(fts.curve.dffMax.get(nEvt)), 5, i+1);
			// width55
			table.setValueAt(new Float(fts.curve.width55.get(nEvt)), 6, i+1);
			// width11
			table.setValueAt(new Float(fts.curve.width11.get(nEvt)), 7, i+1);
			// rise19
			table.setValueAt(new Float(fts.curve.rise19.get(nEvt)), 8, i+1);
			// fall91
			table.setValueAt(new Float(fts.curve.fall91.get(nEvt)), 9, i+1);
			// decayTau
			Float decayTau = fts.curve.decayTau.get(nEvt);
			if(!Float.isNaN(opts.ignoreTau))
				table.setValueAt(new Float(decayTau), 10, i+1);
			// onset overall
			float[] x0 = fts.propagation.propGrowOverall.get(nEvt);
			float sum = 0;
			for(int k=0;k<x0.length;k++)
				sum += x0[k];
			table.setValueAt(new Float(sum), 11, i+1);
			// one direction - Anterior
			table.setValueAt(new Float(x0[0]), 12, i+1);
			// one direction - Posterior
			table.setValueAt(new Float(x0[1]), 13, i+1);
			// one direction - Left
			table.setValueAt(new Float(x0[2]), 14, i+1);
			// one direction - Right
			table.setValueAt(new Float(x0[3]), 15, i+1);
			if(sum!=0) {
				// One Direction - Ratio - Anterior
				table.setValueAt(new Float(x0[0]/sum), 16, i+1);
				// One Direction - Ratio - Posterior
				table.setValueAt(new Float(x0[1]/sum), 17, i+1);
				// One Direction - Ratio - Left
				table.setValueAt(new Float(x0[2]/sum), 18, i+1);
				// One Direction - Ratio - Right
				table.setValueAt(new Float(x0[3]/sum), 19, i+1);
			}
			
			// offset
			x0 = fts.propagation.propShrinkOverall.get(nEvt);
			sum = 0;
			for(int k=0;k<x0.length;k++) {
				x0[k] = Math.abs(x0[k]);
				sum += x0[k];
			}
			table.setValueAt(new Float(sum), 20, i+1);
			// one direction - Anterior
			table.setValueAt(new Float(x0[0]), 21, i+1);
			// one direction - Posterior
			table.setValueAt(new Float(x0[1]), 22, i+1);
			// one direction - Left
			table.setValueAt(new Float(x0[2]), 23, i+1);
			// one direction - Right
			table.setValueAt(new Float(x0[3]), 24, i+1);
			if(sum!=0) {
				// One Direction - Ratio - Anterior
				table.setValueAt(new Float(x0[0]/sum), 25, i+1);
				// One Direction - Ratio - Posterior
				table.setValueAt(new Float(x0[1]/sum), 26, i+1);
				// One Direction - Ratio - Left
				table.setValueAt(new Float(x0[0]/sum), 27, i+1);
				// One Direction - Ratio - Right
				table.setValueAt(new Float(x0[0]/sum), 28, i+1);
			}
			
			int curRow = 29;
			for(int k=1;k<=nLmk;k++) {
				table.setValueAt(new Float(fts.region.landmarkDist.distAvg[nEvt-1][k-1]), curRow, i+1);
				table.setValueAt(new Float(fts.region.landmarkDist.distMin[nEvt-1][k-1]), curRow+1, i+1);
				table.setValueAt(new Float(fts.region.landmarkDir.chgToward[nEvt-1][k-1]), curRow+2, i+1);
				table.setValueAt(new Float(fts.region.landmarkDir.chgAway[nEvt-1][k-1]), curRow+3, i+1);
				curRow += 4;
			}
			
			float minX0 = Float.NaN;
			float minX1 = Float.NaN;
			if(regionExist) {
				minX0 = Float.MAX_VALUE;
				float[] xx0 = fts.region.cell.dist2border[nEvt-1];
				for(int t=0;t<xx0.length;t++) {
					if(!Float.isNaN(xx0[t]))
						minX0 = Math.min(minX0, xx0[t]);
				}
				
				float[] x1 = fts.region.cell.dist2borderNorm[nEvt-1];
				minX1 = Float.MAX_VALUE;
				for(int t=0;t<x1.length;t++) {
					if(!Float.isNaN(x1[t]))
						minX1 = Math.min(minX1, x1[t]);
				}
			}
			table.setValueAt(new Float(minX0), curRow, i+1);
			curRow++;
			table.setValueAt(new Float(minX1), curRow, i+1);
			curRow++;
			table.setValueAt(new Integer(fts.network.nOccurSameLoc[nEvt-1][0]), curRow, i+1);
			curRow++;
			table.setValueAt(new Integer(fts.network.nOccurSameLoc[nEvt-1][1]), curRow, i+1);
			curRow++;
			table.setValueAt(new Integer(fts.network.nOccurSameTime[nEvt-1]), curRow, i+1);
			
			
		}
    	
    	
    	
    	
		
	} 
	
	@Override
	protected void done() {
		frame.setVisible(false);
		JOptionPane.showMessageDialog(null, "Finish");
	}
}
