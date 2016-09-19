package com.wvs.shoppercrux.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.wvs.shoppercrux.Gson.RecyclerViewAdapter;
import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.Utils.PermissionUtils;
import com.wvs.shoppercrux.activities.LocationActivity;
import com.wvs.shoppercrux.activities.LoginActivity;
import com.wvs.shoppercrux.activities.MainActivity;
import com.wvs.shoppercrux.helper.SQLiteHandler;
import com.wvs.shoppercrux.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public double latitude, longitude;
    private LocationManager locationManager;
    private SQLiteHandler db;
    private SessionManager session;
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private Location mLocation;
    private String provider;
//    private FloatingActionButton floatingActionButton;
//    private Boolean isFabOpen = false;
//    private FloatingActionButton fab,fab1,fab2;
//    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private FloatingActionMenu menu;
    private View view;
    private FloatingActionButton setLocation,listView;

    private String plid;
    private Context context;
    public static String LOCATION_PIN="LocationPin";
    private String sid;
    private String id;
    private String pinlat;
    private String pinLong;
    private String locName;
    private RequestQueue requestQueue;
    private List<StoreListModel> mStoreLocLst = new ArrayList<StoreListModel>();

    String JSON_IMAGE_TITLE_NAME = "nickname";
    String JSON_IMAGE_URL = "banner";
    String SELLER_ID = "seller_id";
    String SELLER_ADDRESS = "seller_address";
    String Lattitude = "latitude";
    String Longitude = "longitude";
    private StoreListModel getDataAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.content_home, container, false);
        // SqLite database handler
        db = new SQLiteHandler(getActivity().getApplicationContext());

        // session manager
        session = new SessionManager(getActivity().getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        enableMyLocation();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.getString("locationId") != null) {
                id = bundle.getString("locationId");
                sid = bundle.getString("categoryId");
                pinlat = bundle.getString("pinLat");
                pinLong = bundle.getString("pinLong");
                locName = bundle.getString("locName");
                //   STORE_URL = GET_JSON_DATA_HTTP_URL + id;
                Log.e("ididiid", id);
                Log.e("pinlat", pinlat);
                Log.e("pinLong", pinLong);
                Log.e("locName", locName);
                Log.e("ididiid", sid);

            } else if ((bundle.getString("categoryId") != null)) {
                SharedPreferences preferences = context.getSharedPreferences(LOCATION_PIN, context.MODE_PRIVATE);
                String setPinCode = preferences.getString("LocationId", null);
                Log.d("Shared location code", "Location id:" + setPinCode);
                if (setPinCode != null) {
                    plid = bundle.getString("locationId");
                    sid = bundle.getString("categoryId");
                    //    CATEGORY_STORE_URL = GET_CATEGORY_BASED+"?sid="+sid+"&id="+plid;
                    Log.d("category id", plid);
                }

            }
            getStoreList();
        }

            setLocation = (FloatingActionButton) view.findViewById(R.id.locationBtn);
            listView = (FloatingActionButton) view.findViewById(R.id.listBtn);
            menu = (FloatingActionMenu) view.findViewById(R.id.menu);

            setLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager2 = getFragmentManager();
                    FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                    LocationActivity fragment2 = new LocationActivity();
//                fragmentTransaction2.addToBackStack("xyz");

                    fragmentTransaction2.hide(HomeFragment.this);
                    fragmentTransaction2.add(R.id.content_frame, fragment2);
                    fragmentTransaction2.commit();

                }
            });
            listView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager2 = getFragmentManager();
                    FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                    StoreFragment fragment2 = new StoreFragment();
                    Bundle bundle = new Bundle();
                    String data = RecyclerViewAdapter.locationData;
                    bundle.putString("locationId", data);
                    fragment2.setArguments(bundle);
                   Constants.mapView=false;
//                fragmentTransaction2.addToBackStack("xyz");
                    fragmentTransaction2.hide(HomeFragment.this);
                    fragmentTransaction2.add(R.id.content_frame, fragment2);
                    fragmentTransaction2.commit();
                }
            });


