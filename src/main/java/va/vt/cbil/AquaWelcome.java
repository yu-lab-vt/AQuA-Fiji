/**
 * This class is used to open welcome window and let customer choose the parameters.
 * @author Xuelong Mi
 * @version 1.0
 */
package va.vt.cbil;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

//import net.imagej.ImageJ;

class AquaWelcome {
	
	String path = ""; 
	String proPath = "";
	int size = 0;
	boolean complete = false;
	boolean load = false;
	public int getPages() {
		return size;
	}
	
	
	public boolean getStatus(){
		return complete;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getProPath() {
		return proPath;
	}
	
	public boolean getOption() {
		return load;
	}
	
	public void run() throws Exception {
    	final JFrame welFrame = new JFrame("Welcome to AQUA"); 
    	welFrame.setUndecorated(false);
    	welFrame.setSize(500,500);

		JPanel welPan = new JPanel();

		JButton newPro = new JButton("New Project");
		JButton openExi = new JButton("Open Existing");
		JLabel blank = new JLabel(" ");
		newPro.setPreferredSize(new Dimension(200,80));
		openExi.setPreferredSize(new Dimension(200,80));
		blank.setPreferredSize(new Dimension(200,20));	//Separate the buttons
		
		GridBagPut p = new GridBagPut(welPan);
		p.putGridBag(newPro, welPan, 0, 0);
		p.putGridBag(blank, welPan, 0, 1);
		p.putGridBag(openExi, welPan, 0, 2);

		welFrame.add(welPan);
		welFrame.setLocationRelativeTo(null);
		welFrame.setVisible(true);
		
		newPro.addActionListener(new ActionListener() {	// The Listener of newPro
			public void actionPerformed(ActionEvent ev) {
		    	welFrame.setVisible(false);

		    	// addPath Button
		    	JLabel addPathLabel = new JLabel("Movie (Tiff Stack)");
		    	final JTextField addPathText = new JTextField("");
		    	addPathText.setPreferredSize(new Dimension(375,20));
		    	addPathText.setHorizontalAlignment(JTextField.LEFT);
		    	
		    	JButton addPathButton = new JButton("Choose Path");
		    	addPathButton.setPreferredSize(new Dimension(120,20));
		    	addPathButton.addActionListener(new ActionListener() {	// The Listener of newPro
					public void actionPerformed(ActionEvent ev) {
						JFileChooser chooser = new JFileChooser();
						chooser.setDialogTitle("Choose Image");
						chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
						chooser.setAcceptAllFileFilterUsed(false);
						if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
							path = chooser.getSelectedFile().getPath();
							path = path.replaceAll("\\\\", "\\\\\\\\");
							System.out.println(path);
							addPathText.setText(path);
						} else {
							JOptionPane.showMessageDialog(null, "No Selection ");
						}
					}
		    	});
		    	
		    	// addProFolderPath Button
		    	JLabel proFolder = new JLabel("Set Project Folder");
		    	final JTextField proFolderText = new JTextField("");
		    	proFolderText.setPreferredSize(new Dimension(375,20));
		    	proFolderText.setHorizontalAlignment(JTextField.LEFT);
		    	
		    	JButton addProPathButton = new JButton("Choose Path");
		    	addProPathButton.setPreferredSize(new Dimension(120,20));
		    	addProPathButton.addActionListener(new ActionListener() {	// The Listener of newPro
					public void actionPerformed(ActionEvent ev) {
						JFileChooser chooser = new JFileChooser();
						chooser.setDialogTitle("Choose Project folder");
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						chooser.setAcceptAllFileFilterUsed(false);
						if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
							proPath = chooser.getSelectedFile().getPath();
							proPath = proPath.replaceAll("\\\\", "\\\\\\\\");
							proPath = proPath + "\\\\";
							System.out.println(proPath);
							proFolderText.setText(proPath);
						} else {
							JOptionPane.showMessageDialog(null, "No Selection ");
						}
					}
		    	});
		    	
		    	// addPath Panel
		    	JPanel optionPath = new JPanel();
		    	GridBagPut setting1 = new GridBagPut(optionPath); 
		    	setting1.putGridBag(addPathLabel, optionPath, 0, 0, 1, 1);
		    	setting1.putGridBag(addPathText, optionPath, 0, 1, 1, 1);
		    	setting1.putGridBag(addPathButton, optionPath, 4, 1, 1, 1);	
		    	setting1.putGridBag(proFolder, optionPath, 0, 2, 1, 1);	
		    	setting1.putGridBag(proFolderText, optionPath, 0, 3, 1, 1);
		    	setting1.putGridBag(addProPathButton, optionPath, 4, 3, 1, 1);	
		    	
		    	JTextField jTF1 = new JTextField("0.5");
		    	jTF1.setPreferredSize(new Dimension(80,20));
		    	jTF1.setHorizontalAlignment(JTextField.CENTER);
		    	JTextField jTF2 = new JTextField("0.5");
		    	jTF2.setPreferredSize(new Dimension(80,20));
		    	jTF2.setHorizontalAlignment(JTextField.CENTER);
		    	JTextField jTF3 = new JTextField("5");
		    	jTF3.setPreferredSize(new Dimension(80,20));
		    	jTF3.setHorizontalAlignment(JTextField.CENTER);
		    	
		    	JLabel jL1 = new JLabel("Temporal resolutionl: Frames Per Second");
		    	JLabel jL2 = new JLabel("Spatial Resolution: um per pixel");
		    	JLabel jL3 = new JLabel("Exclude pixels shorter than this distance to border");
		    	
		    	String[] types = {"GCaMP in Vivo (cyto & lck)","GCaMP ex Vivo lck","GCaMP ex Vivo cyto","GluSnFR (astrocytic & neuronal)"};
		    	JComboBox<String> typeJCB = new JComboBox<String>(types);
		    	JLabel jL = new JLabel("Data type (presets)");
		    	typeJCB.setBackground(Color.white);
		    	
		    	typeJCB.addItemListener(new ItemListener() {

					@Override
					public void itemStateChanged(ItemEvent arg0) {
						// TODO Auto-generated method stub
						if(typeJCB.getSelectedIndex()!=0) {
							jTF1.setText("1");
							jTF2.setText("1");
						}else {
							jTF1.setText("0.5");
							jTF2.setText("0.5");
						}
					}
		    		
		    	});
		    	
		    	
		    	
		    	
		    	
		    	
		    	// options Panel
		    	JPanel options = new JPanel();
		    	GridBagPut setting2 = new GridBagPut(options); 
		    	setting2.fillBoth();
		    	setting2.putGridBag(typeJCB, options, 0, 0, 1, 1);
		    	setting2.putGridBag(jL, options, 1, 0, 5, 1);
		     	setting2.putGridBag(jTF1, options, 0, 1, 1, 1);
		    	setting2.putGridBag(jL1, options, 1, 1, 5, 1);
		    	setting2.putGridBag(jTF2, options, 0, 2, 1, 1);
		    	setting2.putGridBag(jL2, options, 1, 2, 5, 1);
		    	setting2.putGridBag(jTF3, options, 0, 3, 1, 1);
		    	setting2.putGridBag(jL3, options, 1, 3, 5, 1);		
		    	
		    	JPanel enterValue = new JPanel();
		    	GridBagPut setting3 = new GridBagPut(enterValue); 
		    	setting3.putGridBag(optionPath, enterValue, 0, 0, 5, 3);
		    	setting3.putGridBag(options, enterValue, 0, 3, 5, 3);
		    	
		    	
		    	int result = JOptionPane.showConfirmDialog(null, enterValue, 
		                "Please Enter Values", JOptionPane.OK_CANCEL_OPTION);
		    	
		    	// When click "OK"
		    	if (result == JOptionPane.OK_OPTION) {
		    		if(path == ""||proPath=="") {
		    			JOptionPane.showMessageDialog(null, "You should select the Image and Project Folder","Warning",JOptionPane.WARNING_MESSAGE); 
		    			welFrame.setVisible(true);
		    		}else {
		    
						complete = true;
						
						AquaRunning aqua = new AquaRunning();
						float ts = Float.parseFloat(jTF1.getText());
						float ss = Float.parseFloat(jTF2.getText());
						int border = (int)Float.parseFloat(jTF3.getText());
						int index = typeJCB.getSelectedIndex()+1;
	//					System.out.println(index);
						aqua.start(path, proPath, load,ts,ss,border,index);
		    		}
//					System.out.println(ts);
		    	}else if (result == JOptionPane.CANCEL_OPTION){	// When click "Cancel"
		        	try {
		        		welFrame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	}else {
		    		welFrame.setVisible(false);
		    	}
		    	
		    	
			}
			
		
		});
        // ask the user for a file to open	
		//dialog.setMultipleMode(true);
		openExi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				welFrame.setVisible(false);
				load = true;
				
		    	// addProFolderPath Button
		    	JLabel proFolder = new JLabel("Choose Project Folder");
		    	final JTextField proFolderText = new JTextField("");
		    	proFolderText.setPreferredSize(new Dimension(247,20));
		    	proFolderText.setHorizontalAlignment(JTextField.LEFT);
		    	
		    	JButton addProPathButton = new JButton("Choose Path");
		    	addProPathButton.setPreferredSize(new Dimension(120,20));
		    	addProPathButton.addActionListener(new ActionListener() {	// The Listener of newPro
					public void actionPerformed(ActionEvent ev) {
						JFileChooser chooser = new JFileChooser();
						chooser.setDialogTitle("Choose Project folder");
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						chooser.setAcceptAllFileFilterUsed(false);
						if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
							proPath = chooser.getSelectedFile().getPath();
							proPath = proPath.replaceAll("\\\\", "\\\\\\\\");
							proPath = proPath + "\\\\";
							System.out.println(proPath);
							proFolderText.setText(proPath);
						} else {
							JOptionPane.showMessageDialog(null, "No Selection ");
						}
					}
		    	});
		    	
