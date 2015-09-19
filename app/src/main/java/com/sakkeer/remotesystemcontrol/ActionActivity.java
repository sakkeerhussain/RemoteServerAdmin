package com.sakkeer.remotesystemcontrol;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.sakkeer.remotesystemcontrol.asynctask.RegisterAsyncTask;


public class ActionActivity extends AppCompatActivity {

    private Toolbar toolBar;
    private Button shutdownButton;
    private Button restartButton;
    private String ip;
    private int portNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        ip = getIntent().getStringExtra("ip");
        portNum = getIntent().getIntExtra("port", 0);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitle("Remote System Control");
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        shutdownButton = (Button)findViewById(R.id.shutdown_button);
        shutdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String imei = telephonyManager.getDeviceId();

                ConnectionModel connectionModel = new ConnectionModel();
                connectionModel.setIp(ip);
                connectionModel.setPort(portNum);
                connectionModel.setData("SHUTDOWN-" + imei);


                final AlertDialog progress = new AlertDialog.Builder(ActionActivity.this)
                        .setMessage("Syncing...")
                        .setCancelable(false)
                        .create();
                progress.show();

                RegisterAsyncTask task = new RegisterAsyncTask(new ServerSyncListener() {
                    @Override
                    public void gotSuccessResponse(String response) {
                        progress.dismiss();

                        AlertDialog error = new AlertDialog.Builder(ActionActivity.this)
                                .setTitle("SUCCESS")
                                .setMessage("Server switched off successfully.\n"
                                        +"Quiting app now.\n" + response)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActionActivity.this.finish();
                                    }
                                })
                                .create();
                        error.show();
                    }

                    @Override
                    public void gotFailureResponse(String response) {
                        progress.dismiss();
                        AlertDialog error = new AlertDialog.Builder(ActionActivity.this)
                                .setTitle("ERROR")
                                .setMessage("Connection failed!\n" + response)
                                .setCancelable(false)
                                .setPositiveButton("OK", null)
                                .create();
                        error.show();
                    }
                });
                task.execute(connectionModel);
            }
        });


        restartButton = (Button)findViewById(R.id.restart_button);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String imei = telephonyManager.getDeviceId();

                ConnectionModel connectionModel = new ConnectionModel();
                connectionModel.setIp(ip);
                connectionModel.setPort(portNum);
                connectionModel.setData("RESTART-" + imei);


                final AlertDialog progress = new AlertDialog.Builder(ActionActivity.this)
                        .setMessage("Syncing...")
                        .setCancelable(false)
                        .create();
                progress.show();

                RegisterAsyncTask task = new RegisterAsyncTask(new ServerSyncListener() {
                    @Override
                    public void gotSuccessResponse(String response) {
                        progress.dismiss();

                        AlertDialog error = new AlertDialog.Builder(ActionActivity.this)
                                .setTitle("SUCCESS")
                                .setMessage("Server restart successfully.\nQuiting app now." + response)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActionActivity.this.finish();
                                    }
                                })
                                .create();
                        error.show();
                    }

                    @Override
                    public void gotFailureResponse(String response) {
                        progress.dismiss();
                        AlertDialog error = new AlertDialog.Builder(ActionActivity.this)
                                .setTitle("ERROR")
                                .setMessage("Connection failed!\n" + response)
                                .setCancelable(false)
                                .setPositiveButton("OK", null)
                                .create();
                        error.show();
                    }
                });
                task.execute(connectionModel);
            }
        });
    }

}
