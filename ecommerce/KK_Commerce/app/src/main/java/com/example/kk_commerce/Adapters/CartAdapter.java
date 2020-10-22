package com.example.kk_commerce.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.bumptech.glide.Glide;
import com.example.kk_commerce.Fragments.CartFragment;
import com.example.kk_commerce.Models.Order;
import com.example.kk_commerce.Models.OrderItem;
import com.example.kk_commerce.Models.Product;
import com.example.kk_commerce.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.kk_commerce.Constants.BASE_URL;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    List<Product> m_products;
    Context mContext;
    protected ItemListener mListener;

    public CartAdapter(Context context, List<Product> products, ItemListener itemListener) {
        m_products = products;
        mContext = context;
        mListener=itemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView, textPrice, txtItemCount;
        public ImageView imageView;
        public RelativeLayout relativeLayout;
        Product product;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            textView = (TextView) v.findViewById(R.id.Title);
            textPrice = (TextView) v.findViewById(R.id.Amount);
            imageView = (ImageView) v.findViewById(R.id.img);
            relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout);
            txtItemCount = v.findViewById(R.id.itemCount);
        }

        public void setData(Product product) {
            this.product = product;

            Log.d("PRODUCT", ""+product.getImage1());
            Log.d("PRODUCT", ""+product.getImage2());
            Log.d("PRODUCT", ""+product.getImage3());
            Log.d("PRODUCT", ""+product.getImage4());
            textView.setText(product.getName());
            textPrice.setText("Ksh " + product.getUnit_price());
            Glide.with(mContext).load("http://192.168.43.168:8000" + product.getImage1()).into(imageView);
//            relativeLayout.setBackgroundColor(Color.parseColor(item.color));
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("User", MODE_PRIVATE);
            String mtoken = String.valueOf(sharedPreferences.getString("token", "1"));

            AndroidNetworking.get( BASE_URL + "get/value/in/cart/" + product.getId())
                    .setTag("Cart")
                    .addHeaders("Authorization","Token " + mtoken)
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsObject(OrderItem.class, new ParsedRequestListener<OrderItem>(){
                        @Override
                        public void onResponse(OrderItem orderItem) {
                            txtItemCount.setText("Item Count: " + orderItem.getQuantity());
                        }
                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(mContext, "" + anError.getErrorBody() + anError.getMessage() + anError.getResponse(), Toast.LENGTH_LONG).show();
                        }
                    });

        }


        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(product);
            }
        }
    }

    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder Vholder, int position) {
        Vholder.setData(m_products.get(position));
    }

    @Override
    public int getItemCount() {
        return m_products.size();
    }

    public interface ItemListener {
        void onItemClick(Product product);
    }
}
