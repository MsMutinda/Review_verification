package com.example.kk_commerce.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.example.kk_commerce.Adapters.CartAdapter;
import com.example.kk_commerce.Adapters.ProductAdapter;
import com.example.kk_commerce.Adapters.ReviewAdapter;
import com.example.kk_commerce.Models.Order;
import com.example.kk_commerce.Models.Product;
import com.example.kk_commerce.Models.Token;
import com.example.kk_commerce.ProductDetailActivity;
import com.example.kk_commerce.R;
import com.example.kk_commerce.RegistrationActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.kk_commerce.Constants.BASE_URL;

public class CartFragment extends Fragment implements CartAdapter.ItemListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    CartAdapter adapter;
    String m_token;
    AlertDialog.Builder builder;
    TextView text_username, text_password;
    Button button;
    ProgressBar progressBar;
    TextView register;
    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance() {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
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
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.simpleProgressBar);
        RecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);

        button = view.findViewById(R.id.add_to_cart);

        SharedPreferences preferences = getContext().getSharedPreferences("User", MODE_PRIVATE);
        m_token = String.valueOf(preferences.getString("token", "1"));

        if (! m_token.equals("1")){
            get_data(m_token);
        }else {
            getDialog();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkout(m_token);
            }
        });

        return view;
    }

    @Override
    public void onItemClick(Product product) {

    }

    public void get_data(String token){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.get( BASE_URL + "cart")
                .setTag("Cart")
                .addHeaders("Authorization","Token " + token)
                .setPriority(Priority.LOW)
                .build()
                .getAsObjectList(Product.class, new ParsedRequestListener<List<Product>>(){
                    @Override
                    public void onResponse(List<Product> products) {
                        progressBar.setVisibility(View.INVISIBLE);
                        adapter = new CartAdapter(getContext(), products, CartFragment.this);
                        recyclerView.setAdapter(adapter);
                        if (products.size() < 1){
                            Snackbar sn = Snackbar.make(getView(),
                                    "Your Cart Is Empty", Snackbar.LENGTH_LONG);
                            sn.show();
                            button.setVisibility(View.GONE);
                        }else{
                            button.setVisibility(View.VISIBLE);
                            Snackbar sn = Snackbar.make(getView(),
                                    "Your Have " + products.size() + " Items In Your Cart", Snackbar.LENGTH_LONG);
                            sn.show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "" + anError.getErrorBody() + anError.getMessage() + anError.getResponse(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void checkout(String token){
        AndroidNetworking.get( BASE_URL + "checkout")
                .setTag("Cart")
                .addHeaders("Authorization","Token " + token)
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(Order.class, new ParsedRequestListener<Order>(){
                    @Override
                    public void onResponse(Order order) {
                        get_data(m_token);
                    }

                    @Override
                    public void onError(ANError anError) {
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
        AndroidNetworking.post(BASE_URL + "login")
                .addBodyParameter("consumer_key", username)
                .addBodyParameter("consumer_password", password)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsObject(Token.class, new ParsedRequestListener<Token>(){
                    @Override
                    public void onResponse(Token token) {
                        SharedPreferences preferences = getContext().getSharedPreferences("User", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("token", token.getToken());
                        editor.apply();
                        m_token = token.getToken();
                        dialogInterface.dismiss();
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.i("conn", ""+ error);
                        if(error.getErrorCode() == 0){
                            Toast.makeText(getContext(), "Network Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}