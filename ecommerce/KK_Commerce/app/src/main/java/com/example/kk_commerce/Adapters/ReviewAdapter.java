package com.example.kk_commerce.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kk_commerce.Models.ProductReview;
import com.example.kk_commerce.R;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{

    List<ProductReview> m_reviews;
    Context mContext;
    protected ReviewAdapter.ItemListener mListener;

    public ReviewAdapter(Context context, List<ProductReview> reviews, ReviewAdapter.ItemListener itemListener) {
        m_reviews = reviews;
        mContext = context;
        mListener=itemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView comment, buyer, verfied_unverified;
        public ImageView imageView;
        public RelativeLayout relativeLayout;
        public RatingBar ratingBar;
        ProductReview productReview;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            comment = (TextView) v.findViewById(R.id.comment);
            buyer = (TextView) v.findViewById(R.id.buyer);
            ratingBar = (RatingBar) v.findViewById(R.id.ratingBar2);
            verfied_unverified = (TextView) v.findViewById(R.id.verfied_unverified);

//            imageView = (ImageView) v.findViewById(R.id.imageView);
            relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout);
        }

        public void setData(ProductReview review) {
            this.productReview = review;
            comment.setText(productReview.getComment());
            buyer.setText(productReview.getBuyer_name());
            ratingBar.setRating(Float.parseFloat(productReview.getRating()));

            if(productReview.getVerified_purchase().equals("1")){
                verfied_unverified.setText("Verified Review");
                verfied_unverified.setTextColor(Color.parseColor("#4CAF50"));
            }else{
                verfied_unverified.setText("Review Pending Verification");
                verfied_unverified.setTextColor(Color.parseColor("#8E8F8F"));
            }
//            textView.setText(product.getName());
//            textPrice.setText("Ksh " + product.getUnit_price());
//            Glide.with(mContext).load(product.getImage1()).into(imageView);
//            relativeLayout.setBackgroundColor(Color.parseColor(item.color));

        }


        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(productReview);
            }
        }
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_rating, parent, false);
        return new ReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder Vholder, int position) {
        Vholder.setData(m_reviews.get(position));
    }

    @Override
    public int getItemCount() {
        return m_reviews.size();
    }

    public interface ItemListener {
        void onItemClick(ProductReview review);
    }
}
