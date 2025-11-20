package com.example.financemanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.financemanager.classes.Categories;
import com.example.financemanager.classes.TransactionData;
import com.example.financemanager.databinding.FragmentIncomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class IncomeFragment extends Fragment {

    FragmentIncomeBinding binding;
    FirebaseAuth auth;
    DatabaseReference transactionRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentIncomeBinding.inflate(inflater, container, false);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                Categories.INCOME
        );
        binding.incomeCategory.setAdapter(adapter);
        binding.incomeCategory.setOnClickListener(v -> binding.incomeCategory.showDropDown());

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            transactionRef = FirebaseDatabase.getInstance()
                    .getReference("Transactions")
                    .child(auth.getCurrentUser().getUid());
        }

        binding.btnSaveIncome.setOnClickListener(v -> saveIncome());

        return binding.getRoot();
    }

    void saveIncome() {
        String amountText = binding.incomeAmount.getText().toString().trim();
        String categoryText = binding.incomeCategory.getText().toString().trim();

        if (amountText.isEmpty() || categoryText.isEmpty()) {
            Toast.makeText(getContext(), "Enter amount and select category", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountText);
        if (amount <= 0) {
            Toast.makeText(getContext(), "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        TransactionData transaction = new TransactionData(
                categoryText,
                amount,
                date,
                "income",
                categoryText
        );

        if (transactionRef != null) {
            String id = transactionRef.push().getKey();
            if (id != null) {
                transactionRef.child(id).setValue(transaction)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Income Added", Toast.LENGTH_SHORT).show();
                            binding.incomeAmount.setText("");
                            binding.incomeCategory.setText("");
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add income",
                                Toast.LENGTH_SHORT).show());
            }
        }
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
