package edu.weber.mail.garrettbarker.cs3270a8;

import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Created by pharrett23 on 10/16/17.
 */

public class CanvasObjects {
    protected class Course {
        protected String id;
        protected String sis_course_id;
        protected String name;
        protected String course_code;
        protected String account_id;
        protected String start_at;
        protected String end_at;
        protected Enrollment[] enrollments;
        protected Calendar calendar;
        protected String syllabus_body;
        protected String needs_grading_count;
        protected Term term;
    }

    protected class Term{
        protected String id;
        protected String name;
        protected String start_at;
        protected String end_at;
    }

    protected class Calendar{

    }

    protected class Enrollment{

    }

    protected class Assignment{
        protected String id;
        protected String name;
    }

}
