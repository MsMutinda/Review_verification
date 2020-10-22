package com.example.kk_commerce.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.example.kk_commerce.Adapters.CartAdapter;
import com.example.kk_commerce.Models.Buyer;
import com.example.kk_commerce.Models.Product;
import com.example.kk_commerce.Models.Token;
import com.example.kk_commerce.R;
import com.example.kk_commerce.RegistrationActivity;
import com.example.kk_commerce.SplashScreenActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.kk_commerce.Constants.BASE_URL;


public class ProfileFragment extends Fragment {
    EditText first_name, last_name, email, password1, password2;
    TextView text_username, text_password, logout;
    String m_token;
    AlertDialog.Builder builder;
    Button update_profile, change_password;
    ProgressBar progressBar;
    TextView register;
    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        first_name = view.findViewById(R.id.first_name);
        last_name = view.findViewById(R.id.last_name);
        password1 = view.findViewById(R.id.password);
        password2 = view.findViewById(R.id.confirm_password);
        email = view.findViewById(R.id.email);
        change_password = view.findViewById(R.id.change_password);
        update_profile = view.findViewById(R.id.submit);
        progressBar = view.findViewById(R.id.simpleProgressBar);
        logout = view.findViewById(R.id.logout);

        SharedPreferences preferences = getContext().getSharedPreferences("User", MODE_PRIVATE);
        m_token = String.valueOf(preferences.getString("token", "1"));

        if (! m_token.equals("1")){
            getData(m_token);
        }else {
            getDialog();
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.INVISIBLE);
                SharedPreferences preferences = getContext().getSharedPreferences("User", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("token", "1");
                editor.apply();
                Toast.makeText(getContext(), "Logged Out", Toast.LENGTH_LONG).show();
                Intent in = new Intent(getContext(), SplashScreenActivity.class);
                startActivity(in);
            }
        });
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass1 = password1.getText().toString().trim();
                String pass2 = password2.getText().toString().trim();
                if(!pass1.equals("") && !pass2.equals("")){
                    if(pass1.equals(pass2)){
                        if(pass1.length() < 8){
                            Snackbar sn = Snackbar.make(getView(), "Password must contain more than 7 characters", Snackbar.LENGTH_LONG);
                            sn.show();
                        }else{
                            changePassword(m_token, pass1);
                        }
                    }else{
                        Snackbar sn = Snackbar.make(getView(), "Passwords do not match", Snackbar.LENGTH_LONG);
                        sn.show();
                    }
                }else{
                    Snackbar sn = Snackbar.make(getView(), "Password fields must contain values", Snackbar.LENGTH_LONG);
                    sn.show();
                }
            }
        });

        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String f_name = first_name.getText().toString().trim();
                String l_name = last_name.getText().toString().trim();
                String e = email.getText().toString().trim();

                if (!f_name.equals("") && !l_name.equals("") && !e.equals("")){
                    updateProfile(m_token, f_name, l_name, e);
                }else{
                    Snackbar sn = Snackbar.make(getView(), "Kindly fill all profile detail fields", Snackbar.LENGTH_LONG);
                    sn.show();
                }
            }
        });
        return view;
    }

    public void getData(String token){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.get( BASE_URL + "profile")
                .setTag("Cart")
                .addHeaders("Authorization","Token " + token)
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(Buyer.class, new ParsedRequestListener<Buyer>(){
                    @Override
                    public void onResponse(Buyer buyer) {
                        progressBar.setVisibility(View.INVISIBLE);
                        first_name.setText(buyer.getFirst_name());
                        last_name.setText(buyer.getLast_name());
                        email.setText(buyer.getEmail());
                        Snackbar sn = Snackbar.make(getView(), "Summary profile details", Snackbar.LENGTH_LONG);
                        sn.show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "" + anError.getErrorBody() + anError.getMessage() + anError.getResponse(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void changePassword(String token, String password){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post( BASE_URL + "change/password")
                .setTag("Cart")
                .addHeaders("Authorization","Token " + token)
                .addBodyParameter("password", password)
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(Buyer.class, new ParsedRequestListener<Buyer>(){
                    @Override
                    public void onResponse(Buyer buyer) {
                        progressBar.setVisibility(View.INVISIBLE);
                        first_name.setText(buyer.getFirst_name());
                        last_name.setText(buyer.getLast_name());
                        email.setText(buyer.getEmail());
                        Snackbar sn = Snackbar.make(getView(), "Profile Information Updated", Snackbar.LENGTH_LONG);
                        sn.show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "" + anError.getErrorBody() + anError.getMessage() + anError.getResponse(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void updateProfile(String token, String f_name, String l_name, String e){
        progressBar.setVisibility(View.INVISIBLE);
        AndroidNetworking.post( BASE_URL + "update/profile")
                .setTag("Cart")
                .addHeaders("Authorization","Token " + token)
                .addBodyParameter("first_name", f_name)
                .addBodyParameter("last_name", l_name)
                .addBodyParameter("email", e)
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(Buyer.class, new ParsedRequestListener<Buyer>(){
                    @Override
                    public void onResponse(Buyer buyer) {
                        progressBar.setVisibility(View.INVISIBLE);
                        first_name.setText(buyer.getFirst_name());
                        last_name.setText(buyer.getLast_name());
                        email.setText(buyer.getEmail());
                        Snackbar sn = Snackbar.make(getView(), "Password Changed", Snackbar.LENGTH_LONG);
                        sn.show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "" + anError.getErrorBody() + anError.getMessage() + anError.getResponse(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    public void getDialog() {
        builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View mView = inflater.inflate(R.layout.dialog_signin, null);
        text_username = mView.findViewById(R.id.username);
        text_password = mView.findViewById(R.id.password);
        register = mView.findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), RegistrationActivity.class);
                startActivity(in);
            }
        });

        builder.setView(mView)
                // Add action buttons
                .setPositiveButton("LogIn", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String username = text_username.getText().toString().trim();
                        String password = text_password.getText().toString().trim();
                        login(username, password, dialog);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void login(String username, String password, final DialogInterface dialogInterface){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(BASE_URL + "login")
                .addBodyParameter("consumer_key", username)
                .addBodyParameter("consumer_password", password)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsObject(Token.class, new ParsedRequestListener<Token>(){
                    @Override
                    public void onResponse(Token token) {
                        progressBar.setVisibility(View.INVISIBLE);
                        SharedPreferences preferences = getContext().getSharedPreferences("User", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("token", token.getToken());
                        editor.apply();
                        m_token = token.getToken();
                        dialogInterface.dismiss();
                    }
                    @Override
                    public void onError(ANError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.i("conn", ""+ error);
                        if(error.getErrorCode() == 0){
                            Toast.makeText(getContext(), "Network Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}