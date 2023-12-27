package com.mobile.platform.SecuGuard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


// 상호작용 버튼이 많아질 수록 각각의 요소에 리스너를 달면 코드가 복잡해짐.
// OnClickListener를 상속받은 다음, onClick이 호출될 때, 요소의 ID로 구분하도록 하여 동작을 구분한다.
// 참고 : https://stackoverflow.com/questions/30082892/best-way-to-implement-view-onclicklistener-in-android

public class LoginScreen extends AppCompatActivity implements View.OnClickListener {

    Button signIn;      // 유저가 입력한 복호키를 검증하는 버튼
    TextView signUp;    // 새로운 암&복호 키를 생성하는 버튼
    EditText userInput; // 유저가 입력한 비밀번호 폼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        signIn = findViewById(R.id.sign_in);
        signUp = findViewById(R.id.sign_up);
        userInput = findViewById(R.id.password);
        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
    }

    // 클릭 요소의 ID 값을 기반으로 인텐드를 실행한다.
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in) {

            // 사용자가 복호키를 입력했다면,
            // 복호키 검증 한 뒤, 저장된 비밀번호와 일치하면 home 액티비티를 실행한다.

            // 암호화 관련 클래스를 불러옴
            Image_aes AES = new Image_aes();

            // 유저의 비밀번호 입력값을 불러온다.
            String userInputPass = userInput.getText().toString();

            // 만약 유저의 입력값이 저장된 비밀번호와의 일치 여부를 확인한다.
            if(userInputPass.equals(AES.getPassword(this))){
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }

            // 비밀번호가 다르면 에러 메세지를 띄운다.
            else{
                Toast myToast = Toast.makeText(this.getApplicationContext(),"비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT);
                myToast.show();
            }

        // 회원가입 버튼을 누르면 해당 회원가입 인텐트를 실행한다.
        } else if (view.getId() == R.id.sign_up) {
            // 회원가입 인텐트 실행
            Intent intent = new Intent(getApplicationContext(), SignupScreen.class);
            startActivity(intent);
        }
    }
}
