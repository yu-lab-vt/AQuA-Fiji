package va.vt.cbil;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

class PanListener implements MouseListener, MouseMotionListener, MouseWheelListener{
    
    private Point dragStartScreen;
    private Point location;
    private double width;
    private double height;
    
    private boolean valid = false;
    ImageDealer imageDealer = null;
    
    public void setValid(boolean valid) {
    	this.valid = valid;
    }
    
    public PanListener(ImageDealer imageDealer, Point location) {
    	this.imageDealer = imageDealer;
    	this.location = location;
    }
    
    
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
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
		if(valid) {
			Point p = e.getPoint();
			Point westNorth = imageDealer.getStartPoint();
			double dx = p.getX() - dragStartScreen.getX();
			double dy = p.getY() - dragStartScreen.getY();
			double x = westNorth.getX() - dx;
			double y = westNorth.getY() - dy;
			if(x<0) {
				x = 0;
			}
			if(y < 0) {
				y = 0;
			}
			if(x + width > imageDealer.getOrigWidth()) {
				x = imageDealer.getOrigWidth() - width;
			}
			if(y + height > imageDealer.getOrigHeight()) {
				y = imageDealer.getOrigHeight() - height;
			}
			Point start = new Point();
			start.setLocation(x, y);
			Point end = new Point();
			end.setLocation(x + width, y + height);
			dragStartScreen.setLocation(p.getX(), p.getY()); 
			imageDealer.setArea(start, end);
			imageDealer.dealImage();
		}
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
			width = imageDealer.getWidth();
			height = imageDealer.getHeight();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	
}
