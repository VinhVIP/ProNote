package com.vinh.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class TypeNoteActivity extends AppCompatActivity {

    private RecyclerView recyclerType;
    private List<Note.TypeNote> listTypes;
    private TypeNoteAdapter adapter;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    public Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_note);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Define.RESULT_CODE_CHANGE);
                finish();
            }
        });

        database = new Database(this);
        listTypes = database.getAllTypeNote();
        adapter = new TypeNoteAdapter(this, listTypes);

        recyclerType = findViewById(R.id.recyclerType);
        recyclerType.setAdapter(adapter);
        recyclerType.setLayoutManager(new LinearLayoutManager(this));

        fab = findViewById(R.id.fab_type);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(-1, "", 0, true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showDialog(final int idType, final String nameType, final int position, final boolean isCreate) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_type);
        final TextInputEditText editText = dialog.findViewById(R.id.edit_type);
        Button btnSave = dialog.findViewById(R.id.btn_save_type);
        if (isCreate) {
            dialog.setTitle("Thêm thể loại:");
        } else {
            dialog.setTitle("Chỉnh sửa:");
            editText.setText(nameType);
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editText.getText().toString().trim();
                if (isCreate) {
                    if (name == null || name.length() == 0) {
                        Toast.makeText(TypeNoteActivity.this, "Bạn chưa nhập tên thể loại!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (database.isExistsTypeNote(name)) {
                            Toast.makeText(TypeNoteActivity.this, "Đã có thể loại này rồi!", Toast.LENGTH_SHORT).show();
                        } else {
                            Note.TypeNote typeNote = new Note.TypeNote(name);
                            int id = database.addTypeNote(typeNote);
                            Toast.makeText(TypeNoteActivity.this, "Đã thêm thể loại!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            listTypes.add(database.getTypeNote(id));
                            adapter.notifyItemInserted(listTypes.size() - 1);
                        }
                    }
                } else {
                    if (!name.equals(nameType)) {
                        if (database.isExistsTypeNote(name)) {
                            Toast.makeText(TypeNoteActivity.this, "Đã có thể loại này rồi!", Toast.LENGTH_SHORT).show();
                        } else {
                            database.updateTypeNote(idType, name);
                            Toast.makeText(TypeNoteActivity.this, "Đã cập nhật thể loại!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            listTypes.set(position, database.getTypeNote(idType));
                            adapter.notifyItemChanged(position);
                        }
                    } else {
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.show();
    }

    public void dialog_delete(final int id, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Toàn bộ ghi chú sẽ được chuyển qua \"Chưa phân loại\". Xác nhận xóa? ");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                database.deleteTypeNote(id);
                adapter.notifyTypeNote(position);
                Toast.makeText(TypeNoteActivity.this, "Đã xóa!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}
