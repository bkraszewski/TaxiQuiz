package com.bartek.taxi.taxiquiz.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bartek.taxi.taxiquiz.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapFragmentDialog extends DialogFragment {
    private static final String TAG = "Map fragment";
    private static final String STREET = "street";
    private String street;

    public static MapFragmentDialog newInstance(String street) {

        Bundle args = new Bundle();
        args.putString(STREET, street);
        MapFragmentDialog fragment = new MapFragmentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.container, container, false);
        GoogleMapOptions options = new GoogleMapOptions();
        options.zoomControlsEnabled(true);

        street = getArguments().getString(STREET);

        MapFragment fragment = MapFragment.newInstance(options);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        fragment.getMapAsync(googleMap -> onReady(googleMap));
        getDialog().dismiss();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().dismiss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    private void onReady(GoogleMap googleMap) {
        getDialog().dismiss();
        Geocoder geocoder = new Geocoder(getActivity());
        try {
            List<Address> addresses = geocoder.getFromLocationName(String.format("Poland, Bialystok, ul %s", street), 1);
            if (!addresses.isEmpty()) {
                MarkerOptions options = new MarkerOptions();
                Address address = addresses.get(0);
                LatLng point = new LatLng(address.getLatitude(), address.getLongitude());
                options.position(point);
                options.title("Ul Zlota");
                googleMap.addMarker(options);

                googleMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(point));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
