package com.example.kk_commerce;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.example.kk_commerce.Adapters.ProductAdapter;
import com.example.kk_commerce.Adapters.ProductImagesAdapter;
import com.example.kk_commerce.Adapters.ReviewAdapter;
import com.example.kk_commerce.Fragments.HomeFragment;
import com.example.kk_commerce.Models.Order;
import com.example.kk_commerce.Models.OrderItem;
import com.example.kk_commerce.Models.Product;
import com.example.kk_commerce.Models.ProductReview;
import com.example.kk_commerce.Models.Token;

import java.util.ArrayList;
import java.util.List;

import static com.example.kk_commerce.Constants.BASE_URL;

public class ProductDetailActivity extends AppCompatActivity implements ReviewAdapter.ItemListener {
    Toolbar toolbar;
    TextView pageTitle, productTitle, amount, productDescription, text_username, text_password;
    RecyclerView recyclerView, recyclerView2;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    LinearLayoutManager HorizontalLayout;
    ArrayList<String> images;
    ProductImagesAdapter adapter;
    ReviewAdapter adapter2;
    Button add_to_cart;
    AlertDialog.Builder builder;
    String m_token, product_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Intent intent = getIntent();

        productDescription = findViewById(R.id.productDescription);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView2 = findViewById(R.id.recyclerView2);
        productTitle = findViewById(R.id.productTitle);
        add_to_cart = (Button) findViewById(R.id.add_to_cart);
        amount = findViewById(R.id.amount);
//        toolbar.setBackgroundColor(getResources().getColor(R.color.colorWhiteTrans));
        setSupportActionBar(toolbar);
//        pageTitle = (TextView) toolbar.findViewById(R.id.tv_title);
//        pageTitle.setTextColor(getResources().getColor(R.color.primaryDarkColor));

        recyclerView2 = findViewById(R.id.recyclerView2);
        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView2.setLayoutManager(RecyclerViewLayoutManager);

        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
        HorizontalLayout = new LinearLayoutManager(ProductDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(HorizontalLayout);
        product_id = intent.getStringExtra("product_id");
        get_product(product_id);
        getReviews(product_id);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        m_token = String.valueOf(preferences.getString("token", "1"));
        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! m_token.equals("1")){
                    add_To_Cart(product_id, m_token);
//                    Toast.makeText(ProductDetailActivity.this, ""+m_token, Toast.LENGTH_LONG).show();
                }else {
                    getDialog();
                }
            }
        });
    }

    public void get_product(final String product_id){
        AndroidNetworking.get( BASE_URL + "products/" + product_id + "/")
                .setTag("Products")
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(Product.class, new ParsedRequestListener<Product>(){
                    @Override
                    public void onResponse(Product product) {
                        images = new ArrayList<>();
                        images.add(product.getImage1());
                        images.add(product.getImage2());
                        images.add(product.getImage3());
                        images.add(product.getImage4());

                        amount.setText("Ksh: " + product.getUnit_price());
                        productTitle.setText(product.getName());
                        productDescription.setText(product.getDescription());
                        adapter = new ProductImagesAdapter(ProductDetailActivity.this, images);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(), "" + anError.getErrorBody() + anError.getMessage() + anError.getResponse(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void getReviews(String product_id){
        AndroidNetworking.get( BASE_URL + "product/reviews/" + product_id + "/")
                .setTag("Product Reviews")
                .setPriority(Priority.LOW)
                .build()
                .getAsObjectList(ProductReview.class, new ParsedRequestListener<List<ProductReview>>(){
                    @Override
                    public void onResponse(List<ProductReview> productReviews) {
                        Log.d("Reviews", ""+ productReviews);
                        adapter2 = new ReviewAdapter(getApplicationContext(), productReviews, ProductDetailActivity.this);
                        recyclerView2.setAdapter(adapter2);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(), "" + anError.getErrorBody() + anError.getMessage() + anError.getResponse(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onItemClick(ProductReview productReview) {

    }

    public void add_To_Cart(String product_id, String token){
        AndroidNetworking.get( BASE_URL + "add/to/cart/" + product_id)
                .setTag("Product Reviews")
                .addHeaders("Authorization","Token " + token)
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(OrderItem.class, new ParsedRequestListener<OrderItem>(){
                    @Override
                    public void onResponse(OrderItem orderItem) {
                        Toast.makeText(getApplicationContext(), "Item Added To Cart. Unit Count Is " + orderItem.getQuantity(), Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(), "" + anError.getErrorBody() + anError.getMessage() + anError.getResponse(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    public void getDialog() {
        builder = new AlertDialog.Builder(ProductDetailActivity.this);
        LayoutInflater inflater = ProductDetailActivity.this.getLayoutInflater();

        View mView = inflater.inflate(R.layout.dialog_signin, null);
        text_username = mView.findViewById(R.id.username);
        text_password = mView.findViewById(R.id.password);
        TextView register = mView.findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(ProductDetailActivity.this, RegistrationActivity.class);
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
                        SharedPreferences preferences = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
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
                            Toast.makeText(ProductDetailActivity.this, "Network Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}