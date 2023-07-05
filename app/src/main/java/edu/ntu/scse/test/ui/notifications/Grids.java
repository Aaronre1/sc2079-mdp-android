package edu.ntu.scse.test.ui.notifications;

import static java.lang.Math.ceil;

import static edu.ntu.scse.test.ui.home.modal.GridSystem.CAR_CELL_CODE;
import static edu.ntu.scse.test.ui.home.modal.GridSystem.EMPTY_CELL_CODE;
import static edu.ntu.scse.test.ui.home.modal.GridSystem.TARGET_CELL_CODE;
import static edu.ntu.scse.test.ui.home.modal.Obstacle.EAST;
import static edu.ntu.scse.test.ui.home.modal.Obstacle.NORTH;
import static edu.ntu.scse.test.ui.home.modal.Obstacle.SOUTH;
import static edu.ntu.scse.test.ui.home.modal.Obstacle.WEST;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import edu.ntu.scse.test.R;
import edu.ntu.scse.test.ui.home.modal.GridSystem;
import edu.ntu.scse.test.ui.home.modal.Obstacle;

public final class Grids extends View {
    private int pathColor;
    private int startColor;
    private int endColor;
    private int wheelColor;
    private int obstacleColor;
    private Paint pathPaintColor = new Paint();
    private Paint startPaintColor = new Paint();
    private Paint endPaintColor = new Paint();
    private Paint obstaclePaintColor = new Paint();

    private int cellSize = 0;
    private GridSystem gridSystem = new GridSystem();
    private int turn = 0;
    private boolean isSolving = false;

    int x0 = -1;
    int y0 = -1;

    public static final int NEW_OBSTACLE = 0;
    public static final int ROBOT = -1;
    public static final int EXISTING_OBSTACLE = 1;

    private Canvas canvas;

    public Grids(Context context) {
        super(context);
    }

    public Grids(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
       // TypedArray typedArray =
       //        context.getTheme().obtainStyledAttributes(attrs, styleable.GridMap, 0, 0);
        //try {
            //this.pathColor = typedArray.getInteger(R.styleable.GridMap_gridColor, 0);
            //this.startColor = typedArray.getInteger(R.styleable.GridMap_roboColor, 0);
            //this.endColor = typedArray.getInteger(R.styleable.GridMap_targetColor, 0);
            //this.wheelColor = typedArray.getInteger(R.styleable.GridMap_wheelColor, 0);
            //this.obstacleColor = typedArray.getInteger(styleable.GridMap_tarNumColor, 0);
        //} finally {
         //   typedArray.recycle();
       // }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int dimension = Math.min(height, width);
        this.cellSize = dimension / 20;
        this.setMeasuredDimension(dimension, dimension);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        this.setPaint(this.pathPaintColor, this.pathColor);
        this.drawGrid(canvas);
        this.drawGridNumber(canvas);

        this.drawCar(canvas);

        int n = 0;
        while (n < gridSystem.getObstacles().size()) {
            this.drawTarget(canvas, n);
            n++;
        }

    }

    private void colorCell(Canvas canvas, int r, int c, Paint paintColor) {
        RectF rectF = new RectF(
                (float)((c - 1) * this.cellSize),
                (float)((r - 1) * this.cellSize),
                (float)(c * this.cellSize),
                (float)(r * this.cellSize)
        );

        canvas.drawRoundRect(
                rectF, 5.0F, 5.0F, paintColor
        );
        this.invalidate();
    }

    private void setPaint(Paint paintColor, int color) {
        paintColor.setStyle(Style.FILL);
        paintColor.setColor(color);
        paintColor.setAntiAlias(true);
    }

    public final GridSystem getGridSystem() {
        return this.gridSystem;
    }

