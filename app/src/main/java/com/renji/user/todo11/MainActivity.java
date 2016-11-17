package com.renji.user.todo11;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.renji.user.todo11.db.TaskContract;
import com.renji.user.todo11.db.TaskDbHelper;
import com.renji.user.todo11.R;

public class MainActivity extends ListActivity {

    private ListAdapter listAdapter;
    private TaskDbHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateUI();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addtask();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void addtask () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a task");
        builder.setMessage("What do you want to do?");
        final EditText inputField = new EditText(this);
        builder.setView(inputField);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String task = inputField.getText().toString();

                helper = new TaskDbHelper(MainActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.clear();
                values.put(TaskContract.Columns.TASK, task);

                db.insertWithOnConflict(TaskContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                updateUI();
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void updateUI() {
        helper = new TaskDbHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getReadableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns._ID, TaskContract.Columns.TASK},
                null, null, null, null, null);

        listAdapter = new SimpleCursorAdapter(
                this,
                R.layout.task_view,
                cursor,
                new String[]{TaskContract.Columns.TASK},
                new int[]{R.id.list},
                0
        );
        this.setListAdapter(listAdapter);
    }


    public void onDeleteButtonClick(View view) {
        View v = (View) view.getParent();
        TextView taskTextView = (TextView) v.findViewById(R.id.list);
        String task = taskTextView.getText().toString();


        String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                TaskContract.TABLE, TaskContract.Columns.TASK, task);


        helper = new TaskDbHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        sqlDB.execSQL(sql);
        updateUI();
    }



    public void update (View view) {
        View u = (View) view.getParent();
        TextView taskUpdateView = (TextView) u.findViewById(R.id.list);
        final int id = u.getId();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Data");
        final EditText inputField = new EditText(this);
        builder.setView(inputField);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String update = inputField.getText().toString();

                helper = new TaskDbHelper(MainActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();
                //db.execSQL(sql);
                ContentValues values = new ContentValues();

                values.clear();
                values.put(TaskContract.Columns._ID, id);
                values.put(TaskContract.Columns.TASK, update);
                db.replace(TaskContract.TABLE, null, values);
                updateUI();
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }
}