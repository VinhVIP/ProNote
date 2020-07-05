package com.vinh.myapplication;

import android.view.View;

interface ItemClickListener {
    void onClick(View view, int position, boolean isLongClick);
}
