package com.example.mrphonglinh.quizflagapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mrphonglinh.quizflagapp.R;
import com.example.mrphonglinh.quizflagapp.model.SoCauTraLoi;

import java.util.ArrayList;

/**
 * Created by MyPC on 05/04/2017.
 */

public class SoCauAdapter extends RecyclerView.Adapter<SoCauAdapter.SoCauHolder> {
    private Context mContext;
    private ArrayList<SoCauTraLoi> mListSoCauTraLoi;
    @Override
    public SoCauHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_socau_traloi,parent,false);
        return new SoCauHolder(view);
    }

    @Override
    public void onBindViewHolder(SoCauHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class SoCauHolder extends RecyclerView.ViewHolder {
        private ImageView mBtnCheck;
        private TextView mTxtSoCauTraLoi;
        private SoCauTraLoi mSoCauTraLoi;
        public SoCauHolder(View itemView) {
            super(itemView);
        }
    }
}
