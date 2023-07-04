package edu.ntu.scse.test.ui.home.modal;

public class Obstacle {
    private int xPosition;
    private int yPosition;
    private int id;
    private int facing;
    private int imageId;

    public static final int EAST = 0;
    public static final int NORTH = 1;
    public static final int WEST = 2;
    public static final int SOUTH = 3;

    public Obstacle(int xPosition, int yPosition, int id) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.id = id;
        this.facing = NORTH;
        this.imageId = -1;
    }

    public int getxPosition() {
        return xPosition;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public void rotateObstacle(boolean clockwise) {
        if (clockwise)
            this.facing = this.facing == SOUTH ? EAST : this.facing + 1;
        else
            this.facing = this.facing == NORTH ? WEST : this.facing - 1;
    }

    @Override
    public String toString() {
        return "Obstacle " + id + ": " +
                "(" + xPosition + "," + yPosition + "), " +
                "Facing: " + facing + "," +
                "ImageId: " + imageId + '.';
    }
}
