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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.example.kk_commerce.Adapters.CartAdapter;
import com.example.kk_commerce.Adapters.OrderAdapter;
import com.example.kk_commerce.Models.Order;
import com.example.kk_commerce.Models.Product;
import com.example.kk_commerce.Models.Token;
import com.example.kk_commerce.OrderDetailsActivity;
import com.example.kk_commerce.R;
import com.example.kk_commerce.RegistrationActivity;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.kk_commerce.Constants.BASE_URL;

public class OrdersFragment extends Fragment implements OrderAdapter.ItemListener {
    String m_token;
    AlertDialog.Builder builder;
    TextView text_username, text_password;
    OrderAdapter adapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    ProgressBar progressBar;
    TextView register;
    public OrdersFragment() {
        // Required empty public constructor
    }

    public static OrdersFragment newInstance() {
        OrdersFragment fragment = new OrdersFragment();
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
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.simpleProgressBar);
        RecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);

        SharedPreferences preferences = getContext().getSharedPreferences("User", MODE_PRIVATE);
        m_token = String.valueOf(preferences.getString("token", "1"));

        if (! m_token.equals("1")){
            get_data(m_token);
        }else {
            getDialog();
        }
        return view;
    }

    @Override
    public void onItemClick(Order order) {
        Intent in = new Intent(getActivity(), OrderDetailsActivity.class);
        in.putExtra("order_id", order.getId());
        Log.d("order_id", order.getId());
        startActivity(in);
    }

    public void get_data(String token){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.get( BASE_URL + "orders/")
                .setTag("Orders")
                .addHeaders("Authorization","Token " + token)
                .setPriority(Priority.LOW)
                .build()
                .getAsObjectList(Order.class, new ParsedRequestListener<List<Order>>(){
                    @Override
                    public void onResponse(List<Order> orders) {
                        progressBar.setVisibility(View.INVISIBLE);
                        adapter = new OrderAdapter(getContext(), orders, OrdersFragment.this);
                        recyclerView.setAdapter(adapter);
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