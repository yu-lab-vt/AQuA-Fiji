package va.vt.cbil;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;

public class DrawFeatures {
	JToggleButton boxPlot = new JToggleButton("Box Plot");
	JToggleButton histogram = new JToggleButton("Histogram");
	JToggleButton filteredData = new JToggleButton("FilteredData");
	JToggleButton area = new JToggleButton("Area");
	JToggleButton dFF = new JToggleButton("dF/F");
	JToggleButton duration = new JToggleButton("Duration");
	JToggleButton pValue = new JToggleButton("P value");
	JToggleButton decayTau = new JToggleButton("DecayTau");
	MyLabel canvas = new MyLabel();
	int figureType = 0;
	int featureType = 0;
	boolean filter = false;
	String title = "Area";

	ImageDealer dealer = null;
	public DrawFeatures() {
		listeners();
		draw();
		
	}
	
	public DrawFeatures(ImageDealer imageDealer) {
		// TODO Auto-generated constructor stub
		dealer = imageDealer;
		draw();
		listeners();
		drawFigure();
	}
	
	public void draw() {
		boxPlot.setPreferredSize(new Dimension(120,30));
		histogram.setPreferredSize(new Dimension(120,30));
		filteredData.setPreferredSize(new Dimension(120,30));
		area.setPreferredSize(new Dimension(120,30));
		dFF.setPreferredSize(new Dimension(120,30));
		duration.setPreferredSize(new Dimension(120,30));
		pValue.setPreferredSize(new Dimension(120,30));
		decayTau.setPreferredSize(new Dimension(120,30));
		
		JFrame aquaWindow = new JFrame("Features");
		aquaWindow.setSize(800,640);
		aquaWindow.setUndecorated(false);
		aquaWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		aquaWindow.setLocationRelativeTo(null);
		aquaWindow.setMinimumSize(new Dimension(800,640));
		aquaWindow.setResizable(false);
		
		
		
		canvas.setPreferredSize(new Dimension(800,550));
		canvas.setOpaque(false);
		canvas.setBackground(Color.white);
		
//		PlaneHistgram planeHistogram = new PlaneHistgram();
//		BufferedImage image = planeHistogram.paintPlaneHistogram("duration",new float[] {1f,1f,1f,2f,3f,4f,12f});
//		PlaneBoxPlot planeBoxPlot = new PlaneBoxPlot();
//		BufferedImage image = planeBoxPlot.paintPlaneBoxPlot("duration",new float[] {1f,2f,3f,4f,5f,6f,7f,30f,20f,-5f});
		
		JPanel curP = new JPanel();
		JPanel imaPanel = new JPanel();
		JPanel buttons = new JPanel();
		
		
		
		curP.setPreferredSize(new Dimension(800,640));
		
		buttons.setPreferredSize(new Dimension(800,90));
		
		imaPanel.add(canvas);
		imaPanel.setPreferredSize(new Dimension(800,550));
//		canvas.setIcon(new ImageIcon(image));
//		canvas.repaint();
		
		
		GridBagPut setting = new GridBagPut(buttons);
		setting.putGridBag(boxPlot, buttons, 0, 0);
		setting.putGridBag(histogram, buttons, 1, 0);
		setting.putGridBag(filteredData, buttons, 4, 0);
		setting.putGridBag(area, buttons, 0, 1);
		setting.putGridBag(dFF, buttons, 1, 1);
		setting.putGridBag(duration, buttons, 2, 1);
		setting.putGridBag(pValue, buttons, 3, 1);
		setting.putGridBag(decayTau, buttons, 4, 1);
		
		GridBagPut setting2 = new GridBagPut(curP);
		setting2.putGridBag(imaPanel, curP, 0, 0);
		setting2.putGridBag(buttons, curP, 0, 1);
		
		boxPlot.setSelected(true);
		
		aquaWindow.add(curP);
		area.setSelected(true);
		
		aquaWindow.setVisible(true);
	};
	
	public void listeners() {
		boxPlot.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(boxPlot.isSelected()) {
					histogram.setSelected(false);
					figureType = 0;
					drawFigure();
				}
			}
		});		
		
		histogram.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(histogram.isSelected()) {
					boxPlot.setSelected(false);
					figureType = 1;
					drawFigure();
				}
			}
		});		
		
		filteredData.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(filteredData.isSelected()) {
					filter = true;
					drawFigure();
				}else{
					filter = false;
					drawFigure();
				}
			}
		});	
		
		area.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(area.isSelected()) {
					dFF.setSelected(false);
					duration.setSelected(false);
					pValue.setSelected(false);
					decayTau.setSelected(false);
					featureType = 0;
					title = "Area";
					drawFigure();
				}
			}
		});	
		
		dFF.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(dFF.isSelected()) {
					area.setSelected(false);
					duration.setSelected(false);
					pValue.setSelected(false);
					decayTau.setSelected(false);
					featureType = 1;
					title = "dF/F";
					drawFigure();
				}
			}
		});	
		
		duration.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(duration.isSelected()) {
					area.setSelected(false);
					dFF.setSelected(false);
					pValue.setSelected(false);
					decayTau.setSelected(false);
					featureType = 2;
					title = "Duration";
					drawFigure();
				}
			}
		});	
		
		pValue.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(pValue.isSelected()) {
					area.setSelected(false);
					dFF.setSelected(false);
					duration.setSelected(false);
					decayTau.setSelected(false);
					featureType = 3;
					title = "P Value";
					drawFigure();
				}
			}
		});	
		
		decayTau.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(decayTau.isSelected()) {
					area.setSelected(false);
					dFF.setSelected(false);
					duration.setSelected(false);
					pValue.setSelected(false);
					featureType = 4;
					title = "DecayTau";
					drawFigure();
				}
			}
		});	
		
	}
	
	public void drawFigure() {
		int nEvt = dealer.fts.basic.area.size();
		boolean[] filterStatus = new boolean[nEvt];
		HashSet<Integer> deleteSet =dealer.deleteColorSet2;
		ArrayList<Float> valueList = new ArrayList<>(); 
		
		for(int i=1;i<=nEvt;i++) {
			if(filter && deleteSet.contains(i))
				continue;
			switch(featureType) {
				case 0:
					valueList.add(dealer.fts.basic.area.get(i));
					break;
				case 1:
					valueList.add(dealer.fts.curve.dffMax.get(i));
					break;
				case 2:
					valueList.add(dealer.fts.curve.width55.get(i));
					break;
				case 3:
					valueList.add(dealer.fts.curve.dffMaxPval.get(i));
					break;
				case 4:
					valueList.add(dealer.fts.curve.decayTau.get(i));
					break;
			}
		}
		
		float[] values = new float[valueList.size()];
		for(int i = 0;i<valueList.size();i++) {
			values[i] = valueList.get(i);
		}
		
		BufferedImage image = null;
		if(figureType==0) {
			image = PlaneHistgram.paintPlaneHistogram(title,values);
		}else
			image = PlaneBoxPlot.paintPlaneBoxPlot(title,values);
		canvas.setIcon(new ImageIcon(image));
		canvas.repaint();
		
	}
	
	class MyLabel extends JLabel{
		
	}
	

}
