package org.micahgruenwald.mandelbrotmultithread;

import io.qt.gui.QColor;
import io.qt.gui.QPixmap;
import io.qt.widgets.QComboBox;
import io.qt.widgets.QDoubleSpinBox;
import io.qt.widgets.QHBoxLayout;
import io.qt.widgets.QLabel;
import io.qt.widgets.QPushButton;
import io.qt.widgets.QSpinBox;
import io.qt.widgets.QVBoxLayout;
import io.qt.widgets.QWidget;

class SidebarPanel extends QWidget {
  private Manager manager;
  private ZoomableCropImageView imageView;

  // Constructs everything about the sidear panel. Most of the class is really just this.
  /**
   * This panel stores all the settings for the renderer
   * @param imageView The zoom bar
   * @param manager the manager for rendering
   */
  SidebarPanel(ZoomableCropImageView imageView, Manager manager) {
    this.manager = manager;
    this.imageView = imageView;
    QVBoxLayout sidebarLayout = new QVBoxLayout();

    QLabel title = new QLabel("Zoom Controls");
    title.setObjectName("controlsLabel");
    sidebarLayout.addWidget(title);
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
    iterationNumber.setValue(200);

    QDoubleSpinBox cx = new QDoubleSpinBox();
    cx.setSingleStep(0.01);
    cx.setRange(-2.0, 2.0);
    cx.setDecimals(4);
    cx.setValue(-0.4);

    QDoubleSpinBox cy = new QDoubleSpinBox();
    cy.setSingleStep(0.01);
    cy.setRange(-2.0, 2.0);
    cy.setDecimals(4);
    cy.setValue(0.6);
    QSpinBox n = new QSpinBox();
    n.setSingleStep(1);
    n.setRange(2, 5);
    n.setValue(2);

    cx.valueChanged.connect((i)->{
      Calculator.setJuliaValues(cx.value(), cy.value(), n.value());
      renderWindow(false);
    });

    cy.valueChanged.connect((i)->{
      Calculator.setJuliaValues(cx.value(), cy.value(), n.value());
      renderWindow(false);
    });
    n.valueChanged.connect((i)->{
      Calculator.setJuliaValues(cx.value(), cy.value(), n.value());
      renderWindow(false);
    });

    cy.setVisible(false);
    cx.setVisible(false);
    n.setVisible(false);
    
    iterationNumber.valueChanged.connect((i)->{
      Calculator.setMaxIterations(i);
      renderWindow(false);
    });

    QComboBox colorChoices = new QComboBox();
    colorChoices.addItem("Classic");
    colorChoices.addItem("Random Colors");
    colorChoices.addItem("Rainbow");
    colorChoices.addItem("Black and White");
    colorChoices.addItem("Blue Green and Black");
    colorChoices.addItem("Viridis");
    colorChoices.addItem("Plasma");
    colorChoices.addItem("Inferno");
    colorChoices.addItem("Magma");
    colorChoices.addItem("Cividis");
    colorChoices.addItem("Lava");
    colorChoices.addItem("Ocean");
    colorChoices.addItem("Pop");
    colorChoices.addItem("Simple Gradient");

    SelectColorButton color1 = new SelectColorButton(this, new QColor(0,0,160));
    SelectColorButton color2 = new SelectColorButton(this, new QColor(160,160,160));
    color1.hide();
    color2.hide();
    color1.clicked.connect(()->{
       QColor color1Color = color1.getColor();
            QColor color2Color = color2.getColor();
            Calculator.setColorMode(new ColorMode.SimpleGradient(color1Color.red(),color1Color.green(),color1Color.blue(),color2Color.red(),color2Color.green(),color2Color.blue()));
              manager.render();
          QPixmap map = manager.getQPixmap();
          imageView.setImage(map);
    });
    color2.clicked.connect(()->{
       QColor color1Color = color1.getColor();
            QColor color2Color = color2.getColor();
            Calculator.setColorMode(new ColorMode.SimpleGradient(color1Color.red(),color1Color.green(),color1Color.blue(),color2Color.red(),color2Color.green(),color2Color.blue()));
              manager.render();
          QPixmap map = manager.getQPixmap();
          imageView.setImage(map);
    });
    //Color choices logic, based off of the spinbox for color
    colorChoices.currentIndexChanged.connect(
        (i) -> {
          //Simple gradient
          if(i==13){
            QColor color1Color = color1.getColor();
            QColor color2Color = color2.getColor();
            color1.show();
            color2.show();
            Calculator.setColorMode(new ColorMode.SimpleGradient(color1Color.red(),color1Color.green(),color1Color.blue(),color2Color.red(),color2Color.green(),color2Color.blue()));
          }else{

          ColorMode mode =
              switch (i) {
                case 0 -> ColorMode.ORANGE_BLACK_BLUE;
                case 1 -> ColorMode.RANDOM;
                case 2 -> ColorMode.HSV_WITH_BLACK;
                case 3 -> ColorMode.BLACK_AND_WHITE;
                case 4-> ColorMode.GREEN_BLUE_BLACK;
                case 5 -> ColorMode.VIRIDIS;
                case 6 -> ColorMode.PLASMA;
                case 7 -> ColorMode.INFERNO;
                case 8 -> ColorMode.MAGMA;
                case 9 -> ColorMode.CIVIDIS;
                case 10 -> ColorMode.LAVA;
                case 11 -> ColorMode.OCEAN;
                case 12 -> ColorMode.POP;
                default -> ColorMode.BLACK_AND_WHITE;
              };
          color1.hide();
          color2.hide();
          Calculator.setColorMode(mode);
          }
          renderWindow(false);
        });

    QComboBox fractalType = new QComboBox();
    fractalType.addItem("Mandelbrot");
    fractalType.addItem("Julia");
    QHBoxLayout cxcynLabel = new QHBoxLayout();
    cxcynLabel.setSpacing(5);
    QLabel cxLabel = new QLabel("x");
    QLabel cyLabel = new QLabel("yi");
    QLabel nLabel = new QLabel("n");
    cxcynLabel.addWidget(cxLabel);
    cxcynLabel.addWidget(cyLabel);
    cxcynLabel.addWidget(nLabel);
    cyLabel.setVisible(false);
    cxLabel.setVisible(false);
    nLabel.setVisible(false);
    fractalType.currentIndexChanged.connect(
        (i) -> {
          if(i == 0){
            manager.setRenderArea(Calculator.DEFAULT_MANDELBROT_AREA);
            Calculator.setJuliaMode(false);
            cy.setVisible(false);
            cx.setVisible(false);
            n.setVisible(false);
            cyLabel.setVisible(false);
            cxLabel.setVisible(false);
            nLabel.setVisible(false);
    
          }else{
            manager.setRenderArea(Calculator.DEFAULT_JULIA_AREA);
            Calculator.setJuliaMode(true);
            cy.setVisible(true);
            cx.setVisible(true);
            n.setVisible(true);
            cyLabel.setVisible(true);
            cxLabel.setVisible(true);
            nLabel.setVisible(true);
          }
          renderWindow(false);
        });
    QHBoxLayout cxcyn = new QHBoxLayout();
    cxcyn.setSpacing(5);
    cxcyn.addWidget(cx);
    cxcyn.addWidget(cy);
    cxcyn.addWidget(n);
   
    QHBoxLayout simpleGradient = new QHBoxLayout();
    simpleGradient.setSpacing(25);
    simpleGradient.addWidget(color1);
    simpleGradient.addWidget(color2);
    QPushButton saveButton = new QPushButton("Save");

    SavePopup savePopup = new SavePopup(this, manager);

    QPushButton setCordsButton = new QPushButton("Set Coordinates");

    CoordinatePopup setCordsPopup = new CoordinatePopup(this, manager);
    zoomInButton.clicked.connect(imageView::zoomOutCenter);
    zoomOutButton.clicked.connect(imageView::zoomInCenter);
    resetZoomButton.clicked.connect(imageView::resetZoom);
    saveButton.clicked.connect(savePopup::exec);
    setCordsButton.clicked.connect(setCordsPopup::exec);


    // Add all of the constructed widgets to the layout
    sidebarLayout.addWidget(new QLabel("Color Choices"));
    sidebarLayout.addWidget(colorChoices);
    sidebarLayout.addLayout(simpleGradient);
    QLabel fractalTypeLabel = new QLabel("Fractal Type");
    fractalTypeLabel.setObjectName("controlsLabel");
    sidebarLayout.addWidget(fractalTypeLabel);
    sidebarLayout.addWidget(fractalType);
    sidebarLayout.addLayout(cxcynLabel); 
    sidebarLayout.addLayout(cxcyn);
    QLabel iterationLabel = new QLabel("Max Iterations");
    iterationLabel.setObjectName("controlsLabel");
    sidebarLayout.addWidget(iterationLabel);
    sidebarLayout.addWidget(iterationNumber);
    sidebarLayout.addWidget(saveButton);
    sidebarLayout.addWidget(setCordsButton);
    sidebarLayout.addStretch(1);

    setLayout(sidebarLayout);
    setMinimumWidth(160);
  }
  /**
   * renders the window
   * @param moving whether to render at a low res(moving true) or a high res(false)
   */
  protected void renderWindow(boolean moving){
    manager.setImage(moving?App.movingImage:App.stationaryImage);
    manager.render();
    imageView.setImage(manager.getQPixmap());
  }

}
