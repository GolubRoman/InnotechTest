package com.golub.golubroman.innotechtest.Start;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.golub.golubroman.innotechtest.Map.Location.LocationFragment;
import com.golub.golubroman.innotechtest.Map.MapActivity_;
import com.golub.golubroman.innotechtest.R;
import com.golub.golubroman.innotechtest.Start.Retrofit.RetrofitModule;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DrawableRes;

import java.io.IOException;
import retrofit2.Response;

//Creating first activity when running the app
@EActivity(R.layout.activity_start)
public class StartActivity extends Activity implements RequestPhoneCodeFragment.OnPhoneCodeSelectedListener,
        LocationFragment.OnLocationListener{

//    Object of interface for getting results from requestphone dialogfragment
    private RequestPhoneCodeFragment requestPhoneCodeFragment;
//    int variable for saving choice on dialogfragment
    private int currentPhoneCodePosition;
    private final int LOCATION_CODE = 1000;
    private final int NOTIFICATION_CODE = 1001;
    private Fragment locationFragment;
    private String code = "0000";

//    retreiving colors from resources
    @ColorRes(R.color.colorGreen)
    int colorGreen;

    @ColorRes(R.color.inactiveText)
    int colorInactiveText;

    @DrawableRes(R.drawable.progress_drawable)
    Drawable progressDrawable;

    @ColorRes(R.color.colorPrimary)
    int colorPrimary;

//    initializing of views on the screen

//    edit text for inputting the phone number
    @ViewById(R.id.input_phone)
    EditText inputPhone;

    @ViewById(R.id.progressLayout)
    RelativeLayout progressLayout;

    @ViewById(R.id.progressBar)
    ProgressBar progressBar;

//    shadow rectangle for making get code button darker
    @ViewById(R.id.get_code_inactive)
    ImageView getCodeInactive;

//    shadow rectangle for making submit button darker
    @ViewById(R.id.submit_code_inactive)
    ImageView submitCodeInactive;

//    textview for displaying phoneCode dialogfragment
    @ViewById(R.id.request_phone_code)
    TextView requestPhoneCode;

//    textview for displaying phoneCode of country
    @ViewById(R.id.phone_code)
    TextView phoneCodeText;

//    button for calling post request to server on background
    @ViewById(R.id.get_code)
    Button getCode;

//    edittext for inputting security code
    @ViewById(R.id.input_code)
    EditText inputCode;

//    button for submitting security code
    @ViewById(R.id.submit_code)
    Button submitCode;

//    listener to changing text in inputPhone view
    @TextChange(R.id.input_phone)
    protected void onPhoneTextChange(){
        if(inputPhone.getText().length() < 10){
            inputCode.setEnabled(false);
            getCode.setEnabled(false);
            submitCode.setEnabled(false);
            getCode.setTextColor(colorInactiveText);
            submitCode.setTextColor(colorInactiveText);
            submitCodeInactive.setVisibility(View.VISIBLE);
            getCodeInactive.setVisibility(View.VISIBLE);
            phoneCodeText.setTextColor(colorPrimary);
            inputPhone.setTextColor(colorPrimary);
        }else{
            inputCode.setEnabled(true);
            getCode.setEnabled(true);
            getCode.setTextColor(Color.WHITE);
            getCodeInactive.setVisibility(View.INVISIBLE);
            phoneCodeText.setTextColor(colorGreen);
            inputPhone.setTextColor(colorGreen);
            if(inputCode.getText().length() == 4) {
                submitCode.setEnabled(true);
                submitCodeInactive.setVisibility(View.INVISIBLE);
                submitCode.setTextColor(Color.WHITE);
            }
        }
    }

    @TextChange(R.id.input_code)
    protected void onCodeTextChange(){
        if(inputCode.getText().length() < 4){
            submitCode.setEnabled(false);
            submitCode.setTextColor(colorInactiveText);
            submitCodeInactive.setVisibility(View.VISIBLE);
        }else{
            submitCode.setEnabled(true);
            submitCode.setTextColor(Color.WHITE);
            submitCodeInactive.setVisibility(View.INVISIBLE);
        }
    }

//    click listener on button for calling dialog fragment
    @Click(R.id.request_phone_code)
    protected void onRequestPhoneCode(){
//        setting extra data to requestPhoneCodeFragment
        Bundle bundle = new Bundle();
        bundle.putInt("currentPosition", currentPhoneCodePosition);
//        creating an object of requestPhoneCodeFragment with androidAnnotations
        requestPhoneCodeFragment = new RequestPhoneCodeFragment_();
        requestPhoneCodeFragment.setArguments(bundle);
//        displaying requestPhoneCodeFragment
        requestPhoneCodeFragment.show(getFragmentManager(), "RequestPhoneCode");
    }

//    click listener to button for submitting code
    @Click(R.id.submit_code)
    protected void onSubmitCode(){
        requestLocationPermission();
    }

//    click listener to button for getting code
    @Click(R.id.get_code)
    protected void onGetCode(){
//        putting request to server on background thread
        onProcessCode();
    }


//    method for requesting server on background thread
    @Background
    protected void onProcessCode(){
        try {
            Response<String> response = RetrofitModule.buildApi().getCode().execute();
            onDisplayCode(response.body());
        } catch (IOException e) {
            e.printStackTrace();
            onDisplayCode(null);
        } catch (NullPointerException e){
            e.printStackTrace();
            onDisplayCode(null);
        }
    }

//    displaying results from server
    @UiThread
    protected void onDisplayCode(String code){
        this.code = code;
        if(code == null) {
            this.code = "0000";
            Toast.makeText(this, "Getting result failed", Toast.LENGTH_SHORT).show();
        }
//        here is notification builder which build notification with code from server
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_lock)
                .setSound(alarmSound)
                .setPriority(Notification.PRIORITY_HIGH)
                .setTicker("Input this code: " + code)
                .setContentTitle("Security code")
                .setContentText("Input this code: " + this.code);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
//        user should see notification
        notificationManager.notify(NOTIFICATION_CODE, mBuilder.build());
    }

