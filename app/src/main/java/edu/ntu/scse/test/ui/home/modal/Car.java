package edu.ntu.scse.test.ui.home.modal;

public class Car {
    private int row;
    private int col;
    private int direction;

    public static final int NORTH = 0;
    public static final int EAST = 1;
    public static final int SOUTH = 2;
    public static final int WEST = 3;

    public Car(int row, int col, int direction) {
        this.row = row;
        this.col = col;
        this.direction = direction;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void rotate() {
        direction = (direction + 1) % 4;
    }

    @Override
    public String toString() {
        return "Car{" +
                "row=" + row +
                ", col=" + col +
                ", direction=" + direction +
                '}';
    }
}

