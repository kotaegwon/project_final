package org.techtown.rc119.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
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


import org.techtown.rc119.Login_Register.JoinData;
import org.techtown.rc119.Login_Register.JoinResponse;
import org.techtown.rc119.Network.ApiService;
import org.techtown.rc119.Network.RetrofitClient;
import org.techtown.rc119.Network.RetrofitClient2;
import org.techtown.rc119.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_id, et_password_register, et_name, et_phone;
    private Button btn_register, btn_cancel;
    private CheckBox showPassword;

    private ApiService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        et_name=(EditText)findViewById(R.id.et_name);
        et_id=(EditText)findViewById(R.id.et_id_register);
        et_password_register=(EditText)findViewById(R.id.et_password_register);

        et_phone=(EditText)findViewById(R.id.et_phone);
        et_phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        service = RetrofitClient.getClient().create(ApiService.class);

        //비밀번호 표시 체크박스
        showPassword=(CheckBox) findViewById(R.id.show_passowrd);
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    et_password_register.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }else{
                    et_password_register.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        btn_register=(Button)findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptJoin();

                Intent intent=new Intent();
                intent.putExtra("id",et_id.getText());
                intent.putExtra("password",et_password_register.getText());
                setResult(RESULT_OK,intent);
            }
        });

        btn_cancel=(Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LogInActivity.class));
            }
        });
    }

    private void attemptJoin() {
        et_name.setError(null);
        et_id.setError(null);
        et_password_register.setError(null);
        et_phone.setError(null);

        String name = et_name.getText().toString();
        String id = et_id.getText().toString();
        String pwd = et_password_register.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // 패스워드의 유효성 검사
        if (pwd.isEmpty()) {
            et_id.setError("비밀번호를 입력해주세요.");
            focusView = et_id;
            cancel = true;

        } //else if (!isPasswordValid(password)) {
//            et_password_register.setError("6자 이상의 비밀번호를 입력해주세요.");
//            focusView = et_password_register;
//            cancel = true;
//        }

        // 아이디 유효성 검사
        if (id.isEmpty()) {
            et_id.setError("아이디를 입력해주세요.");
            focusView = et_id;
            cancel = true;
        }

        // 이름의 유효성 검사
        if (name.isEmpty()) {
            et_name.setError("이름을 입력해주세요.");
            focusView = et_name;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            startJoin();
            //startJoin(new JoinData(name, id, pwd, phone));
        }
    }

    private void startJoin(){
        String username=et_name.getText().toString();
        String userid=et_id.getText().toString();
        String userpassword=et_password_register.getText().toString();
        String userphone=et_phone.getText().toString();

        Call<JoinResponse> call = service.userJoin(new JoinData(username, userid, userpassword, userphone));
        call.enqueue(new Callback<JoinResponse>() {
            @Override
            public void onResponse(Call<JoinResponse> call, Response<JoinResponse> response) {
                JoinResponse result = response.body();
                int code = response.code();
                Toast.makeText(RegisterActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                if (response.isSuccessful()) {
                    Intent intent=new Intent();
                    intent.putExtra("id",et_id.getText());
                    intent.putExtra("password",et_password_register.getText());
                    setResult(RESULT_OK,intent);
                    finish();
                }
//                if(response.isSuccessful()){
//                    startActivity(new Intent(RegisterActivity.this, LogInActivity.class));
//                }
            }
            @Override
            public void onFailure(Call<JoinResponse> call, Throwable t) {
                Log.i("ko", t.getMessage());
                Toast.makeText(getApplicationContext(), "회원가입 에러 발생", Toast.LENGTH_SHORT).show();
            }
        });

    }

//    private void startJoin(JoinData data) {

//        service.userJoin(data).enqueue(new Callback<JoinResponse>() {
//            @Override
//            public void onResponse(Call<JoinResponse> call, Response<JoinResponse> response) {
//                JoinResponse result = response.body();
//                Toast.makeText(RegisterActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
//
//                if (result.getCode() == 200) {
//                    finish();
//                }
//            }
//            @Override
//            public void onFailure(Call<JoinResponse> call, Throwable t) {
//                Toast.makeText(RegisterActivity.this, "회원가입 에러 발생", Toast.LENGTH_SHORT).show();
//                Log.e("회원가입 에러 발생", t.getMessage());
//            }
//        });
//    }
    //이메일 입력 조건
//    private boolean isEmailValid(String email) {
//        return email.contains("@");
//    }

    //비밀번호 입력 조건
//    private boolean isPasswordValid(String password) {
//        return password.length() >= 6;
//    }
//
//    private void showProgress(boolean show) {
//        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//    }
}