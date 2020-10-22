package com.example.kk_commerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.example.kk_commerce.Models.Token;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import static com.example.kk_commerce.Constants.BASE_URL;

public class RegistrationActivity extends AppCompatActivity {
    TextInputEditText txtPassword, txtConfirmPassword, txtFisrtName, txtLastName, txtUsername, txtPhoneNumber;
    Button signUpBtn, signIn;
    LinearLayout linearLayout;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        txtPassword = findViewById(R.id.password);
        txtConfirmPassword = findViewById(R.id.confirm_password);
        txtUsername = findViewById(R.id.username);
        txtFisrtName = findViewById(R.id.first_name);
        txtLastName = findViewById(R.id.last_name);
        signIn = findViewById(R.id.signin);
        signUpBtn = findViewById(R.id.btnSignUp);
        txtPhoneNumber = findViewById(R.id.phone_number);
        linearLayout = findViewById(R.id.linearLayout);
        loadingDialog = new LoadingDialog(RegistrationActivity.this);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(RegistrationActivity.this, LogInActivity.class);
                startActivity(in);
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txtUsername.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();
                String first_name = txtFisrtName.getText().toString().trim();
                String last_name = txtLastName.getText().toString().trim();
                String confirm_password = txtConfirmPassword.getText().toString().trim();
                String phone_number = txtPhoneNumber.getText().toString().trim();

                if(!username.equals("") && !password.equals("") && !first_name.equals("") && !last_name.equals("")
                        && !confirm_password.equals("") && !phone_number.equals("")){
                    if(!password.equals(confirm_password)){
                        Snackbar sn = Snackbar.make(linearLayout, "Passwords Do Not Match", Snackbar.LENGTH_LONG);
                        sn.setTextColor(Color.parseColor("#ffffff"));
                        sn.setBackgroundTint(Color.parseColor("#990033"));
                        sn.show();
                    }else{
                        SignUp(username, password, first_name, last_name, phone_number);
                    }
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
                    Snackbar sn = Snackbar.make(linearLayout, "All Fields Are Required", Snackbar.LENGTH_LONG);
                    sn.setTextColor(Color.parseColor("#ffffff"));
                    sn.setBackgroundTint(Color.parseColor("#990033"));
                    sn.show();
                }
            }
        });
    }

    public void SignUp(String username, String password, String first_name, String last_name, String phone_number){
        loadingDialog.startLoadingDialog();
        AndroidNetworking.post(BASE_URL + "sign/up")
                .addBodyParameter("username", username)
                .addBodyParameter("password", password)
                .addBodyParameter("first_name", first_name)
                .addBodyParameter("last_name", last_name)
                .addBodyParameter("phone_number", last_name)
                .setTag("Registration")
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
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                    }
                    @Override
                    public void onError(ANError error) {
                        loadingDialog.dismissDialog();
                        Log.i("conn", ""+ error);
                        if(error.getErrorCode() == 0){
                            Toast.makeText(RegistrationActivity.this, "Network Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}