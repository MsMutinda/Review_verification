package com.example.kk_commerce;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.bumptech.glide.Glide;
import com.example.kk_commerce.Adapters.ProductImagesAdapter;
import com.example.kk_commerce.Models.Image;
import com.example.kk_commerce.Models.Product;
import com.example.kk_commerce.Models.ProductReview;
import com.example.kk_commerce.Models.Token;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import static com.example.kk_commerce.Constants.BASE_URL;
import static com.example.kk_commerce.Constants.MEDIA_BASE;

public class ReviewActivity extends AppCompatActivity {
    String m_token, product_id, order_id;
    Toolbar toolbar;
    TextView pageTitle, text_username, text_password, product_name, product_price;
    ImageView main_image;
    RatingBar ratingBar;
    AlertDialog.Builder builder;
    TextInputEditText editText;
    Button button;
    RelativeLayout relativeLayout;
    TextView register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setBackgroundColor(getResources().getColor(R.color.colorWhiteTrans));
        setSupportActionBar(toolbar);
//        pageTitle = (TextView) toolbar.findViewById(R.id.tv_title);
//        pageTitle.setTextColor(getResources().getColor(R.color.primaryDarkColor));
        product_name = findViewById(R.id.product_name);
        product_price = findViewById(R.id.product_price);
        main_image = findViewById(R.id.main_image);
        ratingBar = findViewById(R.id.ratingBar);
        editText = findViewById(R.id.edit_text_comment);
        button = findViewById(R.id.submit);
        relativeLayout = findViewById(R.id.relativeLayout);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        m_token = String.valueOf(preferences.getString("token", "1"));
        product_id = intent.getStringExtra("product_id");
        order_id = intent.getStringExtra("order_id");

        if (! m_token.equals("1")){
            get_product(m_token, product_id);
            get_product_review(m_token, order_id, product_id);
        }else {
            getDialog();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = editText.getText().toString().trim();
                String rating = String.valueOf(ratingBar.getRating());
                if(!comment.equals("") || rating.equals("")){
                    submit_rating(m_token, order_id, product_id, rating, comment);
                }else {
                    Snackbar sn = Snackbar.make(relativeLayout,
                            "Missing Mandatory Fields", Snackbar.LENGTH_LONG);
                    sn.show();
                }
            }
        });
    }

    public void get_product(String token, final String product_id){
        AndroidNetworking.get( BASE_URL + "products/" + product_id + "/")
                .setTag("Products")
                .setPriority(Priority.LOW)
                .addHeaders("Authorization","Token " + token)
                .build()
                .getAsObject(Product.class, new ParsedRequestListener<Product>(){
                    @Override
                    public void onResponse(Product product) {
                        product_price.setText("Ksh: " + product.getUnit_price());
                        product_name.setText("Product Name: " + product.getName());
                        Glide.with(ReviewActivity.this).load(product.getImage1()).into(main_image);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(), "" + anError.getErrorBody() + anError.getMessage() + anError.getResponse(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void get_product_review(String token, String order_id, String product_id){
        AndroidNetworking.get( BASE_URL + "product/review/" + order_id + "/" + product_id)
                .setTag("Product Review")
                .setPriority(Priority.LOW)
                .addHeaders("Authorization","Token " + token)
                .build()
                .getAsObject(ProductReview.class, new ParsedRequestListener<ProductReview>(){
                    @Override
                    public void onResponse(ProductReview productReview) {
                        editText.setText(productReview.getComment());
                        ratingBar.setRating(Float.parseFloat(productReview.getRating()));
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(), "" + anError.getErrorBody() + anError.getMessage() + anError.getResponse(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void submit_rating(String token, String order_id, String product_id, String rating, String comment){
        AndroidNetworking.post( BASE_URL + "submit/rating/"+ order_id+ "/" + product_id)
                .setTag("Products")
                .addHeaders("Authorization","Token " + token)
                .addBodyParameter("comment", comment)
                .addBodyParameter("rating", rating)
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(ProductReview.class, new ParsedRequestListener<ProductReview>(){
                    @Override
                    public void onResponse(ProductReview productReview) {
                        editText.setText(productReview.getComment());
                        ratingBar.setRating(Float.parseFloat(productReview.getRating()));
                        Snackbar sn = Snackbar.make(relativeLayout,
                                "Product Review Submitted", Snackbar.LENGTH_LONG);
                        sn.show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(), "" + anError.getErrorBody() + anError.getMessage() + anError.getResponse(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void getDialog() {
        builder = new AlertDialog.Builder(ReviewActivity.this);
        LayoutInflater inflater = ReviewActivity.this.getLayoutInflater();

        View mView = inflater.inflate(R.layout.dialog_signin, null);
        text_username = mView.findViewById(R.id.username);
        text_password = mView.findViewById(R.id.password);
        register = mView.findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(ReviewActivity.this, RegistrationActivity.class);
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
                            Toast.makeText(ReviewActivity.this, "Network Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}