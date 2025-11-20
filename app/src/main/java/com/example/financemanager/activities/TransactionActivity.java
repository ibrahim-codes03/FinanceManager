package com.example.financemanager.activities;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.financemanager.adapters.TransactionPagerAdapter;
import com.example.financemanager.databinding.ActivityTransactionBinding;
import com.example.financemanager.fragments.ExpenseFragment;
import com.example.financemanager.fragments.IncomeFragment;
import com.google.android.material.tabs.TabLayoutMediator;

public class TransactionActivity extends AppCompatActivity {

    ActivityTransactionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.transactionactivity, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TransactionPagerAdapter adapter = new TransactionPagerAdapter(this);
        adapter.addFragment(new ExpenseFragment(), "Expense");
        adapter.addFragment(new IncomeFragment(), "Income");

        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(adapter.getTitle(position))
        ).attach();
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}
