package edu.ntu.scse.test.ui.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import edu.ntu.scse.test.MainActivity;
import edu.ntu.scse.test.R;
import edu.ntu.scse.test.ui.home.modal.Car;
import edu.ntu.scse.test.ui.home.modal.Obstacle;

public class HomeFragment extends Fragment {

    private GridLayout gridLayout;
    private int gridSize = 20, obstaclesSize = 0;
    private ImageView[][] imageViews;
    private ImageView carImageView;
    private boolean placeCarActive = false, addObstacleActive = false, removeObstacleActive = false,
            dragObstacleActive = false, rotateObstacleActive = false;
    private EditText receivedDataTextArea, statusTextArea;
    private static final String TAG = "HomeFragment";
    private ArrayList<Obstacle> obstacles;
    private Obstacle currentObstacle = null;
    private Car myCar;
    private FrameLayout[][] frameLayouts;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final EditText sendDataTextArea = root.findViewById(R.id.sendDataTextArea);
        final LinearLayout homeLayout = root.findViewById(R.id.home_layout);
        gridLayout = root.findViewById(R.id.gridLayout);
        final Button addObstacleButton = root.findViewById(R.id.addObstacleButton);
        final Button removeButton = root.findViewById(R.id.removeButton);
        final Button placeCarButton = root.findViewById(R.id.placeCarButton);
        final Button rotateButton = root.findViewById(R.id.rotateButton);
        final Button sendDataButton = root.findViewById(R.id.sendDataButton);
        final Button dragObstacleButton = root.findViewById(R.id.dragObstacleButton);
        final ImageView upArrowButton = root.findViewById(R.id.upArrow);
        final ImageView downArrowButton = root.findViewById(R.id.downArrow);
        final ImageView leftArrowButton = root.findViewById(R.id.leftArrow);
        final ImageView rightArrowButton = root.findViewById(R.id.rightArrow);

        receivedDataTextArea = root.findViewById(R.id.receivedDataTextArea);
        statusTextArea = root.findViewById(R.id.robotStatusTextArea);

        obstacles = new ArrayList<>();
        imageViews = new ImageView[gridSize][gridSize];
        frameLayouts = new FrameLayout[gridSize][gridSize];
        gridLayout.setColumnCount(gridSize);
        gridLayout.setRowCount(gridSize);

        for (int i = gridSize - 1; i >= 0; i--) {
            for (int j = 0; j < gridSize; j++) {
                //ImageView imageView = new ImageView(getContext());
                FrameLayout frameLayout = new FrameLayout(getContext());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = 0;
                params.columnSpec = GridLayout.spec(j, 1, 1f);
                params.rowSpec = GridLayout.spec(i, 1, 1f);
                frameLayout.setLayoutParams(params);
                frameLayout.setBackgroundColor(Color.WHITE);

                ImageView imageView = new ImageView(getContext());
                imageView.setPadding(0, 1, 0, 1);
                imageView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cell_border));

                TextView textView = new TextView(getContext());
                textView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
                textView.setVisibility(View.INVISIBLE);
                frameLayout.addView(imageView);
                frameLayout.addView(textView);

