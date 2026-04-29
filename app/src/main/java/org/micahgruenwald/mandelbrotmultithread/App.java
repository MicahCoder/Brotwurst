package org.micahgruenwald.mandelbrotmultithread;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import io.qt.core.Qt;
import io.qt.widgets.QApplication;
import io.qt.widgets.QHBoxLayout;
import io.qt.widgets.QSizePolicy;
import io.qt.widgets.QSplitter;
import io.qt.widgets.QWidget;

public class App {
  //This image is written too at low res. Change resolution to make a higher res while moving. 
  public static final BufferedImage movingImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
  //This image is rendered for high res, while we're still. 
  public static final BufferedImage stationaryImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
  //Main class of the project. This is run with ./gradlew run
  public static void main(String[] args) {
    //Begin the application
    QApplication.initialize(args);
    try (InputStream in = App.class.getResourceAsStream("/styles/app.qss")) {
      String style = null;
      if (in != null) {
        style = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      } else {
        //Attatch style guide
        Path p = Path.of("app/src/main/resources/styles/app.qss");
        if (Files.exists(p)) {
          style = Files.readString(p);
        }
      }
      if (style != null && !style.isEmpty()) {
        QApplication appInstance = QApplication.instance();
        if (appInstance != null) {
          appInstance.setStyleSheet(style);
        }
      }
    } catch (Exception e) {
      System.err.println("Could not load stylesheet: " + e.getMessage());
    }
    //Create main windown.
    QWidget window = new QWidget();
    window.setWindowTitle("Mandelbrot Renderer");
    window.resize(900, 600);

    //Define the main layout of the Application
    QHBoxLayout mainLayout = new QHBoxLayout(window);

    //Set Default calculator values
    Calculator.setColorMode(ColorMode.ORANGE_BLACK_BLUE);
    Calculator.setJuliaValues(-0.4, 0.6, 2);
    Calculator.setMaxIterations(200);
    Calculator.setJuliaMode(false);

    //Create manager, this does all the calculations
    Manager manager = new Manager(6, Calculator.DEFAULT_MANDELBROT_AREA, stationaryImage);
    //Render the manager. 
    manager.render();

    //Create an ImageView. This object allows us to zoom and all. 
    ZoomableCropImageView imageView = new ZoomableCropImageView(manager);
    //Sends info from the manager into the image view.
    imageView.setImage(manager.getQPixmap());
    //Set rules for the imageView
    imageView.setSizePolicy(QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Expanding);
    imageView.setMinimumSize(1, 1);
    //Creatwe the panel with all the UI pretty much. 
    SidebarPanel sidebar = new SidebarPanel(imageView, manager);

    //Add settings for sidebar. 
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

    //Add widgets to the window.
    mainLayout.addWidget(splitter);

    //Show the window
    window.show();

    //Run this thing. 
    QApplication.exec();
    QApplication.shutdown();
  }
}
