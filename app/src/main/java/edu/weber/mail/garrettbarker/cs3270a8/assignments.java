package edu.weber.mail.garrettbarker.cs3270a8;

import edu.weber.mail.garrettbarker.cs3270a8.CanvasObjects.Assignment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import edu.weber.mail.garrettbarker.cs3270a8.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class assignments extends Fragment {
    private View rootView;
    private ListView myAssignView;
    private String courseID;
    private long courseNum;
    protected ArrayAdapter<String> assignAdapter;

    public assignments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_assignments, container, false);
        MainActivity ma = (MainActivity) getActivity();
        courseNum = ma.getCourseID();
        if(rootView != null && courseNum != 0){
            loadAssignments();
        }

        return rootView;
    }

    public class getAssignments extends AsyncTask<String, Integer, String> {

        String AUTH_TOKEN = Authorization.AUTH_TOKEN;
        String rawJson = "";

        @Override
        protected String doInBackground(String... strings) {

            Log.d("Test", "In main AsyncTask getting assignments");

            try {
                URL url = new URL("https://weber.instructure.com/api/v1/courses/" + courseID + "/assignments");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + AUTH_TOKEN);
                conn.connect();
                int status = conn.getResponseCode();
                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        rawJson = br.readLine();
                        Log.d("Test", "raw Json String Length: " + rawJson.length());
                        if (rawJson.length() > 256) {
                            Log.d("Test", "raw Json first 256 chars: " + rawJson.substring(0, 256));
                            Log.d("Test", "raw Json last 256 chars: " + rawJson.substring(rawJson.length() - 256, rawJson.length()));
                        }
                }
            } catch (MalformedURLException e) {
                Log.d("Test", "MalFormed " + e.getMessage());
            } catch (IOException e) {
                Log.d("Test", "IO " + e.getMessage());
            }

            return rawJson;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            assignAdapter.clear();
            Log.d("Test", "Starting onPost");
            try {
                Assignment[] assignments = jsonParse(result);
                Log.d("Test", "jsonParsed");
                for (Assignment assignment : assignments) {
                    assignAdapter.add(assignment.name);
                }
            } catch (Exception e) {
                Log.d("Test", e.getMessage());
            }

        }

        private Assignment[] jsonParse(String rawJson) {
            Log.d("Test", "Begin Parse");
            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.create();

            Assignment[] assignments = null;
            Log.d("Test", "Assignment Array created");
            try {
                assignments = gson.fromJson(rawJson, Assignment[].class);
                Log.d("Test", "Number of assignments returned is: " + assignments.length);
                Log.d("Test", "First assignment returned is: " + assignments[0].name);
            } catch (Exception e) {
                Log.d("Test", e.getMessage());
            }

            return assignments;
        }
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public void loadAssignments(){
        myAssignView = rootView.findViewById(R.id.assignListView);

        assignAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1);
        myAssignView.setAdapter(assignAdapter);
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), "Courses", null, 1);

        Cursor cursor = databaseHelper.getACourse(courseNum);
        cursor.moveToFirst();

        courseID = cursor.getString(cursor.getColumnIndex("id"));
        Log.d("Test", "Start getting Assignments, courseID = " + courseID);
        new getAssignments().execute("");
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity ma = (MainActivity) getActivity();
        ma.setAssignCourseID(courseID);
        ma.saveState();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity ma = (MainActivity) getActivity();
        courseNum = ma.getCourseID();
        loadAssignments();
    }
}
