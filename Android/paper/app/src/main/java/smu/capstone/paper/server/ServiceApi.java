package smu.capstone.paper.server;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import smu.capstone.paper.data.CodeResponse;
import smu.capstone.paper.data.EmailAuthData;
import smu.capstone.paper.data.IdCheckData;
import smu.capstone.paper.data.JoinData;
import smu.capstone.paper.data.LoginData;

public interface ServiceApi {
    // 아이디 중복체크
    @POST("/api/dup-idcheck")
    Call<CodeResponse> IdCheck(@Body IdCheckData data);

    // 인증 이메일 보내기
    @POST("/api/email-auth")
    Call<JsonObject> EmailAuth(@Body EmailAuthData data);

    // 회원가입
    @POST("/join")
    Call<CodeResponse> Join(@Body JoinData data);

    // 로그인
    @POST("/login")
    Call<CodeResponse> Login(@Body LoginData data);
}