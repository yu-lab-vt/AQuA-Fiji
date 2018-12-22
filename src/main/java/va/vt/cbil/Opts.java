package va.vt.cbil;

import java.io.Serializable;

public class Opts implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int minSize = 8;
	float smoXY = 0.5f;
	float thrARScl = 2;
	float thrTWScl = 2;
	float thrExtZ = 1;
	int cDelay = 2;
	int cRise = 2;
	float gtwSmo = 1;
	int maxStp = 11;
	int zThr = 2;
	int ignoreMerge = 1;
	int mergeEventDiscon = 0;
	int mergeEventCorr = 0;
	int mergeEventMaxTimeDif = 2;
	
	int regMaskGap = 5;
	boolean usePG = true;
	int cut = 200;
	int movAvgWin = 25;
	int extendSV = 1;
	int legacyModeActRun = 1;
	int getTimeWindowExt = 50;
	int seedNeib = 1;
	int seedRemoveNeib = 2;
	int thrSvSig = 4;
	int superEventdensityFirst = 1;
	int gtwGapSeedRatio = 4;
	int gtwGapSeedMin = 5;
	float cOver = 0.2f;
	float minShow1 = 0.2f;
	float minShowEvtGUI = 0;
	int ignoreTau = 1;
	int correctTrend = 1;
	int extendEvtRe = 0;
	
	float frameRate = 0.5f;
	float spatialRes = 0.5f;
	float varEst = 0.02f;
	int fgFluo = 0;
	int bgFluo = 0;
	float northx = 0;
	float northy = 1;
	
	int W = 501;
	int H = 500;
	int T = 1614;
	int maxValueDat = 65535;
	@Override
	public String toString() {
		return null;
	}
	
	public Opts( int index) {
		switch(index) {
			case 1:
				preset1();
				break;
			case 2:
				preset2();
				break;
			case 3:
				preset3();
				break;
			case 4:
				preset4();
				break;
			default:
				break;
		}
	}
	
	public void preset1() {
		System.out.println("Preset 1");
		minSize = 8;
		smoXY = 0.5f;
		thrARScl = 2;
		thrTWScl = 2;
		thrExtZ = 1;
		cDelay = 2;
		cRise = 2;
		gtwSmo = 1;
		maxStp = 11;
		zThr = 2;
		ignoreMerge = 1;
		mergeEventDiscon = 0;
		mergeEventCorr = 0;
		mergeEventMaxTimeDif = 2;
		
		regMaskGap = 5;
		usePG = true;
		cut = 200;
		movAvgWin = 25;
		extendSV = 1;
		legacyModeActRun = 1;
		getTimeWindowExt = 50;
		seedNeib = 1;
		seedRemoveNeib = 2;
		thrSvSig = 4;
		superEventdensityFirst = 1;
		gtwGapSeedRatio = 4;
		gtwGapSeedMin = 5;
		cOver = 0.2f;
		minShow1 = 0.2f;
		minShowEvtGUI = 0;
		ignoreTau = 1;
		correctTrend = 1;
		extendEvtRe = 0;
		
		frameRate = 0.5f;
		spatialRes = 0.5f;
		varEst = 0.02f;
		fgFluo = 0;
		bgFluo = 0;
		northx = 0;
		northy = 1;
	}
	
	public void preset2() {
		System.out.println("Preset 2");
		minSize = 15;
		smoXY = 0.5f;
		thrARScl = 1.75f;
		thrTWScl = 2;
		thrExtZ = 1;
		cDelay = 2;
		cRise = 2;
		gtwSmo = 1;
		maxStp = 11;
		zThr = 2;
		ignoreMerge = 1;
		mergeEventDiscon = 0;
		mergeEventCorr = 0;
		mergeEventMaxTimeDif = 2;
		
		regMaskGap = 5;
		usePG = true;
		cut = 200;
		movAvgWin = 25;
		extendSV = 1;
		legacyModeActRun = 1;
		getTimeWindowExt = 50;
		seedNeib = 1;
		seedRemoveNeib = 2;
		thrSvSig = 4;
		superEventdensityFirst = 1;
		gtwGapSeedRatio = 4;
		gtwGapSeedMin = 5;
		cOver = 0.2f;
		minShow1 = 0.2f;
		minShowEvtGUI = 0;
		ignoreTau = 1;
		correctTrend = 1;
		extendEvtRe = 0;
		
		frameRate = 1;
		spatialRes = 1;
		varEst = 0.02f;
		fgFluo = 0;
		bgFluo = 0;
		northx = 0;
		northy = 1;
	}
	
	public void preset3() {
		System.out.println("Preset 3");
		minSize = 8;
		smoXY = 0.5f;
		thrARScl = 2;
		thrTWScl = 2;
		thrExtZ = 1;
		cDelay = 2;
		cRise = 2;
		gtwSmo = 1;
		maxStp = 11;
		zThr = 0;
		ignoreMerge = 1;
		mergeEventDiscon = 0;
		mergeEventCorr = 0;
		mergeEventMaxTimeDif = 2;
		
		regMaskGap = 5;
		usePG = true;
		cut = 200;
		movAvgWin = 25;
		extendSV = 1;
		legacyModeActRun = 1;
		getTimeWindowExt = 50;
		seedNeib = 1;
		seedRemoveNeib = 2;
		thrSvSig = 4;
		superEventdensityFirst = 0;
		gtwGapSeedRatio = 4;
		gtwGapSeedMin = 5;
		cOver = 0.2f;
		minShow1 = 0.2f;
		minShowEvtGUI = 0.5f;
		ignoreTau = 1;
		correctTrend = 0;
		extendEvtRe = 0;
		
		frameRate = 1;
		spatialRes = 1;
		varEst = 0.02f;
		fgFluo = 0;
		bgFluo = 0;
		northx = 0;
		northy = 1;
	}
	
	public void preset4() {
		System.out.println("Preset 4");
		minSize = 8;
		smoXY = 1;
		thrARScl = 2;
		thrTWScl = 2;
		thrExtZ = 2;
		cDelay = 2;
		cRise = 2;
		gtwSmo = 0.3f;
		maxStp = 11;
		zThr = 7;
		ignoreMerge = 0;
		mergeEventDiscon = 10;
		mergeEventCorr = 0;
		mergeEventMaxTimeDif = 2;
		
		regMaskGap = 5;
		usePG = true;
		cut = 40;
		movAvgWin = 20;
		extendSV = 0;
		legacyModeActRun = 0;
		getTimeWindowExt = 50;
		seedNeib = 1;
		seedRemoveNeib = 2;
		thrSvSig = 1;
		superEventdensityFirst = 1;
		gtwGapSeedRatio = 4;
		gtwGapSeedMin = 5;
		cOver = 0;
		minShow1 = 0.2f;
		minShowEvtGUI = 0;
		ignoreTau = 0;
		correctTrend = 1;
		extendEvtRe = 0;
		
		frameRate = 1;
		spatialRes = 1;
		varEst = 0.02f;
		fgFluo = 0;
		bgFluo = 0;
		northx = 0;
		northy = 1;
	}
}
