package org.micahgruenwald.mandelbrotmultithread;

import java.awt.Color;
import java.util.Random;

public interface ColorMode {

    public abstract int calcColor(double lightness);
    public static final ColorMode GREEN_BLUE_BLACK = new ComplexGradient(new int[]{new Color(0, 0,0).getRGB(),new Color(200, 255,200).getRGB(),new Color(0, 0,255).getRGB(),new Color(200, 255,200).getRGB(),new Color(0, 0,0).getRGB()}, new float[]{0.0f,0.15f,0.5f,0.85f, 1.0f});
    public static final ColorMode ORANGE_BLACK_BLUE = new ComplexGradient(new int[]{new Color(0, 7, 100).getRGB(), new Color( 32, 107, 203).getRGB(), new Color(237, 255, 255).getRGB(), new Color(255, 170,   0).getRGB(), new Color(  0,   2,   0).getRGB(), new Color(  0,   2,   0).getRGB()}, new float[]{0.0f,0.16f,0.42f,0.6425f,0.8575f,1.0f});
    /*
    The following few gradients (going up to Random), were heavily inspired by:
    @link{https://matplotlib.org/stable/_images/sphx_glr_colormap_reference_001_2_00x.png}
    */
    public static final ColorMode VIRIDIS = new ComplexGradient(new int[]{0x3F0B65,0x404C85,0x5AB080, 0x9AD35D,0xF9E855,0}, new float[]{0.0f,0.25f, 0.5f, 0.75f, .95f, 1.0f});
    public static final ColorMode INFERNO = new ComplexGradient(new int[]{0,0x7E1A9F,0xAD4154,0xED9D39,0xEAEABA,0}, new float[]{0.0f,0.25f, 0.5f, 0.75f, .95f, 1.0f});
        public static final ColorMode PLASMA = new ComplexGradient(new int[]{0x0E0782,0x7C199F, 0xBE5275,0xEA9853,0xF0F757,0}, new float[]{0.0f,0.25f, 0.5f, 0.75f, .95f, 1.0f});
    public static final ColorMode RANDOM = new ColorMode() {
        private int randomOffset = new Random().nextInt();

        public int calcColor(double lightness) {
            if (lightness == 1) {
                return 0;
            }
            Random random = new Random((long) (lightness * Calculator.getMaxIterations() + randomOffset));
            return Color.HSBtoRGB(360 * random.nextFloat(), 1f, random.nextFloat());
        }
    };

    public static final ColorMode HSV = (double lightness) -> Color.HSBtoRGB((float) lightness * 10, 1, .9f);

    public static final ColorMode HSV_WITH_BLACK = (double lightness) -> {
        if (lightness == 1) {
            return 0;
        }
        return Color.HSBtoRGB((float) lightness * 10, 1, .9f);
    };

    public static final ColorMode BLACK_AND_WHITE = (double lightness) -> {
        int gray = (int) (lightness * 255.0);
        return (gray << 16) | (gray << 8) | gray;
    };

    public record SimpleGradient(double r1, double g1, double b1, double r2, double g2, double b2) implements ColorMode {

        @Override
        public int calcColor(double lightness) {
            double p2 = 1.0 - lightness;
            double r = r1 * lightness + r2*p2;
            double g = g1 * lightness+ g2*p2;
            double b = b1 * lightness + b2*p2;
            return (((int) r << 16) | ((int) g << 8) | ((int) b));
        }
    }

    public record ComplexGradient(int[] colors, float[] positions) implements ColorMode {

        @Override
        public int calcColor(double position) {
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
            return (int) r << 16 | (int) g << 8 | (int) b;
        }

        
    }

}