                frameLayouts[gridSize - 1 - i][j] = frameLayout;
                gridLayout.addView(frameLayout);
                imageViews[gridSize - 1 - i][j] = imageView;
            }
        }


        addObstacleButton.setOnClickListener(view -> {
            if(!removeObstacleActive && !dragObstacleActive && !placeCarActive && !rotateObstacleActive){
                addObstacleActive = !addObstacleActive;
                updateButtonState(addObstacleButton, addObstacleActive);
            }else{
                Toast.makeText(getContext(), "Only one button is allowed to be active.", Toast.LENGTH_SHORT).show();
            }

        });

        dragObstacleButton.setOnClickListener(view -> {
            if(!removeObstacleActive && !addObstacleActive && !placeCarActive && !rotateObstacleActive) {
                dragObstacleActive = !dragObstacleActive;
                updateButtonState(dragObstacleButton, dragObstacleActive);
            }else{
                Toast.makeText(getContext(), "Only one button is allowed to be active.", Toast.LENGTH_SHORT).show();
            }
        });

        removeButton.setOnClickListener(view -> {
            if(!addObstacleActive && !dragObstacleActive && !placeCarActive && !rotateObstacleActive) {
                removeObstacleActive = !removeObstacleActive;
                updateButtonState(removeButton, removeObstacleActive);
            }else{
                Toast.makeText(getContext(), "Only one button is allowed to be active.", Toast.LENGTH_SHORT).show();
            }
        });

        rotateButton.setOnClickListener(view -> {
            if(!removeObstacleActive && !addObstacleActive && !dragObstacleActive && !placeCarActive){
                rotateObstacleActive = !rotateObstacleActive;
                updateButtonState(rotateButton, rotateObstacleActive);
            }else{
                Toast.makeText(getContext(), "Only one button is allowed to be active.", Toast.LENGTH_SHORT).show();
            }
        });

        placeCarButton.setOnClickListener(view -> {
            if (!removeObstacleActive && !addObstacleActive && !dragObstacleActive && !rotateObstacleActive) {
                placeCarActive = !placeCarActive;
                updateButtonState(placeCarButton, placeCarActive);
            } else {
                Toast.makeText(getContext(), "Only one button is allowed to be active.", Toast.LENGTH_SHORT).show();
            }
        });

        upArrowButton.setOnClickListener(view -> {
            if(MainActivity.bluetoothClient != null){
                MainActivity.bluetoothClient.sendData("Arrow Up");
            }
            if(myCar!=null) {
                moveCarUp();
            }
        });
        downArrowButton.setOnClickListener(view -> {
            if(MainActivity.bluetoothClient != null) {
                MainActivity.bluetoothClient.sendData("Arrow Down");
            }
            if(myCar!=null) {
                moveCarDown();
            }
        });
        leftArrowButton.setOnClickListener(view -> {
            if(MainActivity.bluetoothClient != null){
                MainActivity.bluetoothClient.sendData("Arrow Left");
            }
            if(myCar!=null) {
                moveCarLeft();
            }
        });
        rightArrowButton.setOnClickListener(view -> {
            if(MainActivity.bluetoothClient != null){
                MainActivity.bluetoothClient.sendData("Arrow Right");
            }
            if(myCar!=null){
                moveCarRight();
            }

        });

        gridLayout.setOnTouchListener((view, motionEvent) -> {
            //int col = Math.min(gridSize - 1, (int) (motionEvent.getX() / view.getWidth() * gridSize));
            //int row = Math.max(-10, Math.min(gridSize - 1, (int) (motionEvent.getY() / view.getHeight() * gridSize)));
            int col = Math.min(gridSize - 1, (int) (motionEvent.getX() / view.getWidth() * gridSize));
            int row = Math.min(gridSize - 1, gridSize - 1 - (int) (motionEvent.getY() / view.getHeight() * gridSize));

            Log.d(TAG,"row >>> " +row);
            Log.d(TAG,"col >>> " +col);
            //drag up of the grid and remove
            if(row<0){
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_MOVE:
                        if (dragObstacleActive) {
                            Log.d(TAG, "CURRENT OBS: " +currentObstacle);
                                if(currentObstacle!=null){
                                    removeObstacle(currentObstacle.getRow(),currentObstacle.getCol());
                                }
                            }
                            return true;
                        }
                }

            if(row >= 0 && col >= 0) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (addObstacleActive) {
                            if (isCellOccupiedByCar(row, col)) {
                                Toast.makeText(getContext(), "This cell is occupied by the car.", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            if(obstacles.size()>0){
                                for (Obstacle obstacle : obstacles){
                                    if(obstacle.getRow() == row && obstacle.getCol() == col){
                                        Toast.makeText(getContext(), "This cell is occupied by an obstacle.", Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                }
                            }
                            Obstacle obstacle = new Obstacle(row, col, obstaclesSize, Obstacle.NORTH);
                            obstacles.add(obstacle);
                            obstaclesSize = obstaclesSize + 1;
                            Log.i(TAG, "Obstacle size after add: " + obstaclesSize);
                            FrameLayout frameLayout = frameLayouts[obstacle.getRow()][obstacle.getCol()];
                            imageViews[obstacle.getRow()][obstacle.getCol()].setImageResource(getDrawableForDirection("obstacle", obstacle.getDirection()));
                            Log.i(TAG, "Obstacle Obj: " + obstacle.toString());

                            TextView textView = (TextView) frameLayout.getChildAt(1);  // get the TextView

                            if(obstacle != null && obstacle.getImageId() != 0){
                                textView.setText(String.valueOf(obstacle.getImageId()));
                            }else{
                                textView.setText(String.valueOf(obstacle.getId()));  // set the text
                            }

                            textView.setTextColor(Color.WHITE);
                            textView.setVisibility(View.VISIBLE);  // make it visible
                            if(MainActivity.bluetoothClient != null){
                                MainActivity.bluetoothClient.sendData("Obstacle: " +obstacle.toString());
                            }
                            return true;
                        } else if (removeObstacleActive) {
                            removeObstacle(row, col);
                            return true;
                        } else if (dragObstacleActive) {
                            Obstacle obstacle = getObstacleAt(row, col);
                            if (obstacle != null) {
                                currentObstacle = obstacle;
                                currentObstacle.isBeingDragged = true;
                                if(MainActivity.bluetoothClient != null){
                                    MainActivity.bluetoothClient.sendData("Drag Obstacle: " +currentObstacle.toString());
                                }
                            }
                            return true;
                        }else if (rotateObstacleActive) {
                            Obstacle obstacle = getObstacleAt(row, col);
                            if (obstacle != null) {
                                rotateObstacle(obstacle);
                                if(MainActivity.bluetoothClient != null){
                                    MainActivity.bluetoothClient.sendData("rotate obs: " +obstacle.toString());
                                }
                            }
                            Log.d(TAG,"ROW:: " +(myCar.getRow() + 3));
                            Log.d(TAG,"col:: " +(myCar.getCol() + 3));
                            if (myCar != null && row <= myCar.getRow() && row > myCar.getRow() - 3 && col >= myCar.getCol() && col < myCar.getCol() + 3) {
                                rotateCar();
                                if(MainActivity.bluetoothClient != null){
                                    MainActivity.bluetoothClient.sendData("myCar rotate >>> " +myCar.toString());
                                }
                                return true;
                            }
                            return true;
                        } else if (placeCarActive) {
                            placeCar(row, col, 0);//set default to north
                            return true;
                        }

                    case MotionEvent.ACTION_MOVE:
                        if (dragObstacleActive && currentObstacle != null) {
                            if (isCellOccupiedByCar(row, col)) {
                                Toast.makeText(getContext(), "This cell is occupied by the car.", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            Log.d(TAG,"current Obstacle: " +currentObstacle.toString());
                            if(MainActivity.bluetoothClient != null){
                                MainActivity.bluetoothClient.sendData("Obstacle dragged to: " +currentObstacle.toString());
                            }
                            // remove previous obstacle image and hide associated text view
                            FrameLayout oldFrameLayout = frameLayouts[currentObstacle.getRow()][currentObstacle.getCol()];
                            ImageView oldImageView = (ImageView) oldFrameLayout.getChildAt(0);  // get the ImageView
                            oldImageView.setImageResource(0);  // remove the image
                            TextView oldTextView = (TextView) oldFrameLayout.getChildAt(1);  // get the TextView
                            oldTextView.setVisibility(View.INVISIBLE);  // make it invisible

                            // check if new cell is occupied by another obstacle
                            Obstacle nextObstacle = getObstacleAt(row, col);
                            if(nextObstacle == null || nextObstacle == currentObstacle) {
                                currentObstacle.setRow(row);
                                currentObstacle.setCol(col);
                                if(isOutsideGrid(currentObstacle)) {
                                    removeObstacle(currentObstacle.getRow(), currentObstacle.getCol());
                                    Toast.makeText(getContext(), "Obstacle removed as it was dragged outside", Toast.LENGTH_SHORT).show();
                                    currentObstacle = null;
                                } else {
                                    FrameLayout newFrameLayout = frameLayouts[currentObstacle.getRow()][currentObstacle.getCol()];
                                    ImageView newImageView = (ImageView) newFrameLayout.getChildAt(0);  // get the ImageView
                                    newImageView.setImageResource(getDrawableForDirection("obstacle",currentObstacle.getDirection()));  // set the image

                                    TextView newTextView = (TextView) newFrameLayout.getChildAt(1);  // get the TextView

                                    if(currentObstacle != null && currentObstacle.getImageId() != 0){
                                        newTextView.setText(String.valueOf(currentObstacle.getImageId()));  // set the text
                                    }else{
                                        newTextView.setText(String.valueOf(currentObstacle.getId()));  // set the text
                                    }

                                    newTextView.setTextColor(Color.WHITE);
                                    newTextView.setVisibility(View.VISIBLE);  // make it visible
                                }
                            } else {
                                Toast.makeText(getContext(), "This cell is occupied by another obstacle.", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (dragObstacleActive && currentObstacle != null) {
                            if (isOutsideGrid(currentObstacle)) {
                                removeObstacle(currentObstacle.getRow(), currentObstacle.getCol());
                                currentObstacle = null;
                                Toast.makeText(getContext(), "Obstacle removed as it was released outside", Toast.LENGTH_SHORT).show();
                            }
                            if (currentObstacle != null) {
                                currentObstacle.isBeingDragged = false;
                                currentObstacle = null;
                            }
                        }
                        Log.i(TAG, "Last position: (" + row + "," + col + ")");
                        if(myCar != null){
                            Log.i("ACTION_UP", "Car: " + myCar.toString());
                        }
                        if(obstacles != null){
                            for(int i=0;i<obstacles.size();i++){
                                Log.i(TAG, "Obstacles: "+ i + " >>> " +obstacles.get(i).toString());
                            }

                        }

                        return true;
                }
            }
            return false;
        });

        homeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (sendDataTextArea.isFocused()) {
                    Rect outRect = new Rect();
                    sendDataTextArea.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                        sendDataTextArea.clearFocus();
                        hideKeyboardFrom(getContext(), v);
                    }
                }
                return false;
            }
        });

        sendDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataToSend = sendDataTextArea.getText().toString();
                if (!dataToSend.isEmpty() && MainActivity.bluetoothClient != null) {
                    MainActivity.bluetoothClient.sendData(dataToSend);
                    Toast.makeText(getContext(), "Send Data Success...", Toast.LENGTH_SHORT).show();
                    sendDataTextArea.getText().clear();
                }else{
                    Toast.makeText(getContext(), "Send Data Failed...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }
    private boolean isOutsideGrid(Obstacle obstacle) {
        return obstacle.getRow() < 0 || obstacle.getCol() < 0 || gridSize - 1 - obstacle.getRow() >= gridSize || obstacle.getCol() >= gridSize;
    }
    private Obstacle getObstacleAt(int row, int col) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getRow() == row && obstacle.getCol() == col) {
                return obstacle;
            }
        }
        return null;
    }

    private void removeObstacle(int row, int col) {
        Obstacle obstacle = getObstacleAt(row, col);
        Log.d(TAG,"checking..."+row);
        Log.d(TAG,"checking..."+col);
        Log.d(TAG,"checking..."+getObstacleAt(row, col));
        if (obstacle != null) {
            obstacles.remove(obstacle);
            imageViews[obstacle.getRow()][obstacle.getCol()].setImageResource(0); //remove
            FrameLayout frameLayout = frameLayouts[obstacle.getRow()][obstacle.getCol()];
            TextView textView = (TextView) frameLayout.getChildAt(1);  // get the TextView
            textView.setVisibility(View.INVISIBLE);  // make it invisible
            if(MainActivity.bluetoothClient != null){
                MainActivity.bluetoothClient.sendData("Obstacle removed: " +obstacle.toString());
            }
            currentObstacle = null;
        }
        if (myCar != null && row >= myCar.getRow() && row < myCar.getRow() + 3 && col >= myCar.getCol() && col < myCar.getCol() + 3) {
            if (carImageView != null) {
                gridLayout.removeView(carImageView);
                myCar = null;
            }
        }
    }

    private int getDrawableForDirection(String obj, int direction) {
        if(obj.equalsIgnoreCase("obstacle")){
            switch (direction) {
                case Obstacle.NORTH:
                    return R.drawable.obstacle_north;
                case Obstacle.EAST:
                    return R.drawable.obstacle_east;
                case Obstacle.SOUTH:
                    return R.drawable.obstacle_south;
                case Obstacle.WEST:
                    return R.drawable.obstacle_west;
                default:
                    return R.drawable.cell_border;
            }
        }else{
            switch (direction) {
                case Car.EAST:
                    return R.drawable.car_east;
                case Car.SOUTH:
                    return R.drawable.car_south;
                case Car.WEST:
                    return R.drawable.car_west;
                default:
                    return R.drawable.car_north;
            }
        }

    }
    private void rotateObstacle(Obstacle obstacle) {
        obstacle.rotate();
        imageViews[obstacle.getRow()][obstacle.getCol()].setImageResource(getDrawableForDirection("obstacle",obstacle.getDirection()));
    }
    private void placeCar(int row, int col, int direction) {
        Log.d("placeCar","row: " +row);
        Log.d("placeCar","col: " +col);
        if (row - 2 < 0 || col + 2 >= gridSize) {
            Toast.makeText(getContext(), "The car cannot be placed here.", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = row; i > row - 3; i--) {
            for (int j = col; j < col + 3; j++) {
                Obstacle obstacle = getObstacleAt(i, j);
                if (obstacle != null) {
                    Toast.makeText(getContext(), "The car cannot be placed on an obstacle.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        ImageView carView = new ImageView(getContext());
        myCar = new Car(row, col, direction);
        carView.setImageResource(getDrawableForDirection("car",myCar.getDirection()));
        carView.setBackgroundResource(R.drawable.cell_border);
        GridLayout.LayoutParams carLayoutParams = new GridLayout.LayoutParams();
        carLayoutParams.width = 0;
        carLayoutParams.height = 0;
        carLayoutParams.columnSpec = GridLayout.spec(col, 3, 1f);
        carLayoutParams.rowSpec = GridLayout.spec(gridSize - 1 - row, 3, 1f);
        carView.setLayoutParams(carLayoutParams);
        if (carImageView != null) {
            gridLayout.removeView(carImageView);
        }
        gridLayout.addView(carView);
        carImageView = carView;
        myCar.setRow(row);
        myCar.setCol(col);
        Log.d("placeCar2","placeCar: "+myCar.toString());
        if(MainActivity.bluetoothClient != null){
            MainActivity.bluetoothClient.sendData("placeCar: " +myCar.toString());
        }
    }
    private void rotateCar() {
        myCar.rotate();
        carImageView.setImageResource(getDrawableForDirection("car", myCar.getDirection()));
        if(MainActivity.bluetoothClient != null){
            MainActivity.bluetoothClient.sendData("rotateCar: " +myCar.toString());
        }
    }
    private void updateButtonState(Button button, boolean isActive) {
        if (isActive) {
            button.setBackgroundColor(Color.LTGRAY);
        } else {
            button.setBackgroundColor(Color.BLUE);
        }
    }
    private boolean isCellOccupiedByCar(int row, int col) {
        if (myCar != null) {
            for (int i = gridSize - 1 - myCar.getRow(); i > gridSize - 1 - myCar.getRow() - 3; i--) {
                for (int j = myCar.getCol(); j < myCar.getCol() + 3; j++) {
                    if (i == row && j == col) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void moveCarUp() {
        if (myCar.getRow() + 1 < gridSize && !isAreaOccupiedByObstacle(myCar.getRow()+1, myCar.getCol())) {
            placeCar(myCar.getRow() + 1, myCar.getCol(), myCar.getDirection());
        } else {
            Toast.makeText(getContext(), "The car cannot be moved up.", Toast.LENGTH_SHORT).show();
        }
        if(myCar != null){
            Log.i("moveCarUp", "Car: " + myCar.toString());
        }
    }

    private void moveCarDown() {
        if (myCar.getRow() - 1 >= 0 && !isAreaOccupiedByObstacle(myCar.getRow() - 1, myCar.getCol())) {
            placeCar(myCar.getRow() - 1, myCar.getCol(), myCar.getDirection());
        } else {
            Toast.makeText(getContext(), "The car cannot be moved down.", Toast.LENGTH_SHORT).show();
        }
        if(myCar != null){
            Log.i("moveCarDown", "Car: " + myCar.toString());
        }
    }

    private void moveCarLeft() {
        if (myCar.getCol() - 1 >= 0 && !isAreaOccupiedByObstacle(myCar.getRow(), myCar.getCol() - 1)) {
            placeCar(myCar.getRow(), myCar.getCol() - 1, myCar.getDirection());
        } else {
            Toast.makeText(getContext(), "The car cannot be moved left.", Toast.LENGTH_SHORT).show();
        }
        if(myCar != null){
            Log.i("moveCarLeft", "Car: " + myCar.toString());
        }
    }

    private void moveCarRight() {
        if (myCar.getCol() + 3 < gridSize && !isAreaOccupiedByObstacle(myCar.getRow(), myCar.getCol() + 1)) {
            placeCar(myCar.getRow(), myCar.getCol() + 1, myCar.getDirection());
        } else {
            Toast.makeText(getContext(), "The car cannot be moved right.", Toast.LENGTH_SHORT).show();
        }
        if(myCar != null){
            Log.i("moveCarRight", "Car: " + myCar.toString());
        }
    }

    private boolean isAreaOccupiedByObstacle(int row, int col) {
        // Check each cell in the 3x3 grid of the target area
        for (int i = row; i > row - 3; i--) {
            for (int j = col; j < col + 3; j++) {
                // If the cell is occupied by an obstacle, return true
                if (getObstacleAt(i, j) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateReceivedData(String data){
        String prefix = getTextBeforeColon(data);
        String[] splitString = null;
        Log.d(TAG,"prefix: " +prefix);
        if(!data.isEmpty() && data.contains(",")){
            splitString = data.split(",");
            //for loop for testing purpose...
            for (String aa : splitString) {
                Log.d(TAG,"splitString: " +aa.trim());
            }
        }

        switch (prefix) {
            case "status":
                //tested using "status:test"
                data = data.substring(data.indexOf(":") + 1).trim();
                statusTextArea.setText(data);
                break;
            case "robot":
                //tested using "robot:,5,6,0"
                if (splitString != null) {
                    int xCoor = Integer.parseInt(splitString[1].trim());
                    int yCoor = Integer.parseInt(splitString[2].trim());
                    int direction = Integer.parseInt(splitString[3].trim());
                    placeCar(xCoor,yCoor,direction);
                }
                break;
            case "obstacle":
                //tested using "obstacle:,1,6"
                //Displaying Image Target ID on Obstacle Blocks in the Map.
                //TARGET, <Obstacle Number>, <Target ID>”.
                //obstacle number == current obstacle ID
                //Target ID == imageId
                Log.d(TAG,"Received Data for obstacle: " +data);
                if (splitString != null) {
                    //missign rol,col,direction
                    int obstacleNum = Integer.parseInt(splitString[1].trim());
                    int tagetId = Integer.parseInt(splitString[2].trim());
                    //get obstacle based on the obstacleNum, set and display ImgId
                    Log.d(TAG,"obstacleNum: " +obstacleNum);
                    Log.d(TAG,"tagetId: " +tagetId);

                    for (Obstacle obstacle : obstacles) {
                        if (obstacle.getId() == obstacleNum) {
                            obstacle.setImageId(tagetId);

                            FrameLayout frameLayout = frameLayouts[obstacle.getRow()][obstacle.getCol()];
                            imageViews[obstacle.getRow()][obstacle.getCol()].setImageResource(getDrawableForDirection("obstacle", obstacle.getDirection()));
                            Log.i(TAG, "Obstacle Obj: " + obstacle.toString());
                            TextView textView = (TextView) frameLayout.getChildAt(1);  // get the TextView
                            textView.setText(String.valueOf(obstacle.getImageId()));
                            textView.setTextColor(Color.WHITE);
                            textView.setVisibility(View.VISIBLE);  // make it visible

                        }
                    }
                }
                break;
            default:
                receivedDataTextArea.setText(data);
        }
    }

    private String getTextBeforeColon(String input) {
        int colonIndex = input.indexOf(':');
        if (colonIndex != -1) {
            return input.substring(0, colonIndex).trim().toLowerCase();
        }
        return input;
    }

}
