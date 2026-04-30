package org.micahgruenwald.mandelbrotmultithread;

import io.qt.core.QPoint;
import io.qt.core.QPointF;
import io.qt.core.QTimer;
import io.qt.core.Qt;
import io.qt.gui.QCursor;
import io.qt.gui.QPainterPath;
import io.qt.gui.QKeyEvent;
import io.qt.gui.QPaintEvent;
import io.qt.gui.QPainter;
import io.qt.gui.QColor;
import io.qt.gui.QPixmap;
import io.qt.gui.QWheelEvent;
import io.qt.widgets.QWidget;

// This class is a custom widget that displays the fractal and allows zooming and panning.
class ZoomableCropImageView extends QWidget {
    private QPixmap image;
    private String errorText = "No image loaded";
    private double zoomFactor = 1.0;
    private QTimer timer;
  private static final int CORNER_RADIUS = 18;
    private static final double ZOOM_STEP = 1.05;
    private static final double MAX_ZOOM = 20.0;
    private static final double DRAG_FACT = 0.01;
    private final Manager manager;
    private QPointF mousePressOffset = new QPointF();

    /**
     * This makes a window where you can zoom in on a render
     * @param manager the manager for rendering. 
     */
    public ZoomableCropImageView(Manager manager) {
      this.manager = manager;
      setFocusPolicy(Qt.FocusPolicy.StrongFocus);
      setAttribute(Qt.WidgetAttribute.WA_StyledBackground, true);
      timer = new QTimer();
      timer.setInterval(100);
      timer.setSingleShot(true);
      timer.timeout.connect(()->renderWindow(false));
    }

    void setImage(QPixmap pixmap) {
      image = pixmap;
      errorText = null;
      zoomFactor = 1.0;
      update();
    }

    // Fallback for when we can't find/show an image.
    void setErrorText(String text) {
      image = null;
      errorText = text;
      update();
    }

    // Zoom logic.
    void zoomIn() {
      RenderArea area = manager.getRenderArea();
      double xWidth = area.xWidth() * ZOOM_STEP;
      double yWidth = area.yWidth() * ZOOM_STEP;
      QPoint mousePose = mapFromGlobal(QCursor.pos());
      Coordinate cartPose = mousePoseToCartesian(mousePose);
      double x = cartPose.x() - (cartPose.x() - area.xCenter()) * ZOOM_STEP;
      double y = cartPose.y() - (cartPose.y() - area.yCenter()) * ZOOM_STEP;
      manager.setRenderArea(new RenderArea(x,y,xWidth,yWidth));
      renderWindow(true);
      setImage(manager.getQPixmap());
    }

    void zoomOut() {
      RenderArea area = manager.getRenderArea();
      double xWidth = area.xWidth()/ ZOOM_STEP;
      double yWidth = area.yWidth()/ZOOM_STEP;
      QPoint mousePose = mapFromGlobal(QCursor.pos());
      Coordinate cartPose = mousePoseToCartesian(mousePose);
      double x = cartPose.x() - (cartPose.x() - area.xCenter()) / ZOOM_STEP;
      double y = cartPose.y() - (cartPose.y() - area.yCenter()) / ZOOM_STEP;
      manager.setRenderArea(new RenderArea(x,y,xWidth,yWidth));
      renderWindow(true);
      setImage(manager.getQPixmap());
    }

    void zoomInCenter() {
      RenderArea area = manager.getRenderArea();
      double xWidth = area.xWidth() * ZOOM_STEP;
      double yWidth = area.yWidth() * ZOOM_STEP;
      manager.setRenderArea(new RenderArea(area.xCenter(),area.yCenter(),xWidth,yWidth));
      renderWindow(true);
      setImage(manager.getQPixmap());
    }

    void zoomOutCenter() {
      RenderArea area = manager.getRenderArea();
      double xWidth = area.xWidth() / ZOOM_STEP;
      double yWidth = area.yWidth() / ZOOM_STEP;
      manager.setRenderArea(new RenderArea(area.xCenter(),area.yCenter(),xWidth,yWidth));
      renderWindow(true);
      setImage(manager.getQPixmap());
    }

    void resetZoom() {
      if(Calculator.getJuliaMode()){
        manager.setRenderArea(Calculator.DEFAULT_JULIA_AREA);
      }else{
        manager.setRenderArea(Calculator.DEFAULT_MANDELBROT_AREA);
      }
      renderWindow(false);
    }

    private void setZoom(double zoom) {
      zoomFactor = Math.max(1.0, Math.min(zoom, MAX_ZOOM));
      update();
    }

