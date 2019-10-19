package va.vt.cbil;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

class LeftGroupPanel {
	ImageDealer imageDealer = null;
	MyImageLabel imageLabel = null;
	
	JPanel leftGroup = new JPanel();
	JPanel left1 = new JPanel();
	JPanel left2 = new JPanel();	
	
	// left1
	JLabel dirLabel = new JLabel(" Direction, region, landmarks");
	JLabel cellBoundary = new JLabel("Cell boundary");
	JLabel landmark = new JLabel("Landmark (soma)");
	JToggleButton addLeft11 = new JToggleButton("+");
	JToggleButton addLeft12 = new JToggleButton("+");
	JToggleButton removeLeft11 = new JToggleButton("-");
	JToggleButton removeLeft12 = new JToggleButton("-");
	JToggleButton name11 = new JToggleButton("Name");
	JToggleButton name12 = new JToggleButton("Name");
	JToggleButton drawAnterior = new JToggleButton("Draw anterior");
	JButton save11 = new JButton("Save");
	JButton save12 = new JButton("Save");
	JButton load11 = new JButton("Load");
	JButton load12 = new JButton("Load");
	JButton maskBuilder = new JButton("Mask builder");
	JButton updateFeatures = new JButton("Update features");
	JPanel left11 = new JPanel();
	JPanel left12 = new JPanel();
	JPanel left13 = new JPanel();
	
	JLabel rowBlank1 = new JLabel();
	
	// left2
	JLabel detLabel = new JLabel(" Detection pipeline");
	JTabbedPane jTP = new JTabbedPane();
	JPanel jTP1 = new JPanel();
	JPanel jTP2 = new JPanel();
	JPanel jTP3 = new JPanel();
	JPanel jTP4 = new JPanel();
	JPanel jTP5 = new JPanel();
	JPanel jTP6 = new JPanel();
	JPanel jTP7 = new JPanel();
	
	int curStatus = 0;
	int jTPStatus = 0;
	JButton backButton = new JButton("Back");
	JButton runButton = new JButton("Run");
	JButton nextButton = new JButton("Next");
	JPanel left2buttons = new JPanel();
	JButton saveopts = new JButton("SaveOpts");
	JButton loadopts = new JButton("LoadOpts");
	JButton runAllButton = new JButton("RunAllSteps");
	JPanel jTPButtons = new JPanel();
	JLabel blankLabel = new JLabel();
	
	// jTP1
	JTextField jTF11 = new JTextField("2");
	JTextField jTF12 = new JTextField("0.5");
	JTextField jTF13 = new JTextField("8");
	JLabel jTPL11 = new JLabel(" Intensity threshold scaling factor");
	JLabel jTPL12 = new JLabel(" Smoothing (sigma)");
	JLabel jTPL13 = new JLabel(" Minimum size (pixels)");
	
	// jTP2
	JTextField jTF21 = new JTextField("2");
	JTextField jTF22 = new JTextField("1");
	JLabel jTPL21 = new JLabel(" Temporal cut threshold");
	JLabel jTPL22 = new JLabel(" Growing z threshold");
	
	// jTP3
	JTextField jTF31 = new JTextField("2");
	JTextField jTF32 = new JTextField("2");
	JTextField jTF33 = new JTextField("1");
	JLabel jTPL31 = new JLabel(" Rising time uncertainty");
	JLabel jTPL32 = new JLabel(" Slowest delay in propagation");
	JLabel jTPL33 = new JLabel(" Propagation smoothness");
	
	// jTP4
	JTextField jTF41 = new JTextField("2");
	JLabel jTPL41 = new JLabel(" Z score threshold");
	
	// jTP5
	JCheckBox jTF51 = new JCheckBox("",true);
	JLabel jTPL51 = new JLabel(" Ignore merging");
	JTextField jTF52 = new JTextField("0");
	JTextField jTF53 = new JTextField("0");
	JTextField jTF54 = new JTextField("2");
	JLabel jTPL52 = new JLabel(" Maximum distance");
	JLabel jTPL53 = new JLabel(" Minimum correlation");
	JLabel jTPL54 = new JLabel(" Maximum time difference");
	
	// jTP6
	JCheckBox jTF61 = new JCheckBox("",false);
	JLabel jTPL61 = new JLabel(" Temporally extend events");
	
	// jTP7
	JCheckBox jTF71 = new JCheckBox("",true);
	JLabel jTPL71 = new JLabel(" Ignore delay Tau");
	
	JLabel rowBlank2 = new JLabel();
	
	// Left 3
	JLabel proofReading = new JLabel(" Proof reading");
	JToggleButton viewFavourite = new JToggleButton("view/favourite");
	JToggleButton deleteRestore = new JToggleButton("delete/restore");
	JButton addAllFiltered = new JButton("addAllFiltered");
	JButton featuresPlot = new JButton("featuresPlot");
	JPanel left3 = new JPanel();
	JPanel left3Buttons = new JPanel();
	JPanel left3Buttons2 = new JPanel();
	DefaultTableModel model = null;
	JTable table = null;
	JScrollPane tablePane = null;
	Object[][] tableData = null;

	JLabel rowBlank3 = new JLabel();
	
	// Left 4
	JPanel left4 = new JPanel();
	JLabel export = new JLabel(" Export");
	JCheckBox events = new JCheckBox(" Events and features",true);
	JCheckBox movie = new JCheckBox(" Movies with overlay",true);
	JButton exportButton = new JButton("Export/Save");
	JButton restart = new JButton("Restart");
	JLabel blank2 = new JLabel();
	
	MyImageLabel leftImageLabel = null;
	MyImageLabel rightImageLabel = null;
	

	// Make Builder
	JPanel builderLeft1 = new JPanel();
	JLabel loadMasks = new JLabel(" Load masks");
	JLabel region = new JLabel(" Region");
	JLabel regionMaker = new JLabel(" Region maker"); 
	JLabel landMark = new JLabel(" Landmark");
	JButton self1 = new JButton("Self");
	JButton self2 = new JButton("Self");
	JButton self3 = new JButton("Self");
	JButton folder1 = new JButton("Folder");
	JButton folder2 = new JButton("Folder");
	JButton folder3 = new JButton("Folder");
	JButton file1 = new JButton("File");
	JButton file2 = new JButton("File");
	JButton file3 = new JButton("File");
	JPanel builderLeft11 = new JPanel();
	JPanel builderLeft12 = new JPanel();
	JPanel builderLeft13 = new JPanel();
	JScrollPane builderTablePane = null;
	JTable builderTable = null;
	DefaultTableModel builderTableModel = null;
	JButton builderRemove = new JButton("Remove");
	JPanel builderManual = new JPanel();
	JLabel buiderManualLabel = new JLabel("Manually Select");
	JButton builderMClear = new JButton("Clear");
	JToggleButton builderMAdd = new JToggleButton("Add");
	JToggleButton builderMRemove = new JToggleButton("Remove");
	JPanel builderLeft1Buttons = new JPanel();
	
	JPanel builderLeft2 = new JPanel();
	JLabel saveRegionsLanmarks = new JLabel(" Save regions/landmarks");
	JLabel role = new JLabel(" Role of region markers");
	JLabel combineRegion = new JLabel(" Combine region masks");
	JLabel combineLandmark = new JLabel(" Combine landmark masks");
	String[] roleString = new String[] {"Segment region","Remove region"};
	JComboBox<String> roleJCB = new JComboBox<String>(roleString);
	String[] combineRegionString = new String[] {"Or","And"};
	JComboBox<String> combineRegionJCB = new JComboBox<String>(combineRegionString);
	String[] combineLandmarkString = new String[] {"Or","And"};
	JComboBox<String> combineLandmarkJCB = new JComboBox<String>(combineLandmarkString);
	JPanel builderLeft21 = new JPanel();
	JPanel builderLeft22 = new JPanel();
	JPanel builderLeft23 = new JPanel();
	JButton apply = new JButton("Apply & Back");
	JButton discard = new JButton("Discard & Back");
	JPanel builderLeft2Buttons = new JPanel();
	
	ArrayList<BuilderTableItem> builderMap = new ArrayList<>();
	ArrayList<Integer> intensityThreshold = new ArrayList<>();
	ArrayList<Integer> minSize = new ArrayList<>();
	ArrayList<Integer> maxSize = new ArrayList<>();
	
	DrawListener drawlistener = null;
	DrawListener drawlistener2 = null;
	BuilderDrawListener builderDrawListener = null;
	RemoveListener removeListener = null;
	RemoveListener removeListener2 = null;
	NameListenerRegion nameListenerRegion = null;
	NameListenerLandMark nameListenerLandMark = null;
	BuilderRemoveListener builderRemoveListener = null;
	
	public LeftGroupPanel(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
		imageLabel = imageDealer.getImageLabel();
	}
	
