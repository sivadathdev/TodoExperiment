package com.example.android.todoexperiment.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class TodoDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = TodoDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "todoDairy.db";
    private static final int DATABASE_VERSION = 1;

    public TodoDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TODO_TABLE =  "CREATE TABLE " + TodoContract.TodoEntry.TODO_TABLE_NAME + " ("
                + TodoContract.TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TodoContract.TodoEntry.COLUMN_TODO_TASK + " TEXT NOT NULL, "
                + TodoContract.TodoEntry.COLUMN_TODO_DESCRIPTION + " TEXT, "
                + TodoContract.TodoEntry.COLUMN_TODO_PRIORITY + " INTEGER NOT NULL, "
                + TodoContract.TodoEntry.COLUMN_TODO_DATE + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
