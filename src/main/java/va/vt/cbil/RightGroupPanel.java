package va.vt.cbil;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

class RightGroupPanel {
	ImageDealer imageDealer = null;
	// right1
	JPanel rightGroup = new JPanel();	
	JPanel right1 = new JPanel();
	JPanel right2 = new JPanel();
	JLabel layers = new JLabel(" Layers");
	JLabel rightLabel1 = new JLabel("--- Movie brightness / contrast ---");
	JLabel minLabel = new JLabel(" Min");
	JSlider minSlider = null;
	JLabel maxLabel = new JLabel(" Max");
	JSlider maxSlider = null;
	JLabel brightnessLabel = new JLabel(" Brightness");
	JPanel brightPanel = new JPanel();
	JSlider contrastSlider = new JSlider(JSlider.HORIZONTAL, 0, 800, 50);
	JSlider contrastSliderl = new JSlider(JSlider.HORIZONTAL, 0, 800, 50);
	JSlider contrastSliderr = new JSlider(JSlider.HORIZONTAL, 0, 800, 50);
	GridBagPut settingRight1 = new GridBagPut(right1);
	
	JLabel rowBlank1 = new JLabel();
	
	// right2
	JLabel rightLabel2 = new JLabel("--- Feature overlay ---");
	JLabel typeLabel = new JLabel(" Type");
	JLabel featureLabel = new JLabel(" Feature");
	JLabel colorLabel = new JLabel(" Color");
	JLabel transformLabel = new JLabel(" Transform");
	JLabel divideLabel = new JLabel(" Divide");
	JLabel proDirLabel = new JLabel(" Propagation direction");
	JLabel landIDLabel = new JLabel(" Landmark ID");
	JTextField landIDLabel2 = new JTextField("0");
	JButton updateOverlayButton = new JButton("Update overlay");
	JButton setColorMap = new JButton("setColorMap");
	
	String[] typeStr = {"None"};
	String[] featureStr = {"Index"};
	String[] colorStr = {"Random"};
	String[] transformStr = {"None"};
	String[] divideStr = {"None"};
	String[] proDirStr = {"Anterior"};
	
	JComboBox<String> typeJCB = new JComboBox<String>(typeStr);
	JComboBox<String> featureJCB = new JComboBox<String>(featureStr);
	JComboBox<String> colorJCB = new JComboBox<String>(colorStr);
	JComboBox<String> transformJCB = new JComboBox<String>(transformStr);
	JComboBox<String> divideJCB = new JComboBox<String>(divideStr);
	JComboBox<String> proDirJCB = new JComboBox<String>(proDirStr);
	
	JPanel rightButtons = new JPanel();
	
	JLabel rowBlank2 = new JLabel();
	
	// right3
	JPanel right3 = new JPanel();
	JLabel minLabel2 = new JLabel(" Min");
	JLabel maxLabel2 = new JLabel(" Max");
	JLabel brightnessLabel2 = new JLabel(" Brightness");
	JSlider minSlider2 = null;
	JSlider maxSlider2 = null;
	JSlider contrastSlider2 = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
	
	
	JLabel rowBlank3 = new JLabel();
	
	// right 4
	JLabel favorite = new JLabel(" Favourite");
	JButton selectAllButton = new JButton("Select all");
	JButton deleteButton = new JButton("Delete");
	JButton details = new JButton("Details");
	JButton showcurvesButton = new JButton("Show curves");
	JButton savecurvesButton = new JButton("Save curves");
	JPanel right4 = new JPanel();
	JPanel right4Buttons = new JPanel();
	DefaultTableModel model = null;
	JTable table = null;
	JScrollPane tablePane = null;
	JPanel eventPanel = new JPanel();
	JLabel eventID = new JLabel(" Event ID");
	JTextField eventText = new JTextField("");
	JButton addToFavorite = new JButton("Add to favorite");
	
	// Builder
	JPanel builderRight = new JPanel();
	JLabel foreground = new JLabel(" Foreground detection");
	JLabel intensity = new JLabel(" Intensity threshold");
	JLabel sizeMin = new JLabel(" Size (min)");
	JLabel sizeMax = new JLabel(" Size (max)");
	JSlider intensitySlider = null;
	JSlider sizeMinSlider = null;
	JSlider sizeMaxSlider = null;
	
	public RightGroupPanel(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
		minSlider =  new JSlider(JSlider.HORIZONTAL, 0, imageDealer.getMax(), 0);
		maxSlider = new JSlider(JSlider.HORIZONTAL, 0, imageDealer.getMax(), imageDealer.getMax());
		minSlider2 =  new JSlider(JSlider.HORIZONTAL, 0, 807, 0);
		maxSlider2 = new JSlider(JSlider.HORIZONTAL, 0, 807, 807);
		intensitySlider = new JSlider(JSlider.HORIZONTAL, 0, imageDealer.maxAvg, imageDealer.maxAvg);
		int maxSize = (int) (imageDealer.getOrigHeight() * imageDealer.getOrigWidth());
		sizeMinSlider = new JSlider(JSlider.HORIZONTAL, 0, maxSize, 0);
		sizeMaxSlider = new JSlider(JSlider.HORIZONTAL, 0, maxSize, maxSize);
	}
	
