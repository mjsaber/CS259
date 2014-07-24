package com.example.necksnake;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class PlayGameDialogFragment extends DialogFragment{
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("SELECT MONITOR TIME")
               .setItems(R.array.interval, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       if (id == 0) {
                    	   GlobalParameters.startTime = 1800000;
                    	   GlobalParameters.startNew = 1800000;
                    	   GlobalParameters.accumulatedTime = 0;
                       }
                       else if (id == 1){
                    	   GlobalParameters.startTime = 3600000;
                    	   GlobalParameters.startNew = 3600000;
                    	   GlobalParameters.accumulatedTime = 0;
                       }
                       else if (id == 2){
                    	   GlobalParameters.startTime = 7200000;
                    	   GlobalParameters.startNew = 7200000;
                    	   GlobalParameters.accumulatedTime = 0;
                       }
                       ((Snake)getActivity()).continueMonitor();
                   }
                   
               });

        // Create the AlertDialog object and return it
        return builder.create();
    }

}
