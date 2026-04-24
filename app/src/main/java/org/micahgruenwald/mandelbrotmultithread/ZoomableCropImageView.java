package org.micahgruenwald.mandelbrotmultithread;

import io.qt.core.Qt;
import io.qt.gui.QPaintEvent;
import io.qt.gui.QPainter;
import io.qt.gui.QPixmap;
import io.qt.gui.QWheelEvent;
import io.qt.widgets.QWidget;

class ZoomableCropImageView extends QWidget {
    private QPixmap image;
    private String errorText = "No image loaded";
    private double zoomFactor = 1.0;
    private static final double ZOOM_STEP = 1.15;
    private static final double MAX_ZOOM = 20.0;

    ZoomableCropImageView() {
      setFocusPolicy(Qt.FocusPolicy.StrongFocus);
    }

    void setImage(QPixmap pixmap) {
      image = pixmap;
      errorText = null;
      zoomFactor = 1.0;
      update();
    }

    void setErrorText(String text) {
      image = null;
      errorText = text;
      update();
    }

    void zoomIn() {
      setZoom(zoomFactor * ZOOM_STEP);
    }

    void zoomOut() {
      setZoom(zoomFactor / ZOOM_STEP);
    }

    void resetZoom() {
      setZoom(1.0);
    }

    private void setZoom(double zoom) {
      zoomFactor = Math.max(1.0, Math.min(zoom, MAX_ZOOM));
      update();
    }

    @Override
    protected void paintEvent(QPaintEvent event) {
      super.paintEvent(event);

      QPainter painter = new QPainter(this);
      try {
        if (image == null || image.isNull()) {
          painter.drawText(rect(), Qt.AlignmentFlag.AlignCenter.value(), errorText);
          return;
        }

        int viewWidth = Math.max(1, width());
        int viewHeight = Math.max(1, height());
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
  }