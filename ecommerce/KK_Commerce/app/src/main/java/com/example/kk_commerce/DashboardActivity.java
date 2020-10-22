package com.example.kk_commerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.example.kk_commerce.Adapters.ProductAdapter;
import com.example.kk_commerce.Fragments.CartFragment;
import com.example.kk_commerce.Fragments.HomeFragment;
import com.example.kk_commerce.Fragments.OrdersFragment;
import com.example.kk_commerce.Fragments.ProfileFragment;
import com.example.kk_commerce.Models.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import static com.example.kk_commerce.Constants.BASE_URL;

public class DashboardActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView pageTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setBackgroundColor(getResources().getColor(R.color.colorWhiteTrans));
        setSupportActionBar(toolbar);
        pageTitle = (TextView) toolbar.findViewById(R.id.tv_title);
        pageTitle.setText("E-COM");
        pageTitle.setTextColor(getResources().getColor(R.color.colorWhiteTrans));
//        getSupportActionBar().setTitle("E-COM");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_new_home);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(new HomeFragment());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.home:
//                    toolbar.setTitle("Shop");
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.cart:
//                    toolbar.setTitle("Cart");
                    fragment = new CartFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.orders:
//                    toolbar.setTitle("Orders");
                    fragment = new OrdersFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.profile:
//                    toolbar.setTitle("Profile");
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };


    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}