package com.vinh.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditNoteActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editTextTitle, editTextContent;
    private Database database;

    private int note_id = -1;
    private String title, content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        database = new Database(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNotChange()) {
                    Intent in = new Intent();
                    in.putExtra(Define.KEY_NOTE_ID, note_id == -1 ? Define.INT_BUG : note_id);
                    setResult(Define.RESULT_CODE_CANCEL, in);
                    finish();
                } else {
                    confirmSave();
                }
            }
        });

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        Helper.setTextSizeForTitleAndContent(editTextTitle, editTextContent);

        getNoteData();
    }

    private void getNoteData() {
        title = content = "";
        note_id = -1;

        Intent intent = getIntent();
        if (intent.hasExtra(Define.KEY_NOTE_CMD)) {
            String cmd = intent.getStringExtra(Define.KEY_NOTE_CMD);

            if (cmd.equals(Define.CMD_CHANGE_NOTE)) {
                note_id = intent.getIntExtra(Define.KEY_NOTE_ID, -2);
                Note note = database.getNote(note_id);
                title = note.getNote_title();
                content = note.getNote_content();
            }
        }
        editTextTitle.setText(title);
        editTextContent.setText(content);
    }

    @Override
    public void onBackPressed() {
        if (isNotChange()) {
            Intent in = new Intent();
            in.putExtra(Define.KEY_NOTE_ID, note_id == -1 ? Define.INT_BUG : note_id);
            setResult(Define.RESULT_CODE_CANCEL, in);
            finish();
        } else {
            confirmSave();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_save:
                if (checkNote()) saveNote();
                break;
            case R.id.button_attachment:
                Toast.makeText(this, "Attachment", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveNote() {
        String cmd = getIntent().getStringExtra(Define.KEY_NOTE_CMD);
        if (cmd.equals(Define.CMD_CREATE_NOTE)) {
            Note note = new Note(Define.currentTypeNote, editTextTitle.getText().toString(), editTextContent.getText().toString(), Helper.getCurrentTime(), Helper.getCurrentTime());
            int insertId = database.addNote(note);
            note_id = insertId;

            Intent intent = new Intent();
            intent.putExtra(Define.KEY_NOTE_ID, insertId);
            setResult(Define.RESULT_CODE_CREATE, intent);
            finish();
        } else if (cmd.equals(Define.CMD_CHANGE_NOTE)) {
            Note note = database.getNote(note_id);
            note.setNote_title(editTextTitle.getText().toString());
            note.setNote_content(editTextContent.getText().toString());
            note.setNote_time(Helper.getCurrentTime());

            database.updateNote(note_id, note);
            Intent intent = new Intent();
            intent.putExtra(Define.KEY_NOTE_ID, note_id);
            setResult(Define.RESULT_CODE_CHANGE, intent);
            finish();
        }
    }

    public void confirmSave() {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Note...");
        dialog.setContentView(R.layout.alert_dialog);
        TextView dialogContent = dialog.findViewById(R.id.dialog_content);
        dialogContent.setText("Lưu thay đổi?");

        Button btnBack = dialog.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Define.RESULT_CODE_CANCEL);
                finish();
            }
        });
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        Button btnSave = dialog.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });
        dialog.show();
    }

    public boolean checkNote() {
        String stringTitle = editTextTitle.getText().toString();
        String stringContent = editTextContent.getText().toString();

        if (stringTitle.trim().length() == 0) {
            editTextTitle.setText("…");
        }

        if (stringContent.trim().length() == 0) {
            editTextContent.setText("…");
        }
        return true;
    }

    private boolean isNotChange() {
        if (title.equals(editTextTitle.getText().toString()) && content.equals(editTextContent.getText().toString())) {
            return true;
        }
        return false;
    }
}
