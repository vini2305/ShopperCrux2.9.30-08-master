package com.wvs.shoppercrux.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowCloseListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.Utils.PermissionUtils;
import com.wvs.shoppercrux.fragments.NavigationDrawerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This shows how to place markers on a map.
 */
public class StoreMapView extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMarkerClickListener,
        OnInfoWindowClickListener,
        OnMarkerDragListener,
        OnSeekBarChangeListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        OnInfoWindowLongClickListener,
        OnInfoWindowCloseListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);
    private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);
    private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);
    private final List<Marker> mMarkerRainbow = new ArrayList<Marker>();
    private final Random mRandom = new Random();
    public List<LatLng> latLngList;
    public double LAT;
    public double LON;
    String GET_JSON_DATA_HTTP_URL = "http://prachodayat.in/shopper_android_api/sellermap.php?id=";
    JsonArrayRequest jsonArrayRequest;
    RequestQueue requestQueue;
    Toolbar toolbar;
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    private String STORE_URL;
    private GoogleMap mMap;
    private Marker mPerth;
    private Marker mSydney;
    private Marker mBrisbane;
    private Marker mAdelaide;
    private Marker mMelbourne;
    /**
     * Keeps track of the last selected marker (though it may no longer be selected).  This is
     * useful for refreshing the info window.
     */
    private Marker mLastSelectedMarker;
    private TextView mTopText;
    private TextView mTagText;
    private String nickName;
    private SeekBar mRotationBar;

    private CheckBox mFlatBox;

    private RadioGroup mOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_map_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Stores");
       //toolbar.setTitleTextColor(Integer.parseInt("#FFFFFF"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,(DrawerLayout)findViewById(R.id.drawer_layout),toolbar);
        Intent intent = getIntent();
        String id = intent.getStringExtra("location_id");
//        textView = (TextView) findViewById(R.id.display_id);
//        textView.setText(id);
        Log.d("location id", id);
        STORE_URL = GET_JSON_DATA_HTTP_URL + id;
        Log.d("location id", STORE_URL);

        mTopText = (TextView) findViewById(R.id.top_text);
        mTagText = (TextView) findViewById(R.id.tag_text);

        //mRotationBar = (SeekBar) findViewById(R.id.rotationSeekBar);
        //mRotationBar.setMax(360);
        //  mRotationBar.setOnSeekBarChangeListener(this);

        // mFlatBox = (CheckBox) findViewById(R.id.flat);

        //mOptions = (RadioGroup) findViewById(R.id.custom_info_window_options);
//        mOptions.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (mLastSelectedMarker != null && mLastSelectedMarker.isInfoWindowShown()) {
//                    // Refresh the info window when the info window's content has changed.
//                    mLastSelectedMarker.showInfoWindow();
//                }
//            }
//        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //   JSON_DATA_WEB_CALL();
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
    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        // Hide the zoom controls as the button panel will cover it.
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Add lots of markers to the map.
//        addMarkersToMap();
        JSON_DATA_WEB_CALL();

        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowCloseListener(this);
        mMap.setOnInfoWindowLongClickListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription("Map with lots of markers.");

        // Pan to see all markers in view.
        // Cannot zoom to bounds until the map has a size.
        final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation") // We use the new method when supported
                @SuppressLint("NewApi") // We check which build version we are using.
                @Override
                public void onGlobalLayout() {
                    LatLngBounds bounds = new LatLngBounds.Builder()
                            .include(PERTH)
                            .include(SYDNEY)
                            .include(ADELAIDE)
                            .include(new LatLng(LAT, LON))
                            .include(new LatLng(LAT, LON))
                            .build();

                    Log.d("Latitude", String.valueOf(LAT));
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private void addMarkersToMap() {
        JSON_DATA_WEB_CALL();
        // Uses a colored icon.
        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(LAT, LON))
                .title("Brisbane")
                .snippet("Population: 2,074,200")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        // Uses a custom icon with the info window popping out of the center of the icon.
        mSydney = mMap.addMarker(new MarkerOptions()
                .position(SYDNEY)
                .title("Sydney")
                .snippet("Population: 4,627,300")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow))
                .infoWindowAnchor(0.5f, 0.5f));

        // Creates a draggable marker. Long press to drag.
        mMelbourne = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(LAT, LON))
                .title("Melbourne")
                .snippet("Population: 4,137,400")
                .draggable(true));

        // A few more markers for good measure.
        mPerth = mMap.addMarker(new MarkerOptions()
                .position(PERTH)
                .title("Perth")
                .snippet("Population: 1,738,800"));
        mAdelaide = mMap.addMarker(new MarkerOptions()
                .position(ADELAIDE)
                .title("Adelaide")
                .snippet("Population: 1,213,000"));

        // Creates a marker rainbow demonstrating how to create default marker icons of different
        // hues (colors).
        float rotation = mRotationBar.getProgress();
        boolean flat = mFlatBox.isChecked();

