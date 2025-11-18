    package com.example.financemanager.classes;

    import android.app.Application;
    import android.content.Context;
    import android.content.SharedPreferences;
    import androidx.appcompat.app.AppCompatDelegate;

    public class FinanceManager extends Application {

        @Override
        public void onCreate() {
            super.onCreate();

            SharedPreferences sharedPreferences = getSharedPreferences("theme_pref", Context.MODE_PRIVATE);

            boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);

            if (sharedPreferences.contains("dark_mode")) {
                AppCompatDelegate.setDefaultNightMode(isDarkMode ?
                        AppCompatDelegate.MODE_NIGHT_YES :
                        AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }
