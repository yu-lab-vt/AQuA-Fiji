package va.vt.cbil;

public class Helper {
  public static void viewMatrix(int W, int H, int P, String name, float[][][] m) {
    
    System.out.println("check " + name);
    int testP, testW, testH;
    if (P > 30) {
      testP = testW = testH = 3;
    }
    else {
      testP = P;
      testW = W;
      testH = H;
    }  
    for(int k=0;k<testP;k++) {
        for(int j=0;j<testH;j++){
            for(int i=0;i<testW;i++){
                System.out.printf("%f ", m[i][j][k]);
            }
            System.out.printf("\n");
        }
        System.out.printf("\n\n");
    }       
 }
  public static void viewMatrix(int W, int H, String name, float[][] m) {
	    
	    System.out.println("check " + name);
	    for(int j=0;j<H;j++){
            for(int i=0;i<W;i++){
                System.out.printf("%f ", m[i][j]);
            }
            System.out.printf("\n");
        }    
        System.out.printf("\n\n");
	 }
}
