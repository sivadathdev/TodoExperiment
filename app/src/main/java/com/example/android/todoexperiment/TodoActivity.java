package com.example.android.todoexperiment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.todoexperiment.data.TodoContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;



public class TodoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mTaskNameEditText;
    private EditText mTaskDescriptionEditText;
    private EditText mDateEditText;
    private Spinner mPrioritySpinner;

    private static final int EXISTING_TASK_LOADER = 0;

    private DatePickerDialog mTodoDatePickerDialog;

    private SimpleDateFormat dateFormat;

    private Uri mCurrentTodoUri;

    private boolean mTodoHasChanged = false;

    private int mPriority = TodoContract.TodoEntry.PRIORITY_LOW;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mTodoHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        Intent intent = getIntent();
        mCurrentTodoUri = intent.getData();
        if (mCurrentTodoUri==null){
            setTitle("Add a Task");
            invalidateOptionsMenu();
        }
        else {
            setTitle("Edit a Task");

            getSupportLoaderManager().initLoader(EXISTING_TASK_LOADER, null, this);
        }

        mTaskNameEditText = (EditText) findViewById(R.id.task_name);
        mTaskDescriptionEditText = (EditText) findViewById(R.id.task_description);
        mPrioritySpinner = (Spinner) findViewById(R.id.spinner_priority);
        mDateEditText = (EditText) findViewById(R.id.task_date);
        mDateEditText.setInputType(InputType.TYPE_NULL);

        mTaskNameEditText.setOnTouchListener(mTouchListener);
        mTaskDescriptionEditText.setOnTouchListener(mTouchListener);
        mPrioritySpinner.setOnTouchListener(mTouchListener);
        mDateEditText.setOnTouchListener(mTouchListener);

        setupSpinner();
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        setDateTimeField();
    }

    private void setDateTimeField() {
        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTodoDatePickerDialog.show();
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        mTodoDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                mDateEditText.setText(dateFormat.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void setupSpinner() {
        ArrayAdapter prioritySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_gender_options, android.R.layout.simple_spinner_item);

        prioritySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mPrioritySpinner.setAdapter(prioritySpinnerAdapter);
        mPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(R.string.priority_low)) {
                        mPriority = 0;
                    } else if (selection.equals(R.string.priority_medium)) {
                        mPriority = 1;
                    } else if (selection.equals(R.string.priority_high)) {
                        mPriority = 2;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mPriority = TodoContract.TodoEntry.PRIORITY_LOW;
            }
        });
    }
    private void saveTask() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String taskString = mTaskNameEditText.getText().toString().trim();
        String descriptionString = mTaskDescriptionEditText.getText().toString().trim();
        String dateString = mDateEditText.getText().toString();


        if (mCurrentTodoUri == null &&
                TextUtils.isEmpty(taskString) && TextUtils.isEmpty(descriptionString) &&
                TextUtils.isEmpty(dateString) && mPriority == TodoContract.TodoEntry.PRIORITY_LOW) {

            return;
        }


        ContentValues values = new ContentValues();
        values.put(TodoContract.TodoEntry.COLUMN_TODO_TASK, taskString);
        values.put(TodoContract.TodoEntry.COLUMN_TODO_DESCRIPTION, descriptionString);
        values.put(TodoContract.TodoEntry.COLUMN_TODO_PRIORITY, mPriority);

        values.put(TodoContract.TodoEntry.COLUMN_TODO_DATE, dateString);

        if (mCurrentTodoUri == null) {

            Uri newUri = getContentResolver().insert(TodoContract.TodoEntry.CONTENT_URI, values);

            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_task_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_task_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentTodoUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_task_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_task_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todo_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentTodoUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Do nothing for now
                saveTask();
                finish();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case R.id.action_alarm:

                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                String message = mTaskNameEditText.getText().toString();
                intent.putExtra(AlarmClock.EXTRA_MESSAGE,message);
                startActivity(intent);
                break;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:

                if (!mTodoHasChanged) {
                    NavUtils.navigateUpFromSameTask(TodoActivity.this);
                    return true;
                }


                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(TodoActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mTodoHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection = {
                TodoContract.TodoEntry._ID,
                TodoContract.TodoEntry.COLUMN_TODO_TASK,
                TodoContract.TodoEntry.COLUMN_TODO_DESCRIPTION,
                TodoContract.TodoEntry.COLUMN_TODO_PRIORITY,
                TodoContract.TodoEntry.COLUMN_TODO_DATE };

        return new CursorLoader(this,
                mCurrentTodoUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }


        if (cursor.moveToFirst()) {

            int taskColumnIndex = cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TODO_TASK);
            int descriptionColumnIndex = cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TODO_DESCRIPTION);
            int priorityColumnIndex = cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TODO_PRIORITY);
            int dateColumnIndex = cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TODO_DATE);

            String task = cursor.getString(taskColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            int priority = cursor.getInt(priorityColumnIndex);
            String date = cursor.getString(dateColumnIndex);

            mTaskNameEditText.setText(task);
            mTaskDescriptionEditText.setText(description);
            mDateEditText.setText(date);


            switch (priority) {
                case TodoContract.TodoEntry.PRIORITY_MEDIUM:
                    mPrioritySpinner.setSelection(1);
                    break;
                case TodoContract.TodoEntry.PRIORITY_HIGH:
                    mPrioritySpinner.setSelection(2);
                    break;
                default:
                    mPrioritySpinner.setSelection(0);
                    break;
            }
        }
    }


    @Override
    public void onLoaderReset( Loader<Cursor> loader) {
        mTaskNameEditText.setText("");
        mTaskDescriptionEditText.setText("");
        mPrioritySpinner.setSelection(0);
        mDateEditText.setText("");
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteTodo();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     */
    private void deleteTodo() {

        if (mCurrentTodoUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentTodoUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_todo_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_todo_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();

    }

}