    private void drawGrid(Canvas canvas) {
        for(int i = 0; i <= 20; ++i) {
            for(int j = 0; j <= 20; ++j) {
                RectF rectF = new RectF(
                        (float)((j - 1) * this.cellSize) + (float)1,
                        (float)((i - 1) * this.cellSize) + (float)1,
                        (float)(j * this.cellSize) - (float)1,
                        (float)(i * this.cellSize) - (float)1
                );
                int cornersRadius = 5;
                canvas.drawRoundRect(
                        rectF, // rect
                        (float)cornersRadius, // rx
                        (float)cornersRadius, // ry
                        this.pathPaintColor // Paint
                );
            }
        }
    }

    private void drawGridNumber(Canvas canvas) {
        this.setPaint(this.obstaclePaintColor, this.obstacleColor);
        float halfSize = this.cellSize * 0.38f;
        for (int x = 19; x >= 0; --x) {
            // Left Vertical
            canvas.drawText(Integer.toString(x+1), this.cellSize * x + halfSize, this.cellSize * 19.6f, obstaclePaintColor);
            // Bottom Horizontal
            canvas.drawText(Integer.toString(20-x), halfSize, this.cellSize * x + halfSize * 1.5f, obstaclePaintColor);
        }
    }

    private void drawCar(Canvas canvas) {
        this.setPaint(this.startPaintColor, this.startColor);

        this.colorCell(canvas, this.gridSystem.getRobot().getRobotY(), this.gridSystem.getRobot().getRobotX(), this.startPaintColor);
        this.colorCell(canvas, this.gridSystem.getRobot().getRobotY(), this.gridSystem.getRobot().getRobotX() + 1, this.startPaintColor);
        this.colorCell(canvas, this.gridSystem.getRobot().getRobotY(), this.gridSystem.getRobot().getRobotX() + 2, this.startPaintColor);

        this.colorCell(canvas, this.gridSystem.getRobot().getRobotY() + 1, this.gridSystem.getRobot().getRobotX(), this.startPaintColor);
        this.colorCell(canvas, this.gridSystem.getRobot().getRobotY() + 1, this.gridSystem.getRobot().getRobotX() + 1, this.startPaintColor);
        this.colorCell(canvas, this.gridSystem.getRobot().getRobotY() + 1, this.gridSystem.getRobot().getRobotX() + 2, this.startPaintColor);

        this.colorCell(canvas, this.gridSystem.getRobot().getRobotY() - 1, this.gridSystem.getRobot().getRobotX(), this.startPaintColor);
        this.colorCell(canvas, this.gridSystem.getRobot().getRobotY() - 1, this.gridSystem.getRobot().getRobotX() + 1, this.startPaintColor);
        this.colorCell(canvas, this.gridSystem.getRobot().getRobotY() - 1, this.gridSystem.getRobot().getRobotX() + 2, this.startPaintColor);
        this.drawWheel(canvas);

        this.invalidate();
    }

    private void drawWheel(Canvas canvas) {
        int facingRotation = gridSystem.getRobot().getFacing(); // 0-->3
        switch (facingRotation) {
            case 0:
                facingRotation = 1;
                break;
            case 1:
                facingRotation = 0;
                break;
            case 2:
                facingRotation = 3;
                break;
            case 3:
                facingRotation = 2;
                break;
        }

        canvas.save();
        canvas.rotate(90 * facingRotation, (this.gridSystem.getRobot().getRobotX()) * this.cellSize, (this.gridSystem.getRobot().getRobotY() - 0.05f) * this.cellSize);


        CornerPathEffect corEffect = new CornerPathEffect(12f);

        Paint paint = new Paint();
        paint.setColor(this.wheelColor);
        paint.setStrokeWidth(6);
        paint.setPathEffect(corEffect);
        Path path = new Path();

        float offsetWheel = 0.9f * this.cellSize;

        float [][] wheels = {
                { (this.gridSystem.getRobot().getRobotX() - 0.7f) * this.cellSize, (this.gridSystem.getRobot().getRobotY() - 0.1f) * this.cellSize},
                { (this.gridSystem.getRobot().getRobotX() - 0.7f) * this.cellSize, (this.gridSystem.getRobot().getRobotY() - 0.75f) * this.cellSize },
                { (this.gridSystem.getRobot().getRobotX() - 0.2f) * this.cellSize, (this.gridSystem.getRobot().getRobotY() - 0.75f) * this.cellSize },
                { (this.gridSystem.getRobot().getRobotX() - 0.2f) * this.cellSize, (this.gridSystem.getRobot().getRobotY() - 0.1f) * this.cellSize }
        };

        path.moveTo(wheels[0][0] + offsetWheel, wheels[0][1]);
        path.lineTo(wheels[1][0] + offsetWheel, wheels[1][1]);
        path.lineTo(wheels[2][0] + offsetWheel, wheels[2][1]);
        path.lineTo(wheels[3][0] + offsetWheel, wheels[3][1]);

        canvas.drawPath(path, paint);

        canvas.restore();


        this.invalidate();

    }

