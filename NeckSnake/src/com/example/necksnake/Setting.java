package com.example.necksnake;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Setting extends Activity {
	
	Spinner spMonitoringTime;
	Spinner spInterval;
	Spinner spStrength;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		spMonitoringTime = (Spinner) findViewById(R.id.spMonitoringTime);
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
		        R.array.timeSlot, android.R.layout.simple_spinner_item);		
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spMonitoringTime.setAdapter(adapter1);
		
//		spInterval = (Spinner) findViewById(R.id.spInterval);
//		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
//		        R.array.interval, android.R.layout.simple_spinner_item);
//		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		spInterval.setAdapter(adapter2);
		spStrength = (Spinner) findViewById(R.id.spStrength);
		ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
		        R.array.strength, android.R.layout.simple_spinner_item);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spStrength.setAdapter(adapter3);
		
		addListenerOnSpinnerItemSelection();
	}



	public void addListenerOnSpinnerItemSelection(){       
        spMonitoringTime.setOnItemSelectedListener(new MonitorTimeListener());
        spInterval.setOnItemSelectedListener(new IntervalListener());
//        spStrength.setOnItemSelectedListener(new StrengthListener());
	}

}