		    	// addPath Panel
		    	JPanel optionPath = new JPanel();
		    	GridBagPut setting1 = new GridBagPut(optionPath); 
		    	setting1.putGridBag(proFolder, optionPath, 0, 1, 1, 1);	
		    	setting1.putGridBag(proFolderText, optionPath, 0, 2, 1, 1);
		    	setting1.putGridBag(addProPathButton, optionPath, 4, 2, 1, 1);	
		    
		    	
		    	
		    	int result = JOptionPane.showConfirmDialog(null, optionPath, 
		                "Please Enter Values", JOptionPane.OK_CANCEL_OPTION);
		    	
		    	// When click "OK"
		    	if (result == JOptionPane.OK_OPTION) {
					complete = true;
					try {
		    			FileInputStream fi = null;
		    			ObjectInputStream oi = null;
		    			
		    			fi = new FileInputStream(new File(proPath + "OrgPath.ser"));	
		    			oi = new ObjectInputStream(fi);
		    			path = (String)oi.readObject();
		    			oi.close();
		    			fi.close();
		    		} catch (FileNotFoundException e) {
		    			e.printStackTrace();
		    		} catch (IOException e) {
		    			e.printStackTrace();
		    		} catch (ClassNotFoundException e) {
		    			e.printStackTrace();
		    		}
					AquaRunning aqua = new AquaRunning();
					aqua.start(path, proPath, load,Float.NaN,Float.NaN,0, 0);
		    	}else if (result == JOptionPane.CANCEL_OPTION){	// When click "Cancel"
		        	try {
		        		welFrame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	}else {
		    		welFrame.setVisible(false);
		    	}
		    	
		    	
			}
			
		});
		
	}	
	
}
