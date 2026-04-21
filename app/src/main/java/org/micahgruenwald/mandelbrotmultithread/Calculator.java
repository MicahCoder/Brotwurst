package org.micahgruenwald.mandelbrotmultithread;

import java.awt.Color;


public class Calculator {
  private static int maxIterations = 100;
  private static ColorMode COLOR_CALC= new ColorMode.ComplexGradient(new int[]{new Color(1.0f, 0.0f, 0.0f).getRGB(),new Color(0.0f, 1.0f,0.0f).getRGB()}, new float[]{0.0f, 1.0f});

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
