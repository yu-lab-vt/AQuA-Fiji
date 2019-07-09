package va.vt.cbil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FtsLst implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Curve curve = null;
	Loc loc = null;
	Basic basic = null;
	Notes notes = null;
	Propagation propagation = null;
	ResReg region = null;
	NetWork networkAll = null;
	NetWork network = null;
	HashMap<Integer, ArrayList<int[]>> border = null;
	public FtsLst() {
		curve = new Curve();
		loc = new Loc();
		basic = new Basic();
		propagation = new Propagation();
		notes = new Notes();
		region = new ResReg();
		border = new HashMap<>();
	}
	
	public void addQuickFeature(int label, float dffMaxZ, float dffMaxPval, int[] rgT1, int its, int ite) {
		curve.addQuickFeature(label, dffMaxZ, dffMaxPval, rgT1, its, ite);
	}
}
class Curve implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<Integer, Float> dffMaxZ = null;
	HashMap<Integer, Float> dffMaxPval = null;
	HashMap<Integer, int[]> rgt1 = null;
	HashMap<Integer, Integer> tBegin = null;
	HashMap<Integer, Integer> tEnd = null;
	HashMap<Integer, Float> dffMax = null;
	HashMap<Integer, Float> dffMax2 = null;
	HashMap<Integer, Float> dffMaxFrame = null;
	HashMap<Integer, Float> rise19 = null;
	HashMap<Integer, Float> fall91 = null;
	HashMap<Integer, Float> width55 = null;
	HashMap<Integer, Float> width11 = null;
	HashMap<Integer, Float> decayTau = null;
	HashMap<Integer, Float> duration = null;
	
	
	public Curve() {
		dffMaxZ = new HashMap<>();
		dffMaxPval = new HashMap<>();
		rgt1 = new HashMap<>();
		tBegin = new HashMap<>();
		tEnd = new HashMap<>();
		dffMax = new HashMap<>();
		dffMax2 = new HashMap<>();
		dffMaxFrame = new HashMap<>();
		rise19 = new HashMap<>();
		fall91 = new HashMap<>();
		width55 = new HashMap<>();
		width11 = new HashMap<>();
		decayTau = new HashMap<>();
		duration = new HashMap<>();
	}
	
	public void addQuickFeature(int label, float dffMaxZ, float dffMaxPval, int[] rgT1, int its, int ite) {
		this.dffMaxZ.put(label, dffMaxZ);
		this.dffMaxPval.put(label, dffMaxPval);
		this.rgt1.put(label, rgT1);
		this.tBegin.put(label, its);
		this.tEnd.put(label, ite);
		duration.put(label, (ite-its+1)*1f);
	}
	
}

class Notes implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String[] propDirectionOrder = null;
	
	public Notes() {
		
	}
}

class NetWork implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int[][] nOccurSameLoc = null;
	int[] nOccurSameTime = null;
	HashMap<Integer,ArrayList<Integer>> occurSameLocList = null;
	HashMap<Integer,ArrayList<Integer>> occurSameLocList2 = null;
	HashMap<Integer,HashSet<Integer>> occurSameTimeList = null;
	
	public NetWork(int[][] nOccurSameLoc, int[] nOccurSameTime, HashMap<Integer,ArrayList<Integer>> occurSameLocList,
			HashMap<Integer,ArrayList<Integer>> occurSameLocList2, HashMap<Integer,HashSet<Integer>> occurSameTimeList) {
		this.nOccurSameLoc = nOccurSameLoc;
		this.nOccurSameTime = nOccurSameTime;
		this.occurSameLocList = occurSameLocList;
		this.occurSameLocList2 = occurSameLocList2;
		this.occurSameTimeList = occurSameTimeList;
	}
}

class Basic implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<Integer, int[][]> map = null;
	HashMap<Integer, Float> area = null;
	HashMap<Integer, Float> perimeter = null;
	HashMap<Integer, Float> circMetric = null;
	public Basic() {
		map = new HashMap<>();
		area = new HashMap<>();
		perimeter = new HashMap<>();
		circMetric = new HashMap<>();
	}
	
	
	
	
}

class Loc implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<Integer, Integer> t0 = null;
	HashMap<Integer, Integer> t1 = null;
	HashMap<Integer, ArrayList<int[]>> x3D = null;
	HashMap<Integer, HashSet<Integer>> x2D = null;
	
	public Loc(HashMap<Integer, Integer> t0, HashMap<Integer, Integer> t1, HashMap<Integer, ArrayList<int[]>> x3D, HashMap<Integer, HashSet<Integer>> x2D ) {
		this.t0 = t0;
		this.t1 = t1;
		this.x3D = x3D;
		this.x2D = x2D;
	}

	public Loc() {
		t0 = new HashMap<>();
		t1 = new HashMap<>();
		x3D = new HashMap<>();
		x2D = new HashMap<>();
	}
	
	
}

