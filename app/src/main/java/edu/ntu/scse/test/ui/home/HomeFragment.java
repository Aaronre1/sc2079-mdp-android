package edu.ntu.scse.test.ui.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import edu.ntu.scse.test.MainActivity;
import edu.ntu.scse.test.R;

public class HomeFragment extends Fragment {

    private GridLayout gridLayout;
    private int gridSize = 20;
    private boolean[][] obstacles;
    private ImageView[][] imageViews;
    private ImageView[][] carImageViews = new ImageView[3][3];
    private int[] carCoordinates = new int[2];
    private ImageView carImageView;
    private int selectedDirection = 0; // 0 - Up, 1 - Right, 2 - Down, 3 - Left
    private boolean addObstacleActive = false; // Track the active state of add obstacle button
    private boolean removeObstacleActive = false; // Track the active state of remove obstacle button
    private EditText receivedDataTextArea;
    private static final String TAG = "HomeFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final EditText sendDataTextArea = root.findViewById(R.id.sendDataTextArea);
        final LinearLayout homeLayout = root.findViewById(R.id.home_layout);
        gridLayout = root.findViewById(R.id.gridLayout);
        final Button addObstacleButton = root.findViewById(R.id.addObstacleButton);
        final Button removeObstacleButton = root.findViewById(R.id.removeObstacleButton);
        final Button placeCarButton = root.findViewById(R.id.placeCarButton);
        final Button rotateButton = root.findViewById(R.id.rotateButton);
        final Button sendDataButton = root.findViewById(R.id.sendDataButton);
        receivedDataTextArea = root.findViewById(R.id.receivedDataTextArea);

        obstacles = new boolean[gridSize][gridSize];
        imageViews = new ImageView[gridSize][gridSize];

        gridLayout.setColumnCount(gridSize);
        gridLayout.setRowCount(gridSize);

        // Set GridLayout background color to black
        gridLayout.setBackgroundColor(Color.BLACK);

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                ImageView imageView = new ImageView(getContext());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = 0;
                params.columnSpec = GridLayout.spec(j, 1, 1f);
                params.rowSpec = GridLayout.spec(i, 1, 1f);
                imageView.setLayoutParams(params);
                imageView.setBackgroundColor(Color.WHITE);
                imageView.setPadding(5, 5, 5, 5);
                imageView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cell_border));
                imageViews[i][j] = imageView;
                gridLayout.addView(imageView);
            }
        }

        addObstacleButton.setOnClickListener(view -> {
            if(!removeObstacleActive){
                addObstacleActive = !addObstacleActive;
                updateButtonState(addObstacleButton, addObstacleActive);
            }else{
                Toast.makeText(getContext(), "Remove Obstacle is in Active state", Toast.LENGTH_SHORT).show();
            }

        });
        removeObstacleButton.setOnClickListener(view -> {
            if(!addObstacleActive) {
                removeObstacleActive = !removeObstacleActive;
                updateButtonState(removeObstacleButton, removeObstacleActive);
            }else{
                Toast.makeText(getContext(), "Add obstacles is in Active state", Toast.LENGTH_SHORT).show();
            }
        });

        gridLayout.setOnTouchListener((view, motionEvent) -> {
            if (addObstacleActive) {
                int row = Math.min(gridSize - 1, (int) (motionEvent.getY() / view.getHeight() * gridSize));
                int col = Math.min(gridSize - 1, (int) (motionEvent.getX() / view.getWidth() * gridSize));
                addObstacle(row, col);
                return true;
            } else if (removeObstacleActive) {
                int row = Math.min(gridSize - 1, (int) (motionEvent.getY() / view.getHeight() * gridSize));
                int col = Math.min(gridSize - 1, (int) (motionEvent.getX() / view.getWidth() * gridSize));
                removeObstacle(row, col);
                return true;
            }
            return false;
        });

        placeCarButton.setOnClickListener(view -> placeCar());
        rotateButton.setOnClickListener(view -> rotateCar());

        // Set up touch listener for non-text box views to hide keyboard.
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

    private void addObstacle(int row, int col) {
        if (obstacles[row][col]) {
            Toast.makeText(getContext(), "Obstacle already exists at (" + row + ", " + col + ")", Toast.LENGTH_SHORT).show();
            return;
        }

        obstacles[row][col] = true;
        imageViews[row][col].setSelected(true); // sets the obstacle color
        Toast.makeText(getContext(), "Obstacle added at (" + row + ", " + col + ")", Toast.LENGTH_SHORT).show();
    }

    private void removeObstacle(int row, int col) {
        if (!obstacles[row][col]) {
            Toast.makeText(getContext(), "No obstacle exists at (" + row + ", " + col + ")", Toast.LENGTH_SHORT).show();
            return;
        }

        obstacles[row][col] = false;
        imageViews[row][col].setSelected(false); // sets the default color
        Toast.makeText(getContext(), "Obstacle removed at (" + row + ", " + col + ")", Toast.LENGTH_SHORT).show();
    }

    private void updateButtonState(Button button, boolean isActive) {
        if (isActive) {
            button.setBackgroundColor(Color.LTGRAY);
        } else {
            button.setBackgroundColor(Color.BLUE);
        }
    }

    private void placeCar() {
        if (carImageViews[0][0] != null) {
            Toast.makeText(getContext(), "Car already placed", Toast.LENGTH_SHORT).show();
            return;
        }

        outerloop:
        for (int i = 0; i < gridSize - 2; i++) {
            for (int j = 0; j < gridSize - 2; j++) {
                // Check for 3x3 free cells
                boolean freeCells = true;
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        if (obstacles[i + k][j + l]) {
                            freeCells = false;
                            break;
                        }
                    }
                    if (!freeCells) {
                        break;
                    }
                }
                // If freeCells is true, then we found a 3x3 free area, place the car here
                if (freeCells) {
                    for (int k = 0; k < 3; k++) {
                        for (int l = 0; l < 3; l++) {
                            carImageViews[k][l] = imageViews[i + k][j + l];
                            carImageViews[k][l].setImageResource(R.drawable.baseline_directions_car_24);

                            switch (selectedDirection) {
                                case 0:
                                    carImageViews[k][l].setRotation(0);
                                    break;
                                case 1:
                                    carImageViews[k][l].setRotation(90);
                                    break;
                                case 2:
                                    carImageViews[k][l].setRotation(180);
                                    break;
                                case 3:
                                    carImageViews[k][l].setRotation(270);
                                    break;
                            }
                        }
                    }
                    // Save the top-left corner of the car
                    carCoordinates[0] = i;
                    carCoordinates[1] = j;

                    Toast.makeText(getContext(), "Car placed at (" + i + ", " + j + ")", Toast.LENGTH_SHORT).show();
                    break outerloop;
                }
            }
        }

        // If no empty cell found, display a message
        Toast.makeText(getContext(), "No empty cell to place the car", Toast.LENGTH_SHORT).show();
    }


    private void rotateCar() {
        selectedDirection = (selectedDirection + 1) % 4;
        Toast.makeText(getContext(), "Car direction changed", Toast.LENGTH_SHORT).show();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public void updateReceivedData(String data){
        Log.i(TAG,"Data >>> " +data);
        receivedDataTextArea.setText(data);
    }

}
