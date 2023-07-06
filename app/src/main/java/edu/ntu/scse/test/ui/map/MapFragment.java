package edu.ntu.scse.test.ui.map;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Objects;

import edu.ntu.scse.test.R;
import edu.ntu.scse.test.databinding.FragmentMapBinding;

public class MapFragment extends Fragment {
    private FragmentMapBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MapViewModel mapViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.view.setOnLongClickListener((new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String clipText = "Obstacle 1";
                ClipData.Item item = new ClipData.Item(clipText);

                ClipData dragData = new ClipData(
                        clipText,
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                        item);

                View.DragShadowBuilder dragShadow = new View.DragShadowBuilder(view);
                view.startDragAndDrop(dragData, dragShadow, view, 0);
                return true;
            }
        }));

        GridLayout gridLayout = binding.gridLayout;
        for (int i = 0; i < gridLayout.getRowCount(); i++) {
            for (int j = 0; j < gridLayout.getColumnCount(); j++) {
                LinearLayout view = new LinearLayout(getContext());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = params.height = 0;
                params.columnSpec = GridLayout.spec(j, 1, 1f);
                params.rowSpec = GridLayout.spec(i, 1, 1f);
                view.setLayoutParams(params);
                view.setBackgroundColor(Color.WHITE);
                view.setPadding(5,5,5,5);
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cell_border));
                gridLayout.addView(view);

                view.setOnDragListener((v, e) -> {
                    switch (e.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            return e.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
                        case DragEvent.ACTION_DRAG_ENTERED:
                            v.setBackgroundColor(Color.GRAY);
                            v.invalidate();
                            return true;
                        case DragEvent.ACTION_DRAG_LOCATION:
                            return true;
                        case DragEvent.ACTION_DRAG_EXITED:
                            v.setBackgroundColor(Color.WHITE);
                            v.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cell_border));
                            v.invalidate();
                            return true;
                        case DragEvent.ACTION_DRAG_ENDED:
                            v.invalidate();
                            return true;
                        case DragEvent.ACTION_DROP:
                            View localState = (View) e.getLocalState();
                            ViewGroup owner = (ViewGroup) localState.getParent();
                            owner.removeView(localState);
                            LinearLayout destination = (LinearLayout) v;
                            destination.addView(localState);
                            return true;
                    }
                    return false;
                });
            }
        }


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
