package com.vinh.myapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private RecyclerView recyclerView;
    private List<Note> listNotes;
    private NoteAdapter mAdapter;

    private FloatingActionButton fab;

    private Toolbar toolbar;
    public Database database;
    private int positionChange = -1;

    public ActionMode actionMode;
    public ActionModeCallback actionModeCallback;

    private ExpandListAdapter expandListAdapter;
    private ExpandableListView expandableListView;
    private List<ExpandModel> listHeader;
    private HashMap<ExpandModel, List<ExpandModel>> listChild;

    private boolean isViewAllNotesMode;
    private int lastSelectedGroup = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // thiết lập Toolbar và đặt icon menu cho nó
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Tất cả");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        database = new Database(this);
        initSetting();

        mDrawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_open, R.string.navigation_close);
        navigationView = findViewById(R.id.nav_view);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ViewNoteActivity.class);
                intent.putExtra(Define.KEY_NOTE_CMD, Define.CMD_CREATE_NOTE);
                startActivityForResult(intent, Define.REQUEST_CODE_MAIN_VIEW);
//                Toast.makeText(MainActivity.this, "FAB", Toast.LENGTH_SHORT).show();
            }
        });

        isViewAllNotesMode = true;
        recyclerView = findViewById(R.id.recyclerView);
        expandableListView = findViewById(R.id.expand_list_view);
        setDataExpandList();
        expandableListView.expandGroup(0);

        //data();
        // thiết lập RecyclerView
//        listNotes = database.getAllNotes();
        mAdapter = new NoteAdapter(listNotes, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        actionModeCallback = new ActionModeCallback();
    }

    private void initSetting() {
        database.getSetting();
    }

    private void sortListNotes() {
        if (Define.order_type == 2) {
            listNotes = Helper.sortByName(listNotes, Define.isAscending);
        } else {
            listNotes = Helper.sortByTime(listNotes, Define.order_type == 1, Define.isAscending);
        }
        mAdapter = new NoteAdapter(listNotes, MainActivity.this);
        recyclerView.setAdapter(mAdapter);
    }

    public void setDataExpandList() {
        prepareDateExpand();
        populateExpandList();
        if (!database.isExistsTypeNote(Define.currentTypeNote)) {
            expandableListView.expandGroup((lastSelectedGroup == 5) ? 0 : lastSelectedGroup);
        }
    }

    private void prepareDateExpand() {
        listHeader = new ArrayList<ExpandModel>();
        listChild = new HashMap<>();

        ExpandModel mainMenu = new ExpandModel(getResources().getString(R.string.header_expand_0), false, false, -1);
        listHeader.add(mainMenu);
        mainMenu = new ExpandModel(getResources().getString(R.string.header_expand_1), false, false, -1);
        listHeader.add(mainMenu);
        mainMenu = new ExpandModel(getResources().getString(R.string.header_expand_2), false, false, -1);
        listHeader.add(mainMenu);
        mainMenu = new ExpandModel(getResources().getString(R.string.header_expand_3), false, false, -1);
        listHeader.add(mainMenu);
        mainMenu = new ExpandModel(getResources().getString(R.string.header_expand_4), false, false, -1);
        listHeader.add(mainMenu);


        List<Note.TypeNote> listType = database.getAllTypeNote();
        List<ExpandModel> listExpand = new ArrayList<>();
        for (Note.TypeNote typeNote : listType) {
            listExpand.add(new ExpandModel(typeNote.getType_name(), false, false, typeNote.getType_id()));
        }
        listExpand.add(new ExpandModel("Chưa phân loại", false, false, -1));
        mainMenu = new ExpandModel(getResources().getString(R.string.header_expand_5), true, true, -1);
        listHeader.add(mainMenu);

        listChild.put(mainMenu, listExpand);
    }

    private void populateExpandList() {
        expandListAdapter = new ExpandListAdapter(this, listHeader, listChild);
        expandableListView.setAdapter(expandListAdapter);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition == 4) {
                    Intent intent = new Intent(MainActivity.this, TypeNoteActivity.class);
                    startActivityForResult(intent, Define.REQUEST_CODE_MAIN_TYPE);
                    expandableListView.collapseGroup(4);
                    expandableListView.expandGroup(lastSelectedGroup);
                    return;
                }

                if (groupPosition == 3) {
//                    Define.currentTypeNote = Define.TYPE_GARBAGE;
                    expandableListView.collapseGroup(3);
                    Intent in = new Intent(MainActivity.this, GarbageActivity.class);
                    startActivityForResult(in, Define.REQUEST_CODE_MAIN_GARBAGE);
                    return;
                }
                lastSelectedGroup = groupPosition;
                for (int i = 0; i < listHeader.size(); i++) {
                    if (i != groupPosition) expandableListView.collapseGroup(i);
                }

