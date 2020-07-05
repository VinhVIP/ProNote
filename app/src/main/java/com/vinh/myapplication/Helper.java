package com.vinh.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class Helper {

    public Helper() {

    }

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        String res = format.format(date);
        return res;
    }

    public static String toTimeString(String time) {
        String[] s = time.split(":");
        time = s[2] + " Th" + s[1] + ", " + s[0] + " - " + s[3] + ":" + s[4];
        return time;
    }

    public static int compareTime(String time1, String time2) {
        String[] s1 = time1.split(":");
        String[] s2 = time2.split(":");
        for (int i = 0; i < s1.length; i++) {
            int i1 = Integer.parseInt(s1[i]);
            int i2 = Integer.parseInt(s2[i]);
            if (i1 < i2) return -1;
            else if (i1 > i2) return 1;
        }
        return 0;
    }

    // Begin sort list
    public static List<Note> sortByTime(List<Note> list, boolean isTimeCreate, boolean isAscending) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (isTimeCreate) {
                    if (Helper.compareTime(list.get(j).getNote_time_create(), list.get(j + 1).getNote_time_create()) < 0) {
                        if (!isAscending) Collections.swap(list, j, j + 1);
                    } else {
                        if (isAscending) Collections.swap(list, j, j + 1);
                    }
                } else {
                    if (Helper.compareTime(list.get(j).getNote_time(), list.get(j + 1).getNote_time()) < 0) {
                        if (!isAscending) Collections.swap(list, j, j + 1);
                    } else {
                        if (isAscending) Collections.swap(list, j, j + 1);
                    }
                }
            }
        }
        return list;
    }

    public static List<Note> sortByName(List<Note> list, boolean isAscending) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (list.get(j).getNote_title().compareTo(list.get(j + 1).getNote_title()) < 0) {
                    if (!isAscending) Collections.swap(list, j, j + 1);
                } else {
                    if (isAscending) Collections.swap(list, j, j + 1);
                }
            }
        }
        return list;
    }
    // End sort list

    public static int positionInsert(List<Note> list, Note note, boolean isTime, boolean isTimeCreate, boolean isAscending) {
        if (isTime) {
            return Helper.positionInsertTime(list, note, isTimeCreate, isAscending);
        } else {
            return Helper.positionInsertName(list, note, isAscending);
        }
    }

    public static int positionInsertName(List<Note> list, Note note, boolean isAscending) {
        int n = list.size();
        for (int i = 0; i < n; i++) {
            if (isAscending) {
                if (list.get(i).getNote_title().compareTo(note.getNote_title()) > 0)
                    return i;
            } else {
                if (list.get(i).getNote_title().compareTo(note.getNote_title()) < 0)
                    return i;
            }
        }
        return n;
    }

    public static int positionInsertTime(List<Note> list, Note note, boolean isTimeCreate, boolean isAscending) {
        int n = list.size();
        for (int i = 0; i < n; i++) {
            if (isTimeCreate) {
                if (isAscending) {
                    if (Helper.compareTime(note.getNote_time_create(), list.get(i).getNote_time_create()) < 0)
                        return i;
                } else {
                    if (Helper.compareTime(note.getNote_time_create(), list.get(i).getNote_time_create()) > 0)
                        return i;
                }
            } else {
                if (isAscending) {
                    if (Helper.compareTime(note.getNote_time(), list.get(i).getNote_time()) < 0)
                        return i;
                } else {
                    if (Helper.compareTime(note.getNote_time(), list.get(i).getNote_time()) > 0)
                        return i;
                }
            }
        }
        return n;
    }

    public static void dialogConfirmPassword(final Activity activity, final Runnable runnable) {
        final Dialog dialog = new Dialog(activity);
        dialog.setTitle("Mở khóa");
        dialog.setContentView(R.layout.dialog_password);
        final TextInputEditText editText = dialog.findViewById(R.id.edit_password);
        Button btnOK = dialog.findViewById(R.id.btnPasswordOK);
        Button btnCancel = dialog.findViewById(R.id.btnPasswordCancel);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.isCorrectPassword(editText.getText().toString())) {
                    dialog.dismiss();
                    // Tránh xung đột Thread
                    activity.runOnUiThread(runnable);

                } else {
                    Toast.makeText(activity, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static boolean isCorrectPassword(String pass) {
        try {
            pass = MD5Hashing(pass);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return pass.equals(Define.password) || pass.equals(Define.password2);
    }

    public static void setTextSizeForTitleAndContent(TextView title, TextView content) {
        title.setTextSize(Define.size_title);
        content.setTextSize(Define.size_content);
    }

    public static String MD5Hashing(String text) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(text.getBytes());
        byte[] digest = messageDigest.digest();

        StringBuilder hexString = new StringBuilder();
        for (byte aMessageDigest : digest) {
            StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
            while (h.length() < 2)
                h.insert(0, "0");
            hexString.append(h);
        }
        return hexString.toString();
    }

    public static boolean isMatchesRegex(String pass) {
        String regex = "^[a-zA-Z0-9_]{4,20}$";
        return Pattern.matches(regex, pass);
    }

    public static void BBCode(TextView textView){
        String s = textView.getText().toString();
        SpannableString spannableString = new SpannableString(s);
        // set style trong nửa khoảng [start, end)
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 1, s.length(), 0);
        textView.setText(spannableString);
    }

    public static void setBold(SpannableString spannableString, int start, int end){
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, 0);
    }
    public static void setUnderline(SpannableString spannableString, int start, int end){
        spannableString.setSpan(new UnderlineSpan(), start, end, 0);
    }
    public static void setItalic(SpannableString spannableString, int start, int end){
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC), start, end, 0);
    }
    public static void setStrike(SpannableString spannableString, int start, int end){
        spannableString.setSpan(new StrikethroughSpan(), start, end, 0);
    }
}
