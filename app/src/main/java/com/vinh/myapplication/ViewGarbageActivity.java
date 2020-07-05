package com.vinh.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ViewGarbageActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvTitle, tvContent, tvTimeCreate, tvTimeEdit, tvType;

    private Database database;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_garbage);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Define.RESULT_CODE_CANCEL);
                finish();
            }
        });

        database = new Database(this);

        tvTitle = findViewById(R.id.note_garbage_title);
        tvContent = findViewById(R.id.note_garbage_content);
        tvType = findViewById(R.id.note_garbage_type);
        tvTimeCreate = findViewById(R.id.note_garbage_time_create);
        tvTimeEdit = findViewById(R.id.note_garbage_time);
        Helper.setTextSizeForTitleAndContent(tvTitle, tvContent);

        getData();
    }

    private void getData(){
        if (getIntent().hasExtra(Define.KEY_NOTE_ID)){
            id = getIntent().getIntExtra(Define.KEY_NOTE_ID, -1);
            Note note = database.getNote(id);

            tvTitle.setText(note.getNote_title());
            tvContent.setText(note.getNote_content());
            tvType.setText(database.getTypeNote(note.getNote_type()).getType_name());
            tvTimeCreate.setText(note.getNote_time_create());
            tvTimeEdit.setText(note.getNote_time());
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Define.RESULT_CODE_CANCEL);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_garbage, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_garbage_restore:
                database.moveToDefault(id);
                setResult(Define.RESULT_CODE_RESTORE);
                finish();
            case R.id.action_view_garbage_delete:
                database.deleteNote(id);
                setResult(Define.RESULT_CODE_DELETE_FOREVER);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
