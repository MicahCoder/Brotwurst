package org.micahgruenwald.mandelbrotmultithread;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Manager extends Thread{
    private final BufferedImage image;
    private final int threadCount;
    private final ArrayList<MandelbrotThread> threads;
    private final RenderArea area;
    public Manager(int threadCount, RenderArea area, BufferedImage image){
        this.threadCount = threadCount;
        this.image = image;
        this.area = area;
        threads = new ArrayList<>(threadCount);
    }

    private static ArrayList<Integer> rowLengths(int dividend, int divisor){
        ArrayList<Integer> rowLengths = new ArrayList<>(divisor);
        int base = dividend / divisor;
        int remainder = dividend % divisor;
        for(int i = 0; i<divisor; i++){
            if(i<remainder){
                rowLengths.add(base + 1);
            }else{
                rowLengths.add(base);
            }
        }
        return rowLengths;
    }

    @Override
    public void run(){
        ArrayList<Integer> rowLengths = rowLengths(image.getHeight(), threadCount);
        double x0 = area.x0();
        double y0 = area.y0();
        double dx = area.xWidth() / image.getWidth();
        double dy = area.xWidth() / image.getHeight();
        for(int i = 0; i<threadCount; i++){
            // threads.add(new )
        }

    }
}
