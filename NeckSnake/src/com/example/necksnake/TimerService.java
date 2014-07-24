package com.example.necksnake;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TimerService extends Service{
	
	private Intent intent;
    public static final String TIMER_ACTION = "com.example.necksnake.Monitor";
	@Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(TIMER_ACTION);  
//        handler.removeCallbacks(sendUpdatesToUI);
//        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second

    }
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
