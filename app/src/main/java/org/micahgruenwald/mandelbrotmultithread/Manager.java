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
        double dx = area.xWidth() / (image.getWidth());
        double dy = area.yWidth() / (image.getHeight());
        double x1 = area.x1();
        double y1 = area.y1();

        int row = 0;
        for(int i = 0; i<threadCount; i++){
            int length = rowLengths.get(i);
            y1 = y0 + (length)*dy;
            // System.out.println("Length: "+ length);
            // System.out.println("dx: "+ dx);
            // System.out.println("dy: "+ dy);
            // System.out.println("x0 "+ x0);
            // System.out.println("x1 "+ x1);
            // System.out.println("y0 "+ y0);
            // System.out.println("y1 "+ y1);
            threads.add(new MandelbrotThread(x0, y0, x1, y1, row,0, row+length, image.getWidth(),image));
            row += length;
            y0 = y1;
        }
        for(Thread thread:threads){
            thread.start();
        }
        try{
        for(Thread thread:threads){
            thread.join();
        }
        }catch(InterruptedException e){};

    }
}