	public void setting() {
		jTF11.setText(imageDealer.opts.thrARScl+"");
		jTF12.setText(imageDealer.opts.smoXY + "");
		jTF13.setText(imageDealer.opts.minSize + "");;
		
		jTF21.setText(imageDealer.opts.thrTWScl + "");
		jTF22.setText(imageDealer.opts.thrExtZ + "");
		
		jTF31.setText(imageDealer.opts.cRise + "");
		jTF32.setText(imageDealer.opts.cDelay + "");
		jTF33.setText(imageDealer.opts.gtwSmo + "");
		
		jTF41.setText(imageDealer.opts.zThr + "");
		
		jTF51.setSelected(imageDealer.opts.ignoreMerge==1);
		jTF52.setText(imageDealer.opts.mergeEventDiscon + "");
		jTF53.setText(imageDealer.opts.mergeEventCorr + "");
		jTF54.setText(imageDealer.opts.mergeEventMaxTimeDif + "");
		
		jTF61.setSelected(imageDealer.opts.extendEvtRe==1);
		
		jTF71.setSelected(imageDealer.opts.ignoreTau==1);
		
		
		addLeft11.setMargin(new Insets(0,0,0,0));
		addLeft12.setMargin(new Insets(0,0,0,0));
		removeLeft11.setMargin(new Insets(0,0,0,0));
		removeLeft12.setMargin(new Insets(0,0,0,0));
		name11.setMargin(new Insets(0,0,0,0));
		name12.setMargin(new Insets(0,0,0,0));
		save11.setMargin(new Insets(0,0,0,0));
		save12.setMargin(new Insets(0,0,0,0));
		load11.setMargin(new Insets(0,0,0,0));
		load12.setMargin(new Insets(0,0,0,0));
		
		// left1
		dirLabel.setOpaque(true);
		dirLabel.setBackground(UI_Beauty.blue);
		dirLabel.setForeground(Color.WHITE);
		dirLabel.setPreferredSize(new Dimension(400,20));
		updateFeatures.setEnabled(false);
		cellBoundary.setPreferredSize(new Dimension(120,20));
		landmark.setPreferredSize(new Dimension(120,20));
		addLeft11.setPreferredSize(new Dimension(30,20));
		addLeft12.setPreferredSize(new Dimension(30,20));
		save11.setPreferredSize(new Dimension(60,20));
		save12.setPreferredSize(new Dimension(60,20));
		load11.setPreferredSize(new Dimension(60,20));
		load12.setPreferredSize(new Dimension(60,20));
		removeLeft11.setPreferredSize(new Dimension(30,20));
		removeLeft12.setPreferredSize(new Dimension(30,20));
		name11.setPreferredSize(new Dimension(65,20));
		name12.setPreferredSize(new Dimension(65,20));
		drawAnterior.setPreferredSize(new Dimension(125,20));
		maskBuilder.setPreferredSize(new Dimension(125,20));
		updateFeatures.setPreferredSize(new Dimension(125,20));
		
//		int fontsize = 10;
//		drawAnterior.setFont(new Font("Courier", Font.BOLD, 12));
//		maskBuilder.setFont(new Font("Courier", Font.BOLD, 12));
//		updateFeatures.setFont(new Font("Courier", Font.BOLD, 10));
//		
//		
//		cellBoundary.setFont(new Font("Courier", Font.BOLD, 12));
//		landmark.setFont(new Font("Courier", Font.BOLD, 12));
//		addLeft11.setFont(new Font("Courier", Font.BOLD, 12));
//		addLeft12.setFont(new Font("Courier", Font.BOLD, 12));
//		save11.setFont(new Font("Courier", Font.BOLD, 12));
//		save12.setFont(new Font("Courier", Font.BOLD, 12));
//		load11.setFont(new Font("Courier", Font.BOLD, 12));
//		load12.setFont(new Font("Courier", Font.BOLD, 12));
//		name11.setFont(new Font("Courier", Font.BOLD, 12));
//		name12.setFont(new Font("Courier", Font.BOLD, 12));
//		removeLeft11.setFont(new Font("Courier", Font.BOLD, 12));
//		removeLeft12.setFont(new Font("Courier", Font.BOLD, 12));
		// left2
		detLabel.setOpaque(true);
		detLabel.setBackground(UI_Beauty.blue);
		detLabel.setForeground(Color.WHITE);
		detLabel.setPreferredSize(new Dimension(400,20));
			
		// TabbedPane
		jTP.setPreferredSize(new Dimension(400,150));
		
		blankLabel.setPreferredSize(new Dimension(150,20));
    	backButton.setEnabled(false);
    	nextButton.setEnabled(false);
    	
			// jTP1
		jTF11.setPreferredSize(new Dimension(80,20));
    	jTF12.setPreferredSize(new Dimension(80,20));
    	jTF13.setPreferredSize(new Dimension(80,20));
    	jTF11.setHorizontalAlignment(JTextField.CENTER);
    	jTF12.setHorizontalAlignment(JTextField.CENTER);
    	jTF13.setHorizontalAlignment(JTextField.CENTER);
    	jTPL11.setPreferredSize(new Dimension(270,20));
    	jTPL12.setPreferredSize(new Dimension(270,20));
    	jTPL13.setPreferredSize(new Dimension(270,20));
    	jTPL11.setFont(new Font("Courier", Font.BOLD, 13));
    	jTPL12.setFont(new Font("Courier", Font.BOLD, 13));
    	jTPL13.setFont(new Font("Courier", Font.BOLD, 13));
    		
    		// jTP2
    	jTF21.setPreferredSize(new Dimension(80,20));
    	jTF22.setPreferredSize(new Dimension(80,20));
    	jTF21.setHorizontalAlignment(JTextField.CENTER);
    	jTF22.setHorizontalAlignment(JTextField.CENTER);
    	jTPL21.setPreferredSize(new Dimension(270,20));
    	jTPL22.setPreferredSize(new Dimension(270,20));
    	jTPL21.setFont(new Font("Courier", Font.BOLD, 13));
    	jTPL22.setFont(new Font("Courier", Font.BOLD, 13));
    	
    		// jTP3
    	jTF31.setPreferredSize(new Dimension(80,20));
    	jTF32.setPreferredSize(new Dimension(80,20));
    	jTF33.setPreferredSize(new Dimension(80,20));
    	jTF31.setHorizontalAlignment(JTextField.CENTER);
    	jTF32.setHorizontalAlignment(JTextField.CENTER);
    	jTF33.setHorizontalAlignment(JTextField.CENTER);
    	jTPL31.setPreferredSize(new Dimension(270,20));
    	jTPL32.setPreferredSize(new Dimension(270,20));
    	jTPL33.setPreferredSize(new Dimension(270,20));
    	jTPL31.setFont(new Font("Courier", Font.BOLD, 13));
    	jTPL32.setFont(new Font("Courier", Font.BOLD, 13));
    	jTPL33.setFont(new Font("Courier", Font.BOLD, 13));

    	
    		// jTP4
    	jTF41.setPreferredSize(new Dimension(80,20));
    	jTF41.setHorizontalAlignment(JTextField.CENTER);
    	jTPL41.setPreferredSize(new Dimension(270,20));
    	jTPL41.setFont(new Font("Courier", Font.BOLD, 13));
    	
    		// jTP5
    	jTF52.setPreferredSize(new Dimension(80,20));
    	jTF53.setPreferredSize(new Dimension(80,20));
    	jTF54.setPreferredSize(new Dimension(80,20));
    	jTF52.setHorizontalAlignment(JTextField.CENTER);
    	jTF53.setHorizontalAlignment(JTextField.CENTER);
    	jTF54.setHorizontalAlignment(JTextField.CENTER);
    	jTPL51.setPreferredSize(new Dimension(270,20));
    	jTPL52.setPreferredSize(new Dimension(270,20));
    	jTPL53.setPreferredSize(new Dimension(270,20));
    	jTPL54.setPreferredSize(new Dimension(270,20));
    	jTPL51.setFont(new Font("Courier", Font.BOLD, 13));
    	jTPL52.setFont(new Font("Courier", Font.BOLD, 13));
    	jTPL53.setFont(new Font("Courier", Font.BOLD, 13));
    	jTPL54.setFont(new Font("Courier", Font.BOLD, 13));
    	
    		// jTP6
    	jTPL61.setPreferredSize(new Dimension(270,20));
    	jTPL61.setFont(new Font("Courier", Font.BOLD, 13));
    	
    		// jTP7
    	jTPL71.setPreferredSize(new Dimension(270,20));
    	jTPL71.setFont(new Font("Courier", Font.BOLD, 13));
    	// left2
    	
    	jTP.add("Signal", jTP1);
    	jTP.add("Voxel", jTP2);
    	jTP.add("Event", jTP3);
    	jTP.add("Clean", jTP4);
    	jTP.add("Merge", jTP5);
    	jTP.add("Recon", jTP6);
    	jTP.add("Fea", jTP7);
    	
    	jTP.setFont(new Font("Courier", Font.BOLD, 12));
    	
    	jTP.setEnabledAt(1, false);
    	jTP.setEnabledAt(2, false);
    	jTP.setEnabledAt(3, false);
    	jTP.setEnabledAt(4, false);
    	jTP.setEnabledAt(5, false);
    	jTP.setEnabledAt(6, false);
    	
    	
    	
    	
//    	backButton.setFont(new Font("Courier", Font.BOLD, 12));
//    	runButton.setFont(new Font("Courier", Font.BOLD, 12));
//    	nextButton.setFont(new Font("Courier", Font.BOLD, 12));
//    	saveopts.setFont(new Font("Courier", Font.BOLD, 12));
//    	loadopts.setFont(new Font("Courier", Font.BOLD, 12));
//    	runAllButton.setFont(new Font("Courier", Font.BOLD, 12));

    	// left 3
    	proofReading.setOpaque(true);
    	proofReading.setBackground(UI_Beauty.blue);
    	proofReading.setForeground(Color.WHITE);
    	proofReading.setPreferredSize(new Dimension(400,20));
    	viewFavourite.setPreferredSize(new Dimension(190,25));
    	deleteRestore.setPreferredSize(new Dimension(190,25));
    	addAllFiltered.setPreferredSize(new Dimension(190,25));
    	featuresPlot.setPreferredSize(new Dimension(190,25));
    	
//    	viewFavourite.setMargin(new Insets(0,0,0,0));
//    	deleteRestore.setMargin(new Insets(0,0,0,0));
//    	deleteRestore.setMargin(new Insets(0,0,0,0));
//    	addAllFiltered.setMargin(new Insets(0,0,0,0));
//    	featuresPlot.setMargin(new Insets(0,0,0,0));

    	viewFavourite.setFont(new Font("Courier", Font.BOLD, 12));
    	deleteRestore.setFont(new Font("Courier", Font.BOLD, 12));
    	addAllFiltered.setFont(new Font("Courier", Font.BOLD, 12));
    	featuresPlot.setFont(new Font("Courier", Font.BOLD, 12));
    		// Table
    	setTable();
    	
    	// left 4
    	export.setOpaque(true);
    	export.setBackground(UI_Beauty.blue);
    	export.setForeground(Color.WHITE);
    	exportButton.setPreferredSize(new Dimension(120,25));
    	blank2.setPreferredSize(new Dimension(120,25));
    	restart.setPreferredSize(new Dimension(120,25));
    	export.setPreferredSize(new Dimension(400,20));
    	events.setPreferredSize(new Dimension(380,30));
    	movie.setPreferredSize(new Dimension(380,30));
    	
//    	events.setFont(new Font("Courier", Font.BOLD, 14));
//    	movie.setFont(new Font("Courier", Font.BOLD, 14));
    	
//    	exportButton.setMargin(new Insets(0,0,0,0));
//    	restart.setMargin(new Insets(0,0,0,0));
//    	
    	exportButton.setFont(new Font("Courier", Font.BOLD, 12));
    	restart.setFont(new Font("Courier", Font.BOLD, 12));
    	
    	rowBlank1.setPreferredSize(new Dimension(400,15));
    	rowBlank2.setPreferredSize(new Dimension(400,15));
    	rowBlank3.setPreferredSize(new Dimension(400,15));
    	
    	
    	
    	
    	// Builder
    	loadMasks.setOpaque(true);
    	loadMasks.setBackground(UI_Beauty.blue);
    	loadMasks.setForeground(Color.WHITE);
    	loadMasks.setPreferredSize(new Dimension(400,20));
    	
    	saveRegionsLanmarks.setOpaque(true);
    	saveRegionsLanmarks.setBackground(UI_Beauty.blue);
    	saveRegionsLanmarks.setForeground(Color.WHITE);
    	saveRegionsLanmarks.setPreferredSize(new Dimension(400,20));
    	
    	region.setPreferredSize(new Dimension(100,20));
    	self1.setPreferredSize(new Dimension(80,20));
    	folder1.setPreferredSize(new Dimension(80,20));
    	file1.setPreferredSize(new Dimension(80,20));
    	region.setFont(new Font("Courier", Font.BOLD, 12));
    	self1.setFont(new Font("Courier", Font.BOLD, 12));
    	folder1.setFont(new Font("Courier", Font.BOLD, 12));
    	file1.setFont(new Font("Courier", Font.BOLD, 12));
    	
    	regionMaker.setPreferredSize(new Dimension(100,20));
    	self2.setPreferredSize(new Dimension(80,20));
    	folder2.setPreferredSize(new Dimension(80,20));
    	file2.setPreferredSize(new Dimension(80,20));
    	regionMaker.setFont(new Font("Courier", Font.BOLD, 12));
    	self2.setFont(new Font("Courier", Font.BOLD, 12));
    	folder2.setFont(new Font("Courier", Font.BOLD, 12));
    	file2.setFont(new Font("Courier", Font.BOLD, 12));
    	
    	landMark.setPreferredSize(new Dimension(100,20));
    	self3.setPreferredSize(new Dimension(80,20));
    	folder3.setPreferredSize(new Dimension(80,20));
    	file3.setPreferredSize(new Dimension(80,20));
    	landMark.setFont(new Font("Courier", Font.BOLD, 12));
    	self3.setFont(new Font("Courier", Font.BOLD, 12));
    	folder3.setFont(new Font("Courier", Font.BOLD, 12));
    	file3.setFont(new Font("Courier", Font.BOLD, 12));
    	
    	setBuilderTable();
    	builderRemove.setPreferredSize(new Dimension(100,20));
    	buiderManualLabel.setPreferredSize(new Dimension(100,20));
    	builderMClear.setPreferredSize(new Dimension(80,20));
    	builderMAdd.setPreferredSize(new Dimension(80,20));
    	builderMRemove.setPreferredSize(new Dimension(80,20));
    	
    	builderRemove.setFont(new Font("Courier", Font.BOLD, 12));
    	buiderManualLabel.setFont(new Font("Courier", Font.BOLD, 10));
    	builderMClear.setFont(new Font("Courier", Font.BOLD, 12));
    	builderMAdd.setFont(new Font("Courier", Font.BOLD, 12));
    	builderMRemove.setFont(new Font("Courier", Font.BOLD, 12));
    	
    	role.setPreferredSize(new Dimension(180,20));
    	combineRegion.setPreferredSize(new Dimension(180,20));
    	combineLandmark.setPreferredSize(new Dimension(180,20));
    	role.setFont(new Font("Courier", Font.BOLD, 12));
    	combineRegion.setFont(new Font("Courier", Font.BOLD, 12));
    	combineLandmark.setFont(new Font("Courier", Font.BOLD, 12));
    	
    	roleJCB.setPreferredSize(new Dimension(180,20));
    	combineRegionJCB.setPreferredSize(new Dimension(180,20));
    	combineLandmarkJCB.setPreferredSize(new Dimension(180,20));
    	roleJCB.setBackground(Color.WHITE);
    	combineRegionJCB.setBackground(Color.WHITE);
    	combineLandmarkJCB.setBackground(Color.WHITE);
    	apply.setPreferredSize(new Dimension(150,20));
    	discard.setPreferredSize(new Dimension(150,20));
    	apply.setFont(new Font("Courier", Font.BOLD, 12));
    	discard.setFont(new Font("Courier", Font.BOLD, 12));

	}
	
