package smu.capstone.paper.activity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import smu.capstone.paper.R;
import smu.capstone.paper.RealmTest;
import smu.capstone.paper.fragment.FragFindId;
import smu.capstone.paper.fragment.FragHomeComu;
import smu.capstone.paper.fragment.FragHomeFeed;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private FragHomeFeed fragHomeFeed;
    private FragHomeComu fragHomeComu;
    private FragFindId.FragHomeProfile fragHomeProfile;
    private FragFindId.FragHomeMarket fragHomeMarket;
    private ImageButton profileBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        profileBtn = findViewById(R.id.home_profile);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              setFrag(3);
            }
        });
        bottomNavigationView = findViewById(R.id.home_tap);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.home_feed:
                        setFrag(0);
                        break;
                    case R.id.home_market:
                        setFrag(1);
                        break;

                    case R.id.home_board:

                        setFrag(2);
                        break;
                }
                return true;
            }
        });

        fragHomeFeed = new FragHomeFeed();
        fragHomeComu = new FragHomeComu();
        fragHomeProfile = new FragFindId.FragHomeProfile();
        fragHomeMarket = new FragFindId.FragHomeMarket();



        setFrag(0);

    }

    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n) {
            case 0:
                ft.replace(R.id.home_frame, fragHomeFeed);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.home_frame, fragHomeMarket);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.home_frame, fragHomeComu);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.home_frame, fragHomeProfile);
                ft.commit();
                break;
        }

    }

    public static class MainActivity extends AppCompatActivity {

        Button temp_login;
        Button temp_home;
        Button temp_realm;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            temp_login = findViewById(R.id.temp_login);
            temp_home = findViewById(R.id.temp_home);
            temp_realm = findViewById(R.id.go_temp_realm);


            temp_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent( MainActivity.this, LoginActivity.class);
                    startActivity(intent);

                }
            });


            temp_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            });


            temp_realm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, RealmTest.class);
                    startActivity(intent);

                }
            });



        }
    }

    public static class JoinActivity extends AppCompatActivity {

        TextView join_timer;
        CountDownTimer countDownTimer;
        Button join_send_btn, join_check_key;
        ImageButton join_back_btn;
        RadioGroup join_sns_radio;
        EditText join_sns_text;

        final int MILLISINFUTURE = 180 * 1000;
        final int COUNT_DOWN_INTERVAL = 1000;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_join);

            join_send_btn = (Button)findViewById(R.id.join_send_btn);
            join_back_btn = (ImageButton)findViewById(R.id.join_back_btn);
            join_check_key = (Button)findViewById(R.id.join_check_key);
            join_timer = findViewById(R.id.join_timer);
            join_sns_radio = (RadioGroup)findViewById(R.id.join_sns_radio);
            join_sns_text = (EditText)findViewById(R.id.join_sns_text);

            join_check_key.setEnabled(false);
            join_timer.setVisibility(View.INVISIBLE); // 인증 제한시간 숨기기

            // 이메일 인증 버튼 눌렀을 시 발생
            join_send_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    join_check_key.setEnabled(true); // 인증번호 확인 버튼 활성화
                    join_timer.setVisibility(View.VISIBLE); // 인증 제한시간 표시
                    countDownTimer(); // 타이머 작동
                }
            });

            join_back_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            join_sns_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // sns 무 선택 -> 입력창 비활성화
                    if(checkedId == R.id.join_sns_n) {
                        join_sns_text.setClickable(false);
                        join_sns_text.setFocusable(false);
                    }
                    // sns 유 선택 -> 입력창 활성화
                    else{
                        join_sns_text.setFocusableInTouchMode(true);
                        join_sns_text.setFocusable(true);
                    }
                }
            });
        }

        public void countDownTimer(){

            countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL ) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long endCount = millisUntilFinished / 1000;

                    if((endCount - ((endCount / 60) * 60)) >= 10){
                        join_timer.setText((endCount / 60) + " : " + (endCount - ((endCount / 60) * 60)));
                    }
                    else // 10초 이하일 때 0붙여 출력
                        join_timer.setText((endCount / 60) + " : 0" + (endCount - ((endCount / 60) *60)));
                }

                @Override
                public void onFinish() { // 카운트 종료 시 일어날 일
                    join_check_key.setEnabled(false); // 인증번호 확인 버튼 비활성화
                }
            }.start();

            // 인증번호 입력 -> 확인 버튼 눌렀을 시 발생
            join_check_key.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    countDownTimer.cancel(); // 타이머 종료
                }
            });
        }

    }

    public static class UploadActivity extends AppCompatActivity {
        Toolbar toolbar;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_upload);

            toolbar = (Toolbar)findViewById(R.id.upload_toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setTitle("");
            toolbar.setSubtitle("");

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24);

        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu){
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.upload_top_menu, menu);
            return true;
        }

        // 툴바 select event 처리
        @Override
        public boolean onOptionsItemSelected(MenuItem item){
            switch(item.getItemId()){
                case R.id.upload_cancel:
                    Toast.makeText(getApplicationContext(), "click next", Toast.LENGTH_LONG).show();
                    break;
                case android.R.id.home:
                    finish();
                    return true;
            }

            return super.onOptionsItemSelected(item);
        }


    }
}