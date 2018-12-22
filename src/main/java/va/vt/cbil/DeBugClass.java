package va.vt.cbil;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;


public class DeBugClass {
	static Color[] labelColors = new Color[80000];
	static int adjustcolor = 50;
	Random rv = new Random();
	JFrame jf = new JFrame();
	JLabel jLabel = new JLabel();
	
	public DeBugClass() {
		for(int i=0;i<labelColors.length;i++) {
			labelColors[i] = new Color(rv.nextInt(256-adjustcolor),rv.nextInt(256-adjustcolor),rv.nextInt(256-adjustcolor));
		}
		jf.setSize(501,501);
		jf.setUndecorated(false);
		jf.setLocationRelativeTo(null);
		
		jf.add(jLabel);
		jf.setVisible(true);
	}
	
	public static void main(String[] args) {
		DeBugClass db = new DeBugClass();
		float[][] dlymap = new float[50][50];
		Random rv = new Random();
		for(int x=0;x<50;x++) {
			for(int y=0;y<50;y++) {
				
			}
		}
		db.labelShow(dlymap);
	}
	
	
	public void labelShow(int[][] labels) {
		int W = labels.length;
		int H = labels[0].length;
		
		
		
		
		BufferedImage prod = new BufferedImage(W, H, BufferedImage.TYPE_3BYTE_BGR);
		
		for(int i=0;i<labels.length;i++) {
			for(int j=0;j<labels[0].length;j++) {
				if(labels[i][j]>0) {
					Color curColor = labelColors[labels[i][j]-1];
					int red = (int)(curColor.getRed()) + adjustcolor;
					int green = (int)(curColor.getGreen()) + adjustcolor;
					int blue = (int)(curColor.getBlue()) + adjustcolor;
					red = red>255?255:red;
					green = green>255?255:green;
					blue = blue>255?255:blue;
					prod.setRGB(i, j, new Color(red,green,blue,255).getRGB());
				}
			}
		}
		
		ImageIcon image= new ImageIcon(prod.getScaledInstance(500, 500, Image.SCALE_DEFAULT));
		jLabel.setIcon(image);
		
	}
	
	public void labelShow(float[][] dlymap) {
		int W = dlymap.length;
		int H = dlymap[0].length;
		
		float max = 0;
		float min = Float.MAX_VALUE;
		for(int i=0;i<dlymap.length;i++) {
			for(int j=0;j<dlymap[0].length;j++) {
				if(dlymap[i][j]<Float.MAX_VALUE) {
					max = Math.max(max, dlymap[i][j]);
					min = Math.min(min, dlymap[i][j]);
				}
			}
		}
		
		System.out.println(min + " " + max);
		BufferedImage prod = new BufferedImage(W, H, BufferedImage.TYPE_3BYTE_BGR);
		
//		int cnt = 0;
//		HashMap<Float, Integer> table = new HashMap<>();
//		for(int x=0;x<dlymap.length;x++) {
//			for(int y=0;y<dlymap[0].length;y++) {
//				if(table.get(dlymap[x][y])==null) {
//					table.put(dlymap[x][y], cnt);
//					cnt++;
//				}
//			}
//		}
		
		for(int i=0;i<dlymap.length;i++) {
			for(int j=0;j<dlymap[0].length;j++) {
				
				float value = dlymap[i][j];
				if(value<Float.MAX_VALUE) {
				int gray = (int) ((value-min)*255/(max-min));
//				Color curColor = labelColors[table.get(dlymap[i][j])];
				int red = gray;
				int green = gray;
				int blue = 100;
				red = red>255?255:red;
				green = green>255?255:green;
				blue = blue>255?255:blue;
				prod.setRGB(i, j, new Color(red,green,blue,255).getRGB());
				}else {
					prod.setRGB(i, j, new Color(0,0,0,255).getRGB());
				}
			}
		}
		ImageIcon image= new ImageIcon(prod.getScaledInstance(W, H, Image.SCALE_DEFAULT));
		jLabel.setIcon(image);
	}
	
	public void labelShow(boolean[][] dlymap) {
		int W = dlymap.length;
		int H = dlymap[0].length;

		BufferedImage prod = new BufferedImage(W, H, BufferedImage.TYPE_3BYTE_BGR);
		
//		int cnt = 0;
//		HashMap<Float, Integer> table = new HashMap<>();
//		for(int x=0;x<dlymap.length;x++) {
//			for(int y=0;y<dlymap[0].length;y++) {
//				if(table.get(dlymap[x][y])==null) {
//					table.put(dlymap[x][y], cnt);
//					cnt++;
//				}
//			}
//		}
		
		for(int i=0;i<dlymap.length;i++) {
			for(int j=0;j<dlymap[0].length;j++) {
				int gray = dlymap[i][j]?255:0;
//				Color curColor = labelColors[table.get(dlymap[i][j])];
				int red = gray;
				int green = gray;
				int blue = gray;
				red = red>255?255:red;
				green = green>255?255:green;
				blue = blue>255?255:blue;
				prod.setRGB(i, j, new Color(red,green,blue,255).getRGB());
			}
		}
		ImageIcon image= new ImageIcon(prod.getScaledInstance(W, H, Image.SCALE_DEFAULT));
		jLabel.setIcon(image);
	}
	
	public static void saveGTWResult(GTWResult gtwResult) {
		try {
			FileOutputStream f = null;
			ObjectOutputStream o = null;
			
			// datR
			f = new FileOutputStream(new File("GTWResult.txt"));
			o = new ObjectOutputStream(f);
			o.writeObject(gtwResult);
			o.close();
			f.close();

			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static GTWResult readGTWResult() {
		GTWResult result = null;
		
		try {
			// data in step1
			FileInputStream fi1 = new FileInputStream(new File("GTWResult.txt"));
			ObjectInputStream oi1 = new ObjectInputStream(fi1);
			result = (GTWResult)oi1.readObject();
			oi1.close();
			fi1.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return result;
	}



	public void updateLabelAndShow(HashMap<Integer, ArrayList<int[]>> spLst, int[] spEvt) {
		int[][] labels = new int[501][501];
		for(int i=0;i<spEvt.length;i++) {
			if(spEvt[i]>0) {
				int label = i+1;
				ArrayList<int[]> points = spLst.get(label);
				for(int[] p:points) {
					labels[p[0]][p[1]] = spEvt[i];
				}
			}
		}
		
		labelShow(labels);
		
	}
}