    private void drawTarget(Canvas canvas, int obstacleId) {
        int x = this.gridSystem.getObstacles().get(obstacleId).getxPosition();
        int y = this.gridSystem.getObstacles().get(obstacleId).getyPosition();

        this.setPaint(this.endPaintColor, this.endColor);
        this.colorCell(canvas, y, x, this.endPaintColor);

        this.setPaint(this.obstaclePaintColor, this.obstacleColor);

        // UPDATE TARGET IMAGE
        if (this.gridSystem.getObstacles().get(obstacleId).getImageId() > -1)
        {
            Paint textPaint = new Paint();
            textPaint.setTextSize(30);
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            textPaint.setColor(Color.WHITE);
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(Integer.toString(this.gridSystem.getObstacles().get(obstacleId).getImageId()),
                    this.cellSize * (x - 1) + this.cellSize * 0.5f,
                    this.cellSize * y - this.cellSize * 0.25f,
                    textPaint
            );
        }

        else {

            canvas.drawText(Integer.toString(obstacleId + 1),
                    this.cellSize * (x - 1) + this.cellSize * 0.4f,
                    this.cellSize * y - this.cellSize * 0.4f,
                    obstaclePaintColor
            );
        }

        this.setPaint(this.startPaintColor, this.startColor);

        float leftBound = 0, topBound = 0, rightBound = 0, bottomBound = 0;
        switch(gridSystem.getObstacles().get(obstacleId).getFacing()) {
            case NORTH:
                leftBound = -1;
                topBound = -0.9f;
                rightBound = 0;
                bottomBound = -1;
                break;
            case EAST:
                leftBound = -0.1f;
                topBound = -1;
                rightBound = 0;
                bottomBound = 0;
                break;
            case SOUTH:
                leftBound = -1;
                topBound = -0.1f;
                rightBound = 0;
                bottomBound = 0;
                break;
            case WEST:
                leftBound = -1;
                topBound = -1;
                rightBound = -0.9f;
                bottomBound = 0;
                break;
        }

        RectF fRect = new RectF(
                (gridSystem.getObstacles().get(obstacleId).getxPosition() + leftBound) * this.cellSize, //left
                (gridSystem.getObstacles().get(obstacleId).getyPosition() + topBound) * this.cellSize, //top
                (gridSystem.getObstacles().get(obstacleId).getxPosition() + rightBound) * this.cellSize, //right
                (gridSystem.getObstacles().get(obstacleId).getyPosition() + bottomBound) * this.cellSize //bottom
        );
        canvas.drawRoundRect(
                fRect, // rect
                5F, // rx
                5F, // ry
                this.startPaintColor // Paint
        );
        this.invalidate();
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        if (!this.isSolving) {
            int y;
            int x;
            Obstacle t;
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    y = (int) (ceil(eventY / cellSize));
                    x = (int) (ceil(eventX / cellSize));
                    t = gridSystem.findObstacle(x, y);

                    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
                        public void onLongPress(MotionEvent e) {
                            dragCell(x, y, turn, 1);
                        }
                    });

