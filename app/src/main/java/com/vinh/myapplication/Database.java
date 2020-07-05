package com.vinh.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.SecureRandomSpi;
import java.sql.SQLClientInfoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    // NOTE DATABASE
    private static final String DATABASE_NAME = "NOTE_DATABASE";

    private static final String TABLE_NOTE = "Note";
    private static final String COLUMN_NOTE_ID = "note_id";
    private static final String COLUMN_NOTE_TYPE = "note_type";
    private static final String COLUMN_NOTE_TITLE = "note_title";
    private static final String COLUMN_NOTE_CONTENT = "note_content";
    private static final String COLUMN_NOTE_TIME_CREATE = "note_time_create";
    private static final String COLUMN_NOTE_TIME = "note_time";
    private static final String COLUMN_NOTE_STAR = "note_star";
    private static final String COLUMN_NOTE_LOCK = "note_lock";
    private static final String COLUMN_NOTE_GARBAGE = "garbage";


    // TYPE DATABASE
    private static final String TABLE_TYPE = "Type";
    private static final String COLUMN_TYPE_ID = "type_id";
    private static final String COLUMN_TYPE_NAME = "type_name";

    // SETTING DATABASE
    private static final String TABLE_SETTING = "settings";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SIZE_TITLE = "size_title";
    private static final String COLUMN_SIZE_CONTENT = "size_content";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PASSWORD_2 = "password_2";
    private static final String COLUMN_ORDER_TYPE = "order_type";
    private static final String COLUMN_ORDER = "orders";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NOTE + "( "
                + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NOTE_TYPE + " INTEGER, "
                + COLUMN_NOTE_TITLE + " TEXT, "
                + COLUMN_NOTE_CONTENT + " TEXT, "
                + COLUMN_NOTE_TIME_CREATE + " TEXT, "
                + COLUMN_NOTE_TIME + " TEXT, "
                + COLUMN_NOTE_STAR + " INTEGER, "
                + COLUMN_NOTE_LOCK + " INTEGER, "
                + COLUMN_NOTE_GARBAGE + " INTEGER "
                + ")";
        db.execSQL(query);

        query = "CREATE TABLE " + TABLE_TYPE + "( "
                + COLUMN_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TYPE_NAME + " TEXT "
                + ")";
        db.execSQL(query);

        query = "CREATE TABLE " + TABLE_SETTING + "( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SIZE_TITLE + " INTEGER, "
                + COLUMN_SIZE_CONTENT + " INTEGER, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_PASSWORD_2 + " TEXT, "
                + COLUMN_ORDER_TYPE + " INTEGER, "
                + COLUMN_ORDER + " INTEGER "
                + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NOTE;
        db.execSQL(query);
        query = "DROP TABLE IF EXISTS " + TABLE_TYPE;
        db.execSQL(query);
        query = "DROP TABLE IF EXISTS " + TABLE_SETTING;
        db.execSQL(query);
        onCreate(db);
    }

    public int addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NOTE_TYPE, note.getNote_type());
        values.put(COLUMN_NOTE_TITLE, note.getNote_title());
        values.put(COLUMN_NOTE_CONTENT, note.getNote_content());
        values.put(COLUMN_NOTE_TIME_CREATE, note.getNote_time_create());
        values.put(COLUMN_NOTE_TIME, note.getNote_time());
        values.put(COLUMN_NOTE_STAR, note.isStar());
        values.put(COLUMN_NOTE_LOCK, note.isLock());
        values.put(COLUMN_NOTE_GARBAGE, note.isInGarbage());

        long id = db.insert(TABLE_NOTE, null, values);
        db.close();
        return (int) id;
    }

    public Note getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTE, new String[]{COLUMN_NOTE_ID, COLUMN_NOTE_TYPE, COLUMN_NOTE_TITLE, COLUMN_NOTE_CONTENT, COLUMN_NOTE_TIME_CREATE, COLUMN_NOTE_TIME, COLUMN_NOTE_STAR, COLUMN_NOTE_LOCK, COLUMN_NOTE_GARBAGE}, COLUMN_NOTE_ID + "=?", new String[]{id + ""}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return null;
        }

        int note_id = cursor.getInt(0);
        int type = cursor.getInt(1);
        String title = cursor.getString(2);
        String content = cursor.getString(3);
        String time_create = cursor.getString(4);
        String time = cursor.getString(5);
        boolean isStar = cursor.getInt(6) == 1;
        boolean isLock = cursor.getInt(7) == 1;
        boolean inGarbage = cursor.getInt(8) == 1;

        Note note = new Note(note_id, type, title, content, time_create, time, isStar, isLock, inGarbage);
        cursor.close();
        return note;
    }

    public List<Note> getAllNotes() {
        List<Note> listNotes = new ArrayList<Note>();
        String selectQuery = "SELECT * FROM " + TABLE_NOTE + " WHERE " + COLUMN_NOTE_GARBAGE + "=0";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int note_id = cursor.getInt(0);
                int type = cursor.getInt(1);
                String title = cursor.getString(2);
                String content = cursor.getString(3);
                String time_create = cursor.getString(4);
                String time = cursor.getString(5);
                boolean isStar = cursor.getInt(6) == 1;
                boolean isLock = cursor.getInt(7) == 1;
                boolean inGarbage = cursor.getInt(8) == 1;

                Note note = new Note(note_id, type, title, content, time_create, time, isStar, isLock, inGarbage);
                listNotes.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return listNotes;
    }

    public List<Note> getNotesByType(int typeId) {
        List<Note> list = new ArrayList<Note>();
        String query = "SELECT * FROM " + TABLE_NOTE + " WHERE " + COLUMN_NOTE_TYPE + "=" + typeId + " AND " + COLUMN_NOTE_GARBAGE +"=0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int note_id = cursor.getInt(0);
                int type = cursor.getInt(1);
                String title = cursor.getString(2);
                String content = cursor.getString(3);
                String time_create = cursor.getString(4);
                String time = cursor.getString(5);
                boolean isStar = cursor.getInt(6) == 1;
                boolean isLock = cursor.getInt(7) == 1;
                boolean inGarbage = cursor.getInt(8) == 1;

                Note note = new Note(note_id, type, title, content, time_create, time, isStar, isLock, inGarbage);
                list.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public List<Note> getAllFavoriteNotes() {
        List<Note> list = new ArrayList<Note>();
        String query = "SELECT * FROM " + TABLE_NOTE + " WHERE " + COLUMN_NOTE_STAR + "=1" + " AND " + COLUMN_NOTE_GARBAGE +"=0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int note_id = cursor.getInt(0);
                int type = cursor.getInt(1);
                String title = cursor.getString(2);
                String content = cursor.getString(3);
                String time_create = cursor.getString(4);
                String time = cursor.getString(5);
                boolean isStar = cursor.getInt(6) == 1;
                boolean isLock = cursor.getInt(7) == 1;
                boolean inGarbage = cursor.getInt(8) == 1;

                Note note = new Note(note_id, type, title, content, time_create, time, isStar, isLock, inGarbage);
                list.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public List<Note> getAllLockNotes() {
        List<Note> list = new ArrayList<Note>();
        String query = "SELECT * FROM " + TABLE_NOTE + " WHERE " + COLUMN_NOTE_LOCK + "=1" + " AND " + COLUMN_NOTE_GARBAGE +"=0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int note_id = cursor.getInt(0);
                int type = cursor.getInt(1);
                String title = cursor.getString(2);
                String content = cursor.getString(3);
                String time_create = cursor.getString(4);
                String time = cursor.getString(5);
                boolean isStar = cursor.getInt(6) == 1;
                boolean isLock = cursor.getInt(7) == 1;
                boolean inGarbage = cursor.getInt(8) == 1;

                Note note = new Note(note_id, type, title, content, time_create, time, isStar, isLock, inGarbage);
                list.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public List<Note> getAllGarbage() {
        List<Note> list = new ArrayList<Note>();
        String query = "SELECT * FROM " + TABLE_NOTE + " WHERE " + COLUMN_NOTE_GARBAGE +"=1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int note_id = cursor.getInt(0);
                int type = cursor.getInt(1);
                String title = cursor.getString(2);
                String content = cursor.getString(3);
                String time_create = cursor.getString(4);
                String time = cursor.getString(5);
                boolean isStar = cursor.getInt(6) == 1;
                boolean isLock = cursor.getInt(7) == 1;
                boolean inGarbage = cursor.getInt(8) == 1;

                Note note = new Note(note_id, type, title, content, time_create, time, isStar, isLock, inGarbage);
                list.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }



    public void updateNote(int id, Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NOTE_TITLE, note.getNote_title());
        values.put(COLUMN_NOTE_TYPE, note.getNote_type());
        values.put(COLUMN_NOTE_CONTENT, note.getNote_content());
        values.put(COLUMN_NOTE_TIME_CREATE, note.getNote_time_create());
        values.put(COLUMN_NOTE_TIME, note.getNote_time());
        values.put(COLUMN_NOTE_STAR, note.isStar());
        values.put(COLUMN_NOTE_LOCK, note.isLock());
        values.put(COLUMN_NOTE_GARBAGE, note.isInGarbage()?1:0);

        db.update(TABLE_NOTE, values, COLUMN_NOTE_ID + "=?", new String[]{id + ""});
    }

    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTE, COLUMN_NOTE_ID + "=?", new String[]{id + ""});
        db.close();
    }

    public void moveToGarbage(int id){
        Note note = getNote(id);
        note.setInGarbage(true);
        updateNote(id, note);
    }

    public void moveToDefault(int id){
        Note note = getNote(id);
        note.setInGarbage(false);
        updateNote(id, note);
    }


    public int addTypeNote(Note.TypeNote typeNote) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TYPE_NAME, typeNote.getType_name());
        long id = db.insert(TABLE_TYPE, null, values);
        db.close();
        return (int) id;
    }

    public Note.TypeNote getTypeNote(int id) {
        if (id < 0) {
            return new Note.TypeNote(-1, "Unknown");
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TYPE, new String[]{COLUMN_TYPE_ID, COLUMN_TYPE_NAME}, COLUMN_TYPE_ID + "=?", new String[]{id + ""}, null, null, null, null);

        if (cursor.moveToFirst()) {
            Note.TypeNote typeNote = new Note.TypeNote(cursor.getInt(0), cursor.getString(1));
            return typeNote;

        }
        return new Note.TypeNote(-1, "Unknown");
    }

    public boolean isExistsTypeNote(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TYPE, new String[]{COLUMN_TYPE_ID, COLUMN_TYPE_NAME}, COLUMN_TYPE_NAME + "=?", new String[]{name}, null, null, null, null);
        if (cursor.moveToFirst()) return true;
        return false;
    }

    public boolean isExistsTypeNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TYPE, new String[]{COLUMN_TYPE_ID, COLUMN_TYPE_NAME}, COLUMN_TYPE_ID + "=?", new String[]{id + ""}, null, null, null, null);
        if (cursor.moveToFirst()) return true;
        return false;
    }

    public List<Note.TypeNote> getAllTypeNote() {
        List<Note.TypeNote> list = new ArrayList<Note.TypeNote>();
        String query = "SELECT * FROM " + TABLE_TYPE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Note.TypeNote typeNote = new Note.TypeNote(cursor.getInt(0), cursor.getString(1));
                list.add(typeNote);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public void updateTypeNote(int id, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE_NAME, newName);
        db.update(TABLE_TYPE, values, COLUMN_TYPE_ID + "=?", new String[]{id + ""});
        db.close();
    }

    public void deleteTypeNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TYPE, COLUMN_TYPE_ID + "=?", new String[]{id + ""});

        List<Note> list = getNotesByType(id);
        for (Note note : list) {
            note.setNote_type(-1);
            updateNote(note.getNote_id(), note);
        }
        db.close();
    }

    /*
    Settings table
     */
    public boolean isExistsSetting() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SETTING;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) return true;
        return false;
    }

    public void declareDefaultSetting() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SIZE_TITLE, 17);
        values.put(COLUMN_SIZE_CONTENT, 15);
        values.put(COLUMN_PASSWORD, "");
        values.put(COLUMN_PASSWORD_2, "");
        values.put(COLUMN_ORDER_TYPE, 0);
        values.put(COLUMN_ORDER, 1);

        db.insert(TABLE_SETTING, null, values);
        db.close();
    }

    public void getSetting() {
        if (!isExistsSetting()) declareDefaultSetting();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SETTING;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        Define.setting_id = cursor.getInt(0);
        Define.size_title = cursor.getInt(1);
        Define.size_content = cursor.getInt(2);
        Define.password = cursor.getString(3);
        Define.password2 = cursor.getString(4);
        Define.order_type = cursor.getInt(5);
        Define.isAscending = cursor.getInt(6) == 1;

        db.close();
    }

    public void updateSetting() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SIZE_TITLE, Define.size_title);
        values.put(COLUMN_SIZE_CONTENT, Define.size_content);
        values.put(COLUMN_PASSWORD, Define.password);
        values.put(COLUMN_PASSWORD_2, Define.password2);
        values.put(COLUMN_ORDER_TYPE, Define.order_type);
        values.put(COLUMN_ORDER, Define.isAscending ? 1 : 0);

        db.update(TABLE_SETTING, values, COLUMN_ID + "=?", new String[]{Define.setting_id + ""});
    }
}

