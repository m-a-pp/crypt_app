package com.example.cryptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewPasswordActivity extends AppCompatActivity {
    Button newPasswordCreateButton;
    EditText newPasswordInputEditText;
    EditText newPasswordReenterInputEditText;

    final String INCORRECT_PASSWORDS = "Passwords are not same!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        newPasswordCreateButton = findViewById(R.id.newPasswordCreateButton);
        newPasswordInputEditText = findViewById(R.id.newPasswordEditText);
        newPasswordReenterInputEditText = findViewById(R.id.newPasswordReenterEditText);

        newPasswordCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass1 = newPasswordInputEditText.getText().toString();
                String pass2 = newPasswordReenterInputEditText.getText().toString();

                if(pass1.equals(pass2)){
                    SharedPreferences sharedPref = getSharedPreferences(MainActivity.MY_APP_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(MainActivity.PASSWORD_PREF, pass1);
                    editor.commit();
                    finish();
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), INCORRECT_PASSWORDS, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}
