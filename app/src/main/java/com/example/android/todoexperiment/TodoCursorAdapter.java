package com.example.android.todoexperiment;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.todoexperiment.data.TodoContract;



public class TodoCursorAdapter extends CursorAdapter {

    private static int viewCount;

    public TodoCursorAdapter(Context context, Cursor c) {

        super(context, c, 0 /* flags */);
        viewCount=0;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,null,false);
        int backgroundColor = ColorUtils.getViewHolderBackgroundColorFromInstance(context,viewCount);
        view.setBackgroundColor(backgroundColor);
        viewCount++;
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        int nameColumnIndex = cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TODO_TASK);
        int summaryColumnIndex = cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TODO_DATE);
        String taskName =cursor.getString(nameColumnIndex);
        String taskSummary = cursor.getString(summaryColumnIndex);
        nameTextView.setText(taskName);
        summaryTextView.setText(taskSummary);

    }
}
