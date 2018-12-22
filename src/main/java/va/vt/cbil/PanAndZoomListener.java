package va.vt.cbil;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

class PanAndZoomListener implements MouseListener, MouseMotionListener, MouseWheelListener{
	public static final int DEFAULT_MIN_ZOOM_LEVEL = 0;
    public static final int DEFAULT_MAX_ZOOM_LEVEL = 40;
    public static final double DEFAULT_ZOOM_MULTIPLICATION_FACTOR = 1.1;
    
    private int zoomLevel = 0;
    private int minZoomLevel = DEFAULT_MIN_ZOOM_LEVEL;
    private int maxZoomLevel = DEFAULT_MAX_ZOOM_LEVEL;
    private double zoomFactor = DEFAULT_ZOOM_MULTIPLICATION_FACTOR;
    private int maxWidth = 0;
    private int maxHeight = 0;
    
    ImageDealer imageDealer = null;
    
    private Point dragStartScreen;
    private Point location;
    private double width;
    private double height;
    
    private boolean valid = false;
    
    public void setValid(boolean valid) {
    	this.valid = valid;
    }
    
    public PanAndZoomListener(ImageDealer imageDealer, Point location) {
    	this.imageDealer = imageDealer;
    	this.location = location;
    	maxWidth = imageDealer.maxImageWidth;
    	maxHeight = imageDealer.maxImageHeight;
    }
    public void reset() {
    	zoomLevel = 0;
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
			float scal = imageDealer.width/imageDealer.height;
			Point p = e.getPoint();
			p = transformPoint(p);
			p = transformPoint2(p,dx,dy);
			if(wheelRotation<0) {
				if(zoomLevel < maxZoomLevel) {
					zoomLevel ++;
					double startX = p.getX()*imageDealer.width/maxWidth - dx/2/zoomFactor;
					double startY = p.getY()*imageDealer.height/maxHeight - dy/2/zoomFactor;
					double endX = startX + dx/zoomFactor;
					double endY = startY + dy/zoomFactor;
					if(startX < 0) {
						endX -= startX;
						startX = 0;
					}
					if(startY < 0) {
						endY -= startY;
						startY = 0;
					}
					// prevent the size exceeds the bound
					if(endX > maxWidth) {
						startX -= endX - maxWidth;
						endX = maxWidth;
					}
					if(endY > maxHeight) {
						startY -= endY - maxHeight;
						endY = maxHeight;
					}
					start.setLocation(startX, startY);
					end.setLocation(endX, endY);
					imageDealer.setArea(start, end);
					imageDealer.dealImage();
				}
			}else {
				if(zoomLevel > minZoomLevel) {
					zoomLevel --;
					double startX = p.getX()*imageDealer.width/maxWidth - dx/2*zoomFactor;
					double startY = p.getY()*imageDealer.height/maxHeight - dy/2*zoomFactor;
					if(dx*zoomFactor>imageDealer.width||dy*zoomFactor>imageDealer.height) {
						start = null;
						end = null;
						imageDealer.setArea(start, end);
						imageDealer.dealImage();
					}else {
						if(startX < 0) {
							startX = 0;
						}
						if(startY < 0) {
							startY = 0;
						}
						double endX = startX + dx*zoomFactor;
						double endY = startY + dy*zoomFactor;
						// prevent the size exceeds the bound
						if(endX > maxWidth) {
							startX -= endX - maxWidth;
							endX = maxWidth;
						}
						if(endY > maxHeight) {
							startY -= endY - maxHeight;
							endY = maxHeight;
						}
						
						start.setLocation(startX, startY);
						end.setLocation(endX, endY);
						
						imageDealer.setArea(start, end);
						imageDealer.dealImage();
					}
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
		double resultX = imageDealer.getStartPoint().getX() + p.getX()*dx/imageDealer.getOrigWidth();
		double resultY = imageDealer.getStartPoint().getY() + p.getY()*dy/imageDealer.getOrigHeight();
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
			dx = dx*width/imageDealer.width;
			dy = dy*height/imageDealer.height;
			double x = westNorth.getX() - dx;
			double y = westNorth.getY() - dy;
			if(x < 0) {
				x = 0;
			}
			if(y < 0) {
				y = 0;
			}
			if(x + width > imageDealer.width) {
				x = imageDealer.width - width;
			}
			if(y + height > imageDealer.height) {
				y = imageDealer.height - height;
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
