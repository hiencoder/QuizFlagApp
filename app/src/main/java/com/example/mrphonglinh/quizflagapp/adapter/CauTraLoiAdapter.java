package com.example.mrphonglinh.quizflagapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mrphonglinh.quizflagapp.R;
import com.example.mrphonglinh.quizflagapp.model.SoCauTraLoi;

import java.util.ArrayList;

/**
 * Created by MyPC on 05/04/2017.
 */

public class CauTraLoiAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<SoCauTraLoi> mListSoCau;

    public CauTraLoiAdapter(Context mContext, ArrayList<SoCauTraLoi> mListSoCau) {
        this.mContext = mContext;
        this.mListSoCau = mListSoCau;
    }

    @Override
    public int getCount() {
        return mListSoCau.size();
    }

    @Override
    public SoCauTraLoi getItem(int position) {
        return mListSoCau.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_socau_traloi,
                    parent,false);
        }
        SoCauTraLoi soCauTraLoi = mListSoCau.get(position);
        ImageView btnCheck = (ImageView) convertView.findViewById(R.id.btn_select_socau_traloi);
        TextView txtSoCau = (TextView) convertView.findViewById(R.id.txt_socau_traloi);
        txtSoCau.setText(soCauTraLoi.getSoCau() + "");
        return convertView;
    }
}
