package com.wvs.shoppercrux.CategoryDrawerItems;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.fragments.Constants;
import com.wvs.shoppercrux.fragments.HomeFragment;
import com.wvs.shoppercrux.fragments.StoreFragment;

import java.util.List;

import static com.wvs.shoppercrux.fragments.NavigationDrawerFragment.readFromPreferences;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {

    private List<ItemObject> itemList;
    private Context context;
    public static String LOCATION_PIN = "LocationPin";
    public static String categoryId;
    boolean mUserLearnedDrawer;
    public static final String KEY_USER__LEARNED_DRAWER="user_learned_drawer";
    public RecyclerViewAdapter(Context context, List<ItemObject> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item2, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, final int position) {
        holder.categoryName.setText("" + itemList.get(position).getCategoryName());
        holder.categoryId.setText("" + itemList.get(position).getCategoryId());
        mUserLearnedDrawer =Boolean.valueOf(readFromPreferences(context,KEY_USER__LEARNED_DRAWER,"false")) ;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = context.getSharedPreferences(LOCATION_PIN, context.MODE_PRIVATE);
                String sharedText = preferences.getString("LocationId", null);
                if (sharedText != null) {
                    categoryId = itemList.get(position).getCategoryId();
                    Constants.catId =categoryId;
                    Log.d("/conatN=", Constants.catId);

                    Log.d("nav_cat_id", categoryId);
                    Log.d("locationShared",sharedText);
                    StoreFragment fragment = new StoreFragment();
                    HomeFragment  mapfragment = new HomeFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("categoryId", categoryId);
                    bundle.putString("locationId", sharedText);
                    fragment.setArguments(bundle);
                    if(Constants.mapView==true){
                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                                .add(R.id.content_frame, mapfragment)
                                .commit();

                    }else {
                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                                .add(R.id.content_frame, fragment)
                                .commit();
                    }



                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}
