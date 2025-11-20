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
import com.example.financemanager.databinding.FragmentExpenseBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExpenseFragment extends Fragment {

    FragmentExpenseBinding binding;
    FirebaseAuth auth;
    DatabaseReference transactionRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExpenseBinding.inflate(inflater, container, false);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                Categories.EXPENSE
        );
        binding.expenseCategory.setAdapter(adapter);
        binding.expenseCategory.setOnClickListener(v -> binding.expenseCategory.showDropDown());

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            transactionRef = FirebaseDatabase.getInstance()
                    .getReference("Transactions")
                    .child(auth.getCurrentUser().getUid());
        }

        binding.btnSaveExpense.setOnClickListener(v -> saveExpense());

        return binding.getRoot();
    }

    void saveExpense() {
        String amountText = binding.expenseAmount.getText().toString().trim();
        String categoryText = binding.expenseCategory.getText().toString().trim();

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
                "expense",
                categoryText
        );

        if (transactionRef != null) {
            String id = transactionRef.push().getKey();
            if (id != null) {
                transactionRef.child(id).setValue(transaction)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Expense Added", Toast.LENGTH_SHORT).show();
                            binding.expenseAmount.setText("");
                            binding.expenseCategory.setText("");
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add expense",
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
