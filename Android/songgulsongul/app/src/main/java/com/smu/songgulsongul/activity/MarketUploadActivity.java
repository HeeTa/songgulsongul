package com.smu.songgulsongul.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.smu.songgulsongul.LoginSharedPreference;
import com.smu.songgulsongul.R;
import com.smu.songgulsongul.server.RetrofitClient;
import com.smu.songgulsongul.server.ServiceApi;
import com.smu.songgulsongul.server.StatusCode;

import java.io.File;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarketUploadActivity extends AppCompatActivity {
    // ServiceApi 객체 생성
    ServiceApi serviceApi = RetrofitClient.getClient().create(ServiceApi.class);

    String filePath, login_id;
    int user_id;
    ImageView market_upload_img;
    EditText market_upload_name, market_upload_price, market_upload_text;
    RequestBody requestFile, requestId, requestPrice, requestText, requestName;
    MultipartBody.Part imageBody;

    long first_time = 0;
    long second_time = 0;

    int BackColor = Color.parseColor("#BFB1D8");
    int FontColor = Color.parseColor("#000000");

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_upload);

        market_upload_name = findViewById(R.id.market_upload_name);
        market_upload_price = findViewById(R.id.market_upload_price);
        market_upload_text = findViewById(R.id.market_upload_text);

        market_upload_img = findViewById(R.id.market_upload_img);

        user_id = LoginSharedPreference.getUserId(this);
        login_id = LoginSharedPreference.getLoginId(this);

        // 툴바 세팅
        Toolbar toolbar = (Toolbar) findViewById(R.id.market_upload_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24); //뒤로가기 버튼 이미지 지정

        //이미지 세팅
        filePath = getIntent().getStringExtra("path");
        Glide.with(this).load(filePath).into(market_upload_img);
    }


    // 서버에 전송할 데이터 묶기
    public void makeUploadData() {

        // 업로드 이미지
        File file = new File(filePath);
        requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        imageBody = MultipartBody.Part.createFormData("img_market", file.getName(), requestFile);

        // 상품 이름
        String name = market_upload_name.getText().toString().trim();
        requestName = RequestBody.create(MediaType.parse("text/plain"), name);

        // 사용자 아이디
        requestId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(user_id));

        //판매 가격
        String price = market_upload_price.getText().toString().trim();
        requestPrice = RequestBody.create(MediaType.parse("text/plain"), price);

        // 상세 설명
        String text = market_upload_text.getText().toString().trim();
        requestText = RequestBody.create(MediaType.parse("text/plain"), text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.done_toolbar, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        second_time = System.currentTimeMillis();
        if (second_time - first_time < 2000) {
            super.onBackPressed();
            finish();
        } else {
            Toasty.custom(this, "한번 더 누르면 업로드를 종료합니다", null, BackColor, FontColor, 2000, false, true).show();
            first_time = System.currentTimeMillis();
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 버튼 눌렀을 때
                // 알림 팝업
                return true;

            case R.id.toolbar_done:
                String name = market_upload_name.getText().toString().trim();
                String price = market_upload_price.getText().toString().trim();

                if (name.getBytes().length <= 0) {
                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MarketUploadActivity.this);
                    builder.setView(dialogView);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView icon = dialogView.findViewById(R.id.warning);

                    TextView txt = dialogView.findViewById(R.id.txtText);
                    txt.setText("제품명을 입력해주세요.");
                    Button ok_btn = dialogView.findViewById(R.id.okBtn);
                    ok_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    Button cancel_btn = dialogView.findViewById(R.id.cancelBtn);
                    cancel_btn.setVisibility(View.GONE);

                } else if (price.getBytes().length <= 0) {
                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MarketUploadActivity.this);
                    builder.setView(dialogView);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView icon = dialogView.findViewById(R.id.warning);

                    TextView txt = dialogView.findViewById(R.id.txtText);
                    txt.setText("판매 가격을 입력해주세요.");
                    Button ok_btn = dialogView.findViewById(R.id.okBtn);
                    ok_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    Button cancel_btn = dialogView.findViewById(R.id.cancelBtn);
                    cancel_btn.setVisibility(View.GONE);
                } else {
                    makeUploadData();
                    serviceApi.MarketUpload(requestName, requestText, requestPrice, requestId, imageBody).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject result = response.body();
                            int resultCode = result.get("code").getAsInt();
                            int sticker_id = result.get("market_id").getAsInt();

                            if (resultCode == StatusCode.RESULT_OK) {
                                Toast toast = Toasty.custom(MarketUploadActivity.this, "업로드 완료", null, BackColor, FontColor, 2000, false, true);
                                toast.show();
                                Intent intent = new Intent(MarketUploadActivity.this, StickerDetailActivity.class);
                                intent.putExtra("sticker_id", sticker_id);
                                startActivity(intent);
                                finish();
                            } else if (resultCode == StatusCode.RESULT_SERVER_ERR) {
                                View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                                AlertDialog.Builder builder = new AlertDialog.Builder(MarketUploadActivity.this);
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
                                        alertDialog.dismiss();
                                    }
                                });
                                Button cancel_btn = dialogView.findViewById(R.id.cancelBtn);
                                cancel_btn.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Toasty.normal(MarketUploadActivity.this, "서버와의 통신이 불안정합니다").show();
                            Log.e("마켓 업로드 에러", t.getMessage());
                            t.printStackTrace(); // 에러 발생 원인 단계별로 출력
                        }
                    });
                }

                return true;

        }
        return super.onOptionsItemSelected(item);
    }


}
