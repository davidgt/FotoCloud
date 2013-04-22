package com.app.fotocloud.facebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.app.fotocloud.MainActivity;
import com.app.fotocloud.R;

public class UploadPhotoDialog extends DialogFragment {
	
	public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
	
	NoticeDialogListener mListener;
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_download_photo)
               .setPositiveButton(R.string.disk, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   ((MainActivity)getActivity()).uploadUsingDisk();
                   }
               })
               .setNegativeButton(R.string.camera, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   ((MainActivity)getActivity()).uploadUsingCamera();
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
    
   /* @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }*/
}