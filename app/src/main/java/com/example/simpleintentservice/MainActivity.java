package com.example.simpleintentservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button buttonStart;
    private Button buttonStop;
    private TextView textViewPercent;
    private ProgressBar progressBar;
    private Intent serviceIntent;
    private ResponseReceiver receiver = new ResponseReceiver();

    public class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(SimpleIntentService.ACTION_1)) {
                int value = intent.getIntExtra(SimpleIntentService.PARAM_PERCENT, 0);
                new ShowProgressBarTask().execute(value);
            }
        }
    }

    class ShowProgressBarTask extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected Integer doInBackground(Integer... args) {
            return args[0];
        }
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            progressBar.setProgress(result);
            textViewPercent.setText(result + " % Loaded");
            if (result == 100) {
                textViewPercent.setText("Completed");
                buttonStart.setEnabled(true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewPercent = (TextView) this.findViewById(R.id.textView);
        progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        buttonStart = (Button) this.findViewById(R.id.button);
        buttonStop = (Button)this.findViewById(R.id.button2);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStart.setEnabled(false);
                serviceIntent = new Intent(MainActivity.this, SimpleIntentService.class);
                startService(serviceIntent);
            }
        });
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(serviceIntent!= null)  {
                    SimpleIntentService.shouldStop = true;
                    buttonStart.setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(
                SimpleIntentService.ACTION_1));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }
}