package com.sourcey.materiallogindemo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sourcey.materiallogindemo.model.User;
import com.sourcey.materiallogindemo.service.ServiceGenerator;
import com.sourcey.materiallogindemo.service.UserClient;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;

    // call for API
    UserClient userClient = ServiceGenerator.createService(UserClient.class);
    private static String token;
    private boolean authticated=false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        //progressDialog.setMessage("Authenticating...");
        progressDialog.setMessage("正在身份验证...");
        progressDialog.show();

        final String username = _nameText.getText().toString();
        final String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.


        final Call<User> call = userClient.login(username,password);

        // 在主线程里，必须使用`异步调用方式`
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    // if you want to get <User> object, you just call `response.body()` to get it.
                    token =response.body().getToken();
                    if(token != null) {
                        //Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();
                        // 获取到token,表示登录成功
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        // On complete call either onLoginSuccess or onLoginFailed
                                        onLoginSuccess();
                                        //onLoginFailed();
                                        progressDialog.dismiss();
                                    }
                                }, 3000);
                    }
                    else{
                        // 获取到token,`token`为空，表示登录失败；这种可能性极小。
                        //Toast.makeText(LoginActivity.this, "token is empty!", Toast.LENGTH_SHORT).show();
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        // On complete call either onLoginSuccess or onLoginFailed
                                        //onLoginSuccess();
                                        onLoginFailed();
                                        progressDialog.dismiss();
                                    }
                                }, 3000);
                    }
                }else {
                    // 登录账号不对
                    //Toast.makeText(LoginActivity.this, "login not correct :(", Toast.LENGTH_SHORT).show();
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    // On complete call either onLoginSuccess or onLoginFailed
                                    Toast.makeText(getBaseContext(), "用户名或密码不正确", Toast.LENGTH_LONG).show();
                                    //onLoginSuccess();
                                    onLoginFailed();
                                    progressDialog.dismiss();
                                }
                            }, 3000);

                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "error login-- :(", Toast.LENGTH_SHORT).show();
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                //onLoginSuccess();
                                onLoginFailed();
                                progressDialog.dismiss();
                            }
                        }, 3000);

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        //Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            //_nameText.setError("at least 3 characters");
            _nameText.setError("用户名，最少三个字符");
            valid = false;
        } else {
            _nameText.setError(null);
        }


        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            //_passwordText.setError("between 4 and 10 alphanumeric characters");
            _passwordText.setError("4-10个数字或字母");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


}
