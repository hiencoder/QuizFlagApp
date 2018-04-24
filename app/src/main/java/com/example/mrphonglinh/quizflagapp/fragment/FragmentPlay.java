package com.example.mrphonglinh.quizflagapp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mrphonglinh.quizflagapp.PlayActivity;
import com.example.mrphonglinh.quizflagapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * Created by MyPC on 07/04/2017.
 */

public class FragmentPlay extends Fragment {
    //String để log error message
    private static final String TAG = FragmentPlay.class.getSimpleName();
    //Biến số câu hỏi
    private static final int FLAG_IN_QUIZ = 10;
    private ArrayList<String> mFileNameList;    //list tên flag
    private ArrayList<String> mQuizCountriesList;   // list tên nước cho bài kiểm tra
    private Set<String> mRegionSet; //Set các châu lục hiện tại
    private String mCorrectAnswer; //Đáp án câu hỏi hiện tại
    private int mTotalGues; //Tổng số câu đã trả lời(cả đúng và sai)
    private int mCorrectAnswers; //Số câu trả lời đúng
    private int mGuesRow; //Số dòng hiển thị button
    private SecureRandom mRandom; //Lấy ngẫu nhiên câu hỏi
    private Handler mHandler; //Sử dụng để delay khi next flag
    private Animation mShakeAnimation; //Animation khi trả lời sai

