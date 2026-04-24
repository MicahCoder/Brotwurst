package org.micahgruenwald.mandelbrotmultithread;

import io.qt.core.Qt;
import io.qt.gui.QPixmap;
import io.qt.widgets.*;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

import io.qt.gui.QPixmap;
import io.qt.widgets.QApplication;
import io.qt.widgets.QComboBox;
import io.qt.widgets.QHBoxLayout;
import io.qt.widgets.QLabel;
import io.qt.widgets.QPushButton;
import io.qt.widgets.QSizePolicy;
import io.qt.widgets.QVBoxLayout;
import io.qt.widgets.QWidget;

public class App {
  public static void main(String[] args) {
    QApplication.initialize(args);

    QWidget window = new QWidget();
    window.setWindowTitle("Test Window");
    window.resize(900, 600);

    QHBoxLayout mainLayout = new QHBoxLayout(window);
    String imagePath = findImagePath();
    ZoomableCropImageView imageView = new ZoomableCropImageView();
    QPixmap pixmap = new QPixmap(imagePath);
    QLabel imageLabel = new QLabel();
    BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
    Calculator.setColorMode(ColorMode.ORANGE_BLACK_BLUE);
    Calculator.setJuliaValues( -0.4,  0.6, 2);
    Calculator.setMaxIterations(200);
    Calculator.setJuliaMode(true);
    Manager manager = new Manager(8, new RenderArea(0,0, 3.5,3.5), image);

    manager.render();
  
    QPixmap pixmap = manager.getQPixmap();

    if (pixmap.isNull()) {
      imageView.setErrorText("Could not load image. Tried: " + imagePath);
    } else {
      imageView.setImage(pixmap);
    }
    imageView.setSizePolicy(QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Expanding);
    imageView.setMinimumSize(1, 1);

    SidebarPanel sidebar = new SidebarPanel(imageView);
    imageLabel.setSizePolicy(QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Expanding);
    
    QVBoxLayout sidebarLayout = new QVBoxLayout();

    QComboBox colorChoices = new QComboBox();
    colorChoices.addItem("Orange Black and Blue");
    colorChoices.addItem("Random Colors");
    colorChoices.addItem("Rainbow");
    colorChoices.addItem("Black and White");

    colorChoices.currentIndexChanged.connect((i)->{
      ColorMode mode = switch(i){
        case 0 -> ColorMode.ORANGE_BLACK_BLUE;
        case 1 -> ColorMode.RANDOM;
        case 2 -> ColorMode.HSV_WITH_BLACK;
        case 3 -> ColorMode.BLACK_AND_WHITE;
        default -> ColorMode.BLACK_AND_WHITE;
      };
      Calculator.setColorMode(mode);
      manager.render();
      QPixmap map = manager.getQPixmap();
        imageLabel.setPixmap(map);
        imageLabel.setScaledContents(true);
    });

    sidebarLayout.addWidget(new QPushButton("Button 1"));
    sidebarLayout.addWidget(new QPushButton("Button 2"));

    sidebarLayout.addWidget(colorChoices);
    sidebarLayout.addStretch(1);

    QSplitter splitter = new QSplitter();
    splitter.setOrientation(Qt.Orientation.Horizontal);
    splitter.addWidget(sidebar);
    splitter.addWidget(imageView);
    splitter.setOpaqueResize(true);
    splitter.setChildrenCollapsible(false);
    splitter.setHandleWidth(8);
    splitter.setStretchFactor(0, 0);
    splitter.setStretchFactor(1, 1);
    splitter.setCollapsible(0, false);
    splitter.setCollapsible(1, false);
    splitter.setSizes(java.util.List.of(180, 320));

    mainLayout.addWidget(splitter);

    window.show();

    QApplication.exec();
    QApplication.shutdown();
  }

  private static String findImagePath() {
    Path cwd = Path.of("").toAbsolutePath().normalize();

    Path[] candidates =
        new Path[] {
          cwd.resolve(
              "app/src/test/java/org/micahgruenwald/mandelbrotmultithread/testOutput/saved.png"),
          cwd.resolve(
              "src/test/java/org/micahgruenwald/mandelbrotmultithread/testOutput/saved.png"),
          cwd.resolve(
              "../app/src/test/java/org/micahgruenwald/mandelbrotmultithread/testOutput/saved.png")
        };

    for (Path candidate : candidates) {
      Path normalized = candidate.normalize();
      if (Files.exists(normalized)) {
        return normalized.toString();
      }
    }

    return candidates[0].normalize().toString();
  }
}
