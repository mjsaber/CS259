package com.example.necksnake;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class StrengthListener extends Activity implements OnItemSelectedListener {
    public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    	String select = parent.getItemAtPosition(pos).toString();
    	if (select.equals("Low")) {
    		GlobalParameters.threshold = 52;
    	}    	
    	else if (select.equals("Moderate")) {
    		GlobalParameters.threshold = 72;
    	}
    	else if (select.equals("Tough")) {
    		GlobalParameters.threshold = 92;
    	}

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}