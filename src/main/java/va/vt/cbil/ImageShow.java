package va.vt.cbil;

import java.awt.image.BufferedImage;

class ImageShow{
	
	BufferedImage cur = null;
	BufferedImage[] imarray = null;
	BufferedImage[] orig = null;
	int curPage = 0;
	String path = null;
	
	public ImageShow(String path){
		this.path = path;
	}
	
	public void run() {
		AquaImageRead aIR = new AquaImageRead(path);
		aIR.run();
		imarray = aIR.getImageArray();
		orig = aIR.getOriginalImageArray();
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setPage(int page) {
		curPage = page;
	}
	
	public BufferedImage getImage() {
		cur = imarray[curPage];
		return cur;
	}
	
	public BufferedImage getOriImage() {
		return orig[curPage];
	}
	
	public BufferedImage[] getOriImageArray() {
		return orig;
	}
	
	public int getPages() {
		return imarray.length;
	}
	
	public int getCurPage() {
		return curPage;
	}
	
	public void setImage(BufferedImage curImage, int value) {
		imarray[value] = curImage;
	}

	

}
