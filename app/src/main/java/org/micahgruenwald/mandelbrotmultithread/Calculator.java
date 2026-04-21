package org.micahgruenwald.mandelbrotmultithread;

import java.awt.Color;
import java.util.Random;

import org.micahgruenwald.mandelbrotmultithread.Calculator.ColorMode;

public class Calculator {
  private static int maxIterations = 100;
  public static final ColorMode COLOR_CALC= ColorMode.HSV_WITH_BLACK;

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

  private Color HSV(float lightness) {
    return new Color(Color.HSBtoRGB(lightness * 10, 1, .9f));
  }

  private Color HSVWithBlack(float lightness) {
    if (lightness == 1) {
      return new Color(0, 0, 0);
    }
    return new Color(Color.HSBtoRGB(lightness * 10, 1, .9f));
  }

  private Color simpleGradient(float r, float g, float b, float lightness) {
    return new Color(r * lightness / 255, g * lightness / 255, b * lightness / 255);
  }

  private Color complexGradient(Color[] colors, float[] positions, float position) {
    Color out = colors[0];
    for (int i = 1; i < positions.length; i++) {
      if (position > positions[i - 1] && position <= positions[i]) {
        out = gradient(colors[i - 1], colors[i], positions[i], positions[i - 1], position);
      }
    }
    return out;
  }

  private Color gradient(Color start, Color end, float startX, float endX, float position) {
    float length = endX - startX;
    float startWeight = (position - startX) / length;
    float endWeight = 1f - startWeight;
    float r = (start.getRed() * startWeight + end.getRed() * endWeight) / 255f;
    float g = (start.getGreen() * startWeight + end.getGreen() * endWeight) / 255f;
    float b = (start.getBlue() * startWeight + end.getBlue() * endWeight) / 255f;
    float a = (start.getAlpha() * startWeight + end.getAlpha() * endWeight) / 255f;
    return new Color(r, g, b, a);
  }

  private Color randomColor(float seed) {
    Random random = new Random((long) seed);
    if (seed == maxIterations) {
      return new Color(0, 0, 0);
    }
    return new Color(Color.HSBtoRGB(360 * random.nextFloat(), 1f, random.nextFloat()));
  }

  public interface ColorMode{
    public abstract int calcColor(double lightness);

    public static final ColorMode RANDOM = new ColorMode() {
      @Override
      public int calcColor(double lightness){
        Random random = new Random((long) lightness);
        if (lightness == maxIterations) {
          return 0;
        }
        return (new Color(Color.HSBtoRGB(360 * random.nextFloat(), 1f, random.nextFloat()))).getRGB();
      }
    };

    public static final ColorMode HSV = new ColorMode() {
      @Override
      public int calcColor(double lightness){
        return  Color.HSBtoRGB((float) lightness * 10, 1, .9f);
      }
    };

    public static final ColorMode HSV_WITH_BLACK = new ColorMode() {
      @Override
      public int calcColor(double lightness){
        if (lightness == 1) {
          return 0;
        }
        return  Color.HSBtoRGB((float) lightness * 10, 1, .9f);
      }
    };
  }
}
