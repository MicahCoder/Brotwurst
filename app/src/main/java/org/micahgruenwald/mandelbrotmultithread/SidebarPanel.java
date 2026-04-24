package org.micahgruenwald.mandelbrotmultithread;

import io.qt.gui.QPixmap;
import io.qt.widgets.QComboBox;
import io.qt.widgets.QHBoxLayout;
import io.qt.widgets.QLabel;
import io.qt.widgets.QPushButton;
import io.qt.widgets.QVBoxLayout;
import io.qt.widgets.QWidget;

class SidebarPanel extends QWidget {
  private Manager manager;
  SidebarPanel(ZoomableCropImageView imageView, Manager manager) {
    this.manager = manager;
    QVBoxLayout sidebarLayout = new QVBoxLayout();
    QLabel titleLabel = new QLabel("Mandelbrot Explorer");
    titleLabel.font().setPixelSize(25);
    titleLabel.font().setBold(true);
    sidebarLayout.addWidget(titleLabel);

    sidebarLayout.addWidget(new QLabel("Zoom Controls"));
    QHBoxLayout zoomButtonsLayout = new QHBoxLayout();
    zoomButtonsLayout.setSpacing(5);
    QPushButton zoomInButton = new QPushButton("+");
    QPushButton zoomOutButton = new QPushButton("-");
    QPushButton resetZoomButton = new QPushButton("Reset");
    zoomButtonsLayout.addWidget(zoomInButton);
    zoomButtonsLayout.addWidget(zoomOutButton);
    zoomButtonsLayout.addWidget(resetZoomButton);
    sidebarLayout.addLayout(zoomButtonsLayout); 

    QComboBox colorChoices = new QComboBox();
    colorChoices.addItem("Orange Black and Blue");
    colorChoices.addItem("Random Colors");
    colorChoices.addItem("Rainbow");
    colorChoices.addItem("Black and White");

    colorChoices.currentIndexChanged.connect(
        (i) -> {
          ColorMode mode =
              switch (i) {
                case 0 -> ColorMode.ORANGE_BLACK_BLUE;
                case 1 -> ColorMode.RANDOM;
                case 2 -> ColorMode.HSV_WITH_BLACK;
                case 3 -> ColorMode.BLACK_AND_WHITE;
                default -> ColorMode.BLACK_AND_WHITE;
              };
          Calculator.setColorMode(mode);
          manager.render();
          QPixmap map = manager.getQPixmap();
          imageView.setImage(map);
        });

    zoomInButton.clicked.connect(imageView::zoomIn);
    zoomOutButton.clicked.connect(imageView::zoomOut);
    resetZoomButton.clicked.connect(imageView::resetZoom);
    sidebarLayout.addWidget(colorChoices);
    sidebarLayout.addStretch(1);

    setLayout(sidebarLayout);
    setMinimumWidth(160);
  }
}
