package com.example.ecommerce;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    myLocationListener locationListener;
    LocationManager locationManager;
    Button cuuBtn;
    EditText LocText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

         cuuBtn = (Button)findViewById(R.id.currBtn);
        final SearchView searchLoc = (SearchView)findViewById(R.id.searchLocBtn);
         LocText = (EditText)findViewById(R.id.locTxt);
        Button orderBtn = (Button)findViewById(R.id.orderBtn);

        locationListener = new myLocationListener(getApplication());
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 0, locationListener);
        }
        catch (SecurityException se) {
            Toast.makeText(getApplicationContext(), "You are not allowed to access the current location", Toast.LENGTH_LONG).show();
        }

        searchLoc.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String lc = searchLoc.getQuery().toString();
                getLocation(lc);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cust_id = getIntent().getStringExtra("cust_id");

                Date c = Calendar.getInstance().getTime();

                SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault());
                String formattedDate = df.format(c);

                DataBaseHelper db = new DataBaseHelper(getApplicationContext());
                db.updateOrder(cust_id, formattedDate, LocText.getText().toString());
                Toast.makeText(getApplicationContext(), "Order confirmed", Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(MapsActivity.this, MainActivity.class));
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.04441960, 31.235711600), 8));

        cuuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocText.getText().clear();
                mMap.clear();
                Geocoder geocoder = new Geocoder(getApplicationContext());
                List<Address> addressList;
                Location location = null;

                try {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                catch (SecurityException se)
                {
                    Toast.makeText(getApplicationContext(), "You are not allowed to access the current location", Toast.LENGTH_LONG).show();
                }

                if(location != null)
                {
                    LatLng myPos = new LatLng(location.getLatitude(), location.getLongitude());

                    try {
                        addressList = geocoder.getFromLocation(myPos.latitude, myPos.longitude, 1);
                        if(!addressList.isEmpty())
                        {
                            String address = "";
                            for(int i = 0; i <= addressList.get(0).getMaxAddressLineIndex(); i++)
                                address += addressList.get(0).getAddressLine(i) + ", ";

                            mMap.addMarker(new MarkerOptions().position(myPos).title("My Location").snippet(address)).setDraggable(true);
                            LocText.setText(address);
                        }
                    }
                    catch (IOException io){
                        mMap.addMarker(new MarkerOptions().position(myPos).title("My Location"));
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 15));
                }
                else
                    Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_LONG).show();
            }
        });

    }

    private  void getLocation(String search)
    {
        LocText.getText().clear();
        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address>list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(search, 1);
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
        if(list.size() > 0)
        {
            String a = "";
            for(int i = 0; i <= list.get(0).getMaxAddressLineIndex(); i++)
                a += list.get(0).getAddressLine(i) + ", ";

            Address address = list.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(search));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            LocText.setText(a);
        }
    }

    public class myLocationListener implements LocationListener
    {
        private Context activityContext;

        public myLocationListener(Context c){
            activityContext = c;
        }

        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(activityContext, location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            Toast.makeText(activityContext, "GPS Enabled", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(activityContext, "GPS Disabled", Toast.LENGTH_LONG).show();
        }
    }
}
