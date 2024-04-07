package com.example.cryptapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ChooseFileActivity extends AppCompatActivity {
    FilesListAdapter adapter;
    ListView listView;

    ArrayList<File> files = new ArrayList<>();

    final String rootDir = "/sdcard/";
    String currentDir = rootDir;

    public final static String ACTIVITY_RESULT_FILE_TAG = "file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_file);

        adapter = new FilesListAdapter(this, files);
        listView = findViewById(R.id.listViewChooseFile);
        listView.setAdapter(adapter);

        loadFiles(currentDir);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                File selectedFile = files.get(position);

                if (selectedFile.isDirectory()) {
                    if (selectedFile.getName().equals("..")) {
                        currentDir = (new File(currentDir)).getParent();
                    } else {
                        currentDir = currentDir + "/" + selectedFile.getName();
                    }
                    loadFiles(currentDir);
                } else {
                    closeActivity(selectedFile);
                }
            }
        });
    }

    void loadFiles(String path) {

        files.clear();

        if(!path.equals(rootDir)) {
            files.add(new File(".."));
        }

        File directory = new File(path);
        File[] files = directory.listFiles();

        if(files != null) {
            this.files.addAll(Arrays.asList(files));
        }

        adapter.notifyDataSetChanged();
        listView.setSelectionAfterHeaderView();
    }

    void closeActivity(File file) {
        Intent data = new Intent();
        data.putExtra(ACTIVITY_RESULT_FILE_TAG, file);
        setResult(RESULT_OK, data);
        finish();
    }
}