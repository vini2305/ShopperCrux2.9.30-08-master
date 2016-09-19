package com.wvs.shoppercrux.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.wvs.shoppercrux.R;
import com.wvs.shoppercrux.fragments.*;

public class OrderSummary extends AppCompatActivity {

    Button goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        goback = (Button) findViewById(R.id.back);
        goback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                com.wvs.shoppercrux.fragments.Constants.setLocation=false;

                Intent intent = new Intent(OrderSummary.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OrderSummary.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
