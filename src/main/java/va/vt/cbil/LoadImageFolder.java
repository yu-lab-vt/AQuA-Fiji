package va.vt.cbil;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

class LoadImageFolder {
	String path = null;
	
	public String getPath() {
		FileDialog dialog = new FileDialog((Frame)null, "Select Image to Analyze");
		dialog.setMode(FileDialog.LOAD);	
		BufferedImage im = null;		
		
		while (im == null) {	
			dialog.setVisible(true);
	    	path = new File(dialog.getFile()).getAbsolutePath();	
			try {
				im = ImageIO.read(new File(path));
				if(im == null) {
					JOptionPane.showMessageDialog(null, "Image file format not supported by ImageIO: " + path +" \n Try it again","Error",JOptionPane.ERROR_MESSAGE);
					throw new IOException("Image file format not supported by ImageIO: " + path);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
		return path;
	}
	
}
