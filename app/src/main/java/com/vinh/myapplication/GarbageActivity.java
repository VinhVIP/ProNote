package com.vinh.myapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GarbageActivity extends AppCompatActivity {

    public Database database;

    private Toolbar toolbar;

    private List<Note> list;
    private RecyclerView recyclerGarbage;
    private GarbageAdapter garbageAdapter;

    public ActionMode actionMode;
    public ActionModeCallback actionModeCallback;

    private int resultCode = Define.RESULT_CODE_CANCEL;
    private int lastPositionClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garbage);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(resultCode);
                finish();
            }
        });

        database = new Database(this);

        recyclerGarbage = findViewById(R.id.recyclerGarbage);
        list = database.getAllGarbage();
        garbageAdapter = new GarbageAdapter(this, list);
        recyclerGarbage.setAdapter(garbageAdapter);
        recyclerGarbage.setLayoutManager(new LinearLayoutManager(this));

        actionModeCallback = new ActionModeCallback();
    }

    public void moveToViewGarbage(int id, int position) {
        lastPositionClick = position;
        Intent in = new Intent(GarbageActivity.this, ViewGarbageActivity.class);
        in.putExtra(Define.KEY_NOTE_ID, id);
        startActivityForResult(in, Define.REQUEST_CODE_GARBAGE_VIEW);
    }

    @Override
    public void onBackPressed() {
        setResult(resultCode);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Define.REQUEST_CODE_GARBAGE_VIEW){
            if(resultCode != Define.RESULT_CODE_CANCEL){
                list.remove(lastPositionClick);
                garbageAdapter.notifyItemRemoved(lastPositionClick);
            }
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.menu_action_mode_garbage, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_garbage_restore:
                    resultCode = Define.RESULT_CODE_CHANGE;
                    garbageAdapter.restoreSelectionNotes();
                    actionMode.finish();
                    return true;
                case R.id.action_garbage_delete:
                    dialog_delete_forever();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }

        private void dialog_delete_forever() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(GarbageActivity.this);
            builder.setTitle("Các ghi chú sẽ bị xóa vĩnh viễn, không thể khôi phục");
            builder.setPositiveButton("XÓA", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (garbageAdapter.hasLockNote()) confirmPassword();
                    else {
                        garbageAdapter.deleteSelectionNotes();
                        actionMode.finish();
                    }
                }
            });
            builder.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }

        private void confirmPassword() {
            Helper.dialogConfirmPassword(GarbageActivity.this, new Runnable() {
                @Override
                public void run() {
                    garbageAdapter.deleteSelectionNotes();
                    actionMode.finish();;
                }
            });
        }
    }

}
