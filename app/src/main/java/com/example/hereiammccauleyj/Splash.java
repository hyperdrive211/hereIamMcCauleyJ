package com.example.hereiammccauleyj;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class Splash extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        iv = (ImageView)findViewById(R.id.softSpotLogo);
        Animation myAnim = AnimationUtils.loadAnimation(
                this,R.anim.mytransition);
        iv.startAnimation(myAnim);
       if(isServicesOK()) {
           Thread timer = new Thread() {
               public void run() {
                   try {
                       sleep(5000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   } finally {
                       finish();
                       init();
                   }
               }
           };

           timer.start();
       }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability
                .getInstance()
                .isGooglePlayServicesAvailable(Splash.this);

        if(available == ConnectionResult.SUCCESS){
            //Everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google PLay Services is working");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //An error has occured and we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability
                    .getInstance()
                    .getErrorDialog(Splash.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(this, "We can't make map requests", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void init() {
     Intent intent = new Intent(Splash.this, MainActivity.class);
     startActivity(intent);
    }
}
