package edu.weber.mail.garrettbarker.cs3270a8;

import edu.weber.mail.garrettbarker.cs3270a8.CanvasObjects.Course;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.AdapterView;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class CourseListFragment extends Fragment{

    private View rootView;
    private FloatingActionButton addItemBTN;
    private ListView courseList;

    public CourseListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_course_list, container, false);
        courseList = (ListView) rootView.findViewById(R.id.courseListView);

        setHasOptionsMenu(true);

        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), "Courses", null, 1);
        Cursor cursor = databaseHelper.getAllCourses();
        String[] columns = new String[] {"name"};
        int[] views = new int[] {android.R.id.text1};
        SimpleCursorAdapter adapter =
                new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, cursor,
                        columns, views, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
       // setListAdapter(adapter);
        courseList.setAdapter(adapter);

        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity ma = (MainActivity) getActivity();
                ma.changeViewToCourseView(l);
            }
        });

        courseList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                MainActivity ma = (MainActivity) getActivity();
                ma.viewAssignments(id);
                return true;
            }
        });

        addItemBTN = (FloatingActionButton) rootView.findViewById(R.id.addBTN);

        addItemBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity ma = (MainActivity) getActivity();
                ma.changeViewToAddView();
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.menu_course_list_view, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.getCourses:
                    new getAllCourses().execute("");

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class getAllCourses extends AsyncTask<String, Integer, String>{

        String AUTH_TOKEN = Authorization.AUTH_TOKEN;
        String rawJson = "";

        @Override
        protected String doInBackground(String... strings) {

            Log.d("Test", "In main AsyncTask getting courses");

            try{
                URL url = new URL("https://weber.instructure.com/api/v1/courses");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + AUTH_TOKEN);
                conn.connect();
                int status = conn.getResponseCode();
                switch(status){
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        rawJson = br.readLine();
                        Log.d("Test", "raw Json String Length: " + rawJson.length());
                        Log.d("Test", "raw Json first 256 chars: " + rawJson.substring(0 , 256));
                        Log.d("Test", "raw Json last 256 chars: " + rawJson.substring(rawJson.length() - 256, rawJson.length()));
                }
            } catch (MalformedURLException e){
                Log.d("Test", "MalFormed " + e.getMessage());
            } catch (IOException e){
                Log.d("Test", "IO " + e.getMessage());
            }

            return rawJson;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), "Courses", null, 1);
            databaseHelper.deleteAll();

            Log.d("Test", "Starting onPost");
            try{
                Course[] courses = jsonParse(result);
                Log.d("Test", "jsonParsed");
                for (Course course : courses){
                    databaseHelper.insertCourse(course.id, course.name,
                         course.course_code, course.start_at, course.end_at);
                }
            } catch (Exception e){
                Log.d("Test", "onPost after adding to database" + e.getMessage());
            }

            Cursor cursor = databaseHelper.getAllCourses();
            String[] columns = new String[] {"name"};
            int[] views = new int[] {android.R.id.text1};
            SimpleCursorAdapter adapter =
                    new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, cursor,
                            columns, views, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            courseList.setAdapter(adapter);

        }

        private Course[] jsonParse(String rawJson){
            Log.d("Test", "Begin Parse");
            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.create();

            Course[] courses = null;
            Log.d("Test", "Course Array created");
            try{
                courses = gson.fromJson(rawJson, Course[].class);
                Log.d("Test", "Number of courses returned is: " + courses.length);
                Log.d("Test", "First Course returned is: " + courses[0].name);
            }catch (Exception e){
                Log.d("Test", e.getMessage());
            }

            return courses;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity ma = (MainActivity) getActivity();
        ma.restoreState();
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity ma = (MainActivity) getActivity();
        ma.saveState();
    }
}
