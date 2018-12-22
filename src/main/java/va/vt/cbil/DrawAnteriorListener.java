package va.vt.cbil;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class DrawAnteriorListener implements MouseMotionListener, MouseListener {

	
	boolean valid = false;
	MyImageLabel label = null;
	ImageDealer imageDealer = null;
	public DrawAnteriorListener(MyImageLabel label, ImageDealer imageDealer) {
		this.label = label;
		this.imageDealer = imageDealer;
	}
	
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(valid) {
			Point p2 = new Point(arg0.getX(),arg0.getY());
			label.setEndAnterior(p2);
			label.repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
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
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if(valid) {
			Point p1 = new Point(e.getX(),e.getY()); 
			Point p2 = new Point(e.getX(),e.getY());
			label.setStartAnterior(p1);
			label.setEndAnterior(p2);
			label.repaint();
			label.setAneriorStatus(false);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(valid) {
			Point p2 = new Point(e.getX(),e.getY());
			label.setEndAnterior(p2);
			label.repaint();
			valid = false;
			imageDealer.left.drawAnterior.doClick();
			label.setAneriorStatus(true);
		}
	}

}
