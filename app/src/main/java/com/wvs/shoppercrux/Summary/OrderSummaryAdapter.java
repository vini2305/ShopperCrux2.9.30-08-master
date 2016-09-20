package com.wvs.shoppercrux.Summary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wvs.shoppercrux.R;

import java.util.List;

/**
 * Created by Varuni on 19-09-2016.
 */
public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder> {

    Context context;
    List<OrderSummaryPojo> orderSummaryPojo;

    public OrderSummaryAdapter(List<OrderSummaryPojo> orderSummaryPojo, Context context) {
        super();
        this.orderSummaryPojo = orderSummaryPojo;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_order, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

       final OrderSummaryPojo summaryPojo = orderSummaryPojo.get(position);
        if(holder.name != null) {
            holder.name.setText(summaryPojo.getProductName());
            holder.quantity.setText(summaryPojo.getQuanity());
            holder.price.setText(summaryPojo.getTotal());
        }
    }

    @Override
    public int getItemCount() {
        return orderSummaryPojo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name,price,quantity;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.prodName);
            price = (TextView) itemView.findViewById(R.id.prodPrice);
            quantity = (TextView) itemView.findViewById(R.id.prodQuantity);
        }
    }
}
