package com.ns.yc.yccustomtext;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ns.yc.yccustomtext.first.FirstActivity;
import com.ns.yc.yccustomtext.second.SecondActivity;
import com.ns.yc.yccustomtext.third.ThirdActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_1;
    private TextView tv_2;
    private TextView tv_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        tv_1 = (TextView) findViewById(R.id.tv_1);
        tv_1.setOnClickListener(this);

        tv_2 = (TextView) findViewById(R.id.tv_2);
        tv_2.setOnClickListener(this);

        tv_3 = (TextView) findViewById(R.id.tv_3);
        tv_3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_1:
                startActivity(new Intent(this, FirstActivity.class));
                break;
            case R.id.tv_2:
                startActivity(new Intent(this, SecondActivity.class));
                break;
            case R.id.tv_3:
                startActivity(new Intent(this, ThirdActivity.class));
                break;
        }
    }
}