class Propagation implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<Integer,float[][]> propGrow = null;
	HashMap<Integer,float[]> propGrowOverall = null;
	HashMap<Integer,float[][]> propShrink = null;
	HashMap<Integer,float[]> propShrinkOverall = null;
	HashMap<Integer,float[][]> areaChange = null;
	HashMap<Integer,float[]> areaChangeRate = null;
	HashMap<Integer,float[][]> areaFrame = null;
	
	public Propagation() {
		propGrow = new HashMap<>();
		propGrowOverall = new HashMap<>();
		propShrink = new HashMap<>();
		propShrinkOverall = new HashMap<>();
		areaChange = new HashMap<>();
		areaChangeRate = new HashMap<>();
		areaFrame = new HashMap<>();
	}
}

class ResReg implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	LandMark landMark = null;
	LandMarkDist landmarkDist = null;
	LandMarkDir landmarkDir = null;
	Cell cell = null;
	public ResReg() {
		landMark = new LandMark();
		cell = new Cell();
	}
}

class LandMark implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<Integer, boolean[][]> mask = null;
	HashMap<Integer, ArrayList<int[]>> border = null;
	float[][] center = null;
	float[] centerBorderAvgDist = null;
	
}

class Cell implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<Integer, boolean[][]> mask = null;
	float[][] center = null;
	HashMap<Integer, ArrayList<int[]>> border = null;
	float[] centerBorderAvgDist = null;
	boolean[][] incluLmk = null;
	boolean[][] memberIdx = null;
	float[][] dist2border = null;
	float[][] dist2borderNorm = null;

}

class LandMarkDir implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	float[][] chgToward = null;
	float[][] chgAway = null;
	HashMap<Integer,float[][][]> pixTwd = null;
	HashMap<Integer,float[][][]> pixAwy = null;
	float[][][] chgTowardThr = null;
	float[][][] chgAwayThr = null;
	HashMap<Integer,float[][][]> chgTowardThrFrame = null;
	HashMap<Integer,float[][][]> chgAwayThrFrame = null;
	
	public LandMarkDir(float[][] chgToward, float[][] chgAway, HashMap<Integer,float[][][]> pixTwd, 
			HashMap<Integer,float[][][]> pixAwy, float[][][] chgTowardThr, float[][][] chgAwayThr,
			HashMap<Integer,float[][][]> chgTowardThrFrame, HashMap<Integer,float[][][]> chgAwayThrFrame) {
		this.chgToward = chgToward;
		this.chgAway = chgAway;
		this.pixTwd = pixTwd;
		this.pixAwy = pixAwy;
		this.chgTowardThr = chgTowardThr;
		this.chgAwayThr = chgAwayThr;
		this.chgTowardThrFrame = chgTowardThrFrame;
		this.chgAwayThrFrame = chgAwayThrFrame;
	}

	public LandMarkDir() {
		// TODO Auto-generated constructor stub
	}
	
}


class LandMarkDist implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<Integer, float[][]> distPerFrame = null;
	float[][] distAvg = null;
	float[][] distMin = null;
	
	public LandMarkDist(HashMap<Integer, float[][]> distPerFrame, float[][] distAvg, float[][] distMin){
		this.distPerFrame = distPerFrame;
		this.distAvg = distAvg;
		this.distMin = distMin;
	}

	public LandMarkDist() {
		// TODO Auto-generated constructor stub
	}
}



class QuickFeatureResult {
	FtsLst ftsLst= null;
	float[][] dffMatExt = null;
	int[][][] evtMap = null;
	
	public QuickFeatureResult(FtsLst ftsLst, float[][] dffMatExt, int[][][] evtMap) {
		this.ftsLst = ftsLst;
		this.dffMatExt = dffMatExt;
		this.evtMap = evtMap;
	}
}

class FeatureTopResult{
	FtsLst ftsLst= null;
	float[][][] dffMat = null;
	float[][][] dMat = null;
	
	public FeatureTopResult(FtsLst ftsLst, float[][][] dffMat, float[][][] dMat) {
		this.ftsLst = ftsLst;
		this.dffMat = dffMat;
		this.dMat = dMat;
	}
	
}