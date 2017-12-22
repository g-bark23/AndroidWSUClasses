package edu.weber.mail.garrettbarker.cs3270a8;


import android.database.Cursor;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;


/**
 * A simple {@link Fragment} subclass.
 */
public class CourseViewFragment extends Fragment {

    private View rootView;
    private EditText courseIDET;
    private EditText courseNameET;
    private EditText courseCodeET;
    private EditText courseStartET;
    private EditText courseEndET;
    private long courseID;

    public CourseViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_course_view, container, false);
        courseIDET = rootView.findViewById(R.id.viewIdET);
        courseNameET = rootView.findViewById(R.id.viewNameET);
        courseCodeET = rootView.findViewById(R.id.viewCourseCodeET);
        courseStartET = rootView.findViewById(R.id.viewStartET);
        courseEndET = rootView.findViewById(R.id.viewEndET);
        MainActivity ma = (MainActivity) getActivity();
        courseID = ma.getCourseID();
        setHasOptionsMenu(true);

        if(rootView != null && courseID != 0){
            populateCourseInfo();
        }

        return rootView;
    }

    public void populateCourseInfo(){
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), "Courses", null, 1);
        Log.d("Test", "courseID: " + courseID);
        Cursor cursor = databaseHelper.getACourse(courseID);
        cursor.moveToFirst();

        String courseID = cursor.getString(cursor.getColumnIndex("id"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        String courseCode = cursor.getString(cursor.getColumnIndex("course_code"));
        String start = cursor.getString(cursor.getColumnIndex("start_at"));
        String end = cursor.getString(cursor.getColumnIndex("end_at"));

        courseIDET.setText(courseID);
        courseNameET.setText(name);
        courseCodeET.setText(courseCode);
        courseStartET.setText(start);
        courseEndET.setText(end);
        courseIDET.setKeyListener(null);
        courseNameET.setKeyListener(null);
        courseCodeET.setKeyListener(null);
        courseStartET.setKeyListener(null);
        courseEndET.setKeyListener(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                MainActivity ma = (MainActivity) getActivity();
                ma.changeViewToEditView();

                return true;

            case R.id.delete:
                DeleteConfigurationDialog dialog = new DeleteConfigurationDialog();
                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), "deleteDialog");

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setCourseID(long courseID) {
        this.courseID = courseID;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity ma = (MainActivity) getActivity();
        ma.restoreState();
        setCourseID(ma.getCourseID());
        populateCourseInfo();
        Log.d("Test", "OnResume CourseID: " + courseID);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity ma = (MainActivity) getActivity();
        ma.saveState();
    }
}
