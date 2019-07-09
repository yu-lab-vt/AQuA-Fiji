package va.vt.cbil;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Map.Entry;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import va.vt.cbil.GTW.BuildGTWGraphResult;
import va.vt.cbil.GTW.Mov2spResult;
import va.vt.cbil.GTW.Sp2GraphResult;
import va.vt.cbil.GTW2.TemplateResult;
import va.vt.cbil.ProgressBarRealizedStep3.EvtGrowResult;
import va.vt.cbil.ProgressBarRealizedStep3.RiseMapResult;
import va.vt.cbil.ProgressBarRealizedStep3.RiseNode;
import va.vt.cbil.ProgressBarRealizedStep3.RiseXX;

import java.util.Random;


public class Test{
	public static void main(String[]args) throws FileNotFoundException{
		double[] zscore = new double[] {1,3,5,4,2,6};
		ValueAndIdx[] zScoreSortedIdx = new ValueAndIdx[zscore.length];
		for (int i=0; i<zscore.length;i++) {
			ValueAndIdx tmp = new ValueAndIdx(i,zscore[i]);
			zScoreSortedIdx[i] = tmp;
		}
		
		Arrays.sort(zScoreSortedIdx, new Comparator<ValueAndIdx>() {
			public int compare(ValueAndIdx o1, ValueAndIdx o2) {
				if(o1.value>o2.value)
					return -1;
				else if(o1.value<o2.value)
					return 1;
				else if(o1.value == o2.value)
					return 0;
				return 0;	
			}
		});
		
		for(ValueAndIdx z:zScoreSortedIdx) {
			System.out.println("" + z.value + " ");
		}
	}
	
	
	static class ValueAndIdx{
		int idx = 0;
		double value = 0;
		public ValueAndIdx(int idx, double value) {
			this.idx = idx;
			this.value = value;
		}
	}
	
	
}