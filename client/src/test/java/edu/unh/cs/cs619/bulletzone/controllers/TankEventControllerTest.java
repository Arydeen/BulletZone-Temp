package edu.unh.cs.cs619.bulletzone.controllers;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.View;
import android.widget.Button;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import edu.unh.cs.cs619.bulletzone.ClientActivity;
import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.TankEventController;

@RunWith(MockitoJUnitRunner.class)
public class TankEventControllerTest {

    private ClientActivity clientActivity;
    @Mock
    private TankEventController tankEventControllerMock;
    @Mock
    private View buttonUpMock;
    @Mock
    private View buttonRightMock;
    @Mock
    private View buttonDownMock;
    @Mock
    private View buttonLeftMock;

    @Before
    public void setUp() {

    clientActivity = new ClientActivity();

    clientActivity.setTankEventController(tankEventControllerMock);

    when(buttonUpMock.getId()).thenReturn(R.id.buttonUp);
    when(buttonRightMock.getId()).thenReturn(R.id.buttonRight);
    when(buttonDownMock.getId()).thenReturn(R.id.buttonDown);
    when(buttonLeftMock.getId()).thenReturn(R.id.buttonLeft);

    }

    @Test
    public void testMoveAsyncUp() {
        clientActivity.moveTest(buttonUpMock);
        verify(tankEventControllerMock).moveAsync(anyLong(), eq((byte) 0));
    }

    @Test
    public void testMoveAsyncRight() {
        clientActivity.moveTest(buttonRightMock);
        verify(tankEventControllerMock).moveAsync(anyLong(), eq((byte) 2));
    }

    @Test
    public void testMoveAsyncDown() {
        clientActivity.moveTest(buttonDownMock);
        verify(tankEventControllerMock).moveAsync(anyLong(), eq((byte) 4));
    }

    @Test
    public void testMoveAsyncLeft() {
        clientActivity.moveTest(buttonLeftMock);
        verify(tankEventControllerMock).moveAsync(anyLong(), eq((byte) 6));
    }

    @Test
    public void testFireAsync() {
        clientActivity.fireTest();
        verify(tankEventControllerMock).fire(anyLong());
    }

    @Test
    public void testTurnAsync() {
        clientActivity.moveTest(buttonUpMock);
        verify(tankEventControllerMock).moveAsync(anyLong(), eq((byte) 0));
        clientActivity.moveTest(buttonRightMock);
        verify(tankEventControllerMock).turnAsync(anyLong(), eq((byte) 2));
    }

}
