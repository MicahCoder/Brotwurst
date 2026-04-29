package org.micahgruenwald.mandelbrotmultithread;

import io.qt.widgets.QDialog;
import io.qt.widgets.QDoubleSpinBox;
import io.qt.widgets.QLabel;
import io.qt.widgets.QPushButton;
import io.qt.widgets.QVBoxLayout;

public class CoordinatePopup extends QDialog{
    private final QDoubleSpinBox xCenter;
    private final QDoubleSpinBox yCenter;
    private final QDoubleSpinBox width;
    private final Manager manager;

    public CoordinatePopup(SidebarPanel parent, Manager manager) {
        super(parent);
        setWindowTitle("Enter Coordinates");
        this.manager = manager;
        QVBoxLayout layout = new QVBoxLayout(this);
        QPushButton set = new QPushButton("Set Coordinates");
        xCenter = new QDoubleSpinBox();
        yCenter = new QDoubleSpinBox();
        width = new QDoubleSpinBox();
        

        xCenter.setDecimals(9);
        xCenter.setRange(-2.5, 2.5);
        yCenter.setDecimals(9);
        yCenter.setRange(-2.5, 2.5);
        width.setDecimals(9);
        width.setRange(0.0000001, 5);

        set.clicked.connect(()->{
            manager.setRenderArea(new RenderArea(xCenter.value(), yCenter.value(), width.value(), width.value()));
            parent.renderWindow(false);
            close();
        });
        layout.addWidget(new QLabel("xCenter"));
        layout.addWidget(xCenter);
        layout.addWidget(new QLabel("yCenter"));
        layout.addWidget(yCenter);
        layout.addWidget(new QLabel("Width"));
        layout.addWidget(width);
        layout.addWidget(set);
    }
    @Override
    public int exec(){
        RenderArea renderArea = manager.getRenderArea();
        xCenter.setValue(renderArea.xCenter());
        yCenter.setValue(renderArea.yCenter());
        width.setValue(renderArea.xWidth());
        double stepSize = Math.pow(10,(int)Math.log10(renderArea.xWidth() / 20));
        xCenter.setSingleStep(stepSize);
        yCenter.setSingleStep(stepSize);
        width.setSingleStep(stepSize);
        return super.exec();
    }
}
