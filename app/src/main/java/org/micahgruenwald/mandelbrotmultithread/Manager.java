package org.micahgruenwald.mandelbrotmultithread;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import io.qt.core.QSize;
import io.qt.gui.QPixmap;

public class Manager{
    private BufferedImage image;
    private final int threadCount;
    private final ArrayList<MandelbrotThread> threads;
    private RenderArea area;
    public Manager(int threadCount, RenderArea area, BufferedImage image){
        this.threadCount = threadCount;
        this.image = image;
        this.area = area;
        threads = new ArrayList<>(threadCount);
    }
    public BufferedImage getImage(){
        return image;
    }

    public void setImage(BufferedImage image){
        this.image = image;
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
    public void setRenderArea(RenderArea renderArea){
        this.area = renderArea;
    }

    public RenderArea getRenderArea(){
        return area;
    }
    public void render(){
        ArrayList<Integer> rowLengths = rowLengths(image.getHeight(), threadCount);
        threads.clear();
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
