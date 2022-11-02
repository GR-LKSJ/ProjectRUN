package com.example.walking;

import static com.kakao.auth.StringSet.error;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
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

    // Google Sign In API와 호출할 구글 로그인 클라이언트
    GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 123;
    private static final String TAG = "MainActivity";
    SignInButton signBt;
    Button logoutBt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //카카오 로그인---------------------------------------------------------------------------------------
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

        //카카오 로그인 끝-----------------------------------------------------------------------------------------------------

/*
        //구글 로그인
        signBt = findViewById(R.id.sign_in_button);
        signBt.setOnClickListener((View.OnClickListener) this);
        logoutBt = findViewById(R.id.logoutBt);
        logoutBt.setOnClickListener((View.OnClickListener) this);


        // 앱에 필요한 사용자 데이터를 요청하도록 로그인 옵션을 설정한다.
        // DEFAULT_SIGN_IN parameter는 유저의 ID와 기본적인 프로필 정보를 요청하는데 사용된다.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // email addresses도 요청함
                .build();

        // 위에서 만든 GoogleSignInOptions을 사용해 GoogleSignInClient 객체를 만듬
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

        // 기존에 로그인 했던 계정을 확인한다.
        GoogleSignInAccount gsa = GoogleSignIn.getLastSignedInAccount(LoginActivity.this);

        // 로그인 되있는 경우 (토큰으로 로그인 처리)
        if (gsa != null && gsa.getId() != null) {

        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                Log.v(TAG, "handleSignInResult:personName "+personName);
                Log.v(TAG, "handleSignInResult:personGivenName "+personGivenName);
                Log.v(TAG, "handleSignInResult:personEmail "+personEmail);
                Log.v(TAG, "handleSignInResult:personId "+personId);
                Log.v(TAG, "handleSignInResult:personFamilyName "+personFamilyName);
                Log.v(TAG, "handleSignInResult:personPhoto "+personPhoto);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                Log.v(TAG, "handleSignInResult:personName ");
                break;
            case R.id.logoutBt:
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(this, task -> {
                            Log.d(TAG, "onClick:logout success ");
                            mGoogleSignInClient.revokeAccess()
                                    .addOnCompleteListener(this, task1 -> Log.d(TAG, "onClick:revokeAccess success "));

                        });
                break;

        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        registerForActivityResult(signInIntent);
        //startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
*/
    }




}
