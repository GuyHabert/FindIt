package com.example.findit;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.findit.Adapter.HistoryPlacesAdapter;
import com.example.findit.Adapter.SelectedPlacesAdapter;
import com.example.findit.Authentication.SigninActivity;
import com.example.findit.Model.HistoryPlacesData;
import com.example.findit.Model.PlacesData;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.button.MaterialButton;
import com.pixplicity.easyprefs.library.Prefs;
import com.shivtechs.maplocationpicker.LocationPickerActivity;
import com.shivtechs.maplocationpicker.MapUtility;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class SecondScreenActivity extends AppCompatActivity implements SelectedPlacesAdapter.OnClickListener {


    RecyclerView recyclerView,recyclerViewHistory;
    ArrayList<PlacesData> list = new ArrayList<>();
    SelectedPlacesAdapter adapter;
    int itemCount = 1;
    CardView cardView;
    MaterialButton btnPlaces,btnPath,btnSignOut;

    int ADDRESS_PICKER_REQUEST = 11;
    ArrayList<HistoryPlacesData> historyPlacesList = new ArrayList<>();
    HistoryPlacesAdapter historyPlacesAdapter;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);
        init();
        listeners();

        adapter = new SelectedPlacesAdapter(this, list,this);
        recyclerView.setAdapter(adapter);

        getAllOldPlaces();

    }

    private void getAllOldPlaces() {
        List<String> list = Paper.book().getAllKeys();
        historyPlacesList = new ArrayList<>();
        Log.e("list_get_now","getting all list");
        for (String s:list){
            historyPlacesList.add(Paper.book().read(s));
            Log.e("names_",s);
        }
        historyPlacesAdapter = new HistoryPlacesAdapter(this,historyPlacesList);
        recyclerViewHistory.setAdapter(historyPlacesAdapter);
    }

    private void listeners() {

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prefs.putBoolean("is_login",false);
                startActivity(new Intent(SecondScreenActivity.this, SigninActivity.class));
                finish();
            }
        });

        btnPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    MapUtility.apiKey = "AIzaSyDiseMHPDT0KAL67brQglMcWXNQJ6FwPk0";
                    Intent i = new Intent(SecondScreenActivity.this, LocationPickerActivity.class);
                    startActivityForResult(i, ADDRESS_PICKER_REQUEST);

            }
        });
        btnPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("before","ad");
                for (PlacesData e:list){
                    Log.e("places,",e.getPlaceName());
                }
                if(list.size() > 1) {
                    for (int i=0;i<list.size()-2;i++){
                        for (int j=0;j<list.size()-2;j++){
                            double lat1 = list.get(j).getPlaceLatLng().latitude;
                            double long1 = list.get(j).getPlaceLatLng().longitude;

                            double lat2 = list.get(j+1).getPlaceLatLng().latitude;
                            double long2 = list.get(j+1).getPlaceLatLng().longitude;

                            double lat3 = list.get(j+2).getPlaceLatLng().latitude;
                            double long3 = list.get(j+2).getPlaceLatLng().longitude;
                            if(distance(lat1,long1,lat2,long2,"K") > distance(lat1,long1,lat3,long3,"K")){
                                PlacesData temp = list.get(j+1);
                                list.set(j+1,list.get(j+2));
                                list.set(j+2,temp);
                            }
                        }
                    }
                    Log.e("after","ad");
                    for (PlacesData e:list){
                        Log.e("places,",e.getPlaceName());
                    }
                    Intent intent = new Intent(SecondScreenActivity.this, MapsActivity.class);
                    intent.putParcelableArrayListExtra("places",list);
                    startActivity(intent);
                }else{
                    Toast.makeText(SecondScreenActivity.this, "Select at least two locations", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {

        Paper.init(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cardView = findViewById(R.id.card_view);
        btnPlaces = findViewById(R.id.btn_places);
        btnPath = findViewById(R.id.btn_path);

        recyclerViewHistory = findViewById(R.id.recycler_view_history);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        btnSignOut = findViewById(R.id.btn_log_out);

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(false)
                .build();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDRESS_PICKER_REQUEST) {
            try {
                if (data != null && data.getStringExtra(MapUtility.ADDRESS) != null) {
                    String address = data.getStringExtra(MapUtility.ADDRESS);

                    double selectedLatitude = data.getDoubleExtra(MapUtility.LATITUDE, 0.0);
                    double selectedLongitude = data.getDoubleExtra(MapUtility.LONGITUDE, 0.0);
                    Log.e("Address: ", address);
                    Log.e("Lat:", selectedLatitude + "  Long:" + selectedLongitude);

                    LatLng latLng = new LatLng(selectedLatitude,selectedLongitude);
                    PlacesData obj = new PlacesData();
                    obj.setPlaceLatLng(latLng);
                    obj.setPlaceName(address);
                    obj.setCount(itemCount);
                    itemCount++;
                    adapter.addItem(obj);
                    if (cardView.getVisibility() == View.GONE) {
                        cardView.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit == "K") {
                dist = dist * 1.609344;
            } else if (unit == "N") {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllOldPlaces();
    }

    private void dialogBuilder(int position) {
        int ia = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?")
                .setMessage("Do you really want to remove this location?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(ia != -1) {
                            list.remove(ia);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
    }

    @Override
    public void onItemClick(View view, int adapterPosition) {
        Log.e("activity_positon",adapterPosition + "");
        dialogBuilder(adapterPosition);
        dialog.show();
    }
}

