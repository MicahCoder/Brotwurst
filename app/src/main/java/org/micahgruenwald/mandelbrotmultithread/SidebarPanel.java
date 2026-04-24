package org.micahgruenwald.mandelbrotmultithread;

import io.qt.gui.QPixmap;
import io.qt.widgets.QComboBox;
import io.qt.widgets.QHBoxLayout;
import io.qt.widgets.QLabel;
import io.qt.widgets.QPushButton;
import io.qt.widgets.QSpinBox;
import io.qt.widgets.QVBoxLayout;
import io.qt.widgets.QWidget;

class SidebarPanel extends QWidget {
  private Manager manager;
  private ZoomableCropImageView imageView;
  SidebarPanel(ZoomableCropImageView imageView, Manager manager) {
    this.manager = manager;
    this.imageView = imageView;
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

    QSpinBox iterationNumber = new QSpinBox();
    iterationNumber.setSingleStep(10);
    iterationNumber.setRange(10, 1000);
    iterationNumber.setValue(100);

    iterationNumber.valueChanged.connect((i)->{
      Calculator.setMaxIterations(i);
      renderWindow();
    });

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

    QComboBox fractalType = new QComboBox();
    fractalType.addItem("Mandelbrot");
    fractalType.addItem("Julia");

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
          renderWindow();
        });

        fractalType.currentIndexChanged.connect(
        (i) -> {
          if(i == 0){
            manager.setRenderArea(new RenderArea(-0.75, 0.0, 2.5,2.5));
            Calculator.setJuliaMode(false);
          }else{
            manager.setRenderArea(new RenderArea(0.0, 0.0, 3.5,3.5));
            Calculator.setJuliaMode(true);
          }
          renderWindow();
        });
    
    zoomInButton.clicked.connect(imageView::zoomIn);
    zoomOutButton.clicked.connect(imageView::zoomOut);
    resetZoomButton.clicked.connect(imageView::resetZoom);
    sidebarLayout.addWidget(new QLabel("Color Choices"));
    sidebarLayout.addWidget(colorChoices);
    sidebarLayout.addWidget(new QLabel("Fractal Type Choices"));
    sidebarLayout.addWidget(fractalType);
    sidebarLayout.addWidget(new QLabel("Max Iterations"));
    sidebarLayout.addWidget(iterationNumber);
    sidebarLayout.addStretch(1);

    setLayout(sidebarLayout);
    setMinimumWidth(160);
  }

  private void renderWindow(){
    manager.render();
    imageView.setImage(manager.getQPixmap());
  }
}
