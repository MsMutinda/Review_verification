package com.example.kk_commerce;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.example.kk_commerce.Adapters.OrderAdapter;
import com.example.kk_commerce.Adapters.OrderProductAdapter;
import com.example.kk_commerce.Fragments.OrdersFragment;
import com.example.kk_commerce.Models.Order;
import com.example.kk_commerce.Models.Product;
import com.example.kk_commerce.Models.Token;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import static com.example.kk_commerce.Constants.BASE_URL;

public class OrderDetailsActivity extends AppCompatActivity implements OrderProductAdapter.ItemListener {
    String m_token;
    AlertDialog.Builder builder;
    TextView text_username, text_password;
    OrderProductAdapter adapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    String order_id;
    Toolbar toolbar;
    TextView pageTitle;
    Button button;
    Order order;
    RelativeLayout relativeLayout;
    TextView register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        button = findViewById(R.id.delivered);
//        toolbar.setBackgroundColor(getResources().getColor(R.color.colorWhiteTrans));
        relativeLayout = findViewById(R.id.relativeLayout);
        setSupportActionBar(toolbar);
//        pageTitle = (TextView) toolbar.findViewById(R.id.tv_title);
//        pageTitle.setText("COMMERCE");
//        pageTitle.setTextColor(getResources().getColor(R.color.primaryDarkColor));
//        getSupportActionBar().setTitle("COMMERCE");

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewLayoutManager = new LinearLayoutManager(OrderDetailsActivity.this);
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);

        SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
        m_token = String.valueOf(preferences.getString("token", "1"));
        Intent intent = getIntent();
        order_id = intent.getStringExtra("order_id");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mark_as_delivered(m_token, order.getId());
            }
        });
        if (! m_token.equals("1")){
            get_data(m_token, order_id);
            get_order(m_token, order_id);
        }else {
            getDialog();
        }
    }

    @Override
    public void onItemClick(Product product) {
        if(order.getDelivered().equals("false")){
            Snackbar sn = Snackbar.make(relativeLayout,
                    "Cannot review this product since the order has not been delivered", Snackbar.LENGTH_LONG);
            sn.show();
        }else{
            Intent in = new Intent(OrderDetailsActivity.this, ReviewActivity.class);
            in.putExtra("product_id", product.getId());
            in.putExtra("order_id", order.getId());
            startActivity(in);
        }
    }

    public void get_data(String token, String order_id){
        AndroidNetworking.get( BASE_URL + "order/products/"+order_id)
                .setTag("Orders")
                .addHeaders("Authorization","Token " + token)
                .setPriority(Priority.LOW)
                .build()
                .getAsObjectList(Product.class, new ParsedRequestListener<List<Product>>(){
                    @Override
                    public void onResponse(List<Product> products) {
                        adapter = new OrderProductAdapter(OrderDetailsActivity.this, products, OrderDetailsActivity.this);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(OrderDetailsActivity.this, "" + anError.getErrorBody() + anError.getMessage() + anError.getResponse(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void get_order(String token, String order_id){
        AndroidNetworking.get( BASE_URL + "order/"+ order_id)
                .setTag("Order")
                .addHeaders("Authorization","Token " + token)
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(Order.class, new ParsedRequestListener<Order>(){
                    @Override
                    public void onResponse(Order m_order) {
                        order = m_order;
                        if (m_order.getDelivered().equals("true")){
                            button.setVisibility(View.GONE);
                        }else{
                            button.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(OrderDetailsActivity.this, "" + anError.getErrorBody() + anError.getMessage() + anError.getResponse(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void mark_as_delivered(String token, String order_id){
        AndroidNetworking.get( BASE_URL + "order/ordered/"+ order_id)
                .setTag("Order")
                .addHeaders("Authorization","Token " + token)
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(Order.class, new ParsedRequestListener<Order>(){
                    @Override
                    public void onResponse(Order m_order) {
                        order = m_order;
                        Snackbar sn = Snackbar.make(relativeLayout,
                                "You can now click on an individual product to leave a review and complete your order", Snackbar.LENGTH_LONG);
                        sn.show();
                        if (m_order.getDelivered().equals("true")){
                            button.setVisibility(View.GONE);
                        }else{
                            button.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(OrderDetailsActivity.this, "" + anError.getErrorBody() + anError.getMessage() + anError.getResponse(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    public void getDialog() {
        builder = new AlertDialog.Builder(OrderDetailsActivity.this);
        LayoutInflater inflater = getLayoutInflater();

        View mView = inflater.inflate(R.layout.dialog_signin, null);
        text_username = mView.findViewById(R.id.username);
        text_password = mView.findViewById(R.id.password);
        register = mView.findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(OrderDetailsActivity.this, RegistrationActivity.class);
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
                        SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
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
                            Toast.makeText(OrderDetailsActivity.this, "Network Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}