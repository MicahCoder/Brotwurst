package org.micahgruenwald.mandelbrotmultithread;

import java.awt.BufferedImage;

public class MandelbrotThread extends Thread {
  private final double x0;
  private final double y0;
  private final double x1;
  private final double y1;
  private final double dx;
  private final double dy;
  private final BufferImage image;

  public MandelbrotThread(double x0, double y0, double x1, double y1, double dx, double dy) {
    this.x0 = x0;
    this.y0 = y0;
    this.x1 = x1;
    this.y1 = y1;
    this.dx = dx;
    this.dy = dy;
    image = new BufferedImage();
  }

  @Override
  public void run() {
    int i = 0;
    int j = 0;
    for (double x = x0; x < x1; x += dx) {
      i++;
      for (double y = x0; y < x1; y += dy) {
        j++;
      }
    }
  }
}
