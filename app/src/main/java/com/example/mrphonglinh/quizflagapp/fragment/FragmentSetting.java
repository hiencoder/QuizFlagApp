package com.example.mrphonglinh.quizflagapp.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.example.mrphonglinh.quizflagapp.R;

/**
 * Created by MyPC on 08/04/2017.
 */

public class FragmentSetting extends PreferenceFragment {
    //Tạo giao diện tham chiếu từ file preferences.xml

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
