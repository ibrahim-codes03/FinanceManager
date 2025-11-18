package com.example.financemanager.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.R;
import com.example.financemanager.classes.CurrencyManager;
import com.example.financemanager.classes.TransactionData;
import com.example.financemanager.adapters.TransactionAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    TextView welcomeText, totalBalanceAmount, incomeAmount, expenseAmount;
    FloatingActionButton fabMain;
    RecyclerView recyclerRecentTransactions;
    TransactionAdapter adapter;
    List<TransactionData> transactionList = new ArrayList<>();

    FirebaseAuth auth;
    DatabaseReference transactionRef;
    private ValueEventListener transactionListener;

    private CurrencyManager currencyManager;
    private String currencySymbol = "$";
    private double currencyRate = 1.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        welcomeText = view.findViewById(R.id.welcomeText);
        fabMain = view.findViewById(R.id.fab_main);
        recyclerRecentTransactions = view.findViewById(R.id.recyclerRecentTransactions);
        totalBalanceAmount = view.findViewById(R.id.totalBalanceAmount);
        incomeAmount = view.findViewById(R.id.earnedAmount);
        expenseAmount = view.findViewById(R.id.spentAmount);

        if (getActivity() != null) currencyManager = new CurrencyManager(getActivity());
        updateCurrencyFromManager();

        auth = FirebaseAuth.getInstance();

        fabMain.setOnClickListener(v -> {
            if (getActivity() != null)
                startActivity(new android.content.Intent(getActivity(), com.example.financemanager.activities.TransactionActivity.class));
        });

        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        if (getContext() != null) {
            recyclerRecentTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new TransactionAdapter(getContext(), transactionList);
            recyclerRecentTransactions.setAdapter(adapter);
        }
    }

    private void attachListener() {
        if (auth == null || auth.getCurrentUser() == null) return;
        if (transactionRef == null) {
            transactionRef = FirebaseDatabase.getInstance()
                    .getReference("Transactions")
                    .child(auth.getCurrentUser().getUid());
        }
        if (transactionListener != null) return;
        transactionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || getView() == null) return;
                transactionList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    TransactionData transaction = data.getValue(TransactionData.class);
                    if (transaction != null) transactionList.add(transaction);
                }
                if (adapter != null) adapter.notifyDataSetChanged();
                updateTotals();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded() && getContext() != null)
                    Toast.makeText(getContext(), "Failed to load transactions", Toast.LENGTH_SHORT).show();
            }
        };
        transactionRef.addValueEventListener(transactionListener);
    }

    private void detachListener() {
        if (transactionRef != null && transactionListener != null) {
            transactionRef.removeEventListener(transactionListener);
            transactionListener = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        attachListener();
        updateCurrencyFromManager();
    }

    @Override
    public void onStop() {
        super.onStop();
        detachListener();
    }

    private void updateTotals() {
        if (totalBalanceAmount == null || incomeAmount == null || expenseAmount == null) return;

        double income = 0, expense = 0;
        for (TransactionData t : transactionList) {
            if (t != null) {
                double amount = t.getAmount();
                if ("income".equals(t.getType())) income += amount;
                else if ("expense".equals(t.getType())) expense += amount;
            }
        }

        double totalBalance = (income - expense) * currencyRate;

        totalBalanceAmount.setText(currencySymbol + totalBalance);
        incomeAmount.setText("+" + currencySymbol + income);
        expenseAmount.setText("-" + currencySymbol + expense);
    }

    private void updateCurrencyFromManager() {
        if (currencyManager != null) {
            currencySymbol = currencyManager.getCurrentSymbol();
            currencyRate = currencyManager.getCurrentRate();
            updateTotals();
        }
    }

    public void updateCurrency(String symbol, double rate) {
        currencySymbol = symbol;
        currencyRate = rate;
        updateTotals();
    }

    public void refreshTransactions() {
        detachListener();
        attachListener();
    }
}
