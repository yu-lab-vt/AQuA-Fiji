package va.vt.cbil;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class SetColor{
	Color cStart = null;
	Color cMid = null;
	Color cEnd = null;
	JFrame window = new JFrame("Set Color 0~255");
	JLabel l1 = new JLabel("");
	JLabel l2 = new JLabel("R");
	JLabel l3 = new JLabel("G");
	JLabel l4 = new JLabel("B");
	JPanel start = new JPanel();
	JLabel s = new JLabel("Start");
	JTextField sR = new JTextField("0");
	JTextField sG = new JTextField("0");
	JTextField sB = new JTextField("0");
	JPanel mid = new JPanel();
	JLabel m = new JLabel("Middle");
	JTextField mR = new JTextField("0");
	JTextField mG = new JTextField("0");
	JTextField mB = new JTextField("0");
	JPanel end = new JPanel();
	JTextField eR = new JTextField("0");
	JTextField eG = new JTextField("0");
	JTextField eB = new JTextField("0");
	JLabel e = new JLabel("End");
	JButton confirm = new JButton("Confirm");
	JPanel labelPanel = new JPanel();
	
	boolean flag = true;
	ImageDealer imageDealer = null;
	
	public SetColor(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
	}
	
	public void run() {
		
		window.setSize(300,200);
		window.setUndecorated(false);
		window.setLocationRelativeTo(null);
		
		
		
		l2.setHorizontalAlignment(JLabel.CENTER);
		l3.setHorizontalAlignment(JLabel.CENTER);
		l4.setHorizontalAlignment(JLabel.CENTER);
		l1.setPreferredSize(new Dimension(50,20));
		l2.setPreferredSize(new Dimension(50,20));
		l3.setPreferredSize(new Dimension(50,20));
		l4.setPreferredSize(new Dimension(50,20));
		
		
		s.setPreferredSize(new Dimension(50,20));
		sR.setPreferredSize(new Dimension(50,20));
		sG.setPreferredSize(new Dimension(50,20));
		sB.setPreferredSize(new Dimension(50,20));
		sR.setHorizontalAlignment(JLabel.CENTER);
		sG.setHorizontalAlignment(JLabel.CENTER);
		sB.setHorizontalAlignment(JLabel.CENTER);
		
		
		m.setPreferredSize(new Dimension(50,20));
		mR.setPreferredSize(new Dimension(50,20));
		mG.setPreferredSize(new Dimension(50,20));
		mB.setPreferredSize(new Dimension(50,20));
		mR.setHorizontalAlignment(JLabel.CENTER);
		mG.setHorizontalAlignment(JLabel.CENTER);
		mB.setHorizontalAlignment(JLabel.CENTER);
		
		
		e.setPreferredSize(new Dimension(50,20));
		eR.setPreferredSize(new Dimension(50,20));
		eG.setPreferredSize(new Dimension(50,20));
		eB.setPreferredSize(new Dimension(50,20));
		eR.setHorizontalAlignment(JLabel.CENTER);
		eG.setHorizontalAlignment(JLabel.CENTER);
		eB.setHorizontalAlignment(JLabel.CENTER);
		
		labelPanel.add(l1);
		labelPanel.add(l2);
		labelPanel.add(l3);
		labelPanel.add(l4);
		
		
		start.add(s);
		start.add(sR);
		start.add(sG);
		start.add(sB);
		
		mid.add(m);
		mid.add(mR);
		mid.add(mG);
		mid.add(mB);
		
		end.add(e);
		end.add(eR);
		end.add(eG);
		end.add(eB);
		
		GridBagPut settingCenterGroup1 = new GridBagPut(window);
		settingCenterGroup1.putGridBag(labelPanel, window, 0, 0);
		settingCenterGroup1.putGridBag(start, window, 0, 1);
		settingCenterGroup1.putGridBag(mid, window, 0, 2);
		settingCenterGroup1.putGridBag(end, window, 0, 3);
		settingCenterGroup1.putGridBag(confirm, window, 0, 4);
		
		window.setVisible(true);
		
		
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int r,g,b=0;
				r = Integer.parseInt(sR.getText());
				g = Integer.parseInt(sG.getText());
				b = Integer.parseInt(sB.getText());
				r = Math.max(Math.min(r, 255),0);
				g = Math.max(Math.min(g, 255),0);
				b = Math.max(Math.min(b, 255),0);
				cStart = new Color(r,g,b);
				
				r = Integer.parseInt(mR.getText());
				g = Integer.parseInt(mG.getText());
				b = Integer.parseInt(mB.getText());
				r = Math.max(Math.min(r, 255),0);
				g = Math.max(Math.min(g, 255),0);
				b = Math.max(Math.min(b, 255),0);
				cMid = new Color(r,g,b);
				
				r = Integer.parseInt(eR.getText());
				g = Integer.parseInt(eG.getText());
				b = Integer.parseInt(eB.getText());
				r = Math.max(Math.min(r, 255),0);
				g = Math.max(Math.min(g, 255),0);
				b = Math.max(Math.min(b, 255),0);
				cEnd = new Color(r,g,b);
				window.setVisible(false);
				
				imageDealer.right.setCenterBar(cStart, cMid, cEnd);
			}
		});
		
		
	}
	


}


