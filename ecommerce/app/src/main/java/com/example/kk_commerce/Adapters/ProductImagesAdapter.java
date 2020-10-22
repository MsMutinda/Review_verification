package com.example.kk_commerce.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.kk_commerce.R;
import java.util.List;

public class ProductImagesAdapter extends RecyclerView.Adapter<ProductImagesAdapter.ViewHolder> {
    private List<String> images;
    Context mCtx;

    public ProductImagesAdapter(Context context, List<String> image_list) {
        this.images = image_list;
        this.mCtx = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public String image;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageView);
        }

        public void setData(String my_image) {
            this.image = my_image;
            Glide.with(mCtx).load(my_image).into(imageView);
        }
    }


    @Override
    public ProductImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(images.get(position));
    }

    // Override getItemCount which Returns
    // the length of the RecyclerView.
    @Override
    public int getItemCount() {
        return images.size();
    }
}
