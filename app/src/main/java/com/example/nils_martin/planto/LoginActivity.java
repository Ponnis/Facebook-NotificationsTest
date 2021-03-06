package com.example.nils_martin.planto;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    LoginButton loginButton;
    private static final String EMAIL = "email";
    public static final String CHANNEL_1_ID = "Channel 1";
    private final String notificationID = "1234";
    private CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createNotificationChannel();
        initializeFB();
    }
    
    /**
     * Initializes anything facebook related (button, permissions, callbacks, fetching data)
     * */
    private void initializeFB(){
       loginButton= (LoginButton)findViewById(R.id.login_button);
       loginButton.setReadPermissions(Arrays.asList(EMAIL,"public_profile"));
       final NotificationManagerCompat nmc = NotificationManagerCompat.from(this);
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
               final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
               mDialog.setMessage("Retrieving data... ");
               mDialog.show();

                GraphRequest graphRequest =GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        displayUserinfo(object);
                        mDialog.dismiss();
                        nmc.notify(Integer.parseInt(notificationID), buildNotificationBuilder("Welcome to Planto","You have" +
                                " logged in with your facebook account!").build());

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields","first_name,last_name,email,id");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });
    }

    /**
     * Fetches the users data and displays it
     * */
    public void displayUserinfo (JSONObject object){
        String first_name,last_name,email,id;
        first_name="";
        last_name="";
        email="";
        id="";
        try {
            first_name =object.getString("first_name");
            last_name = object.getString("last_name");
            email = object.getString("email");
            id=object.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView firstNameTvw,lastNameTvw,emailTvw,idTvw;
        firstNameTvw = findViewById(R.id.firstNameTvw);
        lastNameTvw = findViewById(R.id.lastNameTvw);
        emailTvw = findViewById(R.id.emailTvw);
        idTvw = findViewById(R.id.idTvw);

        firstNameTvw.setText("First Name:" + first_name);
        lastNameTvw.setText("Last Name:" + last_name);
        emailTvw.setText("Email:" + email);
        idTvw.setText("Facebook id:" + id);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Builds the notification with the text provided
     * @return the notificationBuilder
     * */
    private NotificationCompat.Builder buildNotificationBuilder(String contentTitle,String contentText){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        return mBuilder;
    }
    /**
     * Creates a notification channel.
     * */
    private void createNotificationChannel (){
        //Check if IceCreamSandwich or Api too low
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID,"channel1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Generic Channel description");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }
}
