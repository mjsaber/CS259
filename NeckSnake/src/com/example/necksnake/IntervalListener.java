package com.example.necksnake;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class IntervalListener extends Activity implements OnItemSelectedListener {
    public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    	String select = parent.getItemAtPosition(pos).toString();
    	if (select.equals("30 mins")) {
    		GlobalParameters.intervalTime = 1800000;
    	}    	
    	else if (select.equals("60 mins")) {
    		GlobalParameters.intervalTime = 3600000;
    	}

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
