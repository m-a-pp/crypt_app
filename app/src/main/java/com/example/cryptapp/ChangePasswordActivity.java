package com.example.cryptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends AppCompatActivity {
    final String INCORRECT_OLD_PASSWORD = "Old password is incorrect";
    final String INCORRECT_PASSWORDS = "Passwords are not same!";

    Button changePasswordButton;
    Button cancelChangePasswordButton;
    EditText changeOldPasswordInputEditText;
    EditText changePasswordReenterInputEditText;
    EditText changePasswordInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        changePasswordButton = findViewById(R.id.changePasswordButton);
        cancelChangePasswordButton = findViewById(R.id.cancelChangePasswordButton);
        changeOldPasswordInputEditText = findViewById(R.id.inoutOldPasswordEditText);
        changePasswordReenterInputEditText = findViewById(R.id.inputReenterChangePasswordEditText);
        changePasswordInputEditText = findViewById(R.id.inputChangePasswordEditText);


        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences(MainActivity.MY_APP_NAME, MODE_PRIVATE);
                String truePass = sharedPref.getString(MainActivity.PASSWORD_PREF, "");

                String oldPass = changeOldPasswordInputEditText.getText().toString();
                String newPass1 = changePasswordInputEditText.getText().toString();
                String newPass2 = changePasswordReenterInputEditText.getText().toString();

                if(truePass.equals(oldPass)){
                    if(newPass1.equals(newPass2)){
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(MainActivity.PASSWORD_PREF, newPass1);
                        editor.commit();
                        finish();
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(), INCORRECT_PASSWORDS, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), INCORRECT_OLD_PASSWORD, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        cancelChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
