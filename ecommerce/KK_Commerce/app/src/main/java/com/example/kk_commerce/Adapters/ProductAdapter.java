package com.example.kk_commerce.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kk_commerce.Models.Product;
import com.example.kk_commerce.R;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    List<Product> m_products;
    Context mContext;
    protected ItemListener mListener;

    public ProductAdapter(Context context, List<Product> products, ItemListener itemListener) {
        m_products = products;
        mContext = context;
        mListener=itemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView, textPrice;
        public ImageView imageView;
        public RelativeLayout relativeLayout;
        Product product;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            textView = (TextView) v.findViewById(R.id.textView);
            textPrice = (TextView) v.findViewById(R.id.textPrice);
            imageView = (ImageView) v.findViewById(R.id.imageView);
            relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout);
        }

        public void setData(Product product) {
            this.product = product;

            textView.setText(product.getName());
            textPrice.setText("Ksh " + product.getUnit_price());
            Glide.with(mContext).load(product.getImage1()).into(imageView);
//            relativeLayout.setBackgroundColor(Color.parseColor(item.color));

        }


        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(product);
            }
        }
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_view_item, parent, false);
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
