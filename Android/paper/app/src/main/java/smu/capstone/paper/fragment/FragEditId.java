package smu.capstone.paper.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smu.capstone.paper.LoginSharedPreference;
import smu.capstone.paper.R;
import smu.capstone.paper.activity.EditAccountActivity;
import smu.capstone.paper.activity.EditProfileActivity;
import smu.capstone.paper.data.IdCheckData;
import smu.capstone.paper.responseData.CodeResponse;
import smu.capstone.paper.server.RetrofitClient;
import smu.capstone.paper.server.ServiceApi;
import smu.capstone.paper.server.StatusCode;

public class FragEditId extends Fragment {
    public static int id_check = 0;
    public static int id_modify_check = 0;

    ServiceApi serviceApi = RetrofitClient.getClient().create(ServiceApi.class);
    String new_id, login_id;
    EditText account_newid;

    private int NO = 0;
    private int YES = 1;

    public FragEditId() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.frag_edit_id, container, false);

        Button account_id_check= (Button) rootView.findViewById(R.id.account_id_check);
        account_newid = (EditText)rootView.findViewById(R.id.account_newid);
        login_id = LoginSharedPreference.getLoginId(getContext());

        account_id_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_id = account_newid.getText().toString();
                new_id.trim();

                // 입력값이 공백일 경우 --> 서버 통신x
                if(new_id.getBytes().length <= 0){
                    id_check = NO;
                    id_modify_check = NO;
                    new AlertDialog.Builder(getContext())
                            .setTitle("경고")
                            .setMessage("변경할 아이디를 입력해주세요.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }

                // 현재 사용중인 id와 동일한 id 입력시 --> 서버 통신x
                else if (login_id.equals(new_id)){
                    id_check = NO;
                    id_modify_check = NO;
                    new AlertDialog.Builder(getContext())
                            .setMessage("현재 아이디와 동일한 아이디입니다.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }

                else{
                    IdCheckData data = new IdCheckData(new_id);
                    serviceApi.IdCheck(data).enqueue(new Callback<CodeResponse>() {
                        @Override
                        public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                            CodeResponse result = response.body();
                            int resultCode = result.getCode();

                            if(resultCode == StatusCode.RESULT_OK){
                                new AlertDialog.Builder(getContext())
                                        .setMessage("사용할 수 있는 아이디입니다.")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .show();
                                id_check = YES;
                                id_modify_check = YES;
                            }

                            else if (resultCode == StatusCode.RESULT_CLIENT_ERR){
                                id_check = NO;
                                id_modify_check = NO;
                                new AlertDialog.Builder(getContext())
                                        .setTitle("경고")
                                        .setMessage("이미 사용중인 아이디입니다."+"\n"+"다시 입력해 주세요.")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                account_newid.setText(null);
                                            }
                                        })
                                        .show();
                            }

                            else if (resultCode == StatusCode.RESULT_SERVER_ERR){
                                id_check = NO;
                                id_modify_check = NO;
                                new AlertDialog.Builder(getContext())
                                        .setTitle("경고")
                                        .setMessage("에러가 발생했습니다."+"\n"+"다시 시도해주세요.")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<CodeResponse> call, Throwable t) {
                            id_check = NO;
                            id_modify_check = NO;
                            Toast.makeText(getContext(), "서버와의 통신이 불안정합니다.", Toast.LENGTH_SHORT).show();
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
