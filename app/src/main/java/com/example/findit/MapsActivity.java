package com.example.findit;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adroitandroid.chipcloud.Chip;
import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.findit.Model.HistoryPlacesData;
import com.example.findit.Model.PlacesData;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.core.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, RoutingListener {

    private GoogleMap mMap;
    ArrayList<PlacesData> list = new ArrayList<>();
    ArrayList<Marker> markers = new ArrayList<>();
    ChipCloud chipCloud;
    List<LatLng> latLngList;

    int directionsPress = 1;

    MaterialButton btnDirections,btnSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void listeners() {
        chipCloud.setChipListener(new ChipListener() {
            @Override
            public void chipSelected(int index) {
                Chip c = (Chip) chipCloud.getChildAt(index);
                for (Marker m:markers){
                    if(m.getTitle().equals(c.getText())){
                        m.showInfoWindow();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(), 14));
                    }
                }
            }
            @Override
            public void chipDeselected(int index) {
            }
        });

        btnDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(directionsPress == 1) {
                    getRoutes();
                    directionsPress++;
                    btnDirections.setText("Start Route");
                }else{

                    if(list.size() == 2){
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr=" +  list.get(0).getPlaceLatLng().latitude + "," + list.get(0).getPlaceLatLng().longitude +
                                        "&daddr=" +list.get(1).getPlaceLatLng().latitude +"," + list.get(1).getPlaceLatLng().longitude));

                        startActivity(intent);
                    }else{
                        String tempStr = "";
                        for (int i=2;i<list.size();i++){
                            tempStr += ",+to:" + list.get(i).getPlaceLatLng().latitude +"," + list.get(i).getPlaceLatLng().longitude;
                        }
                        Log.e("newStr",tempStr);
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr=" +  list.get(0).getPlaceLatLng().latitude + "," + list.get(0).getPlaceLatLng().longitude +
                                        "&daddr=" +list.get(1).getPlaceLatLng().latitude +"," + list.get(1).getPlaceLatLng().longitude+tempStr));
                        startActivity(intent);

                    }
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HistoryPlacesData data = new HistoryPlacesData();
                data.setPlacesData(list);
                data.setDate(getCurrentData());
                Log.e("current_date",data.getDate());
                Paper.book().write(data.getDate(), data);
                btnSave.setEnabled(false);
            }
        });
    }

    private void getRoutes() {
        Routing routing;

        if(list.size() == 2){
            Log.e("first","asda");
            LatLng start = new LatLng(list.get(0).getPlaceLatLng().latitude,list.get(0).getPlaceLatLng().longitude);
            LatLng end = new LatLng(list.get(1).getPlaceLatLng().latitude,list.get(1).getPlaceLatLng().longitude);

            routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                    .waypoints(start, end)

                    .key("AIzaSyDiseMHPDT0KAL67brQglMcWXNQJ6FwPk0")
                    .build();
            routing.execute();


        }else {
            Log.e("all_list_second","asda");
            routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.DRIVING)
                    .withListener(this)
                    .waypoints(latLngList)
                    .key("AIzaSyDiseMHPDT0KAL67brQglMcWXNQJ6FwPk0")
                    .build();
            routing.execute();
        }
    }

    private void init() {
        chipCloud = findViewById(R.id.chip_cloud);
        setUpChips();
        setUpMarkers();
        setZoom();
        btnDirections = findViewById(R.id.btn_direction);
        btnSave = findViewById(R.id.btn_save);
    }

    private void setZoom() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = 309;
        int padding = (int) (width * 0.12);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,width,height, padding);
        mMap.animateCamera(cu);
        mMap.moveCamera(cu);
    }

    private void setUpMarkers() {
        for (PlacesData c:list) {
            markers.add(
            mMap.addMarker(new MarkerOptions().position(c.getPlaceLatLng())
            .title(c.getPlaceName()))
            );
        }
    }

    private void setUpChips() {
        for (PlacesData c:list) {
            chipCloud.addChip(c.getPlaceName());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if( getIntent().getSerializableExtra("places") != null){

            list = (ArrayList<PlacesData>) getIntent().getSerializableExtra("places");
            latLngList = new ArrayList<>();
            Log.e("yess ","asjbnd");
            for (PlacesData e:list){
                LatLng tempLatLng = new LatLng(e.getPlaceLatLng().latitude,e.getPlaceLatLng().longitude);
                latLngList.add(tempLatLng);
            }
        }
        init();
        listeners();

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (Marker m:markers){
            if(m.equals(marker)){
                Log.e("marrker info",marker.getTitle());
            }
        }
        return false;
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Log.e("failure",e.getMessage());
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
        for (Route e:arrayList){
            mMap.addPolyline(e.getPolyOptions().color(R.color.colorPrimary));
        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    private String getCurrentData(){
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        return (formattedDate + "\n" + c);
    }
}
