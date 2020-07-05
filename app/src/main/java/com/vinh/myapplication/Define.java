package com.vinh.myapplication;

public class Define {
    public static final String KEY_NOTE_ID = "Note_ID";
    public static final String KEY_TYPE_ID = "Type_ID";
    public static final String KEY_NOTE_CMD = "Command";

    public static final String CMD_CREATE_NOTE = "Create_Note";
    public static final String CMD_CHANGE_NOTE = "Change_Note";
    public static final String CMD_CHANGE_TYPE_NOTE = "Change_Type_Note";

    public static final int REQUEST_CODE_MAIN_VIEW = 12;
    public static final int REQUEST_CODE_VIEW_EDIT = 23;
    public static final int REQUEST_CODE_VIEW_TYPE = 25;
    public static final int REQUEST_CODE_MAIN_TYPE = 15;
    public static final int REQUEST_CODE_MAIN_GARBAGE = 16;
    public static final int REQUEST_CODE_GARBAGE_VIEW = 67;
    public static final int REQUEST_CODE_SETTING = 68;


    public static final int RESULT_CODE_CANCEL = 100;
    public static final int RESULT_CODE_CHANGE = 111;
    public static final int RESULT_CODE_CREATE = 123;
    public static final int RESULT_CODE_DELETE_FOREVER = 134;
    public static final int RESULT_CODE_RESTORE = 143;

    public static final String BUG = "BUG";
    public static final int INT_BUG = -999;

    public static final int TYPE_ALL = -1302;
    public static final int TYPE_FAVORITE = -602;
    public static final int TYPE_LOCK = -15069;
    public static final int TYPE_GARBAGE = -830;


    public static int currentTypeNote = -1;

    public static int setting_id;
    public static int size_title = 16;
    public static int size_content = 16;
    public static String password = "";
    public static String password2 = "";
    public static int order_type = 0;
    /*
    0: Time Edit
    1: Time Create
    2: Name
     */
    public static boolean isAscending = true;

    public Define() {
    }
}
