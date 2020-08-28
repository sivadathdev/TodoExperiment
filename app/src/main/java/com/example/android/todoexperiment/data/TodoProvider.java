package com.example.android.todoexperiment.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;



public class TodoProvider extends ContentProvider {

    public static final String LOG_TAG = TodoProvider.class.getSimpleName();

    public TodoDbHelper mDbHelper;

    public static final int TODO = 100;
    public static final int TODO_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(TodoContract.CONTENT_AUTHORITY, TodoContract.PATH_TODO, TODO);
        sUriMatcher.addURI(TodoContract.CONTENT_AUTHORITY, TodoContract.PATH_TODO + "/#", TODO_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new TodoDbHelper(getContext());
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:
                cursor = db.query(TodoContract.TodoEntry.TODO_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

                break;
            case TODO_ID:
                selection = TodoContract.TodoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TodoContract.TodoEntry.TODO_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("cannot query unknown uri" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues values) {

        String task = values.getAsString(TodoContract.TodoEntry.COLUMN_TODO_TASK);
        Integer priority = values.getAsInteger(TodoContract.TodoEntry.COLUMN_TODO_PRIORITY);
        String date = values.getAsString(TodoContract.TodoEntry.COLUMN_TODO_DATE);
        if (task == null) {
            throw new IllegalArgumentException("Task requires a name");
        }
        if (priority == null || !TodoContract.TodoEntry.isValid(priority)) {
            throw new IllegalArgumentException("Task Requires valid priority");
        }
        if (date == null) {
            throw new IllegalArgumentException("Task requires valid date");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(TodoContract.TodoEntry.TODO_TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case TODO_ID:

                selection = TodoContract.TodoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(TodoContract.TodoEntry.COLUMN_TODO_TASK)) {
            String task = values.getAsString(TodoContract.TodoEntry.COLUMN_TODO_TASK);
            if (task == null) {
                throw new IllegalArgumentException("Enter a valid Task");
            }
        }
        if (values.containsKey(TodoContract.TodoEntry.COLUMN_TODO_PRIORITY)) {
            Integer priority = values.getAsInteger(TodoContract.TodoEntry.COLUMN_TODO_PRIORITY);
            if (priority == null || !TodoContract.TodoEntry.isValid(priority)) {
                throw new IllegalArgumentException("Enter a valid Priority");
            }
        }
        if (values.containsKey(TodoContract.TodoEntry.COLUMN_TODO_DATE)) {
            String date = values.getAsString(TodoContract.TodoEntry.COLUMN_TODO_DATE);
            if (date == null) {
                throw new IllegalArgumentException("Enter a valid date");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int numberOfRows = database.update(TodoContract.TodoEntry.TODO_TABLE_NAME, values, selection, selectionArgs);
        if (numberOfRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRows;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:

                getContext().getContentResolver().notifyChange(uri, null);
                rowsDeleted = database.delete(TodoContract.TodoEntry.TODO_TABLE_NAME, selection, selectionArgs);
                break;
            case TODO_ID:

                selection = TodoContract.TodoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TodoContract.TodoEntry.TODO_TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:
                return TodoContract.TodoEntry.CONTENT_LIST_TYPE;
            case TODO_ID:
                return TodoContract.TodoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
