package com.example.cryptapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;

public class FilesListAdapter extends ArrayAdapter {

    Activity context;
    ArrayList<File> files;

    private static final int VIEW_TYPE_FILE = 1;
    private static final int VIEW_TYPE_FOLDER = 2;

    public FilesListAdapter(Activity context, ArrayList<File> files) {
        super(context, R.layout.listview_item_file, files);
        this.context = context;
        this.files = files;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_item_file, null,true);

        File file = files.get(position);

        if (file != null) {
            TextView fileName = rowView.findViewById(R.id.textViewFileName);
            ImageView fileIcon = rowView.findViewById(R.id.imageViewFileIcon);

            fileName.setText(file.getName().replace(MainActivity.CRYPTED_TAG, ""));

            if(file.isFile()) {
                fileIcon.setImageResource(R.drawable.ic_baseline_file_24);
            } else {
                fileIcon.setImageResource(R.drawable.ic_baseline_folder_24);
            }
        }

        return rowView;
    }
}
