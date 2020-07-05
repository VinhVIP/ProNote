package com.vinh.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ViewNoteActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fabEdit;
    private TextView tvTitle, tvType, tvContent, tvTimeCreate, tvTime;
    private CoordinatorLayout coordinatorLayout;

    private Database database;

    private int note_id = -1;
    private Note currentNote;
    private int resultCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMainActivity();
            }
        });


        coordinatorLayout = findViewById(R.id.coordinator);
        tvTitle = findViewById(R.id.note_view_title);
        tvType = findViewById(R.id.note_view_type);
        tvContent = findViewById(R.id.note_view_content);
        tvTimeCreate = findViewById(R.id.note_view_time_create);
        tvTime = findViewById(R.id.note_view_time);

        // set Text size
        Helper.setTextSizeForTitleAndContent(tvTitle, tvContent);

        RelativeLayout layout = findViewById(R.id.linear);

        resultCode = Define.RESULT_CODE_CANCEL;
        database = new Database(this);
        getNoteData();

        fabEdit = findViewById(R.id.fab_edit);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewNoteActivity.this, EditNoteActivity.class);
                intent.putExtra(Define.KEY_NOTE_CMD, Define.CMD_CHANGE_NOTE);
                intent.putExtra(Define.KEY_NOTE_ID, note_id);
                startActivityForResult(intent, Define.REQUEST_CODE_VIEW_EDIT);
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewNoteActivity.this, TypeSelectActivity.class);
                intent.putExtra(Define.KEY_NOTE_CMD, Define.CMD_CHANGE_NOTE);
                intent.putExtra(Define.KEY_NOTE_ID, currentNote.getNote_id());
                if (resultCode == Define.RESULT_CODE_CREATE) {
                    intent.putExtra(Define.BUG, Define.INT_BUG);
                }
                startActivityForResult(intent, Define.REQUEST_CODE_VIEW_TYPE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_menu, menu);
        if (currentNote != null) {
            if (currentNote.isStar()) {
                MenuItem itemStar = menu.findItem(R.id.button_star);
                itemStar.setIcon(R.drawable.ic_star_fill);
            }
            if (currentNote.isLock()) {
                MenuItem itemLock = menu.findItem(R.id.button_lock);
                itemLock.setIcon(R.drawable.ic_lock);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_star:
                if (currentNote.isStar()) {
                    currentNote.setStar(false);
                } else {
                    currentNote.setStar(true);
                }
                database.updateNote(currentNote.getNote_id(), currentNote);
                item.setIcon(currentNote.isStar() ? R.drawable.ic_star_fill : R.drawable.ic_star);
                if (resultCode != Define.RESULT_CODE_CREATE) {
                    resultCode = Define.RESULT_CODE_CHANGE;
                }
                break;

            case R.id.button_lock:
                if (currentNote.isLock()) {
                    currentNote.setLock(false);
                } else {
                    if (Define.password.length() == 0) {
                        Toast.makeText(ViewNoteActivity.this, "Bạn chưa tạo mật khẩu!", Toast.LENGTH_SHORT).show();
                    } else {
                        currentNote.setLock(true);
                    }
                }
                database.updateNote(currentNote.getNote_id(), currentNote);
                item.setIcon(currentNote.isLock() ? R.drawable.ic_lock : R.drawable.ic_lock_open);
                if (resultCode != Define.RESULT_CODE_CREATE) {
                    resultCode = Define.RESULT_CODE_CHANGE;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Define.REQUEST_CODE_VIEW_EDIT) {

            if (resultCode == Define.RESULT_CODE_CREATE) {
                int id = data.getIntExtra(Define.KEY_NOTE_ID, -1);
                this.note_id = id;
                currentNote = database.getNote(note_id);
                setTextViewData(id);
                Snackbar.make(coordinatorLayout, "Tạo mới thành công!", Snackbar.LENGTH_SHORT).show();

                this.resultCode = resultCode;
            }

            if (resultCode == Define.RESULT_CODE_CHANGE) {
                int id = data.getIntExtra(Define.KEY_NOTE_ID, -1);
                this.note_id = id;
                currentNote = database.getNote(note_id);
                setTextViewData(id);
                Snackbar.make(coordinatorLayout, "Cập nhật thành công!", Snackbar.LENGTH_SHORT).show();

                if (this.resultCode != Define.RESULT_CODE_CREATE) this.resultCode = resultCode;
            }

            if (resultCode == Define.RESULT_CODE_CANCEL) {
                int id = -1;
                if (data != null) id = data.getIntExtra(Define.KEY_NOTE_ID, -1);
                if (id == Define.INT_BUG) {
                    setResult(Define.RESULT_CODE_CANCEL);
                    finish();
                }

                this.resultCode = Define.RESULT_CODE_CANCEL;
            }
        } else if (requestCode == Define.REQUEST_CODE_VIEW_TYPE) {
            if (resultCode != Define.RESULT_CODE_CANCEL) {
                this.resultCode = resultCode;
                currentNote = database.getNote(currentNote.getNote_id());
                Note.TypeNote typeNote = database.getTypeNote(currentNote.getNote_type());
                tvType.setText(typeNote.getType_name());
            }
        }

    }

    private void getNoteData() {
        currentNote = null;
        Intent intent = getIntent();

        if (intent.hasExtra(Define.KEY_NOTE_CMD)) {
            String cmd = intent.getStringExtra(Define.KEY_NOTE_CMD);

            if (cmd.equals(Define.CMD_CHANGE_NOTE)) {
                note_id = intent.getIntExtra(Define.KEY_NOTE_ID, -1);
                currentNote = database.getNote(note_id);
                setTextViewData(note_id);
            } else if (cmd.equals(Define.CMD_CREATE_NOTE)) {
                Intent in = new Intent(ViewNoteActivity.this, EditNoteActivity.class);
                in.putExtra(Define.KEY_NOTE_CMD, Define.CMD_CREATE_NOTE);
                startActivityForResult(in, Define.REQUEST_CODE_VIEW_EDIT);
            } else {
//                this.resultCode = Define.RESULT_CODE_CANCEL;
            }
        }
    }

    public void setTextViewData(int id) {
        Note note = database.getNote(id);
        tvTitle.setText(note.getNote_title());
        tvType.setText(database.getTypeNote(note.getNote_type()).getType_name());
        tvContent.setText(note.getNote_content());
        tvTimeCreate.setText("Đã tạo: " + Helper.toTimeString(note.getNote_time_create()));
        tvTime.setText("Sửa lần cuối: " + Helper.toTimeString(note.getNote_time()));

        Helper.BBCode(tvContent);
    }


    public void backToMainActivity() {
        if (resultCode == Define.RESULT_CODE_CREATE) {
            Intent in = new Intent();
            in.putExtra(Define.KEY_NOTE_ID, this.note_id);
            setResult(Define.RESULT_CODE_CREATE, in);
        } else if (resultCode == Define.RESULT_CODE_CHANGE) {
            Intent in = new Intent();
            in.putExtra(Define.KEY_NOTE_ID, this.note_id);
            setResult(Define.RESULT_CODE_CHANGE, in);
        } else if (resultCode == Define.RESULT_CODE_CANCEL) {
            setResult(Define.RESULT_CODE_CANCEL);
            // doing nothing
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        backToMainActivity();
//        super.onBackPressed();
    }
}
