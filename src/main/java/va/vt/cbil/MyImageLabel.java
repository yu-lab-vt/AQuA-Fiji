package va.vt.cbil;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import javax.swing.JLabel;

public class MyImageLabel extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8045037061621519253L;
	private ImageDealer imageDealer = null;
	
	ArrayList<ArrayList<Point>> list1 = null;
	ArrayList<ArrayList<Point>> list2 = null;
	private Color color1 = new Color(0,191,255);
	private Color color1S = new Color(0,230,230);
	private Color color2 = new Color(255,185,15);
	
	private int width = 1;
	Graphics2D g = null;
	
	private Point startPoint = null;
	private Point endPoint = null;
	private int maxImageWidth = 0;
	private int maxImageHeight = 0;
	
	
	private Point startAnterior = null;
	private Point endAnterior = null;
	private boolean anteriorRelease = false;
	
	private boolean valid1 = false;
	private boolean valid2 = false;
	
	boolean drawBorder = true;
	
	public void setDrawBorder(boolean a) {
		drawBorder = a;
	}
	
	public MyImageLabel() {
		list1 = new ArrayList<ArrayList<Point>>();
		list2 = new ArrayList<ArrayList<Point>>();
	}
	
	public void setPointList(ArrayList<ArrayList<Point>> list1, ArrayList<ArrayList<Point>> list2) {
		this.list1 = list1;
		this.list2 = list2;
	}
	
	public void setValid1(boolean valid) {
		valid1 = valid;
	}
	
	public void setValid2(boolean valid) {
		valid2 = valid;
	}
	
	public void setStartAnterior(Point p) {
		startAnterior = p;
	}
	
	public void setAneriorStatus(boolean valid) {
		anteriorRelease = valid;
	}
	
	public void setEndAnterior(Point p) {
		endAnterior = p;
	}
	
	public void setImageDealer(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
	}
	
	public void setMaxSize(int width,int height) {
		maxImageWidth = width;
		maxImageHeight = height;
	}
	
	public ArrayList<ArrayList<Point>> getPointList1() {
		return list1;
	}
	
	public ArrayList<ArrayList<Point>> getPointList2() {
		return list2;
	}
	
	public ArrayList<ArrayList<Point>> getList1(){
		return list1;
	}
	
	public ArrayList<ArrayList<Point>> getList2(){
		return list2;
	}
	
	@Override
	public void paint(Graphics gr) {
		super.paint(gr);
		if(gr!=null) {
			draw(gr);
			drawAnterior(gr);
		}
		
	}
	
	private void drawAnterior(Graphics gr) {
		if(startAnterior!=null) {
			if(!anteriorRelease) {
				g = (Graphics2D) gr;
				final AffineTransform affineTransform = new AffineTransform();
				g.setTransform(affineTransform);
				startPoint = imageDealer.getStartPoint();
				endPoint = imageDealer.getEndPoint();
				double tx = startPoint.getX();
				double ty = startPoint.getY();
				
				double sx = maxImageWidth/(endPoint.getX() - startPoint.getX());
				double sy = maxImageHeight/(endPoint.getY() - startPoint.getY());
				g.scale(sx, sy);
				g.translate(-tx, -ty);
				
		
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(color1);
				g.setStroke(new BasicStroke(width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,0,new float[] {16,4},0));
				
				g.fillOval((int)startAnterior.getX()-2, (int)startAnterior.getY()-2, 4, 4);
	
				g.drawLine((int)startAnterior.getX(), (int)startAnterior.getY(), (int)endAnterior.getX(), (int)endAnterior.getY());
				
				Polygon polygon = new Polygon();
				polygon.addPoint((int)endAnterior.getX(), (int)endAnterior.getY());
				
				int dx = (int) (endAnterior.getX() - startAnterior.getX());
				int dy = (int) (endAnterior.getY() - startAnterior.getY());
				
				double H = 10;
				double L = 4;
				double awrad = Math.atan(L/H);
				double arraow_len = Math.sqrt(L*L + H*H);
	
				double[] v1 = rotateVec(dx,dy,awrad,arraow_len);
				double[] v2 = rotateVec(dx,dy,-awrad,arraow_len);
				int x1 = (int)(endAnterior.getX() - v1[0]);
				int y1 = (int)(endAnterior.getY() - v1[1]);
				int x2 = (int)(endAnterior.getX() - v2[0]);
				int y2 = (int)(endAnterior.getY() - v2[1]);
				
				polygon.addPoint(x1, y1);
				polygon.addPoint(x2, y2);
				
				g.fillPolygon(polygon);
			}else {
				g = (Graphics2D) gr;
				final AffineTransform affineTransform = new AffineTransform();
				g.setTransform(affineTransform);
				startPoint = imageDealer.getStartPoint();
				endPoint = imageDealer.getEndPoint();
				double tx = startPoint.getX();
				double ty = startPoint.getY();
				
				double sx = maxImageWidth/(endPoint.getX() - startPoint.getX());
				double sy = maxImageHeight/(endPoint.getY() - startPoint.getY());
				g.scale(sx, sy);
				g.translate(-tx, -ty);
				
		
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(color1);
				g.setStroke(new BasicStroke(width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,0,new float[] {16,4},0));
				
				int width = this.getWidth();
				int height = this.getHeight();
				int centerX = width/2;
				int centerY = height/2;
				
				g.fillOval(centerX-2, centerY-2, 4, 4);
				
				int dx = (int) (endAnterior.getX() - startAnterior.getX());
				int dy = (int) (endAnterior.getY() - startAnterior.getY());
				float length = (float)Math.sqrt(dx*dx+dy*dy);
				g.drawLine(centerX, centerY, centerX+dx, centerY + dy);
				
				Polygon polygon = new Polygon();
				polygon.addPoint(centerX+dx, centerY+dy);
				
				double H = 10;
				double L = 4;
				double awrad = Math.atan(L/H);
				double arraow_len = Math.sqrt(L*L + H*H);
	
				double[] v1 = rotateVec(dx,dy,awrad,arraow_len);
				double[] v2 = rotateVec(dx,dy,-awrad,arraow_len);
				int x1 = (int)(centerX+dx - v1[0]);
				int y1 = (int)(centerY+dy - v1[1]);
				int x2 = (int)(centerX+dx - v2[0]);
				int y2 = (int)(centerY+dy - v2[1]);
				
				polygon.addPoint(x1, y1);
				polygon.addPoint(x2, y2);
				
				g.fillPolygon(polygon);
				
				g.drawString("North", centerX+dx + 20, centerY+dy + 20);
				
				
				float northx = dx/length;
				float northy = -dy/length;
				
				imageDealer.opts.northx = northx;
				imageDealer.opts.northy = northy;
				
//				System.out.println(northx + " " + northy);
			}
		}
	}
	
	private double[] rotateVec(int px,int py,double ang, double newLen) {
		double vx = px * Math.cos(ang) - py * Math.sin(ang);
		double vy = px * Math.sin(ang) + py * Math.cos(ang);
		double d = Math.sqrt(vx*vx + vy*vy);
		vx = vx/d*newLen;
		vy = vy/d*newLen;
		return new double[] {vx,vy};
		
	}
	
	
	private void draw(Graphics gr) {
		g = (Graphics2D) gr;
		
		// AffineTransform
		final AffineTransform affineTransform = new AffineTransform();
		g.setTransform(affineTransform);
		startPoint = imageDealer.getStartPoint();
		endPoint = imageDealer.getEndPoint();
		if(startPoint==null || endPoint==null) {
			startPoint = new Point();
			startPoint.setLocation(0, 0);
			endPoint = new Point();
			endPoint.setLocation(imageDealer.width,imageDealer.height);
		}
		double tx = startPoint.getX();
		double ty = startPoint.getY();
		
		double sx = maxImageWidth/(endPoint.getX() - startPoint.getX());
		double sy = maxImageHeight/(endPoint.getY() - startPoint.getY());
		g.scale(sx, sy);
		g.translate(-tx, -ty);
		
//		System.out.println(tx + " " + ty);
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(color1);
		g.setStroke(new BasicStroke(width));
		if(valid1)
			drawPolyhydron(g,list1);
		else
			drawRegionMark(g,imageDealer.regionMarkLabel);
		
		g.setColor(color2);
		if(valid2)
			drawPolyhydron(g,list2);
		else
			drawLandMark(g,imageDealer.landMark,imageDealer.landMarkLabel);
		
		HashSet<Integer> events = imageDealer.featureTableList;
//		System.out.println(events.size());
		if(drawBorder) {
			for(int label:events) {
				drawEventBorder(g,label);
			}
		}
	}
	
	private void drawEventBorder(Graphics2D g2, int label) {
		int t = imageDealer.imgPlus.getCurrentSlice()-1;
		int ts = imageDealer.fts.curve.tBegin.get(label);
		int te = imageDealer.fts.curve.tEnd.get(label);
		if(t < ts||t > te)
			return;
		ArrayList<int[]> boundary = imageDealer.fts.border.get(label);
		g.setColor(new Color(237,217,25));

		int x = boundary.get(0)[0];
		int y = boundary.get(0)[1];
		for(int i=1;i<boundary.size();i++) {			
			g.drawLine(boundary.get(i-1)[0], boundary.get(i-1)[1], boundary.get(i)[0], boundary.get(i)[1]);
			x += boundary.get(i)[0];
			y += boundary.get(i)[1];
		}
		
		x /= boundary.size();
		y /= boundary.size();
		g.drawString("" + label, x, y);
		
		
	}

	private void drawRegionMark(Graphics2D g, int[][] regionMarkLabel) {
		HashMap<Integer,String> nameLst = imageDealer.nameLst;
		
		int width = regionMarkLabel.length;
		int height = regionMarkLabel[0].length;
		HashMap<Integer, ArrayList<int[]>> connectedMap = new HashMap<>();
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				if(regionMarkLabel[x][y]!=0) {
//					System.out.println(x + " " + y);
					int label = regionMarkLabel[x][y];
					ArrayList<int[]> l = connectedMap.get(label);
					if(l==null)
						l = new ArrayList<>();
					l.add(new int[] {x,y});
					connectedMap.put(label, l);
				}
			}
		}
		
		int cnt = 0;
		Color curColor = g.getColor();
		g.setColor(color1S);
		HashMap<Integer, ArrayList<int[]>> borderMap = new HashMap<>();
		for(Entry<Integer, ArrayList<int[]>> entry:connectedMap.entrySet()) {
			ArrayList<int[]> points = entry.getValue();
			int label = entry.getKey();
			int minX = Integer.MAX_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int maxY = Integer.MIN_VALUE;
			int sumX = 0;
			int sumY = 0;
			for(int[] p:points) {
				minX = Math.min(minX, p[0]);
				minY = Math.min(minY, p[1]);
				maxX = Math.max(maxX, p[0]);
				maxY = Math.max(maxY, p[1]);
				sumX += p[0];
				sumY += p[1];
			}
			int centerX = sumX/points.size();
			int centerY = sumY/points.size();
//			System.out.println(centerX + " " + centerY);
			
			g.drawString(nameLst.get(label), centerX-5, centerY-5);
			
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
		
		g.setColor(curColor);
		for(Entry<Integer,ArrayList<int[]>> entry:borderMap.entrySet()) {
			ArrayList<int[]> points = entry.getValue();
			for(int i=1;i<points.size();i++) {
				
				g.drawLine(points.get(i-1)[0], points.get(i-1)[1], points.get(i)[0], points.get(i)[1]);
			}
		}
		
		
	}

	private void drawPolyhydron(Graphics2D g, ArrayList<ArrayList<Point>> list) {
		for(ArrayList<Point> points:list) {
			for(int i=0;i<points.size();i++) {
				g.fillOval((int)points.get(i).getX()-2, (int)points.get(i).getY()-2, 4, 4);
				if(i==0) {
					g.drawLine((int)points.get(points.size()-1).getX(), (int)points.get(points.size()-1).getY(), (int)points.get(i).getX(), (int)points.get(i).getY());
				}else {
					g.drawLine((int)points.get(i-1).getX(), (int)points.get(i-1).getY(), (int)points.get(i).getX(), (int)points.get(i).getY());
				}
			}
		}
	}

	private void drawLandMark(Graphics2D g, boolean[][] region, int[][] regionLabel) {
		HashMap<Integer, ArrayList<int[]>> connectedMap = new HashMap<>();
		HashMap<Integer,String> nameLst = imageDealer.nameLstLandMark;
		int W = region.length;
		int H = region[0].length;
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				int label = regionLabel[x][y];
				if(label>0) {
					ArrayList<int[]> pix = connectedMap.get(label);
					if(pix==null) {
						pix = new ArrayList<>();
					}
					pix.add(new int[] {x,y});
					connectedMap.put(label, pix);
				}
			}
		}
		
		
		Color curColor = g.getColor();
		HashMap<Integer, ArrayList<int[]>> borderMap = new HashMap<>();
		for(Entry<Integer, ArrayList<int[]>> entry:connectedMap.entrySet()) {
			ArrayList<int[]> points = entry.getValue();
			int label = entry.getKey();
			int minX = Integer.MAX_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int maxY = Integer.MIN_VALUE;
			int sumX = 0;
			int sumY = 0;
			for(int[] p:points) {
				minX = Math.min(minX, p[0]);
				minY = Math.min(minY, p[1]);
				maxX = Math.max(maxX, p[0]);
				maxY = Math.max(maxY, p[1]);
				sumX += p[0];
				sumY += p[1];
				regionLabel[p[0]][p[1]] = label;
			}
			int centerX = sumX/points.size();
			int centerY = sumY/points.size();
			g.drawString(nameLst.get(label), centerX-5, centerY-5);
			
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
			borderMap.put(label, border);
		}
		
		g.setColor(curColor);
		for(Entry<Integer,ArrayList<int[]>> entry:borderMap.entrySet()) {
			ArrayList<int[]> points = entry.getValue();
			for(int i=1;i<points.size();i++) {
				
				g.drawLine(points.get(i-1)[0], points.get(i-1)[1], points.get(i)[0], points.get(i)[1]);
			}
		}
		
		
	}



}
