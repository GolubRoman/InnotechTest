package com.golub.golubroman.innotechtest.Map;

import android.app.Activity;
import android.widget.Toast;

import com.golub.golubroman.innotechtest.R;
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
//        trying to get map for working with it
        try{
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
//                    map is ready
//                    adding marker to map with title
                    googleMap.addMarker(new MarkerOptions().
                            position(new LatLng(latitude, longitude))
                            .title("Your location"));
//                    displaying the message when map is ready
                    Toast.makeText(MapActivity.this, "Map is ready", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (NullPointerException e){}
    }

}
