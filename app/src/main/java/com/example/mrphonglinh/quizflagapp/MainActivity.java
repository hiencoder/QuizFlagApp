package com.example.mrphonglinh.quizflagapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    /*Mô tả chức năng:
    * - Vào màn hình chính sẽ có hiển thị số câu trả lời và vùng để chọn
    * - Click vào số câu trả lời thì sẽ show ra dialog*/
    private TextView mBtnAbout;
    private TextView mBtnSetting;
    private TextView mBtnExit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    private void initEvent() {
        mBtnAbout.setOnClickListener(this);
        mBtnSetting.setOnClickListener(this);
        mBtnExit.setOnClickListener(this);
    }

    private void initView() {
        mBtnAbout = (TextView) findViewById(R.id.btn_about);
        mBtnSetting = (TextView) findViewById(R.id.btn_setting);
        mBtnExit = (TextView) findViewById(R.id.btn_exit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_about:
                startActivity(new Intent(MainActivity.this,PlayActivity.class));
                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
                break;
            case R.id.btn_setting:
                startActivity(new Intent(MainActivity.this,SettingActivity.class));
                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
                break;
            case R.id.btn_exit:
                break;
        }
    }
}