	private void setBuilderTable(){
		builderTableModel = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;
    		@Override
    		public boolean isCellEditable(int row, int column) {
    			if(column == 1)
    				return true;
    			else
    				return false;
    		}
    	};
    	builderTableModel.addColumn("");
    	builderTableModel.addColumn("");
    	builderTableModel.addColumn("Mask name");
    	builderTableModel.addColumn("Type");
    	builderTable = new JTable(builderTableModel){
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Class getColumnClass(int column) {
				switch (column) {
					case 0:	return String.class;
					case 1: return Boolean.class;
					case 2: return String.class;
					case 3: return String.class;
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
    	builderTable.setDefaultRenderer(Object.class, tcr);
    	builderTable.getColumnModel().getColumn(0).setPreferredWidth(15);
    	builderTable.getColumnModel().getColumn(1).setPreferredWidth(15);
    	builderTable.setSize(new Dimension(380,300));
    	builderTablePane = new JScrollPane(builderTable);
    	builderTablePane.setPreferredSize(new Dimension(380,303));
    	builderTablePane.setOpaque(true);
    	builderTablePane.setBackground(Color.WHITE);
    	
    	builderMap.add(new BuilderTableItem(imageDealer.avgImage,"foreground",imageDealer));
    	builderMap.add(new BuilderTableItem(imageDealer.avgImage,"background",imageDealer));
	}
	
	private void setTable(){
		// Table
    	tableData = new Object[5][5];
    	tableData[0] = new Object[] {new Integer(1),new Boolean(false),"Area (um^2)",new Float(0),new Float(0)};
    	tableData[1] = new Object[] {new Integer(2),new Boolean(false),"dF/F",new Float(0),new Float(0)};
    	tableData[2] = new Object[] {new Integer(3),new Boolean(false),"Durations (s)",new Float(0),new Float(0)};
    	tableData[3] = new Object[] {new Integer(4),new Boolean(false),"P value(dffMax)",new Float(0),new Float(0)};
    	tableData[4] = new Object[] {new Integer(5),new Boolean(false),"Decay Tau",new Float(0),new Float(0)};
		String[] columnNames = {"","","Feature","Min","Max"};
    	model = new DefaultTableModel(tableData,columnNames) {
			private static final long serialVersionUID = 1L;
    		@Override
    		public boolean isCellEditable(int row, int column) {
    			if(column == 1)
    				return true;
    			else
    				return true;
    		}
    	};
    	table = new JTable(model){
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Class getColumnClass(int column) {
				switch (column) {
					case 0:	return String.class;
					case 1: return Boolean.class;
					case 2: return String.class;
					case 3: return Float.class;
					case 4: return Float.class;
					default: return Boolean.class;
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
    	tablePane.setPreferredSize(new Dimension(380,110));
    	tablePane.setOpaque(true);
    	tablePane.setBackground(Color.WHITE);
	}
	
	public void layout2() {
		leftGroup.removeAll();
		
		GridBagPut settingLeftGroup = new GridBagPut(leftGroup);
		settingLeftGroup.putGridBag(builderLeft1, leftGroup, 0, 0);
		settingLeftGroup.putGridBag(rowBlank1, leftGroup, 0, 1);
		settingLeftGroup.putGridBag(builderLeft2, leftGroup, 0, 2);

	}
	
	public void layout1() {
		leftGroup.removeAll();
		
		GridBagPut settingLeftGroup = new GridBagPut(leftGroup);
		settingLeftGroup.putGridBag(left1, leftGroup, 0, 0);
		settingLeftGroup.putGridBag(rowBlank1, leftGroup, 0, 1);
		settingLeftGroup.putGridBag(left2, leftGroup, 0, 2);
		settingLeftGroup.putGridBag(rowBlank2, leftGroup, 0, 3);
		settingLeftGroup.putGridBag(left3, leftGroup, 0, 4);
		settingLeftGroup.putGridBag(rowBlank3, leftGroup, 0, 5);
		settingLeftGroup.putGridBag(left4, leftGroup, 0, 6);

	}
	
	public void layout() {
		// left1
		left11.add(cellBoundary);
		left11.add(addLeft11);
		left11.add(removeLeft11);
		left11.add(name11);
		left11.add(save11);
		left11.add(load11);
		
		left12.add(landmark);
		left12.add(addLeft12);
		left12.add(removeLeft12);
		left12.add(name12);
		left12.add(save12);
		left12.add(load12);
		left13.add(drawAnterior);
		left13.add(maskBuilder);
		left13.add(updateFeatures);
		GridBagPut settingleft1 = new GridBagPut(left1);
		settingleft1.putGridBag(dirLabel, left1, 0, 0);
		settingleft1.putGridBag(left11, left1, 0, 1);
		settingleft1.putGridBag(left12, left1, 0, 2);
		settingleft1.putGridBag(left13, left1, 0, 3);
		left1.setBorder(BorderFactory.createEtchedBorder());
		
		// jTP1
		GridBagPut jTPsetting1 = new GridBagPut(jTP1);
    	jTPsetting1.setAnchorNorthWest();
    	jTPsetting1.fillBoth();
    	jTPsetting1.putGridBag(jTF11, jTP1, 0, 0);
    	jTPsetting1.putGridBag(jTF12, jTP1, 0, 1);
    	jTPsetting1.putGridBag(jTF13, jTP1, 0, 2);
    	jTPsetting1.putGridBag(jTPL11, jTP1, 1, 0);
    	jTPsetting1.putGridBag(jTPL12, jTP1, 1, 1);
    	jTPsetting1.putGridBag(jTPL13, jTP1, 1, 2);
    	jTP1.setBorder(BorderFactory.createTitledBorder("Active voxels"));
    	
		// jTP2
		GridBagPut jTPsetting2 = new GridBagPut(jTP2);
		jTPsetting2.setAnchorNorthWest();
		jTPsetting2.fillBoth();
		jTPsetting2.putGridBag(jTF21, jTP2, 0, 0);
		jTPsetting2.putGridBag(jTF22, jTP2, 0, 1);
		jTPsetting2.putGridBag(jTPL21, jTP2, 1, 0);
		jTPsetting2.putGridBag(jTPL22, jTP2, 1, 1);
    	jTP2.setBorder(BorderFactory.createTitledBorder("Super voxels"));
    	
    	// jTP3
    	GridBagPut jTPsetting3 = new GridBagPut(jTP3);
    	jTPsetting3.setAnchorNorthWest();
    	jTPsetting3.fillBoth();
		jTPsetting3.putGridBag(jTF31, jTP3, 0, 0);
		jTPsetting3.putGridBag(jTF32, jTP3, 0, 1);
		jTPsetting3.putGridBag(jTF33, jTP3, 0, 2);
		jTPsetting3.putGridBag(jTPL31, jTP3, 1, 0);
		jTPsetting3.putGridBag(jTPL32, jTP3, 1, 1);
		jTPsetting3.putGridBag(jTPL33, jTP3, 1, 2);
    	jTP3.setBorder(BorderFactory.createTitledBorder("Super events and events"));
    	
    	// jTP4
    	GridBagPut jTPsetting4 = new GridBagPut(jTP4);
    	jTPsetting4.setAnchorNorthWest();
    	jTPsetting4.fillBoth();
    	jTPsetting4.putGridBag(jTF41, jTP4, 0, 0);
    	jTPsetting4.putGridBag(jTPL41, jTP4, 1, 0);
    	jTP4.setBorder(BorderFactory.createTitledBorder("False positive control"));
    	
    	// jTP5
    	GridBagPut jTPsetting5 = new GridBagPut(jTP5);
    	jTPsetting5.setAnchorNorthWest();
    	jTPsetting5.fillBoth();
    	jTPsetting5.putGridBag(jTF51, jTP5, 0, 0);
    	jTPsetting5.putGridBag(jTPL51, jTP5, 1, 0);
    	jTPsetting5.putGridBag(jTF52, jTP5, 0, 1);
    	jTPsetting5.putGridBag(jTPL52, jTP5, 1, 1);
    	jTPsetting5.putGridBag(jTF53, jTP5, 0, 2);
    	jTPsetting5.putGridBag(jTPL53, jTP5, 1, 2);
    	jTPsetting5.putGridBag(jTF54, jTP5, 0, 3);
    	jTPsetting5.putGridBag(jTPL54, jTP5, 1, 3);
    	jTP4.setBorder(BorderFactory.createTitledBorder("Merging"));
    	
    	// jTP6
    	GridBagPut jTPsetting6 = new GridBagPut(jTP6);
    	jTPsetting6.setAnchorNorthWest();
    	jTPsetting6.fillBoth();
    	jTPsetting6.putGridBag(jTF61, jTP6, 0, 0);
    	jTPsetting6.putGridBag(jTPL61, jTP6, 1, 0);
		jTP7.setBorder(BorderFactory.createTitledBorder("Reconstruct after merging"));
    	
    	// jTP7
    	GridBagPut jTPsetting7 = new GridBagPut(jTP7);
    	jTPsetting7.setAnchorNorthWest();
		jTPsetting7.fillBoth();
		jTPsetting7.putGridBag(jTF71, jTP7, 0, 0);
		jTPsetting7.putGridBag(jTPL71, jTP7, 1, 0);
		jTP7.setBorder(BorderFactory.createTitledBorder("Feature extraction"));
		
		// left2
		jTPButtons.add(blankLabel);
    	jTPButtons.add(backButton);
		jTPButtons.add(runButton);
		jTPButtons.add(nextButton);
		left2buttons.add(saveopts);
		left2buttons.add(loadopts);
		left2buttons.add(runAllButton);
		GridBagPut settingleft2 = new GridBagPut(left2);
		settingleft2.putGridBag(detLabel, left2, 0, 0);
		settingleft2.putGridBag(jTP, left2, 0, 1);
		settingleft2.putGridBag(jTPButtons, left2, 0, 2);
		settingleft2.putGridBag(left2buttons, left2, 0, 3);
		left2.setBorder(BorderFactory.createEtchedBorder());
		
		// left 3
		left3Buttons.add(viewFavourite);
		left3Buttons.add(deleteRestore);
		left3Buttons2.add(addAllFiltered);
		left3Buttons2.add(featuresPlot);
		GridBagPut settingleft3 = new GridBagPut(left3);
		settingleft3.putGridBag(proofReading, left3, 0, 0);
		settingleft3.putGridBag(left3Buttons, left3, 0, 1);
		settingleft3.putGridBag(tablePane, left3, 0, 2);
		settingleft3.putGridBag(left3Buttons2, left3, 0, 3);


    	
		left3.setBorder(BorderFactory.createEtchedBorder());
		
		// left 4
		JPanel exportPanel = new JPanel();
		exportPanel.add(exportButton);
		exportPanel.add(blank2);
		exportPanel.add(restart);
		GridBagPut settingleft4 = new GridBagPut(left4);
		settingleft4.putGridBag(export, left4, 0, 0);
		settingleft4.putGridBag(events, left4, 0, 1);
		settingleft4.putGridBag(movie, left4, 0, 2);
		settingleft4.putGridBag(exportPanel, left4, 0, 3);
		left4.setBorder(BorderFactory.createEtchedBorder());
		
		// Whole
		
		
		left3.setVisible(false);
		left4.setVisible(false);
		
		builderLeft11.add(region);
		builderLeft11.add(self1);
		builderLeft11.add(folder1);
		builderLeft11.add(file1);
		builderLeft12.add(regionMaker);
		builderLeft12.add(self2);
		builderLeft12.add(folder2);
		builderLeft12.add(file2);
		builderLeft13.add(landMark);
		builderLeft13.add(self3);
		builderLeft13.add(folder3);
		builderLeft13.add(file3);
		builderLeft1Buttons.add(builderRemove);
		builderManual.add(buiderManualLabel);
		builderManual.add(builderMClear);
		builderManual.add(builderMAdd);
		builderManual.add(builderMRemove);
		GridBagPut builderSettint1 = new GridBagPut(builderLeft1);
		builderSettint1.setAnchorNorthWest();
		builderSettint1.fillBoth();
		builderSettint1.putGridBag(loadMasks, builderLeft1, 0, 0);
		builderSettint1.putGridBag(builderLeft11, builderLeft1, 0, 1);
		builderSettint1.putGridBag(builderLeft12, builderLeft1, 0, 2);
		builderSettint1.putGridBag(builderLeft13, builderLeft1, 0, 3);
		builderSettint1.putGridBag(builderTablePane, builderLeft1, 0, 4);
		builderSettint1.putGridBag(builderLeft1Buttons, builderLeft1, 0, 5);
		builderSettint1.putGridBag(builderManual, builderLeft1, 0, 6);
		builderLeft1.setBorder(BorderFactory.createEtchedBorder());
		
		

		
		builderLeft21.add(role);
		builderLeft21.add(roleJCB);
		builderLeft22.add(combineRegion);
		builderLeft22.add(combineRegionJCB);
		builderLeft23.add(combineLandmark);
		builderLeft23.add(combineLandmarkJCB);
		builderLeft2Buttons.add(apply);
		builderLeft2Buttons.add(discard);
		
		GridBagPut builderSettint2 = new GridBagPut(builderLeft2);
		builderSettint2.setAnchorNorthWest();
		builderSettint2.fillBoth();
		builderSettint2.putGridBag(saveRegionsLanmarks, builderLeft2, 0, 0);
		builderSettint2.putGridBag(builderLeft21, builderLeft2, 0, 1);
		builderSettint2.putGridBag(builderLeft22, builderLeft2, 0, 2);
		builderSettint2.putGridBag(builderLeft23, builderLeft2, 0, 3);
		builderSettint2.putGridBag(builderLeft2Buttons, builderLeft2, 0, 4);
		builderLeft2.setBorder(BorderFactory.createEtchedBorder());
	}
	
		
	public void addButtonListeners() {
//		ArrayList<ArrayList<Point>> list1 = imageLabel.getList1();
		drawlistener = new DrawListener(imageLabel,addLeft11,imageDealer,imageDealer.regionMark);
		Color color1 = new Color(0,191,255);
		drawlistener.setColor(color1);
		imageLabel.addMouseListener(drawlistener);
		imageLabel.addMouseMotionListener(drawlistener);
		addLeft11.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if(addLeft11.isSelected()) {
					removeLeft11.setSelected(false);
					name11.setSelected(false);
					addLeft12.setSelected(false);
					removeLeft12.setSelected(false);
					name12.setSelected(false);
					drawlistener.setValid(true);
					imageLabel.setValid1(true);
					imageDealer.center.pauseButton.doClick();
					imageDealer.center.panButton.setSelected(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					drawlistener.setValid(false);
					drawlistener.clearPoints();
					imageLabel.setValid1(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//					changeRegionStatus(imageDealer.regionMark,list1);
					imageLabel.repaint();
					dealBuilderRegion();
				}
			}
			
		});		
		
//		ArrayList<ArrayList<Point>> list2 = imageLabel.getList2();
		drawlistener2 = new DrawListener(imageLabel,addLeft12,imageDealer,imageDealer.landMark);
		Color color2 = new Color(255,185,15);
		drawlistener2.setColor(color2);
		imageLabel.addMouseListener(drawlistener2);
		imageLabel.addMouseMotionListener(drawlistener2);
		addLeft12.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if(addLeft12.isSelected()) {
					addLeft11.setSelected(false);
					removeLeft11.setSelected(false);
					name11.setSelected(false);
					removeLeft12.setSelected(false);
					name12.setSelected(false);
					drawlistener2.setValid(true);
					imageLabel.setValid2(true);
					imageDealer.center.pauseButton.doClick();
					imageDealer.center.panButton.setSelected(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					drawlistener2.setValid(false);
					imageLabel.setValid2(false);
					drawlistener2.clearPoints();
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					dealBuilderLandMark();
					imageLabel.repaint();
//					changeRegionStatus(imageDealer.landMark,list2);
				}
			}
			
		});	
		
		removeListener = new RemoveListener(imageLabel, imageDealer,imageDealer.regionMark, imageDealer.regionMarkLabel);
		imageLabel.addMouseListener(removeListener);
		removeLeft11.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if(removeLeft11.isSelected()) {
					addLeft11.setSelected(false);
					name11.setSelected(false);
					addLeft12.setSelected(false);
					removeLeft12.setSelected(false);
					name12.setSelected(false);
					removeListener.setValid(true);
					imageDealer.center.pauseButton.doClick();
					imageDealer.center.panButton.setSelected(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					removeListener.setValid(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageLabel.repaint();
//					changeRegionStatus(imageDealer.regionMark,list1);
				}
			}
			
		});		
			
		
		removeListener2 = new RemoveListener(imageLabel, imageDealer, imageDealer.landMark,imageDealer.landMarkLabel);
		imageLabel.addMouseListener(removeListener2);
		removeLeft12.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if(removeLeft12.isSelected()) {
					addLeft11.setSelected(false);
					removeLeft11.setSelected(false);
					name11.setSelected(false);
					addLeft12.setSelected(false);
					name12.setSelected(false);
					removeListener2.setValid(true);
					imageDealer.center.pauseButton.doClick();
					imageDealer.center.panButton.setSelected(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					removeListener2.setValid(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageLabel.repaint();
//					changeRegionStatus(imageDealer.landMark,list2);
				}
			}
			
		});		
		
		nameListenerRegion = new NameListenerRegion(imageLabel, imageDealer, imageDealer.regionMarkLabel);
		imageLabel.addMouseListener(nameListenerRegion);
		name11.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if(name11.isSelected()) {
					addLeft11.setSelected(false);
					removeLeft11.setSelected(false);
					addLeft12.setSelected(false);
					removeLeft12.setSelected(false);
					name12.setSelected(false);
					nameListenerRegion.setValid(true);
					imageDealer.center.pauseButton.doClick();
					imageDealer.center.panButton.setSelected(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					nameListenerRegion.setValid(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageLabel.repaint();
//					changeRegionStatus(imageDealer.landMark,list2);
				}
			}
			
		});		
		