//                Define.currentTypeNote = Define.TYPE_ALL;
                if (groupPosition != 0) fab.setVisibility(View.INVISIBLE);
                else fab.setVisibility(View.VISIBLE);

                switch (groupPosition) {
                    case 0:
                        isViewAllNotesMode = true;
                        Define.currentTypeNote = -1;
                        toolbar.setTitle("Tất cả");
                        listNotes = database.getAllNotes();
                        sortListNotes();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 1:
                        isViewAllNotesMode = false;
                        Define.currentTypeNote = Define.TYPE_FAVORITE;
                        toolbar.setTitle("Yêu thích");
                        listNotes = database.getAllFavoriteNotes();
                        sortListNotes();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 2:
                        isViewAllNotesMode = false;
                        Define.currentTypeNote = Define.TYPE_LOCK;
                        toolbar.setTitle("Khóa");
                        listNotes = database.getAllLockNotes();
                        sortListNotes();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 3:

                        break;
                }
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                if (groupPosition == lastSelectedGroup && groupPosition != 5) {
                    expandableListView.expandGroup(groupPosition);
                }
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                fab.setVisibility(View.VISIBLE);
                isViewAllNotesMode = false;
                toolbar.setTitle(listChild.get(listHeader.get(groupPosition)).get(childPosition).name);
                Define.currentTypeNote = listChild.get(listHeader.get(groupPosition)).get(childPosition).typeID;
                listNotes = database.getNotesByType(Define.currentTypeNote);
                sortListNotes();
                mDrawerLayout.closeDrawers();
