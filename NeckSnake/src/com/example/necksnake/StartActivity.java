package com.example.necksnake;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		Button btnMonitor = (Button)findViewById(R.id.monitor);
		Button btnSetting = (Button)findViewById(R.id.setting);
		if (GlobalParameters.isGaming) {
			btnMonitor.setEnabled(false);
			btnSetting.setEnabled(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}
	
	public void playGame(View view) {
	    Intent intent = new Intent(this, Snake.class);
	    startActivity(intent);
	}
	
	public void setting(View view) {
	    Intent intent = new Intent(this, Setting.class);
	    startActivity(intent);
	}
	
	public void monitor(View view) {
		Intent intent = new Intent(this, Monitor.class);
		startActivity(intent);
	}
 
}
