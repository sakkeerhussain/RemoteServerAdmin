package com.sakkeer.remotesystemcontrol;

import android.app.AlertDialog;
import android.content.Context;
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
                String ipAddress = ip.getText().toString();
                int portNum;
                try {
                    portNum = Integer.valueOf(port.getText().toString());
                }catch (NumberFormatException e){
                    AlertDialog error = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("ERROR")
                            .setMessage("Enter correct port number")
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

                RegisterAsyncTask task = new RegisterAsyncTask(MainActivity.this, new ServerSyncListener() {
                    @Override
                    public void gotSuccessResponse() {
                        AlertDialog error = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("SUCCESS")
                                .setMessage("Successfully registered.\nNow confirm at server. ")
                                .create();
                        error.show();
                    }

                    @Override
                    public void gotFailureResponse() {
                        AlertDialog error = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("ERROR")
                                .setMessage("Server failed!")
                                .create();
                        error.show();
                    }
                });
                task.execute(connectionModel);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
