package va.vt.cbil;

import java.util.Arrays;
import java.util.Random;

public class MinMoveMean {
	public static float[][] minMoveMean(float[][][] data, int movWin){
		int width = data.length;
		int height = data[0].length;
		int pages = data[0][0].length;
		
		int left = (movWin-1)/2;
		int right = (movWin-1)/2;
		if(movWin%2==0)
			left++;
		
		float[][] result = new float[width][height];
		
		
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				float sum = 0;
				float min = Integer.MAX_VALUE;
				// right
				for(int k=0;k<right;k++) {
					sum += data[i][j][k];
				}
				
				for(int k=0;k<pages;k++) {
					if(k<=left) {
						sum += data[i][j][k+right];
						min = Math.min(min, sum/(k+right+1));
					}else if(k>left && k<pages-right) {
						sum += data[i][j][k+right] - data[i][j][k-left-1];
						min = Math.min(min, sum/movWin);
					}else {
						sum -= data[i][j][k-left-1];
						min = Math.min(min, sum/(pages-k+left));
					}
				}
				
				result[i][j] = min;
			}
		}
		
		
		return result;
		
	}
	
	// correcr
	public static float minMoveMean(float[] data, int movWin){
		int pages = data.length;

		int left = (movWin-1)/2;
		int right = (movWin-1)/2;
		if(movWin%2==0)
			left++;
		
		float result = 0;
		
		

		float sum = 0;
		float min = Integer.MAX_VALUE;
		// right
		for(int k=0;k<right;k++) {
			sum += data[k];
		}
		
		for(int k=0;k<pages;k++) {
			if(k<=left) {
				sum += data[k+right];
				min = Math.min(min, sum/(k+right+1));
			}else if(k>left && k<pages-right) {
				sum += data[k+right] - data[k-left-1];
				min = Math.min(min, sum/movWin);
			}else {
				sum -= data[k-left-1];
				min = Math.min(min, sum/(pages-k+left));
			}
		}
		
		result = min;
	
		return result;
		
	}
	
	// correcr
	public static float[] moveMean(float[] data, int movWin){
		int pages = data.length;

		int left = (movWin-1)/2;
		int right = (movWin-1)/2;
		if(movWin%2==0)
			left++;
		
		float[] result = new float[pages];
		
		

		float sum = 0;

		// right
		for(int k=0;k<right;k++) {
			sum += data[k];
		}
		
		for(int k=0;k<pages;k++) {
			if(k<=left) {
				sum += data[k+right];
				result[k] = sum/(k+right+1);
			}else if(k>left && k<pages-right) {
				sum += data[k+right] - data[k-left-1];
				result[k] = sum/movWin;
			}else {
				sum -= data[k-left-1];
				result[k] = sum/(pages-k+left);
			}
		}
		return result;
		
	}
	
	public static float getNoiseSigmal(float[] input) {
		float[] diff = new float[input.length-1];
		for(int t=0;t<input.length-1;t++) {
			diff[t] = (input[t+1] - input[t])*(input[t+1] - input[t]);
		}
		float result = getMedian(diff);
		result = (float) Math.sqrt(result/0.9133);
		return result;
	}
	
	public static float getMedian(float[] array) {
		Arrays.sort(array);
		float result = 0;
		int len = array.length;
		if(len%2==0)
			result = (array[len/2 - 1] + array[len/2])/2;
		else 
			result = array[len/2];
		return result;
		
	}
	
	// correct
	public static float minMovMean(float[] data, int movWin) {
		int left = (movWin-1)/2;
		int right = (movWin-1)/2;
		int T = data.length;
		if(movWin%2==0)
			left++;
		float sum = 0;
		float[] result = new float[data.length];
		for(int k=0;k<right;k++) {
			sum += data[k];
		}
		float min = Float.MAX_VALUE;
		for(int k=0;k<T;k++) {
			if(k<=left) {
				sum += data[k+right];
				result[k] = sum/(k+right+1);
			}else if(k>left && k<T-right) {
				sum += data[k+right] - data[k-left-1];
				result[k] = sum/movWin;
			}else {
				sum -= data[k-left-1];
				result[k] = sum/(T-k+left);
			}
			min = Math.min(min, result[k]);
			
		}
		
		return min;
	}
	
	
	// correct
	public static float[][][] subMinMoveMean(float[][][] data, int movWin){
		int width = data.length;
		int height = data[0].length;
		int pages = data[0][0].length;
		
		int left = (movWin-1)/2;
		int right = (movWin-1)/2;
		if(movWin%2==0)
			left++;
		
		float[][][] result = new float[width][height][pages];
		
		
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				float sum = 0;
				float min = Integer.MAX_VALUE;
				// right
				for(int k=0;k<right;k++) {
					sum += data[i][j][k];
				}
				
				for(int k=0;k<pages;k++) {
					if(k<=left) {
						sum += data[i][j][k+right];
						min = Math.min(min, sum/(k+right+1));
					}else if(k>left && k<pages-right) {
						sum += data[i][j][k+right] - data[i][j][k-left-1];
						min = Math.min(min, sum/movWin);
					}else {
						sum -= data[i][j][k-left-1];
						min = Math.min(min, sum/(pages-k+left));
					}
				}
				for(int k=0;k<pages;k++)
					result[i][j][k] = data[i][j][k] - min;
			}
		}
		
		
		return result;
		
	}
	
	// correct
	public static float[][][] subMinMoveMean(float[][][] data, int movWin, int cut, float bias, float stdEst){
		int width = data.length;
		int height = data[0].length;
		int pages = data[0][0].length;
		Random rv = new Random();
		// window
		int left = (movWin-1)/2;
		int right = (movWin-1)/2;
		if(movWin%2==0)
			left++;
		
		float[][][] result = new float[width][height][pages];
		
		// cut number
		int nBlk = Math.max(pages/cut, 1);
		
		for(int n=1;n<=nBlk;n++) {
			int t0 = (n-1)*cut;
			int t1;
			if(n == nBlk)
				t1 = pages;
			else
				t1 = t0+cut;

			for(int i=0;i<width;i++) {
				for(int j=0;j<height;j++) {
					float sum = 0;
					float min = Integer.MAX_VALUE;
					// right
					for(int k=t0;k-t0<right;k++) {
						sum += data[i][j][k];
					}
					for(int k=t0;k<t1;k++) {
						if(k-t0<=left) {
							sum += data[i][j][k+right];
							min = Math.min(min, sum/(k-t0+right+1));
						}else if(k-t0>left && k<t1-right) {
							sum += data[i][j][k+right] - data[i][j][k-left-1];
							min = Math.min(min, sum/movWin);
						}else {
							sum -= data[i][j][k-left-1];
							min = Math.min(min, sum/(t1-k+left));
						}
					}
					min = min - bias;
					for(int k=t0;k<t1;k++) {
						result[i][j][k] = data[i][j][k] - min;
						// TODO:recover
						//if(result[i][j][k]<0)
						//	result[i][j][k] = (float)(rv.nextGaussian()*stdEst);
					}
				}
			}
		}
		return result;
	}
	
	public static float getBias(int moveWindowLength, int cut, double stdEst) {
		float bias = 0;
		Random rv = new Random();
		
		// window
		int left = (moveWindowLength-1)/2;
		int right = (moveWindowLength-1)/2;
		if(moveWindowLength%2==0)
			left++;
		
		float[][] xx = new float[10000][cut];
//		float[][] yy = new float[10000][cut];
		for(int i = 0;i<10000;i++) {
			float sumWin = 0;
			float min = Integer.MAX_VALUE;
			for(int j = 0;j<cut;j++) {
				xx[i][j] = (float) (rv.nextGaussian()*stdEst);
			}
			for(int j=0;j<right;j++) {
				sumWin += xx[i][j];
			}
			
			for(int j = 0;j<cut;j++) {	
				if(j<=left) {
					sumWin += xx[i][j+right];
					min = Math.min(min, sumWin/(j+right+1));
				}else if(j>left && j<cut-right) {
					sumWin += xx[i][j+right] - xx[i][j-left-1];
					min = Math.min(min, sumWin/moveWindowLength);
				}else {
					sumWin -= xx[i][j-left-1];
					min = Math.min(min, sumWin/(cut-j+left));
				}
			}
			bias += min;
		}
		bias = bias/10000;

		return bias;
	}
	
	public static float getBiasSP(int T, int moveWindowLength) {
		float bias = 0;
		Random rv = new Random();
		
		// window
		int left = (moveWindowLength-1)/2;
		int right = (moveWindowLength-1)/2;
		if(moveWindowLength%2==0)
			left++;
		
		for(int i = 0;i<10000;i++) {
			float sumWin = 0;
			float max = Integer.MIN_VALUE;
			float[] xx = new float[T];
			for(int j = 0;j<T;j++) {
				xx[j] = (float) (rv.nextGaussian());
			}
			for(int j=0;j<right;j++) {
				sumWin += xx[j];
			}
			
			for(int j = 0;j<T;j++) {	
				if(j<=left) {
					sumWin += xx[j+right];
					max = Math.max(max, sumWin/(j+right+1));
				}else if(j>left && j<T-right) {
					sumWin += xx[j+right] - xx[j-left-1];
					max = Math.max(max, sumWin/moveWindowLength);
				}else {
					sumWin -= xx[j-left-1];
					max = Math.max(max, sumWin/(T-j+left));
				}
			}
			bias += max;
		}
		bias = bias/10000;

		return bias;
	}
	
	public static float[][] maxMoveMean(float[][][] dFip,int moveWindowLength){
		int W = dFip.length;
		int H = dFip[0].length;
		int T = dFip[0][0].length;
		
		int left = (moveWindowLength-1)/2;
		int right = (moveWindowLength-1)/2;
		if(moveWindowLength%2==0)
			left++;
		
		float[][] dFMax = new float[W][H];
		for(int x = 0;x<W;x++) {
			for(int y=0;y<H;y++) {
				float sumWin = 0;
				float max = Integer.MIN_VALUE;
				float[] xx = dFip[x][y];
				for(int j=0;j<right;j++) {
					sumWin += xx[j];
				}
				
				for(int j = 0;j<T;j++) {	
					if(j<=left) {
						sumWin += xx[j+right];
						max = Math.max(max, sumWin/(j+right+1));
					}else if(j>left && j<T-right) {
						sumWin += xx[j+right] - xx[j-left-1];
						max = Math.max(max, sumWin/moveWindowLength);
					}else {
						sumWin -= xx[j-left-1];
						max = Math.max(max, sumWin/(T-j+left));
					}
				}
				if(max<0)
					max = 0;
				dFMax[x][y] = max;
			}
		}
		
		return dFMax;
	}
	
}