    private LinearLayout mQuizLayout; //Layout chứa câu trả lời
    private TextView mTxtQuestionNumber; //Câu hỏi hiện tại
    private ImageView mImgFlag; //Hiển thị flag
    private LinearLayout[] mGuesLayout; //số dòng các button
    private TextView mTxtAnswer; //Textview hiển thị đáp án
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play,container,false);
        mFileNameList = new ArrayList<>();
        mQuizCountriesList = new ArrayList<>();
        mRandom = new SecureRandom();
        mHandler = new Handler();

        //Load animation nếu tra loi sai
        mShakeAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.incorrect_shake);
        mShakeAnimation.setRepeatCount(3);//Lặp lại 3 lần

        //Ánh xạ các view
        mQuizLayout = (LinearLayout) view.findViewById(R.id.layout_quiz);
        mTxtQuestionNumber = (TextView) view.findViewById(R.id.txt_number_question);
        mImgFlag = (ImageView) view.findViewById(R.id.img_flag);
        mGuesLayout = new LinearLayout[4];
        mGuesLayout[0] = (LinearLayout) view.findViewById(R.id.layout_row1);
        mGuesLayout[1] = (LinearLayout) view.findViewById(R.id.layout_row2);
        mGuesLayout[2] = (LinearLayout) view.findViewById(R.id.layout_row3);
        mGuesLayout[3] = (LinearLayout) view.findViewById(R.id.layout_row4);
        mTxtAnswer = (TextView) view.findViewById(R.id.txt_correct_answer);

        //Set sự kiện cho các button của layout
        for (LinearLayout row: mGuesLayout){
            for (int i = 0; i < row.getChildCount(); i++) {
                TextView button = (TextView) row.getChildAt(i);
                button.setOnClickListener(mGuesButtonListener);
            }
        }
        //Set text number question ban đầu
        mTxtQuestionNumber.setText(getString(R.string.question,1,FLAG_IN_QUIZ));

        return view;
    }

    //Sự kiện click cho button
    private View.OnClickListener mGuesButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView button = (TextView) v;
            String guess = button.getText().toString(); //text của thằng button click
            String answer = getCountryName(mCorrectAnswer);
            ++mTotalGues;

            if (guess.equals(answer)){
                //nếu trả lời đúng
                ++mCorrectAnswers;

                //Hiển thị câu trả lời đúng
                mTxtAnswer.setText(answer);
                mTxtAnswer.setTextColor(getResources().getColor(R.color.correct_answer));

                disableButton(); //disable tất cả các button

                //Nếu trả lời đúng hết
                if (mCorrectAnswers == FLAG_IN_QUIZ){
                    //Hiển thị dialog fragment và start bài mới
                    DialogFragment quizResults = new DialogFragment(){
                        //Create diaog cho dialog fragment
                        @Override
                        public Dialog onCreateDialog(Bundle savedInstanceState) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Thông báo");
                            builder.setMessage(getString(R.string.results,mTotalGues,(1000/(double)mTotalGues)));
                            //Reset quiz
                            builder.setPositiveButton(R.string.reset_quiz, new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    resetQuiz();
                                }
                            });
                            return builder.create();
                        }
                    };

                    //Sử dụng fragment manager để hiển thị Dialog fragment
                    quizResults.setCancelable(false);
                    quizResults.show(getFragmentManager(),"quiz result");
                }else {
                    //Trả lời đúng nhưng bài kiểm tra chưa kết thúc
                    //Load next flag sau khoảng 2s
                    mHandler.postDelayed(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                            //animate(true);
                            loadNextFlag();
                        }
                    },2000);
                }
            }else {
                //Nếu trả lời sai
                mImgFlag.startAnimation(mShakeAnimation); //chạy animation shake

                //hiển thị thông báo không chính xác
                mTxtAnswer.setText(R.string.incorrect_answer);
                mTxtAnswer.setTextColor(getResources().getColor(R.color.incorrect_answer));
                button.setEnabled(false); //disable button k chính xác
            }
        }
    };

    //Ẩn các button
    private void disableButton() {
        for (int i = 0; i < mGuesRow; i++) {
            LinearLayout guesRow = mGuesLayout[i];
            for (int j = 0; j < guesRow.getChildCount(); j++) {
                guesRow.getChildAt(j).setEnabled(false);
            }
        }
    }

    //Viết phương thức update số dòng dựa trên sự thay đổi của shared preferences
    public void updateRow(SharedPreferences preferences){
        //Lấy ra số button nên hiển thị
        String choices = preferences.getString(PlayActivity.CHOICES,null);
        mGuesRow = Integer.parseInt(choices)/2;

        //Ẩn đi tất cả các layout của button
        for (LinearLayout layout: mGuesLayout){
            layout.setVisibility(View.GONE);
        }

        //Hiển thị lại theo số dòng
        for (int i = 0; i < mGuesRow; i++) {
            mGuesLayout[i].setVisibility(View.VISIBLE);
        }
    }

    //Phương thức update khu vực dựa theo sự thay đổi của share
    public void updateRegion(SharedPreferences preferences){
        //Lấy ra tập hợp khu vực trong shared
        mRegionSet = preferences.getStringSet(PlayActivity.REGIONS,null);

    }

    //Phương thức resetquiz
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetQuiz(){
        //Sử dụng AssetManager để tên các file ảnh theo khu vực
        AssetManager assetManager = getActivity().getAssets();
        mFileNameList.clear(); //làm trống arraylist
        try {
            for (String region: mRegionSet) {
                //Lấy ra danh sách ảnh các cờ thuộc khu vực đang duyệt
                String[] paths = assetManager.list(region);
                //Duyệt vòng lặp để lấy ra tên các file ảnh đưa vào Arraylist
                for (String path : paths){
                    mFileNameList.add(path.replace(".png",""));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading image file name", e);
        }

        mCorrectAnswers = 0; //reset lại số câu trả lời đúng
        mTotalGues = 0; //reset lại tổng số câu đã trả lời
        mQuizCountriesList.clear(); //reset lại list các nước cho bài kiểm tra

        int flagCounter = 1;
        int numberOfFlag = mFileNameList.size();
        //add random 10 file vào list countries
        while (flagCounter <= FLAG_IN_QUIZ){
            int randomIndex = mRandom.nextInt(numberOfFlag);

            //lấy ra tên file random trong mFileNameList
            String fileName = mFileNameList.get(randomIndex);

            //Add vào mCountrieList nếu câu hỏi chưa có trong list
            if (!mQuizCountriesList.contains(fileName)){
                mQuizCountriesList.add(fileName);
                ++flagCounter;
            }
        }

        loadNextFlag(); //bắt đầu bài kiểm tra bằng cách load câu đầu tiên

    }

    //Sau khi trả lời đúng thì cho phép next câu tiếp theo
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void loadNextFlag() {
        //Lấy ra tên file của lá cờ tiếp và xóa nó khỏi list
        String nextImage = mQuizCountriesList.remove(0);
        mCorrectAnswer = nextImage; //Cập nhật correctAnswer
        mTxtAnswer.setText(""); //Set lại textview hiển thị câu trả lời đúng

        //Hiển thị số câu hỏi hiện tại
        mTxtQuestionNumber.setText(getString(R.string.question,(mCorrectAnswers + 1),FLAG_IN_QUIZ));

        //Tách khu vực từ file tên của ảnh tiếp theo
        String region = nextImage.substring(0,nextImage.indexOf('-'));

        //Sử dụng AssetManager đê load next image từ assets folder
        AssetManager assetManager = getActivity().getAssets();

        //Sử dụng inputstream để đọc dữ liệu từ asset
        try {
            InputStream stream = assetManager.open(region + "/" + nextImage + ".png");
            Drawable flag = Drawable.createFromStream(stream,nextImage);
            mImgFlag.setImageDrawable(flag);

            //animate(false); //Sử dụng hiệu ứng khi hiển thị image lên màn hình
            
        } catch (IOException e) {
            Log.e(TAG, "Error loading " + nextImage, e);
        }

        //Shuffle filename
        Collections.shuffle(mFileNameList);

        //Đưa câu trả lời đúng vào cuối list
        int correct = mFileNameList.indexOf(mCorrectAnswer); //Lấy ra vị trí câu trả lời đúng
        mFileNameList.add(mFileNameList.remove(correct));

        //Add 2,4,6,8 button dựa trên số dòng
        for (int i = 0; i < mGuesRow; i++) {
            for (int j = 0; j < mGuesLayout[i].getChildCount(); j++) {
                TextView newButton = (TextView) mGuesLayout[i].getChildAt(j);
                newButton.setEnabled(true);

                //Lấy tên nước và set giá trị cho các button
                String fileName = mFileNameList.get((i * 2) + j);
                newButton.setText(getCountryName(fileName));
            }
        }

        //Hiển thị ngẫu nhiên vị trí của button câu trả lời đúng
        int rowRandom = mRandom.nextInt(mGuesRow); //random dòng
        int rowColumn = mRandom.nextInt(2); //random cột
        LinearLayout layoutRandom = mGuesLayout[rowRandom];
        String countryName = getCountryName(mCorrectAnswer);
        ((TextView)layoutRandom.getChildAt(rowColumn)).setText(countryName);
    }

    //Phương thức này truyền vào tên file của lá cờ và trả về tên nước
    private String getCountryName(String name) {
        return name.substring(name.indexOf('-') + 1).replace('_',' '); //subtring để lấy ra
        //tên nước rồi thay thế các ký tự gạch dưới bằng ký tự khoảng trắng
    }


/*
    //Phương thức hiệu ứng khi hiển thị image lên màn hinh chuyển giữa 2 câu hỏi
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void animate(boolean animateOut) {
        if (mCorrectAnswers == 0)
            return;

        //Tính toán center x và center y
        int centerX = (mQuizLayout.getLeft() + mQuizLayout.getRight()) / 2;
        int centerY = (mQuizLayout.getTop() + mQuizLayout.getBottom()) / 2;

        //Tính bán kính của animation
        int radius = Math.max(mQuizLayout.getHeight(),mQuizLayout.getWidth());

        Animator animator;
        if (animateOut){
            animator = ViewAnimationUtils.createCircularReveal(mQuizLayout,centerX,centerY,radius,0);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    loadNextFlag();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }else {
            animator = ViewAnimationUtils.createCircularReveal(mQuizLayout,centerX,centerY,0,radius);
        }
        animator.setDuration(500);
        animator.start();
    }
*/
}
