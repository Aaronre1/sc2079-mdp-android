package edu.ntu.scse.test.ui.home.modal;

public class Robot {
    private int robotX;
    private int robotY;
    private int robotFacing;

    private GridSystem gridSystem;

    public static final int EAST = 0;
    public static final int NORTH = 1;
    public static final int WEST = 2;
    public static final int SOUTH = 3;

    public Robot(GridSystem gridSystem) {
        this.robotX = 1;
        this.robotY = 19;
        this.robotFacing = NORTH;
        this.gridSystem = gridSystem;
    }

    public int getRobotX() { return robotX; }
    public void setRobotX(int robotX) { this.robotX = robotX; }

    public int getRobotY() { return robotY; }
    public void setRobotY(int robotY) { this.robotY = robotY; }

    public int getFacing() { return robotFacing; }
    public void setFacing(int f) { this.robotFacing = f; }
    public String getFacingText() {
        switch (this.robotFacing) {
            case 0: return "EAST";
            case 1: return "NORTH";
            case 2: return "WEST";
            case 3: return "SOUTH";
        }
        return "N";
    }

    public void rotateRobot(boolean clockwise) {
        if (clockwise)
            this.robotFacing = this.robotFacing == SOUTH ? EAST : this.robotFacing + 1;
        else
            this.robotFacing = this.robotFacing == NORTH ? WEST : this.robotFacing - 1;
    }

    @Override
    public String toString() {
        return "Robot: " +
                "(" + robotX + ", " + robotY + "), " +
                "Facing:" + robotFacing + '.';
    }
}
