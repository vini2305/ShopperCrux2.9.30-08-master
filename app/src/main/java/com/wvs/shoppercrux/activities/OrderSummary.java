package com.wvs.shoppercrux.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.Summary.OrderSummaryAdapter;
import com.wvs.shoppercrux.Summary.OrderSummaryPojo;
import com.wvs.shoppercrux.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderSummary extends AppCompatActivity {

    private Button goback;
    private RecyclerView orderSummary;
    private List<OrderSummaryPojo> pojoList;
    private OrderSummaryAdapter orderSummaryAdapter;
    private OrderSummaryPojo pojo;
    private String GET_SUMMARY_ORDER="http://shoppercrux.com/shopper_android_api/myorders.php";
    private String GET_TOTAL_PRICE="http://shoppercrux.com/shopper_android_api/totalprice.php";
    private String SUMMARY,TOTAL;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private JsonArrayRequest jsonArrayRequest,jsonArrayRequest1;
    private RequestQueue requestQueue;
    private TextView total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        goback = (Button) findViewById(R.id.back);
        goback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderSummary.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        getOrderSummary();
        orderSummary = (RecyclerView) findViewById(R.id.summary);
        orderSummary.setHasFixedSize(true);
        recyclerLayoutManager = new LinearLayoutManager(this);
        orderSummary.setLayoutManager(recyclerLayoutManager);
        pojoList = new ArrayList<>();
        total = (TextView) findViewById(R.id.prodTotalPrice);
        getTotalPrice();
    }

    private void getTotalPrice() {

        TOTAL = GET_TOTAL_PRICE+"?cid=100470";
        jsonArrayRequest1 = new JsonArrayRequest(TOTAL, new Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject json = null;
                try {
                    for(int i=0 ; i <response.length();i++) {
                        json = response.getJSONObject(i);
                        Log.d("total price","total price response:"+response);
                        String totalprice = json.getString("totalprice");
                        total.setText(totalprice);
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonArrayRequest1);
    }

    private void getOrderSummary() {
        String cid= SQLiteHandler.user_id;
        SUMMARY = GET_SUMMARY_ORDER +"?cid="+cid;
//        Log.d("OrderSummary","OrderSummary url:"+SUMMARY);

        jsonArrayRequest = new JsonArrayRequest(SUMMARY, new Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
//                Log.d("Order Response","Order response:"+response);
                setOrderRecycle(response);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void setOrderRecycle(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            OrderSummaryPojo pojo = new OrderSummaryPojo();

            try {
                JSONObject json = array.getJSONObject(i);
                pojo.setProductName(json.getString("name"));
                pojo.setQuanity(json.getString("quantity"));
                pojo.setTotal(json.getString("total"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            pojoList.add(pojo);
        }
        orderSummaryAdapter = new OrderSummaryAdapter(pojoList,this);
        orderSummary.setAdapter(orderSummaryAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OrderSummary.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