		nameListenerLandMark = new NameListenerLandMark(imageLabel, imageDealer, imageDealer.landMarkLabel);
		imageLabel.addMouseListener(nameListenerLandMark);
		name12.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if(name12.isSelected()) {
					addLeft11.setSelected(false);
					removeLeft11.setSelected(false);
					name11.setSelected(false);
					addLeft12.setSelected(false);
					removeLeft12.setSelected(false);
					nameListenerLandMark.setValid(true);
					imageDealer.center.pauseButton.doClick();
					imageDealer.center.panButton.setSelected(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					nameListenerLandMark.setValid(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageLabel.repaint();
//					changeRegionStatus(imageDealer.landMark,list2);
				}
			}
			
		});		
		
		
		jTP.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				curStatus = jTP.getSelectedIndex();
				if(curStatus==jTPStatus || curStatus==6)
					nextButton.setEnabled(false);
				else
					nextButton.setEnabled(true);
				
				if(curStatus==0)
					backButton.setEnabled(false);
				else
					backButton.setEnabled(true);
				
				if(curStatus == 6) {
					runButton.setText("Extract");
				}else {
					runButton.setText("Run");
				}
				
				if(curStatus == 7) {
					nextButton.setEnabled(false);
					backButton.setEnabled(true);
					left3.setVisible(true);
					left4.setVisible(true);
					imageDealer.right.allFinished();
					updateFeatures.setEnabled(true);
					float[] ftsTable = null;
					try {
						FileInputStream fi = null;
						ObjectInputStream oi = null;
						
						fi = new FileInputStream(new File(imageDealer.proPath + "FtsTableParameters.txt"));	
						oi = new ObjectInputStream(fi);
						ftsTable = (float[])oi.readObject();
						oi.close();
						fi.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
					tableValueSetting(ftsTable[0],ftsTable[1],ftsTable[2],ftsTable[3],ftsTable[4],ftsTable[5],ftsTable[6],ftsTable[7],ftsTable[8],ftsTable[9]);
				}
				
			}
		});
		
		maskBuilder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				layout2();
