package va.vt.cbil;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Arrays;
 

public class PlaneBoxPlot {

    public static BufferedImage paintPlaneBoxPlot(String title, float[] values) {
//        int width = str.length * histogramWidth+str.length*histogramPitch+50;
        int width = 650;
        int height = 400;
        int border = 75;
        int histNum = 20;

        // calculate
        int len = values.length;
        Arrays.sort(values);
        float medianValue = 0;
        float Q1 = 0;
        float Q3 = 0;
        if(len%2==0)
        	medianValue = (values[len/2] + values[len/2+1])/2;
        else
        	medianValue = values[len/2 + 1];
        Q1 = values[Math.max((int)Math.round(len*0.25)-1,1)]; 
        Q3 = values[Math.max((int)Math.round(len*0.75)-1,1)]; 
        float IQR = Q3-Q1;
        float min = Q1 - 1.5f*IQR;
        float max = Q3 + 1.5f*IQR;
        float maxValue = 0;
        float minValue = Float.MAX_VALUE;
        int[] hist = new int[histNum];
        
        for(int i=0;i<values.length;i++) {
        	maxValue = Math.max(values[i],maxValue);
        	minValue = Math.min(values[i],minValue);
        }
        
        
        // border
        BufferedImage bufferImage = new BufferedImage(width + 2*border, height + 2*border,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferImage.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, width + 2*border, height + 2*border);
        g.setColor(Color.BLACK);
        g.drawLine(border, border, border, height + border);
        g.drawLine(border, border, width + border, border);
        g.drawLine(border, border+height, border+width, height + border);
        g.drawLine(width + border, border, width + border, border + height);

        // axis
        FontMetrics metrics = null;
        g.setFont(new Font(null, Font.PLAIN, 18));
        g.setColor(Color.black);
        g.drawString(title, (bufferImage.getWidth() - g.getFontMetrics()
                .stringWidth(title)) >> 1, height + border + 40);
        g.setFont(new Font(null, Font.PLAIN, 12));
        metrics = g.getFontMetrics();
        
        
        // y axis
        int[] results = roundmax(Math.max(maxValue, max));
        float ceil =  (float) (results[0]*Math.pow(10, results[1]));
        int[] results2 = roundmin(Math.min(minValue, min));
        float floor =  (float) (results2[0]*Math.pow(10, results2[1]));
        
        int border2 = 20; 
        int delta = (height - 2*border2)/10;
        int shortline = 5;
        
        for(int i=0;i<= 10;i++) {
        	int h = border + border2 + delta*i; 
        	g.drawLine(border, h, border + shortline, h);
        	float v = (float) ((floor + (ceil-floor)/10*(10 - i))/Math.pow(10, results[1]));
        	String ylabel = String.format("%.2f", v);
        	g.drawString(ylabel, border-ylabel.length()*8, h+5);
        }
        g.drawString("x", border-25, border + border2-11);
        g.drawString("10", border-20, border + border2-10);
        g.setFont(new Font(null, Font.PLAIN, 10));
        g.drawString(""+(results[1]), border-7, border + border2-15);
        g.setFont(new Font(null, Font.PLAIN, 12));
        
        
        
        
        
        int linelength = 40;
        int w = border + width/2;
        int w1 = w-linelength;
        int w2 = w+linelength;
        int w10 = w-linelength/2;
        int w20 = w+linelength/2;
        
        // drawline
        int yMedian = (int) (border + border2 + ((height-2*border2)*(ceil-medianValue))/(ceil-floor));
        int yQ1 = (int) (border + border2 + ((height-2*border2)*(ceil-Q1))/(ceil-floor));
        int yQ3 = (int) (border + border2 + ((height-2*border2)*(ceil-Q3))/(ceil-floor));
        int yMin = (int) (border + border2 + ((height-2*border2)*(ceil-min))/(ceil-floor));
        int yMax = (int) (border + border2 + ((height-2*border2)*(ceil-max))/(ceil-floor));
        g.setColor(Color.red);
        g.drawLine(w1, yMedian, w2, yMedian);
        g.setColor(Color.blue);
        g.drawLine(w1, yQ1, w2, yQ1);
        g.drawLine(w1, yQ3, w2, yQ3);
        g.drawLine(w1, yQ1, w1, yQ3);
        g.drawLine(w2, yQ1, w2, yQ3);
        g.setColor(Color.black);
        g.drawLine(w10, yMin, w20, yMin);
        g.drawLine(w10, yMax, w20, yMax);
        int dashed = 8;
        int n1 = Math.round((yQ3-yMax)/dashed);
        for(int i=0;i<n1;i=i+2) {
        	int hcur = yMax + i*dashed;
        	g.drawLine(w, hcur, w, hcur+dashed);
        }
        int n2 = Math.round((yMin-yQ1)/dashed);
        for(int i=0;i<n1;i=i+2) {
        	int hcur = yQ1 + i*dashed;
        	g.drawLine(w, hcur, w, hcur+dashed);
        }
        
        g.setColor(Color.red);
        for(int i=0;i<values.length;i++) {
        	if(values[i]<min || values[i]>max) {
        		int hcur = (int) (border + border2 + ((height-2*border2)*(ceil-values[i]))/(ceil-floor));
        		g.drawOval(w-2, hcur, 4, 4);
        	}
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
    
    public static int[] roundmin(float value) {
    	if(value==0)
    		return new int[] {0,0};
    	else if(value<0) {
    		int[] r = roundmax(-value);
    		r[0] = - r[0];
    		return r;
    	}
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
	    	cur = (int) Math.floor(value/cur);	    	
    	}else {
//    		cnt = 0;
    		float v = value;
    		while(v<cur) {
    			v *= 10;
    			cnt--;
    		}
    		cur = (int) Math.floor(v/cur);	    	
    	}
		return new int[] {cur,cnt};
    	
    }
}