	public void setting() {
		// right1
		layers.setOpaque(true);
		layers.setBackground(UI_Beauty.blue);
		layers.setForeground(Color.WHITE);
		layers.setPreferredSize(new Dimension(400,15));
		rightLabel1.setHorizontalAlignment(JLabel.CENTER );
		rightLabel1.setPreferredSize(new Dimension(400,15));
		minLabel.setPreferredSize(new Dimension(400,15));
		minSlider.setPreferredSize(new Dimension(350,20));
		maxLabel.setPreferredSize(new Dimension(400,15));
		maxSlider.setPreferredSize(new Dimension(350,20));
		brightnessLabel.setPreferredSize(new Dimension(400,15));
		contrastSlider.setPreferredSize(new Dimension(350,20));
		contrastSliderl.setPreferredSize(new Dimension(175,20));
		contrastSliderr.setPreferredSize(new Dimension(175,20));
		
		// right2
		rightLabel2.setHorizontalAlignment(JLabel.CENTER );
		rightLabel2.setPreferredSize(new Dimension(400,15));
		typeLabel.setPreferredSize(new Dimension(400,15));
		typeJCB.setPreferredSize(new Dimension(390,20));
		typeJCB.setBackground(Color.white);
		typeJCB.setEnabled(false);
		featureLabel.setPreferredSize(new Dimension(400,15));
		featureJCB.setPreferredSize(new Dimension(390,20));
		featureJCB.setEnabled(false);
		colorLabel.setPreferredSize(new Dimension(400,15));
		colorJCB.setPreferredSize(new Dimension(390,20));
		colorJCB.setEnabled(false);
		transformLabel.setPreferredSize(new Dimension(400,15));
		transformJCB.setPreferredSize(new Dimension(390,20));
		transformJCB.setEnabled(false);
		divideLabel.setPreferredSize(new Dimension(400,15));
		divideJCB.setPreferredSize(new Dimension(390,20));
		divideJCB.setEnabled(false);
		proDirLabel.setPreferredSize(new Dimension(400,15));
		proDirJCB.setPreferredSize(new Dimension(390,20));
		proDirJCB.setEnabled(false);
		landIDLabel.setPreferredSize(new Dimension(400,15));
		landIDLabel2.setPreferredSize(new Dimension(390,20));
		landIDLabel2.setBorder(BorderFactory.createEtchedBorder());
		landIDLabel2.setEnabled(false);
		
		// right 3
		
		minLabel2.setPreferredSize(new Dimension(400,15));
		minSlider2.setPreferredSize(new Dimension(350,20));
		maxLabel2.setPreferredSize(new Dimension(400,15));
		maxSlider2.setPreferredSize(new Dimension(350,20));
		brightnessLabel2.setPreferredSize(new Dimension(400,15));
		contrastSlider2.setPreferredSize(new Dimension(350,20));
		
		// right 4
    	favorite.setOpaque(true);
    	favorite.setBackground(UI_Beauty.blue);
    	favorite.setForeground(Color.WHITE);
    	favorite.setPreferredSize(new Dimension(400,20));
    	right4Buttons.setPreferredSize(new Dimension(380,50));
    	selectAllButton.setPreferredSize(new Dimension(120,25));
    	deleteButton.setPreferredSize(new Dimension(120,25));
    	details.setPreferredSize(new Dimension(120,25));
    	showcurvesButton.setPreferredSize(new Dimension(120,25));
    	savecurvesButton.setPreferredSize(new Dimension(120,25));
    	eventID.setPreferredSize(new Dimension(80,15));
    	eventText.setPreferredSize(new Dimension(80,15));
    	addToFavorite.setPreferredSize(new Dimension(120,15));
    	
    	
    	rowBlank1.setPreferredSize(new Dimension(400,10));
		rowBlank2.setPreferredSize(new Dimension(400,10));
		rowBlank3.setPreferredSize(new Dimension(400,10));
		
		updateOverlayButton.setEnabled(false);
    	setColorMap.setEnabled(false);
    	
    	// Table
    	setTable();
    	
    	// Builder
    	foreground.setOpaque(true);
    	foreground.setBackground(UI_Beauty.blue);
    	foreground.setForeground(Color.WHITE);
    	foreground.setPreferredSize(new Dimension(400,20));
    	intensity.setPreferredSize(new Dimension(400,20));
    	sizeMin.setPreferredSize(new Dimension(400,20));
    	sizeMax.setPreferredSize(new Dimension(400,20));
    	intensitySlider.setPreferredSize(new Dimension(350,20));
    	sizeMinSlider.setPreferredSize(new Dimension(350,20));
    	sizeMaxSlider.setPreferredSize(new Dimension(350,20));
	}
	
