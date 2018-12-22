package va.vt.cbil;

import java.io.File;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

public class BuilderTableItem {
	float[][] image = null;
	String maskName = "Project data";
	String type = null;
	ImageDealer imageDealer = null;
	
	public BuilderTableItem(float[][] image, String type, ImageDealer imageDealer) {
		this.image = copyImage(image);
		this.type = type;
		this.imageDealer = imageDealer;
		tableset();
		
		int maxAvg = 0;
		for(int x=0;x<image.length;x++) {
			for(int y=0;y<image[0].length;y++) {
				maxAvg = (int) Math.max(maxAvg, image[x][y]);
			}
		}
		imageDealer.maxAvg = maxAvg;
	}
	
	public BuilderTableItem(String path, String type, ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
		ImagePlus imgPlus = new ImagePlus(path);
		ImageConverter converter = new ImageConverter(imgPlus);
		converter.convertToGray8();
		int maxValueDat = (int) (Math.pow(2,8)-1);
		
		int width = imgPlus.getWidth();
		int height = imgPlus.getHeight();
		int pages = imgPlus.getImageStackSize();
		ImageStack stk = imgPlus.getStack().convertToFloat();
		imgPlus.setStack(stk);
		ImageProcessor imgProcessor = imgPlus.getProcessor();
		
		image = new float[width][height];
		for(int k = 1;k<=pages;k++) {
			imgPlus.setPosition(k);
			float[][] f = imgProcessor.getFloatArray();
			for(int i = 0;i<width;i++) {
				for(int j=0;j<height;j++) {
					f[i][j] = (float) Math.sqrt(f[i][j]/maxValueDat);
					image[i][j] += f[i][j]/pages;
				}
			}
		}
		int maxAvg = 0;
		for(int x=0;x<image.length;x++) {
			for(int y=0;y<image[0].length;y++) {
				maxAvg = (int) Math.max(maxAvg, image[x][y]);
			}
		}
		imageDealer.maxAvg = maxAvg;
		
		this.type = type;
		tableset();
	}

	public BuilderTableItem(String folderPath, File[] folder, String type, int width, int height, ImageDealer imageDealer) {
		image = new float[width][height];
		this.imageDealer = imageDealer;
		for(File file : folder) {
			String path = folderPath +"\\"+ file.getName(); 
			System.out.println("Read " + file.getName());
			ImagePlus imgPlus = new ImagePlus(path);
			ImageConverter converter = new ImageConverter(imgPlus);
			converter.convertToGray8();
			ImageStack stk = imgPlus.getStack().convertToFloat();
			imgPlus.setStack(stk);
			int maxValueDat = (int) (Math.pow(2,8)-1);
			ImageProcessor imgProcessor = imgPlus.getProcessor();
			int pages = imgPlus.getImageStackSize();
			for(int k = 1;k<=pages;k++) {
				imgPlus.setPosition(k);
				float[][] f = imgProcessor.getFloatArray();
				for(int i = 0;i<width;i++) {
					for(int j=0;j<height;j++) {
//						System.out.println(f[i][j]); 
						f[i][j] = (float) Math.sqrt(f[i][j]/maxValueDat);
						image[i][j] += f[i][j]/pages/folder.length;
					}
				}
			}
			
		}
		int maxAvg = 0;
		for(int x=0;x<image.length;x++) {
			for(int y=0;y<image[0].length;y++) {
				maxAvg = (int) Math.max(maxAvg, image[x][y]);
			}
		}
		imageDealer.maxAvg = maxAvg;
		
		this.type = type;
		tableset();
	}
	
	private void tableset() {
		// TODO Auto-generated method stub
		int count = imageDealer.left.builderTable.getRowCount() + 1;
		imageDealer.curBuilderImage = image;
		imageDealer.dealBuilderImageLabel();		
		for(int i=0;i<count-1;i++) {
			imageDealer.left.builderTableModel.setValueAt(new Boolean(false), i, 1);
		}
		imageDealer.left.builderTableModel.addRow(new Object[] {new Integer(count),new Boolean(true),maskName,type});
		imageDealer.left.intensityThreshold.add((int)(0.4*imageDealer.opts.maxValueDat)-1);
		imageDealer.left.minSize.add(0);
		imageDealer.left.maxSize.add((int) (imageDealer.getOrigHeight() * imageDealer.getOrigWidth()));
		imageDealer.right.intensitySlider.setValue((int)(0.4*imageDealer.opts.maxValueDat)-1);
		imageDealer.right.sizeMinSlider.setValue(0);
		imageDealer.right.sizeMaxSlider.setValue((int) (imageDealer.getOrigHeight() * imageDealer.getOrigWidth()));
		
	}

	private float[][] copyImage(float[][] image){
		int width = image.length;
		int height = image[0].length;
		float[][] result = new float[width][height];
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				result[i][j] = image[i][j];
			}
		}
		return result;
	}
}
