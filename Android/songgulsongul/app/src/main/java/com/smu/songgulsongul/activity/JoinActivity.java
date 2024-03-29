package com.smu.songgulsongul.activity;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.JsonObject;

import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.smu.songgulsongul.R;
import com.smu.songgulsongul.data.CodeResponse;
import com.smu.songgulsongul.data.user.EmailData;
import com.smu.songgulsongul.data.user.IdCheckData;
import com.smu.songgulsongul.data.user.JoinData;
import com.smu.songgulsongul.server.RetrofitClient;
import com.smu.songgulsongul.server.ServiceApi;

public  class JoinActivity extends AppCompatActivity {
    // ServiceApi 객체 생성
    ServiceApi serviceApi = RetrofitClient.getClient().create(ServiceApi.class);

    TextView join_timer, join_id_text, join_pw_correct_text;
    CountDownTimer countDownTimer;
    Button join_send_btn, join_check_key, join_check_id, join_btn;
    RadioGroup join_sns_radio;
    EditText join_sns_text, join_, join_email_text,  join_pw_text, join_pw_check_text;

    String auth;
    final int MILLISINFUTURE = 180 * 1000;
    final int COUNT_DOWN_INTERVAL = 1000;
    final int RESULT_OK = 200;
    final int RESULT_CLIENT_ERR= 204;
    final int RESULT_SERVER_ERR = 500;
    int id_check_flag = 0; // 아이디 중복 확인 여부 체크
    int email_check_flag = 0; // 이메일 중복 확인 여부 체크
    int password_check_flag = 0; // 비밀번호 체크
    int sns_check_flag = 0; // sns계정 유 무 체크

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        join_send_btn = (Button)findViewById(R.id.join_send_btn);
        join_check_key = (Button)findViewById(R.id.join_check_key);
        join_timer = findViewById(R.id.join_timer);
        join_sns_radio = (RadioGroup)findViewById(R.id.join_sns_radio);
        join_sns_text = (EditText)findViewById(R.id.join_sns_text);
        join_check_id = (Button)findViewById(R.id.join_check_id);
        join_id_text = (TextView)findViewById(R.id.join_id_text);
        join_ = (EditText) findViewById(R.id.join_);
        join_email_text = (EditText)findViewById(R.id.join_email_text);
        join_pw_text = (EditText)findViewById(R.id.join_pw_text);
        join_pw_check_text = (EditText)findViewById(R.id.join_pw_check_text);
        join_pw_correct_text = (TextView)findViewById(R.id.join_pw_correct_text);
        join_btn = (Button)findViewById(R.id.join_btn);

        join_check_key.setEnabled(false);
        join_timer.setVisibility(View.INVISIBLE); // 인증 제한시간 숨기기
        join_pw_correct_text.setVisibility(View.INVISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.join_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        //  actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setTitle("Join");
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24); //뒤로가기 버튼 이미지 지정

        // 아이디 중복 확인 버튼
        join_check_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login_id = join_id_text.getText().toString();

