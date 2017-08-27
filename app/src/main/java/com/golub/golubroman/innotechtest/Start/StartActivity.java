package com.golub.golubroman.innotechtest.Start;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
    private Fragment locationFragment;

//    retreiving colors from resources
    @ColorRes(R.color.colorGreen)
    int colorGreen;

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

//    shadow rectangle for making button darker
    @ViewById(R.id.get_code_inactive)
    ImageView getCodeInactive;

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
            getCode.setEnabled(false);
            getCodeInactive.setVisibility(View.VISIBLE);
            phoneCodeText.setTextColor(colorPrimary);
            inputPhone.setTextColor(colorPrimary);
        }else{
            getCode.setEnabled(true);
            getCodeInactive.setVisibility(View.INVISIBLE);
            phoneCodeText.setTextColor(colorGreen);
            inputPhone.setTextColor(colorGreen);
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
            Response<Integer> response = RetrofitModule.buildApi().getCode().execute();
//            onDisplayCode(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
        onDisplayCode(0);
    }

//    displaying results from server
    @UiThread
    protected void onDisplayCode(int code){
        Toast.makeText(this, "Your code " + Integer.toString(code), Toast.LENGTH_SHORT).show();
    }

//    method after views are ready
    @AfterViews
    protected void afterViews(){
//        start configuration of the screen
        progressBar.setProgressDrawable(progressDrawable);
        getCode.setEnabled(false);
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
                getFragmentManager().beginTransaction().
                        replace(android.R.id.content, locationFragment, "LocationFragment").
                        commit();
                progressLayout.setVisibility(View.VISIBLE);

            }else{
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_CODE);
    }

    @Override
    public void setLocation(double latitude, double longitude) {
        progressLayout.setVisibility(View.GONE);
        MapActivity_.intent(this).setLong(longitude).setLat(latitude).start();
    }
}
