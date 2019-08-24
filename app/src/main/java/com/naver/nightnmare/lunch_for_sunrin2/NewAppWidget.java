package com.naver.nightnmare.lunch_for_sunrin2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    static String menu;
    private static final String ACTION_BUTTON1 = "com.example.gyu_won.lunch_for_sunrin.Refresh";
    static String current_time;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        views.setTextViewText(R.id.appwidget_text, menu);


        Intent intentSync = new Intent(context, NewAppWidget.class);
        intentSync.setAction(ACTION_BUTTON1); //You need to specify the action for the intent. Right now that intent is doing nothing for there is no action to be broadcasted.

        PendingIntent pendingSync = PendingIntent.getBroadcast(context,0, intentSync, PendingIntent.FLAG_UPDATE_CURRENT); //You need to specify a proper flag for the intent. Or else the intent will become deleted.
        views.setOnClickPendingIntent(R.id.button,pendingSync);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Log.e("1", "1");
        update u = new update(context, appWidgetManager, appWidgetIds);
        u.thread.start();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), NewAppWidget.class.getName());
        int[] appWidgets = appWidgetManager.getAppWidgetIds(thisAppWidget);

//        String action = intent.getAction();
        if(intent.getAction().equals(ACTION_BUTTON1)){
            onUpdate(context,appWidgetManager,appWidgets);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    private static class update {
        Context context;
        AppWidgetManager appWidgetManager;
        int[] appWidgetIds;

        update(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            this.context = context;
            this.appWidgetIds = appWidgetIds;
            this.appWidgetManager = appWidgetManager;
        }

        Handler handler = new Handler();

        Thread thread = new Thread() {
            public void run() {
                menu = "Reloading";
                for (int appWidgetId : appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId);
                }
                menu ="";
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getData(context,appWidgetManager,appWidgetIds);
                    }
                });
            }
        };
    }
    private static void getData(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds){
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd");
        long mNow= System.currentTimeMillis();
        Date mDate = new Date(mNow);;
        current_time = mFormat.format(mDate);
        menu=current_time.substring(0,4)+"."+current_time.substring(4,6)+"."+current_time.substring(6,8)+"\n\n";

        Call<JsonObject> res = NetRetrofit.getInstance().getService().getData("high","B100000658",current_time.substring(4,6),true);
//        Call<JsonObject> res = NetRetrofit.getInstance().getService().getData("high","B100000658","09",true);

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
                        menu+=data.getLunch().get(i).getAsString()+"\n";
                    }
                    for (int appWidgetId : appWidgetIds) {
                        updateAppWidget(context, appWidgetManager, appWidgetId);
                    }

                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Err", t.getMessage());
            }
        });
    }
}

