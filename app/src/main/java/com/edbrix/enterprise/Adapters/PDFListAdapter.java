package com.edbrix.enterprise.Adapters;


import android.widget.BaseAdapter;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edbrix.enterprise.R;

import java.io.File;
import java.util.ArrayList;

public class PDFListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<File> pdfList;
    private SparseBooleanArray selectedItems;


    public PDFListAdapter(Context mContext, ArrayList<File> pdfList) {
        this.mContext = mContext;
        this.pdfList = pdfList;
        this.selectedItems = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return this.pdfList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.pdfList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.pdfList.get(position).length();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        File pdfFileItem = pdfList.get(position);
        double fileLength = pdfFileItem.length();
        double fileSizeKb = (fileLength / 1024);
        double fileSizeMb = (fileSizeKb / 1024);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pdf_list, parent, false);
        TextView fileName = (TextView) view.findViewById(R.id.fileName);
        TextView fileSize = (TextView) view.findViewById(R.id.fileSize);

        fileName.setText(pdfFileItem.getName());
        if (fileSizeMb >= 1) {
            fileSize.setText(String.format("%.2f", fileSizeMb) + " MB");
        } else {
            fileSize.setText(String.format("%.2f", fileSizeKb) + " KB");

        }
        if (selectedItems.get(position)) {
            view.setBackgroundResource(R.color.ColorTabBg);
//            fileName.setTextColor(ContextCompat.getColor(mContext,R.color.colorWhite));
//            fileSize.setTextColor(ContextCompat.getColor(mContext,R.color.colorWhite));
        }
        return view;
    }

    public void toggleSelection(int pos) {

        selectedItems.clear();
        selectedItems.put(pos, true);
        notifyDataSetChanged();
    }

}
