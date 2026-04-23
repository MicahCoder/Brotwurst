package org.micahgruenwald.mandelbrotmultithread;


public class Calculator {
  private static int maxIterations = 300;
  private static ColorMode COLOR_CALC= ColorMode.HSV_WITH_BLACK;
  private static double cx = -.8;
  private static double cy = .156;
  private static int n = 2;
  private static int  R = escapeRadius(cx, cy, n);
//new ColorMode.ComplexGradient(new int[]{new Color(1.0f, 0.0f, 0.0f).getRGB(),new Color(0.0f, 1.0f,0.0f).getRGB()}, new float[]{0.0f, 1.0f});
  public static double mandelbrotValue(double x, double y) {
    double x2 = 0.0;
    double y2 = 0.0;
    double w = 0.0;
    double x0 = x;
    double y0 = y;
    int iteration = 0;
    while (x2 + y2 <= 4 && iteration < maxIterations) {
      x = x2 - y2 + x0;
      y = w - x2 - y2 + y0;
      x2 = x * x;
      y2 = y * y;
      w = (x + y) * (x + y);
      iteration++;
    }
    return (double) iteration / maxIterations;
  }

  public static double juliaValue(double x, double y) {
    int iteration = 0;
    // System.out.print("RInit: " + R);
    while (x*x + y*y < R * R && iteration < maxIterations) {
      double xTemp = x*x - y*y;
      y = 2*x*y + cy;
      x = xTemp + cx;
      iteration++;
    }
    return (double) iteration / maxIterations;
  }
  // n is the order of the julia set. n = 2 is the normal julia set. 
  public static int escapeRadius(double cx,double cy, int n){
    int r = 1;
    double v =Math.sqrt(cx*cx + cy*cy);
    System.out.println(v);
    while(Math.pow(r,n) - r < v){
      System.out.println(Math.pow(r,n));
      r++;
    }
    System.out.println("RFinal: " + r);
    return r;
  }


  public static void setMaxIterations(int maxIts) {
    maxIterations = maxIts;
  }

  public static int getMaxIterations(){
    return maxIterations;
  }

  public static void setColorMode(ColorMode colorMode){
    COLOR_CALC = colorMode;
  }

  public static ColorMode getColorCalc(){
    return COLOR_CALC;
  }

  
}
