package va.vt.cbil;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JToggleButton;

public class BuilderRemoveListener implements MouseListener {
	private Graphics2D g;
	private boolean valid = false;
	MakeBuilderLabel canvas = null;
	JToggleButton button = null;
	int width = 1;
	private int maxLength = 500;
	boolean[][] region = null;
	
	public BuilderRemoveListener (MakeBuilderLabel canvas) {
		this.canvas = canvas;
//		this.list = list;
	}

	
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public void setRegion(boolean[][] region) {
		this.region = region;
	}
	
//	public void setRegionLabel(int[][] label) {
//		this.label = label;
//	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
		if(valid) {
			int cwidth = canvas.getWidth();
			int cheight = canvas.getHeight();
			Point point = e.getPoint();
//			double length = imageDealer.getWidth();
//			double tx = imageDealer.getStartPoint().getX();
//			double ty = imageDealer.getStartPoint().getY();
//			Point p = new Point();
//			int x = (int) (point.getX()*imageDealer.getWidth()/imageDealer.maxImageWidth + tx);
//			int y = (int) (point.getY()*imageDealer.getHeight()/imageDealer.maxImageHeight + ty);
			int x = (int) (point.getX()/cwidth*region.length);
			int y = (int) (point.getY()/cheight*region[0].length);
			System.out.println(x + " " + y);
			
			
			if(region[x][y]) {
				HashMap<Integer, ArrayList<int[]>> connectedMap = ConnectedComponents.twoPassConnect2D(region);
				for(Entry<Integer, ArrayList<int[]>> entry:connectedMap.entrySet()) {
					ArrayList<int[]> points = entry.getValue();
					boolean find = false;
					for(int[] xy:points) {
						if(xy[0]==x && xy[1]==y) {
							find = true;
							break;
						}
					}
					
					if(find) {
						for(int[] xy:points) {
//							System.out.println(xy[0] + " " + xy[1]);
							region[xy[0]][xy[1]] = false;
						}
						canvas.getComponentBorder(region);
						break;
					}
				}
			}
			
			canvas.paint(g);
		}
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
		if(valid) {
			g = (Graphics2D) canvas.getGraphics();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
