package va.vt.cbil;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class CenterGroupPanel {
	
//	ImageShow image = null;
	MyImageLabel imageLabel = null;
	ImageDealer imageDealer = null;
	float ts = 1;
	JPanel centerGroup = new JPanel();
	JPanel centerGroup1 = new JPanel();
	JPanel centerGroup2 = new JPanel();
	// Group1
	JPanel center1 = new JPanel();
	JPanel center2 = new JPanel();
	// Group2
	JPanel center3 = new JPanel();
	JPanel center4 = new JPanel();
	// Group1
		// center1
	JToggleButton panButton = new JToggleButton("PanAndZoom");
	JButton resetButton = new JButton("Reset");
	JToggleButton sideButton = new JToggleButton("Side by side");
	JToggleButton gaussfilter = new JToggleButton("Gauss");
	JLabel cL11 = new JLabel(" Jump to");
	JLabel cL12 = new JLabel(" Playback frame rate");
	JTextField jumpJTF = new JTextField("1");
	JTextField fpsJTF = new JTextField("5");
	int pages = 0;
	JLabel status = new JLabel();
		// center2
	JPanel imagePanel = new JPanel();
	// Group2
		// center3
	JButton playButton = new JButton("Play");
	JButton pauseButton = new JButton("Pause");
	JButton prev = new JButton("<<");
	JButton next = new JButton(">>");
	JSlider imageSlider = null;
	JLabel nEvt = new JLabel("nEvt",JLabel.CENTER);
	JLabel EvtNumber = new JLabel("0",JLabel.CENTER);
		// center4
	
	ColorLabel bar = new ColorLabel();
	JPanel axis = new JPanel();
	JLabel minLabel = new JLabel();
	JLabel maxLabel = new JLabel();
	JLabel midLabel = new JLabel();
	JPanel colorbarPanel = new JPanel();
	DrawCurveLabel resultsLabel = null; 
	
	// Side by side
	int length = 400;
	String[] leftJCBString = {"Raw", "Raw + overlay", "Rising map", "Maximum projection","dF"};
	JComboBox<String> leftJCB = new JComboBox<String>(leftJCBString);
	String[] rightJCBString = {"Raw", "Raw + overlay", "Rising map", "Maximum projection","dF"};
	JComboBox<String> rightJCB = new JComboBox<String>(rightJCBString);
	JPanel leftPanel = new JPanel();
	JPanel leftImagePanel = new JPanel();
	MyImageLabel leftImageLabel = new MyImageLabel();
	JPanel rightPanel = new JPanel();
	JPanel rightImagePanel = new JPanel();
	MyImageLabel rightImageLabel = new MyImageLabel();
	JLabel blankLabel1 = new JLabel();
	JLabel blankLabel2 = new JLabel();
	JPanel sidebysidePanel = new JPanel();
	ColorLabel2 colorbarleft = new ColorLabel2();
	ColorLabel2 colorbarright = new ColorLabel2();
	// Builder
	JPanel builderPane = new JPanel();
	MakeBuilderLabel builderImageLabel = null;
	
	
	public CenterGroupPanel(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
		imageLabel = imageDealer.getImageLabel();
		builderImageLabel = imageDealer.builderImageLabel; 
//		image = imageDealer.getImageShow();
		pages = imageDealer.getPages();
		status.setText("1/" + pages + " Frame " + ts + "/" + pages*ts + " Second");
		imageSlider = new JSlider(JSlider.HORIZONTAL, 0, pages-1, 0);
		resultsLabel = new DrawCurveLabel(imageDealer);
		imageDealer.setCurveLabel(resultsLabel);
	}
	
	public void setting() {
		// center1
		jumpJTF.setPreferredSize(new Dimension(30,20));
		fpsJTF.setPreferredSize(new Dimension(25,20));
		jumpJTF.setHorizontalAlignment(JTextField.CENTER);
		fpsJTF.setHorizontalAlignment(JTextField.CENTER);
		status.setPreferredSize(new Dimension(175,20));
		status.setHorizontalAlignment(JLabel.RIGHT);
		
		
		int fontsize = 11;
		status.setFont(new Font("Dialog", Font.BOLD, fontsize));
		panButton.setFont(new Font("Dialog", Font.BOLD, fontsize));
		resetButton.setFont(new Font("Dialog", Font.BOLD, fontsize));
		sideButton.setFont(new Font("Dialog", Font.BOLD, fontsize));
		gaussfilter.setFont(new Font("Dialog", Font.BOLD, fontsize));
		cL11.setFont(new Font("Dialog", Font.BOLD, fontsize));
		cL12.setFont(new Font("Dialog", Font.BOLD, fontsize));
		panButton.setPreferredSize(new Dimension(110,20));
		resetButton.setPreferredSize(new Dimension(70,20));
		sideButton.setPreferredSize(new Dimension(105,20));
		gaussfilter.setPreferredSize(new Dimension(70,20));
		cL11.setPreferredSize(new Dimension(50,20));
		cL12.setPreferredSize(new Dimension(125,20));
		
		gaussfilter.setEnabled(false);
		
		// center2
		center2.setPreferredSize(new Dimension(818,520));
		
			// imageLabel
		float scal = (float)imageDealer.width/imageDealer.height;
		
		int width = 0;
		int height = 0;
		if(500*scal >800) {
			width = 800;
			height = (int) (800/scal);
		}else {
			width = (int) (500*scal);
			height = 500;
		}
//		System.out.println(width + " " + height);
		imageLabel.setPreferredSize(new Dimension(width,height));
		imageDealer.maxImageWidth = width;
		imageDealer.maxImageHeight = height;
		imageLabel.setMaxSize(width, height);
		
		// center3
		pauseButton.setEnabled(false);
		prev.setPreferredSize(new Dimension(48,25));
		next.setPreferredSize(new Dimension(48,25));
		imageSlider.setMinorTickSpacing(1); 		
		imageSlider.setPreferredSize(new Dimension(452,20));
		nEvt.setPreferredSize(new Dimension(50,20));
//		nEvt.setAlignmentX(JLabel.CENTER);
		EvtNumber.setPreferredSize(new Dimension(50,20));
		EvtNumber.setOpaque(true);
		EvtNumber.setBackground(Color.white);
//		EvtNumber.setAlignmentX(JLabel.CENTER);
		// center4
		bar.setPreferredSize(new Dimension(808,12));
		bar.setOpaque(true);
//		bar.setBackground(Color.red);
		axis.setPreferredSize(new Dimension(808,25));
		minLabel.setPreferredSize(new Dimension(100,25));
		midLabel.setPreferredSize(new Dimension(808 - 3*100,25));
		maxLabel.setPreferredSize(new Dimension(100,25));
		minLabel.setHorizontalAlignment(JLabel.LEFT);
		midLabel.setHorizontalAlignment(JLabel.CENTER);
		maxLabel.setHorizontalAlignment(JLabel.RIGHT);
		
		
		resultsLabel.setPreferredSize(new Dimension(808,200));
		resultsLabel.setOpaque(true);
		resultsLabel.setBackground(Color.WHITE);
		// Side by side
		leftJCB.setPreferredSize(new Dimension(400,30));
		rightJCB.setPreferredSize(new Dimension(400,30));
		leftJCB.setBackground(Color.white);
		rightJCB.setBackground(Color.white);
		
		ArrayList<ArrayList<Point>> list1 = imageLabel.getPointList1();
		ArrayList<ArrayList<Point>> list2 = imageLabel.getPointList2();
		leftImageLabel.setImageDealer(imageDealer);
		rightImageLabel.setImageDealer(imageDealer);
		leftImageLabel.setPointList(list1, list2);
		rightImageLabel.setPointList(list1, list2);
		imageDealer.setTwoLabels(leftImageLabel, rightImageLabel);
		
		int sw = 0;
		int sh = 0;
		if(400*scal >400) {
			sw = 400;
			sh = (int) (400/scal);
		}else {
			sw = (int) (400*scal);
			sh = 400;
		}
		
		leftImageLabel.setPreferredSize(new Dimension(sw,sh));
		rightImageLabel.setPreferredSize(new Dimension(sw,sh));
		leftImageLabel.setMaxSize(sw,sh);
		rightImageLabel.setMaxSize(sw,sh);
		imageDealer.setLength(sw,sh);
		blankLabel1.setPreferredSize(new Dimension(404,30));
		blankLabel2.setPreferredSize(new Dimension(404,30));
		colorbarleft.setPreferredSize(new Dimension(404,25));
		colorbarright.setPreferredSize(new Dimension(404,25));
		sidebysidePanel.setPreferredSize(new Dimension(818,495));
//		colorbarleft.setOpaque(true);
//		colorbarright.setOpaque(true);
		
		
		sw = 0;
		sh = 0;
		if(700*scal >700) {
			sw = 700;
			sh = (int) (700/scal);
		}else {
			sw = (int) (700*scal);
			sh = 700;
		}
		
//		System.out.println(sw + " " + sh);
		builderImageLabel.setPreferredSize(new Dimension(sw,sh));
		imageDealer.maxBuilderWidth = sw;
		imageDealer.maxBuilderHeight = sh;
		builderPane.setPreferredSize(new Dimension(800,800));
		imageDealer.dealBuilderImageLabel();
	}
	
	public void layout() {
		// center1
		center1.add(panButton);
		center1.add(resetButton);
		center1.add(cL11);
		center1.add(jumpJTF);
		center1.add(cL12);
		center1.add(fpsJTF);
		center1.add(sideButton);
		center1.add(gaussfilter);
		center1.add(status);
		// center2
		
		GridBagPut settingImagePanel = new GridBagPut(imagePanel);
//		settingImagePanel.putGridBag(imageDealer.left.canvas, imagePanel, 0, 0);
		settingImagePanel.putGridBag(imageLabel, imagePanel, 0, 0);
		
		
		GridBagPut settingCenter2 = new GridBagPut(center2);
		settingCenter2.fillBoth();
		settingCenter2.putGridBag(imagePanel, center2, 0, 0);
		// centerGroup1
		GridBagPut settingCenterGroup1 = new GridBagPut(centerGroup1);
		settingCenterGroup1.putGridBag(center1, centerGroup1, 0, 0);
		settingCenterGroup1.putGridBag(center2, centerGroup1, 0, 1);
		centerGroup1.setBorder(BorderFactory.createEtchedBorder());
		
		// center3
		center3.add(playButton);
		center3.add(pauseButton);
		center3.add(prev);
		center3.add(imageSlider);
		center3.add(next);
		center3.add(nEvt);
		center3.add(EvtNumber);
		// center4
		center4.add(resultsLabel);
		// centerGroup2
		GridBagPut settingCenterGroup2 = new GridBagPut(centerGroup2);
		settingCenterGroup2.putGridBag(center3, centerGroup2, 0, 0);
		settingCenterGroup2.putGridBag(center4, centerGroup2, 0, 1);
		centerGroup2.setBorder(BorderFactory.createEtchedBorder());
		
		axis.add(minLabel);
		axis.add(midLabel);
		axis.add(maxLabel);
		bar.setVisible(false);
		axis.setVisible(false);
		
		GridBagPut settingImagePanel2 = new GridBagPut(builderPane);
		settingImagePanel2.fillBoth();
		settingImagePanel2.putGridBag(builderImageLabel, builderPane, 0, 0);
		
		// Side by side
		leftImagePanel.add(leftImageLabel);
		rightImagePanel.add(rightImageLabel);
		
		GridBagPut leftPanelSetting = new GridBagPut(leftPanel);
		leftPanelSetting.fillBoth();
		leftPanelSetting.putGridBag(leftJCB, leftPanel, 0, 0);
		leftPanelSetting.putGridBag(blankLabel1, leftPanel, 0, 1);
		leftPanelSetting.putGridBag(leftImagePanel, leftPanel, 0, 2);
//		leftPanelSetting.putGridBag(colorbarleft, leftPanel, 0, 3);
		
		GridBagPut rightPanelSetting = new GridBagPut(rightPanel);
		rightPanelSetting.fillBoth();
		rightPanelSetting.putGridBag(rightJCB, rightPanel, 0, 0);
		rightPanelSetting.putGridBag(blankLabel2, rightPanel, 0, 1);
		rightPanelSetting.putGridBag(rightImagePanel, rightPanel, 0, 2);
//		rightPanelSetting.putGridBag(colorbarright, rightPanel, 0, 3);
		
		GridBagPut settingSide = new GridBagPut(sidebysidePanel);
		settingSide.fillBoth();					
		settingSide.putGridBag(leftPanel, sidebysidePanel, 0, 0);
//		settingSide.putGridBag(colorbarleft, sidebysidePanel, 0, 1);
		settingSide.putGridBag(rightPanel, sidebysidePanel, 1, 0);
//		settingSide.putGridBag(colorbarright, sidebysidePanel, 1, 1);
	}
	
	
	public void layout1() {
		centerGroup.removeAll();

		
		// centerGroup
		GridBagPut settingCenterGroup = new GridBagPut(centerGroup);
		settingCenterGroup.putGridBag(centerGroup1, centerGroup, 0, 0);
		settingCenterGroup.putGridBag(bar, centerGroup, 0, 1);
		settingCenterGroup.putGridBag(axis, centerGroup, 0, 2);
		settingCenterGroup.putGridBag(centerGroup2, centerGroup, 0, 3);
	}
	
	public void layout2() {
		centerGroup.removeAll();
		
		GridBagPut settingCenterGroup = new GridBagPut(centerGroup);
		settingCenterGroup.putGridBag(builderPane, centerGroup, 0, 0);
		builderPane.setBorder(BorderFactory.createEtchedBorder());
	}
	
	public void addButtonListeners() {
		prev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int now = imageSlider.getValue();
				if(now==0)
					imageSlider.setValue(pages-1);
				else if (now > 0)
					imageSlider.setValue(now - 1);
			}
		});
		
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int now = imageSlider.getValue();
				if(now==pages-1)
					imageSlider.setValue(0);
				else if (now < pages - 1)
					imageSlider.setValue(now + 1);
			}
		});
		
		imageSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt){
				if(!imageSlider.getValueIsAdjusting()){  // when adjust finish, change
					int value = imageSlider.getValue();
					if(value == pages) {
						value = 0;
					}
					imageDealer.setPage(value);
					imageDealer.dealImage();		
					resultsLabel.repaint();
					int page = value + 1;
					status.setText(page + "/" + pages + " Frame " + page*ts +" /" + pages*ts + " Second");					
				}
			}
		});
		
		ActionListener tasker = new ActionListener() {	// A task
			public void actionPerformed(ActionEvent evt) {
				int value = imageSlider.getValue();
				if(value == pages - 1 ) {
					imageSlider.setValue(0);
				}
				else
					imageSlider.setValue(value + 1);
			}
		};
		
		ActionListener tasker2 = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int value = imageSlider.getValue();
				if(value == 0 ) {
					imageSlider.setValue(pages - 1);
				}
				else
					imageSlider.setValue(value - 1);
			}
		};
		int fps = Integer.parseInt(fpsJTF.getText());
		Timer timer = new Timer((int)(1000/(fps)), tasker);
		Timer timer2 = new Timer((int)(1000/fps),tasker2);
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int fpsData = Integer.parseInt(fpsJTF.getText());
				if(fpsData == 0) {
					timer.stop();
					timer2.stop();
					playButton.setEnabled(true);
					pauseButton.setEnabled(false);
				}else {
					if(fpsData>0) {
						timer.setDelay((int)(1000/fpsData));
						timer.setRepeats(true);
						timer.start();	
					}else {
						timer2.setDelay((int)(-1000/fpsData));
						timer2.setRepeats(true);
						timer2.start();	
					}
					playButton.setEnabled(false);
					pauseButton.setEnabled(true);
				}
			}
		});
		
		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				timer.stop();
				timer2.stop();
				playButton.setEnabled(true);
				pauseButton.setEnabled(false);
			}
		});
		
		leftJCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						imageDealer.dealImage();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						leftImageLabel.repaint();
