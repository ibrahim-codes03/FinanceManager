package com.example.financemanager.activities;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import com.example.financemanager.R;
import com.example.financemanager.fragments.HomeFragment;
import com.example.financemanager.fragments.ProfileFragment;
import com.example.financemanager.fragments.StatsFragment;
import com.example.financemanager.fragments.WalletFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Fragment activeFragment;
    private HomeFragment homeFragment;
    private WalletFragment walletFragment;
    private StatsFragment statsFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("home");
        walletFragment = (WalletFragment) getSupportFragmentManager().findFragmentByTag("wallet");
        statsFragment = (StatsFragment) getSupportFragmentManager().findFragmentByTag("stats");
        profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("profile");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (homeFragment == null) {
            homeFragment = new HomeFragment();
            ft.add(R.id.fragment_container, homeFragment, "home");
        }
        if (walletFragment == null) {
            walletFragment = new WalletFragment();
            ft.add(R.id.fragment_container, walletFragment, "wallet").hide(walletFragment);
        }
        if (statsFragment == null) {
            statsFragment = new StatsFragment();
            ft.add(R.id.fragment_container, statsFragment, "stats").hide(statsFragment);
        }
        if (profileFragment == null) {
            profileFragment = new ProfileFragment();
            ft.add(R.id.fragment_container, profileFragment, "profile").hide(profileFragment);
        }

        ft.commit();

        if (savedInstanceState == null) {
            activeFragment = homeFragment;
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else {
            if (homeFragment != null && homeFragment.isAdded()) activeFragment = homeFragment;
            else if (walletFragment != null && walletFragment.isAdded()) activeFragment = walletFragment;
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switchFragment(item.getItemId());
            return true;
        });
    }

    private void switchFragment(int itemId) {
        if (activeFragment == null) return;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(activeFragment);

        if (itemId == R.id.nav_home) activeFragment = homeFragment;
        else if (itemId == R.id.nav_wallet) activeFragment = walletFragment;
        else if (itemId == R.id.nav_stats) activeFragment = statsFragment;
        else if (itemId == R.id.nav_profile) activeFragment = profileFragment;

        if (!activeFragment.isAdded()) transaction.add(R.id.fragment_container, activeFragment).commit();
        else transaction.show(activeFragment).commit();
    }
}