//        int numMarkersInRainbow = 12;
//        for (int i = 0; i < numMarkersInRainbow; i++) {
//            Marker marker = mMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(
//                            -30 + 10 * Math.sin(i * Math.PI / (numMarkersInRainbow - 1)),
//                            135 - 10 * Math.cos(i * Math.PI / (numMarkersInRainbow - 1))))
//                    .title("Marker " + i)
//                    .icon(BitmapDescriptorFactory.defaultMarker(i * 360 / numMarkersInRainbow))
//                    .flat(flat)
//                    .rotation(rotation));
//            marker.setTag(0);
//            mMarkerRainbow.add(marker);
//        }
    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Called when the Clear button is clicked.
     */
    public void onClearMap(View view) {
        if (!checkReady()) {
            return;
        }
        mMap.clear();
    }

    /**
     * Called when the Reset button is clicked.
     */
    public void onResetMap(View view) {
        if (!checkReady()) {
            return;
        }
        // Clear the map because we don't want duplicates of the markers.
        mMap.clear();
        // addMarkersToMap();
    }

    /**
     * Called when the Reset button is clicked.
     */
    public void onToggleFlat(View view) {
        if (!checkReady()) {
            return;
        }
        boolean flat = mFlatBox.isChecked();
        for (Marker marker : mMarkerRainbow) {
            marker.setFlat(flat);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!checkReady()) {
            return;
        }
        float rotation = seekBar.getProgress();
        for (Marker marker : mMarkerRainbow) {
            marker.setRotation(rotation);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Do nothing.
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Do nothing.
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker.equals(mPerth)) {
            // This causes the marker at Perth to bounce into position when it is clicked.
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final long duration = 1500;

            final Interpolator interpolator = new BounceInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = Math.max(
                            1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                    marker.setAnchor(0.5f, 1.0f + 2 * t);

                    if (t > 0.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
            });
        } else if (marker.equals(mAdelaide)) {
            // This causes the marker at Adelaide to change color and alpha.
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(mRandom.nextFloat() * 360));
            marker.setAlpha(mRandom.nextFloat());
        }

        // Markers have a z-index that is settable and gettable.
        float zIndex = marker.getZIndex() + 1.0f;
        marker.setZIndex(zIndex);
        Toast.makeText(this, marker.getTitle() + " z-index set to " + zIndex,
                Toast.LENGTH_SHORT).show();

        // Markers can store and retrieve a data object via the getTag/setTag methods.
        // Here we use it to retrieve the number of clicks stored for this marker.
        Integer clickCount = (Integer) marker.getTag();
        // Check if a click count was set.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            // Markers can store and retrieve a data object via the getTag/setTag methods.
            // Here we use it to store the number of clicks for this marker.
            marker.setTag(clickCount);
            mTagText.setText(marker.getTitle() + " has been clicked " + clickCount + " times.");
        } else {
            mTagText.setText("");
        }

        mLastSelectedMarker = marker;
        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    //
    // Marker related listeners.
    //

    @Override
    public void onInfoWindowClick(Marker marker) {

        Toast.makeText(this, "Click Info Window", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        //Toast.makeText(this, "Close Info Window", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        Toast.makeText(this, "Info Window long click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

        mTopText.setText("onMarkerDragStart");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mTopText.setText("onMarkerDragEnd");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        mTopText.setText("onMarkerDrag.  Current Position: " + marker.getPosition());
    }

    public void JSON_DATA_WEB_CALL() {

        jsonArrayRequest = new JsonArrayRequest(STORE_URL,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        JSON_PARSE_DATA_AFTER_WEBCALL(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(jsonArrayRequest);
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {
        latLngList = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {

            JSONObject json = null;
            try {

                json = array.getJSONObject(i);


                String lat = json.getString("latitude");
                String longi = json.getString("longitude");
                String countryName = json.getString("nickname");

                LAT = Double.parseDouble(lat);
                LON = Double.parseDouble(longi);
                // nickName = json.getString("nickname");

                // Uses a colored icon.
                mBrisbane = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(LAT, LON))
                        .title(countryName)
                        .snippet("Population: 2,074,200")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng((new LatLng(LAT, LON))));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(10));

//                // Uses a custom icon with the info window popping out of the center of the icon.
//                mSydney = mMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(LAT, LON))
//                        .title("Sydney")
//                        .snippet("Population: 4,627,300")
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow))
//                        .infoWindowAnchor(0.5f, 0.5f));
//
//                // Creates a draggable marker. Long press to drag.
//                mMelbourne = mMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(LAT, LON))
//                        .title("Melbourne")
//                        .snippet("Population: 4,137,400")
//                        .draggable(true));
//
//                // A few more markers for good measure.
//                mPerth = mMap.addMarker(new MarkerOptions()
//                        .position(PERTH)
//                        .title("Perth")
//                        .snippet("Population: 1,738,800"));
//                mAdelaide = mMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(LAT, LON))
//                        .title("Adelaide")
//                        .snippet("Population: 1,213,000"));

                // Creates a marker rainbow demonstrating how to create default marker icons of different
                // hues (colors).
                //  float rotation = mRotationBar.getProgress();
                //boolean flat = mFlatBox.isChecked();

//        int numMarkersInRainbow = 12;
//        for (int i = 0; i < numMarkersInRainbow; i++) {
//            Marker marker = mMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(
//                            -30 + 10 * Math.sin(i * Math.PI / (numMarkersInRainbow - 1)),
//                            135 - 10 * Math.cos(i * Math.PI / (numMarkersInRainbow - 1))))
//                    .title("Marker " + i)
//                    .icon(BitmapDescriptorFactory.defaultMarker(i * 360 / numMarkersInRainbow))
//                    .flat(flat)
//                    .rotation(rotation));
//            marker.setTag(0);
//            mMarkerRainbow.add(marker);
//        }


            } catch (JSONException e) {

                e.printStackTrace();
            }
            latLngList.add(new LatLng(LAT, LON));

        }


    }

    @Override
    public boolean onMyLocationButtonClick() {

        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    /**
     * Demonstrates customizing the info window and/or its contents.
     */
    class CustomInfoWindowAdapter implements InfoWindowAdapter {

        // These are both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;

        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
//            if (mOptions.getCheckedRadioButtonId() != R.id.custom_info_window) {
//                // This means that getInfoContents will be called.
//                return null;
//            }
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
//            if (mOptions.getCheckedRadioButtonId() != R.id.custom_info_contents) {
//                // This means that the default info contents will be used.
//                return null;
//            }
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            int badge;
            // Use the equals() method on a Marker to check for equals.  Do not use ==.
            if (marker.equals(mBrisbane)) {
                badge = R.drawable.badge_qld;
            } else if (marker.equals(mAdelaide)) {
                badge = R.drawable.badge_sa;
            } else if (marker.equals(mSydney)) {
                badge = R.drawable.badge_nsw;
            } else if (marker.equals(mMelbourne)) {
                badge = R.drawable.badge_victoria;
            } else if (marker.equals(mPerth)) {
                badge = R.drawable.badge_wa;
            } else {
                // Passing 0 to setImageResource will clear the image view.
                badge = 0;
            }
            ((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);

            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }
        }
    }


}