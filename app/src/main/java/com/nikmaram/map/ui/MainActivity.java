package com.nikmaram.map.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nikmaram.map.BuildConfig;
import com.nikmaram.map.utility.MapTools;
import com.nikmaram.map.utility.PolylineEncoding;
import com.nikmaram.map.R;
import com.nikmaram.map.database_helper.AssetDatabaseHelper;
import com.nikmaram.map.model.AddressModel;
import com.nikmaram.map.model.PointModel;
import com.nikmaram.map.model.RoutesModel;

import org.neshan.core.LngLat;
import org.neshan.layers.Layer;
import org.neshan.layers.VectorElementEventListener;
import org.neshan.layers.VectorElementLayer;
import org.neshan.services.NeshanMapStyle;
import org.neshan.services.NeshanServices;
import org.neshan.ui.ClickData;
import org.neshan.ui.ClickType;
import org.neshan.ui.ElementClickData;
import org.neshan.ui.MapEventListener;
import org.neshan.ui.MapView;
import org.neshan.vectorelements.Marker;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    LngLat selectPosition;
    private LngLat focalPoint;
    private MapView map;
    // our database points
    private AssetDatabaseHelper db;
    // layer number in which map is added
    final int BASE_MAP_INDEX = 0;
    // You can add some elements to a VectorElementLayer
    VectorElementLayer markerLayer;
    int vectorElementId;
    // an id for each marker
    long markerId = new Random().nextLong();

    // save selected Marker for select and deselect function
    Marker selectedMarker = null;

    // used to track request permissions
    final int REQUEST_CODE = 123;

    // location updates interval - 1 sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    // fastest updates interval - 1 sec
    // location updates will be received if another app is requesting the locations
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    // User's current location
    private Location userLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private SettingsClient settingsClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationCallback locationCallback;
    private String lastUpdateTime;
    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;
    // save current map style
    NeshanMapStyle mapStyle;

    //We add lines and markers to this layer.
     VectorElementLayer lineLayer;

    List<PolylineEncoding.LatLng> decodedOverviewPath;
    List<PolylineEncoding.LatLng> decodedStepByStepPath;

    // value for difference mapSetZoom
    boolean overview = false;
    private Button btn_routing;
    private LinearLayout frameLayout;
    private ProgressBar progressBar;
    private TextView tx_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new AssetDatabaseHelper( getBaseContext() );
        map = findViewById(R.id.map);
        btn_routing = findViewById(R.id.route);
        frameLayout = findViewById(R.id.frame);
        progressBar = findViewById(R.id.progress);
        tx_address = findViewById(R.id.txt_address);


        // Initializing mapView element
        initMap();
        //load points in map
        MapTools.getDBPoints(this,markerLayer,map);
        // map listener: Long Click -> add marker Single Click -> deselect marker
        // marker listener for select and deselect markers
        EventMap();

    }

    private void initMap() {

        // Creating a VectorElementLayer(called markerLayer) to add all markers to it and adding it to map's layers
        markerLayer = NeshanServices.createVectorElementLayer();
        lineLayer = NeshanServices.createVectorElementLayer();

        //reading all points and adding a marker for each one
        map.getLayers().add(markerLayer);
        map.getLayers().add(lineLayer);

        initLocation();
        startReceivingLocationUpdates();

        ///set map focus position
         focalPoint = new LngLat(51.336434, 35.6990015);
        map.setFocalPointPosition(focalPoint, 0f);
        map.setZoom(12f,1);
        // Cache base map
        // Cache size is 10 MB
        Layer baseMap = NeshanServices.createBaseMap(NeshanMapStyle.STANDARD_DAY, getCacheDir()+"/baseMap", 10);
        map.getLayers().insert(BASE_MAP_INDEX, baseMap);
        // Cache POI layer
        // Cache size is 10 MB
        Layer poiLayer = NeshanServices.createPOILayer(false, getCacheDir() + "/poiLayer", 10);
        map.getLayers().insert(1, poiLayer);
    }

   private void EventMap(){
       // map listener: Long Click -> add marker Single Click -> deselect marker
       map.setMapEventListener(new MapEventListener() {
           @Override
           public void onMapClicked(ClickData mapClickInfo) {
               super.onMapClicked(mapClickInfo);

               // long tap on map
               if (mapClickInfo.getClickType() == ClickType.CLICK_TYPE_LONG) {
                   // check the bottom sheet expanded or collapsed

                   if (selectedMarker != null) {

                       //  any marker selected deselect that marker by long tap
                       deselectMarker(selectedMarker);
                   }

                   // by calling getClickPos(), we can get position of clicking (or tapping)
                   LngLat clickedLocation = mapClickInfo.getClickPos();
                   // addMarker adds a marker (pretty self explanatory :D) to the clicked location
                   MapTools.addMarker(clickedLocation, MainActivity.this,markerLayer,markerId);
                   runOnUiThread(new Runnable() {

                       @Override
                       public void run() {
                           selectPosition=clickedLocation;
                           frameLayout.setVisibility(View.VISIBLE);
                            lineLayer.clear();
                           //request for get address and save to sqllite
                           Get_Address(clickedLocation.getY(),clickedLocation.getX(),true);
                       }
                   });



                   // increment id
                   markerId++;
               } else if (mapClickInfo.getClickType() == ClickType.CLICK_TYPE_SINGLE && selectedMarker != null) {
                   // deselect marker when tap on map and a marker is selected
                   deselectMarker(selectedMarker);
               }
           }
       });

       // marker listener for select and deselect markers
       markerLayer.setVectorElementEventListener(new VectorElementEventListener() {
           @Override
           public boolean onVectorElementClicked(ElementClickData clickInfo) {
               if (clickInfo.getClickType() == ClickType.CLICK_TYPE_SINGLE) {
                    vectorElementId = (int) clickInfo.getVectorElement().getMetaDataElement("id").getLong();
                   if (selectedMarker != null) {
                       // deselect marker when tap on a marker and a marker is selected
                       deselectMarker(selectedMarker);
                   } else {
                       // select marker when tap on a marker
                       selectMarker((Marker) clickInfo.getVectorElement(),clickInfo.getClickPos());
                       runOnUiThread(new Runnable() {

                           @Override
                           public void run() {

                               //request for get address and save to sqllite
                               Get_Address(selectPosition.getY(),selectPosition.getX(),false);
                               frameLayout.setVisibility(View.VISIBLE);
                                                       }
                       });


                   }
               }
               return true;
           }
       });







       // do routing
       btn_routing.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (selectPosition != null) {
                   neshanRoutingApi();

               }
           }
       });

}

    private void initLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        settingsClient = LocationServices.getSettingsClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                userLocation = locationResult.getLastLocation();
                lastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                onLocationChange();
            }
        };

        mRequestingLocationUpdates = false;

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();

    }


    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        settingsClient
                .checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("TAG", "All location settings are satisfied.");

                        //noinspection MissingPermission
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                        onLocationChange();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i("TAG", "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CODE);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("TAG", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("TAG", errorMessage);

                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        onLocationChange();
                    }
                });
    }

    public void startReceivingLocationUpdates() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).check();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e("TAG", "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e("TAG", "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    private void onLocationChange() {
        if(userLocation != null) {
            MapTools.addUserMarker(new LngLat(userLocation.getLongitude(), userLocation.getLatitude()),this,markerLayer);
            stopLocationUpdates();
        }

    }



    // request routing method from Neshan Server
    private void neshanRoutingApi() {
     progressBar.setVisibility(View.VISIBLE);
     tx_address.setText("");
    String first_point=userLocation.getLatitude() + "," + userLocation.getLongitude();
    String two_point = selectPosition.getY()+ "," + selectPosition.getX();
        ViewModel_Routes viewmodel =new ViewModelProvider(this).get(ViewModel_Routes.class);
        viewmodel.Routes(first_point,two_point);
        viewmodel.mutableRoutes.observe(this, new Observer<RoutesModel>() {
            @Override
            public void onChanged(RoutesModel routesModel) {
                lineLayer.clear();
              String  encodedOverviewPath =routesModel.getRoutes().get(0).getOverviewPolyline().getPoints();
                decodedOverviewPath = PolylineEncoding.decode(encodedOverviewPath);
                MapTools.drawLineGeom(decodedOverviewPath,lineLayer);
              String route =  routesModel.getRoutes().get(0).getLegs().get(0).getSummary()+"\n"
                      + routesModel.getRoutes().get(0).getLegs().get(0).getDistance().getText()+"\n"
                      + routesModel.getRoutes().get(0).getLegs().get(0).getDuration().getText();
                progressBar.setVisibility(View.GONE);
                tx_address.setText(route);



               // List<Step> stepByStepPath = routesModel.getRoutes().get(0).getLegs().get(0).getSteps();
              //  decodedStepByStepPath = new ArrayList<>();

                // decoding each segment of steps and putting to an array
                //for (int i = 0; i < stepByStepPath.size(); i++) {
                 //   List<PolylineEncoding.LatLng> decodedEachStep = PolylineEncoding.decode(stepByStepPath.get(i).getPolyline());
                  //  decodedStepByStepPath.addAll(decodedEachStep);
                //}
               // MapTools.drawLineGeom(decodedStepByStepPath,lineLayer);


            }
        });


    }

    // request for get address
    private void Get_Address(Double lat,Double lng,Boolean save){

        progressBar.setVisibility(View.VISIBLE);
        tx_address.setText("");
        ViewModel_Address viewmodel =new ViewModelProvider(this).get(ViewModel_Address.class);
        viewmodel.Address(String.valueOf(lat),String.valueOf(lng) );
        viewmodel.mutableAddress.observe(this, new Observer<AddressModel>() {
            @Override
            public void onChanged(AddressModel addressModel) {

            String  address =addressModel.getFormattedAddress();
           progressBar.setVisibility(View.GONE);
           tx_address.setText(address);



                //save to db
                if (save)
          MapTools.SaveDBPoint(MainActivity.this,new PointModel(markerId,lng,lat,address));

            }
        });

    }

    // deselect marker and collapsing bottom sheet
    private void deselectMarker(final Marker deselectMarker) {
        MapTools.changeMarkerToBlue(this,deselectMarker);
        selectedMarker = null;
//        remove_marker.setVisibility(View.GONE);

    }

    private void selectMarker(final Marker selectMarker, LngLat clickPos) {
        MapTools.changeMarkerToRed(this,selectMarker);
        selectedMarker = selectMarker;
         selectPosition = clickPos;
    }

    //show dialog for Permission
    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {

       if (frameLayout.getVisibility()==View.VISIBLE){
            frameLayout.setVisibility(View.GONE);
            return;
        }
        else if(!lineLayer.getAll().isEmpty()){
            lineLayer.clear();
        }

       else if (selectedMarker != null) {
            deselectMarker(selectedMarker);
        }


        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        MapTools.is_marker_first=true;
    }

    public void stopLocationUpdates() {
        // Removing location updates
        fusedLocationClient
                .removeLocationUpdates(locationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                     //   Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void focusOnUserLocation(View view) {
        if(userLocation != null) {
            map.setFocalPointPosition(
                    new LngLat(userLocation.getLongitude(), userLocation.getLatitude()), 0.25f);
            map.setZoom(15, 0.25f);
        }
    }
}