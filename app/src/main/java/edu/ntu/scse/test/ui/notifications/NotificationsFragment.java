package edu.ntu.scse.test.ui.notifications;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ntu.scse.test.R;
import edu.ntu.scse.test.databinding.FragmentNotificationsBinding;
import edu.ntu.scse.test.ui.home.modal.GridSystem;
import edu.ntu.scse.test.ui.home.modal.Obstacle;
import edu.ntu.scse.test.ui.home.modal.Robot;

public class NotificationsFragment extends Fragment {
    private TableLayout tableLayout;
    private GridSystem gridSystem;
    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        tableLayout = root.findViewById(R.id.gridSystem);
        gridSystem = new GridSystem();  // assuming you've defined GridSystem

        populateGrid();

        return root;
    }
    private void populateGrid() {
        int[][] grids = gridSystem.getGrids();

        for (int i = 0; i < grids.length; i++) {
            TableRow tableRow = new TableRow(getContext());

            for (int j = 0; j < grids[i].length; j++) {
                View cell = new View(getContext());
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1.0f);
                layoutParams.setMargins(1, 1, 1, 1);  // Add a margin to create a grid effect
                cell.setLayoutParams(layoutParams);
                cell.setMinimumHeight(20);  // Set a minimum height
                cell.setMinimumWidth(20);  // Set a minimum width

                switch (grids[i][j]) {
                    case GridSystem.EMPTY_CELL_CODE:
                        cell.setBackgroundColor(Color.WHITE);
                        break;
                    case GridSystem.CAR_CELL_CODE:
                        cell.setBackgroundColor(Color.BLUE);
                        break;
                    // add other cell code cases

                    default:
                        cell.setBackgroundColor(Color.WHITE);
                        break;
                }

                tableRow.addView(cell);
            }

            tableLayout.addView(tableRow);
        }
    }

    public void addCarToGrid(Robot robot) {
        int x = robot.getRobotX();
        int y = robot.getRobotY();

        View cell = tableLayout.getChildAt(x).findViewById(y);
        cell.setBackgroundColor(Color.BLUE);
        // You can replace the above line with custom view representing the car.

        gridSystem.getRobot().setRobotX(x);
        gridSystem.getRobot().setRobotY(y);
        gridSystem.getRobot().setFacing(robot.getFacing());
    }

    public void addObstacleToGrid(Obstacle obstacle) {
        int x = obstacle.getxPosition();
        int y = obstacle.getyPosition();

        View cell = tableLayout.getChildAt(x).findViewById(y);
        cell.setBackgroundColor(Color.RED);
        // You can replace the above line with custom view representing the obstacle.

        gridSystem.getObstacles().add(obstacle);
    }

    public void rotateCar(boolean clockwise) {
        gridSystem.getRobot().rotateRobot(clockwise);
        // Update the car's image or custom view according to the new facing direction
    }

    public void rotateObstacle(Obstacle obstacle, boolean clockwise) {
        obstacle.rotateObstacle(clockwise);
        // Update the obstacle's image or custom view according to the new facing direction
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}