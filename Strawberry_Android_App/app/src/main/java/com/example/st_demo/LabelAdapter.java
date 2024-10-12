package com.example.st_demo;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

class LabelsAdapter extends ArrayAdapter<String> {

    public LabelsAdapter(Context context, List<String> labels) {
        super(context, android.R.layout.simple_list_item_1, labels);
    }
}
