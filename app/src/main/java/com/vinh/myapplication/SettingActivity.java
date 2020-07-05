package com.vinh.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private EditText editSizeTitle, editSizeContent;
    private Button btnTitleSub, btnTitleAdd, btnContentSub, btnContentAdd;
    private LinearLayout itemPassword;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        database = new Database(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSetting();
            }
        });

        declare();
    }

    private void updateSetting(){
        Define.size_title = Math.min(30, Math.max(10, Integer.parseInt(editSizeTitle.getText().toString())));
        Define.size_content = Math.min(30, Math.max(10, Integer.parseInt(editSizeContent.getText().toString())));
        database.updateSetting();
        setResult(Define.RESULT_CODE_CHANGE);
        finish();
    }

    private void declare() {
        editSizeTitle = findViewById(R.id.edit_size_title);
        editSizeContent = findViewById(R.id.edit_size_content);
        editSizeTitle.setText(Define.size_title + "");
        editSizeContent.setText(Define.size_content + "");

        btnTitleSub = findViewById(R.id.btn_setting_size_title_subtract);
        btnTitleAdd = findViewById(R.id.btn_setting_size_title_add);
        btnContentSub = findViewById(R.id.btn_setting_size_content_subtract);
        btnContentAdd = findViewById(R.id.btn_setting_size_content_add);

        itemPassword = findViewById(R.id.item_setting_password);

        btnTitleSub.setOnClickListener(this);
        btnTitleAdd.setOnClickListener(this);
        btnContentSub.setOnClickListener(this);
        btnContentAdd.setOnClickListener(this);
        itemPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int sizeTitle = Integer.parseInt(editSizeTitle.getText().toString());
        int sizeContent = Integer.parseInt(editSizeContent.getText().toString());

        switch (view.getId()) {
            case R.id.btn_setting_size_title_subtract:
                sizeTitle = Math.max(sizeTitle - 1, 10);
                break;
            case R.id.btn_setting_size_title_add:
                sizeTitle = Math.min(sizeTitle + 1, 30);
                break;
            case R.id.btn_setting_size_content_subtract:
                sizeContent = Math.max(sizeContent - 1, 10);
                break;
            case R.id.btn_setting_size_content_add:
                sizeContent = Math.min(sizeContent + 1, 30);
                break;
            case R.id.item_setting_password:
                Intent in = new Intent(SettingActivity.this, PasswordActivity.class);
                startActivity(in);
                break;
        }

        editSizeTitle.setText(sizeTitle + "");
        editSizeContent.setText(sizeContent + "");
    }

    @Override
    public void onBackPressed() {
        updateSetting();
    }
}
