package va.vt.cbil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

public class Test2 {
	public static void main(String[] args) {
		
		
		
//		BasicFeatureDealer.findBoundary(orgImage)
		
		
		
		
	}
	
	
	public float correlation(float[] c0, float[] c1, int t0, int t1) {
		int T = t1-t0+1;
		float m1 = 0;
		float m2 = 0;
		float s1 = 0;
		float s2 = 0;
		float cov = 0;
		for(int t=t0;t<=t1;t++) {
			m1 += c0[t]/T;
			m2 += c1[t]/T;
			s1 += c0[t]*c0[t]/T;
			s2 += c1[t]*c1[t]/T;
			cov += c0[t]*c1[t]/T;
		}
		s1 -= m1*m1;
		s2 -= m2*m2;
		cov -= m1*m2;
		float result = (float) (cov/Math.sqrt(s1*s2));
		
		return result;
	}
}