//						leftImageLabel.validate();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						rightImageLabel.repaint();
//						leftImageLabel.validate();
						
					}
					
				}).start();
			}
		});
		
		rightJCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						imageDealer.dealImage();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						leftImageLabel.repaint();
//						leftImageLabel.validate();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						rightImageLabel.repaint();
//						leftImageLabel.validate();
						
					}
					
				}).start();
			}
		});
		
		jumpJTF.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyChar()==KeyEvent.VK_ENTER) {
					if(jumpJTF.getText() == "")
						JOptionPane.showMessageDialog(null, "You should fill the suitable number","Warning",JOptionPane.WARNING_MESSAGE);
					int page = Integer.parseInt(jumpJTF.getText())-1;
					if(page >= 0 && page <= pages-1)
						imageSlider.setValue(page);
					else
						JOptionPane.showMessageDialog(null, "You should fill the suitable number","Warning",JOptionPane.WARNING_MESSAGE);
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub			
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub		
			}
		});
			
		fpsJTF.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyChar()==KeyEvent.VK_ENTER) {
					if(fpsJTF.getText() == "")
						JOptionPane.showMessageDialog(null, "You should fill the suitable number","Warning",JOptionPane.WARNING_MESSAGE);
					int fps = Integer.parseInt(fpsJTF.getText());
					if(fps>0) {
						timer.setDelay((int)(1000/(fps)));
						if(timer2.isRunning()) {
							timer2.stop();
							timer.start();
							timer.setRepeats(true);
						}
						
					}else if(fps<0) {
						timer2.setDelay((int)(-1000/(fps)));
						if(timer.isRunning()) {
							timer.stop();
							timer2.start();
							timer2.setRepeats(true);
						}
					}else {
						timer.stop();
						timer2.stop();
						playButton.setEnabled(true);
						pauseButton.setEnabled(false);
					}
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub			
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub		
			}
		});
		
		sideButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if(sideButton.isSelected()) {
					imageDealer.changeStatus();
					
					center2.removeAll();
//					center2.setPreferredSize(new Dimension(818,520));
					colorbarPanel.add(colorbarleft);	
					colorbarPanel.add(colorbarright);	
					colorbarleft.setText(" ");
					colorbarright.setText(" ");
//					colorbarleft.setVisible(false);
//					colorbarright.setVisible(false);
					GridBagPut settingCenter2 = new GridBagPut(center2);
					settingCenter2.fillBoth();					
					settingCenter2.putGridBag(sidebysidePanel, center2, 0, 0);
					settingCenter2.putGridBag(colorbarPanel, center2, 0, 1);
					center2.repaint();
					
					imageDealer.right.brightPanel.removeAll();
					imageDealer.right.brightPanel.add(imageDealer.right.contrastSliderl);
					imageDealer.right.brightPanel.add(imageDealer.right.contrastSliderr);
					
					
//					center2.validate();
					new Thread(new Runnable() {

						@Override
						public void run() {
							imageDealer.dealImage();
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							leftImageLabel.repaint();
//							leftImageLabel.validate();
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							rightImageLabel.repaint();
//							leftImageLabel.validate();
							
						}
						
					}).start();
					
				}else {
					imageDealer.changeStatus();
					center2.removeAll();
					center2.setPreferredSize(new Dimension(818,520));
					
					imageDealer.right.brightPanel.removeAll();
					imageDealer.right.brightPanel.add(imageDealer.right.contrastSlider);
					
					GridBagPut settingCenter2 = new GridBagPut(center2);					
					settingCenter2.putGridBag(imagePanel, center2, 0, 0);
					settingCenter2.fillBoth();
					
					center2.repaint();
					center2.validate();
					
					new Thread(new Runnable() {

						@Override
						public void run() {
							imageDealer.dealImage();
							imageLabel.repaint();
						}
						
					}).start();
					

				}
			}			
		});
		
		gaussfilter.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if(gaussfilter.isSelected()) {
					imageDealer.gaussStatus = true;
				}else
					imageDealer.gaussStatus = false;
				
				imageDealer.dealImage();

			}			
		});
		
		PanAndZoomListener panAndZoomListener = new PanAndZoomListener(imageDealer, imageLabel.getLocation());
		imageLabel.addMouseListener(panAndZoomListener);
		imageLabel.addMouseMotionListener(panAndZoomListener);
		imageLabel.addMouseWheelListener(panAndZoomListener);

		leftImageLabel.addMouseListener(panAndZoomListener);
		leftImageLabel.addMouseMotionListener(panAndZoomListener);
		leftImageLabel.addMouseWheelListener(panAndZoomListener);
		
		rightImageLabel.addMouseListener(panAndZoomListener);
		rightImageLabel.addMouseMotionListener(panAndZoomListener);
		rightImageLabel.addMouseWheelListener(panAndZoomListener);
		panButton.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				if(panButton.isSelected()) {
					panAndZoomListener.setValid(true);
					imageDealer.left.addLeft11.setSelected(false);
					imageDealer.left.addLeft12.setSelected(false);
					imageDealer.left.removeLeft11.setSelected(false);
					imageDealer.left.removeLeft12.setSelected(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}else {
					panAndZoomListener.setValid(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
				}
			}
			
		});
		
		resetButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				panAndZoomListener.reset();
				imageDealer.reset();
			}
			
		});
	}
	
	public JPanel createPanel() {		
		setting();
		layout();
		layout1();	
		
		addButtonListeners();		
		
		return centerGroup;
	}

	public void settingChange(int width, int height) {
		center2.setPreferredSize(new Dimension(width-32,height-330));
		imageSlider.setPreferredSize(new Dimension(width-298,20));
		resultsLabel.setPreferredSize(new Dimension(width-42,200));
		bar.setPreferredSize(new Dimension(width-42,12));
		center3.setPreferredSize(new Dimension(width-32,25));
		center4.setPreferredSize(new Dimension(width-32,200));
		
		int curW = width - 20;
		int curH = height - 350;
		int w = 0;
		int h = 0;
		float scal = (float)imageDealer.width/imageDealer.height;
		if(curH*scal >curW) {
			w = curW;
			h = (int) (curW/scal);
		}else {
			w = (int) (curH*scal);
			h = curH;
		}
		imageLabel.setPreferredSize(new Dimension(w,h));
		imageDealer.maxImageWidth = w;
		imageDealer.maxImageHeight = h;
		imageLabel.setMaxSize(w, h);
		
		int sw = 0;
		int sh = 0;
		curW /= 2;
		curW -= 10;
		if(curH*scal >curW) {
			sw = curW;
			sh = (int) (curW/scal);
		}else {
			sw = (int) (curH*scal);
			sh = curH;
		}
		
		leftImageLabel.setPreferredSize(new Dimension(sw,sh));
		rightImageLabel.setPreferredSize(new Dimension(sw,sh));
		leftImageLabel.setMaxSize(sw,sh);
		rightImageLabel.setMaxSize(sw,sh);
		imageDealer.setLength(sw,sh);
		imageDealer.dealImage();
		
	}
}
