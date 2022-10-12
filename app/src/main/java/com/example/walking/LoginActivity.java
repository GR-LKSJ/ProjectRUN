package com.example.walking;

import static com.kakao.auth.StringSet.error;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.sdk.common.model.AuthErrorCause;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

public class LoginActivity extends AppCompatActivity {

    private ISessionCallback mSessionCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        UserApiClient.getInstance().accessTokenInfo((tokenInfo, error) -> {
            if (error != null) {
                Toast.makeText(this, "토큰 정보 보기 실패. 로그인 기록 없음", Toast.LENGTH_SHORT).show();
            }
            else if (tokenInfo != null) {
                Toast.makeText(this, "토큰 정보 보기 성공. 로그인 기록 있음", Toast.LENGTH_SHORT).show();
                UserApiClient.getInstance().me((user, meError) -> {
                    if (meError != null) {

                    } else {
                        Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                        intent.putExtra("name", user.getKakaoAccount().getProfile().getNickname());
                        intent.putExtra("profileImg", user.getKakaoAccount().getProfile().getProfileImageUrl());
                        intent.putExtra("email", user.getKakaoAccount().getEmail());
                        startActivity(intent);
                    }
                    return null;
                });

            }
            return null;
        });




        ImageButton btn = (ImageButton) findViewById(R.id.loginBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this,(oAuthToken, error) -> {
                    if (error != null) {
                        Toast.makeText(LoginActivity.this, "로그인이 실패했습니다.", Toast.LENGTH_SHORT).show();
                    } else if (oAuthToken != null) {
                        UserApiClient.getInstance().me((user, meError) -> {
                            if (meError != null) {

                            } else {
                                Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                                intent.putExtra("name", user.getKakaoAccount().getProfile().getNickname());
                                intent.putExtra("profileImg", user.getKakaoAccount().getProfile().getProfileImageUrl());
                                intent.putExtra("email", user.getKakaoAccount().getEmail());
                                startActivity(intent);
                            }
                            return null;
                        });

                        Toast.makeText(LoginActivity.this, "로그인이 성공했습니다.", Toast.LENGTH_SHORT).show();
                    }
                    return null;
                });
            }
        });







    }
}
