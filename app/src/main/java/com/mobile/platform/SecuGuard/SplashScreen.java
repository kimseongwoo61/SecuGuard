package com.mobile.platform.SecuGuard;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


// 스플레시 화면을 구현한다. -> 앱 로고 띄움
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 타이틀 바 숨김
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 전체 화면으로 동작하도록 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 스플레시 레이아웃을 설정한다.
        setContentView(R.layout.activity_splash_screen);


        // 스플레시 화면을 3초간 띄운 후, 로그인 화면으로 이동한다.
        // 참고 : https://aries574.tistory.com/243
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, LoginScreen.class);
                startActivity(intent);
                finish();
            }
        }, 3000);

    }
}
