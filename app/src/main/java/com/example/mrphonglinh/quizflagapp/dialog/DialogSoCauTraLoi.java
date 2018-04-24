package com.example.mrphonglinh.quizflagapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.ListView;

import com.example.mrphonglinh.quizflagapp.R;
import com.example.mrphonglinh.quizflagapp.adapter.CauTraLoiAdapter;
import com.example.mrphonglinh.quizflagapp.model.SoCauTraLoi;

import java.util.ArrayList;

/**
 * Created by MyPC on 05/04/2017.
 */

public class DialogSoCauTraLoi extends Dialog {
    private ListView mLvSoCau;
    private ArrayList<SoCauTraLoi> mListSoCau = new ArrayList<>();
    private CauTraLoiAdapter mSoCauAdapter;
    private Context mContext;
    public DialogSoCauTraLoi(Context context) {
        super(context);
        mContext = context;
        mLvSoCau = (ListView) findViewById(R.id.lv_socau_traloi);
        setupAdapter();
    }

    private void setupAdapter() {

        mSoCauAdapter = new CauTraLoiAdapter(mContext,mListSoCau);
        mLvSoCau.setAdapter(mSoCauAdapter);
    }
}
