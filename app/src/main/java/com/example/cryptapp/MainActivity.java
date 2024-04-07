package com.example.cryptapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    SharedPreferences prefs = null;
    public static final String MY_APP_NAME = "com.example.cryptapp";
    public static final String PASSWORD_PREF = "password";
    public static final String IS_PASSWORD_SET_PREF = "is_password_set";
    public static final String CRYPTED_TAG = "_crptd";
    public String CRYPT_DIRECTORY;
    public String DECRYPT_DIRECTORY;
    final static int CHOOSE_FILE_REQUEST_CODE = 1;

    Context context;
    FilesListAdapter adapter;
    ListView listView;
    ArrayList<File> encryptedFiles = new ArrayList<>();

    FloatingActionButton floatingActionButtonChangePassword;
    FloatingActionButton floatingActionButtonAddFile;

    static {System.loadLibrary("encryptor-native-lib");}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(MY_APP_NAME, MODE_PRIVATE);

        runPasswordRoutine();

        CRYPT_DIRECTORY = getExternalFilesDir(null).getPath() + "/crypt/";
        DECRYPT_DIRECTORY = getExternalFilesDir(null).getPath() + "/decrypt/";

        Log.d("LOGS_PATH", CRYPT_DIRECTORY);
        Log.d("LOGS_PATH", DECRYPT_DIRECTORY);

        setContentView(R.layout.activity_main);
        String[] req_string = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, req_string, 1);

        floatingActionButtonChangePassword = findViewById(R.id.floatingActionButtonChangePassword_);
        floatingActionButtonAddFile = findViewById(R.id.floatingActionButtonAddFile);

        adapter = new FilesListAdapter(this, encryptedFiles);
        listView = findViewById(R.id.recyclerViewEncryptedFiles);
        listView.setAdapter(adapter);

        ActivityCompat.requestPermissions(this, req_string, 2);

        scanEncryptedFiles();
        context = this;
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final File selectedFile = encryptedFiles.get(position);

                final String[] items = {"Decrypt"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (items[item]) {
                            case "Decrypt":
                                int res = decryptFile(selectedFile);
                                if(res != 0) {
                                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
                                    dlgAlert.setMessage("Cannot decrypt file: error code " + res);
                                    dlgAlert.setPositiveButton("OK", null);
                                    dlgAlert.setCancelable(true);
                                    dlgAlert.create().show();
                                    break;
                                }
                                encryptedFiles.remove(position);
                                adapter.notifyDataSetChanged();
                                selectedFile.delete();
                                break;

                            default:
                                break;
                        }
                    }
                });

                builder.show();

                return true;
            }
        });

        encryptor_init();
    }

    public void changePasswordRoutine(View view) {
        Intent changePasswordIntent = new Intent(this, ChangePasswordActivity.class);
        startActivity(changePasswordIntent);
    }

    public void addFileRoutine(View view) {
        Intent addFileIntent = new Intent(this, ChooseFileActivity.class);
        startActivityForResult(addFileIntent, CHOOSE_FILE_REQUEST_CODE);
    }

    private void runPasswordRoutine(){
        Boolean isSet = prefs.getBoolean(IS_PASSWORD_SET_PREF, false);
        if (isSet){
            Intent setInputPasswordIntent = new Intent(this, InputPasswordActivity.class);
            startActivity(setInputPasswordIntent);
        }
        else{
            Intent setNewPasswordIntent = new Intent(this, NewPasswordActivity.class);
            startActivity(setNewPasswordIntent);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(MainActivity.IS_PASSWORD_SET_PREF, true);
            editor.commit();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case CHOOSE_FILE_REQUEST_CODE:

                if (resultCode == RESULT_OK) {
                    File newFile = (File) data.getSerializableExtra(ChooseFileActivity.ACTIVITY_RESULT_FILE_TAG);
                    if(newFile != null) {
                        Pair<Integer, String> res = encryptFile(newFile);
                        if(res.first != 0) {

                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                            dlgAlert.setMessage("error code " + res);
                            dlgAlert.setPositiveButton("OK", null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();

                            break;
                        }

                        encryptedFiles.add(new File(res.second));
                        Collections.reverse(encryptedFiles);
                        adapter.notifyDataSetChanged();
                        listView.setSelectionAfterHeaderView();
                    }
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private Pair<Integer, String> encryptFile(final File file) {
        final String fileToEncrypt = file.getAbsolutePath();

        File enc_file = new File(CRYPT_DIRECTORY, file.getName() + CRYPTED_TAG);
        Log.d("file", fileToEncrypt);
        Log.d("file", enc_file.toString());
        final String encryptedFile = enc_file.getAbsolutePath();
        boolean b_res = false;
        File crypt = enc_file.getParentFile();
        if(!crypt.exists())
        {
            boolean fl = crypt.mkdir();
            Log.d(" crypt dir", Boolean.toString(fl));
        }
        try
        {
            b_res = enc_file.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Log.d("dir", "is maked file? " + b_res);
        int res = encryptor_encryptFile(fileToEncrypt, encryptedFile, file.getName());

        return new Pair<>(res, encryptedFile);
    }

    private int decryptFile(File file) {

        String fileToDecrypt = file.getAbsolutePath();
        File decryptedFile = new File(DECRYPT_DIRECTORY, file.getName().replace(CRYPTED_TAG, ""));
        File decrypt = decryptedFile.getParentFile();

        if (!decrypt.exists()) {
            boolean fl = decrypt.mkdir();
            Log.d(" crypt dir", Boolean.toString(fl));
        }

        try {
            decryptedFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("dir", "decrypted file is not created");
        }

        return encryptor_decryptFile(fileToDecrypt, decryptedFile.getPath(), file.getName());
    }

    private void scanEncryptedFiles() {

        File directory = new File(CRYPT_DIRECTORY);
        File[] files = directory.listFiles();

        if(files != null) {
            for (File file : files) {
                // Если в конце имени файла нет постфикса _encrypted
                if (!file.getName().endsWith(CRYPTED_TAG)) {
                    encryptFile(file);
                }
            }
            encryptedFiles.addAll(Arrays.asList(files));
            adapter.notifyDataSetChanged();
        }
    }

    public native int encryptor_init();
    public native int encryptor_encryptFile(String filename, String newFilename, String iv);
    public native int encryptor_decryptFile(String filename, String newFilename, String iv);
}
