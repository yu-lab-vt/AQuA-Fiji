package va.vt.cbil;
/**
 *  * Button Listener, add it to the add button on the leftPanel.
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JToggleButton;


class DrawListener implements MouseListener, MouseMotionListener {


	private Color color = new Color(128, 128, 255);
	private int width = 1;
	private ArrayList<Point> points = new ArrayList<Point>();
//	ArrayList<ArrayList<Point>> list = null;
	private Point startPoint = null;
	private Graphics2D g;
	
	private int maxLength = 500;
	private double radius = 5;
	private boolean valid = false;
	JComponent canvas = null;
	JToggleButton button = null;
	ImageDealer imageDealer = null;
	boolean[][] region = null;
	
	public DrawListener(JComponent canvas, JToggleButton button, ImageDealer imageDealer, boolean[][] region) {
		this.canvas = canvas;
		this.button = button;
//		this.list = list;
		this.imageDealer = imageDealer;
		this.region = region;
	}
	
	public void setRegion(boolean[][] region) {
		this.region = region;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public void clearPoints() {
		points = new ArrayList<Point>();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(valid) {
			Point curPoint = e.getPoint();
			points.add(curPoint);
			int pointSize = points.size();
			if(pointSize == 1) {
				startPoint = curPoint;
				g.fillOval((int)curPoint.getX()-2, (int)curPoint.getY()-2, 4, 4);
			}
			
			if(pointSize >= 2) {
				if(curPoint.distance(startPoint.getX(),startPoint.getY()) < radius) {
					curPoint = startPoint;
				}
				Point prePoint = points.get(pointSize-2);
				g.drawLine((int)prePoint.getX(), (int)prePoint.getY(), (int)curPoint.getX(), (int)curPoint.getY());
				g.fillOval((int)curPoint.getX()-2, (int)curPoint.getY()-2, 4, 4);
				
				if(curPoint == startPoint) {
					double length = imageDealer.getWidth();
					double tx = imageDealer.getStartPoint().getX();
					double ty = imageDealer.getStartPoint().getY();
					ArrayList<Point> affinePoints = new ArrayList<Point>();
					for(Point point:points) {
						Point affinePoint = new Point();
						double x = point.getX()*length/maxLength + tx;
						double y = point.getY()*length/maxLength + ty;
						affinePoint.setLocation(x, y);
						affinePoints.add(affinePoint);
					}
//					list.add(affinePoints);
					int minX = Integer.MAX_VALUE;
					int minY = Integer.MAX_VALUE;
					int maxX = Integer.MIN_VALUE;
					int maxY = Integer.MIN_VALUE;
					for(Point p:points) {
						minX = (int) Math.min(minX, p.getX());
						minY = (int) Math.min(minY, p.getY());
						maxX = (int) Math.max(maxX, p.getX());
						maxY = (int) Math.max(maxY, p.getY());
					}
					
					for(int i=minX;i<=maxX;i++) {
						for(int j=minY;j<=maxY;j++) {
							if(!region[i][j]&&judgePointInPolygon(i,j,points))
								region[i][j] = true;
						}
					}
					ConnectedComponents.twoPassConnect2D_ForBuilder(region,4);
					System.out.println(ConnectedComponents.twoPassConnect2D_ForBuilder(region,4).size());
					points = new ArrayList<Point>();
					startPoint = null;
					valid = false;
					button.setSelected(false);
				}
			}

		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(valid && g == null) {
			g = (Graphics2D) canvas.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(color);
			g.setStroke(new BasicStroke(width));
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean judgePointInPolygon(int xx, int yy, ArrayList<Point> list) {
		boolean result = false;
		
		int number = list.size();
		double px = xx;
		double py = yy;
		
		for(int i = 0,j = number-1;i<number;j=i,i++) {
			double sx = list.get(i).getX();
			double sy = list.get(i).getY();
			double tx = list.get(j).getX();
			double ty = list.get(j).getY();
			
			if((sx == px && sy == py)||(tx==px && ty == py)) {
				return true;
			}
			
			if((sy < py && ty >= py) || (sy >= py && ty < py)) {
				double x = sx + (py - sy) * (tx - sx) / (ty - sy);
				if(x == px)
					return true;
				if(x > px)
					result = !result;
			}
		}	
		return result;	
	}
}
