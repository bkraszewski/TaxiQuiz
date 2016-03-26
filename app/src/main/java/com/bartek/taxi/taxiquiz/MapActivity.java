package com.bartek.taxi.taxiquiz;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity {
    private static final String STREET = "street";
    private String street;


    public static void start(Context context, String street) {
        Intent starter = new Intent(context, MapActivity.class);
        starter.putExtra(STREET, street);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);

        street = getIntent().getStringExtra(STREET);

        MapFragment fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(googleMap -> onReady(googleMap));



        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(street);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onReady(GoogleMap googleMap) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(String.format("Poland, Bialystok, ul %s", street), 1);
            if (!addresses.isEmpty()) {
                MarkerOptions options = new MarkerOptions();
                Address address = addresses.get(0);
                LatLng point = new LatLng(address.getLatitude(), address.getLongitude());
                options.position(point);
                options.title(String.format("Ul %s", street));
                googleMap.addMarker(options);

                googleMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(point));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
