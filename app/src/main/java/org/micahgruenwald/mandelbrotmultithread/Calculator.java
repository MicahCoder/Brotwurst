package org.micahgruenwald.mandelbrotmultithread;

public class Calculator {
  /**
   * @author mgruenwald
   */
  // The amount of iterations the calculator will run until it breaks for rednering.
  private static int maxIterations = 600;
  // The way the calculator transaltes valeus [0.0,1.0] into a color for rendering.
  private static ColorMode COLOR_CALC = ColorMode.HSV_WITH_BLACK;
  // The Julai coordiantes (where z = cx + cy * i).
  private static double cx = -.8;
  private static double cy = .156;
  // The order of the julia set; anything above 2 is unstable.
  private static double n = 2;
  // This is used for Julia sets to find the values for which mandelbrot is automatically divergent.
  private static int R = escapeRadius(cx, cy, n);
  // This boolean stores if we are rendering the Julia set.
  private static boolean juliaMode = true;
  // These RenderAreas decide the default renderArea for Julia and Mandelbrot.
  public static final RenderArea DEFAULT_JULIA_AREA = new RenderArea(0.0, 0.0, 3.5, 3.5);
  public static final RenderArea DEFAULT_MANDELBROT_AREA = new RenderArea(-0.75, 0.0, 2.5, 2.5);

  // new ColorMode.ComplexGradient(new int[]{new Color(1.0f, 0.0f, 0.0f).getRGB(),new Color(0.0f,
  // 1.0f,0.0f).getRGB()}, new float[]{0.0f, 1.0f});
    /**
   * @author mgruenwald
   * @param x x cord
   * @param y y cord
   */
  public static double render(double x, double y) {
    if (!juliaMode) {
      // Render a mandelbrot set
      return mandelbrotValue(x, y);
    }
    if (n == 2) {
      // Render a Julai set with power 2 (optimized)
      return julia2Value(x, y);
    }
    // Render a julia set with power n (slow)
    return juliaValue(x, y);
  }

  // Finds the value of the mandelbrot set at (x,y)
      /**
   * @author mgruenwald, psuedocode found at @link{https://en.wikipedia.org/wiki/Mandelbrot_set#Computer_drawings}
   * @param x x cord
   * @param y y cord
   */
  private static double mandelbrotValue(double x, double y) {
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

  // Finds the value of the julia set at (x,y)
      /**
   * @author mgruenwald, psuedocode found @link{https://en.wikipedia.org/wiki/Julia_set#Pseudocode_for_multi-Julia_sets}
   * @param x x cord
   * @param y y cord
   */
  private static double juliaValue(double x, double y) {
    int iteration = 0;
    // System.out.print("RInit: " + R);
    while (x * x + y * y < R * R && iteration < maxIterations) {
      double w = n * Math.atan2(y, x);
      double f = Math.pow(x * x + y * y, n / 2);
      double xTemp = f * Math.cos(w) + cx;
      y = f * Math.sin(w) + cy;
      x = xTemp;
      iteration++;
    }
    return (double) iteration / maxIterations;
  }

  // n is the order of the julia set. n = 2 is the normal julia set.
  // Finds the escape radius.
      /**
   * @author mgruenwald
   * @param cx x complex cord
   * @param cy y complex cord
   * @param n order
   */
  private static int escapeRadius(double cx, double cy, double n) {
    int r = 1;
    double v = Math.sqrt(cx * cx + cy * cy);
    while (Math.pow(r, n) - r < v) {
      r++;
    }
    return r;
  }

  // Finds the value of the julia set at (x,y) (order 2)
  /**
   * @author mgruenwald, psuedocode found @link{https://en.wikipedia.org/wiki/Julia_set#Pseudocode_for_normal_Julia_sets}
   * @param x
   * @param y
   */
  public static double julia2Value(double x, double y) {
    int iteration = 0;
    // System.out.print("RInit: " + R);
    while (x * x + y * y < R * R && iteration < maxIterations) {
      double xTemp = x * x - y * y;
      y = 2 * x * y + cy;
      x = xTemp + cx;
      iteration++;
    }
    return (double) iteration / maxIterations;
  }

  // Sets the max iterations
  /**
   * @author mgruenwald
   */
  public static void setMaxIterations(int maxIts) {
    maxIterations = maxIts;
  }
  /**
   * @author mgruenwald
   */
  // Get the max iterations
  public static int getMaxIterations() {
    return maxIterations;
  }
  /**
   * @author mgruenwald
   */
  // set the color mode
  public static void setColorMode(ColorMode colorMode) {
    COLOR_CALC = colorMode;
  }
  /**
   * @author mgruenwald
   */
  // Get the colorMode
  public static ColorMode getColorCalc() {
    return COLOR_CALC;
  }
  /**
   * @author mgruenwald
   */
  // Set Julia Settinhs
  public static void setJuliaValues(double cx, double cy, double n) {
    Calculator.cx = cx;
    Calculator.cy = cy;
    Calculator.n = n;
    Calculator.R = escapeRadius(cx, cy, n);
  }
  /**
   * @author mgruenwald
   */
  // Set the julia mdoe
  public static void setJuliaMode(boolean state) {
    Calculator.juliaMode = state;
  }
  /**
   * @author mgruenwald
   */
  // get the julia mode.
  public static boolean getJuliaMode() {
    return Calculator.juliaMode;
  }
}
