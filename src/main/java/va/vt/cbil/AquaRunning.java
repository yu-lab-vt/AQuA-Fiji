package va.vt.cbil;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class AquaRunning{
	public void start(String path, String proPath, boolean load, float ts, float ss, int border, int index) {
		// Show the window
		JFrame aquaWindow = new JFrame("Aqua");
		aquaWindow.setSize(1650,850);
		aquaWindow.setUndecorated(false);
		aquaWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		aquaWindow.setLocationRelativeTo(null);
		aquaWindow.setMinimumSize(new Dimension(1660,900));
		aquaWindow.setResizable(false);
		
		/*  ----------------------------- ImagePlus ----------------------------------- */
		MyImageLabel imageLabel = new MyImageLabel();
		ImageDealer imageDealer = new ImageDealer(path,proPath,border,index);
		MakeBuilderLabel builderImageLabel = new MakeBuilderLabel(imageDealer);
		
		imageDealer.setImageLabel(imageLabel);
		imageLabel.setImageDealer(imageDealer);
		imageDealer.setBuilderImageLabel(builderImageLabel);
		imageDealer.setWindow(aquaWindow);
		
		/*  ------------------------------ For Test ----------------------------------- */
		LeftGroupPanel left = new LeftGroupPanel(imageDealer);
		CenterGroupPanel center = new CenterGroupPanel(imageDealer);
		RightGroupPanel right = new RightGroupPanel(imageDealer);
		
		imageDealer.setPanelGroup(left, center, right,aquaWindow);
		if(!Float.isNaN(ts))
			imageDealer.setImageConfig(ts,ss);
		
		JPanel centerGroup = center.createPanel();
		JPanel leftGroup = left.createPanel();
		JPanel rightGroup = right.createPanel();		

		// Window set
		GridBagPut settingWindow = new GridBagPut(aquaWindow);
		settingWindow.setAnchorNorthWest();
		settingWindow.putGridBag(leftGroup, aquaWindow, 0, 0);
		settingWindow.putGridBag(centerGroup, aquaWindow, 1, 0);
		settingWindow.putGridBag(rightGroup, aquaWindow, 2, 0);
		
		
		if(load)
			imageDealer.load(proPath);
		
		aquaWindow.setTitle("AQuA: " + imageDealer.opts.filename);

		aquaWindow.setVisible(true);
		try {
			imageDealer.dealImage();
			Thread.sleep(200);
			imageLabel.repaint();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		centerGroup.repaint();
//		aquaWindow.repaint();

		
//		aquaWindow.addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//            	int width = aquaWindow.getWidth() - 400*2;
//				int height = aquaWindow.getHeight();
//				center.settingChange(width,height);
//            }
//        });
		
		
		
		
	}
	
	public static void  main(String[] args) {
		String path = "D:\\Test.tif";
		String propath = "D:\\Tfolder2\\";
		AquaRunning aq = new AquaRunning();
		aq.start(path,propath,false,1,1,10,1);
		
		
	}
}
