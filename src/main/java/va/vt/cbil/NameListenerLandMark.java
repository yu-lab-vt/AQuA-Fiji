package va.vt.cbil;
/**
 *  * Button Listener, add it to the add button on the leftPanel.
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;


class NameListenerLandMark implements MouseListener, MouseMotionListener {


	private boolean valid = false;
	JComponent canvas = null;
	JToggleButton button = null;
	ImageDealer imageDealer = null;
	int[][] regionLabel = null;
	
	public NameListenerLandMark(JComponent canvas, ImageDealer imageDealer, int[][] regionLabel) {
		this.canvas = canvas;
		this.imageDealer = imageDealer;
		this.regionLabel = regionLabel;
	}
	
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(valid) {
			Point curPoint = e.getPoint();
			double tx = imageDealer.getStartPoint().getX();
			double ty = imageDealer.getStartPoint().getY();
			int x = (int) Math.round(curPoint.getX()*imageDealer.getWidth()/imageDealer.maxImageWidth + tx);
			int y = (int) Math.round(curPoint.getY()*imageDealer.getHeight()/imageDealer.maxImageHeight + ty);
			int label = regionLabel[x][y];
			
//			System.out.println(x + " " + y + " " + label);
			if(label>0) {
				JPanel enterValue = new JPanel();
				JLabel text = new JLabel("New Name: ");
				text.setPreferredSize(new Dimension(80,20));
				JTextField nameTF = new JTextField(label + "");
				nameTF.setPreferredSize(new Dimension(80,20));
				enterValue.add(text);
				enterValue.add(nameTF);
				
				int result = JOptionPane.showConfirmDialog(null, enterValue, 
		                "Please Enter New Name", JOptionPane.OK_CANCEL_OPTION);
				
				if (result == JOptionPane.OK_OPTION) {
					String name = nameTF.getText();
					if(name.length()==0) {
						JOptionPane.showMessageDialog(null, "You should Enter a Name","Warning",JOptionPane.WARNING_MESSAGE); 
					}else {
						imageDealer.nameLstLandMark.put(label, name);
						imageDealer.left.name12.setSelected(false);
						imageDealer.dealImage();
						
					}
					
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
	
}
