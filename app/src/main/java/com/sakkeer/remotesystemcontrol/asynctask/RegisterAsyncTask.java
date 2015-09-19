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
        try {
            Socket skt = new Socket(connectionModel.getIp(),connectionModel.getPort());

            //Send the message to the server
            OutputStream os = skt.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            String sendMessage = connectionModel.getData();
            bw.write(sendMessage);
            bw.flush();
            Log.d(TAG, "Message sent to the server : " + sendMessage);

            BufferedReader in = new BufferedReader(new
                    InputStreamReader(skt.getInputStream()));
            Log.d(TAG, "Received string:");

            while (!in.ready()) {}
            String result = in.readLine(); // Read one line and output it
            Log.d(TAG, result);
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
