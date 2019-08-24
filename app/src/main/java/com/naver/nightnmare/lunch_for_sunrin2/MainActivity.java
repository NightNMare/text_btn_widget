package com.naver.nightnmare.lunch_for_sunrin2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //https://github.com/5d-jh/school-menu-api
    //https://schoolmenukr.ml/api/high/B100000658?month=9&day=16

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd");
    TextView tv;
    Button btn;

    String current_time;
    String menus="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        btn = findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText("Reloading");
                getData();
            }
        });
        getData();
    }
    private void getData(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        current_time = mFormat.format(mDate);
        menus=current_time.substring(0,4)+"."+current_time.substring(4,6)+"."+current_time.substring(6,8)+"\n\n";

        Call<JsonObject> res = NetRetrofit.getInstance().getService().getData("high","B100000658",current_time.substring(4,6),false);
//        Call<JsonObject> res = NetRetrofit.getInstance().getService().getData("high","B100000658","09",false);

        res.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.body() != null) {
                    JsonElement datas = response.body().get("menu");
                    JsonArray root = datas.getAsJsonArray();

                    JsonObject jsonObject = root.get(Integer.parseInt(current_time.substring(6,8))-1).getAsJsonObject();
//                    JsonObject jsonObject = root.get(26).getAsJsonObject();

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