//				imageDealer.center.layout2();
//				imageDealer.right.layout2();
//				imageDealer.window.revalidate();
				new Thread(new Runnable() {

					@Override
					public void run() {
						layout2();
						imageDealer.center.layout2();
						imageDealer.right.layout2();
						imageDealer.window.revalidate();
					}
					
				}).start();
			}
		});
		
		discard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				layout1();
//				imageDealer.center.layout1();
//				imageDealer.right.layout1();
//				imageDealer.window.revalidate();
				
				new Thread(new Runnable() {

					@Override
					public void run() {
						layout1();
						imageDealer.center.layout1();
						imageDealer.right.layout1();
						imageDealer.window.revalidate();
						imageDealer.dealImage();
					}
					
				}).start();
			}
		});
		
		apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dealBuilder();
				new Thread(new Runnable() {

					@Override
					public void run() {
						layout1();
						imageDealer.center.layout1();
						imageDealer.right.layout1();
						dealBuilder();
						imageDealer.window.revalidate();
						imageDealer.dealImage();
					}
					
				}).start();
				
				
			}
		});
		
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(curStatus) {
					case 0:
						float thrArscl = Float.parseFloat(jTF11.getText());
						float sigma = Float.parseFloat(jTF12.getText());
						int minSize = Integer.parseInt(jTF13.getText());
						imageDealer.setSignalProcessingParameters(thrArscl, sigma, minSize);
						imageDealer.signalProcessing();
						break;
					case 1:
						float thrTWScl = Float.parseFloat(jTF21.getText());
						float thrExtZ = Float.parseFloat(jTF22.getText());
						imageDealer.setStep2(thrTWScl, thrExtZ);
						imageDealer.step2Start();
						break;
					case 2:
						int cRise = Integer.parseInt(jTF31.getText());
						int cDelay = Integer.parseInt(jTF32.getText());
						float gtwSmo = Float.parseFloat(jTF33.getText());
						imageDealer.setStep3(cRise, cDelay, gtwSmo);
						imageDealer.step3Start();
						break;
					case 3:
						int zThr = Integer.parseInt(jTF41.getText());
						imageDealer.setStep4(zThr);
						imageDealer.step4Start();
						break;
					case 4:
						int ignoreMerge = jTF51.isSelected()?1:0;
						int mergeEventDiscon = Integer.parseInt(jTF52.getText());
						float mergeEventCorr = Float.parseFloat(jTF53.getText());
						int mergeEventMaxTimeDif = Integer.parseInt(jTF54.getText());
						imageDealer.setStep5(ignoreMerge, mergeEventDiscon, mergeEventCorr,mergeEventMaxTimeDif);
						imageDealer.step5Start();
						break;
					case 5:
						int extendEvtRe = jTF61.isSelected()?1:0;
						imageDealer.setStep6(extendEvtRe);
						imageDealer.step6Start();
						break;
					case 6:
						boolean isChecked = jTF71.isSelected();
						imageDealer.setStep7(isChecked);
						imageDealer.step7Start();
						break;
				}	
			}
		});
		
		updateFeatures.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean isChecked = jTF71.isSelected();
				imageDealer.setStep7(isChecked);
				imageDealer.step7Start();
			}
		});
		
		
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				curStatus++;
				jTP.setSelectedIndex(curStatus);
			}
		});
		
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				curStatus--;
				jTP.setSelectedIndex(curStatus);
			}
		});
		
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean eventsExtract = events.isSelected();
				boolean movieExtract = movie.isSelected();
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose output folder");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
					System.out.println(savePath);
					imageDealer.export(eventsExtract,movieExtract,savePath);
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				
			}
		});
		
		saveopts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("csv","csv");
				chooser.setFileFilter(filter);
