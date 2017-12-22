package edu.weber.mail.garrettbarker.cs3270a8;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pharrett23 on 10/2/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    private SQLiteDatabase database;

    public DatabaseHelper (Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    public SQLiteDatabase open(){
        database = getWritableDatabase();
        return database;
    }

    public void close(){
        if(database != null){
            database.close();
        }
    }

    public Cursor getAllCourses(){
        Cursor cursor = null;
        if (open() != null){
            cursor = database.rawQuery("SELECT * FROM myCourses ORDER BY name", null);
        }
        return cursor;
    }

    public Cursor getACourse(long id){
        Cursor cursor = null;
        if (open() != null){
            cursor = database.rawQuery("SELECT * FROM myCourses WHERE _id= '" + id + "' ORDER BY name", null);
        }
        return cursor;
    }

    public void deleteCourse(long id){
        if (open() != null){
            database.delete("myCourses", "_id= " + id, null);
        }
    }

    public void deleteAll(){
        if (open() != null){
            database.execSQL("delete from myCourses");
        }
    }

    public long insertCourse(String courseID, String name, String courseCode, String start, String end){
        long rowID = -1;

        ContentValues newCourse = new ContentValues();
        newCourse.put("id", courseID);
        newCourse.put("name", name);
        newCourse.put("course_code", courseCode);
        newCourse.put("start_at", start);
        newCourse.put("end_at", end);

        if (open() != null){
            rowID = database.insert("myCourses", null, newCourse);
            close();
        }

        return rowID;
    }

    public long updateCourse(long id, String courseID, String name, String courseCode, String start, String end){
        long rowID = -1;

        ContentValues editCourse = new ContentValues();
        editCourse.put("id", courseID);
        editCourse.put("name", name);
        editCourse.put("course_code", courseCode);
        editCourse.put("start_at", start);
        editCourse.put("end_at", end);

        if (open() != null){
            rowID = database.update("myCourses", editCourse, "_id=" + id, null);
            close();
        }

        return rowID;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createQuery = "CREATE TABLE myCourses" +
                "(_id integer primary key autoincrement," +
                "id TEXT, name TEXT, course_code TEXT," +
                "start_at TEXT, end_at TEXT);";
        close();
        sqLiteDatabase.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
