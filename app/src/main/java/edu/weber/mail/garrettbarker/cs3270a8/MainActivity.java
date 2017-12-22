package edu.weber.mail.garrettbarker.cs3270a8;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private long courseID;
    private boolean editExisting;
    private String assignCourseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            changeViewToListView();
        }
    }

    public void changeViewToAddView(){
        editExisting = false;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, new CourseEditFragment(), "CourseEdit")
                .addToBackStack(null)
                .commit();
    }

    public void changeViewToEditView(){
        editExisting = true;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, new CourseEditFragment(), "CourseEdit")
                .addToBackStack(null)
                .commit();
    }

    public void changeViewToListView(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, new CourseListFragment(), "CourseList")
                .addToBackStack(null)
                .commit();
    }

    public void changeViewToCourseView(long id){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, new CourseViewFragment(), "CourseView")
                .addToBackStack(null)
                .commit();
        this.courseID = id;

        Log.d("Test", "ChangeView CourseID: " + id);
    }

    public long getCourseID() {
        return courseID;
    }

    public boolean isEditExisting() { return editExisting; }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void viewAssignments(long id){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, new assignments(), "assign")
                .addToBackStack(null)
                .commit();
        this.courseID = id;
    }

    public void saveState(){
        SharedPreferences sp = getPreferences((MODE_PRIVATE));
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString("assignCourseID", getAssignCourseID());
        spEditor.putLong("courseID", getCourseID());
        spEditor.putBoolean("editExisting", isEditExisting());
        spEditor.commit();
    }

    public void restoreState(){
        SharedPreferences sp = getPreferences((MODE_PRIVATE));
        assignCourseID = sp.getString("assignCourseID", "");
        courseID = sp.getLong("courseID", 0);
        editExisting = sp.getBoolean("editExisting", false);
    }

    public String getAssignCourseID() {
        return assignCourseID;
    }

    public void setAssignCourseID(String assignCourseID) {
        this.assignCourseID = assignCourseID;
    }

    public void setEditExisting(boolean editExisting) {
        this.editExisting = editExisting;
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreState();
    }
}
