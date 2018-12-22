package va.vt.cbil;
/**
 * Deal with Image, change the brightness, change the window size, and other functions 
 */
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

class ImageDealer {

	public static final int DEFAULT_CONTRAST_BRIGHTNESS = 100;
	public static final Point DEFAULT_STARTPOINT= null;
	public static final Point DEFAULT_ENDPOINT= null;
	
	private int min = 0;
	private int max = 0;
	private int contrast = DEFAULT_CONTRAST_BRIGHTNESS;
	private Point startPoint = null;
	private Point endPoint = null;
	private int length = 1;	// little images's size	
	private float colorScale = 0.5f;	// adjust color brightness
	int colorBase = 80;
	int border = 0;
	int maxImageWidth = 0;
	int maxImageHeight = 0;
	int sw = 0;
	int sh = 0;
	int orgMax = 0;
	int maxAvg = 0;
	
	int[][][] datR = null;
	float[][] curBuilderImage = null;
	float[][] avgImage = null;
	
	int maxBuilderWidth = 0;
	int maxBuilderHeight = 0;
//	ImageShow image = null;
	MyImageLabel imageLabel = null;
	MakeBuilderLabel builderImageLabel = null;
	JLabel leftImageLabel = null;
	JLabel rightImageLabel = null;
	DrawCurveLabel curveLabel = null;
	
	LeftGroupPanel left = null;
	CenterGroupPanel center = null;
	RightGroupPanel right = null;
	
	JFrame window = null;
	private boolean status = true;
	
	HashSet<Integer> featureTableList = new HashSet<>();
	FtsLst fts = null;
	
	// ImageJ
	ImagePlus imgPlus = null;
	ImageProcessor imgProcessor = null;
	// Step1 variables
	int[][][] label = null;
	Color[] labelColors = null;
	int pages = 0;
    int width = 0;
    int height = 0;
	boolean drawRegion = false;
	Opts opts = null;
	HashSet<Integer> deleteColorSet = new HashSet<>();
	String path = null;
	String proPath = null;
	
	// mark
	boolean[][] regionMark = null;
	int[][] regionMarkLabel = null;
	boolean[][] landMark = null;
	int[][] landMarkLabel = null;
	ArrayList<ArrayList<Point>> regionMarkLst = null;
	ArrayList<ArrayList<Point>> landMarkLst = null;
	
	
	// result Curve
	float[][][] dffMat = null;
	
	public ImageDealer(String path, String proPath, int border, int index) {
		this.path = path;
		this.proPath = proPath;
		this.border = border;
		opts = new Opts(index);
		imgPlus = new ImagePlus(path);
		opts.maxValueDat = (int) (Math.pow(2,imgPlus.getBitDepth())-1);
		
		width = imgPlus.getWidth();
		height = imgPlus.getHeight();
//		System.out.println(width + " " + height);
		pages = imgPlus.getImageStackSize();
		opts.W = width;
		opts.H = height;
		opts.T = pages;
		ImageStack stk = imgPlus.getStack().convertToFloat();
		imgPlus.setStack(stk);
		imgProcessor = imgPlus.getProcessor();
		regionMark = new boolean[width][height];
		landMark = new boolean[width][height];
		regionMarkLabel = new int[width][height];
		landMarkLabel = new int[width][height];
		label = new int[width][height][pages];
		
		avgImage = new float[width][height];
		curBuilderImage = new float[width][height];
		
		if(border!=0) {
			for(int x = border;x<width-border;x++) {
				for(int y = border;y<height-border;y++) {
					regionMark[x][y] = true;
					regionMarkLabel[x][y] = 1;
				}
			}
		}
		
		for(int k = 1;k<=pages;k++) {
			imgPlus.setPosition(k);
			float[][] f = imgProcessor.getFloatArray();
			for(int i = 0;i<width;i++) {
				for(int j=0;j<height;j++) {
					max = (int) Math.max(max, f[i][j]);
					f[i][j] = (float) Math.sqrt(f[i][j]/opts.maxValueDat);
					curBuilderImage[i][j] += f[i][j]/pages;
					avgImage[i][j] += f[i][j]/pages;
					maxAvg = (int) Math.max(maxAvg, curBuilderImage[i][j]*curBuilderImage[i][j]*opts.maxValueDat);
				}
			}
			imgProcessor.setFloatArray(f);
		}
		orgMax = max;
		imgPlus.setPosition(1);
	}
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
	
