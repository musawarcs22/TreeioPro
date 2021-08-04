package com.example.treeiopro;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    NavigationView mNavigationView;
    ActionBarDrawerToggle actionBartoggle;
    DrawerLayout mainDrawer;
    MaterialToolbar appBar;


    //**** For Weather start *********//
    String iconUrlStart="http://openweathermap.org/img/wn/";
    String iconUrlEnd="@2x.png"; //Icon Post Fix
    String apiKey = "ba6edf2a3cce3d66b4d567d853b38fbc";
    String city = "Mianwali";
    String apiURL = "http://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+apiKey;
    RequestQueue requestQueue;
    ImageView imageView;
    TextView textViewTemp;
    TextView tempDescription;
    double tempCelcius;
    int tempCelciusInt;
    String wheatherDiscription;
    //**** For Weather ends *********//

    // For directing to add record and view records activities//
    Button addRecord;
    Button viewOldRecords;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationView = findViewById(R.id.navigationView);
        mNavigationView.setItemIconTintList(null);

        setUpViews();
        setUpWeatherInfo();
        setUpButtons(); // For add new records and view old records

    }

    private void setUpButtons() {
        addRecord = findViewById(R.id.btnAddNewRecord);
        viewOldRecords = findViewById(R.id.btnHistory);
        addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddNewRecordActivity.class);
                startActivity(intent);
            }
        });
        viewOldRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewTreeOnMapActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpWeatherInfo() {
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req=new JsonObjectRequest(Request.Method.GET, apiURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("MMB","onResponse: "+response);
                Gson gson = new Gson();
                CurrentWeatherData data = gson.fromJson(response.toString(),CurrentWeatherData.class);

                Log.i("MMB", "onResponse--->Name : "+data.getName());
                Log.i("MMB", "onResponse--->Wheather[0].description : "+data.getWeather().get(0).getDescription());
                Log.i("MMB", "onResponse--->Icon Code : "+data.getWeather().get(0).getIcon());

                Log.i("MMB", "onResponse--->Temperature : "+data.getMain().getTemp());
                tempCelcius = data.getMain().getTemp()-273.15;
                tempCelciusInt = (int) tempCelcius;
                wheatherDiscription = data.getWeather().get(0).getDescription();
                textViewTemp=findViewById(R.id.textViewtemp);
                textViewTemp.setText(tempCelciusInt+"°C");
                Log.i("MMB", "Output Check temp : "+tempCelcius+"°C");
                tempDescription = findViewById(R.id.textViewWheatherDes);
                tempDescription.setText(wheatherDiscription);
                Log.i("MMB", "Output Check temp Descruption: "+wheatherDiscription);

                String iconCode = data.getWeather().get(0).getIcon();
                String iconCompUrl = iconUrlStart+iconCode+iconUrlEnd;


                imageView = findViewById(R.id.imageViewWheatherIcon);
                Glide.with(MainActivity.this)
                        .load(iconCompUrl)
                        .into(imageView);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MMB", "onResponse: ",error);
            }
        });

        requestQueue.add(req);

    }

    private void setUpViews() {
        setUpDrawerLayout();
    }

    private void setUpDrawerLayout() {
      /*  NavigationView navView = findViewById(R.id.navigationView);
        navView.setNavigationItemSelectedListener(item -> {


            Log.i("NAVMMB","Selected Item id : ==========> "+item.getItemId());
           // Intent intent = new Intent(this, ProfileActivity.class);
           // MainActivity.this.startActivity(intent);
            //finish();

            // mainDrawer.closeDrawer(mainDrawer);
            return true;
        });
        String TAG = "MYCHECK";
        Log.i(TAG, "setUpNavView:      "+navView);
*/
        appBar = findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        mainDrawer = findViewById(R.id.mainDrawer);

        actionBartoggle = new ActionBarDrawerToggle(MainActivity.this, mainDrawer, R.string.app_name, R.string.app_name);
        actionBartoggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(item -> {


            Log.i("NAVMMB","Selected Item id : ==========> "+item.getItemId()+"find"+R.id.btnNavProfile);

            if(item.getItemId() == R.id.btnNavProfile){
                Intent intent = new Intent(this, ProfileActivity.class);
                MainActivity.this.startActivity(intent);
                // mainDrawer.closeDrawer(mainDrawer);
            }
            return true;
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBartoggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}