	private void setTable(){
		// Table
    	model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;
    		@Override
    		public boolean isCellEditable(int row, int column) {
    			if(column == 1)
    				return true;
    			else
    				return false;
    		}
    	};
    	model.addColumn("");
    	model.addColumn("");
    	model.addColumn("Index");
    	model.addColumn("Frame");
    	model.addColumn("Size");
    	model.addColumn("Duration");
    	model.addColumn("dF/F");
    	model.addColumn("Tau");
    	table = new JTable(model){
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Class getColumnClass(int column) {
				switch (column) {
					case 0:	return String.class;
					case 1: return Boolean.class;
					case 2: return String.class;
					case 3: return String.class;
					case 4: return String.class;
					case 5: return String.class;
					default: return String.class;
				}
			}
    		
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				
				if(column==0)
					component.setBackground(new Color(238, 238, 238));
				else {
					if(row%2==1)
						component.setBackground(new Color(245, 245, 250));
					else
						component.setBackground(Color.white);
				}
				return component;
			}
    	};
    	DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
    	tcr.setHorizontalAlignment(JLabel.CENTER);
    	table.setDefaultRenderer(Object.class, tcr);
    	table.getColumnModel().getColumn(0).setPreferredWidth(15);
    	table.getColumnModel().getColumn(1).setPreferredWidth(15);
    	table.setSize(new Dimension(380,100));
    	tablePane = new JScrollPane(table);
    	tablePane.setPreferredSize(new Dimension(380,103));
    	tablePane.setOpaque(true);
    	
    	
    	

	}
	
	public void layout2() {
		rightGroup.removeAll();
		
		
		GridBagPut settingRightGroup = new GridBagPut(rightGroup);
		settingRightGroup.putGridBag(builderRight, rightGroup, 0, 0);
	}
	
	public void layout() {
		// right1
		brightPanel.add(contrastSlider);
		settingRight1.putGridBag(layers, right1, 0, 0);
		settingRight1.putGridBag(rightLabel1, right1, 0, 1);
		settingRight1.putGridBag(minLabel, right1, 0, 2);
		settingRight1.putGridBag(minSlider, right1, 0, 3);
		settingRight1.putGridBag(maxLabel, right1, 0, 4);
		settingRight1.putGridBag(maxSlider, right1, 0, 5);
		settingRight1.putGridBag(brightnessLabel, right1, 0, 6);
		settingRight1.putGridBag(brightPanel, right1, 0, 7);
		right1.setBorder(BorderFactory.createEtchedBorder());
		
		//right2
		rightButtons.add(updateOverlayButton);
		rightButtons.add(setColorMap);
		
		GridBagPut settingRight2 = new GridBagPut(right2);
		settingRight2.putGridBag(rightLabel2, right2, 0, 0);
		settingRight2.putGridBag(typeLabel, right2, 0, 1);
		settingRight2.putGridBag(typeJCB, right2, 0, 2);
		settingRight2.putGridBag(featureLabel, right2, 0, 3);
		settingRight2.putGridBag(featureJCB, right2, 0, 4);
		settingRight2.putGridBag(colorLabel, right2, 0, 5);
		settingRight2.putGridBag(colorJCB, right2, 0, 6);
		settingRight2.putGridBag(transformLabel, right2, 0, 7);
		settingRight2.putGridBag(transformJCB, right2, 0, 8);
		settingRight2.putGridBag(divideLabel, right2, 0, 9);
		settingRight2.putGridBag(divideJCB, right2, 0, 10);
		settingRight2.putGridBag(proDirLabel, right2, 0, 11);
		settingRight2.putGridBag(proDirJCB, right2, 0, 12);
		settingRight2.putGridBag(landIDLabel, right2, 0, 13);
		settingRight2.putGridBag(landIDLabel2, right2, 0, 14);
		settingRight2.putGridBag(rightButtons, right2, 0, 15);
		right2.setBorder(BorderFactory.createEtchedBorder());
		
		// right3
		GridBagPut settingRight3 = new GridBagPut(right3);
		settingRight3.putGridBag(minLabel2, right3, 0, 0);
		settingRight3.putGridBag(minSlider2, right3, 0, 1);
		settingRight3.putGridBag(maxLabel2, right3, 0, 2);
		settingRight3.putGridBag(maxSlider2, right3, 0, 3);
		settingRight3.putGridBag(brightnessLabel2, right3, 0, 4);
		settingRight3.putGridBag(contrastSlider2, right3, 0, 5);
		right3.setBorder(BorderFactory.createEtchedBorder());
		
		// right 4
//		GridBagPut settingRight4Buttons = new GridBagPut(right4Buttons);
//		settingRight4Buttons.putGridBag(selectAllButton, right4Buttons, 0, 0);
//		settingRight4Buttons.putGridBag(deleteButton, right4Buttons, 1, 0);
//		settingRight4Buttons.putGridBag(details, right4Buttons, 2, 0);
//		settingRight4Buttons.putGridBag(showcurvesButton, right4Buttons, 0, 1);
//		settingRight4Buttons.putGridBag(savecurvesButton, right4Buttons, 1, 1);
		
		
		right4Buttons.setLayout(new GridLayout(2,3));
		right4Buttons.add(selectAllButton);
		right4Buttons.add(deleteButton);
		right4Buttons.add(details);
		right4Buttons.add(showcurvesButton);
		right4Buttons.add(savecurvesButton);
		
		eventPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		eventPanel.add(eventID);
		eventPanel.add(eventText);
		eventPanel.add(addToFavorite);
		
		
		GridBagPut settingright4 = new GridBagPut(right4);
		settingright4.putGridBag(favorite, right4, 0, 0);
		settingright4.putGridBag(right4Buttons, right4, 0, 1);
		settingright4.putGridBag(tablePane, right4, 0, 2);
		settingright4.putGridBag(eventPanel, right4, 0, 3);
		right4.setBorder(BorderFactory.createEtchedBorder());
		
		GridBagPut setting = new GridBagPut(builderRight);
		setting.putGridBag(foreground, builderRight, 0, 0);
		setting.putGridBag(intensity, builderRight, 0, 1);
		setting.putGridBag(intensitySlider, builderRight, 0, 2);
		setting.putGridBag(sizeMin, builderRight, 0, 3);
		setting.putGridBag(sizeMinSlider, builderRight, 0, 4);
		setting.putGridBag(sizeMax, builderRight, 0, 5);
		setting.putGridBag(sizeMaxSlider, builderRight, 0, 6);
		builderRight.setBorder(BorderFactory.createEtchedBorder());
		
		
		
		right3.setVisible(false);
		right4.setVisible(false);
	}
	
	public void layout1() {
		rightGroup.removeAll();
		// rightGroup set
		GridBagPut settingRightGroup = new GridBagPut(rightGroup);
		settingRightGroup.putGridBag(right1, rightGroup, 0, 0);
		settingRightGroup.putGridBag(rowBlank1, rightGroup, 0, 1);
		settingRightGroup.putGridBag(right2, rightGroup, 0, 2);
		settingRightGroup.putGridBag(rowBlank2, rightGroup, 0, 3);
		settingRightGroup.putGridBag(right3, rightGroup, 0, 4);
		settingRightGroup.putGridBag(rowBlank3, rightGroup, 0, 5);
		settingRightGroup.putGridBag(right4, rightGroup, 0, 6);
	}
	
	public void addButtonListeners() {
		minSlider.setMinorTickSpacing(1);
		minSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt){
				if(!minSlider.getValueIsAdjusting()){
					int min = minSlider.getValue();
					imageDealer.setMin(min);
					imageDealer.dealImage();	
				}
			}
		});
		
		maxSlider.setMinorTickSpacing(1);
		maxSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt){
				if(!maxSlider.getValueIsAdjusting()){
					int max = maxSlider.getValue();
					imageDealer.setMax(max);
					imageDealer.dealImage();	
				}
			}
		});
		
		contrastSlider.setMinorTickSpacing(1);
		contrastSlider.setMajorTickSpacing(100);
		contrastSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt){
				if(!contrastSlider.getValueIsAdjusting()){
					int contr = contrastSlider.getValue();
					imageDealer.setConstrast(contr);
					imageDealer.dealImage();
				}
			}
		});
		
		contrastSliderl.setMinorTickSpacing(1);
		contrastSliderl.setMajorTickSpacing(100);
		contrastSliderl.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt){
				if(!contrastSliderl.getValueIsAdjusting()){
					int contr = contrastSliderl.getValue();
					imageDealer.setConstrastl(contr);
					imageDealer.dealImage();
				}
			}
		});
		
		contrastSliderr.setMinorTickSpacing(1);
		contrastSliderr.setMajorTickSpacing(100);
		contrastSliderr.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt){
				if(!contrastSliderr.getValueIsAdjusting()){
					int contr = contrastSliderr.getValue();
					imageDealer.setConstrastr(contr);
					imageDealer.dealImage();
				}
			}
		});
		
		contrastSlider2.setMinorTickSpacing(1);
		contrastSlider2.setMajorTickSpacing(100);
		contrastSlider2.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt){
				if(!contrastSlider2.getValueIsAdjusting()){
					int contr = contrastSlider2.getValue();
					imageDealer.setColorConstrast(contr);
					imageDealer.dealImage();
				}
			}
		});
		
		intensitySlider.setMinorTickSpacing(1);
		intensitySlider.setMajorTickSpacing(100);
		intensitySlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt){
				if(!intensitySlider.getValueIsAdjusting()){
					imageDealer.builderImageLabel.getComponentBorder();
					int r = -1;
					int rNumber = imageDealer.left.builderTable.getRowCount();
					for(int i=0;i<rNumber;i++) {
						if((boolean) imageDealer.left.builderTableModel.getValueAt(i, 1))
							r = i;
					}
					
					if(r==-1)
						return;
					
					imageDealer.left.intensityThreshold.set(r, intensitySlider.getValue());
					BuilderTableItem item = imageDealer.left.builderMap.get(r);
					item.region = imageDealer.left.getRegion(item.image,imageDealer.left.minSize.get(r),imageDealer.left.maxSize.get(r),imageDealer.left.intensityThreshold.get(r));
				}
			}
		});
		
		sizeMinSlider.setMinorTickSpacing(1);
		sizeMinSlider.setMajorTickSpacing(100);
		sizeMinSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt){
				if(!sizeMinSlider.getValueIsAdjusting()){
					imageDealer.builderImageLabel.getComponentBorder();
					int r = -1;
					int rNumber = imageDealer.left.builderTable.getRowCount();
					for(int i=0;i<rNumber;i++) {
						if((boolean) imageDealer.left.builderTableModel.getValueAt(i, 1))
							r = i;
					}
					
					if(r==-1)
						return;
					imageDealer.left.minSize.set(r, sizeMinSlider.getValue());
					BuilderTableItem item = imageDealer.left.builderMap.get(r);
					item.region = imageDealer.left.getRegion(item.image,imageDealer.left.minSize.get(r),imageDealer.left.maxSize.get(r),imageDealer.left.intensityThreshold.get(r));
				}
			}
		});
		
		sizeMaxSlider.setMinorTickSpacing(1);
		sizeMaxSlider.setMajorTickSpacing(100);
		sizeMaxSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt){
				if(!sizeMaxSlider.getValueIsAdjusting()){
					imageDealer.builderImageLabel.getComponentBorder();
					int r = -1;
					int rNumber = imageDealer.left.builderTable.getRowCount();
					for(int i=0;i<rNumber;i++) {
						if((boolean) imageDealer.left.builderTableModel.getValueAt(i, 1))
							r = i;
					}
					
					if(r==-1)
						return;
					imageDealer.left.maxSize.set(r, sizeMaxSlider.getValue());
					BuilderTableItem item = imageDealer.left.builderMap.get(r);
					item.region = imageDealer.left.getRegion(item.image,imageDealer.left.minSize.get(r),imageDealer.left.maxSize.get(r),imageDealer.left.intensityThreshold.get(r));
				}
			}
		});
		
		
		// table
		
		addToFavorite.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int nEvt = Integer.parseInt(eventText.getText());
				imageDealer.addCurve(nEvt);
				imageDealer.dealImage();
			}
			
		});
		
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int r = table.getSelectedRow();
				int nEvt = (int) table.getValueAt(r, 2);
				imageDealer.drawCurve(nEvt);
			}
		});
		
		selectAllButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int rowNumber = table.getRowCount();
				for(int r=0;r<rowNumber;r++) {
					table.setValueAt(new Boolean(true), r, 1);
				}
			}
			
		});
		
		deleteButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int rowNumber = table.getRowCount();
				DefaultTableModel model = (DefaultTableModel)table.getModel();
				for(int r=rowNumber-1;r>=0;r--) {
					boolean selected = (boolean) table.getValueAt(r, 1);
					if(selected) {
						Integer label = (Integer) table.getValueAt(r, 2);
						imageDealer.featureTableList.remove(label);
						model.removeRow(r);
					}
				}
				imageDealer.dealImage();
			}
		});
		
		details.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				ArrayList<Integer> indexLst = new ArrayList<>();
				int rowNumber = table.getRowCount();
				for(int r=0;r<rowNumber;r++) {
					int nEvt = (Integer) table.getValueAt(r, 2);
					indexLst.add(nEvt);
				}
				if(indexLst.size()>0)
					Aqua_OutPut.showDetails(indexLst,imageDealer.fts,imageDealer.opts);
			}
		});
		
		showcurvesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<Integer> indexLst = new ArrayList<>();
				int rowNumber = table.getRowCount();
				for(int r=0;r<rowNumber;r++) {
					boolean selected = (boolean) table.getValueAt(r, 1);
					if(selected) {
						int nEvt = (Integer) table.getValueAt(r, 2);
						indexLst.add(nEvt);
					}
				}
				if(indexLst.size()>0)
					ShowCurves.showCurves(indexLst,imageDealer.dffMat,imageDealer.fts);
				else
					JOptionPane.showMessageDialog(null, "Need Select First! ");
			}
		});
		
		savecurvesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose output folder");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
					System.out.println(savePath);
					saveCurve(savePath);
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				
			}
		});
		
		typeJCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int index = typeJCB.getSelectedIndex();
				if(index!=0) {
					try {
						LabelRead labelread = new LabelRead(imageDealer,index);
						labelread.setting();
						labelread.execute();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
				}
				
			}
		});
		
		updateOverlayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				setCenterBar(null,null,null);
			}
		});
		
		minSlider2.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent evt){
				setCenterBar(null,null,null);
			}
		});

		maxSlider2.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent evt){
				setCenterBar(null,null,null);
			}
		});
		
		setColorMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				SetColor setColor = new SetColor(imageDealer);
				setColor.run();
			}
		});
	}
	
	
	public void setCenterBar(Color c1, Color c2, Color c3) {
		int featureIndex = featureJCB.getSelectedIndex();
		int colorIndex = colorJCB.getSelectedIndex();
//		int transformIndex = transformJCB.getSelectedIndex();
//		int divideIndex = divideJCB.getSelectedIndex();
		int nLmk = 0;
		if(imageDealer.fts.region!=null && imageDealer.fts.region.landMark!=null&& imageDealer.fts.region.landMark.center!=null)
			nLmk = imageDealer.fts.region.landMark.center.length;
		boolean regionExist = false;
		if(imageDealer.fts.region.cell!=null && imageDealer.fts.region.cell.border!=null)
			regionExist = true;
		
		
		Color[] colors = imageDealer.labelColors;
		int nEvt = imageDealer.fts.basic.area.size();
		HashMap<Integer, Float> feature = null;
		switch(featureIndex) {
			case 0: 
				Random rv = new Random();
				int colorBase = imageDealer.colorBase;
				for(int i=0;i<imageDealer.fts.basic.area.size();i++) {
					colors[i] = new Color(colorBase + rv.nextInt(256-colorBase), colorBase + rv.nextInt(256-colorBase),colorBase + rv.nextInt(256-colorBase));
				}
				imageDealer.dealImage();
				imageDealer.center.bar.setVisible(false);
				imageDealer.center.axis.setVisible(false);
				imageDealer.labelColors = colors;
				return;
			case 1:	feature = imageDealer.fts.basic.area; break;
			case 2: feature = imageDealer.fts.basic.perimeter; break;
			case 3: feature = imageDealer.fts.basic.circMetric; break;
			case 4: feature = imageDealer.fts.curve.dffMaxPval; break;
			case 5: feature = imageDealer.fts.curve.dffMax;	break;
			case 6: feature = imageDealer.fts.curve.width55; break;
			case 7: feature = imageDealer.fts.curve.width11; break;
			case 8: feature = imageDealer.fts.curve.rise19; break;
			case 9: feature = imageDealer.fts.curve.fall91; break;
			case 10: feature = imageDealer.fts.curve.decayTau; break;
			case 11:
				feature = new HashMap<>();
				for(int i=1;i<=nEvt;i++) {
					float[] x0 = imageDealer.fts.propagation.propGrowOverall.get(i);
					float sum = 0;
					for(int k=0;k<x0.length;k++)
						sum += x0[k];
					feature.put(i, sum);
				}
				break;
			case 12:
				feature = new HashMap<>();
				for(int i=1;i<=nEvt;i++) {
					float[] x0 = imageDealer.fts.propagation.propGrowOverall.get(i);
					feature.put(i, x0[0]);
				}
				break;
			case 13:
				feature = new HashMap<>();
				for(int i=1;i<=nEvt;i++) {
					float[] x0 = imageDealer.fts.propagation.propGrowOverall.get(i);
					float sum = 0;
					for(int k=0;k<x0.length;k++)
						sum += x0[k];
					if(sum!=0)
						feature.put(i, x0[0]/sum);
					else
						feature.put(i, 0f);
				}
				break;
			case 14:
				feature = new HashMap<>();
				for(int i=1;i<=nEvt;i++) {
					float[] x0 = imageDealer.fts.propagation.propShrinkOverall.get(i);
					float sum = 0;
					for(int k=0;k<x0.length;k++)
						sum += Math.abs(x0[k]);
					feature.put(i, sum);
				}
				break;
			case 15:
				feature = new HashMap<>();
				for(int i=1;i<=nEvt;i++) {
					float[] x0 = imageDealer.fts.propagation.propShrinkOverall.get(i);
					feature.put(i, Math.abs(x0[0]));
				}
				break;
			case 16:
				feature = new HashMap<>();
				for(int i=1;i<=nEvt;i++) {
					float[] x0 = imageDealer.fts.propagation.propShrinkOverall.get(i);
					float sum = 0;
					for(int k=0;k<x0.length;k++)
						sum += x0[k];
					if(sum!=0)
						feature.put(i, Math.abs(x0[0]/sum));
					else
						feature.put(i, 0f);
				}
				break;
			case 17:
				feature = new HashMap<>();
				if(nLmk==0) {
					JOptionPane.showMessageDialog(null, "No landmark","Warning",JOptionPane.WARNING_MESSAGE); 
					return;
				}

				for(int i=1;i<=nEvt;i++) {
					feature.put(i, imageDealer.fts.region.landmarkDist.distAvg[i-1][0]);
				}
				break;
			case 18:
				feature = new HashMap<>();
				if(nLmk==0) {
					JOptionPane.showMessageDialog(null, "No landmark","Warning",JOptionPane.WARNING_MESSAGE); 
					return;
				}

				for(int i=1;i<=nEvt;i++) {
					feature.put(i, imageDealer.fts.region.landmarkDist.distMin[i-1][0]);
				}
				break;
			case 19:
				feature = new HashMap<>();
				if(nLmk==0) {
					JOptionPane.showMessageDialog(null, "No landmark","Warning",JOptionPane.WARNING_MESSAGE); 
					return;
				}

				for(int i=1;i<=nEvt;i++) {
					feature.put(i, imageDealer.fts.region.landmarkDir.chgToward[i-1][0]);
				}
				break;
			case 20:
				feature = new HashMap<>();
				if(nLmk==0) {
					JOptionPane.showMessageDialog(null, "No landmark","Warning",JOptionPane.WARNING_MESSAGE); 
					return;
				}

				for(int i=1;i<=nEvt;i++) {
					feature.put(i, imageDealer.fts.region.landmarkDir.chgAway[i-1][0]);
				}
				break;
			case 21:
				feature = new HashMap<>();
				if(!regionExist) {
					JOptionPane.showMessageDialog(null, "No Region","Warning",JOptionPane.WARNING_MESSAGE); 
					return;
				}

				for(int i=1;i<=nEvt;i++) {
					float minX0 = Float.MAX_VALUE;
					float[] xx0 = imageDealer.fts.region.cell.dist2border[i-1];
					for(int t=0;t<xx0.length;t++) {
						if(!Float.isNaN(xx0[t]))
							minX0 = Math.min(minX0, xx0[t]);
					}
					feature.put(i, minX0);
				}
				break;
			case 22:
				feature = new HashMap<>();
				if(!regionExist) {
					JOptionPane.showMessageDialog(null, "No Region","Warning",JOptionPane.WARNING_MESSAGE); 
					return;
				}

				for(int i=1;i<=nEvt;i++) {
					float minX0 = Float.MAX_VALUE;
					float[] xx0 = imageDealer.fts.region.cell.dist2borderNorm[i-1];
					for(int t=0;t<xx0.length;t++) {
						if(!Float.isNaN(xx0[t]))
							minX0 = Math.min(minX0, xx0[t]);
					}
					feature.put(i, minX0);
				}
				break;
			case 23:
				feature = new HashMap<>();
				for(int i=1;i<=nEvt;i++) {
					feature.put(i, (float)imageDealer.fts.network.nOccurSameLoc[i-1][0]);
				}
				break;
			case 24:
				feature = new HashMap<>();
				for(int i=1;i<=nEvt;i++) {
					feature.put(i, (float)imageDealer.fts.network.nOccurSameLoc[i-1][1]);
				}
				break;
			case 25:
				feature = new HashMap<>();
				for(int i=1;i<=nEvt;i++) {
					feature.put(i, (float)imageDealer.fts.network.nOccurSameTime[i-1]);
				}
				break;
			default: return;
		}
		
		float maxF = -Float.MAX_VALUE;
		float minF = Float.MAX_VALUE;
		
		
		
		Color cStart = c1;
		Color cMid = c2;
		Color cEnd = c3;
		
		if(c1==null) {
			switch(colorIndex) {
				case 1:	cStart = new Color(0,255,0); cEnd = new Color(255,0,0); cMid = new Color(128,128,0); break;
				case 2: cStart = new Color(255,0,0); cEnd = new Color(0,0,255); cMid = new Color(128,0,128); break;
				case 3: cStart = new Color(255,0,0); cEnd = new Color(0,0,255); cMid = new Color(255,255,0); break;
				case 4: cStart = new Color(255,255,0); cEnd = new Color(0,0,255); cMid = new Color(0,255,0); break;
				default: 
					Random rv = new Random();
					cStart = new Color(rv.nextInt(256),rv.nextInt(256),rv.nextInt(256));
					cMid = new Color(rv.nextInt(256),rv.nextInt(256),rv.nextInt(256));
					cEnd = new Color(rv.nextInt(256),rv.nextInt(256),rv.nextInt(256));
			}
		}
		int min = minSlider2.getValue();
		int max = maxSlider2.getValue();
		
		imageDealer.center.bar.setColorLabel(cStart, cMid, cEnd, min, max);
		float[] features = new float[feature.size()];
		for(int i=1;i<=feature.size();i++) {
			float value = feature.get(i);
			if(transformJCB.getSelectedIndex() == 1)
				value = (float) Math.sqrt(value);
			else if(transformJCB.getSelectedIndex() == 2)
				value = (float) Math.log10(value);
			
			float size = 1;
			if(divideJCB.getSelectedIndex() == 1)
				size = imageDealer.fts.basic.area.get(i);
			else if(divideJCB.getSelectedIndex() == 2)
				size = (float) Math.sqrt(imageDealer.fts.basic.area.get(i));
			
			value /= size;
			maxF = Math.max(maxF, value);
			minF = Math.min(minF, value);
			features[i-1] = value;
		}
		
		int dR = cMid.getRed() - cStart.getRed();
		int dG = cMid.getGreen() - cStart.getGreen();
		int dB = cMid.getBlue() - cStart.getBlue();
		
		int dR2 = cEnd.getRed() - cMid.getRed();
		int dG2= cEnd.getGreen() - cMid.getGreen();
		int dB2 = cEnd.getBlue() - cMid.getBlue();
		int width = imageDealer.center.bar.getWidth();
		imageDealer.center.bar.setVisible(true);
		
		int mid = (min+max)/2;
		for(int i=0;i<features.length;i++) {
			float value = features[i];
			int pos = (int) ((value - minF)/(maxF - minF)*width);
			if(pos<min)
				colors[i] = cStart;
			else if(pos>max)
				colors[i] = cEnd;
			else if(pos<mid)
				colors[i] = new Color(cStart.getRed() + (pos-min)*dR/(mid - min),cStart.getGreen() + (pos-min)*dG/(mid - min),cStart.getBlue() + (pos-min)*dB/(mid - min));
			else
				colors[i] = new Color(cMid.getRed() + (pos-mid)*dR2/(width - mid),cMid.getGreen() + (pos-mid)*dG2/(width - mid),cMid.getBlue() + (pos-mid)*dB2/(width - mid));
		}
		
		imageDealer.labelColors = colors;
		
		// TextBar JLabel axis = new JLabel();
		float midF = (maxF+minF)/2;
		imageDealer.center.minLabel.setText("" + minF);
		imageDealer.center.midLabel.setText("" + midF);
		imageDealer.center.maxLabel.setText("" + maxF);
		imageDealer.center.axis.setVisible(true);
		imageDealer.dealImage();
	}
	
	public void allFinished() {
		right3.setVisible(true);
		right4.setVisible(true);
		featureJCB.addItem("Basic - Area");
		featureJCB.addItem("Basic - Perimeter");
		featureJCB.addItem("Basic - Circurlarity");
		featureJCB.addItem("Curve - P Value on max Dff (-log10)");
		featureJCB.addItem("Curve - Max Dff");
		featureJCB.addItem("Curve - Duration 50% to 50%");
		featureJCB.addItem("Curve - Duration 10% to 10%");
		featureJCB.addItem("Curve - Risng duration 10% to 90%");
		featureJCB.addItem("Curve - Decaying duration 90% to 10%");
		featureJCB.addItem("Curve - Decay Tau");
		featureJCB.addItem("Propagation - Onset - Overall");
		featureJCB.addItem("Propagation - Onset - One Direction");
		featureJCB.addItem("Propagation - Onset - One Direction - Ratio");
		featureJCB.addItem("Propagation - Offset - Overall");
		featureJCB.addItem("Propagation - Offset - One Direction");
		featureJCB.addItem("Propagation - Offset - One Direction - Ratio");
		featureJCB.addItem("Landmark - Event Average Distance");
		featureJCB.addItem("Landmark - Event Minimum Distance");
		featureJCB.addItem("Landmark - Event Toward Distance");
		featureJCB.addItem("Landmark - Event Away Distance");
		featureJCB.addItem("Region - event centroid distance to border");
		featureJCB.addItem("Region - event centroid distance to border - normalized by region radius");
		featureJCB.addItem("Network - Temporal density");
		featureJCB.addItem("Network - Temporal density with similar size only");
		featureJCB.addItem("Network - Spatial density");
		featureJCB.setBackground(Color.WHITE);
		featureJCB.setEnabled(true);
		colorJCB.addItem("GreenRed");
		colorJCB.addItem("RdBu");
		colorJCB.addItem("RdYlBu");
		colorJCB.addItem("YlGnBu");
		colorJCB.setBackground(Color.WHITE);
		colorJCB.setEnabled(true);
		transformJCB.addItem("Square Root");
		transformJCB.addItem("Log10");
		transformJCB.setBackground(Color.WHITE);
		transformJCB.setEnabled(true);
		divideJCB.addItem("Size");
		divideJCB.addItem("SqrtSize");
		divideJCB.setBackground(Color.WHITE);
		divideJCB.setEnabled(true);
		proDirJCB.addItem("Posterior");
		proDirJCB.addItem("Lateral Left");
		proDirJCB.addItem("Lateral Right");
		proDirJCB.setBackground(Color.WHITE);
		proDirJCB.setEnabled(true);
		landIDLabel2.setEnabled(true);
		updateOverlayButton.setEnabled(true);
    	setColorMap.setEnabled(true);
	}
	
	public void saveCurve(String savePath) {
		JLabel canvas = imageDealer.center.resultsLabel;
		BufferedImage img=new BufferedImage(canvas.getWidth(),canvas.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = img.createGraphics();
		canvas.printAll(g2d);
		File f=new File(savePath + ".jpg");
		try {
			ImageIO.write(img, "jpg", f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		g2d.dispose();

		
	}
	public JPanel createPanel() {
		setting();
		layout();
		layout1();
		
		addButtonListeners();		
		return rightGroup;
		
	}
}
