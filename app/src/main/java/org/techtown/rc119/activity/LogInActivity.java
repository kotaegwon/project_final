package org.techtown.rc119.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;

import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;


import com.dd.CircularProgressButton;

import org.techtown.rc119.Login_Register.LoginData;
import org.techtown.rc119.Login_Register.LoginResponse;
import org.techtown.rc119.Network.ApiService;
import org.techtown.rc119.Network.RetrofitClient;
import org.techtown.rc119.Network.RetrofitClient2;
import org.techtown.rc119.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LogInActivity extends AppCompatActivity {

    private EditText et_id, et_password;
    private Button btn_login, btn_join;
    private ApiService service;
    private CheckBox show_passowrd_Login;
    private CircularProgressButton circularProgressButton;
    final static int REQUEST_CODE_START_INPUT=1;
    private long backtime=0;
    private boolean keyboardListenersAttached=false;
    private ViewGroup rootLayout;

    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener=new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff=rootLayout.getRootView().getHeight()-rootLayout.getHeight();
            int contentViewTop=getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

            if(heightDiff<=contentViewTop){
                rootLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        ((ScrollView)rootLayout).fullScroll(ScrollView.FOCUS_UP);
                    }
                });
            }
            else{
                rootLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        ((ScrollView)rootLayout).fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        if(!keyboardListenersAttached){
            rootLayout=(ViewGroup) findViewById(R.id.login_scroll);
            rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

            keyboardListenersAttached=true;
        }

        et_id = (EditText) findViewById(R.id.et_id);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_join=(Button)findViewById(R.id.btn_register);

        service = RetrofitClient2.getClient().create(ApiService.class);

        //비밀번호 보이기 체크박스
        show_passowrd_Login=(CheckBox)findViewById(R.id.show_passowrd_Login);
        show_passowrd_Login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }else{
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        //로그인 버튼 클릭 시 attempLogin 메서드 실행
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        //회원 가입 버튼 클릭시 이동
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goRegister=new Intent(LogInActivity.this, RegisterActivity.class);
                startActivityForResult(goRegister, REQUEST_CODE_START_INPUT);
            }
        });
    }

    //회원가입에 성공한 아이디 비밀번호 로그인 액티비티 에디트 텍스트에 출력
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_START_INPUT) {
            if (resultCode == RESULT_OK) {
                et_id.setText(data.getCharSequenceExtra("id"));
                et_password.setText(data.getCharSequenceExtra("password"));
            }
        }

    }

    private void attemptLogin() {
        et_id.setError(null);
        et_password.setError(null);

        String id = et_id.getText().toString();
        String pw = et_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // 패스워드의 유효성 검사
        if (pw.isEmpty()) {
            et_id.setError("비밀번호를 입력해주세요.");
            focusView = et_id;
            cancel = true;
          } //else if (!isPasswordValid(password)) {
//            et_password.setError("6자 이상의 비밀번호를 입력해주세요.");
//            focusView = et_password;
//            cancel = true;
//        }

        // 아이디 유효성 검사
        if (id.isEmpty()) {
            et_id.setError("아이디를 입력해주세요.");
            focusView = et_id;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            startLogin();
            //showProgress(true);
        }
    }
    private void startLogin(){
        {
            String id=et_id.getText().toString();
            String pw=et_password.getText().toString();

            Call<LoginResponse> call = service.userLogin(new LoginData(id, pw));
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    LoginResponse result = response.body();
                    Toast.makeText(LogInActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    if (result.getCode() == 200) {
                        startActivity(new Intent(LogInActivity.this, ControlActivity.class));
                    }
                 }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.i("ko", t.getMessage());
                    Toast.makeText(getApplicationContext(), "로그인 에러 발생", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("앱을 종료하시겠습니까?");
        builder.setPositiveButton("아니오",((dialog, which) -> {dialog.cancel();}));
        builder.setNegativeButton("예",((dialog, which) -> {finish();}));
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(keyboardListenersAttached){
            rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(keyboardLayoutListener);
        }
    }

    //이메일 로그인 조건
//    private boolean isEmailValid(String email) {
//        return email.contains("@");
//    }

    //비밀번호 조건
//    private boolean isPasswordValid(String password) {
//        return password.length() >= 6;
//    }

//    private void showProgress(boolean show) {
//        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//    }
}

