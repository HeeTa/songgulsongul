package com.smu.songgulsongul.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.smu.songgulsongul.R;
import com.smu.songgulsongul.fragment.FragUploadCam;
import com.smu.songgulsongul.fragment.FragUploadGal;

import es.dmoral.toasty.Toasty;

public class UploadActivity extends AppCompatActivity {

    final private int GALLERY = 123;
    final private int CAMERA = 456;

    private BottomNavigationView bottomNavigationView;
    FragUploadCam fragUploadCam;
    FragUploadGal fragUploadGal;
    private Toolbar toolbar;
    private FragmentManager fm;
    private FragmentTransaction ft;

    int frag_status;
    public boolean isQuick;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //편집모드 체킹
        isQuick = getIntent().getBooleanExtra("isQuick", false);


        //툴바 세팅
        toolbar = findViewById(R.id.upload_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기


        //프레그먼트 세팅
        fragUploadCam = new FragUploadCam(isQuick);
        fragUploadGal = new FragUploadGal();

        //하단 네비게이션뷰 세팅
        bottomNavigationView = findViewById(R.id.upload_tap);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.upload_cam:
                        setFrag(CAMERA);
                        break;
                    case R.id.upload_gal:
                        setFrag(GALLERY);
                        break;
                }
                return true;
            }
        });


        setFrag(CAMERA);

    }

    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n) {
            case CAMERA:
                ft.replace(R.id.upload_frame, fragUploadCam);
                ft.commit();
                frag_status = CAMERA;
                invalidateOptionsMenu();

                break;
            case GALLERY:
                ft.replace(R.id.upload_frame, fragUploadGal);
                ft.commit();
                frag_status = GALLERY;
                invalidateOptionsMenu();
                break;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.next_toolbar, menu);
        menu.findItem(R.id.toolbar_next).setVisible(frag_status != CAMERA);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home: { // 뒤로가기 버튼 눌렀을 때
                finish();
                return true;
            }

            case R.id.toolbar_next:
                if (frag_status == GALLERY) {

                    String filePath = fragUploadGal.getPicked_path();
                    if (filePath != null) {
                        Intent intent;
                        if (isQuick)
                            intent = new Intent(this, EditDoneActivity.class);

                        else
                            intent = new Intent(this, DetectPaperActivity.class);


                        intent.putExtra("path", filePath);
                        startActivity(intent);
                        finish();
                    } else {
                        Toasty.normal(this, "선택된 사진이 없습니다").show();
                    }

                } else if (frag_status == CAMERA) {
                    //  Not happened!
                }
                return true;

        }
        return true;
    }

}
