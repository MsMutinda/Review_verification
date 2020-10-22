package com.example.kk_commerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.example.kk_commerce.Models.Buyer;
import com.example.kk_commerce.Models.ResponseMessage;
import com.example.kk_commerce.Models.Token;
import com.google.android.material.snackbar.Snackbar;

import static com.example.kk_commerce.Constants.BASE_URL;

public class LogInActivity extends AppCompatActivity {
    Button signUp, btnLogin;
    String m_token;
    TextView txtUsername, txtPassword;
    LinearLayout linearLayout;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtPassword = findViewById(R.id.password);
        txtUsername = findViewById(R.id.username);
        signUp = findViewById(R.id.signup);
        btnLogin = findViewById(R.id.btnLogin);
        linearLayout = findViewById(R.id.linearLayout);
        loadingDialog = new LoadingDialog(LogInActivity.this);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(LogInActivity.this, RegistrationActivity.class);
                startActivity(in);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txtUsername.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                if(!username.equals("") && !password.equals("")){
                    login(username, password);
                }else if(!username.equals("")){
                    Snackbar sn = Snackbar.make(linearLayout, "Username Required", Snackbar.LENGTH_LONG);
                    sn.setTextColor(Color.parseColor("#ffffff"));
                    sn.setBackgroundTint(Color.parseColor("#990033"));
                    sn.show();
                }else if(!password.equals("")){
                    Snackbar sn = Snackbar.make(linearLayout, "Password Required", Snackbar.LENGTH_LONG);
                    sn.setTextColor(Color.parseColor("#ffffff"));
                    sn.setBackgroundTint(Color.parseColor("#990033"));
                    sn.show();
                }else{
                    Snackbar sn = Snackbar.make(linearLayout, "Username and Password Required", Snackbar.LENGTH_LONG);
                    sn.setTextColor(Color.parseColor("#ffffff"));
                    sn.setBackgroundTint(Color.parseColor("#990033"));
                    sn.show();
                }
            }
        });
    }

    public void login(String username, String password){
        loadingDialog.startLoadingDialog();
        AndroidNetworking.post(BASE_URL + "login")
                .addBodyParameter("consumer_key", username)
                .addBodyParameter("consumer_password", password)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsObject(Token.class, new ParsedRequestListener<Token>(){
                    @Override
                    public void onResponse(Token token) {
                        loadingDialog.dismissDialog();
                        SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("token", token.getToken());
                        editor.apply();
                        m_token = token.getToken();
                        Snackbar sn = Snackbar.make(linearLayout, "Login Successful", Snackbar.LENGTH_LONG);
                        sn.setTextColor(Color.parseColor("#ffffff"));
                        sn.setBackgroundTint(Color.parseColor("#990033"));
                        sn.show();
                        Intent in = new Intent(LogInActivity.this, DashboardActivity.class);
                        startActivity(in);
                    }
                    @Override
                    public void onError(ANError error) {
                        loadingDialog.dismissDialog();
                        Log.i("conn", ""+ error);
                        if(error.getErrorCode() == 0){
                            Toast.makeText(LogInActivity.this, "Network Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}