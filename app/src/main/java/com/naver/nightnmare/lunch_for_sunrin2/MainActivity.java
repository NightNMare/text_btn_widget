package com.naver.nightnmare.lunch_for_sunrin2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //https://github.com/5d-jh/school-menu-api
    //https://schoolmenukr.ml/api/high/B100000658?month=9&day=16

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMddHH");
    TextView tv;
    Button btn;
    Switch isBlack;

    String current_time;
    String menus="";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        btn = findViewById(R.id.button);
        isBlack = findViewById(R.id.isBlack);
        prefs = getSharedPreferences("isBlack",MODE_PRIVATE);
        editor= prefs.edit();

        if(prefs.getBoolean("isBlack",false)){
            isBlack.setChecked(true);
            isBlack.setText("Black");
        }

        isBlack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    isBlack.setText("Black");
                    editor.putBoolean("isBlack",true);
                }else{
                    isBlack.setText("White");
                    editor.putBoolean("isBlack",false);
                }
                editor.apply();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText("Reloading");
                getData();
            }
        });
        getData();
    }
    int day;
    int last_day_of_month;
    int month;

    private void getData(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        current_time = mFormat.format(mDate);


        day = Integer.parseInt(current_time.substring(6,8))-1;
        month = Integer.parseInt(current_time.substring(4,6));
        last_day_of_month = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);

        if(Integer.parseInt(current_time.substring(8,10))>15){ //3시 넘으면
            day++;//최대가 마지막날짜
            if(day > last_day_of_month){//3시가 넘고 오늘이 월 마지막 날이면
                month++;
                day=0;
            }
        }

        Call<JsonObject> res = NetRetrofit.getInstance().getService().getData("high","B100000658",month+"",false);

        res.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.body() != null) {
                    JsonElement datas = response.body().get("menu");
                    JsonArray root = datas.getAsJsonArray();

                    //오늘 급식이 없으면
                    while(root.get(day).getAsJsonObject().get("lunch").getAsJsonArray().size()==0){
                        day++;
                        if(day > last_day_of_month){//오늘이 월 마지막날이면
                            month++;
                            day=0;
                        }
                    }
                    JsonObject jsonObject = root.get(day).getAsJsonObject();

                    menus=current_time.substring(0,4)+"."+month+"."+(day+1)+"\n\n";
                    Data data = new Data(jsonObject.get("date").getAsString(),jsonObject.get("breakfast").getAsJsonArray(),jsonObject.get("lunch").getAsJsonArray(),jsonObject.get("dinner").getAsJsonArray());
                    for(int i=0;i<data.getLunch().size();i++){
                        menus+=data.getLunch().get(i).getAsString()+"\n\n";
                    }
                    tv.setText(menus);
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Err", t.getMessage());
            }
        });
    }

}