                // 입력한 아이디가 공백값일 경우 --> 서버 통신 x
                login_id = login_id.trim();
                if(login_id.getBytes().length <= 0){
                    id_check_flag = 0;

                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setView(dialogView);

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView icon=dialogView.findViewById(R.id.warning);

                    TextView txt=dialogView.findViewById(R.id.txtText);
                    txt.setText("아이디를 입력해주세요.");

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

                else {
                    // login_id로 server와 통신
                    IdCheckData data = new IdCheckData(login_id);
                    serviceApi.IdCheck(data).enqueue(new Callback<CodeResponse>() {

                        // 통신 성공시 호출
                        @Override
                        public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                            CodeResponse result = response.body();
                                int resultCode = result.getCode();

                                if (resultCode == RESULT_OK) {
                                    id_check_flag = 1;

                                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                    builder.setView(dialogView);

                                    final AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    ImageView icon=dialogView.findViewById(R.id.warning);
                                    icon.setVisibility(View.GONE);

                                    TextView txt=dialogView.findViewById(R.id.txtText);
                                    txt.setText("사용할 수 있는 아이디입니다.");

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

                                else if (resultCode == RESULT_CLIENT_ERR){
                                    id_check_flag = 0;

                                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                    builder.setView(dialogView);

                                    final AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    ImageView icon=dialogView.findViewById(R.id.warning);

                                    TextView txt=dialogView.findViewById(R.id.txtText);
                                    txt.setText("이미 사용중인 아이디입니다."+"\n"+"다시 입력해주세요.");

                                    Button ok_btn = dialogView.findViewById(R.id.okBtn);
                                    ok_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                        }
                                    });

                                    Button cancel_btn = dialogView.findViewById(R.id.cancelBtn);
                                    cancel_btn.setVisibility(View.GONE);

                                    join_id_text.setText(null);
                                }

                                else if (resultCode == RESULT_SERVER_ERR){
                                    id_check_flag = 0;

                                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                    builder.setView(dialogView);

                                    final AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    ImageView icon=dialogView.findViewById(R.id.warning);

                                    TextView txt=dialogView.findViewById(R.id.txtText);
                                    txt.setText("에러가 발생했습니다."+"\n"+"다시 시도해주세요.");

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

                                else{
                                    id_check_flag = 0;
                                    Toasty.normal(JoinActivity.this, "서버와의 통신이 불안정합니다").show();
                                }
                        }

                        // 통신 실패시 호출
                        @Override
                        public void onFailure(Call<CodeResponse> call, Throwable t) {
                            id_check_flag = 0;
                            Toasty.normal(JoinActivity.this, "서버와의 통신이 불안정합니다").show();
                            Log.e("아이디 중복확인 에러", t.getMessage());
                            t.printStackTrace(); // 에러 발생 원인 단계별로 출력
                        }
                    });
                }
            }
        });

        // 비밀번호 일치 확인
        join_pw_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(join_pw_check_text.getText().toString().trim().length() > 0) {
                    if (s.toString().equals(join_pw_check_text.getText().toString())) {
                        join_pw_correct_text.setText("비밀번호가 일치합니다.");
                        join_pw_correct_text.setVisibility(View.VISIBLE);
                        password_check_flag = 1;
                    } else {
                        join_pw_correct_text.setText("비밀번호가 일치하지 않습니다.");
                        join_pw_correct_text.setVisibility(View.VISIBLE);
                        password_check_flag = 0;
                    }
                }
            }
        });

        join_pw_check_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                join_pw_correct_text.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(join_pw_text.getText().toString())) {
                    join_pw_correct_text.setText("비밀번호가 일치합니다.");
                    join_pw_correct_text.setVisibility(View.VISIBLE);
                    password_check_flag = 1;
                }
                else {
                    join_pw_correct_text.setText("비밀번호가 일치하지 않습니다.");
                    join_pw_correct_text.setVisibility(View.VISIBLE);
                    password_check_flag = 0;
                }
            }
        });

        // 이메일 인증 버튼 눌렀을 시 발생
        join_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pattern email_pattern = Patterns.EMAIL_ADDRESS;
                String email = join_email_text.getText().toString();
                email = email.trim();

                // 입력한 이메일이 공백값일 경우 --> 서버 통신 x
                if(email.getBytes().length <= 0){
                    email_check_flag = 0;

                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setView(dialogView);

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView icon=dialogView.findViewById(R.id.warning);

                    TextView txt=dialogView.findViewById(R.id.txtText);
                    txt.setText("이메일을 입력해주세요.");

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

                // 입력한 이메일이 형식에서 벗어날 경우 --> 서버 통신x
                else if(!email_pattern.matcher(email).matches()){
                    email_check_flag = 0;

                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setView(dialogView);

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView icon=dialogView.findViewById(R.id.warning);

                    TextView txt=dialogView.findViewById(R.id.txtText);
                    txt.setText("올바른 이메일 형식이 아닙니다.");

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

                else {
                    // 입력한 email로 server통신
                    EmailData data = new EmailData(email);
                    serviceApi.EmailAuth(data).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                JsonObject result = response.body();
                                if(result!=null) {
                                    auth = result.get("authNumber").getAsString();
                                }
                                int resultCode = result.get("code").getAsInt();

                                if(resultCode == RESULT_OK) {
                                    join_check_key.setEnabled(true); // 인증번호 확인 버튼 활성화
                                    join_timer.setVisibility(View.VISIBLE); // 인증 제한시간 표시
                                    countDownTimer(); // 타이머 작동
                                }
                                else if(resultCode == RESULT_SERVER_ERR){
                                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                    builder.setView(dialogView);

                                    final AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    ImageView icon=dialogView.findViewById(R.id.warning);

                                    TextView txt=dialogView.findViewById(R.id.txtText);
                                    txt.setText("에러가 발생했습니다."+"\n"+"다시 시도해주세요.");

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
                                else{
                                    Toasty.normal(JoinActivity.this, "서버와의 통신이 불안정합니다").show();
                                }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            email_check_flag = 0;
                            Toasty.normal(JoinActivity.this, "서버와의 통신이 불안정합니다").show();
                            Log.e("이메일 인증 에러", t.getMessage());
                            t.printStackTrace(); // 에러 발생 원인 단계별로 출력
                        }
                    });
                }
            }
        });

        // 인증번호 입력 -> 확인 버튼 눌렀을 시 발생
        join_check_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputAuth = join_.getText().toString();
                inputAuth = inputAuth.trim();

                // 인증번호 입력이 공백값일 경우
                if(inputAuth.getBytes().length <= 0){
                    email_check_flag = 0;

                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setView(dialogView);

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView icon=dialogView.findViewById(R.id.warning);

                    TextView txt=dialogView.findViewById(R.id.txtText);
                    txt.setText("인증번호를 입력해주세요.");

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
                else {
                    countDownTimer.cancel(); // 타이머 종료

                    if(inputAuth.equals(auth)){
                        email_check_flag = 1;

                        View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                        builder.setView(dialogView);

                        final AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        ImageView icon=dialogView.findViewById(R.id.warning);
                        icon.setVisibility(View.GONE);

                        TextView txt=dialogView.findViewById(R.id.txtText);
                        txt.setText("인증되었습니다.");

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
                    else{
                        email_check_flag = 0;

                        View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                        builder.setView(dialogView);

                        final AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        ImageView icon=dialogView.findViewById(R.id.warning);
                        icon.setVisibility(View.GONE);

                        TextView txt=dialogView.findViewById(R.id.txtText);
                        txt.setText("인증실패!"+"\n"+"다시 시도해주세요.");

                        Button ok_btn = dialogView.findViewById(R.id.okBtn);
                        ok_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });

                        Button cancel_btn = dialogView.findViewById(R.id.cancelBtn);
                        cancel_btn.setVisibility(View.GONE);

                        join_email_text.setText(null);
                        join_.setText(null);
                        join_check_key.setEnabled(false);
                        join_timer.setVisibility(View.INVISIBLE);
                        auth = null;
                    }
                }
            }
        });

        // sns 계정 입력
        join_sns_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // sns 무 선택 -> 입력창 비활성화
                if(checkedId == R.id.join_sns_n) {
                    join_sns_text.setClickable(false);
                    join_sns_text.setFocusable(false);
                    join_sns_text.setText(null);
                    sns_check_flag = 0;
                }
                // sns 유 선택 -> 입력창 활성화
                else{
                    join_sns_text.setFocusableInTouchMode(true);
                    join_sns_text.setFocusable(true);
                    sns_check_flag = 1;
                }
            }
        });

        // 회원가입 버튼
        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = join_email_text.getText().toString();
                String password = join_pw_text.getText().toString();
                String login_id = join_id_text.getText().toString();
                String sns_url = join_sns_text.getText().toString();
                join_sns_text.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

                // 입력값 공백 제거
                email = email.trim();
                password = password.trim();
                login_id = login_id.trim();
                sns_url = sns_url.trim();

                int email_num = email.getBytes().length;
                int password_num = password.getBytes().length;
                int login_id_num = login_id.getBytes().length;
                int sns_url_num = sns_url.getBytes().length;

                // sns계정 유 선택 --> 값 입력x 경우 --> 서버 통신x
                if(sns_check_flag == 1 && sns_url_num <= 0 ){

                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setView(dialogView);

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView icon=dialogView.findViewById(R.id.warning);

                    TextView txt=dialogView.findViewById(R.id.txtText);
                    txt.setText("sns계정 입력을 완료해주세요.");

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

                // 미입력한 값이 있을 경우 --> 서버 통신x
                if(email_num <= 0 || password_num <= 0 || login_id_num <= 0){

                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setView(dialogView);

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView icon=dialogView.findViewById(R.id.warning);

                    TextView txt=dialogView.findViewById(R.id.txtText);
                    txt.setText("항목을 모두 입력해주세요.");

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

                // 아이디 중복확인 완료x --> 서버 통신x
                else if(id_check_flag == 0){

                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setView(dialogView);

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView icon=dialogView.findViewById(R.id.warning);

                    TextView txt=dialogView.findViewById(R.id.txtText);
                    txt.setText("아이디 중복확인을 완료해주세요.");

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

                // 이메일 인증 완료x --> 서버 통신x
                else if(email_check_flag == 0){

                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setView(dialogView);

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView icon=dialogView.findViewById(R.id.warning);

                    TextView txt=dialogView.findViewById(R.id.txtText);
                    txt.setText("이메일 인증을 완료해주세요.");

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

                // 비밀번호 일치 여부 확인x --> 서버 통신x
                else if(password_check_flag == 0){

                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    builder.setView(dialogView);

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView icon=dialogView.findViewById(R.id.warning);

                    TextView txt=dialogView.findViewById(R.id.txtText);
                    txt.setText("비밀번호 확인을 완료해주세요.");

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

                else {
                    // joindata로 server와 통신
                    JoinData data = new JoinData(email, password, login_id, sns_url);
                    serviceApi.Join(data).enqueue(new Callback<CodeResponse>() {
                        @Override
                        public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                            try {
                                CodeResponse result = response.body();
                                int resultCode = result.getCode();
                                if (resultCode == RESULT_OK) {

                                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                    builder.setView(dialogView);

                                    final AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    ImageView icon=dialogView.findViewById(R.id.warning);
                                    icon.setVisibility(View.GONE);

                                    TextView txt=dialogView.findViewById(R.id.txtText);
                                    txt.setText("회원가입 완료!");

                                    Button ok_btn = dialogView.findViewById(R.id.okBtn);
                                    ok_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //회원가입 성공시 로그인 화면으로 전환
                                            Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                                        }
                                    });

                                    Button cancel_btn = dialogView.findViewById(R.id.cancelBtn);
                                    cancel_btn.setVisibility(View.GONE);
                                } else if (resultCode == RESULT_CLIENT_ERR) {

                                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                    builder.setView(dialogView);

                                    final AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    ImageView icon=dialogView.findViewById(R.id.warning);
                                    icon.setVisibility(View.GONE);

                                    TextView txt=dialogView.findViewById(R.id.txtText);
                                    txt.setText("회원가입 실패"+"\n"+"다시 시도해주세요.");

                                    Button ok_btn = dialogView.findViewById(R.id.okBtn);
                                    ok_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //회원가입 실패시 화면 새로고침 --> 처음부터 재시도
                                            Intent intent = getIntent();
                                            finish();
                                            startActivity(intent);
                                        }
                                    });

                                    Button cancel_btn = dialogView.findViewById(R.id.cancelBtn);
                                    cancel_btn.setVisibility(View.GONE);
                                } else {
                                    Toasty.normal(JoinActivity.this, "서버와의 통신이 불안정합니다").show();
                                }
                            } catch (NullPointerException e){

                                View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                builder.setView(dialogView);

                                final AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                ImageView icon=dialogView.findViewById(R.id.warning);

                                TextView txt=dialogView.findViewById(R.id.txtText);
                                txt.setText("에러가 발생했습니다."+"\n"+"다시 시도해주세요.");

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
                        public void onFailure(Call<CodeResponse> call, Throwable t) {
                            Toasty.normal(JoinActivity.this, "서버와의 통신이 불안정합니다").show();
                            Log.e("회원가입 에러", t.getMessage());
                            t.printStackTrace(); // 에러 발생 원인 단계별로 출력
                        }
                    });
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 뒤로가기 버튼 눌렀을 때
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}