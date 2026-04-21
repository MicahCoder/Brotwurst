package org.micahgruenwald.mandelbrotmultithread;

import java.awt.Color;
import java.util.Random;

public interface ColorMode{
    public abstract int calcColor(double lightness);

    public static final ColorMode RANDOM = new ColorMode(){
      private int randomOffset = new Random().nextInt();

      public int calcColor(double lightness){
        if (lightness == 1) {
          return 0;
        }
        Random random = new Random((long) (lightness* Calculator.getMaxIterations() + randomOffset));
        return Color.HSBtoRGB(360 * random.nextFloat(), 1f, random.nextFloat());
      }
    };

    public static final ColorMode HSV = (double lightness) -> Color.HSBtoRGB((float) lightness * 10, 1, .9f);

    public static final ColorMode HSV_WITH_BLACK = (double lightness) ->{
        if (lightness == 1) {
          return 0;
        }
        return  Color.HSBtoRGB((float) lightness * 10, 1, .9f);
    };

    public static final ColorMode BLACK_AND_WHITE = (double lightness) -> {
        int gray = (int)(lightness * 255.0);
        return (gray << 16) | (gray << 8) | gray;
    };
    public record SimpleGradient(float r, float g, float b) implements ColorMode{
        @Override
        public int calcColor(double lightness){
            return (((int) (r * lightness) << 16) | ((int) (g * lightness) << 8) | ((int) (b * lightness)));
        }
    }
    public record ComplexGradient(int[] colors, float[] positions) implements ColorMode{
        @Override
        public int calcColor(double position){
            int out = colors[0];
            for (int i = 1; i < positions.length; i++) {
                if (position > positions[i - 1] && position <= positions[i]) {
                    out = gradient(new Color(colors[i - 1]), new Color(colors[i]), positions[i], positions[i - 1], (float) position);
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
            return (int) r << 16 | (int) g << 8 | (int) b ;
        }
  }

  
}