//				chooser.setDialogTitle("Select Option File Path");
//				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
					imageDealer.exportOpts(savePath);
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				
			}
		});
		
		loadopts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("csv","csv");
				chooser.setFileFilter(filter);
//				chooser.setDialogTitle("Select Option File Path");
//				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
					imageDealer.loadOpts(savePath);
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				
			}
		});
		
		save11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("ser","ser");
				chooser.setFileFilter(filter);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				    String outputPath = null;
				    if(savePath.substring(savePath.length()-4).equals(".ser"))
						outputPath = savePath;
					else
						outputPath = savePath + ".ser";
				    try {
						FileOutputStream f = null;
						ObjectOutputStream o = null;
						
						f = new FileOutputStream(new File(outputPath));
						o = new ObjectOutputStream(f);
						o.writeObject(imageDealer.regionMark);
						System.out.println("Save Region");
						o.close();
						f.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
			}
		});
		
		load11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("ser","ser");
				chooser.setFileFilter(filter);
				chooser.setAcceptAllFileFilterUsed(false);
				boolean[][] regionMark = null;
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				    try {
				    	FileInputStream fi = null;
						ObjectInputStream oi = null;
						
						fi = new FileInputStream(new File(savePath));	
						oi = new ObjectInputStream(fi);
						regionMark = (boolean[][])oi.readObject();
						System.out.println("Load Region");
						oi.close();
						fi.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				    int width = regionMark.length;
				    int height = regionMark[0].length;
				    boolean[][] region = imageDealer.regionMark;
				    for(int x=0;x<width;x++) {
				    	for(int y=0;y<height;y++) {
				    		region[x][y] = regionMark[x][y];
				    	}
				    }
					imageLabel.repaint();
					dealBuilderRegion();
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
			}
		});
		
		save12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("ser","ser");
				chooser.setFileFilter(filter);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				    String outputPath = null;
				    if(savePath.substring(savePath.length()-4).equals(".ser"))
						outputPath = savePath;
					else
						outputPath = savePath + ".ser";
				    try {
						FileOutputStream f = null;
						ObjectOutputStream o = null;
						
						f = new FileOutputStream(new File(outputPath));
						o = new ObjectOutputStream(f);
						o.writeObject(imageDealer.landMark);
						System.out.println("Save LandMark");
						o.close();
						f.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
			}
		});
		
		load12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("ser","ser");
				chooser.setFileFilter(filter);
				chooser.setAcceptAllFileFilterUsed(false);
				boolean[][] regionMark = null;
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				    try {
				    	FileInputStream fi = null;
						ObjectInputStream oi = null;
						
						fi = new FileInputStream(new File(savePath));	
						oi = new ObjectInputStream(fi);
						regionMark = (boolean[][])oi.readObject();
						System.out.println("Load LandMark");
						oi.close();
						fi.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				    int width = regionMark.length;
				    int height = regionMark[0].length;
				    boolean[][] region = imageDealer.landMark;
				    for(int x=0;x<width;x++) {
				    	for(int y=0;y<height;y++) {
				    		region[x][y] = regionMark[x][y];
				    	}
				    }
					imageLabel.repaint();
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
			}
		});
		
			
		
		runAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				float thrArscl = Float.parseFloat(jTF11.getText());
				float sigma = Float.parseFloat(jTF12.getText());
				int minSize = Integer.parseInt(jTF13.getText());
				imageDealer.setSignalProcessingParameters(thrArscl, sigma, minSize);
				
				float thrTWScl = Float.parseFloat(jTF21.getText());
				float thrExtZ = Float.parseFloat(jTF22.getText());
				imageDealer.setStep2(thrTWScl, thrExtZ);
				
				int cRise = Integer.parseInt(jTF31.getText());
				int cDelay = Integer.parseInt(jTF32.getText());
				float gtwSmo = Float.parseFloat(jTF33.getText());
				imageDealer.setStep3(cRise, cDelay, gtwSmo);
				
				int zThr = Integer.parseInt(jTF41.getText());
				imageDealer.setStep4(zThr);
				int ignoreMerge = jTF51.isSelected()?1:0;
				int mergeEventDiscon = Integer.parseInt(jTF52.getText());
				int mergeEventCorr = Integer.parseInt(jTF53.getText());
				int mergeEventMaxTimeDif = Integer.parseInt(jTF54.getText());
				imageDealer.setStep5(ignoreMerge, mergeEventDiscon, mergeEventCorr,mergeEventMaxTimeDif);

				int extendEvtRe = jTF61.isSelected()?1:0;
				imageDealer.setStep6(extendEvtRe);

				boolean isChecked = jTF71.isSelected();
				imageDealer.setStep7(isChecked);
				
				new Thread(new Runnable() {

					@Override
					public void run() {
						jTP.setSelectedIndex(0);
						boolean startRun = true;
						while(true) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if(!imageDealer.running) {
//								System.out.println("run");						
								try {
									if(startRun) {
										runButton.doClick();
										Thread.sleep(200);
										startRun = false;
									}else {
										nextButton.doClick();
										Thread.sleep(200);
										runButton.doClick();
										Thread.sleep(200);
									}
									
									if(curStatus==6)
										break;
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
//								System.out.println("next");
								
								
//								Thread.sleep(100);
								
							}
						}
					}
				}).start();
				
//				imageDealer.runAllSteps();
				
			}
		});
		
		restart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO:
				imageDealer.window.dispose();
				AquaWelcome begin = new AquaWelcome();
		    	try {
					begin.run();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		ViewFeatureListener viewListener = new ViewFeatureListener(imageDealer,imageDealer.imageLabel);
		ViewFeatureListener viewListener1 = new ViewFeatureListener(imageDealer,imageDealer.center.leftImageLabel);
		ViewFeatureListener viewListener2 = new ViewFeatureListener(imageDealer,imageDealer.center.rightImageLabel);
		imageLabel.addMouseListener(viewListener);
		imageDealer.center.leftImageLabel.addMouseListener(viewListener1);
		imageDealer.center.rightImageLabel.addMouseListener(viewListener2);
		viewFavourite.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				if(viewFavourite.isSelected()) {
					viewListener.setValid(true);
					viewListener1.setValid(true);
					viewListener2.setValid(true);
					deleteRestore.setSelected(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					imageDealer.center.leftImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					imageDealer.center.rightImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}else {
					viewListener.setValid(false);
					viewListener1.setValid(false);
					viewListener2.setValid(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageDealer.center.leftImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageDealer.center.rightImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
				}
			}
			
		});
		
		
		DeleteButtonListener deleteListener = new DeleteButtonListener(imageDealer,imageDealer.imageLabel);
		DeleteButtonListener deleteListener1 = new DeleteButtonListener(imageDealer,imageDealer.center.leftImageLabel);
		DeleteButtonListener deleteListener2 = new DeleteButtonListener(imageDealer,imageDealer.center.rightImageLabel);
		imageLabel.addMouseListener(deleteListener);
		imageDealer.center.leftImageLabel.addMouseListener(deleteListener1);
		imageDealer.center.rightImageLabel.addMouseListener(deleteListener2);
		deleteRestore.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				if(deleteRestore.isSelected()) {
					deleteListener.setValid(true);
					deleteListener1.setValid(true);
					deleteListener2.setValid(true);
					viewFavourite.setSelected(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					imageDealer.center.leftImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					imageDealer.center.rightImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}else {
					deleteListener.setValid(false);
					deleteListener1.setValid(false);
					deleteListener2.setValid(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageDealer.center.leftImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageDealer.center.rightImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
				}
			}
			
		});
		
		DrawAnteriorListener drawAnteriorListener = new DrawAnteriorListener(imageLabel,imageDealer);
		imageLabel.addMouseListener(drawAnteriorListener);
		imageLabel.addMouseMotionListener(drawAnteriorListener);
		drawAnterior.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(drawAnterior.isSelected()) {
					drawAnteriorListener.setValid(true);

					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					drawAnteriorListener.setValid(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
		
		
		self1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BuilderTableItem item = new BuilderTableItem(imageDealer.avgImage,"region", imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
		});
		
		self2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BuilderTableItem item = new BuilderTableItem(imageDealer.avgImage,"region mark", imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
		});
		
		self3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BuilderTableItem item = new BuilderTableItem(imageDealer.avgImage,"landmark", imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
		});
		
		file1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose file (same size)");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				String savePath = null;
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				BuilderTableItem item = new BuilderTableItem(savePath,"region", imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
		});
		
		file2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose file (same size)");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				String savePath = null;
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				BuilderTableItem item = new BuilderTableItem(savePath,"region mark", imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
		});
		
		file3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose file (same size)");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				String savePath = null;
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				BuilderTableItem item = new BuilderTableItem(savePath,"landmark", imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
		});
		
		folder1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose folder");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				String savePath = null;
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				File folder = new File(savePath);
				File[] listOfFiles = folder.listFiles();
				BuilderTableItem item = new BuilderTableItem(savePath,listOfFiles,"region",(int)imageDealer.getOrigWidth(),(int)imageDealer.getOrigHeight(), imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
			
		});
		
		folder2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose folder");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				String savePath = null;
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				File folder = new File(savePath);
				File[] listOfFiles = folder.listFiles();
				BuilderTableItem item = new BuilderTableItem(savePath,listOfFiles,"region mark",(int)imageDealer.getOrigWidth(),(int)imageDealer.getOrigHeight(), imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
			
		});
		
		folder3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose folder");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				String savePath = null;
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				File folder = new File(savePath);
				File[] listOfFiles = folder.listFiles();
				BuilderTableItem item = new BuilderTableItem(savePath,listOfFiles,"landmark",(int)imageDealer.getOrigWidth(),(int)imageDealer.getOrigHeight(), imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
			
		});
		
		
		builderTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int r = builderTable.getSelectedRow();
				int rNumber = builderTable.getRowCount();
				for(int i=0;i<rNumber;i++) {
					builderTableModel.setValueAt(new Boolean(false), i, 1);
				}
				builderTableModel.setValueAt(new Boolean(true), r, 1);
				imageDealer.curBuilderImage = builderMap.get(r).image;
				imageDealer.builderImageLabel.getComponentBorder(builderMap.get(r).region);
				imageDealer.dealBuilderImageLabel();
