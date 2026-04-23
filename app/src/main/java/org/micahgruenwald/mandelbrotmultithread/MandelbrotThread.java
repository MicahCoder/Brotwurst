package org.micahgruenwald.mandelbrotmultithread;

import java.awt.image.BufferedImage;

public class MandelbrotThread extends Thread {
  private final double x0;
  private final double y0;
  private final double x1;
  private final double y1;

  private final BufferedImage image;
  private final int i0;
  private final int j0;
    private final int i1;
  private final int j1;

  public MandelbrotThread(
      double x0,
      double y0,
      double x1,
      double y1,
      int i0,
      int j0,
      int i1,
      int j1,
      BufferedImage image) {
    this.x0 = x0;
    this.y0 = y0;
    this.x1 = x1;
    this.y1 = y1;
    this.i0 = i0;
    this.j0 = j0;
    this.i1 = i1;
    this.j1 = j1;
    this.image = image;
  }

  @Override
  public void run() {
    double dx = (x1 - x0)/(j1 - j0);
    double dy = (y1 - y0)/(i1 - i0);
    // System.out.println("Dx: " + dx);
    // System.out.println("Dy: " + dy);
    double x = x0;
    double y = y0;
    // System.out.println("x: " + x);
    // System.out.println("y: " + y);
    for (int i = i0; i < i1; i++) {
      x=x0;
      for (int j = j0; j < j1; j++) {
        // image.setRGB(j,i, Calculator.getColorCalc().calcColor(Calculator.mandelbrotValue(x, y)));
        image.setRGB(j,i, Calculator.getColorCalc().calcColor(Calculator.render(x, y)));
        x+= dx;
      }
      y+=dy;

    }
    // System.out.println("x: " + x);
    // System.out.println("y: " + y);
  }

  public BufferedImage getImage() {
    return image;
  }
}
