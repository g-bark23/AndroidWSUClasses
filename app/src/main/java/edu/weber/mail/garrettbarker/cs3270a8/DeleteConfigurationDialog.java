package edu.weber.mail.garrettbarker.cs3270a8;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeleteConfigurationDialog extends DialogFragment {


    public DeleteConfigurationDialog() {
        // Required empty public constructor
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Dialog dialog =
                builder.setMessage("This will permanently delete the course.")
                        .setCancelable(false)
                        .setTitle("Are You Sure?")
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                return;
                            }
                        })
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                MainActivity ma = (MainActivity) getActivity();
                                DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), "Courses", null, 1);
                                databaseHelper.deleteCourse(ma.getCourseID());
                                ma.changeViewToListView();
                            }
                        }).create();

        return dialog;
    }

}
