package com.wvs.shoppercrux.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.fragments.Constants;
import com.wvs.shoppercrux.fragments.HomeFragment;
import com.wvs.shoppercrux.fragments.StoreFragment;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {

    public static String locationData;
    private List<ItemObject> itemList;
    private Context context;
    public static String LOCATION_PIN="LocationPin";

    private String categoryId;
    private String pinLat;

    public RecyclerViewAdapter(Context context, List<ItemObject> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent,false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, final int position) {
        holder.locationName.setText("" + itemList.get(position).getLocationName());
        holder.locationId.setText("" + itemList.get(position).getLocationId());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public String locName;
            public String pinLong;
            @Override
            public void onClick(View v) {

                locationData = itemList.get(position).getLocationId();
                categoryId  = itemList.get(position).getLocationPincode();
                pinLat  = itemList.get(position).getLatitude();
                pinLong  = itemList.get(position).getLongitude();
                locName   = itemList.get(position).getLocationName();

               StoreFragment store_fragment = new StoreFragment();
                HomeFragment map_fragment = new HomeFragment();
                Bundle bundle = new Bundle();
                bundle.putString("locationId", locationData);
                bundle.putString("categoryId",categoryId);
                bundle.putString("pinLat",pinLat);
                bundle.putString("pinLong",pinLong);
                bundle.putString("locName",locName);
                Constants.setLocation =true;
                if(Constants.mapView==true) {


                    map_fragment.setArguments(bundle);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                            .add(R.id.content_frame, map_fragment)
                            .commit();
                }else{
                    store_fragment.setArguments(bundle);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                            .add(R.id.content_frame, store_fragment)
                            .commit();
                }

                SharedPreferences preferences = context.getSharedPreferences(LOCATION_PIN,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("LocationId",itemList.get(position).getLocationId());
                editor.putString("Pincode",itemList.get(position).getLocationPincode());
                editor.putString("Latitude",itemList.get(position).getLatitude());
                editor.putString("Longitude",itemList.get(position).getLongitude());
                editor.putString("LocationName",itemList.get(position).getLocationName());
                editor.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}
