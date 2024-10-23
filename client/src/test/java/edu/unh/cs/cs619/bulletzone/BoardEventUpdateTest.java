package edu.unh.cs.cs619.bulletzone;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import android.content.Context;
import android.widget.GridView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import edu.unh.cs.cs619.bulletzone.events.GameEvent;
import edu.unh.cs.cs619.bulletzone.events.GameEventProcessor;
import edu.unh.cs.cs619.bulletzone.events.MoveEvent;
import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;

public class BoardEventUpdateTest {
    @Mock
    EventBus mockEB;

    @Mock
    BulletZoneRestClient mockRestClient;

    @Mock
    GridView mockGV;

    @Mock
    TextView mockTV;

    @Mock
    Context mockContext;

    @Mock
    GridAdapter mockAdapter;

    GameEventProcessor testEventProcessor;

    @Before
    public void setup() {
        initMocks(this);
        when(mockGV.getContext()).thenReturn(mockContext);

        int[][] initialGrid = {
                {0, 1000, 1000, 1000, 0, 1000, 0, 1000, 1000, 1000, 0, 0, 0, 0, 0, 0},
                {0, 1000, 0, 0, 0, 1000, 0, 1000, 1000, 1000, 10010746, 0, 0, 0, 0, 0},
                {0, 0, 1000, 1000, 0, 1000, 0, 1000, 1000, 1000, 0, 0, 0, 9999610, 0, 0},
                {0, 0, 1000, 1000, 0, 1000, 0, 0, 2003, 1000, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 10021006, 1000, 0, 0, 0, 0, 3000, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2002, 0, 0, 0},
                {0, 2002, 0, 0, 0, 0, 0, 0, 0, 0, 2002, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 2003, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1001704, 0, 0, 0, 999736, 0, 0, 0, 0, 1000502, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 2003, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 3000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3000},
                {0, 0, 0, 3000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };

        testEventProcessor = new GameEventProcessor();
        testEventProcessor.eb = mockEB;

        testEventProcessor.setBoard(initialGrid);
    }

    @Test
    public void gameEventProcessor_onStartCalled_registersToEventBus() {
        testEventProcessor.start();
        verify(mockEB).register(testEventProcessor);
    }

    @Test
    public void gameEventProcessor_onMoveEvent_changesBoard() {
//        mockEventProcessor.start();
//
//        MoveEvent move = new MoveEvent();
//        mockEB.post(move);
//
//        verify(mockEventProcessor).onNewEvent(move);
    }

}
