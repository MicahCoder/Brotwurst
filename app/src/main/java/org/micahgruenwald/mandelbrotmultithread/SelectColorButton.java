package org.micahgruenwald.mandelbrotmultithread;

import io.qt.gui.QColor;
import io.qt.widgets.QColorDialog;
import io.qt.widgets.QPushButton;
import io.qt.widgets.QWidget;

/*
Inspiration found @link{https://stackoverflow.com/questions/18257281/qt-color-picker-widget}
*/
public class SelectColorButton extends QPushButton {
  private QColor color;

  /**
   * @param parent defines parent widget
   * @param color defines the intial color of the box.
   */
  public SelectColorButton(QWidget parent, QColor color) {
    super(parent);
    this.color = color;
    setFixedSize(25, 25);
    updateColor();
    clicked.connect(this::changeColor);
  }

  /** Updates the color of the box */
  public void updateColor() {
    setStyleSheet("background-color: " + color.name());
  }

  /** Changes the color with a popup dialog. */
  public void changeColor() {
    QColor newColor = QColorDialog.getColor(color, parentWidget());
    if (newColor != color) {
      setColor(newColor);
    }
  }

  /**
   * Set the color to color
   *
   * @param color the given color
   */
  public void setColor(QColor color) {
    this.color = color;
    updateColor();
  }

  /**
   * @return the color of the box.
   */
  public QColor getColor() {
    return color;
  }
}
