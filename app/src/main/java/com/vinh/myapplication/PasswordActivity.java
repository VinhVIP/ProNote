package com.vinh.myapplication;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;

public class PasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private LinearLayout itemCreatePass, itemChangePass;

    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        database = new Database(this);

        declare();
    }

    private void declare() {
        itemCreatePass = findViewById(R.id.password_create);
        itemChangePass = findViewById(R.id.password_change);

        itemCreatePass.setOnClickListener(this);
        itemChangePass.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.password_create:
                if (Define.password != null && Define.password.length() > 0) {
                    Toast.makeText(PasswordActivity.this, "Bạn đã tạo mật khẩu trước đó", Toast.LENGTH_SHORT).show();
                } else {
                    CreatePassword createPassword = new CreatePassword();
                    createPassword.showDialog();
                }
                break;
            case R.id.password_change:
                if (Define.password.length() > 0) {
                    confirmPassword();
                } else {
                    Toast.makeText(PasswordActivity.this, "Bạn chưa tạo mật khẩu", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void confirmPassword() {
        Helper.dialogConfirmPassword(PasswordActivity.this, new Runnable() {
            @Override
            public void run() {
                new ChangePassword().change();
            }
        });
    }


    // Class tạo mật khẩu
    // Sử dụng song song 2 mật khẩu
    // Nếu quên 1 mật khẩu thì vẫn có thể dùng mật khẩu còn lại để tạo mật khẩu mới
    private class CreatePassword {

        TextInputLayout passCreateWrapper, rePassCreateWrapper, passCreateWrapper2, rePassCreateWrapper2;
        TextInputEditText editPassCreate, editRePassCreate, editPassCreate2, editRePassCreate2;
        Button btnSavePass;
        Dialog dialog;

        CreatePassword() {
            dialog = new Dialog(PasswordActivity.this);
            dialog.setContentView(R.layout.dialog_create_password);

            passCreateWrapper = dialog.findViewById(R.id.wrapper_password_create);
            rePassCreateWrapper = dialog.findViewById(R.id.wrapper_re_password_create);
            passCreateWrapper2 = dialog.findViewById(R.id.wrapper_password_create_2);
            rePassCreateWrapper2 = dialog.findViewById(R.id.wrapper_re_password_create_2);

            editPassCreate = dialog.findViewById(R.id.edit_password_create);
            editRePassCreate = dialog.findViewById(R.id.edit_re_password_create);
            editPassCreate2 = dialog.findViewById(R.id.edit_password_create_2);
            editRePassCreate2 = dialog.findViewById(R.id.edit_re_password_create_2);

//            setTextWatcher(editPassCreate, editRePassCreate, passCreateWrapper, rePassCreateWrapper);
//            setTextWatcher(editPassCreate2, editRePassCreate2, passCreateWrapper2, rePassCreateWrapper2);

            btnSavePass = dialog.findViewById(R.id.btnSavePassword);
            btnSavePass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isEnabled()) {
                        String pass = editPassCreate.getText().toString();
                        String pass2 = editPassCreate2.getText().toString();
                        try {
                            Define.password = Helper.MD5Hashing(pass);
                            Define.password2 = Helper.MD5Hashing(pass2);
                            database.updateSetting();
                            dialog.dismiss();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        void showDialog() {
            dialog.show();
        }

        private boolean isEnabled() {
            String stringPass = editPassCreate.getText().toString();
            String stringRePass = editRePassCreate.getText().toString();
            String stringPass2 = editPassCreate2.getText().toString();
            String stringRePass2 = editRePassCreate2.getText().toString();

            boolean check = stringPass.length() > 0 && stringPass.equals(stringRePass);
            boolean check2 = stringPass2.length() > 0 && stringPass2.equals(stringRePass2);

            return check && check2;
        }

        private CharSequence message(String pass) {
            if (pass == null) return pass;
            if (pass.length() < 4) return "Mật khẩu phải từ 4 kí tự trở lên";
            if (pass.length() > 20) return "Mật khẩu chỉ có nhiều nhất 20 kí tự";
            if (!Helper.isMatchesRegex(pass))
                return "Chỉ cho phép các kí tự chữ cái, số và dấu gạch dưới";
            return null;
        }

        private void setTextWatcher(final TextInputEditText mk, final TextInputEditText rmk, final TextInputLayout layout, final TextInputLayout rlayout) {
            mk.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    layout.setError(message(charSequence.toString()));
                    btnSavePass.setBackgroundResource(isEnabled() ? R.drawable.button_active : R.drawable.button_default);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String s = rmk.getText().toString();
                    if (s.length() > 0 && !editable.toString().equals(s)) {
                        rlayout.setError("Mật khẩu không khớp");
                    } else {
                        rlayout.setError(null);
                    }
                }
            });

            rmk.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    rlayout.setError(message(charSequence.toString()));
                    btnSavePass.setBackgroundResource(isEnabled() ? R.drawable.button_active : R.drawable.button_default);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!editable.toString().equals(mk.getText().toString())) {
                        rlayout.setError("Mật khẩu không khớp");
                    } else {
                        rlayout.setError(null);
                    }
                }
            });
        }
    }

    private class ChangePassword {

        ChangePassword() {
        }

        void change() {
            CreatePassword createPassword = new CreatePassword();
            createPassword.showDialog();
        }
    }
}
