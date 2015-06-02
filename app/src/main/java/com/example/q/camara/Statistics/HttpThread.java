package com.example.q.camara.Statistics;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.q.camara.MessageID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by q on 27/05/15.
 */
public class HttpThread extends Thread {


    private Handler h;
    private String method;
    private JSONObject jsonObject;
    private String url;
    private OutputStream outputStream;
    private Message message;
    private Context context;

    public HttpThread(String method, String url, JSONObject jsonObject, Handler h, Context context) {
        this.method = method;
        this.url = url;
        this.jsonObject = jsonObject;
        this.h = h;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            String ip = "192.168.1.138";
            String port = "3000";
            URL ur = null;

            ur = new URL("http://" + ip + ":" + port + "/" + url);
            Log.e(".....", ur.toString());
            HttpURLConnection httpURLConnection = (HttpURLConnection) ur.openConnection();
            if (method.equals("GET")) {
                httpURLConnection.setDoOutput(false);
                httpURLConnection.setRequestMethod("GET");

                if (httpURLConnection.getResponseCode() == 200) {

                    BufferedReader br = new BufferedReader(new InputStreamReader((httpURLConnection.getInputStream())));
                    StringBuilder sb = new StringBuilder();
                    String output;

                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }

                    message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("json", sb.toString());
                    message.setData(bundle);
                    message.what = MessageID.GET_SUCCESSFUL;
                    h.sendMessage(message);

                    System.out.println(sb.toString()+" sb.toString");

                }else{
                    message = new Message();
                    message.obj = "received" + httpURLConnection.getResponseCode();
                    message.what = MessageID.GET_FAILED;
                    h.sendMessage(message);

                    System.out.println("else");
                }
            }
            else if(method.equals("POST")){
                SharedPreferences sharedPreferences = context.getSharedPreferences("Data", Context.MODE_PRIVATE);
                String cookie = sharedPreferences.getString("cookie","A?N");
                httpURLConnection.setRequestProperty("cookie",cookie);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                outputStream = httpURLConnection.getOutputStream();
                outputStream.write(jsonObject.toString().getBytes("UTF-8"));
                if (httpURLConnection.getResponseCode() == 200) {
                    message = new Message();
                    BufferedReader br = new BufferedReader(new InputStreamReader((httpURLConnection.getInputStream())));
                    StringBuilder sb = new StringBuilder();
                    String output;

                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    message.obj = new JSONObject(sb.toString());
                    message.what = MessageID.POST_SUCCESS;
                    h.sendMessage(message);

                } else {
                    message = new Message();
                    message.obj = "received" + httpURLConnection.getResponseCode();
                    message.what = MessageID.POST_FAILED;
                    h.sendMessage(message);
                }
            }
            else if(method.equals("PUT")){

                System.out.println(ur);

                SharedPreferences sharedPreferences = context.getSharedPreferences("Data", Context.MODE_PRIVATE);
                String cookie = sharedPreferences.getString("cookie","A?N");
                httpURLConnection.setRequestProperty("cookie",cookie);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("PUT");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                outputStream = httpURLConnection.getOutputStream();
                outputStream.write(jsonObject.toString().getBytes("UTF-8"));

                message = new Message();

                if (httpURLConnection.getResponseCode() == 200) {
                    System.out.println(ur+"   200");
                    message.what = MessageID.PUT;
                    h.sendMessage(message);

                } else {
                    message.what = MessageID.PUT;
                    h.sendMessage(message);
                }
            }
            else if(method.equals("DELETE")){

                //Log.e(".......", "ttttt");
                httpURLConnection.setDoOutput(false);
                httpURLConnection.setRequestMethod("DELETE");
                SharedPreferences sharedPreferences = context.getSharedPreferences("Data", Context.MODE_PRIVATE);
                String cookie = sharedPreferences.getString("cookie","A?N");
                httpURLConnection.setRequestProperty("cookie",cookie);

                //httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                //outputStream = httpURLConnection.getOutputStream();
                //outputStream.write(jsonObject.toString().getBytes("UTF-8"));
                //Log.e(",,,,,,", "llllll");
                if (httpURLConnection.getResponseCode() == 200) {
                    message = new Message();
                    message.obj = "ha arribat DELETE correctament";
                    h.sendMessage(message);

                } else {
                    message = new Message();
                    message.obj = "received" + httpURLConnection.getResponseCode();
                    h.sendMessage(message);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}