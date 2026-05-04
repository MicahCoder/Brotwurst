package org.micahgruenwald.mandelbrotmultithread;

/**
 * @param xCenter the center of the region, x coordinate
 * @param yCenter the center of the region, y coordinate
 * @param xWidth the width of the region
 * @param yWidth the height of the region.
 *@author mgruenwald
 */
public record RenderArea(double xCenter, double yCenter, double xWidth, double yWidth) {
  /**
   * @return the minimum x coordinate
   * @author mgruenwald
   */
  public double x0() {
    return xCenter - xWidth / 2;
  }

  /**
   * @return the minimum y coordinate
   * @author mgruenwald
   */
  public double y0() {
    return yCenter - yWidth / 2;
  }

  /**
   * @return the maximum x coordinate
   * @author mgruenwald
   */
  public double x1() {
    return xCenter + xWidth / 2;
  }

  /**
   * @return maximum y coordinate
   * @author mgruenwald
   */
  public double y1() {
    return yCenter + yWidth / 2;
  }
}
