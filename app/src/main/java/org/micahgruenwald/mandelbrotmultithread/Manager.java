package org.micahgruenwald.mandelbrotmultithread;

import io.qt.core.QSize;
import io.qt.gui.QPixmap;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Manager {
  private BufferedImage image;
  private final int threadCount;
  private final ArrayList<MandelbrotThread> threads;
  private RenderArea area;

  /*
      Define a mandelbrot thread manager. (this breaks rendering an image into multiple threads)
      @param threadCount - the amount of threads used to render the set
      @param area - defines the coordinates for rendering
      @param image - defines the file the manager is writing to.
  */
  public Manager(int threadCount, RenderArea area, BufferedImage image) {
    this.threadCount = threadCount;
    this.image = image;
    this.area = area;
    threads = new ArrayList<>(threadCount);
  }

  // returns the image
  public BufferedImage getImage() {
    return image;
  }

  // Sets the buffered image
  public void setImage(BufferedImage image) {
    this.image = image;
  }

  /*
  takes an amount of rows and an amount of threads, and breaks them down into equal sized tasks.
  E.g 18 broken into 5 threads goes to lines of length [4,4,4,3,3]
  */
  private static ArrayList<Integer> rowLengths(int dividend, int divisor) {
    ArrayList<Integer> rowLengths = new ArrayList<>(divisor);
    int base = dividend / divisor;
    int remainder = dividend % divisor;
    for (int i = 0; i < divisor; i++) {
      if (i < remainder) {
        rowLengths.add(base + 1);
      } else {
        rowLengths.add(base);
      }
    }
    return rowLengths;
  }

  // Sets the area the manager is rendering
  public void setRenderArea(RenderArea renderArea) {
    this.area = renderArea;
  }

  // Gets the render area.
  public RenderArea getRenderArea() {
    return area;
  }

  // Render the image on the bufferedImagefile
  public void render() {
    // Break the image into rowLengths
    ArrayList<Integer> rowLengths = rowLengths(image.getHeight(), threadCount);
    // Makes sure there are no old threads.
    threads.clear();

    // Define constants for iteration
    double x0 = area.x0();
    double y0 = area.y0();
    double dy = area.yWidth() / (image.getHeight());
    double x1 = area.x1();
    double y1 = area.y1();

    int row = 0;
    // Iterate across threads
    for (int i = 0; i < threadCount; i++) {
      // Find the amount of rows the thread is solving
      int length = rowLengths.get(i);
      // find the ending y cordinate.
      y1 = y0 + (length) * dy;
      // System.out.println("Length: "+ length);
      // System.out.println("dx: "+ dx);
      // System.out.println("dy: "+ dy);
      // System.out.println("x0 "+ x0);
      // System.out.println("x1 "+ x1);
      // System.out.println("y0 "+ y0);
      // System.out.println("y1 "+ y1);
      threads.add(
          new MandelbrotThread(x0, y0, x1, y1, row, 0, row + length, image.getWidth(), image));
      // Setup for next thread.
      row += length;
      y0 = y1;
    }
    // Run all of the threads, in paralell.
    for (Thread thread : threads) {
      thread.start();
    }
    // Wait for all the threads to finish rendering.
    try {

      for (Thread thread : threads) {
        thread.join();
      }
    } catch (InterruptedException e) {
    }
    ;
  }

  // Converts bufferedImage to a QPixmap(compatible with QT);
  public QPixmap getQPixmap() {
    // Initialize QPixmap with the correct dimensions

    BufferedImage img = image;
    QPixmap result = new QPixmap(new QSize(img.getWidth(), img.getHeight()));
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {
      // Write the BufferedImage to a byte array (using "png" or "jpg")
      ImageIO.write(img, "png", baos);
      // Load the data directly into the Qt object
      result.loadFromData(baos.toByteArray());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }
}
