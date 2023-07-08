package edu.ntu.scse.test.ui.home.modal;

import java.util.ArrayList;

public class Grid {
    private int gridSize;
    private ArrayList<Obstacle> obstacles;

    public Grid(int gridSize, ArrayList<Obstacle> obstacles) {
        this.gridSize = gridSize;
        this.obstacles = obstacles;
    }

    public boolean isOutsideGrid(int row, int col) {
        return row < 0 || col < 0 || row >= gridSize || col >= gridSize;
    }

    public Obstacle getObstacleAt(int row, int col) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getRow() == row && obstacle.getCol() == col) {
                return obstacle;
            }
        }
        return null;
    }

    public void removeObstacle(Obstacle obstacle) {
        if (obstacle != null) {
            obstacles.remove(obstacle);
        }
    }

    public void addObstacle(Obstacle obstacle) {
        if (obstacle != null && !isOutsideGrid(obstacle.getRow(), obstacle.getCol())) {
            obstacles.add(obstacle);
        }
    }

    public boolean isCellOccupiedByCar(Car myCar, int row, int col) {
        if (myCar != null) {
            // check each cell in the 3x3 grid of the car
            for (int i = myCar.getRow(); i < myCar.getRow() + 3; i++) {
                for (int j = myCar.getCol(); j < myCar.getCol() + 3; j++) {
                    // if the row and column match any of the cells in the car's grid, return true
                    if (i == row && j == col) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
