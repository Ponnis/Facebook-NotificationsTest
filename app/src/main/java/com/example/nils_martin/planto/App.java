package com.example.nils_martin.planto;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class App extends AppCompatActivity{
    public static final String CHANNEL_1_ID = "Channel 1";
    private TextView textView;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        setContentView(R.layout.app_view);
    }
    private void createNotificationChannel (){
        //Check if IceCreamSandwich or Api too low
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID,"channel1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("We miss you at TICTACTOE, come back!");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }


}
