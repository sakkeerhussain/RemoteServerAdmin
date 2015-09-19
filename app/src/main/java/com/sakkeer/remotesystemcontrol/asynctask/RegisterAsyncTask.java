package com.sakkeer.remotesystemcontrol.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sakkeer.remotesystemcontrol.ConnectionModel;
import com.sakkeer.remotesystemcontrol.ServerSyncListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by sakkeer on 19/9/15.
 */
public class RegisterAsyncTask extends AsyncTask<ConnectionModel, Void, Boolean> {
    final String TAG = "RegisterAsyncTask";

    private ProgressDialog progressDialog;
    private ServerSyncListener listener;

    public RegisterAsyncTask(Context context, ServerSyncListener listener){
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setMessage("Syncing....");
        this.progressDialog.setCancelable(false);
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (this.progressDialog != null) {
            this.progressDialog.show();
        }
    }

    @Override
    protected Boolean doInBackground(ConnectionModel... params) {
        ConnectionModel connectionModel = params[0];

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            echoSocket = new Socket(connectionModel.getIp(), connectionModel.getPort());
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    echoSocket.getInputStream()));

            out.println(connectionModel.getData());

            while (!in.ready()) {}
            String result = in.readLine(); // Read one line and output it
            Log.d(TAG, "Result : "+result);

            echoSocket.close();
            out.close();
            in.close();

            if (result.equals("y")){
                return true;
            }else{
                return false;
            }

        }catch (Exception e){
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
        }
        if (this.listener != null) {
            if (aBoolean){
                this.listener.gotSuccessResponse();
            }else {
                this.listener.gotFailureResponse();
            }
        }
    }
}