//        fab = (FloatingActionButton)view.findViewById(R.id.fab);
//
//        fab1 = (FloatingActionButton)view.findViewById(R.id.fab1);
//        fab2 = (FloatingActionButton)view.findViewById(R.id.fab2);
//        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
//        fab_close = AnimationUtils.loadAnimation(getContext(),R.anim.fab_close);
//        rotate_forward = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_forward);
//        rotate_backward = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_backward);
//        fab.setOnClickListener(this);
//        fab1.setOnClickListener(this);
//        fab2.setOnClickListener(this);
            // Inflate the layout for this fragment
            return view;

        }


    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        enableMyLocation();
        mMap.setOnMyLocationButtonClickListener(this);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,
                                           int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getActivity().getSupportFragmentManager(), "dialog");
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {

        MainActivity activity = (MainActivity) getActivity();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(activity, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
            mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);

            if(provider!=null && !provider.equals("")){

                // Get the location from the given provider
                Location location = locationManager.getLastKnownLocation(provider);

                locationManager.requestLocationUpdates(provider, 20000, 1, this);

                if(location!=null) {
                    onLocationChanged(mLocation);
                    mMap.getUiSettings().setZoomControlsEnabled(false);
               //     mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
                //    mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Your Location"));
                    addMarkersToMap();

                }
                else {
                    Toast.makeText(getActivity(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void addMarkersToMap() {
        mMap.clear();

        if(Constants.setLocation==true) {
            Log.e("change location","change location");
            MarkerOptions marker = new MarkerOptions();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(pinlat), Double.parseDouble(pinLong)), 15));
            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(pinlat), Double.parseDouble(pinLong))).icon(BitmapDescriptorFactory.fromResource(R.mipmap.user_location)).title(locName));
            Constants.setLocation=false;

            //  mMap.addMarker(marker);
        }else {
            Log.e("same location","same location");
            MarkerOptions marker = new MarkerOptions();

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_set_map_other_locale)).title("Your Location"));
            Toast.makeText(getActivity(), "Please set the location ", Toast.LENGTH_LONG).show();
//            mMap.addMarker(marker);
        }

//        if (mStoreLocLst != null && mStoreLocLst.size() > 0) {
//
//            for (int i = 0; i < mStoreLocLst.size(); i++)
//                try {
//                    final StoreListModel vendorLoc = getDataAdapter.get
//                            .get(i);
//         Log.e("mStorelist", mStoreLocLst.get(i).toString());
//                    // Marker m = googleMap.addMarker(new MarkerOptions()
//                    // .position(new LatLng(vendorLoc.getLongitude(),
//                    // vendorLoc.getLatitude())));
//
//                    final Thread thread = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            URL url;
//                            final Bitmap bmp;
//                            try {
//                                //url = new URL(vendorLoc.getMap_icon());
//                                url = new URL("http://54.186.39.139/silenthill/images/emergency.png");
//                                bmp = BitmapFactory.decodeStream(url
//                                        .openConnection().getInputStream());
//
//
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Marker source = mMap
//                                                .addMarker(new MarkerOptions()
//                                                        .position(
//                                                                new LatLng(
//                                                                        Double.parseDouble(vendorLoc.getLongitude()), Double.parseDouble(vendorLoc.getLatitude()))).icon(BitmapDescriptorFactory
//                                                                .fromResource(R.drawable.arrow)));
//                                    }
//                                });
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                    thread.start();
//                    //						 pt = new PoiTarget(m);
//                    //						 poiTargets.add(pt);
//                    //						 Picasso.with(AurospaceHomeActivity.this)
//                    //						 .load(vendorLoc.getMap_icon()).into(pt);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } // end catch
//        }
//      //   mHandler.postDelayed(mRunnable, 15000);

    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        switch (id){
//            case R.id.fab:
//
//                animateFAB();
//                break;
//            case R.id.fab1:
//              startActivity(new Intent(getContext(), LocationActivity.class));
//                break;
//            case R.id.fab2:
//
//                break;
//        }
//    }
//    public void animateFAB(){
//
//        if(isFabOpen){
//
//            fab.startAnimation(rotate_backward);
//            fab1.startAnimation(fab_close);
//            fab2.startAnimation(fab_close);
//            fab1.setClickable(false);
//            fab2.setClickable(false);
//            isFabOpen = false;
//            Log.d("ShopperCrux", "close");
//
//        } else {
//
//            fab.startAnimation(rotate_forward);
//            fab1.startAnimation(fab_open);
//            fab2.startAnimation(fab_open);
//            fab1.setClickable(true);
//            fab2.setClickable(true);
//            isFabOpen = true;
//            Log.d("ShopperCrux","open");
//
//        }
//    }

    public void getStoreList(){

        // Tag used to cancel the request
        final String tag_json_arry = "json_array_req";
        final String jsonResponse;

        // String url = "http://shoppercrux.com/shopper_android_api/storeMap.php?id=56";
        String url = "http://shoppercrux.com/shopper_android_api/seller.php?id="+id;

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("storelist", response.toString());


                        pDialog.hide();

                        JSON_PARSE_DATA_AFTER_WEBCALL(response);
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Map", "Error: " + error.getMessage());
                        pDialog.hide();
                    }
                });
        //   AppController.getInstance().addToRequestQueue(jsonArrayRequest, tag_json_arry);

        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);

