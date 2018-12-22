package va.vt.cbil;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;

public class DrawCurveLabel extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final int lineWidth = 1;
	Graphics2D g = null;
	float[] curve1 = null;
	float[] curve2 = null;
	int tBegin = 0;
	int tEnd = 0;
	float dffMax = 0;
	int nEvt = 0;
	public void drawCurve(float[][][] dffMat, int nEvt, FtsLst fts) {
		
		int T = dffMat[0].length;
		
		float min = Float.MAX_VALUE;
		float max = -Float.MAX_VALUE;
		for(int t = 0;t<T;t++) {
			min = Math.min(min, dffMat[nEvt-1][t][0]);
			min = Math.min(min, dffMat[nEvt-1][t][1]);
			max = Math.max(max, dffMat[nEvt-1][t][0]);
			max = Math.max(max, dffMat[nEvt-1][t][1]);
		}	
		curve1 = new float[dffMat[0].length];
		curve2 = new float[dffMat[0].length];
		
		for(int t = 0;t<T;t++) {
			curve1[t] = (dffMat[nEvt-1][t][0] - min)/(max-min);
			curve2[t] = (dffMat[nEvt-1][t][1] - min)/(max-min);
		}
		
		tBegin = fts.curve.tBegin.get(nEvt);
		tEnd = fts.curve.tEnd.get(nEvt);
		dffMax = fts.curve.dffMax.get(nEvt);
		this.nEvt = nEvt;
		repaint();
	}
	
	@Override
	public void paint(Graphics gr) {
		super.paint(gr);
		
		if(curve1!=null) {
			g = (Graphics2D) gr;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(Color.gray);
			g.setStroke(new BasicStroke(lineWidth));
			
			int width = this.getWidth();
			int height = this.getHeight();
			int T = curve1.length;
			
			for(int t=1;t<T;t++) {
				g.drawLine((t-1)*width/T, (int)((1-curve1[t-1])*height), t*width/T, (int)((1-curve1[t])*height));
			}
			
			g.setColor(Color.blue);
			for(int t=1;t<T;t++) {
				g.drawLine((t-1)*width/T, (int)((1-curve2[t-1])*height), t*width/T, (int)((1-curve2[t])*height));
			}
			
			g.setColor(Color.red);
			for(int t=tBegin+1;t<tEnd+1;t++) {
				g.drawLine((t-1)*width/T, (int)((1-curve1[t-1])*height), t*width/T, (int)((1-curve1[t])*height));
			}
			
			int x = (tBegin+tEnd)/2*width/T;
			if(x+100>width)
				x = width-100;
			
			g.drawString(nEvt + " dff: " + dffMax, x, 20);
		}
	}
	
}
