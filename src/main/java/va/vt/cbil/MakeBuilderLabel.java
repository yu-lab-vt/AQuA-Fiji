package va.vt.cbil;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JLabel;

public class MakeBuilderLabel extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8045037061621519253L;
	private ImageDealer imageDealer = null;
	private int lineWidth = 1;
	Graphics2D g = null;

	float[][] curImage = null;
	HashMap<Integer, ArrayList<int[]>> borderMap = null;
	
	int width = 0;
	int height = 0;
	
	public MakeBuilderLabel(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
		curImage = imageDealer.curBuilderImage;
		
		width = curImage.length;
		height = curImage[0].length;
	}
	
	public void getComponentBorder() {
		borderMap = new HashMap<>();
		boolean[][] thresholdMap = new boolean[width][height];
		float threshold = imageDealer.right.intensitySlider.getValue();
		threshold = (float) Math.sqrt(threshold/imageDealer.opts.maxValueDat);

		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++)
				if(curImage[i][j]>=threshold)
					thresholdMap[i][j] = true;
		}
		
		
		HashMap<Integer, ArrayList<int[]>> connectedMap = ConnectedComponents.twoPassConnect2D(thresholdMap);
		
		
		
		int minSize = imageDealer.right.sizeMinSlider.getValue();
		int maxSize = imageDealer.right.sizeMaxSlider.getValue();
		int max = imageDealer.right.sizeMaxSlider.getMaximum();
		int bit = (int) (Math.log10(max)/Math.log10(2))+1;
		
		minSize = (int) Math.pow(2,((double)minSize*bit)/max);
		maxSize = (int) Math.pow(2,((double)maxSize*bit)/max);
		
		
		int cnt = 0;
		for(Entry<Integer, ArrayList<int[]>> entry:connectedMap.entrySet()) {
			ArrayList<int[]> points = entry.getValue();
			
			if(points.size()<minSize || points.size()>maxSize)
				continue;
			int minX = Integer.MAX_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int maxY = Integer.MIN_VALUE;
			for(int[] p:points) {
				minX = Math.min(minX, p[0]);
				minY = Math.min(minY, p[1]);
				maxX = Math.max(maxX, p[0]);
				maxY = Math.max(maxY, p[1]);
			}
			
			
			boolean[][] smallMap = new boolean[maxX-minX+1][maxY-minY+1];
			for(int[] p:points) {
				smallMap[p[0]-minX][p[1]-minY] = true;
			}
			int x = 0;
			int y = 0;
			
			while(!smallMap[x][y]) {
				x++;
			}
			ArrayList<int[]> boundary = new ArrayList<>();
			BasicFeatureDealer.findBoundary(smallMap,x,y,boundary,1);
			
			ArrayList<int[]> border = new ArrayList<>();
			for(int[] p:boundary) {
				border.add(new int[] {p[0]+minX, p[1]+minY});
			}
			cnt++;
			borderMap.put(cnt, border);
		}
		
//		System.out.println(borderMap.size());
				
		
		repaint();
	}
	
	
	@Override
	public void paint(Graphics gr) {
		super.paint(gr);
		drawBorder(gr);
	}
	
	private void drawBorder(Graphics gr) {
		if(borderMap!=null) {	
			g = (Graphics2D) gr;

			final AffineTransform affineTransform = new AffineTransform();
			g.setTransform(affineTransform);
//			double maxLength = imageDealer.maxBuilderlength;
			double sx = imageDealer.maxBuilderWidth/imageDealer.getOrigWidth();
			double sy = imageDealer.maxBuilderHeight/imageDealer.getOrigHeight();
			g.scale(sx, sy);
			
			Color color = UI_Beauty.red;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(color);
			g.setStroke(new BasicStroke(lineWidth));
			
			for(Entry<Integer,ArrayList<int[]>> entry:borderMap.entrySet()) {
				ArrayList<int[]> points = entry.getValue();
				for(int i=1;i<points.size();i++) {
					
					g.drawLine(points.get(i-1)[0], points.get(i-1)[1], points.get(i)[0], points.get(i)[1]);
				}
			}
			
		}
	}
}
