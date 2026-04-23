package org.micahgruenwald.mandelbrotmultithread;

import io.qt.gui.QPixmap;
import io.qt.widgets.*;

import java.nio.file.Files;
import java.nio.file.Path;

public class App {
  public static void main(String[] args) {
    QApplication.initialize(args);

    QWidget window = new QWidget();
    window.setWindowTitle("Test Window");
    window.resize(500, 300);

    QVBoxLayout layout = new QVBoxLayout(window);
    String imagePath = findImagePath();
    QLabel imageLabel = new QLabel();
    QPixmap pixmap = new QPixmap(imagePath);

    if (pixmap.isNull()) {
      imageLabel.setText("Could not load image. Tried: " + imagePath);
    } else {
      imageLabel.setPixmap(pixmap);
      imageLabel.setScaledContents(true);
    }

    layout.addWidget(imageLabel);
    layout.addWidget(new QPushButton("Test Button"));

    window.show();

    QApplication.exec();
    QApplication.shutdown();
  }

  private static String findImagePath() {
    Path cwd = Path.of("").toAbsolutePath().normalize();

    Path[] candidates = new Path[] {
      cwd.resolve("app/src/test/java/org/micahgruenwald/mandelbrotmultithread/testOutput/saved.png"),
      cwd.resolve("src/test/java/org/micahgruenwald/mandelbrotmultithread/testOutput/saved.png"),
      cwd.resolve("../app/src/test/java/org/micahgruenwald/mandelbrotmultithread/testOutput/saved.png")
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
