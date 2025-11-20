package com.example.financemanager.activities;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.financemanager.R;
import com.example.financemanager.databinding.ActivityMainBinding;
import com.example.financemanager.fragments.HomeFragment;
import com.example.financemanager.fragments.ProfileFragment;
import com.example.financemanager.fragments.StatsFragment;
import com.example.financemanager.fragments.WalletFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Fragment activeFragment;
    HomeFragment homeFragment;
    WalletFragment walletFragment;
    StatsFragment statsFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setInsets();


        NavHostFragment hostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        NavController controller = hostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, controller);


    }
    void setInsets(){
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}
