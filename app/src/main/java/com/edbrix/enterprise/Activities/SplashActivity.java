package com.edbrix.enterprise.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edbrix.enterprise.MainActivity;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.baseclass.BaseActivity;

public class SplashActivity extends BaseActivity {

    private ProgressBar infoProgressBar;
    private TextView infoTextView;
    private TextView tVersion;
    private String vNm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        infoProgressBar = (ProgressBar) findViewById(R.id.infoProgressBar);
        infoTextView = (TextView) findViewById(R.id.infoTextView);
        tVersion = (TextView) findViewById(R.id.textView_version);

//         Set Application Version
        try {
            vNm =getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

        } catch (PackageManager.NameNotFoundException e) {

            Log.v("Splash.java", "Buid version not generated: Exception = " + e.getMessage());
        }
        tVersion.setText("Version " + vNm);

        new SplashTimeoutTask().execute(1500);
    }

    class SplashTimeoutTask extends AsyncTask<Integer, Void, Void> {

        private static final int TimeoutSleepInterval = 200;

        @Override
        protected Void doInBackground(Integer... params) {
            int count = 0;
            int maxCount = params[0] / TimeoutSleepInterval;

            while (count < maxCount) {
                try {
                    Thread.sleep(TimeoutSleepInterval);
                } catch (InterruptedException e) {
                    ; // Ignore
                }
                count++;
                if (this.isCancelled()) {
                    break;
                }
                if (false) {
                    break;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Intent mainIntent = new Intent();
            mainIntent.setClass(SplashActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            finish();
        }
    }
}
