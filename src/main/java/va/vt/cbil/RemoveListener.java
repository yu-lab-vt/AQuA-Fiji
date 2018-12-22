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

public class RemoveListener implements MouseListener {
	private Graphics2D g;
	private boolean valid = false;
	JComponent canvas = null;
	JToggleButton button = null;
	int width = 1;
	ImageDealer imageDealer = null;
	private int maxLength = 500;
	boolean[][] region = null;
	int[][] label = null;
	
	public RemoveListener (JComponent canvas, ImageDealer imageDealer, boolean[][] region, int[][] label) {
		this.canvas = canvas;
//		this.list = list;
		this.imageDealer = imageDealer;
		this.region = region;
		this.label = label;
	}

	
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public void setRegion(boolean[][] region) {
		this.region = region;
	}
	
	public void setRegionLabel(int[][] label) {
		this.label = label;
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
		if(valid) {
			Point point = e.getPoint();
			double length = imageDealer.getWidth();
			double tx = imageDealer.getStartPoint().getX();
			double ty = imageDealer.getStartPoint().getY();
//			Point p = new Point();
			int x = (int) (point.getX()*length/maxLength + tx);
			int y = (int) (point.getY()*length/maxLength + ty);
//			p.setLocation(x, y);
//
//
//			ArrayList<Integer> index = new ArrayList<>();
//			for(int i=0;i<list.size();i++) {
//				if(judgePointInPolygon(p, list.get(i)))
//					index.add(i);
//			}
//			int n = index.size()-1;
//			for(int j = list.size()-1;j>=0;j--) {
//				if(n < 0)
//					break;
//				int k = index.get(n);
//				if(j == k) {
//					list.remove(j);
//					index.remove(n);
//					n--;
//				}
//			}	
			
			
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
							label[xy[0]][xy[1]] = 0;
						}
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
