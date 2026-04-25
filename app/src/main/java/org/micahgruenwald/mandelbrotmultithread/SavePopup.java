package org.micahgruenwald.mandelbrotmultithread;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.qt.gui.QIntValidator;
import io.qt.widgets.QDialog;
import io.qt.widgets.QFileDialog;
import io.qt.widgets.QFileIconProvider;
import io.qt.widgets.QLabel;
import io.qt.widgets.QLineEdit;
import io.qt.widgets.QPushButton;
import io.qt.widgets.QVBoxLayout;
import io.qt.widgets.QWidget;

public class SavePopup extends QDialog{
    private String filepath;
    public SavePopup(QWidget parent, Manager manager) {
        super(parent);
        filepath = "";
        setWindowTitle("Save");
        
        QVBoxLayout layout = new QVBoxLayout(this);
        
        QPushButton fileButton = new QPushButton("File Location");

        QLineEdit fileName = new QLineEdit("render");
        QPushButton closeButton = new QPushButton("Save");
        QLineEdit resolution = new QLineEdit("1080");
        resolution.setValidator(new QIntValidator(10, 10000, this));

        QFileIconProvider iconProvider = new QFileIconProvider();
        fileButton.setIcon(iconProvider.icon(QFileIconProvider.IconType.File));
        fileButton.clicked.connect(()->{
            setFilepath(QFileDialog.getExistingDirectory(this, tr("Select Directory"),"")); });
        closeButton.clicked.connect(()->{
            int res = Integer.parseInt(resolution.text());
            BufferedImage image =  new BufferedImage(res,res, BufferedImage.TYPE_INT_RGB);
            Manager highResManager = new Manager(7, manager.getRenderArea(), image);
            highResManager.render();
            try {
                File outputfile = new File(filepath + fileName.text() + ".png"
              );
            if(filepath.length() != 0 && filepath != null && fileName.text() != null){
                ImageIO.write(highResManager.getImage(), "png", outputfile);
            }
            } catch (IOException e) {
            }
            close();
        });
        layout.addWidget(fileButton);
        layout.addWidget(new QLabel("File Name"));
        layout.addWidget(fileName);
        layout.addWidget(new QLabel("Resolution(px)"));
        layout.addWidget(resolution);
        layout.addWidget(closeButton);

    }

    private void setFilepath(String path){
        this.filepath = path;
    }
}
