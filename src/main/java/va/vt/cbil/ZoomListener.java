package va.vt.cbil;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

class ZoomListener implements MouseListener, MouseMotionListener, MouseWheelListener{
	public static final int DEFAULT_MIN_ZOOM_LEVEL = 0;
    public static final int DEFAULT_MAX_ZOOM_LEVEL = 20;
    public static final double DEFAULT_ZOOM_MULTIPLICATION_FACTOR = 1.2;
    
    private int zoomLevel = 0;
    private int minZoomLevel = DEFAULT_MIN_ZOOM_LEVEL;
    private int maxZoomLevel = DEFAULT_MAX_ZOOM_LEVEL;
    private double zoomFactor = DEFAULT_ZOOM_MULTIPLICATION_FACTOR;
    
    ImageDealer imageDealer = null;
    
    private Point dragStartScreen;
    private Point dragEndScreen;
    private Point location;
    
    private boolean valid = false;
    
    public void setValid(boolean valid) {
    	this.valid = valid;
    }
    
    public ZoomListener(ImageDealer imageDealer, Point location) {
    	this.imageDealer = imageDealer;
    	this.location = location;
    }
    
    
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		if(valid) {
			int wheelRotation = e.getWheelRotation();
			Point start = imageDealer.getStartPoint();
			Point end = imageDealer.getEndPoint();
			double dx = end.getX() - start.getX();
			double dy = end.getY() - start.getY();
			Point p = e.getPoint();
			p = transformPoint(p);
			p = transformPoint2(p,dx,dy);
			if(wheelRotation<0) {
				if(zoomLevel < maxZoomLevel) {
					zoomLevel ++;
					start.setLocation(p.getX() - dx/2/zoomFactor, p.getY() - dy/2/zoomFactor);
					end.setLocation(p.getX() + dx/2/zoomFactor, p.getY() + dy/2/zoomFactor);
					imageDealer.setArea(start, end);
					imageDealer.dealImage();
				}
			}else {
				if(zoomLevel > minZoomLevel) {
					zoomLevel --;
					start.setLocation(p.getX() - dx/2*zoomFactor, p.getY() - dy/2*zoomFactor);
					end.setLocation(p.getX() + dx/2*zoomFactor, p.getY() + dy/2*zoomFactor);
					imageDealer.setArea(start, end);
					imageDealer.dealImage();
				}else if(zoomLevel == minZoomLevel) {
					start = null;
					end = null;
					imageDealer.setArea(start, end);
					imageDealer.dealImage();
				}
			}
		}
	}
	public Point transformPoint2(Point p, double dx, double dy) {
		double resultX = imageDealer.getStartPoint().getX() + p.getX()*dx/500;
		double resultY = imageDealer.getStartPoint().getY() + p.getY()*dy/500;
		Point result = new Point();
		result.setLocation(resultX, resultY);
		return result;
	}
	
	public Point transformPoint(Point p) {
		double resultX = p.getX() - location.getX();
		double resultY = p.getY() - location.getY();
		Point result = new Point();
		result.setLocation(resultX, resultY);
		return result;
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
//		dragEndScreen = e.getPoint();
//		imageDealer.setArea(transformPoint(dragStartScreen), transformPoint(dragEndScreen));
//		imageDealer.dealImage();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if(valid) {
			dragStartScreen = e.getPoint();
			dragEndScreen = null;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if(valid) {
			dragEndScreen = e.getPoint();
			imageDealer.setArea(transformPoint(dragStartScreen), transformPoint(dragEndScreen));
			imageDealer.dealImage();
		}
	}
	
}
