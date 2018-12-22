package va.vt.cbil;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

import javax.swing.JLabel;

public class ColorLabel extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	Color cStart = null;
	Color cMid = null;
	Color cEnd = null;
	int min = 0;
	int max = 0;
	public ColorLabel() {
		Random rv = new Random();
		cStart = new Color(rv.nextInt(256),rv.nextInt(256),rv.nextInt(256));
		cMid = new Color(rv.nextInt(256),rv.nextInt(256),rv.nextInt(256));
		cEnd = new Color(rv.nextInt(256),rv.nextInt(256),rv.nextInt(256));
		min = 0;
		max = 800;
	}
	
	public void setColorLabel(Color cStart, Color cMid, Color cEnd, int min, int max) {
		this.cStart = cStart;
		this.cMid = cMid;
		this.cEnd = cEnd;
		this.min = min;
		this.max = max;
		repaint();
	}
	
	
	@Override
	public void paint(Graphics gr) {
		super.paint(gr);
		Graphics2D g = (Graphics2D) gr;
		
		int width = this.getWidth();
		int height = this.getHeight();
		
		
		int dR = cMid.getRed() - cStart.getRed();
		int dG = cMid.getGreen() - cStart.getGreen();
		int dB = cMid.getBlue() - cStart.getBlue();
		
		int dR2 = cEnd.getRed() - cMid.getRed();
		int dG2= cEnd.getGreen() - cMid.getGreen();
		int dB2 = cEnd.getBlue() - cMid.getBlue();
		
		for(int i=0;i<min;i++) {
			g.setColor(cStart);
			g.drawLine(i, 0, i, height);
		}
		
		for(int i=max;i<width;i++) {
			g.setColor(cEnd);
			g.drawLine(i, 0, i, height);
		}
		
		if(min>=max)
			return;
		
		int mid = (min + max)/2;
		
		for(int i=min;i<mid;i++) {
			Color curColor = new Color(cStart.getRed() + (i-min)*dR/(mid - min),cStart.getGreen() + (i-min)*dG/(mid - min), cStart.getBlue() + (i-min)*dB/(mid - min));	
			g.setColor(curColor);
			g.drawLine(i, 0, i, height);
		}
		
		for(int i=mid;i<width;i++) {
			Color curColor = new Color(cMid.getRed() + (i-mid)*dR2/(width - mid),cMid.getGreen() + (i-mid)*dG2/(width - mid), cMid.getBlue() + (i-mid)*dB2/(width - mid));	
			g.setColor(curColor);
			g.drawLine(i, 0, i, height);
		}
		
		
	}
	
	
	
}
