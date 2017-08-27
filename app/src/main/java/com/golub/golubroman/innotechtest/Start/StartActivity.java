package com.golub.golubroman.innotechtest.Start;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;
import retrofit2.Response;

//Creating first activity when running the app
@EActivity(R.layout.activity_start)
public class StartActivity extends Activity implements RequestPhoneCodeFragment.OnPhoneCodeSelectedListener{

//    Object of interface for getting results from requestphone dialogfragment
    private RequestPhoneCodeFragment requestPhoneCodeFragment;
//    int variable for saving choice on dialogfragment
    private int currentPhoneCodePosition;

//    retreiving colors from resources
    @ColorRes(R.color.colorGreen)
    int colorGreen;

    @ColorRes(R.color.colorPrimary)
    int colorPrimary;

//    initializing of views on the screen

//    edit text for inputting the phone number
    @ViewById(R.id.input_phone)
    EditText inputPhone;

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
        MapActivity_.intent(this).start();
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
        getCode.setEnabled(false);
        phoneCodeText.setTextColor(colorPrimary);
        inputPhone.setTextColor(colorPrimary);
    }

//    interface method for getting results from dialog
    @Override
    public void onPhoneCodeSelected(int position, String phoneCode) {
        currentPhoneCodePosition = position;
        phoneCodeText.setText(phoneCode);
    }
}
