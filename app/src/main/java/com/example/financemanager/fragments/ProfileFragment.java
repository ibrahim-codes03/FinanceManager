package com.example.financemanager.fragments;

import static androidx.core.app.ActivityCompat.recreate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.example.financemanager.classes.CurrencyManager;
import com.example.financemanager.databinding.FragmentProfileBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    SharedPreferences sharedPreferences;
    CurrencyManager currencyManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        if (getActivity() != null) {
            sharedPreferences = getActivity().getSharedPreferences("theme_pref", Context.MODE_PRIVATE);
            currencyManager = new CurrencyManager(getActivity());
        }

        boolean isDarkMode = sharedPreferences != null && sharedPreferences.getBoolean("dark_mode", false);
        binding.switchTheme.setChecked(isDarkMode);

        binding.currencyText.setText(currencyManager.getCurrentSymbol());

        binding.switchTheme.setOnCheckedChangeListener((b, isChecked) -> {
            if (sharedPreferences != null)
                sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();



            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            recreate(getActivity());

        });

        binding.cardCurrency.setOnClickListener(v -> {
            if (currencyManager == null) return;

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Select Currency")
                    .setItems(currencyManager.currencyNames, (dialog, which) -> {
                        currencyManager.saveCurrency(
                                currencyManager.currencySymbols[which],
                                currencyManager.conversionRates[which]
                        );

                        binding.currencyText.setText(currencyManager.currencySymbols[which]);

                        HomeFragment homeFragment = (HomeFragment) getActivity()
                                .getSupportFragmentManager()
                                .findFragmentByTag("home");

                        if (homeFragment != null)
                            homeFragment.updateCurrency(
                                    currencyManager.currencySymbols[which],
                                    currencyManager.conversionRates[which]
                            );
                    })
                    .show();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