    // Called when the widget needs to be redrawn (after the manager renders a new image or when zooming/panning)
    @Override
    protected void paintEvent(QPaintEvent event) {
      super.paintEvent(event);

      QPainter painter = new QPainter(this);
      try {
        painter.setRenderHint(QPainter.RenderHint.Antialiasing, true);
        painter.setRenderHint(QPainter.RenderHint.SmoothPixmapTransform, true);

        int viewWidth = Math.max(1, width());
        int viewHeight = Math.max(1, height());
        QPainterPath clipPath = new QPainterPath();
        clipPath.addRoundedRect(0, 0, viewWidth, viewHeight, CORNER_RADIUS, CORNER_RADIUS);
        painter.setClipPath(clipPath);
        painter.fillPath(clipPath, new QColor("#0f1115"));

        if (image == null || image.isNull()) {
          painter.drawText(rect(), Qt.AlignmentFlag.AlignCenter.value(), errorText);
          return;
        }

        int imageWidth = image.width();
        int imageHeight = image.height();

        double viewAspect = (double) viewWidth / viewHeight;
        double imageAspect = (double) imageWidth / imageHeight;

        double baseSourceWidth;
        double baseSourceHeight;
        if (imageAspect > viewAspect) {
          baseSourceHeight = imageHeight;
          baseSourceWidth = imageHeight * viewAspect;
        } else {
          baseSourceWidth = imageWidth;
          baseSourceHeight = imageWidth / viewAspect;
        }

        int sourceWidth = Math.max(1, (int) Math.round(baseSourceWidth / zoomFactor));
        int sourceHeight = Math.max(1, (int) Math.round(baseSourceHeight / zoomFactor));
        int sourceX = (imageWidth - sourceWidth) / 2;
        int sourceY = (imageHeight - sourceHeight) / 2;

        painter.drawPixmap(0, 0, viewWidth, viewHeight, image, sourceX, sourceY, sourceWidth, sourceHeight);
      } finally {
        painter.end();
      }
    }

    // Zoom on scroll by capturing wheel events
    @Override
    protected void wheelEvent(QWheelEvent event) {
      if (event.angleDelta().y() > 0) {
        zoomIn();
      } else if (event.angleDelta().y() < 0) {
        zoomOut();
      } else {
        super.wheelEvent(event);
        return;
      }

      event.accept();
    }

    // Pan around with arrow keys
    @Override
    protected void keyPressEvent(QKeyEvent event){
      RenderArea area = manager.getRenderArea();
      switch(event.key()){
        case 0x01000013: //Up
            area = new RenderArea(area.xCenter(), area.yCenter() - DRAG_FACT * area.yWidth(), area.xWidth(), area.yWidth());
            manager.setRenderArea(area);
            renderWindow(true);
            break;
        case 0x01000014://Right
            area = new RenderArea(area.xCenter() + DRAG_FACT * area.xWidth(), area.yCenter(), area.xWidth(), area.yWidth());
            manager.setRenderArea(area);
            renderWindow(true);
            break;
        case 0x01000015: //Down
            area = new RenderArea(area.xCenter() , area.yCenter()+ DRAG_FACT * area.yWidth(), area.xWidth(), area.yWidth());
            manager.setRenderArea(area);
            renderWindow(true);
            break;
        case 0x01000012: //Left
            area = new RenderArea(area.xCenter() - DRAG_FACT * area.xWidth(), area.yCenter(), area.xWidth(), area.yWidth());
            manager.setRenderArea(area);
            renderWindow(true);
            break;
        default:
          break;
      }
    }

    //Not Working just yet.
    // @Override
    // protected void mousePressEvent(QMouseEvent event){
    //   RenderArea area = manager.getRenderArea();
    //   mousePressOffset = cartesianToMousePose(new Coordinate(area.xCenter(), area.yCenter())).minus(event.pos());
    // }

    // @Override
    // protected void mouseMoveEvent(QMouseEvent event){
    //   RenderArea area = manager.getRenderArea();
    //   Coordinate newCenter = mousePoseToCartesian(event.position().plus(mousePressOffset));
    //   RenderArea newArea = new RenderArea(newCenter.x(), newCenter.y(), area.xWidth(),area.yWidth());
    //   manager.setRenderArea(newArea);
    //   manager.render();
    //   setImage(manager.getQPixmap());
    // }

    private Coordinate mousePoseToCartesian(QPoint mousePose){
      RenderArea area = manager.getRenderArea();
      double mx = (mousePose.x()/(double)width())*area.xWidth() +area.x0() ;
      double my = (( mousePose.y()) / (double) height()) * area.yWidth() + area.y0();
      return new Coordinate(mx,my);
    }
    private Coordinate mousePoseToCartesian(QPointF mousePose){
      RenderArea area = manager.getRenderArea();
      double mx = (mousePose.x()/(double)width())*area.xWidth() +area.x0() ;
      double my = (( mousePose.y()) / (double) height()) * area.yWidth() + area.y0();
      return new Coordinate(mx,my);
    }

    private QPointF cartesianToMousePose(Coordinate cart){
      RenderArea area = manager.getRenderArea();
      double x = (cart.x() - area.x0()) * (double) width() / area.xWidth();  
      double y = (cart.y() - area.y0()) * (double) height() / area.yWidth();  
      return new QPointF(x,y);
    }

    private record Coordinate(double x, double y) {
      public Coordinate minus(Coordinate other){
        return new Coordinate(x() - other.x(), y() - other.y());
      }
      public Coordinate plus(Coordinate other){
        return new Coordinate(x() + other.x(), y() + other.y());
      }
      public Coordinate times(double s){
        return new Coordinate(x()*s, y()*s);
      }


    }

  private void renderWindow(boolean moving){
    if(moving){
      timer.start();
      manager.setImage(App.movingImage);
    }else{
      manager.setImage(App.stationaryImage);
    }
    manager.render();
    setImage(manager.getQPixmap());
  }


  }