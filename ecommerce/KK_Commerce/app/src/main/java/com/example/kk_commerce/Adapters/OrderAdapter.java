package com.example.kk_commerce.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.bumptech.glide.Glide;
import com.example.kk_commerce.Models.Order;
import com.example.kk_commerce.Models.OrderItem;
import com.example.kk_commerce.Models.OrderStatus;
import com.example.kk_commerce.Models.Product;
import com.example.kk_commerce.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.kk_commerce.Constants.BASE_URL;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    List<Order> m_orders;
    Context mContext;
    protected ItemListener mListener;

    public OrderAdapter(Context context, List<Order> orders, ItemListener itemListener) {
        m_orders = orders;
        mContext = context;
        mListener=itemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textDate, textD_Status, textR_Status, textOrder, textAmount;
        public ImageView imageView;
        public RelativeLayout relativeLayout;
        Order order;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            textDate = v.findViewById(R.id.order_date);
            textD_Status = v.findViewById(R.id.delivery_status);
            textR_Status = v.findViewById(R.id.review_status);
            textOrder = v.findViewById(R.id.order);
            textAmount = v.findViewById(R.id.total_amount);

            relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout);
        }

        public void setData(Order order) {
            this.order = order;

            textOrder.setText(order.getUnique_ref());
            textAmount.setText(order.getTotal_amount() + "Ksh");
            if(order.getDelivered().equals("true")){
                textD_Status.setText("Delivered");
                textD_Status.setTextColor(Color.parseColor("#005522"));
                textDate.setText(order.getOrdered_date());
                textDate.setTextColor(Color.parseColor("#005522"));
            }else{
                textD_Status.setText("Delivery Pending");
                textD_Status.setTextColor(Color.parseColor("#C1C1C1"));
                textDate.setText("Pending");
                textDate.setTextColor(Color.parseColor("#C1C1C1"));
            }

            SharedPreferences sharedPreferences = mContext.getSharedPreferences("User", MODE_PRIVATE);
            String mtoken = String.valueOf(sharedPreferences.getString("token", "1"));
            AndroidNetworking.get( BASE_URL + "order/review/status/" + order.getId())
                    .setTag("Cart")
                    .addHeaders("Authorization","Token " + mtoken)
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsObject(OrderStatus.class, new ParsedRequestListener<OrderStatus>(){
                        @Override
                        public void onResponse(OrderStatus orderStatus) {
                            if(orderStatus.getResponse().equals("Reviewed")){
                                textR_Status.setText(orderStatus.getResponse());
                                textR_Status.setTextColor(Color.parseColor("#005522"));
                            }else{
                                textR_Status.setText(orderStatus.getResponse());
                                textR_Status.setTextColor(Color.parseColor("#C1C1C1"));
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(mContext, "" + anError.getErrorBody() + anError.getMessage() + anError.getResponse(), Toast.LENGTH_LONG).show();
                        }
                    });
//            relativeLayout.setBackgroundColor(Color.parseColor(item.color));
        }


        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(order);
            }
        }
    }

    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.orders_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder Vholder, int position) {
        Vholder.setData(m_orders.get(position));
    }

    @Override
    public int getItemCount() {
        return m_orders.size();
    }

    public interface ItemListener {
        void onItemClick(Order order);
    }
}
