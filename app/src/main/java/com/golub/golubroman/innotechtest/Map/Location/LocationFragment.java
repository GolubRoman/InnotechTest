package com.golub.golubroman.innotechtest.Map.Location;

import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


/**
 * Created by User on 12.02.2017.
 */


public class LocationFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location location;
    private OnLocationListener getLocationListener;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 60000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 5;

    public interface OnLocationListener{
        void setLocation(double latitude, double longitude);
    }

    public static LocationFragment newInstance() {
        
        Bundle args = new Bundle();
        LocationFragment fragment = new LocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    void buildGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            createLocationRequest();
        }

    }


    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    @Override
    public void onConnected(Bundle connectionHint){
            try {
                location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                getLocationListener = (OnLocationListener)getActivity();
                getLocationListener.setLocation(location.getLatitude(), location.getLongitude());
            } catch (SecurityException e) {
            }catch(NullPointerException e){
            }
            startLocationUpdates();
    }
    @Override
    public void onStart() {
        super.onStart();
        buildGoogleApiClient();
        googleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
    void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    protected void startLocationUpdates() {
        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }catch(SecurityException e){

        }
    }


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        ErrorDialogFragment connectionFailed= new ErrorDialogFragment();
        Bundle args=new Bundle();
        args.putInt("connection error №", result.getErrorCode());
        connectionFailed.setArguments(args);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i("LocationFragment", "Connection suspended");
        googleApiClient.connect();
    }

}