//        Log.e("storelist",requestQueue.toString());
//        try {
//            JSONObject resultJson = new JSONObject(requestQueue.toString());
//            Gson mapstorelist = new GsonBuilder().create();
//            Type storeloctype = new TypeToken<List<StoreListModel>>() {
//            }.getType();
//
//            mStoreLocLst = mapstorelist.fromJson(String.valueOf(resultJson.getJSONObject("object")),storeloctype);
//            Log.e("storelist",mStoreLocLst.get(0).toString());
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


    }
    private void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {


        for (int i = 0; i < array.length(); i++) {

            getDataAdapter = new StoreListModel();

            JSONObject json = null;
            try {

                json = array.getJSONObject(i);

                getDataAdapter.setId(json.getString(SELLER_ID));
                getDataAdapter.setName(json.getString(JSON_IMAGE_TITLE_NAME));
                getDataAdapter.setBanner(json.getString(JSON_IMAGE_URL));
                getDataAdapter.setLatitude(json.getString(Lattitude));
                getDataAdapter.setLongitude(json.getString(Longitude));
                getDataAdapter.setAddress(json.getString(SELLER_ADDRESS).replaceAll("\t", ""));
               // Log.e("HHlat", getDataAdapter.getLatitude());
              //  Log.e("HHlong", getDataAdapter.getLongitude());

                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(getDataAdapter.getLatitude().trim()),Double.parseDouble(getDataAdapter.getLongitude().trim()))).icon(BitmapDescriptorFactory.fromResource(R.mipmap.store_location)).title(getDataAdapter.getName()));


//                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
//                {
//
//                    @Override
//                    public boolean onMarkerClick(Marker arg0) {
//                        if(arg0 != null && arg0.getTitle().equals(markerOptions2.getTitle().toString())); // if marker  source is clicked
//                        Toast.makeText(menu.this, arg0.getTitle(), Toast.LENGTH_SHORT).show();// display toast
//                        return true;
//                    }
//
//                });
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        FragmentManager fragmentManager2 = getFragmentManager();
                        FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                        StoreFragment fragment2 = new StoreFragment();
                        Bundle bundle = new Bundle();
                        String data = RecyclerViewAdapter.locationData;
                        bundle.putString("locationId", data);
                        fragment2.setArguments(bundle);
//                fragmentTransaction2.addToBackStack("xyz");
                        fragmentTransaction2.hide(HomeFragment.this);
                        fragmentTransaction2.add(R.id.content_frame, fragment2);
                        fragmentTransaction2.commit();
                    }
                });
//                Marker source = mMap
//                        .addMarker(new MarkerOptions()
//                                .position(
//                                        new LatLng(
//                                                Double.parseDouble(getDataAdapter.getLatitude().trim()),Double.parseDouble(getDataAdapter.getLongitude().trim()))).title(getDataAdapter.getName()));

            } catch (Exception e) {
                e.printStackTrace();
            } // end catch


        }

    }
}