//                Toast.makeText(MainActivity.this, "TypeID: " + listChild.get(listHeader.get(groupPosition)).get(childPosition).typeID, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    public void moveToViewActivity(int note_id, int position, String cmd) {
        this.positionChange = position;
        Intent intent = new Intent(MainActivity.this, ViewNoteActivity.class);
        intent.putExtra(Define.KEY_NOTE_ID, note_id);
        intent.putExtra(Define.KEY_NOTE_CMD, cmd);
        startActivityForResult(intent, Define.REQUEST_CODE_MAIN_VIEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Define.REQUEST_CODE_MAIN_VIEW) {
            if (resultCode == Define.RESULT_CODE_CREATE) {
                // thêm note mới vào danh sách
                int id = data.getIntExtra(Define.KEY_NOTE_ID, -1);

                Note note = database.getNote(id);
                int positionInsert = Helper.positionInsert(listNotes, note, Define.order_type != 2, Define.order_type == 1, Define.isAscending);
                listNotes.add(positionInsert, note);
                mAdapter.notifyItemInserted(positionInsert);
                recyclerView.smoothScrollToPosition(positionInsert);

            } else if (resultCode == Define.RESULT_CODE_CHANGE) {
                // thay đổi note
                // xóa vị trí cũ, sau đó thêm note vào vị trí mới
                int id = data.getIntExtra(Define.KEY_NOTE_ID, -1);

                // xóa note ở vị trí cũ
                listNotes.remove(positionChange);
                mAdapter.notifyItemRemoved(positionChange);


                // thêm note vào vị trí mới cho phù hợp với chế độ xem
                // scroll tới vị trí mới
                Note note = database.getNote(id);
                // nếu là chế độ xem Tất cả thì hiển thị
                // hoặc note được thêm phải có cùng Type với List hiện tại
                if (isViewAllNotesMode || note.getNote_type() == Define.currentTypeNote) {
                    int positionInsert = Helper.positionInsert(listNotes, note, Define.order_type != 2, Define.order_type == 1, Define.isAscending);
                    listNotes.add(positionInsert, note);
                    mAdapter.notifyItemInserted(positionInsert);
                    recyclerView.smoothScrollToPosition(positionInsert);
                }
            }
        } else if (requestCode == Define.REQUEST_CODE_MAIN_TYPE) {
            if (resultCode == Define.RESULT_CODE_CHANGE && data != null) {
                int typeID = data.getIntExtra(Define.KEY_TYPE_ID, -5);
                if (typeID != -5) {
                    Toast.makeText(this, "Change list", Toast.LENGTH_SHORT).show();
                    mAdapter.moveSelectionNotes(typeID);
                }
                if (actionMode != null) actionMode.finish();
            }
            setDataExpandList();
        } else if (requestCode == Define.REQUEST_CODE_MAIN_GARBAGE) {
            if (resultCode == Define.RESULT_CODE_CHANGE) {
                Define.currentTypeNote = -1;
                setDataExpandList();
            }
        } else if (requestCode == Define.REQUEST_CODE_SETTING) {
            mAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(mAdapter);
        }

        positionChange = -1;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            case R.id.action_search:
                break;
            case R.id.action_view_setting:
                dialog_view_setting();
                break;
            case R.id.action_setting:
                Intent in = new Intent(MainActivity.this, SettingActivity.class);
                startActivityForResult(in, Define.REQUEST_CODE_SETTING);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void dialog_view_setting() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_setting);

        final RadioButton radioButtonName, radioButtonTimeCreate, radioButtonTimeEdit;
        final RadioButton radioButtonAscending, radioButtonDescending;
        Button btnSave;

        radioButtonName = dialog.findViewById(R.id.radio_button_name);
        radioButtonTimeCreate = dialog.findViewById(R.id.radio_button_time_create);
        radioButtonTimeEdit = dialog.findViewById(R.id.radio_button_time_edit);
        switch (Define.order_type) {
            case 1:
                radioButtonTimeCreate.setChecked(true);
                break;
            case 2:
                radioButtonName.setChecked(true);
                break;
            default:
                radioButtonTimeEdit.setChecked(true);
        }

        radioButtonAscending = dialog.findViewById(R.id.radio_button_ascending);
        radioButtonDescending = dialog.findViewById(R.id.radio_button_descending);
        if (Define.isAscending) radioButtonAscending.setChecked(true);
        else radioButtonDescending.setChecked(true);

        btnSave = dialog.findViewById(R.id.btnSaveSetting);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioButtonTimeEdit.isChecked()) Define.order_type = 0;
                else if (radioButtonTimeCreate.isChecked()) Define.order_type = 1;
                else Define.order_type = 2;

                if (radioButtonAscending.isChecked()) Define.isAscending = true;
                else Define.isAscending = false;

                database.updateSetting();
                sortListNotes();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (actionMode != null) {
            actionMode.finish();
            mAdapter.clearAllSelection();
        } else {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawers();
            } else {
                if (isViewAllNotesMode) {
                    finish();
                } else {
                    isViewAllNotesMode = true;
                    expandableListView.expandGroup(0);
                    toolbar.setTitle("Tất cả");
                    listNotes = database.getAllNotes();
                    mAdapter = new NoteAdapter(listNotes, MainActivity.this);
                    recyclerView.setAdapter(mAdapter);
                }
            }
        }
    }


    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {
            AlertDialog.Builder builder;

            switch (menuItem.getItemId()) {
                case R.id.action_delete:
                    builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Bạn muốn xóa " + mAdapter.listSelectionNotes.size() + " ghi chú ?");
                    builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // delete
                            if (mAdapter.hasLockNote()) confirmPassword();
                            else moveSelectionNotesToGarbage();
                        }
                    });
                    builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                    return true;
                case R.id.action_move:
                    Intent in = new Intent(MainActivity.this, TypeSelectActivity.class);
                    in.putExtra(Define.KEY_NOTE_CMD, Define.CMD_CHANGE_TYPE_NOTE);
                    startActivityForResult(in, Define.REQUEST_CODE_MAIN_TYPE);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearAllSelection();
            actionMode = null;
        }

        private void confirmPassword() {
            class Process implements Runnable{

                @Override
                public void run() {
                    moveSelectionNotesToGarbage();
                }
            }
            Process process = new Process();
            Helper.dialogConfirmPassword(MainActivity.this, process);
        }

        private void moveSelectionNotesToGarbage() {
            mAdapter.deleteSelectionNotes();
            actionMode.finish();
        }
    }
}
