package edu.unh.cs.cs619.bulletzone.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.events.UpdateBoardEvent;
import edu.unh.cs.cs619.bulletzone.model.BoardCell;
import edu.unh.cs.cs619.bulletzone.model.SimulationBoard;
import edu.unh.cs.cs619.bulletzone.model.TankItem;
import edu.unh.cs.cs619.bulletzone.model.TurnableGoblin;

@EBean
public class GridAdapter extends BaseAdapter {

    private final Object monitor = new Object();
    @SystemService
    protected LayoutInflater inflater;
    private int[][] mEntities = new int[16][16];
    private SimulationBoard simBoard;
    public boolean isUpdated = false;
    private long tankId = -1;

    /**
     * Updates the entities array of new input after events have changed it from the server
     *
     * @param entities Game board array
     */
    public void updateList(int[][] entities) {
        synchronized (monitor) {
            // Add debug logging
            boolean foundPowerUp = false;
            for (int i = 0; i < entities.length; i++) {
                for (int j = 0; j < entities[i].length; j++) {
                    if (entities[i][j] >= 3000 && entities[i][j] <= 3003) {
                        Log.d("GridAdapter", "Power-up found in update: " + entities[i][j]);
                        Log.d("GridAdapter", "Position: [" + i + "," + j + "]");
                        foundPowerUp = true;
                    }
                }
            }
            if (!foundPowerUp) {
                Log.d("GridAdapter", "No power-ups found in update");
            }

            this.mEntities = entities;
            this.notifyDataSetChanged();
            simBoard.setUsingBoard(mEntities);
            this.isUpdated = true;
        }
    }

    @AfterInject
    protected void afterInject() {
        EventBus.getDefault().register(this);
    }

    /**
     * Subscribes to changes from events form the UpdateBoardEvent and updates the view
     *
     * @param event New event made to the board
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleUpdate(UpdateBoardEvent event) {
        this.notifyDataSetChanged();
        simBoard.setUsingBoard(mEntities); // Updates simulation board when events are posted
        this.isUpdated = true;
    }

    public void setSimBoard(SimulationBoard board) {
        this.simBoard = board;
    }

    public int[][] getBoard() {
        return mEntities;
    }

    @Override
    public int getCount() {
        return 16 * 16;
    }

    @Override
    public Object getItem(int position) {
        return mEntities[(int) position / 16][position % 16];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setTankId(long tankId) {
        this.tankId = tankId;
    }


    /**
     * Updates the desired cell from events in the gridView, using SimulationBoard's Board Cells
     *
     * @param position    The position in the SimulationBoard to be updated
     * @param convertView The view to be updated, it is gridView
     * @param parent      The parent activity of the view, Client Activity
     * @return Returns the updated view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new GridView.LayoutParams(50, 50));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(1, 1, 1, 1);
        } else {
            imageView = (ImageView) convertView;
        }

        if (this.isUpdated) {
            int value = mEntities[position / 16][position % 16];
            BoardCell currCell = simBoard.getCell(position);

            // Handle power-ups
            if (value >= 3000 && value <= 3003) {
                Log.d("GridAdapter", "Found power-up at position " + position);
                Log.d("GridAdapter", "Power-up value: " + value);
                Log.d("GridAdapter", "Power-up type: " + (value - 3000));

                switch (value - 3000) {
                    case 1:
                        Log.d("GridAdapter", "Setting Thingamajig icon");
                        imageView.setImageResource(R.drawable.thingamajig_icon);
                        break;
                    case 2:
                        Log.d("GridAdapter", "Setting AntiGrav icon");
                        imageView.setImageResource(R.drawable.anti_grav_icon);
                        break;
                    case 3:
                        Log.d("GridAdapter", "Setting FusionReactor icon");
                        imageView.setImageResource(R.drawable.fusion_reactor_icon);
                        break;
                    default:
                        imageView.setImageResource(R.drawable.blank);
                }
            }
            // Handle tanks
            else if (currCell.getCellType().equals("Tank")) {
                int tankIdTest = (currCell.getRawValue() / 10000) - 1000;
                if (tankIdTest == this.tankId) {
                    imageView.setImageResource(R.drawable.small_goblin_red);
                } else {
                    imageView.setImageResource(currCell.getResourceID());
                }
            }
            // Handle all other cells
            else {
                imageView.setImageResource(currCell.getResourceID());
            }

            imageView.setRotation(currCell.getRotation());
            Log.d("GridAdapter", "Position " + position + " value: " + currCell.getRawValue());
        } else {
            imageView.setImageResource(R.drawable.blank);
        }

        return imageView;
    }
}