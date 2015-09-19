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
public class RegisterAsyncTask extends AsyncTask<ConnectionModel, Void, String> {
    final String TAG = "RegisterAsyncTask";

    private ServerSyncListener listener;

    public RegisterAsyncTask(ServerSyncListener listener){
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(ConnectionModel... params) {
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
            Log.d(TAG, "Result : " + result);

            echoSocket.close();
            out.close();
            in.close();

            return result;

        }catch (Exception e){
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if (this.listener != null) {
            if (response.equals("YES")){
                this.listener.gotSuccessResponse(response);
            }else {
                this.listener.gotFailureResponse(response);
//                this.listener.gotSuccessResponse(response);
            }
        }
    }
}
