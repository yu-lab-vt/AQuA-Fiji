package va.vt.cbil;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class ColorLabel2 extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	boolean drawColor = false;
	
	@Override
	public void paint(Graphics gr) {
		super.paint(gr);
		Graphics2D g = (Graphics2D) gr;
		
		if(drawColor) {
		int width = this.getWidth();
		int height = 5;//this.getHeight();
//		System.out.println(height + "");
		
		int red = 0;
		int green = 0;
		int blue = 0;
		for(int i=0;i<Math.round(width*0.125f);i++) {
			float v = (float)i/width;
			red = 0;
			green = 0;
			blue = 127 + Math.round(1020*v);
			Color curColor = new Color(red,green,blue);	
			g.setColor(curColor);
			g.drawLine(i, 0, i, height);
		}
		
		for(int i= Math.round(width*0.125f);i<Math.round(width*0.375f);i++) {
			float v = (float)i/width;
			red = 0;
			green = Math.max(Math.min(Math.round((v-0.125f)*1020),255),0);
			blue = 255;
			Color curColor = new Color(red,green,blue);	
			g.setColor(curColor);
			g.drawLine(i, 0, i, height);
		}
		
		for(int i= Math.round(width*0.375f);i<Math.round(width*0.625f);i++) {
			float v = (float)i/width;
			red = Math.max(Math.min(Math.round((v-0.375f)*1020),255),0);
			green = 255;
			blue = Math.max(Math.min(Math.round((0.625f-v)*1020),255),0);
			Color curColor = new Color(red,green,blue);	
			g.setColor(curColor);
			g.drawLine(i, 0, i, height);
		}
		
		for(int i= Math.round(width*0.625f);i<Math.round(width*0.875f);i++) {
			float v = (float)i/width;
			red = 255;
			green = Math.min(Math.round((0.875f-v)*1020),255);
			blue = 0;
			Color curColor = new Color(red,green,blue);	
			g.setColor(curColor);
			g.drawLine(i, 0, i, height);
		}
		
		for(int i= Math.round(width*0.875f);i<width;i++) {
			float v = (float)i/width;
			red = Math.min(127 + Math.round(1020*(1-v)),255);
			green = 0;
			blue = 0;
			Color curColor = new Color(red,green,blue);	
			g.setColor(curColor);
			g.drawLine(i, 0, i, height);
		}
		
		
//		g.setColor(Color.WHITE);
//		g.drawString("15.0", 0, 0);
		
		}
		
		
	}
	
	public static void main(String[] args) {
		JFrame a = new JFrame();
		a.setVisible(true);
		ColorLabel2 b = new ColorLabel2();
		a.add(b);
	}
	
	
	
}
