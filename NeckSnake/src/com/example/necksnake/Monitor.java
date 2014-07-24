package com.example.necksnake;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;

public class Monitor extends Activity {
	TextView myLabel;
	Vibrator v;
    CircleTimerView mCircleView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);
		myLabel = (TextView)findViewById(R.id.label);
		mCircleView = (CircleTimerView)findViewById(R.id.timer_time);
		mCircleView.setIntervalTime((long)GlobalParameters.startNew);
		this.start();
		CountDownTimer timer = new CountDownTimer(GlobalParameters.startTime, 1000) {
	        public void onTick(long millisUntilFinished) {
	        	String fontPath = "fonts/AndroidClockMono-Thin.ttf"; 
	        	Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
	        	GlobalParameters.startTime = millisUntilFinished;
	        	long mins = millisUntilFinished / 60000;
	        	int secs = (int) (millisUntilFinished/1000)%60;
	        	GlobalParameters.accumulatedTime = GlobalParameters.startNew - secs * 1000;
	        	myLabel.setTypeface(tf);
	        	if (secs < 10)
	        		myLabel.setText(" " + mins + ":0" + secs);
	        	else
	        		myLabel.setText(" " + mins + ":" + secs);
	        }
	        public void onFinish() {       	
	            myLabel.setText("Snake!");

	            //beginVibrate();
	            popUpNotification();
	            GlobalParameters.isGaming = true; 
	        }
	     };
	    timer.start();
	}
    @Override
    protected void onPause() { 
        super.onPause();
        // Pause the game along with the activity
        myLabel.setText("pause");
        startService(new Intent(this, TimerService.class));
        //timer.cancel();
    }

    public void popUpDialog() {
        DialogFragment dialog = new PlayGameDialogFragment();
        dialog.show(getFragmentManager(), "play_game");
    }
    public void popUpNotification() {
    	long[] pattern ={0, 1500, 500,1500,500,1500,500,1500,500,1500,500,1500,500,1500,500,1500,500,1500,500,1500,500};
    	NotificationCompat.Builder mBuilder =
    	        new NotificationCompat.Builder(this)
    	        .setSmallIcon(R.drawable.ic_launcher)
    	        .setContentTitle("Time to Snake!")
    	        .setContentText("Click to Exercise Your Neck!")
    	        .setVibrate(pattern)
    	        .setAutoCancel(true);
    	// Creates an explicit intent for an Activity in your app
    	Intent resultIntent = new Intent(this, Snake.class);

    	TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    	stackBuilder.addParentStack(Snake.class);
    	stackBuilder.addNextIntent(resultIntent);
    	PendingIntent resultPendingIntent =
    	        stackBuilder.getPendingIntent(
    	            0,
    	            PendingIntent.FLAG_UPDATE_CURRENT
    	        );
    	mBuilder.setContentIntent(resultPendingIntent);
    	NotificationManager mNotificationManager =
    	    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	// mId allows you to update the notification later on.
    	mNotificationManager.notify(1, mBuilder.build());
    }
    public void start() {
        mCircleView.startIntervalAnimation(); 
    }
}
