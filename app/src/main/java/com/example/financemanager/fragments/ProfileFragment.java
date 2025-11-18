package com.example.financemanager.fragments;

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
import com.example.financemanager.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ProfileFragment extends Fragment {

    private SwitchMaterial switchTheme;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        switchTheme = view.findViewById(R.id.switchTheme);
        if (getActivity() != null)
            sharedPreferences = getActivity().getSharedPreferences("theme_pref", Context.MODE_PRIVATE);

        boolean isDarkMode = sharedPreferences != null && sharedPreferences.getBoolean("dark_mode", false);
        switchTheme.setChecked(isDarkMode);

        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (sharedPreferences != null)
                sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();

            final AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Applying Theme")
                    .setMessage("Please wait...")
                    .setCancelable(false)
                    .show();

            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            new Handler().postDelayed(() -> {
                if (dialog.isShowing()) dialog.dismiss();
            }, 300);
        });

        return view;
    }
}
