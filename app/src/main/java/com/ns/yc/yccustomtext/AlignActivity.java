package com.ns.yc.yccustomtext;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.yc.supertextlib.AlignTextView;
import com.yc.supertextlib.BalanceTextView;

public class AlignActivity extends AppCompatActivity {

    private TextView mTv0;
    private AlignTextView mTvAlign0;
    private BalanceTextView mTvSuper0;
    private TextView mTv1;
    private AlignTextView mTvAlign1;
    private BalanceTextView mTvSuper1;
    private TextView mTv2;
    private AlignTextView mTvAlign2;
    private BalanceTextView mTvSuper3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_align_text);



        mTv0 = findViewById(R.id.tv_0);
        mTvAlign0 = findViewById(R.id.tv_align_0);
        mTvSuper0 = findViewById(R.id.tv_super_0);
        mTv1 = findViewById(R.id.tv_1);
        mTvAlign1 = findViewById(R.id.tv_align_1);
        mTvSuper1 = findViewById(R.id.tv_super_1);
        mTv2 = findViewById(R.id.tv_2);
        mTvAlign2 = findViewById(R.id.tv_align_2);
        mTvSuper3 = findViewById(R.id.tv_super_3);

        mTv0.setText(getResources().getString(R.string.content0));
        mTvAlign0.setText(getResources().getString(R.string.content0));
        mTvSuper0.setText(getResources().getString(R.string.content0));
        mTv1.setText(getResources().getString(R.string.content1));
        mTvAlign1.setText(getResources().getString(R.string.content1));
        mTvSuper1.setText(getResources().getString(R.string.content1));
        mTv2.setText(getResources().getString(R.string.content2));
        mTvAlign2.setText(getResources().getString(R.string.content2));
        mTvSuper3.setText(getResources().getString(R.string.content2));
    }
}