//				imageDealer.right.intensitySlider.setValue(intensityThreshold.get(r));
//				imageDealer.right.sizeMinSlider.setValue(minSize.get(r));
//				imageDealer.right.sizeMaxSlider.setValue(maxSize.get(r));
				
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int nEvt = imageDealer.fts.basic.area.size();
				imageDealer.deleteColorSet2 = new HashSet<>();
				if((boolean) table.getValueAt(0, 1)) {
					float min = (float) table.getValueAt(0, 3);
					float max = (float) table.getValueAt(0, 4);
					for(int i=1;i<=nEvt;i++) {
						float value = imageDealer.fts.basic.area.get(i);
						if(value<min || value > max) {
							imageDealer.deleteColorSet2.add(i);
						}
					}
				}
				if((boolean) table.getValueAt(1, 1)) {
					float min = (float) table.getValueAt(1, 3);
					float max = (float) table.getValueAt(1, 4);
					for(int i=1;i<=nEvt;i++) {
						float value = imageDealer.fts.curve.dffMax.get(i);
						if(value<min || value > max) {
							imageDealer.deleteColorSet2.add(i);
						}
					}
				}
				if((boolean) table.getValueAt(2, 1)) {
					float min = (float) table.getValueAt(2, 3);
					float max = (float) table.getValueAt(2, 4);
					for(int i=1;i<=nEvt;i++) {
						float value = imageDealer.fts.curve.duration.get(i);
						if(value<min || value > max) {
							imageDealer.deleteColorSet2.add(i);
						}
					}
				}
				if((boolean) table.getValueAt(3, 1)) {
					float min = (float) table.getValueAt(3, 3);
					float max = (float) table.getValueAt(3, 4);
					for(int i=1;i<=nEvt;i++) {
						float value = imageDealer.fts.curve.dffMaxPval.get(i);
						if(value<min || value > max) {
							imageDealer.deleteColorSet2.add(i);
						}
					}
				}
				if((boolean) table.getValueAt(4, 1)) {
					float min = (float) table.getValueAt(4, 3);
					float max = (float) table.getValueAt(4, 4);
					for(int i=1;i<=nEvt;i++) {
						float value = imageDealer.fts.curve.decayTau.get(i);
						if(value<min || value > max) {
							imageDealer.deleteColorSet2.add(i);
						}
					}
				}
				imageDealer.dealImage();
			}
		});
		
		builderRemove.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				int r = -1;
				int rNumber = builderTable.getRowCount();
				for(int i=0;i<rNumber;i++) {
					if((boolean) builderTableModel.getValueAt(i, 1))
						r = i;
				}
				
				if(r==-1)
					return;
				
				builderTableModel.removeRow(r);
				builderMap.remove(r);
				intensityThreshold.remove(r);
				minSize.remove(r);
				maxSize.remove(r);
				
				for(int i=0;i<rNumber-1;i++) {
					builderTableModel.setValueAt(new Integer(i+1), i, 0);
				}
			}
		});
		
		builderMClear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				int r = -1;
				int rNumber = builderTable.getRowCount();
				for(int i=0;i<rNumber;i++) {
					if((boolean) builderTableModel.getValueAt(i, 1))
						r = i;
				}
				
				if(r==-1)
					return;
				
				boolean[][] region = builderMap.get(r).region;
				int width = region.length;
				int height = region[0].length;
				builderMap.get(r).region = new boolean[width][height];
				imageDealer.builderImageLabel.getComponentBorder(builderMap.get(r).region);
				imageDealer.dealBuilderImageLabel();
			}
		});
		
		builderDrawListener = new BuilderDrawListener(imageDealer.builderImageLabel,builderMAdd,imageDealer);
		drawlistener.setColor(color1);
		imageDealer.builderImageLabel.addMouseListener(builderDrawListener);
		imageDealer.builderImageLabel.addMouseMotionListener(builderDrawListener);
		builderMAdd.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				int r = -1;
				int rNumber = builderTable.getRowCount();
				for(int i=0;i<rNumber;i++) {
					if((boolean) builderTableModel.getValueAt(i, 1))
						r = i;
				}
				
				if(r==-1)
					return;
				
				boolean[][] region = builderMap.get(r).region;
				builderDrawListener.setRegion(region);
				// TODO Auto-generated method stub
				if(builderMAdd.isSelected()) {
					builderMRemove.setSelected(false);
					builderDrawListener.setValid(true);
					imageDealer.builderImageLabel.setValid1(true);
					imageDealer.builderImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					builderDrawListener.setValid(false);
					builderDrawListener.clearPoints();
					imageDealer.builderImageLabel.setValid1(false);
					imageDealer.builderImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					builderMap.get(r).region = region;
					imageDealer.builderImageLabel.getComponentBorder(builderMap.get(r).region);
				}
			}
			
		});		
		
		builderRemoveListener = new BuilderRemoveListener(imageDealer.builderImageLabel);
		imageDealer.builderImageLabel.addMouseListener(builderRemoveListener);
		builderMRemove.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				int r = -1;
				int rNumber = builderTable.getRowCount();
				for(int i=0;i<rNumber;i++) {
					if((boolean) builderTableModel.getValueAt(i, 1))
						r = i;
				}
				
				if(r==-1)
					return;
				
				boolean[][] region = builderMap.get(r).region;
				builderRemoveListener.setRegion(region);
				// TODO Auto-generated method stub
				if(builderMRemove.isSelected()) {
					builderMAdd.setSelected(false);
					builderRemoveListener.setValid(true);
					imageDealer.builderImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					builderRemoveListener.setValid(false);
					imageDealer.builderImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageDealer.builderImageLabel.repaint();
					builderMap.get(r).region = region;
					imageDealer.builderImageLabel.getComponentBorder(builderMap.get(r).region);
//					changeRegionStatus(imageDealer.regionMark,list1);
				}
			}
			
		});
		
		
		addAllFiltered.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int nEvt = imageDealer.fts.basic.area.size();
				for(int i=1;i<=nEvt;i++) {
					if((imageDealer.deleteColorSet2.contains(i)) || (imageDealer.featureTableList.contains(i)))
						continue;
					
					imageDealer.featureTableList.add(i);
//					System.out.println(featureTableList.size());
					int rowNumber = imageDealer.right.table.getRowCount();
					int frame = imageDealer.fts.curve.tBegin.get(i);
					float size = imageDealer.fts.basic.area.get(i);
					float duration = imageDealer.fts.curve.duration.get(i);
					float dffMax = imageDealer.fts.curve.dffMax.get(i);
					float tau = imageDealer.fts.curve.decayTau.get(i); 
					imageDealer.right.model.addRow(new Object[] {new Integer(rowNumber+1),new Boolean(false),new Integer(i),new Integer(frame+1),new Float(size),new Float(duration),new Float(dffMax),new Float(tau)});

				}
				imageDealer.dealImage();
			}
			
		});
		
		featuresPlot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DrawFeatures draw = new DrawFeatures(imageDealer);
			}
			
		});
	}
	
