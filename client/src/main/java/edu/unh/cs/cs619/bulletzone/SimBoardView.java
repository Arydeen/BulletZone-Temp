package edu.unh.cs.cs619.bulletzone;

import android.widget.GridView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import edu.unh.cs.cs619.bulletzone.model.SimulationBoard;
import edu.unh.cs.cs619.bulletzone.rest.GridUpdateEvent;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

@EBean
public class SimBoardView {
    private final SimulationBoard simBoard = new SimulationBoard(16,16);

    @Bean
    protected GridAdapter adapter;

    public Object gridEventHandler = new Object() {
        @Subscribe
        public void onUpdateGrid(GridUpdateEvent event) {
            updateGrid(event.gw, event.tw);
        }
    };

    public void updateGrid(GridWrapper gw, GridWrapper tw) {
        adapter.updateList(gw.getGrid(), tw.getGrid());
    }

    public void attach(GridView gView, Long tankID) {
        adapter.setSimBoard(simBoard);
        adapter.setTankId(tankID);
        gView.setAdapter(adapter);
        EventBus.getDefault().register(gridEventHandler);
    }

    public void detach() {
        EventBus.getDefault().unregister(gridEventHandler);
    }

}
