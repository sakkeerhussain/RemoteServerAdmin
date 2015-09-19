package com.sakkeer.remotesystemcontrol;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.TextView;

import com.sakkeer.remotesystemcontrol.asynctask.RegisterAsyncTask;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolBar;
    private Button connectButton;
    private EditText ip;
    private EditText port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitle("Remote System Control");

        ip = (EditText)findViewById(R.id.ip_address);
        port = (EditText)findViewById(R.id.port);

        connectButton = (Button)findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String ipAddress = ip.getText().toString();
                final int portNum;
                try {
                    portNum = Integer.valueOf(port.getText().toString());
                }catch (NumberFormatException e){
                    AlertDialog error = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("ERROR")
                            .setMessage("Enter correct port number")
                            .setCancelable(false)
                            .setPositiveButton("OK", null)
                            .create();
                    error.show();
                    return;
                }
                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String imei = telephonyManager.getDeviceId();

                ConnectionModel connectionModel = new ConnectionModel();
                connectionModel.setIp(ipAddress);
                connectionModel.setPort(portNum);
                connectionModel.setData("REGISTER-" + imei);


                final AlertDialog progress = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Syncing...")
                        .setCancelable(false)
                        .create();
                progress.show();

                RegisterAsyncTask task = new RegisterAsyncTask(new ServerSyncListener() {
                    @Override
                    public void gotSuccessResponse(String response) {
                        progress.dismiss();
                        Intent i = new Intent(MainActivity.this, ActionActivity.class);
                        i.putExtra("ip", ipAddress);
                        i.putExtra("port", portNum);
                        startActivity(i);
                        MainActivity.this.finish();
                    }

                    @Override
                    public void gotFailureResponse(String response) {
                        progress.dismiss();
                        AlertDialog error = new AlertDialog.Builder(MainActivity.this)
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
