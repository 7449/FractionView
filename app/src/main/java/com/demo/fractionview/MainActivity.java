package com.demo.fractionview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.demo.fractionview.view.FractionView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FractionView fractionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fractionView = (FractionView) findViewById(R.id.fraction_view);
        findViewById(R.id.startAnimator).setOnClickListener(this);
        findViewById(R.id.stopAnimator).setOnClickListener(this);
        findViewById(R.id.startOuterAnimator).setOnClickListener(this);
        findViewById(R.id.stopOuterAnimator).setOnClickListener(this);
        findViewById(R.id.startInnerAnimator).setOnClickListener(this);
        findViewById(R.id.stopInnerAnimator).setOnClickListener(this);
        fractionView.setText("MainActivity");
        fractionView.setInnerRingSpeed(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startAnimator:
                fractionView.startAnimator();
                break;
            case R.id.stopAnimator:
                fractionView.stopAnimator();
                break;
            case R.id.startOuterAnimator:
                fractionView.startOuterRoundAnimator();
                break;
            case R.id.stopOuterAnimator:
                fractionView.stopOuterRoundAnimator();
                break;
            case R.id.startInnerAnimator:
                fractionView.startInnerRoundAnimator();
                break;
            case R.id.stopInnerAnimator:
                fractionView.stopInnerRoundAnimator();
                break;
        }
    }
}
