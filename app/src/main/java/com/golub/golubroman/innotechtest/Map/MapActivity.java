package com.golub.golubroman.innotechtest.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.golub.golubroman.innotechtest.R;
import com.golub.golubroman.innotechtest.Start.StartActivity_;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;

/**
 * Created by roman on 27.08.17.
 */

//  creating activity for displaying map
@EActivity(R.layout.activity_map)
public class MapActivity extends Activity {

    private double longitude, latitude;
    private Handler handler;
    private boolean doubleClick;

    @Extra
    void setLong(double longitude){
        this.longitude = longitude;
    }

    @Extra
    void setLat(double latitude){
        this.latitude = latitude;
    }

//    retrieving fragment map from xml by id
    @FragmentById(R.id.map)
    MapFragment mapFragment;

//    method calling after views are ready
    @AfterViews
    protected void afterViews(){
        handler = new Handler();
//        trying to get map for working with it
        try{
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
//                    map is ready
//                    adding marker to map with title
                    googleMap.addMarker(new MarkerOptions().
                            position(new LatLng(latitude, longitude))
                            .title("Ваше местоположение"));
                }
            });
        }catch (NullPointerException e){}
    }

    @Override
    public void onBackPressed() {
        if(doubleClick) {
            StartActivity_.intent(this).
                    flags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK).
                    category(Intent.CATEGORY_HOME).setEnd(true).start();
            finish();
        }else{
            doubleClick = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleClick = false;
                }
            }, 400);
            Toast.makeText(this, "Двойной клик для выхода", Toast.LENGTH_SHORT).show();

        }

    }
}
