package com.smu.songgulsongul.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.smu.songgulsongul.LoginSharedPreference;
import com.smu.songgulsongul.R;
import com.smu.songgulsongul.recycler_adapter.PostImageRVAdapter;
import com.smu.songgulsongul.data.post.KeepData;
import com.smu.songgulsongul.data.post.Response.KeepResponse;
import com.smu.songgulsongul.recycler_item.Post;
import com.smu.songgulsongul.server.DefaultImage;
import com.smu.songgulsongul.server.RetrofitClient;
import com.smu.songgulsongul.server.ServiceApi;
import com.smu.songgulsongul.server.StatusCode;

public class KeepActivity extends AppCompatActivity {
    // ServiceApi 객체 생성
    ServiceApi serviceApi = RetrofitClient.getClient().create(ServiceApi.class);
    List<Post> keep_data;

    PostImageRVAdapter adapter;
    RecyclerView recyclerView;
    TextView keep_count, keep_id;
    ImageView keep_imae;

    String login_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep);

        keep_count = findViewById(R.id.keep_count);
        keep_id = findViewById(R.id.keep_id);
        keep_imae = findViewById(R.id.keep_imae);
        Toolbar toolbar = (Toolbar) findViewById(R.id.keep_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("보관함");
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24); //뒤로가기 버튼 이미지 지정

        login_id = LoginSharedPreference.getLoginId(KeepActivity.this);

        // view에서 id 찾아야함
        recyclerView = findViewById(R.id.keep_grid);

        getKeepData();

    }

    public void setKeepData(KeepResponse data) {


        // 로그인한 Id로 셋팅
        keep_id.setText(login_id);

        // 프로필 이미지 셋팅
        String profile_image = data.getProfileImg();
        String img_addr;
        if (profile_image.equals(DefaultImage.DEFAULT_IMAGE))
            img_addr = RetrofitClient.getBaseUrl() + profile_image;
        else
            img_addr = profile_image;
        Glide.with(this).load(img_addr).into(keep_imae);

        // 보관한 게시글 개수 셋팅
        int keep_cnt = data.getKeepcnt();
        keep_count.setText("보관한 게시글 " + keep_cnt);

        // 어뎁터 적용

        adapter = new PostImageRVAdapter(this, data.getKeepinfo());
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    //server에서 data전달
    public void getKeepData() {
        KeepData data = new KeepData(login_id);
        serviceApi.Keep(data).enqueue(new Callback<KeepResponse>() {
            @Override
            public void onResponse(Call<KeepResponse> call, Response<KeepResponse> response) {
                KeepResponse result = response.body();
                keep_data = result.getKeepinfo();
                int resultCode = result.getCode();
                if (resultCode == StatusCode.RESULT_OK) {
                    setKeepData(result);
                } else if (resultCode == StatusCode.RESULT_SERVER_ERR) {

                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(KeepActivity.this);
                    builder.setView(dialogView);

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView icon = dialogView.findViewById(R.id.warning);

                    TextView txt = dialogView.findViewById(R.id.txtText);
                    txt.setText("에러가 발생했습니다." + "\n" + "다시 시도해주세요.");

                    Button ok_btn = dialogView.findViewById(R.id.okBtn);
                    ok_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //에러 발생 시 새로고침
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });

                    Button cancel_btn = dialogView.findViewById(R.id.cancelBtn);
                    cancel_btn.setVisibility(View.GONE);
                } else {
                    Toasty.normal(KeepActivity.this, "서버와의 통신이 불안정합니다").show();
                }

            }

            @Override
            public void onFailure(Call<KeepResponse> call, Throwable t) {
                Toasty.normal(KeepActivity.this, "서버와의 통신이 불안정합니다").show();
                Log.e("보관함 데이터 불러오기 에러", t.getMessage());
                t.printStackTrace(); // 에러 발생 원인 단계별로 출력
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 버튼 눌렀을 때
                Log.d("TAG", "뒤로,,,");
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}