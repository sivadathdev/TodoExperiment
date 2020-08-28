package com.example.android.todoexperiment.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class TodoContract{
    private TodoContract(){

    }
    public static final class TodoEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODO;

        public final static String TODO_TABLE_NAME = "todo";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_TODO_TASK = "taskName";

        public final static String COLUMN_TODO_DESCRIPTION = "taskDescription";

        public final static String COLUMN_TODO_PRIORITY = "priority";

        public final static String COLUMN_TODO_DATE = "date";

        public static final int PRIORITY_LOW = 0;
        public static final int PRIORITY_MEDIUM = 1;
        public static final int PRIORITY_HIGH = 2;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_TODO);

        public static boolean isValid(Integer priority) {
            if (priority==PRIORITY_LOW || priority==PRIORITY_MEDIUM || priority==PRIORITY_HIGH){
                return true;
            }
            return false;
        }
    }
    public static final String CONTENT_AUTHORITY = "com.example.android.todoexperiment";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_TODO = "todo";

}