//    method after views are ready
    @AfterViews
    protected void afterViews(){
//        start configuration of the screen
        progressBar.setProgressDrawable(progressDrawable);
        inputCode.setEnabled(false);
        getCode.setTextColor(colorInactiveText);
        submitCode.setTextColor(colorInactiveText);
        getCode.setEnabled(false);
        submitCode.setEnabled(false);
        phoneCodeText.setTextColor(colorPrimary);
        inputPhone.setTextColor(colorPrimary);
        locationFragment = LocationFragment.newInstance();
    }

//    interface method for getting results from dialog
    @Override
    public void onPhoneCodeSelected(int position, String phoneCode) {
        currentPhoneCodePosition = position;
        phoneCodeText.setText(phoneCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(!checkSwitchLocation()){
                    showAlert();
                }else {
                    if (code.equals(inputCode.getText().toString())) {
                        getFragmentManager().beginTransaction().
                                replace(android.R.id.content, locationFragment, "LocationFragment").
                                commit();
                        progressLayout.setVisibility(View.VISIBLE);
                        progressLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    } else {
                        Toast.makeText(this, "Authentication failed, the code is incorrect",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }else{
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    requesting permission for location
    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_CODE);
    }

//    getting results from interface method realization
    @Override
    public void setLocation(double latitude, double longitude) {
        progressLayout.setVisibility(View.GONE);
        MapActivity_.intent(this).setLong(longitude).setLat(latitude).start();
    }

//    checking if location is enabled
    private boolean checkSwitchLocation(){
        int locationMode = 0;
        String locationString;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        }else{
            locationString = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationString);
        }

    }

//    showing alert because of
private void showAlert(){
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    alertDialog.setTitle("Location").
            setMessage("Please, enable getting location for further work with app").
            setCancelable(true).
            setPositiveButton("OKEY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    getIntent().putExtra("back", "yes");
                }
            }).create().show();
}
}