                    this.turn = (t != null)
                            ? EXISTING_OBSTACLE
                            : ((x == this.gridSystem.getRobot().getRobotX() && y == this.gridSystem.getRobot().getRobotY()) ||
                            (x == this.gridSystem.getRobot().getRobotX() && y == this.gridSystem.getRobot().getRobotY()+1) ||
                            (x == this.gridSystem.getRobot().getRobotX() && y == this.gridSystem.getRobot().getRobotY()+2) ||
                            (x == this.gridSystem.getRobot().getRobotX()+1 && y == this.gridSystem.getRobot().getRobotY()) ||
                            (x == this.gridSystem.getRobot().getRobotX()+1 && y == this.gridSystem.getRobot().getRobotY()+1) ||
                            (x == this.gridSystem.getRobot().getRobotX()+1 && y == this.gridSystem.getRobot().getRobotY()+2) ||
                            (x == this.gridSystem.getRobot().getRobotX()-1 && y == this.gridSystem.getRobot().getRobotY()) ||
                            (x == this.gridSystem.getRobot().getRobotX()-1 && y == this.gridSystem.getRobot().getRobotY()+1) ||
                            (x == this.gridSystem.getRobot().getRobotX()-1 && y == this.gridSystem.getRobot().getRobotY()+2))
                            ? ROBOT
                            : NEW_OBSTACLE;

                    switch(this.turn) {
                        case EXISTING_OBSTACLE:
                            t.rotateObstacle(true);
                            gridSystem.setObstacleToEdit(t);
                            break;
                        case NEW_OBSTACLE:
                            return gestureDetector.onTouchEvent(event);
                        case ROBOT:
                            gridSystem.getRobot().rotateRobot(true);
                            break;
                    }

                    break;
                // MOVE CELL 1 by 1
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                    t = gridSystem.getObstacleToEdit();
                    y = (int) (ceil(eventY / cellSize));
                    x = (int) (ceil(eventX / cellSize));
                    this.dragCell(x, y, turn, 0);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (x != x0 || y != y0) {
                            x0 = x;
                            y0 = y;
                            int yPos = 21 - y0;
                            try {
                                int targetNo = t.getId() + 1;
                                Log.d("MapCanvas", "onTouchEvent: OBS" + targetNo + " moved to (" + x + ", " + yPos + ")\n");
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
            }

            this.invalidate();
        }


