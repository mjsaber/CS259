package com.example.necksnake;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Snake: a simple game that everyone can enjoy.
 * 
 * This is an implementation of the classic Game "Snake", in which you control a serpent roaming
 * around the garden looking for apples. Be careful, though, because when you catch one, not only
 * will you become longer, but you'll move faster. Running into yourself or the walls will end the
 * game.
 * 
 */
public class Snake extends Activity {

    /**
     * Constants for desired direction of moving the snake
     */
    public static int MOVE_LEFT = 0;
    public static int MOVE_UP = 1;
    public static int MOVE_DOWN = 2;
    public static int MOVE_RIGHT = 3;
    
	private static final String TAG = "";
    TextView myLabel;
    static TextView myTimer;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    InputStream mmInputStream;
    OutputStream mmOutputStream;
    Thread workerThread;
    Thread dataThread;
	String result;
    volatile boolean stopWorker = false;
	static long runningTime = 20000;

    private static String ICICLE_KEY = "snake-view";
    
    private SnakeView mSnakeView;
    public static CountDownTimer timer;
    /**
     * Called when Activity is first created. Turns off the title bar, sets up the content views,
     * and fires up the SnakeView.
     * 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	runningTime = 20000;
    	Intent intent = getIntent();
    	    	
        setContentView(R.layout.activity_snake);

        mSnakeView = (SnakeView) findViewById(R.id.snake);
        mSnakeView.setDependentViews((TextView) findViewById(R.id.text), findViewById(R.id.background));
        Button openButton = (Button)findViewById(R.id.open);
        Button closeButton = (Button)findViewById(R.id.close);

        myLabel = (TextView)findViewById(R.id.label);
        myTimer = (TextView)findViewById(R.id.timer);

        openButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try 
                {
                    findBT();
                    openBT();
                }
                catch (IOException ex) { }
            }
        });
       
        closeButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try 
                {
                    closeBT();
                }
                catch (IOException ex) { }
            }
        });
        if (savedInstanceState == null) {
            // We were just launched -- set up a new game
            mSnakeView.setMode(SnakeView.READY);
        } else {
            // We are being restored
            Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
            if (map != null) {
                mSnakeView.restoreState(map);
            } else {
                mSnakeView.setMode(SnakeView.PAUSE);
            }
        }
        mSnakeView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSnakeView.getGameState() != SnakeView.RUNNING) {
                    mSnakeView.moveSnake(MOVE_UP);
                    timer = new CountDownTimer(runningTime, 1000) {
                        public void onTick(long millisUntilFinished) {
                        	runningTime = millisUntilFinished;
                        	long mins = millisUntilFinished / 60000;
                        	int secs = (int) (millisUntilFinished/1000)%60;
                        	if (secs < 10)
                        		myTimer.setText(" " + mins + ":0" + secs);
                        	else
                        		myTimer.setText(" " + mins + ":" + secs);

                        }

                        public void onFinish() {
                            myTimer.setText("");
                            mSnakeView.setMode(SnakeView.FINISHED);
                            runningTime = 20000;
                            GlobalParameters.isGaming = false;
                            
                        }

                     };
                    timer.start();
                }
                
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause the game along with the activity
        mSnakeView.setMode(SnakeView.PAUSE);
        timer.cancel();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Store the game state
        outState.putBundle(ICICLE_KEY, mSnakeView.saveState());
    }

    /**
     * Handles key events in the game. Update the direction our snake is traveling based on the
     * DPAD.
     *
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                mSnakeView.moveSnake(MOVE_UP);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mSnakeView.moveSnake(MOVE_RIGHT);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mSnakeView.moveSnake(MOVE_DOWN);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mSnakeView.moveSnake(MOVE_LEFT);
                break;
        }

        return super.onKeyDown(keyCode, msg);
    }
    
    public void continueMonitor() {
    	Intent intent = new Intent(this, Monitor.class);
	    startActivity(intent);
    }
    
    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            myLabel.setText("No bluetooth adapter available");
        }
        
        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }
        
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("ArduinoBT")) 
                {
                    mmDevice = device;
                    break;
                }
            }
        }
        myLabel.setText("Bluetooth Device Found,click again to connect");
    }
    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);        
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        myLabel.setText("Bluetooth Opened");

        beginListenForData();
        
    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        mmInputStream.close();
        mmSocket.close();
        myLabel.setText("Bluetooth Closed");
    }

    void beginListenForData()
    {
    	myLabel.setText("ready to listen");
        final Handler handler = new Handler();         
        final int lefthold = 128 + GlobalParameters.threshold;
        final int righthold = 128 - GlobalParameters.threshold;
        final int counter = 6;
        
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {     
               int STATE = 0;
               int leftcount = 0;
               int rightcount = 0;
               int time = 0;
               int timeout = 1000;
               
               while(!Thread.currentThread().isInterrupted() && !stopWorker)
               { 
                    try 
                    {
                    	while (true) {
                    		int b = mmInputStream.read();
                			Log.i(TAG, "write." );
 
                    		switch(STATE){
                    			case -1:
                    				result = "timeout";
                    				if (time < timeout){
                    					time++;
                    				}
                    				else {
                        				STATE = 0;
                        				time = 0;
                    				}
                    				break;
                    			case 0:
                    				result = "hold";
                    				if (b > lefthold) {               
                    					STATE=1;
                    				}
                    				else if (b < righthold) {                    				
                    					STATE=2;
                    				}
                    				break;
                    			case 1:
                    				if (b > lefthold) {
                    					STATE=1;
                    				}
                    				else {
                    					STATE = 0;
                    					leftcount = 0;
                    					break;
                    				}
                    				if (leftcount < counter) {
                    					leftcount++;
                    				}
                    				else {
                        				result = "left";
                        				mSnakeView.moveSnake(0);
                        				leftcount = 0;
                        				STATE = -1;
                    				}
                    				break;
                    			case 2:
                    				if (b < righthold)
                    					STATE = 2;
                    				else {
                    					STATE = 0;
                    					rightcount = 0;
                    					break;
                    				}
                    				if (rightcount < counter) {
                    					rightcount++;
                    				}
                    				else {
                        				result = "right";
                        				mSnakeView.moveSnake(3);
                        				rightcount = 0;
                        				STATE = -1;
                    				}
                    				break;
                    		}
                			handler.post(new Runnable()
                          {
                              public void run()
                              {
                                  myLabel.setText(result);
                              }
                          });
                    	}        	
                    } 
                    catch (IOException ex) 
                    {
                        stopWorker = true;
                    }
               }
            }
        });

        workerThread.start();
    }
    public void stopTimer(){
    	timer.cancel();
    }

}
