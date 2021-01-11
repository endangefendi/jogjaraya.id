package com.jogjaraya.id.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.jogjaraya.id.R;
import com.jogjaraya.id.fragment.AgendaFragment;
import com.jogjaraya.id.fragment.EnsikloFragment;
import com.jogjaraya.id.fragment.HomeFragment;
import com.jogjaraya.id.fragment.InfoFragment;
import com.jogjaraya.id.fragment.JelajahFragment;
import com.jogjaraya.id.network.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.jogjaraya.id.network.Constant.URL.UPDATE_TOKEN;


/**
 * Created by Endang Efendi on Aug 2020.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            String updateLocation = getLocation();
            if (!updateLocation.equalsIgnoreCase("") || !updateLocation.equalsIgnoreCase("null")||
            !updateLocation.equalsIgnoreCase("0") || !updateLocation.equalsIgnoreCase(" ") ){
                getCurrentFirebaseToken(updateLocation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_home :
                        loadFragmment(new HomeFragment());
                        break;
                    case R.id.navigation_jelajah :
                        loadFragmment(new JelajahFragment());
                        break;
                    case R.id.navigation_agenda :
                        loadFragmment(new AgendaFragment());
                        break;
                    case R.id.navigation_ensiklo :
                        loadFragmment(new EnsikloFragment());
                        break;
                    case R.id.navigation_info :
                        loadFragmment(new InfoFragment());
                        break;
                }

                return true;
            }
        });

        loadFragmment(new HomeFragment());
    }

    public void loadFragmment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (bottomNavigationView.getSelectedItemId() == R.id.navigation_home) {
            super.onBackPressed();
        } else {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private String getLocation() throws Exception {
        //Get location
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();
        // Now get the Looper from the HandlerThread
        // NOTE: This call will block until the HandlerThread gets control and initializes its Looper
        Looper looper = handlerThread.getLooper();

        String alamat = "";
        Location location = null;
        for (String provider : providers) {

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(provider, 1000, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                }, looper);
                location = locationManager.getLastKnownLocation(provider);
            }else {
                throw new Exception("Can't get Location");
            }
        }

        if(location != null){
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address obj = addresses.get(0);
            alamat = obj.getAddressLine(0);
        } else {
            alamat = "Kota Yogyakarta";
        }

        return alamat;
    }

    private void getCurrentFirebaseToken(final String updateLocation){
        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        int verCode = 0;
                        try {
                            PackageInfo pInfo = MainActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                            verCode = pInfo.versionCode;
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        String android_id = Settings.Secure.getString(MainActivity.this.getContentResolver(),
                                Settings.Secure.ANDROID_ID);

                        String manufacturer = Build.MANUFACTURER;
                        String model = Build.MODEL;
                        int api = Build.VERSION.SDK_INT;
                        String nama_os = "";
                        if (api==16) {
                            nama_os = "Jelly Bean. ";
                        }else if (api==17) {
                            nama_os = "Jelly Bean. ";
                        }else if (api==18) {
                            nama_os = "Jelly Bean. ";
                        }else if (api==19) {
                            nama_os = "KitKat. ";
                        }else if (api==20) {
                            nama_os = "KitKat. ";
                        }else if (api==21) {
                            nama_os = "Lollipop. ";
                        }else if (api==22) {
                            nama_os = "Lollipop. ";
                        }else if (api==23) {
                            nama_os = "Marshmallow. ";
                        }else if (api==24) {
                            nama_os = "Nougat. ";
                        }else if (api==25) {
                            nama_os = "Nougat. ";
                        }else if (api==26) {
                            nama_os = "Oreo. ";
                        }else if (api==27) {
                            nama_os = "Oreo. ";
                        }else if (api==28) {
                            nama_os = "Pie. ";
                        }else if (api==29) {
                            nama_os = "10. ";
                        }

                        String versionRelease = nama_os +Build.VERSION.RELEASE;

                        updateData(token, verCode, android_id, manufacturer, model, versionRelease,updateLocation);
                    }
                });
    }

    private void updateData(String token, int verCode, String android_id, String manufacturer, String model, String versionRelease,String updateLocation) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject json = new JSONObject();
            json.put("token_fcm", token);
            json.put("user_unique_id", android_id);
            json.put("app_version", String.valueOf(verCode));
            json.put("os_version", versionRelease);
            json.put("device_model", model);
            json.put("device_manufacturer", manufacturer);
            json.put("lokasi", updateLocation);

            Log.e(TAG, json.toString());
            JsonRequest request = new JsonObjectRequest(Request.Method.POST, UPDATE_TOKEN, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "onResponse UPDATE_TOKEN" + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error);
                    Constant.parseError(MainActivity.this, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return Constant.getHeaders();
                }
            };
//            request.setRetryPolicy(Constant.getDefaultRetryPolicy());
            request.setRetryPolicy(new DefaultRetryPolicy(40000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