        return true;
    }

    private void dragCell(int x, int y, int turn, int firstTouch) throws ArrayIndexOutOfBoundsException {
        // MOVE BLUE START BLOCK
        boolean isTargetInGrid = x >= 1 && y >= 1 && x <= 20 && y <= 20;
        if (turn == ROBOT) {
            boolean isCarInGrid = x >= 1 && y >= 1 && x+1 <= 20 && y+1 <= 20;
            if (isCarInGrid && (this.gridSystem.getGrids()[x][y] == EMPTY_CELL_CODE)
                    && (this.gridSystem.getGrids()[x][y] != TARGET_CELL_CODE) && (this.gridSystem.getGrids()[x+1][y+1] != TARGET_CELL_CODE)
                    && (this.gridSystem.getGrids()[x][y+1] != TARGET_CELL_CODE) && (this.gridSystem.getGrids()[x+1][y] != TARGET_CELL_CODE)
            ) {

                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()][this.gridSystem.getRobot().getRobotY()] = EMPTY_CELL_CODE;
                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()][this.gridSystem.getRobot().getRobotY()+1] = EMPTY_CELL_CODE;
                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()][this.gridSystem.getRobot().getRobotY()-1] = EMPTY_CELL_CODE;
                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()+1][this.gridSystem.getRobot().getRobotY()] = EMPTY_CELL_CODE;
                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()+1][this.gridSystem.getRobot().getRobotY()+1] = EMPTY_CELL_CODE;
                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()+1][this.gridSystem.getRobot().getRobotY()-1] = EMPTY_CELL_CODE;
                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()-1][this.gridSystem.getRobot().getRobotY()] = EMPTY_CELL_CODE;
                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()-1][this.gridSystem.getRobot().getRobotY()+1] = EMPTY_CELL_CODE;
                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()+1][this.gridSystem.getRobot().getRobotY()-1] = EMPTY_CELL_CODE;

                this.gridSystem.getRobot().setRobotX(x);
                this.gridSystem.getRobot().setRobotY(y);

                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()][this.gridSystem.getRobot().getRobotY()] = CAR_CELL_CODE;
                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()][this.gridSystem.getRobot().getRobotY()+1] = CAR_CELL_CODE;
                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()][this.gridSystem.getRobot().getRobotY()-1] = CAR_CELL_CODE;
                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()+1][this.gridSystem.getRobot().getRobotY()] = CAR_CELL_CODE;
                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()+1][this.gridSystem.getRobot().getRobotY()+1] = CAR_CELL_CODE;
                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()+1][this.gridSystem.getRobot().getRobotY()-1] = CAR_CELL_CODE;
                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()-1][this.gridSystem.getRobot().getRobotY()] = CAR_CELL_CODE;
                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()-1][this.gridSystem.getRobot().getRobotY()+1] = CAR_CELL_CODE;
                this.gridSystem.getGrids()[this.gridSystem.getRobot().getRobotX()-1][this.gridSystem.getRobot().getRobotY()-1] = CAR_CELL_CODE;
                Log.d("MapCanvas", "dragCell: " + gridSystem.getGrids().length +" "+ (gridSystem.getRobot().getRobotX()) + " " + (gridSystem.getRobot().getRobotY()));
            }

        } else if (turn == EXISTING_OBSTACLE) {
            // MOVE RED TARGET BLOCK TO EMPTY CELL BUT AVOID CAR CELL
            if (isTargetInGrid && (this.gridSystem.getGrids()[x][y] == EMPTY_CELL_CODE)
                    && (this.gridSystem.getGrids()[x][y] != CAR_CELL_CODE)
            ) {
                Obstacle t = gridSystem.getObstacleToEdit();
                this.gridSystem.getGrids()[t.getxPosition()][t.getyPosition()] = EMPTY_CELL_CODE;
                t.setxPosition(x);
                t.setyPosition(y);
                this.gridSystem.getGrids()[t.getxPosition()][t.getyPosition()] = TARGET_CELL_CODE;

            } else if (!isTargetInGrid) {
                Obstacle t = this.gridSystem.getObstacleToEdit();
                if (gridSystem.getObstacles().contains(t)){
                    gridSystem.deleteObstacle(t);
                    int targetNo = t.getId() + 1;
                    Log.d("MapCanvas", "dragCell: OBS" + targetNo + " deleted.\n");
                }
                this.gridSystem.getGrids()[t.getxPosition()][t.getyPosition()] = EMPTY_CELL_CODE;

            }
        } else if (turn == NEW_OBSTACLE) {
            if (isTargetInGrid && (this.gridSystem.getGrids()[y][x] == EMPTY_CELL_CODE)) {

                Obstacle t = new Obstacle(x, y, gridSystem.getObstacles().size());
                this.gridSystem.getGrids()[t.getxPosition()][t.getyPosition()] = TARGET_CELL_CODE;
                gridSystem.getObstacles().add(t);
                int yPos = 21 - y;
                int targetNo = t.getId() + 1;
                Log.d("MapCanvas", "dragCell: OBS" + targetNo +" created at (" + x + ", " + yPos + ")\n");
            }
        }
    }

    public final void setSolving(boolean flag) {
        this.isSolving = flag;
    }
}


