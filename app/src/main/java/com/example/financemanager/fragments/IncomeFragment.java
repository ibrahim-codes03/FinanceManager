package com.example.financemanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.financemanager.R;
import com.example.financemanager.classes.Categories;
import com.example.financemanager.classes.TransactionData;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class IncomeFragment extends Fragment {

    TextInputEditText incomeAmount;
    AutoCompleteTextView incomeCategory;
    MaterialButton btnSaveIncome;
    FirebaseAuth auth;
    DatabaseReference transactionRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        incomeAmount = view.findViewById(R.id.incomeAmount);
        incomeCategory = view.findViewById(R.id.incomeCategory);
        btnSaveIncome = view.findViewById(R.id.btnSaveIncome);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                Categories.INCOME
        );
        incomeCategory.setAdapter(adapter);
        incomeCategory.setOnClickListener(v -> incomeCategory.showDropDown());

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            transactionRef = FirebaseDatabase.getInstance()
                    .getReference("Transactions")
                    .child(auth.getCurrentUser().getUid());
        }

        btnSaveIncome.setOnClickListener(v -> saveIncome());

        return view;
    }

    void saveIncome() {
        String amountText = incomeAmount.getText().toString().trim();
        String categoryText = incomeCategory.getText().toString().trim();

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
                            incomeAmount.setText("");
                            incomeCategory.setText("");
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add income",
                                Toast.LENGTH_SHORT).show());
            }
        }
    }
}
