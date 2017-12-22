package edu.weber.mail.garrettbarker.cs3270a8;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class CourseEditFragment extends Fragment {

    private View rootView;
    private FloatingActionButton saveFloatingActionButton;
    private EditText courseID;
    private EditText courseName;
    private EditText courseCode;
    private EditText courseStart;
    private EditText courseEnd;
    private Boolean editExisting;

    public CourseEditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_course_edit, container, false);

        MainActivity ma = (MainActivity) getActivity();

        courseID = (EditText) rootView.findViewById(R.id.editIdET);
        courseName = (EditText) rootView.findViewById(R.id.editNameET);
        courseCode = (EditText) rootView.findViewById(R.id.editCourseCodeET);
        courseStart = (EditText) rootView.findViewById(R.id.editStartET);
        courseEnd = (EditText) rootView.findViewById(R.id.editEndET);

        editExisting = ma.isEditExisting();
        saveFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.saveFAB);
        saveFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity ma = (MainActivity) getActivity();
                DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), "Courses", null, 1);
                if (!courseID.isFocused() && !courseName.isFocused() && !courseCode.isFocused() && !courseStart.isFocused() && !courseEnd.isFocused()){
                    return;
                }
                if (editExisting){
                    databaseHelper.updateCourse(ma.getCourseID(), courseID.getText().toString(), courseName.getText().toString(),
                            courseCode.getText().toString(), courseStart.getText().toString(), courseEnd.getText().toString());
                }else if (!editExisting){
                    long rowID = databaseHelper.insertCourse(courseID.getText().toString(), courseName.getText().toString(),
                            courseCode.getText().toString(), courseStart.getText().toString(), courseEnd.getText().toString());
                }
                ma.hideSoftKeyboard(ma);
                ma.changeViewToListView();
                ma.setEditExisting(false);
            }
        });

        if(rootView != null && editExisting) {
            populateData();
        }
        return rootView;
    }

    public void populateData(){
        MainActivity ma = (MainActivity) getActivity();
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), "Courses", null, 1);
        Cursor cursor = databaseHelper.getACourse(ma.getCourseID());
        cursor.moveToFirst();

        String courseID = cursor.getString(cursor.getColumnIndex("id"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        String courseCode = cursor.getString(cursor.getColumnIndex("course_code"));
        String start = cursor.getString(cursor.getColumnIndex("start_at"));
        String end = cursor.getString(cursor.getColumnIndex("end_at"));

        this.courseID.setText(courseID);
        courseName.setText(name);
        this.courseCode.setText(courseCode);
        courseStart.setText(start);
        courseEnd.setText(end);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity ma = (MainActivity) getActivity();
        ma.restoreState();
        editExisting = ma.isEditExisting();
        if(rootView != null && editExisting) {
            populateData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity ma = (MainActivity) getActivity();
        ma.saveState();
    }
}
