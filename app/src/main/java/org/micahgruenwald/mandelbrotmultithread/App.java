package org.micahgruenwald.mandelbrotmultithread;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

import io.qt.core.Qt;
import io.qt.widgets.QApplication;
import io.qt.widgets.QHBoxLayout;
import io.qt.widgets.QSizePolicy;
import io.qt.widgets.QSplitter;
import io.qt.widgets.QWidget;

public class App {
  public static void main(String[] args) {
    QApplication.initialize(args);

    QWidget window = new QWidget();
    window.setWindowTitle("Mandelbrot Renderer");
    window.resize(900, 600);

    QHBoxLayout mainLayout = new QHBoxLayout(window);
    BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
    Calculator.setColorMode(ColorMode.ORANGE_BLACK_BLUE);
    Calculator.setJuliaValues(-0.4, 0.6, 2);
    Calculator.setMaxIterations(200);
    Calculator.setJuliaMode(false);
    Manager manager = new Manager(8, new RenderArea(-0.75, 0, 2.5, 2.5), image);

    manager.render();

    ZoomableCropImageView imageView = new ZoomableCropImageView();
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
