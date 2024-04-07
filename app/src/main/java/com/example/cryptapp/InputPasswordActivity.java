package com.example.cryptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InputPasswordActivity extends AppCompatActivity {
    Button inputPasswordButton;
    EditText inputPasswordEditText;
    final String INCORRECT_PASSWORD = "Passwords does not match true password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_password);

        inputPasswordButton = findViewById(R.id.inputPasswordButton);
        inputPasswordEditText = findViewById(R.id.inputPasswordEditText);

        inputPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass1 = inputPasswordEditText.getText().toString();

                SharedPreferences sharedPref = getSharedPreferences(MainActivity.MY_APP_NAME, MODE_PRIVATE);
                String truePass = sharedPref.getString(MainActivity.PASSWORD_PREF, "");

                if(pass1.equals(truePass)){
                    Toast toast = Toast.makeText(getApplicationContext(), "GOOD", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), INCORRECT_PASSWORD, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}
