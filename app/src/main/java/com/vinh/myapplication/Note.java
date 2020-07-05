package com.vinh.myapplication;

public class Note {
    private int note_id;
    private int note_type;
    private String note_title;
    private String note_content;
    private String note_time_create;
    private String note_time;
    private boolean isStar;
    private boolean isLock;
    private boolean inGarbage;

    private boolean isSelected = false;

    public Note() {
    }

    public Note(int type, String title, String content, String note_time_create, String note_time) {
        this.note_type = type;
        this.note_title = title;
        this.note_content = content;
        this.note_time_create = note_time_create;
        this.note_time = note_time;
    }

    public Note(int id, int type, String title, String content, String note_time_create, String note_time) {
        this.note_id = id;
        this.note_type = type;
        this.note_title = title;
        this.note_content = content;
        this.note_time_create = note_time_create;
        this.note_time = note_time;
    }

    public Note(int id, int type, String title, String content, String note_time_create, String note_time, boolean isStar, boolean isLock) {
        this.note_id = id;
        this.note_type = type;
        this.note_title = title;
        this.note_content = content;
        this.note_time_create = note_time_create;
        this.note_time = note_time;
        this.isStar = isStar;
        this.isLock = isLock;
    }

    public Note(int id, int type, String title, String content, String note_time_create, String note_time, boolean isStar, boolean isLock, boolean inGarbage) {
        this.note_id = id;
        this.note_type = type;
        this.note_title = title;
        this.note_content = content;
        this.note_time_create = note_time_create;
        this.note_time = note_time;
        this.isStar = isStar;
        this.isLock = isLock;
        this.inGarbage = inGarbage;
    }

    public void setInGarbage(boolean inGarbage){
        this.inGarbage = inGarbage;
    }

    public boolean isInGarbage(){
        return inGarbage;
    }
    public int getNote_id() {
        return this.note_id;
    }

    public void setNote_id(int id) {
        this.note_id = id;
    }

    public int getNote_type() {
        return this.note_type;
    }

    public void setNote_type(int type) {
        this.note_type = type;
    }

    public String getNote_title() {
        return this.note_title;
    }

    public void setNote_title(String title) {
        this.note_title = title;
    }

    public String getNote_content() {
        return this.note_content;
    }

    public void setNote_content(String content) {
        this.note_content = content;
    }

    public String getNote_time_create() {
        return note_time_create;
    }

    public void setNote_time_create(String time) {
        this.note_time_create = time;
    }

    public String getNote_time() {
        return this.note_time;
    }

    public void setNote_time(String time) {
        this.note_time = time;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isStar() {
        return isStar;
    }

    public void setStar(boolean isStar) {
        this.isStar = isStar;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }


    public static class TypeNote {
        private int type_id;
        private String type_name = "Hic";

        public TypeNote(int id, String name) {
            type_id = id;
            type_name = name;
        }

        public TypeNote(String name) {
            type_name = name;
        }

        public int getType_id() {
            return type_id;
        }

        public void setType_id(int id) {
            type_id = id;
        }

        public String getType_name() {
            return type_name;
        }

        public void setType_name(String name) {
            type_name = name;
        }
    }
}

