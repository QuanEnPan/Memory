package com.example.q.camara.Statistics;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.q.camara.MessageID;
import com.example.q.camara.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class StatisticsActivity extends ActionBarActivity {

    RecyclerView recyclerView;
    Handler h;
    HttpThread thread1;
    List<StatisticsInfo> statisticsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        recyclerView = (RecyclerView) findViewById(R.id.rView_statistics);

        statisticsInfo = new ArrayList<>();
//
//        {"data":[
//            {"data":"03\/30\/03","opponent":"QuanEn","winner":true},
//            {"data":"03\/30\/04","opponent":"Jordi","winner":false}
//            ]
//        }

        h = new Handler(){
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case MessageID.GET_SUCCESSFUL:
                        Bundle bundle;
                        bundle = msg.getData();

                        try {
                            fillStatistics(bundle.getString("json","N?A"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;

                    case MessageID.GET_FAILED:
                        Toast.makeText(getApplicationContext(), "Get Failed", Toast.LENGTH_LONG).show();
                }
                String s = (String)msg.obj;
                Log.e("++++", "-------"+s);
            }
        };

        thread1 = new HttpThread("GET","users/55689f16dcc442e41b840281/games",null,h,this);
        thread1.start();

        /*statisticsInfo.add(new StatisticsInfo("H","1",false));
        statisticsInfo.add(new StatisticsInfo("E","2",true));
        statisticsInfo.add(new StatisticsInfo("R","3",true));
        statisticsInfo.add(new StatisticsInfo("G","4",true));
        statisticsInfo.add(new StatisticsInfo("T","5",true));
        statisticsInfo.add(new StatisticsInfo("B","6",true));
        statisticsInfo.add(new StatisticsInfo("J","7",true));
        statisticsInfo.add(new StatisticsInfo("L","1",true));
        statisticsInfo.add(new StatisticsInfo("Ñ","2",true));*/


        StatisticsInfoAdapter statisticsInfoAdapter = new StatisticsInfoAdapter(statisticsInfo,this);
        recyclerView.setAdapter(statisticsInfoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
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

    public void fillStatistics(String json) throws JSONException {
        SharedPreferences sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("email","A?N");

//        JSONObject jsonObject = new JSONObject(json);

        System.out.println("hhhhhh");

        JSONObject jsonObject = new JSONObject(json);
//        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
//
//        System.out.println(jsonObject1+" jjjjj");

        JSONArray jsonArray = jsonObject.getJSONArray("data");

        System.out.println(jsonArray+" kkkkk");

        for(int i=0; i<jsonArray.length(); i++){

            JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
            System.out.println(jsonObject2);

            String loser = (String) jsonObject2.get("loser");
            String winner = (String) jsonObject2.get("winner");

            String initDate = (String) jsonObject2.get("initDate");
//            String _id = (String) jsonObject2.get("_id");

            if(loser.equals(user)){
                statisticsInfo.add(new StatisticsInfo("opponent: "+winner,"Date: "+initDate,false));
            }else
                statisticsInfo.add(new StatisticsInfo("opponent: "+loser,"Date: "+initDate,true));

//            System.out.println("initDate: "+initDate+"  _id: "+_id+" user: "+user);
        }
    }
}
