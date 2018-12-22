package va.vt.cbil;

import java.util.ArrayList;
import java.util.HashMap;

public class Res {
	float[] charxIn = null;
	int[] tw = null;
	int[] iSeed = null;
	int rgHs = 0;
	int rgHe = 0;
	int rgWs = 0;
	int rgWe = 0;
	int rgTs = 0;
	int rgTe = 0;
	boolean cont = true;
//	boolean[][] fiux = null;
	HashMap<Integer,int[]> fiux = null;
//	boolean[][] pixBad = null;
	ArrayList<int[]> pixBad = null;
	ArrayList<Integer> pixNew = null;
	HashMap<Integer,int[]> twMap = null;
	boolean stg = false;
	public Res(float[] x1, int[] tw, int[] iSeed, int rgH1s, int rgH1e, int rgW1s, int rgW1e, int rgTs, int rgTe, boolean cont, boolean stg) {
		this.charxIn = x1;
		this.tw = tw;
		this.iSeed = iSeed;
		this.rgHs = rgH1s;
		this.rgHe = rgH1e;
		this.rgWs = rgW1s;
		this.rgWe = rgW1e;
		this.rgTs = rgTs;
		this.rgTe = rgTe;
		this.cont = cont;
		this.stg = stg;
		
	}
}
