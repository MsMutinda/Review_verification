package com.example.kk_commerce.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.example.kk_commerce.Adapters.ProductAdapter;
import com.example.kk_commerce.Constants;
import com.example.kk_commerce.DashboardActivity;
import com.example.kk_commerce.Models.Product;
import com.example.kk_commerce.ProductDetailActivity;
import com.example.kk_commerce.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

import static com.example.kk_commerce.Constants.BASE_URL;

public class HomeFragment extends Fragment implements ProductAdapter.ItemListener {

    ProductAdapter adapter;
    RecyclerView recyclerView;
    GridLayoutManager manager;
    ProgressBar progressBar;
    public HomeFragment() {

    }


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home2, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.simpleProgressBar);
        manager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        get_data();
//        if(Constants.isServerReachable()){
//            get_data();
//        }else{
//            Snackbar sn = Snackbar.make(view.getRootView(), "Error connecting to server", Snackbar.LENGTH_LONG);
//            sn.show();
//        }

        return view;
    }

    public void get_data(){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.get( BASE_URL + "products")
                .setTag("Products")
                .setPriority(Priority.LOW)
                .build()
                .getAsObjectList(Product.class, new ParsedRequestListener<List<Product>>(){
                    @Override
                    public void onResponse(List<Product> products) {
                        progressBar.setVisibility(View.INVISIBLE);
                        adapter = new ProductAdapter(getContext(), products, HomeFragment.this);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "" + anError.getErrorBody() + anError.getMessage() + anError.getErrorCode(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onItemClick(Product product) {
//        Toast.makeText(getContext(), product.getName() + " is clicked", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getActivity(), ProductDetailActivity.class);
        i.putExtra("product_id", product.getId());
        startActivity(i);
    }
}