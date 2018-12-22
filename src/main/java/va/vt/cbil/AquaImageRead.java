package va.vt.cbil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

class AquaImageRead {
	BufferedImage imarray[] = null;
	BufferedImage orig[] = null;
	BufferedImage meanOrig = null;
	String path = null;
	
	public AquaImageRead(String path) {
		this.path = path;
	}
	
	public BufferedImage[] getImageArray() {
		return imarray;
	}
	
	public BufferedImage[] getOriginalImageArray() {
		return orig;
	}
	
	
	public int run() {
		ImageInputStream is = null;
		try {
			is = ImageIO.createImageInputStream(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("TIFF");
    	if (iterator == null || !iterator.hasNext()) {
    		  try {
				throw new IOException("Image file format not supported by ImageIO: " + path);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	//
    	ImageReader reader = (ImageReader) iterator.next();
    	reader.setInput(is);
    	int pages = 0;
		try {
			pages = reader.getNumImages(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	imarray = new BufferedImage[pages];
    	orig = new BufferedImage[pages];
    	for (int imageIndex = 0; imageIndex < pages; imageIndex++) {
            BufferedImage bufferedImage = null;
			try {
				bufferedImage = reader.read(imageIndex);
			} catch (IOException e) {
				e.printStackTrace();
			}
            imarray[imageIndex] = bufferedImage;
            orig[imageIndex] = bufferedImage;
        }
    	return pages;
	}
	
}