	public void setMin(int min) {
		this.min = min;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public void setConstrast(int contrast) {
		this.contrast = contrast;
	}
	public void setArea(Point dragStartScreen, Point dragEndScreen) {
		startPoint = dragStartScreen;
		endPoint = dragEndScreen;
	}
	public void setImageLabel(MyImageLabel imageLabel) {
		this.imageLabel = imageLabel;
		regionMarkLst = imageLabel.getList1();
		landMarkLst = imageLabel.getList2();
	}
	public void setTwoLabels (JLabel left, JLabel right) {
		leftImageLabel = left;
		rightImageLabel = right;
	}
	public void setLength(int sw, int sh) {
		this.sw = sw;
		this.sh = sh;
	}
	public void setPanelGroup(LeftGroupPanel left, CenterGroupPanel center, RightGroupPanel right, JFrame aquaWindow) {
		this.left = left;
		this.center = center;
		this.right = right;
		window = aquaWindow;
	}
	public void setWindow(JFrame window) {
		this.window = window;
	}
	public void setPage(int page) {
		imgPlus.setPosition(page+1);
	}
	public void setSignalProcessingParameters(float thrArscl, float sigma, int minSize) {
		opts.thrARScl = thrArscl;
		opts.smoXY = sigma;
		opts.minSize = minSize;
	}
	public void changeSignalDrawRegionStatus() {
		drawRegion = true;
	}
	public void changeStatus() {
		status = !status;
	}
	
	public Point getStartPoint() {
		return startPoint;
	}
	public Point getEndPoint() {
		return endPoint;
	}
	public double getWidth() {
		if(endPoint == null)
			return 500;
		else
			return endPoint.getX() - startPoint.getX();
	}
	public double getHeight() {
		return endPoint.getY() - startPoint.getY();
	}
	public double getOrigWidth() {
		return width;
	}
	public double getOrigHeight() {
		return height;
	}
	public MyImageLabel getImageLabel() {
		return imageLabel;
	}
	public int getPages() {
		return pages;
	}
	public MyImageLabel getLeftLabel() {
		return center.leftImageLabel;
	}
	public MyImageLabel getRightLabel() {
		return center.rightImageLabel;
	}
	
	public void reset() {
		min = 0;
		max = orgMax;
		contrast = DEFAULT_CONTRAST_BRIGHTNESS;
		startPoint = DEFAULT_STARTPOINT;
		endPoint = DEFAULT_ENDPOINT;
		right.minSlider.setValue(min);
		right.maxSlider.setValue(max);
		right.contrastSlider.setValue(contrast);
		dealImage();
	}	
	public void adjustPoint(BufferedImage origImage) {
		if(startPoint == null || endPoint==null) {
			startPoint = new Point(0,0);
			endPoint = new Point(origImage.getWidth(), origImage.getHeight());
		}
		
		int startX = (int)startPoint.getX();
		int startY = (int)startPoint.getY();
		int endX = (int)endPoint.getX();
		int endY = (int)endPoint.getY();
		
		if(startX == endX) {
			endX = startX + 1;
		}
		if(startY == endY) {
			endY = startY + 1;
		}
		if(startX < 0) {
			startX = 0;
		}
		if(startY < 0) {
			startY = 0;
		}
		if(endX > width) {
			endX = width;
		}
		if(endY > height) {
			endY = height;
		}
		
		int width = endX - startX;
		int height = endY - startY;
//		if(width < height) {
//			height = width;
//		}
//		if(width > height) {
//			width = height;
//		}
		endX = startX + width;
		endY = startY + height;
		
		startPoint.setLocation(startX, startY);
		endPoint.setLocation(endX, endY);
	}	
	public void dealImage() {
		if(status) {
			BufferedImage result = dealRaw();
			if(drawRegion)
				result = addColor(result);
			
			if(imageLabel!=null) {
				imageLabel.setIcon(new ImageIcon(result.getScaledInstance(maxImageWidth, maxImageHeight, Image.SCALE_DEFAULT)));
			}
		}else {
			BufferedImage leftResult = dealRaw();	// method will change
			BufferedImage rightResult = dealRaw();
			
			if(center.leftJCB.getSelectedIndex()==1)
				leftResult = addColor(leftResult);
			
			if(center.rightJCB.getSelectedIndex()==1)
				rightResult = addColor(rightResult);
			
			// Size
			leftImageLabel = center.leftImageLabel;
			rightImageLabel = center.rightImageLabel;

			leftImageLabel.setIcon(new ImageIcon(leftResult.getScaledInstance(sw, sh, Image.SCALE_DEFAULT)));
			rightImageLabel.setIcon(new ImageIcon(rightResult.getScaledInstance(sw, sh, Image.SCALE_DEFAULT)));
		}
	}
	private BufferedImage dealRaw() {
		BufferedImage origImage = imgPlus.getBufferedImage();
		adjustPoint(origImage);
		
		int w = width;
		int h = height;
		
		int startX = (int)(startPoint.getX());
		int startY = (int)(startPoint.getY());
		int endX = (int)(endPoint.getX());
		int endY = (int)(endPoint.getY());

//		System.out.println(startX + " " + startY + " " + endX + " " + endY);
		BufferedImage prod = new BufferedImage(endX - startX, endY - startY, BufferedImage.TYPE_3BYTE_BGR);

		for(int i=startX;i<endX;i++) {
			for(int j=startY;j<endY;j++) {
				float gray = imgProcessor.getf(i, j);
				gray = gray*gray*opts.maxValueDat;
				if (gray >= max) {
	                prod.setRGB(i - startX, j - startY, new Color(255, 255, 255, 255).getRGB());
				}else if(gray <= min) {
	                prod.setRGB(i - startX, j - startY, new Color(0, 0, 0, 255).getRGB());
	            }else {
	                gray = (float)((gray - min) * 255/(max - min));
	                prod.setRGB(i - startX, j - startY, new Color((int)gray, (int)gray, (int)gray, 255).getRGB());
				}
			}
		}
		
		float cont = (float)contrast / 100;
		RescaleOp rescaleOp = new RescaleOp(cont, 1.0f, null);
		BufferedImage result = rescaleOp.filter(prod, null);
		return result;
	}
	
	public void dealBuilderImageLabel() {
		int width = curBuilderImage.length;
		int height = curBuilderImage[0].length;
		BufferedImage prod = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				float gray = curBuilderImage[i][j];
				gray = gray*gray*opts.maxValueDat;
				if (gray >= max) {
	                prod.setRGB(i, j, new Color(255, 255, 255, 255).getRGB());
				}else if(gray <= min) {
	                prod.setRGB(i, j , new Color(0, 0, 0, 255).getRGB());
	            }else {
	                gray = (float)((gray - min) * 255/(max - min));
	                prod.setRGB(i - 0, j - 0, new Color((int)gray, (int)gray, (int)gray, 255).getRGB());
				}
			}
		}
		int sw = 0;
		int sh = 0;
		float scal = (float)width/height;
		if(700*scal >700) {
			sw = 700;
			sh = (int) (700/scal);
		}else {
			sw = (int) (700*scal);
			sh = 700;
		}
		

