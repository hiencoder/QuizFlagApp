package com.example.mrphonglinh.quizflagapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mrphonglinh.quizflagapp.fragment.FragmentPlay;

import java.util.Set;

public class PlayActivity extends AppCompatActivity {
    //Biến lấy các giá trị trong shared preferences
    public static final String CHOICES = "pref_numberOfChoice";
    public static final String REGIONS = "pref_regionsToInclude";

    //Biến kiểm tra trạng thái thay đổi kiểu hiển thị của màn hình và share
    private boolean mPhoneDevice = true;
    private boolean mChangeShared = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //Set giá trị mặc định cho shared preferences của app
        PreferenceManager.setDefaultValues(this,//Context: cung cấp truy cập thông tin về các thành
                //phần đang chạy trong ứng dụng và cho phép sử dụng các dịch vụ android khác nhau
                R.xml.preferences,//Id của resource cấu hình sharedpreferences
                false //Cho biết liệu giá trị nên đc đặt lại mỗi khi pt setDefaultValues() đc gọi
                //false: giá trị mặc định chỉ đặt lần đầu chạy app
        );

        //Đăng ký lắng nghe cho sự thay đổi của SharedPreferences
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(preferencesChangeListener);

        //Xác định kích thước màn hình
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        //Nếu thiết bị là tablet thì set mPhoneDevice = false
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE){
            mPhoneDevice = false;
        }

        //Nếu thiết bị đang chạy là phone thì chỉ cho phép hiển thị chế độ portrait
        if (mPhoneDevice){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }

    /*- Gọi onstart sau oncreate để đảm bảo rằng bài kiểm tra đã được cấu hình chính xác.
    * dựa trên sharedpreferences mặc định của app khi app đc cài lần đầu hay những lần sau
    * khi mà ta đã thay đổi preferences trong những lần chạy sau
    * - Khi ứng dụng mở SettingActivity thì MainActivity sẽ kill. Khi mở lại MainActivity
    * thì ứng dụng sẽ chạy vào onStart nên ta cần đảm bảo rằng quiz đã đc cấu hình lại shared*/
    @Override
    protected void onStart() {
        super.onStart();
        //Kiểm tra xem có sự thay đổi sharePreferences hay k
        if (mChangeShared){
            //Nếu default sharedpreferences đã đc set
            //Khởi tạo FragmentPlay
            FragmentPlay fragmentPlay = (FragmentPlay) getSupportFragmentManager().findFragmentById(R.id.fr_play);
            //Gọi phương thức update số câu trả lời và update châu lục
            fragmentPlay.updateRow(PreferenceManager.getDefaultSharedPreferences(this));
            fragmentPlay.updateRegion(PreferenceManager.getDefaultSharedPreferences(this));
            fragmentPlay.resetQuiz();
            mChangeShared = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Lấy ra trạng thái hiển thị hiện tại thiết bị
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT){
            //Nếu là portrait thì hiển thị menu
            getMenuInflater().inflate(R.menu.menu_main,menu);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Hiển thị thằng setting
        startActivity(new Intent(PlayActivity.this,SettingActivity.class));
        return super.onOptionsItemSelected(item);
    }

    //Sự kiện lấng nghe sự thay đổi của sharepreferences
    private OnSharedPreferenceChangeListener preferencesChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        //Được gọi khi người dùng thay đổi preferences của app
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            mChangeShared = true;
            FragmentPlay fragmentPlay = (FragmentPlay) getSupportFragmentManager()
                    .findFragmentById(R.id.fr_play);
            if (key.equals(CHOICES)){
                fragmentPlay.updateRow(sharedPreferences);
                fragmentPlay.resetQuiz();
            }else if (key.equals(REGIONS)){
                //Nếu có sự thay đổi thì lấy ra các vùng được kích hoạt
                Set<String> regions = sharedPreferences.getStringSet(REGIONS,null);
                if (regions != null && regions.size() > 0){
                    //Nếu số vùng kích hoạt khác 0(có check)
                    fragmentPlay.updateRegion(sharedPreferences);
                    fragmentPlay.resetQuiz();
                }else {
                    //Set North America là mặc định của region khi null(bỏ check)
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    regions.add(getString(R.string.default_regions));
                    editor.putStringSet(REGIONS,regions);
                    editor.apply();

                    Toast.makeText(PlayActivity.this,
                            getString(R.string.default_region_message),
                            Toast.LENGTH_SHORT).show();
                }
            }
            Toast.makeText(PlayActivity.this, getString(R.string.reset_quiz), Toast.LENGTH_SHORT).show();
        }
    };
}
