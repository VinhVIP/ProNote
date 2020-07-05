package com.vinh.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class TypeSelectActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private RecyclerView recyclerTypeSelect;
    private TypeSelectAdapter adapter;
    private List<Note.TypeNote> listType;
    private Database database;

    private Note currentNote;

    public boolean isChangeListType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_select);

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


        listType = database.getAllTypeNote();
        listType.add(0, new Note.TypeNote(-1, "Không xác định"));
        recyclerTypeSelect = findViewById(R.id.recyclerTypeSelect);
        adapter = new TypeSelectAdapter(this, listType);
        recyclerTypeSelect.setAdapter(adapter);
        recyclerTypeSelect.setLayoutManager(new LinearLayoutManager(this));

        getData();

    }

    private void getData() {
        Intent intent = getIntent();
        if (intent.hasExtra(Define.KEY_NOTE_CMD)) {
            String cmd = intent.getStringExtra(Define.KEY_NOTE_CMD);
            if (cmd.equals(Define.CMD_CHANGE_NOTE)) {
                int noteID = intent.getIntExtra(Define.KEY_NOTE_ID, -1);
                currentNote = database.getNote(noteID);
            }else if(cmd.equals(Define.CMD_CHANGE_TYPE_NOTE)){
                isChangeListType = true;
            }
        }
    }

    public void returnData(int typeID){
        Intent in = new Intent();
        in.putExtra(Define.KEY_TYPE_ID, typeID);
        setResult(Define.RESULT_CODE_CHANGE, in);
        finish();
    }

    public void changeTypeNote(int typeID) {
        currentNote.setNote_type(typeID);
        database.updateNote(currentNote.getNote_id(), currentNote);
        if(getIntent().hasExtra(Define.BUG)) setResult(Define.RESULT_CODE_CREATE);
        else setResult(Define.RESULT_CODE_CHANGE);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(Define.RESULT_CODE_CANCEL);
        finish();
    }
}