		builderImageLabel.setIcon(new ImageIcon(prod.getScaledInstance(sw, sh, Image.SCALE_DEFAULT)));
	}
	
	public BufferedImage addColor(BufferedImage curImg) {
		int w = width;
		int h = height;
		
//		int startX = (int)(startPoint.getX()*w/maxImageWidth);
//		int startY = (int)(startPoint.getY()*h/maxImageHeight);
//		int endX = (int)(endPoint.getX()*w/maxImageWidth);
//		int endY = (int)(endPoint.getY()*h/maxImageHeight);
		
		int startX = (int)(startPoint.getX());
		int startY = (int)(startPoint.getY());
		int endX = (int)(endPoint.getX());
		int endY = (int)(endPoint.getY());
		
		for(int i=startX;i<endX;i++) {
			for(int j=startY;j<endY;j++) {
				int k = imgPlus.getCurrentSlice()-1;
				if(label[i][j][k]!=0) {
					if(deleteColorSet.contains(label[i][j][k])){
						continue;
					}
					Color curColor = labelColors[label[i][j][k]-1];
					Color curImgColor = new Color(curImg.getRGB(i-startX, j-startY));
					float saturation = 1;
					if(datR!=null)
						saturation = (float)datR[i][j][k]/255;
					int red = (int)(curColor.getRed()*colorScale*saturation + curImgColor.getRed()*1.2);
					int green = (int)(curColor.getGreen()*colorScale*saturation + curImgColor.getGreen()*1.2);
					int blue = (int)(curColor.getBlue()*colorScale*saturation + curImgColor.getBlue()*1.2);
					red = red>255?255:red;
					green = green>255?255:green;
					blue = blue>255?255:blue;
					curImg.setRGB(i-startX, j-startY, new Color(red,green,blue,255).getRGB());
					
				}
			}
		}
		return curImg;
		
	}
	
	public void signalProcessing() {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	ProgressBarRealizedStep1 task = new ProgressBarRealizedStep1(dealer);
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
	}
	
	public void step2Start() {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	ProgressBarRealizedStep2 task = new ProgressBarRealizedStep2(dealer);
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
            }
        });
		
	}
	
	public void step3Start() {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	ProgressBarRealizedStep3 task = new ProgressBarRealizedStep3(dealer);
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
            }
        });
		
	}
	
	public void setStep2(float thrTWScl, float thrExtZ) {
		opts.thrTWScl = thrTWScl;
		opts.thrExtZ = thrExtZ;
		
	} 
	
	public void setStep3(int cRise, int cDelay, float gtwSmo) {
		opts.cRise = cRise;
		opts.cDelay = cDelay;
		opts.gtwSmo = gtwSmo;
		
	}
	public void step4Start() {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	ProgressBarRealizedStep4 task = new ProgressBarRealizedStep4(dealer);
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
            }
        });
		
	}
	public void setStep4(int zThr) {
		opts.zThr = zThr;
	}
	public void setStep7(boolean isChecked) {
		opts.ignoreTau = isChecked?1:0;
		
	}
	public void step7Start() {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	ProgressBarRealizedStep7 task = new ProgressBarRealizedStep7(dealer);
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
            }
        });
		
	}
	public void export(boolean eventsExtract, boolean movieExtract, String savePath) {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	Aqua_OutPut task = new Aqua_OutPut(dealer,eventsExtract,movieExtract,path, label, labelColors, savePath);
//                	task.doInBackground();
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
            }
        });
	}
	
	public void setCurveLabel(DrawCurveLabel resultsLabel) {
		curveLabel = resultsLabel;
		
	} 
	
	public void drawCurve(Point p) {
//		System.out.println(imgPlus.getCurrentSlice());
		int x = (int)p.getX();
		int y = (int)p.getY();
//		System.out.println(x + " " + y);
		int t = imgPlus.getCurrentSlice()-1;
		int nEvt = label[x][y][t];
		if(nEvt!=0) {
			featureTableList.add(nEvt);
//			System.out.println(featureTableList.size());
			dealImage();
			curveLabel.drawCurve(dffMat, nEvt,fts);
			int rowNumber = right.table.getRowCount();
			for(int r=0;r<rowNumber;r++) {
				int label = (Integer) right.table.getValueAt(r, 2);
				if(label==nEvt)
					return;
			}
			int frame = fts.curve.tBegin.get(nEvt);
			float size = fts.basic.area.get(nEvt);
			float duration = fts.curve.width55.get(nEvt);
			float dffMax = fts.curve.dffMax.get(nEvt);
			float tau = fts.curve.decayTau.get(nEvt); 
			right.model.addRow(new Object[] {new Integer(rowNumber+1),new Boolean(false),new Integer(nEvt),new Integer(frame),new Float(size),new Float(duration),new Float(dffMax),new Float(tau)});
		}
	}
	
	public void drawCurve(int nEvt) {
		if(nEvt!=0 && fts.curve.dffMax.get(nEvt)!=null) {
			curveLabel.drawCurve(dffMat, nEvt, fts);
		}
	}
	
	public void setColorConstrast(int contr) {
		colorScale = ((float)contr)/100;
		
	}
	public void addCurve(int nEvt) {
		if(nEvt!=0 && fts.curve.dffMax.get(nEvt)!=null) {
			curveLabel.drawCurve(dffMat, nEvt,fts);
			int rowNumber = right.table.getRowCount();
			for(int r=0;r<rowNumber;r++) {
				int label = (Integer) right.table.getValueAt(r, 2);
				if(label==nEvt)
					return;
			}
			
			
			int frame = fts.curve.tBegin.get(nEvt);
			center.imageSlider.setValue(frame+1);
			float size = fts.basic.area.get(nEvt);
			float duration = fts.curve.width55.get(nEvt);
			float dffMax = fts.curve.dffMax.get(nEvt);
			float tau = fts.curve.decayTau.get(nEvt); 
			right.model.addRow(new Object[] {new Integer(rowNumber+1),new Boolean(false),new Integer(nEvt),new Integer(frame+1),new Float(size),new Float(duration),new Float(dffMax),new Float(tau)});
		}
		
	}
	public void deleteShowEvent(Point p) {
		int x = (int)p.getX();
		int y = (int)p.getY();
		int t = imgPlus.getCurrentSlice()-1;
		int nEvt = label[x][y][t];
		if(deleteColorSet.contains(nEvt)) {
			deleteColorSet.remove(nEvt);
		}else
			deleteColorSet.add(nEvt);
		dealImage();
		
	}
	public void setBuilderImageLabel(MakeBuilderLabel builderImageLabel) {
		this.builderImageLabel = builderImageLabel;
	}
	
	public void load(String proPath) {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	LoadProject task = new LoadProject(dealer);
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
            }
        });
	}
	
	public void saveStatus() {
		int curStatus = left.curStatus;
		int jTPStatus = left.jTPStatus;
		
		
		try {
			FileOutputStream f = null;
			ObjectOutputStream o = null;
			
			f = new FileOutputStream(new File(proPath + "LabelColors.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(labelColors);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath + "Opts.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(opts);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath + "CurStatus.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(curStatus);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath + "JTPStatus.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(jTPStatus);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath + "Region.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(regionMark);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath + "RegionLabels.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(regionMarkLabel);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath + "LandMark.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(landMark);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath + "LandMarkLabels.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(landMarkLabel);
			o.close();
			f.close();
			
			f = new FileOutputStream(new File(proPath + "OrgPath.ser"));
			o = new ObjectOutputStream(f);
			o.writeObject(path);
			o.close();
			f.close();
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setImageConfig(float ts, float ss) {
		opts.spatialRes = ss;
		opts.frameRate = ts;
		center.ts = ts;
	}

	public void setStep5(int ignoreMerge, int mergeEventDiscon, int mergeEventCorr, int mergeEventMaxTimeDif) {
		opts.ignoreMerge = ignoreMerge;
		opts.mergeEventDiscon = mergeEventDiscon;
		opts.mergeEventCorr = mergeEventCorr;
		opts.mergeEventMaxTimeDif = mergeEventMaxTimeDif;
		
	}

	
	public void step5Start() {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	ProgressBarRealizedStep5 task = new ProgressBarRealizedStep5(dealer);
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
            }
        });
		
	}

	public void setStep6(int extendEvtRe) {
		opts.extendEvtRe = extendEvtRe;
		
	}

	public void step6Start() {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	ProgressBarRealizedStep6 task = new ProgressBarRealizedStep6(dealer);
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
            }
        });
		
	}
	
	
}
