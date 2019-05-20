package va.vt.cbil;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
 

public class PlaneHistgram {

    public static BufferedImage paintPlaneHistogram(String title, float[] values) {
//        int width = str.length * histogramWidth+str.length*histogramPitch+50;
        int width = 650;
        int height = 400;
        int border = 75;
        int histNum = 20;

        
        // histgram number calculate
        float maxValue = 0;
        float minValue = Float.MAX_VALUE;
        int[] hist = new int[histNum];
        
        for(int i=0;i<values.length;i++) {
        	maxValue = Math.max(values[i], maxValue);
        	minValue = Math.min(values[i], minValue);
        }
        
        
        float binW =  maxValue/histNum;
        for(int i=0;i<values.length;i++) {
        	int cur = (int) (values[i]/binW);
        	if(cur==histNum)
        		cur--;
        	hist[cur] = hist[cur]+1;
        }
        
        int maxFre = 0;
        for(int i=0;i<histNum;i++) {
        	maxFre = Math.max(hist[i],maxFre);
        }
        
        // border
        BufferedImage bufferImage = new BufferedImage(width + 2*border, height + 2*border,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferImage.getGraphics();
//        g.setColor(Color.LIGHT_GRAY);
        g.setColor(Color.white);
        g.fillRect(0, 0, width + 2*border, height + 2*border);

        // axis
        FontMetrics metrics = null;
        g.setFont(new Font(null, Font.PLAIN, 18));
        g.setColor(Color.black);
        g.drawString(title, (bufferImage.getWidth() - g.getFontMetrics()
                .stringWidth(title)) >> 1, height + border + 40);
// 
        g.setFont(new Font(null, Font.PLAIN, 12));
        metrics = g.getFontMetrics();
        

 
        g.setColor(Color.BLACK);
        g.drawLine(border, border, border, height + border);
        g.drawLine(border, border, width + border, border);
        g.drawLine(border, border+height, border+width, height + border);
        g.drawLine(width + border, border, width + border, border + height);
        
        // y axis
        int[] results = roundmax(maxFre);
        double multi =  Math.pow(10, results[1]-1);
        int border2 = 0; 
        int delta = (height - 2*border2)/10;
        int shortline = 5;
        for(int i=0;i<= 10;i++) {
        	int h = border + border2 + delta*i; 
        	g.drawLine(border, h, border + shortline, h);
        	double v =(10 - i) * results[0]/10;
        	String ylabel = String.format("%.1f", v);
        	g.drawString(ylabel, border-ylabel.length()*8, h+5);
        }
        g.drawString("x", border-25, border + border2-11);
        g.drawString("10", border-20, border + border2-10);
        g.setFont(new Font(null, Font.PLAIN, 10));
        g.drawString(""+results[1], border-7, border + border2-15);
        g.setFont(new Font(null, Font.PLAIN, 12));
        
        // x axis
        int binWidth = width/histNum; 
        int[] results2 = roundmax(maxValue);
        for(int i=0;i<= histNum;i++) {
        	int w = border +  binWidth*i; 
        	g.drawLine(w, border + height, w, border + height - shortline);
        	String xlabel = String.format("%.1f", maxValue/histNum*i/Math.pow(10, results2[1]-1));
        	g.drawString(xlabel, w - 7, border + height - shortline + 20);
        }
        g.drawString("x", border + width+15, border + height +9);
        g.drawString("10", border + width+20, border + + height +10);
        g.setFont(new Font(null, Font.PLAIN, 10));
        g.drawString(""+(results2[1]-1), border + width + 33, border + height+5);
        g.setFont(new Font(null, Font.PLAIN, 12));
        
        
        // bin
        
        for(int i=0;i<histNum;i++) {
        	g.setColor(Color.BLUE);
        	int w = border +  binWidth*i; 
        	int y1 = (int)(border + (1 - (double)hist[i]/(results[0]*10*multi))*(height-2*border2));
        	int y2 = border + height - y1;
        	g.fillRect(w, y1, binWidth, y2);
        	g.setColor(Color.black);
        	g.drawLine(w, y1, w+binWidth, y1);
        	g.drawLine(w, y1, w, y1+y2);
        	g.drawLine(w+binWidth, y1, w+binWidth, y1+y2);
        }
        

 
        return bufferImage;
    }
    
    public static void main(String[] args)  {
    	int[] r = roundmax(5f);
    	System.out.println(r[0] + " "+r[1]);
    }
 
    public static int[] roundmax(float value) {
    	if(value==0)
    		return new int[] {0,0};
    	int cur = 1;
    	int cnt = 0;
    	if(value>=1) {
	    	int cur1 = 1;
	    	while(value >=cur1) {
	    		cnt ++;
	    		cur1 *= 10;
	    	}
	    	cur = cur1/10;
	    	cnt--;
	    	cur = (int) Math.ceil(value/cur);	    	
    	}else {
//    		cnt = 0;
    		float v = value;
    		while(v<cur) {
    			v *= 10;
    			cnt--;
    		}
    		cur = (int) Math.ceil(v/cur);	    	
    	}
		return new int[] {cur,cnt};
    	
    }
}
