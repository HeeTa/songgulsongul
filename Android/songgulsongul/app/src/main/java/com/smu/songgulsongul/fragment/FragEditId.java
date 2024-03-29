package com.smu.songgulsongul.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.smu.songgulsongul.LoginSharedPreference;
import com.smu.songgulsongul.R;
import com.smu.songgulsongul.data.user.IdCheckData;
import com.smu.songgulsongul.data.CodeResponse;
import com.smu.songgulsongul.server.RetrofitClient;
import com.smu.songgulsongul.server.ServiceApi;
import com.smu.songgulsongul.server.StatusCode;

public class FragEditId extends Fragment {
    public static int id_check = 0;
    public static int id_modify_check = 0;

    ServiceApi serviceApi = RetrofitClient.getClient().create(ServiceApi.class);
    String new_id, login_id;
    EditText account_newid;

    private final int NO = 0;
    private final int YES = 1;

    public FragEditId() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.frag_edit_id, container, false);

        Button account_id_check = (Button) rootView.findViewById(R.id.account_id_check);
        account_newid = (EditText) rootView.findViewById(R.id.account_newid);
        login_id = LoginSharedPreference.getLoginId(getContext());

        account_newid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                id_check = NO;
                id_modify_check = NO;
            }
        });

        account_id_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_id = account_newid.getText().toString();
                new_id.trim();

                // 입력값이 공백일 경우 --> 서버 통신x
                if (new_id.getBytes().length <= 0) {
                    id_check = NO;
                    id_modify_check = NO;


                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                    Context context = getActivity();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(dialogView);

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView icon = dialogView.findViewById(R.id.warning);

                    TextView txt = dialogView.findViewById(R.id.txtText);
                    txt.setText("변경할 아이디를 입력해주세요.");

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

                // 현재 사용중인 id와 동일한 id 입력시 --> 서버 통신x
                else if (login_id.equals(new_id)) {
                    id_check = NO;
                    id_modify_check = NO;

                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                    Context context = getActivity();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(dialogView);

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView icon = dialogView.findViewById(R.id.warning);
                    icon.setVisibility(View.GONE);

                    TextView txt = dialogView.findViewById(R.id.txtText);
                    txt.setText("현재 아이디와 동일한 아이디입니다.");

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
                    IdCheckData data = new IdCheckData(new_id);
                    serviceApi.IdCheck(data).enqueue(new Callback<CodeResponse>() {
                        @Override
                        public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                            CodeResponse result = response.body();
                            int resultCode = result.getCode();

                            if (resultCode == StatusCode.RESULT_OK) {

                                View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                                Context context = getActivity();
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setView(dialogView);

                                final AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                ImageView icon = dialogView.findViewById(R.id.warning);
                                icon.setVisibility(View.GONE);

                                TextView txt = dialogView.findViewById(R.id.txtText);
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

                                id_check = YES;
                                id_modify_check = YES;
                            } else if (resultCode == StatusCode.RESULT_CLIENT_ERR) {
                                id_check = NO;
                                id_modify_check = NO;

                                View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                                Context context = getActivity();
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setView(dialogView);

                                final AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                ImageView icon = dialogView.findViewById(R.id.warning);

                                TextView txt = dialogView.findViewById(R.id.txtText);
                                txt.setText("이미 사용중인 아이디입니다." + "\n" + "다시 입력해주세요.");

                                Button ok_btn = dialogView.findViewById(R.id.okBtn);
                                ok_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        account_newid.setText(null);
                                        alertDialog.dismiss();
                                    }
                                });

                                Button cancel_btn = dialogView.findViewById(R.id.cancelBtn);
                                cancel_btn.setVisibility(View.GONE);

                            } else if (resultCode == StatusCode.RESULT_SERVER_ERR) {
                                id_check = NO;
                                id_modify_check = NO;

                                View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                                Context context = getActivity();
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                        public void onFailure(Call<CodeResponse> call, Throwable t) {
                            id_check = NO;
                            id_modify_check = NO;
                            Toasty.normal(getContext(), "서버와의 통신이 불안정합니다.").show();
                            Log.e("아이디 중복확인 에러", t.getMessage());
                            t.printStackTrace(); // 에러 발생 원인 단계별로 출력
                        }
                    });
                }
            }
        });

        return rootView;

    }
}
