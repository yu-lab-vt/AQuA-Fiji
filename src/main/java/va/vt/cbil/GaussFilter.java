package va.vt.cbil;

public class GaussFilter {
	public static float[] gaussFilter(float[] data, float smoX) {
		int W = data.length;
		int exTX =  (int)(Math.ceil(smoX*2));
		
		float[] filter = gaussFilterGenerator(smoX);
		
		FftFilter ff = new FftFilter(filter);
		float[] dataExtend = extendMatirx(data,smoX);
		dataExtend = ff.apply(dataExtend);
		
		float[] result = new float[W];
		for(int x=0;x<W;x++) {
			result[x] = dataExtend[x+exTX];
		}
		
		return result;
		
	}
	
	

	public static float[][] gaussFilter(float[][] data, float smoX, float smoY) {
		int W = data.length;
		int H = data[0].length;
		int exTX =  (int)(Math.ceil(smoX*2));
		int exTY =  (int)(Math.ceil(smoY*2));
		
		float[][] filter = gaussFilterGenerator(smoX,smoY);
		
		FftFilter ff = new FftFilter(filter);
		float[][] dataExtend = extendMatirx(data,smoX,smoY);
		dataExtend = ff.apply(dataExtend);
		
		float[][] result = new float[W][H];
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				result[x][y] = dataExtend[x+exTX][y+exTY];
			}
		}
		
		return result;
		
	}
	
	public static float[][][] gaussFilter(float[][][] data, float smoX, float smoY, float smoZ) {
		int W = data.length;
		int H = data[0].length;
		int T = data[0][0].length;
		int exTX =  (int)(Math.ceil(smoX*2));
		int exTY =  (int)(Math.ceil(smoY*2));
		int exTZ =  (int)(Math.ceil(smoZ*2));
		
		float[][][] filter = gaussFilterGenerator(smoX,smoY,smoZ);
		
		FftFilter ff = new FftFilter(filter);
		float[][][] dataExtend = extendMatirx(data,smoX,smoY,smoZ);
		dataExtend = ff.apply(dataExtend);
		
		float[][][] result = new float[W][H][T];
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				for(int t=0;t<T;t++) {
					result[x][y][t] = dataExtend[x+exTX][y+exTY][t+exTZ];
				}
			}
		}
		
		return result;
		
	}
	
	public static float[] gaussFilterGenerator(float smoX) {
		int W = (int)(Math.ceil(smoX*2)*2)+1;
		int centerX = W/2;
		
		float[] filter = new float[W];
		float sum = 0;
		for(int x=0;x<W;x++) {
			filter[x] = (float) Math.exp(-((x-centerX)*(x-centerX)/(smoX*smoX))/2);
			sum += filter[x];
		}

		for(int x=0;x<W;x++) {
			filter[x] /= sum;
		}
		return filter;
	}
	
	public static float[][] gaussFilterGenerator(float smoX,float smoY) {
		int W = (int)(Math.ceil(smoX*2)*2)+1;
		int H = (int)(Math.ceil(smoY*2)*2)+1;
		int centerX = W/2;
		int centerY = H/2;
		
		float[][] filter = new float[W][H];
		float sum = 0;
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				filter[x][y] = (float) Math.exp(-((x-centerX)*(x-centerX)/(smoX*smoX) + (y-centerY)*(y-centerY)/(smoY*smoY))/2);
				sum += filter[x][y];
			}
		}

		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				filter[x][y] /= sum;
			}
		}
		return filter;
	}
	
	public static float[] extendMatirx(float[] data, float smoX) {
		int W = data.length;
		int exTX =  (int)(Math.ceil(smoX*2));
		float[] dataExtend = new float[W + 2*exTX];
		
		for(int x=0;x<W;x++) {
			dataExtend[x+exTX] = data[x];
		}
		
		for(int x=0;x<exTX;x++) {
			dataExtend[x] = data[0];
		}
		
		for(int x=W+exTX;x<W+2*exTX;x++) {
			dataExtend[x] = data[W-1];
		}
		
		return dataExtend;
	}
	
	
	public static float[][] extendMatirx(float[][] data,float smoX,float smoY){
		int W = data.length;
		int H = data[0].length;
		int exTX =  (int)(Math.ceil(smoX*2));
		int exTY =  (int)(Math.ceil(smoY*2));
		
		float[][] dataExtend = new float[W + 2*exTX][H + 2*exTY];
		
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				dataExtend[x+exTX][y+exTY] = data[x][y];
			}
		}
		
		for(int x=0;x<exTX;x++) {
			for(int y=0;y<exTY;y++) {
				dataExtend[x][y] = data[0][0];
			}
			for(int y=H+2*exTY-1;y>=H+exTY;y--) {
				dataExtend[x][y] = data[0][H-1];
			}
		}
		
		for(int x=W+2*exTX-1;x>=W+exTY;x--) {
			for(int y=0;y<exTY;y++) {
				dataExtend[x][y] = data[W-1][0];
			}
			for(int y=H+2*exTY-1;y>=H+exTY;y--) {
				dataExtend[x][y] = data[W-1][H-1];
			}
		}
		
		for(int x=exTX;x<W+exTY;x++) {
			for(int y=0;y<exTY;y++) {
				dataExtend[x][y] = data[x-exTX][0];
			}
			for(int y=H+2*exTY-1;y>=H+exTY;y--) {
				dataExtend[x][y] = data[x-exTX][H-1];
			}
		}
		
		for(int y=exTY;y<H+exTY;y++) {
			for(int x=0;x<exTX;x++) {
				dataExtend[x][y] = data[0][y-exTY];
			}
			for(int x=W+2*exTX-1;x>=W+exTY;x--) {
				dataExtend[x][y] = data[W-1][y-exTY];
			}
		}
		return dataExtend;
	}
	
	/**
	 * Generate a gaussian filter matrix according to parameters 
	 * 
	 * @param smoX the standard variance in X direction
	 * @param smoY the standard variance in Y direction
	 * @param smoZ the standard variance in Z direction
	 * @return the gaussian filter
	 */
	public static float[][][] gaussFilterGenerator(float smoX,float smoY, float smoZ) {
		int W = (int)(Math.ceil(smoX*2)*2)+1;
		int H = (int)(Math.ceil(smoY*2)*2)+1;
		int T = (int)(Math.ceil(smoZ*2)*2)+1;
		int centerX = W/2;
		int centerY = H/2;
		int centerZ = T/2;
		
		float[][][] filter = new float[W][H][T];
		float sum = 0;
		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				for(int z=0;z<T;z++) {
					filter[x][y][z] = (float) Math.exp(-((x-centerX)*(x-centerX)/(smoX*smoX) + (y-centerY)*(y-centerY)/(smoY*smoY) + (z-centerZ)*(z-centerZ)/(smoZ*smoZ))/2);
					sum += filter[x][y][z];
				}
			}
		}

		for(int x=0;x<W;x++) {
			for(int y=0;y<H;y++) {
				for(int z=0;z<T;z++) {
					filter[x][y][z] /= sum;
				}
			}
		}
		return filter;
	}
	
	public static float[][][] extendMatirx(float[][][] data,float smoX,float smoY,float smoZ){
		int W = data.length;
		int H = data[0].length;
		int T = data[0][0].length;
		int exTX =  (int)(Math.ceil(smoX*2));
		int exTY =  (int)(Math.ceil(smoY*2));
		int exTZ =  (int)(Math.ceil(smoZ*2));
		
		float[][][] dataExtend = new float[W + 2*exTX][H + 2*exTY][T + 2*exTZ];
		
		for(int x=exTX;x<W+exTX;x++) {
			dataExtend[x] = extendMatirx(data[x-exTX],smoY,smoZ);
		}
		for(int x=0;x<exTX;x++) {
			for(int y=0;y<H + 2*exTY;y++) {
				for(int z=0;z<T + 2*exTZ;z++) {
					dataExtend[x][y][z] = dataExtend[exTX][y][z];
				}
			}
		}
		
		for(int x=W+2*exTX-1;x>=W+exTX;x--) {
			for(int y=0;y<H + 2*exTY;y++) {
				for(int z=0;z<T + 2*exTZ;z++) {
					dataExtend[x][y][z] = dataExtend[W+exTX-1][y][z];
				}
			}
		}

		return dataExtend;
	}
}
