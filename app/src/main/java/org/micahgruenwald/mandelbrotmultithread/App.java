package org.micahgruenwald.mandelbrotmultithread;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import io.qt.core.QTimer;
import io.qt.core.Qt;
import io.qt.widgets.QApplication;
import io.qt.widgets.QLabel;
import io.qt.widgets.QSizePolicy;
import io.qt.widgets.QSplitter;
import io.qt.widgets.QVBoxLayout;
import io.qt.widgets.QWidget;

public class App {
  /**
   * @author bmalia
   * @param window
   * @param floatingBar
   */
  private static void positionFloatingBar(QWidget window, QLabel floatingBar) {
    floatingBar.adjustSize();
    int margin = 12;
    floatingBar.move(
        window.width() - floatingBar.width() - margin,
        window.height() - floatingBar.height() - margin);
  }

  // This image is written to at low res. Change resolution to make a higher res while moving.
  /**@author mgruenwald */
  public static final BufferedImage movingImage =
      new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
  // This image is rendered for high res, while we're still.
  /**
   * @author mgruenwald 
   */
  public static final BufferedImage stationaryImage =
      new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

  // Main class/entrypoint of the project. This is run with ./gradlew run
  /**
   * @author banks (primary)
   * @author mgruenwald (secondary)
   */
  public static void main(String[] args) {
    // Initialize the application
    QApplication.initialize(args);
    try (InputStream in = App.class.getResourceAsStream("/styles/app.qss")) {
      String style = null;
      if (in != null) {
        style = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      } else {
        // Load QSS stylesheet
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

    final QLabel[] floatingRef = new QLabel[1];

    // Create main window
    QWidget window =
        new QWidget() {
          @Override
          protected void resizeEvent(io.qt.gui.QResizeEvent event) {
            super.resizeEvent(event);
            QLabel f = floatingRef[0];
            if (f != null) {
              positionFloatingBar(this, f);
            }
          }
        };
    window.setWindowTitle("Mandelbrot Renderer");
    window.resize(900, 600);

    // Define the main layout of the Application
    QVBoxLayout mainLayout = new QVBoxLayout(window);

    // Set Default calculator values
    Calculator.setColorMode(ColorMode.ORANGE_BLACK_BLUE);
    Calculator.setJuliaValues(-0.4, 0.6, 2);
    Calculator.setMaxIterations(200);
    Calculator.setJuliaMode(false);

    // Create manager, this does all the calculations for the fractal
    Manager manager = new Manager(6, Calculator.DEFAULT_MANDELBROT_AREA, stationaryImage);
    // Call the manager to render
    manager.render();

    QLabel titleLabel = new QLabel("Brotwurst");
    titleLabel.setObjectName("titleLabel");
    titleLabel.setSizePolicy(QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Fixed);
    mainLayout.addWidget(titleLabel);

    // Create an ImageView. This object allows us to zoom and all.
    ZoomableCropImageView imageView = new ZoomableCropImageView(manager);
    // Sends info from the manager into the image view.
    imageView.setImage(manager.getQPixmap());
    // Set rules for the imageView
    imageView.setSizePolicy(QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Expanding);
    imageView.setMinimumSize(1, 1);
    // Creatwe the panel with all the UI pretty much.
    SidebarPanel sidebar = new SidebarPanel(imageView, manager);

    // Add settings for sidebar.
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

    // Add widgets to the window.
    mainLayout.addWidget(splitter);

    // Small status bar in the bottom right
    QLabel floatingBar =
        new QLabel(
            "X =" + manager.getRenderArea().xCenter() + ", Y =" + manager.getRenderArea().yCenter() + ", Width = " + manager.getRenderArea().xWidth(),
            window);
    floatingBar.setObjectName("floatingBar");
    floatingBar.setStyleSheet(
        "background-color: rgba(0,0,0,160); color: white; padding:6px; border-radius:15px;");
    floatingBar.setAttribute(
        io.qt.core.Qt.WidgetAttribute.WA_TransparentForMouseEvents, true); // Passes clicks through
    floatingRef[0] = floatingBar;

    QTimer refreshTimer = new QTimer();
    refreshTimer.setInterval(100);
    refreshTimer.timeout.connect(
        () -> {
          floatingBar.setText(
              "X =" + (float)manager.getRenderArea().xCenter() + ", Y =" + (float)manager.getRenderArea().yCenter() + ", Width = " + (float)manager.getRenderArea().xWidth());
          positionFloatingBar(window, floatingBar);
        });
    refreshTimer.start();

    positionFloatingBar(window, floatingBar);
    floatingBar.raise();

    // Show the window
    window.show();

    // Run everything
    QApplication.exec();
    QApplication.shutdown();
  }
}
