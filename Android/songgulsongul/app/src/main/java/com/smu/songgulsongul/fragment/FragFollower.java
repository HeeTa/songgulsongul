package com.smu.songgulsongul.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.smu.songgulsongul.LoginSharedPreference;
import com.smu.songgulsongul.R;
import com.smu.songgulsongul.recycler_adapter.FollowAdapter;
import com.smu.songgulsongul.data.user.FollowListData;
import com.smu.songgulsongul.server.RetrofitClient;
import com.smu.songgulsongul.server.ServiceApi;
import com.smu.songgulsongul.server.StatusCode;

public class FragFollower extends Fragment {
    // ServiceApi 객체 생성
    ServiceApi serviceApi = RetrofitClient.getClient().create(ServiceApi.class);
    StatusCode statusCode;


    RecyclerView rv;
    FollowAdapter adapter;
    JsonObject obj = new JsonObject();
    int status;
    int sflag = 0;

    final int MY = 1;
    final int OTHER = 0;

    String login_id;
    String user_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_follower, container, false);

        rv = rootView.findViewById(R.id.follow_follower_rv);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);

        login_id = LoginSharedPreference.getLoginId(getContext());


        Bundle bundle = getArguments();
        if (!login_id.equals(bundle.getString("userId"))) {
            user_id = bundle.getString("userId");
            status = OTHER;
        } else {
            status = MY;
        }

        getFollowerData();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    // server에서 data전달
    public void getFollowerData() {
        FollowListData data = new FollowListData(login_id);
        data.addStatus(status);
        if (status == OTHER) {
            data.addUserId(user_id);
        }
        serviceApi.FollowerList(data).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject result = response.body();
                int resultCode = result.get("code").getAsInt();

                if (resultCode == StatusCode.RESULT_OK) {
                    JsonArray following_list = result.getAsJsonArray("followingInfo");
                    JsonArray follower_list = result.getAsJsonArray("followerInfo");

                    // 팔로워 리스트에 있는 사용자를 팔로우 했는지 체크
                    for (int i = 0; i < follower_list.size(); i++) {
                        if (following_list.size() == 0) {
                            follower_list.get(i).getAsJsonObject().addProperty("flag", false);
                            continue;
                        }
                        for (int j = 0; j < following_list.size(); j++) {
                            int check = 0;
                            String following_id = following_list.get(j).getAsJsonObject().get("userId").getAsString();
                            String follower_id = follower_list.get(i).getAsJsonObject().get("userId").getAsString();

                            // Following
                            if (following_id.equals(follower_id)) {
                                follower_list.get(i).getAsJsonObject().addProperty("flag", true);
                                check = 1;
                                break;
                            }

                            // Unfollowing
                            if (check == 0)
                                follower_list.get(i).getAsJsonObject().addProperty("flag", false);
                        }
                    }
                    obj.add("data", follower_list);
                    adapter = new FollowAdapter(getContext(), obj, sflag);
                    rv.setAdapter(adapter);
                } else if (resultCode == StatusCode.RESULT_CLIENT_ERR) {


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

                } else {
                    Toasty.normal(getContext(), "서버와의 통신이 불안정합니다.").show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toasty.normal(getContext(), "서버와의 통신이 불안정합니다.").show();
                Log.e("팔로워 리스트 불러오기 에러", t.getMessage());
                t.printStackTrace(); // 에러 발생 원인 단계별로 출력
            }
        });
    }
}