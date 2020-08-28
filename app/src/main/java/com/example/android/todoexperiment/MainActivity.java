package com.example.android.todoexperiment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.todoexperiment.data.TodoContract;
import com.example.android.todoexperiment.data.TodoDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;



public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private TodoDbHelper mDbHelper;
    private static final int TODO_LOADER = 0;
    TodoCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TodoActivity.class);
                startActivity(intent);
            }
        });
        mDbHelper = new TodoDbHelper(this);

        getSupportLoaderManager().initLoader(TODO_LOADER, null, this);

        ListView displayListView = (ListView) findViewById(R.id.list_view_todo);
        View emptyView = findViewById(R.id.empty_view);
        displayListView.setEmptyView(emptyView);

        getSupportLoaderManager().initLoader(TODO_LOADER, null, this);
        mCursorAdapter = new TodoCursorAdapter(this, null);
        displayListView.setAdapter(mCursorAdapter);

        displayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,TodoActivity.class);
                Uri currentTaskUri = ContentUris.withAppendedId(TodoContract.TodoEntry.CONTENT_URI,id);
                intent.setData(currentTaskUri);
                startActivity(intent);

            }
        });
    }

    private void insertTask() {


        ContentValues values = new ContentValues();
        values.put(TodoContract.TodoEntry.COLUMN_TODO_TASK, "Family MeetUp");
        values.put(TodoContract.TodoEntry.COLUMN_TODO_DESCRIPTION, "Brother's Birthday Celebration");
        values.put(TodoContract.TodoEntry.COLUMN_TODO_PRIORITY, TodoContract.TodoEntry.PRIORITY_HIGH);
        values.put(TodoContract.TodoEntry.COLUMN_TODO_DATE, "02-01-2021");

        Uri newUri = getContentResolver().insert(TodoContract.TodoEntry.CONTENT_URI,values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        int id = item.getItemId();
        if (id == R.id.action_insert_dummy_data) {
            insertTask();
            return true;
        }
        else if (id==R.id.action_delete_all){
            deleteAllTasks();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id,  Bundle args) {
        String[] projection ={TodoContract.TodoEntry._ID, TodoContract.TodoEntry.COLUMN_TODO_TASK, TodoContract.TodoEntry.COLUMN_TODO_DATE};
        return new CursorLoader(this, TodoContract.TodoEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private void deleteAllTasks() {
        int rowsDeleted = getContentResolver().delete(TodoContract.TodoEntry.CONTENT_URI, null, null);
    }
}


