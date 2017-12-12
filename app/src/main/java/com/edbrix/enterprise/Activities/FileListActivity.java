package com.edbrix.enterprise.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.edbrix.enterprise.Adapters.PDFListAdapter;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.baseclass.BaseActivity;

import java.io.File;
import java.util.ArrayList;

public class FileListActivity extends BaseActivity implements ListView.OnItemClickListener {

    ProgressBar _file_progress_bar;
    private ArrayList<File> fileList;
    private String[] pdflist;
    private ListView pdfListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PDFListAdapter pdfListAdapter;
    private File directory;
    private String fileType;
    private String extention;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdflist);

        Intent intent = getIntent();
        fileType = intent.getStringExtra("title");

        /*if (fileType.equals("playwire"))
        {
            extention = ".mp4";
        } else {
            extention = ".pdf";
        }*/

        _file_progress_bar = findViewById(R.id.file_progress_bar);
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        this.pdfListView = (ListView) findViewById(R.id.pdfListView);
        this.fileList = new ArrayList<File>();
//        showBusyProgress();
        this.directory = Environment.getExternalStorageDirectory();
//        imagelist = directory.listFiles(new FilenameFilter() {
//            public boolean accept(File dir, String name) {
//                return ((name.endsWith(".pdf")));
//            }
//        });
//        getFile(directory);

//        pdflist = new String[fileList.size()];
//        for (int i = 0; i < fileList.size(); i++) {
//            pdflist[i] = fileList.get(i).getName();
//        }

//        pdfListAdapter = new PDFListAdapter(FileListActivity.this,fileList);
//        hideBusyProgress();
//        pdfListView.setAdapter(pdfListAdapter);
//        listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, pdflist));
        this.pdfListView.setOnItemClickListener(this);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPDFList();
            }
        });

        //get PDF Document list from SDCARD
        this.getPDFList();
    }

    private void getPDFList() {
        new GetFilesListAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, directory);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ((PDFListAdapter) adapterView.getAdapter()).toggleSelection(position);
        File fileObject = (File) adapterView.getAdapter().getItem(position);
        if (fileObject != null && fileObject.isFile()) {
            double fileLength = fileObject.length();
            double fileSizeKb = (fileLength / 1024);
            double fileSizeMb = (fileSizeKb / 1024);
            if (fileSizeMb > 25.0) {
                showToast("Unable to process, file size is more than 25 MB.");
            } else {
                // showToast("Loading... Please wait.", Toast.LENGTH_SHORT);
                Intent intent = new Intent();
                Uri uri = Uri.fromFile(fileObject.getAbsoluteFile());
                intent.setData(uri);
                setResult(RESULT_OK, intent);
                finish();
            }
        } else {
            setResult(RESULT_CANCELED, null);
            finish();
        }
    }

    /*public ArrayList<File> getFile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    // fileList.add(listFile[i]);
                    getFile(listFile[i]);

                } else {
                    if (listFile[i].getName().endsWith(".pdf")) {
                        fileList.add(listFile[i]);
                    }
                }

            }
        }
        return fileList;
    }*/

    private class GetFilesListAsyncTask extends AsyncTask<File, Void, ArrayList<File>> {
        private ArrayList<File> fileList;

        @Override
        protected void onPreExecute() {
            // showBusyProgress();
            fileList = new ArrayList<>();
        }

        @Override
        protected ArrayList<File> doInBackground(File... files) {
            getFile(files[0]);
            return fileList;
        }

        @Override
        protected void onPostExecute(ArrayList<File> files) {
            // hideBusyProgress();
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (_file_progress_bar.isShown()) {
                _file_progress_bar.setVisibility(View.INVISIBLE);
            }

            if (files != null && files.size() > 0) {
                pdfListAdapter = new PDFListAdapter(FileListActivity.this, files);
                pdfListView.setVisibility(View.VISIBLE);
                pdfListView.setAdapter(pdfListAdapter);
            } else {
                pdfListView.setVisibility(View.GONE);
            }
        }

        private ArrayList<File> getFile(File dir) {
            // _file_progress_bar.setVisibility(View.VISIBLE);
            File listFile[] = dir.listFiles();
            if (listFile != null && listFile.length > 0) {
                for (int i = 0; i < listFile.length; i++) {

                    if (listFile[i].isDirectory()) {
                        // fileList.add(listFile[i]);
                        getFile(listFile[i]);

                    } else {
                        if (listFile[i].getName().endsWith(".pdf") || listFile[i].getName().endsWith(".doc")) {
                            this.fileList.add(listFile[i]);
                        }
                    }

                }
            }

            return this.fileList;
        }
    }

//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//        if (fileList.size() > 0 && fileList.get(position).isFile()) {
//            Intent intent = new Intent();
//            Uri uri = Uri.fromFile(fileList.get(position).getAbsoluteFile());
//            intent.setData(uri);
//            setResult(RESULT_OK, intent);
//            finish();
//        }else{
//            setResult(RESULT_CANCELED, null);
//            finish();
//        }
//        PackageManager packageManager = getPackageManager();
//        Intent testIntent = new Intent(Intent.ACTION_VIEW);
//        testIntent.setType("application/pdf");
//        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
//        if (list.size() > 0 && imagelist[(int) id].isFile()) {

//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_VIEW);
//            Uri uri = Uri.fromFile(imagelist[(int) id].getAbsoluteFile());
//            intent.setDataAndType(uri, "application/pdf");
//            startActivity(intent);
//        }

//    }
}