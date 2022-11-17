package com.leewyatt.fxtools.event;

import javafx.scene.paint.Paint;

import java.util.Objects;

/**
 * @author LeeWyatt
 */
public class PaintChooseEvent {
    private int index;
    private Paint paint;

    public PaintChooseEvent() {
    }

    public PaintChooseEvent(int index, Paint paint) {
        this.index = index;
        this.paint = paint;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaintChooseEvent that = (PaintChooseEvent) o;
        return index == that.index && Objects.equals(paint, that.paint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, paint);
    }

    @Override
    public String toString() {
        return "PaintChooseEvent{" +
                "index=" + index +
                ", paint=" + paint +
                '}';
    }
}
