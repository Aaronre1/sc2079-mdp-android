package edu.ntu.scse.test.ui.home.modal;

import java.util.ArrayList;
public class GridSystem {
    Robot robot;
    ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    Obstacle obstacleToEdit;

    private int rows = 21;
    private int cols = 21;

    int[][] grids = new int[rows][cols];

    public static final int EMPTY_CELL_CODE = 0;
    public static final int CAR_CELL_CODE = 1;
    public static final int TARGET_CELL_CODE = 3;
    public static final int EXPLORE_CELL_CODE = 4;
    public static final int EXPLORE_HEAD_CELL_CODE = 5;
    public static final int FINAL_PATH_CELL_CODE = 6;

    public GridSystem() {
        super();
        robot = new Robot(this);
        grids[robot.getRobotX()][robot.getRobotY()] = CAR_CELL_CODE;
        grids[robot.getRobotX()][robot.getRobotY()+1] = CAR_CELL_CODE;
        grids[robot.getRobotX()][robot.getRobotY()-1] = CAR_CELL_CODE;
        grids[robot.getRobotX()+1][robot.getRobotY()] = CAR_CELL_CODE;
        grids[robot.getRobotX()+1][robot.getRobotY()+1] = CAR_CELL_CODE;
        grids[robot.getRobotX()+1][robot.getRobotY()-1] = CAR_CELL_CODE;
        grids[robot.getRobotX()-1][robot.getRobotY()] = CAR_CELL_CODE;
        grids[robot.getRobotX()-1][robot.getRobotY()+1] = CAR_CELL_CODE;
        grids[robot.getRobotX()-1][robot.getRobotY()-1] = CAR_CELL_CODE;

    }

    public final void resetGrid() {
        for(int i = 1; i <= 19; ++i)
            for(int j = 1; j <= 19; ++j)
                this.grids[i][j] = 0;

        this.getRobot().setRobotX(1);
        this.getRobot().setRobotY(19);
        this.getRobot().setFacing(Robot.NORTH);
        obstacles.clear();
        this.grids[getRobot().getRobotX()][getRobot().getRobotY()] = CAR_CELL_CODE;
        this.grids[robot.getRobotX()][robot.getRobotY()+1] = CAR_CELL_CODE;
        this.grids[robot.getRobotX()][robot.getRobotY()-1] = CAR_CELL_CODE;
        this.grids[robot.getRobotX()+1][robot.getRobotY()] = CAR_CELL_CODE;
        this.grids[robot.getRobotX()+1][robot.getRobotY()+1] = CAR_CELL_CODE;
        this.grids[robot.getRobotX()+1][robot.getRobotY()-1] = CAR_CELL_CODE;
        this.grids[robot.getRobotX()-1][robot.getRobotY()] = CAR_CELL_CODE;
        this.grids[robot.getRobotX()-1][robot.getRobotY()+1] = CAR_CELL_CODE;
        this.grids[robot.getRobotX()-1][robot.getRobotY()-1] = CAR_CELL_CODE;
    }

    public Robot getRobot() { return robot;}

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public void deleteObstacle(Obstacle obstacle) {
        int id = obstacle.getId();

        obstacles.remove(obstacle.getId());

        for (int i = id; i < obstacles.size(); i++)
        {
            obstacles.get(id).setId(id);
            id++;
        }
    }

    public Obstacle getObstacleToEdit() {
        return obstacleToEdit;
    }

    public void setObstacleToEdit(Obstacle obstacleToEdit) {
        this.obstacleToEdit = obstacleToEdit;
    }

    public Obstacle findObstacle(int x, int y) {
        Obstacle obstacle = null;
        for (int i = 0; i < obstacles.size(); i++) {
            if (obstacles.get(i).getxPosition() == x && obstacles.get(i).getyPosition() == y)
                obstacle = obstacles.get(i);
        }
        return obstacle;
    }

    public int[][] getGrids() {
        return grids;
    }
}
