package org.micahgruenwald.mandelbrotmultithread;

import java.awt.image.BufferedImage;

public class MandelbrotThread extends Thread {
  private final double x0;
  private final double y0;
  private final double x1;
  private final double y1;
  private final double dx;
  private final double dy;
  private final BufferedImage image;
  private final int i0;
  private final int j0;

  public MandelbrotThread(
      double x0,
      double y0,
      double x1,
      double y1,
      double dx,
      double dy,
      int i0,
      int j0,
      BufferedImage image) {
    this.x0 = x0;
    this.y0 = y0;
    this.x1 = x1;
    this.y1 = y1;
    this.dx = dx;
    this.dy = dy;
    this.i0 = i0;
    this.j0 = j0;
    this.image = image;
  }

  public MandelbrotThread(double x0, double y0, double x1, double y1, double dx, double dy) {
    this(
        x0,
        y0,
        x1,
        y1,
        dx,
        dy,
        0,
        0,
        new BufferedImage(
            (int) ((x1 - x0) / dx), (int) ((y1 - y0) / dy), BufferedImage.TYPE_INT_RGB));
  }

  @Override
  public void run() {
    int i = i0;
    int j;
    for (double x = x0; x < x1; x += dx) {

      j = j0;
      for (double y = y0; y < y1; y += dy) {
        int gray = (int) (255 * Calculator.mandelbrotValue(x, y));
        image.setRGB(i, j, ((gray << 16) | (gray << 8) | gray));
        j++;
      }
      i++;
    }
  }

  public BufferedImage getImage() {
    return image;
  }
}
