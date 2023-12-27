package com.mobile.platform.SecuGuard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class SignupScreen extends AppCompatActivity implements View.OnClickListener {

    EditText UserNewPassword;
    Button SignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);

        // 각각의 요소 리소스 확인 및 리스너 세팅을 진행한다.
        UserNewPassword = findViewById(R.id.new_input_password);
        SignUp = findViewById(R.id.sign_up);
        SignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.sign_up){

            // 우선 정상적으로 비밀번호를 입력했는지 확인한다.
            if(UserNewPassword.getText().toString().isEmpty())
                Toast.makeText(getApplicationContext(),"생성할 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();

            // 비밀번호 생성이나 변경하기 전,
            // 이미 사전에 생성한 암호화 데이터의 초기화 경고를 출력한다.
            // 사용자가 동의하는 경우 내부 경로 삭제 및 비밀번호 갱신을 진행한다.
            showAlert();
        }
    }

    public void showAlert(){
        AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(SignupScreen.this);
        // alert의 title과 Messege 세팅
        myAlertBuilder.setTitle("Warning!!!");
        myAlertBuilder.setMessage("기기에 존재하는 암호화 파일은 삭제됩니다!");

        // 버튼 추가 (Ok 버튼과 Cancle 버튼 )
        myAlertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // OK 버튼을 눌렸을 경우
                Toast.makeText(getApplicationContext(), "새로운 패스워드 갱신진행", Toast.LENGTH_SHORT).show();

                // 내부 저장소의 SecuGuard, Decrypt 폴더를
                // 완전히 삭제한다.(재귀적인 삭제 진행 -> 내부 파일도 완전 삭제)
                deleteDirectory(new File(getFilesDir(), "SecuGuard"));
                deleteDirectory(new File(getFilesDir(), "Decrypt"));

                // 사용자의 입력값을 불러온다.
                String NewPassword = UserNewPassword.getText().toString();

                // 비밀번호를 갱신한다.
                Image_aes SetNewPass = new Image_aes();
                SetNewPass.savePassword(getApplicationContext(), NewPassword);

                // 작업 완료 여부를 화면에 표시한다.
                Toast.makeText(getApplicationContext(),"비밀번호를 갱신하였습니다.", Toast.LENGTH_SHORT).show();
                finish();

            }
        });

        myAlertBuilder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancle 버튼을 눌렸을 경우
                Toast.makeText(getApplicationContext(),"작업을 중지합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // Alert를 생성해주고 보여주는 메소드(show를 선언해야 Alert가 생성됨)
        myAlertBuilder.show();
    }

    private void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                File childFile = new File(dir, child);
                deleteDirectory(childFile);
            }
        }
        dir.delete();
    }


}