//	public void changeRegionStatus(boolean[][] region, ArrayList<ArrayList<Point>> list) {
//		for(ArrayList<Point> points:list) {
//			int minX = Integer.MAX_VALUE;
//			int minY = Integer.MAX_VALUE;
//			int maxX = Integer.MIN_VALUE;
//			int maxY = Integer.MIN_VALUE;
//			for(Point p:points) {
//				minX = (int) Math.min(minX, p.getX());
//				minY = (int) Math.min(minY, p.getY());
//				maxX = (int) Math.max(maxX, p.getX());
//				maxY = (int) Math.max(maxY, p.getY());
//			}
//			
//			for(int i=minX;i<=maxX;i++) {
//				for(int j=minY;j<=maxY;j++) {
//					if(!region[i][j]&&judgePointInPolygon(i,j,points))
//						region[i][j] = true;
//				}
//			}
//		}
//		
//		HashMap<Integer, ArrayList<Integer>> regionCC = ConnectedComponents.twoPassConnect2D_ForBuilder(region,4);
//		
//		list = new ArrayList<>();
//		
//		imageLabel.repaint();
//	}
	
	public boolean judgePointInPolygon(int xx, int yy, ArrayList<Point> list) {
		boolean result = false;
		
		int number = list.size();
		double px = xx;
		double py = yy;
		
		for(int i = 0,j = number-1;i<number;j=i,i++) {
			double sx = list.get(i).getX();
			double sy = list.get(i).getY();
			double tx = list.get(j).getX();
			double ty = list.get(j).getY();
			
			if((sx == px && sy == py)||(tx==px && ty == py)) {
				return true;
			}
			
			if((sy < py && ty >= py) || (sy >= py && ty < py)) {
				double x = sx + (py - sy) * (tx - sx) / (ty - sy);
				if(x == px)
					return true;
				if(x > px)
					result = !result;
			}
		}	
		return result;	
	}
	
	public JPanel createPanel() {
		setting();
		layout();
		layout1();
		
		addButtonListeners();
		
		// Test

//		jTPStatus = 6;
////		nextButton.setEnabled(true);
//		Random rv = new Random();
//		imageDealer.changeSignalDrawRegionStatus();
//		imageDealer.labelColors = new Color[40000];
//		for(int i=0;i<imageDealer.labelColors.length;i++) {
//			imageDealer.labelColors[i] = new Color(rv.nextInt(256),rv.nextInt(256),rv.nextInt(256));
//		}
//		jTP.setEnabledAt(6, true);
//		imageDealer.left.left3.setVisible(true);
//		imageDealer.left.left4.setVisible(true);
		 
		return leftGroup;
	}

	public void tableValueSetting(float minArea, float maxArea, float minPvalue, float maxPvalue, float minDecayTau,
			float maxDecayTau, float minDuration, float maxDuration, float mindffMax, float maxdffMax) {
		table.setValueAt(new Float(minArea), 0, 3);
		table.setValueAt(new Float(maxArea), 0, 4);
		table.setValueAt(new Float(mindffMax), 1, 3);
		table.setValueAt(new Float(maxdffMax), 1, 4);
		table.setValueAt(new Float(minDuration), 2, 3);
		table.setValueAt(new Float(maxDuration), 2, 4);
		table.setValueAt(new Float(minPvalue), 3, 3);
		table.setValueAt(new Float(maxPvalue), 3, 4);
		table.setValueAt(new Float(minDecayTau), 4, 3);
		table.setValueAt(new Float(maxDecayTau), 4, 4);
		
	}
	
	public void dealBuilder() {
		dealBuilderRegion();
		dealBuilderLandMark();
		
	}

	private void dealBuilderRegion() {
		boolean[][] region = imageDealer.regionMark;
//		System.out.println(ConnectedComponents.twoPassConnect2D_ForBuilder(region,4).size());
		int width = region.length;
		int height = region[0].length;
		int status = combineRegionJCB.getSelectedIndex();
		for(int i=0;i<builderMap.size();i++) {
			BuilderTableItem item = builderMap.get(i);
			if(!item.type.equals("region")) 
				continue;	
			boolean[][] curRegion = item.region;
					//getRegion(item.image,minSize.get(i),maxSize.get(i),intensityThreshold.get(i));
			if(status==0) {
				for(int x=0;x<width;x++) {
					for(int y=0;y<height;y++) {
						region[x][y] |= curRegion[x][y];
					}
				}
			}
			if(status==1) {
				for(int x=0;x<width;x++) {
					for(int y=0;y<height;y++) {
						region[x][y] &= curRegion[x][y];
					}
				}
			}
		}
		
		int[][] regionLabel = imageDealer.regionMarkLabel;
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				regionLabel[x][y] = 0;
			}
		}
		HashMap<Integer, ArrayList<Integer>> regionCC = ConnectedComponents.twoPassConnect2D_ForBuilder(region,4);
		
		int changeParameter = Math.max(width, height);
		
		for(Entry<Integer, ArrayList<Integer>> entry:regionCC.entrySet()) {
			int label = entry.getKey();
			ArrayList<Integer> points = entry.getValue();
			for(int xy:points) {
				int x = xy/changeParameter;
				int y = xy%changeParameter;
				regionLabel[x][y] = label;
//				if(label!=0)
//					System.out.println(label);
			}
		}
		
		boolean[][] regionMark = getRegionMarker();
		
		if(regionMark!=null) {
			HashMap<Integer, ArrayList<Integer>> marker = ConnectedComponents.twoPassConnect2D_ForBuilder(regionMark);
			int opertaion = roleJCB.getSelectedIndex();
			
			int cnt = regionCC.size();
			for(int i=1;i<=regionCC.size();i++) {
				ArrayList<Integer> curRegion = regionCC.get(i);
				ArrayList<Integer> interLabel = new ArrayList<>();
				for(int j=1;j<=marker.size();j++) {
					ArrayList<Integer> interSection = new ArrayList<>(marker.get(j));
					interSection.retainAll(curRegion);
					if(interSection.size()>0)
						interLabel.add(j);
				}
				
				if(opertaion==1 && interLabel.size()>0) {
					for(int xy:curRegion) {
						int x = xy/changeParameter;
						int y = xy%changeParameter;
						regionLabel[x][y] = 0;
						region[x][y] = false;
					}
				}
				
				if(opertaion==0 && interLabel.size()>1) {
					for(int xy:curRegion) {
						int x = xy/changeParameter;
						int y = xy%changeParameter;
						float distance = Float.MAX_VALUE;
						int curLabel = 0;
						for(int j=0;j<interLabel.size();j++) {
							int label = interLabel.get(j);
							for(int xy2:marker.get(label)) {
								int x2 = xy2/changeParameter;
								int y2 = xy2%changeParameter;
								if((x-x2)*(x-x2) + (y-y2)*(y-y2)<distance) {
									distance = (x-x2)*(x-x2) + (y-y2)*(y-y2);
									curLabel = cnt + j;
								}
							}
						}
						
						if(curLabel!=cnt)
							regionLabel[x][y] = curLabel;
					}
					cnt += interLabel.size()-1;
				}
			}
		}
		
		
		HashMap<Integer,String> nameLst = new HashMap<>();
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				int label = regionLabel[x][y];
				if(label>0 && !nameLst.containsKey(label)) {
					nameLst.put(label, label+"");
				}
			}
		}
		imageDealer.nameLst = nameLst;
		
	}
	
	private boolean[][] getRegionMarker() {
		for(int i=0;i<builderMap.size();i++) {
			BuilderTableItem item = builderMap.get(i);
			if(!item.type.equals("region mark")) 
				continue;
			
			return getRegion(item.image,minSize.get(i),maxSize.get(i),intensityThreshold.get(i));
		}
//		System.out.println("No region marker");
		return null;
	}
	
	private void dealBuilderLandMark() {
		boolean[][] landMark = imageDealer.landMark;
		int width = landMark.length;
		int height = landMark[0].length;
		int status = combineLandmarkJCB.getSelectedIndex();
		for(int i=0;i<builderMap.size();i++) {
			BuilderTableItem item = builderMap.get(i);
			if(!item.type.equals("landmark")) 
				continue;
			
			boolean[][] curRegion = getRegion(item.image,minSize.get(i),maxSize.get(i),intensityThreshold.get(i));
			if(status==0) {
				for(int x=0;x<width;x++) {
					for(int y=0;y<height;y++) {
						landMark[x][y] |= curRegion[x][y];
					}
				}
			}
			if(status==1) {
				for(int x=0;x<width;x++) {
					for(int y=0;y<height;y++) {
						landMark[x][y] &= curRegion[x][y];
					}
				}
			}
		}
		
		int[][] landMarkLabel = imageDealer.landMarkLabel;
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				landMarkLabel[x][y] = 0;
			}
		}
		HashMap<Integer, ArrayList<Integer>> regionCC = ConnectedComponents.twoPassConnect2D_ForBuilder(landMark,4);
		HashMap<Integer,String> nameLst = new HashMap<>();
		int changeParameter = Math.max(width, height);
		for(Entry<Integer, ArrayList<Integer>> entry:regionCC.entrySet()) {
			int label = entry.getKey();
			ArrayList<Integer> points = entry.getValue();
			for(int xy:points) {
				int x = xy/changeParameter;
				int y = xy%changeParameter;
				landMarkLabel[x][y] = label;
			}
			nameLst.put(label, label+"");
		}
		imageDealer.nameLstLandMark = nameLst;
		
		
	}
	
	public boolean[][] getRegion(float[][] curImage, int minSize, int maxSize, float threshold) {
		int width = curImage.length;
		int height = curImage[0].length;
		boolean[][] thresholdMap = new boolean[width][height];
		threshold = (float) Math.sqrt(threshold/imageDealer.opts.maxValueDat);
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++)
				if(curImage[i][j]>=threshold)
					thresholdMap[i][j] = true;
		}
		
		HashMap<Integer, ArrayList<int[]>> connectedMap = ConnectedComponents.twoPassConnect2D(thresholdMap);
		
		boolean[][] result = new boolean[width][height];
		
		int max = imageDealer.right.sizeMaxSlider.getMaximum();
		int bit = (int) (Math.log10(max)/Math.log10(2))+1;
		
		minSize = (int) Math.pow(2,((double)minSize*bit)/max);
		maxSize = (int) Math.pow(2,((double)maxSize*bit)/max);
		
		for(Entry<Integer, ArrayList<int[]>> entry:connectedMap.entrySet()) {
			ArrayList<int[]> points = entry.getValue();
			
			if(points.size()<minSize || points.size()>maxSize)
				continue;
			
			for(int[] p:points) {
				result[p[0]][p[1]] = true;
			}
		}
		return result;
	}
}
