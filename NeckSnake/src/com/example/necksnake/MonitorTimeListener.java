package com.example.necksnake;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class MonitorTimeListener extends Activity implements OnItemSelectedListener {
    public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    	String select = parent.getItemAtPosition(pos).toString();
    	if (select.equals("30 mins")) {
    		GlobalParameters.startTime = 1800000;
     	   GlobalParameters.startNew = 1800000;
    	}    	
    	else if (select.equals("60 mins")) {
    		GlobalParameters.startTime = 3600000;
      	   GlobalParameters.startNew = 3600000;
    	}
    	else if (select.equals("120 mins")) {
    		GlobalParameters.startTime = 7200000;
      	   GlobalParameters.startNew = 7200000;
    	}
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
