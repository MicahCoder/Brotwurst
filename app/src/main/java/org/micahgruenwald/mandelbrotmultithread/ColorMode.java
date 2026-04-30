package org.micahgruenwald.mandelbrotmultithread;

import java.awt.Color;
import java.util.Random;

// This interface defines a calculation for finding an int (RGB) color.
public interface ColorMode {
  // Define the methods of the class
  public abstract int calcColor(double lightness);

  // A few basic gradients
  public static final ColorMode GREEN_BLUE_BLACK =
      new ComplexGradient(
          new int[] {
            new Color(0, 0, 0).getRGB(),
            new Color(200, 255, 200).getRGB(),
            new Color(0, 0, 255).getRGB(),
            new Color(200, 255, 200).getRGB(),
            new Color(0, 0, 0).getRGB()
          },
          new float[] {0.0f, 0.15f, 0.5f, 0.85f, 1.0f});
  public static final ColorMode ORANGE_BLACK_BLUE =
      new ComplexGradient(
          new int[] {
            new Color(0, 7, 100).getRGB(),
            new Color(32, 107, 203).getRGB(),
            new Color(237, 255, 255).getRGB(),
            new Color(255, 170, 0).getRGB(),
            new Color(0, 2, 0).getRGB(),
            new Color(0, 2, 0).getRGB()
          },
          new float[] {0.0f, 0.16f, 0.42f, 0.6425f, 0.8575f, 1.0f});
  /*
  The following few gradients (going up to Random), were heavily inspired by:
  @link{https://matplotlib.org/stable/_images/sphx_glr_colormap_reference_001_2_00x.png}
  */
  public static final ColorMode VIRIDIS =
      new ComplexGradient(
          new int[] {0x3F0B65, 0x404C85, 0x5AB080, 0x9AD35D, 0xF9E855, 0},
          new float[] {0.0f, 0.25f, 0.5f, 0.75f, 0.99f, 1.0f});
  public static final ColorMode INFERNO =
      new ComplexGradient(
          new int[] {0, 0x7E1A9F, 0xAD4154, 0xED9D39, 0xEAEABA, 0},
          new float[] {0.0f, 0.25f, 0.5f, 0.75f, 0.99f, 1.0f});
  public static final ColorMode PLASMA =
      new ComplexGradient(
          new int[] {0x0E0782, 0x7C199F, 0xBE5275, 0xEA9853, 0xF0F757, 0},
          new float[] {0.0f, 0.25f, 0.5f, 0.75f, 0.99f, 1.0f});
  public static final ColorMode MAGMA =
      new ComplexGradient(
          new int[] {0, 0x37126D, 0xCA526B, 0xF3B381, 0xEBECBA, 0},
          new float[] {0.0f, 0.25f, 0.5f, 0.75f, 0.99f, 1.0f});
  public static final ColorMode CIVIDIS =
      new ComplexGradient(
          new int[] {0x0A224D, 0x4A5169, 0x8D897A, 0xD1C26D, 0xEEDD58, 0},
          new float[] {0.0f, 0.25f, 0.5f, 0.75f, 0.99f, 1.0f});
  /*
  These were inspired by @link{https://bertbaron.github.io/mandelbrot/?params=eyJjZW50ZXIiOlt7ImJpZ0ludCI6Ii0yMTQyMTE0NzcwMDUwMDM2NzMiLCJzY2FsZSI6NTh9LHsiYmlnSW50IjoiNjY4MTQ1MjkwOTc0NzA5NTciLCJzY2FsZSI6NTh9XSwiem9vbSI6eyJiaWdJbnQiOiI1MTc5NzAxODUzNDk1NDUyMjUiLCJzY2FsZSI6NTh9LCJtYXhfaXRlciI6MTAwMCwic21vb3RoIjp0cnVlLCJwYWxldHRlIjp7ImlkIjoib2NlYW4iLCJkZW5zaXR5IjoxLCJyb3RhdGUiOjB9fQ%3D%3D#}
  */
  public static final ColorMode LAVA =
      new ComplexGradient(
          new int[] {0, 0xBE3E1E, 0xFEF963, 0xFFFFFF, 0xFEF963, 0xBE3E1E, 0},
          new float[] {0.0f, 0.2f, 0.4f, 0.5f, 0.7f, 0.99f, 1.0f});
  public static final ColorMode OCEAN =
      new ComplexGradient(
          new int[] {0x000034, 0x184074, 0x5498f8, 0xFFFFFF, 0x5498f8, 0x184074, 0x000000},
          new float[] {0.0f, 0.2f, 0.4f, 0.5f, 0.7f, 0.99f, 1.0f});
  public static final ColorMode POP =
      new ComplexGradient(
          new int[] {
            0xEA3524, 0xFBE94E, 0x51A02D, 0x0201ED, 0xDD30E3, 0xF5BDCB, 0xEC755A, 0x75FBF1,
            0x72F54B, 0xEA332E, 0x000000
          },
          new float[] {0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f});

  // Og
  // Defines the random clor mdoe. This uses random seeds to render different colors.
  public static final ColorMode RANDOM =
      new ColorMode() {
        private final int randomOffset = new Random().nextInt();

        public int calcColor(double lightness) {
          if (lightness == 1) {
            return 0;
          }
          Random random =
              new Random((long) (lightness * Calculator.getMaxIterations() + randomOffset));
          return Color.HSBtoRGB(360 * random.nextFloat(), 1f, random.nextFloat());
        }
      };
  // Uses HSV to render random colors
  public static final ColorMode HSV =
      (double lightness) -> Color.HSBtoRGB((float) lightness * 10, 1, .9f);
  // Uses HSV to render random colors, if the value is 1, return black.
  public static final ColorMode HSV_WITH_BLACK =
      (double lightness) -> {
        if (lightness == 1) {
          return 0;
        }
        return Color.HSBtoRGB((float) lightness * 10, 1, .9f);
      };
  // Simple gradient between black and white. Optimized with bit shifting.
  public static final ColorMode BLACK_AND_WHITE =
      (double lightness) -> {
        int gray = (int) (lightness * 255.0);
        return (gray << 16) | (gray << 8) | gray;
      };

  // Creates a gradient between two colors.
  public record SimpleGradient(double r1, double g1, double b1, double r2, double g2, double b2)
      implements ColorMode {

    @Override
    public int calcColor(double lightness) {
      double p2 = 1.0 - lightness;
      double r = r1 * lightness + r2 * p2;
      double g = g1 * lightness + g2 * p2;
      double b = b1 * lightness + b2 * p2;
      return (((int) r << 16) | ((int) g << 8) | ((int) b));
    }
  }

  // Creates a gradient between an int[] of colors and a float[] of positions
  public record ComplexGradient(int[] colors, float[] positions) implements ColorMode {

    @Override
    public int calcColor(double position) {
      int out = colors[0];
      for (int i = 1; i < positions.length; i++) {
        if (position > positions[i - 1] && position <= positions[i]) {
          out =
              gradient(
                  new Color(colors[i - 1]),
                  new Color(colors[i]),
                  positions[i],
                  positions[i - 1],
                  (float) position);
        }
      }
      return out;
    }

    private int gradient(Color start, Color end, float startX, float endX, float position) {
      float length = endX - startX;
      float startWeight = (position - startX) / length;
      float endWeight = 1f - startWeight;
      float r = (start.getRed() * startWeight + end.getRed() * endWeight);
      float g = (start.getGreen() * startWeight + end.getGreen() * endWeight);
      float b = (start.getBlue() * startWeight + end.getBlue() * endWeight);
      return (int) r << 16 | (int) g << 8 | (int) b;
    }
  }
}
