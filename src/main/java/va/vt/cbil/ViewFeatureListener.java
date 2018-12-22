package va.vt.cbil;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ViewFeatureListener implements MouseListener {
    ImageDealer imageDealer = null;
    private Point location;
    private boolean valid = false;
    
	public ViewFeatureListener(ImageDealer imageDealer, Point location) {
		this.imageDealer = imageDealer;
		this.location = location;
	}
	
	public void setValid(boolean valid) {
    	this.valid = valid;
    }
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(valid) {
			Point p = e.getPoint();
			p = transformPoint(p);
			Point start = imageDealer.getStartPoint();
			Point end = imageDealer.getEndPoint();
			double dx = end.getX() - start.getX();
			double dy = end.getY() - start.getY();
//			System.out.println(p.getX() + " " + p.getY());
//			System.out.println(dx + " " + dy);
			p  = transformPoint2(p,dx,dy);
			
			imageDealer.drawCurve(p);
		}
	}

	public Point transformPoint2(Point p, double dx, double dy) {
		double resultX = imageDealer.getStartPoint().getX() + p.getX()*dx/imageDealer.maxImageWidth;
		double resultY = imageDealer.getStartPoint().getY() + p.getY()*dy/imageDealer.maxImageHeight;
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
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
