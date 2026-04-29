package org.micahgruenwald.mandelbrotmultithread;

import io.qt.core.QTimer;
import io.qt.core.Qt;
import io.qt.widgets.QApplication;
import io.qt.widgets.QLabel;
import io.qt.widgets.QSizePolicy;
import io.qt.widgets.QSplitter;
import io.qt.widgets.QVBoxLayout;
import io.qt.widgets.QWidget;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class App {

  private static void positionFloatingBar(QWidget window, QLabel floatingBar) {
    floatingBar.adjustSize();
    int margin = 12;
    floatingBar.move(
        window.width() - floatingBar.width() - margin,
        window.height() - floatingBar.height() - margin);
  }

  public static final BufferedImage movingImage =
      new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
  public static final BufferedImage stationaryImage =
      new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

  public static void main(String[] args) {
    QApplication.initialize(args);
    try (InputStream in = App.class.getResourceAsStream("/styles/app.qss")) {
      String style = null;
      if (in != null) {
        style = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      } else {
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

    QVBoxLayout mainLayout = new QVBoxLayout(window);
    Calculator.setColorMode(ColorMode.ORANGE_BLACK_BLUE);
    Calculator.setJuliaValues(-0.4, 0.6, 2);
    Calculator.setMaxIterations(200);
    Calculator.setJuliaMode(false);
    Manager manager = new Manager(6, Calculator.DEFAULT_MANDELBROT_AREA, stationaryImage);

    manager.render();

    QLabel titleLabel = new QLabel("Brotwurst");
    titleLabel.setObjectName("titleLabel");
    titleLabel.setSizePolicy(QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Fixed);
    mainLayout.addWidget(titleLabel);

    ZoomableCropImageView imageView = new ZoomableCropImageView(manager);
    imageView.setImage(manager.getQPixmap());
    imageView.setSizePolicy(QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Expanding);
    imageView.setMinimumSize(1, 1);

    SidebarPanel sidebar = new SidebarPanel(imageView, manager);

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

    QLabel floatingBar =
        new QLabel(
            "X=" + manager.getRenderArea().xCenter() + ", Y=" + manager.getRenderArea().yCenter(),
            window);
    floatingBar.setObjectName("floatingBar");
    // simple translucent background + padding; you can also use app.qss to style by objectName
    floatingBar.setStyleSheet(
        "background-color: rgba(0,0,0,160); color: white; padding:6px; border-radius:15px;");
    // let mouse events pass through to underlying widgets (optional)
    floatingBar.setAttribute(io.qt.core.Qt.WidgetAttribute.WA_TransparentForMouseEvents, true);
    floatingRef[0] = floatingBar;

    QTimer refreshTimer = new QTimer();
    refreshTimer.setInterval(100);
    refreshTimer.timeout.connect(
        () -> {
          floatingBar.setText("X=" + (float) manager.getRenderArea().xCenter() + ", Y=" + (float) manager.getRenderArea().yCenter());
          positionFloatingBar(window, floatingBar);
        });
    refreshTimer.start();

    window.show();
    // position the floating bar initially (resizeEvent will keep it in place)
    positionFloatingBar(window, floatingBar);
    floatingBar.raise();

    QApplication.exec();
    QApplication.shutdown();
  }
}
